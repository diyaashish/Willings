package jp.loioz.app.user.depositRecvDetail.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonAccgSummaryService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.user.depositRecvDetail.dto.DepositRecvDetailListDto;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailInputForm;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailSearchForm;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailViewForm;
import jp.loioz.bean.AccgInvoiceStatementBean;
import jp.loioz.bean.DepositRecvDetailListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AccgMenu;
import jp.loioz.common.constant.CommonConstant.DepositRecvCreatedType;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.ExpenseInvoiceFlg;
import jp.loioz.common.constant.CommonConstant.InvoiceDepositType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAccgDocInvoiceDao;
import jp.loioz.dao.TAccgDocInvoiceDepositDao;
import jp.loioz.dao.TAccgDocInvoiceDepositTDepositRecvMappingDao;
import jp.loioz.dao.TAccgDocRepayDao;
import jp.loioz.dao.TAccgDocRepayTDepositRecvMappingDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAccgDocInvoiceDepositEntity;
import jp.loioz.entity.TAccgDocInvoiceDepositTDepositRecvMappingEntity;
import jp.loioz.entity.TAccgDocInvoiceEntity;
import jp.loioz.entity.TAccgDocRepayEntity;
import jp.loioz.entity.TAccgDocRepayTDepositRecvMappingEntity;
import jp.loioz.entity.TDepositRecvEntity;

/**
 * 預り金明細画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecvDetailService extends DefaultService {

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 会計書類Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** 請求項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceDao tAccgDocInvoiceDao;

	/** 請求預り金項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceDepositDao tAccgDocInvoiceDepositDao;

	/** 請求項目-預り金（実費）_預り金テーブルマッピングDaoクラス */
	@Autowired
	private TAccgDocInvoiceDepositTDepositRecvMappingDao tAccgDocInvoiceDepositTDepositRecvMappingDao;

	/** 既入金項目_預り金テーブルマッピングDaoクラス */
	@Autowired
	private TAccgDocRepayTDepositRecvMappingDao tAccgDocRepayTDepositRecvMappingDao;

	/** 既入金Daoクラス */
	@Autowired
	private TAccgDocRepayDao tAccgDocRepayDao;

	/** 会計管理共通サービスクラス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 会計管理：預り金詳細、報酬詳細の共通サービス */
	@Autowired
	private CommonAccgSummaryService commonAccgSummaryService;

	/** アカウント共通サービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** メッセージ取得サービス */
	@Autowired
	private MessageService messageService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 明細画面表示用フォームを作成する
	 * 
	 * @return
	 */
	public DepositRecvDetailViewForm createViewForm() {
		DepositRecvDetailViewForm viewForm = new DepositRecvDetailViewForm();
		return viewForm;
	}

	/**
	 * 明細画面入力用フォームを作成する
	 * 
	 * @return
	 */
	public DepositRecvDetailInputForm createInputForm() {
		DepositRecvDetailInputForm inputForm = new DepositRecvDetailInputForm();
		return inputForm;
	}

	/**
	 * 明細ヘッダー情報を取得します
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void getAccgData(DepositRecvDetailViewForm viewForm, DepositRecvDetailSearchForm searchForm) {
		// 案件「会計管理」の案件情報
		AccgCaseForm accgCaseForm = commonAccgSummaryService.getAccgCase(searchForm.getAnkenId(), searchForm.getPersonId());
		viewForm.setAccgCaseForm(accgCaseForm);

		// 案件「会計管理」の報酬、預り金合計
		AccgCaseSummaryForm accgCaseSummaryForm = commonAccgSummaryService.getAccgCaseSummary(searchForm.getAnkenId(), searchForm.getPersonId());
		viewForm.setAccgCaseSummaryForm(accgCaseSummaryForm);

		// 案件「会計管理」の顧客一覧
		AccgCasePersonForm accgCasePersonForm = commonAccgSummaryService.getAccgCasePerson(searchForm.getAnkenId(), searchForm.getPersonId());
		viewForm.setAccgCasePersonForm(accgCasePersonForm);

		// 預り金選択
		accgCaseForm.setAccgMenu(AccgMenu.DEPOSIT_RECV);

	}

	/**
	 * 明細一覧情報を取得します
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void searchDepositRecvDetail(DepositRecvDetailViewForm viewForm, DepositRecvDetailSearchForm searchForm) {

		// 明細一覧情報を取得
		List<DepositRecvDetailListBean> detailBeanList = tDepositRecvDao.selectDepositRecvDetailListByParams(
				searchForm.getAnkenId(), searchForm.getPersonId(), searchForm.toDepositRecvDetailSortCondition());

		// Dto変換してviewFormにセット
		List<DepositRecvDetailListDto> detailDtoList = this.convertBeanList2Dto(detailBeanList);
		viewForm.setDepositRecvDetailList(detailDtoList);

		// 一覧表示のソートキーとソート順をviewFormにセット
		viewForm.setDepositRecvDetailSortItem(searchForm.getDepositRecvDetailSortItem());
		viewForm.setDepositRecvDetailSortOrder(searchForm.getDepositRecvDetailSortOrder());
	}

	/**
	 * 預り金データを入力用フォームに設定します
	 * 
	 * @param depositRecvSeq
	 * @param inputForm
	 */
	public void setDepositRecv(Long depositRecvSeq, DepositRecvDetailInputForm inputForm) throws AppException {

		// 預り金データの確認
		TDepositRecvEntity entity = tDepositRecvDao.selectDepositRecvByDepositRecvSeq(depositRecvSeq);
		if (entity == null) {
			// 預り金データが既に無いためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// formに預り金情報をセット
		this.setDepositRecvDetailInputForm(entity, inputForm);
	}

	/**
	 * 項目の候補データを取得します
	 * 
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	public List<SelectOptionForm> searchDepositRecvDataList(String searchWord, String depositType) {
		return commonAccgService.searchDepositRecvDataList(searchWord, depositType);
	}

	/**
	 * 預り金データを取得します
	 * 
	 * @param depositRecvSeq
	 * @throws AppException
	 */
	public DepositRecvDetailListDto getDepositRecv(Long depositRecvSeq) throws AppException {

		// 預り金データの確認
		DepositRecvDetailListBean bean = tDepositRecvDao.selectDepositRecvDetailByDepositRecvSeq(depositRecvSeq);
		if (bean == null) {
			// 預り金データが既に無いためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		return this.convertDepositRecvDetailBean2Dto(bean);
	}

	/**
	 * 紐づく請求書、精算書が発行されているかチェックします。<br>
	 * 発行されていれば key:isIssued, val:true を返します。<br>
	 * 下書きであれば key:isIssued, val:false を返します。<br>
	 * 発行時のみ書類タイプを key:accgDocType, val:AccgDocType を返します。
	 * 
	 * @param depositRecvSeq
	 * @return
	 * @throws AppException
	 */
	public Map<String, Object> checkIssueStatusIssued(Long depositRecvSeq) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// 預り金データ取得
		TDepositRecvEntity tDepositRecvEntity = this.getTDepositRecvEntity(depositRecvSeq);
		Long usingAccgDocSeq = tDepositRecvEntity.getUsingAccgDocSeq();
		if (usingAccgDocSeq == null) {
			// 請求書、精算書に紐づいていなければ発行されていないとする
			response.put("isIssued", false);
			return response;
		}

		// 会計書類データを取得し発行ステータスを判定
		AccgInvoiceStatementBean accgInvoiceStateBean = commonAccgSummaryService.getAccgInvoiceStatementDetail(usingAccgDocSeq);
		if (AccgDocType.INVOICE.equalsByCode(accgInvoiceStateBean.getAccgDocType())) {
			// 請求書の場合
			String invoiceIssueStatus = accgInvoiceStateBean.getInvoiceIssueStatus();

			if (!IssueStatus.DRAFT.equalsByCode(invoiceIssueStatus)) {
				// 下書きではない場合（発行済み）
				response.put("isIssued", true);
				response.put("accgDocType", AccgDocType.INVOICE);
			} else {
				// 下書きの場合（未発行）
				response.put("isIssued", false);
			}

		} else {
			// 精算書の場合
			String statementIssueStatus = accgInvoiceStateBean.getStatementIssueStatus();

			if (!IssueStatus.DRAFT.equalsByCode(statementIssueStatus)) {
				// 下書きではない場合（発行済み）
				response.put("isIssued", true);
				response.put("accgDocType", AccgDocType.STATEMENT);
			} else {
				// 下書きの場合（未発行）
				response.put("isIssued", false);
			}
		}

		return response;
	}

	/**
	 * 預り金データを登録
	 * 
	 * @param inputForm
	 * @param searchForm
	 * @throws AppException
	 */
	public void registDepositRecv(DepositRecvDetailInputForm inputForm, DepositRecvDetailSearchForm searchForm) throws AppException {

		Long ankenId = searchForm.getAnkenId();
		Long personId = searchForm.getPersonId();

		// 既に預り金の登録上限に達している場合
		List<DepositRecvDetailListBean> list = tDepositRecvDao.selectDepositRecvDetailListByParams(ankenId, personId, searchForm.toDepositRecvDetailSortCondition());
		if (!list.isEmpty() && list.size() >= CommonConstant.DEPOSIT_RECV_ADD_REGIST_LIMIT) {
			// 預り金の登録上限に達しているためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00174, null, "預り金／実費", String.valueOf(CommonConstant.DEPOSIT_RECV_ADD_REGIST_LIMIT));
		}

		// 預り金の登録
		TDepositRecvEntity entity = new TDepositRecvEntity();
		this.setDepositRecvDetailInputFormToEntity(inputForm, entity);

		int insertCount = 0;

		try {

			// 登録
			insertCount = tDepositRecvDao.insert(entity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		// 紐づく請求書、精算書の発行ステータスが下書きであれば実費明細書の再作成フラグを立てる
		commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);
	}

	/**
	 * 預り金変更時の相関バリデーションチェックです。<br>
	 * 種別を入金に変更する場合、使用先の請求書や精算書の請求項目に「預り金」が存在しないか確認します。<br>
	 * 
	 * @param depositRecvDetailInputForm
	 * @param result
	 * @throws AppException
	 */
	public void validateDepositRecvDetailInputForm(DepositRecvDetailInputForm depositRecvDetailInputForm, BindingResult result) throws AppException {
		// 新規登録の場合はチェックしない
		if (depositRecvDetailInputForm.getDepositRecvSeq() == null) {
			return;
		}
		
		// 預り金の種別が入金の場合、使用先の請求書や精算書の請求項目に「預り金」が存在するかチェック
		if (this.chekcIfInvoiceDepositExists(depositRecvDetailInputForm)) {
			result.rejectValue("depositType", null, messageService.getMessage(MessageEnum.MSG_E00180, SessionUtils.getLocale()));
		}
	}

	/**
	 * 預り金データを編集
	 * 
	 * @param inputForm
	 * @param searchForm
	 * @throws AppException
	 */
	public void editDepositRecv(DepositRecvDetailInputForm inputForm, DepositRecvDetailSearchForm searchForm) throws AppException {

		Long ankenId = searchForm.getAnkenId();
		Long personId = searchForm.getPersonId();

		// 預り金データ取得
		Long depositRecvSeq = inputForm.getDepositRecvSeq();
		TDepositRecvEntity tDepositRecvEntity = this.getTDepositRecvEntity(depositRecvSeq);

		// ▼ 預り金データの更新

		// 変更前のDBの値
		// （入出金タイプ、事務所負担フラグ、会計書類SEQ（使用先））
		String beforeDepositType = tDepositRecvEntity.getDepositType();
		String beforeTenantBearFlg = tDepositRecvEntity.getTenantBearFlg();
		Long beforeUsingAccgDocSeq = tDepositRecvEntity.getUsingAccgDocSeq();

		// formの情報をentityにセット
		this.setDepositRecvDetailInputFormToEntity(inputForm, tDepositRecvEntity);
		// 預り金情報更新
		tDepositRecvEntity = this.updateDepositRecv(tDepositRecvEntity);


		// 変更後のDBの値
		// 入出金タイプ、事務所負担フラグ、会計書類SEQ（使用先）
		String afterDepositType = tDepositRecvEntity.getDepositType();
		String afterTenantBearFlg = tDepositRecvEntity.getTenantBearFlg();
		Long afterUsingAccgDocSeq = tDepositRecvEntity.getUsingAccgDocSeq();
		// 変更後の入出金タイプが出金かどうか（出金ではない場合は入金になる）
		boolean afterDepositTypeIsShukkin = DepositType.SHUKKIN.equalsByCode(afterDepositType);

		// 案件-顧客の請求書、精算書の実費明細書の再作成フラグを立てる（発行ステータスが下書き）
		commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		// ▼ マッピングデータの更新（マッピング先の既入金項目、請求項目も含む）

		// 変更前のマッピング情報
		// ※預り金データが請求書／精算書で利用されていない場合は両方NULL。利用されている場合は、既入金項目か請求項目のどちらか片方のみマッピンデータが存在する。（両方存在することはない）

		// 既入金項目-預り金マッピングデータ
		TAccgDocRepayTDepositRecvMappingEntity beforeTAccgDocRepayTDepositRecvMappingEntity = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeq(depositRecvSeq);
		// 請求項目-預り金マッピングデータ
		TAccgDocInvoiceDepositTDepositRecvMappingEntity beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeq(depositRecvSeq);

		// 入出金タイプが変更されたかどうか
		boolean isChangeDepositType = !(beforeDepositType.equals(afterDepositType));
		// 事務所負担フラグがONに変更されたかどうか
		boolean isChangeTenantBearFlgOn = checkWhetherTenantBearFlgOn(beforeTenantBearFlg, afterTenantBearFlg);

		if (isChangeDepositType && isChangeTenantBearFlgOn) {
			// 入出金タイプの変更、と、事務所負担フラグをONに変更、の両方の変更がされた場合

			// マッピングデータ、項目データを更新
			// （既存でマッピングされていたマッピング情報（既入金項目とのマッピング）の削除）
			this.updateDepositRecvMappingCaseChangeDepositTypeAndTenantBearFlgOn(afterDepositTypeIsShukkin,
					beforeTAccgDocRepayTDepositRecvMappingEntity, beforeUsingAccgDocSeq);

		} else if (!isChangeDepositType && !isChangeTenantBearFlgOn) {
			// 入出金タイプの変更、と、事務所負担フラグをONに変更、のどちらの変更もされなかった場合

			// マッピングデータ、項目データを更新
			// （既存でマッピングしている、既入金項目or請求項目への、預り金データの反映（同期））
			this.updateDepositRecvMappingCaseNotChangeDepositTypeAndTenantBearFlgOn(afterDepositTypeIsShukkin,
					beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity, beforeTAccgDocRepayTDepositRecvMappingEntity, afterUsingAccgDocSeq);

		} else if (isChangeTenantBearFlgOn) {
			// 事務所負担フラグがONに変更された場合（入出金タイプの変更はなし）

			// マッピングデータ、項目データを更新
			// （既存でマッピングされていたマッピング情報（請求項目とのマッピング）の削除）
			this.updateDepositRecvMappingCaseChangeTenantBearFlgOn(afterDepositTypeIsShukkin,
					beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity, beforeUsingAccgDocSeq);

		} else {
			// 入出金タイプの変更がされた場合（事務所負担フラグのONへの変更はなし）

			// マッピングデータ、項目データを更新
			// （既存で請求項目にマッピングしている場合 -> 請求項目とのマッピングを削除し、既入金項目とマッピングさせる）
			// （既存で既入金項目にマッピングしている場合 -> 既入金項目とのマッピングを削除し、請求項目とマッピングさせる）
			this.updateDepositRecvMappingCaseChangeDepositType(afterDepositTypeIsShukkin,
					beforeTAccgDocRepayTDepositRecvMappingEntity, beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity,
					beforeUsingAccgDocSeq, afterUsingAccgDocSeq, beforeTenantBearFlg, depositRecvSeq);
		}
	}

	/**
	 * 預り金と預り金に関するデータを削除
	 * 
	 * @param depositRecvSeq
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void deleteDepositAndDataRelatedToDeposit(Long depositRecvSeq, Long personId, Long ankenId) throws AppException {

		// 預り金データを削除
		TDepositRecvEntity tDepositRecvEntity = this.deleteDepositRecv(depositRecvSeq);
		// 紐づく請求書、精算書の発行ステータスが下書きであれば実費明細書の再作成フラグを立てる
		commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		Long usingAccgDocSeq = tDepositRecvEntity.getUsingAccgDocSeq();
		if (usingAccgDocSeq == null) {
			// 預り金が請求書／精算書で使用されていない場合
			// -> マッピングデータの更新は不要なため、処理終了
			return;
		}

		// 預り金が請求書／精算書で使用されている場合
		// -> マッピングデータの更新を行う

		String depositType = tDepositRecvEntity.getDepositType();
		// 入金タイプなら既入金項目を削除
		if (DepositType.NYUKIN.equalsByCode(depositType)) {
			// 預り金SEQに紐づく、既入金項目_預り金テーブルマッピング、既入金項目のデータを削除
			this.deleteRelatedToAccgDocRepay(depositRecvSeq);

			// 出金タイプなら請求項目を削除
		} else if (DepositType.SHUKKIN.equalsByCode(depositType)) {
			// 預り金SEQに紐づく、請求項目-預り金（実費）_預り金テーブルマッピング、請求項目-預り金、請求項目のデータを削除
			this.deleteRelatedToAccgDocInvoiceDeposit(depositRecvSeq);

		} else {
			throw new RuntimeException("想定外の入出金タイプ");
		}

		// 請求額／精算額の更新と、再作成フラグの更新
		commonAccgService.updateInvoiceStatementAmount(usingAccgDocSeq);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	// =========================================================================
	// ▼ 登録／更新／削除系
	// =========================================================================

	/**
	 * 預り金データとのマッピングデータの更新（入出金タイプの変更と、事務所負担フラグのONへの変更が行われたケース）<br>
	 * 
	 * <pre>
	 * 処理内容：
	 *  ・既存でマッピングされていたマッピング情報（既入金項目とのマッピング）の削除
	 * </pre>
	 * 
	 * <pre>
	 * 事務所負担フラグをONに更新できるのは、入出金タイプが「出金」の場合のみなので、
	 * このケースの変更操作は下記のケースのみとなる。
	 * 
	 * ・入出金タイプの変更：入金→出金
	 * ・事務所負担グラグの変更：OFF→ON
	 * </pre>
	 * 
	 * @param afterDepositTypeIsShukkin 預り金データ変更後の、入出金タイプ
	 * @param beforeTAccgDocRepayTDepositRecvMappingEntity
	 * 預り金データ変更前の、既入金項目とのマッピングデータ
	 * @param beforeUsingAccgDocSeq 預り金データ変更前の、マッピングしていた既入金項目が登録されていた会計書類のSEQ
	 * @throws AppException
	 */
	private void updateDepositRecvMappingCaseChangeDepositTypeAndTenantBearFlgOn(boolean afterDepositTypeIsShukkin, TAccgDocRepayTDepositRecvMappingEntity beforeTAccgDocRepayTDepositRecvMappingEntity, Long beforeUsingAccgDocSeq) throws AppException {

		if (!afterDepositTypeIsShukkin) {
			// 変更後の入出金タイプが「出金」になっていない場合
			// -> 楽観ロックエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (beforeTAccgDocRepayTDepositRecvMappingEntity != null) {
			// もともと既入金項目にマッピングされていた場合

			// 既入金のマッピングデータを削除し、紐づいていた既入金項目のデータの更新or削除を行う
			this.deleteMappingAndUpdateDocRepay(beforeTAccgDocRepayTDepositRecvMappingEntity, beforeUsingAccgDocSeq);
			// 請求額／精算額の更新と、再作成フラグの更新
			commonAccgService.updateInvoiceStatementAmount(beforeUsingAccgDocSeq);

		} else {
			// もともとマッピングされていなかった場合
			// -> なにもしない
		}
	}

	/**
	 * 預り金データとのマッピングデータの更新（入出金タイプの変更と、事務所負担フラグのONへの変更のどちらも行われなかったケース）<br>
	 * 
	 * <pre>
	 * 処理内容：
	 *  ・既存でマッピングしている、既入金項目or請求項目への、預り金データの反映（同期）
	 * </pre>
	 * 
	 * <pre>
	 * このケースの場合、
	 * 預り金と既入金項目、請求項目とのマッピングの状態は変わらないため、既入金項目、請求項目のデータの更新（変更された預り金情報の反映）のみ実施
	 * </pre>
	 * 
	 * @param afterDepositTypeIsShukkin 預り金データ変更後の、入出金タイプ
	 * @param beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity
	 * 預り金データ変更前の、請求項目とのマッピングデータ
	 * @param beforeTAccgDocRepayTDepositRecvMappingEntity
	 * 預り金データ変更前の、既入金項目とのマッピングデータ
	 * @param afterUsingAccgDocSeq 預り金データ変更前の、マッピングしていた請求項目or既入金項目が登録されていた会計書類のSEQ
	 * @throws AppException
	 */
	private void updateDepositRecvMappingCaseNotChangeDepositTypeAndTenantBearFlgOn(boolean afterDepositTypeIsShukkin,
			TAccgDocInvoiceDepositTDepositRecvMappingEntity beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity, TAccgDocRepayTDepositRecvMappingEntity beforeTAccgDocRepayTDepositRecvMappingEntity,
			Long afterUsingAccgDocSeq) throws AppException {

		if (afterDepositTypeIsShukkin) {
			// 出金の場合

			if (beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity != null) {
				// もともと請求項目（実費）にマッピングされていた場合

				// -> 請求項目データの更新（マッピングしている預り金の情報を反映）
				this.updateDocInvoiceDepositExpense(beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity, afterUsingAccgDocSeq);
				// 請求額／精算額の更新と、再作成フラグの更新
				commonAccgService.updateInvoiceStatementAmount(afterUsingAccgDocSeq);

			} else {
				// もともとマッピングされていなかった場合
				// -> なにもしない
			}

		} else {
			// 入金の場合

			if (beforeTAccgDocRepayTDepositRecvMappingEntity != null) {
				// もともと既入金にマッピングされていた場合

				// -> 既入金項目データの更新（マッピングしている預り金の情報を反映）
				this.updateDocRepay(beforeTAccgDocRepayTDepositRecvMappingEntity, afterUsingAccgDocSeq);
				// 請求額／精算額の更新と、再作成フラグの更新
				commonAccgService.updateInvoiceStatementAmount(afterUsingAccgDocSeq);

			} else {
				// もともとマッピングされていなかった場合
				// -> なにもしない
			}
		}
	}

	/**
	 * 預り金データとのマッピングデータの更新（事務所負担フラグのONへの変更がされたケース（入出金タイプの変更はされなかった））<br>
	 * 
	 * <pre>
	 * 処理内容：
	 *  ・既存でマッピングされていたマッピング情報（請求項目とのマッピング）の削除
	 * </pre>
	 * 
	 * <pre>
	 * 事務所負担フラグをONに更新できるのは、入出金タイプが「出金」の場合のみなので、
	 * このケースの変更操作は下記のケースのみとなる。
	 * 
	 * ・入出金タイプの変更：出金→出金（変更なし）
	 * ・事務所負担グラグの変更：OFF→ON
	 * </pre>
	 * 
	 * @param afterDepositTypeIsShukkin 預り金データ変更後の、入出金タイプ
	 * @param beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity
	 * 預り金データ変更前の、請求項目とのマッピングデータ
	 * @param beforeUsingAccgDocSeq 預り金データ変更前の、マッピングしていた請求項目が登録されていた会計書類のSEQ
	 * @throws AppException
	 */
	private void updateDepositRecvMappingCaseChangeTenantBearFlgOn(boolean afterDepositTypeIsShukkin, TAccgDocInvoiceDepositTDepositRecvMappingEntity beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity, Long beforeUsingAccgDocSeq) throws AppException {

		if (!afterDepositTypeIsShukkin) {
			// 変更後の入出金タイプが「出金」になっていない（「入金」になっている）場合
			// -> 事務所負担フラグをONにすることは通常ではできないので、このケースは楽観ロックエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 以下、入出金タイプが「出金」となっている場合

		if (beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity != null) {
			// もともと請求項目（実費）にマッピングされていた場合

			// 請求項目(実費)のマッピングデータを削除し、紐づいていた請求項目(実費)のデータの更新or削除を行う
			this.deleteMappingAndUpdateDocInvoiceDepositExpense(beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity, beforeUsingAccgDocSeq);
			// 請求額／精算額の更新と、再作成フラグの更新
			commonAccgService.updateInvoiceStatementAmount(beforeUsingAccgDocSeq);

		} else {
			// もともとマッピングされていなかった場合
			// -> なにもしない
		}
	}

	/**
	 * 預り金データとのマッピングデータの更新（入出金タイプの変更が行われたケース（事務所負担フラグのONへの変更はされなかった））<br>
	 * 
	 * <pre>
	 * 処理内容：
	 *  ・既存で請求項目にマッピングしている場合 -> 請求項目とのマッピングを削除し、既入金項目とマッピングさせる。
	 *  ・既存で既入金項目にマッピングしている場合 -> 既入金項目とのマッピングを削除し、請求項目とマッピングさせる。
	 * </pre>
	 * 
	 * <pre>
	 * このケースになる変更操作は下記のケースとなる。
	 * 
	 * ・入出金タイプの変更：「入金→出金」 or「 出金→入金」
	 * ・事務所負担グラグの変更：「OFF→OFF」 or 「ON→ON」or「ON→OFF」
	 * </pre>
	 * 
	 * @param afterDepositTypeIsShukkin 預り金データ変更後の、入出金タイプ
	 * @param beforeTAccgDocRepayTDepositRecvMappingEntity
	 * 預り金データ変更前の、既入金項目とのマッピングデータ
	 * @param beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity
	 * 預り金データ変更前の、請求項目とのマッピングデータ
	 * @param beforeUsingAccgDocSeq
	 * 預り金データ変更前の、預り金がマッピングしていた既入金項目or請求項目が登録されていた会計書類のSEQ
	 * @param afterUsingAccgDocSeq
	 * 預り金データ変更後の、預り金がマッピングしている既入金項目or請求項目が登録されている会計書類のSEQ
	 * @param beforeTenantBearFlg 預り金データ変更前の、事務所負担フラグの値
	 * @param depositRecvSeq 更新を行った預り金データのSEQ
	 * @throws AppException
	 */
	private void updateDepositRecvMappingCaseChangeDepositType(boolean afterDepositTypeIsShukkin,
			TAccgDocRepayTDepositRecvMappingEntity beforeTAccgDocRepayTDepositRecvMappingEntity, TAccgDocInvoiceDepositTDepositRecvMappingEntity beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity,
			Long beforeUsingAccgDocSeq, Long afterUsingAccgDocSeq,
			String beforeTenantBearFlg, Long depositRecvSeq) throws AppException {

		if (afterDepositTypeIsShukkin) {
			// 更新後の入出金タイプが「出金」（更新前の入出金タイプは「入金」）
			// -> 更新前の入出金タイプが「入金」の場合、更新前の事務所負担フラグはOFFのため「OFF→OFF」の変更

			if (beforeTAccgDocRepayTDepositRecvMappingEntity != null) {
				// もともと既入金項目にマッピングされていた場合

				// 既入金のマッピングデータを削除し、紐づいていた既入金項目のデータの更新or削除
				this.deleteMappingAndUpdateDocRepay(beforeTAccgDocRepayTDepositRecvMappingEntity, beforeUsingAccgDocSeq);
				// 請求項目のマッピングデータを登録し、請求項目を更新or登録
				this.registTAccgDocInvoiceDepositTDepositRecvMappingAndUpdateDocInvoiceDeposit(afterUsingAccgDocSeq, depositRecvSeq);

				// 請求額／精算額の更新と、再作成フラグの更新
				commonAccgService.updateInvoiceStatementAmount(beforeUsingAccgDocSeq);

				return;
			} else {
				// もともとマッピングされていなかった場合
				// -> なにもしない
				return;
			}

		} else {
			// 更新後の入出金タイプが「入金」（更新前の入出金タイプは「出金」）
			// -> 更新後の入出金タイプが「入金」の場合、更新後の事務所負担フラグはOFFのため「OFF→OFF」か「ON→OFF」の変更

			// 更新前の事務所負担フラグ
			boolean beforeTenantBearFlgIsON = SystemFlg.FLG_ON.equalsByCode(beforeTenantBearFlg);

			if (beforeTenantBearFlgIsON) {
				// 事務所負担フラグの変更が「ON→OFF」の場合
				// -> 事務所負担フラグがもともとONだった場合 = もともとマッピングされたいなかった場合
				// -> なにもしない
				return;

			} else {
				// 事務所負担フラグの変更が「OFF→OFF」の場合

				if (beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity != null) {
					// もともと請求項目にマッピングされていた場合

					// 請求項目(実費)のマッピングデータを削除し、紐づいていた請求項目(実費)のデータの更新or削除を行う
					this.deleteMappingAndUpdateDocInvoiceDepositExpense(beforeTAccgDocInvoiceDepositTDepositRecvMappingEntity, beforeUsingAccgDocSeq);
					// 既入金項目のマッピングデータを登録し、既入金項目を更新or登録
					this.registTAccgDocRepayTDepositRecvMappingAndUpdateDocRepay(afterUsingAccgDocSeq, depositRecvSeq);

					// 請求額／精算額の更新と、再作成フラグの更新
					commonAccgService.updateInvoiceStatementAmount(beforeUsingAccgDocSeq);

					return;
				} else {
					// もともと請求項目にマッピングされていなかった場合
					// -> なにもしない
					return;
				}
			}

		}

	}

	/**
	 * 既入金項目の更新を行う<br>
	 * （引数のマッピング情報、会計書類SEQをもとに、(まとめられているorまとめられていない)既入金項目の金額などを、マッピングしている預り金の状態をもとに更新する）
	 * 
	 * @param tAccgDocRepayTDepositRecvMappingEntity マッピング情報
	 * @param accgDocSeq マッピングしている既入金項目が登録されている会計書類のSEQ
	 * @throws AppException
	 */
	private void updateDocRepay(TAccgDocRepayTDepositRecvMappingEntity tAccgDocRepayTDepositRecvMappingEntity, Long accgDocSeq) throws AppException {

		// 既入金項目データのSEQ
		Long docRepaySeq = tAccgDocRepayTDepositRecvMappingEntity.getDocRepaySeq();
		// 預り金データのSEQ
		Long depositRecvSeq = tAccgDocRepayTDepositRecvMappingEntity.getDepositRecvSeq();

		// 対象の請求書／精算書の請求項目(実費)のまとめるフラグがONかどうか
		String repaySumFlg = commonAccgService.getRepaySumFlg(accgDocSeq);
		boolean isOnRepaySumFlg = SystemFlg.FLG_ON.equalsByCode(repaySumFlg);

		if (isOnRepaySumFlg) {
			// まとめるフラグONの場合（既入金項目がまとめられている場合）

			// マッピング情報
			List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
					.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(docRepaySeq);

			if (CollectionUtils.isEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
				// マッピング情報がない
				// -> この処理のケースでは、少なくとも、引数で渡ってきたマッピング情報がDBに存在するため、存在しない場合は楽観ロックエラーとする
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// マッピング情報がある
			// -> 紐づけられている預り金データから、まとめられている既入金項目の金額を再計算し、値を反映

			// まとめられている既入金項目の金額を、現在のマッピング情報をもとに計算し、更新
			this.updateDocRepaySumAmount(docRepaySeq, tAccgDocRepayTDepositRecvMappingEntityList);

		} else {
			// まとめるフラグOFFの場合（既入金項目がまとめられていない場合）

			// 既入金項目に、預り金の情報を反映（同期）
			this.updateDocRepaySyncDeposit(docRepaySeq, depositRecvSeq);
		}
	}

	/**
	 * 請求項目の更新を行う<br>
	 * （引数のマッピング情報、会計書類SEQをもとに、(まとめられているorまとめられていない)請求項目の金額などを、マッピングしている預り金の状態をもとに更新する）
	 * 
	 * @param tAccgDocInvoiceDepositTDepositRecvMappingEntity マッピング情報
	 * @param accgDocSeq マッピングしている請求項目が登録されている会計書類のSEQ
	 * @throws AppException
	 */
	private void updateDocInvoiceDepositExpense(TAccgDocInvoiceDepositTDepositRecvMappingEntity tAccgDocInvoiceDepositTDepositRecvMappingEntity, Long accgDocSeq) throws AppException {

		// 請求項目データのSEQ
		Long docInvoiceDepositSeq = tAccgDocInvoiceDepositTDepositRecvMappingEntity.getDocInvoiceDepositSeq();
		// 預り金データのSEQ
		Long depositRecvSeq = tAccgDocInvoiceDepositTDepositRecvMappingEntity.getDepositRecvSeq();

		// 対象の請求書／精算書の請求項目(実費)のまとめるフラグがONかどうか
		String expenseSumFlg = commonAccgService.getExpenseSumFlg(accgDocSeq);
		boolean isOnExpenseSumFlg = SystemFlg.FLG_ON.equalsByCode(expenseSumFlg);

		if (isOnExpenseSumFlg) {
			// まとめるフラグONの場合（請求(実費)項目がまとめられている場合）

			// マッピング情報
			List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
					.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(docInvoiceDepositSeq);

			if (CollectionUtils.isEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
				// マッピング情報がない
				// -> この処理のケースでは、少なくとも、引数で渡ってきたマッピング情報がDBに存在するため、存在しない場合は楽観ロックエラーとする
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// マッピング情報がある
			// -> 紐づけられている預り金データから、まとめられている請求項目(実費)の金額を再計算し、値を反映

			this.updateDocInvoiceDepositExpenseSumAmount(docInvoiceDepositSeq, tAccgDocInvoiceDepositTDepositRecvMappingEntityList);
			// まとめられている請求項目(実費)の金額を、現在のマッピング情報をもとに計算し、更新

		} else {
			// まとめるフラグOFFの場合（請求(実費)がまとめられていない場合）

			// 請求項目に、預り金の情報を反映（同期）
			this.updateDocInvoiceDepositExpenseSyncDeposit(docInvoiceDepositSeq, depositRecvSeq);
		}
	}

	/**
	 * 既入金項目の金額を、マッピングデータを元に更新する
	 * 
	 * <pre>
	 * ※ 注意：既入金項目がまとめられている場合専用のメソッド
	 * </pre>
	 * 
	 * @param docRepaySeq 更新対象の既入金項目のSEQ
	 * @param tAccgDocRepayTDepositRecvMappingEntityList 既入金項目と預り金のマッピングデータ
	 * @throws AppException
	 */
	private void updateDocRepaySumAmount(Long docRepaySeq, List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList) throws AppException {

		List<Long> depositRecvSeqList = tAccgDocRepayTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());

		// まとめられていた他の預り金データ取得
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (CollectionUtils.isEmpty(tDepositRecvEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 他の預り金の入金額を合算して既入金項目に反映
		List<BigDecimal> depositAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getDepositAmount()).collect(Collectors.toList());
		BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);

		TAccgDocRepayEntity tAccgDocRepayEntity = tAccgDocRepayDao.selectDocRepayByDocRepaySeq(docRepaySeq);
		if (tAccgDocRepayEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 既入金項目の金額を更新
		tAccgDocRepayEntity.setRepayAmount(totalDepositAmount);
		this.updateTAccgDocRepay(tAccgDocRepayEntity);
	}

	/**
	 * 既入金項目に預り金のデータを反映（同期）させる<br>
	 * （反映させるデータは、項目名、金額、取引日、摘要。）
	 * 
	 * <pre>
	 * ※ 注意：既入金項目がまとめられていない（預り金と1対1で紐づいている）場合専用のメソッド
	 * </pre>
	 * 
	 * @param docRepaySeq 既入金項目のSEQ（反映先）
	 * @param depositRecvSeq 預り金のSEQ（反映元）
	 * @throws AppException
	 */
	private void updateDocRepaySyncDeposit(Long docRepaySeq, Long depositRecvSeq) throws AppException {

		// 預り金データ
		TDepositRecvEntity tDepositRecvEntity = this.getTDepositRecvEntity(depositRecvSeq);

		// 既入金項目データ
		TAccgDocRepayEntity tAccgDocRepayEntity = tAccgDocRepayDao.selectDocRepayByDocRepaySeq(docRepaySeq);
		if (tAccgDocRepayEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// マッピング情報
		List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
				.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(docRepaySeq);

		if (tAccgDocRepayTDepositRecvMappingEntityList.size() != 1) {
			// 1対1で紐づいていない -> 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 引数の預り金SEQと、マッピング情報の預り金SEQが同じかチェック
		TAccgDocRepayTDepositRecvMappingEntity mappingEntity = tAccgDocRepayTDepositRecvMappingEntityList.get(0);
		Long mappingDepositRecvSeq = mappingEntity.getDepositRecvSeq();
		if (!depositRecvSeq.equals(mappingDepositRecvSeq)) {
			// 同じではない -> 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 金額（出金額）、項目名、取引日、摘要をマッピングしている既入金項目に反映
		tAccgDocRepayEntity.setRepayAmount(tDepositRecvEntity.getDepositAmount());
		tAccgDocRepayEntity.setRepayItemName(tDepositRecvEntity.getDepositItemName());
		tAccgDocRepayEntity.setRepayTransactionDate(tDepositRecvEntity.getDepositDate());
		tAccgDocRepayEntity.setSumText(tDepositRecvEntity.getSumText());

		// 更新
		this.updateTAccgDocRepay(tAccgDocRepayEntity);
	}

	/**
	 * 請求項目(実費)の金額を、マッピングデータを元に更新する
	 * 
	 * <pre>
	 * ※ 注意：請求項目(実費)がまとめられている場合専用のメソッド
	 * </pre>
	 * 
	 * @param docInvoiceDepositSeq 更新対象の請求項目のSEQ
	 * @param tAccgDocInvoiceDepositTDepositRecvMappingEntityList 請求項目と預り金のマッピングデータ
	 * @throws AppException
	 */
	private void updateDocInvoiceDepositExpenseSumAmount(Long docInvoiceDepositSeq, List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList) throws AppException {

		List<Long> depositRecvSeqList = tAccgDocInvoiceDepositTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());

		// まとめられていた他の預り金データ取得
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (CollectionUtils.isEmpty(tDepositRecvEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 他の預り金の出金額を合算して請求項目(実費)に反映
		List<BigDecimal> withdrawalAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getWithdrawalAmount()).collect(Collectors.toList());
		BigDecimal totalWithdrawalAmount = AccountingUtils.calcTotal(withdrawalAmountList);

		TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceDepositSeq(docInvoiceDepositSeq);
		if (tAccgDocInvoiceDepositEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 請求項目の金額を更新
		tAccgDocInvoiceDepositEntity.setDepositAmount(totalWithdrawalAmount);
		this.updateTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntity);
	}

	/**
	 * 請求項目(実費)に預り金のデータを反映（同期）させる<br>
	 * （反映させるデータは、項目名、金額、取引日、摘要。）
	 * 
	 * <pre>
	 * ※ 注意：請求項目(実費)がまとめられていない（預り金と1対1で紐づいている）場合専用のメソッド
	 * </pre>
	 * 
	 * @param docInvoiceDepositSeq 請求項目のSEQ（反映先）
	 * @param depositRecvSeq 預り金のSEQ（反映元）
	 * @throws AppException
	 */
	private void updateDocInvoiceDepositExpenseSyncDeposit(Long docInvoiceDepositSeq, Long depositRecvSeq) throws AppException {

		// 預り金データ
		TDepositRecvEntity tDepositRecvEntity = this.getTDepositRecvEntity(depositRecvSeq);

		// 請求項目データ
		TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceDepositSeq(docInvoiceDepositSeq);
		if (tAccgDocInvoiceDepositEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// マッピング情報
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
				.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(docInvoiceDepositSeq);

		if (tAccgDocInvoiceDepositTDepositRecvMappingEntityList.size() != 1) {
			// 1対1で紐づいていない -> 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 引数の預り金SEQと、マッピング情報の預り金SEQが同じかチェック
		TAccgDocInvoiceDepositTDepositRecvMappingEntity mappingEntity = tAccgDocInvoiceDepositTDepositRecvMappingEntityList.get(0);
		Long mappingDepositRecvSeq = mappingEntity.getDepositRecvSeq();
		if (!depositRecvSeq.equals(mappingDepositRecvSeq)) {
			// 同じではない -> 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 金額（出金額）、項目名、取引日、摘要をマッピングしている請求項目に反映
		tAccgDocInvoiceDepositEntity.setDepositAmount(tDepositRecvEntity.getWithdrawalAmount());
		tAccgDocInvoiceDepositEntity.setDepositItemName(tDepositRecvEntity.getDepositItemName());
		tAccgDocInvoiceDepositEntity.setDepositTransactionDate(tDepositRecvEntity.getDepositDate());
		tAccgDocInvoiceDepositEntity.setSumText(tDepositRecvEntity.getSumText());

		// 更新
		this.updateTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntity);
	}

	/**
	 * 既入金項目-預り金マッピングデータを登録し、<br>
	 * 既入金項目の現在の登録状況、まとめ状況によって、既入金項目-預り金の登録or更新を行う
	 * 
	 * @param accgDocSeq
	 * @param depositRecvSeq
	 * @throws AppException
	 */
	private void registTAccgDocRepayTDepositRecvMappingAndUpdateDocRepay(Long accgDocSeq, Long depositRecvSeq) throws AppException {

		// 対象の請求書／精算書の請求項目(実費)のまとめるフラグがONかどうか
		String repaySumFlg = commonAccgService.getRepaySumFlg(accgDocSeq);
		boolean isOnRepaySumFlg = SystemFlg.FLG_ON.equalsByCode(repaySumFlg);

		if (isOnRepaySumFlg) {
			// まとめるフラグONの場合（既入金項目がまとめられている場合）

			// 会計書類の既入金項目を取得
			List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);

			// 既入金項目のSEQ（預り金とマッピングさせるもの）
			Long docRepaySeq = null;

			if (!CollectionUtils.isEmpty(tAccgDocRepayEntityList)) {
				// 既入金項目がすでにある

				if (tAccgDocRepayEntityList.size() != 1) {
					// まとめられている場合、既入金項目は1つしかはずだが、複数存在している場合
					// -> 楽観ロックエラーとする
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}

				TAccgDocRepayEntity tAccgDocRepayEntity = tAccgDocRepayEntityList.get(0);
				docRepaySeq = tAccgDocRepayEntity.getDocRepaySeq();

				// 既入金項目と、預り金のマッピングデータを登録
				this.registTAccgDocRepayTDepositRecvMapping(docRepaySeq, depositRecvSeq);
				// 既入金項目の金額更新
				this.updateDocRepayAmount(docRepaySeq);

			} else {
				// 既入金項目がまだない

				// 預り金と紐づく、既入金項目を新規に登録する
				TAccgDocRepayEntity tAccgDocRepayEntity = this.registAccgDocRepay(depositRecvSeq, accgDocSeq, isOnRepaySumFlg);
				docRepaySeq = tAccgDocRepayEntity.getDocRepaySeq();

				// 既入金項目と、預り金のマッピングデータを登録
				this.registTAccgDocRepayTDepositRecvMapping(docRepaySeq, depositRecvSeq);
			}

		} else {
			// まとめるフラグOFFの場合（既入金項目がまとめられていない場合）

			// 預り金と紐づく、既入金項目を新規に登録する
			TAccgDocRepayEntity tAccgDocRepayEntity = this.registAccgDocRepay(depositRecvSeq, accgDocSeq, isOnRepaySumFlg);
			Long docRepaySeq = tAccgDocRepayEntity.getDocRepaySeq();

			// 既入金項目と、預り金のマッピングデータを登録
			this.registTAccgDocRepayTDepositRecvMapping(docRepaySeq, depositRecvSeq);
		}
	}

	/**
	 * 請求項目-預り金マッピングデータを登録し、<br>
	 * 請求項目の現在の登録状況、まとめ状況によって、請求項目-預り金の登録or更新を行う
	 * 
	 * @param accgDocSeq
	 * @param depositRecvSeq
	 * @throws AppException
	 */
	private void registTAccgDocInvoiceDepositTDepositRecvMappingAndUpdateDocInvoiceDeposit(Long accgDocSeq, Long depositRecvSeq) throws AppException {

		// 対象の請求書／精算書の請求項目(実費)のまとめるフラグがONかどうか
		String expenseSumFlg = commonAccgService.getExpenseSumFlg(accgDocSeq);
		boolean isOnExpenseSumFlg = SystemFlg.FLG_ON.equalsByCode(expenseSumFlg);

		if (isOnExpenseSumFlg) {
			// まとめるフラグONの場合（請求(実費)項目がまとめられている場合）

			// 会計書類の請求項目-預り金を取得（種別「実費」のものがあるかチェック）
			List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByAccgDocSeq(accgDocSeq);
			List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositExpenseEntityList = tAccgDocInvoiceDepositEntityList.stream()
					.filter(entity -> InvoiceDepositType.EXPENSE.equalsByCode(entity.getInvoiceDepositType()))
					.collect(Collectors.toList());

			// 請求項目-預り金のSEQ（預り金とマッピングさせるもの）
			Long docInvoiceDepositSeq = null;

			if (!CollectionUtils.isEmpty(tAccgDocInvoiceDepositExpenseEntityList)) {
				// 請求項目-預り金(実費)がすでにある

				if (tAccgDocInvoiceDepositExpenseEntityList.size() != 1) {
					// まとめられている場合、「実費」の請求項目は1つしかはずだが、複数存在している場合
					// -> 楽観ロックエラーとする
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}

				// 預り金と紐づく、請求項目-預り金(実費)のレコード取得（まとめられているもの）
				TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = tAccgDocInvoiceDepositExpenseEntityList.get(0);
				docInvoiceDepositSeq = tAccgDocInvoiceDepositEntity.getDocInvoiceDepositSeq();

				// 請求項目-預り金と、預り金のマッピングデータを登録
				this.registTAccgDocInvoiceDepositTDepositRecvMapping(docInvoiceDepositSeq, depositRecvSeq);
				// 請求項目-預り金の金額更新
				this.updateDocInvoiceAmountDepositAmount(docInvoiceDepositSeq);

			} else {
				// 請求項目-預り金(実費)がまだない

				// 預り金と紐づく、請求項目-預り金(実費)を新規に登録する
				TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = this.registAccgDocInvoiceDepositExpense(depositRecvSeq, accgDocSeq, isOnExpenseSumFlg);
				docInvoiceDepositSeq = tAccgDocInvoiceDepositEntity.getDocInvoiceDepositSeq();

				// 請求項目-預り金と、預り金のマッピングデータを登録
				this.registTAccgDocInvoiceDepositTDepositRecvMapping(docInvoiceDepositSeq, depositRecvSeq);
			}

		} else {
			// まとめるフラグOFFの場合（請求(実費)項目がまとめられていない場合）

			// 預り金と紐づく、請求項目-預り金(実費)を新規に登録する
			TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = this.registAccgDocInvoiceDepositExpense(depositRecvSeq, accgDocSeq, isOnExpenseSumFlg);
			Long docInvoiceDepositSeq = tAccgDocInvoiceDepositEntity.getDocInvoiceDepositSeq();

			// 請求項目-預り金と、預り金のマッピングデータを登録
			this.registTAccgDocInvoiceDepositTDepositRecvMapping(docInvoiceDepositSeq, depositRecvSeq);
		}

	}

	/**
	 * 引数の預り金データ（SEQ）と紐づく、既入金項目を新規に登録する。<br>
	 * ※isOnExpenseSumFlgの状態によって、まとめ状態のデータ作成するか、まとめ状態ではないデータを作成するかを制御する
	 * 
	 * @param depositRecvSeq 預り金データのSEQ（登録する既入金項目と紐づくもの）
	 * @param accgDocSeq 会計書類SEQ（既入金項目を登録する会計書類）
	 * @param isOnRepaySumFlg 会計書類（請求書／精算書）の既入金項目合算フラグがONかどうか
	 * @return
	 * @throws AppException
	 */
	private TAccgDocRepayEntity registAccgDocRepay(Long depositRecvSeq, Long accgDocSeq, boolean isOnRepaySumFlg) throws AppException {

		TDepositRecvEntity tDepositRecvEntity = this.getTDepositRecvEntity(depositRecvSeq);

		// 登録されている既入金項目データ取得、登録する既入金項目データの並び順の値を算出
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		Long docRepayOrder = CollectionUtils.isEmpty(tAccgDocRepayEntityList) ? 1L : tAccgDocRepayEntityList.get(tAccgDocRepayEntityList.size() - 1).getDocRepayOrder() + 1;

		// 既入金項目データを作成
		TAccgDocRepayEntity newAccgDocRepayEntity = new TAccgDocRepayEntity();
		newAccgDocRepayEntity.setAccgDocSeq(accgDocSeq);
		newAccgDocRepayEntity.setRepayTransactionDate(tDepositRecvEntity.getDepositDate());
		newAccgDocRepayEntity.setSumText(tDepositRecvEntity.getSumText());
		newAccgDocRepayEntity.setDocRepayOrder(docRepayOrder);

		// 金額（預り金の入金額を設定する）
		if (DepositType.SHUKKIN.equalsByCode(tDepositRecvEntity.getDepositType())) {
			// 登録する既入金項目に紐づく預り金の、入出金タイプが「出金」になっている場合。（「入金」でないといけない）

			// 楽観ロックエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		newAccgDocRepayEntity.setRepayAmount(tDepositRecvEntity.getDepositAmount());

		// まとめる設定の状態により値が変わるもの
		if (isOnRepaySumFlg) {
			// 既入金項目をまとめる設定がされている場合

			// 項目名 -> 「預り金」とする
			newAccgDocRepayEntity.setRepayItemName(InvoiceDepositType.DEPOSIT.getVal());
		} else {
			// 既入金項目をまとめる設定がされていない場合

			// 項目名 -> 預り金の項目名と同じにする
			newAccgDocRepayEntity.setRepayItemName(tDepositRecvEntity.getDepositItemName());
		}

		this.registTAccgDocRepay(newAccgDocRepayEntity);

		return newAccgDocRepayEntity;
	}

	/**
	 * 引数の預り金データ（SEQ）と紐づく、請求項目-預り金(実費)を新規に登録する。<br>
	 * ※isOnExpenseSumFlgの状態によって、まとめ状態のデータ作成するか、まとめ状態ではないデータを作成するかを制御する
	 * 
	 * @param depositRecvSeq 預り金データのSEQ（登録する請求項目と紐づくもの）
	 * @param accgDocSeq 会計書類SEQ（請求項目を登録する会計書類）
	 * @param isOnExpenseSumFlg 会計書類（請求書／精算書）の実費合算フラグがONかどうか
	 * @throws AppException
	 */
	private TAccgDocInvoiceDepositEntity registAccgDocInvoiceDepositExpense(Long depositRecvSeq, Long accgDocSeq, boolean isOnExpenseSumFlg) throws AppException {

		TDepositRecvEntity tDepositRecvEntity = this.getTDepositRecvEntity(depositRecvSeq);

		// 登録されている請求項目データ取得、登録する請求項目データの並び順の値を算出
		List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByAccgDocSeq(accgDocSeq);
		Long docInvoiceOrder = CollectionUtils.isEmpty(tAccgDocInvoiceEntityList) ? 1L : tAccgDocInvoiceEntityList.get(tAccgDocInvoiceEntityList.size() - 1).getDocInvoiceOrder() + 1;

		// 請求項目データを作成
		TAccgDocInvoiceEntity tAccgDocInvoiceEntity = this.registTAccgDocInvoice(accgDocSeq, docInvoiceOrder);
		Long docInvoiceSeq = tAccgDocInvoiceEntity.getDocInvoiceSeq();

		// 請求項目-預り金データを作成
		TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = new TAccgDocInvoiceDepositEntity();
		tAccgDocInvoiceDepositEntity.setDocInvoiceSeq(docInvoiceSeq);
		tAccgDocInvoiceDepositEntity.setDepositTransactionDate(tDepositRecvEntity.getDepositDate());
		tAccgDocInvoiceDepositEntity.setSumText(tDepositRecvEntity.getSumText());

		// 金額（預り金の出金額を設定する）
		if (DepositType.NYUKIN.equalsByCode(tDepositRecvEntity.getDepositType())) {
			// 登録する請求項目-預り金(実費)に紐づく預り金の、入出金タイプが「入金」になっている場合。（「出金」でないといけない）

			// 楽観ロックエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		tAccgDocInvoiceDepositEntity.setDepositAmount(tDepositRecvEntity.getWithdrawalAmount());

		// 預り金タイプは固定で「実費」
		tAccgDocInvoiceDepositEntity.setInvoiceDepositType(InvoiceDepositType.EXPENSE.getCd());

		// まとめる設定の状態により値が変わるもの
		if (isOnExpenseSumFlg) {
			// 実費をまとめる設定がされている場合

			// 項目名 -> 「実費」とする
			tAccgDocInvoiceDepositEntity.setDepositItemName(InvoiceDepositType.EXPENSE.getVal());
		} else {
			// 実費をまとめる設定がされていない場合

			// 項目名 -> 預り金の項目名と同じにする
			tAccgDocInvoiceDepositEntity.setDepositItemName(tDepositRecvEntity.getDepositItemName());
		}

		// 登録
		this.registTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntity);

		return tAccgDocInvoiceDepositEntity;
	}

	/**
	 * 既入金項目-預り金マッピングを削除し、既入金項目の更新（もしくは削除）を行う
	 * 
	 * @param tAccgDocRepayTDepositRecvMappingEntity 削除対象のEntity
	 * @param accgDocSeq 更新対象の既入金項目を持つ会計書類のSEQ
	 * @throws AppException
	 */
	private void deleteMappingAndUpdateDocRepay(TAccgDocRepayTDepositRecvMappingEntity tAccgDocRepayTDepositRecvMappingEntity, Long accgDocSeq) throws AppException {

		// 既入金項目データのSEQ
		Long docRepaySeq = tAccgDocRepayTDepositRecvMappingEntity.getDocRepaySeq();

		// マッピングデータを削除
		this.deletetAccgDocRepayTDepositRecvMapping(tAccgDocRepayTDepositRecvMappingEntity);

		// 対象の請求書／精算書の既入金のまとめるフラグがONかどうか
		String repaySumFlg = commonAccgService.getRepaySumFlg(accgDocSeq);
		boolean isOnRepaySumFlg = SystemFlg.FLG_ON.equalsByCode(repaySumFlg);

		if (isOnRepaySumFlg) {
			// まとめるフラグONの場合（既入金項目がまとめられている場合）

			// 削除後に残っているマッピング情報
			List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
					.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(docRepaySeq);

			if (CollectionUtils.isEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
				// マッピング情報が残っていない
				// -> 既入金項目を削除

				this.deleteTAccgDocRepay(docRepaySeq);
			} else {
				// マッピング情報が残っている
				// -> 紐づけられている預り金データから、まとめられている既入金項目の金額を再計算し、値を反映

				// まとめられている既入金項目の金額を、現在のマッピング情報をもとに計算し、更新
				this.updateDocRepaySumAmount(docRepaySeq, tAccgDocRepayTDepositRecvMappingEntityList);
			}
		} else {
			// まとめるフラグOFFの場合（既入金項目がまとめられていない場合）

			// 預り金と1対1で紐づいていた既入金項目を削除
			this.deleteTAccgDocRepay(docRepaySeq);
		}

	}

	/**
	 * 請求項目(実費)-預り金マッピングを削除し、請求項目(実費)の更新（もしくは削除）を行う
	 * 
	 * @param tAccgDocInvoiceDepositTDepositRecvMappingEntity 削除対象のEntity
	 * @param accgDocSeq 更新対象の請求項目を持つ会計書類のSEQ
	 * @throws AppException
	 */
	private void deleteMappingAndUpdateDocInvoiceDepositExpense(TAccgDocInvoiceDepositTDepositRecvMappingEntity tAccgDocInvoiceDepositTDepositRecvMappingEntity, Long accgDocSeq) throws AppException {

		// 請求項目データのSEQ
		Long docInvoiceDepositSeq = tAccgDocInvoiceDepositTDepositRecvMappingEntity.getDocInvoiceDepositSeq();

		// マッピングデータを削除
		this.deleteTAccgDocInvoiceDepositTDepositRecvMapping(tAccgDocInvoiceDepositTDepositRecvMappingEntity);

		// 対象の請求書／精算書の請求項目(実費)のまとめるフラグがONかどうか
		String expenseSumFlg = commonAccgService.getExpenseSumFlg(accgDocSeq);
		boolean isOnExpenseSumFlg = SystemFlg.FLG_ON.equalsByCode(expenseSumFlg);

		if (isOnExpenseSumFlg) {
			// まとめるフラグONの場合（請求(実費)項目がまとめられている場合）

			// 削除後に残っているマッピング情報
			List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
					.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(docInvoiceDepositSeq);

			if (CollectionUtils.isEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
				// マッピング情報が残っていない
				// -> 請求項目を削除

				commonAccgService.deleteTAccgDocInvoiceDepositAndDocInvoice(Arrays.asList(docInvoiceDepositSeq));
			} else {
				// マッピング情報が残っている
				// -> 紐づけられている預り金データから、まとめられている請求項目(実費)の金額を再計算し、値を反映

				// まとめられている請求項目(実費)の金額を、現在のマッピング情報をもとに計算し、更新
				this.updateDocInvoiceDepositExpenseSumAmount(docInvoiceDepositSeq, tAccgDocInvoiceDepositTDepositRecvMappingEntityList);
			}
		} else {
			// まとめるフラグOFFの場合（請求(実費)がまとめられていない場合）

			// 預り金と1対1で紐づいていた請求項目を削除
			commonAccgService.deleteTAccgDocInvoiceDepositAndDocInvoice(Arrays.asList(docInvoiceDepositSeq));
		}
	}

	/**
	 * まとめて表示している既入金項目の金額の値を再計算し、更新
	 * 
	 * @param docRepaySeq
	 * @throws AppException
	 */
	private void updateDocRepayAmount(Long docRepaySeq) throws AppException {
		// 既入金項目SEQに紐づく既入金預り金マッピングデータ取得
		List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
				.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(docRepaySeq);
		if (CollectionUtils.isEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
			return;
		}
		// まとめられている預り金SEQを取得
		List<Long> depositRecvSeqList = tAccgDocRepayTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
		// 対象の預り金データを取得
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (CollectionUtils.isEmpty(tDepositRecvEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 預り金の入金額合計を既入金金額へ反映
		List<BigDecimal> depositAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getDepositAmount()).collect(Collectors.toList());
		BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);

		TAccgDocRepayEntity tAccgDocRepayEntity = tAccgDocRepayDao.selectDocRepayByDocRepaySeq(docRepaySeq);
		tAccgDocRepayEntity.setRepayAmount(totalDepositAmount);
		this.updateTAccgDocRepay(tAccgDocRepayEntity);
	}

	/**
	 * まとめて表示している請求項目-預り金(実費)の金額を再計算し、更新
	 * 
	 * @param docInvoiceDepositSeq
	 * @throws AppException
	 */
	private void updateDocInvoiceAmountDepositAmount(Long docInvoiceDepositSeq) throws AppException {
		// 請求預り金項目SEQに紐づく請求項目預り金マッピングデータ取得
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
				.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(docInvoiceDepositSeq);
		if (CollectionUtils.isEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
			return;
		}
		// まとめられている預り金SEQを取得
		List<Long> depositRecvSeqList = tAccgDocInvoiceDepositTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
		// 対象の預り金データを取得
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (CollectionUtils.isEmpty(tDepositRecvEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 預り金の出金額合計を預り金金額へ反映
		List<BigDecimal> withdrawalAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getWithdrawalAmount()).collect(Collectors.toList());
		BigDecimal totalWithdrawalAmount = AccountingUtils.calcTotal(withdrawalAmountList);

		TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceDepositSeq(docInvoiceDepositSeq);
		tAccgDocInvoiceDepositEntity.setDepositAmount(totalWithdrawalAmount);
		this.updateTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntity);
	}

	/**
	 * 預り金SEQに紐づく、既入金項目_預り金テーブルマッピング、既入金項目のデータを削除します。<br>
	 * 削除する預り金が他の預り金とまとめられている場合は、まとめられていた預り金額の合計を再計算します。
	 * 
	 * @param depositRecvSeq
	 * @throws AppException
	 */
	private void deleteRelatedToAccgDocRepay(Long depositRecvSeq) throws AppException {
		// 既入金項目マッピングデータを削除
		TAccgDocRepayTDepositRecvMappingEntity tAccgDocRepayTDepositRecvMappingEntity = this.deletetAccgDocRepayTDepositRecvMapping(depositRecvSeq);
		if (tAccgDocRepayTDepositRecvMappingEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 既入金項目でまとめられているデータを取得
		Long docRepaySeq = tAccgDocRepayTDepositRecvMappingEntity.getDocRepaySeq();
		List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
				.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(docRepaySeq);

		if (CollectionUtils.isEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
			// まとめるチェックの状態にかかわらず、引数の預り金データのみが既入金項目に紐づいていた場合

			// まとめているデータがなければ既入金項目データを削除
			this.deleteTAccgDocRepay(docRepaySeq);
		} else {
			// このケースにくるのは、
			// まとめるチェックがされており、かつ、引数の預り金データ以外の預り金データが既入金項目に紐づいていた場合

			// 他の預り金とまとめられていた場合は、他の預り金を合計して既入金項目に反映
			List<Long> depositRecvSeqList = tAccgDocRepayTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
			// まとめられていた他の預り金データ取得
			List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
			if (CollectionUtils.isEmpty(tDepositRecvEntityList)) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			// 他預り金の入金額を合算して既入金項目に反映
			List<BigDecimal> depositAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getDepositAmount()).collect(Collectors.toList());
			BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);
			TAccgDocRepayEntity tAccgDocRepayEntity = tAccgDocRepayDao.selectDocRepayByDocRepaySeq(docRepaySeq);
			if (tAccgDocRepayEntity == null) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			tAccgDocRepayEntity.setRepayAmount(totalDepositAmount);
			this.updateTAccgDocRepay(tAccgDocRepayEntity);
		}
	}

	/**
	 * 預り金SEQに紐づく、請求項目-預り金（実費）_預り金テーブルマッピング、請求項目-預り金、請求項目のデータを削除します。<br>
	 * 削除する預り金が他の預り金とまとめられている場合は、まとめられていた預り金額の合計を再計算します。
	 * 
	 * @param depositRecvSeq
	 * @throws AppException
	 */
	private void deleteRelatedToAccgDocInvoiceDeposit(Long depositRecvSeq) throws AppException {
		// 請求項目マッピングデータを削除
		TAccgDocInvoiceDepositTDepositRecvMappingEntity tAccgDocInvoiceDepositTDepositRecvMappingEntity = this.deleteTAccgDocInvoiceDepositTDepositRecvMapping(depositRecvSeq);
		if (tAccgDocInvoiceDepositTDepositRecvMappingEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 請求項目-預り金でまとめられているデータを取得
		Long docInvoiceDepositSeq = tAccgDocInvoiceDepositTDepositRecvMappingEntity.getDocInvoiceDepositSeq();
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
				.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(docInvoiceDepositSeq);

		if (CollectionUtils.isEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
			// まとめるチェックの状態にかかわらず、引数の預り金データのみが請求項目に紐づいていた場合

			// まとめているデータがなければ、請求項目、請求項目-預り金データを削除
			commonAccgService.deleteTAccgDocInvoiceDepositAndDocInvoice(Arrays.asList(docInvoiceDepositSeq));
		} else {
			// このケースにくるのは、
			// まとめるチェックがされており、かつ、引数の預り金データ以外の預り金データが請求項目に紐づいていた場合

			// 他の預り金とまとめられていた場合は、その預り金額を合計して請求項目-預り金に反映
			List<Long> depositRecvSeqList = tAccgDocInvoiceDepositTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
			List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
			if (CollectionUtils.isEmpty(tDepositRecvEntityList)) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			List<BigDecimal> withdrawalAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getWithdrawalAmount()).collect(Collectors.toList());
			BigDecimal totalWithdrawalAmount = AccountingUtils.calcTotal(withdrawalAmountList);
			TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceDepositSeq(docInvoiceDepositSeq);
			if (tAccgDocInvoiceDepositEntity == null) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			tAccgDocInvoiceDepositEntity.setDepositAmount(totalWithdrawalAmount);
			this.updateTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntity);
		}
	}

	// =========================================================================
	// ▼ 状態判定系
	// =========================================================================

	/**
	 * 事務所負担フラグが「事務所負担ではない」→「事務所負担」に変更されたかチェックします。<br>
	 * 事務所負担に変更された場合は true を返します。
	 * 
	 * @param beforeTenantBearFlg
	 * @param afterTenantBearFlg
	 * @return
	 */
	private boolean checkWhetherTenantBearFlgOn(String beforeTenantBearFlg, String afterTenantBearFlg) {
		if ((SystemFlg.FLG_OFF.equalsByCode(beforeTenantBearFlg) || beforeTenantBearFlg == null) && SystemFlg.FLG_ON.equalsByCode(afterTenantBearFlg)) {
			return true;
		}
		return false;
	}

	/**
	 * 預り金データが請求書／精算書から作成されているかチェックします。<br>
	 * 
	 * @param depositRecvSeq
	 * @return
	 * @throws AppException
	 */
	public Map<String, Object> checkIfDepositCreatedFromAccgDoc(Long depositRecvSeq) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// 預り金データ取得
		TDepositRecvEntity tDepositRecvEntity = this.getTDepositRecvEntity(depositRecvSeq);
		Long createdAccgDocSeq = tDepositRecvEntity.getCreatedAccgDocSeq();
		if (createdAccgDocSeq == null) {
			// 請求書、精算書から作成されていない
			response.put("isAccgDocCreated", false);
			return response;
		} else {
			// 請求書、精算書から作成されている
			TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(createdAccgDocSeq);
			if (tAccgDocEntity == null) {
				throw new RuntimeException("作成元の請求書／精算書が存在しない想定外な状態");
			}
			response.put("isAccgDocCreated", true);
			response.put("accgDocType", AccgDocType.of(tAccgDocEntity.getAccgDocType()));
			return response;
		}
	}

	/**
	 * 請求書や精算書に既入金と預り金の登録を防ぐため
	 * 預り金種別が入金の場合、請求項目-預り金テーブルに預り金情報が存在するかチェックします。<br>
	 * 存在する場合はtrueを返します。<br>
	 * 
	 * @param depositRecvDetailInputForm
	 * @return
	 * @throws AppException
	 */
	private boolean chekcIfInvoiceDepositExists(DepositRecvDetailInputForm depositRecvDetailInputForm) throws AppException {
		boolean isInvoiceDepositExists = false;
		// 種別が出金であればチェックしない
		if (DepositType.SHUKKIN.equalsByCode(depositRecvDetailInputForm.getDepositType())) {
			return isInvoiceDepositExists;
		}
		
		// 登録中の預り金情報
		TDepositRecvEntity registeredDepositRecvEntity = this
				.getTDepositRecvEntity(depositRecvDetailInputForm.getDepositRecvSeq());
		
		// 使用先会計書類SEQ
		Long usingAccgDocSeq = registeredDepositRecvEntity.getUsingAccgDocSeq();
		
		// 請求書や精算書で使用されていなければチェックしない
		if (usingAccgDocSeq == null) {
			return isInvoiceDepositExists;
		}
		
		isInvoiceDepositExists = commonAccgService.chekcIfInvoiceDepositExists(usingAccgDocSeq);
		
		return isInvoiceDepositExists;
	}
	// =========================================================================
	// ▼ 取得／データ変換系
	// =========================================================================

	/**
	 * 預り金データを取得します
	 * 
	 * @param depositRecvSeq
	 * @return
	 * @throws AppException
	 */
	private TDepositRecvEntity getTDepositRecvEntity(Long depositRecvSeq) throws AppException {
		TDepositRecvEntity tDepositRecvEntity = tDepositRecvDao.selectDepositRecvByDepositRecvSeq(depositRecvSeq);
		if (tDepositRecvEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		return tDepositRecvEntity;
	}

	/**
	 * 預り金明細のチェックボックス表示/非表示を預り金情報を基に判定します。<br>
	 * チェックボックスを表示する場合は true を返します。<br>
	 * 
	 * <pre>
	 * ■true条件
	 * ユーザーが手動作成した「入金/出金（事務所負担ではない）」で請求書/精算書で使用されていない預り金。
	 * 請求書/精算書から自動生成された「実費以外の入金済み」で請求書/精算書で使用されていない預り金。
	 * ■false条件
	 * ユーザーが手動作成し請求書/精算書で使用されている預り金。
	 * ユーザーが手動作成した事務所負担の預り金。
	 * 請求書/精算書から自動生成された「出金」の預り金。
	 * 請求書/精算書から自動生成された「入金」の預り金で未入金のもの。
	 * 請求書/精算書から自動生成された「入金」の預り金で「実費」のもの。
	 * 請求書/精算書から自動生成された「入金」の預り金で「実費以外」で入金済みでも請求書/精算書で使用されているもの
	 * 請求書/精算書から自動生成された「既入金の報酬振替」の預り金。
	 * </pre>
	 * 
	 * @param depositRecvDetailListBean 預り金情報
	 * @return
	 */
	private boolean getDepositRecvDetailCheckboxDisplayFlg(DepositRecvDetailListBean depositRecvDetailListBean) {
		boolean isCheckboxDispFlg = false;
		// 作成タイプ
		DepositRecvCreatedType createdType = DepositRecvCreatedType.of(depositRecvDetailListBean.getCreatedType());
		// 事務所負担フラグ
		SystemFlg tenantBearFlg = SystemFlg.of(depositRecvDetailListBean.getTenantBearFlg());
		// 会計書類使用先
		Long usingAccgDocSeq = depositRecvDetailListBean.getUsingAccgDocSeq();
		// 入金/出金タイプ
		DepositType depositType = DepositType.of(depositRecvDetailListBean.getDepositType());
		// 実費入金フラグ
		ExpenseInvoiceFlg expenseInvoiceFlg = ExpenseInvoiceFlg.of(depositRecvDetailListBean.getExpenseInvoiceFlg());
		// 入出金完了フラグ
		SystemFlg depositCompleteFlg = SystemFlg.of(depositRecvDetailListBean.getDepositCompleteFlg());

		// 対象の預り金データが、請求書／精算書で利用可能（チェック可能）かどうかを判定する
		isCheckboxDispFlg = commonAccgService.isCanUseDepositRecvOnAccgDoc(createdType, tenantBearFlg, usingAccgDocSeq,
				depositType, expenseInvoiceFlg, depositCompleteFlg);

		return isCheckboxDispFlg;
	}

	/**
	 * List<DepositRecvDetailListBean>型からList<DepositRecvDetailListDto>型に変換します<br>
	 * 
	 * @param depositRecvDetailListBeanList
	 * @return
	 */
	private List<DepositRecvDetailListDto> convertBeanList2Dto(List<DepositRecvDetailListBean> depositRecvDetailListBeanList) {
		// Bean型をDto型に変換する
		List<DepositRecvDetailListDto> dtoList = new ArrayList<DepositRecvDetailListDto>();
		depositRecvDetailListBeanList.forEach(bean -> {
			dtoList.add(convertDepositRecvDetailBean2Dto(bean));
		});
		return dtoList;
	}

	/**
	 * DepositRecvDetailListBean型からDepositRecvDetailListDto型に変換します<br>
	 * 
	 * @param depositRecvDetailListBean
	 * @return
	 */
	private DepositRecvDetailListDto convertDepositRecvDetailBean2Dto(DepositRecvDetailListBean depositRecvDetailListBean) {

		DepositRecvDetailListDto depositRecvDetailListDto = new DepositRecvDetailListDto();

		// ID系
		depositRecvDetailListDto.setDepositRecvSeq(depositRecvDetailListBean.getDepositRecvSeq());
		depositRecvDetailListDto.setPersonId(depositRecvDetailListBean.getPersonId());
		depositRecvDetailListDto.setAnkenId(depositRecvDetailListBean.getAnkenId());

		// 表示データ系
		depositRecvDetailListDto.setDepositItemName(depositRecvDetailListBean.getDepositItemName());
		depositRecvDetailListDto.setDepositDate(depositRecvDetailListBean.getDepositDate());
		depositRecvDetailListDto.setDepositType(depositRecvDetailListBean.getDepositType());
		depositRecvDetailListDto.setDepositAmount(depositRecvDetailListBean.getDepositAmount());
		depositRecvDetailListDto.setWithdrawalAmount(depositRecvDetailListBean.getWithdrawalAmount());
		depositRecvDetailListDto.setTenantBear(SystemFlg.FLG_ON.equalsByCode(depositRecvDetailListBean.getTenantBearFlg()) ? true : false);
		depositRecvDetailListDto.setSumText(depositRecvDetailListBean.getSumText());
		depositRecvDetailListDto.setDepositRecvMemo(depositRecvDetailListBean.getDepositRecvMemo());

		// 状態判定系
		depositRecvDetailListDto.setDepositCompleteFlg(depositRecvDetailListBean.getDepositCompleteFlg());
		depositRecvDetailListDto.setUncollectibleFlg(depositRecvDetailListBean.getUncollectibleFlg());
		depositRecvDetailListDto.setCreatedType(depositRecvDetailListBean.getCreatedType());
		depositRecvDetailListDto.setExpenseInvoiceFlg(depositRecvDetailListBean.getExpenseInvoiceFlg());
		depositRecvDetailListDto.setCheckboxDisplayFlg(this.getDepositRecvDetailCheckboxDisplayFlg(depositRecvDetailListBean));

		// 請求書／精算書番号系
		depositRecvDetailListDto.setCreatedAccgDocSeq(depositRecvDetailListBean.getCreatedAccgDocSeq());
		depositRecvDetailListDto.setCreatedInvoiceSeq(depositRecvDetailListBean.getCreatedInvoiceSeq());
		depositRecvDetailListDto.setCreatedInvoiceNo(depositRecvDetailListBean.getCreatedInvoiceNo());
		depositRecvDetailListDto.setCreatedInvoicePaymentStatus(depositRecvDetailListBean.getCreatedInvoicePaymentStatus());
		depositRecvDetailListDto.setCreatedStatementSeq(depositRecvDetailListBean.getCreatedStatementSeq());
		depositRecvDetailListDto.setCreatedStatementNo(depositRecvDetailListBean.getCreatedStatementNo());
		depositRecvDetailListDto.setCreatedStatementRefundStatus(depositRecvDetailListBean.getCreatedStatementRefundStatus());
		depositRecvDetailListDto.setUsingAccgDocSeq(depositRecvDetailListBean.getUsingAccgDocSeq());
		depositRecvDetailListDto.setUsingInvoiceSeq(depositRecvDetailListBean.getUsingInvoiceSeq());
		depositRecvDetailListDto.setUsingInvoiceNo(depositRecvDetailListBean.getUsingInvoiceNo());
		depositRecvDetailListDto.setUsingInvoicePaymentStatus(depositRecvDetailListBean.getUsingInvoicePaymentStatus());
		depositRecvDetailListDto.setUsingStatementSeq(depositRecvDetailListBean.getUsingStatementSeq());
		depositRecvDetailListDto.setUsingStatementNo(depositRecvDetailListBean.getUsingStatementNo());
		depositRecvDetailListDto.setUsingStatementRefundStatus(depositRecvDetailListBean.getUsingStatementRefundStatus());

		return depositRecvDetailListDto;
	}

	/**
	 * 預り金明細入力フォームの情報を預り金エンティティに設定します
	 * 
	 * @param inputForm
	 * @param entity
	 */
	private void setDepositRecvDetailInputFormToEntity(DepositRecvDetailInputForm inputForm, TDepositRecvEntity entity) {

		entity.setPersonId(inputForm.getPersonId());
		entity.setAnkenId(inputForm.getAnkenId());
		entity.setDepositDate(DateUtils.parseToLocalDate(inputForm.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		entity.setDepositItemName(inputForm.getDepositItemName());
		entity.setDepositType(inputForm.getDepositType());
		entity.setSumText(inputForm.getSumText());
		entity.setDepositRecvMemo(inputForm.getDepositRecvMemo());

		DepositType depositTypeEnum = DepositType.of(inputForm.getDepositType());

		// 入金額、出金額
		if (depositTypeEnum == DepositType.NYUKIN) {
			// 入金の場合
			entity.setDepositAmount(LoiozNumberUtils.parseAsBigDecimal(inputForm.getAmountOfMoney()));
			entity.setWithdrawalAmount(null);
		} else {
			// 出金の場合
			entity.setDepositAmount(null);
			entity.setWithdrawalAmount(LoiozNumberUtils.parseAsBigDecimal(inputForm.getAmountOfMoney()));
		}

		// 事務所負担フラグ
		if (depositTypeEnum == DepositType.NYUKIN) {
			// 入金の場合
			// -> 固定でNULL（事務所負担フラグは入金の場合は設定不可）
			entity.setTenantBearFlg(null);
		} else {
			// 出金の場合
			// -> 入力値の状態のまま設定
			String isTenantBearFlg = SystemFlg.booleanToCode(inputForm.isTenantBearFlg());
			entity.setTenantBearFlg(isTenantBearFlg);
		}

		// 会計書類SEQ（使用先）
		if (SystemFlg.codeToBoolean(entity.getTenantBearFlg())) {
			// 事務所負担フラグがONの場合
			// -> この預り金は請求書／精算書で利用されないので、NULLを設定
			entity.setUsingAccgDocSeq(null);
		}

		// 作成タイプが無ければ（初回作成時）、作成タイプ、入出金完了フラグ、実費入金フラグを設定する。
		// 請求書／精算書から自動生成されているデータでも「メモ」のみ更新できるため、その場合に自動生成時の状態を上書かないようにする
		if (StringUtils.isEmpty(entity.getCreatedType())) {
			// 作成タイプ
			entity.setCreatedType(DepositRecvCreatedType.USER_CREATED.getCd());

			// 入出金完了フラグ
			entity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());

			// 実費入金フラグ
			entity.setExpenseInvoiceFlg(SystemFlg.FLG_OFF.getCd());
		}
	}

	/**
	 * 預り金情報を入力用フォームに設定します
	 * 
	 * @param entity
	 * @param inputForm
	 */
	private void setDepositRecvDetailInputForm(TDepositRecvEntity entity, DepositRecvDetailInputForm inputForm) throws AppException {
		// 預り金SEQ、名簿ID、案件ID
		inputForm.setDepositRecvSeq(entity.getDepositRecvSeq());
		inputForm.setPersonId(entity.getPersonId());
		inputForm.setAnkenId(entity.getAnkenId());

		// 発生日
		if (entity.getDepositDate() != null) {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			inputForm.setDepositDate(entity.getDepositDate().format(dateTimeFormatter));
		} else {
			inputForm.setDepositDate(null);
		}

		// 入金額、出金額
		inputForm.setDepositItemName(entity.getDepositItemName());
		if (DepositType.NYUKIN.equalsByCode(entity.getDepositType())) {
			inputForm.setAmountOfMoney(entity.getDepositAmount().toString());
		} else {
			inputForm.setAmountOfMoney(entity.getWithdrawalAmount().toString());
		}

		// 種別、摘要、事務所負担フラグ、メモ
		inputForm.setDepositType(entity.getDepositType());
		inputForm.setSumText(entity.getSumText());
		inputForm.setTenantBearFlg(SystemFlg.FLG_ON.equalsByCode(entity.getTenantBearFlg()) ? true : false);
		inputForm.setDepositRecvMemo(entity.getDepositRecvMemo());

		// アカウント名を取得
		Map<Long, String> accountMap = commonAccountService.getAccountNameMap();
		String createdByName = accountMap.get(entity.getCreatedBy());
		String updatedByName = accountMap.get(entity.getUpdatedBy());

		// 登録情報
		inputForm.setCreatedAtStr(DateUtils.parseToString(entity.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		inputForm.setCreatedByName(createdByName);

		// 更新情報
		inputForm.setUpdatedAtStr(DateUtils.parseToString(entity.getUpdatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		inputForm.setUpdatedByName(updatedByName);

		// 紐づく請求書、精算書が発行しているかどうか
		Map<String, Object> issueStatusResult = this.checkIssueStatusIssued(entity.getDepositRecvSeq());
		boolean isIssued = (boolean) issueStatusResult.get("isIssued");
		inputForm.setIssued(isIssued);
		if (isIssued) {
			// 紐づく請求書／精算書が発行されている場合 -> 編集不可メッセージ
			AccgDocType docType = (AccgDocType) issueStatusResult.get("accgDocType");
			String issuedMessage = messageService.getMessage(MessageEnum.MSG_W00016, SessionUtils.getLocale(),
					docType.getVal(), "メモ以外の編集");
			inputForm.setIssuedMessage(issuedMessage);
		}
	}

	// =========================================================================
	// ▼ 登録／更新／削除の実行メソッド（ロジックをほぼ含まないもの）
	// =========================================================================

	/**
	 * 預り金データを更新します。
	 * 
	 * @param entity
	 * @throws AppException
	 */
	private TDepositRecvEntity updateDepositRecv(TDepositRecvEntity entity) throws AppException {
		int updateCount = 0;

		try {
			// 更新
			updateCount = tDepositRecvDao.update(entity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 変更処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return entity;
	}

	/**
	 * 預り金SEQに紐づく預り金データを削除します
	 * 
	 * @param depositRecvSeq
	 */
	private TDepositRecvEntity deleteDepositRecv(Long depositRecvSeq) throws AppException {

		// 預り金データの確認
		TDepositRecvEntity entity = tDepositRecvDao.selectDepositRecvByDepositRecvSeq(depositRecvSeq);
		if (entity == null) {
			// 預り金データが既に無いためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tDepositRecvDao.delete(entity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}

		return entity;
	}

	/**
	 * 請求項目データを登録します。
	 * 
	 * @param usingAccgDocSeq
	 * @param docInvoiceOrder
	 * @return
	 * @throws AppException
	 */
	private TAccgDocInvoiceEntity registTAccgDocInvoice(Long usingAccgDocSeq, Long docInvoiceOrder) throws AppException {
		TAccgDocInvoiceEntity tAccgDocInvoiceEntity = new TAccgDocInvoiceEntity();
		tAccgDocInvoiceEntity.setAccgDocSeq(usingAccgDocSeq);
		tAccgDocInvoiceEntity.setDocInvoiceOrder(docInvoiceOrder);

		int insertCount = 0;

		try {
			// 登録
			insertCount = tAccgDocInvoiceDao.insert(tAccgDocInvoiceEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tAccgDocInvoiceEntity;
	}

	/**
	 * 請求項目-預り金データを登録する
	 * 
	 * @param insertEntity
	 * @return
	 * @throws AppException
	 */
	private TAccgDocInvoiceDepositEntity registTAccgDocInvoiceDeposit(TAccgDocInvoiceDepositEntity insertEntity) throws AppException {

		int insertCount = 0;

		try {
			// 登録
			insertCount = tAccgDocInvoiceDepositDao.insert(insertEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return insertEntity;
	}

	/**
	 * 請求項目-預り金を更新します
	 * 
	 * @param entity
	 * @throws AppException
	 */
	private void updateTAccgDocInvoiceDeposit(TAccgDocInvoiceDepositEntity entity) throws AppException {
		int updateCount = 0;

		try {
			updateCount = tAccgDocInvoiceDepositDao.update(entity);
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
	 * 既入金項目データを登録
	 * 
	 * @param insertEntity
	 * @return
	 * @throws AppException
	 */
	private TAccgDocRepayEntity registTAccgDocRepay(TAccgDocRepayEntity insertEntity) throws AppException {

		int insertCount = 0;

		try {
			// 登録
			insertCount = tAccgDocRepayDao.insert(insertEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return insertEntity;
	}

	/**
	 * 既入金項目を更新します
	 * 
	 * @param entity
	 * @throws AppException
	 */
	private void updateTAccgDocRepay(TAccgDocRepayEntity entity) throws AppException {
		int updateCount = 0;

		try {
			updateCount = tAccgDocRepayDao.update(entity);
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
	 * 既入金項目SEQに紐づく既入金項目データを削除します。
	 * 
	 * @param docRepaySeq
	 * @throws AppException
	 */
	private void deleteTAccgDocRepay(Long docRepaySeq) throws AppException {
		// 既入金項目データ取得
		TAccgDocRepayEntity entity = tAccgDocRepayDao.selectDocRepayByDocRepaySeq(docRepaySeq);
		if (entity == null) {
			return;
		}

		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tAccgDocRepayDao.delete(entity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 請求項目、預り金マッピングデータを登録します。
	 * 
	 * @param docInvoiceDepositSeq
	 * @param depositRecvSeq
	 * @return
	 * @throws AppException
	 */
	private TAccgDocInvoiceDepositTDepositRecvMappingEntity registTAccgDocInvoiceDepositTDepositRecvMapping(Long docInvoiceDepositSeq, Long depositRecvSeq) throws AppException {
		TAccgDocInvoiceDepositTDepositRecvMappingEntity tAccgDocInvoiceDepositTDepositRecvMappingEntity = new TAccgDocInvoiceDepositTDepositRecvMappingEntity();
		tAccgDocInvoiceDepositTDepositRecvMappingEntity.setDocInvoiceDepositSeq(docInvoiceDepositSeq);
		tAccgDocInvoiceDepositTDepositRecvMappingEntity.setDepositRecvSeq(depositRecvSeq);

		int insertCount = 0;

		try {
			// 登録
			insertCount = tAccgDocInvoiceDepositTDepositRecvMappingDao.insert(tAccgDocInvoiceDepositTDepositRecvMappingEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tAccgDocInvoiceDepositTDepositRecvMappingEntity;
	}

	/**
	 * 預り金SEQに紐づく請求項目-預り金（実費）_預り金テーブルマッピングデータを削除します
	 * 
	 * @param depositRecvSeq
	 * @return
	 * @throws AppException
	 */
	private TAccgDocInvoiceDepositTDepositRecvMappingEntity deleteTAccgDocInvoiceDepositTDepositRecvMapping(Long depositRecvSeq) throws AppException {
		// 請求項目-預り金（実費）_預り金テーブルマッピングデータ取得
		TAccgDocInvoiceDepositTDepositRecvMappingEntity entity = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeq(depositRecvSeq);
		if (entity == null) {
			return entity;
		}

		return this.deleteTAccgDocInvoiceDepositTDepositRecvMapping(entity);
	}

	/**
	 * 請求項目(実費)_預り金テーブルマッピングデータを削除する
	 * 
	 * @param deleteEntity
	 * @return
	 * @throws AppException
	 */
	private TAccgDocInvoiceDepositTDepositRecvMappingEntity deleteTAccgDocInvoiceDepositTDepositRecvMapping(TAccgDocInvoiceDepositTDepositRecvMappingEntity deleteEntity) throws AppException {

		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tAccgDocInvoiceDepositTDepositRecvMappingDao.delete(deleteEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}

		return deleteEntity;
	}

	/**
	 * 既入金項目_預り金テーブルマッピングを登録します。
	 * 
	 * @param docRepaySeq
	 * @param depositRecvSeq
	 * @return
	 * @throws AppException
	 */
	private TAccgDocRepayTDepositRecvMappingEntity registTAccgDocRepayTDepositRecvMapping(Long docRepaySeq, Long depositRecvSeq) throws AppException {
		TAccgDocRepayTDepositRecvMappingEntity newAccgDocRepayTDepositRecvMappingEntity = new TAccgDocRepayTDepositRecvMappingEntity();
		newAccgDocRepayTDepositRecvMappingEntity.setDocRepaySeq(docRepaySeq);
		newAccgDocRepayTDepositRecvMappingEntity.setDepositRecvSeq(depositRecvSeq);

		int insertCount = 0;

		try {
			// 登録
			insertCount = tAccgDocRepayTDepositRecvMappingDao.insert(newAccgDocRepayTDepositRecvMappingEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return newAccgDocRepayTDepositRecvMappingEntity;
	}

	/**
	 * 既入金項目_預り金テーブルマッピングデータを削除します。
	 * 
	 * @param depositRecvSeq
	 * @return
	 * @throws AppException
	 */
	private TAccgDocRepayTDepositRecvMappingEntity deletetAccgDocRepayTDepositRecvMapping(Long depositRecvSeq) throws AppException {
		// 既入金項目_預り金テーブルマッピングデータ取得
		TAccgDocRepayTDepositRecvMappingEntity entity = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeq(depositRecvSeq);
		if (entity == null) {
			return entity;
		}

		return this.deletetAccgDocRepayTDepositRecvMapping(entity);
	}

	/**
	 * 既入金項目_預り金テーブルマッピングデータを削除する
	 * 
	 * @param deleteEntity
	 * @return
	 * @throws AppException
	 */
	private TAccgDocRepayTDepositRecvMappingEntity deletetAccgDocRepayTDepositRecvMapping(TAccgDocRepayTDepositRecvMappingEntity deleteEntity) throws AppException {

		if (deleteEntity == null) {
			return deleteEntity;
		}

		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tAccgDocRepayTDepositRecvMappingDao.delete(deleteEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}

		return deleteEntity;
	}

}