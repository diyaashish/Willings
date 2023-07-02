package jp.loioz.app.user.invoiceDetail.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.form.accg.AccgDocSummaryForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BankDetailInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BaseFromInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BaseOtherInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BaseTitleInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BaseToInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.InvoiceRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.MemoInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RemarksInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.SettingInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm;
import jp.loioz.app.common.service.CommonAccgAmountService;
import jp.loioz.app.common.service.CommonAccgInvoiceStatementService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.bean.AccgDocFileBean;
import jp.loioz.bean.AccgInvoicePaymentPlanBean;
import jp.loioz.bean.InvoiceDetailBean;
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
import jp.loioz.common.constant.CommonConstant.FractionalMonthType;
import jp.loioz.common.constant.CommonConstant.InvoiceType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.MailTemplateType;
import jp.loioz.common.constant.CommonConstant.NameEnd;
import jp.loioz.common.constant.CommonConstant.SeisanShiharaiMonthDay;
import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
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
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MMailDao;
import jp.loioz.dao.MMailSettingDao;
import jp.loioz.dao.MMailTemplateDao;
import jp.loioz.dao.MStatementSettingDao;
import jp.loioz.dao.TAccgDocActSendFileDao;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAccgDocFileDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgInvoicePaymentPlanConditionDao;
import jp.loioz.dao.TAccgInvoicePaymentPlanDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.dto.AccgInvoiceStatementAmountDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.FileContentsDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MMailEntity;
import jp.loioz.entity.MMailSettingEntity;
import jp.loioz.entity.MMailTemplateEntity;
import jp.loioz.entity.MStatementSettingEntity;
import jp.loioz.entity.TAccgDocActEntity;
import jp.loioz.entity.TAccgDocActSendFileEntity;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgInvoicePaymentPlanConditionEntity;
import jp.loioz.entity.TAccgInvoicePaymentPlanEntity;
import jp.loioz.entity.TAccgStatementEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 請求書詳細画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InvoiceDetailService extends DefaultService {

	/** 年：プルダウンに基準年から何年前まで表示するか */
	private static final int YEAR_SELECT_OPTION_BEFORE_NUMBER = 3;
	/** 年：プルダウンに基準年から何年後まで表示するか */
	private static final int YEAR_SELECT_OPTION_AFTER_NUMBER = 5;

	/** PDFService */
	@Autowired
	private PdfService pdfService;

	/** 会計管理共通サービス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 会計管理の金額を扱う共通サービス */
	@Autowired
	private CommonAccgAmountService commonAccgAmountService;

	/** 会計管理：請求書詳細、精算書詳細の共通サービス */
	@Autowired
	private CommonAccgInvoiceStatementService commonAccgInvoiceStatementService;

	/** 共通：名簿サービス */
	@Autowired
	private CommonPersonService commonPersonService;

	/** 共通：ファイルストレージサービスクラス */
	@Autowired
	private FileStorageService fileStorageService;

	/** アカウントDao */
	@Autowired
	private MAccountDao mAccountDao;

	/** メールテンプレートDaoクラス */
	@Autowired
	private MMailTemplateDao mMailTemplateDao;

	/** メール設定Daoクラス */
	@Autowired
	private MMailSettingDao mMailSettingDao;

	/** 請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

	/** 会計書類Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** 会計書類-対応-送付-ファイルDaoクラス */
	@Autowired
	private TAccgDocActSendFileDao tAccgDocActSendFileDao;

	/** 支払分割条件Daoクラス */
	@Autowired
	private TAccgInvoicePaymentPlanConditionDao tAccgInvoicePaymentPlanConditionDao;

	/** 支払計画Daoクラス */
	@Autowired
	private TAccgInvoicePaymentPlanDao tAccgInvoicePaymentPlanDao;

	/** 会計ファイル情報Daoクラス */
	@Autowired
	private TAccgDocFileDao tAccgDocFileDao;

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 顧客共通Daoクラス */
	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

	/** 管理DB：メールDaoクラス */
	@Autowired
	private MMailDao mgtMMailDao;

	/** 精算書設定マスタDaoクラス */
	@Autowired
	private MStatementSettingDao mStatementSettingDao;

	/** 案件情報用のDaoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	//=========================================================================
	// ▼ 取得／データ変換系
	//=========================================================================
	
	/**
	 * 会計書類SEQを取得します
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	public Long getAccgDocSeq(Long invoiceSeq) {
		return commonAccgService.getAccgDocSeqByInvoiceSeq(invoiceSeq);
	}

	/**
	 * 請求書詳細画面表示情報フォームを作成します
	 * 
	 * @return
	 */
	public AccgInvoiceStatementViewForm createViewForm() {
		AccgInvoiceStatementViewForm viewForm = new AccgInvoiceStatementViewForm();
		return viewForm;
	}

	/**
	 * 請求書詳細情報を取得します
	 * 
	 * @param viewForm
	 * @param accgDocSeq
	 * @param invoiceSeq
	 * @throws DataNotFoundException
	 */
	public void searchInvoiceDetail(AccgInvoiceStatementViewForm viewForm, Long accgDocSeq, Long invoiceSeq) throws DataNotFoundException {

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が見つかりません。");
		}

		// SEQ情報を設定
		viewForm.setAccgDocSeq(accgDocSeq);
		viewForm.setInvoiceSeq(invoiceSeq);
		viewForm.setAnkenId(tAccgDocEntity.getAnkenId());
		viewForm.setPersonId(tAccgDocEntity.getPersonId());
		viewForm.setAccgDocType(AccgDocType.INVOICE);

		// 案件情報
		AccgInvoiceStatementViewForm.AnkenForm ankenForm = this.getAnkenForm(accgDocSeq);
		viewForm.setAnkenForm(ankenForm);
		// タブ情報
		AccgInvoiceStatementViewForm.DocContentsForm docContentsForm = this.getDocContentsForm(accgDocSeq, this.getInitInvoiceStatementDetailViewTabByIssueStatus(accgDocSeq));
		viewForm.setDocContentsForm(docContentsForm);
		// 基本タイトル情報
		AccgInvoiceStatementViewForm.BaseTitleViewForm baseTitleViewForm = this.getBaseTitleViewForm(accgDocSeq);
		viewForm.setBaseTitleViewForm(baseTitleViewForm);
		// 請求先情報
		AccgInvoiceStatementViewForm.BaseToViewForm baseToViewForm = this.getBaseToViewForm(accgDocSeq);
		viewForm.setBaseToViewForm(baseToViewForm);
		// 請求元情報
		AccgInvoiceStatementViewForm.BaseFromViewForm baseFromViewForm = this.getBaseFromViewForm(accgDocSeq);
		viewForm.setBaseFromViewForm(baseFromViewForm);
		// 挿入文情報
		AccgInvoiceStatementViewForm.BaseOtherViewForm baseOtherViewForm = this.getBaseOtherViewForm(accgDocSeq);
		viewForm.setBaseOtherViewForm(baseOtherViewForm);
		// 既入金情報
		AccgInvoiceStatementViewForm.RepayViewForm repayViewForm = this.getRepayViewForm(accgDocSeq);
		viewForm.setRepayViewForm(repayViewForm);
		// 請求情報
		AccgInvoiceStatementViewForm.InvoiceViewForm invoiceViewForm = this.getInvoiceViewForm(accgDocSeq);
		viewForm.setInvoiceViewForm(invoiceViewForm);
		// 振込先情報
		AccgInvoiceStatementViewForm.BankDetailViewForm bankDetailViewForm = this.getBankDetailViewForm(accgDocSeq);
		viewForm.setBankDetailViewForm(bankDetailViewForm);
		// 備考情報
		AccgInvoiceStatementViewForm.RemarksViewForm remarksViewForm = this.getRemarksViewForm(accgDocSeq);
		viewForm.setRemarksViewForm(remarksViewForm);
		// 取引状況情報
		AccgDocSummaryForm docSummaryForm = this.getDocSummaryForm(accgDocSeq);
		viewForm.setDocSummaryForm(docSummaryForm);
		// 進行状況情報
		AccgInvoiceStatementViewForm.DocActivityForm docActivityForm = this.getDocActivityForm(accgDocSeq);
		viewForm.setDocActivityForm(docActivityForm);
		// 内部メモ情報
		AccgInvoiceStatementViewForm.MemoViewForm memoViewForm = this.getMemoViewForm(accgDocSeq);
		viewForm.setMemoViewForm(memoViewForm);

		// 請求書タブ情報
		try {
			AccgInvoiceStatementViewForm.DocInvoicePdfViewForm docInvoicePdfViewForm = this.getDocInvoicePdfViewForm(invoiceSeq);
			viewForm.setDocInvoicePdfViewForm(docInvoicePdfViewForm);
		} catch (DataNotFoundException e) {
			// 請求書タブ情報が無い場合はセットしない
		}
	}

	/**
	 * 請求書詳細の案件用情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.AnkenForm getAnkenForm(Long accgDocSeq) {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgInvoiceStatementViewForm.AnkenForm form = new AccgInvoiceStatementViewForm.AnkenForm();
		form.setAnkenId(bean.getAnkenId());
		form.setAnkenName(bean.getAnkenName());
		form.setAnkenStatus(AnkenStatus.of(bean.getAnkenStatus()));
		form.setAnkenType(AnkenType.of(bean.getAnkenType()));
		form.setAnkenTypeName(StringUtils.isEmpty(bean.getAnkenType()) ? "" : AnkenType.of(bean.getAnkenType()).getVal());
		form.setBunyaName(bean.getBunyaName());
		form.setIssueStatus(bean.getInvoiceIssueStatus());
		form.setPersonId(bean.getPersonId());
		form.setPersonName(bean.getPersonNameSei() + " " + bean.getPersonNameMei());
		form.setPersonAttribute(PersonAttribute.of(bean.getCustomerFlg(), bean.getAdvisorFlg(), bean.getCustomerType()));
		form.setCustomerType(bean.getCustomerType());
		form.setSettingViewForm(getSettingViewForm(accgDocSeq));
		form.setAccgDocType(AccgDocType.INVOICE);
		return form;
	}

	/**
	 * 請求書詳細の設定用情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	/**
	 * 請求書詳細：印刷して送信モーダルの画面オブジェクトを取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.FilePrintSendViewForm getFilePrintSendViewForm(Long invoiceSeq) throws DataNotFoundException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[invoiceSeq=" + invoiceSeq + "]");
		}

		return commonAccgInvoiceStatementService.getFilePrintSendViewForm(tAccgInvoiceEntity.getAccgDocSeq());
	}

	/**
	 * 請求書SEQをキーとして、現在対象のPDFのダウンロード情報を設定する
	 * 
	 * @param invoiceSeq
	 * @param response
	 * @throws AppException
	 * @throws Exception
	 */
	public void printDownload(Long invoiceSeq, HttpServletResponse response) throws AppException, Exception {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null || !IssueStatus.isIssued(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
			// データが取得できない || 未発行データの場合 -> 排他エラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		this.printDownload(tAccgInvoiceEntity, response);
	}

	/**
	 * 現在対象のPDFのダウンロード情報を設定する
	 * 
	 * @param tAccgInvoiceEntity
	 * @param response
	 * @throws AppException
	 * @throws Exception
	 */
	private void printDownload(TAccgInvoiceEntity tAccgInvoiceEntity, HttpServletResponse response)
			throws AppException, Exception {

		FileContentsDto fileContents = this.donwloadInvoiceFileZip(tAccgInvoiceEntity);
		URLCodec codec = new URLCodec("UTF-8");
		String fileName = codec.encode(this.createAccgDocZipFileName(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocType.INVOICE,
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
	 * 請求書の現在対象となっているPDFをZip形式で取得する
	 * 
	 * @param tAccgInvoiceEntity
	 * @return
	 */
	private FileContentsDto donwloadInvoiceFileZip(TAccgInvoiceEntity tAccgInvoiceEntity) {

		List<AccgDocFileBean> beanList = new ArrayList<>(3);

		// 請求書PDFの取得
		AccgDocFileBean targetInvoiceFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE);
		beanList.add(targetInvoiceFileBean);

		// 実費明細PDFの取得
		if (SystemFlg.codeToBoolean(tAccgInvoiceEntity.getDepositDetailAttachFlg())) {
			AccgDocFileBean targetDepositRecordFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.DEPOSIT_DETAIL);
			beanList.add(targetDepositRecordFileBean);
		}

		// 支払計画書PDFの取得
		if (SystemFlg.codeToBoolean(tAccgInvoiceEntity.getPaymentPlanAttachFlg())) {
			AccgDocFileBean targetInvoicePlanFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE_PAYMENT_PLAN);
			beanList.add(targetInvoicePlanFileBean);
		}

		return commonAccgInvoiceStatementService.toZipFileContents(beanList);
	}

	/**
	 * 請求書を送付済みにする
	 * 
	 * @param tAccgInvoiceEntity
	 * @throws AppException
	 */
	private void changeToSend(TAccgInvoiceEntity tAccgInvoiceEntity) throws AppException {

		// 請求書SEQ
		tAccgInvoiceEntity.setInvoiceIssueStatus(IssueStatus.SENT.getCd());

		// 請求書発行ステータスの更新
		this.updateAccgInvoice(tAccgInvoiceEntity);

		// 会計書類-対応データの登録処理
		TAccgDocActEntity tAccgDocActEntity = commonAccgService.registAccgDocAct(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocActType.SEND);
		Long accgDocActSeq = tAccgDocActEntity.getAccgDocActSeq();

		// 会計書類-対応-送付データの登録処理
		Long accgDocActSendSeq = commonAccgInvoiceStatementService.registAccgDocActSendForChangeToSend(accgDocActSeq);

		// 請求書：会計書類-対応-送付-ファイルの登録処理
		this.registInvoiceAccgDocActSendFile(tAccgInvoiceEntity.getInvoiceSeq(), accgDocActSendSeq);
	}

	/**
	 * 送付ファイルをダウンロードして、送付済みにする
	 * 
	 * @param invoiceSeq
	 * @param response
	 * @throws AppException
	 * @throws Exception
	 */
	public void downloadAndChangeToSend(Long invoiceSeq, HttpServletResponse response) throws AppException, Exception {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null || !IssueStatus.isIssued(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
			// データが取得できない || 未発行データの場合 -> 排他エラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		this.changeToSend(tAccgInvoiceEntity);
		this.printDownload(tAccgInvoiceEntity, response);
	}

	/**
	 * 請求書詳細の設定用情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.SettingViewForm getSettingViewForm(Long accgDocSeq) {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgInvoiceStatementViewForm.SettingViewForm form = new AccgInvoiceStatementViewForm.SettingViewForm();
		form.setAccgDocType(AccgDocType.INVOICE);
		form.setDepositDetailAttachFlg(bean.getDepositDetailAttachFlg());
		form.setInvoiceTypeName(StringUtils.isEmpty(bean.getInvoiceType()) ? "" : InvoiceType.of(bean.getInvoiceType()).getVal());
		form.setPaymentPlanAttachFlg(bean.getPaymentPlanAttachFlg());
		form.setSalesAccountName(bean.getSalesAccountNameSei() + " " + bean.getSalesAccountNameMei());
		form.setSalesDate(bean.getSalesDate());
		return form;
	}

	/**
	 * 請求書詳細の設定用情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementInputForm.SettingInputForm getSettingInputForm(Long accgDocSeq) throws DataNotFoundException {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgInvoiceStatementInputForm.SettingInputForm form = new AccgInvoiceStatementInputForm.SettingInputForm();
		form.setInvoiceType(bean.getInvoiceType());
		form.setDepositDetailAttachFlg(SystemFlg.FLG_ON.equalsByCode(bean.getDepositDetailAttachFlg()));
		form.setPaymentPlanAttachFlg(SystemFlg.FLG_ON.equalsByCode(bean.getPaymentPlanAttachFlg()));
		form.setSalesAccount(bean.getSalesAccountSeq());
		form.setSalesDate(DateUtils.parseToString(bean.getSalesDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		setDispProperties(accgDocSeq, form);
		return form;
	}

	/**
	 * 請求書詳細の設定用情報表示用プロパティを設定する
	 * 
	 * @param accgDocSeq
	 * @param form
	 * @throws DataNotFoundException
	 */
	public void setDispProperties(Long accgDocSeq, AccgInvoiceStatementInputForm.SettingInputForm form) throws DataNotFoundException {

		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		form.setAccgDocType(AccgDocType.INVOICE);
		form.setIssueStatus(bean.getInvoiceIssueStatus());
		form.setSalesAccountList(commonAccgInvoiceStatementService.generateAccgSalesOwnerOptionForm(accgDocSeq));
	}

	/**
	 * 請求書詳細の設定用情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementInputForm.MemoInputForm getMemoInputForm(Long accgDocSeq) throws DataNotFoundException {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgInvoiceStatementInputForm.MemoInputForm form = new AccgInvoiceStatementInputForm.MemoInputForm();

		setDispProperties(accgDocSeq, form);
		return form;
	}

	/**
	 * 請求書詳細の内部メモ用情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.MemoViewForm getMemoViewForm(Long accgDocSeq) {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgInvoiceStatementViewForm.MemoViewForm form = new AccgInvoiceStatementViewForm.MemoViewForm();
		form.setMemo(bean.getInvoiceMemo());
		return form;
	}

	/**
	 * 請求書詳細の内部メモ用情報表示用プロパティを設定する
	 * 
	 * @param accgDocSeq
	 * @param form
	 * @throws DataNotFoundException
	 */
	public void setDispProperties(Long accgDocSeq, AccgInvoiceStatementInputForm.MemoInputForm form) throws DataNotFoundException {

		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		form.setMemo(bean.getInvoiceMemo());
	}

	/**
	 * 請求書詳細のタグ用情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.DocContentsForm getDocContentsForm(Long accgDocSeq, AccggInvoiceStatementDetailViewTab selectedTab) {
		// 会計書類ファイル情報取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null || tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("会計書類ファイル情報、請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementViewForm.DocContentsForm form = new AccgInvoiceStatementViewForm.DocContentsForm();
		String issueStatus = tAccgInvoiceEntity.getInvoiceIssueStatus();
		if (selectedTab == null) {
			// 請求書の発行ステータスで、初期選択タブを変更
			if (IssueStatus.DRAFT.equalsByCode(issueStatus)) {
				form.setSelectedTab(AccggInvoiceStatementDetailViewTab.EDIT_TAB);
			} else {
				form.setSelectedTab(AccggInvoiceStatementDetailViewTab.INVOICE_PDF_TAB);
			}
		} else {
			form.setSelectedTab(selectedTab);
		}
		form.setAccgDocType(AccgDocType.of(tAccgDocEntity.getAccgDocType()));
		form.setInvoiceType(tAccgInvoiceEntity.getInvoiceType());
		form.setDepositDetailAttachFlg(SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getDepositDetailAttachFlg()));
		form.setPaymentPlanAttachFlg(SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getPaymentPlanAttachFlg()));
		// 下書きの状態のみ、編集タブを表示
		if (IssueStatus.DRAFT.equalsByCode(issueStatus)) {
			form.setShowEditTab(true);
		}

		Long invoiceSeq = tAccgInvoiceEntity.getInvoiceSeq();
		if (isInvoicePlanPdfReCreateFailured(invoiceSeq)
				|| isOldInvoicePlan(invoiceSeq)
				|| needInvoicePlanReBuild(invoiceSeq)) {
			// どれか一つでも引っかかった場合はwarningを表示
			form.setHasPaymentPlanWarning(true);
		}

		return form;
	}

	/**
	 * 基本情報_タイトル情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.BaseTitleViewForm getBaseTitleViewForm(Long accgDocSeq) {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		// formにセット
		AccgInvoiceStatementViewForm.BaseTitleViewForm form = new AccgInvoiceStatementViewForm.BaseTitleViewForm();
		form.setAccgDocType(AccgDocType.INVOICE);
		form.setBaseDate(bean.getInvoiceDate());
		form.setBaseNumber(bean.getInvoiceNo());
		form.setBaseTitle(bean.getInvoiceTitle());
		return form;
	}

	/**
	 * 基本情報_タイトル情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.BaseTitleInputForm getBaseTitleInputForm(Long accgDocSeq) {
		// 請求書データ取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		// formにセット
		AccgInvoiceStatementInputForm.BaseTitleInputForm form = new AccgInvoiceStatementInputForm.BaseTitleInputForm();
		form.setBaseTitle(tAccgInvoiceEntity.getInvoiceTitle());
		form.setBaseDate(DateUtils.parseToString(tAccgInvoiceEntity.getInvoiceDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		form.setBaseNo(tAccgInvoiceEntity.getInvoiceNo());

		setDispProperties(form);
		return form;
	}

	/**
	 * 基本情報_タイトル情報入力用オブジェクトの表示用プロパティを設定せる
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.BaseTitleInputForm inputForm) {

		// 会計書類種別の設定
		inputForm.setAccgDocType(AccgDocType.INVOICE);
	}
	
	/**
	 * 基本情報_請求先情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.BaseToViewForm getBaseToViewForm(Long accgDocSeq) {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementViewForm.BaseToViewForm form = new AccgInvoiceStatementViewForm.BaseToViewForm();
		form.setBaseToDetail(tAccgInvoiceEntity.getInvoiceToDetail());
		if (!StringUtils.isEmpty(tAccgInvoiceEntity.getInvoiceToName())) {
			form.setBaseToName(tAccgInvoiceEntity.getInvoiceToName() + CommonConstant.SPACE
					+ (NameEnd.isExist(tAccgInvoiceEntity.getInvoiceToNameEnd()) ? NameEnd.of(tAccgInvoiceEntity.getInvoiceToNameEnd()).getVal() : ""));
		}
		return form;
	}

	/**
	 * 基本情報_請求先情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.BaseToInputForm getBaseToInputForm(Long accgDocSeq) {
		// 請求書情報取得
		InvoiceDetailBean invoiceDetailBean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (invoiceDetailBean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementInputForm.BaseToInputForm form = new AccgInvoiceStatementInputForm.BaseToInputForm();
		form.setBaseToDetail(invoiceDetailBean.getInvoiceToDetail());
		form.setBaseToName(invoiceDetailBean.getInvoiceToName());
		form.setBaseToNameEnd(invoiceDetailBean.getInvoiceToNameEnd());
		form.setAnkenId(invoiceDetailBean.getAnkenId());
		form.setAnkenName(invoiceDetailBean.getAnkenName());
		form.setBillToPersonId(invoiceDetailBean.getBillToPersonId());
		form.setChangeBillToPersonId(invoiceDetailBean.getBillToPersonId());
		form.setBillToPersonName(StringUtils.null2blank(invoiceDetailBean.getBillToPersonNameSei()) + CommonConstant.SPACE
				+ StringUtils.null2blank(invoiceDetailBean.getBillToPersonNameMei()));
		Long ankenId = invoiceDetailBean.getAnkenId();
		form.setCustomerKanyoshaList(this.getCustomerKanyoshaList(ankenId));

		return form;
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
	 * 案件の顧客と関与者のプルダウン用リストを取得します。
	 * 
	 * @param ankenId
	 * @return
	 */
	public List<CustomerKanyoshaPulldownDto> getCustomerKanyoshaList(Long ankenId) {
		List<CustomerKanyoshaPulldownDto> customerkanyoshaList = tCustomerCommonDao.selectCustomerKanyoshaPulldownByAnkenId(ankenId);
		return customerkanyoshaList;
	}
	
	/**
	 * 基本情報_請求元情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.BaseFromViewForm getBaseFromViewForm(Long accgDocSeq) {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementViewForm.BaseFromViewForm form = new AccgInvoiceStatementViewForm.BaseFromViewForm();
		form.setBaseFromDetail(tAccgInvoiceEntity.getInvoiceFromDetail());
		form.setBaseFromOfficeName(tAccgInvoiceEntity.getInvoiceFromTenantName());
		form.setTenantStampPrintFlg(tAccgInvoiceEntity.getTenantStampPrintFlg());

		return form;
	}

	/**
	 * 基本情報_請求元情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.BaseFromInputForm getBaseFromInputForm(Long accgDocSeq) {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementInputForm.BaseFromInputForm form = new AccgInvoiceStatementInputForm.BaseFromInputForm();
		form.setBaseFromDetail(tAccgInvoiceEntity.getInvoiceFromDetail());
		form.setBaseFromOfficeName(tAccgInvoiceEntity.getInvoiceFromTenantName());
		form.setTenantStampPrintFlg(SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getTenantStampPrintFlg()));

		return form;
	}
	
	/**
	 * 基本情報_挿入文情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.BaseOtherViewForm getBaseOtherViewForm(Long accgDocSeq) {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgInvoiceStatementViewForm.BaseOtherViewForm form = new AccgInvoiceStatementViewForm.BaseOtherViewForm();
		form.setAccgDocType(AccgDocType.INVOICE);
		form.setDeadline(bean.getDueDate());
		form.setSubText(bean.getInvoiceSubText());
		form.setTitle(bean.getInvoiceSubject());
		form.setDueDatePrintFlg(bean.getDueDatePrintFlg());
		return form;
	}

	/**
	 * 基本情報_挿入文情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.BaseOtherInputForm getBaseOtherInputForm(Long accgDocSeq) {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementInputForm.BaseOtherInputForm form = new AccgInvoiceStatementInputForm.BaseOtherInputForm();
		form.setDeadline(DateUtils.parseToString(tAccgInvoiceEntity.getDueDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		form.setSubText(tAccgInvoiceEntity.getInvoiceSubText());
		form.setTitle(tAccgInvoiceEntity.getInvoiceSubject());
		form.setDeadLinePrintFlg(SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getDueDatePrintFlg()));

		setDispProperties(form);
		return form;
	}

	/**
	 * 基本情報_挿入文情報入力用オブジェクトに表示用オブジェクトを設定する
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.BaseOtherInputForm inputForm) {

		// 会計書類種別を設定
		inputForm.setAccgDocType(AccgDocType.INVOICE);
	}
	
	/**
	 * 既入金情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.RepayViewForm getRepayViewForm(Long accgDocSeq) {
		AccgInvoiceStatementViewForm.RepayViewForm repayViewForm = commonAccgInvoiceStatementService.getRepayViewForm(accgDocSeq);
		return repayViewForm;
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
	 * 既入金項目情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.RepayInputForm getRepayInputForm(Long accgDocSeq) {
		AccgInvoiceStatementInputForm.RepayInputForm form = commonAccgInvoiceStatementService.getRepayInputForm(accgDocSeq);
		return form;
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
	 * 既入金項目の一覧をdocRepayOrder昇順に並び替えます。
	 * 
	 * @param repayRowList
	 * @return
	 */
	public List<RepayRowInputForm> sortRepayList(List<RepayRowInputForm> repayRowList) {
		return commonAccgInvoiceStatementService.sortRepayList(repayRowList);
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
	 * 請求項目情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.InvoiceViewForm getInvoiceViewForm(Long accgDocSeq) {
		AccgInvoiceStatementViewForm.InvoiceViewForm form = commonAccgInvoiceStatementService.getInvoiceViewForm(accgDocSeq);
		return form;
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
	 * 請求項目情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceInputForm getInvoiceInputForm(Long accgDocSeq) {
		AccgInvoiceStatementInputForm.InvoiceInputForm form = commonAccgInvoiceStatementService.getInvoiceInputForm(accgDocSeq);
		return form;
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
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewDepositRecvRowInputForm(Long accgDocSeq,
			Long invoiceRowCount) {
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
	 * 請求項目の一覧をdocInvoiceOrder昇順に並び替えます。
	 * 
	 * @param invoiceRowList
	 * @return
	 */
	public List<InvoiceRowInputForm> sortInvoiceList(List<InvoiceRowInputForm> invoiceRowList) {
		return commonAccgInvoiceStatementService.sortInvoiceList(invoiceRowList);
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
	 * 振込先情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.BankDetailViewForm getBankDetailViewForm(Long accgDocSeq) {
		// 請求書情報取得
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementViewForm.BankDetailViewForm form = new AccgInvoiceStatementViewForm.BankDetailViewForm();
		form.setAnkenId(bean.getAnkenId());
		form.setTenantBankDetail(bean.getTenantBankDetail());

		return form;
	}

	/**
	 * 振込先情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.BankDetailInputForm getBankDetailInputForm(Long accgDocSeq) {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementInputForm.BankDetailInputForm form = new AccgInvoiceStatementInputForm.BankDetailInputForm();
		form.setTenantBankDetail(tAccgInvoiceEntity.getTenantBankDetail());

		return form;
	}
	
	/**
	 * 備考情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.RemarksViewForm getRemarksViewForm(Long accgDocSeq) {
		InvoiceDetailBean bean = tAccgInvoiceDao.selectInvoiceDetailByAccgDocSeq(accgDocSeq);
		if (bean == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgInvoiceStatementViewForm.RemarksViewForm form = new AccgInvoiceStatementViewForm.RemarksViewForm();
		form.setRemarks(bean.getInvoiceRemarks());

		return form;
	}

	/**
	 * 備考情報入力用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementInputForm.RemarksInputForm getRemarksInputForm(Long accgDocSeq) {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// formにセット
		AccgInvoiceStatementInputForm.RemarksInputForm form = new AccgInvoiceStatementInputForm.RemarksInputForm();
		form.setRemarks(tAccgInvoiceEntity.getInvoiceRemarks());

		return form;
	}
	
	/**
	 * 取引状況情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgDocSummaryForm getDocSummaryForm(Long accgDocSeq) {
		TAccgInvoiceEntity entity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (entity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		AccgDocSummaryForm form = commonAccgService.getAccgDocSummaryForm(accgDocSeq);

		return form;
	}

	/**
	 * 進行状況情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.DocActivityForm getDocActivityForm(Long accgDocSeq) throws DataNotFoundException {
		return commonAccgInvoiceStatementService.getDocActivityForm(accgDocSeq);
	}
	
	/**
	 * 請求書画面_請求書タブ内画面表示用オブジェクトを取得
	 *
	 * @param invoiceSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.DocInvoicePdfViewForm getDocInvoicePdfViewForm(Long invoiceSeq) throws DataNotFoundException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			// 会計書類データが存在しない場合
			throw new DataNotFoundException("精算書データが存在しません。");
		}

		var docInvoicePdfViewForm = new AccgInvoiceStatementViewForm.DocInvoicePdfViewForm();
		List<String> pngHtmlImgSrcList = commonAccgInvoiceStatementService.getAccgPdfImgHtmlSrcList(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE);

		docInvoicePdfViewForm.setInvoiceSeq(invoiceSeq);
		docInvoicePdfViewForm.setAccgDocSeq(tAccgInvoiceEntity.getAccgDocSeq());
		docInvoicePdfViewForm.setInvoicePngSrc(pngHtmlImgSrcList);
		docInvoicePdfViewForm.setCanReCreate(IssueStatus.DRAFT.equalsByCode(tAccgInvoiceEntity.getInvoiceIssueStatus()));// 下書き時のみ再作成可能

		return docInvoicePdfViewForm;
	}

	/**
	 * 請求書画面_実費明細タブ内画面表示用オブジェクトを取得
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.DipositRecordPdfViewForm getDipositRecordPdfViewForm(Long accgDocSeq) throws DataNotFoundException {
		return commonAccgInvoiceStatementService.getDipositRecordPdfViewForm(accgDocSeq);
	}

	/**
	 * 支払条件情報表示用を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.PaymentPlanConditionViewForm getPaymentPlanConditionViewForm(Long accgDocSeq) throws DataNotFoundException {
		// 支払分割条件データ取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書データが存在しません。");
		}

		Long invoiceSeq = tAccgInvoiceEntity.getInvoiceSeq();

		// 画面オブジェクトを作成
		AccgInvoiceStatementViewForm.PaymentPlanConditionViewForm viewForm = new AccgInvoiceStatementViewForm.PaymentPlanConditionViewForm();
		viewForm.setInvoiceSeq(invoiceSeq);
		viewForm.setIssued(IssueStatus.isIssued(tAccgInvoiceEntity.getInvoiceIssueStatus()));

		TAccgInvoicePaymentPlanConditionEntity TAccgInvoicePaymentPlanConditionEntity = tAccgInvoicePaymentPlanConditionDao.selectInvoicePaymentPlanConditionByAccgDocSeq(accgDocSeq);
		if (TAccgInvoicePaymentPlanConditionEntity == null) {
			// 支払条件を作成していない場合は、ここで終了
			return viewForm;
		}

		// 支払条件情報を設定
		viewForm.setFractionalMonthType(FractionalMonthType.of(TAccgInvoicePaymentPlanConditionEntity.getFractionalMonthType()));
		viewForm.setMonthPaymentAmount(AccountingUtils.toDispAmountLabel(TAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount()));
		viewForm.setMonthPaymentDate(TAccgInvoicePaymentPlanConditionEntity.getMonthPaymentDd());
		viewForm.setPaymentStartDate(TAccgInvoicePaymentPlanConditionEntity.getPaymentStartDate());

		if (isInvoicePlanPdfReCreateFailured(invoiceSeq)) {
			// 支払計画の再作成に失敗している場合
			viewForm.setLostPlanPdf(true);
		}

		if (needInvoicePlanReBuild(invoiceSeq) || isOldInvoicePlan(invoiceSeq)) {
			// 再作成が必要なデータが存在する場合
			viewForm.setNeedReCreatePlan(true);
		}

		// 支払計画データ取得
		List<AccgInvoicePaymentPlanBean> paymentPlanList = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanSalesByAccgDocSeq(accgDocSeq);
		if (!CollectionUtils.isEmpty(paymentPlanList)) {
			viewForm.setCanPlanEdit(true);
			viewForm.setNumberOfPayment(paymentPlanList.size());
		}

		// 支払計画書の画像情報を設定
		List<String> pngHtmlImgSrc = commonAccgInvoiceStatementService.getAccgPdfImgHtmlSrcList(accgDocSeq, AccgDocFileType.INVOICE_PAYMENT_PLAN);
		// 画像情報を設定
		viewForm.setPlanPngSrc(pngHtmlImgSrc);

		return viewForm;
	}

	/**
	 * 支払分割条件入力用オブジェクトを作成する
	 * 
	 * @return
	 * @throws AppException
	 */
	public AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm createPaymentPlanConditionInputForm(Long invoiceSeq) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// オブジェクトの生成
		var paymentPlanConditionInputForm = new AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm();
		paymentPlanConditionInputForm.setInvoiceSeq(invoiceSeq);

		// 表示用プロパティの設定
		this.setDispProperties(paymentPlanConditionInputForm);

		TAccgInvoicePaymentPlanConditionEntity tAccgInvoicePaymentPlanConditionEntity = tAccgInvoicePaymentPlanConditionDao.selectAccgInvoicePaymentPlanConditionByInvoiceSeq(invoiceSeq);
		if (tAccgInvoicePaymentPlanConditionEntity == null) {
			// 新規用 入力プロパティを設定
			paymentPlanConditionInputForm.setNew(true);

			// 初期値の設定
			paymentPlanConditionInputForm.setSeisanShiharaiMonthDay(SeisanShiharaiMonthDay.LASTDAY.getCd());
			paymentPlanConditionInputForm.setFractionalMonthType(FractionalMonthType.FIRST.getCd());

		} else {
			// 編集用 入力プロパティを設定
			paymentPlanConditionInputForm.setNew(false);
			paymentPlanConditionInputForm.setMonthPaymentAmount(AccountingUtils.toDispAmountLabel(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount()));
			Optional<LocalDate> paymentStartDateOption = Optional.ofNullable(tAccgInvoicePaymentPlanConditionEntity.getPaymentStartDate());
			paymentPlanConditionInputForm.setYear(paymentStartDateOption.map(LocalDate::getYear).map(LoiozNumberUtils::parseAsString).orElse(null));
			paymentPlanConditionInputForm.setMonth(paymentStartDateOption.map(LocalDate::getMonthValue).map(LoiozNumberUtils::parseAsString).orElse(null));
			paymentPlanConditionInputForm.setMonthPaymentDd(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentDd());
			paymentPlanConditionInputForm.setFractionalMonthType(tAccgInvoicePaymentPlanConditionEntity.getFractionalMonthType());

			if (tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentDd() != null && LoiozNumberUtils.parseAsInteger(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentDd()) == 31) {
				// 支払日が31に設定されている場合 -> 月末
				paymentPlanConditionInputForm.setSeisanShiharaiMonthDay(SeisanShiharaiMonthDay.LASTDAY.getCd());
			} else {
				// 支払日が31日以外(仕様上 1 ~ 29 or null)の場合 -> 日払い
				paymentPlanConditionInputForm.setSeisanShiharaiMonthDay(SeisanShiharaiMonthDay.DESIGNATEDDAY.getCd());
			}
		}
		return paymentPlanConditionInputForm;
	}

	/**
	 * 支払分割条件入力用オブジェクト：表示用プロパティの設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm inputForm) {

		TAccgInvoicePaymentPlanConditionEntity tAccgInvoicePaymentPlanConditionEntity = tAccgInvoicePaymentPlanConditionDao.selectAccgInvoicePaymentPlanConditionByInvoiceSeq(inputForm.getInvoiceSeq());
		if (tAccgInvoicePaymentPlanConditionEntity != null) {
			inputForm.setYearSelectOptionFormList(this.createYearSelectOptionList(tAccgInvoicePaymentPlanConditionEntity.getPaymentStartDate()));
		} else {
			inputForm.setYearSelectOptionFormList(this.createYearSelectOptionList(null));
		}
	}

	/**
	 * 請求書のPDF表示
	 * 
	 * @param invoiceSeq
	 * @param response
	 */
	public void docInvoicePdfPreview(Long invoiceSeq, HttpServletResponse response) {

		Long accgDocSeq = getAccgDocSeq(invoiceSeq);
		
		AccgDocFileBean accgDocFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(accgDocSeq, AccgDocFileType.INVOICE);
		if (accgDocFileBean == null) {
			throw new DataNotFoundException("請求書PDFが存在しません");
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
			throw new RuntimeException("請求書PDF取得に失敗しました", ex);
		}
	}

	/**
	 * 実費明細のPDF表示
	 * 
	 * @param invoiceSeq
	 * @param response
	 */
	public void dipositRecordPdfPreview(Long invoiceSeq, HttpServletResponse response) {

		Long accgDocSeq = getAccgDocSeq(invoiceSeq);
		
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
	 * 支払計画書のPDF表示
	 * 
	 * @param invoiceSeq
	 * @param response
	 */
	public void planPreview(Long invoiceSeq, HttpServletResponse response) {

		Long accgDocSeq = getAccgDocSeq(invoiceSeq);
		
		AccgDocFileBean accgDocFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(accgDocSeq, AccgDocFileType.INVOICE_PAYMENT_PLAN);
		if (accgDocFileBean == null) {
			throw new DataNotFoundException("分割予定表が存在しません");
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
			throw new RuntimeException("分割予定表取得に失敗しました", ex);
		}
	}

	
	//=========================================================================
	// ▼ チェック、バリデーション系
	//=========================================================================
	
	/**
	 * 請求書が下書き状態か確認します。<br>
	 * 下書きの場合は true を返します。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public boolean checkIfInvoiceIsDraftStatus(Long accgDocSeq) throws AppException {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 発行ステータスが下書きか確認
		String issueStatus = tAccgInvoiceEntity.getInvoiceIssueStatus();
		if (IssueStatus.DRAFT.equalsByCode(issueStatus)) {
			return true;
		} else {
			return false;
		}
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
	 * 対象の会計書類（請求書／精算書）が預り金請求のデータを作成しており、かつ、<br>
	 * その預り金請求データが、他の請求書／精算書で利用されているかどうかを判定する。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public boolean isCreatedDepositInvoiceAndUsingOther(Long accgDocSeq) {
		return commonAccgService.isCreatedDepositInvoiceAndUsingOther(accgDocSeq);
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
	 * DB登録値：請求項目 ― 画面で入力した既入金 と、登録済み請求額が同じかを確認します<br>
	 * 
	 * @param accgDocSeq
	 * @param repayInputForm
	 * @return
	 */
	public boolean checkInvoiceAmountChange(Long accgDocSeq, AccgInvoiceStatementInputForm.RepayInputForm repayInputForm) {

		// 請求書詳細画面で入力した既入金項目のデータを取得
		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();

		// 既入金項目の計算
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDtoUsingRepayRowInputForm(repayRowList, accgDocSeq);
		// 画面の請求額
		BigDecimal invoiceAmount = accgInvoiceStatementAmountDto.getInvoiceAmount();

		// DBに登録してある請求額
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		BigDecimal registeredTotal = tAccgInvoiceEntity.getInvoiceAmount();

		// 請求額が画面とDB値で異なる場合
		if (invoiceAmount.compareTo(registeredTotal) != 0) {
			return true;
		}
		return false;
	}

	/**
	 * 画面で入力した請求項目の合計 ― DB登録値：既入金 と、登録済み請求額が同じかを確認します<br>
	 * 
	 * @param accgDocSeq
	 * @param invoiceInputForm
	 * @return
	 */
	public boolean checkInvoiceAmountChange(Long accgDocSeq, AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) {
		// 請求書詳細画面で入力した請求項目のデータを取得
		List<InvoiceRowInputForm> invoiceRowList = invoiceInputForm.getInvoiceRowList();

		// 請求項目の計算
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(invoiceRowList, accgDocSeq);
		// 画面の請求額
		BigDecimal invoiceAmount = accgInvoiceStatementAmountDto.getInvoiceAmount();

		// DBに登録してある請求額
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		BigDecimal registeredTotal = tAccgInvoiceEntity.getInvoiceAmount();

		// 合計額が画面とDB値で異なる場合
		if (invoiceAmount.compareTo(registeredTotal) != 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 請求書発行前のチェック処理
	 * 
	 * @param requestSourceAccgDocType リクエスト元の会計書類種別
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void checkOfBeforeIssue(AccgDocType requestSourceAccgDocType, Long accgDocSeq) throws AppException {
		commonAccgInvoiceStatementService.checkOfBeforeIssue(requestSourceAccgDocType, accgDocSeq);
	}

	/**
	 * 請求額が0以下かチェックします。<br>
	 * 0以下の場合trueを返します。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public boolean checkIfInvoiceAmountIsMinus(Long accgDocSeq) {
		boolean isInvoiceAmountNegative = false;

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		BigDecimal invoiceAmount = tAccgInvoiceEntity.getInvoiceAmount();
		if (BigDecimal.ZERO.compareTo(invoiceAmount) >= 0) {
			isInvoiceAmountNegative = true;
		}
		return isInvoiceAmountNegative;
	}
	
	/**
	 * 請求書の支払計画を添付するフラグが立っている かつ 作成されているか確認します<br>
	 * 作成されていればtrueを返します。
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	public boolean checkIfPaymentPlanCreated(Long invoiceSeq) {
		boolean isPaymentPlanCreated = false;

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[invoiceSeq=" + invoiceSeq + "]");
		}

		// 支払計画を添付しない
		if (SystemFlg.FLG_OFF.equalsByCode(tAccgInvoiceEntity.getPaymentPlanAttachFlg())) {
			return isPaymentPlanCreated;
		}

		// 支払計画が作成されているか
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntityList = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(invoiceSeq);
		if (!CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntityList)) {
			isPaymentPlanCreated = true;
		}

		return isPaymentPlanCreated;
	}

	/**
	 * 請求書に実費明細添付するかしないか<br>
	 * 実費明細を添付する場合は、true を返します。<br>
	 * 
	 * @param invoiceSeq
	 * @return
	 * @throws AppException
	 */
	public boolean checkDepositDetailAttachFlg(Long invoiceSeq) throws AppException {
		boolean isDepositDetailAttachFlg = true;
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			// 請求書データが存在しない場合
			throw new DataNotFoundException("請求書データが存在しません。");
		}

		if (!SystemFlg.codeToBoolean(tAccgInvoiceEntity.getDepositDetailAttachFlg())) {
			// 実費明細添付しない場合、作成しない
			isDepositDetailAttachFlg = false;
		}

		return isDepositDetailAttachFlg;
	}
	
	/**
	 * 支払分割条件：保存処理入力相関バリデーション
	 * 
	 * @param inputForm
	 * @param result
	 */
	public void savePaymentPlanConditionValidate(AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm inputForm, BindingResult result) {

		if (SeisanShiharaiMonthDay.DESIGNATEDDAY.equalsByCode(inputForm.getSeisanShiharaiMonthDay()) && inputForm.getMonthPaymentDdInteger() == null) {
			// 日払いの場合に、日付が入力されていない
			result.rejectValue("monthPaymentDd", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(), null, "");
		}
	}
	
	//=========================================================================
	// ▼ 登録／更新／削除系
	//=========================================================================
	
	/**
	 * 請求書詳細の設定情報を保存します。
	 * 
	 * @param accgDocSeq
	 * @param settingInputForm
	 * @throws AppException
	 */
	public void saveSetting(Long accgDocSeq, SettingInputForm settingInputForm) throws AppException {
		// 請求書情報を取得
		TAccgInvoiceEntity entity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (entity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 請求書が発行されている場合は、画面で請求方法と添付資料が変更できないため変更をしない
		if (checkIfInvoiceIsDraftStatus(accgDocSeq)) {
			entity.setInvoiceType(settingInputForm.getInvoiceType());
			entity.setDepositDetailAttachFlg(settingInputForm.isDepositDetailAttachFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
			entity.setPaymentPlanAttachFlg(settingInputForm.isPaymentPlanAttachFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
		}
		entity.setSalesDate(DateUtils.parseToLocalDate(settingInputForm.getSalesDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		entity.setSalesAccountSeq(settingInputForm.getSalesAccount());

		int updateCount = 0;
		try {
			updateCount = tAccgInvoiceDao.update(entity);
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
	 * 請求書詳細の設定情報を保存します。
	 * 
	 * @param accgDocSeq
	 * @param settingInputForm
	 * @throws AppException
	 */
	public void saveMemo(Long accgDocSeq, MemoInputForm memoInputForm) throws AppException {
		// 請求書情報を取得
		TAccgInvoiceEntity entity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (entity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		entity.setInvoiceMemo(memoInputForm.getMemo());

		// 更新
		this.updateAccgInvoice(entity);
	}

	/**
	 * 基本情報_タイトル情報を保存し会計書類ファイルの再作成フラグをたてます。<br>
	 * 
	 * @param accgDocSeq
	 * @param baseTitleInputForm
	 * @throws AppException
	 */
	public void saveBaseTitle(Long accgDocSeq, BaseTitleInputForm baseTitleInputForm) throws AppException {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 基本情報_タイトルを更新
		this.updateBaseTitle(tAccgInvoiceEntity, baseTitleInputForm);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「請求書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「実費明細」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.DEPOSIT_DETAIL);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「支払計画書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE_PAYMENT_PLAN);
	}

	/**
	 * 基本情報_請求先情報を保存し会計書類ファイルの再作成フラグをたてます。
	 * 
	 * @param accgDocSeq
	 * @param baseToInputForm
	 * @throws AppException
	 */
	public void saveBaseTo(Long accgDocSeq, BaseToInputForm baseToInputForm) throws AppException {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 基本情報_請求先を更新
		this.updateBaseTo(tAccgInvoiceEntity, baseToInputForm);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「請求書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「実費明細」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.DEPOSIT_DETAIL);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「支払計画書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE_PAYMENT_PLAN);
	}

	/**
	 * 基本情報_請求元情報を保存します
	 * 
	 * @param accgDocSeq
	 * @param baseFromInputForm
	 * @throws AppException
	 */
	public void saveBaseFrom(Long accgDocSeq, BaseFromInputForm baseFromInputForm) throws AppException {
		// 請求書情報を取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 基本情報_請求元を更新
		this.updateBaseFrom(tAccgInvoiceEntity, baseFromInputForm);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「請求書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「実費明細」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.DEPOSIT_DETAIL);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「支払計画書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE_PAYMENT_PLAN);
	}

	/**
	 * 基本情報_挿入文情報を保存します
	 * 
	 * @param accgDocSeq
	 * @param baseOtherInputForm
	 * @throws AppException
	 */
	public void saveBaseOther(Long accgDocSeq, BaseOtherInputForm baseOtherInputForm) throws AppException {
		// 請求書情報を取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 基本情報_挿入文を更新
		this.updateBaseOther(tAccgInvoiceEntity, baseOtherInputForm);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「請求書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「実費明細」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.DEPOSIT_DETAIL);

		// 基本情報_タイトルで変更があった場合、会計書類ファイルの「支払計画書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE_PAYMENT_PLAN);
	}

	/**
	 * 請求書詳細の既入金情報を保存する
	 * 
	 * @param invoiceSeq
	 * @param repayInputForm
	 * @throws AppException
	 */
	public void saveRepay(Long invoiceSeq, AccgInvoiceStatementInputForm.RepayInputForm repayInputForm) throws AppException {
		// 既入金情報を保存
		commonAccgInvoiceStatementService.saveRepay(repayInputForm);
	}

	/**
	 * 請求書詳細の請求情報を保存する
	 * 
	 * @param invoiceSeq
	 * @param invoiceInputForm
	 * @throws AppException
	 */
	public void saveInvoice(Long invoiceSeq, AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) throws AppException {
		// 請求項目情報を保存
		commonAccgInvoiceStatementService.saveInvoice(invoiceInputForm);
	}
	
	/**
	 * 振込先情報を保存します
	 * 
	 * @param accgDocSeq
	 * @param bankDetailInputForm
	 * @throws AppException
	 */
	public void saveBankDetail(Long accgDocSeq, BankDetailInputForm bankDetailInputForm) throws AppException {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 振込先情報を更新
		this.updateBankDetail(tAccgInvoiceEntity, bankDetailInputForm);

		// 振込情報で変更があった場合、会計書類ファイルの「請求書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE);

		// 振込情報で変更があった場合、会計書類ファイルの「実費明細」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.DEPOSIT_DETAIL);

		// 振込情報で変更があった場合、会計書類ファイルの「支払計画書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE_PAYMENT_PLAN);
	}

	/**
	 * 備考情報を保存します
	 * 
	 * @param accgDocSeq
	 * @param remarksInputForm
	 * @throws AppException
	 */
	public void saveRemarks(Long accgDocSeq, RemarksInputForm remarksInputForm) throws AppException {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 備考情報を更新
		this.updateInvoiceRemarks(tAccgInvoiceEntity, remarksInputForm);

		// 備考情報で変更があった場合、会計書類ファイルの「請求書」に再作成実行待ちフラグを立てる
		commonAccgService.updateAccgDocReBuildFlg2On(Arrays.asList(accgDocSeq), AccgDocFileType.INVOICE);
	}

	/**
	 * 発行処理
	 *
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void issue(Long accgDocSeq, Long ankenId, Long personId) throws AppException {

		// 請求書発行処理のバリデーション
		this.validateUnChangedFormatIssue(accgDocSeq);

		// 請求書の発行処理を行う
		commonAccgInvoiceStatementService.issue(accgDocSeq, ankenId, personId, AccgDocType.INVOICE);
	}

	/**
	 * 会計書類SEQに紐づく請求書データを基に精算書データを作成し、請求書に関するデータは削除します<br>
	 * 
	 * <pre>
	 * S3オブジェクトキーをDBから削除するため、S3オブジェクトファイル削除を呼び出し元コントローラーで実施すること
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @param personId
	 * @param ankenId
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 */
	public Long changeToStatement(Long accgDocSeq, Long ankenId, Set<String> deleteS3ObjectKeys) throws AppException {
		// 会計書類のドキュメントタイプ変更
		commonAccgInvoiceStatementService.updateAccgDocType(accgDocSeq, AccgDocType.STATEMENT);

		// 請求書情報から精算書情報の作成
		TAccgStatementEntity tAccgStatementEntity = this.createStatementBasedOnInvoice(accgDocSeq, ankenId);

		// 請求書情報の削除
		this.deleteAccgInvoice(accgDocSeq, deleteS3ObjectKeys);

		return tAccgStatementEntity.getStatementSeq();
	}
	
	/**
	 * 請求書を発行前に戻す処理
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void updateInvoiceToDraftAndRemoveRelatedData(Long accgDocSeq, Long ankenId, Long personId) throws AppException {
		// 下書き変更処理
		commonAccgInvoiceStatementService.updateInvoiceToDraftAndRemoveRelatedData(accgDocSeq, personId, ankenId);
	}

	/**
	 * 請求書削除処理
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void delete(Long accgDocSeq, Long ankenId, Long personId) throws AppException {

		// 送付済みの書類があるか確認
		if (checkIfAccgDocHasBeenSent(accgDocSeq)) {
			// 書類を送付している場合は削除不可とする
			throw new AppException(MessageEnum.MSG_E00086, null, "送付済みの");
		}
		
		// この請求書から作成された預り金請求のデータが、他の請求書／精算書で利用されているか
		boolean isCreatedDepositInvoiceAndUsingOther = this.isCreatedDepositInvoiceAndUsingOther(accgDocSeq);
		if (isCreatedDepositInvoiceAndUsingOther) {
			// 利用されている場合は削除不可とする
			throw new AppException(MessageEnum.MSG_E00086, null, "この請求書で請求を行った預り金が、<br>他の請求書／精算書で利用されている");
		}
		
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 売上明細SEQ
		Long salesDetailSeq = tAccgInvoiceEntity.getSalesDetailSeq();
		// 回収不能金詳細SEQ
		Long uncollectibleDetailSeq = tAccgInvoiceEntity.getUncollectibleDetailSeq();

		// 支払分割条件データ、支払計画データ
		TAccgInvoicePaymentPlanConditionEntity tAccgInvoicePaymentPlanConditionEntity = tAccgInvoicePaymentPlanConditionDao.selectAccgInvoicePaymentPlanConditionByInvoiceSeq(tAccgInvoiceEntity.getInvoiceSeq());
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntityList = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(tAccgInvoiceEntity.getInvoiceSeq());

		// 請求書の関連データを削除
		try {
			// 請求書情報削除
			tAccgInvoiceDao.delete(tAccgInvoiceEntity);

			if (tAccgInvoicePaymentPlanConditionEntity != null) {
				// 支払分割条件データが存在する場合 -> 削除処理
				tAccgInvoicePaymentPlanConditionDao.delete(tAccgInvoicePaymentPlanConditionEntity);
			}

			if (!CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntityList)) {
				// 支払計画データが存在する場合 -> 削除処理
				tAccgInvoicePaymentPlanDao.batchDelete(tAccgInvoicePaymentPlanEntityList);
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 請求書、精算書削除時の共通処理
		commonAccgInvoiceStatementService.commonDeleteForInvoiceAndStatements(accgDocSeq, personId, ankenId, salesDetailSeq, uncollectibleDetailSeq);
	}

	/**
	 * 請求書の作成/再作成処理
	 * 
	 * <pre>
	 * 画面表示より前に別のトランザクションで行うことを想定
	 * 
	 * ・請求書画面_請求書タブの表示
	 * ・発行処理 など
	 * </pre>
	 *
	 * @param invoiceSeq
	 * @param tenantSeq
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 */
	public void beforeTransactionalDocInvoicePdfCreate(Long invoiceSeq, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			// 請求書データが存在しない場合
			throw new DataNotFoundException("請求書データが存在しません。");
		}

		if (this.needCreateInvoicePdf(tAccgInvoiceEntity)) {
			// PDFが未作成 || 再作成フラグが１のデータが存在する -> PDFの作成/再作成を行う
			commonAccgService.createAccgDocFileAndDetail(tAccgInvoiceEntity.getAccgDocSeq(), tenantSeq, true, deleteS3ObjectKeys, AccgDocFileType.INVOICE);
		}
	}

	/**
	 * 請求書PDFの生成が必要か
	 * 
	 * @param invoiceSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public boolean needCreateInvoicePdf(Long invoiceSeq) throws DataNotFoundException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			// 請求書データが存在しない場合
			throw new DataNotFoundException("請求書データが存在しません。");
		}

		return this.needCreateInvoicePdf(tAccgInvoiceEntity);
	}

	/**
	 * 請求書PDFの生成が必要か
	 * 
	 * @param tAccgInvoiceEntity
	 * @return
	 */
	private boolean needCreateInvoicePdf(TAccgInvoiceEntity tAccgInvoiceEntity) {

		if (IssueStatus.isIssued(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
			// 発行済の場合は、再作成は行わない
			return false;
		}

		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE);

		// PDFファイルのみに絞り込み
		List<AccgDocFileBean> accgDocFilePdfBeans = accgDocFileBeans.stream().filter(e -> Objects.equals(e.getFileExtension(), FileExtension.PDF.getVal())).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(accgDocFilePdfBeans) || accgDocFileBeans.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getRecreateStandbyFlg()))) {
			// PDFが未作成 || 再作成フラグが１のデータが存在する -> PDFの作成/再作成が必要
			return true;
		}

		return false;
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
	 * @param accgDocSeq
	 * @param tenantSeq
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 */
	public void beforeTransactionalDipositRecordPdfCreate(Long accgDocSeq, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			// 請求書データが存在しない場合
			throw new DataNotFoundException("請求書データが存在しません。");
		}

		if (this.needCreateDipositRecordPdf(tAccgInvoiceEntity)) {
			// PDFが未作成 || 再作成フラグが１のデータが存在する -> PDFの作成/再作成を行う
			commonAccgService.createAccgDocFileAndDetail(accgDocSeq, tenantSeq, true, deleteS3ObjectKeys, AccgDocFileType.DEPOSIT_DETAIL);
		}
	}

	/**
	 * 実費明細PDFの生成が必要か
	 * 
	 * @param invoiceSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public boolean needCreateDipositRecordPdf(Long invoiceSeq) throws DataNotFoundException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			// 請求書データが存在しない場合
			throw new DataNotFoundException("請求書データが存在しません。");
		}

		return needCreateDipositRecordPdf(tAccgInvoiceEntity);
	}

	/**
	 * 実費明細PDFの生成が必要か
	 * 
	 * @param tAccgInvoiceEntity
	 * @return
	 */
	private boolean needCreateDipositRecordPdf(TAccgInvoiceEntity tAccgInvoiceEntity) {

		if (IssueStatus.isIssued(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
			// 発行済みの場合、再作成は行わない
			return false;
		}

		if (!SystemFlg.codeToBoolean(tAccgInvoiceEntity.getDepositDetailAttachFlg())) {
			// 実費明細添付しないなら作成しない
			return false;
		}

		// 取引実績ファイル情報を取得
		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.DEPOSIT_DETAIL);

		// PDFファイルのみに絞り込み
		List<AccgDocFileBean> accgDocFilePdfBeans = accgDocFileBeans.stream().filter(e -> Objects.equals(e.getFileExtension(), FileExtension.PDF.getVal())).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(accgDocFilePdfBeans) || accgDocFileBeans.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getRecreateStandbyFlg()))) {
			// PDFが未作成 || 再作成フラグが１のデータが存在する -> PDFの作成/再作成が必要
			return true;
		}

		return false;
	}

	/**
	 * 支払計画書のPDFを再ビルドを処理を行う ※
	 * この処理は、発行済みの状態から下書きに戻したときに支払計画情報は存在するが、PDFデータが存在しない場合を想定している
	 *
	 * @param accgDocSeq
	 * @param tenantSeq
	 * @throws AppException
	 */
	public void rebuildInvoicePlanPdf(Long invoiceSeq, Long tenantSeq) throws AppException {

		try {
			Long accgDocSeq = getAccgDocSeq(invoiceSeq);
			List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntities = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByAccgDocSeq(accgDocSeq);
			if (CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntities)) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			// 再ビルド処理では、PDFデータの削除を許容しない(削除S3Objectkeyは発生しない)
			// 再ビルド処理では、再作成フラグをONの状態で作成する
			commonAccgService.createAccgDocFileAndDetailForRebuild(accgDocSeq, tenantSeq, false, null, AccgDocFileType.INVOICE_PAYMENT_PLAN);
		} catch (DataNotFoundException e) {
			// DataNotFoundExceptionはAppExceptionとして処理する
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}
	
	/**
	 * 支払分割条件：登録処理 ※ S3APIコールを含む
	 * 
	 * @param inputForm
	 */
	public void registPaymentPlanCondition(AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm inputForm, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		TAccgInvoicePaymentPlanConditionEntity tAccgInvoicePaymentPlanConditionEntity = new TAccgInvoicePaymentPlanConditionEntity();
		tAccgInvoicePaymentPlanConditionEntity.setInvoiceSeq(inputForm.getInvoiceSeq());
		tAccgInvoicePaymentPlanConditionEntity.setMonthPaymentAmount(inputForm.getMonthPaymentAmountDecimal());
		tAccgInvoicePaymentPlanConditionEntity.setFractionalMonthType(FractionalMonthType.of(inputForm.getFractionalMonthType()).getCd());

		if (SeisanShiharaiMonthDay.LASTDAY.equalsByCode(inputForm.getSeisanShiharaiMonthDay())) {
			// 月末の場合
			tAccgInvoicePaymentPlanConditionEntity.setMonthPaymentDd(DateUtils.END_OF_MONTH);
			tAccgInvoicePaymentPlanConditionEntity.setPaymentStartDate(DateUtils.getLastDateOfThisMonth(inputForm.getYearInteger(), inputForm.getMonthInteger()));

		} else if (SeisanShiharaiMonthDay.DESIGNATEDDAY.equalsByCode(inputForm.getSeisanShiharaiMonthDay())) {

			LocalDate paymentStartDate = DateUtils.parseToLocalDate(
					String.format("%s/%s/%s", inputForm.getYear(), StringUtils.leftPad(inputForm.getMonth(), 2, CommonConstant.ZERO), StringUtils.leftPad(inputForm.getMonthPaymentDd(), 2, CommonConstant.ZERO)),
					DateUtils.DATE_FORMAT_SLASH_DELIMITED,
					ResolverStyle.SMART);

			tAccgInvoicePaymentPlanConditionEntity.setMonthPaymentDd(inputForm.getMonthPaymentDd());
			tAccgInvoicePaymentPlanConditionEntity.setPaymentStartDate(paymentStartDate);

		} else {
			// 想定外のEnum値
			throw new RuntimeException("Enum値が想定外です");
		}

		// invoiceSeqに紐づく支払分割条件が既に登録されている場合は楽観ロックエラー
		TAccgInvoicePaymentPlanConditionEntity entity = tAccgInvoicePaymentPlanConditionDao.selectAccgInvoicePaymentPlanConditionByInvoiceSeq(inputForm.getInvoiceSeq());
		if (entity != null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 支払分割条件を作成
			tAccgInvoicePaymentPlanConditionDao.insert(tAccgInvoicePaymentPlanConditionEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 支払計画データを作成する
		this.createInvoicePaymentPlan(inputForm.getInvoiceSeq(), false, tenantSeq, deleteS3ObjectKeys);
	}

	/**
	 * 支払分割条件：更新処理 ※ S3APIコールを含む
	 * 
	 * @param inputForm
	 */
	public void updatePaymentPlanCondition(AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm inputForm, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		TAccgInvoicePaymentPlanConditionEntity tAccgInvoicePaymentPlanConditionEntity = tAccgInvoicePaymentPlanConditionDao.selectAccgInvoicePaymentPlanConditionByInvoiceSeq(inputForm.getInvoiceSeq());
		tAccgInvoicePaymentPlanConditionEntity.setMonthPaymentAmount(inputForm.getMonthPaymentAmountDecimal());
		tAccgInvoicePaymentPlanConditionEntity.setFractionalMonthType(FractionalMonthType.of(inputForm.getFractionalMonthType()).getCd());

		if (SeisanShiharaiMonthDay.LASTDAY.equalsByCode(inputForm.getSeisanShiharaiMonthDay())) {
			// 月末の場合
			tAccgInvoicePaymentPlanConditionEntity.setMonthPaymentDd(DateUtils.END_OF_MONTH);
			tAccgInvoicePaymentPlanConditionEntity.setPaymentStartDate(DateUtils.getLastDateOfThisMonth(inputForm.getYearInteger(), inputForm.getMonthInteger()));

		} else if (SeisanShiharaiMonthDay.DESIGNATEDDAY.equalsByCode(inputForm.getSeisanShiharaiMonthDay())) {

			LocalDate paymentStartDate = DateUtils.parseToLocalDate(
					String.format("%s/%s/%s", inputForm.getYear(), StringUtils.leftPad(inputForm.getMonth(), 2, CommonConstant.ZERO), StringUtils.leftPad(inputForm.getMonthPaymentDd(), 2, CommonConstant.ZERO)),
					DateUtils.DATE_FORMAT_SLASH_DELIMITED,
					ResolverStyle.SMART);

			tAccgInvoicePaymentPlanConditionEntity.setMonthPaymentDd(inputForm.getMonthPaymentDd());
			tAccgInvoicePaymentPlanConditionEntity.setPaymentStartDate(paymentStartDate);

		} else {
			// 想定外のEnum値
			throw new RuntimeException("Enum値が想定外です");
		}

		try {
			// 支払分割条件を作成
			tAccgInvoicePaymentPlanConditionDao.update(tAccgInvoicePaymentPlanConditionEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 支払計画データを作成する
		this.createInvoicePaymentPlan(inputForm.getInvoiceSeq(), true, tenantSeq, deleteS3ObjectKeys);
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
	
	//=========================================================================
	// ▼ 送付処理系
	//=========================================================================
	
	/**
	 * 請求書画面：会計書類送付入力フォームオブジェクトを作成
	 *
	 * @param invoiceSeq
	 * @param sendType
	 * @param loginAccountSeq
	 * @param tenantName
	 * @param selectedMailTemplateSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementInputForm.FileSendInputForm createInvoiceFileSendInputForm(Long invoiceSeq, AccgDocSendType sendType, Long loginAccountSeq, String tenantName, Long selectedMailTemplateSeq) throws DataNotFoundException {

		TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntity(invoiceSeq);

		var invoiceFileSendInputForm = new AccgInvoiceStatementInputForm.FileSendInputForm();
		invoiceFileSendInputForm.setAccgDocSeq(tAccgInvoiceEntity.getAccgDocSeq());
		invoiceFileSendInputForm.setSendType(sendType.getCd());

		// 表示用プロパティの設定
		setDispProperties(invoiceFileSendInputForm);

		// 送付先の初期値を設定
		String yusenMailAddress = commonPersonService.getYusenMailAddress(tAccgInvoiceEntity.getBillToPersonId());
		invoiceFileSendInputForm.setSendTo(yusenMailAddress);

		// 送付元名の初期値を設定
		invoiceFileSendInputForm.setSendFromName(tAccgInvoiceEntity.getInvoiceFromTenantName());

		// 署名を設定
		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(loginAccountSeq);
		invoiceFileSendInputForm.setMailSignature(mAccountEntity.getAccountMailSignature());

		if (sendType == AccgDocSendType.WEB) {
			// パスワード設定の規定フラグを設定
			MMailSettingEntity mMailSettingEntity = mMailSettingDao.select();
			invoiceFileSendInputForm.setDownloadViewPasswordEnabled(SystemFlg.codeToBoolean(mMailSettingEntity.getDownloadViewPasswordEnableFlg()));
		}

		// 規定データの設定
		MMailTemplateEntity mMailTemplateEntity;
		if (selectedMailTemplateSeq == null) {
			// デフォルトデータを取得
			mMailTemplateEntity = mMailTemplateDao.selectDefaultUseMailTemplateByMailTemplateType(MailTemplateType.INVOICE);
		} else {
			// 選択したデータを設定
			mMailTemplateEntity = mMailTemplateDao.selectBySeq(selectedMailTemplateSeq);
		}
		if (mMailTemplateEntity != null) {
			invoiceFileSendInputForm.setTemplateSeq(mMailTemplateEntity.getMailTemplateSeq());
			invoiceFileSendInputForm.setSendCc(mMailTemplateEntity.getMailCc());
			invoiceFileSendInputForm.setSendBcc(mMailTemplateEntity.getMailBcc());
			invoiceFileSendInputForm.setReplyTo(mMailTemplateEntity.getMailReplyTo());
			invoiceFileSendInputForm.setSendSubject(mMailTemplateEntity.getSubject());
			invoiceFileSendInputForm.setSendBody(
					commonAccgInvoiceStatementService.generateDefaultInputSendBody(
							tenantName,
							tAccgInvoiceEntity.getInvoiceToName(),
							tAccgInvoiceEntity.getInvoiceToNameEnd(),
							mMailTemplateEntity.getContents()));
		} else {
			invoiceFileSendInputForm.setSendBody(
					commonAccgInvoiceStatementService.generateDefaultInputSendBody(
							tenantName,
							tAccgInvoiceEntity.getInvoiceToName(),
							tAccgInvoiceEntity.getInvoiceToNameEnd(),
							""));
		}

		return invoiceFileSendInputForm;
	}

	/**
	 * 請求書画面：会計書類送付入力フォームオブジェクトの表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(AccgInvoiceStatementInputForm.FileSendInputForm inputForm) {
		// 表示用オブジェクトの設定
		inputForm.setAccgDocType(AccgDocType.INVOICE);

		// 送信元メールアドレスを取得
		// ※ 保存処理によって送信されるメールはM0013以外の場合も考えられるが、
		// 利用される送信元メールアドレスはすべて同じになる想定であるため、すべてM0013に登録されているメールアドレスを設定する
		// 今後、保存処理内で送信するメールの送信元メールアドレスに条件分岐等で異なる場合は修正が必要となる
		MMailEntity mMailEntity = mgtMMailDao.selectByMailId(MailIdList.MAIL_13.getCd());
		inputForm.setSendFrom(mMailEntity.getSendFrom());

		// テンプレートオプションを設定
		List<MMailTemplateEntity> mMailTemplateEntities = mMailTemplateDao.selectByTemplateType(MailTemplateType.INVOICE.getCd());
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
					AccgDocType.INVOICE.getVal(),
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
	 * 請求書画面：送付処理
	 *
	 * @param tenantSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void accgInvoiceFileSend(Long tenantSeq, Long loginAccountSeq, AccgInvoiceStatementInputForm.FileSendInputForm inputForm) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(inputForm.getAccgDocSeq());
		if (tAccgInvoiceEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 請求書SEQ
		Long invoiceSeq = tAccgInvoiceEntity.getInvoiceSeq();
		tAccgInvoiceEntity.setInvoiceIssueStatus(IssueStatus.SENT.getCd());

		// 請求書発行ステータスの更新
		this.updateAccgInvoice(tAccgInvoiceEntity);

		// 会計書類-対応データの登録処理
		TAccgDocActEntity tAccgDocActEntity = commonAccgService.registAccgDocAct(inputForm.getAccgDocSeq(), AccgDocActType.SEND);
		Long accgDocActSeq = tAccgDocActEntity.getAccgDocActSeq();

		// 会計書類-対応-送付データの登録処理
		Long accgDocActSendSeq = commonAccgInvoiceStatementService.registAccgDocActSendRecord(accgDocActSeq, inputForm);

		// 請求書：会計書類-対応-送付-ファイルの登録処理
		this.registInvoiceAccgDocActSendFile(invoiceSeq, accgDocActSendSeq);

		AccgDocSendType accgDocSendType = AccgDocSendType.of(inputForm.getSendType());
		if (AccgDocSendType.WEB == accgDocSendType) {

			// WEB共通情報の登録
			Long webDownloadSeq = commonAccgService.registAccgDocWebDownload(accgDocActSendSeq,
					tAccgInvoiceEntity.getInvoiceFromTenantName(), tAccgInvoiceEntity.getInvoiceDate(),
					inputForm.getPassword());

			// WEB共通メール送信
			if (!StringUtils.isEmpty(inputForm.getPassword())) {
				// メール送信(送信先：顧客 URLとパスワード)
				commonAccgService.sendMail4M0013AndM0014(accgDocActSendSeq, inputForm.getPassword(), AccgDocType.INVOICE, tenantSeq, webDownloadSeq, inputForm.getMailSignature());
			} else {
				// メール送信(送信先：顧客 URL)
				commonAccgService.sendMail4M0013(AccgDocType.INVOICE, tenantSeq, accgDocActSendSeq, webDownloadSeq, inputForm.getMailSignature());
			}
		} else if (AccgDocSendType.MAIL == accgDocSendType) {
			// メール送信処理

			// メール送信(送信先：顧客ファイル添付)
			commonAccgService.sendMail4M0015(AccgDocType.INVOICE, tenantSeq, accgDocActSendSeq,
					inputForm.getMailSignature(), inputForm.getAccgDocSeq());

		} else {
			// 想定外のEnum値
			throw new RuntimeException("Eunm値が不正です");
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	//=========================================================================
	// ▼ 取得／データ変換系
	//=========================================================================
	
	/**
	 * 請求書データの取得
	 * 
	 * @param invoiceSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	private TAccgInvoiceEntity getAccgInvoiceEntity(Long invoiceSeq) throws DataNotFoundException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書が存在しません");
		}
		return tAccgInvoiceEntity;
	}

	/**
	 * 請求書詳細画面初期表示時、選択状態にするタブを取得します<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	private AccggInvoiceStatementDetailViewTab getInitInvoiceStatementDetailViewTabByIssueStatus(Long accgDocSeq) {
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		String issueStatus = tAccgInvoiceEntity == null ? IssueStatus.DRAFT.getCd() : tAccgInvoiceEntity.getInvoiceIssueStatus();
		if (IssueStatus.DRAFT.equalsByCode(issueStatus)) {
			return AccggInvoiceStatementDetailViewTab.EDIT_TAB;
		} else {
			return AccggInvoiceStatementDetailViewTab.INVOICE_PDF_TAB;
		}
	}

	/**
	 * 【支払条件画面】年：プルダウン用オブジェクトを作成する
	 * 
	 * @param targetDate
	 * @return
	 */
	private List<SelectOptionForm> createYearSelectOptionList(LocalDate targetDate) {

		// 現在日付を取得する
		LocalDate now = LocalDate.now();

		Integer beforeYearBase = now.getYear();
		Integer afterYearBase = now.getYear();
		if (targetDate != null) {
			Integer targetYear = targetDate.getYear();
			if (beforeYearBase > targetYear) {
				// 登録している支払開始年が現在日付より前の場合 -> 支払開始年からXX年前からプルダウンに表示する
				beforeYearBase = targetYear;
			}
			if (afterYearBase < targetYear) {
				// 登録している支払開始年が現在日付より後の場合 -> 支払開始年からXX年後までプルダウンに表示する
				afterYearBase = targetYear;
			}
		}

		List<SelectOptionForm> yearSelectOptionForm = new ArrayList<>();
		for (int i = (beforeYearBase - YEAR_SELECT_OPTION_BEFORE_NUMBER); i <= (afterYearBase + YEAR_SELECT_OPTION_AFTER_NUMBER); i++) {
			yearSelectOptionForm.add(new SelectOptionForm(i, String.format("%d年", i)));
		}

		return yearSelectOptionForm;
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

	//=========================================================================
	// ▼ チェック、バリデーション系
	//=========================================================================
	
	/**
	 * 請求書の発行時にバリデーション(請求書 -> 精算書のケース)
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	private void validateUnChangedFormatIssue(Long accgDocSeq) throws AppException {

		// 請求書データの取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		Long invoiceSeq = tAccgInvoiceEntity.getInvoiceSeq();

		if (SystemFlg.codeToBoolean(tAccgInvoiceEntity.getPaymentPlanAttachFlg())) {
			// 支払計画を発行する場合
			List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE_PAYMENT_PLAN);
			if (accgDocFileBeans.stream().noneMatch(e -> Objects.equals(e.getFileExtension(), FileExtension.PDF.getVal()))) {
				// 送付済み以外のPDFが存在しない
				throw new AppException(MessageEnum.MSG_E00182, null);
			}

			if (needInvoicePlanReBuild(invoiceSeq)) {
				// 支払計画の再作成フラグが必要な場合
				throw new AppException(MessageEnum.MSG_E00182, null);
			}

			if (isOldInvoicePlan(invoiceSeq)) {
				// 支払計画の金額が現在の請求金額と異なる場合
				throw new AppException(MessageEnum.MSG_E00182, null);
			}

			if (isInvoicePlanPdfReCreateFailured(invoiceSeq)) {
				// 支払計画のPDF作成ができていない場合
				throw new AppException(MessageEnum.MSG_E00182, null);
			}

		}

	}
	
	/**
	 * 支払計画PDFの再作成が必要かどうか
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	private boolean needInvoicePlanReBuild(Long invoiceSeq) {

		// 請求書データの取得
		TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntity(invoiceSeq);

		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE_PAYMENT_PLAN);
		if (accgDocFileBeans.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getRecreateStandbyFlg()))) {
			// 再作成が必要なデータが存在する
			return true;
		}

		return false;
	}

	/**
	 * DBに登録されている支払計画情報が古いか否か ※比較元はDBに登録されている請求金額
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	private boolean isOldInvoicePlan(Long invoiceSeq) throws DataNotFoundException {

		// 請求書データの取得
		TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntity(invoiceSeq);

		// 支払計画情報
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntities = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(invoiceSeq);
		if (CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntities)) {
			// 支払計画が存在しない場合 -> false
			return false;
		}

		List<BigDecimal> planAmountList = tAccgInvoicePaymentPlanEntities.stream().map(TAccgInvoicePaymentPlanEntity::getPaymentScheduleAmount).collect(Collectors.toList());
		BigDecimal planTotalAmount = AccountingUtils.calcTotal(planAmountList);

		return !LoiozNumberUtils.equalsDecimal(tAccgInvoiceEntity.getInvoiceAmount(), planTotalAmount);
	}

	/**
	 * 支払計画PDFの再作成処理が失敗しているか否か
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	private boolean isInvoicePlanPdfReCreateFailured(Long invoiceSeq) throws DataNotFoundException {

		// 請求書データの取得
		TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntity(invoiceSeq);
		Long accgDocSeq = tAccgInvoiceEntity.getAccgDocSeq();

		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntities = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(invoiceSeq);
		if (CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntities)) {
			// 支払計画が存在しない場合 -> false
			return false;
		}

		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(accgDocSeq, AccgDocFileType.INVOICE_PAYMENT_PLAN);
		if (accgDocFileBeans.stream().anyMatch(e -> Objects.equals(e.getFileExtension(), FileExtension.PNG.getVal()))) {
			// 支払計画が存在するのに PNGデータが存在する場合 -> false
			return false;
		}

		return true;
	}
	
	//=========================================================================
	// ▼ 登録／更新／削除系
	//=========================================================================

	/**
	 * 基本情報_タイトルを更新します
	 * 
	 * @param tAccgInvoiceEntity
	 * @param baseTitleInputForm
	 * @throws AppException
	 */
	private void updateBaseTitle(TAccgInvoiceEntity tAccgInvoiceEntity, BaseTitleInputForm baseTitleInputForm) throws AppException {
		// entityにセット
		tAccgInvoiceEntity.setInvoiceTitle(baseTitleInputForm.getBaseTitle());
		tAccgInvoiceEntity.setInvoiceDate(DateUtils.parseToLocalDate(baseTitleInputForm.getBaseDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgInvoiceEntity.setInvoiceNo(baseTitleInputForm.getBaseNo());

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}

	/**
	 * 基本情報_請求先を更新します
	 * 
	 * @param tAccgInvoiceEntity
	 * @param baseToInputForm
	 * @throws AppException
	 */
	private void updateBaseTo(TAccgInvoiceEntity tAccgInvoiceEntity, BaseToInputForm baseToInputForm) throws AppException {
		// entityにセット
		tAccgInvoiceEntity.setInvoiceToDetail(baseToInputForm.getBaseToDetail());
		tAccgInvoiceEntity.setInvoiceToName(baseToInputForm.getBaseToName());
		tAccgInvoiceEntity.setInvoiceToNameEnd(baseToInputForm.getBaseToNameEnd());
		Long changeBillToPersonId = baseToInputForm.getChangeBillToPersonId();
		if (changeBillToPersonId != null) {
			if (!commonAccgService.checkAnkenIsCustomerKanyosha(changeBillToPersonId, baseToInputForm.getAnkenId())) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			tAccgInvoiceEntity.setBillToPersonId(changeBillToPersonId);
		}

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}

	/**
	 * 基本情報_請求元を更新します
	 * 
	 * @param tAccgInvoiceEntity
	 * @param baseFromInputForm
	 * @throws AppException
	 */
	private void updateBaseFrom(TAccgInvoiceEntity tAccgInvoiceEntity, BaseFromInputForm baseFromInputForm) throws AppException {
		// entityにセット
		tAccgInvoiceEntity.setInvoiceFromTenantName(baseFromInputForm.getBaseFromOfficeName());
		tAccgInvoiceEntity.setInvoiceFromDetail(baseFromInputForm.getBaseFromDetail());
		tAccgInvoiceEntity.setTenantStampPrintFlg(baseFromInputForm.isTenantStampPrintFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}

	/**
	 * 基本情報_挿入文を更新します
	 * 
	 * @param tAccgInvoiceEntity
	 * @param baseOtherInputForm
	 * @throws AppException
	 */
	private void updateBaseOther(TAccgInvoiceEntity tAccgInvoiceEntity, BaseOtherInputForm baseOtherInputForm) throws AppException {
		// entityにセット
		tAccgInvoiceEntity.setDueDate(DateUtils.parseToLocalDate(baseOtherInputForm.getDeadline(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAccgInvoiceEntity.setInvoiceSubText(baseOtherInputForm.getSubText());
		tAccgInvoiceEntity.setInvoiceSubject(baseOtherInputForm.getTitle());
		tAccgInvoiceEntity.setDueDatePrintFlg(baseOtherInputForm.isDeadLinePrintFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}

	/**
	 * 振込先情報を更新します
	 * 
	 * @param tAccgInvoiceEntity
	 * @param bankDetailInputForm
	 * @throws AppException
	 */
	private void updateBankDetail(TAccgInvoiceEntity tAccgInvoiceEntity, BankDetailInputForm bankDetailInputForm) throws AppException {
		// entityにセット
		tAccgInvoiceEntity.setTenantBankDetail(bankDetailInputForm.getTenantBankDetail());

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}

	/**
	 * 備考情報を更新します
	 * 
	 * @param tAccgInvoiceEntity
	 * @param remarksInputForm
	 * @throws AppException
	 */
	private void updateInvoiceRemarks(TAccgInvoiceEntity tAccgInvoiceEntity, RemarksInputForm remarksInputForm) throws AppException {
		// entityに備考をセット
		tAccgInvoiceEntity.setInvoiceRemarks(remarksInputForm.getRemarks());

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}
	
	/**
	 * 支払分割条件を元にして、支払計画情報を作成する
	 * 
	 * <pre>
	 * ※ 支払分割条件の 登録 or 更新 と同じトランザクションで呼び出しを行うこと
	 * 　また、支払計画書類の作成及びS3へのアップロードを行うため、トランザクションの最後に呼び出しを行うこと
	 * </pre>
	 * 
	 * @param invoiceSeq 請求書SEQ
	 * @param validateReCreatePlan 再作成を許容するかどうか(有効の場合、既存データは削除する)
	 * @throws AppException
	 */
	private void createInvoicePaymentPlan(Long invoiceSeq, boolean validateReCreatePlan, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		// 支払計画条件を取得する
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		TAccgInvoicePaymentPlanConditionEntity tAccgInvoicePaymentPlanConditionEntity = tAccgInvoicePaymentPlanConditionDao.selectAccgInvoicePaymentPlanConditionByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null || tAccgInvoicePaymentPlanConditionEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		BigDecimal invoiceAmount = tAccgInvoiceEntity.getInvoiceAmount();

		if (LoiozNumberUtils.isLessThan(invoiceAmount, tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount())) {
			// 分割支払金額で入力した場合
			throw new AppException(MessageEnum.MSG_E00062, null);
		}

		// 分割回数を算出 (割り切れなかった場合も金額は最初か最後に組み込まれるので、回数は小数点以下切り捨て)
		long planCount = invoiceAmount.divide(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount(), RoundingMode.DOWN).longValueExact();
		if (planCount > AccgConstant.ACCG_PAYMENT_PLAN_LIMIT) {
			// 分割回数が上限値を超えた場合
			throw new AppException(MessageEnum.MSG_E00062, null);
		}

		// 現在の支払計画情報を取得する
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntities = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(invoiceSeq);
		if (!validateReCreatePlan && !CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntities)) {
			// 再作成は許容していないが、支払計画情報を存在している場合
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 端数金額を算出 (請求金額合計 - (月々の支払い回数 * 分割回数))
		BigDecimal fractionalAmount = invoiceAmount.subtract(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount().multiply(BigDecimal.valueOf(planCount)));

		// 支払開始日の月初日付を作成
		LocalDate paymentStartYearMonthFirstDay = DateUtils.getFirstDateOfThisMonth(tAccgInvoicePaymentPlanConditionEntity.getPaymentStartDate());

		// 登録Entrityの作成
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanInsertEntities = new ArrayList<>();
		for (var i = 1; i <= planCount; i++) {

			// 支払開始日の月初日付を分割回数に応じて、月を追加する
			LocalDate paymentPlanYearMonthFirstDay = paymentStartYearMonthFirstDay.plusMonths(i - 1);
			// 各データの支払期日を発行する
			LocalDate paymentPlanDate = DateUtils.parseToLocalDate(
					String.format("%s/%s/%s",
							LoiozNumberUtils.parseAsString(paymentPlanYearMonthFirstDay.getYear()),
							StringUtils.leftPad(LoiozNumberUtils.parseAsString(paymentPlanYearMonthFirstDay.getMonthValue()), 2, CommonConstant.ZERO),
							StringUtils.leftPad(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentDd(), 2, CommonConstant.ZERO)),
					DateUtils.DATE_FORMAT_SLASH_DELIMITED,
					ResolverStyle.SMART);

			TAccgInvoicePaymentPlanEntity tAccgInvoicePaymentPlanEntity = new TAccgInvoicePaymentPlanEntity();
			tAccgInvoicePaymentPlanEntity.setInvoiceSeq(invoiceSeq);
			tAccgInvoicePaymentPlanEntity.setPaymentScheduleDate(paymentPlanDate);

			if (FractionalMonthType.FIRST.equalsByCode(tAccgInvoicePaymentPlanConditionEntity.getFractionalMonthType()) && i == 1) {
				// 端数処理が初回の場合、１回目に端数を含める
				tAccgInvoicePaymentPlanEntity.setPaymentScheduleAmount(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount().add(fractionalAmount));
			} else if (FractionalMonthType.LAST.equalsByCode(tAccgInvoicePaymentPlanConditionEntity.getFractionalMonthType()) && i == planCount) {
				// 端数処理が最終の場合、最終回に端数を含める
				tAccgInvoicePaymentPlanEntity.setPaymentScheduleAmount(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount().add(fractionalAmount));
			} else {
				// それ以外は月額分登録
				tAccgInvoicePaymentPlanEntity.setPaymentScheduleAmount(tAccgInvoicePaymentPlanConditionEntity.getMonthPaymentAmount());
			}

			tAccgInvoicePaymentPlanInsertEntities.add(tAccgInvoicePaymentPlanEntity);
		}

		try {
			if (validateReCreatePlan) {
				// 再作成が有効の場合のみ削除処理を実行する
				tAccgInvoicePaymentPlanDao.batchDelete(tAccgInvoicePaymentPlanEntities);
			}

			// 登録処理を行う
			tAccgInvoicePaymentPlanDao.batchInsert(tAccgInvoicePaymentPlanInsertEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 支払計画書の作成
		this.createPaymentPlanOutput(invoiceSeq, validateReCreatePlan, tenantSeq, deleteS3ObjectKeys);
	}

	/**
	 * 支払計画のPDF・PNGの作成処理<br>
	 * 
	 * <pre>
	 * ※本メソッドはS3APIコールを含むため、トランザクションの最後に呼ぶこと
	 * </pre>
	 * 
	 * @param invoiceSeq
	 * @param validateReCreate
	 * @throws AppException
	 */
	private void createPaymentPlanOutput(Long invoiceSeq, boolean validateReCreate, Long tenantSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntities = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(invoiceSeq);

		if (CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntities)) {
			// プラン情報が存在しない場合
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 会計書類ファイル,会計書類ファイル詳細の作成処理
		commonAccgService.createAccgDocFileAndDetail(tAccgInvoiceEntity.getAccgDocSeq(), tenantSeq, validateReCreate, deleteS3ObjectKeys, AccgDocFileType.INVOICE_PAYMENT_PLAN);
	}

	/**
	 * 会計書類SEQに紐づく請求書情報を削除します<br>
	 * 
	 * <pre>
	 * S3オブジェクトキーをDBから削除するため、S3オブジェクトファイル削除を呼び出し元コントローラーで実施すること
	 * </pre>
	 *
	 * @param accgDocSeq
	 * @param deleteS3ObjectKeys DBから削除したS3オブジェクトキー
	 * @throws AppException
	 */
	private void deleteAccgInvoice(Long accgDocSeq, Set<String> deleteS3ObjectKeys) throws AppException {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 請求書の関連データを削除
		TAccgInvoicePaymentPlanConditionEntity tAccgInvoicePaymentPlanConditionEntity = tAccgInvoicePaymentPlanConditionDao.selectAccgInvoicePaymentPlanConditionByInvoiceSeq(tAccgInvoiceEntity.getInvoiceSeq());
		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntityList = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(tAccgInvoiceEntity.getInvoiceSeq());

		try {
			// 請求書情報削除
			tAccgInvoiceDao.delete(tAccgInvoiceEntity);

			if (tAccgInvoicePaymentPlanConditionEntity != null) {
				// 支払分割条件データが存在する場合 -> 削除処理
				tAccgInvoicePaymentPlanConditionDao.delete(tAccgInvoicePaymentPlanConditionEntity);
			}

			if (!CollectionUtils.isEmpty(tAccgInvoicePaymentPlanEntityList)) {
				// 支払計画データが存在する場合 -> 削除処理
				tAccgInvoicePaymentPlanDao.batchDelete(tAccgInvoicePaymentPlanEntityList);
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 会計書類ファイルの削除処理
		commonAccgService.deleteAccgDocFileExcludeSend(accgDocSeq, deleteS3ObjectKeys);
	}

	/**
	 * 請求書情報を基に精算書情報を作成します。<br>
	 * 請求書作成時用に設定情報がある項目は設定情報のデフォルト値を設定し、それ以外は請求書情報を引き継ぎます。<br>
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	private TAccgStatementEntity createStatementBasedOnInvoice(Long accgDocSeq, Long ankenId) throws AppException {
		// 請求書情報取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		// 設定用精算書設定を取得
		MStatementSettingEntity mStatementSettingEntity = mStatementSettingDao.select();
		
		// 請求書情報を基に精算書を作成
		TAccgStatementEntity tAccgStatementEntity = new TAccgStatementEntity();
		
		// 会計書類SEQ
		tAccgStatementEntity.setAccgDocSeq(accgDocSeq);
		
		// 売上明細SEQ
		tAccgStatementEntity.setSalesDetailSeq(tAccgInvoiceEntity.getSalesDetailSeq());
		
		// 発行ステータス
		tAccgStatementEntity.setStatementIssueStatus(IssueStatus.DRAFT.getCd());
		
		// 返金ステータス
		tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.DRAFT.getCd());
		
		// 精算額
		BigDecimal invoiceAmount = tAccgInvoiceEntity.getInvoiceAmount() == null ? BigDecimal.ZERO : tAccgInvoiceEntity.getInvoiceAmount();
		BigDecimal statementAmount = BigDecimal.ZERO.compareTo(invoiceAmount) == 0 ? BigDecimal.ZERO : invoiceAmount.negate();
		tAccgStatementEntity.setStatementAmount(statementAmount);
		
		// 返金先名簿ID
		Long billToPersonId = tAccgInvoiceEntity.getBillToPersonId();
		tAccgStatementEntity.setRefundToPersonId(billToPersonId);
		
		// 売上日
		tAccgStatementEntity.setSalesDate(tAccgInvoiceEntity.getSalesDate());
		
		// 売上計上先
		tAccgStatementEntity.setSalesAccountSeq(tAccgInvoiceEntity.getSalesAccountSeq());
		
		// 実費明細添付フラグ
		tAccgStatementEntity.setDepositDetailAttachFlg(tAccgInvoiceEntity.getDepositDetailAttachFlg());
		
		// タイトルは、精算書作成時用のデフォルトタイトルで上書き
		tAccgStatementEntity.setStatementTitle(mStatementSettingEntity.getDefaultTitle());
		
		// 日付
		tAccgStatementEntity.setStatementDate(tAccgInvoiceEntity.getInvoiceDate());
		
		// 精算番号は新しい精算書番号を採番する
		tAccgStatementEntity.setStatementNo(commonAccgService.issueNewStatementNo());
		
		// 精算先名称
		tAccgStatementEntity.setStatementToName(tAccgInvoiceEntity.getInvoiceToName());
		
		// 精算先敬称
		tAccgStatementEntity.setStatementToNameEnd(tAccgInvoiceEntity.getInvoiceToNameEnd());
		
		// 精算先詳細
		tAccgStatementEntity.setStatementToDetail(tAccgInvoiceEntity.getInvoiceToDetail());
		
		// 差出人事務所名
		tAccgStatementEntity.setStatementFromTenantName(tAccgInvoiceEntity.getInvoiceFromTenantName());
		
		// 差出人詳細
		tAccgStatementEntity.setStatementFromDetail(tAccgInvoiceEntity.getInvoiceFromDetail());
		
		// 挿入文は、精算書作成時用のデフォルト挿入文で上書き
		tAccgStatementEntity.setStatementSubText(mStatementSettingEntity.getDefaultSubText());
		
		// 案件名を取得する
		String ankenName = CommonConstant.BLANK;
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		if (tAnkenEntity != null) {
			ankenName = tAnkenEntity.getAnkenName();
		}
		
		// 件名をマスタ設定の指定方式に変換する
		String invoiceSubject = AccountingUtils.formatAccgDocSubject(ankenName,
				mStatementSettingEntity.getSubjectPrefix(), mStatementSettingEntity.getSubjectSuffix());
		
		// 件名をマスタ設定の指定方式に変換する
		tAccgStatementEntity.setStatementSubject(invoiceSubject);
		
		// 返金期限は空にする
		tAccgStatementEntity.setRefundDate(null);
		
		// 返金期限印字フラグは、精算書作成用のデフォルトで上書き
		tAccgStatementEntity.setRefundDatePrintFlg(mStatementSettingEntity.getRefundDatePrintFlg());
		
		// 案件-案件区分を取得する
		String ankenType = tAnkenEntity.getAnkenType();
		
		// 印影表示フラグ - コピーせず、事務所案件なら事務所印の設定値、個人案件なら弁護士印の設定値をデフォルト値としてセットする
		if (AnkenType.JIMUSHO.equalsByCode(ankenType)) {
			// 事務所案件
			
			// 精算書設定で設定されている事務所印の表示設定で上書き
			tAccgStatementEntity.setTenantStampPrintFlg(mStatementSettingEntity.getTenantStampPrintFlg());
			
		} else if (AnkenType.KOJIN.equalsByCode(ankenType)) {
			// 個人案件
			
			Long salesAccountSeq = tAccgInvoiceEntity.getSalesAccountSeq();
			
			// 売上計上先の個人設定で設定されている弁護士印の表示設定で上書き
			String tenantStampPrintFlg = SystemFlg.FLG_OFF.getCd();
			if (salesAccountSeq != null) {
				// 売上計上先のアカウント情報
				MAccountEntity mAccountEntity = mAccountDao.selectBySeq(salesAccountSeq);
				if (mAccountEntity != null) {
					tenantStampPrintFlg = mAccountEntity.getAccountLawyerStampPrintFlg() != null ? mAccountEntity.getAccountLawyerStampPrintFlg() : SystemFlg.FLG_OFF.getCd();
				}
			}
			tAccgStatementEntity.setTenantStampPrintFlg(tenantStampPrintFlg);
			
		} else {
			// 想定外
			throw new RuntimeException("想定外のEnum値 [ankenType=" + ankenType + "]");
		}
			
		// 返金先は名簿情報から口座情報を取得
		if (billToPersonId == null) {
			throw new DataNotFoundException("請求先名簿IDが存在しません。[accgDocSeq=" + accgDocSeq + " ,billToPersonId=" + billToPersonId + "]");
		}
		TPersonEntity tPersonEntity = tPersonDao.selectPersonByPersonId(tAccgInvoiceEntity.getBillToPersonId());
		if (tPersonEntity == null) {
			throw new DataNotFoundException("名簿情報が存在しません。[billToPersonId=" + tAccgInvoiceEntity.getBillToPersonId() + "]");
		}
		tAccgStatementEntity.setRefundBankDetail(commonAccgService.bankToDetail(tPersonEntity.getGinkoName(),
				tPersonEntity.getShitenName(), tPersonEntity.getShitenNo(), tPersonEntity.getKozaType(),
				tPersonEntity.getKozaNo(), tPersonEntity.getKozaName()));
		
		// 備考は、精算書作成用のデフォルト備考で上書き
		tAccgStatementEntity.setStatementRemarks(mStatementSettingEntity.getDefaultRemarks());
		
		// 取引日-印字フラグ（既入金）は、精算書作成用のデフォルトで上書き
		tAccgStatementEntity.setRepayTransactionDatePrintFlg(mStatementSettingEntity.getTransactionDatePrintFlg());
		
		// 取引日-印字フラグ（請求）は、精算書作成用のデフォルトで上書き
		tAccgStatementEntity.setInvoiceTransactionDatePrintFlg(mStatementSettingEntity.getTransactionDatePrintFlg());
		
		// 既入金項目合算フラグ（既入金）
		tAccgStatementEntity.setRepaySumFlg(tAccgInvoiceEntity.getRepaySumFlg());
		
		// 実費項目合算フラグ（請求）
		tAccgStatementEntity.setExpenseSumFlg(tAccgInvoiceEntity.getExpenseSumFlg());
		
		// メモ
		tAccgStatementEntity.setStatementMemo(tAccgInvoiceEntity.getInvoiceMemo());

		// 精算書登録
		int insertCount = tAccgStatementDao.insert(tAccgStatementEntity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tAccgStatementEntity;
	}
	
	/**
	 * 請求書情報を更新する
	 * 
	 * @param tAccgInvoiceEntity
	 * @throws AppException
	 */
	private void updateAccgInvoice(TAccgInvoiceEntity tAccgInvoiceEntity) throws AppException {
		
		int updateCount = 0;
		try {
			updateCount = tAccgInvoiceDao.update(tAccgInvoiceEntity);
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

	//=========================================================================
	// ▼ 送付処理系
	//=========================================================================
	
	/**
	 * 請求書データの会計書類-対応-送付-ファイル情報を登録
	 * 
	 * @param invoiceSeq
	 * @param accgDocActSendSeq
	 * @throws AppException
	 */
	private void registInvoiceAccgDocActSendFile(Long invoiceSeq, Long accgDocActSendSeq) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		Set<Long> accgDocFileSeqList = new HashSet<>();

		// 請求書PDFの取得
		AccgDocFileBean targetInvoiceFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE);
		accgDocFileSeqList.add(targetInvoiceFileBean.getAccgDocFileSeq());

		// 実費明細PDFの取得
		if (SystemFlg.codeToBoolean(tAccgInvoiceEntity.getDepositDetailAttachFlg())) {
			AccgDocFileBean targetDepositRecordFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.DEPOSIT_DETAIL);
			accgDocFileSeqList.add(targetDepositRecordFileBean.getAccgDocFileSeq());
		}

		// 支払計画書PDFの取得
		if (SystemFlg.codeToBoolean(tAccgInvoiceEntity.getPaymentPlanAttachFlg())) {
			AccgDocFileBean targetInvoicePlanFileBean = commonAccgInvoiceStatementService.getCurrentPdfAccgDocFileBean(tAccgInvoiceEntity.getAccgDocSeq(), AccgDocFileType.INVOICE_PAYMENT_PLAN);
			accgDocFileSeqList.add(targetInvoicePlanFileBean.getAccgDocFileSeq());
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

}