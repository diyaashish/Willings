package jp.loioz.app.user.feeDetail.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0020ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.FeeDetailListExcelDto;
import jp.loioz.app.common.excel.dto.FeeDetailListExcelDto.ExcelFeeDetailListRowData;
import jp.loioz.app.user.feeDetail.form.FeeDetailSearchForm;
import jp.loioz.bean.FeeDetailListBean;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.CommonConstant.FeePaymentStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.CommonConstant.WithholdingFlg;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 報酬明細画面のExcel出力サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeDetailDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 報酬Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	/** 顧客情報用Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件情報用Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** メッセージサービス */
	@Autowired
	MessageService messageService;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬明細情報をExcelで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputFeeDetailExcel(HttpServletResponse response, FeeDetailSearchForm searchForm) throws Exception {

		Long personId = searchForm.getPersonId();
		Long ankenId = searchForm.getAnkenId();

		// ■1.Builderを定義
		En0020ExcelBuilder en0020excelBuilder = new En0020ExcelBuilder();
		en0020excelBuilder.setConfig(excelConfig);
		en0020excelBuilder.setMessageService(messageService);
		en0020excelBuilder.setFileName(StringUtils.removeSpaceCharacter(
				en0020excelBuilder.createFileName(this.getPersonName(personId), this.getAnkenName(ankenId))));

		// ■2.DTOを定義
		FeeDetailListExcelDto feeDetailListExcelDto = en0020excelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータ取得と設定
		feeDetailListExcelDto.setFeeDetailListDtoList(this.getFeeDetailExcelData(searchForm));
		feeDetailListExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));
		feeDetailListExcelDto.setPersonName("名前：" + this.getPersonName(personId));
		feeDetailListExcelDto.setAnkenName("案件名：" + this.getAnkenName(ankenId));

		en0020excelBuilder.setFeeDetailListExcelDto(feeDetailListExcelDto);
		try {
			// Excelファイルの出力処理
			en0020excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * Excel出力用に報酬明細データを取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelFeeDetailListRowData> getFeeDetailExcelData(FeeDetailSearchForm searchForm) {

		// 明細一覧情報を取得
		List<FeeDetailListBean> detailBeanList = tFeeDao.selectFeeDetailListByParams(
				searchForm.getAnkenId(), searchForm.getPersonId(), searchForm.toFeeDetailSortCondition());

		return convertFeeDetailListBeanForExcelDataList(detailBeanList);
	}


	/**
	 * list<FeeDetailListBean>型からlist<ExcelFeeDetailListRowData>型に変換します
	 * 
	 * @param feeDetailListBean
	 * @return
	 */
	private List<ExcelFeeDetailListRowData> convertFeeDetailListBeanForExcelDataList(
			List<FeeDetailListBean> feeDetailListBean) {

		// Bean型をDto型に変換する
		List<ExcelFeeDetailListRowData> excelFeeDetailDataList = new ArrayList<ExcelFeeDetailListRowData>();
		feeDetailListBean.forEach(bean -> {
			excelFeeDetailDataList.add(convertFeeDetailBeanForExcelData(bean));
		});

		return excelFeeDetailDataList;
	}

	/**
	 * FeeDetailListBean型からExcelFeeDetailListRowData型に変換します<br>
	 * 
	 * @param rowData
	 * @return
	 */
	private ExcelFeeDetailListRowData convertFeeDetailBeanForExcelData(FeeDetailListBean rowData) {
		
		ExcelFeeDetailListRowData excelFeeDetailListRowData = new ExcelFeeDetailListRowData();
		
		// 項目
		if (!StringUtils.isEmpty(rowData.getFeeItemName())) {
			excelFeeDetailListRowData.setFeeItemName(rowData.getFeeItemName());
		}
		
		// 発生日
		if (rowData.getFeeDate() != null) {
			excelFeeDetailListRowData
					.setFeeDate(DateUtils.parseToString(rowData.getFeeDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}
		
		// 報酬ステータス
		if (!StringUtils.isEmpty(rowData.getFeePaymentStatus())) {
			excelFeeDetailListRowData.setFeePaymentStatus(FeePaymentStatus.of(rowData.getFeePaymentStatus()).getVal());
		}
		
		// 回収不能
		if (SystemFlg.FLG_ON.equalsByCode(rowData.getUncollectibleFlg())) {
			String feePaymentStatus = excelFeeDetailListRowData.getFeePaymentStatus();
			feePaymentStatus = feePaymentStatus + "\r\n*"
					+ messageService.getMessage(MessageEnum.MSG_W00022, SessionUtils.getLocale());
			excelFeeDetailListRowData.setFeePaymentStatus(feePaymentStatus);
		}
		
		// 消費税
		if (!StringUtils.isEmpty(rowData.getTaxRateType())) {
			if (TaxRate.TAX_FREE.equalsByCode(rowData.getTaxRateType())) {
				excelFeeDetailListRowData.setTaxRateType("なし");
			} else {
				excelFeeDetailListRowData.setTaxRateType(TaxRate.of(rowData.getTaxRateType()).getVal() + "%");
			}
		}
		
		// 源泉徴収
		if (!StringUtils.isEmpty(rowData.getWithholdingFlg())) {
			excelFeeDetailListRowData.setWithholding(WithholdingFlg.of(rowData.getWithholdingFlg()).getVal());
		}
		
		// 報酬額（税込）
		if (rowData.getFeeAmountTaxIn() != null) {
			excelFeeDetailListRowData.setFeeAmountTaxIn(rowData.getFeeAmountTaxIn());
		}
		
		// 請求書番号／精算書番号
		if (!StringUtils.isEmpty(rowData.getInvoiceNo()) || !StringUtils.isEmpty(rowData.getStatementNo())) {
			excelFeeDetailListRowData.setInvoiceStatementNo(
					StringUtils.isEmpty(rowData.getInvoiceNo()) ? "精算 " + rowData.getStatementNo()
							: "請求 " + rowData.getInvoiceNo());
		}
		
		// タイムチャージ単価
		if (rowData.getHourPrice() != null) {
			excelFeeDetailListRowData.setHourPrice(rowData.getHourPrice());
		}
		
		// タイムチャージ時間
		if (rowData.getWorkTimeMinute() != null) {
			excelFeeDetailListRowData.setWorkTimeMinute(rowData.getWorkTimeMinute().toString());
		}
		
		// メモ
		if (!StringUtils.isEmpty(rowData.getFeeMemo())) {
			excelFeeDetailListRowData.setFeeMemo(rowData.getFeeMemo());
		}
		
		// 摘要
		if (!StringUtils.isEmpty(rowData.getSumText())) {
			excelFeeDetailListRowData.setSumText(rowData.getSumText());
		}
		
		return excelFeeDetailListRowData;
	}

	/**
	 * 顧客名を取得します。
	 * 
	 * @param personId
	 * @return
	 */
	private String getPersonName(Long personId) {
		TPersonEntity tPersonEntity = tPersonDao.selectPersonByPersonId(personId);
		if (tPersonEntity == null) {
			return "";
		}
		return PersonName.fromEntity(tPersonEntity).getName();
	}

	/**
	 * 案件名を取得します。
	 * 
	 * @param ankenId
	 * @return
	 */
	private String getAnkenName(Long ankenId) {
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		if (tAnkenEntity == null || StringUtils.isEmpty(tAnkenEntity.getAnkenName())) {
			return "(案件名未入力)";
		}
		return tAnkenEntity.getAnkenName();
	}

}