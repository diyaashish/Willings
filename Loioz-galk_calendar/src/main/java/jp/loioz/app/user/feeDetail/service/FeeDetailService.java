package jp.loioz.app.user.feeDetail.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.form.accg.AccgCaseForm;
import jp.loioz.app.common.form.accg.AccgCasePersonForm;
import jp.loioz.app.common.form.accg.AccgCaseSummaryForm;
import jp.loioz.app.common.service.CommonAccgAmountService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonAccgSummaryService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.user.feeDetail.dto.FeeDetailListDto;
import jp.loioz.app.user.feeDetail.form.FeeDetailInputForm;
import jp.loioz.app.user.feeDetail.form.FeeDetailSearchForm;
import jp.loioz.app.user.feeDetail.form.FeeDetailViewForm;
import jp.loioz.bean.AccgInvoiceStatementBean;
import jp.loioz.bean.FeeDetailListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AccgMenu;
import jp.loioz.common.constant.CommonConstant.FeePaymentStatus;
import jp.loioz.common.constant.CommonConstant.GensenChoshu;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAccgDocInvoiceDao;
import jp.loioz.dao.TAccgDocInvoiceFeeDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TFeeAddTimeChargeDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TSalesDao;
import jp.loioz.dao.TSalesDetailDao;
import jp.loioz.dao.TSalesDetailTaxDao;
import jp.loioz.dto.AccgInvoiceStatementAmountDto;
import jp.loioz.entity.TAccgDocInvoiceEntity;
import jp.loioz.entity.TAccgDocInvoiceFeeEntity;
import jp.loioz.entity.TFeeAddTimeChargeEntity;
import jp.loioz.entity.TFeeEntity;
import jp.loioz.entity.TSalesDetailEntity;
import jp.loioz.entity.TSalesDetailTaxEntity;
import jp.loioz.entity.TSalesEntity;

/**
 * 報酬明細画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeDetailService extends DefaultService {

	/** 報酬Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	/** タイムチャージ設定Daoクラス */
	@Autowired
	private TFeeAddTimeChargeDao tFeeAddTimeChargeDao;

	/** 請求項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceDao tAccgDocInvoiceDao;

	/** 請求報酬項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceFeeDao tAccgDocInvoiceFeeDao;

	/** 売上Daoクラス */
	@Autowired
	private TSalesDao tSalesDao;

	/** 売上明細Daoクラス */
	@Autowired
	private TSalesDetailDao tSalesDetailDao;

	/** 売上明細-消費税Daoクラス */
	@Autowired
	private TSalesDetailTaxDao tSalesDetailTaxDao;

	/** 案件-担当者Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** アカウント共通サービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 会計管理：預り金詳細、報酬詳細の共通サービス */
	@Autowired
	private CommonAccgSummaryService commonAccgSummaryService;

	/** 会計管理共通サービスクラス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 会計管理の金額を扱う共通サービス */
	@Autowired
	private CommonAccgAmountService commonAccgAmountService;

	/** メッセージサービスクラス */
	@Autowired
	private MessageService messageService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 明細画面表示情報を作成する
	 * 
	 * @return
	 */
	public FeeDetailViewForm createViewForm() {
		FeeDetailViewForm viewForm = new FeeDetailViewForm();
		return viewForm;
	}

	/**
	 * 明細画面入力用フォームを作成する
	 * 
	 * @return
	 */
	public FeeDetailInputForm createInputForm() {
		FeeDetailInputForm inputForm = new FeeDetailInputForm();
		return inputForm;
	}

	/**
	 * 会計管理の共通情報を取得します
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void getAccgData(FeeDetailViewForm viewForm, FeeDetailSearchForm searchForm) {
		// 案件「会計管理」の案件情報
		AccgCaseForm accgCaseForm = commonAccgSummaryService.getAccgCase(searchForm.getAnkenId(), searchForm.getPersonId());
		viewForm.setAccgCaseForm(accgCaseForm);

		// 案件「会計管理」の報酬、預り金合計
		AccgCaseSummaryForm accgCaseSummaryForm = commonAccgSummaryService.getAccgCaseSummary(searchForm.getAnkenId(), searchForm.getPersonId());
		viewForm.setAccgCaseSummaryForm(accgCaseSummaryForm);

		// 案件「会計管理」の顧客一覧
		AccgCasePersonForm accgCasePersonForm = commonAccgSummaryService.getAccgCasePerson(searchForm.getAnkenId(), searchForm.getPersonId());
		viewForm.setAccgCasePersonForm(accgCasePersonForm);

		// 報酬選択
		accgCaseForm.setAccgMenu(AccgMenu.FEE);

	}

	/**
	 * 明細一覧情報を取得します
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void searchFeeDetail(FeeDetailViewForm viewForm, FeeDetailSearchForm searchForm) {
		// 明細一覧情報を取得
		List<FeeDetailListBean> detailBeanList = tFeeDao.selectFeeDetailListByParams(
				searchForm.getAnkenId(), searchForm.getPersonId(), searchForm.toFeeDetailSortCondition());

		// Dto変換してviewFormにセット
		List<FeeDetailListDto> detailDtoList = this.convertBeanList2Dto(detailBeanList);
		viewForm.setFeeDetailList(detailDtoList);

		// 一覧表示のソートキーとソート順をviewFormにセット
		viewForm.setFeeDetailSortItem(searchForm.getFeeDetailSortItem());
		viewForm.setFeeDetailSortOrder(searchForm.getFeeDetailSortOrder());
	}

	/**
	 * 報酬登録時の相関バリデーションチェックです。<br>
	 * 
	 * @param feeDetailInputForm
	 * @param result
	 * @throws AppException
	 */
	public void validateFeeDetailInputForm(FeeDetailInputForm feeDetailInputForm, BindingResult result) throws AppException {
		// 請求書や精算書で使用されていない報酬の場合、入金ステータスが「入金済み」の場合は発生日が必須
		if (!feeDetailInputForm.createdInvoiceStatement()
				&& FeePaymentStatus.DEPOSITED.equalsByCode(feeDetailInputForm.getFeePaymentStatus())
				&& StringUtils.isEmpty(feeDetailInputForm.getFeeDate())) {
			result.rejectValue("feeDate", null, messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
		}
		
		// 請求書や精算書で使用されている報酬の場合、入金ステータスは変更できない
		if (this.checkIfAboutToChangeFeePaymentStatusOfAccgDoc(feeDetailInputForm)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * 報酬データを登録します
	 * 
	 * @param inputForm
	 * @param searchForm
	 * @throws AppException
	 */
	public void registFee(FeeDetailInputForm inputForm, FeeDetailSearchForm searchForm) throws AppException {
		// 既に報酬の登録上限に達している場合
		List<FeeDetailListBean> list = tFeeDao.selectFeeDetailListByParams(searchForm.getAnkenId(), searchForm.getPersonId(), searchForm.toFeeDetailSortCondition());
		if (!list.isEmpty() && list.size() >= CommonConstant.FEE_ADD_REGIST_LIMIT) {
			// 報酬の登録上限に達しているためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00174, null, "報酬", String.valueOf(CommonConstant.FEE_ADD_REGIST_LIMIT));
		}

		// 報酬の登録
		TFeeEntity tFeeEntity = this.registFee(inputForm);

		// 登録した報酬SEQをinputFormにセット
		inputForm.setFeeSeq(tFeeEntity.getFeeSeq());

		// 入金ステータスが「入金待ち」、「入金済み」の場合に、売上、売上明細、売上明細-消費税を登録し見込み額などを更新
		if (FeePaymentStatus.AWAITING_PAYMENT.equalsByCode(tFeeEntity.getFeePaymentStatus())
				|| FeePaymentStatus.DEPOSITED.equalsByCode(tFeeEntity.getFeePaymentStatus())) {

			// 売上情報を登録
			TSalesDetailEntity tSalesDetailEntity = this.registSalesRerated(tFeeEntity);

			// 売上明細SEQを報酬テーブルに反映
			tFeeEntity.setSalesDetailSeq(tSalesDetailEntity.getSalesDetailSeq());
			this.updateFee(tFeeEntity);

			// 売上データの見込み額と実績額を更新
			commonAccgService.updateSalesAmount(tFeeEntity.getPersonId(), tFeeEntity.getAnkenId());
		}
	}

	/**
	 * タイムチャージ設定データを登録します<br>
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registTimeChargeSetting(FeeDetailInputForm inputForm) throws AppException {

		TFeeAddTimeChargeEntity entity = new TFeeAddTimeChargeEntity();
		setFeeDetailInputFormToTimeChargeSettingEntity(inputForm, entity);

		int insertCount = tFeeAddTimeChargeDao.insert(entity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 報酬データを変更します。<br>
	 * 入金ステータス値によって、売上に関するデータを更新します。<br>
	 * 
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void editFee(FeeDetailInputForm inputForm) throws AppException {

		// 入力値から報酬データの更新を行う
		this.updateFee(inputForm);

		// タイムチャージ設定削除処理
		this.deleteTimeCharge(inputForm.getFeeSeq());
	}

	/**
	 * タイムチャージ報酬変更処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void editFeeTimeCharge(FeeDetailInputForm inputForm) throws AppException {

		// 入力値から報酬データの更新を行う
		this.updateFee(inputForm);

		// タイムチャージ設定変更処理
		this.editTimeChargeSetting(inputForm);
	}

	/**
	 * 報酬データを入力用フォームに設定します
	 * 
	 * @param feeSeq
	 * @param inputForm
	 */
	public void setFee(Long feeSeq, FeeDetailInputForm inputForm) throws AppException {

		// 報酬データの確認
		FeeDetailListBean bean = tFeeDao.selectFeeDetailByFeeSeq(feeSeq);
		if (bean == null) {
			// 報酬データが既に無いためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 報酬情報をinputFormにセット
		this.setFeeDetailInputForm(bean, inputForm);
		// ステータスの選択リストをセット
		inputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions(bean.getFeePaymentStatus()));
	}

	/**
	 * 報酬データを取得します
	 * 
	 * @param feeSeq
	 * @throws AppException
	 */
	public FeeDetailListDto getFee(Long feeSeq) throws AppException {

		// 報酬データの確認
		FeeDetailListBean bean = tFeeDao.selectFeeDetailByFeeSeq(feeSeq);
		if (bean == null) {
			// 報酬データが既に無いためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		return convertFeeDetailBean2Dto(bean);
	}

	/**
	 * 紐づく請求書、精算書が発行されているかチェックします。<br>
	 * 発行されていれば key:isIssued, val:true を返します。<br>
	 * 下書きであれば key:isIssued, val:false を返します。<br>
	 * 発行時のみ書類タイプを key:accgDocType, val:AccgDocType を返します。
	 * 
	 * @param feeSeq
	 * @return
	 * @throws AppException
	 */
	public Map<String, Object> checkIssueStatusIssued(Long feeSeq) throws AppException {
		Map<String, Object> response = new HashMap<>();
		// 報酬データ取得
		TFeeEntity tFeeEntity = this.getTFeeEntity(feeSeq);
		Long accgDocSeq = tFeeEntity.getAccgDocSeq();
		if (accgDocSeq == null) {
			// 請求書、精算書に紐づいていなければ発行されていない
			response.put("isIssued", false);
			return response;
		}

		// 会計書類データ取得を取得し発行ステータスを判定
		AccgInvoiceStatementBean accgInvoiceStateBean = commonAccgSummaryService.getAccgInvoiceStatementDetail(accgDocSeq);
		if (AccgDocType.INVOICE.equalsByCode(accgInvoiceStateBean.getAccgDocType())) {
			String invoiceIssueStatus = accgInvoiceStateBean.getInvoiceIssueStatus();
			// 下書きではない場合
			if (!IssueStatus.DRAFT.equalsByCode(invoiceIssueStatus)) {
				response.put("isIssued", true);
				response.put("accgDocType", AccgDocType.INVOICE);
			} else {
				response.put("isIssued", false);
			}
		} else {
			String statementIssueStatus = accgInvoiceStateBean.getStatementIssueStatus();
			// 下書きではない場合
			if (!IssueStatus.DRAFT.equalsByCode(statementIssueStatus)) {
				response.put("isIssued", true);
				response.put("accgDocType", AccgDocType.STATEMENT);
			} else {
				response.put("isIssued", false);
			}
		}

		return response;
	}

	/**
	 * 報酬データ、タイムチャージデータを削除します。<br>
	 * 紐づく請求書、精算書があれば下記を実行します。<br>
	 * ・請求項目の消費税、源泉徴収税を再計算。<br>
	 *
	 * @param feeSeq
	 * @throws AppException
	 */
	public void deleteFeeAndUpdateTax(Long feeSeq) throws AppException {

		// 報酬データの取得
		TFeeEntity tFeeEntity = this.getTFeeEntity(feeSeq);

		// 報酬データのデータを削除前に保持しておく
		Long ankenId = tFeeEntity.getAnkenId();
		Long personId = tFeeEntity.getPersonId();
		Long accgDocSeq = tFeeEntity.getAccgDocSeq();
		Long salesDetailSeq = tFeeEntity.getSalesDetailSeq();

		// 報酬の削除処理
		this.deleteFee(feeSeq);
		// タイムチャージデータ削除
		this.deleteTimeCharge(feeSeq);

		if (salesDetailSeq != null) {
			// 報酬単体で売上計上しているもの
			// （請求書／精算書では利用されているものではない）

			// 売上明細詳細SEQに登録済みの場合 -> 削除処理
			this.deleteSalesDetail(salesDetailSeq);

			// 売上金額の再計算
			TSalesEntity tSalesEntity = tSalesDao.selectSalesEntityByPersonIdAndAnkenId(personId, ankenId);
			if (tSalesEntity != null) {
				commonAccgService.updateSalesAmount(personId, ankenId);
			}

		} else {
			// 報酬単体で売上計上されていないもの
			// （請求書／精算書で使われていないものか、下書きの請求書／精算書でつかわれているもの）
			// ※発行済みの請求書／精算書で使われている報酬は、削除処理を行えないようにしているため考慮しない。

			// 請求項目-報酬データ削除
			List<TAccgDocInvoiceFeeEntity> deletedTAccgDocInvoiceFeeEntityList = this.deleteAccgDocInvoiceFee(feeSeq);

			if (!CollectionUtils.isEmpty(deletedTAccgDocInvoiceFeeEntityList)) {
				// 請求項目-報酬の削除が行われた場合（報酬が請求書／精算書で利用されていた場合）

				// 請求項目データ削除
				List<Long> docInvoiceSeqList = deletedTAccgDocInvoiceFeeEntityList.stream().map(TAccgDocInvoiceFeeEntity::getDocInvoiceSeq).collect(Collectors.toList());
				this.deleteAccgDocInvoice(docInvoiceSeqList);

				if (accgDocSeq == null) {
					// 報酬が請求書／精算書で利用されていた場合は必ず存在する
					// -> 存在しない場合は楽観ロックエラーとする
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}

				// 請求項目情報取得
				AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

				// Dtoの値をもとに金額を再計算し、
				// 請求項目-消費税、請求項目-源泉徴収税のデータを更新
				commonAccgService.recalcAndUpdateAccgInvoiceTaxAndWithholding(accgDocSeq, accgInvoiceStatementAmountDto);

				// 請求額／精算額を更新し、PDFの再作成フラグを立てる
				commonAccgService.updateInvoiceStatementAmount(accgDocSeq, accgInvoiceStatementAmountDto);
			}
		}
	}

	/**
	 * 項目の候補データを取得します
	 * 
	 * @param searchWord
	 * @return
	 */
	public List<SelectOptionForm> searchFeeDataList(String searchWord) {
		return commonAccgService.searchFeeDataList(searchWord);
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
		return commonAccgAmountService.calculateTimeCharge(tanka, decimalMinutes);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 会計書類（請求書、精算書）に紐づく報酬の入金ステータスを変更するかチェックします。<br>
	 * 変更する場合は true を返します。
	 * 
	 * @param feeDetailInputForm
	 * @return
	 */
	private boolean checkIfAboutToChangeFeePaymentStatusOfAccgDoc(FeeDetailInputForm feeDetailInputForm) {
		boolean isChanged = false;
		
		// 報酬SEQが無い場合は新規登録なので「変更しない」とする
		Long feeSeq = feeDetailInputForm.getFeeSeq();
		if (feeSeq == null) {
			return isChanged;
		}
		
		// 報酬データが無い、報酬に紐づく会計書類SEQが無い場合は「変更しない」とする
		TFeeEntity tFeeEntity = tFeeDao.selectFeeByFeeSeq(feeSeq);
		if (tFeeEntity == null || tFeeEntity.getAccgDocSeq() == null) {
			return isChanged;
		}
		
		// DBに登録している入金ステータスとinpuFormの値を比較
		if (!tFeeEntity.getFeePaymentStatus().equals(feeDetailInputForm.getFeePaymentStatus())) {
			isChanged = true;
		}
		return isChanged;
	}

	/**
	 * List<FeeDetailListBean>型からList<FeeDetailListDto>型に変換します<br>
	 * 
	 * @param feeDetailListBeanList
	 * @return
	 */
	private List<FeeDetailListDto> convertBeanList2Dto(List<FeeDetailListBean> feeDetailListBeanList) {
		// Bean型をDto型に変換する
		List<FeeDetailListDto> dtoList = new ArrayList<FeeDetailListDto>();
		feeDetailListBeanList.forEach(bean -> {
			dtoList.add(convertFeeDetailBean2Dto(bean));
		});
		return dtoList;
	}

	/**
	 * FeeDetailListBean型からFeeDetailListDto型に変換します<br>
	 * 
	 * @param feeDetailListBean
	 * @return
	 */
	private FeeDetailListDto convertFeeDetailBean2Dto(FeeDetailListBean feeDetailListBean) {

		FeeDetailListDto feeDetailListDto = new FeeDetailListDto();

		feeDetailListDto.setAnkenId(feeDetailListBean.getAnkenId());
		feeDetailListDto.setFeeDate(DateUtils.parseToString(feeDetailListBean.getFeeDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		feeDetailListDto.setFeeItemName(feeDetailListBean.getFeeItemName());
		feeDetailListDto.setFeePaymentStatus(feeDetailListBean.getFeePaymentStatus());
		feeDetailListDto.setFeeSeq(feeDetailListBean.getFeeSeq());
		feeDetailListDto.setInvoiceSeq(feeDetailListBean.getInvoiceSeq());
		feeDetailListDto.setInvoiceNo(feeDetailListBean.getInvoiceNo());
		feeDetailListDto.setInvoicePaymentStatus(feeDetailListBean.getInvoicePaymentStatus());
		feeDetailListDto.setPersonId(feeDetailListBean.getPersonId());
		feeDetailListDto.setStatementSeq(feeDetailListBean.getStatementSeq());
		feeDetailListDto.setStatementNo(feeDetailListBean.getStatementNo());
		feeDetailListDto.setStatementRefundStatus(feeDetailListBean.getStatementRefundStatus());
		feeDetailListDto.setSumText(feeDetailListBean.getSumText());
		feeDetailListDto.setFeeMemo(feeDetailListBean.getFeeMemo());
		feeDetailListDto.setUncollectibleFlg(feeDetailListBean.getUncollectibleFlg());
		feeDetailListDto.setFeeAmount(AccountingUtils.toDispAmountLabel(feeDetailListBean.getFeeAmount()));
		feeDetailListDto.setTaxAmount(AccountingUtils.toDispAmountLabel(feeDetailListBean.getTaxAmount()));
		feeDetailListDto.setWithholdingAmount(AccountingUtils.toDispAmountLabel(feeDetailListBean.getWithholdingAmount()));
		feeDetailListDto.setFeeAmountTaxIn(AccountingUtils.toDispAmountLabel(feeDetailListBean.getFeeAmountTaxIn()));
		feeDetailListDto.setAfterWithholdingTax(AccountingUtils.toDispAmountLabel(feeDetailListBean.getAfterWithholdingTax()));
		feeDetailListDto.setWithholdingChecked(CommonConstant.SystemFlg.FLG_ON.equalsByCode(feeDetailListBean.getWithholdingFlg()));
		// タイムチャージ報酬
		feeDetailListDto.setTimeCharge(CommonConstant.SystemFlg.FLG_ON.equalsByCode(feeDetailListBean.getFeeTimeChargeFlg()));
		feeDetailListDto.setWorkTimeMinute(feeDetailListBean.getWorkTimeMinute());
		feeDetailListDto.setHourPrice(AccountingUtils.toDispAmountLabel(feeDetailListBean.getHourPrice()));
		return feeDetailListDto;
	}

	/**
	 * 報酬明細入力フォームの情報を報酬エンティティに設定します
	 * 
	 * @param inputForm
	 * @param entity
	 */
	private void setFeeDetailInputFormToEntity(FeeDetailInputForm inputForm, TFeeEntity entity) {

		// 名簿ID
		entity.setPersonId(inputForm.getPersonId());
		// 案件ID
		entity.setAnkenId(inputForm.getAnkenId());
		// 発生日
		entity.setFeeDate(DateUtils.parseToLocalDate(inputForm.getFeeDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		// 報酬項目名
		entity.setFeeItemName(inputForm.getFeeItemName());
		// 報酬額（税抜）
		BigDecimal feeAmountExcludingTax = commonAccgAmountService.calcFeeAmountExcludingTax(LoiozNumberUtils.parseAsBigDecimal((inputForm.getFeeAmount())), inputForm.getTaxRateType(), inputForm.getTaxFlg());
		entity.setFeeAmount(feeAmountExcludingTax);
		// 入金ステータス
		entity.setFeePaymentStatus(inputForm.getFeePaymentStatus());
		// 消費税金額
		entity.setTaxAmount(commonAccgAmountService.calcTaxAmount(LoiozNumberUtils.parseAsBigDecimal((inputForm.getFeeAmount())), inputForm.getTaxRateType(), inputForm.getTaxFlg()));
		// 消費税率
		entity.setTaxRateType(inputForm.getTaxRateType());
		// 課税区分
		entity.setTaxFlg(inputForm.getTaxFlg());
		// 源泉徴収額
		GensenChoshu gensenChoshu = GensenChoshu.of(inputForm.getWithholdingFlg());
		entity.setWithholdingFlg(gensenChoshu.getCd());
		if (gensenChoshu == GensenChoshu.DO) {
			// 源泉徴収する場合 -> 源泉徴収額を計算し設定
			BigDecimal withholdingAmount = commonAccgAmountService.calcWithholdingAmount(feeAmountExcludingTax);
			entity.setWithholdingAmount(withholdingAmount);
		} else if (gensenChoshu == GensenChoshu.DO_NOT) {
			// 源泉徴収をしない場合 -> 厳選徴収額をNullに設定
			entity.setWithholdingAmount(null);
		} else {
			// Enum値が取得できない場合
			throw new RuntimeException("Enum値が不正です。");
		}

		// メモ
		entity.setFeeMemo(inputForm.getFeeMemo());
		// 摘要
		entity.setSumText(inputForm.getSumText());
		// タイムチャージフラグ
		entity.setFeeTimeChargeFlg(SystemFlg.booleanToCode(inputForm.isFeeTimeChargeFlg()));
	}

	/**
	 * 報酬明細入力フォームの情報をタイムチャージ設定エンティティに設定します
	 * 
	 * @param inputForm
	 * @param entity
	 */
	private void setFeeDetailInputFormToTimeChargeSettingEntity(FeeDetailInputForm inputForm, TFeeAddTimeChargeEntity entity) {
		entity.setFeeSeq(inputForm.getFeeSeq());
		entity.setHourPrice(StringUtils.isEmpty(inputForm.getTimeChargeTanka()) ? null : new BigDecimal(inputForm.getTimeChargeTanka()));
		entity.setWorkTimeMinute(StringUtils.isEmpty(inputForm.getTimeChargeTime()) ? null : Integer.parseInt(inputForm.getTimeChargeTime()));
	}

	/**
	 * 報酬情報を入力用フォームに設定します
	 * 
	 * @param bean
	 * @param inputForm
	 * @throws AppException
	 */
	private void setFeeDetailInputForm(FeeDetailListBean bean, FeeDetailInputForm inputForm) throws AppException {
		inputForm.setPersonId(bean.getPersonId());
		inputForm.setAnkenId(bean.getAnkenId());
		inputForm.setFeeItemName(bean.getFeeItemName());
		if (bean.getFeeDate() != null) {
			inputForm.setFeeDate(DateUtils.parseToString(bean.getFeeDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		} else {
			inputForm.setFeeDate(null);
		}
		inputForm.setFeePaymentStatus(bean.getFeePaymentStatus());
		// 税込で登録している場合は、報酬額 + 税を報酬額とする
		if (TaxFlg.INTERNAL_TAX.equalsByCode(bean.getTaxFlg())) {
			inputForm.setFeeAmount(bean.getFeeAmountTaxIn().toPlainString());
		} else {
			inputForm.setFeeAmount(bean.getFeeAmount().toPlainString());
		}
		inputForm.setTaxFlg(bean.getTaxFlg());
		inputForm.setTaxRateType(bean.getTaxRateType());
		inputForm.setWithholdingFlg(bean.getWithholdingFlg());
		inputForm.setSumText(bean.getSumText());
		inputForm.setFeeSeq(bean.getFeeSeq());
		inputForm.setInvoiceNo(bean.getInvoiceNo());
		inputForm.setStatementNo(bean.getStatementNo());
		inputForm.setFeeTimeChargeFlg(SystemFlg.FLG_ON.equalsByCode(bean.getFeeTimeChargeFlg()) ? true : false);
		if (bean.getHourPrice() != null) {
			inputForm.setTimeChargeTanka(bean.getHourPrice().toPlainString());
		}
		if (bean.getWorkTimeMinute() != null) {
			inputForm.setTimeChargeTime(bean.getWorkTimeMinute().toString());
		}
		inputForm.setFeeMemo(bean.getFeeMemo());

		// アカウント名を取得
		Map<Long, String> accountMap = commonAccountService.getAccountNameMap();
		String createdByName = accountMap.get(bean.getCreatedBy());
		String updatedByName = accountMap.get(bean.getUpdatedBy());

		// 登録情報
		inputForm.setCreatedAtStr(
				DateUtils.parseToString(bean.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		inputForm.setCreatedByName(createdByName);

		// 更新情報
		inputForm.setUpdatedAtStr(
				DateUtils.parseToString(bean.getUpdatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		inputForm.setUpdatedByName(updatedByName);

		// 紐づく請求書、精算書が発行しているかどうか
		Map<String, Object> issueStatusResult = this.checkIssueStatusIssued(bean.getFeeSeq());
		boolean isIssued = (boolean) issueStatusResult.get("isIssued");
		inputForm.setIssued(isIssued);
		if (isIssued) {
			// 紐づく請求書／精算書が発行されている場合 -> 編集不可メッセージ
			AccgDocType docType = (AccgDocType) issueStatusResult.get("accgDocType");
			String issuedMessage = messageService.getMessage(MessageEnum.MSG_W00016, SessionUtils.getLocale(), docType.getVal(), "内部メモ以外の編集");
			inputForm.setIssuedMessage(issuedMessage);
		}
	}

	/**
	 * 報酬データを更新します
	 * 
	 * @param tFeeEntity
	 * @throws AppException
	 */
	private void updateFee(TFeeEntity tFeeEntity) throws AppException {
		int updateCount = 0;
		try {
			updateCount = tFeeDao.update(tFeeEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (updateCount != 1) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 報酬データの発生日、摘要を請求項目-報酬に反映します
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	private void applyUpdateFeeToAccgDocInvoice(FeeDetailInputForm inputForm) throws AppException {
		// 請求項目-報酬データ取得
		TAccgDocInvoiceFeeEntity tAccgDocInvoiceFeeEntity = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeByFeeSeq(inputForm.getFeeSeq());
		if (tAccgDocInvoiceFeeEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 発生日、摘要をentityにセット
		tAccgDocInvoiceFeeEntity.setFeeTransactionDate(DateUtils.parseToLocalDate(inputForm.getFeeDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgDocInvoiceFeeEntity.setSumText(inputForm.getSumText());
		
		// 請求項目-報酬更新
		this.updateTAccgDocInvoiceFeeEntity(tAccgDocInvoiceFeeEntity);
	}

	/**
	 * 請求項目-報酬テーブルを更新します
	 * 
	 * @param tAccgInvoiceTaxEntity
	 * @throws AppException
	 */
	private void updateTAccgDocInvoiceFeeEntity(TAccgDocInvoiceFeeEntity tAccgDocInvoiceFeeEntity) throws AppException {
		int updateCount = 0;
		try {
			updateCount = tAccgDocInvoiceFeeDao.update(tAccgDocInvoiceFeeEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (updateCount != 1) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 報酬の入金ステータス値によって、売上データの作成、更新、削除をおこないます。<br>
	 * 
	 * @param tFeeEntity 報酬データ（現在の、更新後の）
	 * @param feePaymentStatusBeforeUpdate 更新前の入金ステータス
	 * @param salesDetailSeqBeforeUpdate 更新前の売上明細SEQ
	 * @throws AppException
	 */
	private void updateSalesWithPaymentStatusBeforeAndAfterUpdate(TFeeEntity tFeeEntity,
			String feePaymentStatusBeforeUpdate, Long salesDetailSeqBeforeUpdate) throws AppException {
		// 報酬データの入金ステータスなどを取得
		String feePaymentStatusAfterUpdate = tFeeEntity.getFeePaymentStatus();
		Long personId = tFeeEntity.getPersonId();
		Long ankenId = tFeeEntity.getAnkenId();

		// 「未請求」以外で更新前と現在の入金ステータスが同じ場合
		if (!FeePaymentStatus.UNCLAIMED.equalsByCode(feePaymentStatusBeforeUpdate)
				&& feePaymentStatusBeforeUpdate.equals(feePaymentStatusAfterUpdate)) {
			// 売上、売上明細を更新し見込み額と実績額を再計算する
			this.updateSalesAndSalesDetail(personId, ankenId, salesDetailSeqBeforeUpdate, tFeeEntity);
		}

		// 更新前が「未請求」 更新後が「入金待ち」 or 「入金済み」
		if (FeePaymentStatus.UNCLAIMED.equalsByCode(feePaymentStatusBeforeUpdate)
				&& (FeePaymentStatus.AWAITING_PAYMENT.equalsByCode(feePaymentStatusAfterUpdate)
						|| FeePaymentStatus.DEPOSITED.equalsByCode(feePaymentStatusAfterUpdate))) {
			// 売上、売上明細を作成し見込み額と実績額を再計算する
			this.registSalesAndSalesDetail(personId, ankenId, tFeeEntity);
		}

		// 更新前が「入金待ち」 更新後が「未請求」
		if (FeePaymentStatus.AWAITING_PAYMENT.equalsByCode(feePaymentStatusBeforeUpdate)
				&& FeePaymentStatus.UNCLAIMED.equalsByCode(feePaymentStatusAfterUpdate)) {
			// 報酬の売上明細SEQを空にして売上を更新し見込み額と実績額を再計算する
			this.deleteSalesDetailSeqFromFeeAndUpdateSales(personId, ankenId, salesDetailSeqBeforeUpdate, tFeeEntity);
		}

		// 更新前が「入金待ち」 更新後が「入金済み」
		if (FeePaymentStatus.AWAITING_PAYMENT.equalsByCode(feePaymentStatusBeforeUpdate)
				&& FeePaymentStatus.DEPOSITED.equalsByCode(feePaymentStatusAfterUpdate)) {
			// 売上、売上明細の更新
			this.updateSalesAndSalesDetail(personId, ankenId, salesDetailSeqBeforeUpdate, tFeeEntity);
		}

		// 更新前が「入金済み」 更新後が「未請求」
		if (FeePaymentStatus.DEPOSITED.equalsByCode(feePaymentStatusBeforeUpdate)
				&& FeePaymentStatus.UNCLAIMED.equalsByCode(feePaymentStatusAfterUpdate)) {
			// 報酬の売上明細SEQを空にして売上を更新し見込み額と実績額を再計算する
			this.deleteSalesDetailSeqFromFeeAndUpdateSales(personId, ankenId, salesDetailSeqBeforeUpdate, tFeeEntity);
		}

		// 更新前が「入金済み」 更新後が「入金待ち」
		if (FeePaymentStatus.DEPOSITED.equalsByCode(feePaymentStatusBeforeUpdate)
				&& FeePaymentStatus.AWAITING_PAYMENT.equalsByCode(feePaymentStatusAfterUpdate)) {
			// 売上、売上明細の更新し見込み額と実績額を再計算する
			this.updateSalesAndSalesDetail(personId, ankenId, salesDetailSeqBeforeUpdate, tFeeEntity);
		}
	}

	/**
	 * 売上明細データを更新します。
	 * 
	 * @param salesDetailSeq
	 * @param tFeeEntity
	 * @throws AppException
	 */
	private void updateSalesDetail(Long salesDetailSeq, TFeeEntity tFeeEntity) throws AppException {
		TSalesDetailEntity tSalesDetailEntity = tSalesDetailDao.selectSalesDetailBySalesDetailSeq(salesDetailSeq);
		if (tSalesDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 売上計上先を取得
		Long ankenId = tFeeEntity.getAnkenId();
		Long salesAccountSeq = this.getSalesAccountSeq(ankenId);

		// entityにセット
		tSalesDetailEntity.setSalesAmount(tFeeEntity.getFeeAmount());
		tSalesDetailEntity.setSalesTaxAmount(LoiozNumberUtils.nullToZero(tFeeEntity.getTaxAmount()));
		tSalesDetailEntity.setSalesWithholdingAmount(LoiozNumberUtils.nullToZero(tFeeEntity.getWithholdingAmount()));
		tSalesDetailEntity.setSalesDate(tFeeEntity.getFeeDate());
		tSalesDetailEntity.setSalesAccountSeq(salesAccountSeq);

		// 登録
		int updateCount = 0;
		try {
			updateCount = tSalesDetailDao.update(tSalesDetailEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (updateCount != 1) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 売上、売上明細、売上明細-消費税データを更新します。<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @param salesDetailSeq
	 * @param tFeeEntity
	 * @throws AppException
	 */
	private void updateSalesAndSalesDetail(Long personId, Long ankenId, Long salesDetailSeq, TFeeEntity tFeeEntity) throws AppException {
		// 売上明細の更新
		this.updateSalesDetail(salesDetailSeq, tFeeEntity);

		// 売上明細-消費税の更新
		this.deleteInsertSalesDetailTax(salesDetailSeq, tFeeEntity);

		// 売上データの見込み額と実績額を更新
		commonAccgService.updateSalesAmount(personId, ankenId);
	}

	/**
	 * 入力フォームから報酬情報を更新
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	private void updateFee(FeeDetailInputForm inputForm) throws AppException {

		// 報酬データの取得
		TFeeEntity tFeeEntity = this.getTFeeEntity(inputForm.getFeeSeq());

		// 更新前の入金ステータス、売上明細SEQを取得
		String feePaymentStatusBeforeUpdate = tFeeEntity.getFeePaymentStatus();
		Long salesDetailSeqBeforeUpdate = tFeeEntity.getSalesDetailSeq();

		// 報酬データの更新
		this.setFeeDetailInputFormToEntity(inputForm, tFeeEntity);
		this.updateFee(tFeeEntity);

		Long accgDocSeq = tFeeEntity.getAccgDocSeq();
		if (accgDocSeq != null) {
			// 更新を行った報酬が、請求書／精算書で利用されているものの場合
			// -> 請求書／精算書の金額変更、必要なPDFの再生性フラグを立てる

			// 請求項目-報酬の更新（発生日、摘要を反映）
			this.applyUpdateFeeToAccgDocInvoice(inputForm);

			// 請求項目情報取得
			AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

			// Dtoの値をもとに金額を再計算し、
			// 請求項目-消費税、請求項目-源泉徴収税のデータを更新
			commonAccgService.recalcAndUpdateAccgInvoiceTaxAndWithholding(accgDocSeq, accgInvoiceStatementAmountDto);

			// 請求額／精算額を更新し、PDFの再作成フラグを立てる
			commonAccgService.updateInvoiceStatementAmount(accgDocSeq, accgInvoiceStatementAmountDto);
			
		} else {
			// 更新を行った報酬が、請求書／精算書で利用されていないものの場合
			// -> 請求書／精算書で利用されておらず、報酬単体でステータスの変更を行っている場合、売上データを更新する
			// （ステータスが「未請求」→「未請求」の変更の場合は売上の更新はないのでなにもしない）

			// 入金ステータス値によって、売上に関するデータ更新
			this.updateSalesWithPaymentStatusBeforeAndAfterUpdate(tFeeEntity, feePaymentStatusBeforeUpdate, salesDetailSeqBeforeUpdate);
		}

	}

	/**
	 * タイムチャージ報酬データを変更します
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	private void editTimeChargeSetting(FeeDetailInputForm inputForm) throws AppException {

		TFeeAddTimeChargeEntity tTimeChargeSettingEntity = tFeeAddTimeChargeDao.selectTimeChargeSettingByFeeSeq(inputForm.getFeeSeq());

		// 既に登録があれば更新
		if (tTimeChargeSettingEntity != null) {
			setFeeDetailInputFormToTimeChargeSettingEntity(inputForm, tTimeChargeSettingEntity);
			int updateCount = tFeeAddTimeChargeDao.update(tTimeChargeSettingEntity);
			if (updateCount != 1) {
				// 更新処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00012, null);
			}
		} else {
			// 新規登録
			this.registTimeChargeSetting(inputForm);
		}
	}

	/**
	 * 売上明細SEQに紐づく売上明細-消費税データを再作成します。
	 * 
	 * @param salesDetailSeq
	 * @param tFeeEntity
	 * @throws AppException
	 */
	private void deleteInsertSalesDetailTax(Long salesDetailSeq, TFeeEntity tFeeEntity) throws AppException {
		List<TSalesDetailTaxEntity> tSalesDetailTaxEntityList = tSalesDetailTaxDao.selectSalesDetailTaxBySalesDetailSeq(salesDetailSeq);
		if (LoiozCollectionUtils.isNotEmpty(tSalesDetailTaxEntityList)) {
			// 売上明細-消費税データを削除
			this.batchDeleteTSalesDetailTax(tSalesDetailTaxEntityList);
		}

		// 売上明細-消費税データを作成
		this.registerSalesDetailTax(salesDetailSeq, tFeeEntity);
	}

	/**
	 * 報酬データを削除します
	 * 
	 * @param feeSeq
	 */
	private TFeeEntity deleteFee(Long feeSeq) throws AppException {
		// 報酬データの確認
		TFeeEntity tFeeEntity = tFeeDao.selectFeeByFeeSeq(feeSeq);
		if (tFeeEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		int deleteCount = tFeeDao.delete(tFeeEntity);

		if (deleteCount != 1) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
		return tFeeEntity;
	}

	/**
	 * タイムチャージを削除します。
	 * 
	 * @param feeSeq
	 * @throws AppException
	 */
	private void deleteTimeCharge(Long feeSeq) throws AppException {
		// タイムチャージデータの確認
		TFeeAddTimeChargeEntity tTimeChargeSettingEntity = tFeeAddTimeChargeDao.selectTimeChargeSettingByFeeSeq(feeSeq);
		if (tTimeChargeSettingEntity != null) {
			// タイムチャージデータが有れば削除
			int deleteCount = tFeeAddTimeChargeDao.delete(tTimeChargeSettingEntity);

			if (deleteCount != 1) {
				// 削除処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00014, null);
			}
		}
	}

	/**
	 * 売上情報を削除
	 * 
	 * @param salesSeq
	 * @throws AppException
	 */
	private void deleteSales(Long salesSeq) throws AppException {
		// 報酬明細データが空の場合
		TSalesEntity tSalesEntity = tSalesDao.selectBySeq(salesSeq);

		try {
			// 削除処理
			tSalesDao.delete(tSalesEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 売上明細情報を削除する
	 * 
	 * @param salesDetailSeq
	 * @throws AppException
	 */
	private void deleteSalesDetail(Long salesDetailSeq) throws AppException {

		// 売上明細データ
		TSalesDetailEntity tSalesDetailEntity = tSalesDetailDao.selectSalesDetailBySalesDetailSeq(salesDetailSeq);
		if (tSalesDetailEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 売上明細税データ
		List<TSalesDetailTaxEntity> tSalesDetailTaxEntities = tSalesDetailTaxDao.selectSalesDetailTaxBySalesDetailSeq(salesDetailSeq);

		// 報酬SEQを確保しておく
		Long salesSeq = tSalesDetailEntity.getSalesSeq();

		try {
			// 削除処理
			tSalesDetailDao.delete(tSalesDetailEntity);
			tSalesDetailTaxDao.batchDelete(tSalesDetailTaxEntities);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		List<TSalesDetailEntity> tSalesDetailEntities = tSalesDetailDao.selectSalesDetailBySalesSeq(salesSeq);
		if (CollectionUtils.isEmpty(tSalesDetailEntities)) {
			// 売上明細データが空の場合
			// -> 売上情報を削除
			this.deleteSales(salesSeq);
		}

	}

	/**
	 * 請求報酬項目を削除します。
	 * 
	 * @param feeSeq
	 * @return
	 * @throws AppException
	 */
	private List<TAccgDocInvoiceFeeEntity> deleteAccgDocInvoiceFee(Long feeSeq) throws AppException {
		List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeByFeeSeqList(List.of(feeSeq));
		if (CollectionUtils.isEmpty(tAccgDocInvoiceFeeEntityList)) {
			return tAccgDocInvoiceFeeEntityList;
		}

		int[] deleteAccgDocInvoiceFeeCount = null;
		try {
			deleteAccgDocInvoiceFeeCount = tAccgDocInvoiceFeeDao.batchDelete(tAccgDocInvoiceFeeEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceFeeEntityList.size() != deleteAccgDocInvoiceFeeCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
		return tAccgDocInvoiceFeeEntityList;
	}

	/**
	 * 請求項目を削除します。
	 * 
	 * @param docInvoiceSeqList
	 * @throws AppException
	 */
	private void deleteAccgDocInvoice(List<Long> docInvoiceSeqList) throws AppException {
		List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
		if (CollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
			return;
		}

		int[] deleteAccgDocInvoiceCount = null;
		try {
			deleteAccgDocInvoiceCount = tAccgDocInvoiceDao.batchDelete(tAccgDocInvoiceEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceEntityList.size() != deleteAccgDocInvoiceCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 報酬の売上明細SEQを空にして、売上明細SEQで紐づいていた売上明細、売上明細-消費税データを削除します。<br>
	 * 売上【見込み額】、【実績額】を再計算し売上テーブルに反映します。
	 * 
	 * @param personId
	 * @param ankenId
	 * @param salesDetailSeq
	 * @param tFeeEntity
	 * @throws AppException
	 */
	private void deleteSalesDetailSeqFromFeeAndUpdateSales(Long personId, Long ankenId, Long salesDetailSeq, TFeeEntity tFeeEntity) throws AppException {
		// 報酬データの売上明細SEQを空にする
		tFeeEntity.setSalesDetailSeq(null);
		this.updateFee(tFeeEntity);

		// 売上明細、売上明細-消費税を削除、売上明細がなければ売上を削除、売上明細があれば売上を更新し見込み額と実績額を再計算する
		commonAccgService.deleteSalesRelated(salesDetailSeq, personId, ankenId);

	}

	/**
	 * 売上明細-消費税テーブルの一括削除をおこないます
	 * 
	 * @param tSalesDetailTaxEntityList
	 * @throws AppException
	 */
	private void batchDeleteTSalesDetailTax(List<TSalesDetailTaxEntity> tSalesDetailTaxEntityList) throws AppException {
		int[] deleteCount = null;
		try {
			deleteCount = tSalesDetailTaxDao.batchDelete(tSalesDetailTaxEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tSalesDetailTaxEntityList.size() != deleteCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 報酬データを登録します。
	 * 
	 * @param inputForm
	 * @return
	 * @throws AppException
	 */
	private TFeeEntity registFee(FeeDetailInputForm inputForm) throws AppException {
		TFeeEntity entity = new TFeeEntity();
		this.setFeeDetailInputFormToEntity(inputForm, entity);

		int insertCount = tFeeDao.insert(entity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return entity;
	}

	/**
	 * 売上、売上明細の登録をします<br>
	 * 
	 * @param tFeeEntity
	 * @return
	 * @throws AppException
	 */
	private TSalesDetailEntity registSalesRerated(TFeeEntity tFeeEntity) throws AppException {
		// 売上データ取得
		Long ankenId = tFeeEntity.getAnkenId();
		Long personId = tFeeEntity.getPersonId();
		TSalesEntity tSalesEntity = tSalesDao.selectSalesEntityByPersonIdAndAnkenId(personId, ankenId);
		if (tSalesEntity == null) {
			// 売上が登録されていなければ、売上データを登録し明細を登録する。
			tSalesEntity = commonAccgService.registSales(ankenId, personId);
		}

		// 売上明細の登録
		TSalesDetailEntity tSalesDetailEntity = this.registSalesDetail(tSalesEntity.getSalesSeq(), tFeeEntity);

		// 売上明細-消費税の登録
		this.registerSalesDetailTax(tSalesDetailEntity.getSalesDetailSeq(), tFeeEntity);

		return tSalesDetailEntity;
	}

	/**
	 * 報酬情報を売上明細に登録します。
	 * 
	 * @param salesSeq
	 * @param tFeeEntity
	 * @return
	 * @throws AppException
	 */
	private TSalesDetailEntity registSalesDetail(Long salesSeq, TFeeEntity tFeeEntity) throws AppException {
		TSalesDetailEntity tSalesDetailEntity = new TSalesDetailEntity();
		// 報酬が無い場合は登録しない
		if (tFeeEntity == null || LoiozNumberUtils.equalsZero(tFeeEntity.getFeeAmount()) || salesSeq == null) {
			return tSalesDetailEntity;
		}

		// 売上計上先を取得
		Long ankenId = tFeeEntity.getAnkenId();
		Long salesAccountSeq = this.getSalesAccountSeq(ankenId);

		// 登録データをentityにセット
		tSalesDetailEntity.setSalesSeq(salesSeq);
		tSalesDetailEntity.setSalesAmount(tFeeEntity.getFeeAmount());
		tSalesDetailEntity.setSalesTaxAmount(LoiozNumberUtils.nullToZero(tFeeEntity.getTaxAmount()));
		tSalesDetailEntity.setSalesWithholdingAmount(LoiozNumberUtils.nullToZero(tFeeEntity.getWithholdingAmount()));
		tSalesDetailEntity.setSalesDate(tFeeEntity.getFeeDate());
		tSalesDetailEntity.setSalesAccountSeq(salesAccountSeq);

		// 登録
		int insertCount = tSalesDetailDao.insert(tSalesDetailEntity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tSalesDetailEntity;
	}

	/**
	 * 報酬情報を売上明細-消費税に登録します。
	 * 
	 * @param salesDetailSeq
	 * @param tFeeEntity
	 * @return
	 * @throws AppException
	 */
	private TSalesDetailTaxEntity registerSalesDetailTax(Long salesDetailSeq, TFeeEntity tFeeEntity) throws AppException {
		TSalesDetailTaxEntity salesDetailTaxEntity = new TSalesDetailTaxEntity();
		// 報酬が無い場合、消費税0%の場合は登録しない
		if (LoiozNumberUtils.equalsZero(tFeeEntity.getFeeAmount()) || TaxRate.TAX_FREE.equalsByCode(tFeeEntity.getTaxRateType()) || salesDetailSeq == null) {
			return salesDetailTaxEntity;
		}

		// 対象額、税額セット
		salesDetailTaxEntity.setSalesDetailSeq(salesDetailSeq);
		salesDetailTaxEntity.setTaxableAmount(tFeeEntity.getFeeAmount());
		salesDetailTaxEntity.setTaxAmount(tFeeEntity.getTaxAmount());

		// 8%消費税
		if (TaxRate.EIGHT_PERCENT.equalsByCode(tFeeEntity.getTaxRateType())) {
			salesDetailTaxEntity.setTaxRateType(TaxRate.EIGHT_PERCENT.getCd());
		}

		// 10%消費税
		if (TaxRate.TEN_PERCENT.equalsByCode(tFeeEntity.getTaxRateType())) {
			salesDetailTaxEntity.setTaxRateType(TaxRate.TEN_PERCENT.getCd());
		}

		// 登録
		int insertSalesDetailCount = 0;
		insertSalesDetailCount = tSalesDetailTaxDao.insert(salesDetailTaxEntity);
		if (insertSalesDetailCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return salesDetailTaxEntity;
	}

	/**
	 * 報酬データの売上、売上明細、売上明細-消費税データを作成し売上明細SEQを報酬にセットします。<br>
	 * 売上【見込み額】、【実績額】を再計算し売上データに反映します。
	 * 
	 * @param personId
	 * @param ankenId
	 * @param tFeeEntity
	 * @return
	 * @throws AppException
	 */
	private TSalesDetailEntity registSalesAndSalesDetail(Long personId, Long ankenId, TFeeEntity tFeeEntity) throws AppException {
		// 売上、売上明細、売上明細-消費税を作成
		TSalesDetailEntity tSalesDetailEntity = this.registSalesRerated(tFeeEntity);

		// 報酬データに売上明細SEQを反映
		tFeeEntity.setSalesDetailSeq(tSalesDetailEntity.getSalesDetailSeq());
		this.updateFee(tFeeEntity);

		// 売上データの見込み額と実績額を更新
		commonAccgService.updateSalesAmount(personId, ankenId);

		return tSalesDetailEntity;
	}

	/**
	 * 報酬SEQをキーに報酬データを取得します。<br>
	 * 
	 * @param feeSeq
	 * @return
	 * @throws AppException
	 */
	private TFeeEntity getTFeeEntity(Long feeSeq) throws AppException {
		TFeeEntity tFeeEntity = tFeeDao.selectFeeByFeeSeq(feeSeq);
		if (tFeeEntity == null) {
			// 報酬データが既に無いためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		return tFeeEntity;
	}

	/**
	 * 案件の売上計上先SEQ（先頭1件目）を取得します。
	 * 
	 * @param ankenId
	 * @return
	 */
	private Long getSalesAccountSeq(Long ankenId) {
		// 売上計上先を取得
		List<Long> accountSeqList = tAnkenTantoDao.selectByAnkenIdAndTantoType(ankenId, TantoType.SALES_OWNER.getCd());
		Long salesAccountSeq = null;
		if (accountSeqList.size() > 0) {
			// 売上計上先：1件目を売上計上先に指定する
			salesAccountSeq = accountSeqList.get(0);
		}
		return salesAccountSeq;
	}
}