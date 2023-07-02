package jp.loioz.app.common.mvc.accgPaymentPlanEdit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.mvc.accgPaymentPlanEdit.form.AccgInvoiceStatementPaymentPlanEditForm;
import jp.loioz.app.common.mvc.accgPaymentPlanEdit.form.AccgInvoiceStatementPaymentPlanEditForm.PaymentPlanRowInputForm;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.bean.AccgInvoicePaymentPlanBean;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgInvoicePaymentPlanDao;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgInvoicePaymentPlanEntity;

/**
 * 支払計画編集モーダルサービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccgInvoiceStatementPaymentPlanEditCommonService extends DefaultService {

	/** ファイルストレージサービス */
	@Autowired
	private FileStorageService fileStorageService;

	/** 会計管理共通サービス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 支払計画Daoクラス */
	@Autowired
	private TAccgInvoicePaymentPlanDao tAccgInvoicePaymentPlanDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 編集フォームオブジェクトの作成
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	public AccgInvoiceStatementPaymentPlanEditForm createAccgInvoiceStatementPaymentPlanEditForm(Long invoiceSeq) throws AppException {

		// オブジェクトの生成
		var accgInvoiceStatementPaymentPlanEditForm = new AccgInvoiceStatementPaymentPlanEditForm();
		accgInvoiceStatementPaymentPlanEditForm.setInvoiceSeq(invoiceSeq);

		// 表示用プロパティの設定
		setDispProperties(accgInvoiceStatementPaymentPlanEditForm);

		// 初期データ情報を設定
		List<AccgInvoicePaymentPlanBean> paymentPlanBeanList = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanSalesByInvoiceSeq(invoiceSeq);
		List<PaymentPlanRowInputForm> paymentPlanEditDtoList = paymentPlanBeanList.stream().map(this::convertBean2Form).collect(Collectors.toList());
		accgInvoiceStatementPaymentPlanEditForm.setPaymentPlanList(paymentPlanEditDtoList);

		return accgInvoiceStatementPaymentPlanEditForm;
	}

	/**
	 * 表示用プロパティの設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(AccgInvoiceStatementPaymentPlanEditForm inputForm) throws AppException {

		// データの取得
		TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntity(inputForm.getInvoiceSeq());

		// TODO 表示用データの設定
		inputForm.setInvoiceAmount(AccountingUtils.toDispAmountLabel(tAccgInvoiceEntity.getInvoiceAmount()));
		inputForm.setSalesAmount("0");
		inputForm.setZankin("0");
		inputForm.setSeisanCompleted(false);

	}

	/**
	 * 支払計画の保存処理<br>
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveShiharaiPlan(AccgInvoiceStatementPaymentPlanEditForm inputForm, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		Long invoiceSeq = inputForm.getInvoiceSeq();
		TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntity(invoiceSeq);
		if (!LoiozNumberUtils.equalsDecimal(tAccgInvoiceEntity.getInvoiceAmount(), inputForm.getTotalInputAmount())) {
			// 請求金額と入金予定の金額(入力値)が一致しない場合 -> 入力エラー
			throw new AppException(MessageEnum.MSG_E00002, null, "請求金額", "入金予定額の合計");
		}

		if (!IssueStatus.DRAFT.equalsByCode(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
			// 下書きではない場合は編集不可
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanDeleteEntities = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(invoiceSeq);
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanInsertEntity = inputForm.getPaymentPlanList().stream().map(e -> {
			TAccgInvoicePaymentPlanEntity tAccgInvoicePaymentPlanEntity = new TAccgInvoicePaymentPlanEntity();
			tAccgInvoicePaymentPlanEntity.setInvoiceSeq(invoiceSeq);
			tAccgInvoicePaymentPlanEntity.setPaymentScheduleDate(e.getPaymentScheduleLocalDate());
			tAccgInvoicePaymentPlanEntity.setPaymentScheduleAmount(e.getPaymentScheduleAmountDecimal());
			tAccgInvoicePaymentPlanEntity.setSumText(e.getSumText());
			return tAccgInvoicePaymentPlanEntity;
		}).collect(Collectors.toList());

		try {
			// 削除登録
			tAccgInvoicePaymentPlanDao.batchDelete(tAccgInvoicePaymentPlanDeleteEntities);
			tAccgInvoicePaymentPlanDao.batchInsert(tAccgInvoicePaymentPlanInsertEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 支払計画PDFの再作成処理
		commonAccgService.createAccgDocFileAndDetail(tAccgInvoiceEntity.getAccgDocSeq(), tenantSeq, true, deleteS3ObjectKeys, AccgDocFileType.INVOICE_PAYMENT_PLAN);

	}

	/**
	 * S3オブジェクトの削除処理
	 * 
	 * <pre>
	 * ゴミデータとなるS3オブジェクトの削除処理
	 * S3オブジェクトキーを含むレコードをDBから削除したときに、別トランザクションで実施することを想定している
	 * そのため、呼び出し元はコントローラーメソッドのみとすること
	 * </pre>
	 * 
	 * @param s3ObjectKeys
	 */
	public void deleteS3Object(Set<String> deleteS3ObjectKeys) {
		if (CollectionUtils.isEmpty(deleteS3ObjectKeys)) {
			return;
		}

		try {
			// S3オブジェクトの削除処理
			fileStorageService.deleteFile(new ArrayList<>(deleteS3ObjectKeys));

		} catch (Exception ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + "S3オブジェクトの削除に失敗しました。DBに管理されていないS3オブジェクトが残っています", ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 請求情報の取得
	 * 
	 * @param invoiceSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	private TAccgInvoiceEntity getAccgInvoiceEntity(Long invoiceSeq) throws AppException {
		// データの取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		return tAccgInvoiceEntity;
	}

	/**
	 * AccgInvoicePaymentPlanBean型からPaymentPlanRowInputForm型に変換します<br>
	 * 
	 * @param bean
	 * @return
	 */
	private PaymentPlanRowInputForm convertBean2Form(AccgInvoicePaymentPlanBean bean) {

		PaymentPlanRowInputForm rowForm = new PaymentPlanRowInputForm();

		rowForm.setPaymentScheduleAmount(AccountingUtils.toDispAmountLabel(bean.getPaymentScheduleAmount()));
		rowForm.setPaymentScheduleDate(DateUtils.parseToString(bean.getPaymentScheduleDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		rowForm.setSumText(bean.getSumText());

		return rowForm;
	}

}