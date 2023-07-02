package jp.loioz.app.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.InvoiceRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayRowInputForm;
import jp.loioz.bean.AccgDocInvoiceBean;
import jp.loioz.common.constant.CommonConstant.GensenChoshuRate;
import jp.loioz.common.constant.CommonConstant.HoshuHasuType;
import jp.loioz.common.constant.CommonConstant.InvoiceDepositType;
import jp.loioz.common.constant.CommonConstant.InvoiceOtherItemType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxHasuType;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAccgDocInvoiceDao;
import jp.loioz.dao.TAccgDocRepayDao;
import jp.loioz.dao.TAccgInvoiceTaxDao;
import jp.loioz.dao.TAccgInvoiceWithholdingDao;
import jp.loioz.dao.TAccgRecordDetailDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dto.AccgInvoiceStatementAmountDto;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAccgDocRepayEntity;
import jp.loioz.entity.TAccgInvoiceTaxEntity;
import jp.loioz.entity.TAccgInvoiceWithholdingEntity;
import jp.loioz.entity.TAccgRecordDetailEntity;
import jp.loioz.entity.TFeeEntity;
import jp.loioz.entity.TSalesDetailEntity;

/**
 * 会計管理の金額を扱う共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonAccgAmountService extends DefaultService {

	/** テナント用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 請求項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceDao tAccgDocInvoiceDao;

	/** 請求項目-消費税Daoクラス */
	@Autowired
	private TAccgInvoiceTaxDao tAccgInvoiceTaxDao;

	/** 請求項目-源泉徴収Daoクラス */
	@Autowired
	private TAccgInvoiceWithholdingDao tAccgInvoiceWithholdingDao;

	/** 既入金Daoクラス */
	@Autowired
	private TAccgDocRepayDao tAccgDocRepayDao;

	/** 取引実績明細Daoクラス */
	@Autowired
	private TAccgRecordDetailDao tAccgRecordDetailDao;

	/** 報酬用のDaoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 請求書／精算書の金額情報Dtoを取得
	 * 
	 * <pre>
	 * 現在、DBに登録されているデータを元に、金額情報Dtoを作成する。
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementAmountDto getAccgInvoiceStatementAmountDto(Long accgDocSeq) {
		// 請求項目（報酬、値引き、実費、預り金、消費税、源泉徴収税）に関する金額を取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = this.getAmountRelatedToAccgDocInvoice(accgDocSeq);
		
		// 既入金の取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		List<BigDecimal> repayAmountList = tAccgDocRepayEntityList.stream().map(entity -> entity.getRepayAmount()).collect(Collectors.toList());
		BigDecimal repayAmount = AccountingUtils.calcTotal(repayAmountList);
		accgInvoiceStatementAmountDto.setTotalRepayAmount(repayAmount);
		
		return accgInvoiceStatementAmountDto;
	}

	/**
	 * 請求書／精算書の金額情報Dtoを取得
	 * 
	 * <pre>
	 * 引数の入力フォームのデータを元に、金額情報Dtoを作成する。
	 * 
	 * ※請求項目のデータ（報酬、値引き、実費請求、預り金請求）については、入力フォームのデータを元にするが、
	 * 　既入金項目のデータについては、現在DBに登録されているデータを元にする。
	 * </pre>
	 * 
	 * @param invoiceList 請求項目1行の入力フォームのリスト
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementAmountDto getAccgInvoiceStatementAmountDto(List<InvoiceRowInputForm> invoiceRowList, Long accgDocSeq) {

		AccgInvoiceStatementAmountDto dto = new AccgInvoiceStatementAmountDto();

		if (CollectionUtils.isEmpty(invoiceRowList)) {
			return dto;
		}

		// 報酬（税率0%）源泉徴収ありの合計
		List<BigDecimal> taxNonFeeAmountWithholdingList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> StringUtils.isEmpty(row.getInvoieType()))
				.filter(row -> TaxRate.TAX_FREE.equalsByCode(row.getTaxRateType()))
				.filter(row -> row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalFeeAmountTaxNonWithholding = AccountingUtils.calcTotal(taxNonFeeAmountWithholdingList);
		dto.setTotalFeeAmountTaxNonWithholding(totalFeeAmountTaxNonWithholding);

		// 報酬（税率0%）源泉徴収なしの合計
		List<BigDecimal> taxNonFeeAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> StringUtils.isEmpty(row.getInvoieType()))
				.filter(row -> TaxRate.TAX_FREE.equalsByCode(row.getTaxRateType()))
				.filter(row -> !row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalFeeAmountTaxNon = AccountingUtils.calcTotal(taxNonFeeAmountList);
		dto.setTotalFeeAmountTaxNon(totalFeeAmountTaxNon);

		// 報酬（税率8%）源泉徴収ありの合計
		List<BigDecimal> tax8FeeAmountWithholdingList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> StringUtils.isEmpty(row.getInvoieType()))
				.filter(row -> TaxRate.EIGHT_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalFeeAmountTax8Withholding = AccountingUtils.calcTotal(tax8FeeAmountWithholdingList);
		dto.setTotalFeeAmountTax8Withholding(totalFeeAmountTax8Withholding);

		// 報酬（税率8%）源泉徴収なしの合計
		List<BigDecimal> tax8FeeAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> StringUtils.isEmpty(row.getInvoieType()))
				.filter(row -> TaxRate.EIGHT_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> !row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalFeeAmountTax8 = AccountingUtils.calcTotal(tax8FeeAmountList);
		dto.setTotalFeeAmountTax8(totalFeeAmountTax8);

		// 報酬（税率10%）源泉徴収ありの合計
		List<BigDecimal> tax10FeeAmountWithholdingList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> StringUtils.isEmpty(row.getInvoieType()))
				.filter(row -> TaxRate.TEN_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalFeeAmountTax10Withholding = AccountingUtils.calcTotal(tax10FeeAmountWithholdingList);
		dto.setTotalFeeAmountTax10Withholding(totalFeeAmountTax10Withholding);

		// 報酬（税率10%）源泉徴収なしの合計
		List<BigDecimal> tax10FeeAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> StringUtils.isEmpty(row.getInvoieType()))
				.filter(row -> TaxRate.TEN_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> !row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalFeeAmountTax10 = AccountingUtils.calcTotal(tax10FeeAmountList);
		dto.setTotalFeeAmountTax10(totalFeeAmountTax10);

		// 値引き（税率0%）源泉徴収ありの合計
		List<BigDecimal> taxNonDiscountAmountWithholdingList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType()))
				.filter(row -> TaxRate.TAX_FREE.equalsByCode(row.getTaxRateType()))
				.filter(row -> row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalDiscountAmountTaxNonWithholding = AccountingUtils.calcTotal(taxNonDiscountAmountWithholdingList);
		dto.setTotalDiscountAmountTaxNonWithholding(totalDiscountAmountTaxNonWithholding);

		// 値引き（税率0%）源泉徴収なしの合計
		List<BigDecimal> taxNonDiscountAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType()))
				.filter(row -> TaxRate.TAX_FREE.equalsByCode(row.getTaxRateType()))
				.filter(row -> !row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalDiscountAmountTaxNon = AccountingUtils.calcTotal(taxNonDiscountAmountList);
		dto.setTotalDiscountAmountTaxNon(totalDiscountAmountTaxNon);

		// 値引き（税率8%）源泉徴収ありの合計
		List<BigDecimal> tax8DiscountAmountWithholdingList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType()))
				.filter(row -> TaxRate.EIGHT_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalDiscountAmountTax8Withholding = AccountingUtils.calcTotal(tax8DiscountAmountWithholdingList);
		dto.setTotalDiscountAmountTax8Withholding(totalDiscountAmountTax8Withholding);

		// 値引き（税率8%）源泉徴収なしの合計
		List<BigDecimal> tax8DiscountAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType()))
				.filter(row -> TaxRate.EIGHT_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> !row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalDiscountAmountTax8 = AccountingUtils.calcTotal(tax8DiscountAmountList);
		dto.setTotalDiscountAmountTax8(totalDiscountAmountTax8);

		// 値引き（税率10%）源泉徴収ありの合計
		List<BigDecimal> tax10DiscountAmountWithholdingList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType()))
				.filter(row -> TaxRate.TEN_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalDiscountAmountTax10Withholding = AccountingUtils.calcTotal(tax10DiscountAmountWithholdingList);
		dto.setTotalDiscountAmountTax10Withholding(totalDiscountAmountTax10Withholding);

		// 値引き（税率10%）源泉徴収なしの合計
		List<BigDecimal> tax10DiscountAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> !row.isDepositRecvFlg())
				.filter(row -> InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType()))
				.filter(row -> TaxRate.TEN_PERCENT.equalsByCode(row.getTaxRateType()))
				.filter(row -> !row.isWithholdingFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalDiscountAmountTax10 = AccountingUtils.calcTotal(tax10DiscountAmountList);
		dto.setTotalDiscountAmountTax10(totalDiscountAmountTax10);

		// 預り金の合計
		List<BigDecimal> depositAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> row.isDepositRecvFlg())
				.filter(row -> InvoiceDepositType.DEPOSIT.equalsByCode(row.getInvoieType()))
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);
		dto.setTotalDepositAmount(totalDepositAmount);

		// 実費の合計
		List<BigDecimal> advanceMoneyAmountList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> row.isDepositRecvFlg())
				.filter(row -> InvoiceDepositType.EXPENSE.equalsByCode(row.getInvoieType()))
				.filter(row -> !row.isRowExpenseSumFlg())
				.map(row -> LoiozNumberUtils.parseAsBigDecimal(row.getAmount().replaceAll(",", "")))
				.collect(Collectors.toList());
		BigDecimal totalAdvanceMoneyAmount = AccountingUtils.calcTotal(advanceMoneyAmountList);
		dto.setTotalAdvanceMoneyAmount(totalAdvanceMoneyAmount);

		// 消費税の計算（報酬の消費税-値引きの消費税）
		this.calcTax8OnInvoiceItem(dto);
		this.calcTax10OnInvoiceItem(dto);

		// 源泉徴収額の計算（源泉徴収ありの報酬-源泉徴収ありの値引きの結果×源泉税率）
		this.calcWithholdingOnInvoiceItem(dto);

		// 既入金の取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		List<BigDecimal> repayAmountList = tAccgDocRepayEntityList.stream().map(entity -> entity.getRepayAmount()).collect(Collectors.toList());
		BigDecimal repayAmount = AccountingUtils.calcTotal(repayAmountList);
		dto.setTotalRepayAmount(repayAmount);

		return dto;
	}

	/**
	 * 請求書／精算書の金額情報Dtoを取得
	 * 
	 * <pre>
	 * 引数の入力フォームのデータを元に、金額情報Dtoを作成する。
	 * 
	 * ※請求項目のデータ（報酬、値引き、実費請求、預り金請求）については、現在DBに登録されているデータを元にするが、
	 * 　既入金項目のデータについては、入力フォームのデータを元にする。
	 * </pre>
	 * 
	 * @param repayRowList 既入金項目1行の入力フォームのリスト
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementAmountDto getAccgInvoiceStatementAmountDtoUsingRepayRowInputForm(
			List<RepayRowInputForm> repayRowList, Long accgDocSeq) {
		// 請求項目（報酬、値引き、実費、預り金、消費税、源泉徴収税）に関する金額を取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = this.getAmountRelatedToAccgDocInvoice(accgDocSeq);
		
		// 既入金の取得
		if (LoiozCollectionUtils.isEmpty(repayRowList)) {
			accgInvoiceStatementAmountDto.setTotalRepayAmount(BigDecimal.ZERO);
		} else {
			List<BigDecimal> repayAmountList = repayRowList.stream()
					.filter(row -> !row.isDeleteFlg())
					// 行データからまとめ表示を除外する（まとめ表示するしないに関わらず存在する行データを対象に確認）
					.filter(row -> !row.isRowRepaySumFlg())
					.map(form -> StringUtils.isEmpty(form.getRepayAmount())
							? BigDecimal.ZERO
							: LoiozNumberUtils.parseAsBigDecimal(form.getRepayAmount()))
					.collect(Collectors.toList());
			BigDecimal repayAmount = AccountingUtils.calcTotal(repayAmountList);
			accgInvoiceStatementAmountDto.setTotalRepayAmount(repayAmount);
		}
		
		return accgInvoiceStatementAmountDto;
	}

	/**
	 * 請求項目の8%対象の報酬について消費税を計算し結果を AccgInvoiceStatementAmountDto にセットします<br>
	 * 
	 * @param dto
	 */
	public void calcTax8OnInvoiceItem(AccgInvoiceStatementAmountDto dto) {
		if (dto == null) {
			return;
		}
		// 報酬（源泉徴収なし、あり）
		BigDecimal totalFeeAmountTax8 = dto.getTotalFeeAmountTax8();
		BigDecimal totalFeeAmountTax8Withholding = dto.getTotalFeeAmountTax8Withholding();
		// 値引き（源泉徴収なし、あり）
		BigDecimal totalDiscountAmountTax8 = dto.getTotalDiscountAmountTax8();
		BigDecimal totalDiscountAmountTax8Withholding = dto.getTotalDiscountAmountTax8Withholding();

		// 報酬ごと、値引きごとで消費税を算出
		BigDecimal feeTax8Amount = this.calcTaxAmount(totalFeeAmountTax8Withholding.add(totalFeeAmountTax8), TaxRate.EIGHT_PERCENT.getCd(), TaxFlg.FOREIGN_TAX.getCd());
		BigDecimal discountTax8Amount = this.calcTaxAmount(totalDiscountAmountTax8Withholding.add(totalDiscountAmountTax8), TaxRate.EIGHT_PERCENT.getCd(), TaxFlg.FOREIGN_TAX.getCd());
		
		// 消費税の対象額（値引き前）
		dto.setTotalTaxAmount8TargetFeeNonDiscount(totalFeeAmountTax8Withholding.add(totalFeeAmountTax8));
		// 消費税の対象額からの値引き額
		dto.setTotalTaxAmount8TargetDiscount(totalDiscountAmountTax8Withholding.add(totalDiscountAmountTax8));
		// 消費税額（値引き前）
		dto.setTotalTaxAmount8NonDiscount(feeTax8Amount);
		// 消費税額からの値引き額
		dto.setTotalTaxAmount8Discount(discountTax8Amount);
	}

	/**
	 * 請求項目の10%対象の報酬について消費税を計算し結果を AccgInvoiceStatementAmountDto にセットします<br>
	 * 
	 * @param dto
	 */
	public void calcTax10OnInvoiceItem(AccgInvoiceStatementAmountDto dto) {
		if (dto == null) {
			return;
		}
		// 報酬（源泉徴収なし、あり）
		BigDecimal totalFeeAmountTax10 = dto.getTotalFeeAmountTax10();
		BigDecimal totalFeeAmountTax10Withholding = dto.getTotalFeeAmountTax10Withholding();
		// 値引き（源泉徴収なし、あり）
		BigDecimal totalDiscountAmountTax10 = dto.getTotalDiscountAmountTax10();
		BigDecimal totalDiscountAmountTax10Withholding = dto.getTotalDiscountAmountTax10Withholding();

		// 報酬ごと、値引きごとで消費税を計算
		BigDecimal feeTax10Amount = this.calcTaxAmount(totalFeeAmountTax10Withholding.add(totalFeeAmountTax10), TaxRate.TEN_PERCENT.getCd(), TaxFlg.FOREIGN_TAX.getCd());
		BigDecimal discountTax10Amount = this.calcTaxAmount(totalDiscountAmountTax10Withholding.add(totalDiscountAmountTax10), TaxRate.TEN_PERCENT.getCd(), TaxFlg.FOREIGN_TAX.getCd());
		
		// 対象額（値引き前）
		dto.setTotalTaxAmount10TargetFeeNonDiscount(totalFeeAmountTax10Withholding.add(totalFeeAmountTax10));
		// 対象額からの値引き額
		dto.setTotalTaxAmount10TargetDiscount(totalDiscountAmountTax10Withholding.add(totalDiscountAmountTax10));
		// 消費税額（値引き前）
		dto.setTotalTaxAmount10NonDiscount(feeTax10Amount);
		// 消費税額からの値引き額
		dto.setTotalTaxAmount10Discount(discountTax10Amount);
	}

	/**
	 * 請求項目の源泉徴収ありの報酬について源泉徴収額を計算し結果を AccgInvoiceStatementAmountDto にセットします<br>
	 * 
	 * @param dto
	 */
	public void calcWithholdingOnInvoiceItem(AccgInvoiceStatementAmountDto dto) {
		// 源泉徴収ありの報酬
		BigDecimal totalFeeAmountTaxNonWithholding = dto.getTotalFeeAmountTaxNonWithholding();
		BigDecimal totalFeeAmountTax8Withholding = dto.getTotalFeeAmountTax8Withholding();
		BigDecimal totalFeeAmountTax10Withholding = dto.getTotalFeeAmountTax10Withholding();
		// 源泉徴収ありの値引き
		BigDecimal totalDiscountAmountTaxNonWithholding = dto.getTotalDiscountAmountTaxNonWithholding();
		BigDecimal totalDiscountAmountTax8Withholding = dto.getTotalDiscountAmountTax8Withholding();
		BigDecimal totalDiscountAmountTax10Withholding = dto.getTotalDiscountAmountTax10Withholding();

		// 源泉徴収ありの報酬の合計
		BigDecimal totalWithholdingTargetFee = totalFeeAmountTaxNonWithholding.add(totalFeeAmountTax8Withholding).add(totalFeeAmountTax10Withholding);
		// 源泉徴収有りの値引きの合計
		BigDecimal totalWithholdingTargetDiscount = totalDiscountAmountTaxNonWithholding.add(totalDiscountAmountTax8Withholding).add(totalDiscountAmountTax10Withholding);

		// 源泉徴収対象額（報酬-値引き）
		dto.setTotalWithholdingTargetFee(totalWithholdingTargetFee.subtract(totalWithholdingTargetDiscount));

		// 源泉徴収額算出（源泉徴収対象額X税率）
		BigDecimal withholdingAmount = this.calcWithholdingAmount(totalWithholdingTargetFee.subtract(totalWithholdingTargetDiscount));
		dto.setTotalWithholdingAmount(withholdingAmount);
	}

	/**
	 * Dtoが保持する金額情報を元に、<br>
	 * 報酬入金額【見込】 を算出する。（報酬(税抜-値引き+消費税)-源泉徴収額）
	 * 
	 * @param dto
	 * @return
	 */
	public BigDecimal calcFeeAmountExpect(AccgInvoiceStatementAmountDto dto) {

		BigDecimal feeAmountExpect = BigDecimal.ZERO
				// 報酬：消費税込の合計（報酬-値引き)+(報酬の消費税-値引きの消費税)
				.add(dto.getTotalFeeAmount())
				// 源泉徴収額
				.subtract(dto.getTotalWithholdingAmount());

		return feeAmountExpect;
	}

	/**
	 * Dtoが保持する金額情報を元に、<br>
	 * 預り金入金額【見込】 を算出する。
	 * 
	 * @param dto
	 * @return
	 */
	public BigDecimal calcDepositRecvAmountExpect(AccgInvoiceStatementAmountDto dto) {

		// 既入金-請求(預り金、実費)合計
		BigDecimal totalRepayInvoiceDepositAmount = dto.getTotalRepayInvoiceDepositAmount();

		boolean isMinus = LoiozNumberUtils.isLessThan(totalRepayInvoiceDepositAmount, BigDecimal.ZERO);
		if (!isMinus) {
			// 既入金-請求(預り金、実費)合計がマイナスではない = 既入金が大きいか、既入金と請求(預り金、実費)の金額が同じ場合
			// -> 預り金としては返金（出金）となるため、入金見込みは0となる
			return BigDecimal.ZERO;
		}

		// 既入金-請求(預り金、実費)合計がマイナス = 既入金より、請求(預り金、実費)が大きい場合
		// -> 預り金としては請求（入金）となり、入金見込みは、既入金-請求(預り金、実費)合計のマイナス値の絶対値となる

		return totalRepayInvoiceDepositAmount.abs();
	}

	/**
	 * Dtoが保持する金額情報を元に、<br>
	 * 預り金出金額【見込】 を算出する。
	 * 
	 * @param dto
	 * @return
	 */
	public BigDecimal calcDepositPaymentAmountExpect(AccgInvoiceStatementAmountDto dto) {

		// 預り金出金額【見込】
		BigDecimal depositPaymentAmountExpect = BigDecimal.ZERO;

		// 既入金-請求(預り金、実費)合計
		BigDecimal totalRepayInvoiceDepositAmount = dto.getTotalRepayInvoiceDepositAmount();

		boolean isMinus = LoiozNumberUtils.isLessThan(totalRepayInvoiceDepositAmount, BigDecimal.ZERO);
		if (isMinus) {
			// 既入金-請求(預り金、実費)合計がマイナスになる = 既入金より、請求(預り金、実費)の金額の方が大きい
			// -> 預り金としては請求（入金）となるため、出金見込みは0となる
			return depositPaymentAmountExpect;
		}

		// 既入金-請求(預り金、実費)合計がマイナスにならない = 既入金の金額が大きいか、既入金と請求(預り金、実費)の金額が同じ場合

		// 振替額
		BigDecimal refundAmount = this.calcRefundAmount(dto);

		boolean isRefundAmountZero = LoiozNumberUtils.equalsDecimal(refundAmount, BigDecimal.ZERO);
		if (isRefundAmountZero) {
			// 振替額が0 = 預り金（既入金）の報酬への振替がない 場合
			// -> 既入金-請求(預り金、実費)合計の値（請求に対して既入金が超過している分）が出金見込みとなる

			depositPaymentAmountExpect = totalRepayInvoiceDepositAmount;
			return depositPaymentAmountExpect;
		}

		// 預り金（既入金）の報酬への振替が発生している場合

		// 出金見込み額は、
		// 既入金-請求(預り金、実費)合計の値（請求に対して既入金が超過している分）から、報酬へ振替を行った金額を差し引いた分の金額となる

		depositPaymentAmountExpect = totalRepayInvoiceDepositAmount.subtract(refundAmount);
		return depositPaymentAmountExpect;
	}

	/**
	 * Dtoが保持する金額情報を元に、<br>
	 * 振替額（既入金の報酬への振替額）を算出する。
	 * 
	 * @param dto
	 * @return
	 */
	public BigDecimal calcRefundAmount(AccgInvoiceStatementAmountDto dto) {

		// 振替額
		BigDecimal refundAmount = BigDecimal.ZERO;

		//
		// 報酬金額のチェック
		//

		// 報酬合計（報酬入金額【見込】）
		BigDecimal feeAmountSum = this.calcFeeAmountExpect(dto);

		if (feeAmountSum.compareTo(BigDecimal.ZERO) == 0) {
			// 報酬合計が0の場合

			// 報酬がない場合は、既入金の報酬への振替は発生しないので、振替額は0
			return refundAmount;
		}

		//
		// 預り金（既入金、預り金請求、実費請求）金額のチェック
		//

		// 既入金-請求(預り金、実費)合計
		BigDecimal totalRepayInvoiceDepositAmount = dto.getTotalRepayInvoiceDepositAmount();

		if (totalRepayInvoiceDepositAmount.compareTo(BigDecimal.ZERO) == 0 || totalRepayInvoiceDepositAmount.compareTo(BigDecimal.ZERO) == -1) {
			// 既入金-請求(預り金、実費)合計が 0 か マイナスの場合（既入金（入金）より、請求（出金）が多い場合）

			// 報酬に振り替えるだけの金額がない状態のため、振替額は0
			return refundAmount;
		}

		//
		// 振替額の算出
		// ※ この時点で、報酬合計は0以上、既入金-請求(預り金、実費)合計も0以上（請求より、既入金が多い）の状態となる
		//

		boolean totalRepayInvoiceDepositAmountIsGreater = LoiozNumberUtils.isGreaterThan(totalRepayInvoiceDepositAmount, feeAmountSum);
		if (totalRepayInvoiceDepositAmountIsGreater) {
			// 報酬合計よりも、既入金-請求(預り金、実費)合計の方が金額が大きい

			// 報酬の全額を、請求(預り金、実費)に対しての既入金の超過分で振替可能 -> 振替額は報酬合計の金額となる
			refundAmount = feeAmountSum;
		} else {
			// 報酬合計よりも、既入金-請求(預り金、実費)合計の方が金額が小さいか、同じ場合

			// 報酬の全額の振替はできないが、請求(預り金、実費)に対しての既入金の超過分は振替可能 -> 振替額は既入金の超過分の金額となる
			refundAmount = totalRepayInvoiceDepositAmount;
		}

		return refundAmount;
	}

	/**
	 * 引数の売上明細リストを元に、<br>
	 * 売上合計【見込】（税込）を算出する。
	 * 
	 * <pre>
	 * ※引数の売上詳細リストは、特定の案件-顧客の、全ての売上明細情報のリストを想定している。
	 * </pre>
	 * 
	 * @param tSalesDetailEntityList
	 * @return
	 */
	public BigDecimal calcSalesAmountExpect(List<TSalesDetailEntity> tSalesDetailEntityList) {

		// 売上明細の売上の合計：（売上金額+消費税）-（値引き+値引き分消費税）-源泉徴収額
		List<BigDecimal> salesAmountList = tSalesDetailEntityList.stream().map(entity -> {
			return AccountingUtils
					.calcTotal(entity.getSalesAmount(), entity.getSalesTaxAmount())
					.subtract(AccountingUtils.calcTotal(entity.getSalesDiscountAmount(), entity.getSalesDiscountTaxAmount()))
					.subtract(LoiozNumberUtils.nullToZero(entity.getSalesWithholdingAmount()));
		}).collect(Collectors.toList());

		BigDecimal salesAmountExpect = AccountingUtils.calcTotal(salesAmountList);

		return salesAmountExpect;
	}

	/**
	 * 対象の案件、顧客、売上データを元に、<br>
	 * 売上合計【実績】（税込）を算出する。
	 * 
	 * @param salesSeq
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	public BigDecimal calcSalesAmountResult(Long personId, Long ankenId, Long salesSeq) {

		// 売上情報（名簿、案件）に紐づく取引実績情報をすべて取得する
		List<TAccgRecordDetailEntity> tAccgRecordDetailEntities = tAccgRecordDetailDao.selectAccgRecordDetailEntityBySalesSeq(salesSeq);
		List<BigDecimal> feeRecordAmountList = tAccgRecordDetailEntities.stream().map(TAccgRecordDetailEntity::getRecordFeeAmount).collect(Collectors.toList());
		BigDecimal totalFeeRecordAmount = AccountingUtils.calcTotal(feeRecordAmountList);

		// 名簿、案件に紐づく、入金済みステータス且つ売上実績に紐づく報酬情報をすべて取得
		List<TFeeEntity> tFeeEntities = tFeeDao.selectDepositStatusSalesFeeByPersonIdAndAnkenId(personId, ankenId);
		List<BigDecimal> feeDepositedSalesAmountList = tFeeEntities.stream().map(e -> {
			return AccountingUtils.calcTotal(e.getFeeAmount(), e.getTaxAmount()).subtract(LoiozNumberUtils.nullToZero(e.getWithholdingAmount()));
		}).collect(Collectors.toList());
		BigDecimal totalFeeDepositedSalesAmount = AccountingUtils.calcTotal(feeDepositedSalesAmountList);

		// 売上合計【実績】
		BigDecimal salesAmountResult = AccountingUtils.calcTotal(totalFeeRecordAmount, totalFeeDepositedSalesAmount);

		return salesAmountResult;
	}

	/**
	 * 報酬額（税抜）を計算します。
	 * 
	 * @param feeAmount 報酬額
	 * @param taxRateCd 消費税率コード
	 * @param taxFlg 課税区分コード
	 * @return 報酬額（税抜）
	 */
	public BigDecimal calcFeeAmountExcludingTax(BigDecimal feeAmount, String taxRateCd, String taxFlg) {
		BigDecimal feeAmountExcludingTax = BigDecimal.ZERO;
		if (TaxFlg.INTERNAL_TAX.equalsByCode(taxFlg)) {
			// 税込みの場合は、報酬額から消費税を引く
			BigDecimal taxAmount = this.calcTaxAmount(feeAmount, taxRateCd, taxFlg);
			feeAmountExcludingTax = feeAmount.subtract(taxAmount);
		} else {
			// 税抜きの場合は、受け取った報酬額のまま
			feeAmountExcludingTax = feeAmount;
		}
		return feeAmountExcludingTax;
	}

	/**
	 * 金額に対する消費税額を消費税率、課税区分から計算します。
	 * 
	 * @param kingaku 金額
	 * @param taxRateCd 消費税率コード
	 * @param taxFlg 課税区分コード
	 * @return 消費税額
	 */
	public BigDecimal calcTaxAmount(BigDecimal kingaku, String taxRateCd, String taxFlg) {
		// 端数時の処理方法
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		String taxHasuType = tenantEntity.getTaxHasuType();

		// 消費税の計算
		BigDecimal taxAmount = BigDecimal.ZERO;
		if (TaxFlg.INTERNAL_TAX.equalsByCode(taxFlg)) {
			// 税込みの場合
			taxAmount = this.calculateTaxInclusiveTax(kingaku, taxRateCd, taxHasuType);
		} else {
			// 税抜きの場合
			taxAmount = this.calculateTaxExclusiveTax(kingaku, taxRateCd, taxHasuType);
		}
		return taxAmount;
	}

	/**
	 * 源泉徴収額を計算します。<br>
	 *
	 * <pre>
	 * 源泉徴収率の計算から行います。
	 * </pre>
	 *
	 * @param kingakuExcludingTax 金額（税抜）
	 * @return 源泉徴収額
	 */
	public BigDecimal calcWithholdingAmount(BigDecimal kingakuExcludingTax) {

		// 源泉徴収計算の基準値
		BigDecimal oneMillion = CommonAccgService.GENSEN_CALC_BORDER_DECIMAL;

		// 厳選徴収計算の比率
		BigDecimal tenPerGensenRate = new BigDecimal(GensenChoshuRate.TEN.getVal());
		BigDecimal twentyPerGensenRate = new BigDecimal(GensenChoshuRate.TWENTY.getVal());

		// 前線徴収金額
		BigDecimal result;
		if (kingakuExcludingTax.compareTo(oneMillion) == 1) {
			// 100万円より高額の場合

			// 源泉徴収率が10.21％になる部分の金額
			BigDecimal tenPerGensen = this.calcWithholdingAmount(tenPerGensenRate, oneMillion);

			// 源泉徴収額が20.42％になる部分の金額
			BigDecimal twentyPerKingaku = kingakuExcludingTax.subtract(oneMillion);
			BigDecimal twentyPerGensen = this.calcWithholdingAmount(twentyPerGensenRate, twentyPerKingaku);

			// 源泉徴収率を足します。
			result = tenPerGensen.add(twentyPerGensen);

		} else {
			// 100万円以下の場合

			// 源泉徴収額を計算をする(源泉徴収率は10.21%)
			result = this.calcWithholdingAmount(tenPerGensenRate, kingakuExcludingTax);
		}

		return result;
	}

	/**
	 * 単価と時間から報酬額を計算します。
	 * 
	 * @param tanka
	 * @param decimalMinutes
	 * @return
	 * @throws AppException
	 */
	public BigDecimal calculateTimeCharge(BigDecimal tanka, BigDecimal decimalMinutes) throws AppException {

		// 60分
		BigDecimal oneHourMinutes = BigDecimal.valueOf(Duration.ofHours(1).toMinutes());

		// テナントの報酬の端数の報酬の端数処理を取得します
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		HoshuHasuType hoshuHasuType = HoshuHasuType.of(tenantEntity.getHoshuHasuType());

		// テナントの報酬の端数の報酬の端数処理に従い 単価 × 時間 を計算します。
		BigDecimal feeAmount = tanka.multiply(decimalMinutes).divide(oneHourMinutes, 0, hoshuHasuType.getRoundingMode());

		return feeAmount;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 消費税額を計算します。<br>
	 *
	 * <pre>
	 * 税込用です。<br>
	 * 消費税の計算・端数処理まで行います。
	 * </pre>
	 *
	 * @param kingaku 金額（税込）
	 * @param taxRateCd 消費税率Cd
	 * @param taxHasuType 消費税の端数処理方法
	 * @return 消費税
	 */
	private BigDecimal calculateTaxInclusiveTax(BigDecimal kingaku, String taxRateCd, String taxHasuType) {

		TaxRate taxRateEnum = TaxRate.of(taxRateCd);
		TaxHasuType taxHasuTypeEnum = TaxHasuType.of(taxHasuType);

		if (taxRateEnum == null || taxHasuTypeEnum == null) {
			throw new RuntimeException("必須パラメータが不足しています");
		}

		// -------------------------------------------------------
		// 消費税の計算
		// -------------------------------------------------------
		// 消費税率
		String taxRateStr = taxRateEnum.getVal();

		BigDecimal taxRate = new BigDecimal(taxRateStr);
		BigDecimal hundred = BigDecimal.valueOf(100);

		// 消費税を計算します。
		// 計算式：金額 × 消費税率(%) ÷ (100 + 消費税率(%))
		// 例：1,080 × 8 ÷ 108
		// ********************************************
		// 本来は、1,080 ÷ 108 × 8 ですが
		// これだと計算途中に小数を考慮しなければならないため
		// 順番を変更して掛け算から先に計算するようにします。
		// ********************************************
		// a = 100 + 消費税率(%)
		BigDecimal calcNum = hundred.add(taxRate);
		// b = 金額 ×消費税率(%)
		BigDecimal calcTax = kingaku.multiply(taxRate);

		// -------------------------------------------------------
		// 消費税の計算・端数処理
		// (消費税 = b ÷ a)
		// -------------------------------------------------------
		return calcTax.divide(calcNum, 0, taxHasuTypeEnum.getRoundingMode());
	}

	/**
	 * 消費税を計算します。<br>
	 *
	 * <pre>
	 * 税抜用です。<br>
	 * 消費税の計算・端数処理まで行います。
	 * </pre>
	 *
	 * @param kingaku 金額（税抜）
	 * @param taxRateCd 消費税率Cd
	 * @param taxHasuType 消費税の端数処理方法
	 * @return 消費税
	 */
	private BigDecimal calculateTaxExclusiveTax(BigDecimal kingaku, String taxRateCd, String taxHasuType) {

		TaxRate taxRateEnum = TaxRate.of(taxRateCd);
		TaxHasuType taxHasuTypeEnum = TaxHasuType.of(taxHasuType);

		if (taxRateEnum == null || taxHasuTypeEnum == null) {
			throw new RuntimeException("必須パラメータが不足しています");
		}

		// -------------------------------------------------------
		// 消費税の計算
		// -------------------------------------------------------

		// 消費税率
		String taxRateStr = taxRateEnum.getVal();
		BigDecimal taxRate = new BigDecimal(taxRateStr);

		// 税理率は%表示なので、小数点にする
		BigDecimal calcTax = taxRate.divide(BigDecimal.valueOf(100));

		// 消費税を計算する（計算式：金額 × 消費税率）
		BigDecimal tax = kingaku.multiply(calcTax);

		// 端数処理
		return tax.setScale(0, taxHasuTypeEnum.getRoundingMode());
	}

	/**
	 * 源泉徴収額を計算します。<br>
	 *
	 * <pre>
	 * 源泉徴収率の計算は行わず、金額のみの計算です。
	 * </pre>
	 *
	 * @param gensenRateStr 源泉徴収率の文字列
	 * @param kingaku 金額
	 * @return 源泉徴収額
	 */
	private BigDecimal calcWithholdingAmount(BigDecimal gensenRate, BigDecimal kingaku) {

		// 源泉徴収率(小数)に変換します。
		BigDecimal gensenDecimal = gensenRate.divide(BigDecimal.valueOf(100));

		// -------------------------------------------------------
		// 源泉徴収額の計算
		// -------------------------------------------------------
		// 金額 × 源泉徴収率
		BigDecimal gensenGaku = BigDecimal.ZERO;
		gensenGaku = kingaku.multiply(gensenDecimal);

		// *******************************
		// 小数点は切り捨てます。
		// ※国税庁発行の下記の源泉徴収の計算方法に従い、端数は切り捨てる。
		// https://www.nta.go.jp/taxes/tetsuzuki/shinsei/annai/gensen/fukko/pdf/02.pdf
		// *******************************
		BigDecimal roundDownGensen = gensenGaku.setScale(0, RoundingMode.DOWN);

		return roundDownGensen;
	}

	/**
	 * 請求項目に関する金額情報をDBから取得します。<br>
	 * （報酬額、値引き、預り金、実費、消費税、源泉聴取額）
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private AccgInvoiceStatementAmountDto getAmountRelatedToAccgDocInvoice(Long accgDocSeq) {
		// 請求項目データ取得
		List<AccgDocInvoiceBean> beanList = tAccgDocInvoiceDao.selectAccgDocInvoiceBeanByAccgDocSeq(accgDocSeq);
		
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = new AccgInvoiceStatementAmountDto();
		if (LoiozCollectionUtils.isNotEmpty(beanList)) {
			
			// 報酬（税率0%）源泉徴収ありの合計
			List<BigDecimal> taxNonFeeAmountWithholdingList = beanList.stream()
					.filter(bean -> StringUtils.isEmpty(bean.getTaxRateType()) || TaxRate.TAX_FREE.equalsByCode(bean.getTaxRateType()))
					.filter(bean -> SystemFlg.FLG_ON.equalsByCode(bean.getWithholdingFlg()))
					.map(bean -> bean.getFeeAmount())
					.collect(Collectors.toList());
			BigDecimal totalFeeAmountTaxNonWithholding = AccountingUtils.calcTotal(taxNonFeeAmountWithholdingList);
			accgInvoiceStatementAmountDto.setTotalFeeAmountTaxNonWithholding(totalFeeAmountTaxNonWithholding);
			
			// 報酬（税率0%）源泉徴収なしの合計
			List<BigDecimal> taxNonFeeAmountList = beanList.stream()
					.filter(bean -> StringUtils.isEmpty(bean.getTaxRateType()) || TaxRate.TAX_FREE.equalsByCode(bean.getTaxRateType()))
					.filter(bean -> SystemFlg.FLG_OFF.equalsByCode(bean.getWithholdingFlg()))
					.map(bean -> bean.getFeeAmount())
					.collect(Collectors.toList());
			BigDecimal totalFeeAmountTaxNon = AccountingUtils.calcTotal(taxNonFeeAmountList);
			accgInvoiceStatementAmountDto.setTotalFeeAmountTaxNon(totalFeeAmountTaxNon);
			
			// 報酬（税率8%）源泉徴収ありの合計
			List<BigDecimal> tax8FeeAmountWithholdingList = beanList.stream()
					.filter(bean -> TaxRate.EIGHT_PERCENT.equalsByCode(bean.getTaxRateType()))
					.filter(bean -> SystemFlg.FLG_ON.equalsByCode(bean.getWithholdingFlg()))
					.map(bean -> bean.getFeeAmount())
					.collect(Collectors.toList());
			BigDecimal totalFeeAmountTax8Withholding = AccountingUtils.calcTotal(tax8FeeAmountWithholdingList);
			accgInvoiceStatementAmountDto.setTotalFeeAmountTax8Withholding(totalFeeAmountTax8Withholding);
			
			// 報酬（税率8%）源泉徴収なしの合計
			List<BigDecimal> tax8FeeAmountList = beanList.stream()
					.filter(bean -> TaxRate.EIGHT_PERCENT.equalsByCode(bean.getTaxRateType()))
					.filter(bean -> SystemFlg.FLG_OFF.equalsByCode(bean.getWithholdingFlg()))
					.map(bean -> bean.getFeeAmount())
					.collect(Collectors.toList());
			BigDecimal totalFeeAmountTax8 = AccountingUtils.calcTotal(tax8FeeAmountList);
			accgInvoiceStatementAmountDto.setTotalFeeAmountTax8(totalFeeAmountTax8);
			
			// 報酬（税率10%）源泉徴収ありの合計
			List<BigDecimal> tax10FeeAmountWithholdingList = beanList.stream()
					.filter(bean -> TaxRate.TEN_PERCENT.equalsByCode(bean.getTaxRateType()))
					.filter(bean -> SystemFlg.FLG_ON.equalsByCode(bean.getWithholdingFlg()))
					.map(bean -> bean.getFeeAmount())
					.collect(Collectors.toList());
			BigDecimal totalFeeAmountTax10Withholding = AccountingUtils.calcTotal(tax10FeeAmountWithholdingList);
			accgInvoiceStatementAmountDto.setTotalFeeAmountTax10Withholding(totalFeeAmountTax10Withholding);
			
			// 報酬（税率10%）源泉徴収なしの合計
			List<BigDecimal> tax10FeeAmountList = beanList.stream()
					.filter(bean -> TaxRate.TEN_PERCENT.equalsByCode(bean.getTaxRateType()))
					.filter(bean -> SystemFlg.FLG_OFF.equalsByCode(bean.getWithholdingFlg()))
					.map(bean -> bean.getFeeAmount())
					.collect(Collectors.toList());
			BigDecimal totalFeeAmountTax10 = AccountingUtils.calcTotal(tax10FeeAmountList);
			accgInvoiceStatementAmountDto.setTotalFeeAmountTax10(totalFeeAmountTax10);
			
			// 値引き（税率0%）源泉徴収ありの合計
			List<BigDecimal> taxNonDiscountAmountWithholdingList = beanList.stream()
					.filter(bean -> InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType()))
					.filter(bean -> StringUtils.isEmpty(bean.getDiscountTaxRateType()) || TaxRate.TAX_FREE.equalsByCode(bean.getDiscountTaxRateType()))
					.filter(bean -> SystemFlg.FLG_ON.equalsByCode(bean.getDiscountWithholdingFlg()))
					.map(bean -> bean.getOtherAmount())
					.collect(Collectors.toList());
			BigDecimal totalDiscountAmountTaxNonWithholding = AccountingUtils.calcTotal(taxNonDiscountAmountWithholdingList);
			accgInvoiceStatementAmountDto.setTotalDiscountAmountTaxNonWithholding(totalDiscountAmountTaxNonWithholding);
			
			// 値引き（税率0%）源泉徴収なしの合計
			List<BigDecimal> taxNonDiscountAmountList = beanList.stream()
					.filter(bean -> InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType()))
					.filter(bean -> StringUtils.isEmpty(bean.getDiscountTaxRateType()) || TaxRate.TAX_FREE.equalsByCode(bean.getDiscountTaxRateType()))
					.filter(bean -> SystemFlg.FLG_OFF.equalsByCode(bean.getDiscountWithholdingFlg()))
					.map(bean -> bean.getOtherAmount())
					.collect(Collectors.toList());
			BigDecimal totalDiscountAmountTaxNon = AccountingUtils.calcTotal(taxNonDiscountAmountList);
			accgInvoiceStatementAmountDto.setTotalDiscountAmountTaxNon(totalDiscountAmountTaxNon);
			
			// 値引き（税率8%）源泉徴収ありの合計
			List<BigDecimal> tax8DiscountAmountWithholdingList = beanList.stream()
					.filter(bean -> InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType()))
					.filter(bean -> TaxRate.EIGHT_PERCENT.equalsByCode(bean.getDiscountTaxRateType()))
					.filter(bean -> SystemFlg.FLG_ON.equalsByCode(bean.getDiscountWithholdingFlg()))
					.map(bean -> bean.getOtherAmount())
					.collect(Collectors.toList());
			BigDecimal totalDiscountAmountTax8Withholding = AccountingUtils.calcTotal(tax8DiscountAmountWithholdingList);
			accgInvoiceStatementAmountDto.setTotalDiscountAmountTax8Withholding(totalDiscountAmountTax8Withholding);
			
			// 値引き（税率8%）源泉徴収なしの合計
			List<BigDecimal> tax8DiscountAmountList = beanList.stream()
					.filter(bean -> InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType()))
					.filter(bean -> TaxRate.EIGHT_PERCENT.equalsByCode(bean.getDiscountTaxRateType()))
					.filter(bean -> SystemFlg.FLG_OFF.equalsByCode(bean.getDiscountWithholdingFlg()))
					.map(bean -> bean.getOtherAmount())
					.collect(Collectors.toList());
			BigDecimal totalDiscountAmountTax8 = AccountingUtils.calcTotal(tax8DiscountAmountList);
			accgInvoiceStatementAmountDto.setTotalDiscountAmountTax8(totalDiscountAmountTax8);
			
			// 値引き（税率10%）源泉徴収ありの合計
			List<BigDecimal> tax10DiscountAmountWithholdingList = beanList.stream()
					.filter(bean -> InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType()))
					.filter(bean -> TaxRate.TEN_PERCENT.equalsByCode(bean.getDiscountTaxRateType()))
					.filter(bean -> SystemFlg.FLG_ON.equalsByCode(bean.getDiscountWithholdingFlg()))
					.map(bean -> bean.getOtherAmount())
					.collect(Collectors.toList());
			BigDecimal totalDiscountAmountTax10Withholding = AccountingUtils.calcTotal(tax10DiscountAmountWithholdingList);
			accgInvoiceStatementAmountDto.setTotalDiscountAmountTax10Withholding(totalDiscountAmountTax10Withholding);
			
			// 値引き（税率10%）源泉徴収なしの合計
			List<BigDecimal> tax10DiscountAmountList = beanList.stream()
					.filter(bean -> InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType()))
					.filter(bean -> TaxRate.TEN_PERCENT.equalsByCode(bean.getDiscountTaxRateType()))
					.filter(bean -> SystemFlg.FLG_OFF.equalsByCode(bean.getDiscountWithholdingFlg()))
					.map(bean -> bean.getOtherAmount())
					.collect(Collectors.toList());
			BigDecimal totalDiscountAmountTax10 = AccountingUtils.calcTotal(tax10DiscountAmountList);
			accgInvoiceStatementAmountDto.setTotalDiscountAmountTax10(totalDiscountAmountTax10);
			
			// 預り金の合計
			List<BigDecimal> depositAmountList = beanList.stream()
					.filter(bean -> InvoiceDepositType.DEPOSIT.equalsByCode(bean.getInvoiceDepositType()))
					.map(bean -> bean.getDepositAmount())
					.collect(Collectors.toList());
			BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);
			accgInvoiceStatementAmountDto.setTotalDepositAmount(totalDepositAmount);
			
			// 実費の合計
			List<BigDecimal> advanceMoneyAmountList = beanList.stream()
					.filter(bean -> InvoiceDepositType.EXPENSE.equalsByCode(bean.getInvoiceDepositType()))
					.map(bean -> bean.getDepositAmount())
					.collect(Collectors.toList());
			BigDecimal totalAdvanceMoneyAmount = AccountingUtils.calcTotal(advanceMoneyAmountList);
			accgInvoiceStatementAmountDto.setTotalAdvanceMoneyAmount(totalAdvanceMoneyAmount);
			
			// 実費/預り金の合計
			BigDecimal totalDepositAllAmount = AccountingUtils.calcTotal(totalDepositAmount, totalAdvanceMoneyAmount);
			accgInvoiceStatementAmountDto.setTotalDepositAllAmount(totalDepositAllAmount);
		}
		
		// 消費税データ取得
		List<TAccgInvoiceTaxEntity> accgInvoiceTaxList = tAccgInvoiceTaxDao.selectAccgInvoiceTaxByAccgDocSeq(accgDocSeq);
		if (!CollectionUtils.isEmpty(accgInvoiceTaxList)) {
			for (TAccgInvoiceTaxEntity entity : accgInvoiceTaxList) {
				if (TaxRate.EIGHT_PERCENT.equalsByCode(entity.getTaxRateType())) {
					// 対象額
					accgInvoiceStatementAmountDto.setTotalTaxAmount8TargetFeeNonDiscount(entity.getTaxableAmount());
					accgInvoiceStatementAmountDto.setTotalTaxAmount8TargetDiscount(entity.getDiscountTaxableAmount());
					// 消費税
					accgInvoiceStatementAmountDto.setTotalTaxAmount8NonDiscount(entity.getTaxAmount());
					accgInvoiceStatementAmountDto.setTotalTaxAmount8Discount(entity.getDiscountTaxAmount());
				} else if (TaxRate.TEN_PERCENT.equalsByCode(entity.getTaxRateType())) {
					// 対象額
					accgInvoiceStatementAmountDto.setTotalTaxAmount10TargetFeeNonDiscount(entity.getTaxableAmount());
					accgInvoiceStatementAmountDto.setTotalTaxAmount10TargetDiscount(entity.getDiscountTaxableAmount());
					// 消費税
					accgInvoiceStatementAmountDto.setTotalTaxAmount10NonDiscount(entity.getTaxAmount());
					accgInvoiceStatementAmountDto.setTotalTaxAmount10Discount(entity.getDiscountTaxAmount());
				}
			}
		}
		
		// 源泉徴収額データ取得
		TAccgInvoiceWithholdingEntity tAccgInvoiceWithholdingEntity = tAccgInvoiceWithholdingDao.selectAccgInvoiceWithholdingByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceWithholdingEntity != null) {
			accgInvoiceStatementAmountDto.setTotalWithholdingAmount(tAccgInvoiceWithholdingEntity.getWithholdingAmount());
			accgInvoiceStatementAmountDto.setTotalWithholdingTargetFee(tAccgInvoiceWithholdingEntity.getSourceWithholdingAmount());
		}
		
		return accgInvoiceStatementAmountDto;
	}
}
