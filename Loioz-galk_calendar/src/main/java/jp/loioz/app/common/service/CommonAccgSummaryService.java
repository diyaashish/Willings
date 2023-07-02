package jp.loioz.app.common.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.accg.AccgCaseForm;
import jp.loioz.app.common.form.accg.AccgCasePersonForm;
import jp.loioz.app.common.form.accg.AccgCaseSummaryForm;
import jp.loioz.bean.AccgFeeSumListBean;
import jp.loioz.bean.AccgInvoiceStatementBean;
import jp.loioz.bean.AccgSalesTotalBean;
import jp.loioz.bean.AnkenAddKeijiBean;
import jp.loioz.bean.AnkenKanyoshaBean;
import jp.loioz.bean.DepositRecvSummaryBean;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.FeePaymentStatus;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TSalesDao;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.dto.AccgSalesFeeDepositTotalDto;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.PersonInfoForAnkenDto;

/**
 * 会計管理：サマリ情報-共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonAccgSummaryService extends DefaultService {

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件-顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 報酬Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 売上Daoクラス */
	@Autowired
	private TSalesDao tSalesDao;

	/** 会計書類Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 共通アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 共通分野サービスクラス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 会計管理機能の案件情報を取得します
	 * 
	 * @param ankenId
	 * @param personId
	 */
	public AccgCaseForm getAccgCase(Long ankenId, Long personId) {
		AccgCaseForm accgCaseForm = new AccgCaseForm();

		// 対象案件ID
		List<Long> ankenIdList = Arrays.asList(ankenId);

		// 案件IDに紐づく顧客情報の取得
		List<PersonInfoForAnkenDto> personInfoForAnkenDtoList = tAnkenCustomerDao.selectPersonInfoForAnkenByAnkenId(ankenIdList);
		// 複数顧客か判定
		if (personInfoForAnkenDtoList.size() > 1) {
			// 複数顧客
			accgCaseForm.setCustomers(true);
		} else {
			// 単一顧客
			accgCaseForm.setCustomers(false);
		}

		// 名簿情報を取得
		PersonInfoForAnkenDto personInfoForAnkenDto = personInfoForAnkenDtoList.stream()
				.filter(dto -> personId.equals(dto.getPersonId())).findFirst().get();
		accgCaseForm.setPersonName(personInfoForAnkenDto.getCustomerName());

		// 案件（刑事も含めて）情報を取得
		AnkenAddKeijiBean bean = tAnkenDao.selectAddKeijiById(ankenId);
		BunyaDto bunyaDto = commonBunyaService.getBunya(bean.getBunyaId());

		// 案件情報を設定
		accgCaseForm.setAnkenId(ankenId);
		accgCaseForm.setAnkenName(bean.getAnkenName());
		accgCaseForm.setAnkenType(AnkenType.of(bean.getAnkenType()));
		accgCaseForm.setAnkenCreatedDate(bean.getAnkenCreatedDate());

		// 分野に関する情報を設定
		accgCaseForm.setBunyaName(bunyaDto.getBunyaName());
		accgCaseForm.setKeiji(bunyaDto.isKeiji());
		if (bunyaDto.isKeiji()) {
			// 刑事の場合
			accgCaseForm.setBengoType(BengoType.of(bean.getLawyerSelectType()));
		}

		// 案件情報（相手方）をセット
		List<AccgCaseForm.Aitegata> detailHeaderAitegataList = this.getAitegata(ankenId);
		accgCaseForm.setRelatedAitegata(detailHeaderAitegataList);

		// 案件情報（担当弁護士、担当事務）をセット
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();
		List<AnkenTantoDto> ankenTantoDto = commonAnkenService.getAnkenTantoListByAnkenId(ankenId);

		// 売上計上先
		List<AccgCaseForm.AnkenTanto> salesOwner = ankenTantoDto.stream()
				.filter(e -> TantoType.SALES_OWNER.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());
		accgCaseForm.setSalesOwner(salesOwner);

		// 担当弁護士
		List<AccgCaseForm.AnkenTanto> tantoLaywer = ankenTantoDto.stream()
				.filter(e -> TantoType.LAWYER.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());
		accgCaseForm.setTantoLaywer(tantoLaywer);

		// 担当事務
		List<AccgCaseForm.AnkenTanto> tantoJimu = ankenTantoDto.stream()
				.filter(e -> TantoType.JIMU.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());
		accgCaseForm.setTantoJimu(tantoJimu);

		return accgCaseForm;
	}

	/**
	 * 会計管理機能の売上、預り金明細、案件顧客を取得します
	 * 
	 * @param ankenId
	 * @param personId
	 */
	public AccgCaseSummaryForm getAccgCaseSummary(Long ankenId, Long personId) {
		AccgCaseSummaryForm accgCaseSummaryForm = new AccgCaseSummaryForm();

		// 案件IDを設定
		accgCaseSummaryForm.setAnkenId(ankenId);
		// 名簿IDを設定
		accgCaseSummaryForm.setPersonId(personId);

		// 報酬、預り金、売上サマリー情報を取得
		AccgSalesFeeDepositTotalDto accgSalesFeeDepositTotalDto = this.getAccgSalesFeeDepositSummaryDto(ankenId, personId);

		// 報酬をセット
		// 報酬合計（税込）
		accgCaseSummaryForm.setFeeTotalAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getFeeTotalAmount()));
		// 未請求報酬
		accgCaseSummaryForm.setFeeUnclaimedTotalAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getFeeUnclaimedTotalAmount()));

		// 預り金セット
		accgCaseSummaryForm.setTotalDepositBalanceAmount(accgSalesFeeDepositTotalDto.getTotalDepositBalanceAmount());
		accgCaseSummaryForm.setTotalDepositAmount(accgSalesFeeDepositTotalDto.getTotalDepositAmount());
		accgCaseSummaryForm.setTotalWithdrawalAmount(accgSalesFeeDepositTotalDto.getTotalWithdrawalAmount());
		accgCaseSummaryForm.setTotalTenantBearAmount(accgSalesFeeDepositTotalDto.getTotalTenantBearAmount());

		// 売上をセット

		// 売上合計【見込】（税込）
		accgCaseSummaryForm.setSalesAmountExpect(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesAmountExpect()));
		// 売上合計【実績】（税込）
		accgCaseSummaryForm.setSalesAmountResult(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesAmountResult()));

		// 売上合計
		accgCaseSummaryForm.setSalesTaxIncludedAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesTaxIncludedTotalAmount()));
		// 入金待ち
		accgCaseSummaryForm.setSalesAwaitingPaymentAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesAwaitingPaymentAmount()));
		// 源泉徴収税
		accgCaseSummaryForm.setSalesWithholdingAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesWithholdingTotalAmount()));
		// 値引き額（税込み）
		accgCaseSummaryForm.setSalesDiscountAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesDiscountTaxIncludedTotalAmount()));

		return accgCaseSummaryForm;
	}

	/**
	 * 「案件の会計管理」案件に紐づく顧客を取得
	 * 
	 * @param ankenId
	 * @param personId
	 */
	public AccgCasePersonForm getAccgCasePerson(Long ankenId, Long personId) {
		AccgCasePersonForm accgCasePersonForm = new AccgCasePersonForm();
		// 案件ID
		accgCasePersonForm.setAnkenId(ankenId);
		// 名簿ID
		accgCasePersonForm.setPersonId(personId);

		// 案件に紐づく顧客を取得
		List<AccgCasePersonForm.Customer> customerList = this.getAccgCasePersonCustomer(ankenId, personId);
		accgCasePersonForm.setRelatedCustomer(customerList);

		// 複数顧客存在するか
		accgCasePersonForm.setClients(customerList.size() > 1);

		return accgCasePersonForm;
	}

	/**
	 * 会計書類SEQをキーに請求書or精算書 情報を取得します。<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementBean getAccgInvoiceStatementDetail(Long accgDocSeq) throws AppException {
		AccgInvoiceStatementBean accgInvoiceStatementBean = tAccgDocDao.selectAccgInvoiceStateBeanByAccgDocSeq(accgDocSeq);
		if (accgInvoiceStatementBean == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		return accgInvoiceStatementBean;
	}

	/**
	 * 会計 報酬、預り金、売上サマリー情報の取得
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	public AccgSalesFeeDepositTotalDto getAccgSalesFeeDepositSummaryDto(Long ankenId, Long personId) {

		var accgFeeDepositTotalDto = new AccgSalesFeeDepositTotalDto();

		// ID情報の設定
		accgFeeDepositTotalDto.setAnkenId(ankenId);
		accgFeeDepositTotalDto.setPersonId(personId);

		// ステータス毎の報酬合計を取得
		List<AccgFeeSumListBean> accgFeeSumListBean = tFeeDao.selectSumFeeStatusByPersonIdAndAnkenId(personId, ankenId);
		accgFeeSumListBean.forEach(bean -> {
			// 報酬税込み金額
			BigDecimal statusTotalAmount = AccountingUtils.calcTotal(bean.getFeeAmount(), bean.getTaxAmount());

			switch (FeePaymentStatus.of(bean.getFeePaymentStatus())) {
			// 未請求
			case UNCLAIMED:
				accgFeeDepositTotalDto.setFeeUnclaimedTotalAmount(statusTotalAmount);
				break;

			// 入金待ち
			case AWAITING_PAYMENT:
				accgFeeDepositTotalDto.setFeeWaitingPaymentTotalAmount(statusTotalAmount);
				break;

			// 入金済み
			case DEPOSITED:
				accgFeeDepositTotalDto.setFeeDepositedTotalAmount(statusTotalAmount);
				break;

			// 一部入金
			case PARTIAL_DEPOSIT:
				accgFeeDepositTotalDto.setFeePartialDepositTotalAmount(statusTotalAmount);
				break;
			}
		});
		// 請求済み（未請求分を含めない）
		BigDecimal totalFeeAlreadyClaimedAmount = AccountingUtils.calcTotal(
				accgFeeDepositTotalDto.getFeeWaitingPaymentTotalAmount(),
				accgFeeDepositTotalDto.getFeeDepositedTotalAmount(),
				accgFeeDepositTotalDto.getFeePartialDepositTotalAmount());
		accgFeeDepositTotalDto.setFeeAlreadyClaimedTotalAmount(totalFeeAlreadyClaimedAmount);

		// 報酬合計（請求済み＋未請求分）
		BigDecimal totalAmount = AccountingUtils.calcTotal(
				accgFeeDepositTotalDto.getFeeUnclaimedTotalAmount(),
				totalFeeAlreadyClaimedAmount);
		accgFeeDepositTotalDto.setFeeTotalAmount(totalAmount);

		// 預り金情報を取得し「報酬明細、預り金明細」ヘッダーフォームにセット
		DepositRecvSummaryBean depositRecvSummaryBean = tDepositRecvDao.selectSummaryByParams(ankenId, personId);
		accgFeeDepositTotalDto.setTotalDepositBalanceAmount(depositRecvSummaryBean.getTotalDepositBalanceAmount());
		accgFeeDepositTotalDto.setTotalDepositAmount(depositRecvSummaryBean.getTotalDepositAmount());
		accgFeeDepositTotalDto.setTotalWithdrawalAmount(depositRecvSummaryBean.getTotalWithdrawalAmount());
		accgFeeDepositTotalDto.setTotalTenantBearAmount(depositRecvSummaryBean.getTotalTenantBearAmount());

		// 売上合計情報を取得
		AccgSalesTotalBean accgSalesTotalBean = tSalesDao.selectSalesTotalByPersonIdAndAnkenId(personId, ankenId);

		// 売上合計【見込】（税込）
		accgFeeDepositTotalDto.setSalesAmountExpect(accgSalesTotalBean.getSalesAmountExpect());
		// 売上合計【実績】（税込）
		accgFeeDepositTotalDto.setSalesAmountResult(accgSalesTotalBean.getSalesAmountResult());

		// 売上（税抜）＋ 消費税
		BigDecimal totalSalesAmountIncludeTax = AccountingUtils
				.calcTotal(accgSalesTotalBean.getSalesTotalAmount(), accgSalesTotalBean.getSalesTaxTotalAmount());
		// 売上金額（税込）※値引き含めない
		accgFeeDepositTotalDto.setSalesTaxIncludedTotalAmount(totalSalesAmountIncludeTax);

		// 値引き額（税抜）＋値引き_消費税額
		BigDecimal totalDiscountAmountIncludeTax = AccountingUtils
				.calcTotal(accgSalesTotalBean.getSalesDiscountTotalAmount(), accgSalesTotalBean.getSalesDiscountTaxTotalAmount());
		// 値引き合計（税込）
		accgFeeDepositTotalDto.setSalesDiscountTaxIncludedTotalAmount(totalDiscountAmountIncludeTax);

		// 売上金額（税抜）
		accgFeeDepositTotalDto.setSalesTotalAmount(accgSalesTotalBean.getSalesTotalAmount());
		// 消費税額
		accgFeeDepositTotalDto.setSalesTaxTotalAmount(accgSalesTotalBean.getSalesTaxTotalAmount());

		// 値引き_売上金額（税抜）
		accgFeeDepositTotalDto.setSalesDiscountTotalAmount(accgSalesTotalBean.getSalesDiscountTotalAmount());
		// 値引き_消費税額
		accgFeeDepositTotalDto.setSalesDiscountTaxTotalAmount(accgSalesTotalBean.getSalesDiscountTaxTotalAmount());

		// 源泉徴収税額
		accgFeeDepositTotalDto.setSalesWithholdingTotalAmount(accgSalesTotalBean.getSalesWithholdingTotalAmount());

		// 入金待ち
		BigDecimal salesAwaitingPaymentTotalAmount = AccountingUtils
				.calcSubtract(accgSalesTotalBean.getSalesAmountExpect(), accgSalesTotalBean.getSalesAmountResult());
		accgFeeDepositTotalDto.setSalesAwaitingPaymentAmount(salesAwaitingPaymentTotalAmount);

		return accgFeeDepositTotalDto;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 会計管理：サマリ情報に表示する案件情報（相手方）をセットします。<br>
	 * 
	 * @param accgCaseForm
	 */
	private List<AccgCaseForm.Aitegata> getAitegata(Long ankenId) {

		if (ankenId == null) {
			return Collections.emptyList();
		}

		// 対象案件ID
		List<Long> ankenIdList = Arrays.asList(ankenId);

		// 案件IDに紐づく関与者情報の取得
		List<AnkenKanyoshaBean> ankenKanyoshaList = tKanyoshaDao.selectAnkenRelatedKanyoshaByAnkenIdList(ankenIdList);

		// 相手方
		List<AnkenKanyoshaBean> aitegataBeanList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.AITEGATA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());

		// 相手方リストから代理人を除く
		List<AccgCaseForm.Aitegata> aitegataList = aitegataBeanList.stream()
				.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt)) // 作成日時でソート
				.filter(e -> SystemFlg.FLG_OFF.equalsByCode(e.getDairiFlg())) // 代理人を除外
				.map(e -> {
					return AccgCaseForm.Aitegata.builder()
							.kanyoshaSeq(e.getKanyoshaSeq())
							.kanyoshaName(e.getKanyoshaName())
							.build();
				}).collect(Collectors.toList());

		return aitegataList;
	}

	/**
	 * 会計管理：サマリ情報に表示する案件＋名簿情報を取得します。<br>
	 * 
	 * @param headerForm
	 */
	private List<AccgCasePersonForm.Customer> getAccgCasePersonCustomer(Long ankenId, Long personId) {

		// 対象案件ID
		List<Long> ankenIdList = Arrays.asList(ankenId);

		// 案件IDに紐づく顧客情報の取得
		List<PersonInfoForAnkenDto> personInfoForAnkenDtoList = tAnkenCustomerDao.selectPersonInfoForAnkenByAnkenId(ankenIdList);

		// ソートキー 名簿ID
		Function<PersonInfoForAnkenDto, Long> personIdMapper = PersonInfoForAnkenDto::getPersonId;

		// 顧客一覧を作成
		List<AccgCasePersonForm.Customer> customerList = personInfoForAnkenDtoList.stream()
				.sorted(Comparator.comparing(personIdMapper))
				.map(e -> {
					return AccgCasePersonForm.Customer.builder()
							.personId(e.getPersonId())
							.personName(e.getCustomerName())
							.ankenStatus(AnkenStatus.of(e.getAnkenStatus()))
							.customerType(CustomerType.of(e.getCustomerType()))
							.personAttribute(PersonAttribute.of(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()))
							.build();
				})
				.collect(Collectors.toList());

		return customerList;
	}

	/**
	 * 担当者情報を表示用Dtoに変換する
	 * 
	 * @param ankenTantoDto
	 * @param accountNameMap
	 * @return
	 */
	private AccgCaseForm.AnkenTanto comvert2AnkenTantoDispDto(AnkenTantoDto ankenTantoDto, Map<Long, String> accountNameMap) {
		return AccgCaseForm.AnkenTanto.builder()
				.accountSeq(ankenTantoDto.getAccountSeq())
				.accountName(accountNameMap.getOrDefault(ankenTantoDto.getAccountSeq(), ""))
				.accountColor(ankenTantoDto.getAccountColor())
				.isMain(SystemFlg.codeToBoolean(ankenTantoDto.getAnkenMainTantoFlg()))
				.tantoTypeBranchNo(ankenTantoDto.getTantoTypeBranchNo())
				.build();
	}

}