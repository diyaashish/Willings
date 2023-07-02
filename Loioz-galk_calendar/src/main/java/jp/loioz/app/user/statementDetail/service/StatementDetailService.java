package jp.loioz.app.user.statementDetail.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.form.accg.AccgDocSummaryForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BaseToInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.InvoiceRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm;
import jp.loioz.app.common.service.CommonAccgAmountService;
import jp.loioz.app.common.service.CommonAccgInvoiceStatementService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.bean.AccgDocFileBean;
import jp.loioz.bean.StatementDetailBean;
import jp.loioz.bean.TenantDetailKozaBean;
import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocActType;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AccggInvoiceStatementDetailViewTab;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import jp.loioz.common.constant.CommonConstant.InvoiceType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.MailTemplateType;
import jp.loioz.common.constant.CommonConstant.NameEnd;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.service.pdf.PdfService;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MInvoiceSettingDao;
import jp.loioz.dao.MMailDao;
import jp.loioz.dao.MMailSettingDao;
import jp.loioz.dao.MMailTemplateDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAccgDocActSendFileDao;
import jp.loioz.dao.TAccgDocFileDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.FileContentsDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MInvoiceSettingEntity;
import jp.loioz.entity.MMailEntity;
import jp.loioz.entity.MMailSettingEntity;
import jp.loioz.entity.MMailTemplateEntity;
import jp.loioz.entity.TAccgDocActEntity;
import jp.loioz.entity.TAccgDocActSendFileEntity;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgStatementEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TGinkoKozaEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 精算書詳細覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StatementDetailService extends DefaultService {

	/** 共通：請求書/精算書サービス */
	@Autowired
	private CommonAccgInvoiceStatementService commonAccgInvoiceStatementService;

	/** 共通：会計サービス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 会計管理の金額を扱う共通サービス */
	@Autowired
	private CommonAccgAmountService commonAccgAmountService;

	/** 共通：名簿サービスクラス */
	@Autowired
	private CommonPersonService commonPersonService;

	/** 共通：ファイルストレージサービス */
	@Autowired
	private FileStorageService fileStorageService;

	/** 共通：PDFサービス */
	@Autowired
	private PdfService pdfService;

	/** 管理DB：メールDaoクラス */
	@Autowired
	private MMailDao mgtMMailDao;

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** テナントDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** メール設定Daoクラス */
	@Autowired
	private MMailSettingDao mMailSettingDao;

	/** メールテンプレートDaoクラス */
	@Autowired
	private MMailTemplateDao mMailTemplateDao;

	/** 請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

	/** 会書類-ファイルDaoクラス */
	@Autowired
	private TAccgDocFileDao tAccgDocFileDao;

	/** 会計書類-対応-送付-ファイルDaoクラス */
	@Autowired
	private TAccgDocActSendFileDao tAccgDocActSendFileDao;

	/** 顧客共通Daoクラス */
	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

	/** 請求書設定マスタDaoクラス */
	@Autowired
	private MInvoiceSettingDao mInvoiceSettingDao;

	/** 案件情報用のDaoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 顧客情報用のDaoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 銀行口座用のDaoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 会計書類SEQを取得します
	 * 
	 * @param statementSeq
	 * @return
	 */
	public Long getAccgDocSeq(Long statementSeq) {
		return commonAccgService.getAccgDocSeqByStatementSeq(statementSeq);
	}
	
	/**
	 * 精算書：請求書/精算書画面のフォームオブジェクトを作成する
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm createStatementViewForm(Long statementSeq) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		StatementDetailBean statementDetailBean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(accgDocSeq);

		var viewForm = new AccgInvoiceStatementViewForm();

		// SEQ情報を設定
		viewForm.setAccgDocSeq(accgDocSeq);
		viewForm.setStatementSeq(statementSeq);
		viewForm.setAnkenId(statementDetailBean.getAnkenId());
		viewForm.setPersonId(statementDetailBean.getPersonId());
		viewForm.setAccgDocType(AccgDocType.STATEMENT);

		// 案件情報の設定
		viewForm.setAnkenForm(this.getAnkenForm(statementDetailBean));
		// タブ選択情報の設定
		viewForm.setDocContentsForm(this.getDocContentsForm(statementDetailBean, null));

		// 精算書_基本情報_タイトルエリア
		viewForm.setBaseTitleViewForm(this.getBaseTitleViewForm(statementDetailBean));
		// 精算書_基本情報_精算先エリア
		viewForm.setBaseToViewForm(this.getBaseToViewForm(statementDetailBean));
		// 精算書_基本情報_精算元エリア
		viewForm.setBaseFromViewForm(this.getBaseFromViewForm(statementDetailBean));
		// 精算書_基本情報_挿入文情報エリア
		viewForm.setBaseOtherViewForm(this.getBaseOtherViewForm(statementDetailBean));

		// 既入金表示エリア
		viewForm.setRepayViewForm(getRepayViewForm(accgDocSeq));
		// 請求項目エリア
		viewForm.setInvoiceViewForm(getInvoiceViewForm(statementSeq));

		// 銀行詳細情報エリア
		viewForm.setBankDetailViewForm(this.getBankDetailViewForm(statementDetailBean));
		// 備考情報エリア
		viewForm.setRemarksViewForm(this.getRemarksViewForm(statementDetailBean));

		// 取引情報エリア
		viewForm.setDocSummaryForm(getDocSummaryForm(statementSeq));
		// 進行状況エリア
		viewForm.setDocActivityForm(getDocActivityForm(statementSeq));
		// 精算書の内部メモ
		viewForm.setMemoViewForm(this.getMemoViewForm(statementDetailBean));

		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合 -> 基本情報は表示せず、精算書PDFを表示する
			viewForm.setDocStatementPdfViewForm(getDocStatementPdfViewForm(statementSeq));
		}

		return viewForm;
	}

	/**
	 * 精算書詳細の案件用情報表示用を取得します
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.AnkenForm getAnkenForm(Long statementSeq) throws DataNotFoundException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		// 精算書詳細データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(accgDocSeq);

		return this.getAnkenForm(bean);
	}

	/**
	 * 精算書発行前のチェック処理
	 * 
	 * @param requestSourceAccgDocType リクエスト元の会計書類種別
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void checkOfBeforeIssue(AccgDocType requestSourceAccgDocType, Long statementSeq) throws AppException {
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		commonAccgInvoiceStatementService.checkOfBeforeIssue(requestSourceAccgDocType, tAccgStatementEntity.getAccgDocSeq());
	}

	/**
	 * 精算書発行前のチェック (フォーマット変更フラグを考慮済み)
	 * 
	 * @param requestSourceAccgDocType
	 * @param statementSeq
	 * @param changeFormat
	 * @throws AppException
	 */
	public void checkOfBeforeIssue(AccgDocType requestSourceAccgDocType, Long statementSeq, boolean changeFormat) throws AppException {

		checkOfBeforeIssue(requestSourceAccgDocType, statementSeq);

		boolean isMinus = checkIfStatementAmountIsMinus(statementSeq);
		if (changeFormat && !isMinus) {
			// フォーマット変更フラグがON だが、精算金額がマイナスではない場合 -> 楽観ロックエラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		if (!changeFormat && isMinus) {
			// フォーマット変更フラグがOFF だが、精算金額がマイナスの場合 -> 楽観ロックエラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * 精算額がマイナスかどうか
	 * 
	 * @param statementSeq
	 * @return
	 * @throws AppException
	 */
	public boolean checkIfStatementAmountIsMinus(Long statementSeq) throws AppException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		if (LoiozNumberUtils.isLessThan(tAccgStatementEntity.getStatementAmount(), BigDecimal.ZERO)) {
			// 精算額が０より小さい場合
			return true;
		}

		return false;
	}

	/**
	 * 精算書_発行処理
	 * 
	 * @param statementSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void issue(Long statementSeq, Long ankenId, Long personId) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		// 精算書発行処理のバリデーション
		this.validateUnChangedFormatIssue(tAccgStatementEntity);

		// 精算書の発行処理を行う
		commonAccgInvoiceStatementService.issue(tAccgStatementEntity.getAccgDocSeq(), ankenId, personId, AccgDocType.STATEMENT);
	}

	/**
	 * 精算書 -> 請求書処理
	 * 
	 * <pre>
	 * S3オブジェクトキーをDBから削除するため、S3オブジェクトファイルを削除を呼び出し元コントローラーで実施すること
	 * </pre>
	 * 
	 * @param statementSeq
	 * @param ankenId
	 * @param deleteS3ObjectKeys 削除するS3オブジェクトキー
	 * @return 変更後の請求書SEQ
	 * @throws AppException
	 */
	public Long changeToInvoice(Long statementSeq, Long ankenId, Set<String> deleteS3ObjectKeys) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		// 会計書類のドキュメントタイプ変更
		commonAccgInvoiceStatementService.updateAccgDocType(accgDocSeq, AccgDocType.INVOICE);

		// 精算書情報から請求書情報の作成
		TAccgInvoiceEntity tAccgInvoiceEntity = this.toAccgInvoiceEntity(tAccgStatementEntity, ankenId);
		try {
			// 精算書情報の削除
			tAccgStatementDao.delete(tAccgStatementEntity);

			// 請求書情報の登録
			tAccgInvoiceDao.insert(tAccgInvoiceEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 会計書類ファイルの削除処理
		commonAccgService.deleteAccgDocFileExcludeSend(tAccgStatementEntity.getAccgDocSeq(), deleteS3ObjectKeys);

		return tAccgInvoiceEntity.getInvoiceSeq();
	}

	/**
	 * 精算書を発行前に戻す処理
	 * 
	 * @param statementSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void updateStatementToDraftAndRemoveRelatedData(Long statementSeq, Long ankenId, Long personId) throws AppException {

		// 精算データ
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		// 下書き変更処理
		commonAccgInvoiceStatementService.updateStatementToDraftAndRemoveRelatedData(tAccgStatementEntity.getAccgDocSeq(), personId, ankenId);
	}

	/**
	 * 会計書類SEQに紐づく書類が送信されているか確認します。<br>
	 * 1つでも送信してあればtrueを返します。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public boolean checkIfAccgDocHasBeenSent(Long accgDocSeq) {
		return commonAccgInvoiceStatementService.checkIfAccgDocHasBeenSent(accgDocSeq);
	}

	/**
	 * 既入金フラグメント入力情報相関チェック<br>
	 * ・請求書の発行ステータスチェック<br>
	 * ・必須チェック <br>
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param repayRowList
	 * @param result
	 * @return
	 * @throws AppException
	 */
	public BindingResult validateRepayRowList(Long accgDocSeq, Long ankenId, Long personId, List<RepayRowInputForm> repayRowList, BindingResult result) throws AppException {
		return commonAccgInvoiceStatementService.validateRepayRowList(accgDocSeq, ankenId, personId, repayRowList, result);
	}
	
	/**
	 * 請求フラグメント入力情報相関チェック<br>
	 * ・請求書の発行ステータスチェック<br>
	 * ・必須チェック <br>
	 * ・値引き上限チェック <br>
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param invoiceRowList
	 * @param result
	 * @return
	 * @throws AppException
	 */
	public BindingResult validateInvoiceRowList(Long accgDocSeq, Long ankenId, Long personId, List<InvoiceRowInputForm> invoiceRowList, BindingResult result) throws AppException {
		return commonAccgInvoiceStatementService.validateInvoiceRowList(accgDocSeq, ankenId, personId, invoiceRowList, result);
	}
	
	/**
	 * 請求フラグメントまとめ表示処理前チェック<br>
	 * 実費データに対して下記チェックをおこなう<br>
	 * ・必須チェック<br>
	 * ・日付のフォーマットチェック<br>
	 *  
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param invoiceRowList
	 * @param result
	 * @return
	 * @throws AppException
	 */
	public BindingResult validationBeforeGroupOrUngroupSimilarInvoiceItems(Long accgDocSeq, Long ankenId, Long personId, List<InvoiceRowInputForm> invoiceRowList, BindingResult result) throws AppException {
		return commonAccgInvoiceStatementService.validationBeforeGroupOrUngroupSimilarInvoiceItems(accgDocSeq, ankenId, personId, invoiceRowList, result);
	}
	
	/**
	 * 精算書削除処理
	 * 
	 * @param statementSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void delete(Long statementSeq, Long ankenId, Long personId) throws AppException {
		
		Long accgDocSeq = getAccgDocSeq(statementSeq);
		
		// 送付済みの書類があるか確認
		if (checkIfAccgDocHasBeenSent(accgDocSeq)) {
			// 書類を送付している場合は削除不可とする
			throw new AppException(MessageEnum.MSG_E00086, null, "送付済みの");
		}

		// 精算情報の取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		try {
			// 精算書情報削除
			tAccgStatementDao.delete(tAccgStatementEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 請求書、精算書削除時の共通処理
		commonAccgInvoiceStatementService.commonDeleteForInvoiceAndStatements(tAccgStatementEntity.getAccgDocSeq(), personId, ankenId, tAccgStatementEntity.getSalesDetailSeq(), null);
	}

	/**
	 * 請求書画面：会計書類送付入力フォームオブジェクトを作成
	 * 
	 * @param statementSeq
	 * @param sendType
	 * @param loginAccountSeq
	 * @param tenantName
	 * @param selectedMailTemplateSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.FileSendInputForm createStatementFileSendInputForm(Long statementSeq, AccgDocSendType sendType, Long loginAccountSeq, String tenantName, Long selectedMailTemplateSeq) throws AppException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		var statementFileSendInputForm = new AccgInvoiceStatementInputForm.FileSendInputForm();
		statementFileSendInputForm.setAccgDocSeq(tAccgStatementEntity.getAccgDocSeq());
		statementFileSendInputForm.setSendType(sendType.getCd());

		// 表示用プロパティの設定
		setDispProperties(statementFileSendInputForm);

		// 送付先の初期値を設定
		String yusenMailAddress = commonPersonService.getYusenMailAddress(tAccgStatementEntity.getRefundToPersonId());
		statementFileSendInputForm.setSendTo(yusenMailAddress);

		// 送付元名の初期値を設定
		statementFileSendInputForm.setSendFromName(tAccgStatementEntity.getStatementFromTenantName());

		// 署名を設定
		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(loginAccountSeq);
		statementFileSendInputForm.setMailSignature(mAccountEntity.getAccountMailSignature());

		if (sendType == AccgDocSendType.WEB) {
			// パスワード設定の規定フラグを設定
			MMailSettingEntity mMailSettingEntity = mMailSettingDao.select();
			statementFileSendInputForm.setDownloadViewPasswordEnabled(SystemFlg.codeToBoolean(mMailSettingEntity.getDownloadViewPasswordEnableFlg()));
		}

		// 規定データの設定
		MMailTemplateEntity mMailTemplateEntity;
		if (selectedMailTemplateSeq == null) {
			// デフォルトデータを取得
			mMailTemplateEntity = mMailTemplateDao.selectDefaultUseMailTemplateByMailTemplateType(MailTemplateType.STATEMENT);
		} else {
			// 選択したデータを設定
			mMailTemplateEntity = mMailTemplateDao.selectBySeq(selectedMailTemplateSeq);
		}

		if (mMailTemplateEntity != null) {
			statementFileSendInputForm.setTemplateSeq(mMailTemplateEntity.getMailTemplateSeq());
			statementFileSendInputForm.setSendCc(mMailTemplateEntity.getMailCc());
			statementFileSendInputForm.setSendBcc(mMailTemplateEntity.getMailBcc());
			statementFileSendInputForm.setReplyTo(mMailTemplateEntity.getMailReplyTo());
			statementFileSendInputForm.setSendSubject(mMailTemplateEntity.getSubject());
			statementFileSendInputForm.setSendBody(
					commonAccgInvoiceStatementService.generateDefaultInputSendBody(
							tenantName,
							tAccgStatementEntity.getStatementToName(),
							tAccgStatementEntity.getStatementToNameEnd(),
							mMailTemplateEntity.getContents()));
		} else {
			statementFileSendInputForm.setSendBody(
					commonAccgInvoiceStatementService.generateDefaultInputSendBody(
							tenantName,
							tAccgStatementEntity.getStatementToName(),
							tAccgStatementEntity.getStatementToNameEnd(),
							""));
		}

		return statementFileSendInputForm;
	}

	/**
	 * 請求書画面：会計書類送付入力フォームオブジェクトの表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.FileSendInputForm inputForm) {
		// 表示用オブジェクトの設定
		inputForm.setAccgDocType(AccgDocType.STATEMENT);

		// 送信元メールアドレスを取得
		// ※ 保存処理によって送信されるメールはM0013以外の場合も考えられるが、
		// 利用される送信元メールアドレスはすべて同じになる想定であるため、すべてM0013に登録されているメールアドレスを設定する
		// 今後、保存処理内で送信するメールの送信元メールアドレスに条件分岐等で異なる場合は修正が必要となる
		MMailEntity mMailEntity = mgtMMailDao.selectByMailId(MailIdList.MAIL_13.getCd());
		inputForm.setSendFrom(mMailEntity.getSendFrom());

		// テンプレートオプションを設定
		List<MMailTemplateEntity> mMailTemplateEntities = mMailTemplateDao.selectByTemplateType(MailTemplateType.STATEMENT.getCd());
		List<SelectOptionForm> templateOptions = mMailTemplateEntities.stream().map(e -> new SelectOptionForm(e.getMailTemplateSeq(), e.getTemplateTitle())).collect(Collectors.toList());
		inputForm.setTemplateOptions(templateOptions);
	}

	/**
	 * 送付モーダル：プレビュー表示の表示用プロパティを設定する
	 * 
	 * @param inputForm
	 */
	public void setAccgDocFileSendPreviewDispProperties(AccgInvoiceStatementInputForm.FileSendInputForm inputForm) {

		StringBuilder dispMailSendBody = new StringBuilder("");

		if (!StringUtils.isEmpty(inputForm.getSendBody())) {
			// メール本文
			dispMailSendBody.append(inputForm.getSendBody());
			dispMailSendBody.append(CommonConstant.CRLF_CODE);
			dispMailSendBody.append(CommonConstant.CRLF_CODE);
		}

		if (AccgDocSendType.WEB.equalsByCode(inputForm.getSendType())) {
			// WEB共有の場合のみ追加文言
			LocalDate downloadLimitDate = commonAccgService.getAccgWebShareDownloadLimitDate();

			// パスワード設定時のみ追加文言を設定(パスワードを設定しない場合は空文字)
			String passwordMsg = inputForm.isDownloadViewPasswordEnabled() ? AccgConstant.ACCG_INVOICE_FILE_SEND_WEB_SHARED_PASSWORD_MSG : "";

			String webSharedAddMsg = String.format(
					AccgConstant.ACCG_INVOICE_FILE_SEND_WEB_SHARED_ADD_MSG,
					AccgDocType.STATEMENT.getVal(),
					passwordMsg,
					"https://loioz.jp/" + UrlConstant.G_DOWNLOAD_AUTH_URL + "/...（URLを自動で生成いたします）",
					DateUtils.parseToString(downloadLimitDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			dispMailSendBody.append(webSharedAddMsg);
			dispMailSendBody.append(CommonConstant.CRLF_CODE);
			dispMailSendBody.append(CommonConstant.CRLF_CODE);
		}

		if (!StringUtils.isEmpty(inputForm.getMailSignature())) {
			// 著名
			dispMailSendBody.append(inputForm.getMailSignature());
		}

		inputForm.setDispMailSendBody(dispMailSendBody.toString());
	}

	/**
	 * 精算書画面：送付処理
	 *
	 * @param tenantSeq
	 * @param loginAccountSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void accgStatementFileSend(Long tenantSeq, Long loginAccountSeq, AccgInvoiceStatementInputForm.FileSendInputForm inputForm) throws AppException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(inputForm.getAccgDocSeq());
		if (tAccgStatementEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 精算書SEQ
		Long statementSeq = tAccgStatementEntity.getStatementSeq();
		tAccgStatementEntity.setStatementIssueStatus(IssueStatus.SENT.getCd());

		// 精算書発行ステータスの更新
		this.updateAccgStatement(tAccgStatementEntity);

		// 会計書類-対応データの登録処理
		TAccgDocActEntity tAccgDocActEntity = commonAccgService.registAccgDocAct(inputForm.getAccgDocSeq(), AccgDocActType.SEND);
		Long accgDocActSeq = tAccgDocActEntity.getAccgDocActSeq();

		// 会計書類-対応-送付データの登録処理
		Long accgDocActSendSeq = commonAccgInvoiceStatementService.registAccgDocActSendRecord(accgDocActSeq, inputForm);

		// 精算書：会計書類-対応-送付-ファイルの登録処理
		this.registStatementAccgDocActSendFile(statementSeq, accgDocActSendSeq);

		AccgDocSendType accgDocSendType = AccgDocSendType.of(inputForm.getSendType());
		if (AccgDocSendType.WEB == accgDocSendType) {

			// WEB共通情報の登録
			Long webDownloadSeq = commonAccgService.registAccgDocWebDownload(accgDocActSendSeq,
					tAccgStatementEntity.getStatementFromTenantName(), tAccgStatementEntity.getStatementDate(),
					inputForm.getPassword());

			// WEB共通メール送信
			if (!StringUtils.isEmpty(inputForm.getPassword())) {
				// メール送信(送信先：顧客 URLとパスワード)
				commonAccgService.sendMail4M0013AndM0014(accgDocActSendSeq, inputForm.getPassword(), AccgDocType.STATEMENT, tenantSeq, webDownloadSeq, inputForm.getMailSignature());
			} else {
				// メール送信(送信先：顧客 URL)
				commonAccgService.sendMail4M0013(AccgDocType.STATEMENT, tenantSeq, accgDocActSendSeq, webDownloadSeq, inputForm.getMailSignature());
			}


		} else if (AccgDocSendType.MAIL == accgDocSendType) {
			// メール送信処理

			// メール送信(送信先：顧客ファイル添付)
			commonAccgService.sendMail4M0015(AccgDocType.STATEMENT, tenantSeq, accgDocActSendSeq,
					inputForm.getMailSignature(), inputForm.getAccgDocSeq());

		} else {
			// 想定外のEnum値
			throw new RuntimeException("Eunm値が不正です");
		}

	}

	/**
	 * 精算書詳細：印刷して送信モーダルの画面オブジェクトを取得する
	 * 
	 * @param statementSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.FilePrintSendViewForm getFilePrintSendViewForm(Long statementSeq) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByStatementSeq(statementSeq);
		if (tAccgStatementEntity == null) {
			throw new DataNotFoundException("精算書情報が存在しません。[invoiceSeq=" + statementSeq + "]");
		}

		return commonAccgInvoiceStatementService.getFilePrintSendViewForm(tAccgStatementEntity.getAccgDocSeq());
	}

	/**
	 * 精算書SEQをキーとして、現在対象のPDFのダウンロード情報を設定する
	 * 
	 * @param statementSeq
	 * @param response
	 * @throws AppException
	 * @throws Exception
	 */
	public void printDownload(Long statementSeq, HttpServletResponse response) throws AppException, Exception {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByStatementSeq(statementSeq);
		if (tAccgStatementEntity == null || !IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// データが取得できない || 未発行データの場合 -> 排他エラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		this.printDownload(tAccgStatementEntity, response);
	}

	/**
	 * 現在対象のPDFのダウンロード情報を設定する
	 * 
	 * @param tAccgStatementEntity
	 * @param response
	 * @throws AppException
	 * @throws Exception
	 */
	private void printDownload(TAccgStatementEntity tAccgStatementEntity, HttpServletResponse response)
			throws AppException, Exception {

		FileContentsDto fileContents = this.donwloadStatementFileZip(tAccgStatementEntity);
		URLCodec codec = new URLCodec("UTF-8");
		String fileName = codec.encode(this.createAccgDocZipFileName(tAccgStatementEntity.getAccgDocSeq(), AccgDocType.STATEMENT,
				FileExtension.ofExtension(fileContents.getExtension())));
		try (InputStream is = new ByteArrayInputStream(fileContents.getByteArray())) {
			// レスポンス情報の書き込み
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + "\"");
			response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT,
					CommonConstant.HEADER_VALUE_OF_AJAX_PROC_RESULT_SUCCESS);

			// ファイルオブジェクトの書き込み
			IOUtils.copy(is, response.getOutputStream());
		} catch (Exception ex) {
			// 想定外のエラー
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 精算書の現在対象となっているPDFをZip形式で取得する
	 * 
	 * @param tAccgStatementEntity
	 * @return
	 */
	private FileContentsDto donwloadStatementFileZip(TAccgStatementEntity tAccgStatementEntity) {

		List<AccgDocFileBean> beanList = new ArrayList<>(2);

		// 精算書PDFの取得
		AccgDocFileBean targetStatementFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgStatementEntity.getAccgDocSeq(), AccgDocFileType.STATEMENT);
		beanList.add(targetStatementFileBean);

		// 実費明細PDFの取得
		if (SystemFlg.codeToBoolean(tAccgStatementEntity.getDepositDetailAttachFlg())) {
			AccgDocFileBean targetDepositRecordFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgStatementEntity.getAccgDocSeq(), AccgDocFileType.DEPOSIT_DETAIL);
			beanList.add(targetDepositRecordFileBean);
		}

		return commonAccgInvoiceStatementService.toZipFileContents(beanList);
	}

	/**
	 * 精算書を送付済みにする
	 * 
	 * @param tAccgStatementEntity
	 * @throws AppException
	 */
	private void changeToSend(TAccgStatementEntity tAccgStatementEntity) throws AppException {

		// 精算書SEQ
		tAccgStatementEntity.setStatementIssueStatus(IssueStatus.SENT.getCd());

		// 精算書発行ステータスの更新
		this.updateAccgStatement(tAccgStatementEntity);

		// 会計書類-対応データの登録処理
		TAccgDocActEntity tAccgDocActEntity = commonAccgService.registAccgDocAct(tAccgStatementEntity.getAccgDocSeq(), AccgDocActType.SEND);
		Long accgDocActSeq = tAccgDocActEntity.getAccgDocActSeq();

		// 会計書類-対応-送付データの登録処理
		Long accgDocActSendSeq = commonAccgInvoiceStatementService.registAccgDocActSendForChangeToSend(accgDocActSeq);

		// 精算書：会計書類-対応-送付-ファイルの登録処理
		this.registStatementAccgDocActSendFile(tAccgStatementEntity.getStatementSeq(), accgDocActSendSeq);
	}

	/**
	 * 送付ファイルをダウンロードして、送付済みにする
	 * 
	 * @param statementSeq
	 * @param response
	 * @throws AppException
	 * @throws Exception
	 */
	public void downloadAndChangeToSend(Long statementSeq, HttpServletResponse response) throws AppException, Exception {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByStatementSeq(statementSeq);
		if (tAccgStatementEntity == null || !IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// データが取得できない || 未発行データの場合 -> 排他エラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		this.changeToSend(tAccgStatementEntity);
		this.printDownload(tAccgStatementEntity, response);
	}

	/**
	 * 精算書詳細の設定情報表示用を取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.SettingViewForm getSettingViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(accgDocSeq);

		// 精算書詳細の設定情報表示用オブジェクトの取得
		return this.getSettingViewForm(bean);
	}

	/**
	 * 精算書詳細情報入力用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.SettingInputForm getSettingInputForm(Long statementSeq) throws AppException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		var inputForm = new AccgInvoiceStatementInputForm.SettingInputForm();

		// 入力項目の設定
		inputForm.setSalesDate(DateUtils.parseToString(tAccgStatementEntity.getSalesDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setSalesAccount(tAccgStatementEntity.getSalesAccountSeq());
		inputForm.setDepositDetailAttachFlg(SystemFlg.codeToBoolean(tAccgStatementEntity.getDepositDetailAttachFlg()));

		// 請求書項目のため未設定
		inputForm.setInvoiceType(null);
		inputForm.setPaymentPlanAttachFlg(false);

		// 表示用プロパティの設定
		setDispProperties(statementSeq, inputForm);

		return inputForm;
	}

	/**
	 * 精算書詳細情報入力用オブジェクトの表示用プロパティを設定する
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void setDispProperties(Long statementSeq, AccgInvoiceStatementInputForm.SettingInputForm inputForm) throws AppException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		inputForm.setAccgDocType(AccgDocType.STATEMENT);
		inputForm.setIssueStatus(tAccgStatementEntity.getStatementIssueStatus());
		inputForm.setSalesAccountList(commonAccgInvoiceStatementService.generateAccgSalesOwnerOptionForm(tAccgStatementEntity.getAccgDocSeq()));

	}

	/**
	 * 精算書詳細の設定情報を保存処理
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveSetting(Long statementSeq, AccgInvoiceStatementInputForm.SettingInputForm inputForm) throws AppException {

		// 精算書情報を取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		if (!commonAccgInvoiceStatementService.isSalesAccountValid(inputForm.getSalesAccount(), tAccgStatementEntity.getAccgDocSeq())) {
			// 売上計上先ユーザーの整合性チェック
			// 無効の場合は排他エラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力値の設定
		tAccgStatementEntity.setSalesDate(DateUtils.parseToLocalDate(inputForm.getSalesDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgStatementEntity.setSalesAccountSeq(inputForm.getSalesAccount());

		if (!IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 未発行の場合のみ変更可能な値を設定
			tAccgStatementEntity.setDepositDetailAttachFlg(SystemFlg.booleanToCode(inputForm.isDepositDetailAttachFlg()));
		}

		// 更新処理
		this.updateAccgStatement(tAccgStatementEntity);

	}

	/**
	 * 精算書詳細の内部メモ情報表示用を取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.MemoViewForm getMemoViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(accgDocSeq);

		// 精算書詳細の設定情報表示用オブジェクトの取得
		return this.getMemoViewForm(bean);
	}

	/**
	 * 精算書詳細内部メモ入力用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.MemoInputForm getMemoInputForm(Long statementSeq) throws AppException {

		var inputForm = new AccgInvoiceStatementInputForm.MemoInputForm();

		// 表示用プロパティの設定
		setDispProperties(statementSeq, inputForm);

		return inputForm;
	}

	/**
	 * 精算書内部メモ情報入力用オブジェクトの表示用プロパティを設定する
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void setDispProperties(Long statementSeq, AccgInvoiceStatementInputForm.MemoInputForm inputForm) throws AppException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		inputForm.setMemo(tAccgStatementEntity.getStatementMemo());
	}

	/**
	 * 精算書詳細のメモ情報を保存処理
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveMemo(Long statementSeq, AccgInvoiceStatementInputForm.MemoInputForm inputForm) throws AppException {

		// 精算書情報を取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		// 入力値の設定
		tAccgStatementEntity.setStatementMemo(inputForm.getMemo());

		// 更新処理
		this.updateAccgStatement(tAccgStatementEntity);

	}

	/**
	 * 精算書詳細のタブ情報を取得
	 * 
	 * @param statementSeq
	 * @param selectedTab (Nullable)
	 * @return
	 */
	public AccgInvoiceStatementViewForm.DocContentsForm getDocContentsForm(Long statementSeq, @Nullable AccggInvoiceStatementDetailViewTab selectedTab) throws DataNotFoundException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(accgDocSeq);

		// タブ情報表示用オブジェクトを取得
		return this.getDocContentsForm(bean, selectedTab);
	}

	/**
	 * 精算書：基本情報_タイトル情報表示用を取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.BaseTitleViewForm getBaseTitleViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(accgDocSeq);

		// 基本情報_タイトル情報表示用オブジェクトの作成
		return this.getBaseTitleViewForm(bean);
	}

	/**
	 * 精算書_基本情報_タイトル情報入力用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.BaseTitleInputForm getBaseTitleInputForm(Long statementSeq) throws AppException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		// 入力用オブジェクトの作成・設定
		AccgInvoiceStatementInputForm.BaseTitleInputForm inputForm = new AccgInvoiceStatementInputForm.BaseTitleInputForm();
		inputForm.setBaseTitle(tAccgStatementEntity.getStatementTitle());
		inputForm.setBaseDate(DateUtils.parseToString(tAccgStatementEntity.getStatementDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setBaseNo(tAccgStatementEntity.getStatementNo());

		// 表示用プロパティの設定
		setDispProperties(inputForm);

		return inputForm;
	}

	/**
	 * 精算書_基本情報_タイトル情報入力用オブジェクトの表示用プロパティを設定せる
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.BaseTitleInputForm inputForm) {

		// 会計書類種別の設定
		inputForm.setAccgDocType(AccgDocType.STATEMENT);
	}

	/**
	 * 精算書：基本情報_タイトル情報の保存処理
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveBaseTitle(Long statementSeq, AccgInvoiceStatementInputForm.BaseTitleInputForm inputForm) throws AppException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合
			throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
		}

		// 入力データの設定
		tAccgStatementEntity.setStatementTitle(inputForm.getBaseTitle());
		tAccgStatementEntity.setStatementDate(DateUtils.parseToLocalDate(inputForm.getBaseDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgStatementEntity.setStatementNo(inputForm.getBaseNo());

		// タイトル情報の更新処理
		this.updateAccgStatement(tAccgStatementEntity);

		// 基本情報更新後のPDF再作成フラグの更新
		this.baseInfoChangeAfterPdfRebuildUpdate(tAccgStatementEntity.getAccgDocSeq());

	}

	/**
	 * 精算書：基本情報_精算先情報表示用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.BaseToViewForm getBaseToViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得処理
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(accgDocSeq);

		// 基本情報_精算先情報オブジェクトの作成・設定
		return this.getBaseToViewForm(bean);
	}

	/**
	 * 精算書：基本情報_請求先情報入力用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.BaseToInputForm getBaseToInputForm(Long statementSeq) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		// 精算書データの取得
		StatementDetailBean bean;
		try {
			bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(tAccgStatementEntity.getAccgDocSeq());
		} catch (DataNotFoundException ex) {
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		var inputForm = new AccgInvoiceStatementInputForm.BaseToInputForm();
		// 登録されないが、パラメータ受け渡しが発生するパタメータ
		// 表示用プロパティだが、請求書側と作りを合わせる関係上ここで設定しておく
		inputForm.setAnkenId(bean.getAnkenId());
		inputForm.setAnkenName(bean.getAnkenName());
		inputForm.setBillToPersonId(bean.getRefundToPersonId());
		inputForm.setBillToPersonName(bean.getRefundPersonName().getName());

		// 登録されるプロパティ
		inputForm.setChangeBillToPersonId(tAccgStatementEntity.getRefundToPersonId());
		inputForm.setBaseToDetail(tAccgStatementEntity.getStatementToDetail());
		inputForm.setBaseToName(tAccgStatementEntity.getStatementToName());
		inputForm.setBaseToNameEnd(tAccgStatementEntity.getStatementToNameEnd());

		// 表示用プロパティの設定
		setDispProperties(inputForm);

		return inputForm;
	}

	/**
	 * 名簿IDに紐づく請求先の名称、敬称、詳細情報を取得します
	 * 
	 * @param personId
	 * @return
	 */
	public BaseToInputForm getBaseToNameAndDetail(Long personId) {
		BaseToInputForm form = commonAccgInvoiceStatementService.getBaseToNameAndDetail(personId);
		return form;
	}

	/**
	 * 精算書：基本情報_精算先情報入力用オブジェクトの表示用プロパティを設定
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.BaseToInputForm inputForm) throws AppException {

		// 顧客関与者プルダウンの設定
		List<CustomerKanyoshaPulldownDto> customerKanyoshaList = tCustomerCommonDao.selectCustomerKanyoshaPulldownByAnkenId(inputForm.getAnkenId());
		inputForm.setCustomerKanyoshaList(customerKanyoshaList);
	}

	/**
	 * 精算書：基本情報_精算先情報の保存処理<br>
	 * 精算先の保存と同時に「振込先」を返金先名簿IDの口座情報に変更します。
	 * 
	 * @param statementSeq
	 * @param baseToInputForm
	 * @throws AppException
	 */
	public void saveBaseTo(Long statementSeq, AccgInvoiceStatementInputForm.BaseToInputForm baseToInputForm) throws AppException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合
			throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
		}

		// 入力値の設定
		tAccgStatementEntity.setStatementToDetail(baseToInputForm.getBaseToDetail());
		tAccgStatementEntity.setStatementToName(baseToInputForm.getBaseToName());
		tAccgStatementEntity.setStatementToNameEnd(baseToInputForm.getBaseToNameEnd());
		Long changeBillToPersonId = baseToInputForm.getChangeBillToPersonId();
		
		// 請求先が変更されていたら、返金先の名簿IDと口座情報を変更する
		if (changeBillToPersonId != null && !baseToInputForm.getBillToPersonId().equals(changeBillToPersonId)) {
			if (!commonAccgService.checkAnkenIsCustomerKanyosha(changeBillToPersonId, baseToInputForm.getAnkenId())) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			tAccgStatementEntity.setRefundToPersonId(changeBillToPersonId);
			
			// 名簿情報取得
			TPersonEntity tPersonEntity = tPersonDao.selectPersonByPersonId(changeBillToPersonId);
			if (tPersonEntity == null) {
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 返金先-銀行口座
			String refundBankDetail = commonAccgService.bankToDetail(tPersonEntity.getGinkoName(), tPersonEntity.getShitenName(),
					tPersonEntity.getShitenNo(), tPersonEntity.getKozaType(), tPersonEntity.getKozaNo(), tPersonEntity.getKozaName());
			tAccgStatementEntity.setRefundBankDetail(refundBankDetail);
		}

		// 更新処理
		this.updateAccgStatement(tAccgStatementEntity);

		// 基本情報更新後のPDF再作成フラグの更新
		this.baseInfoChangeAfterPdfRebuildUpdate(tAccgStatementEntity.getAccgDocSeq());

	}

	/**
	 * 精算書：基本情報_精算元情報入力用オブジェクトの表示用プロパティを設定
	 * 
	 * @param statementSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.BaseFromViewForm getBaseFromViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(tAccgStatementEntity.getAccgDocSeq());

		// 基本情報_精算元情報オブジェクトの作成・設定
		return this.getBaseFromViewForm(bean);
	}

	/**
	 * 基本情報_請求元情報入力用を取得します
	 * 
	 * @param statementSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.BaseFromInputForm getBaseFromInputForm(Long statementSeq) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		AccgInvoiceStatementInputForm.BaseFromInputForm inputForm = new AccgInvoiceStatementInputForm.BaseFromInputForm();
		inputForm.setBaseFromDetail(tAccgStatementEntity.getStatementFromDetail());
		inputForm.setBaseFromOfficeName(tAccgStatementEntity.getStatementFromTenantName());
		inputForm.setTenantStampPrintFlg(SystemFlg.codeToBoolean(tAccgStatementEntity.getTenantStampPrintFlg()));

		return inputForm;
	}

	/**
	 * 精算書：基本情報_精算元情報の保存処理
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveBaseFrom(Long statementSeq, AccgInvoiceStatementInputForm.BaseFromInputForm inputForm) throws AppException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合
			throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
		}

		// 入力値の設定
		tAccgStatementEntity.setStatementFromTenantName(inputForm.getBaseFromOfficeName());
		tAccgStatementEntity.setStatementFromDetail(inputForm.getBaseFromDetail());
		tAccgStatementEntity.setTenantStampPrintFlg(SystemFlg.booleanToCode(inputForm.isTenantStampPrintFlg()));

		// 更新処理
		this.updateAccgStatement(tAccgStatementEntity);

		// 基本情報更新後のPDF再作成フラグの更新
		this.baseInfoChangeAfterPdfRebuildUpdate(tAccgStatementEntity.getAccgDocSeq());

	}

	/**
	 * 精算書：基本情報_挿入文情報表示用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.BaseOtherViewForm getBaseOtherViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(tAccgStatementEntity.getAccgDocSeq());

		// 基本情報_精算元情報オブジェクトの作成・設定
		return this.getBaseOtherViewForm(bean);
	}

	/**
	 * 精算書：基本情報_挿入文情報入力用オブジェクトを取得
	 *
	 * @param statementSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.BaseOtherInputForm getBaseOtherInputForm(Long statementSeq) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		// 挿入文情報入力用オブジェクトの作成・設定
		AccgInvoiceStatementInputForm.BaseOtherInputForm inputForm = new AccgInvoiceStatementInputForm.BaseOtherInputForm();
		inputForm.setDeadline(DateUtils.parseToString(tAccgStatementEntity.getRefundDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setSubText(tAccgStatementEntity.getStatementSubText());
		inputForm.setTitle(tAccgStatementEntity.getStatementSubject());
		inputForm.setDeadLinePrintFlg(SystemFlg.codeToBoolean(tAccgStatementEntity.getRefundDatePrintFlg()));

		setDispProperties(inputForm);
		return inputForm;
	}

	/**
	 * 基本情報_挿入文情報入力用オブジェクトに表示用オブジェクトを設定する
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.BaseOtherInputForm inputForm) {

		// 会計書類種別を設定
		inputForm.setAccgDocType(AccgDocType.STATEMENT);
	}

	/**
	 * 基本情報_挿入文情報の保存処理
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveBaseOther(Long statementSeq, AccgInvoiceStatementInputForm.BaseOtherInputForm inputForm) throws AppException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合
			throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
		}

		// 入力値の設定
		tAccgStatementEntity.setRefundDate(DateUtils.parseToLocalDate(inputForm.getDeadline(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgStatementEntity.setStatementSubText(inputForm.getSubText());
		tAccgStatementEntity.setStatementSubject(inputForm.getTitle());
		tAccgStatementEntity.setRefundDatePrintFlg(SystemFlg.booleanToCode(inputForm.isDeadLinePrintFlg()));

		// 更新処理
		this.updateAccgStatement(tAccgStatementEntity);

		// 基本情報更新後のPDF再作成フラグの更新
		this.baseInfoChangeAfterPdfRebuildUpdate(tAccgStatementEntity.getAccgDocSeq());

	}

	/**
	 * 請求書詳細画面、精算書詳細画面の既入金項目表示用情報を取得します。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.RepayViewForm getRepayViewForm(Long accgDocSeq) {
		AccgInvoiceStatementViewForm.RepayViewForm repayViewForm = commonAccgInvoiceStatementService.getRepayViewForm(accgDocSeq);
		return repayViewForm;
	}

	/**
	 * 既入金項目情報入力用オブジェクトを取得
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.RepayInputForm getRepayInputForm(Long accgDocSeq) throws AppException {
		try {
			// 共通既入金入力フォームオブジェクトの取得
			return commonAccgInvoiceStatementService.getRepayInputForm(accgDocSeq);
		} catch (DataNotFoundException ex) {
			// データ取得時のエラー
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * パラメータで受け取った預り金SEQの預り金情報を既入金項目情報入力用に追加します。<br>
	 * 会計書類の既入金項目合算フラグがONであれば預り金情報を1つにまとめリストにセットして返します。<br>
	 * 会計書類の既入金項目合算フラグがOFFであれば預り金情報を個々にリストにセットして返します。<br>
	 * 
	 * @param repayInputForm 既入金項目情報入力用
	 * @param depositRecvSeqList 預り金SEQリスト
	 * @return
	 * @throws AppException
	 */
	public List<RepayRowInputForm> addNyukinDepositToRepayInput(
			AccgInvoiceStatementInputForm.RepayInputForm repayInputForm, List<Long> depositRecvSeqList)
			throws AppException {
		return commonAccgInvoiceStatementService.addNyukinDepositToRepayInput(repayInputForm, depositRecvSeqList);
	}

	/**
	 * 既入金項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 
	 * @param repayInputForm
	 * @return
	 */
	public List<RepayRowInputForm> groupOrUngroupSimilarRepayItems(
			AccgInvoiceStatementInputForm.RepayInputForm repayInputForm) {
		return commonAccgInvoiceStatementService.groupOrUngroupSimilarRepayItems(repayInputForm);
	}

	/**
	 * 既入金項目リストの有効行だけを取得します。<br>
	 * 
	 * @param repayRowList
	 * @return
	 */
	public List<RepayRowInputForm> getEnabledRepayList(List<RepayRowInputForm> repayRowList) {
		return commonAccgInvoiceStatementService.getEnabledRepayList(repayRowList);
	}

	/**
	 * 既入金項目の一覧をdocRepayOrder昇順に並び替えます。
	 * 
	 * @param repayRowList
	 * @return
	 */
	public List<RepayRowInputForm> sortRepayList(List<RepayRowInputForm> repayRowList) {
		return commonAccgInvoiceStatementService.sortRepayList(repayRowList);
	}

	/**
	 * 請求項目リストの有効行だけを取得します。<br>
	 * 
	 * @param invoiceRowList
	 * @return
	 */
	public List<InvoiceRowInputForm> getEnabledInvoiceList(List<InvoiceRowInputForm> invoiceRowList) {
		return commonAccgInvoiceStatementService.getEnabledInvoiceList(invoiceRowList);
	}

	/**
	 * 請求項目の一覧をdocInvoiceOrder昇順に並び替えます。
	 * 
	 * @param invoiceRowList
	 * @return
	 */
	public List<InvoiceRowInputForm> sortInvoiceList(List<InvoiceRowInputForm> invoiceRowList) {
		return commonAccgInvoiceStatementService.sortInvoiceList(invoiceRowList);
	}

	/**
	 * invoiceInputFormの単価と時間から報酬額を算出しinvoiceInputFormにセットします。
	 * 
	 * @param invoiceInputForm
	 * @throws AppException
	 */
	public void calculateTimeCharge(AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) throws AppException {
		commonAccgInvoiceStatementService.calculateTimeCharge(invoiceInputForm);
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

	/**
	 * 精算書詳細_既入金情報を保存処理
	 * 
	 * @param statementSeq
	 * @param repayInputForm
	 * @throws AppException
	 */
	public void saveRepay(Long statementSeq, AccgInvoiceStatementInputForm.RepayInputForm repayInputForm) throws AppException {
		// 既入金情報を保存
		commonAccgInvoiceStatementService.saveRepay(repayInputForm);
	}

	/**
	 * 精算書詳細_請求情報を保存処理
	 * 
	 * @param statementSeq
	 * @param invoiceInputForm
	 * @throws AppException
	 */
	public void saveInvoice(Long statementSeq, AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) throws AppException {
		// 請求項目情報を保存
		commonAccgInvoiceStatementService.saveInvoice(invoiceInputForm);
	}
	
	/**
	 * 精算書：請求項目情報表示用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.InvoiceViewForm getInvoiceViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		return commonAccgInvoiceStatementService.getInvoiceViewForm(tAccgStatementEntity.getAccgDocSeq());
	}

	/**
	 * 精算書：請求項目情報入力用オブジェクトを取得
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.InvoiceInputForm getInvoiceInputForm(Long accgDocSeq) throws AppException {
		try {
			// 共通処理から入力用オブジェクトを取得
			return commonAccgInvoiceStatementService.getInvoiceInputForm(accgDocSeq);

		} catch (DataNotFoundException ex) {
			// データ取得エラーは楽観ロックエラーとして処理する
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 請求項目情報合計額入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowInputFormList
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm getInvoiceTotalAmountInputForm(Long accgDocSeq, List<InvoiceRowInputForm> invoiceRowInputFormList) {
		AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm form = commonAccgInvoiceStatementService.getInvoiceTotalAmountInputForm(accgDocSeq, invoiceRowInputFormList);
		return form;
	}

	/**
	 * 新規追加用の請求項目-報酬のInputFormを作成する
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewFeeRowInputForm(Long accgDocSeq,
			Long invoiceRowCount) {
		return commonAccgInvoiceStatementService.createNewFeeRowInputForm(accgDocSeq, invoiceRowCount);
	}

	/**
	 * 報酬SEQの請求項目情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @param unPaidFeeSeqList
	 * @return
	 */
	public List<InvoiceRowInputForm> createNewUnPaidFeeRowInputForm(Long accgDocSeq,
			Long invoiceRowCount, List<Long> unPaidFeeSeqList) {
		return commonAccgInvoiceStatementService.createNewUnPaidFeeRowInputForm(accgDocSeq, invoiceRowCount, unPaidFeeSeqList);
	}
	
	/**
	 * 請求項目入力の預り金用Formを新規作成します。
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewDepositRecvRowInputForm(Long accgDocSeq, Long invoiceRowCount) {
		return commonAccgInvoiceStatementService.createNewDepositRecvRowInputForm(accgDocSeq, invoiceRowCount);
	}
	
	/**
	 * パラメータで受け取った預り金SEQの預り金情報を請求項目情報入力用に追加します。<br>
	 * 会計書類の請求項目合算フラグがONであれば預り金情報を1つにまとめリストにセットして返します。<br>
	 * 会計書類の請求項目合算フラグがOFFであれば預り金情報を個々にリストにセットして返します。<br>
	 * 
	 * @param invoiceInputForm 請求項目情報入力用
	 * @param depositRecvSeqList 預り金SEQリスト
	 * @return
	 * @throws AppException
	 */
	public List<InvoiceRowInputForm> addShukkinDepositToInvoiceInput(
			AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm, List<Long> depositRecvSeqList)
			throws AppException {
		return commonAccgInvoiceStatementService.addShukkinDepositToInvoiceInput(invoiceInputForm, depositRecvSeqList);
	}

	/**
	 * 新規追加用の請求項目-値引きのInputFormを作成する
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewDiscountRowInputForm(Long accgDocSeq, Long invoiceRowCount) {
		return commonAccgInvoiceStatementService.createNewDiscountRowInputForm(accgDocSeq, invoiceRowCount);
	}

	/**
	 * 新規追加用の請求項目-テキストのInputFormを作成する
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewTextRowInputForm(Long accgDocSeq, Long invoiceRowCount) {
		return commonAccgInvoiceStatementService.createNewTextRowInputForm(accgDocSeq, invoiceRowCount);
	}

	/**
	 * 請求項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 
	 * @param invoiceInputForm
	 * @return
	 */
	public List<InvoiceRowInputForm> groupOrUngroupSimilarInvoiceItems(
			AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) {
		return commonAccgInvoiceStatementService.groupOrUngroupSimilarInvoiceItems(invoiceInputForm);
	}

	/**
	 * 報酬項目の候補データを取得します
	 * 
	 * @param searchWord
	 * @return
	 */
	public List<SelectOptionForm> searchFeeDataList(String searchWord) {
		return commonAccgService.searchFeeDataList(searchWord);
	}

	/**
	 * 預り金項目の候補データを取得します
	 * 
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	public List<SelectOptionForm> searchDepositRecvDataList(String searchWord, String depositType) {
		return commonAccgService.searchDepositRecvDataList(searchWord, depositType);
	}

	/**
	 * 精算書：振込先情報表示用オブジェクトを取得する
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.BankDetailViewForm getBankDetailViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(tAccgStatementEntity.getAccgDocSeq());

		// 基本情報_振込先情報表示用オブジェクトの作成・設定
		return this.getBankDetailViewForm(bean);
	}

	/**
	 * 精算書：振込先情報入力用オブジェクトを取得する
	 * 
	 * @param statementSeq
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.BankDetailInputForm getBankDetailInputForm(Long statementSeq) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		// 振込先情報入力用オブジェクトを作成する
		AccgInvoiceStatementInputForm.BankDetailInputForm inputForm = new AccgInvoiceStatementInputForm.BankDetailInputForm();
		inputForm.setTenantBankDetail(tAccgStatementEntity.getRefundBankDetail());

		return inputForm;
	}

	/**
	 * 精算書：振込先情報を保存処理
	 * 
	 * @param statementSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveBankDetail(Long statementSeq, AccgInvoiceStatementInputForm.BankDetailInputForm inputForm) throws AppException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合
			throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
		}

		// 入直値を設定
		tAccgStatementEntity.setRefundBankDetail(inputForm.getTenantBankDetail());

		// 更新処理
		this.updateAccgStatement(tAccgStatementEntity);

		// 基本情報更新後のPDF再作成フラグの更新
		this.baseInfoChangeAfterPdfRebuildUpdate(tAccgStatementEntity.getAccgDocSeq());

	}

	/**
	 * 精算書：備考情報表示用オブジェクトを取得する
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.RemarksViewForm getRemarksViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		// 精算書データの取得
		StatementDetailBean bean = this.getStatementDetailBeanIfNullThrowDataNotFoundException(tAccgStatementEntity.getAccgDocSeq());

		// 基本情報_振込先情報表示用オブジェクトの作成・設定
		return this.getRemarksViewForm(bean);
	}

	/**
	 * 精算書：備考情報入力用オブジェクトを取得する
	 * 
	 * @param statementSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.RemarksInputForm getRemarksInputForm(Long statementSeq) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		// 精算書：備考情報入力用オブジェクトの作成・設定
		AccgInvoiceStatementInputForm.RemarksInputForm inputForm = new AccgInvoiceStatementInputForm.RemarksInputForm();
		inputForm.setRemarks(tAccgStatementEntity.getStatementRemarks());

		return inputForm;
	}

	/**
	 * 精算書：備考情報を保存処理
	 * 
	 * @param statementSeq
	 * @param remarksInputForm
	 * @throws AppException
	 */
	public void saveRemarks(Long statementSeq, AccgInvoiceStatementInputForm.RemarksInputForm remarksInputForm) throws AppException {

		// 精算書データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);
		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合
			throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
		}

		// 入直値を設定
		tAccgStatementEntity.setStatementRemarks(remarksInputForm.getRemarks());

		// 更新処理
		this.updateAccgStatement(tAccgStatementEntity);

		// 基本情報で変更があった場合、会計書類ファイルの「精算書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(tAccgStatementEntity.getAccgDocSeq()), AccgDocFileType.STATEMENT);

	}
	
	/**
	 * 精算書の作成/再作成処理
	 * 
	 * <pre>
	 * 画面表示より前に別のトランザクションで行うことを想定
	 * 
	 * ・精算書画面_精算書タブの表示
	 * ・発行処理 など
	 * </pre>
	 *
	 * @param statementSeq
	 * @param tenantSeq
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 * @throws DataNotFoundException
	 */
	public void beforeTransactionalDocStatementPdfCreate(Long statementSeq, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException, DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		if (this.needCreateStatementPdf(tAccgStatementEntity)) {
			// PDFが未作成 || 再作成フラグが１のデータが存在する -> PDFの作成/再作成を行う
			commonAccgService.createAccgDocFileAndDetail(tAccgStatementEntity.getAccgDocSeq(), tenantSeq, true, deleteS3ObjectKeys, AccgDocFileType.STATEMENT);
		}
	}

	/**
	 * 精算書PDFの生成が必要か
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public boolean needCreateStatementPdf(Long statementSeq) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByStatementSeq(statementSeq);
		if (tAccgStatementEntity == null) {
			// 精算書データが存在しない場合
			throw new DataNotFoundException("精算書データが存在しません。");
		}

		return this.needCreateStatementPdf(tAccgStatementEntity);
	}

	/**
	 * 精算書PDFの生成が必要か
	 * 
	 * @param tAccgStatementEntity
	 * @return
	 */
	private boolean needCreateStatementPdf(TAccgStatementEntity tAccgStatementEntity) {

		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済の場合は、再作成は行わない
			return false;
		}

		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(tAccgStatementEntity.getAccgDocSeq(), AccgDocFileType.STATEMENT);

		// PDFファイルのみに絞り込み
		List<AccgDocFileBean> accgDocFilePdfBeans = accgDocFileBeans.stream().filter(e -> Objects.equals(e.getFileExtension(), FileExtension.PDF.getVal())).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(accgDocFilePdfBeans) || accgDocFileBeans.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getRecreateStandbyFlg()))) {
			// PDFが未作成 || 再作成フラグが１のデータが存在する -> PDFの作成/再作成が必要
			return true;
		}

		return false;
	}

	/**
	 * 精算書：精算書タブ内画面表示用オブジェクトを取得
	 *
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.DocStatementPdfViewForm getDocStatementPdfViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		var docStatementPdfViewForm = new AccgInvoiceStatementViewForm.DocStatementPdfViewForm();

		// 精算書画像HTML情報を取得
		List<String> pngHtmlImgSrcList = commonAccgInvoiceStatementService.getAccgPdfImgHtmlSrcList(accgDocSeq, AccgDocFileType.STATEMENT);

		docStatementPdfViewForm.setStatementSeq(statementSeq);
		docStatementPdfViewForm.setAccgDocSeq(accgDocSeq);
		docStatementPdfViewForm.setStatementPngSrc(pngHtmlImgSrcList);
		docStatementPdfViewForm.setCanReCreate(!IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus()));// 下書き時のみ再作成可能

		return docStatementPdfViewForm;
	}

	/**
	 * 精算書のPDFプレビュー表示
	 * 
	 * @param statementSeq
	 * @param response
	 */
	public void docStatementPdfPreview(Long statementSeq, HttpServletResponse response) {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();
		
		AccgDocFileBean accgDocFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(accgDocSeq, AccgDocFileType.STATEMENT);
		if (accgDocFileBean == null) {
			throw new DataNotFoundException("精算書PDFが存在しません");
		}
		
		// ファイル名を作成
		String fileName = commonAccgService.createAccgDocFileName(accgDocSeq,
				AccgDocFileType.of(accgDocFileBean.getAccgDocFileType()),
				FileExtension.ofExtension(accgDocFileBean.getFileExtension()));
		
		try (InputStream is = fileStorageService.fileDownload(accgDocFileBean.getS3ObjectKey()).getObjectContent();) {
			// PDFプレビュー画面表示の設定
			pdfService.setPreviewPdf(is, response, fileName);

		} catch (Exception ex) {
			// 出力処理に失敗
			throw new RuntimeException("精算書PDF取得に失敗しました", ex);
		}
	}

	/**
	 * 実費明細書の作成/再作成処理
	 * 
	 * <pre>
	 * 画面表示より前に別のトランザクションで行うことを想定
	 * 
	 * ・実費明細タブの表示
	 * ・発行処理 など
	 * </pre>
	 *
	 * @param statementSeq
	 * @param tenantSeq
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 */
	public void beforeTransactionalDipositRecordPdfCreate(Long statementSeq, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws DataNotFoundException, AppException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		if (this.needCreateDipositRecordPdf(tAccgStatementEntity)) {
			commonAccgService.createAccgDocFileAndDetail(tAccgStatementEntity.getAccgDocSeq(), tenantSeq, true, deleteS3ObjectKeys, AccgDocFileType.DEPOSIT_DETAIL);
		}
	}

	/**
	 * 実費明細PDFの生成が必要か
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public boolean needCreateDipositRecordPdf(Long statementSeq) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByStatementSeq(statementSeq);
		if (tAccgStatementEntity == null) {
			// 請求書データが存在しない場合
			throw new DataNotFoundException("精算書データが存在しません。");
		}

		return needCreateDipositRecordPdf(tAccgStatementEntity);
	}

	/**
	 * 実費明細PDFの生成が必要か
	 * 
	 * @param tAccgInvoiceEntity
	 * @return
	 */
	private boolean needCreateDipositRecordPdf(TAccgStatementEntity tAccgStatementEntity) {

		if (IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行済みの場合、再作成は行わない
			return false;
		}

		if (!SystemFlg.codeToBoolean(tAccgStatementEntity.getDepositDetailAttachFlg())) {
			// 実費明細添付しないなら作成しない
			return false;
		}

		// 会計書類ファイル情報を取得
		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(tAccgStatementEntity.getAccgDocSeq(), AccgDocFileType.DEPOSIT_DETAIL);

		// PDFファイルのみに絞り込み
		List<AccgDocFileBean> accgDocFilePdfBeans = accgDocFileBeans.stream().filter(e -> Objects.equals(e.getFileExtension(), FileExtension.PDF.getVal())).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(accgDocFilePdfBeans) || accgDocFileBeans.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getRecreateStandbyFlg()))) {
			// PDFが未作成 || 再作成フラグが１のデータが存在する -> PDFの作成/再作成が必要
			return true;
		}

		return false;
	}

	/**
	 * 精算書：実費明細タブ内画面表示用オブジェクトを取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.DipositRecordPdfViewForm getDipositRecordPdfViewForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();

		return commonAccgInvoiceStatementService.getDipositRecordPdfViewForm(accgDocSeq);
	}

	/**
	 * 実費明細のPDF表示
	 * 
	 * @param invoiceSeq
	 * @param response
	 */
	public void dipositRecordPdfPreview(Long statementSeq, HttpServletResponse response) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		Long accgDocSeq = tAccgStatementEntity.getAccgDocSeq();
		
		AccgDocFileBean accgDocFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(accgDocSeq, AccgDocFileType.DEPOSIT_DETAIL);
		if (accgDocFileBean == null) {
			throw new DataNotFoundException("実費明細書が存在しません");
		}
		
		// ファイル名を作成
		String fileName = commonAccgService.createAccgDocFileName(accgDocSeq,
				AccgDocFileType.of(accgDocFileBean.getAccgDocFileType()),
				FileExtension.ofExtension(accgDocFileBean.getFileExtension()));
		
		try (InputStream is = fileStorageService.fileDownload(accgDocFileBean.getS3ObjectKey()).getObjectContent();) {
			// PDFプレビュー画面表示の設定
			pdfService.setPreviewPdf(is, response, fileName);

		} catch (Exception ex) {
			// 出力処理に失敗
			throw new RuntimeException("実費明細書取得に失敗しました", ex);
		}
	}

	/**
	 * 取引状況情報表示用オブジェクトを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgDocSummaryForm getDocSummaryForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		return commonAccgService.getAccgDocSummaryForm(tAccgStatementEntity.getAccgDocSeq());
	}

	/**
	 * 進行状況情報表示用オブジェクトを取得する
	 * 
	 * @param statementSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.DocActivityForm getDocActivityForm(Long statementSeq) throws DataNotFoundException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);

		return commonAccgInvoiceStatementService.getDocActivityForm(tAccgStatementEntity.getAccgDocSeq());
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
			// 当メソッドによりは、削除処理のエラーは握り潰す
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + "S3オブジェクトの削除に失敗しました。DBに管理されていないS3オブジェクトが残っています", ex);
		}
	}

	// =========================================================================
	// データ取得 共通系メソッド
	// =========================================================================

	/**
	 * 精算書：基本情報の更新処理後に行うPDF情報の再作成フラグの更新処理
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void baseInfoChangeAfterPdfRebuildUpdate(Long accgDocSeq) throws AppException {

		// 基本情報で変更があった場合、会計書類ファイルの「精算書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.STATEMENT);

		// 基本情報で変更があった場合、会計書類ファイルの「実費明細」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.DEPOSIT_DETAIL);
	}

	/**
	 * 会計書類圧縮ファイル名を作成します
	 * 
	 * @param accgDocSeq
	 * @param accgDocType
	 * @param fileExtension
	 * @return
	 */
	private String createAccgDocZipFileName(Long accgDocSeq, AccgDocType accgDocType, FileExtension fileExtension) {
		return commonAccgService.createAccgDocZipFileName(accgDocSeq, accgDocType, fileExtension);
	}

	/**
	 * 精算書データの取得
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	private StatementDetailBean getStatementDetailBeanIfNullThrowDataNotFoundException(Long accgDocSeq) throws DataNotFoundException {
		StatementDetailBean bean = tAccgStatementDao.selectStatementDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			// 精算書情報が存在しない場合
			throw new DataNotFoundException("精算書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		return bean;
	}

	/**
	 * 精算書データの取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws DataNotFoundException データが見つからない場合
	 */
	private TAccgStatementEntity getAccgStatementEntityIfNullThrowDataNotFoundException(Long statementSeq) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByStatementSeq(statementSeq);
		if (tAccgStatementEntity == null) {
			throw new DataNotFoundException("statementSeq = 「" + statementSeq + "」のデータが見つかりません");
		}

		return tAccgStatementEntity;
	}

	/**
	 * 精算書データの取得
	 * 
	 * @param statementSeq
	 * @return
	 * @throws AppException データが見つからない場合(MSG_E00025)
	 */
	private TAccgStatementEntity getAccgStatementEntityIfNullThrowAppException(Long statementSeq) throws AppException {
		try {
			return getAccgStatementEntityIfNullThrowDataNotFoundException(statementSeq);
		} catch (DataNotFoundException ex) {
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 精算書詳細Beanから精算書詳細の案件用情報表示用オブジェクトを作成
	 * 
	 * @param bean 精算書詳細Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.AnkenForm getAnkenForm(StatementDetailBean bean) throws DataNotFoundException {

		AccgInvoiceStatementViewForm.AnkenForm viewForm = new AccgInvoiceStatementViewForm.AnkenForm();
		// 案件情報の設定
		viewForm.setAnkenId(bean.getAnkenId());
		viewForm.setAnkenName(bean.getAnkenName());
		viewForm.setAnkenStatus(AnkenStatus.of(bean.getAnkenStatus()));
		AnkenType ankenType = AnkenType.of(bean.getAnkenType());
		viewForm.setAnkenType(ankenType);
		viewForm.setAnkenTypeName(DefaultEnum.getVal(ankenType));
		viewForm.setBunyaName(bean.getBunyaName());

		// 名簿情報の設定
		viewForm.setPersonId(bean.getPersonId());
		viewForm.setCustomerType(bean.getCustomerType());
		viewForm.setPersonName(bean.getPersonName().getName());
		viewForm.setPersonAttribute(
				PersonAttribute.of(
						bean.getCustomerFlg(),
						bean.getAdvisorFlg(),
						bean.getCustomerType()));

		// 設定表示用ViewFormを設定
		viewForm.setSettingViewForm(this.getSettingViewForm(bean));

		// 請求/精算情報の設定
		viewForm.setIssueStatus(bean.getStatementIssueStatus());
		viewForm.setAccgDocType(AccgDocType.STATEMENT);
		return viewForm;
	}

	/**
	 * 精算書詳細Beanから精算書詳細の設定情報表示用を取得
	 * 
	 * @param bean 精算書詳細Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.SettingViewForm getSettingViewForm(StatementDetailBean bean) throws DataNotFoundException {

		AccgInvoiceStatementViewForm.SettingViewForm viewForm = new AccgInvoiceStatementViewForm.SettingViewForm();
		viewForm.setAccgDocType(AccgDocType.STATEMENT);
		viewForm.setDepositDetailAttachFlg(bean.getDepositDetailAttachFlg());
		viewForm.setSalesAccountName(bean.getSalesAccountPersonName().getName());
		viewForm.setSalesDate(bean.getSalesDate());

		// 精算書では未設定項目
		viewForm.setInvoiceTypeName(CommonConstant.BLANK);
		viewForm.setPaymentPlanAttachFlg(SystemFlg.FLG_OFF.getCd());

		return viewForm;
	}

	/**
	 * 精算書詳細Beanから精算書詳細の内部メモ情報表示用を取得
	 * 
	 * @param bean 精算書詳細Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.MemoViewForm getMemoViewForm(StatementDetailBean bean) throws DataNotFoundException {

		AccgInvoiceStatementViewForm.MemoViewForm viewForm = new AccgInvoiceStatementViewForm.MemoViewForm();
		viewForm.setMemo(bean.getStatementMemo());

		return viewForm;
	}

	/**
	 * 精算書Beanから精算書詳細のタブ情報を取得
	 * 
	 * @param bean 精算書詳細Bean
	 * @param selectedTab (Nullable)
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.DocContentsForm getDocContentsForm(StatementDetailBean bean, @Nullable AccggInvoiceStatementDetailViewTab selectedTab) throws DataNotFoundException {

		// タブ情報表示用オブジェクトを取得
		AccgInvoiceStatementViewForm.DocContentsForm viewForm = new AccgInvoiceStatementViewForm.DocContentsForm();
		String issueStatus = bean.getStatementIssueStatus();
		if (selectedTab != null) {
			// 表示タブが選択されている場合
			viewForm.setSelectedTab(selectedTab);
		} else if (IssueStatus.isIssued(issueStatus)) {
			// 発行済の場合 -> 精算書タブの表示
			viewForm.setSelectedTab(AccggInvoiceStatementDetailViewTab.STATEMENT_PDF_TAB);
		} else {
			// それ以外の場合 -> 編集タブの表示
			viewForm.setSelectedTab(AccggInvoiceStatementDetailViewTab.EDIT_TAB);
		}

		viewForm.setAccgDocType(AccgDocType.STATEMENT);
		viewForm.setDepositDetailAttachFlg(SystemFlg.codeToBoolean(bean.getDepositDetailAttachFlg()));

		// 請求書のみの項目
		viewForm.setInvoiceType(null);
		viewForm.setPaymentPlanAttachFlg(false);
		viewForm.setHasPaymentPlanWarning(false);

		// 下書きの状態のみ、編集タブを表示
		if (IssueStatus.DRAFT.equalsByCode(issueStatus)) {
			viewForm.setShowEditTab(true);
		}

		return viewForm;
	}

	/**
	 * 精算書詳細Beanから基本情報_タイトル情報表示用オブジェクトを作成する
	 * 
	 * @param bean 精算書詳細情報Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.BaseTitleViewForm getBaseTitleViewForm(StatementDetailBean bean) throws DataNotFoundException {

		// 基本情報_タイトル情報表示用オブジェクトの作成
		AccgInvoiceStatementViewForm.BaseTitleViewForm viewForm = new AccgInvoiceStatementViewForm.BaseTitleViewForm();
		viewForm.setAccgDocType(AccgDocType.STATEMENT);
		viewForm.setBaseTitle(bean.getStatementTitle());
		viewForm.setBaseDate(bean.getStatementDate());
		viewForm.setBaseNumber(bean.getStatementNo());
		return viewForm;
	}

	/**
	 * 精算書詳細Beanから基本情報_精算先情報表示用オブジェクトを作成する
	 * 
	 * @param bean 精算書詳細情報Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.BaseToViewForm getBaseToViewForm(StatementDetailBean bean) throws DataNotFoundException {

		// 基本情報_精算先情報オブジェクトの作成・設定
		AccgInvoiceStatementViewForm.BaseToViewForm viewForm = new AccgInvoiceStatementViewForm.BaseToViewForm();
		viewForm.setBaseToDetail(bean.getStatementToDetail());

		String baseToName = "";
		if (!StringUtils.isEmpty(bean.getStatementToName())) {
			baseToName = bean.getStatementToName();

			if (NameEnd.isExist(bean.getStatementToNameEnd())) {
				baseToName += CommonConstant.SPACE;
				baseToName += NameEnd.of(bean.getStatementToNameEnd()).getVal();
			}
		}
		viewForm.setBaseToName(baseToName);

		return viewForm;
	}

	/**
	 * 精算書詳細Beanから基本情報_精算元情報オブジェクトを取得する
	 *
	 * @param bean 精算書詳細情報Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.BaseFromViewForm getBaseFromViewForm(StatementDetailBean bean) throws DataNotFoundException {

		// 基本情報_精算元情報オブジェクトの作成・設定
		AccgInvoiceStatementViewForm.BaseFromViewForm viewForm = new AccgInvoiceStatementViewForm.BaseFromViewForm();
		viewForm.setBaseFromDetail(bean.getStatementFromDetail());
		viewForm.setBaseFromOfficeName(bean.getStatementFromTenantName());
		viewForm.setTenantStampPrintFlg(bean.getTenantStampPrintFlg());

		return viewForm;
	}

	/**
	 * 精算書詳細Beanから基本情報_挿入文情報表示用オブジェクトを作成
	 * 
	 * @param bean 精算書詳細Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.BaseOtherViewForm getBaseOtherViewForm(StatementDetailBean bean) throws DataNotFoundException {

		AccgInvoiceStatementViewForm.BaseOtherViewForm viewForm = new AccgInvoiceStatementViewForm.BaseOtherViewForm();
		viewForm.setAccgDocType(AccgDocType.STATEMENT);
		viewForm.setDeadline(bean.getRefundDate());
		viewForm.setSubText(bean.getStatementSubText());
		viewForm.setTitle(bean.getStatementSubject());
		viewForm.setRefundDatePrintFlg(bean.getRefundDatePrintFlg());

		// 請求書のみの項目
		viewForm.setDueDatePrintFlg(null);

		return viewForm;
	}

	/**
	 * 精算書詳細Beanから振込先情報表示用オブジェクトを作成する
	 * 
	 * @param bean 精算書詳細Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.BankDetailViewForm getBankDetailViewForm(StatementDetailBean bean) throws DataNotFoundException {

		// 基本情報_振込先情報表示用オブジェクトの作成
		AccgInvoiceStatementViewForm.BankDetailViewForm inputForm = new AccgInvoiceStatementViewForm.BankDetailViewForm();
		inputForm.setAnkenId(bean.getAnkenId());
		inputForm.setTenantBankDetail(bean.getRefundBankDetail());

		return inputForm;
	}

	/**
	 * 精算書詳細Beanから備考情報表示用オブジェクトを取得する
	 * 
	 * @param bean 精算書詳細Bean
	 * @return
	 * @throws DataNotFoundException
	 */
	private AccgInvoiceStatementViewForm.RemarksViewForm getRemarksViewForm(StatementDetailBean bean) throws DataNotFoundException {

		AccgInvoiceStatementViewForm.RemarksViewForm inputForm = new AccgInvoiceStatementViewForm.RemarksViewForm();
		inputForm.setRemarks(bean.getStatementRemarks());

		return inputForm;
	}

	/**
	 * 精算書データの会計書類-対応-送付-ファイル情報を登録
	 * 
	 * @param statementSeq
	 * @param accgDocActSendSeq
	 * @throws AppException
	 */
	private void registStatementAccgDocActSendFile(Long statementSeq, Long accgDocActSendSeq) throws AppException {

		// 精算データの取得
		TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityIfNullThrowAppException(statementSeq);

		Set<Long> accgDocFileSeqList = new HashSet<>();

		// 精算書PDFの取得
		AccgDocFileBean targetInvoiceFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgStatementEntity.getAccgDocSeq(), AccgDocFileType.STATEMENT);
		accgDocFileSeqList.add(targetInvoiceFileBean.getAccgDocFileSeq());

		// 実費明細PDFの取得
		if (SystemFlg.codeToBoolean(tAccgStatementEntity.getDepositDetailAttachFlg())) {
			AccgDocFileBean targetDepositRecordFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgStatementEntity.getAccgDocSeq(), AccgDocFileType.DEPOSIT_DETAIL);
			accgDocFileSeqList.add(targetDepositRecordFileBean.getAccgDocFileSeq());
		}

		// 登録データの作成
		List<TAccgDocActSendFileEntity> tAccgDocActSendFileEntities = accgDocFileSeqList.stream().map(e -> {
			TAccgDocActSendFileEntity tAccgDocActSendFileEntity = new TAccgDocActSendFileEntity();
			tAccgDocActSendFileEntity.setAccgDocActSendSeq(accgDocActSendSeq);
			tAccgDocActSendFileEntity.setAccgDocFileSeq(e);
			return tAccgDocActSendFileEntity;
		}).collect(Collectors.toList());

		try {
			// 登録処理
			tAccgDocActSendFileDao.batchInsert(tAccgDocActSendFileEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 精算書情報を基に請求書情報を作成する。<br>
	 * 請求書作成時用に設定情報がある項目は設定情報のデフォルト値を設定し、それ以外は請求書情報を引き継ぎます。<br>
	 * 
	 * @param tAccgStatementEntity
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	private TAccgInvoiceEntity toAccgInvoiceEntity(TAccgStatementEntity tAccgStatementEntity,
			Long ankenId) throws AppException {
		// 設定用に請求書設定情報を取得
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();
		
		// 請求書情報作成
		TAccgInvoiceEntity tAccgInvoiceEntity = new TAccgInvoiceEntity();
		tAccgInvoiceEntity.setInvoiceSeq(null);
		tAccgInvoiceEntity.setAccgDocSeq(tAccgStatementEntity.getAccgDocSeq());
		tAccgInvoiceEntity.setSalesDetailSeq(tAccgStatementEntity.getSalesDetailSeq());
		tAccgInvoiceEntity.setUncollectibleDetailSeq(null);
		tAccgInvoiceEntity.setInvoiceIssueStatus(IssueStatus.DRAFT.getCd());
		tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.DRAFT.getCd());
		tAccgInvoiceEntity
				.setInvoiceAmount(LoiozNumberUtils.nullToZero(tAccgStatementEntity.getStatementAmount()).negate());
		tAccgInvoiceEntity.setInvoiceType(InvoiceType.IKKATSU.getCd());
		tAccgInvoiceEntity.setBillToPersonId(tAccgStatementEntity.getRefundToPersonId());
		tAccgInvoiceEntity.setSalesDate(tAccgStatementEntity.getSalesDate());
		tAccgInvoiceEntity.setSalesAccountSeq(tAccgStatementEntity.getSalesAccountSeq());
		tAccgInvoiceEntity.setDepositDetailAttachFlg(tAccgStatementEntity.getDepositDetailAttachFlg());
		tAccgInvoiceEntity.setPaymentPlanAttachFlg(SystemFlg.FLG_OFF.getCd());
		tAccgInvoiceEntity.setInvoiceDate(tAccgStatementEntity.getStatementDate());
		
		// タイトルは、請求書作成時用のデフォルトタイトルで上書き
		tAccgInvoiceEntity.setInvoiceTitle(mInvoiceSettingEntity.getDefaultTitle());
		
		// 請求書番号を新しく採番する
		tAccgInvoiceEntity.setInvoiceNo(commonAccgService.issueNewInvoiceNo());
		
		// 請求先名称
		tAccgInvoiceEntity.setInvoiceToName(tAccgStatementEntity.getStatementToName());
		
		// 請求先敬称
		tAccgInvoiceEntity.setInvoiceToNameEnd(tAccgStatementEntity.getStatementToNameEnd());
		
		// 請求先詳細
		tAccgInvoiceEntity.setInvoiceToDetail(tAccgStatementEntity.getStatementToDetail());
		
		// 挿入文は、請求書作成時用のデフォルト挿入文で上書き
		tAccgInvoiceEntity.setInvoiceSubText(mInvoiceSettingEntity.getDefaultSubText());
		
		// 案件名を取得する
		String ankenName = CommonConstant.BLANK;
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		if (tAnkenEntity != null) {
			ankenName = tAnkenEntity.getAnkenName();
		}
		// 件名をマスタ設定の指定方式に変換する
		String invoiceSubject = AccountingUtils.formatAccgDocSubject(ankenName,
				mInvoiceSettingEntity.getSubjectPrefix(), mInvoiceSettingEntity.getSubjectSuffix());
		tAccgInvoiceEntity.setInvoiceSubject(invoiceSubject);
		
		// 差出人事務所名
		tAccgInvoiceEntity.setInvoiceFromTenantName(tAccgStatementEntity.getStatementFromTenantName());
		
		// 差出人詳細
		tAccgInvoiceEntity.setInvoiceFromDetail(tAccgStatementEntity.getStatementFromDetail());
		
		// 支払期限は空にする
		tAccgInvoiceEntity.setDueDate(null);
		
		// 支払期限印字フラグは、請求書作成時用のデフォルトで上書き
		tAccgInvoiceEntity.setDueDatePrintFlg(mInvoiceSettingEntity.getDueDatePrintFlg());
		
		// 案件-案件区分を取得する
		String ankenType = tAnkenEntity.getAnkenType();
		
		// 口座情報 - コピーせず、事務所案件ならテナント口座、個人案件なら売上計上先の口座情報をセットする
		// 印影表示フラグ - コピーせず、事務所案件なら事務所印の設定値、個人案件なら弁護士印の設定値をデフォルト値としてセットする
		if (AnkenType.JIMUSHO.equalsByCode(ankenType)) {
			// 事務所案件
			
			// 銀行口座にテナント口座をセット
			TenantDetailKozaBean tenantDetailKozaBean = mTenantDao
					.selectTenantDetailAndKozaBySeq(SessionUtils.getTenantSeq());
			tAccgInvoiceEntity.setTenantBankDetail(commonAccgService.tenantBankToDetail(tenantDetailKozaBean));
			
			// 請求書設定で設定されている事務所印の表示設定で上書き
			tAccgInvoiceEntity.setTenantStampPrintFlg(mInvoiceSettingEntity.getTenantStampPrintFlg());
			
		} else if (AnkenType.KOJIN.equalsByCode(ankenType)) {
			// 個人案件
			
			Long salesAccountSeq = tAccgStatementEntity.getSalesAccountSeq();
			
			// 銀行口座に売上計上先の口座をセット
			TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao
					.selectDefaultSalesAccountKozaByAccountSeq(salesAccountSeq);
			tAccgInvoiceEntity.setTenantBankDetail(commonAccgService.salesAccountBankToDetail(tGinkoKozaEntity));
			
			// 売上計上先の個人設定で設定されている弁護士印の表示設定で上書き
			String tenantStampPrintFlg = SystemFlg.FLG_OFF.getCd();
			if (salesAccountSeq != null) {
				// 売上計上先のアカウント情報
				MAccountEntity mAccountEntity = mAccountDao.selectBySeq(salesAccountSeq);
				if (mAccountEntity != null) {
					tenantStampPrintFlg = mAccountEntity.getAccountLawyerStampPrintFlg() != null ? mAccountEntity.getAccountLawyerStampPrintFlg() : SystemFlg.FLG_OFF.getCd();
				}
			}
			tAccgInvoiceEntity.setTenantStampPrintFlg(tenantStampPrintFlg);
			
		} else {
			// 想定外
			throw new RuntimeException("想定外のEnum値 [ankenType=" + ankenType + "]");
		}
		
		// 備考は、請求書作成時用のデフォルト備考で上書き
		tAccgInvoiceEntity.setInvoiceRemarks(mInvoiceSettingEntity.getDefaultRemarks());
		
		// 既入金取引日印字フラグは、請求書作成時用のデフォルトで上書き
		tAccgInvoiceEntity.setRepayTransactionDatePrintFlg(mInvoiceSettingEntity.getTransactionDatePrintFlg());
		
		// 請求取引日印字フラグは、請求書作成時用のデフォルトで上書き
		tAccgInvoiceEntity.setInvoiceTransactionDatePrintFlg(mInvoiceSettingEntity.getTransactionDatePrintFlg());
		tAccgInvoiceEntity.setRepaySumFlg(tAccgStatementEntity.getRepaySumFlg());
		tAccgInvoiceEntity.setExpenseSumFlg(tAccgStatementEntity.getExpenseSumFlg());
		tAccgInvoiceEntity.setInvoiceMemo(tAccgStatementEntity.getStatementMemo());

		return tAccgInvoiceEntity;
	}

	/**
	 * 精算書情報を更新する
	 * 
	 * @param tAccgStatementEntity
	 * @throws AppException
	 */
	private void updateAccgStatement(TAccgStatementEntity tAccgStatementEntity) throws AppException {
		
		int updateCount = 0;
		try {
			updateCount = tAccgStatementDao.update(tAccgStatementEntity);
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
	}
	
	/**
	 * 精算書の発行時にバリデーション
	 * 
	 * @param tAccgStatementEntity
	 * @return
	 */
	private void validateUnChangedFormatIssue(TAccgStatementEntity tAccgStatementEntity) throws AppException {
		// 現状なし
	}

}