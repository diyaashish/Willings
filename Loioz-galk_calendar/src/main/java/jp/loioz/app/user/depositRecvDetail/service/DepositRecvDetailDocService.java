package jp.loioz.app.user.depositRecvDetail.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0021ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.DepositRecvDetailListExcelDto;
import jp.loioz.app.common.excel.dto.DepositRecvDetailListExcelDto.ExcelDepositRecvDetailListRowData;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailSearchForm;
import jp.loioz.bean.DepositRecvDetailListBean;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 預り金明細画面のExcel出力サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecvDetailDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 報酬Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

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
	 * 預り金明細情報をExcelで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputDepositRecvDetailExcel(HttpServletResponse response, DepositRecvDetailSearchForm searchForm)
			throws Exception {

		Long personId = searchForm.getPersonId();
		Long ankenId = searchForm.getAnkenId();

		// ■1.Builderを定義
		En0021ExcelBuilder en0021excelBuilder = new En0021ExcelBuilder();
		en0021excelBuilder.setConfig(excelConfig);
		en0021excelBuilder.setMessageService(messageService);
		en0021excelBuilder.setFileName(StringUtils.removeSpaceCharacter(
				en0021excelBuilder.createFileName(this.getPersonName(personId), this.getAnkenName(ankenId))));

		// ■2.DTOを定義
		DepositRecvDetailListExcelDto depositRecvDetailListExcelDto = en0021excelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータ取得と設定
		depositRecvDetailListExcelDto.setDepositRecvDetailListDtoList(this.getDepositRecvDetailExcelData(searchForm));
		depositRecvDetailListExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));
		depositRecvDetailListExcelDto.setPersonName("名前：" + this.getPersonName(personId));
		depositRecvDetailListExcelDto.setAnkenName("案件名：" + this.getAnkenName(ankenId));

		en0021excelBuilder.setDepositRecvDetailListExcelDto(depositRecvDetailListExcelDto);
		try {
			// Excelファイルの出力処理
			en0021excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * Excel出力用に預り金明細データを取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelDepositRecvDetailListRowData> getDepositRecvDetailExcelData(DepositRecvDetailSearchForm searchForm) {

		// 明細一覧情報を取得
		List<DepositRecvDetailListBean> detailBeanList = tDepositRecvDao.selectDepositRecvDetailListByParams(
				searchForm.getAnkenId(), searchForm.getPersonId(), searchForm.toDepositRecvDetailSortCondition());

		return convertDepositRecvDetailListBeanForExcelDataList(detailBeanList);
	}


	/**
	 * list<DepositRecvDetailListBean>型からlist<ExcelDepositRecvDetailListRowData>型に変換します
	 * 
	 * @param depositRecvDetailListBean
	 * @return
	 */
	private List<ExcelDepositRecvDetailListRowData> convertDepositRecvDetailListBeanForExcelDataList(
			List<DepositRecvDetailListBean> depositRecvDetailListBean) {

		// Bean型をDto型に変換する
		List<ExcelDepositRecvDetailListRowData> excelDepositRecvDetailDataList = new ArrayList<ExcelDepositRecvDetailListRowData>();
		depositRecvDetailListBean.forEach(bean -> {
			excelDepositRecvDetailDataList.add(convertDepositRecvDetailBeanForExcelData(bean));
		});

		return excelDepositRecvDetailDataList;
	}

	/**
	 * DepositRecvDetailListBean型からExcelDepositRecvDetailListRowData型に変換します<br>
	 * 
	 * @param rowData
	 * @return
	 */
	private ExcelDepositRecvDetailListRowData convertDepositRecvDetailBeanForExcelData(
			DepositRecvDetailListBean rowData) {
		
		ExcelDepositRecvDetailListRowData excelDepositRecvDetailListRowData = new ExcelDepositRecvDetailListRowData();
		
		// 入出金タイプ
		if (!StringUtils.isEmpty(rowData.getDepositType())) {
			excelDepositRecvDetailListRowData.setDepositTypeCd(rowData.getDepositType());
			excelDepositRecvDetailListRowData.setDepositType(DepositType.of(rowData.getDepositType()).getVal());
		}
		
		// 項目
		if (!StringUtils.isEmpty(rowData.getDepositItemName())) {
			excelDepositRecvDetailListRowData.setDepositItemName(rowData.getDepositItemName());
		}
		
		// 発生日
		if (rowData.getDepositDate() != null) {
			excelDepositRecvDetailListRowData.setDepositDate(
					DateUtils.parseToString(rowData.getDepositDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}
		
		// 回収不能フラグ
		if (!StringUtils.isEmpty(rowData.getUncollectibleFlg())) {
			excelDepositRecvDetailListRowData.setUncollectibleFlg(rowData.getUncollectibleFlg());
			excelDepositRecvDetailListRowData.setUncollectibleWarningMessage(
					messageService.getMessage(MessageEnum.MSG_W00022, SessionUtils.getLocale()));
		}
		
		// 入出金完了フラグ
		if (!StringUtils.isEmpty(rowData.getDepositCompleteFlg())) {
			excelDepositRecvDetailListRowData.setDepositCompleteFlg(rowData.getDepositCompleteFlg());
		}
		
		// 入金額
		if (!StringUtils.isEmpty(rowData.getDepositAmount()) && !rowData.getDepositAmount().equals("0")) {
			excelDepositRecvDetailListRowData
					.setDepositAmount(LoiozNumberUtils.parseAsBigDecimal(rowData.getDepositAmount()));
		}
		
		// 出金額
		if (!StringUtils.isEmpty(rowData.getWithdrawalAmount()) && !rowData.getWithdrawalAmount().equals("0")) {
			excelDepositRecvDetailListRowData
					.setWithdrawalAmount(LoiozNumberUtils.parseAsBigDecimal(rowData.getWithdrawalAmount()));
		}
		
		// 事務所負担フラグ
		excelDepositRecvDetailListRowData
				.setTenantBear(SystemFlg.FLG_ON.equalsByCode(rowData.getTenantBearFlg()) ? true : false);
		
		// 作成元請求書番号／作成元精算書番号
		if (!StringUtils.isEmpty(rowData.getCreatedInvoiceNo())
				|| !StringUtils.isEmpty(rowData.getCreatedStatementNo())) {
			excelDepositRecvDetailListRowData.setInvoiceStatementNo(
					StringUtils.isEmpty(rowData.getCreatedInvoiceNo()) ? "精算 " + rowData.getCreatedStatementNo()
							: "請求 " + rowData.getCreatedInvoiceNo());
		}
		
		// 使用先請求書番号／使用先精算書番号
		if (!StringUtils.isEmpty(rowData.getUsingInvoiceNo())
				|| !StringUtils.isEmpty(rowData.getUsingStatementNo())) {
			String invoiceStatementNo = StringUtils.isEmpty(excelDepositRecvDetailListRowData.getInvoiceStatementNo())
					? ""
					: excelDepositRecvDetailListRowData.getInvoiceStatementNo() + "\r\n";
			invoiceStatementNo = invoiceStatementNo
					+ (StringUtils.isEmpty(rowData.getUsingInvoiceNo()) ? "精算 " + rowData.getUsingStatementNo()
							: "請求 " + rowData.getUsingInvoiceNo());
			excelDepositRecvDetailListRowData.setInvoiceStatementNo(invoiceStatementNo);
		}
		
		// 摘要
		if (!StringUtils.isEmpty(rowData.getSumText())) {
			excelDepositRecvDetailListRowData.setSumText(rowData.getSumText());
		}
		
		// メモ
		if (!StringUtils.isEmpty(rowData.getDepositRecvMemo())) {
			excelDepositRecvDetailListRowData.setDepositRecvMemo(rowData.getDepositRecvMemo());
		}
		
		return excelDepositRecvDetailListRowData;
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