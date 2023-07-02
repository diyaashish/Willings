package jp.loioz.app.user.recordDetail.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccgAmountService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.recordDetail.dto.PaymentPlanListItemDto;
import jp.loioz.app.user.recordDetail.dto.RecordDetailInputPatternDto;
import jp.loioz.app.user.recordDetail.dto.RecordInvoiceSummaryDto;
import jp.loioz.app.user.recordDetail.dto.RecordListItemDto;
import jp.loioz.app.user.recordDetail.dto.RecordStatementSummaryDto;
import jp.loioz.app.user.recordDetail.dto.RecordSummaryDto;
import jp.loioz.app.user.recordDetail.form.RecordDetailInputForm;
import jp.loioz.app.user.recordDetail.form.RecordDetailViewForm;
import jp.loioz.bean.AccgDocAndRecordBean;
import jp.loioz.bean.AccgInvoicePaymentPlanBean;
import jp.loioz.bean.AccgRecordDetailBean;
import jp.loioz.bean.AnkenPersonRelationBean;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.ExpenseInvoiceFlg;
import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import jp.loioz.common.constant.CommonConstant.InvoiceType;
import jp.loioz.common.constant.CommonConstant.RecordType;
import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgInvoicePaymentPlanDao;
import jp.loioz.dao.TAccgRecordDao;
import jp.loioz.dao.TAccgRecordDetailDao;
import jp.loioz.dao.TAccgRecordDetailOverPaymentDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TUncollectibleDao;
import jp.loioz.dao.TUncollectibleDetailDao;
import jp.loioz.domain.condition.RecordDetailSearchCondition;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.dto.AccgInvoiceStatementAmountDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgRecordDetailEntity;
import jp.loioz.entity.TAccgRecordDetailOverPaymentEntity;
import jp.loioz.entity.TAccgRecordEntity;
import jp.loioz.entity.TAccgStatementEntity;
import jp.loioz.entity.TDepositRecvEntity;
import jp.loioz.entity.TFeeEntity;
import jp.loioz.entity.TUncollectibleDetailEntity;
import jp.loioz.entity.TUncollectibleEntity;

/**
 * 取引実績詳細覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RecordDetailService extends DefaultService {

	/** 取引実績情報：各実績集計MapのKey */
	private static final String TOTAL_AMOUNT_MAP_KEY = "recordAmount";
	private static final String TOTAL_FEE_AMOUNT_MAP_KEY = "recordFeeAmount";
	private static final String TOTAL_FEE_REPAY_AMOUNT_MAP_KEY = "recordFeeRepayAmount";
	private static final String TOTAL_DEPOSIT_RECV_AMOUNT_MAP_KEY = "recordDepositRecvAmount";
	private static final String TOTAL_DEPOSIT_PAYMENT_AMOUNT_MAP_KEY = "recordDepositPaymentAmount";
	private static final String TOTAL_OVER_PAYMENT_AMOUNT_MAP_KEY = "recordOverPaymentAmount";
	private static final String TOTAL_REFUNDED_OVER_PAYMENT_AMOUNT_MAP_KEY = "recordRefundedOverPaymentAmount";
	private static final String TOTAL_UNREFUNDED_OVER_PAYMENT_AMOUNT_MAP_KEY = "recordUnrefundedOverPaymentAmount";

	/** 会計書類Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** 会計請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 支払計画Daoクラス */
	@Autowired
	private TAccgInvoicePaymentPlanDao tAccgInvoicePaymentPlanDao;

	/** 会計精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

	/** 取引実績Daoクラス */
	@Autowired
	private TAccgRecordDao tAccgRecordDao;

	/** 取引実績明細Daoクラス */
	@Autowired
	private TAccgRecordDetailDao tAccgRecordDetailDao;

	/** 取引実績明細-過入金クラス */
	@Autowired
	private TAccgRecordDetailOverPaymentDao tAccgRecordDetailOverPaymentDao;

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 報酬Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	/** 回収不能金Daoクラス */
	@Autowired
	private TUncollectibleDao tUncollectibleDao;

	/** 回収不能詳細Daoクラス */
	@Autowired
	private TUncollectibleDetailDao tUncollectibleDetailDao;

	/** 共通サービス：分野 */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通サービス：会計管理 */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 会計管理の金額を扱う共通サービス */
	@Autowired
	private CommonAccgAmountService commonAccgAmountService;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 取引実績詳細画面表示用オブジェクトの作成
	 * 
	 * @param accgRecordSeq
	 * @return
	 * @throws DataNotFoundException
	 * @throws AppException
	 */
	public RecordDetailViewForm createRecordDetailViewForm(Long accgRecordSeq) throws DataNotFoundException, AppException {

		// 取引実績画面情報を作成
		var recordDetailViewForm = new RecordDetailViewForm();

		// 取引実績情報を取得・設定
		TAccgRecordEntity tAccgRecordEntity = this.getAccgRecordEntity(accgRecordSeq);
		Long accgDocSeq = tAccgRecordEntity.getAccgDocSeq();
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());

		Long statementSeq = null;
		if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合
			TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntity(accgDocSeq);
			statementSeq = tAccgStatementEntity.getStatementSeq();
		}

		Long invoiceSeq = null;
		TAccgInvoiceEntity tAccgInvoiceEntity = null;
		if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合
			tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			invoiceSeq = tAccgInvoiceEntity.getInvoiceSeq();
		}

		recordDetailViewForm.setAccgRecordSeq(accgRecordSeq);
		recordDetailViewForm.setAccgDocSeq(accgDocSeq);
		recordDetailViewForm.setAccgDocType(accgDocType);
		recordDetailViewForm.setStatementSeq(statementSeq);
		recordDetailViewForm.setInvoiceSeq(invoiceSeq);
		recordDetailViewForm.setPersonId(tAccgDocEntity.getPersonId());
		recordDetailViewForm.setAnkenId(tAccgDocEntity.getAnkenId());

		// =========================== 子フラグメントの設定 =========================== //

		// 取引実績画面 会計書類（請求書・精算書）情報の設定
		RecordDetailViewForm.RecordDetailAccgDocViewForm recordDetailAccgDocViewForm = this.createRecordDetailAccgDocViewForm(accgRecordSeq, tAccgRecordEntity);
		recordDetailViewForm.setRecordDetailAccgDocViewForm(recordDetailAccgDocViewForm);

		// 取引実績画面 一覧情報の設定
		RecordDetailViewForm.RecordListViewForm recordListViewForm = this.createRecordListViewForm(accgRecordSeq, tAccgRecordEntity);
		recordDetailViewForm.setRecordListViewForm(recordListViewForm);

		// 支払計画 一覧情報の設定
		if (tAccgInvoiceEntity != null) {
			RecordDetailViewForm.PaymentPlanListViewForm paymentPlanListViewForm = this.createPaymentPlanListViewForm(tAccgInvoiceEntity);
			recordDetailViewForm.setPaymentPlanListViewForm(paymentPlanListViewForm);
		}

		// 報酬、預り金／実費内訳の設定
		RecordDetailViewForm.BreakdownOfFeeAndDepositViewForm breakdownOfFeeAndDepositViewForm = this.createBreakdownOfFeeAndDepositViewForm(accgRecordSeq);
		recordDetailViewForm.setBreakdownOfFeeAndDepositViewForm(breakdownOfFeeAndDepositViewForm);

		// =========================== 子フラグメントの設定 end =========================== //

		return recordDetailViewForm;
	}

	/**
 	 * 会計書類（請求書・精算書） オブジェクトを作成する<br>
	 * 取引実積取得あり
	 * 
	 * @param accgRecordSeq
	 * @return
	 * @throws DataNotFoundException
	 * @throws AppException
	 */
	public RecordDetailViewForm.RecordDetailAccgDocViewForm createRecordDetailAccgDocViewForm(Long accgRecordSeq) throws DataNotFoundException, AppException {
		// 取引実績情報を取得
		TAccgRecordEntity tAccgRecordEntity = this.getAccgRecordEntity(accgRecordSeq);
		return this.createRecordDetailAccgDocViewForm(accgRecordSeq, tAccgRecordEntity);
	}

	/**
 	 * 会計書類（請求書・精算書） オブジェクトを作成する<br>
	 * 取引実積取得なし
	 * 
	 * @param accgRecordSeq
	 * @param tAccgRecordEntity
	 * @return
	 * @throws AppException
	 */
	private RecordDetailViewForm.RecordDetailAccgDocViewForm createRecordDetailAccgDocViewForm(Long accgRecordSeq, TAccgRecordEntity tAccgRecordEntity) throws AppException {

		var recordDetailAccgDocViewForm = new RecordDetailViewForm.RecordDetailAccgDocViewForm();

		// 会計書類取得
		Long accgDocSeq = tAccgRecordEntity.getAccgDocSeq();
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);

		// 会計書類に紐づく、顧客情報と案件情報を取得
		AnkenPersonRelationBean ankenPersonRelationBean = tAccgDocDao.selectAnkenPersonRelationBeanByAccgDocSeq(accgDocSeq);
		// 分野取得
		BunyaDto bunya = commonBunyaService.getBunya(ankenPersonRelationBean.getBunyaId());

		// 取引実績画面 会計書類（請求書・精算書）オブジェクトに設定
		recordDetailAccgDocViewForm.setAccgDocSeq(accgDocSeq);
		recordDetailAccgDocViewForm.setAccgRecordSeq(accgRecordSeq);
		recordDetailAccgDocViewForm.setPersonId(ankenPersonRelationBean.getPersonId());
		recordDetailAccgDocViewForm.setPersonName(ankenPersonRelationBean.toPersonName().getName());
		recordDetailAccgDocViewForm.setPersonAttribute(
				PersonAttribute.of(
						ankenPersonRelationBean.getCustomerFlg(),
						ankenPersonRelationBean.getAdvisorFlg(),
						ankenPersonRelationBean.getCustomerType()));
		recordDetailAccgDocViewForm.setCustomerType(CustomerType.of(ankenPersonRelationBean.getCustomerType()));
		recordDetailAccgDocViewForm.setAnkenId(ankenPersonRelationBean.getAnkenId());
		recordDetailAccgDocViewForm.setAnkenName(ankenPersonRelationBean.getAnkenName());
		recordDetailAccgDocViewForm.setAnkenType(AnkenType.of(ankenPersonRelationBean.getAnkenType()));
		recordDetailAccgDocViewForm.setAnkenStatus(AnkenStatus.of(ankenPersonRelationBean.getAnkenStatus()));
		recordDetailAccgDocViewForm.setBunyaName(bunya.getBunyaName());
		recordDetailAccgDocViewForm.setAccgDocType(AccgDocType.of(tAccgDocEntity.getAccgDocType()));

		// 会計書類（請求書・精算書）情報を取得
		RecordSummaryDto recordSummaryDto = this.getRecordSummaryDto(accgRecordSeq, accgDocSeq, AccgDocType.of(tAccgDocEntity.getAccgDocType()));
		recordDetailAccgDocViewForm.setRecordSummaryDto(recordSummaryDto);

		return recordDetailAccgDocViewForm;
	}

	/**
	 * 取引実績画面：報酬、預り金／実費内訳表示情報を作成する
	 * 
	 * @param accgRecordSeq
	 * @return
	 * @throws AppException
	 */
	public RecordDetailViewForm.BreakdownOfFeeAndDepositViewForm createBreakdownOfFeeAndDepositViewForm(Long accgRecordSeq) throws AppException {
		RecordDetailViewForm.BreakdownOfFeeAndDepositViewForm breakdownOfFeeAndDepositViewForm = new RecordDetailViewForm.BreakdownOfFeeAndDepositViewForm();
		
		// 取引実績情報を取得・設定
		TAccgRecordEntity tAccgRecordEntity = this.getAccgRecordEntity(accgRecordSeq);
		Long accgDocSeq = tAccgRecordEntity.getAccgDocSeq();
		TAccgDocEntity tAccgDocEntity = this.getAccgDocEntity(accgDocSeq);
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		
		// 取引実績画面：会計書類（請求書・精算書）の金額サマリ情報取得
		RecordSummaryDto recordSummaryDto = this.getRecordSummaryDto(accgRecordSeq, accgDocSeq, accgDocType);
		
		// 報酬入金額セット
		breakdownOfFeeAndDepositViewForm.setRecordFeeAmount(recordSummaryDto.getRecordFeeAmount());
		// 預り金入金額セット
		breakdownOfFeeAndDepositViewForm.setRecordDepositRecvAmount(recordSummaryDto.getRecordDepositRecvAmount());
		// 過入金額セット
		breakdownOfFeeAndDepositViewForm.setOverPaymentAmount(recordSummaryDto.getOverPaymentAmount());
		// 入金額合計セット
		breakdownOfFeeAndDepositViewForm.setRecordTotalAmount(LoiozNumberUtils.parseAsBigDecimal(recordSummaryDto.getTotalPayAmount()));
		
		// 報酬入金額見込セット
		breakdownOfFeeAndDepositViewForm.setFeeAmountExpect(recordSummaryDto.getFeeAmountExpect());
		// 預り金入金額見込セット
		breakdownOfFeeAndDepositViewForm.setDepositRecvAmountExpect(recordSummaryDto.getDepositRecvAmountExpect());
		// 報酬残金セット
		breakdownOfFeeAndDepositViewForm.setFeeBalance(recordSummaryDto.getFeeBalance());
		// 預り金残金セット
		breakdownOfFeeAndDepositViewForm.setDepositRecvBalance(recordSummaryDto.getDepositRecvBalance());
		// 残金合計セット
		breakdownOfFeeAndDepositViewForm.setTotalBalance(LoiozNumberUtils.parseAsBigDecimal(recordSummaryDto.getTotalBalance()));
		
		return breakdownOfFeeAndDepositViewForm;
	}

	/**
	 * 一覧 表示オブジェクトを作成する<br>
	 * 取引実積取得あり
	 *
	 * @param accgRecordSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public RecordDetailViewForm.RecordListViewForm createRecordListViewForm(Long accgRecordSeq) throws DataNotFoundException {
		// 取引実積取得
		TAccgRecordEntity tAccgRecordEntity = this.getAccgRecordEntity(accgRecordSeq);
		return this.createRecordListViewForm(accgRecordSeq, tAccgRecordEntity);
	}

	/**
	 * 一覧 表示オブジェクトを作成する<br>
	 * 取引実積取得なし
	 * 
	 * @param accgRecordSeq
	 * @param tAccgRecordEntity
	 * @return
	 */
	private RecordDetailViewForm.RecordListViewForm createRecordListViewForm(Long accgRecordSeq, TAccgRecordEntity tAccgRecordEntity) {

		// 取引実績一覧画面オブジェクト
		var recordListViewForm = new RecordDetailViewForm.RecordListViewForm();

		// 親データの存在チェック
		Long accgDocSeq = tAccgRecordEntity.getAccgDocSeq();

		// 一覧データの取得・設定
		RecordDetailSearchCondition conditions = RecordDetailSearchCondition.builder().accgRecordSeq(accgRecordSeq).build();
		List<AccgRecordDetailBean> accgRecordDetailBeanList = tAccgRecordDetailDao.selectAccgRecordDetailBeanByRecordDetailSearchConditions(conditions);
		List<RecordListItemDto> recordListItemDtoList = accgRecordDetailBeanList.stream().map(this::toRecordListItemDto).collect(Collectors.toList());

		boolean existsOverPayment = this.existsOverPayment(accgRecordSeq);

		recordListViewForm.setAccgRecordSeq(accgRecordSeq);
		recordListViewForm.setAccgDocSeq(accgDocSeq);
		recordListViewForm.setRecordList(recordListItemDtoList);
		recordListViewForm.setExistsOverPaymentRecord(existsOverPayment);
		recordListViewForm.setExistsRefundedRecord(this.existsOverPaymentRefundedRecord(accgRecordSeq));

		// 会計書類
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (AccgDocType.STATEMENT.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 精算書の場合
			recordListViewForm.setCanAdd(!existsOverPayment && !this.equalsByDepositPaymentExpect(accgRecordSeq));
		} else if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 請求書の場合
			recordListViewForm.setCanAdd(!existsOverPayment && !this.equalsByRecordExpect(accgRecordSeq));
		} else {
			// 想定外のデータパターン
			throw new RuntimeException("想定外のEnum値");
		}
		// 回収不能
		recordListViewForm.setChangableToUncollectible(this.isChangableToUncollectible(accgDocSeq));
		recordListViewForm.setUndoableFromUncollectible(this.isUndoableFromUncollectible(accgDocSeq));

		return recordListViewForm;
	}

	/**
	 * 取引実績画面：支払計画一覧画面表示情報を作成する
	 * 
	 * @param accgRecordSeq
	 * @return 支払計画一覧画面表示情報（画面表示情報を表示しない場合はNull）
	 * @throws DataNotFoundException
	 */
	public RecordDetailViewForm.PaymentPlanListViewForm createPaymentPlanListViewForm(Long accgRecordSeq) throws DataNotFoundException {

		TAccgRecordEntity tAccgRecordEntity = this.getAccgRecordEntity(accgRecordSeq);
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(tAccgRecordEntity.getAccgDocSeq());
		if (tAccgInvoiceEntity == null) {
			// 請求書データが存在しない場合、支払計画は存在しないので、Nullを返却
			return null;
		}

		return this.createPaymentPlanListViewForm(tAccgInvoiceEntity);
	}

	/**
	 * 取引実績画面：支払計画一覧画面表示情報を作成する
	 * 
	 * @param tAccgInvoiceEntity
	 * @return 支払計画一覧画面表示情報（支払計画添付フラグが0の場合はNull）
	 */
	private RecordDetailViewForm.PaymentPlanListViewForm createPaymentPlanListViewForm(TAccgInvoiceEntity tAccgInvoiceEntity) {

		if (!SystemFlg.codeToBoolean(tAccgInvoiceEntity.getPaymentPlanAttachFlg())) {
			// 支払計画添付フラグがOFFの場合は、Nullを返却
			return null;
		}

		Long invoiceSeq = tAccgInvoiceEntity.getInvoiceSeq();

		// 初期データ情報を設定
		List<AccgInvoicePaymentPlanBean> paymentPlanBeanList = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanSalesByInvoiceSeq(invoiceSeq);
		List<PaymentPlanListItemDto> paymentPlanListItemDtoList = paymentPlanBeanList.stream().map(e -> {
			return PaymentPlanListItemDto.builder()
					.invoicePaymentPlanSeq(e.getInvoicePaymentPlanSeq())
					.paymentScheduleAmount(AccountingUtils.toDispAmountLabel(e.getPaymentScheduleAmount()))
					.paymentScheduleDate(DateUtils.parseToString(e.getPaymentScheduleDate(), DateUtils.DATE_JP_YYYY_MM_DD))
					.sumText(e.getSumText())
					.build();
		}).collect(Collectors.toList());

		var viewForm = new RecordDetailViewForm.PaymentPlanListViewForm();
		viewForm.setInvoiceSeq(invoiceSeq);
		viewForm.setAccgDocSeq(tAccgInvoiceEntity.getAccgDocSeq());
		viewForm.setPaymentPlanList(paymentPlanListItemDtoList);

		return viewForm;
	}

	/**
	 * 取引実績画面：新規登録用_詳細入力用オブジェクトを作成する
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @return
	 */
	public RecordDetailInputForm.RecordDetailRowInputForm createRecordDetailRowInputForm(Long accgRecordSeq, Long accgDocSeq) throws AppException {

		var inputForm = new RecordDetailInputForm.RecordDetailRowInputForm();

		// キー情報を設定
		inputForm.setAccgRecordSeq(accgRecordSeq);
		inputForm.setAccgDocSeq(accgDocSeq);

		// 表示用プロパティを設定
		this.setDispProperties(inputForm);

		return inputForm;
	}

	/**
	 * 取引実績画面：編集用_詳細入力用オブジェクトを作成する
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @param accgRecordDetailSeq
	 * @return
	 */
	public RecordDetailInputForm.RecordDetailRowInputForm createRecordDetailRowInputForm(Long accgRecordSeq, Long accgDocSeq, Long accgRecordDetailSeq) throws AppException {

		RecordDetailInputForm.RecordDetailRowInputForm inputForm = createRecordDetailRowInputForm(accgRecordSeq, accgDocSeq);

		// 入力値を設定
		TAccgRecordDetailEntity tAccgRecordDetailEntity = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordDetailSeq(accgRecordDetailSeq);
		if (tAccgRecordDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 対象の取引実績明細が編集可能かをチェック
		boolean isEditable = this.isEditable(tAccgRecordDetailEntity, accgDocSeq);
		if (!isEditable) {
			// 編集不可
			throw new AppException(MessageEnum.MSG_E00191, null, "取引実績で入金された預り金が、<br>他の請求書／精算書で利用されている");
		}

		// 預り金
		BigDecimal recordDepositRecvAmount = tAccgRecordDetailEntity.getRecordDepositRecvAmount();

		// 過入金を取得
		TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordDetailSeq(accgRecordDetailSeq);
		if (tAccgRecordDetailOverPaymentEntity != null) {
			// 過入金あり
			inputForm.setOverPayment(true);
			// 過入金が存在している場合、預り金に計上する
			recordDepositRecvAmount = AccountingUtils.calcTotal(recordDepositRecvAmount, tAccgRecordDetailOverPaymentEntity.getOverPaymentAmount());
		} else {
			// 過入金なし
			inputForm.setOverPayment(false);
		}

		inputForm.setAccgRecordDetailSeq(tAccgRecordDetailEntity.getAccgRecordDetailSeq());
		inputForm.setRecordDate(DateUtils.parseToString(tAccgRecordDetailEntity.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setSeparateInput(SystemFlg.codeToBoolean(tAccgRecordDetailEntity.getRecordSeparateInputFlg()));
		inputForm.setRecordAmount(AccountingUtils.toDispAmountLabel(tAccgRecordDetailEntity.getRecordAmount()));
		inputForm.setRecordFeeAmount(AccountingUtils.toDispAmountLabel(tAccgRecordDetailEntity.getRecordFeeAmount()));
		inputForm.setRecordDepositRecvAmount(AccountingUtils.toDispAmountLabel(recordDepositRecvAmount));
		inputForm.setRecordDepositPaymentAmount(AccountingUtils.toDispAmountLabel(tAccgRecordDetailEntity.getRecordDepositPaymentAmount()));
		inputForm.setRemarks(tAccgRecordDetailEntity.getRemarks());

		return inputForm;
	}

	/**
	 * 対象の取引実績明細データが編集（削除も含む）が可能かどうかを判定する
	 * 
	 * @param tAccgRecordDetailEntity 判定対象の取引実績明細データ
	 * @param accgDocSeq 対象の取引実績明細を持っている会計書類のSEQ
	 * @return true:編集（削除）可能、false:編集（削除）不可
	 */
	private boolean isEditable(TAccgRecordDetailEntity tAccgRecordDetailEntity, Long accgDocSeq) {

		//
		// 取引実績明細の実費／預り金に入金がされており、かつ、
		// この取引実績の請求書／精算書から作成された預り金請求のデータが、他の請求書／精算書で利用されている場合は編集不可とする
		// ※編集を不可としないと、預り金請求のデータを利用している側の請求書／精算書に影響をしてしまうため
		//

		// 実費／預り金への入金額
		BigDecimal recordDepositRecvAmount = tAccgRecordDetailEntity.getRecordDepositRecvAmount();

		if (recordDepositRecvAmount != null && !LoiozNumberUtils.equalsZero(recordDepositRecvAmount)) {
			// 実費／預り金が入金されている場合

			boolean isCreatedDepositInvoiceAndUsingOther = commonAccgService.isCreatedDepositInvoiceAndUsingOther(accgDocSeq);
			if (isCreatedDepositInvoiceAndUsingOther) {
				// 預り金請求のデータが他の請求書／精算書で利用されている場合

				// 編集（削除）不可
				return false;
			}
		}

		// 編集（削除）可能
		return true;
	}

	/**
	 * 取引実績画面：詳細入力用オブジェクトの表示用プロパティを設定する<br>
	 * ※ データ取得のキーとなる情報は格納してあることが想定(accgRecordSeq, accgDocSeq)
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		AccgDocAndRecordBean accgDocAndRecordBean = tAccgRecordDao.selectAccgDocByAccgDocSeq(inputForm.getAccgDocSeq());
		if (accgDocAndRecordBean == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		inputForm.setAccgDocType(AccgDocType.of(accgDocAndRecordBean.getAccgDocType()));
		inputForm.setSeparateInputEnable(this.isSeparateInputEnableCheck(accgDocAndRecordBean.getFeeAmountExpect(),
				accgDocAndRecordBean.getDepositRecvAmountExpect()));
	}

	/**
	 * 登録・更新処理の保存処理時に行う入力チェック
	 * 
	 * @param inputForm
	 * @param result
	 * @throws AppException
	 */
	public void saveRecordDetailValidate(RecordDetailInputForm.RecordDetailRowInputForm inputForm, BindingResult result) throws AppException {

		Long accgDocSeq = inputForm.getAccgDocSeq();
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (AccgDocType.STATEMENT.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 精算の場合
			if (inputForm.getRecordDepositPaymentAmountDecimal() == null) {
				// 預り金出金額未入力の場合
				result.rejectValue("recordDepositPaymentAmount", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(), null, "");
			} else if (LoiozNumberUtils.equalsZero(inputForm.getRecordDepositPaymentAmountDecimal())) {
				// 預り金出金額０円入力の場合
				result.rejectValue("recordDepositPaymentAmount", MessageEnum.MSG_E00050.getMessageKey(), null, "");
			} else {
				// なにもしない
			}

		} else if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 請求の場合

			if (inputForm.isSeparateInput()) {
				if (LoiozNumberUtils.equalsZero(LoiozNumberUtils.nullToZero(inputForm.getRecordFeeAmountDecimal())) && LoiozNumberUtils.equalsZero(LoiozNumberUtils.nullToZero(inputForm.getRecordDepositRecvAmountDecimal()))) {
					result.rejectValue("recordFeeAmount", MessageEnum.MSG_E00050.getMessageKey(), null, "");
				}

			} else {
				if (inputForm.getRecordAmountDecimal() == null) {
					// 実績入金額未入力
					result.rejectValue("recordAmount", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(), null, "");
				}
			}

		} else {
			// 想定外のEnum
			throw new RuntimeException("想定外のEnum値です");
		}

	}

	/**
	 * 入力内容に基づき、過入金が発生するかどうか
	 * 
	 * @param inputForm
	 * @return
	 * @throws AppException
	 */
	public boolean isOccurOverPayment(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		Long accgDocSeq = inputForm.getAccgDocSeq();
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		RecordDetailInputPatternDto recordDetailInputPatternDto = this.toRecordDetailInputPattern(inputForm);
		BigDecimal overPayment = recordDetailInputPatternDto.getOverPayment();

		return (overPayment != null && !LoiozNumberUtils.equalsZero(overPayment));
	}

	/**
	 * 取引実績画面：登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registRecordDetail(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(inputForm.getAccgRecordSeq());
		if (tAccgRecordEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(inputForm.getAccgDocSeq());
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (AccgDocType.STATEMENT.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 精算の場合
			this.registStatementRecordDetail(inputForm);

		} else if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 請求の場合
			this.registInvoiceRecordDetail(inputForm);

		} else {
			// 想定外のEnum
			throw new RuntimeException("想定外のEnum値です");
		}

	}

	/**
	 * 取引実績画面：更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateRecordDetail(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(inputForm.getAccgRecordSeq());
		if (tAccgRecordEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(inputForm.getAccgDocSeq());
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (AccgDocType.STATEMENT.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 精算の場合
			this.updateStatementRecordDetail(inputForm);

		} else if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 請求の場合
			this.updateInvoiceRecordDetail(inputForm);

		} else {
			// 想定外のEnum
			throw new RuntimeException("想定外のEnum値です");
		}

	}

	/**
	 * 取引実績：削除処理
	 * 
	 * @param accgRecordDetailSeq
	 * @throws AppException
	 */
	public void deleteRecordDetail(Long accgRecordSeq, Long accgRecordDetailSeq) throws AppException {

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (tAccgRecordEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(tAccgRecordEntity.getAccgDocSeq());
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (AccgDocType.STATEMENT.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 精算の場合
			this.deleteStatementRecordDetail(tAccgDocEntity.getAccgDocSeq(), accgRecordSeq, accgRecordDetailSeq);

		} else if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 請求の場合
			this.deleteInvoiceRecordDetail(tAccgDocEntity.getAccgDocSeq(), accgRecordSeq, accgRecordDetailSeq);

		} else {
			// 想定外のEnum
			throw new RuntimeException("想定外のEnum値です");
		}

	}

	/**
	 * 取引実績：過入金返金用の登録フォームオブジェクトを作成
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 * @return
	 * @throws AppException
	 */
	public RecordDetailInputForm.RecordOverpayRefundRowInputForm createRecordOverpayRefundRowInputForm(Long accgDocSeq, Long accgRecordSeq) throws AppException {

		var inputForm = new RecordDetailInputForm.RecordOverpayRefundRowInputForm();
		inputForm.setAccgDocSeq(accgDocSeq);
		inputForm.setAccgRecordSeq(accgRecordSeq);

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		return inputForm;
	}

	/**
	 * 取引実績：過入金返金用の更新フォームオブジェクトを作成
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 * @param accgRecordDetailSeq 過入金返金レコードのSEQ ※過入金が発生したレコードのSEQではない
	 * @return
	 * @throws AppException
	 */
	public RecordDetailInputForm.RecordOverpayRefundRowInputForm createRecordOverpayRefundRowInputForm(Long accgDocSeq, Long accgRecordSeq, Long accgRecordDetailSeq) throws AppException {

		// フォームの作成は新規作成と同じ
		RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm = createRecordOverpayRefundRowInputForm(accgDocSeq, accgRecordSeq);

		TAccgRecordDetailEntity tAccgRecordDetailEntity = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordDetailSeq(accgRecordDetailSeq);
		if (tAccgRecordDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		inputForm.setAccgRecordDetailSeq(accgRecordDetailSeq);
		inputForm.setRecordDate(DateUtils.parseToString(tAccgRecordDetailEntity.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setRemarks(tAccgRecordDetailEntity.getRemarks());

		return inputForm;
	}

	/**
	 * 取引実績：過入金返金用入力フォームオブジェクトの表示プロパティを設定
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void setDispProperties(RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm) throws AppException {

		// 取引実績に対して発生している過入金情報を取得する
		TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordSeq(inputForm.getAccgRecordSeq());
		if (tAccgRecordDetailOverPaymentEntity == null) {
			// 過入金データが存在しない場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 返金対応が必要な金額を設定
		inputForm.setOverPaymentRecvAmount(AccountingUtils.toDispAmountLabel(tAccgRecordDetailOverPaymentEntity.getOverPaymentAmount()));
	}

	/**
	 * 取引実績：過入金返金の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registRecordDetailRefund(RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm) throws AppException {

		// 取引実績に紐づく過入金情報を取得する
		TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordSeq(inputForm.getAccgRecordSeq());
		if (tAccgRecordDetailOverPaymentEntity == null || SystemFlg.codeToBoolean(tAccgRecordDetailOverPaymentEntity.getOverPaymentRefundFlg())) {
			// 過入金データが存在しない || すでに過入金返金済み
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TAccgRecordDetailEntity> tAccgRecordDetailEntities = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordSeq(inputForm.getAccgRecordSeq());
		if (tAccgRecordDetailEntities.stream().anyMatch(e -> RecordType.OVER_PAYMENT_REFUND.equalsByCode(e.getRecordType()))) {
			// 過入金返金データがすでに存在する場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 過入金返金情報を作成
		TAccgRecordDetailEntity tAccgRecordDetailEntity = new TAccgRecordDetailEntity();
		tAccgRecordDetailEntity.setAccgRecordSeq(inputForm.getAccgRecordSeq());
		tAccgRecordDetailEntity.setRecordDate(DateUtils.parseToLocalDate(inputForm.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgRecordDetailEntity.setRecordType(RecordType.OVER_PAYMENT_REFUND.getCd());
		tAccgRecordDetailEntity.setRecordSeparateInputFlg(SystemFlg.FLG_OFF.getCd());
		tAccgRecordDetailEntity.setRecordDepositPaymentAmount(tAccgRecordDetailOverPaymentEntity.getOverPaymentAmount());
		tAccgRecordDetailEntity.setRemarks(inputForm.getRemarks());

		// 過入金返金フラグをONにする
		tAccgRecordDetailOverPaymentEntity.setOverPaymentRefundFlg(SystemFlg.FLG_ON.getCd());

		try {
			// 登録処理
			tAccgRecordDetailDao.insert(tAccgRecordDetailEntity);

			// 更新処理
			tAccgRecordDetailOverPaymentDao.update(tAccgRecordDetailOverPaymentEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 請求書ステータスの更新処理(ここで必ず入金済みになる想定)
		commonAccgService.updateInvoiceStatus(inputForm.getAccgDocSeq());
	}

	/**
	 * 取引実績：過入金返金の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateRecordDetailRefund(RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm) throws AppException {

		// 取引実績に紐づく過入金情報を取得する
		TAccgRecordDetailEntity tAccgRecordDetailEntity = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordDetailSeq(inputForm.getAccgRecordDetailSeq());
		if (tAccgRecordDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 変更内容は「決済日」と「備考」のみ
		// 金額は変更しないため、請求書のステータス更新も行わない
		tAccgRecordDetailEntity.setRecordDate(DateUtils.parseToLocalDate(inputForm.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgRecordDetailEntity.setRemarks(inputForm.getRemarks());

		try {
			// 更新処理
			tAccgRecordDetailDao.update(tAccgRecordDetailEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 取引実績：過入金返金の取り下げ
	 * 
	 * @param accgDoc
	 * @param accgRecordSeq
	 * @param accgRecordDetailSeq
	 * @throws AppException
	 */
	public void dropRefund(Long accgDoc, Long accgRecordSeq) throws AppException {

		// DBからデータを取得
		TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordSeq(accgRecordSeq);
		List<TAccgRecordDetailEntity> tAccgRecordDetailEntities = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordSeq(accgRecordSeq);

		if (tAccgRecordDetailOverPaymentEntity == null || !SystemFlg.codeToBoolean(tAccgRecordDetailOverPaymentEntity.getOverPaymentRefundFlg())) {
			// 過入金情報が存在しない || 過入金が未処理の場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TAccgRecordDetailEntity> refundRecordEntities = tAccgRecordDetailEntities.stream().filter(e -> RecordType.OVER_PAYMENT_REFUND.equalsByCode(e.getRecordType())).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(refundRecordEntities)) {
			// 過入金返金レコードが存在しない
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (refundRecordEntities.size() > 1) {
			// 過入金返金情報は1件のみの想定
			throw new RuntimeException("過入金返金情報が複数件存在しています。");
		}

		// 過入金返金フラグをOFFにする
		tAccgRecordDetailOverPaymentEntity.setOverPaymentRefundFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// 過入金返金情報の削除処理
			refundRecordEntities.stream().findFirst().ifPresent(tAccgRecordDetailDao::delete);

			// 過入金情報を更新
			tAccgRecordDetailOverPaymentDao.update(tAccgRecordDetailOverPaymentEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 請求書ステータスの更新処理(ここで必ず過入金になる想定)
		commonAccgService.updateInvoiceStatus(accgDoc);
	}

	/**
	 * 取引実績：回収不能にする
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 */
	public void changeToUncollectible(Long accgDocSeq, Long accgRecordSeq) throws AppException {

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (!this.isChangableToUncollectible(accgDocSeq) || tAccgDocEntity == null || tAccgRecordEntity == null) {
			// 回収不能への変更ができない場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		BigDecimal recordAmountExpect = AccountingUtils.calcTotal(Arrays.asList(tAccgRecordEntity.getFeeAmountExpect(), tAccgRecordEntity.getDepositRecvAmountExpect()));
		Map<String, BigDecimal> recordDetailTotalAmountMap = this.getRecordDetailTotalAmountMapByAccgRecordSeq(accgRecordSeq);

		// 精算書に対する回収不能金額を取得する
		// ここでは入金待ちか一部入金の場合のみの想定のため、実績の集計に過入金の場合や過入金返金データは考慮しない
		BigDecimal uncollectibleAmount = recordAmountExpect.subtract(recordDetailTotalAmountMap.get(TOTAL_AMOUNT_MAP_KEY));
		if (LoiozNumberUtils.equalsZero(uncollectibleAmount) || LoiozNumberUtils.isLessThan(uncollectibleAmount, BigDecimal.ZERO)) {
			// 入金待ち、一部入金だが、回収不能金額が０円以下の場合は、DBデータの整合性があっていない
			throw new RuntimeException("回収不能金額が０円以下です");
		}

		// 名簿・案件に対する回収不能金額情報取得する
		TUncollectibleEntity tUncollectibleEntity = tUncollectibleDao.selectUncollectibleByPersonIdAndAnkenId(tAccgDocEntity.getPersonId(), tAccgDocEntity.getAnkenId());
		try {

			if (tUncollectibleEntity == null) {
				// 名簿案件単位で回収不能が発生していない場合は、レコード無いので作成する
				tUncollectibleEntity = new TUncollectibleEntity();
				tUncollectibleEntity.setPersonId(tAccgDocEntity.getPersonId());
				tUncollectibleEntity.setAnkenId(tAccgDocEntity.getAnkenId());
				tUncollectibleEntity.setTotalUncollectibleAmount(uncollectibleAmount);

				tUncollectibleDao.insert(tUncollectibleEntity);
			} else {
				// 別の請求書で回収不能が発生している場合、回収不能金額の集計金額を再計算が必要
				// この再計算は共通処理を利用し、本メソッドの最後に実行するため、ここではなにもしない
			}

			// 回収不能詳細データの作成
			var tUncollectibleDetailEntity = new TUncollectibleDetailEntity();
			tUncollectibleDetailEntity.setUncollectibleSeq(tUncollectibleEntity.getUncollectibleSeq());
			tUncollectibleDetailEntity.setUncollectibleAmount(uncollectibleAmount);
			tUncollectibleDetailDao.insert(tUncollectibleDetailEntity);

			// 回収不能が情報を請求書に設定(回収不能解除可能チェックでデータ取得のnullケースを考慮済)
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			tAccgInvoiceEntity.setUncollectibleDetailSeq(tUncollectibleDetailEntity.getUncollectibleDetailSeq());
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.UNCOLLECTIBLE.getCd());
			tAccgInvoiceDao.update(tAccgInvoiceEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 報酬情報の回収不能フラグを更新する
		this.updateFeeUncollectibleFlg(accgDocSeq, accgRecordSeq);

		// 預り金情報の回収不能フラグを更新する
		this.updateDepositRecvUncollectibleFlg(accgDocSeq, accgRecordSeq);

		// 名簿案件単位で回収不能金額の再計算
		this.reCalcUnCollectibleUpdate(tUncollectibleEntity.getUncollectibleSeq());

	}

	/**
	 * 取引実績：回収不能を解除する
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 * @throws AppException
	 */
	public void undoFromUncollectible(Long accgDocSeq, Long accgRecordSeq) throws AppException {

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (!this.isUndoableFromUncollectible(accgDocSeq) || tAccgDocEntity == null || tAccgRecordEntity == null) {
			// 回収不能の解除ができない場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 請求書データの取得(回収不能解除可能チェックでnullケースを考慮済)
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		// 回収不能詳細情報を取得
		TUncollectibleDetailEntity tUncollectibleDetailEntity = tUncollectibleDetailDao.selectUncollectibleDetailByUncollectibleDetailSeq(tAccgInvoiceEntity.getUncollectibleDetailSeq());
		if (tUncollectibleDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 集計元の回収不能SEQを保持
		Long uncollectibleSeq = tUncollectibleDetailEntity.getUncollectibleSeq();

		try {
			// 回収不能詳細情報の削除
			tUncollectibleDetailDao.delete(tUncollectibleDetailEntity);

			// 請求書データの回収不能詳細SEQをnullに設定
			// 請求書ステータスは共通処理を利用して行うため、ここではステータスの更新を行わない
			tAccgInvoiceEntity.setUncollectibleDetailSeq(null);
			tAccgInvoiceDao.update(tAccgInvoiceEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 請求書ステータスの更新を行う ※ステータスを更新後に回収不能状態を更新する
		commonAccgService.updateInvoiceStatus(accgDocSeq);

		// 報酬情報の回収不能フラグを更新する
		this.updateFeeUncollectibleFlg(accgDocSeq, accgRecordSeq);

		// 預り金情報の回収不能フラグを更新する
		this.updateDepositRecvUncollectibleFlg(accgDocSeq, accgRecordSeq);

		// 回収不能金の再計算処理を行う
		this.reCalcUnCollectibleUpdate(uncollectibleSeq);

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 請求書：取引実績詳細の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	private void registInvoiceRecordDetail(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		Long accgDocSeq = inputForm.getAccgDocSeq();

		// 取引実績情報を取得
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		if (this.existsOverPayment(tAccgRecordEntity.getAccgRecordSeq()) || this.equalsByRecordExpect(tAccgRecordEntity.getAccgRecordSeq())) {
			// 過入金が存在する場合 || 実績見込金額と同額を実績登録済は、エラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力パターンによる金額情報Dtoを取得
		RecordDetailInputPatternDto recordDetailInputPattern = this.toRecordDetailInputPattern(inputForm);

		// 金額データ
		BigDecimal recordAmount = recordDetailInputPattern.getRecordAmount();
		BigDecimal recordFeeAmount = recordDetailInputPattern.getRecordFeeAmount();
		BigDecimal recordDepositRecvAmount = recordDetailInputPattern.getRecordDepositRecvAmount();
		BigDecimal overPayment = recordDetailInputPattern.getOverPayment();

		// 決済日
		LocalDate recordDate = DateUtils.parseToLocalDate(inputForm.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);

		// 取引実績詳細情報の作成
		TAccgRecordDetailEntity tAccgRecordDetailEntity = new TAccgRecordDetailEntity();
		tAccgRecordDetailEntity.setAccgRecordSeq(inputForm.getAccgRecordSeq());
		tAccgRecordDetailEntity.setRecordDate(recordDate);
		tAccgRecordDetailEntity.setRecordType(RecordType.INTO.getCd()); // 請求書で登録する場合は、「入金」のみ
		tAccgRecordDetailEntity.setRecordSeparateInputFlg(SystemFlg.booleanToCode(inputForm.isSeparateInput()));
		tAccgRecordDetailEntity.setRecordAmount(recordAmount);
		tAccgRecordDetailEntity.setRecordFeeAmount(recordFeeAmount);
		tAccgRecordDetailEntity.setRecordDepositRecvAmount(recordDepositRecvAmount);
		tAccgRecordDetailEntity.setRemarks(inputForm.getRemarks());

		try {
			// 取引実績の登録処理
			tAccgRecordDetailDao.insert(tAccgRecordDetailEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (overPayment != null) {
			// 過入金が存在する場合は、過入金の登録を行う

			Long accgRecordDetailSeq = tAccgRecordDetailEntity.getAccgRecordDetailSeq();
			TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = new TAccgRecordDetailOverPaymentEntity();
			tAccgRecordDetailOverPaymentEntity.setAccgRecordDetailSeq(accgRecordDetailSeq);
			tAccgRecordDetailOverPaymentEntity.setOverPaymentAmount(overPayment);
			tAccgRecordDetailOverPaymentEntity.setOverPaymentRefundFlg(SystemFlg.FLG_OFF.getCd());

			try {
				// 過入金の登録処理
				tAccgRecordDetailOverPaymentDao.insert(tAccgRecordDetailOverPaymentEntity);

			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}

		}

		if (recordFeeAmount != null) {
			// 報酬金額に変動がある場合

			TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(inputForm.getAccgDocSeq());

			// 報酬ステータスの更新処理
			commonAccgService.updateFeeStatusByAccgDoc(tAccgDocEntity.getAccgDocSeq());

			// 売上合計の再計算処理
			commonAccgService.recalcSalesAmountResultUpdate(tAccgDocEntity.getPersonId(), tAccgDocEntity.getAnkenId());
		}

		if (recordDepositRecvAmount != null && !LoiozNumberUtils.equalsZero(recordDepositRecvAmount)) {
			// 実費／預り金への入金が登録された場合

			// 実費／預り金の入金済みデータを作成する（入金予定データの金額更新も行う）
			this.createDepositRecvCompleteReplicate(accgDocSeq, recordDepositRecvAmount, recordDate);
		}

		// 回収不能金額の更新処理を行う
		this.updateUnCollectible(inputForm.getAccgDocSeq());

		// 請求書の更新処理 ※処理を行った結果からステータスを判別し更新する
		commonAccgService.updateInvoiceStatus(inputForm.getAccgDocSeq());

	}

	/**
	 * 精算書：取引実績詳細の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	private void registStatementRecordDetail(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		try {
			// 精算書情報の存在チェック
			this.getAccgStatementEntity(inputForm.getAccgDocSeq());
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 入力パターンによる金額情報Dtoを取得
		RecordDetailInputPatternDto recordDetailInputPattern = this.toRecordDetailInputPattern(inputForm);
		BigDecimal recordDepositPaymentAmount = recordDetailInputPattern.getRecordDepositPaymentAmount();

		// 取引実績詳細情報の作成
		TAccgRecordDetailEntity tAccgRecordDetailEntity = new TAccgRecordDetailEntity();
		tAccgRecordDetailEntity.setAccgRecordSeq(inputForm.getAccgRecordSeq());
		tAccgRecordDetailEntity.setRecordDate(DateUtils.parseToLocalDate(inputForm.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgRecordDetailEntity.setRecordType(RecordType.WITHDRAW.getCd()); // 精算書で登録する場合は、「出金」のみ
		tAccgRecordDetailEntity.setRecordSeparateInputFlg(SystemFlg.FLG_OFF.getCd()); // 出金に入力項目の分割はない
		tAccgRecordDetailEntity.setRecordDepositPaymentAmount(recordDepositPaymentAmount);
		tAccgRecordDetailEntity.setRemarks(inputForm.getRemarks());

		try {
			// 取引実績の登録処理
			tAccgRecordDetailDao.insert(tAccgRecordDetailEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 預り金予定データの更新を行う 本メソッドは登録処理なので、日付は入力値, 完了としての更新
		this.updateScheduleDepositByStatementCase(inputForm.getAccgDocSeq(), tAccgRecordDetailEntity.getRecordDate(), true);

		// 精算書ステータスの更新
		commonAccgService.updateStatementStatus(inputForm.getAccgDocSeq());

	}

	/**
	 * 取引実績：請求書更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	private void updateInvoiceRecordDetail(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		Long accgDocSeq = inputForm.getAccgDocSeq();

		// 取引実績情報を取得
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		List<Long> updatableRecordDetailSeqList = this.getUpdatableAmountRecordDetailSeq(inputForm.getAccgRecordSeq());
		if (!updatableRecordDetailSeqList.contains(inputForm.getAccgRecordDetailSeq())) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力パターンによる金額情報Dtoを取得
		RecordDetailInputPatternDto recordDetailInputPattern = this.toRecordDetailInputPattern(inputForm);

		// 金額データ
		BigDecimal recordAmount = recordDetailInputPattern.getRecordAmount();
		BigDecimal recordFeeAmount = recordDetailInputPattern.getRecordFeeAmount();
		BigDecimal recordDepositRecvAmount = recordDetailInputPattern.getRecordDepositRecvAmount();
		BigDecimal overPayment = recordDetailInputPattern.getOverPayment();

		// 過入金があるか判定
		if (overPayment != null && !LoiozNumberUtils.equalsZero(overPayment)) {
			// 報酬入力
			BigDecimal feeAmountExpect = tAccgRecordEntity.getFeeAmountExpect();
			if (recordFeeAmount != null && LoiozNumberUtils.isGreaterThan(recordFeeAmount, feeAmountExpect)) {
				// 登録するデータは上限の金額
				recordFeeAmount = feeAmountExpect;
			}
			// 預り金入力
			BigDecimal depositRecvAmountExpect = tAccgRecordEntity.getDepositRecvAmountExpect();
			if (recordDepositRecvAmount != null && LoiozNumberUtils.isGreaterThan(recordDepositRecvAmount, depositRecvAmountExpect)) {
				// 登録するデータは上限の金額
				recordDepositRecvAmount = depositRecvAmountExpect;
			}
		}

		// 取引実績詳細情報の作成
		TAccgRecordDetailEntity tAccgRecordDetailEntity = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordDetailSeq(inputForm.getAccgRecordDetailSeq());
		if (tAccgRecordDetailEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 対象の取引実績明細が編集可能かをチェック
		boolean isEditable = this.isEditable(tAccgRecordDetailEntity, accgDocSeq);
		if (!isEditable) {
			// 編集フォームの表示時にもチェックを行っているので、ここでは楽観ロックエラーのメッセージとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新前の実費／預り金への入金額
		BigDecimal beforeRecordDepositRecvAmount = tAccgRecordDetailEntity.getRecordDepositRecvAmount();

		// 決済日
		LocalDate recordDate = DateUtils.parseToLocalDate(inputForm.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);

		tAccgRecordDetailEntity.setAccgRecordSeq(inputForm.getAccgRecordSeq());
		tAccgRecordDetailEntity.setRecordDate(recordDate);
		tAccgRecordDetailEntity.setRecordType(RecordType.INTO.getCd()); // 請求書で登録する場合は、「入金」のみ
		tAccgRecordDetailEntity.setRecordSeparateInputFlg(SystemFlg.booleanToCode(inputForm.isSeparateInput()));
		tAccgRecordDetailEntity.setRecordAmount(recordAmount);
		tAccgRecordDetailEntity.setRecordFeeAmount(recordFeeAmount);
		tAccgRecordDetailEntity.setRecordDepositRecvAmount(recordDepositRecvAmount);
		tAccgRecordDetailEntity.setRemarks(inputForm.getRemarks());

		try {
			// 取引実績レコードの更新処理
			tAccgRecordDetailDao.update(tAccgRecordDetailEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 過入金情報の登録処理
		TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordDetailSeq(tAccgRecordDetailEntity.getAccgRecordDetailSeq());
		try {
			if (tAccgRecordDetailOverPaymentEntity == null) {
				// 既存過入金が存在しない
				if (overPayment != null && !LoiozNumberUtils.equalsZero(overPayment)) {
					// 過入金が発生した場合
					tAccgRecordDetailOverPaymentEntity = new TAccgRecordDetailOverPaymentEntity();
					tAccgRecordDetailOverPaymentEntity.setAccgRecordDetailSeq(tAccgRecordDetailEntity.getAccgRecordDetailSeq());
					tAccgRecordDetailOverPaymentEntity.setOverPaymentAmount(overPayment);
					tAccgRecordDetailOverPaymentEntity.setOverPaymentRefundFlg(SystemFlg.FLG_OFF.getCd());

					// 登録処理
					tAccgRecordDetailOverPaymentDao.insert(tAccgRecordDetailOverPaymentEntity);
				}
			} else {
				// 既存過入金がすでに存在する
				if (overPayment != null && !LoiozNumberUtils.equalsZero(overPayment)) {
					// 更新後も過入金が発生する場合
					tAccgRecordDetailOverPaymentEntity.setOverPaymentAmount(overPayment);

					// 更新処理
					tAccgRecordDetailOverPaymentDao.update(tAccgRecordDetailOverPaymentEntity);
				} else {
					// 更新後は過入金が発生しない場合

					// 削除処理
					tAccgRecordDetailOverPaymentDao.delete(tAccgRecordDetailOverPaymentEntity);
				}

			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (recordFeeAmount != null) {
			// 報酬金額に変動がある場合

			TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);

			// 報酬ステータスの更新処理
			commonAccgService.updateFeeStatusByAccgDoc(tAccgDocEntity.getAccgDocSeq());

			// 売上合計の再計算処理
			commonAccgService.recalcSalesAmountResultUpdate(tAccgDocEntity.getPersonId(), tAccgDocEntity.getAnkenId());
		}

		//
		// 取引実績の変更による、預り金情報（預り金の予定、実績情報）の更新
		//

		// 更新前の実費／預り金への入金がなかったか（ない場合はtrue）
		boolean isNoAmountBeforeDepositRecv = (beforeRecordDepositRecvAmount == null || LoiozNumberUtils.equalsZero(beforeRecordDepositRecvAmount));
		// 更新後の実費／預り金への入金がないか（ない場合はtrue）
		boolean isNoAmountAfterDepositRecv = (recordDepositRecvAmount == null || LoiozNumberUtils.equalsZero(recordDepositRecvAmount));

		if (!isNoAmountBeforeDepositRecv && !isNoAmountAfterDepositRecv) {
			// 更新前の入金金額があり、更新後の入金金額もある場合（実費／預り金金額が変更された場合（金額の数値自体の変更はされていない場合もある））

			// 【預り金の予定／実績データの更新】（現在の取引実績明細の状態をもとに、預り金の予定／実績データを全て再作成する）
			this.updateDepositRecvCompleteReplicate(accgDocSeq);

		} else if (!isNoAmountBeforeDepositRecv && isNoAmountAfterDepositRecv) {
			// 更新前の入金金額があり、更新後の入金金額がない場合（実費／預り金金額が変更された（金額が0円に変更されたか消された）場合）

			// 【預り金の予定／実績データの更新】（現在の取引実績明細の状態をもとに、預り金の予定／実績データを全て再作成する）
			this.updateDepositRecvCompleteReplicate(accgDocSeq);

		} else if (isNoAmountBeforeDepositRecv && !isNoAmountAfterDepositRecv) {
			// 更新前の入金金額はなく、更新後の入金金額がある場合

			// 【預り金の予定／実績データの更新】（入金金額をもとに、入金済みデータを作成する（入金予定データの金額更新も行う）。※再作成は行わない）
			this.createDepositRecvCompleteReplicate(accgDocSeq, recordDepositRecvAmount, recordDate);

		} else {
			// 更新前の入金金額はなく、更新後の入金金額もない場合（実費／預り金への入金がされないままの場合（報酬への入金のみの場合））
			// 預り金側のデータに影響はしないため何もしない
		}

		// 回収不能金額の更新処理を行う
		this.updateUnCollectible(inputForm.getAccgDocSeq());

		// 請求書の更新処理 ※処理を行った結果からステータスを判別し更新する
		commonAccgService.updateInvoiceStatus(inputForm.getAccgDocSeq());

	}

	/**
	 * 取引実績：精算書更新処理
	 * 
	 * @throws AppException
	 */
	private void updateStatementRecordDetail(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		// 取引実績情報を取得
		TAccgRecordDetailEntity tAccgRecordDetailEntity = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordDetailSeq(inputForm.getAccgRecordDetailSeq());
		if (tAccgRecordDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<Long> updatableRecordDetailSeqList = this.getUpdatableAmountRecordDetailSeq(inputForm.getAccgRecordSeq());
		if (!updatableRecordDetailSeqList.contains(inputForm.getAccgRecordDetailSeq())) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力パターンによる金額情報Dtoを取得
		RecordDetailInputPatternDto recordDetailInputPattern = this.toRecordDetailInputPattern(inputForm);
		BigDecimal recordDepositPaymentAmount = recordDetailInputPattern.getRecordDepositPaymentAmount();

		// 取引実績詳細情報の作成
		tAccgRecordDetailEntity.setRecordDate(DateUtils.parseToLocalDate(inputForm.getRecordDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgRecordDetailEntity.setRecordDepositPaymentAmount(recordDepositPaymentAmount);
		tAccgRecordDetailEntity.setRemarks(inputForm.getRemarks());

		try {
			// 取引実績の登録処理
			tAccgRecordDetailDao.update(tAccgRecordDetailEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 預り金予定データの更新を行う 本メソッドは更新処理なので、日付は入力値, 完了としての更新
		this.updateScheduleDepositByStatementCase(inputForm.getAccgDocSeq(), tAccgRecordDetailEntity.getRecordDate(), true);

		// 精算書ステータスの更新
		commonAccgService.updateStatementStatus(inputForm.getAccgDocSeq());

	}

	/**
	 * 請求書：取引実績詳細情報の削除処理
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 * @param accgRecordDetailSeq
	 * @throws AppException
	 */
	private void deleteInvoiceRecordDetail(Long accgDocSeq, Long accgRecordSeq, Long accgRecordDetailSeq) throws AppException {

		// 取引実績情報を取得
		TAccgRecordDetailEntity tAccgRecordDetailEntity = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordDetailSeq(accgRecordDetailSeq);
		if (tAccgRecordDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 当メソッドでは過入金返金レコードの削除処理は行わないため、更新可能SEQが存在しない場合はない
		List<Long> updatableRecordDetailSeqList = this.getUpdatableAmountRecordDetailSeq(accgRecordSeq);
		if (!updatableRecordDetailSeqList.contains(accgRecordDetailSeq)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 対象の取引実績明細が編集（削除）可能かをチェック
		boolean isEditable = this.isEditable(tAccgRecordDetailEntity, accgDocSeq);
		if (!isEditable) {
			// 編集（削除）不可
			throw new AppException(MessageEnum.MSG_E00086, null, "取引実績で入金された預り金が、<br>他の請求書／精算書で利用されている");
		}

		// 報酬金額を保持しておく
		BigDecimal recordFeeAmount = tAccgRecordDetailEntity.getRecordFeeAmount();
		TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordDetailSeq(tAccgRecordDetailEntity.getAccgRecordDetailSeq());

		// 実費／預り金の金額を保持しておく
		BigDecimal recordDepositRecvAmount = tAccgRecordDetailEntity.getRecordDepositRecvAmount();

		try {
			// 取引実績詳細の削除
			tAccgRecordDetailDao.delete(tAccgRecordDetailEntity);

			// 過入金データが紐づく場合は、過入金レコードを削除
			Optional.ofNullable(tAccgRecordDetailOverPaymentEntity).ifPresent(tAccgRecordDetailOverPaymentDao::delete);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (recordFeeAmount != null && !LoiozNumberUtils.equalsZero(recordFeeAmount)) {
			// 報酬金額に変動がある場合

			// 報酬ステータスの更新処理
			commonAccgService.updateFeeStatusByAccgDoc(accgDocSeq);

			// 売上合計の再計算処理
			TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
			commonAccgService.recalcSalesAmountResultUpdate(tAccgDocEntity.getPersonId(), tAccgDocEntity.getAnkenId());

		}

		if (recordDepositRecvAmount != null && !LoiozNumberUtils.equalsZero(recordDepositRecvAmount)) {
			// 預り金金額に変動がある場合

			// 預り金情報への実績情報を転記する
			this.updateDepositRecvCompleteReplicate(accgDocSeq);
		}

		// 回収不能金額の更新処理を行う
		this.updateUnCollectible(accgDocSeq);

		// 請求書の更新処理 ※処理を行った結果からステータスを判別し更新する
		commonAccgService.updateInvoiceStatus(accgDocSeq);

	}

	/**
	 * 精算書：取引実績詳細情報の削除処理
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 * @param accgRecordDetailSeq
	 * @throws AppException
	 */
	private void deleteStatementRecordDetail(Long accgDocSeq, Long accgRecordSeq, Long accgRecordDetailSeq) throws AppException {

		// 取引実績情報を取得
		TAccgRecordDetailEntity tAccgRecordDetailEntity = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordDetailSeq(accgRecordDetailSeq);
		if (tAccgRecordDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 当メソッドでは過入金返金レコードの削除処理は行わないため、更新可能SEQが存在しない場合はない
		List<Long> updatableRecordDetailSeqList = this.getUpdatableAmountRecordDetailSeq(accgRecordSeq);
		if (!updatableRecordDetailSeqList.contains(accgRecordDetailSeq)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 取引実績の登録処理
			tAccgRecordDetailDao.delete(tAccgRecordDetailEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 預り金予定データの更新を行う 本メソッドは削除処理なので、日付はNull, 未完了への更新
		this.updateScheduleDepositByStatementCase(accgDocSeq, null, false);

		// 精算書ステータスの更新
		commonAccgService.updateStatementStatus(accgDocSeq);

	}

	/**
	 * 精算書：取引実績の登録・更新・削除による預り金予定データの更新を行う
	 * 
	 * @param accgDocSeq 会計書類SEQ
	 * @param depositDate 預り金日時
	 * @param isCompleted 予定か実績か
	 * @throws AppException
	 */
	private void updateScheduleDepositByStatementCase(Long accgDocSeq, @Nullable LocalDate depositDate, boolean isCompleted) throws AppException {

		// 精算書の場合は、預り金予定データは1件のみ必ず存在する想定
		List<TDepositRecvEntity> tDepositRecvEntities = tDepositRecvDao.selectScheduleDepositRecvEntityByCreatedAccgDocSeq(accgDocSeq);
		if (tDepositRecvEntities.size() != 1) {
			// データは1件のみ存在している想定
			throw new RuntimeException("措定外のデータが存在します");
		}

		// 予定データに実績情報を登録する
		TDepositRecvEntity tDepositRecvEntity = tDepositRecvEntities.stream().findFirst().orElseThrow();
		tDepositRecvEntity.setDepositDate(depositDate);
		tDepositRecvEntity.setDepositCompleteFlg(SystemFlg.booleanToCode(isCompleted));

		try {
			// 更新処理
			tDepositRecvDao.update(tDepositRecvEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 会計書類情報の取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 預り金の更新を行うため、実費明細PDFの再作成フラグを更新する
		commonAccgService.updateDepositDetailRebuildFlg(tAccgDocEntity.getAnkenId(), tAccgDocEntity.getPersonId());

	}

	/**
	 * 取引実績情報
	 * 
	 * @param accgRecordSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	private TAccgRecordEntity getAccgRecordEntity(Long accgRecordSeq) throws DataNotFoundException {

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (tAccgRecordEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new DataNotFoundException();
		}

		return tAccgRecordEntity;
	}

	/**
	 * 会計書類情報取得
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	private TAccgDocEntity getAccgDocEntity(Long accgDocSeq) throws AppException {
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		return tAccgDocEntity;
	}

	/**
	 * 会計書類SEQに紐づく精算書データを取得する
	 * 
	 * @param accgDocSeq 会計書類SEQ
	 * @return
	 * @throws DataNotFoundException データの取得に失敗したケース
	 */
	private TAccgStatementEntity getAccgStatementEntity(Long accgDocSeq) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity == null) {
			// データ取得ができない場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new DataNotFoundException("accgDocSeq：「" + accgDocSeq + "」に紐づく精算書情報が見つかりません");
		}

		return tAccgStatementEntity;
	}

	/**
	 * 取引実績画面：会計書類（請求書・精算書）の金額サマリ情報を取得する
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @param accgDocType
	 * @return
	 * @throws AppException
	 */
	private RecordSummaryDto getRecordSummaryDto(Long accgRecordSeq, Long accgDocSeq, AccgDocType accgDocType) throws AppException {

		var recordSummaryDto = new RecordSummaryDto();

		// 共通項目の設定
		recordSummaryDto.setAccgRecordSeq(accgRecordSeq);
		recordSummaryDto.setAccgDocSeq(accgDocSeq);
		recordSummaryDto.setAccgDocType(accgDocType);

		// 取引実績入金合計を取得
		Map<String, BigDecimal> recordDetailTotalAmountMap = this.getRecordDetailTotalAmountMapByAccgRecordSeq(accgRecordSeq);

		// 取引実績を取得
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (tAccgRecordEntity == null) {
			// 取引実績が作られていない場合は楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 金額データの取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// 報酬入金額見込
		BigDecimal feeAmountExpect = tAccgRecordEntity.getFeeAmountExpect() == null ? BigDecimal.ZERO : tAccgRecordEntity.getFeeAmountExpect();
		recordSummaryDto.setFeeAmountExpect(feeAmountExpect);
		// 預り金入金額見込
		BigDecimal depositRecvAmountExpect = tAccgRecordEntity.getDepositRecvAmountExpect() == null ? BigDecimal.ZERO : tAccgRecordEntity.getDepositRecvAmountExpect();
		recordSummaryDto.setDepositRecvAmountExpect(depositRecvAmountExpect);
		// 過入金額
		BigDecimal totalUnrefundAmount = recordDetailTotalAmountMap.get(TOTAL_UNREFUNDED_OVER_PAYMENT_AMOUNT_MAP_KEY);
		recordSummaryDto.setOverPaymentAmount(totalUnrefundAmount);
		// 報酬入金額
		BigDecimal totalFeeAmount = recordDetailTotalAmountMap.get(TOTAL_FEE_AMOUNT_MAP_KEY);
		recordSummaryDto.setRecordFeeAmount(totalFeeAmount);
		// 預り金入金額
		BigDecimal totalDepositRecvAmount = recordDetailTotalAmountMap.get(TOTAL_DEPOSIT_RECV_AMOUNT_MAP_KEY);
		recordSummaryDto.setRecordDepositRecvAmount(totalDepositRecvAmount);
		// 報酬残金
		BigDecimal feeBalance = feeAmountExpect.subtract(totalFeeAmount);
		recordSummaryDto.setFeeBalance(feeBalance);
		// 預り金残金
		BigDecimal depositRecvBalance = depositRecvAmountExpect.subtract(totalDepositRecvAmount);
		recordSummaryDto.setDepositRecvBalance(depositRecvBalance);

		if (accgDocType == AccgDocType.STATEMENT) {
			// 精算の場合

			// 精算書情報の取得
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);

			RecordStatementSummaryDto statementSummary = new RecordStatementSummaryDto();
			statementSummary.setStatementSeq(tAccgStatementEntity.getStatementSeq());
			statementSummary.setStatementRefundStatus(StatementRefundStatus.of(tAccgStatementEntity.getStatementRefundStatus()));
			statementSummary.setStatementAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getStatementAmount())));
			statementSummary.setStatementNo(tAccgStatementEntity.getStatementNo());
			statementSummary.setStatementDate(tAccgStatementEntity.getStatementDate());
			statementSummary.setRefundDate(tAccgStatementEntity.getRefundDate());

			// 預り金見込み(出金)
			BigDecimal depositPaymentAmountExpect = LoiozNumberUtils.nullToZero(tAccgRecordEntity.getDepositPaymentAmountExpect());

			// 出金計
			BigDecimal totalDepositPaymentAmount = recordDetailTotalAmountMap.get(TOTAL_DEPOSIT_PAYMENT_AMOUNT_MAP_KEY);
			recordSummaryDto.setTotalPayAmount(AccountingUtils.toDispAmountLabel(totalDepositPaymentAmount));

			// 出金残額
			BigDecimal totalBalance = depositPaymentAmountExpect.subtract(totalDepositPaymentAmount);
			recordSummaryDto.setTotalBalance(AccountingUtils.toDispAmountLabel(totalBalance));

			recordSummaryDto.setStatementSummary(statementSummary);

		} else if (accgDocType == AccgDocType.INVOICE) {
			// 請求の場合

			// 請求書情報の取得
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			InvoicePaymentStatus invoicePaymentStatus = InvoicePaymentStatus.of(tAccgInvoiceEntity.getInvoicePaymentStatus());

			RecordInvoiceSummaryDto invoiceSummary = new RecordInvoiceSummaryDto();
			invoiceSummary.setInvoiceSeq(tAccgInvoiceEntity.getInvoiceSeq());
			invoiceSummary.setInvoicePaymentStatus(invoicePaymentStatus);
			invoiceSummary.setFeeAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalFeeAmountAfterWithholding())));
			invoiceSummary.setTotalDepositAllAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalDepositAllAmount())));
			invoiceSummary.setTotalAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalAmount())));
			invoiceSummary.setInvoiceAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getInvoiceAmount())));
			invoiceSummary.setInvoiceTypeName(InvoiceType.of(tAccgInvoiceEntity.getInvoiceType()).getVal());
			invoiceSummary.setInvoiceNo(tAccgInvoiceEntity.getInvoiceNo());
			invoiceSummary.setInvoiceDate(tAccgInvoiceEntity.getInvoiceDate());
			invoiceSummary.setDueDate(tAccgInvoiceEntity.getDueDate());

			//
			// 入金額の計算
			//

			// 取引実績の各金額集計値を取得
			BigDecimal totalRecordAmount = recordDetailTotalAmountMap.get(TOTAL_AMOUNT_MAP_KEY);
			BigDecimal totalFeeRepayAmount = recordDetailTotalAmountMap.get(TOTAL_FEE_REPAY_AMOUNT_MAP_KEY);
			BigDecimal totalRefundAmount = recordDetailTotalAmountMap.get(TOTAL_REFUNDED_OVER_PAYMENT_AMOUNT_MAP_KEY);

			// 入金の集計金額から振替金額と過入金返金額を引く
			// 取引実績の入金集計値には振替金額が含まれており、画面表示では、振替金額は考慮しないため除算
			BigDecimal totalPayAmount = totalRecordAmount.subtract(AccountingUtils.calcTotal(totalFeeRepayAmount, totalRefundAmount));
			recordSummaryDto.setTotalPayAmount(AccountingUtils.toDispAmountLabel(totalPayAmount));

			//
			// 残金の計算
			//

			// 予定金額を取得
			BigDecimal totalExpect = AccountingUtils.calcTotal(tAccgRecordEntity.getFeeAmountExpect(), tAccgRecordEntity.getDepositRecvAmountExpect());

			// 残金 = 予定金額 - (入金合計 - 返金額)
			// 予定金額には、振替金額が含まれているため、入金合計から振替金額は除算しない
			BigDecimal totalBalance = totalExpect.subtract(totalRecordAmount.subtract(totalRefundAmount));
			recordSummaryDto.setTotalBalance(AccountingUtils.toDispAmountLabel(totalBalance));

			if (InvoicePaymentStatus.OVER_PAYMENT == invoicePaymentStatus) {
				// 過入金が発生し、返金対応を行っていない場合 -> 残金がマイナス値になってしまう。表示上は0にする
				recordSummaryDto.setTotalBalance(AccountingUtils.toDispAmountLabel(BigDecimal.ZERO));
			}

			recordSummaryDto.setInvoiceSummary(invoiceSummary);

		} else {
			// Enum値が想定外の場合
			throw new RuntimeException("想定外のEnum値です");
		}

		return recordSummaryDto;
	}

	/**
	 * AccgRecordDetailBean -> RecordListItemDtoに変換する
	 * 
	 * @param accgRecordDetailBean
	 * @return
	 */
	private RecordListItemDto toRecordListItemDto(AccgRecordDetailBean accgRecordDetailBean) {

		RecordListItemDto dto = new RecordListItemDto();
		RecordType recordType = RecordType.of(accgRecordDetailBean.getRecordType());

		dto.setAccgRecordDetailSeq(accgRecordDetailBean.getAccgRecordDetailSeq());
		dto.setRecordType(recordType);
		dto.setRecordSeparateInputFlg(SystemFlg.codeToBoolean(accgRecordDetailBean.getRecordSeparateInputFlg()));
		dto.setRecordDate(DateUtils.parseToString(accgRecordDetailBean.getRecordDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		dto.setRecordAmount(AccountingUtils.toDispAmountLabel(accgRecordDetailBean.getRecordAmount()));
		dto.setRecordFeeAmount(AccountingUtils.toDispAmountLabel(accgRecordDetailBean.getRecordFeeAmount()));
		dto.setRecordDepositRecvAmount(AccountingUtils.toDispAmountLabel(accgRecordDetailBean.getRecordDepositRecvAmount()));
		dto.setAccgRecordDetailOverPaymentSeq(accgRecordDetailBean.getAccgRecordDetailOverPaymentSeq());
		dto.setOverPaymentAmount(AccountingUtils.toDispAmountLabel(accgRecordDetailBean.getOverPaymentAmount()));
		dto.setOverPayment(accgRecordDetailBean.getAccgRecordDetailOverPaymentSeq() != null);
		dto.setOverPaymentRefund(SystemFlg.codeToBoolean(accgRecordDetailBean.getOverPaymentRefundFlg()));
		dto.setRecordDepositPaymentAmount(AccountingUtils.toDispAmountLabel(accgRecordDetailBean.getRecordDepositPaymentAmount()));
		dto.setRemarks(accgRecordDetailBean.getRemarks());

		if (recordType == RecordType.INTO || recordType == RecordType.TRANSFER_DEPOSIT_INTO) {
			// 入金／振替（預り金）の場合：内訳を表示
			dto.setShowFeeAndDepositRecv(true);
		}

		return dto;
	}

	/**
	 * 回収不能へ変更可能かどうか
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private boolean isChangableToUncollectible(Long accgDocSeq) {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		Function<TAccgInvoiceEntity, InvoicePaymentStatus> paymentStatus = (e) -> InvoicePaymentStatus.of(e.getInvoicePaymentStatus());
		if (tAccgInvoiceEntity == null || InvoicePaymentStatus.DRAFT == paymentStatus.apply(tAccgInvoiceEntity)) {
			// 請求書が存在しない場合、取引実績の作成がされていない場合は、回収不能にはできない
			return false;
		}

		if (InvoicePaymentStatus.AWAITING_PAYMENT == paymentStatus.apply(tAccgInvoiceEntity)
				|| InvoicePaymentStatus.PARTIAL_DEPOSIT == paymentStatus.apply(tAccgInvoiceEntity)) {
			// 入金待ち、一部入金の場合のみ回収不能に変更可能
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 回収不能から元に戻すことが可能かどうか
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private boolean isUndoableFromUncollectible(Long accgDocSeq) {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		Function<TAccgInvoiceEntity, InvoicePaymentStatus> paymentStatus = (e) -> InvoicePaymentStatus.of(e.getInvoicePaymentStatus());
		if (tAccgInvoiceEntity == null || InvoicePaymentStatus.DRAFT == paymentStatus.apply(tAccgInvoiceEntity)) {
			// 請求書が存在しない場合、取引実績の作成がされていない場合は、回収不能にはできない
			return false;
		}

		if (InvoicePaymentStatus.UNCOLLECTIBLE == paymentStatus.apply(tAccgInvoiceEntity)) {
			// 回収不能の場合のみ回収不能を元に戻すことが可能
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 入金の実績と見込みが一致するかどうか
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	private boolean equalsByRecordExpect(Long accgRecordSeq) {

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (tAccgRecordEntity == null) {
			return false;
		}

		// 取引実績：入金見込み
		BigDecimal feeAmountExpect = tAccgRecordEntity.getFeeAmountExpect();
		BigDecimal depositRecvAmountExpect = tAccgRecordEntity.getDepositRecvAmountExpect();
		BigDecimal recordAmountExpect = AccountingUtils.calcTotal(feeAmountExpect, depositRecvAmountExpect);

		Map<String, BigDecimal> recordDetailTotalAmountMap = this.getRecordDetailTotalAmountMapByAccgRecordSeq(accgRecordSeq);
		if (LoiozNumberUtils.equalsDecimal(recordDetailTotalAmountMap.get(TOTAL_AMOUNT_MAP_KEY), recordAmountExpect)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 出金の実績と見込みが一致するかどうか
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	private boolean equalsByDepositPaymentExpect(Long accgRecordSeq) {

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (tAccgRecordEntity == null) {
			return false;
		}

		BigDecimal depositPaymentAmountExpect = LoiozNumberUtils.nullToZero(tAccgRecordEntity.getDepositPaymentAmountExpect());
		Map<String, BigDecimal> recordDetailTotalAmountMap = this.getRecordDetailTotalAmountMapByAccgRecordSeq(accgRecordSeq);
		if (LoiozNumberUtils.equalsDecimal(depositPaymentAmountExpect, recordDetailTotalAmountMap.get(TOTAL_DEPOSIT_PAYMENT_AMOUNT_MAP_KEY))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 取引実績に過入金データが存在するかどうか
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	private boolean existsOverPayment(Long accgRecordSeq) {
		List<AccgRecordDetailBean> accgRecordDetailBeanList = tAccgRecordDetailDao.selectAccgRecordDetailBeanByAccgRecordSeq(accgRecordSeq);
		return accgRecordDetailBeanList.stream().anyMatch(e -> e.getAccgRecordDetailOverPaymentSeq() != null);
	}

	/**
	 * 取引実績に過入金返金データが存在するかどうか
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	private boolean existsOverPaymentRefundedRecord(Long accgRecordSeq) {
		List<AccgRecordDetailBean> accgRecordDetailBeanList = tAccgRecordDetailDao.selectAccgRecordDetailBeanByAccgRecordSeq(accgRecordSeq);
		return accgRecordDetailBeanList.stream().anyMatch(e -> RecordType.OVER_PAYMENT_REFUND.equalsByCode(e.getRecordType()));
	}

	/**
	 * 請求書：取引実績における預り金額完了情報を更新する（指定の実費／預り金の入金額分を入金済みとする（入金予定の更新、入金済みデータの登録））
	 * 
	 * <pre>
	 * ※注意：既存の預り金の入金待ちデータの金額の更新と、入金済みデータの新規登録を行う。（delete&insertは行わない）
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @param recordDepositRecvAmount 実費／預り金の入金額
	 * @param recordDate 決済日（入金日）
	 * @throws AppException
	 */
	private void createDepositRecvCompleteReplicate(Long accgDocSeq, BigDecimal recordDepositRecvAmount, LocalDate recordDate) throws AppException {

		if (recordDepositRecvAmount == null || LoiozNumberUtils.equalsZero(recordDepositRecvAmount)) {
			// 入金額がない場合 -> なにもしない
			return;
		}

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgRecordEntity == null || tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 振替入金以外の請求書に紐づく預り金予定／実績データを取得
		List<TDepositRecvEntity> tDepositRecvEntities = tDepositRecvDao.selectScheduleDepositRecvEntityByCreatedAccgDocSeq(tAccgDocEntity.getAccgDocSeq());
		if (CollectionUtils.isEmpty(tDepositRecvEntities)) {
			// 請求書・精算書に紐付く預り金予定／実績データが存在しない場合 -> 何もせずに終了
			return;
		}

		// 入金予定データのみに絞り込み ※最大で2つとなる（「実費の入金予定」と「預り金の入金予定」）
		List<TDepositRecvEntity> notCompletedDepositRecvEntities = tDepositRecvEntities.stream()
				// 完了していない（予定データ）
				.filter(e -> SystemFlg.of(e.getDepositCompleteFlg()) == SystemFlg.FLG_OFF)
				// ※このメソッド自体が請求書（での入金）の場合のみに実行されるので、不要な絞り込みだが念のため絞り込む
				.filter(e -> DepositType.of(e.getDepositType()) == DepositType.NYUKIN)
				.collect(Collectors.toList());

		// 「実費の入金予定」データを取得
		TDepositRecvEntity notCompletedDepositRecvExpenseEntity = null;
		List<TDepositRecvEntity> notCompletedDepositRecvExpenseEntities = notCompletedDepositRecvEntities.stream()
				.filter(e -> ExpenseInvoiceFlg.of(e.getExpenseInvoiceFlg()) == ExpenseInvoiceFlg.EXPENSE)
				.collect(Collectors.toList());
		if (notCompletedDepositRecvExpenseEntities.size() >= 2) {
			// 「実費の入金予定」のデータが2つ以上存在している
			// -> あり得ない状態なので、エラーとする
			throw new IllegalStateException("1つの請求書から作成された「実費の入金予定」データが2つ以上存在している。accgDocSeq=" + accgDocSeq);
		}
		if (notCompletedDepositRecvExpenseEntities.size() == 1) {
			notCompletedDepositRecvExpenseEntity = notCompletedDepositRecvExpenseEntities.get(0);
		}

		// 「預り金の入金予定」データを取得
		TDepositRecvEntity notCompletedDepositRecvDepositInvoiceEntity = null;
		List<TDepositRecvEntity> notCompletedDepositRecvDepositInvoiceEntities = notCompletedDepositRecvEntities.stream()
				.filter(e -> ExpenseInvoiceFlg.of(e.getExpenseInvoiceFlg()) == ExpenseInvoiceFlg.EXCEPT_EXPENSE)
				.collect(Collectors.toList());
		if (notCompletedDepositRecvDepositInvoiceEntities.size() >= 2) {
			// 「預り金の入金予定」のデータが2つ以上存在している
			// -> あり得ない状態なので、エラーとする
			throw new IllegalStateException("1つの請求書から作成された「預り金の入金予定」データが2つ以上存在している。accgDocSeq=" + accgDocSeq);
		}
		if (notCompletedDepositRecvDepositInvoiceEntities.size() == 1) {
			notCompletedDepositRecvDepositInvoiceEntity = notCompletedDepositRecvDepositInvoiceEntities.get(0);
		}

		//
		// 予定データの更新と、入金済みデータの登録
		//

		// ※実費の入金予定データから入金済みにしていく

		// 入金額（予定に対して「入金」として使える金額）
		BigDecimal depositAmount = BigDecimal.valueOf(recordDepositRecvAmount.longValue());

		if (notCompletedDepositRecvExpenseEntity != null) {
			// 「実費の入金予定」データが存在する場合

			// 「実費の入金予定」に対して入金を行う
			depositAmount = this.depositToNotCompletedDepositRecv(notCompletedDepositRecvExpenseEntity, depositAmount, recordDate);
		}

		if (notCompletedDepositRecvDepositInvoiceEntity != null) {
			// 「預り金の入金予定」データが存在する場合

			// 「預り金の入金予定」に対して入金を行う
			this.depositToNotCompletedDepositRecv(notCompletedDepositRecvDepositInvoiceEntity, depositAmount, recordDate);
		}

		// 実費明細書の再作成フラグを更新する
		commonAccgService.updateDepositDetailRebuildFlg(tAccgDocEntity.getAnkenId(), tAccgDocEntity.getPersonId());
	}

	/**
	 * 預り金の入金予定データ（実費請求の入金予定か、預り金請求の入金予定）に対して入金を行う
	 * 
	 * <pre>
	 * ・入金額が予定額に対して満額の場合：予定データのステータスを入金済みに更新
	 * ・入金額が予定額より小さい場合：予定データを、予定と入金済みのデータに分割して、更新、登録
	 * ・入金額が予定額より大きい場合：予定データのステータスを入金済みに更新
	 * 
	 * いずれの場合も、入金額の残分（入金額から入金予定に対して入金した金額分を差し引いた残額分）を戻り値として返却
	 * </pre>
	 * 
	 * @param notCompletedDepositRecvEntity 預り金の入金予定データ
	 * @param depositAmount 入金額
	 * @param recordDate 決済日（入金日）
	 * @return 入金額の残額
	 * @throws AppException
	 */
	private BigDecimal depositToNotCompletedDepositRecv(TDepositRecvEntity notCompletedDepositRecvEntity, BigDecimal depositAmount, LocalDate recordDate) throws AppException {

		if (depositAmount == null || LoiozNumberUtils.equalsZero(depositAmount)) {
			// 入金額がない場合 -> なにもしない
			return depositAmount;
		}

		// 「預り金の入金予定」データの入金予定額
		BigDecimal notCompletedAmount = notCompletedDepositRecvEntity.getDepositAmount();

		if (LoiozNumberUtils.equalsDecimal(notCompletedAmount, depositAmount)) {
			// 入金予定額と、入金額が同じ場合
			// -> 「預り金の入金予定」を入金済み状態に更新する

			// 入金日
			notCompletedDepositRecvEntity.setDepositDate(recordDate);
			// 入金済みにする
			notCompletedDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());
			// 更新
			this.updateDepositRecv(notCompletedDepositRecvEntity);

			// 【入金額の残額】入金予定額と入金額が同じ金額のため、残額は0になる
			return BigDecimal.ZERO;
		}

		if (LoiozNumberUtils.isGreaterThan(notCompletedAmount, depositAmount)) {
			// 入金予定額が、入金額より大きい場合（全額入金しても、入金予定が残るケース）
			// -> 入金済みデータを登録し、入金予定データの金額を更新する

			// 「預り金の入金予定」データをコピーして、登録する入金済みデータを作成
			TDepositRecvEntity insertCompletedDepositRecvEntity = getDepostScheduleClone().apply(notCompletedDepositRecvEntity);

			//
			// 入金済みデータの登録
			//

			// 入金日
			insertCompletedDepositRecvEntity.setDepositDate(recordDate);
			// 入金額
			insertCompletedDepositRecvEntity.setDepositAmount(depositAmount);
			// 入金済みにする
			insertCompletedDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());
			// 登録
			this.insertDepositRecv(insertCompletedDepositRecvEntity);

			//
			// 予定データの更新
			//

			// 金額を変更（もともとの入金予定の金額から、入金済みになった金額を差し引く）
			notCompletedAmount = notCompletedAmount.subtract(depositAmount);
			notCompletedDepositRecvEntity.setDepositAmount(notCompletedAmount);
			// 更新
			this.updateDepositRecv(notCompletedDepositRecvEntity);

			// 【入金額の残額】予定への入金で入金額を使い切ったため、残額は0になる
			return BigDecimal.ZERO;

		} else {
			// 入金予定額が、入金額より小さい場合（予定額の全額に対して入金を行っても、まだ入金額が余るケース）
			// -> 「預り金の入金予定」を入金済み状態に更新する

			// 入金日
			notCompletedDepositRecvEntity.setDepositDate(recordDate);
			// 入金済みにする
			notCompletedDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());
			// 更新
			this.updateDepositRecv(notCompletedDepositRecvEntity);

			// 【入金額の残額】入金額から、入金済みとした金額分を差し引く
			depositAmount = depositAmount.subtract(notCompletedAmount);
			return depositAmount;
		}
	}

	/**
	 * 請求書：取引実績における預り金額完了情報を更新する
	 * 
	 * <pre>
	 * 取引実績詳細の預り金入金情報を預り金テーブルにコピーする
	 * 
	 * ※注意：既存の預り金の入金待ち、入金済みデータの全てを再作成する。（delete&insert）
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateDepositRecvCompleteReplicate(Long accgDocSeq) throws AppException {

		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgRecordEntity == null || tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 振替入金以外の請求書に紐づく預り金予定データを取得
		List<TDepositRecvEntity> tDepositRecvEntities = tDepositRecvDao.selectScheduleDepositRecvEntityByCreatedAccgDocSeq(tAccgDocEntity.getAccgDocSeq());
		if (CollectionUtils.isEmpty(tDepositRecvEntities)) {
			// 請求書・精算書に紐付く預り金予定データが存在しない場合 -> 何もせずに終了
			return;
		}

		// 予定データの作成する元となるEntity情報を格納する(実費請求と預り金請求で2レコードとなる想定)
		Map<ExpenseInvoiceFlg, TDepositRecvEntity> typeToCloneSouceEntity = new HashMap<>();
		// 予定金額を格納する(実費請求と預り金請求で2レコードとなる想定)
		Map<ExpenseInvoiceFlg, BigDecimal> typeToScheduleAmount = new HashMap<>();

		// 実費請求と預り金請求のレコードに分ける
		Function<TDepositRecvEntity, ExpenseInvoiceFlg> entityToExpenseInvoiceFlg = (e) -> ExpenseInvoiceFlg.of(e.getExpenseInvoiceFlg());
		Map<ExpenseInvoiceFlg, List<TDepositRecvEntity>> typeToDepositRecvList = tDepositRecvEntities.stream()
				.collect(Collectors.groupingBy(entityToExpenseInvoiceFlg, Collectors.toList()));

		// 実費請求と預り金請求を分割したマップから、コピー元Entityと予定金額をMapに格納する
		for (Entry<ExpenseInvoiceFlg, List<TDepositRecvEntity>> entry : typeToDepositRecvList.entrySet()) {
			// コピー元予定データを作成
			TDepositRecvEntity copySoruceEntity = entry.getValue().stream().findFirst().orElseThrow();
			typeToCloneSouceEntity.put(entry.getKey(), getDepostScheduleClone().apply(copySoruceEntity));

			// 予定金額合計を算出
			BigDecimal amount = entry.getValue().stream().map(TDepositRecvEntity::getDepositAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			typeToScheduleAmount.put(entry.getKey(), amount);
		}

		// 取引実績情報を取得
		List<AccgRecordDetailBean> accgRecordDetailBeanList = tAccgRecordDetailDao.selectAccgRecordDetailBeanByAccgRecordSeq(tAccgRecordEntity.getAccgRecordSeq());
		// 振替金額は再計算から除外
		List<AccgRecordDetailBean> accgRecordDetailBeanExcludeTransferIntoList = accgRecordDetailBeanList.stream().filter(e -> !RecordType.TRANSFER_DEPOSIT_INTO.equalsByCode(e.getRecordType())).collect(Collectors.toList());

		// 予定データ再振り分け後の登録用データを作成する
		List<TDepositRecvEntity> tDepositRecvInsertEntities = this.generateInvoiceDepRecvReplicateInsertEntities(typeToCloneSouceEntity, typeToScheduleAmount, accgRecordDetailBeanExcludeTransferIntoList);

		try {
			// 削除登録
			tDepositRecvDao.batchDelete(tDepositRecvEntities);
			tDepositRecvDao.batchInsert(tDepositRecvInsertEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 実費明細書の再作成フラグを更新する
		commonAccgService.updateDepositDetailRebuildFlg(tAccgDocEntity.getAnkenId(), tAccgDocEntity.getPersonId());

	}

	/**
	 * 請求時の取引実績編集処理後のデータケースを元に預り金予定の登録データを作成
	 * 
	 * @param typeToCloneSouceEntity
	 * @param typeToScheduleAmount
	 * @param accgRecordDetailBeanList
	 * @return
	 */
	private List<TDepositRecvEntity> generateInvoiceDepRecvReplicateInsertEntities(
			Map<ExpenseInvoiceFlg, TDepositRecvEntity> typeToCloneSouceEntity,
			Map<ExpenseInvoiceFlg, BigDecimal> typeToScheduleAmount,
			List<AccgRecordDetailBean> accgRecordDetailBeanList) {

		// 登録用エンティティを作成
		List<TDepositRecvEntity> depRecvInsertEntities = new ArrayList<>();

		BigDecimal expenseScheduleAmount = typeToScheduleAmount.getOrDefault(ExpenseInvoiceFlg.EXPENSE, BigDecimal.ZERO);
		BigDecimal depositScheduleAmount = typeToScheduleAmount.getOrDefault(ExpenseInvoiceFlg.EXCEPT_EXPENSE, BigDecimal.ZERO);
		for (AccgRecordDetailBean bean : accgRecordDetailBeanList) {

			BigDecimal targetAmount = LoiozNumberUtils.nullToZero(bean.getRecordDepositRecvAmount());
			if (LoiozNumberUtils.equalsZero(targetAmount)) {
				// 預り金の入金がない場合は、次へ
				continue;
			}

			if (!LoiozNumberUtils.equalsZero(expenseScheduleAmount)) {
				// 実費精算金額が存在する場合
				if (LoiozNumberUtils.equalsDecimal(targetAmount, expenseScheduleAmount) || LoiozNumberUtils.isLessThan(targetAmount, expenseScheduleAmount)) {
					// 予定金額が実績以上の場合

					// 予定金額から実績を減算
					expenseScheduleAmount = expenseScheduleAmount.subtract(targetAmount);

					// エンティティの作成(金額はtargetAmount)
					TDepositRecvEntity tDepositRecvEntity = getDepostScheduleClone().apply(typeToCloneSouceEntity.get(ExpenseInvoiceFlg.EXPENSE));
					tDepositRecvEntity.setDepositDate(bean.getRecordDate());
					tDepositRecvEntity.setDepositAmount(targetAmount);
					tDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());

					// 登録エンティティに追加
					depRecvInsertEntities.add(tDepositRecvEntity);
					// 実費分のエンティティを作成したので、次のデータへ
					continue;

				} else {
					// 予定金額が実績以下の場合

					// 実績から予定金額を減算
					targetAmount = targetAmount.subtract(expenseScheduleAmount);

					// エンティティの作成
					TDepositRecvEntity tDepositRecvEntity = getDepostScheduleClone().apply(typeToCloneSouceEntity.get(ExpenseInvoiceFlg.EXPENSE));
					tDepositRecvEntity.setDepositDate(bean.getRecordDate());
					tDepositRecvEntity.setDepositAmount(expenseScheduleAmount);
					tDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());

					// 予定金額分の入金が発生したため、予定額はゼロを設定
					expenseScheduleAmount = BigDecimal.ZERO;

					// 登録エンティティに追加
					depRecvInsertEntities.add(tDepositRecvEntity);

					// 処理しきれていない実績が残っているので、後続処理を行う
				}
			}

			if (LoiozNumberUtils.isGreaterThan(targetAmount, depositScheduleAmount)) {
				// 予定額より実費が多い場合
				throw new RuntimeException("入金予定額以上の預り金登録が発生しているケース");
			}

			// 実績から予定金額を減算
			depositScheduleAmount = depositScheduleAmount.subtract(targetAmount);

			// エンティティの作成
			TDepositRecvEntity tDepositRecvEntity = getDepostScheduleClone().apply(typeToCloneSouceEntity.get(ExpenseInvoiceFlg.EXCEPT_EXPENSE));
			tDepositRecvEntity.setDepositDate(bean.getRecordDate());
			tDepositRecvEntity.setDepositAmount(targetAmount);
			tDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());

			// 登録エンティティに追加
			depRecvInsertEntities.add(tDepositRecvEntity);
		}

		if (!LoiozNumberUtils.equalsZero(expenseScheduleAmount)) {
			// 予定額分の登録がされていない場合なので、予定レコードとして追加
			TDepositRecvEntity tDepositRecvEntity = getDepostScheduleClone().apply(typeToCloneSouceEntity.get(ExpenseInvoiceFlg.EXPENSE));
			tDepositRecvEntity.setDepositAmount(expenseScheduleAmount);
			tDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_OFF.getCd());

			// 登録エンティティに追加
			depRecvInsertEntities.add(tDepositRecvEntity);
		}

		if (!LoiozNumberUtils.equalsZero(depositScheduleAmount)) {
			// 予定額分の登録がされていない場合なので、予定レコードとして追加
			TDepositRecvEntity tDepositRecvEntity = getDepostScheduleClone().apply(typeToCloneSouceEntity.get(ExpenseInvoiceFlg.EXCEPT_EXPENSE));
			tDepositRecvEntity.setDepositAmount(depositScheduleAmount);
			tDepositRecvEntity.setDepositCompleteFlg(SystemFlg.FLG_OFF.getCd());

			// 登録エンティティに追加
			depRecvInsertEntities.add(tDepositRecvEntity);

		}

		return depRecvInsertEntities;
	}

	/**
	 * 預り金予定データの複製する関数を取得する<br>
	 * 
	 * <pre>
	 * 預り金予定レコードから取引実績の入金預り金額分差し引いて、残った金額分予定データとして作成する際に利用する
	 * コピーするデータとコピーしないデータが存在するので、仕様変更やレコード追加の際には注意すること
	 * </pre>
	 * 
	 * @return
	 */
	private Function<TDepositRecvEntity, TDepositRecvEntity> getDepostScheduleClone() {
		return (e) -> {
			TDepositRecvEntity entity = new TDepositRecvEntity();
			// 発生日・金額・完了フラグ以外は、元データの内容を引き継いで設定する
			// 該当データは預り金側から更新ができない想定なので、予定データが複数存在しても、引き継ぎされるデータはすべて同じになるはず
			entity.setDepositRecvSeq(null); // PK
			entity.setPersonId(e.getPersonId());
			entity.setAnkenId(e.getAnkenId());
			entity.setCreatedAccgDocSeq(e.getCreatedAccgDocSeq());
			entity.setUsingAccgDocSeq(e.getUsingAccgDocSeq());
			entity.setCreatedType(e.getCreatedType());
			entity.setExpenseInvoiceFlg(e.getExpenseInvoiceFlg());
			entity.setDepositDate(null); // 複製後に設定するパラメータ
			entity.setDepositItemName(e.getDepositItemName());
			entity.setDepositAmount(null);// 複製後に設定するパラメータ
			entity.setWithdrawalAmount(null);// 複製後に設定するパラメータ
			entity.setDepositType(e.getDepositType());
			entity.setSumText(e.getSumText());
			entity.setDepositRecvMemo(e.getDepositRecvMemo());
			entity.setTenantBearFlg(e.getTenantBearFlg());
			entity.setDepositCompleteFlg(null);// 複製後に設定するパラメータ
			entity.setUncollectibleFlg(e.getUncollectibleFlg());
			// 作成日時、更新日時、バージョンNoは自動生成なので設定しない
			return entity;
		};
	}

	/**
	 * 取引実績における回収不能状況の更新処理
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateUnCollectible(Long accgDocSeq) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null || tAccgRecordEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!InvoicePaymentStatus.UNCOLLECTIBLE.equalsByCode(tAccgInvoiceEntity.getInvoicePaymentStatus())) {
			// 回収不能ではない場合は、何もせずに終了
			return;
		}

		// データの取得
		TUncollectibleDetailEntity tUncollectibleDetailEntity = tUncollectibleDetailDao.selectUncollectibleDetailByUncollectibleDetailSeq(tAccgInvoiceEntity.getUncollectibleDetailSeq());
		Map<String, BigDecimal> recordDetailTotalAmountMap = getRecordDetailTotalAmountMapByAccgRecordSeq(tAccgRecordEntity.getAccgRecordSeq());

		// 入金見込み金額の集計値を算出
		BigDecimal recordAmountExpect = AccountingUtils.calcTotal(Arrays.asList(tAccgRecordEntity.getDepositRecvAmountExpect(), tAccgRecordEntity.getFeeAmountExpect()));

		// 実費明細集計値を取得
		BigDecimal recordAmountResult = recordDetailTotalAmountMap.get(TOTAL_AMOUNT_MAP_KEY);

		// 回収不能詳細情報は削除される可能性があるので、SEQを保持しておく
		Long uncollectibleSeq = tUncollectibleDetailEntity.getUncollectibleSeq();
		try {

			BigDecimal unCollectibleAmount = recordAmountExpect.subtract(recordAmountResult);
			if (LoiozNumberUtils.isGreaterThan(unCollectibleAmount, BigDecimal.ZERO)) {

				// 現在のステータスが回収不能 && 見込み金額よりも実費集計値が少ない場合 -> 金額を再計算して設定
				tUncollectibleDetailEntity.setUncollectibleAmount(unCollectibleAmount);

				// 金額の更新処理
				tUncollectibleDetailDao.update(tUncollectibleDetailEntity);

			} else {
				// 現在のステータスが回収不能 && 見込み金額以上 -> 回収済みになる

				// 回収不能詳細情報の削除
				tUncollectibleDetailDao.delete(tUncollectibleDetailEntity);

				// 回収不能詳細SEQをNullに設定し、更新(ステータス更新を行わない)
				tAccgInvoiceEntity.setUncollectibleDetailSeq(null);
				tAccgInvoiceDao.update(tAccgInvoiceEntity);

				// ステータス更新処理
				commonAccgService.updateInvoiceStatus(accgDocSeq);
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 報酬情報の回収不能フラグを更新する
		this.updateFeeUncollectibleFlg(accgDocSeq, tAccgRecordEntity.getAccgRecordSeq());

		// 預り金情報の回収不能フラグを更新する
		this.updateDepositRecvUncollectibleFlg(accgDocSeq, tAccgRecordEntity.getAccgRecordSeq());

		// 回収不能金額の再計算処理
		this.reCalcUnCollectibleUpdate(uncollectibleSeq);

	}

	/**
	 * 報酬情報の回収不能フラグを更新する
	 * 
	 * <pre>
	 * 回収不能ステータス時の取引実績データ登録・更新・削除
	 * 回収不能にする、解除処理時の利用を想定
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 * @throws AppException
	 */
	private void updateFeeUncollectibleFlg(Long accgDocSeq, Long accgRecordSeq) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (tAccgInvoiceEntity == null || tAccgRecordEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TFeeEntity> tFeeEntities = tFeeDao.selectFeeEntityByAccgDocSeq(accgDocSeq);
		if (CollectionUtils.isEmpty(tFeeEntities)) {
			// 報酬データがない場合はなにもしない
			return;
		}

		List<TFeeEntity> tFeeUpdateEntities;
		if (InvoicePaymentStatus.UNCOLLECTIBLE.equalsByCode(tAccgInvoiceEntity.getInvoicePaymentStatus())) {
			// 現在のステータスが「回収不能」の場合
			// 取引実績の報酬金額によって、回収不能フラグを設定する
			Map<String, BigDecimal> recordDetailTotalMap = this.getRecordDetailTotalAmountMapByAccgRecordSeq(accgRecordSeq);
			// 報酬項目が満額入金されているかどうか(入金可能かどうか)
			boolean isPayableFee = !LoiozNumberUtils.equalsDecimal(tAccgRecordEntity.getFeeAmountExpect(), recordDetailTotalMap.get(TOTAL_FEE_AMOUNT_MAP_KEY));

			// 回収不能時に入金可能の場合 -> 回収不能フラグをON
			tFeeUpdateEntities = tFeeEntities.stream()
					.filter(e -> SystemFlg.codeToBoolean(e.getUncollectibleFlg()) != isPayableFee) // ON -> OFF || OFF -> ON のケースのみ更新対象
					.peek(e -> e.setUncollectibleFlg(SystemFlg.booleanToCode(isPayableFee)))
					.collect(Collectors.toList());

		} else {
			// 現在のステータスが「回収不能」以外の場合
			// 請求書に紐づく報酬データの回収不能フラグをOFFにする
			tFeeUpdateEntities = tFeeEntities.stream()
					.filter(e -> SystemFlg.codeToBoolean(e.getUncollectibleFlg())) // ON -> OFFのデータのみ更新対象
					.peek(e -> e.setUncollectibleFlg(SystemFlg.FLG_OFF.getCd()))
					.collect(Collectors.toList());
		}

		if (CollectionUtils.isEmpty(tFeeUpdateEntities)) {
			// 更新データがなければ何もせずに終了
			return;
		}

		try {
			// 更新処理
			tFeeDao.batchUpdate(tFeeUpdateEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 預り金情報の回収不能フラグを更新する
	 * 
	 * <pre>
	 * 回収不能ステータス時の取引実績データ登録・更新・削除
	 * 回収不能にする、解除処理時の利用を想定
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @param accgRecordSeq
	 */
	private void updateDepositRecvUncollectibleFlg(Long accgDocSeq, Long accgRecordSeq) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgRecordSeq(accgRecordSeq);
		if (tAccgInvoiceEntity == null || tAccgRecordEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 振替入金以外の請求書に紐づく預り金予定データを取得
		List<TDepositRecvEntity> tDepositRecvEntities = tDepositRecvDao.selectScheduleDepositRecvEntityByCreatedAccgDocSeq(accgDocSeq);
		if (CollectionUtils.isEmpty(tDepositRecvEntities)) {
			return;
		}

		List<TDepositRecvEntity> tDepositRecvUpdateEntities;
		if (InvoicePaymentStatus.UNCOLLECTIBLE.equalsByCode(tAccgInvoiceEntity.getInvoicePaymentStatus())) {
			// 現在のステータスが「回収不能」の場合
			// 請求書に紐づく預り金データの入金完了フラグにもとづき回収不能フラグを設定
			tDepositRecvUpdateEntities = tDepositRecvEntities.stream()
					.filter(e -> SystemFlg.codeToBoolean(e.getDepositCompleteFlg()) == SystemFlg.codeToBoolean(e.getUncollectibleFlg()))
					.peek(e -> {
						// 入出金完了フラグを逆にして設定（完了の場合はOFF,未完了の場合はON）
						boolean isCompleted = SystemFlg.codeToBoolean(e.getDepositCompleteFlg());
						e.setUncollectibleFlg(SystemFlg.booleanToCode(!isCompleted));
					})
					.collect(Collectors.toList());

		} else {
			// 現在のステータスが「回収不能」以外の場合
			// 請求書に紐づく預り金データの回収不能フラグをOFFにする
			tDepositRecvUpdateEntities = tDepositRecvEntities.stream()
					.filter(e -> SystemFlg.codeToBoolean(e.getUncollectibleFlg())) // ON -> OFFのデータのみ更新対象
					.peek(e -> e.setUncollectibleFlg(SystemFlg.FLG_OFF.getCd()))
					.collect(Collectors.toList());
		}

		if (CollectionUtils.isEmpty(tDepositRecvUpdateEntities)) {
			// 更新データがなければ何もせずに終了
			return;
		}

		try {
			// 更新処理
			tDepositRecvDao.batchUpdate(tDepositRecvUpdateEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 取引実績情報の入力パターンによる分割後の金額と過入金金額を取得し、Dtoに変換する
	 *
	 * @param inputForm
	 * @return
	 * @throws AppException
	 */
	private RecordDetailInputPatternDto toRecordDetailInputPattern(RecordDetailInputForm.RecordDetailRowInputForm inputForm) throws AppException {

		var recordDetailInputPatternDto = new RecordDetailInputPatternDto();

		// 会計書類＋取引実績情報を取得
		AccgDocAndRecordBean accgDocAndRecordBean = tAccgRecordDao.selectAccgDocByAccgDocSeq(inputForm.getAccgDocSeq());
		if (accgDocAndRecordBean == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (AccgDocType.STATEMENT.equalsByCode(accgDocAndRecordBean.getAccgDocType())) {
			// 精算書の場合 -> 金額項目を分割したり、過入金等の概念がないので、入力値を設定して返却
			if (!LoiozNumberUtils.equalsDecimal(accgDocAndRecordBean.getDepositPaymentAmountExpect(), inputForm.getRecordDepositPaymentAmountDecimal())) {
				// 出金予定額と入力した金額が一致しない場合 -> エラー
				throw new AppException(MessageEnum.MSG_E00199, null);
			}
			recordDetailInputPatternDto.setRecordDepositPaymentAmount(inputForm.getRecordDepositPaymentAmountDecimal());
			return recordDetailInputPatternDto;
		}

		// ==========================| 以下、請求書のパターン |===========================

		// 取引実績：入金見込み
		BigDecimal feeAmountExpect = accgDocAndRecordBean.getFeeAmountExpect();
		BigDecimal depositRecvAmountExpect = accgDocAndRecordBean.getDepositRecvAmountExpect();
		BigDecimal recordAmountExpect = AccountingUtils.calcTotal(Arrays.asList(feeAmountExpect, depositRecvAmountExpect));

		// 取引実績：各金額項目の集計Map
		Map<String, BigDecimal> recordDetailTotalAmountMap;
		if (inputForm.isNew()) {
			// 新規登録時は、現在のDB値を集計
			// 新規登録時に過入金が発生しているケースはない
			recordDetailTotalAmountMap = this.getRecordDetailTotalAmountMapByAccgRecordSeq(inputForm.getAccgRecordSeq());
		} else {
			// 更新時は、更新する項目を除いた詳細情報の集計
			// 更新時には、更新対象データを集計から除外しているので、集計結果も過入金が発生していることはない
			recordDetailTotalAmountMap = this.getRecordDetailTotalAmountMapByAccgRecordSeq(inputForm.getAccgRecordSeq(), Arrays.asList(inputForm.getAccgRecordDetailSeq()));
		}

		// 金額データ
		BigDecimal recordAmount = inputForm.getRecordAmountDecimal();
		BigDecimal recordFeeAmount = inputForm.getRecordFeeAmountDecimal();
		BigDecimal recordDepositRecvAmount = inputForm.getRecordDepositRecvAmountDecimal();

		// 過入金金額
		BigDecimal overPayment = null;

		if (LoiozNumberUtils.equalsDecimal(recordDetailTotalAmountMap.get(TOTAL_AMOUNT_MAP_KEY), recordAmountExpect)) {
			// すでに登録済の実績が、予定金額と一致する場合 -> 満額過入金は登録できない
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (inputForm.isSeparateInput()) {
			// 個別入金の場合

			// 実績入金を算出(報酬金額 + 預り金入金)
			recordAmount = AccountingUtils.calcTotal(Arrays.asList(recordFeeAmount, recordDepositRecvAmount));

			BigDecimal feeAmounIncludeInputFee = LoiozNumberUtils.nullToZero(recordFeeAmount).add(recordDetailTotalAmountMap.get(TOTAL_FEE_AMOUNT_MAP_KEY));
			if (LoiozNumberUtils.isGreaterThan(feeAmounIncludeInputFee, feeAmountExpect)) {
				// 報酬金額入力値 + すでに登録した報酬実績の集計が見込み予定額を超過した場合 -> エラー
				throw new AppException(MessageEnum.MSG_E00178, null, "「報酬の入金合計額」", "「報酬計の残額」");
			}

			// 入力値の預り金額とすでに登録済の実績を集計する
			BigDecimal depositRecvAmountIncludeInputDepositRecv = LoiozNumberUtils.nullToZero(recordDepositRecvAmount)
					.add(recordDetailTotalAmountMap.get(TOTAL_DEPOSIT_RECV_AMOUNT_MAP_KEY));

			if (LoiozNumberUtils.isGreaterThan(depositRecvAmountIncludeInputDepositRecv, depositRecvAmountExpect)) {
				// 預り金見込み額が超過する場合 -> 過入金が発生するの場合

				if (feeAmounIncludeInputFee.compareTo(feeAmountExpect) != 0) {
					// 報酬残額ありの状態で、預り金の過入金状態の場合 -> エラー
					throw new AppException(MessageEnum.MSG_E00201, null);
				}

				// 過入金金額を算出 ((入力値の預かり入金額 + 入金済預かり入金額集計額 - 過入金額集計) - 見込み預かり入金額)
				overPayment = depositRecvAmountIncludeInputDepositRecv.subtract(depositRecvAmountExpect);

				// 預かり金額は、加入金を除く
				recordDepositRecvAmount = recordDepositRecvAmount.subtract(overPayment);
			}

		} else {
			// 一括入金の場合

			// 入金見込みの残金を算出する (入金見込み金額 - 取引実績入金集計金額)
			BigDecimal recordBalanceAmount = recordAmountExpect.subtract(recordDetailTotalAmountMap.get(TOTAL_AMOUNT_MAP_KEY));

			if (LoiozNumberUtils.isLessThan(recordAmount, recordBalanceAmount) &&
					(LoiozNumberUtils.isGreaterThan(feeAmountExpect, BigDecimal.ZERO) && LoiozNumberUtils.isGreaterThan(depositRecvAmountExpect, BigDecimal.ZERO))) {
				// 「報酬と預り金の両方に見込み金額がある」且つ「見込み残高より少ない」場合、一括入力では残り残金以上の入力が必須
				throw new AppException(MessageEnum.MSG_E00200, null);
			}

			// 過入金の判定
			if (LoiozNumberUtils.isGreaterThan(recordBalanceAmount, BigDecimal.ZERO) && LoiozNumberUtils.isGreaterThan(recordAmount, recordBalanceAmount)) {
				// 入金見込み残高が1円以上 && 入力値金額が見込み数値より大きい場合 -> 過入金が発生する（入力値 - 入金見込み（残高））
				overPayment = recordAmount.subtract(recordBalanceAmount); // 過入金額の算出

				// 見込み残高が1円以上存在し、過入金が発生する場合
				// 報酬入金額は、報酬見込みの残金の満額を設定(報酬見込み残金が0円の場合はnullを設定)
				BigDecimal feeBalanceAmount = feeAmountExpect.subtract(recordDetailTotalAmountMap.get(TOTAL_FEE_AMOUNT_MAP_KEY));
				recordFeeAmount = LoiozNumberUtils.zeroToNull(feeBalanceAmount);

				// 預り金額は、入力値から報酬残金額を除算した金額を設定(過入金なので、報酬残金額を除算しても0円になることはない)
				BigDecimal depositRecvBalanceAmount = depositRecvAmountExpect.subtract(recordDetailTotalAmountMap.get(TOTAL_DEPOSIT_RECV_AMOUNT_MAP_KEY));
				recordDepositRecvAmount = LoiozNumberUtils.zeroToNull(depositRecvBalanceAmount);

			} else {
				// 過入金は発生しない場合は入力値のまま

				// 報酬残額
				BigDecimal recordFeeBalance = feeAmountExpect.subtract(recordDetailTotalAmountMap.get(TOTAL_FEE_AMOUNT_MAP_KEY));
				// 預り金残額
				BigDecimal recordDepositRecvBalance = depositRecvAmountExpect.subtract(recordDetailTotalAmountMap.get(TOTAL_DEPOSIT_RECV_AMOUNT_MAP_KEY));

				if (this.isSeparateInputEnableCheck(feeAmountExpect, depositRecvAmountExpect)) {
					// 報酬と預り金の請求

					// 残額を全て報酬と預り金に反映する
					recordFeeAmount = recordFeeBalance;
					recordDepositRecvAmount = recordDepositRecvBalance;
				} else {
					// 入力値の反映

					// 報酬と預り金のどちらか請求
					if (LoiozNumberUtils.isGreaterThan(feeAmountExpect, BigDecimal.ZERO)) {
						// 報酬に反映
						recordFeeAmount = recordAmount;
					} else if (LoiozNumberUtils.isGreaterThan(depositRecvAmountExpect, BigDecimal.ZERO)) {
						// 預り金に反映
						recordDepositRecvAmount = recordAmount;
					}
				}
			}
		}

		recordDetailInputPatternDto.setSeparateInput(inputForm.isSeparateInput());
		recordDetailInputPatternDto.setRecordAmount(recordAmount);
		recordDetailInputPatternDto.setRecordFeeAmount(recordFeeAmount);
		recordDetailInputPatternDto.setRecordDepositRecvAmount(recordDepositRecvAmount);
		recordDetailInputPatternDto.setOverPayment(overPayment);

		return recordDetailInputPatternDto;
	}

	/**
	 * 取引実績の金額変更が可能な取引実績詳細SEQを取得する<br>
	 * 更新だけでなく、削除時も実質金額の編集が発生するのでチェックが必要
	 * 
	 * <pre>
	 * 取引実績詳細の金額の編集が不可の条件は以下となる。
	 * １．取引実績に過入金が発生している場合に、発生した過入金に対して「処理済み」の場合
	 * 　　⇒ すべてのレコード金額の編集が不可となる
	 * ２．取引実績に過入金が発生している場合で、発生した過入金に対して「未処理」の場合
	 * 　　⇒ 過入金が発生したレコード以外のレコードが金額変更不可となる
	 * ３．上記以外のすべてのケース
	 * 　　→ 振替入金レコードの編集が不可
	 * </pre>
	 * 
	 * @return
	 */
	private List<Long> getUpdatableAmountRecordDetailSeq(Long accgRecordSeq) {

		// 取引実績詳細情報を取得
		TAccgRecordDetailOverPaymentEntity tAccgRecordDetailOverPaymentEntity = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordSeq(accgRecordSeq);

		if (tAccgRecordDetailOverPaymentEntity != null) {
			// 過入金が発生しているケース
			if (SystemFlg.codeToBoolean(tAccgRecordDetailOverPaymentEntity.getOverPaymentRefundFlg())) {
				// 過入金処理済みの場合 -> すべてのレコードが金額の変更が不可
				return Collections.emptyList();
			} else {
				// 過入金未処理 -> 過入金発生レコードのみ編集可能
				return Arrays.asList(tAccgRecordDetailOverPaymentEntity.getAccgRecordDetailSeq());
			}
		}

		List<AccgRecordDetailBean> accgRecordDetailBeanList = tAccgRecordDetailDao.selectAccgRecordDetailBeanByAccgRecordSeq(accgRecordSeq);
		return accgRecordDetailBeanList.stream()
				.filter(e -> !RecordType.TRANSFER_DEPOSIT_INTO.equalsByCode(e.getRecordType()))
				.map(AccgRecordDetailBean::getAccgRecordDetailSeq)
				.collect(Collectors.toList());
	}

	/**
	 * 取引実績詳細の各金額集計値Mapを取得
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	private Map<String, BigDecimal> getRecordDetailTotalAmountMapByAccgRecordSeq(Long accgRecordSeq) {
		return getRecordDetailTotalAmountMapByAccgRecordSeq(accgRecordSeq, Collections.emptyList());
	}

	/**
	 * 取引実績詳細-各合計金額の値Mapを取得
	 * 
	 * @param accgRecordSeq
	 * @param excludeAccgRecordDetailSeqList
	 * @return
	 */
	private Map<String, BigDecimal> getRecordDetailTotalAmountMapByAccgRecordSeq(Long accgRecordSeq, List<Long> excludeAccgRecordDetailSeqList) {

		// 取引実績SEQに紐づく、取引実績詳細情報をすべて取得
		List<AccgRecordDetailBean> tAccgRecordDetailEntities = tAccgRecordDetailDao.selectAccgRecordDetailBeanByAccgRecordSeq(accgRecordSeq);

		// 除外する取引実績詳細
		Predicate<AccgRecordDetailBean> excludeFilter = (e) -> !excludeAccgRecordDetailSeqList.contains(e.getAccgRecordDetailSeq());

		// 実績入金額
		List<BigDecimal> recordAmount = tAccgRecordDetailEntities.stream().filter(excludeFilter)
				.map(AccgRecordDetailBean::getRecordAmount).collect(Collectors.toList());
		// 報酬入金額
		List<BigDecimal> recordFeeAmount = tAccgRecordDetailEntities.stream().filter(excludeFilter)
				.map(AccgRecordDetailBean::getRecordFeeAmount).collect(Collectors.toList());
		// 報酬入金額（既入金のみ）
		List<BigDecimal> recordFeeRepayAmount = tAccgRecordDetailEntities.stream()
				.filter(excludeFilter)
				.filter(e -> RecordType.TRANSFER_DEPOSIT_INTO.equalsByCode(e.getRecordType()))
				.map(AccgRecordDetailBean::getRecordFeeAmount).collect(Collectors.toList());
		// 預り金入金額
		List<BigDecimal> recordDepositRecvAmount = tAccgRecordDetailEntities.stream().filter(excludeFilter)
				.map(AccgRecordDetailBean::getRecordDepositRecvAmount).collect(Collectors.toList());
		// 預り金出金額
		List<BigDecimal> recordDepositPaymentAmount = tAccgRecordDetailEntities.stream().filter(excludeFilter)
				.map(AccgRecordDetailBean::getRecordDepositPaymentAmount).collect(Collectors.toList());
		// 過入金額
		List<BigDecimal> recordOverPaymentAmount = tAccgRecordDetailEntities.stream().filter(excludeFilter)
				.map(AccgRecordDetailBean::getOverPaymentAmount).collect(Collectors.toList());
		// 過入金額の返金済み金額
		List<BigDecimal> recordRefundedOverPaymentAmount = tAccgRecordDetailEntities.stream()
				.filter(excludeFilter)
				.filter(e -> e.getAccgRecordDetailOverPaymentSeq() != null && SystemFlg.FLG_ON.equalsByCode(e.getOverPaymentRefundFlg()))
				.map(AccgRecordDetailBean::getOverPaymentAmount).collect(Collectors.toList());

		// 過入金額の返金前の金額
		List<BigDecimal> recordUnrefundedOverPaymentAmount = tAccgRecordDetailEntities.stream()
				.filter(excludeFilter)
				.filter(e -> e.getAccgRecordDetailOverPaymentSeq() != null && SystemFlg.FLG_OFF.equalsByCode(e.getOverPaymentRefundFlg()))
				.map(AccgRecordDetailBean::getOverPaymentAmount).collect(Collectors.toList());

		var recordDetailTotalAmountMap = new HashMap<String, BigDecimal>();
		recordDetailTotalAmountMap.put(TOTAL_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordAmount));
		recordDetailTotalAmountMap.put(TOTAL_FEE_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordFeeAmount));
		recordDetailTotalAmountMap.put(TOTAL_FEE_REPAY_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordFeeRepayAmount));
		recordDetailTotalAmountMap.put(TOTAL_DEPOSIT_RECV_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordDepositRecvAmount));
		recordDetailTotalAmountMap.put(TOTAL_DEPOSIT_PAYMENT_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordDepositPaymentAmount));
		recordDetailTotalAmountMap.put(TOTAL_OVER_PAYMENT_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordOverPaymentAmount));
		recordDetailTotalAmountMap.put(TOTAL_REFUNDED_OVER_PAYMENT_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordRefundedOverPaymentAmount));
		recordDetailTotalAmountMap.put(TOTAL_UNREFUNDED_OVER_PAYMENT_AMOUNT_MAP_KEY, AccountingUtils.calcTotal(recordUnrefundedOverPaymentAmount));

		return recordDetailTotalAmountMap;
	}

	/**
	 * 回収不能金情報を再計算し、更新を行う
	 * 
	 * @param uncollectibleSeq
	 * @throws AppException
	 */
	private void reCalcUnCollectibleUpdate(Long uncollectibleSeq) throws AppException {

		TUncollectibleEntity tUncollectibleEntity = tUncollectibleDao.selectUncollectibleByUncollectibleSeq(uncollectibleSeq);
		if (tUncollectibleEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TUncollectibleDetailEntity> tUncollectibleDetailEntities = tUncollectibleDetailDao.selectUncollectibleDetailByUncollectibleSeq(uncollectibleSeq);
		List<BigDecimal> uncollectibleAmountList = tUncollectibleDetailEntities.stream().map(TUncollectibleDetailEntity::getUncollectibleAmount).collect(Collectors.toList());
		BigDecimal uncollectibleAmount = AccountingUtils.calcTotal(uncollectibleAmountList);

		try {
			if (LoiozNumberUtils.equalsZero(uncollectibleAmount)) {
				// 金額が０円の場合 -> 回収不能金額が存在しないので、削除処理
				tUncollectibleDao.delete(tUncollectibleEntity);

			} else {
				// 金額を再設定
				tUncollectibleEntity.setTotalUncollectibleAmount(uncollectibleAmount);
				tUncollectibleDao.update(tUncollectibleEntity);

			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 個別入力が可能かチェック
	 * 
	 * @param feeAmountExpect
	 * @param depositRecvAmountExpect
	 * @return
	 */
	private boolean isSeparateInputEnableCheck(BigDecimal feeAmountExpect, BigDecimal depositRecvAmountExpect) {
		// 個別入力の可否
		boolean isSeparateInputEnable = false;
		if (LoiozNumberUtils.isGreaterThan(feeAmountExpect, BigDecimal.ZERO) && LoiozNumberUtils.isGreaterThan(depositRecvAmountExpect, BigDecimal.ZERO)) {
			// 報酬と預り金の両方を請求している場合
			isSeparateInputEnable = true;
		}
		return isSeparateInputEnable;
	}

	/**
	 * 預り金データを登録
	 * 
	 * @param entity
	 * @return
	 * @throws AppException
	 */
	private TDepositRecvEntity insertDepositRecv(TDepositRecvEntity entity) throws AppException {
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

		return entity;
	}

	/**
	 * 預り金データを更新
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

}