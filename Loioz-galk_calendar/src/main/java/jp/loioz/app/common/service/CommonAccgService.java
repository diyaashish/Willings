package jp.loioz.app.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.form.accg.AccgDocSummaryForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.InvoiceRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayRowInputForm;
import jp.loioz.app.common.pdf.builder.P0001PdfBuilder;
import jp.loioz.app.common.pdf.builder.P0002PdfBuilder;
import jp.loioz.app.common.pdf.builder.P0003PdfBuilder;
import jp.loioz.app.common.pdf.builder.P0005PdfBuilder;
import jp.loioz.app.common.pdf.config.PdfConfig;
import jp.loioz.app.common.pdf.data.P0001Data;
import jp.loioz.app.common.pdf.data.P0002Data;
import jp.loioz.app.common.pdf.data.P0003Data;
import jp.loioz.app.common.pdf.data.P0005Data;
import jp.loioz.app.global.downloadAuth.controller.DownloadAuthController;
import jp.loioz.bean.AccgDocFileBean;
import jp.loioz.bean.AccgDocInvoiceBean;
import jp.loioz.bean.AccgInvoiceStatementBean;
import jp.loioz.bean.AccgRecordDetailBean;
import jp.loioz.bean.RepayBean;
import jp.loioz.bean.TenantDetailKozaBean;
import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocActType;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AccgNoNumberingType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.DepositRecvCreatedType;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.DepositUseStatus;
import jp.loioz.common.constant.CommonConstant.ExpenseInvoiceFlg;
import jp.loioz.common.constant.CommonConstant.FeePaymentStatus;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.constant.CommonConstant.InvoiceDepositType;
import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import jp.loioz.common.constant.CommonConstant.InvoiceType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.NameEnd;
import jp.loioz.common.constant.CommonConstant.RecordType;
import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.FileStorageConstant.S3AccountingDir;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.service.pdf.PdfService;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.FileUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozIOUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.PdfUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MAccountImgDao;
import jp.loioz.dao.MDepositItemDao;
import jp.loioz.dao.MFeeItemDao;
import jp.loioz.dao.MInvoiceSettingDao;
import jp.loioz.dao.MMailSettingDao;
import jp.loioz.dao.MStatementSettingDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAccgDocActDao;
import jp.loioz.dao.TAccgDocActSendDao;
import jp.loioz.dao.TAccgDocActSendFileDao;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAccgDocDownloadDao;
import jp.loioz.dao.TAccgDocFileDao;
import jp.loioz.dao.TAccgDocFileDetailDao;
import jp.loioz.dao.TAccgDocInvoiceDao;
import jp.loioz.dao.TAccgDocInvoiceDepositDao;
import jp.loioz.dao.TAccgDocInvoiceDepositTDepositRecvMappingDao;
import jp.loioz.dao.TAccgDocInvoiceFeeDao;
import jp.loioz.dao.TAccgDocInvoiceOtherDao;
import jp.loioz.dao.TAccgDocLastUsedNumberDao;
import jp.loioz.dao.TAccgDocRepayDao;
import jp.loioz.dao.TAccgDocRepayTDepositRecvMappingDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgInvoicePaymentPlanConditionDao;
import jp.loioz.dao.TAccgInvoicePaymentPlanDao;
import jp.loioz.dao.TAccgInvoiceTaxDao;
import jp.loioz.dao.TAccgInvoiceWithholdingDao;
import jp.loioz.dao.TAccgRecordDao;
import jp.loioz.dao.TAccgRecordDetailDao;
import jp.loioz.dao.TAccgRecordDetailOverPaymentDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dao.TFeeAddTimeChargeDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSalesDao;
import jp.loioz.dao.TSalesDetailDao;
import jp.loioz.dao.TSalesDetailTaxDao;
import jp.loioz.dao.TUncollectibleDao;
import jp.loioz.dao.TUncollectibleDetailDao;
import jp.loioz.domain.UriService;
import jp.loioz.domain.file.Directory;
import jp.loioz.domain.mail.builder.AbstractMailBuilder;
import jp.loioz.domain.mail.builder.M0013MailBuilder;
import jp.loioz.domain.mail.builder.M0014MailBuilder;
import jp.loioz.domain.mail.builder.M0015MailBuilder;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AccgInvoiceStatementAmountDto;
import jp.loioz.dto.AccgInvoiceStatementListItemDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.FileContentsDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MAccountImgEntity;
import jp.loioz.entity.MDepositItemEntity;
import jp.loioz.entity.MFeeItemEntity;
import jp.loioz.entity.MInvoiceSettingEntity;
import jp.loioz.entity.MMailSettingEntity;
import jp.loioz.entity.MStatementSettingEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAccgDocActEntity;
import jp.loioz.entity.TAccgDocActSendEntity;
import jp.loioz.entity.TAccgDocActSendFileEntity;
import jp.loioz.entity.TAccgDocDownloadEntity;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAccgDocFileDetailEntity;
import jp.loioz.entity.TAccgDocFileEntity;
import jp.loioz.entity.TAccgDocInvoiceDepositEntity;
import jp.loioz.entity.TAccgDocInvoiceEntity;
import jp.loioz.entity.TAccgDocInvoiceFeeEntity;
import jp.loioz.entity.TAccgDocLastUsedNumberEntity;
import jp.loioz.entity.TAccgDocRepayEntity;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgInvoicePaymentPlanEntity;
import jp.loioz.entity.TAccgInvoiceTaxEntity;
import jp.loioz.entity.TAccgInvoiceWithholdingEntity;
import jp.loioz.entity.TAccgRecordDetailEntity;
import jp.loioz.entity.TAccgRecordDetailOverPaymentEntity;
import jp.loioz.entity.TAccgRecordEntity;
import jp.loioz.entity.TAccgStatementEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TDepositRecvEntity;
import jp.loioz.entity.TFeeEntity;
import jp.loioz.entity.TGinkoKozaEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSalesDetailEntity;
import jp.loioz.entity.TSalesDetailTaxEntity;
import jp.loioz.entity.TSalesEntity;
import jp.loioz.entity.TUncollectibleDetailEntity;
import jp.loioz.entity.TUncollectibleEntity;

/**
 * 会計管理の共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonAccgService extends DefaultService {

	/** PDFConfig */
	@Autowired
	private PdfConfig pdfConfig;

	/** PDFService */
	@Autowired
	private PdfService pdfService;

	/** メールサービスクラス */
	@Autowired
	private MailService mailService;

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** アカウント印影Daoクラス */
	@Autowired
	private MAccountImgDao mAccountImgDao;

	/** テナント用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** メール設定Daoクラス */
	@Autowired
	private MMailSettingDao mMailSettingDao;

	/** 請求書設定Daoクラス */
	@Autowired
	private MInvoiceSettingDao mInvoiceSettingDao;

	/** 精算書設定Daoクラス */
	@Autowired
	private MStatementSettingDao mStatementSettingDao;

	/** 報酬項目Daoクラス */
	@Autowired
	private MFeeItemDao mFeeItemDao;

	/** 預り金項目Daoクラス */
	@Autowired
	private MDepositItemDao mDepositItemDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件-担当者Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 案件-顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 会計書類Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** 請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

	/** 会計書類-対応Daoクラス */
	@Autowired
	private TAccgDocActDao tAccgDocActDao;

	/** 会計書類対応-送付Daoクラス */
	@Autowired
	private TAccgDocActSendDao tAccgDocActSendDao;

	/** 会計書類対応送付ファイルDaoクラス */
	@Autowired
	private TAccgDocActSendFileDao tAccgDocActSendFileDao;

	/** 会計書類-ダウンロード期間Daoクラス */
	@Autowired
	private TAccgDocDownloadDao tAccgDocDownloadDao;

	/** 会計書類-最終採番番号Daoクラス */
	@Autowired
	private TAccgDocLastUsedNumberDao tAccgDocLastUsedNumberDao;

	/** 請求項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceDao tAccgDocInvoiceDao;

	/** 請求預り金項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceDepositDao tAccgDocInvoiceDepositDao;

	/** 顧客の共通Daoクラス */
	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

	/** 請求項目-消費税Daoクラス */
	@Autowired
	private TAccgInvoiceTaxDao tAccgInvoiceTaxDao;

	/** 既入金Daoクラス */
	@Autowired
	private TAccgDocRepayDao tAccgDocRepayDao;

	/** 取引実績Daoクラス */
	@Autowired
	private TAccgRecordDao tAccgRecordDao;

	/** 取引実績明細Daoクラス */
	@Autowired
	private TAccgRecordDetailDao tAccgRecordDetailDao;

	/** 取引実績明細-過入金Daoクラス */
	@Autowired
	private TAccgRecordDetailOverPaymentDao tAccgRecordDetailOverPaymentDao;

	/** 請求項目-源泉徴収Daoクラス */
	@Autowired
	private TAccgInvoiceWithholdingDao tAccgInvoiceWithholdingDao;

	/** 売上Daoクラス */
	@Autowired
	private TSalesDao tSalesDao;

	/** 売上明細Daoクラス */
	@Autowired
	private TSalesDetailDao tSalesDetailDao;

	/** 売上明細-消費税Daoクラス */
	@Autowired
	private TSalesDetailTaxDao tSalesDetailTaxDao;

	/** 報酬Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	/** 請求項目-報酬Daoクラス */
	@Autowired
	private TAccgDocInvoiceFeeDao tAccgDocInvoiceFeeDao;

	/** 支払計画Daoクラス */
	@Autowired
	private TAccgInvoicePaymentPlanDao tAccgInvoicePaymentPlanDao;

	/** 会計ファイル情報Daoクラス */
	@Autowired
	private TAccgDocFileDao tAccgDocFileDao;

	/** 会計ファイル詳細情報Daoクラス */
	@Autowired
	private TAccgDocFileDetailDao tAccgDocFileDetailDao;

	/** 回収不能Daoクラス */
	@Autowired
	private TUncollectibleDao tUncollectibleDao;

	/** 回収不能詳細Daoクラス */
	@Autowired
	private TUncollectibleDetailDao tUncollectibleDetailDao;

	/** 銀行口座用のDaoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** 請求項目-預り金（実費）_預り金テーブルマッピング用のDaoクラス */
	@Autowired
	private TAccgDocInvoiceDepositTDepositRecvMappingDao tAccgDocInvoiceDepositTDepositRecvMappingDao;

	/** 請求その他項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceOtherDao tAccgDocInvoiceOtherDao;

	/** 既入金項目_預り金テーブルマッピング用のDaoクラス */
	@Autowired
	private TAccgDocRepayTDepositRecvMappingDao tAccgDocRepayTDepositRecvMappingDao;

	/** 支払分割条件Daoクラス */
	@Autowired
	private TAccgInvoicePaymentPlanConditionDao tAccgInvoicePaymentPlanConditionDao;

	/** タイムチャージ設定用のDaoクラス */
	@Autowired
	private TFeeAddTimeChargeDao tFeeAddTimeChargeDao;

	/** 共通：ファイルストレージサービスクラス */
	@Autowired
	private FileStorageService fileStorageService;

	/** 共通：URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** 会計管理の金額を扱う共通サービス */
	@Autowired
	private CommonAccgAmountService commonAccgAmountService;

	/** 請求書詳細、精算書詳細の共通サービスクラス */
	@Autowired
	private CommonAccgInvoiceStatementService commonAccgInvoiceStatementService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** 源泉徴収計算比率基準金額 */
	public static final BigDecimal GENSEN_CALC_BORDER_DECIMAL = BigDecimal.valueOf(1000000);

	// =========================================================================
	// public メソッド
	// =========================================================================

	// =========================================================================
	// ▼ 取得／データ変換系
	// =========================================================================

	/**
	 * 会計書類SEQを取得します
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	public Long getAccgDocSeqByInvoiceSeq(Long invoiceSeq) {
		TAccgInvoiceEntity entity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (entity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[invoiceSeq=" + invoiceSeq + "]");
		}
		return entity.getAccgDocSeq();
	}

	/**
	 * 会計書類SEQを取得します
	 * 
	 * @param statementSeq
	 * @return
	 */
	public Long getAccgDocSeqByStatementSeq(Long statementSeq) {
		TAccgStatementEntity entity = tAccgStatementDao.selectStatementByStatementSeq(statementSeq);
		if (entity == null) {
			throw new DataNotFoundException("精算書情報が存在しません。[statementSeq=" + statementSeq + "]");
		}
		return entity.getAccgDocSeq();
	}

	/**
	 * 取引状況エリア用フォームを取得
	 * 
	 * ※ 請求書画面、精算書画面、取引実績画面で利用を想定する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgDocSummaryForm getAccgDocSummaryForm(Long accgDocSeq) {

		var accgDocSummaryForm = new AccgDocSummaryForm();

		// 会計書類情報を取得する
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(tAccgDocEntity.getAnkenId(), tAccgDocEntity.getPersonId());
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());

		// 共通項目の設定
		accgDocSummaryForm.setAccgRecordSeq(tAccgRecordEntity == null ? null : tAccgRecordEntity.getAccgRecordSeq());
		accgDocSummaryForm.setAccgDocSeq(accgDocSeq);
		accgDocSummaryForm.setPersonId(tAccgDocEntity.getPersonId());
		accgDocSummaryForm.setAnkenId(tAccgDocEntity.getAnkenId());
		accgDocSummaryForm.setAccgDocType(accgDocType);
		accgDocSummaryForm.setAnkenStatus(AnkenStatus.of(tAnkenCustomerEntity.getAnkenStatus()));

		if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合
			AccgDocSummaryForm.StatementSummary statementSummary = this.getAccgDocStatementSummary(accgDocSeq);
			accgDocSummaryForm.setStatementSummary(statementSummary);

		} else if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合
			AccgDocSummaryForm.InvoiceSummary invoiceSummary = this.getAccgDocInvoiceSummary(accgDocSeq);
			accgDocSummaryForm.setInvoiceSummary(invoiceSummary);

		} else {
			// 想定しないEnum値
			throw new RuntimeException("想定外のEnum値です");
		}

		return accgDocSummaryForm;
	}

	/**
	 * 既入金項目合算フラグ（既入金）を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	public String getRepaySumFlg(Long accgDocSeq) throws AppException {
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity != null) {
			return tAccgInvoiceEntity.getRepaySumFlg();
		} else if (tAccgStatementEntity != null) {
			return tAccgStatementEntity.getRepaySumFlg();
		} else {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * 実費項目合算フラグ（請求）を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	public String getExpenseSumFlg(Long accgDocSeq) throws AppException {
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity != null) {
			return tAccgInvoiceEntity.getExpenseSumFlg();
		} else if (tAccgStatementEntity != null) {
			return tAccgStatementEntity.getExpenseSumFlg();
		} else {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * 案件IDと名簿IDから請求書/精算書一覧リストを取得する
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	public List<AccgInvoiceStatementListItemDto> getAccgDocInvoiceStatementList(Long ankenId, Long personId, boolean isAllView) {

		Integer limitCount = AccgConstant.ACCG_INVOICE_STATEMENT_LIST_DISP_LIMIT;
		if (isAllView) {
			limitCount = null;
		}

		List<AccgInvoiceStatementBean> accgInvoiceStateBeanList = tAccgDocDao.selectAccgInvoiceStateBeanByAnkenIdAndPersonIdAndLimit(ankenId, personId, limitCount);
		return accgInvoiceStateBeanList.stream().map(this::toAccgInvoiceStateListItemDto).collect(Collectors.toList());
	}

	/**
	 * 報酬項目の候補データを取得します
	 * 
	 * @param searchWord
	 * @return
	 */
	public List<SelectOptionForm> searchFeeDataList(String searchWord) {
		// 有効な報酬項目を取得
		List<MFeeItemEntity> feeList = mFeeItemDao.selectEnabledFeeItemBySearchWord(searchWord);
		// 取得できない場合は空のリストを返却
		if (CollectionUtils.isEmpty(feeList)) {
			return Collections.emptyList();
		}

		List<SelectOptionForm> selectOptionDtoList = feeList.stream().map(entity -> {
			SelectOptionForm dto = new SelectOptionForm(entity.getFeeItemSeq().toString(), entity.getFeeItemName());
			return dto;
		}).collect(Collectors.toList());

		return selectOptionDtoList;
	}

	/**
	 * 預り金項目の候補データを取得します
	 * 
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	public List<SelectOptionForm> searchDepositRecvDataList(String searchWord, String depositType) {
		// 有効な預り金項目を取得
		List<MDepositItemEntity> depositList = mDepositItemDao.selectEnabledDepositItemBySearchWord(searchWord, depositType);
		// 取得できない場合は空のリストを返却
		if (CollectionUtils.isEmpty(depositList)) {
			return Collections.emptyList();
		}

		List<SelectOptionForm> selectOptionDtoList = depositList.stream().map(entity -> {
			SelectOptionForm dto = new SelectOptionForm(entity.getDepositItemSeq().toString(), entity.getDepositItemName());
			return dto;
		}).collect(Collectors.toList());

		return selectOptionDtoList;
	}

	/**
	 * Dtoが保持する金額情報を元に、消費税額と源泉徴収額の値を再計算し、<br>
	 * 請求項目-消費税、請求項目-源泉徴収税データを更新する。
	 * 
	 * <pre>
	 * ※再計算し、DBに登録／更新した消費税額、源泉徴収額の値は、Dtoのプロパティに設定をするため、
	 * 　このメソッドの実行前と後で、Dtoに設定されている消費税額、源泉徴収額の値は変更されることに注意。
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @param accgInvoiceStatementAmountDto
	 * @throws AppException
	 */
	public void recalcAndUpdateAccgInvoiceTaxAndWithholding(Long accgDocSeq, AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) throws AppException {

		// 消費税額（税率8%）の計算とDtoへの設定
		commonAccgAmountService.calcTax8OnInvoiceItem(accgInvoiceStatementAmountDto);
		// 消費税額（税率10%）の計算とDtoへの設定
		commonAccgAmountService.calcTax10OnInvoiceItem(accgInvoiceStatementAmountDto);
		// 源泉徴収額の計算とDtoへの設定
		commonAccgAmountService.calcWithholdingOnInvoiceItem(accgInvoiceStatementAmountDto);

		// 8%の請求項目-消費税を更新
		this.updateAccgInvoiceTax8(accgDocSeq, accgInvoiceStatementAmountDto);
		// 10%の請求項目-消費税を更新
		this.updateAccgInvoiceTax10(accgDocSeq, accgInvoiceStatementAmountDto);
		// 請求項目-源泉徴収を更新
		this.updateAccgInvoiceWithholding(accgDocSeq, accgInvoiceStatementAmountDto);
	}

	/**
	 * 顧客種別をもとに敬称を返す
	 * 
	 * @param personTypeCd
	 * @return
	 */
	public String personTypeToNameEnd(String personTypeCd) {
		String nameEndTypeCd = CommonConstant.BLANK;
		if (CommonConstant.CustomerType.KOJIN.equalsByCode(personTypeCd)) {
			// 個人 -> 様
			nameEndTypeCd = NameEnd.SAMA.getCd();
		} else if (CommonConstant.CustomerType.HOJIN.equalsByCode(personTypeCd)) {
			// 企業・団体 -> 御中
			nameEndTypeCd = NameEnd.ONCHU.getCd();
		} else if (CommonConstant.CustomerType.LAWYER.equalsByCode(personTypeCd)) {
			// 弁護士 -> 様
			nameEndTypeCd = NameEnd.SAMA.getCd();
		} else {
			// 敬称なし
			nameEndTypeCd = CommonConstant.NameEnd.NONE.getCd();
		}
		return nameEndTypeCd;
	}

	/**
	 * 対象名簿の詳細を返す
	 * 
	 * @param tPersonEntity
	 * @return
	 */
	public String personInfoToDetail(TPersonEntity tPersonEntity) {
		StringBuilder sb = new StringBuilder();
		// 〒番号
		String zipCode = tPersonEntity.getZipCode();
		if (StringUtils.isNotEmpty(zipCode)) {
			sb.append("〒");
			sb.append(zipCode);
		}
		// 住所1
		String address1 = tPersonEntity.getAddress1();
		if (StringUtils.isNotEmpty(address1)) {
			sb.append(this.lineFeedCodeInsert(sb, address1));
		}
		// 住所2
		String address2 = tPersonEntity.getAddress2();
		if (StringUtils.isNotEmpty(address2)) {
			sb.append(this.lineFeedCodeInsert(sb, address2));
		}
		return sb.toString();
	}

	/**
	 * 発行元情報を返す
	 * 
	 * @param tenantDetailKozaBean
	 * @param tAnkenEntity
	 * @param salesAccountSeq
	 * @return
	 */
	public String tenantInfoToDetail(TenantDetailKozaBean tenantDetailKozaBean, TAnkenEntity tAnkenEntity, Long salesAccountSeq) {
		StringBuilder sb = new StringBuilder();
		// 適格請求書発行事業者登録番号（DB登録用）
		String invoiceRegistrationNo = CommonConstant.BLANK;

		// アカウント適格請求書発行事業者登録番号
		String accountInvoiceRegistrationNo = CommonConstant.BLANK;
		PersonName personName = null;
		// アカウント情報を取得
		if (salesAccountSeq != null) {
			MAccountEntity mAccountEntity = mAccountDao.selectBySeq(salesAccountSeq);
			// 売上計上先の弁護士名
			personName = PersonName.fromEntity(mAccountEntity);
			// 売上計上先の適格請求書発行事業者登録番号
			accountInvoiceRegistrationNo = mAccountEntity.getAccountInvoiceRegistrationNo();
		}

		// 案件種別によって適格請求書発行事業者登録番号を切り替える
		String ankenType = tAnkenEntity.getAnkenType();
		if (AnkenType.JIMUSHO.equalsByCode(ankenType)) {
			// 事務所案件の場合：事務所の適格請求書発行事業者登録番号を取得する
			invoiceRegistrationNo = AccountingUtils.formatInvoiceRegistrationNo(tenantDetailKozaBean.getTenantInvoiceRegistrationNo());

		} else if (AnkenType.KOJIN.equalsByCode(ankenType)) {
			// 個人事件の場合は、売上計上先のアカウントの適格請求書発行事業者登録番号を取得する
			invoiceRegistrationNo = AccountingUtils.formatInvoiceRegistrationNo(accountInvoiceRegistrationNo);

		}

		// 弁護士名
		if (personName != null) {
			sb.append(this.lineFeedCodeInsert(sb, "弁護士　" + personName.getName()));
		}
		// 〒番号
		String zipCode = tenantDetailKozaBean.getTenantZipCd();
		if (StringUtils.isNotEmpty(zipCode)) {
			sb.append(this.lineFeedCodeInsert(sb, "〒" + zipCode));
		}
		// 住所1
		String address1 = tenantDetailKozaBean.getTenantAddress1();
		if (StringUtils.isNotEmpty(address1)) {
			sb.append(this.lineFeedCodeInsert(sb, address1));
		}
		// 住所2
		String address2 = tenantDetailKozaBean.getTenantAddress2();
		if (StringUtils.isNotEmpty(address2)) {
			sb.append(this.lineFeedCodeInsert(sb, address2));
		}
		// 電話
		String telNo = tenantDetailKozaBean.getTenantTelNo();
		if (StringUtils.isNotEmpty(telNo)) {
			sb.append(this.lineFeedCodeInsert(sb, "電話：" + telNo));
		}
		// FAX
		String faxNo = tenantDetailKozaBean.getTenantFaxNo();
		if (StringUtils.isNotEmpty(faxNo)) {
			sb.append(this.lineFeedCodeInsert(sb, "FAX：" + faxNo));
		}
		// インボイス登録番号
		if (StringUtils.isNotEmpty(invoiceRegistrationNo)) {
			sb.append(this.lineFeedCodeInsert(sb, "登録番号：" + invoiceRegistrationNo));
		}
		return sb.toString();
	}

	/**
	 * 振込先を返す<br>
	 * ※テナント用
	 * 
	 * @param tenantDetailKozaBean
	 * @return
	 */
	public String tenantBankToDetail(TenantDetailKozaBean bean) {
		return bankToDetail(bean.getGinkoName(), bean.getShitenName(),
				bean.getShitenNo(), bean.getKozaType(), bean.getKozaNo(), bean.getKozaName());
	}

	/**
	 * 振込先を返す<br>
	 * 
	 * @param entity
	 * @return
	 */
	public String salesAccountBankToDetail(TGinkoKozaEntity entity) {
		if (entity == null) {
			return "";
		}
		return bankToDetail(entity.getGinkoName(), entity.getShitenName(),
				entity.getShitenNo(), entity.getKozaType(), entity.getKozaNo(), entity.getKozaName());
	}

	/**
	 * 口座情報を返す<br>
	 * ※名簿用
	 * 
	 * @param ginkoName
	 * @param shitenName
	 * @param shitenNo
	 * @param kozaType
	 * @param kozaNo
	 * @param kozaName
	 * @return
	 */
	public String bankToDetail(String ginkoName, String shitenName,
			String shitenNo, String kozaType, String kozaNo, String kozaName) {

		StringBuilder sb = new StringBuilder();
		// 銀行名
		sb.append(StringUtils.null2blank(ginkoName));
		// 支店名
		if (StringUtils.isNotEmpty(shitenName)) {
			sb.append(CommonConstant.SPACE);
			sb.append(shitenName);
		}
		// 支店番号
		if (StringUtils.isNotEmpty(shitenNo)) {
			sb.append("（支店番号" + CommonConstant.SPACE);
			sb.append(shitenNo);
			sb.append("）");
		}
		String sbVal = sb.toString();
		if (StringUtils.isNotEmpty(sbVal)) {
			sb.append(CommonConstant.LINE_FEED_CODE);
		}
		// 口座種別
		if (StringUtils.isNotEmpty(kozaType)) {
			sb.append(KozaType.of(kozaType).getVal());
			sb.append(CommonConstant.SPACE);
		}
		// 口座番号
		sb.append(StringUtils.null2blank(kozaNo));
		// 口座名義
		if (StringUtils.isNotEmpty(kozaName)) {
			sb.append("　口座名義" + CommonConstant.SPACE);
			sb.append(kozaName);
		}
		String val = null;
		sbVal = sb.toString();
		if (StringUtils.isNotEmpty(sbVal)) {
			val = sbVal;
		}
		return val;
	}

	/**
	 * 送付済みファイルをZip化したバイナリーオブジェクトで取得
	 * 
	 * @param accgDocActSendSeq
	 * @return
	 */
	public FileContentsDto getAccgSendFileZip(Long accgDocActSendSeq) {

		// ファイル情報を取得
		List<TAccgDocActSendFileEntity> tAccgDocActSendFileEntities = tAccgDocActSendFileDao.selectAccgDocActSendFileByAccgDocActSendSeq(accgDocActSendSeq);
		List<Long> accgDocFileSeqList = tAccgDocActSendFileEntities.stream().map(TAccgDocActSendFileEntity::getAccgDocFileSeq).collect(Collectors.toList());

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);) {

			for (Long accgDocFileSeq : accgDocFileSeqList) {
				// メモリに格納するファイル情報は最小限にするため、forループ内で1件づつ添付ファイルを処理する
				List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanByAccgDocFileSeq(accgDocFileSeq);
				AccgDocFileBean accgDocFileBean = accgDocFileBeans.stream().findFirst().orElseThrow();

				String entryFileName = this.createAccgDocFileName(accgDocFileBean.getAccgDocSeq(), AccgDocFileType.of(accgDocFileBean.getAccgDocFileType()),
						FileExtension.ofExtension(accgDocFileBean.getFileExtension()));
				ZipEntry zipEntry = new ZipEntry(entryFileName);
				zos.putNextEntry(zipEntry);
				LoiozIOUtils.copy(fileStorageService.fileDownload(accgDocFileBean.getS3ObjectKey()).getObjectContent(), zos);
				zos.closeEntry();

				// オブジェクトの容量が大きい(ファイル)ため、少しでも早く開放したいため
				// GC対象に認識しやすくするためにNullを代入
				accgDocFileBeans = null;
				accgDocFileBean = null;
			}

			// 明示的にZipOutputStreamを閉じる(try-with-resouceのfinallyより前でbuilderにセットするため)
			zos.close();

			// ファイルコンテンツオブジェクトとして返却
			return new FileContentsDto(baos.toByteArray(), FileExtension.ZIP.getVal());
		} catch (Exception ex) {
			// 予期せぬエラー
			throw new RuntimeException("ファイルオブジェクトの生成時にエラーが発生しました。", ex);
		}

	}

	/**
	 * 
	 * 下記の形式で会計書類の圧縮ファイル名を作成します。<br>
	 * 
	 * ・【請求書類一式】<i>請求先名称請求先敬称</i>_yyyymmdd.<i>拡張子</i><br>
	 * ・【精算書類一式】<i>精算先名称精算先敬称</i>_yyyymmdd.<i>拡張子</i><br>
	 * yyyymmddは、請求日/精算日<br>
	 * 
	 * 
	 * 
	 * @param accgDocSeq
	 * @param fileExtension
	 * @return
	 * @throws AppException
	 */
	public String createAccgDocZipFileName(Long accgDocSeq, AccgDocType accgDocType, FileExtension fileExtension) {
		// 返却用ファイル名
		String fileName = "";
		// 請求先名称／精算先名称
		String invoiceStatementToName = "";
		// 請求先名称／精算先敬称
		String invoiceStatementToNameEnd = "";
		// 請求日／精算日（uuuuMMdd）
		String invoiceStatementDate = "";

		if (fileExtension == null) {
			return fileName;
		}

		// 請求書／精算書情報取得
		if (AccgDocType.STATEMENT.equals(accgDocType)) {
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			invoiceStatementDate = DateUtils.parseToString(tAccgStatementEntity.getStatementDate(),
					DateUtils.DATE_FORMAT_NON_DELIMITED);
			invoiceStatementToName = StringUtils
					.removeSpaceCharacter(StringUtils.isEmpty(tAccgStatementEntity.getStatementToName()) ? "" : tAccgStatementEntity.getStatementToName());
			invoiceStatementToNameEnd = NameEnd.of(tAccgStatementEntity.getStatementToNameEnd()).getVal();
		} else if (AccgDocType.INVOICE.equals(accgDocType)) {
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			invoiceStatementDate = DateUtils.parseToString(tAccgInvoiceEntity.getInvoiceDate(),
					DateUtils.DATE_FORMAT_NON_DELIMITED);
			invoiceStatementToName = StringUtils
					.removeSpaceCharacter(StringUtils.isEmpty(tAccgInvoiceEntity.getInvoiceToName()) ? "" : tAccgInvoiceEntity.getInvoiceToName());
			invoiceStatementToNameEnd = NameEnd.of(tAccgInvoiceEntity.getInvoiceToNameEnd()).getVal();
		} else {
			throw new RuntimeException("不正な会計書類タイプ[accgDocType=" + accgDocType + "]");
		}

		// ファイル名作成
		fileName = "【" + accgDocType.getVal() + "類一式】"
				+ invoiceStatementToName
				+ invoiceStatementToNameEnd + "_"
				+ invoiceStatementDate;

		// 拡張子
		fileName = fileName + fileExtension.getExtensionWithInDot();

		return fileName;
	}

	/**
	 * 下記の形式で会計書類ファイル名を作成します。<br>
	 * 
	 * 請求書<br>
	 * ・【御請求書】<i>請求先名称請求先敬称</i>_<i>金額</i>円_yyyymmdd.<i>拡張子</i><br>
	 * ・【実費明細書】<i>請求先名称請求先敬称</i>_yyyymmdd.<i>拡張子</i><br>
	 * ・【支払計画表】<i>請求先名称請求先敬称</i>_yyyymmdd.<i>拡張子</i><br>
	 * yyyymmddは、請求日<br>
	 * 
	 * 精算書<br>
	 * ・【御精算書】<i>精算先名称精算先敬称</i>_<i>金額</i>円_yyyymmdd.<i>拡張子</i><br>
	 * ・【実費明細書】<i>精算先名称精算先敬称</i>_yyyymmdd.<i>拡張子</i><br>
	 * yyyymmddは、精算日<br>
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @param fileExtension
	 * @return
	 * @throws AppException
	 */
	public String createAccgDocFileName(Long accgDocSeq, AccgDocFileType accgDocFileType, FileExtension fileExtension) {
		// 返却用ファイル名
		String fileName = "";
		// 請求先名称／精算先名称
		String invoiceStatementToName = "";
		// 請求先名称／精算先敬称
		String invoiceStatementToNameEnd = "";
		// 請求額／精算額
		String invoiceStatementAmount = "";
		// 請求日／精算日（uuuuMMdd）
		String invoiceStatementDate = "";

		if (accgDocFileType == null || fileExtension == null) {
			return fileName;
		}

		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);

		// 請求書／精算書情報取得
		String accgDocType = tAccgDocEntity.getAccgDocType();
		if (AccgDocType.STATEMENT.equalsByCode(accgDocType)) {
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			invoiceStatementDate = DateUtils.parseToString(tAccgStatementEntity.getStatementDate(),
					DateUtils.DATE_FORMAT_NON_DELIMITED);
			invoiceStatementToName = StringUtils
					.removeSpaceCharacter(StringUtils.isEmpty(tAccgStatementEntity.getStatementToName()) ? "" : tAccgStatementEntity.getStatementToName());
			invoiceStatementToNameEnd = NameEnd.of(tAccgStatementEntity.getStatementToNameEnd()).getVal();
			invoiceStatementAmount = tAccgStatementEntity.getStatementAmount().toPlainString();
		} else if (AccgDocType.INVOICE.equalsByCode(accgDocType)) {
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			invoiceStatementDate = DateUtils.parseToString(tAccgInvoiceEntity.getInvoiceDate(),
					DateUtils.DATE_FORMAT_NON_DELIMITED);
			invoiceStatementToName = StringUtils
					.removeSpaceCharacter(StringUtils.isEmpty(tAccgInvoiceEntity.getInvoiceToName()) ? "" : tAccgInvoiceEntity.getInvoiceToName());
			invoiceStatementToNameEnd = NameEnd.of(tAccgInvoiceEntity.getInvoiceToNameEnd()).getVal();
			invoiceStatementAmount = tAccgInvoiceEntity.getInvoiceAmount().toPlainString();
		} else {
			throw new RuntimeException("不正な会計書類タイプ[accgDocType=" + accgDocType + "]");
		}

		// ファイル名作成
		if (AccgDocFileType.INVOICE.equals(accgDocFileType)) {
			// 請求書
			fileName = "【御" + AccgDocFileType.INVOICE.getVal() + "】"
					+ invoiceStatementToName
					+ invoiceStatementToNameEnd + "_"
					+ invoiceStatementAmount + "円_"
					+ invoiceStatementDate;
		} else if (AccgDocFileType.STATEMENT.equals(accgDocFileType)) {
			// 精算書
			fileName = "【御" + AccgDocFileType.STATEMENT.getVal() + "】"
					+ invoiceStatementToName
					+ invoiceStatementToNameEnd + "_"
					+ invoiceStatementAmount + "円_"
					+ invoiceStatementDate;
		} else if (AccgDocFileType.DEPOSIT_DETAIL.equals(accgDocFileType)) {
			// 実費明細書
			fileName = "【" + AccgDocFileType.DEPOSIT_DETAIL.getVal() + "】"
					+ invoiceStatementToName
					+ invoiceStatementToNameEnd + "_"
					+ invoiceStatementDate;
		} else if (AccgDocFileType.INVOICE_PAYMENT_PLAN.equals(accgDocFileType)) {
			// 支払計画表
			fileName = "【" + AccgDocFileType.INVOICE_PAYMENT_PLAN.getVal() + "】"
					+ invoiceStatementToName
					+ invoiceStatementToNameEnd + "_"
					+ invoiceStatementDate;
		} else {
			throw new RuntimeException("不正な会計書類ファイル種別[accgDocFileType=" + accgDocFileType + "]");
		}

		// 拡張子
		fileName = fileName + fileExtension.getExtensionWithInDot();

		return fileName;
	}

	/**
	 * メール設定データを参照し、現在日時からダウンロード期日を作成する
	 * 
	 * @return
	 */
	public LocalDate getAccgWebShareDownloadLimitDate() {
		MMailSettingEntity mMailSettingEntity = mMailSettingDao.select();
		long downloadLimitDateCount = mMailSettingEntity.getDownloadDayCount();
		LocalDate now = LocalDate.now();
		// 当日もダウンロード日数に含むため、カウントから-1する
		LocalDate downloadLimitDate = now.plusDays(downloadLimitDateCount - 1);
		return downloadLimitDate;
	}

	/**
	 * 新しい請求書番号を発番します。<br>
	 * 発番時、会計書類-最終採番番号テーブルに最終採番番号として請求書番号を登録します。<br>
	 * 
	 * @return
	 * @throws AppException
	 */
	public String issueNewInvoiceNo() throws AppException {
		// 請求書設定を取得
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();

		// 請求書の最終採番番号を取得
		Long invoiceNoLastUseNumber = this.registUpdateAccgDocLastUsedNumber(
				AccgDocType.INVOICE.getCd(), mInvoiceSettingEntity.getInvoiceNoNumberingType());

		// 請求番号のフォーマット
		String invoiceNo = AccountingUtils.formatNumberingLastNo(invoiceNoLastUseNumber,
				mInvoiceSettingEntity.getInvoiceNoPrefix(), mInvoiceSettingEntity.getInvoiceNoYFmt(),
				mInvoiceSettingEntity.getInvoiceNoMFmt(), mInvoiceSettingEntity.getInvoiceNoDFmt(),
				mInvoiceSettingEntity.getInvoiceNoDelimiter(), mInvoiceSettingEntity.getInvoiceNoZeroPadFlg(),
				mInvoiceSettingEntity.getInvoiceNoZeroPadDigits());

		return invoiceNo;
	}

	/**
	 * 新しい精算書番号を発番します。<br>
	 * 発番時、会計書類-最終採番番号テーブルに最終採番番号として精算書番号を登録します。<br>
	 * 
	 * @return
	 * @throws AppException
	 */
	public String issueNewStatementNo() throws AppException {
		// 精算書設定を取得
		MStatementSettingEntity mStatementSettingEntity = mStatementSettingDao.select();

		// 精算書の最終採番番号を取得
		Long statementNoLastUseNumber = this.registUpdateAccgDocLastUsedNumber(
				AccgDocType.STATEMENT.getCd(), mStatementSettingEntity.getStatementNoNumberingType());

		// 精算書のフォーマット
		String statementNo = AccountingUtils.formatNumberingLastNo(statementNoLastUseNumber,
				mStatementSettingEntity.getStatementNoPrefix(), mStatementSettingEntity.getStatementNoYFmt(),
				mStatementSettingEntity.getStatementNoMFmt(), mStatementSettingEntity.getStatementNoDFmt(),
				mStatementSettingEntity.getStatementNoDelimiter(), mStatementSettingEntity.getStatementNoZeroPadFlg(),
				mStatementSettingEntity.getStatementNoZeroPadDigits());

		return statementNo;
	}

	// =========================================================================
	// ▼ チェック、バリデーション系
	// =========================================================================

	/**
	 * 会計書類が発行済みかどうか
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public boolean isIssued(Long accgDocSeq) {

		// 会計書類情報を取得する
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// データが取得できない場合
			return false;
		}
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			return IssueStatus.isIssued(tAccgStatementEntity.getStatementIssueStatus());

		} else if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			return IssueStatus.isIssued(tAccgInvoiceEntity.getInvoiceIssueStatus());

		} else {
			// 想定しないEnum値
			throw new RuntimeException("想定外のEnum値です");
		}

	}

	/**
	 * 精算完了かどうかを確認します
	 *
	 * @param customerId
	 * @param ankenId
	 * @return 精算完了かどうか
	 */
	public boolean isSeisanComplete(Long customerId, Long ankenId) {

		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		if (tAnkenCustomerEntity == null) {
			// データの取得
			throw new DataNotFoundException("顧客ID : " + customerId + CommonConstant.SPACE + "案件ID : " + ankenId);
		}

		// 完了の場合
		if (AnkenStatus.isSeisanComp(tAnkenCustomerEntity.getAnkenStatus())) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 案件IDに紐づく、新会計関連情報の存在するかどうか
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsAccgDataByAnkenId(Long ankenId) {

		final int accgDocCount = tAccgDocDao.selectAccgDocCountByAnkenId(ankenId);
		final int depositCount = tDepositRecvDao.selectDepositRecvCountByAnkenId(ankenId);
		final int feeCount = tFeeDao.selectFeeCountByAnkenId(ankenId);
		final int salesCount = tSalesDao.selectSalesCountByAnkenId(ankenId);
		final int uncollectibleCount = tUncollectibleDao.selectUncollectibleCountByAnkenId(ankenId);

		if (IntStream.of(accgDocCount, depositCount, feeCount, salesCount, uncollectibleCount).anyMatch(count -> count > 0)) {
			return true;
		}

		return false;
	}

	/**
	 * 預り金テーブルのレコードの各パラメータの値をもとに、<br>
	 * 対象の預り金データが、請求書／精算書で利用可能（チェックして利用可能）かどうかを判定する。
	 * 
	 * @param createdType 作成タイプ
	 * @param tenantBearFlg 事務所負担フラグ
	 * @param usingAccgDocSeq 会計書類SEQ（使用先）
	 * @param depositType 入出金タイプ
	 * @param expenseInvoiceFlg 実費入金フラグ
	 * @param depositCompleteFlg 入出金完了フラグ
	 * 
	 * @return 利用可能の場合：true、利用不可の場合：false
	 */
	public boolean isCanUseDepositRecvOnAccgDoc(DepositRecvCreatedType createdType, SystemFlg tenantBearFlg, Long usingAccgDocSeq,
			DepositType depositType, ExpenseInvoiceFlg expenseInvoiceFlg, SystemFlg depositCompleteFlg) {

		// どの請求書／精算書で利用可能かの判定をするかを指定しない
		Long targetAccgDocSeq = null;

		return this.isCanUseDepositRecvOnAccgDoc(createdType, tenantBearFlg, usingAccgDocSeq,
				depositType, expenseInvoiceFlg, depositCompleteFlg, targetAccgDocSeq);
	}

	/**
	 * 預り金テーブルのレコードの各パラメータの値をもとに、<br>
	 * 対象の預り金データが、対象となる請求書／精算書で利用可能（チェックして利用可能）かどうかを判定する。<br>
	 * ※対象となる請求書／精算書すでに利用されている預り金も「利用可能」と判定される
	 * 
	 * @param createdType 作成タイプ
	 * @param tenantBearFlg 事務所負担フラグ
	 * @param usingAccgDocSeq 会計書類SEQ（使用先）
	 * @param depositType 入出金タイプ
	 * @param expenseInvoiceFlg 実費入金フラグ
	 * @param depositCompleteFlg 入出金完了フラグ
	 * @param targetAccgDocSeq 対象となる請求書／精算書の会計書類SEQ
	 * 
	 * @return 利用可能の場合：true、利用不可の場合：false
	 */
	public boolean isCanUseDepositRecvOnTargetAccgDocSeq(DepositRecvCreatedType createdType, SystemFlg tenantBearFlg, Long usingAccgDocSeq,
			DepositType depositType, ExpenseInvoiceFlg expenseInvoiceFlg, SystemFlg depositCompleteFlg, Long targetAccgDocSeq) {

		// どの請求書／精算書で利用可能かの判定をするかを指定する（自身で既に利用されている預り金も「利用可能」と判定される）
		// targetAccgDocSeq = targetAccgDocSeq;

		return this.isCanUseDepositRecvOnAccgDoc(createdType, tenantBearFlg, usingAccgDocSeq,
				depositType, expenseInvoiceFlg, depositCompleteFlg, targetAccgDocSeq);
	}

	/**
	 * 対象の会計書類（請求書／精算書）が預り金請求のデータを作成しており、かつ、<br>
	 * その預り金請求データが、他の請求書／精算書で利用されている（チェックして利用されている）かどうかを判定する。
	 * 
	 * <pre>
	 * このメソッドの判定がtrueとなる場合、
	 * 他の請求書／精算書で利用されている預り金請求データの金額が変更される下記の操作が行えないように制御をする必要がある。
	 * 
	 * ・対象の会計書類（請求書／精算書）の「削除」
	 * ・対象の会計書類（請求書／精算書）の「発行前に戻す」
	 * ・対象の会計書類（請求書／精算書）の取引実績データのうち、「実費／預り金」に入金をしているデータの「削除」「編集」
	 * </pre>
	 * 
	 * @param accgDocSeq 対象の会計書類（請求書／精算書）のSEQ
	 * @return true:他の請求書／精算書で利用されている預り金請求データあり、false:他の請求書／精算書で利用されている預り金請求データなし
	 */
	public boolean isCreatedDepositInvoiceAndUsingOther(Long accgDocSeq) {

		// 対象の会計書類（請求書／精算書）から作成された預り金データ
		List<TDepositRecvEntity> createdDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByAccgDocSeq(accgDocSeq);

		if (CollectionUtils.isEmpty(createdDepositRecvEntityList)) {
			// 作成された預り金データがない場合
			// -> 対象データなし
			return false;
		}

		// 預り金請求のデータで、他の請求書／精算書で利用されているデータ
		List<TDepositRecvEntity> depositInvoiceUsingOtherEntityList = createdDepositRecvEntityList.stream()
				// 発行時に予定として作成されたデータ
				.filter(entity -> DepositRecvCreatedType.of(entity.getCreatedType()) == DepositRecvCreatedType.CREATED_BY_ISSUANCE)
				// 入金データ
				.filter(entity -> DepositType.of(entity.getDepositType()) == DepositType.NYUKIN)
				// 実費入金以外のデータ
				.filter(entity -> ExpenseInvoiceFlg.of(entity.getExpenseInvoiceFlg()) == ExpenseInvoiceFlg.EXCEPT_EXPENSE)
				// ここまでの絞り込みで「預り金請求のデータ」のみに絞り込まれる（発行時に予定として作成される「入金」データは、実費請求のデータか、預り金請求のデータの2種類しかないため）
				// 他の請求書／精算書で利用されている
				.filter(entity -> entity.getUsingAccgDocSeq() != null)
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(depositInvoiceUsingOtherEntityList)) {
			// 他の請求書／精算書で利用されている、預り金請求のデータがない場合
			// -> 対象データなし
			return false;
		}

		// 他の請求書／精算書で利用されている、預り金請求のデータがある場合
		return true;
	}

	/**
	 * 請求項目-預り金テーブルに預り金情報が存在するかチェックします。<br>
	 * 存在する場合はtrueを返します。<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public boolean chekcIfInvoiceDepositExists(Long accgDocSeq) {
		boolean isInvoiceDepositExists = false;
		// 請求項目-預り金情報取得
		List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByAccgDocSeq(accgDocSeq);
		if (CollectionUtils.isEmpty(tAccgDocInvoiceDepositEntityList)) {
			return isInvoiceDepositExists;
		}
		long depositDataCount = tAccgDocInvoiceDepositEntityList.stream().filter(entity -> InvoiceDepositType.DEPOSIT.equalsByCode(entity.getInvoiceDepositType())).count();
		if (depositDataCount > 0) {
			isInvoiceDepositExists = true;
		}

		return isInvoiceDepositExists;
	}

	/**
	 * 名簿IDが案件の顧客か関与者かチェックします。<br>
	 * 案件の顧客や関与者であればtrueを返します。<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	public boolean checkAnkenIsCustomerKanyosha(Long personId, Long ankenId) throws AppException {
		boolean isCustomerKanyosha = true;
		// 案件の顧客と関与者情報取得
		List<CustomerKanyoshaPulldownDto> customerKanyoshaList = tCustomerCommonDao.selectCustomerKanyoshaPulldownByAnkenId(ankenId);
		if (CollectionUtils.isEmpty(customerKanyoshaList)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 名簿IDが顧客か関与者か
		int size = customerKanyoshaList.stream().filter(dto -> dto.getId().equals(personId)).collect(Collectors.toList()).size();
		if (size == 0) {
			isCustomerKanyosha = false;
		}
		
		return isCustomerKanyosha;
	}

	/**
	 * 請求項目入力データの報酬について、ステータスが全て「未請求」かチェックします。<br>
	 * 報酬が全て「未請求」の場合 true を返します。
	 * 
	 * @param feeSeqList
	 * @throws AppException
	 */
	public boolean checkFeePaymentStatusIsUnclaimed(List<Long> feeSeqList) throws AppException {
		boolean isFeePaymentStatusOfAllUnclaimed = true;
		
		// 報酬SEQがない場合は「未請求」とする
		if (CollectionUtils.isEmpty(feeSeqList)) {
			return isFeePaymentStatusOfAllUnclaimed;
		}
		
		// 報酬データ取得
		List<TFeeEntity> feeEntityList = tFeeDao.selectFeeByFeeSeqList(feeSeqList);
		if (CollectionUtils.isEmpty(feeEntityList) || feeSeqList.size() != feeEntityList.size()) {
			// 報酬SEQ分報酬データが取得できなかった場合は楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 報酬が全て「未請求」か
		isFeePaymentStatusOfAllUnclaimed = feeEntityList.stream().allMatch(entity -> FeePaymentStatus.UNCLAIMED.equalsByCode(entity.getFeePaymentStatus()));
		
		return isFeePaymentStatusOfAllUnclaimed;
	}

	// =========================================================================
	// ▼ 登録／更新／削除系
	// =========================================================================

	/**
	 * Dtoの値をもとに請求項目-消費税（税率8%）データを更新します。
	 * 
	 * @param accgDocSeq
	 * @param accgInvoiceStatementAmountDto
	 * @throws AppException
	 */
	public void updateAccgInvoiceTax8(Long accgDocSeq, AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) throws AppException {

		// 対象額（値引き前）
		BigDecimal totalTaxAmount8TargetFeeNonDiscount = accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFeeNonDiscount();
		// 消費税額（値引き前）
		BigDecimal totalTaxAmount8NonDiscount = accgInvoiceStatementAmountDto.getTotalTaxAmount8NonDiscount();

		// 対象額からの値引き額（値引きがある場合）
		BigDecimal totalTaxAmount8TargetDiscount = accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetDiscount();
		// 消費税額（値引き分）
		BigDecimal totalTaxAmount8Discount = accgInvoiceStatementAmountDto.getTotalTaxAmount8Discount();

		// 登録済み8%消費税データ取得
		TAccgInvoiceTaxEntity tAccgInvoiceTax8Entity = tAccgInvoiceTaxDao.selectAccgInvoiceTax8ByAccgDocSeq(accgDocSeq);

		if (totalTaxAmount8TargetFeeNonDiscount.precision() - totalTaxAmount8TargetFeeNonDiscount.scale() > AccgConstant.MX_TOTAL_INVOICE_AMOUNT) {
			// 計算した結果、対象額（値引き前）がDBに登録できる桁をオーバーしているためエラーとする
			TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
			if (tAccgDocEntity == null) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			
			AccgDocType docType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
			if (docType.isInvoice()) {
				// 請求書の場合
				throw new AppException(MessageEnum.MSG_E00188, null, "請求書", "請求");
			} else if (docType.isStatement()) {
				// 精算書の場合
				throw new AppException(MessageEnum.MSG_E00188, null, "精算書", "精算");
			} else {
				// 想定外のEnum値
				throw new RuntimeException("想定外のEnum値");
			}
		} else if (totalTaxAmount8NonDiscount == null || LoiozNumberUtils.equalsZero(totalTaxAmount8NonDiscount)) {
			// 計算した結果、消費税がないため登録データがあれば削除する
			if (tAccgInvoiceTax8Entity == null) {
				return;
			}
			// 削除
			this.deleteAccgInvoiceTax(tAccgInvoiceTax8Entity);
		} else {
			// 計算した結果、消費税があるため登録 or 更新する
			if (tAccgInvoiceTax8Entity == null) {
				TAccgInvoiceTaxEntity entity = new TAccgInvoiceTaxEntity();
				entity.setAccgDocSeq(accgDocSeq);
				entity.setTaxRateType(TaxRate.EIGHT_PERCENT.getCd());

				// 値引き前の対象額、税額
				entity.setTaxableAmount(totalTaxAmount8TargetFeeNonDiscount);
				entity.setTaxAmount(totalTaxAmount8NonDiscount);

				// 対象額、税額からの値引き額
				if (totalTaxAmount8Discount == null || LoiozNumberUtils.equalsZero(totalTaxAmount8Discount)) {
					// 値引きがなし
					entity.setDiscountTaxableAmount(null);
					entity.setDiscountTaxAmount(null);
				} else {
					// 値引きあり
					entity.setDiscountTaxableAmount(totalTaxAmount8TargetDiscount);
					entity.setDiscountTaxAmount(totalTaxAmount8Discount);
				}

				// 登録
				this.insertAccgInvoiceTax(entity);

			} else {

				// 値引き前の対象額、税額
				tAccgInvoiceTax8Entity.setTaxableAmount(totalTaxAmount8TargetFeeNonDiscount);
				tAccgInvoiceTax8Entity.setTaxAmount(totalTaxAmount8NonDiscount);

				// 対象額、税額からの値引き額
				if (totalTaxAmount8Discount == null || LoiozNumberUtils.equalsZero(totalTaxAmount8Discount)) {
					// 値引きがなし
					tAccgInvoiceTax8Entity.setDiscountTaxableAmount(null);
					tAccgInvoiceTax8Entity.setDiscountTaxAmount(null);
				} else {
					// 値引きあり
					tAccgInvoiceTax8Entity.setDiscountTaxableAmount(totalTaxAmount8TargetDiscount);
					tAccgInvoiceTax8Entity.setDiscountTaxAmount(totalTaxAmount8Discount);
				}

				// 更新
				this.updateAccgInvoiceTax(tAccgInvoiceTax8Entity);
			}
		}
	}

	/**
	 * Dtoの値をもとに請求項目-消費税（税率10%）データを更新します。
	 * 
	 * @param accgDocSeq
	 * @param accgInvoiceStatementAmountDto
	 * @throws AppException
	 */
	public void updateAccgInvoiceTax10(Long accgDocSeq, AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) throws AppException {

		// 対象額（値引き前）
		BigDecimal totalTaxAmount10TargetFeeNonDiscount = accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFeeNonDiscount();
		// 消費税額（値引き前）
		BigDecimal totalTaxAmount10NonDiscount = accgInvoiceStatementAmountDto.getTotalTaxAmount10NonDiscount();

		// 対象額からの値引き額（値引きがある場合）
		BigDecimal totalTaxAmount10TargetDiscount = accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetDiscount();
		// 消費税額（値引き分）
		BigDecimal totalTaxAmount10Discount = accgInvoiceStatementAmountDto.getTotalTaxAmount10Discount();

		// 登録済み10%消費税データ取得
		TAccgInvoiceTaxEntity tAccgInvoiceTax10Entity = tAccgInvoiceTaxDao.selectAccgInvoiceTax10ByAccgDocSeq(accgDocSeq);

		if (totalTaxAmount10TargetFeeNonDiscount.precision() - totalTaxAmount10TargetFeeNonDiscount.scale() > AccgConstant.MX_TOTAL_INVOICE_AMOUNT) {
			// 計算した結果、対象額（値引き前）がDBに登録できる桁をオーバーしているためエラーとする
			TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
			if (tAccgDocEntity == null) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			
			AccgDocType docType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
			if (docType.isInvoice()) {
				// 請求書の場合
				throw new AppException(MessageEnum.MSG_E00188, null, "請求書", "請求");
			} else if (docType.isStatement()) {
				// 精算書の場合
				throw new AppException(MessageEnum.MSG_E00188, null, "精算書", "精算");
			} else {
				// 想定外のEnum値
				throw new RuntimeException("想定外のEnum値");
			}
		} else if (totalTaxAmount10NonDiscount == null || LoiozNumberUtils.equalsZero(totalTaxAmount10NonDiscount)) {
			// 計算した結果、消費税がないため登録データがあれば削除する
			if (tAccgInvoiceTax10Entity == null) {
				return;
			}
			// 削除
			this.deleteAccgInvoiceTax(tAccgInvoiceTax10Entity);
		} else {
			// 計算した結果、消費税があるため登録 or 更新する
			if (tAccgInvoiceTax10Entity == null) {
				TAccgInvoiceTaxEntity entity = new TAccgInvoiceTaxEntity();
				entity.setAccgDocSeq(accgDocSeq);
				entity.setTaxRateType(TaxRate.TEN_PERCENT.getCd());

				// 値引き前の対象額、税額
				entity.setTaxableAmount(totalTaxAmount10TargetFeeNonDiscount);
				entity.setTaxAmount(totalTaxAmount10NonDiscount);

				// 対象額、税額からの値引き額
				if (totalTaxAmount10Discount == null || LoiozNumberUtils.equalsZero(totalTaxAmount10Discount)) {
					// 値引きがなし
					entity.setDiscountTaxableAmount(null);
					entity.setDiscountTaxAmount(null);
				} else {
					// 値引きあり
					entity.setDiscountTaxableAmount(totalTaxAmount10TargetDiscount);
					entity.setDiscountTaxAmount(totalTaxAmount10Discount);
				}

				this.insertAccgInvoiceTax(entity);
			} else {
				// 値引き前の対象額、税額
				tAccgInvoiceTax10Entity.setTaxableAmount(totalTaxAmount10TargetFeeNonDiscount);
				tAccgInvoiceTax10Entity.setTaxAmount(totalTaxAmount10NonDiscount);

				// 対象額、税額からの値引き額
				if (totalTaxAmount10Discount == null || LoiozNumberUtils.equalsZero(totalTaxAmount10Discount)) {
					// 値引きがなし
					tAccgInvoiceTax10Entity.setDiscountTaxableAmount(null);
					tAccgInvoiceTax10Entity.setDiscountTaxAmount(null);
				} else {
					// 値引きあり
					tAccgInvoiceTax10Entity.setDiscountTaxableAmount(totalTaxAmount10TargetDiscount);
					tAccgInvoiceTax10Entity.setDiscountTaxAmount(totalTaxAmount10Discount);
				}

				// 更新
				this.updateAccgInvoiceTax(tAccgInvoiceTax10Entity);
			}
		}
	}

	/**
	 * 請求項目-源泉徴収データを更新します。
	 * 
	 * @param accgDocSeq
	 * @param accgInvoiceStatementAmountDto
	 * @throws AppException
	 */
	public void updateAccgInvoiceWithholding(Long accgDocSeq, AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) throws AppException {

		// 源泉徴収額、対象額を取得
		BigDecimal totalWithholdingAmount = accgInvoiceStatementAmountDto.getTotalWithholdingAmount();
		BigDecimal totalWithholdingTarget = accgInvoiceStatementAmountDto.getTotalWithholdingTargetFee();

		// 登録済み源泉徴収データ取得
		TAccgInvoiceWithholdingEntity tAccgInvoiceWithholdingEntity = tAccgInvoiceWithholdingDao.selectAccgInvoiceWithholdingByAccgDocSeq(accgDocSeq);

		if (totalWithholdingTarget.precision() - totalWithholdingTarget.scale() > AccgConstant.MX_TOTAL_INVOICE_AMOUNT) {
			// 計算した結果、対象額がDBに登録できる桁をオーバーしているためエラーとする
			TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
			if (tAccgDocEntity == null) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			
			AccgDocType docType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
			if (docType.isInvoice()) {
				// 請求書の場合
				throw new AppException(MessageEnum.MSG_E00188, null, "請求書", "請求");
			} else if (docType.isStatement()) {
				// 精算書の場合
				throw new AppException(MessageEnum.MSG_E00188, null, "精算書", "精算");
			} else {
				// 想定外のEnum値
				throw new RuntimeException("想定外のEnum値");
			}
		} else if (totalWithholdingAmount == null || LoiozNumberUtils.equalsZero(totalWithholdingAmount)) {
			// 計算した結果、源泉徴収額がないため登録データがあれば削除する
			if (tAccgInvoiceWithholdingEntity == null) {
				return;
			}
			this.deleteAccgInvoiceWithholding(tAccgInvoiceWithholdingEntity);
		} else {
			// 計算した結果、源泉徴収額があるため登録 or 更新する
			if (tAccgInvoiceWithholdingEntity == null) {
				TAccgInvoiceWithholdingEntity entity = new TAccgInvoiceWithholdingEntity();
				entity.setAccgDocSeq(accgDocSeq);
				entity.setSourceWithholdingAmount(totalWithholdingTarget);
				entity.setWithholdingAmount(totalWithholdingAmount);
				this.insertAccgInvoiceWithholding(entity);
			} else {
				tAccgInvoiceWithholdingEntity.setSourceWithholdingAmount(totalWithholdingTarget);
				tAccgInvoiceWithholdingEntity.setWithholdingAmount(totalWithholdingAmount);
				this.updateAccgInvoiceWithholding(tAccgInvoiceWithholdingEntity);
			}
		}
	}

	/**
	 * 請求書を新規作成する<br>
	 * ※請求項目（報酬）なし<br>
	 * ※請求項目（預り金）なし<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	public Long registInvoice(Long personId, Long ankenId) throws AppException {
		return this.registInvoice(personId, ankenId, null, null);
	}

	/**
	 * 請求書を新規作成する<br>
	 * ※請求項目（報酬）あり<br>
	 * ※請求項目（預り金）あり<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeqList
	 * @param depositRecvSeqList
	 * @return
	 * @throws AppException
	 */
	public Long registInvoice(Long personId, Long ankenId, List<Long> feeSeqList, List<Long> depositRecvSeqList) throws AppException {

		// 会計書類登録
		Long accgDocSeq = this.registAccgDoc(personId, ankenId, AccgDocType.INVOICE.getCd());

		// 請求書登録
		Long invoiceSeq = this.registAccgInvoice(accgDocSeq, personId, ankenId);

		// 会計書類-対応-登録
		this.registAccgDocAct(accgDocSeq, AccgDocActType.NEW);

		// 請求項目、請求項目-報酬の登録
		this.registAccgDocInvoiceFee(accgDocSeq, ankenId, personId, feeSeqList);

		// 請求項目、請求項目-預り金の登録
		this.registAccgDocInvoiceDeposit(accgDocSeq, ankenId, personId, depositRecvSeqList);

		return invoiceSeq;
	}

	/**
	 * 会計書類-対応テーブルに登録します。
	 * 
	 * @param accgDocSeq
	 * @param accgDocActType
	 * @return
	 * @throws AppException
	 */
	public TAccgDocActEntity registAccgDocAct(Long accgDocSeq, AccgDocActType accgDocActType) throws AppException {
		// 会計書類-対応-登録
		TAccgDocActEntity tAccgDocActEntity = new TAccgDocActEntity();
		tAccgDocActEntity.setAccgDocSeq(accgDocSeq);
		tAccgDocActEntity.setActType(accgDocActType.getCd());
		tAccgDocActEntity.setActAt(LocalDateTime.now());
		tAccgDocActEntity.setActBy(SessionUtils.getLoginAccountSeq());

		// 登録処理
		int insertCount = tAccgDocActDao.insert(tAccgDocActEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return tAccgDocActEntity;
	}

	/**
	 * 会計書類-ダウンロード情報の登録処理
	 * 
	 * @param accgDocActSendSeq
	 * @param tenantName
	 * @param issueDate
	 * @param password
	 * @return 登録データのPK
	 * @throws AppException
	 */
	public Long registAccgDocWebDownload(Long accgDocActSendSeq, String tenantName, LocalDate issueDate, String password) throws AppException {

		TAccgDocActSendEntity tAccgDocActSendEntity = tAccgDocActSendDao.selectAccgDocActSendByActSendSeq(accgDocActSendSeq);
		if (tAccgDocActSendEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAccgDocDownloadEntity tAccgDocDownloadEntity = new TAccgDocDownloadEntity();
		tAccgDocDownloadEntity.setAccgDocActSendSeq(accgDocActSendSeq);
		tAccgDocDownloadEntity.setDownloadViewUrlKey(StringUtils.randomAlphanumeric(AccgConstant.ACCG_FILE_VALIFICATION_KEY_LENGTH));
		tAccgDocDownloadEntity.setVerificationToken(StringUtils.randomAlphanumeric(AccgConstant.ACCG_FILE_VALIFICATION_KEY_LENGTH));
		tAccgDocDownloadEntity.setTenantName(tenantName);
		tAccgDocDownloadEntity.setSendTo(tAccgDocActSendEntity.getSendTo());
		tAccgDocDownloadEntity.setIssueDate(issueDate);
		tAccgDocDownloadEntity.setDownloadLimitDate(tAccgDocActSendEntity.getDownloadLimitDate());

		if (!StringUtils.isEmpty(password)) {
			// パスワードが入力されたときのみ設定する
			tAccgDocDownloadEntity.setDownloadViewPassword(new BCryptPasswordEncoder().encode(password));
		}

		try {
			// 登録処理
			tAccgDocDownloadDao.insert(tAccgDocDownloadEntity);

			return tAccgDocDownloadEntity.getAccgDocDownloadSeq();

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * M0013(会計書類 WEB共有 顧客宛)のメール送信処理
	 *
	 * @param accgDocType
	 * @param tenantSeq
	 * @param accgDocActSendSeq
	 * @param accgDocDownloadSeq
	 * @param mailSignature
	 */
	public void sendMail4M0013(AccgDocType accgDocType, Long tenantSeq, Long accgDocActSendSeq, Long accgDocDownloadSeq, String mailSignature) {

		// M0013MailBuilder（会計書類送付 WEB共有 顧客宛）作成
		M0013MailBuilder builder = this.createM0013MailBuilder(accgDocActSendSeq, accgDocType, tenantSeq, accgDocDownloadSeq, mailSignature);;

		mailService.sendAsync(builder);
	}

	/**
	 * M0013(会計書類 WEB共有 顧客宛)のメール送信処理<br>
	 * M0014(会計書類 WEB共有パスワード 顧客宛)のメール送信処理<br>
	 * 
	 * @param accgDocActSendSeq
	 * @param password
	 */
	public void sendMail4M0013AndM0014(Long accgDocActSendSeq, String password, AccgDocType accgDocType, Long tenantSeq, Long accgDocDownloadSeq, String mailSignature) {

		if (StringUtils.isEmpty(password)) {
			// パスワードが存在しない場合はエラーとする
			throw new RuntimeException("パスワードが未入力によるメソッド呼び出しです");
		}

		// M0013MailBuilder（会計書類送付 WEB共有 顧客宛）作成
		M0013MailBuilder builder13 = this.createM0013MailBuilder(accgDocActSendSeq, accgDocType, tenantSeq, accgDocDownloadSeq, mailSignature);;

		// M0014MailBuilder（会計書類送付 会計書類送付 WEB共有パスワード 顧客宛）作成
		M0014MailBuilder builder14 = this.createM0014MailBuilder(accgDocActSendSeq, password);

		List<AbstractMailBuilder> builderList = new ArrayList<>();
		builderList.add(builder13);
		builderList.add(builder14);

		mailService.sendAsyncMails(builderList);
	}

	/**
	 * M0015(会計書類送付 メール送付 顧客宛)のメール送信
	 * 
	 * @param accgDocType
	 * @param tenantSeq
	 * @param accgDocActSendSeq
	 * @param mailSignature
	 * @param accgDocSeq
	 */
	public void sendMail4M0015(AccgDocType accgDocType, Long tenantSeq, Long accgDocActSendSeq,
			String mailSignature, Long accgDocSeq) {

		M0015MailBuilder builder = new M0015MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_15.getCd(), builder);

		// データの取得
		TAccgDocActSendEntity tAccgDocActSendEntity = tAccgDocActSendDao
				.selectAccgDocActSendByActSendSeq(accgDocActSendSeq);

		// 送信先の設定
		builder.setWorkTo(Arrays.asList(tAccgDocActSendEntity.getSendTo()));
		builder.setWorkCc(StringUtils.toArray(tAccgDocActSendEntity.getSendCc()));
		builder.setWorkBcc(StringUtils.toArray(tAccgDocActSendEntity.getSendBcc()));
		builder.setWorkReplyTo(tAccgDocActSendEntity.getReplyTo());

		// 送信元の名前設定
		builder.setWorkFromName(tAccgDocActSendEntity.getSendFromName());

		// 内容の設定
		builder.makeTitle(tAccgDocActSendEntity.getSendSubject());
		builder.makeBody(tAccgDocActSendEntity.getSendBody(), mailSignature);

		// ファイルオブジェクトの取得
		FileContentsDto fileContentsDto = getAccgSendFileZip(accgDocActSendSeq);

		builder.setWorkFileContents(fileContentsDto.getByteArray());
		builder.setWorkFileName(this.createAccgDocZipFileName(accgDocSeq, accgDocType,
				FileExtension.ofExtension(fileContentsDto.getExtension())));

		// メールの送信
		mailService.sendAsync(builder);
	}

	/**
	 * 精算書を新規作成する
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	public Long registStatement(Long personId, Long ankenId) throws AppException {

		// 会計書類登録
		Long accgDocSeq = this.registAccgDoc(personId, ankenId, AccgDocType.STATEMENT.getCd());

		// 精算書登録
		Long accgStatementSeq = this.registAccgStatement(accgDocSeq, personId, ankenId);

		// 会計書類-対応-登録
		this.registAccgDocAct(accgDocSeq, AccgDocActType.NEW);

		return accgStatementSeq;
	}

	/**
	 * 精算書を新規作成する<br>
	 * ※請求項目（報酬）あり<br>
	 * ※請求項目（預り金）あり<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeqList
	 * @param depositRecvSeqList
	 * @return
	 * @throws AppException
	 */
	public Long registStatement(Long personId, Long ankenId, List<Long> feeSeqList, List<Long> depositRecvSeqList) throws AppException {

		// 会計書類登録
		Long accgDocSeq = this.registAccgDoc(personId, ankenId, AccgDocType.STATEMENT.getCd());

		// 精算書登録
		Long accgStatementSeq = this.registAccgStatement(accgDocSeq, personId, ankenId);

		// 会計書類-対応-登録
		this.registAccgDocAct(accgDocSeq, AccgDocActType.NEW);

		// 請求項目、請求項目-報酬の登録
		this.registAccgDocInvoiceFee(accgDocSeq, ankenId, personId, feeSeqList);

		// 請求項目、請求項目-預り金の登録
		this.registAccgDocInvoiceDeposit(accgDocSeq, ankenId, personId, depositRecvSeqList);

		return accgStatementSeq;
	}

	/**
	 * 売上テーブルに売上データを 登録します<br>
	 * 売上合計【見込】（税込）、売上合計【実績】（税込）は0のデータを作成します。
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 * @throws AppException
	 */
	public TSalesEntity registSales(Long ankenId, Long personId) throws AppException {
		// 売上データ作成
		TSalesEntity tSalesEntity = new TSalesEntity();
		tSalesEntity.setAnkenId(ankenId);
		tSalesEntity.setPersonId(personId);
		tSalesEntity.setSalesAmountExpect(BigDecimal.ZERO);
		tSalesEntity.setSalesAmountResult(BigDecimal.ZERO);

		int insertCount = tSalesDao.insert(tSalesEntity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return tSalesEntity;
	}

	/**
	 * 未精算の報酬を会計書類SEQに紐づけ、請求項目データを登録します（請求書、精算書への紐づけ）。
	 * 
	 * @param unPaidFeeList
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void registUnPaidFee(List<InvoiceRowInputForm> unPaidFeeList, Long accgDocSeq, Long ankenId, Long personId) throws AppException {
		if (CollectionUtils.isEmpty(unPaidFeeList)) {
			return;
		}

		// 請求項目の登録
		List<TAccgDocInvoiceEntity> accgDocInvoiceEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : unPaidFeeList) {
			TAccgDocInvoiceEntity entity = new TAccgDocInvoiceEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setDocInvoiceOrder(row.getDocInvoiceOrder());
			accgDocInvoiceEntityList.add(entity);
		}
		List<TAccgDocInvoiceEntity> afterInsertAccgDocInvoiceEntityList = this
				.batchInsertTAccgDocInvoice(accgDocInvoiceEntityList);

		// 登録時の請求項目SEQをformリストにセット
		for (int i = 0; i < afterInsertAccgDocInvoiceEntityList.size(); i++) {
			unPaidFeeList.get(i).setDocInvoiceSeq(afterInsertAccgDocInvoiceEntityList.get(i).getDocInvoiceSeq());
		}
		
		// 報酬が他の請求書、精算書で使用されている場合は楽観ロックエラー
		List<Long> feeSeqList = unPaidFeeList.stream().map(row -> row.getFeeSeq()).collect(Collectors.toList());
		List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeByFeeSeqList(feeSeqList);
		if (!CollectionUtils.isEmpty(tAccgDocInvoiceFeeEntityList)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 請求項目-報酬の登録
		List<TAccgDocInvoiceFeeEntity> accgDocInvoiceFeeEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : unPaidFeeList) {
			TAccgDocInvoiceFeeEntity entity = new TAccgDocInvoiceFeeEntity();
			entity.setDocInvoiceSeq(row.getDocInvoiceSeq());
			entity.setFeeSeq(row.getFeeSeq());
			entity.setFeeTransactionDate(
					DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setSumText(row.getSumText());
			accgDocInvoiceFeeEntityList.add(entity);
		}
		this.batchInsertAccgDocInvoiceFee(accgDocInvoiceFeeEntityList);

		// 報酬データに会計書類SEQをセット
		List<TFeeEntity> tFeeEntityList = tFeeDao.selectFeeByFeeSeqList(feeSeqList);
		if (CollectionUtils.isEmpty(tFeeEntityList)) {
			throw new DataNotFoundException("報酬情報が存在しません。[feeSeqList=" + feeSeqList + "]");
		}
		for (TFeeEntity entity : tFeeEntityList) {
			entity.setAccgDocSeq(accgDocSeq);
		}
		this.batchUpdateTFee(tFeeEntityList);
	}

	/**
	 * 請求項目-預り金SEQに紐づく請求項目-預り金情報、請求項目情報を一括削除する
	 * 
	 * @param docInvoiceDepositSeqList
	 * @throws AppException
	 */
	public void deleteTAccgDocInvoiceDepositAndDocInvoice(List<Long> docInvoiceDepositSeqList) throws AppException {
		if (CollectionUtils.isEmpty(docInvoiceDepositSeqList)) {
			return;
		}

		//
		// 請求項目-預り金の削除
		//

		// 請求項目-預り金取得
		List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceDepositSeqList(docInvoiceDepositSeqList);
		if (CollectionUtils.isEmpty(tAccgDocInvoiceDepositEntityList)) {
			throw new DataNotFoundException("請求項目-預り金情報が存在しません。[docInvoiceDepositSeqList=" + docInvoiceDepositSeqList + "]");
		}

		// 削除
		this.batchDeleteTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntityList);

		//
		// 請求項目の削除
		//

		// 削除された請求項目-預り金と紐づく、請求項目を取得
		List<Long> docInvoiceSeqList = tAccgDocInvoiceDepositEntityList.stream().map(e -> e.getDocInvoiceSeq()).collect(Collectors.toList());
		List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
		if (CollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
			throw new DataNotFoundException("請求項目が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
		}

		// 削除
		this.batchDeleteTAccgDocInvoice(tAccgDocInvoiceEntityList);
	}

	/**
	 * 会計書類SEQの請求項目情報を再計算し請求項目-消費税、請求項目-源泉徴収税データを更新します。
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void updateAccgInvoiceTaxAndWithholding(Long accgDocSeq) throws AppException {
		if (accgDocSeq == null) {
			return;
		}
		// 請求項目情報取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// Dtoの値をもとに金額を再計算し、
		// 請求項目-消費税、請求項目-源泉徴収税のデータを更新
		this.recalcAndUpdateAccgInvoiceTaxAndWithholding(accgDocSeq, accgInvoiceStatementAmountDto);
	}

	/**
	 * 請求書、精算書作成時の売上データ更新処理。<br>
	 * 売上テーブルの 売上合計【見込】（税込） 、売上合計【実績】（税込） を更新します。<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void updateSalesAmount(Long personId, Long ankenId) throws AppException {
		if (personId == null || ankenId == null) {
			return;
		}
		// 売上合計【見込み】（税込み）を更新
		this.recalcSalesAmountExpectUpdate(personId, ankenId);

		// 売上合計【実績】（税込）を更新
		this.recalcSalesAmountResultUpdate(personId, ankenId);
	}

	/**
	 * 会計書類に紐づく報酬のステータスを取引実績の報酬金額に基づいて設定する
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void updateFeeStatusByAccgDoc(Long accgDocSeq) throws AppException {

		// 取引実績情報を取得する
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		if (tAccgRecordEntity == null) {
			// 取引実績が作られていない場合は楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TFeeEntity> tFeeEntities = tFeeDao.selectFeeEntityByAccgDocSeq(accgDocSeq);
		List<TAccgRecordDetailEntity> tAccgRecordDetailEntities = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgDocSeq(accgDocSeq);
		List<BigDecimal> feeAmountList = tAccgRecordDetailEntities.stream().map(TAccgRecordDetailEntity::getRecordFeeAmount).collect(Collectors.toList());
		BigDecimal recordFeeAmount = AccountingUtils.calcTotal(feeAmountList);

		FeePaymentStatus status;
		if (LoiozNumberUtils.equalsZero(recordFeeAmount)) {
			// 報酬金額が0円の場合 -> 入金待ち
			status = FeePaymentStatus.AWAITING_PAYMENT;

		} else if (LoiozNumberUtils.isLessThan(recordFeeAmount, tAccgRecordEntity.getFeeAmountExpect())) {
			// 報酬金額が報酬見込み金額より少ない場合 -> 一部入金
			status = FeePaymentStatus.PARTIAL_DEPOSIT;

		} else if (LoiozNumberUtils.equalsDecimal(recordFeeAmount, tAccgRecordEntity.getFeeAmountExpect())) {
			// 報酬金額が報酬見込み金額と同じ場合 -> 入金済み
			status = FeePaymentStatus.DEPOSITED;

		} else {
			// それ以外のケースはエラーとする
			throw new RuntimeException("想定外のデータパターン");
		}

		// 報酬情報にステータスを設定
		List<TFeeEntity> tFeeUpdateEntities = tFeeEntities.stream().peek(e -> e.setFeePaymentStatus(status.getCd())).collect(Collectors.toList());

		try {
			// 更新処理
			tFeeDao.batchUpdate(tFeeUpdateEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * DB値から請求書ステータスを判別し、更新処理を行う
	 * 
	 * @param accgDocSeq
	 */
	public void updateInvoiceStatus(Long accgDocSeq) throws AppException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 発行ステータスが、「下書き」の場合 -> 「発行待ち」
		if (IssueStatus.DRAFT.equalsByCode(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.DRAFT.getCd());

			try {
				// 更新処理をして終了
				tAccgInvoiceDao.update(tAccgInvoiceEntity);
				return;

			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
		}

		// ======================| 以下、発行済み | ===========================

		// 取引実績情報を取得(未発行だとヌルポ発生)
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		List<AccgRecordDetailBean> accgRecordDetailBeanList = tAccgRecordDetailDao.selectAccgRecordDetailBeanByAccgRecordSeq(tAccgRecordEntity.getAccgRecordSeq());

		BigDecimal recordFeeAmountExpect = tAccgRecordEntity.getFeeAmountExpect();
		BigDecimal recordDepositRecvAmountExpect = tAccgRecordEntity.getDepositRecvAmountExpect();
		BigDecimal recordAmountExpect = AccountingUtils.calcTotal(recordFeeAmountExpect, recordDepositRecvAmountExpect);

		List<BigDecimal> totalRecordAmountList = accgRecordDetailBeanList.stream().map(AccgRecordDetailBean::getRecordAmount).collect(Collectors.toList());
		List<BigDecimal> totalRecordDepositPaymentAmountAmountList = accgRecordDetailBeanList.stream().map(AccgRecordDetailBean::getRecordDepositPaymentAmount).collect(Collectors.toList());

		BigDecimal totalRecordAmount = AccountingUtils.calcTotal(totalRecordAmountList);
		BigDecimal totalRecordDepositPayment = AccountingUtils.calcTotal(totalRecordDepositPaymentAmountAmountList);

		// 請求書も過払い処理を行った場合、出金データが作成されるので、減算を行う(加入金額分も実績に登録されているのでマイナス値になることはない)
		BigDecimal totalAmount = totalRecordAmount.subtract(totalRecordDepositPayment);

		if (tAccgInvoiceEntity.getUncollectibleDetailSeq() != null) {
			// 回収不能詳細SEQが存在する場合 -> 回収不能
			// ※ 回収不能詳細SEQのレコード存在チェックは行わない
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.UNCOLLECTIBLE.getCd());

		} else if (CollectionUtils.isEmpty(accgRecordDetailBeanList)
				|| accgRecordDetailBeanList.stream().allMatch(e -> RecordType.TRANSFER_DEPOSIT_INTO.equalsByCode(e.getRecordType()))) {
			// 取引実績詳細情報が存在しない || すべてが振替入金の場合 -> 「入金待ち」
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.AWAITING_PAYMENT.getCd());

		} else if (accgRecordDetailBeanList.stream().anyMatch(e -> e.getAccgRecordDetailOverPaymentSeq() != null && SystemFlg.FLG_OFF.equalsByCode(e.getOverPaymentRefundFlg()))) {
			// 過入金データが存在する -> 「過入金」
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.OVER_PAYMENT.getCd());

		} else if (LoiozNumberUtils.equalsDecimal(totalAmount, recordAmountExpect)) {
			// 実績が見込みと同額 -> 「入金済み」
			// ※過入金の場合はすでに処理済のため
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.DEPOSITED.getCd());

		} else if (LoiozNumberUtils.isLessThan(totalAmount, recordAmountExpect)) {
			// 実績が見込み以下の場合 -> 「一部入金」
			// ※ 入金していないケースは「入金待ち」として処理済みのため
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.PARTIAL_DEPOSIT.getCd());

		} else {
			// 想定外のデータパターン
			throw new RuntimeException("想定外のデータパターン");
		}

		try {
			// 更新処理をして終了
			tAccgInvoiceDao.update(tAccgInvoiceEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 精算書出金ステータスの更新を行う<br>
	 * 
	 * <pre>
	 * 精算発行ステータスが「下書き」の場合 -> 「下書き」
	 * 発行済 && 精算額が0円の場合 -> 「精算済み」
	 * 発行済 && 精算額 と 取引実績出金データの集計が一致しない場合 -> 「精算待ち」
	 * 発行済 && 精算額 と 取引実績出金データの集計が一致する場合 -> 「精算済み」
	 * </pre>
	 *
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void updateStatementStatus(Long accgDocSeq) throws AppException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (IssueStatus.DRAFT.equalsByCode(tAccgStatementEntity.getStatementIssueStatus())) {
			// 発行ステータスが下書きの場合 ->
			tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.DRAFT.getCd());

			try {
				// 更新処理をして終了
				tAccgStatementDao.update(tAccgStatementEntity);
				return;

			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
		}

		// ======================| 以下、発行済み | ===========================

		if (LoiozNumberUtils.equalsZero(tAccgStatementEntity.getStatementAmount())) {
			// 精算額が0円の場合 -> 精算完了
			tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.PAID.getCd());
		}

		// 取引実績情報を取得(未発行だとヌルポ発生)
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		List<AccgRecordDetailBean> accgRecordDetailBeanList = tAccgRecordDetailDao.selectAccgRecordDetailBeanByAccgRecordSeq(tAccgRecordEntity.getAccgRecordSeq());

		// 出金データの集計 = 精算額
		BigDecimal totalDepositPaymentAmount = accgRecordDetailBeanList.stream()
				.map(AccgRecordDetailBean::getRecordDepositPaymentAmount)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (LoiozNumberUtils.equalsDecimal(totalDepositPaymentAmount, tAccgRecordEntity.getDepositPaymentAmountExpect())) {
			// 出金予定額 と 取引実績の出金額合計 が一致する場合 -> 精算完了
			tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.PAID.getCd());

		} else {
			// 出金予定額 と 取引実績の出金額合計 が一致しない場合 -> 精算待ち
			tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.AWAITING_STATEMENT.getCd());

		}

		try {
			// 更新処理をして終了
			tAccgStatementDao.update(tAccgStatementEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 案件ID、名簿IDに紐づく請求書、精算書の発行ステータスが「下書き」の場合に実費明細書の再作成フラグを立てる<br>
	 * 
	 * <pre>
	 * 複数の会計書類をまたがって表示するケース(案件ID, 名簿IDをキーとしたPDFの作成)は
	 * 現状、「実費明細書」のみであるため、独自メソッドを作成。
	 * </pre>
	 * 
	 * @param ankenId
	 * @param personId
	 */
	public void updateDepositDetailRebuildFlg(Long ankenId, Long personId) throws AppException {

		// 案件ID, 名簿IDに紐づく会計書類情報をすべて取得
		List<TAccgDocEntity> tAccgDocEntityList = tAccgDocDao.selectAccgDocByParams(ankenId, personId);
		List<Long> accgDocSeqList = tAccgDocEntityList.stream().map(entity -> entity.getAccgDocSeq()).collect(Collectors.toList());

		// 複数の会計書類に対して、再作成フラグ更新を行う
		updateAccgDocReBuildFlg2On(accgDocSeqList, AccgDocFileType.DEPOSIT_DETAIL);
	}

	/**
	 * 会計書類の再作成フラグをONにする
	 * 
	 * <pre>
	 * 再作成フラグをONにするデータは以下の条件に合致するものとする
	 * ・会計書類の発行ステータスが「下書き」の場合
	 * ・会計書類が未送付のデータ
	 * </pre>
	 * 
	 * @param accgDocSeqList
	 * @param fileType
	 */
	public void updateAccgDocReBuildFlg2On(List<Long> accgDocSeqList, AccgDocFileType fileType) throws AppException {

		// 請求書と精算書データを取得する
		List<TAccgInvoiceEntity> tAccgInvoiceEntities = tAccgInvoiceDao.selectInvoiceByAccgDocSeqList(accgDocSeqList);
		List<TAccgStatementEntity> tAccgStatementEntities = tAccgStatementDao.selectStatementByAccgDocSeqList(accgDocSeqList);

		// ファイルの再作成が可能なデータは、発行ステータスが「下書き」のもの
		Set<Long> draftIssueStatusAccgDocSeqSet = new HashSet<>();
		tAccgInvoiceEntities.stream().filter(e -> IssueStatus.DRAFT.equalsByCode(e.getInvoiceIssueStatus())).map(TAccgInvoiceEntity::getAccgDocSeq).forEach(draftIssueStatusAccgDocSeqSet::add);
		tAccgStatementEntities.stream().filter(e -> IssueStatus.DRAFT.equalsByCode(e.getStatementIssueStatus())).map(TAccgStatementEntity::getAccgDocSeq).forEach(draftIssueStatusAccgDocSeqSet::add);

		if (draftIssueStatusAccgDocSeqSet.isEmpty()) {
			// 会計書類ファイルの再作成が可能な会計書類が存在しない場合 -> 何もせずに終了
			return;
		}

		// 対象の会計書類SEQをキーとして、データの取得を行う(送付済みのファイルは対象外とする)
		List<TAccgDocFileEntity> tAccgDocFileEntities = tAccgDocFileDao.selectAccgDocFileExcludeSendByAccgDocSeqList(new ArrayList<>(draftIssueStatusAccgDocSeqSet));
		List<TAccgDocFileEntity> tAccgDocFileUpdateEntities = tAccgDocFileEntities.stream()
				.filter(e -> fileType.equalsByCode(e.getAccgDocFileType())) // 対象のファイル種別に絞り込み
				.peek(e -> e.setRecreateStandbyFlg(SystemFlg.FLG_ON.getCd())) // 再作成フラグを立てる
				.collect(Collectors.toList());

		// 更新処理
		this.batchUpdateTAccgDocFile(tAccgDocFileUpdateEntities);
	}

	/**
	 * 売上明細SEQに紐づく売上に関するデータを削除します<br>
	 * 
	 * @param salesDetailSeq
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void deleteSalesRelated(Long salesDetailSeq, Long personId, Long ankenId) throws AppException {
		// 売上データ取得
		TSalesEntity tSalesEntity = tSalesDao.selectSalesEntityByPersonIdAndAnkenId(personId, ankenId);
		if (tSalesEntity == null) {
			return;
		}

		// 売上明細の削除
		this.deleteSalesDetail(salesDetailSeq);

		// 売上明細-消費税の削除
		this.deleteSalesDetailTax(salesDetailSeq);

		// 売上明細データ取得
		Long salesSeq = tSalesEntity.getSalesSeq();
		List<TSalesDetailEntity> tSalesDetailEntityList = tSalesDetailDao.selectSalesDetailBySalesSeq(salesSeq);
		if (LoiozCollectionUtils.isNotEmpty(tSalesDetailEntityList)) {
			// 売上明細データがある場合は、売上合計【見込み】と売上合計【実績】を更新する
			this.updateSalesAmount(personId, ankenId);
		} else {
			// 売上明細データが無い場合は売上データを削除する
			int deleteCount = 0;
			try {
				deleteCount = tSalesDao.delete(tSalesEntity);
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
	}

	/**
	 * 会計書類SEQに紐づく取引実績に関連するデータを削除します
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void deleteAccgRecordRelated(Long accgDocSeq) throws AppException {
		// 取引実績情報があれば削除
		TAccgRecordEntity tAccgRecordEntity = tAccgRecordDao.selectAccgRecordEntityByAccgDocSeq(accgDocSeq);
		if (tAccgRecordEntity == null) {
			return;
		}
		this.deleteTAccgRecord(tAccgRecordEntity);

		// 取引実績明細情報があれば削除
		Long accgRecordSeq = tAccgRecordEntity.getAccgRecordSeq();
		List<TAccgRecordDetailEntity> tAccgRecordDetailEntityList = tAccgRecordDetailDao.selectAccgRecordDetailEntityByAccgRecordSeq(accgRecordSeq);
		this.batchDeleteTAccgRecordDetail(tAccgRecordDetailEntityList);

		// 取引実績明細-過入金情報があれば削除
		List<Long> accgRecordDetailSeqList = tAccgRecordDetailEntityList.stream().map(entity -> entity.getAccgRecordDetailSeq()).collect(Collectors.toList());
		List<TAccgRecordDetailOverPaymentEntity> tAccgRecordDetailOverPaymentEntityList = tAccgRecordDetailOverPaymentDao.selectAccgRecordDetailOverPaymentByAccgRecordDetailSeqList(accgRecordDetailSeqList);
		this.batchDeleteTAccgRecordDetailOverPayment(tAccgRecordDetailOverPaymentEntityList);
	}

	/**
	 * 会計書類SEQに紐づく預り金データを削除します。
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void deleteDepositRecv(Long accgDocSeq) throws AppException {
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isNotEmpty(tDepositRecvEntityList)) {
			int[] deleteFeeCount = null;
			try {
				deleteFeeCount = tDepositRecvDao.batchDelete(tDepositRecvEntityList);
			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
			if (tDepositRecvEntityList.size() != deleteFeeCount.length) {
				// 削除処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00014, null);
			}
		}
	}

	/**
	 * 回収不能金-詳細データを削除し、回収不能金の合計額を再計算して更新します。
	 * 
	 * @param uncollectibleDetailSeq
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void deleteUncollectibleRelated(Long uncollectibleDetailSeq, Long personId, Long ankenId) throws AppException {
		if (uncollectibleDetailSeq == null) {
			return;
		}
		// 回収不能金-詳細データを削除
		this.deleteUncollectibleDetail(uncollectibleDetailSeq);

		// 回収不能金テーブルの更新 or 削除
		this.updateOrDeleteUncollectible(personId, ankenId);
	}

	/**
	 * 会計書類ファイルの削除処理<br>
	 * ※送信済みのPDFファイルは削除対象から除外する
	 * 
	 * <pre>
	 * S3オブジェクトキーをDBから削除するため、S3オブジェクトファイル削除を呼び出し元コントローラーで実施すること
	 * </pre>
	 *
	 * @param accgDocSeq
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 */
	public void deleteAccgDocFileExcludeSend(Long accgDocSeq, Set<String> deleteS3ObjectKeys) throws AppException {

		List<TAccgDocFileEntity> tAccgDocFileEntityList = tAccgDocFileDao.selectAccgDocFileExcludeSendByAccgDocSeq(accgDocSeq);
		List<TAccgDocFileDetailEntity> tAccgDocFileDetailEntityList = tAccgDocFileDetailDao.selectAccgDocFileDetailExcludeSendByAccgDocSeq(accgDocSeq);

		// 削除予定のS3オブジェクトキーを引数の削除対象に追加
		tAccgDocFileDetailEntityList.stream().map(TAccgDocFileDetailEntity::getS3ObjectKey).forEach(deleteS3ObjectKeys::add);

		try {
			if (!CollectionUtils.isEmpty(tAccgDocFileEntityList)) {
				// 会計書類ファイル情報が存在する場合 -> 削除処理
				tAccgDocFileDao.batchDelete(tAccgDocFileEntityList);
			}

			if (!CollectionUtils.isEmpty(tAccgDocFileDetailEntityList)) {
				// 会計書類ファイル詳細情報が存在する場合 -> 削除処理
				tAccgDocFileDetailDao.batchDelete(tAccgDocFileDetailEntityList);
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 売上テーブルの売上【見込み】を再計算し、更新する
	 * 
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void recalcSalesAmountExpectUpdate(Long personId, Long ankenId) throws AppException {
		// 売上データ取得
		TSalesEntity tSalesEntity = tSalesDao.selectSalesEntityByPersonIdAndAnkenId(personId, ankenId);
		if (tSalesEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 名簿ID、案件IDに紐づく売上明細データ取得
		List<TSalesDetailEntity> tSalesDetailEntityList = tSalesDetailDao.selectSalesDetailByPersonIdAndAnkenId(personId, ankenId);
		if (CollectionUtils.isEmpty(tSalesDetailEntityList)) {
			return;
		}

		// 売上金額【見込み】を算出し、設定
		BigDecimal salesAmountExpect = commonAccgAmountService.calcSalesAmountExpect(tSalesDetailEntityList);
		if (salesAmountExpect.precision() > CommonConstant.MAX_KINGAKU_KETA) {
			throw new AppException(MessageEnum.MSG_E00188, null, "案件", "売上");
		}

		tSalesEntity.setSalesAmountExpect(salesAmountExpect);

		int updateCount = 0;
		try {
			// 更新処理
			updateCount = tSalesDao.update(tSalesEntity);

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
	 * 売上テーブルの売上【実績】を再計算し、更新する
	 *
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void recalcSalesAmountResultUpdate(Long personId, Long ankenId) throws AppException {

		TSalesEntity tSalesEntity = tSalesDao.selectSalesEntityByPersonIdAndAnkenId(personId, ankenId);
		if (tSalesEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 売上金額【実績】を算出し、設定
		BigDecimal salesAmountResult = commonAccgAmountService.calcSalesAmountResult(personId, ankenId, tSalesEntity.getSalesSeq());
		if (salesAmountResult.precision() > CommonConstant.MAX_KINGAKU_KETA) {
			throw new AppException(MessageEnum.MSG_E00188, null, "案件", "売上");
		}
		tSalesEntity.setSalesAmountResult(salesAmountResult);

		try {
			// 更新処理
			tSalesDao.update(tSalesEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 請求書の請求額を変更します
	 * 
	 * @param accgInvoiceStatementAmountDto
	 * @param accgDocSeq
	 * @return 更新後のEntityを返却（金額が同じで、更新する必要がない（更新処理を行わない）場合はNULLを返却）
	 * @throws AppException
	 */
	public TAccgInvoiceEntity updateTAccgInvoiceAmount(AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto, Long accgDocSeq) throws AppException {
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		BigDecimal nowDbInvoiceAmount = tAccgInvoiceEntity.getInvoiceAmount();
		BigDecimal updateInvoiceAmount = accgInvoiceStatementAmountDto.getInvoiceAmount();

		if (LoiozNumberUtils.equalsDecimal(nowDbInvoiceAmount, updateInvoiceAmount)) {
			// 現在登録されている金額と、更新金額が同じ場合
			// -> 更新処理は行わない
			return null;
		}

		// 請求額の桁がオーバーしている場合はエラー
		if (updateInvoiceAmount.precision() > CommonConstant.MAX_KINGAKU_KETA) {
			throw new AppException(MessageEnum.MSG_E00188, null, "請求書", "請求");
		}

		// 請求額を設定
		tAccgInvoiceEntity.setInvoiceAmount(updateInvoiceAmount);

		int updateAccgInvoice = 0;
		try {
			updateAccgInvoice = tAccgInvoiceDao.update(tAccgInvoiceEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (updateAccgInvoice != 1) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		return tAccgInvoiceEntity;
	}

	/**
	 * 精算書の精算額を変更します
	 * 
	 * @param accgInvoiceStatementAmountDto
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void updateTAccgStatementAmount(AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto, Long accgDocSeq) throws AppException {
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		BigDecimal updateStatementAmount = accgInvoiceStatementAmountDto.getStatementAmount();

		// 精算額の桁がオーバーしている場合はエラー
		if (updateStatementAmount.precision() > CommonConstant.MAX_KINGAKU_KETA) {
			throw new AppException(MessageEnum.MSG_E00188, null, "精算書", "精算");
		}

		// 精算額を設定
		tAccgStatementEntity.setStatementAmount(accgInvoiceStatementAmountDto.getStatementAmount());

		int updateAccgStatement = 0;
		try {
			updateAccgStatement = tAccgStatementDao.update(tAccgStatementEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (updateAccgStatement != 1) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 請求書／精算書の請求額／精算額を、現在の最新状態で更新し、<br>
	 * PDFの再作成フラグを立てる。（請求書／精算書と、支払計画）
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void updateInvoiceStatementAmount(Long accgDocSeq) throws AppException {
		// 会計書類の金額情報取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		this.updateInvoiceStatementAmount(accgDocSeq, accgInvoiceStatementAmountDto);
	}

	/**
	 * 請求書／精算書の請求額／精算額を、受け取った請求書、精算書に関する金額情報で更新し、<br>
	 * PDFの再作成フラグを立てる。（請求書／精算書と、支払計画）
	 * 
	 * @param accgDocSeq
	 * @param accgInvoiceStatementAmountDto 請求書、精算書に関する金額情報
	 * @throws AppException
	 */
	public void updateInvoiceStatementAmount(Long accgDocSeq, AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) throws AppException {

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 請求書の場合

			// 請求書の再作成フラグを立てる
			this.updateAccgDocReBuildFlg2On(List.of(accgDocSeq), AccgDocFileType.INVOICE);

			// 請求額の更新
			TAccgInvoiceEntity tAccgInvoiceEntity = this.updateTAccgInvoiceAmount(accgInvoiceStatementAmountDto, accgDocSeq);

			if (tAccgInvoiceEntity != null) {
				// 請求額が変更された場合
				// 支払計画の再作成フラグを立てる
				this.updateAccgDocReBuildFlg2On(List.of(accgDocSeq), AccgDocFileType.INVOICE_PAYMENT_PLAN);
			}

		} else {
			// 精算書の場合

			// 精算書の再作成フラグを立てる
			this.updateAccgDocReBuildFlg2On(List.of(accgDocSeq), AccgDocFileType.STATEMENT);
			// 精算額の更新
			this.updateTAccgStatementAmount(accgInvoiceStatementAmountDto, accgDocSeq);
		}
	}

	/**
	 * 会計書類の各PDFを引数に応じて出力し、S3にアップロード、DBへの登録処理を行う
	 * 
	 * ※本メソッドはS3APIコールを含むため、トランザクションの最後に呼ぶこと
	 * 
	 * @param accgDocSeq 会計書類SEQ
	 * @param tenantSeq テナントSEQ
	 * @param validateReCreate 再作成を許容するかどうか
	 * @param deleteS3ObjectKeys
	 * 本メソッドのレコード削除処理により、アプリケーションの管理対象から外れたS3オブジェクトキーを格納するオブジェクト
	 * @param fileType 会計書類ファイル種別
	 * @throws AppException
	 */
	public void createAccgDocFileAndDetail(Long accgDocSeq, Long tenantSeq, boolean validateReCreate, Set<String> deleteS3ObjectKeys, AccgDocFileType fileType) throws AppException, DataNotFoundException {
		this.createAccgDocFileAndDetail(accgDocSeq, tenantSeq, validateReCreate, deleteS3ObjectKeys, fileType, false);
	}

	/**
	 * 会計書類の各PDFを引数に応じて出力し、S3にアップロード、DBへの登録処理を行う<br>
	 * ※ 会計書類を再作成が必要な状態で保存する必要がある特殊なケースでのみ、本メソッドを呼び出す
	 * 
	 * <pre>
	 * ※本メソッドはS3APIコールを含むため、トランザクションの最後に呼ぶこと
	 * </pre>
	 *
	 * @param accgDocSeq 会計書類SEQ
	 * @param tenantSeq テナントSEQ
	 * @param validateReCreate 再作成を許容するかどうか
	 * @param deleteS3ObjectKeys
	 * 本メソッドのレコード削除処理により、アプリケーションの管理対象から外れたS3オブジェクトキーを格納するオブジェクト
	 * @param fileType 会計書類ファイル種別
	 * @throws AppException
	 */
	public void createAccgDocFileAndDetailForRebuild(Long accgDocSeq, Long tenantSeq, boolean validateReCreate, Set<String> deleteS3ObjectKeys, AccgDocFileType fileType) throws AppException, DataNotFoundException {
		this.createAccgDocFileAndDetail(accgDocSeq, tenantSeq, validateReCreate, deleteS3ObjectKeys, fileType, true);
	}

	/**
	 * 会計管理（新会計）のテンプデータをすべて削除する。<br>
	 * （会計管理に関するマスタデータのテーブル（報酬項目マスタや請求書設定マスタなど）については削除処理は行わない。）
	 * 
	 * <pre>
	 * ※注意：プラン変更により、会計管理の機能が利用できなくなるときに、全ての会計データを削除するためのメソッド。
	 * 　現状では、プラン変更時のみでの利用を想定している。
	 * </pre>
	 */
	public void deleteAllAccgTempData() {

		// 報酬
		tFeeDao.deleteAllTFee();
		// タイムチャージ設定
		tFeeAddTimeChargeDao.deleteAllTFeeAddTimeCharge();

		// 預り金
		tDepositRecvDao.deleteAllTDepositRecv();

		// 売上
		tSalesDao.deleteAllTSales();
		// 売上明細
		tSalesDetailDao.deleteAllTSalesDetail();
		// 売上明細-消費税
		tSalesDetailTaxDao.deleteAllTSalesDetailTax();

		// 回収不能金
		tUncollectibleDao.deleteAllTUncollectible();
		// 回収不能詳細
		tUncollectibleDetailDao.deleteAllTUncollectibleDetail();

		// 会計書類-最終採番番号
		tAccgDocLastUsedNumberDao.deleteAllTAccgDocLastUsedNumber();

		// 会計書類
		tAccgDocDao.deleteAllTAccgDoc();
		// 請求書
		tAccgInvoiceDao.deleteAllTAccgInvoice();
		// 精算書
		tAccgStatementDao.deleteAllTAccgStatement();

		// 支払分割条件
		tAccgInvoicePaymentPlanConditionDao.deleteAllTAccgInvoicePaymentPlanCondition();
		// 支払計画
		tAccgInvoicePaymentPlanDao.deleteAllTAccgInvoicePaymentPlan();

		// 会計書類ファイル
		tAccgDocFileDao.deleteAllTAccgDocFile();
		// 会計書類ファイル詳細
		tAccgDocFileDetailDao.deleteAllTAccgDocFileDetail();

		// 会計書類-対応
		tAccgDocActDao.deleteAllTAccgDocAct();
		// 会計書類対応-送付
		tAccgDocActSendDao.deleteAllTAccgDocActSend();
		// 会計書類-対応-送付-ファイル
		tAccgDocActSendFileDao.deleteAllTAccgDocActSendFile();
		// 会計書類-ダウンロード情報
		tAccgDocDownloadDao.deleteAllTAccgDocDownload();

		// 既入金
		tAccgDocRepayDao.deleteAllTAccgDocRepay();
		// 既入金項目_預り金テーブルマッピング
		tAccgDocRepayTDepositRecvMappingDao.deleteAllTAccgDocRepayTDepositRecvMapping();

		// 請求項目
		tAccgDocInvoiceDao.deleteAllTAccgDocInvoice();
		// 請求報酬項目
		tAccgDocInvoiceFeeDao.deleteAllTAccgDocInvoiceFee();
		// 請求預り金項目
		tAccgDocInvoiceDepositDao.deleteAllTAccgDocInvoiceDeposit();
		// 請求その他項目
		tAccgDocInvoiceOtherDao.deleteAllTAccgDocInvoiceOther();

		// 請求項目-預り金（実費）_預り金テーブルマッピング
		tAccgDocInvoiceDepositTDepositRecvMappingDao.deleteAllTAccgDocInvoiceDepositTDepositRecvMapping();

		// 請求項目-消費税
		tAccgInvoiceTaxDao.deleteAllTAccgInvoiceTax();
		// 請求項目-源泉徴収
		tAccgInvoiceWithholdingDao.deleteAllTAccgInvoiceWithholding();

		// 取引実績
		tAccgRecordDao.deleteAllTAccgRecord();
		// 取引実績明細
		tAccgRecordDetailDao.deleteAllTAccgRecordDetail();
		// 取引実績明細-過入金
		tAccgRecordDetailOverPaymentDao.deleteAllTAccgRecordDetailOverPayment();

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	// =========================================================================
	// ▼ 取得／データ変換系
	// =========================================================================

	/**
	 * 改行コードを挿入する<br>
	 * ※詳細ブロックを生成時に使用する
	 * 
	 * @param sb
	 * @param addText
	 * @return
	 */
	private String lineFeedCodeInsert(StringBuilder sb, String addText) {
		String val = null;
		boolean flg = false;
		if (StringUtils.isNotEmpty(sb.toString())) {
			flg = true;
		}
		if (StringUtils.isNotEmpty(addText)) {
			if (flg) {
				val = CommonConstant.LINE_FEED_CODE + addText;
			} else {
				val = addText;
			}
		}
		return val;
	}

	/**
	 * 取引状況：請求書データの取得
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private AccgDocSummaryForm.InvoiceSummary getAccgDocInvoiceSummary(Long accgDocSeq) {

		var invoiceSummary = new AccgDocSummaryForm.InvoiceSummary();

		// 請求データの取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// 請求書情報の取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);

		// 既入金情報の取得
		List<RepayBean> repayBeanList = tAccgDocRepayDao.selectRepayBeanListByAccgDocSeq(accgDocSeq);
		List<BigDecimal> repayAmountList = repayBeanList.stream().map(RepayBean::getRepayAmount).collect(Collectors.toList());
		BigDecimal totalRepayAmount = AccountingUtils.calcTotal(repayAmountList);

		// データの設定
		invoiceSummary.setInvoiceSeq(tAccgInvoiceEntity.getInvoiceSeq());
		invoiceSummary.setInvoicePaymentStatus(InvoicePaymentStatus.of(tAccgInvoiceEntity.getInvoicePaymentStatus()));
		invoiceSummary.setFeeAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalFeeAmountAfterWithholding())));
		invoiceSummary.setTotalDepositAllAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalDepositAllAmount())));
		invoiceSummary.setAdvanceMoneyAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalAdvanceMoneyAmount())));
		invoiceSummary.setDepositAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalDepositAmount())));
		invoiceSummary.setInvoiceAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getInvoiceAmount())));
		invoiceSummary.setInvoiceTypeName(InvoiceType.of(tAccgInvoiceEntity.getInvoiceType()).getVal());
		invoiceSummary.setInvoiceNo(tAccgInvoiceEntity.getInvoiceNo());
		invoiceSummary.setInvoiceDate(tAccgInvoiceEntity.getInvoiceDate());
		invoiceSummary.setDueDate(tAccgInvoiceEntity.getDueDate());
		invoiceSummary.setRepayAmount(AccountingUtils.toDispAmountLabel(totalRepayAmount));

		return invoiceSummary;
	}

	/**
	 * 取引状況：精算書データの取得
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private AccgDocSummaryForm.StatementSummary getAccgDocStatementSummary(Long accgDocSeq) {

		var statementSummary = new AccgDocSummaryForm.StatementSummary();

		// 精算データの取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// 精算書情報の取得
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);

		// 既入金情報の取得
		List<RepayBean> repayBeanList = tAccgDocRepayDao.selectRepayBeanListByAccgDocSeq(accgDocSeq);
		List<BigDecimal> repayAmountList = repayBeanList.stream().map(RepayBean::getRepayAmount).collect(Collectors.toList());
		BigDecimal totalRepayAmount = AccountingUtils.calcTotal(repayAmountList);

		// データの設定
		statementSummary.setStatementSeq(tAccgStatementEntity.getStatementSeq());
		statementSummary.setStatementRefundStatus(StatementRefundStatus.of(tAccgStatementEntity.getStatementRefundStatus()));
		statementSummary.setFeeAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalFeeAmountAfterWithholding())));
		statementSummary.setAdvanceMoneyAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalAdvanceMoneyAmount())));
		statementSummary.setDepositAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalDepositAmount())));
		statementSummary.setStatementAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getStatementAmount())));
		statementSummary.setStatementNo(tAccgStatementEntity.getStatementNo());
		statementSummary.setStatementDate(tAccgStatementEntity.getStatementDate());
		statementSummary.setRefundDate(tAccgStatementEntity.getRefundDate());
		statementSummary.setRepayAmount(AccountingUtils.toDispAmountLabel(totalRepayAmount));

		// 0円精算かどうかを設定
		statementSummary.setStatementAmountZero(LoiozNumberUtils.equalsZero(tAccgStatementEntity.getStatementAmount()));

		return statementSummary;
	}

	/**
	 * 会計書類ファイルのS3DirPathを作成
	 * 
	 * @param tenantSeq
	 * @return
	 */
	private Directory createAccgS3DirectoryDomain(Long tenantSeq) {
		String dirPath = fileStorageService.getRepDirPath(S3AccountingDir.TE_DO_ACCOUNTING.getPath(), tenantSeq, uriService.getSubDomainName());
		return new Directory(dirPath);
	}

	/**
	 * テナント印鑑画像の取得
	 * 
	 * @param tenantSeq
	 * @return
	 */
	private FileContentsDto getTenantStamp(Long tenantSeq) {
		// 事務所案件の場合、事務所印鑑を表示
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(tenantSeq);
		if (mTenantEntity.getTenantStampImg() == null || StringUtils.isEmpty(mTenantEntity.getTenantStampImgExtension())) {
			// 印鑑画像が登録されていない -> nullを返却
			return null;
		}
		return new FileContentsDto(FileUtils.toByteArray(mTenantEntity.getTenantStampImg()), mTenantEntity.getTenantStampImgExtension());
	}

	/**
	 * アカウントの印鑑画像を取得
	 *
	 * @param accountSeq
	 * @return
	 */
	private FileContentsDto getSalesOwnerStamp(Long accountSeq) {

		if (accountSeq == null) {
			// 対象のアカウントSEQがnullの場合は印鑑画像を取得できないのでnullを返却
			return null;
		}

		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(accountSeq);
		if (mAccountEntity.getAccountLawyerStampImgSeq() == null) {
			// 弁護士画像が未登録 -> nullを返却
			return null;
		}
		MAccountImgEntity mAccountImgEntity = mAccountImgDao.selectBySeq(mAccountEntity.getAccountLawyerStampImgSeq());
		if (mAccountImgEntity.getImgContents() == null || StringUtils.isEmpty(mAccountImgEntity.getImgExtension())) {
			// 印鑑画像が登録されていない -> nullを返却
			return null;
		}
		return new FileContentsDto(FileUtils.toByteArray(mAccountImgEntity.getImgContents()), mAccountImgEntity.getImgExtension());
	}

	/**
	 * AccgInvoiceStateBean -> AccgInvoiceStateListItemDto
	 * 
	 * @param bean
	 * @return
	 */
	private AccgInvoiceStatementListItemDto toAccgInvoiceStateListItemDto(AccgInvoiceStatementBean bean) {

		var accgIncoiveStateListItemDto = new AccgInvoiceStatementListItemDto();

		AccgDocType accgDocType = AccgDocType.of(bean.getAccgDocType());
		if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合
			accgIncoiveStateListItemDto.setAccgDocType(accgDocType);
			accgIncoiveStateListItemDto.setInvoiceStatementSeq(bean.getInvoiceSeq());
			accgIncoiveStateListItemDto.setInvoiceStatementNo(bean.getInvoiceNo());
			accgIncoiveStateListItemDto.setIssueStatus(IssueStatus.of(bean.getInvoiceIssueStatus()));
			accgIncoiveStateListItemDto.setInvoicePaymentStatus(InvoicePaymentStatus.of(bean.getInvoicePaymentStatus()));
			accgIncoiveStateListItemDto.setInvoceStatementDate(bean.getInvoiceDate());
			accgIncoiveStateListItemDto.setInvoceStatementDueDate(bean.getDueDate());
			accgIncoiveStateListItemDto.setAccgAmount(AccountingUtils.toDispAmountLabel(bean.getInvoiceAmount()));
			accgIncoiveStateListItemDto.setInvoiceType(InvoiceType.of(bean.getInvoiceType()));
			accgIncoiveStateListItemDto.setInvoiceStatementMemo(bean.getInvoiceMemo());

		} else if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合
			accgIncoiveStateListItemDto.setAccgDocType(accgDocType);
			accgIncoiveStateListItemDto.setInvoiceStatementSeq(bean.getStatementSeq());
			accgIncoiveStateListItemDto.setInvoiceStatementNo(bean.getStatementNo());
			accgIncoiveStateListItemDto.setIssueStatus(IssueStatus.of(bean.getStatementIssueStatus()));
			accgIncoiveStateListItemDto.setStatementRefundStatus(StatementRefundStatus.of(bean.getStatementRefundStatus()));
			accgIncoiveStateListItemDto.setInvoceStatementDate(bean.getStatementDate());
			accgIncoiveStateListItemDto.setInvoceStatementDueDate(bean.getRefundDate());
			accgIncoiveStateListItemDto.setAccgAmount(AccountingUtils.toDispAmountLabel(bean.getStatementAmount()));
			accgIncoiveStateListItemDto.setInvoiceStatementMemo(bean.getStatementMemo());

		} else {
			// 想定外の入力値
			throw new RuntimeException("想定外のEnum値");
		}
		// 取引実績
		accgIncoiveStateListItemDto.setAccgRecordSeq(bean.getAccgRecordSeq());

		return accgIncoiveStateListItemDto;
	}

	/**
	 * 会計書類が発行済かどうかを保持するMapを作成する
	 * 
	 * @param accgDocSeqList
	 * @return
	 */
	private Map<Long, Boolean> generateAccgDocSeqToIssuedCheckResultMap(List<Long> accgDocSeqList) {
		if (CollectionUtils.isEmpty(accgDocSeqList)) {
			return Collections.emptyMap();
		}

		// usingAccgDocに紐づく請求書/精算書を取得
		List<TAccgInvoiceEntity> tAccgInvoiceEntities = tAccgInvoiceDao.selectInvoiceByAccgDocSeqList(accgDocSeqList);
		List<TAccgStatementEntity> tAccgStatementEntities = tAccgStatementDao.selectStatementByAccgDocSeqList(accgDocSeqList);

		// usingAccgDocが発行済かどうかをMapに格納
		Map<Long, Boolean> usingAccgDocSeqToIssuedCheckResult = new HashMap<>();
		this.put2IssuedCheckResultMap(usingAccgDocSeqToIssuedCheckResult, tAccgInvoiceEntities, TAccgInvoiceEntity::getAccgDocSeq, TAccgInvoiceEntity::getInvoiceIssueStatus);
		this.put2IssuedCheckResultMap(usingAccgDocSeqToIssuedCheckResult, tAccgStatementEntities, TAccgStatementEntity::getAccgDocSeq, TAccgStatementEntity::getStatementIssueStatus);

		return usingAccgDocSeqToIssuedCheckResult;
	}

	/**
	 * 会計書類SEQに紐づく請求書/精算書の発行済かどうか判定を行い、Mapに格納する
	 * 
	 * @param <T> TAccgInvoiceEntity or TAccgStatementEntity
	 * @param accgDocSeqToIssuedCheckResult Map<会計書類SEQ(long), 発行済かどうか(bool)>
	 * @param entities TAccgInvoiceEntities or TAccgStatementEntities
	 * @param accgSeqMapper T::getAccgDocSeq
	 * @param issueStatusMapper T::getIssueStatus
	 */
	private <T> void put2IssuedCheckResultMap(Map<Long, Boolean> accgDocSeqToIssuedCheckResult, List<T> entities, Function<T, Long> accgSeqMapper, Function<T, String> issueStatusMapper) {
		if (CollectionUtils.isEmpty(entities)) {
			return;
		}

		entities.stream().forEach(e -> {
			accgDocSeqToIssuedCheckResult.put(
					accgSeqMapper.apply(e),
					IssueStatus.isIssued(issueStatusMapper.apply(e)));
		});
	}

	/**
	 * 実費明細PDF作成時の状況ステータスを取得する
	 * 
	 * @param targetAccgDocSeq 今回の会計書類SEQ
	 * @param tDepositRecvEntity 対象の預り金レコード(今回の会計書類SEQと案件ID,名簿IDは一致しているデータ)
	 * @param usingAccgDocSeqToIssuedCheckResult 使用会計書類の発行済みかどうか
	 * 
	 * @return
	 */
	private DepositUseStatus depositUseStatusMapper(Long targetAccgDocSeq, TDepositRecvEntity tDepositRecvEntity, Map<Long, Boolean> usingAccgDocSeqToIssuedCheckResult) {

		if (!SystemFlg.codeToBoolean(tDepositRecvEntity.getDepositCompleteFlg())) {
			// 実費明細PDFに表示するデータに予定データは表示しないため
			// 当メソッドでは例外とする
			throw new RuntimeException("引数の預り金エンティティは想定外のレコードです");
		}

		Long createdAccgDocSeq = tDepositRecvEntity.getCreatedAccgDocSeq();
		if (createdAccgDocSeq != null) {

			if (DepositRecvCreatedType.TRANFER_TO_FEE_UPON_ISSUANCE.equalsByCode(tDepositRecvEntity.getCreatedType())) {
				// 振替入金データの場合 -> 入金実績となるため「済」
				return DepositUseStatus.USED;

			} else if (DepositRecvCreatedType.CREATED_BY_ISSUANCE.equalsByCode(tDepositRecvEntity.getCreatedType())
					&& ExpenseInvoiceFlg.EXPENSE.equalsByCode(tDepositRecvEntity.getExpenseInvoiceFlg())) {
				// 取引実績の実費入金予定預かりレコードの場合 -> 実績のため「済」
				return DepositUseStatus.USED;

			} else if (DepositRecvCreatedType.CREATED_BY_ISSUANCE.equalsByCode(tDepositRecvEntity.getCreatedType())
					&& DepositType.SHUKKIN.equalsByCode(tDepositRecvEntity.getDepositType())) {
				// 取引実績による出金レコードの場合 -> 「済」
				return DepositUseStatus.USED;
			} else {
				// 作成元会計書類SEQに紐づく上記以外のケースは、紐づいていないデータパターンと同じ条件分岐判定とする。
				// そのため、この分岐ケースでは何もしない
			}
		}

		Long usingAccgDocSeq = tDepositRecvEntity.getUsingAccgDocSeq();
		if (usingAccgDocSeq == null) {
			// 使用していない預り金の場合 -> UN_USED
			// 使用しているが未発行の請求書/精算書の場合もUN_USEDにするが、
			// 今回の会計書類も未発行のケースが考えられるので、この条件分岐では判定しない
			return DepositUseStatus.UN_USED;
		}

		if (Objects.equals(targetAccgDocSeq, usingAccgDocSeq)) {
			// 今回の会計書類SEQとエンティティデータの使用会計書類SEQが一致した場合 -> 「今回対象」
			return DepositUseStatus.USING;
		}

		// 以下、今回の会計書類SEQとエンティティデータの使用会計書類SEQが一致しない場合
		if (usingAccgDocSeqToIssuedCheckResult.get(usingAccgDocSeq)) {
			// 使用している会計書類が発行済みとなっているケース
			return DepositUseStatus.USED;
		}

		// 使用しているが未発行の請求書/精算書のケース && 今回の会計書類ではない
		return DepositUseStatus.UN_USED;
	}

	/**
	 * P0001(請求書)のPDFデータを作成
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private FileContentsDto createP0001FileContentsDto(Long accgDocSeq, Long tenantSeq) {

		// PDF出力
		P0001PdfBuilder P0001PdfBuilder = new P0001PdfBuilder();
		P0001PdfBuilder.setConfig(pdfConfig);
		P0001PdfBuilder.setPdfService(pdfService);

		// PDFデータの作成
		P0001Data P0001Data = this.createP0001Data(accgDocSeq, tenantSeq);
		P0001PdfBuilder.setPdfData(P0001Data);

		try {
			// PDFの生成
			return P0001PdfBuilder.makePdfContentsDto();
		} catch (Exception e) {
			// 出力エラー
			throw new RuntimeException(e);
		}
	}

	/**
	 * 請求書出力用データを作成する
	 * 
	 * @return
	 */
	private P0001Data createP0001Data(Long accgDocSeq, Long tenantSeq) throws DataNotFoundException {

		P0001Data p0001Data = new P0001Data();

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null || tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません");
		}

		p0001Data.setTitle(tAccgInvoiceEntity.getInvoiceTitle());
		p0001Data.setOutputDate(DateUtils.parseToString(tAccgInvoiceEntity.getInvoiceDate(), DateUtils.DATE_JP_YYYY_MM_DD));
		p0001Data.setSeikyuNo(tAccgInvoiceEntity.getInvoiceNo());
		p0001Data.setAtesakiName(StringUtils.isEmpty(tAccgInvoiceEntity.getInvoiceToName()) ? "" : tAccgInvoiceEntity.getInvoiceToName() + CommonConstant.FULL_SPACE
				+ DefaultEnum.getVal(NameEnd.of(tAccgInvoiceEntity.getInvoiceToNameEnd())));
		p0001Data.setAtesakiDetail(PdfUtils.newLineToBreak(tAccgInvoiceEntity.getInvoiceToDetail()));
		p0001Data.setJimushoName(tAccgInvoiceEntity.getInvoiceFromTenantName());
		if (SystemFlg.codeToBoolean(tAccgInvoiceEntity.getTenantStampPrintFlg())) {
			TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(tAccgDocEntity.getAnkenId());
			FileContentsDto fileContentsDto;
			if (AnkenType.JIMUSHO.equalsByCode(tAnkenEntity.getAnkenType())) {
				// 事務所案件の場合、事務所印鑑を表示
				fileContentsDto = this.getTenantStamp(tenantSeq);

			} else if (AnkenType.KOJIN.equalsByCode(tAnkenEntity.getAnkenType())) {
				// 個人案件の場合、弁護士印鑑を表示
				fileContentsDto = this.getSalesOwnerStamp(tAccgInvoiceEntity.getSalesAccountSeq());

			} else {
				// 想定外のパラメータ
				throw new RuntimeException("Enum値が不正です");
			}

			if (fileContentsDto != null) {
				// ファイルが存在する場合のみ設定
				p0001Data.setStamp(fileContentsDto.getByteArray());
			}
		}
		p0001Data.setSofumotoDetail(tAccgInvoiceEntity.getInvoiceFromDetail());
		p0001Data.setSubTitle(tAccgInvoiceEntity.getInvoiceSubText());
		p0001Data.setAnkenName(tAccgInvoiceEntity.getInvoiceSubject());
		p0001Data.setSeikyuKingaku(PdfUtils.toDispTotalAmount(tAccgInvoiceEntity.getInvoiceAmount()));
		if (SystemFlg.codeToBoolean(tAccgInvoiceEntity.getDueDatePrintFlg())) {
			p0001Data.setShiharaiLimitDate(DateUtils.parseToString(tAccgInvoiceEntity.getDueDate(), DateUtils.DATE_JP_YYYY_MM_DD));
		}
		p0001Data.setPaymentDestination(PdfUtils.newLineToBreak(tAccgInvoiceEntity.getTenantBankDetail()));

		// お預り金(過入金情報)を取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntities = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);

		// 預り金一覧情報の設定
		boolean needRepayTransactionDatePrint = SystemFlg.codeToBoolean(tAccgInvoiceEntity.getRepayTransactionDatePrintFlg());
		List<P0001Data.AzukariKingakuListItem> azukariKingakuList = tAccgDocRepayEntities.stream().map(e -> {
			var item = new P0001Data.AzukariKingakuListItem();
			var azukariDate = "";
			if (needRepayTransactionDatePrint) {
				azukariDate = DateUtils.parseToString(e.getRepayTransactionDate(), DateUtils.DATE_JP_YYYY_MM_DD);
			}
			item.setAzukariDate(azukariDate);
			item.setAzukariKingakuName(e.getRepayItemName());
			item.setAzukariKingakuDetail(PdfUtils.newLineToBreak(e.getSumText()));
			item.setKingaku(PdfUtils.toDispAmount(e.getRepayAmount()));
			return item;
		}).collect(Collectors.toList());
		p0001Data.setAzukariKingakuList(azukariKingakuList);
		p0001Data.setHideAzukariDate(!SystemFlg.codeToBoolean(tAccgInvoiceEntity.getRepayTransactionDatePrintFlg()));

		// 預り金合計金額の設定
		List<BigDecimal> azukariAmountList = tAccgDocRepayEntities.stream().map(TAccgDocRepayEntity::getRepayAmount).collect(Collectors.toList());
		BigDecimal totalAzukariAmount = AccountingUtils.calcTotal(azukariAmountList);
		p0001Data.setAzukariKingakuTotal(PdfUtils.toDispTotalAmount(totalAzukariAmount));

		// 請求情報を取得する
		List<AccgDocInvoiceBean> accgDocInvoiceBeans = tAccgDocInvoiceDao.selectAccgDocInvoiceBeanByAccgDocSeq(accgDocSeq);
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		if (accgDocInvoiceBeans == null) {
			accgDocInvoiceBeans = Collections.emptyList();
		}

		final String hoshuJippiListDateFormat = DateUtils.DATE_JP_YYYY_MM_DD;
		boolean needInvoiceTransactionDatePrint = SystemFlg.codeToBoolean(tAccgInvoiceEntity.getInvoiceTransactionDatePrintFlg());
		List<P0001Data.HoshuJippiKingakuListItem> hoshuJippiKingakuList = accgDocInvoiceBeans.stream().map(e -> {
			var item = new P0001Data.HoshuJippiKingakuListItem();
			var hoshuJippiDate = "";
			if (e.getDocInvoiceFeeSeq() != null) {
				// 報酬項目
				if (needInvoiceTransactionDatePrint) {
					hoshuJippiDate = DateUtils.parseToString(e.getFeeTransactionDate(), hoshuJippiListDateFormat);
				}
				item.setHoshuJippiDate(hoshuJippiDate);
				item.setHoshuJippiKingakuName(e.getFeeItemName());
				item.setHoshuJippiKingakuDetail(PdfUtils.newLineToBreak(e.getFeeSumText()));
				item.setKingaku(PdfUtils.toDispAmount(e.getFeeAmount()));
			} else if (e.getDocInvoiceDepositSeq() != null) {
				// 実費項目
				if (needInvoiceTransactionDatePrint) {
					hoshuJippiDate = DateUtils.parseToString(e.getDepositTransactionDate(), hoshuJippiListDateFormat);
				}
				item.setHoshuJippiDate(hoshuJippiDate);
				item.setHoshuJippiKingakuName(e.getDepositItemName());
				item.setHoshuJippiKingakuDetail(PdfUtils.newLineToBreak(e.getDepositSumText()));
				item.setKingaku(PdfUtils.toDispAmount(e.getDepositAmount()));
			} else if (e.getDocInvoiceOtherSeq() != null) {
				// その他の項目
				if (needInvoiceTransactionDatePrint) {
					hoshuJippiDate = DateUtils.parseToString(e.getOtherTransactionDate(), hoshuJippiListDateFormat);
				}
				item.setHoshuJippiDate(hoshuJippiDate);
				item.setHoshuJippiKingakuName(e.getOtherItemName());
				item.setHoshuJippiKingakuDetail(PdfUtils.newLineToBreak(e.getOtherSumText()));
				if (CommonConstant.InvoiceOtherItemType.DISCOUNT.equalsByCode(e.getOtherItemType())) {
					// 値引き金額はマイナス表示
					item.setKingaku(PdfUtils.toDispAmount(e.getOtherAmount().negate()));
				} else {
					// 値引き以外の場合、金額は表示しない
					item.setKingaku(CommonConstant.BLANK);
				}
			} else {
				// 想定外のデータ
				throw new RuntimeException("請求書PDF作成時の報酬/実費明細データが想定外のデータパターンです。");
			}
			return item;
		}).collect(Collectors.toList());

		PdfUtils.appendEmptyListItem(hoshuJippiKingakuList, P0001Data.HoshuJippiKingakuListItem::new, 1);
		p0001Data.setHoshuJippiKingakuList(hoshuJippiKingakuList);
		p0001Data.setHideHoshuJippiDate(!SystemFlg.codeToBoolean(tAccgInvoiceEntity.getInvoiceTransactionDatePrintFlg()));

		// 合計金額情報を設定
		p0001Data.setHoshuJippiSubTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getSubTotal())));
		p0001Data.setHoshuJippiTaxTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount())));
		p0001Data.setHoshuJippiGensenTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalWithholdingAmount())));
		p0001Data.setHoshuJippiTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalAmount())));
		p0001Data.setTenPercentTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFee())));
		p0001Data.setTenPercentTaxTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10())));
		p0001Data.setHasTenPercentTax(!LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFee()) || !LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10()));
		p0001Data.setEightPercentTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFee())));
		p0001Data.setEightPercentTaxTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8())));
		p0001Data.setHasEightPercentTax(!LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFee()) || !LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8()));

		// 精算合計(すでに設定した請求額と同額)
		p0001Data.setSeisanTotal(PdfUtils.toDispTotalAmount(tAccgInvoiceEntity.getInvoiceAmount()));

		// 備考の設定
		p0001Data.setRemarks(PdfUtils.newLineToBreak(tAccgInvoiceEntity.getInvoiceRemarks()));

		return p0001Data;
	}

	/**
	 * P0002(精算書)のPDFデータを作成
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private FileContentsDto createP0002FileContentsDto(Long accgDocSeq, Long tenantSeq) {

		// PDF出力
		P0002PdfBuilder P0002PdfBuilder = new P0002PdfBuilder();
		P0002PdfBuilder.setConfig(pdfConfig);
		P0002PdfBuilder.setPdfService(pdfService);

		// PDFデータの作成
		P0002Data P0002Data = this.createP0002Data(accgDocSeq, tenantSeq);
		P0002PdfBuilder.setPdfData(P0002Data);

		try {
			// PDFの生成
			return P0002PdfBuilder.makePdfContentsDto();
		} catch (Exception e) {
			// 出力エラー
			throw new RuntimeException(e);
		}
	}

	/**
	 * TODO 表示項目の確認 請求書出力用データを作成する
	 * 
	 * @return
	 */
	private P0002Data createP0002Data(Long accgDocSeq, Long tenantSeq) throws DataNotFoundException {

		P0002Data p0002Data = new P0002Data();

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null || tAccgStatementEntity == null) {
			throw new DataNotFoundException("精算書情報が存在しません");
		}

		p0002Data.setTitle(tAccgStatementEntity.getStatementTitle());
		p0002Data.setOutputDate(DateUtils.parseToString(tAccgStatementEntity.getStatementDate(), DateUtils.DATE_JP_YYYY_MM_DD));
		p0002Data.setSeisanNo((tAccgStatementEntity.getStatementNo()));
		p0002Data.setAtesakiName(StringUtils.isEmpty(tAccgStatementEntity.getStatementToName()) ? "" : tAccgStatementEntity.getStatementToName() + CommonConstant.FULL_SPACE
				+ DefaultEnum.getVal(NameEnd.of(tAccgStatementEntity.getStatementToNameEnd())));
		p0002Data.setAtesakiDetail(PdfUtils.newLineToBreak(tAccgStatementEntity.getStatementToDetail()));
		p0002Data.setJimushoName(tAccgStatementEntity.getStatementFromTenantName());
		if (SystemFlg.codeToBoolean(tAccgStatementEntity.getTenantStampPrintFlg())) {
			TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(tAccgDocEntity.getAnkenId());
			FileContentsDto fileContentsDto;
			if (AnkenType.JIMUSHO.equalsByCode(tAnkenEntity.getAnkenType())) {
				// 事務所案件の場合、事務所印鑑を表示
				fileContentsDto = this.getTenantStamp(tenantSeq);

			} else if (AnkenType.KOJIN.equalsByCode(tAnkenEntity.getAnkenType())) {
				// 個人案件の場合、弁護士印鑑を表示
				fileContentsDto = this.getSalesOwnerStamp(tAccgStatementEntity.getSalesAccountSeq());

			} else {
				// 想定外のパラメータ
				throw new RuntimeException("Enum値が不正です");
			}

			if (fileContentsDto != null) {
				// ファイルが存在する場合のみ設定
				p0002Data.setStamp(fileContentsDto.getByteArray());
			}
		}
		p0002Data.setSofumotoDetail(tAccgStatementEntity.getStatementFromDetail());
		p0002Data.setSubTitle(tAccgStatementEntity.getStatementSubText());
		p0002Data.setAnkenName(tAccgStatementEntity.getStatementSubject());
		p0002Data.setSeisanKingaku(PdfUtils.toDispTotalAmount(tAccgStatementEntity.getStatementAmount()));
		if (SystemFlg.codeToBoolean(tAccgStatementEntity.getRefundDatePrintFlg())) {
			p0002Data.setShiharaiLimitDate(DateUtils.parseToString(tAccgStatementEntity.getRefundDate(), DateUtils.DATE_JP_YYYY_MM_DD));
		}
		p0002Data.setPaymentDestination(PdfUtils.newLineToBreak(tAccgStatementEntity.getRefundBankDetail()));

		// お預り金(過入金情報)を取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntities = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);

		// 預り金一覧情報の設定
		boolean needRepayTransactionDatePrint = SystemFlg.codeToBoolean(tAccgStatementEntity.getRepayTransactionDatePrintFlg());
		List<P0002Data.AzukariKingakuListItem> azukariKingakuList = tAccgDocRepayEntities.stream().map(e -> {
			var item = new P0002Data.AzukariKingakuListItem();
			var azukariDate = "";
			if (needRepayTransactionDatePrint) {
				azukariDate = DateUtils.parseToString(e.getRepayTransactionDate(), DateUtils.DATE_JP_YYYY_MM_DD);
			}
			item.setAzukariDate(azukariDate);
			item.setAzukariKingakuName(e.getRepayItemName());
			item.setAzukariKingakuDetail(PdfUtils.newLineToBreak(e.getSumText()));
			item.setKingaku(PdfUtils.toDispAmount(e.getRepayAmount()));
			return item;
		}).collect(Collectors.toList());
		p0002Data.setAzukariKingakuList(azukariKingakuList);
		p0002Data.setHideAzukariDate(!SystemFlg.codeToBoolean(tAccgStatementEntity.getRepayTransactionDatePrintFlg()));

		// 預り金合計金額の設定
		List<BigDecimal> azukariAmountList = tAccgDocRepayEntities.stream().map(TAccgDocRepayEntity::getRepayAmount).collect(Collectors.toList());
		BigDecimal totalAzukariAmount = AccountingUtils.calcTotal(azukariAmountList);
		p0002Data.setAzukariKingakuTotal(PdfUtils.toDispTotalAmount(totalAzukariAmount));

		// 請求情報を取得する
		List<AccgDocInvoiceBean> accgDocInvoiceBeans = tAccgDocInvoiceDao.selectAccgDocInvoiceBeanByAccgDocSeq(accgDocSeq);
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		if (accgDocInvoiceBeans == null) {
			accgDocInvoiceBeans = Collections.emptyList();
		}

		final String hoshuJippiListDateFormat = DateUtils.DATE_JP_YYYY_MM_DD;
		boolean needInvoiceTransactionDatePrint = SystemFlg.codeToBoolean(tAccgStatementEntity.getInvoiceTransactionDatePrintFlg());
		List<P0002Data.HoshuJippiKingakuListItem> hoshuJippiKingakuList = accgDocInvoiceBeans.stream().map(e -> {
			var item = new P0002Data.HoshuJippiKingakuListItem();
			var hoshuJippiDate = "";
			if (e.getDocInvoiceFeeSeq() != null) {
				// 報酬項目
				if (needInvoiceTransactionDatePrint) {
					hoshuJippiDate = DateUtils.parseToString(e.getFeeTransactionDate(), hoshuJippiListDateFormat);
				}
				item.setHoshuJippiDate(hoshuJippiDate);
				item.setHoshuJippiKingakuName(e.getFeeItemName());
				item.setHoshuJippiKingakuDetail(PdfUtils.newLineToBreak(e.getFeeSumText()));
				item.setKingaku(PdfUtils.toDispAmount(e.getFeeAmount()));
			} else if (e.getDocInvoiceDepositSeq() != null) {
				// 実費項目
				if (needInvoiceTransactionDatePrint) {
					hoshuJippiDate = DateUtils.parseToString(e.getDepositTransactionDate(), hoshuJippiListDateFormat);
				}
				item.setHoshuJippiDate(hoshuJippiDate);
				item.setHoshuJippiKingakuName(e.getDepositItemName());
				item.setHoshuJippiKingakuDetail(PdfUtils.newLineToBreak(e.getDepositSumText()));
				item.setKingaku(PdfUtils.toDispAmount(e.getDepositAmount()));
			} else if (e.getDocInvoiceOtherSeq() != null) {
				// その他の項目
				if (needInvoiceTransactionDatePrint) {
					hoshuJippiDate = DateUtils.parseToString(e.getOtherTransactionDate(), hoshuJippiListDateFormat);
				}
				item.setHoshuJippiDate(hoshuJippiDate);
				item.setHoshuJippiKingakuName(e.getOtherItemName());
				item.setHoshuJippiKingakuDetail(PdfUtils.newLineToBreak(e.getOtherSumText()));
				if (CommonConstant.InvoiceOtherItemType.DISCOUNT.equalsByCode(e.getOtherItemType())) {
					// 値引き金額はマイナス表示
					item.setKingaku(PdfUtils.toDispAmount(e.getOtherAmount().negate()));
				} else {
					// 値引き以外の場合、金額は表示しない
					item.setKingaku(CommonConstant.BLANK);
				}
			} else {
				// 想定外のデータ
				throw new RuntimeException("請求書PDF作成時の報酬/実費明細データが想定外のデータパターンです。");
			}
			return item;
		}).collect(Collectors.toList());

		PdfUtils.appendEmptyListItem(hoshuJippiKingakuList, P0002Data.HoshuJippiKingakuListItem::new, 1);
		p0002Data.setHoshuJippiKingakuList(hoshuJippiKingakuList);
		p0002Data.setHideHoshuJippiDate(!SystemFlg.codeToBoolean(tAccgStatementEntity.getInvoiceTransactionDatePrintFlg()));

		// 合計金額情報を設定
		p0002Data.setHoshuJippiSubTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getSubTotal())));
		p0002Data.setHoshuJippiTaxTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount())));
		p0002Data.setHoshuJippiGensenTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalWithholdingAmount())));
		p0002Data.setHoshuJippiTotal(PdfUtils.toDispTotalAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalAmount())));
		p0002Data.setTenPercentTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFee())));
		p0002Data.setTenPercentTaxTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10())));
		p0002Data.setHasTenPercentTax(!LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFee()) || !LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10()));
		p0002Data.setEightPercentTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFee())));
		p0002Data.setEightPercentTaxTotal(PdfUtils.toDispAmount(LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8())));
		p0002Data.setHasEightPercentTax(!LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFee()) || !LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8()));

		// 精算合計(すでに設定した請求額と同額)
		p0002Data.setSeisanTotal(PdfUtils.toDispTotalAmount(tAccgStatementEntity.getStatementAmount()));

		// 備考の設定
		p0002Data.setRemarks(PdfUtils.newLineToBreak(tAccgStatementEntity.getStatementRemarks()));

		return p0002Data;
	}

	/**
	 * P0003(支払計画書)のPDFデータを作成
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private FileContentsDto createP0003FileContentsDto(Long accgDocSeq) {

		// PDF出力
		P0003PdfBuilder p0003PdfBuilder = new P0003PdfBuilder();
		p0003PdfBuilder.setConfig(pdfConfig);
		p0003PdfBuilder.setPdfService(pdfService);

		// PDFデータの作成
		P0003Data p0003Data = this.createP0003Data(accgDocSeq);
		p0003PdfBuilder.setPdfData(p0003Data);

		try {
			// PDFの生成
			return p0003PdfBuilder.makePdfContentsDto();
		} catch (Exception e) {
			// 出力エラー
			throw new RuntimeException(e);
		}
	}

	/**
	 * 実費明細出力用データを作成する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private P0003Data createP0003Data(Long accgDocSeq) throws DataNotFoundException {

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません");
		}

		P0003Data p0003Data = new P0003Data();

		String outputDate;
		String ankenName;
		if (AccgDocType.STATEMENT.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 精算書の場合
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			outputDate = DateUtils.parseToString(tAccgStatementEntity.getStatementDate(), DateUtils.DATE_JP_YYYY_MM_DD);
			ankenName = tAccgStatementEntity.getStatementSubject();

		} else if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 請求書の場合
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			outputDate = DateUtils.parseToString(tAccgInvoiceEntity.getInvoiceDate(), DateUtils.DATE_JP_YYYY_MM_DD);
			ankenName = tAccgInvoiceEntity.getInvoiceSubject();
		} else {
			// 想定外のEnum値
			throw new RuntimeException("Enum値が不正です");
		}

		// 実費明細一覧データを取得
		List<TDepositRecvEntity> tDepositRecvEntities = tDepositRecvDao.selectActualCostDetailByAnkenIdAndPersonId(tAccgDocEntity.getAnkenId(), tAccgDocEntity.getPersonId());

		// usingAccgDocSeqを抽出
		Set<Long> usingAccgDocSeqSet = tDepositRecvEntities.stream().map(TDepositRecvEntity::getUsingAccgDocSeq).filter(Objects::nonNull).collect(Collectors.toSet());
		// usingAccgDocに紐づく請求書/精算書が発行済かどうかをMapに格納
		Map<Long, Boolean> usingAccgDocSeqToIssuedCheckResult = this.generateAccgDocSeqToIssuedCheckResultMap(new ArrayList<>(usingAccgDocSeqSet));

		List<P0003Data.JippiListItem> jippiListItemList = tDepositRecvEntities.stream().map(e -> {
			return P0003Data.JippiListItem.builder()
					.jippiDate(DateUtils.parseToString(e.getDepositDate(), DateUtils.DATE_JP_YYYY_MM_DD))
					.komokuName(e.getDepositItemName())
					.nyukinGaku(PdfUtils.toDispAmount(e.getDepositAmount()))
					.shukkinGaku(PdfUtils.toDispAmount(e.getWithdrawalAmount()))
					.tekiyo(PdfUtils.newLineToBreak(e.getSumText()))
					.status(this.depositUseStatusMapper(accgDocSeq, e, usingAccgDocSeqToIssuedCheckResult).getLabel())
					.build();
		}).collect(Collectors.toList());

		// 一覧データが存在しない場合、PDFに一覧が作成されないため、空行を追加
		PdfUtils.appendEmptyListItem(jippiListItemList, P0003Data.JippiListItem::new, 1);

		// 表示金額の合計値を算出
		List<BigDecimal> nyukinAmountList = tDepositRecvEntities.stream().map(TDepositRecvEntity::getDepositAmount).collect(Collectors.toList());
		List<BigDecimal> shukkinAmountList = tDepositRecvEntities.stream().map(TDepositRecvEntity::getWithdrawalAmount).collect(Collectors.toList());

		BigDecimal nyukinTotalAmount = AccountingUtils.calcTotal(nyukinAmountList);
		BigDecimal shukkinTotalAmount = AccountingUtils.calcTotal(shukkinAmountList);
		BigDecimal nyushukkinTotalAmount = nyukinTotalAmount.subtract(shukkinTotalAmount);

		String nyukinTotal = PdfUtils.toDispTotalAmount(nyukinTotalAmount);
		String shukkinTotal = PdfUtils.toDispTotalAmount(shukkinTotalAmount);
		String nyushukkinTotal = PdfUtils.toDispTotalAmount(nyushukkinTotalAmount);

		// データの設定
		p0003Data.setOutputDate(outputDate);
		p0003Data.setAnkenName(ankenName);
		p0003Data.setJippiList(jippiListItemList);
		p0003Data.setNyukinTotal(nyukinTotal);
		p0003Data.setShukkinTotal(shukkinTotal);
		p0003Data.setNyushukkinTotal(nyushukkinTotal);

		return p0003Data;
	}

	/**
	 * P005(支払計画書)のPDFデータを作成
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private FileContentsDto createP0005FileContentsDto(Long accgDocSeq) {

		// PDF出力
		P0005PdfBuilder p0005PdfBuilder = new P0005PdfBuilder();
		p0005PdfBuilder.setConfig(pdfConfig);
		p0005PdfBuilder.setPdfService(pdfService);

		// PDFデータの作成
		P0005Data p0005Data = this.createP0005Data(accgDocSeq);
		p0005PdfBuilder.setPdfData(p0005Data);

		try {
			// PDFの生成
			return p0005PdfBuilder.makePdfContentsDto();
		} catch (Exception e) {
			// 出力エラー
			throw new RuntimeException(e);
		}
	}

	/**
	 * 支払計画出力用データの作成
	 *
	 * @param accgDocSeq
	 * @return
	 */
	private P0005Data createP0005Data(Long accgDocSeq) throws DataNotFoundException {

		P0005Data p0005Data = new P0005Data();

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません");
		}

		List<TAccgInvoicePaymentPlanEntity> tAccgInvoicePaymentPlanEntities = tAccgInvoicePaymentPlanDao.selectInvoicePaymentPlanByInvoiceSeq(tAccgInvoiceEntity.getInvoiceSeq());

		// 出力日の設定
		p0005Data.setOutputDate(DateUtils.parseToString(tAccgInvoiceEntity.getInvoiceDate(), DateUtils.DATE_JP_YYYY_MM_DD));

		// 案件名の設定
		p0005Data.setAnkenName(tAccgInvoiceEntity.getInvoiceSubject());

		// 請求金額の表示
		List<BigDecimal> planAmountList = tAccgInvoicePaymentPlanEntities.stream().map(TAccgInvoicePaymentPlanEntity::getPaymentScheduleAmount).collect(Collectors.toList());
		BigDecimal invoiceAmount = AccountingUtils.calcTotal(planAmountList);
		p0005Data.setSeikyuTotal(PdfUtils.toDispTotalAmount(invoiceAmount));

		// 振込先データの設定
		p0005Data.setPaymentDestination(PdfUtils.newLineToBreak(tAccgInvoiceEntity.getTenantBankDetail()));

		// 一覧データの設定
		// 件数の設定
		int count = tAccgInvoicePaymentPlanEntities.size();
		p0005Data.setInstallmentsCount(String.format("%d回", count));

		// 一覧データの作成
		List<P0005Data.StatementScheduleListItem> leftItemList = new ArrayList<>();
		List<P0005Data.StatementScheduleListItem> rightItemList = new ArrayList<>();

		// 左右に表示する件数を設定
		// 合計が60件以下の場合 -> 行数は30件(左右合計で60件となる)
		// 合計が60件より大きいの場合 -> 行数は増えた件数分増える
		Integer listItemRowCount =0;
		if ((P0005Data.LIST_ROW_SIZE * 2) >= tAccgInvoicePaymentPlanEntities.size()) {
			listItemRowCount = P0005Data.LIST_ROW_SIZE;
		} else {
			int planCount = tAccgInvoicePaymentPlanEntities.size();
			if (planCount % 2 == 0) {
				// データ数が偶数の場合
				listItemRowCount = (planCount / 2);
			} else {
				// データ数が奇数の場合
				listItemRowCount = (planCount / 2) + 1;
			}
		}
		
		int counta = 1;
		BigDecimal balance = invoiceAmount;
		for (TAccgInvoicePaymentPlanEntity entity : tAccgInvoicePaymentPlanEntities) {
			var item = new P0005Data.StatementScheduleListItem();
			balance = balance.subtract(entity.getPaymentScheduleAmount());

			item.setCountNo(counta);
			item.setLimitDate(DateUtils.parseToString(entity.getPaymentScheduleDate(), DateUtils.DATE_JP_YYYY_MM_DD));
			item.setKingaku(PdfUtils.toDispAmount(entity.getPaymentScheduleAmount()));
			item.setBalance(PdfUtils.toDispAmount(balance));

			if (listItemRowCount >= counta) {
				// 左に表示するListに追加
				leftItemList.add(item);
			} else {
				// 右に表示するListに追加
				rightItemList.add(item);
			}

			counta++;
		}

		// 一覧データに空行を追加
		PdfUtils.appendEmptyListItem(leftItemList, P0005Data.StatementScheduleListItem::new, listItemRowCount);
		PdfUtils.appendEmptyListItem(rightItemList, P0005Data.StatementScheduleListItem::new, listItemRowCount);

		// 一覧データに設定
		p0005Data.setStatementScheduleLeftList(leftItemList);
		p0005Data.setStatementScheduleRightList(rightItemList);

		return p0005Data;
	}

	/**
	 * M0013MailBuilder（会計書類送付 WEB共有 顧客宛）を作成します
	 * 
	 * @param accgDocActSendSeq
	 * @param accgDocType
	 * @param tenantSeq
	 * @param accgDocDownloadSeq
	 * @param mailSignature
	 * @return
	 */
	private M0013MailBuilder createM0013MailBuilder(Long accgDocActSendSeq, AccgDocType accgDocType, Long tenantSeq, Long accgDocDownloadSeq, String mailSignature) {
		M0013MailBuilder builder13 = new M0013MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_13.getCd(), builder13);

		// データの取得
		TAccgDocActSendEntity tAccgDocActSendEntity = tAccgDocActSendDao.selectAccgDocActSendByActSendSeq(accgDocActSendSeq);
		TAccgDocDownloadEntity tAccgDocDownloadEntity = tAccgDocDownloadDao.selectAccgDocDownloadByAccgDocDownloadSeq(accgDocDownloadSeq);

		// 送信先の設定
		builder13.setWorkTo(Arrays.asList(tAccgDocActSendEntity.getSendTo()));
		builder13.setWorkCc(StringUtils.toArray(tAccgDocActSendEntity.getSendCc()));
		builder13.setWorkBcc(StringUtils.toArray(tAccgDocActSendEntity.getSendBcc()));
		builder13.setWorkReplyTo(tAccgDocActSendEntity.getReplyTo());

		// 送信元の名前設定
		builder13.setWorkFromName(tAccgDocActSendEntity.getSendFromName());

		// 内容の設定
		String webUrl = uriService.getGlobalUrl(DownloadAuthController.class, (controller) -> controller.verify(generateTenantAuthKey(tenantSeq), tAccgDocDownloadEntity.getDownloadViewUrlKey(), null, null));

		// パスワード設定時のみ追加文言を設定(パスワードを設定しない場合は空文字)
		String passwordMsg = "";
		if (!StringUtils.isEmpty(tAccgDocDownloadEntity.getDownloadViewPassword())) {
			passwordMsg = AccgConstant.ACCG_INVOICE_FILE_SEND_WEB_SHARED_PASSWORD_MSG;
		}

		String webSharedAddMsg = String.format(
				AccgConstant.ACCG_INVOICE_FILE_SEND_WEB_SHARED_ADD_MSG,
				accgDocType.getVal(),
				passwordMsg,
				webUrl,
				DateUtils.parseToString(tAccgDocActSendEntity.getDownloadLimitDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		builder13.makeTitle(tAccgDocActSendEntity.getSendSubject());
		builder13.makeBody(tAccgDocActSendEntity.getSendBody(), webSharedAddMsg, mailSignature);

		return builder13;
	}

	/**
	 * M0014MailBuilder（会計書類送付 会計書類送付 WEB共有パスワード 顧客宛）を作成します
	 * 
	 * @param accgDocActSendSeq
	 * @param password
	 * @return
	 */
	private M0014MailBuilder createM0014MailBuilder(Long accgDocActSendSeq, String password) {
		// データの取得
		TAccgDocActSendEntity tAccgDocActSendEntity = tAccgDocActSendDao.selectAccgDocActSendByActSendSeq(accgDocActSendSeq);
		
		M0014MailBuilder builder14 = new M0014MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_14.getCd(), builder14);

		builder14.setWorkTo(Arrays.asList(tAccgDocActSendEntity.getSendTo()));
		builder14.setWorkCc(StringUtils.toArray(tAccgDocActSendEntity.getSendCc()));
		builder14.setWorkBcc(StringUtils.toArray(tAccgDocActSendEntity.getSendBcc()));
		builder14.setWorkReplyTo(tAccgDocActSendEntity.getReplyTo());

		// 送信元の名前設定
		builder14.setWorkFromName(tAccgDocActSendEntity.getSendFromName());

		builder14.makeTitle(tAccgDocActSendEntity.getSendSubject());
		builder14.makeBody(tAccgDocActSendEntity.getSendSubject(), password);

		return builder14;
	}

	// =========================================================================
	// ▼ チェック、バリデーション系
	// =========================================================================

	/**
	 * 各会計書類PDFが作成可能かどうか
	 * 
	 * @param accgDocSeq
	 * @param fileType
	 * @return
	 */
	private boolean isCreatiblePdf(Long accgDocSeq, AccgDocFileType fileType) {

		// 会計書類情報を取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);

		switch (fileType) {
		case INVOICE:
			// 請求書PDFの作成可能チェック
			if (!IssueStatus.DRAFT.equalsByCode(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
				// 請求書の発行ステータスが「下書き」以外の場合は、作成不可
				return false;
			}
			return true;
		case STATEMENT:
			// 精算書書PDFの作成可能チェック
			if (!IssueStatus.DRAFT.equalsByCode(tAccgStatementEntity.getStatementIssueStatus())) {
				// 精算書の発行ステータスが「下書き」以外の場合は、作成不可
				return false;
			}
			return true;
		case DEPOSIT_DETAIL:
			// 実費明細PDFの作成可能チェック
			if (AccgDocType.INVOICE.equalsByCode(tAccgDocEntity.getAccgDocType())) {
				// 請求書の場合
				if (!IssueStatus.DRAFT.equalsByCode(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
					// 請求書の発行ステータスが「下書き」以外の場合 -> 作成不可
					return false;
				}
				if (!SystemFlg.codeToBoolean(tAccgInvoiceEntity.getDepositDetailAttachFlg())) {
					// 請求書の実費明細添付フラグがOFFの場合 -> 作成不可
					return false;
				}

				return true;
			} else if (AccgDocType.STATEMENT.equalsByCode(tAccgDocEntity.getAccgDocType())) {
				// 精算書の場合、精算書の発行ステータスが「下書き」以外の場合 -> 作成不可
				if (!IssueStatus.DRAFT.equalsByCode(tAccgStatementEntity.getStatementIssueStatus())) {
					// 精算書の発行ステータスが「下書き」以外の場合 -> 作成不可
					return false;
				}
				if (!SystemFlg.codeToBoolean(tAccgStatementEntity.getDepositDetailAttachFlg())) {
					// 精算書の実費明細添付フラグがOFFの場合 -> 作成不可
					return false;
				}
				return true;
			} else {
				// 想定外のEnum値
				throw new RuntimeException("想定外のEnum値です");
			}
		case INVOICE_PAYMENT_PLAN:
			// 支払計画書PDFの作成可能チェック
			if (!IssueStatus.DRAFT.equalsByCode(tAccgInvoiceEntity.getInvoiceIssueStatus())) {
				// 請求書の発行ステータスが「下書き」以外の場合は、作成不可
				return false;
			}
			if (!SystemFlg.codeToBoolean(tAccgInvoiceEntity.getPaymentPlanAttachFlg())) {
				// 請求書の支払計画添付フラグがOFFの場合は、作成不可
				return false;
			}
			return true;
		default:
			throw new RuntimeException("Enum値が不正です");
		}

	}

	/**
	 * 預り金テーブルのレコードの各パラメータの値をもとに、<br>
	 * 対象の預り金データが、対象となる請求書／精算書で利用可能（チェックして利用可能）かどうかを判定する。<br>
	 * ※targetAccgDocSeqに値がある場合、その会計書類で使用されている（usingAccgDocSeqに値がある）預り金も利用可能と判定する
	 * 
	 * <pre>
	 * ■true条件
	 * ユーザーが手動作成した「入金/出金（事務所負担ではない）」で請求書/精算書で使用されていない預り金。
	 * 請求書/精算書から自動生成された「実費以外の入金済み」で請求書/精算書で使用されていない預り金。
	 * 対象となる請求書/精算書の会計書類SEQが指定されている場合、その会計書類ですでに使用されている預り金。
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
	 * @param createdType 作成タイプ
	 * @param tenantBearFlg 事務所負担フラグ
	 * @param usingAccgDocSeq 会計書類SEQ（使用先）
	 * @param depositType 入出金タイプ
	 * @param expenseInvoiceFlg 実費入金フラグ
	 * @param depositCompleteFlg 入出金完了フラグ
	 * @param targetAccgDocSeq 対象となる請求書／精算書の会計書類SEQ
	 * 
	 * @return 利用可能の場合：true、利用不可の場合：false
	 */
	private boolean isCanUseDepositRecvOnAccgDoc(DepositRecvCreatedType createdType, SystemFlg tenantBearFlg, Long usingAccgDocSeq,
			DepositType depositType, ExpenseInvoiceFlg expenseInvoiceFlg, SystemFlg depositCompleteFlg, Long targetAccgDocSeq) {

		boolean isCanUse = false;

		// ユーザーが手動作成した「入金/出金（事務所負担ではない）」で請求書/精算書で使用されていない預り金
		if (DepositRecvCreatedType.USER_CREATED == createdType
				&& SystemFlg.FLG_ON != tenantBearFlg
				&& usingAccgDocSeq == null) {
			isCanUse = true;
		}

		// 請求書/精算書から自動生成された「実費以外の入金済み」で請求書/精算書で使用されていない預り金
		if (DepositRecvCreatedType.CREATED_BY_ISSUANCE == createdType
				&& DepositType.NYUKIN == depositType
				&& ExpenseInvoiceFlg.EXCEPT_EXPENSE == expenseInvoiceFlg
				&& SystemFlg.FLG_ON == depositCompleteFlg
				&& usingAccgDocSeq == null) {
			isCanUse = true;
		}

		// 対象の精算書／請求書が指定されている場合、
		// 対象の精算書／請求書ですでに利用されている預り金も「利用可能」と判定する
		if (targetAccgDocSeq != null && targetAccgDocSeq.equals(usingAccgDocSeq)) {
			isCanUse = true;
		}

		return isCanUse;
	}

	// =========================================================================
	// ▼ 登録／更新／削除系
	// =========================================================================

	/**
	 * WEB共有用テナント認証キーを作成する
	 * 
	 * @param tenantSeq
	 * @return
	 */
	private String generateTenantAuthKey(Long tenantSeq) {
		return StringUtils.randomNumeric(CommonConstant.GLOBAL_TENANT_AUTH_DUMMY_LENGTH) + tenantSeq;
	}

	/**
	 * 会計書類の各PDFを引数に応じて出力し、S3にアップロード、DBへの登録処理を行う<br>
	 * 
	 * <pre>
	 * ※本メソッドはS3APIコールを含むため、トランザクションの最後に呼ぶこと
	 * </pre>
	 *
	 * @param accgDocSeq 会計書類SEQ
	 * @param tenantSeq テナントSEQ
	 * @param validateReCreate 再作成を許容するかどうか
	 * @param deleteS3ObjectKeys(再作成しない場合は、Nullを許容)
	 * 本メソッドのレコード削除処理により、アプリケーションの管理対象から外れたS3オブジェクトキーを格納するオブジェクト
	 * @param fileType 会計書類ファイル種別
	 * @param needRebuildCreate 会計書類ファイルレコードに再作成が必要な状態で作成する
	 * @throws AppException
	 */
	private void createAccgDocFileAndDetail(Long accgDocSeq, Long tenantSeq, boolean validateReCreate, @Nullable Set<String> deleteS3ObjectKeys, AccgDocFileType fileType, boolean needRebuildCreate) throws AppException, DataNotFoundException {

		if (!isCreatiblePdf(accgDocSeq, fileType)) {
			// 請求書/精算書の発行ステータスに元に会計書類のPDF作成を制御 -> 作成不可の場合
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 未送付の会計書類ファイル情報を種別をキーとして取得
		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(accgDocSeq, fileType);

		if (!validateReCreate && !CollectionUtils.isEmpty(accgDocFileBeans)) {
			// 再作成無効時にファイル情報が存在している場合
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除するS3ファイルのオブジェクトキーを保持しておく(このAPIは最後に投げる)
		if (validateReCreate) {
			// 再作成時のみ、事前に削除処理を行う
			Set<Long> accgDocFileSeqList = accgDocFileBeans.stream().map(AccgDocFileBean::getAccgDocFileSeq).collect(Collectors.toSet());
			Set<Long> accgDocFileDetailSeqList = accgDocFileBeans.stream().map(AccgDocFileBean::getAccgDocFileDetailSeq).collect(Collectors.toSet());

			List<TAccgDocFileEntity> tAccgDocFileDeleteEntities = tAccgDocFileDao.selectAccgDocFileByAccgDocFileSeq(new ArrayList<>(accgDocFileSeqList));
			List<TAccgDocFileDetailEntity> tAccgDocFileDetailEntities = tAccgDocFileDetailDao.selectAccgDocFileDetailByAccgDocFileDetailSeq(new ArrayList<>(accgDocFileDetailSeqList));

			// 削除対象のS3オブジェクトキーを引数のオブジェクトにセット
			tAccgDocFileDetailEntities.stream().map(TAccgDocFileDetailEntity::getS3ObjectKey).forEach(deleteS3ObjectKeys::add);

			try {
				// 削除処理
				tAccgDocFileDao.batchDelete(tAccgDocFileDeleteEntities);
				tAccgDocFileDetailDao.batchDelete(tAccgDocFileDetailEntities);

			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
		}

		FileContentsDto pdfFileContentsDto;
		List<FileContentsDto> pngFileContentsDtos;

		try {
			switch (fileType) {
			case INVOICE:
				// 請求書PDFの作成
				pdfFileContentsDto = this.createP0001FileContentsDto(accgDocSeq, tenantSeq);
				break;
			case STATEMENT:
				// 精算書PDFの作成
				pdfFileContentsDto = this.createP0002FileContentsDto(accgDocSeq, tenantSeq);
				break;
			case DEPOSIT_DETAIL:
				// 実費明細PDFの作成
				pdfFileContentsDto = this.createP0003FileContentsDto(accgDocSeq);
				break;
			case INVOICE_PAYMENT_PLAN:
				// 支払計画書PDFの作成
				pdfFileContentsDto = this.createP0005FileContentsDto(accgDocSeq);
				break;
			default:
				throw new RuntimeException("Enum値が不正です");
			}

			// PDF -> PNGの生成
			pngFileContentsDtos = pdfService.convertPdf2PngList(pdfFileContentsDto);
		} catch (Exception e) {
			// 出力エラー
			throw new RuntimeException(e);
		}

		// =======================================================
		// 会計書類ファイルデータの作成
		// =======================================================

		// 新規登録用TAccgDocFileEntityの作成関数
		Function<FileExtension, TAccgDocFileEntity> createInsertEntityFn = (fileExtension) -> {
			TAccgDocFileEntity entity = new TAccgDocFileEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setAccgDocFileType(fileType.getCd());
			entity.setFileExtension(fileExtension.getVal());
			entity.setRecreateStandbyFlg(SystemFlg.booleanToCode(needRebuildCreate));
			return entity;
		};

		// 会計書類の登録用データを作成
		TAccgDocFileEntity pdfAccgDocFileEntity = createInsertEntityFn.apply(FileExtension.PDF);
		TAccgDocFileEntity pngAccgDocFileEntity = createInsertEntityFn.apply(FileExtension.PNG);

		try {
			// 登録処理
			tAccgDocFileDao.insert(pdfAccgDocFileEntity);
			tAccgDocFileDao.insert(pngAccgDocFileEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// =======================================================
		// 会計書類ファイル-詳細データの作成
		// =======================================================

		// アップロード情報を作成
		Map<String, FileContentsDto> uploadFileMap = new HashMap<String, FileContentsDto>();

		// 会計書類ファイルのS3DirectoryDomainを作成
		Directory dir = this.createAccgS3DirectoryDomain(tenantSeq);

		// 会計書類詳細データの登録用データを作成
		List<TAccgDocFileDetailEntity> tAccgDocFileDetailInsertEntities = new ArrayList<>();

		// PDFのオブジェクトキーを作成
		String pdfS3ObjectKey = fileStorageService.generateS3ObjectKey(dir, pdfFileContentsDto.getExtension());
		uploadFileMap.put(pdfS3ObjectKey, pdfFileContentsDto);

		// 登録用Entityを作成
		TAccgDocFileDetailEntity pdfAccgDocFileDetailEntity = new TAccgDocFileDetailEntity();
		pdfAccgDocFileDetailEntity.setAccgDocFileSeq(pdfAccgDocFileEntity.getAccgDocFileSeq());
		pdfAccgDocFileDetailEntity.setFileBranchNo(1L); // 連番の初期値は１とする
		pdfAccgDocFileDetailEntity.setS3ObjectKey(pdfS3ObjectKey);
		tAccgDocFileDetailInsertEntities.add(pdfAccgDocFileDetailEntity); //

		// 枝番(初期値：１)
		Long branchNo = 1L;
		for (FileContentsDto pngFileContentsDto : pngFileContentsDtos) {
			// PNGのオブジェクトキーを作成
			String pngS3ObjectKey = fileStorageService.generateS3ObjectKey(dir, pngFileContentsDto.getExtension());
			uploadFileMap.put(pngS3ObjectKey, pngFileContentsDto);

			// 登録用Entityを作成
			TAccgDocFileDetailEntity pngAccgDocFileDetailEntity = new TAccgDocFileDetailEntity();
			pngAccgDocFileDetailEntity.setAccgDocFileSeq(pngAccgDocFileEntity.getAccgDocFileSeq());
			pngAccgDocFileDetailEntity.setFileBranchNo(branchNo);
			pngAccgDocFileDetailEntity.setS3ObjectKey(pngS3ObjectKey);
			tAccgDocFileDetailInsertEntities.add(pngAccgDocFileDetailEntity);
			branchNo++;
		}

		try {
			// 登録処理
			tAccgDocFileDetailDao.batchInsert(tAccgDocFileDetailInsertEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// =======================================================
		// 会計書類ファイル-詳細データのS3APIコール
		// =======================================================

		try {
			// ファイルのアップロード処理
			fileStorageService.multiUploadPrivateSpecifyObjectKey(uploadFileMap);

		} catch (IOException e) {
			// S3APIでエラーが発生した場合
			throw new RuntimeException("PDF作成処理のS3アップロードが失敗しました。", e);
		}
	}

	/**
	 * 会計書類登録
	 * 
	 * @param personId
	 * @param ankenId
	 * @param accgDocType
	 * @return
	 * @throws AppException
	 */
	private Long registAccgDoc(Long personId, Long ankenId, String accgDocType) throws AppException {
		// 会計書類-登録
		TAccgDocEntity tAccgDocEntity = new TAccgDocEntity();

		tAccgDocEntity.setPersonId(personId);
		tAccgDocEntity.setAnkenId(ankenId);
		tAccgDocEntity.setAccgDocType(accgDocType);
		int insertCount = tAccgDocDao.insert(tAccgDocEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tAccgDocEntity.getAccgDocSeq();
	}

	/**
	 * 請求書-登録
	 * 
	 * @param accgDocSeq
	 * @param personId
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	private Long registAccgInvoice(Long accgDocSeq, Long personId, Long ankenId) throws AppException {

		// 請求書-登録
		TAccgInvoiceEntity tAccgInvoiceEntity = new TAccgInvoiceEntity();

		// 会計書類SEQ
		tAccgInvoiceEntity.setAccgDocSeq(accgDocSeq);

		// 発行ステータス：下書き
		tAccgInvoiceEntity.setInvoiceIssueStatus(IssueStatus.DRAFT.getCd());
		// 入金ステータス：発行待ち
		tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.DRAFT.getCd());
		// 請求額
		tAccgInvoiceEntity.setInvoiceAmount(BigDecimal.ZERO);

		// 請求タイプ
		tAccgInvoiceEntity.setInvoiceType(InvoiceType.IKKATSU.getCd());
		// 請求先名簿ID
		tAccgInvoiceEntity.setBillToPersonId(personId);
		// 売上日
		LocalDate now = LocalDate.now();
		tAccgInvoiceEntity.setSalesDate(now);

		// 売上計上先SEQを取得
		List<Long> accountSeqList = tAnkenTantoDao.selectByAnkenIdAndTantoType(ankenId, TantoType.SALES_OWNER.getCd());
		Long salesAccountSeq = null;
		if (accountSeqList.size() > 0) {
			// 売上計上先：1件目を売上計上先に指定する
			salesAccountSeq = accountSeqList.get(0);
			tAccgInvoiceEntity.setSalesAccountSeq(salesAccountSeq);
		}

		// 実費明細添付フラグ
		tAccgInvoiceEntity.setDepositDetailAttachFlg(SystemFlg.FLG_OFF.getCd());
		// 支払計画添付フラグ
		tAccgInvoiceEntity.setPaymentPlanAttachFlg(SystemFlg.FLG_OFF.getCd());

		// 請求書設定を取得
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();
		// タイトル
		tAccgInvoiceEntity.setInvoiceTitle(mInvoiceSettingEntity.getDefaultTitle());
		// 日付
		tAccgInvoiceEntity.setInvoiceDate(now);

		// 請求番号
		tAccgInvoiceEntity.setInvoiceNo(issueNewInvoiceNo());

		// 名簿情報取得
		TPersonEntity tPersonEntity = tPersonDao.selectPersonByPersonId(personId);
		PersonName personName = PersonName.fromEntity(tPersonEntity);
		// 請求先名称
		tAccgInvoiceEntity.setInvoiceToName(personName.getName());
		// 請求先敬称
		tAccgInvoiceEntity.setInvoiceToNameEnd(this.personTypeToNameEnd(tPersonEntity.getCustomerType()));
		// 請求先詳細
		tAccgInvoiceEntity.setInvoiceToDetail(this.personInfoToDetail(tPersonEntity));

		// 案件情報を取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		// 案件名を取得する
		String ankenName = CommonConstant.BLANK;
		if (tAnkenEntity != null) {
			ankenName = tAnkenEntity.getAnkenName();
		}
		// 件名をマスタ設定の指定方式に変換する
		String invoiceSubject = AccountingUtils.formatAccgDocSubject(ankenName,
				mInvoiceSettingEntity.getSubjectPrefix(), mInvoiceSettingEntity.getSubjectSuffix());
		// 件名
		tAccgInvoiceEntity.setInvoiceSubject(invoiceSubject);

		// 挿入文
		tAccgInvoiceEntity.setInvoiceSubText(mInvoiceSettingEntity.getDefaultSubText());

		// 差出人事務所名
		TenantDetailKozaBean tenantDetailKozaBean = mTenantDao.selectTenantDetailAndKozaBySeq(SessionUtils.getTenantSeq());
		String tenantName = StringUtils.isEmpty(tenantDetailKozaBean.getTenantName()) ? "" : tenantDetailKozaBean.getTenantName();
		tAccgInvoiceEntity.setInvoiceFromTenantName(tenantName.length() > AccgConstant.MX_LEN_INVOICE_FROM_OFFICE_NAME ? tenantName.substring(0, AccgConstant.MX_LEN_INVOICE_FROM_OFFICE_NAME) : tenantName);
		// 差出人詳細
		tAccgInvoiceEntity.setInvoiceFromDetail(this.tenantInfoToDetail(tenantDetailKozaBean, tAnkenEntity, salesAccountSeq));

		// 取引日表示フラグ
		String transactionDatePrintFlg = mInvoiceSettingEntity.getTransactionDatePrintFlg();
		// 既入金取引日印字フラグ
		tAccgInvoiceEntity.setRepayTransactionDatePrintFlg(transactionDatePrintFlg);
		// 請求取引日印字フラグ
		tAccgInvoiceEntity.setInvoiceTransactionDatePrintFlg(transactionDatePrintFlg);

		// 振込期日表示フラグ
		tAccgInvoiceEntity.setDueDatePrintFlg(mInvoiceSettingEntity.getDueDatePrintFlg());

		// 案件-案件区分を取得する
		String ankenType = tAnkenEntity.getAnkenType();
		
		// 銀行口座 -  案件区分が事務所案件ならテナント口座、個人案件なら売上計上先の口座
		// 印影表示フラグ - 事務所案件なら事務所印の設定値、個人案件なら弁護士印の設定値をデフォルト値としてセットする
		if (AnkenType.JIMUSHO.equalsByCode(ankenType)) {
			// 事務所案件
			
			// 銀行口座にテナント口座をセット
			tAccgInvoiceEntity.setTenantBankDetail(this.tenantBankToDetail(tenantDetailKozaBean));
			
			// 事務所印表示フラグ（請求書設定で設定されている事務所印の表示設定をセット）
			tAccgInvoiceEntity.setTenantStampPrintFlg(mInvoiceSettingEntity.getTenantStampPrintFlg());
			
		} else if (AnkenType.KOJIN.equalsByCode(ankenType)) {
			// 個人案件
			
			// 銀行口座に売上計上先の口座をセット
			TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao.selectDefaultSalesAccountKozaByAccountSeq(salesAccountSeq);
			tAccgInvoiceEntity.setTenantBankDetail(this.salesAccountBankToDetail(tGinkoKozaEntity));
			
			// 事務所印表示フラグ（売上計上先の個人設定で設定されている弁護士印の表示設定をセット）
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

		// 備考
		tAccgInvoiceEntity.setInvoiceRemarks(mInvoiceSettingEntity.getDefaultRemarks());

		// 既入金項目合算フラグ（既入金）
		tAccgInvoiceEntity.setRepaySumFlg(SystemFlg.FLG_OFF.getCd());
		// 実費項目合算フラグ（請求）
		tAccgInvoiceEntity.setExpenseSumFlg(SystemFlg.FLG_OFF.getCd());

		// 登録処理
		int insertCount = tAccgInvoiceDao.insert(tAccgInvoiceEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tAccgInvoiceEntity.getInvoiceSeq();
	}

	/**
	 * 精算書-登録
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	private Long registAccgStatement(Long accgDocSeq, Long personId, Long ankenId) throws AppException {

		// 精算書-登録
		TAccgStatementEntity tAccgStatementEntity = new TAccgStatementEntity();

		// 会計書類SEQ
		tAccgStatementEntity.setAccgDocSeq(accgDocSeq);

		// 発行ステータス：下書き
		tAccgStatementEntity.setStatementIssueStatus(IssueStatus.DRAFT.getCd());
		// 精算状況：発行待ち
		tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.DRAFT.getCd());
		// 精算額
		tAccgStatementEntity.setStatementAmount(BigDecimal.ZERO);

		// 返金先名簿ID
		tAccgStatementEntity.setRefundToPersonId(personId);
		// 売上日
		LocalDate now = LocalDate.now();
		tAccgStatementEntity.setSalesDate(now);

		// 売上計上先を取得
		List<Long> accountSeqList = tAnkenTantoDao.selectByAnkenIdAndTantoType(ankenId, TantoType.SALES_OWNER.getCd());
		Long salesAccountSeq = null;
		if (accountSeqList.size() > 0) {
			// 売上計上先：1件目を売上計上先に指定する
			salesAccountSeq = accountSeqList.get(0);
			tAccgStatementEntity.setSalesAccountSeq(salesAccountSeq);
		}

		// 実費明細添付フラグ
		tAccgStatementEntity.setDepositDetailAttachFlg(SystemFlg.FLG_OFF.getCd());

		// 精算書設定を取得
		MStatementSettingEntity mStatementSettingEntity = mStatementSettingDao.select();
		// タイトル
		tAccgStatementEntity.setStatementTitle(mStatementSettingEntity.getDefaultTitle());
		// 日付
		tAccgStatementEntity.setStatementDate(now);

		// 精算番号
		tAccgStatementEntity.setStatementNo(issueNewStatementNo());

		// 名簿情報取得
		TPersonEntity tPersonEntity = tPersonDao.selectPersonByPersonId(personId);
		PersonName personName = PersonName.fromEntity(tPersonEntity);
		// 精算先名称
		tAccgStatementEntity.setStatementToName(personName.getName());
		// 精算先敬称
		tAccgStatementEntity.setStatementToNameEnd(this.personTypeToNameEnd(tPersonEntity.getCustomerType()));
		// 精算先詳細
		tAccgStatementEntity.setStatementToDetail(this.personInfoToDetail(tPersonEntity));

		// 案件名を取得する
		String ankenName = CommonConstant.BLANK;
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		if (tAnkenEntity != null) {
			ankenName = tAnkenEntity.getAnkenName();
		}
		// 件名をマスタ設定の指定方式に変換する
		String invoiceSubject = AccountingUtils.formatAccgDocSubject(ankenName,
				mStatementSettingEntity.getSubjectPrefix(), mStatementSettingEntity.getSubjectSuffix());
		// 件名
		tAccgStatementEntity.setStatementSubject(invoiceSubject);

		// 挿入文
		tAccgStatementEntity.setStatementSubText(mStatementSettingEntity.getDefaultSubText());

		// 差出人事務所名
		TenantDetailKozaBean tenantDetailKozaBean = mTenantDao.selectTenantDetailAndKozaBySeq(SessionUtils.getTenantSeq());
		String tenantName = StringUtils.isEmpty(tenantDetailKozaBean.getTenantName()) ? "" : tenantDetailKozaBean.getTenantName();
		tAccgStatementEntity.setStatementFromTenantName(tenantName.length() > AccgConstant.MX_LEN_INVOICE_FROM_OFFICE_NAME ? tenantName.substring(0, AccgConstant.MX_LEN_INVOICE_FROM_OFFICE_NAME) : tenantName);
		// 差出人詳細
		tAccgStatementEntity.setStatementFromDetail(this.tenantInfoToDetail(tenantDetailKozaBean, tAnkenEntity, salesAccountSeq));

		// 取引日表示フラグ
		String transactionDatePrintFlg = mStatementSettingEntity.getTransactionDatePrintFlg();
		// 既入金取引日印字フラグ
		tAccgStatementEntity.setRepayTransactionDatePrintFlg(transactionDatePrintFlg);
		// 請求取引日印字フラグ
		tAccgStatementEntity.setInvoiceTransactionDatePrintFlg(transactionDatePrintFlg);

		// 返金期日表示フラグ
		tAccgStatementEntity.setRefundDatePrintFlg(mStatementSettingEntity.getRefundDatePrintFlg());

		// 返金先-銀行口座
		tAccgStatementEntity.setRefundBankDetail(this.bankToDetail(tPersonEntity.getGinkoName(), tPersonEntity.getShitenName(),
				tPersonEntity.getShitenNo(), tPersonEntity.getKozaType(), tPersonEntity.getKozaNo(), tPersonEntity.getKozaName()));
		
		// 案件-案件区分を取得する
		String ankenType = tAnkenEntity.getAnkenType();
		
		// 印影表示フラグ - 事務所案件なら事務所印の設定値、個人案件なら弁護士印の設定値をデフォルト値としてセットする
		if (AnkenType.JIMUSHO.equalsByCode(ankenType)) {
			// 事務所案件
			
			// 印影表示フラグ（精算書設定で設定されている事務所印の表示設定をセット）
			tAccgStatementEntity.setTenantStampPrintFlg(mStatementSettingEntity.getTenantStampPrintFlg());
			
		} else if (AnkenType.KOJIN.equalsByCode(ankenType)) {
			// 個人案件
			
			// 印影表示フラグ（売上計上先の個人設定で設定されている弁護士印の表示設定をセット）
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
		
		// 備考
		tAccgStatementEntity.setStatementRemarks(mStatementSettingEntity.getDefaultRemarks());

		// 既入金項目合算フラグ（既入金）
		tAccgStatementEntity.setRepaySumFlg(SystemFlg.FLG_OFF.getCd());
		// 実費項目合算フラグ（請求）
		tAccgStatementEntity.setExpenseSumFlg(SystemFlg.FLG_OFF.getCd());

		// 登録処理
		int insertCount = tAccgStatementDao.insert(tAccgStatementEntity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tAccgStatementEntity.getStatementSeq();
	}

	/**
	 * 請求項目、請求項目-報酬テーブルに報酬データを登録します。<br>
	 * 報酬テーブルの会計書類SEQを登録します。
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param feeSeqList
	 * @throws AppException
	 */
	private void registAccgDocInvoiceFee(Long accgDocSeq, Long ankenId, Long personId, List<Long> feeSeqList)
			throws AppException {
		if (CollectionUtils.isEmpty(feeSeqList)) {
			return;
		}
		// 報酬データ取得
		List<TFeeEntity> tFeeEntityList = tFeeDao.selectFeeByFeeSeqList(feeSeqList);
		if (CollectionUtils.isEmpty(tFeeEntityList) || feeSeqList.size() != tFeeEntityList.size()) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 登録データ作成
		List<InvoiceRowInputForm> feeList = new ArrayList<>();
		long docInvoiceOrder = 1;
		for (TFeeEntity entity : tFeeEntityList) {
			InvoiceRowInputForm invoiceRowInputForm = new InvoiceRowInputForm();
			invoiceRowInputForm.setFeeSeq(entity.getFeeSeq());
			invoiceRowInputForm.setDocInvoiceOrder(docInvoiceOrder++);
			invoiceRowInputForm.setTransactionDate(
					DateUtils.parseToString(entity.getFeeDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			invoiceRowInputForm.setSumText(entity.getSumText());
			feeList.add(invoiceRowInputForm);
		}
		// 登録
		registUnPaidFee(feeList, accgDocSeq, ankenId, personId);
	}

	/**
	 * 請求項目、請求項目-預り金テーブルに預り金データを登録します。<br>
	 * 預り金テーブルの会計書類SEQを登録します。
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param depositRecvSeqList
	 * @throws AppException
	 */
	private void registAccgDocInvoiceDeposit(Long accgDocSeq, Long ankenId, Long personId,
			List<Long> depositRecvSeqList) throws AppException {
		if (CollectionUtils.isEmpty(depositRecvSeqList)) {
			return;
		}
		// 預り金データ取得
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao
				.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (CollectionUtils.isEmpty(tDepositRecvEntityList)
				|| depositRecvSeqList.size() != tDepositRecvEntityList.size()) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入金データは既入金として登録
		List<TDepositRecvEntity> nyukinDepositList = tDepositRecvEntityList.stream()
				.filter(entity -> DepositType.NYUKIN.equalsByCode(entity.getDepositType()))
				.collect(Collectors.toList());
		List<RepayRowInputForm> depositNyukinList = new ArrayList<>();
		long docRepayOrder = 1;
		for (TDepositRecvEntity entity : nyukinDepositList) {
			RepayRowInputForm repayRowInputForm = new RepayRowInputForm();
			repayRowInputForm.setDepositRecvSeqList(List.of(entity.getDepositRecvSeq()));
			repayRowInputForm.setAccgDocSeq(accgDocSeq);
			repayRowInputForm.setDocRepayOrder(docRepayOrder++);
			repayRowInputForm.setRepayAmount(LoiozNumberUtils.parseAsString(entity.getDepositAmount()));
			repayRowInputForm.setRepayItemName(entity.getDepositItemName());
			repayRowInputForm.setRepayTransactionDate(
					DateUtils.parseToString(entity.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			repayRowInputForm.setSumText(entity.getSumText());
			depositNyukinList.add(repayRowInputForm);
		}
		commonAccgInvoiceStatementService.registRepay(depositNyukinList, accgDocSeq);

		// 出金データは実費として登録
		List<TDepositRecvEntity> shukkinDepositList = tDepositRecvEntityList.stream()
				.filter(entity -> DepositType.SHUKKIN.equalsByCode(entity.getDepositType()))
				.collect(Collectors.toList());
		List<InvoiceRowInputForm> depositShukkinList = new ArrayList<>();
		long docInvoiceOrder = 1;
		for (TDepositRecvEntity entity : shukkinDepositList) {
			InvoiceRowInputForm invoiceRowInputForm = new InvoiceRowInputForm();
			invoiceRowInputForm.setInvoieType(InvoiceDepositType.EXPENSE.getCd());
			invoiceRowInputForm.setDepositRecvSeqList(List.of(entity.getDepositRecvSeq()));
			invoiceRowInputForm.setItemName(entity.getDepositItemName());
			invoiceRowInputForm.setAmount(LoiozNumberUtils.parseAsString(entity.getWithdrawalAmount()));
			invoiceRowInputForm.setDocInvoiceOrder(docInvoiceOrder++);
			invoiceRowInputForm.setTransactionDate(
					DateUtils.parseToString(entity.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			invoiceRowInputForm.setSumText(entity.getSumText());
			depositShukkinList.add(invoiceRowInputForm);
		}
		commonAccgInvoiceStatementService.registInvoiceDeposit(depositShukkinList, accgDocSeq);
	}

	/**
	 * 会計書類-最終採番番号の登録・更新
	 * 
	 * @param accgDocType
	 * @param numberingType
	 * @return
	 * @throws AppException
	 */
	private Long registUpdateAccgDocLastUsedNumber(String accgDocType, String numberingType) throws AppException {
		// 会計書類で使用する連番
		Long useLastUsedNumber = null;

		// 採番タイプ（連番）を取得
		TAccgDocLastUsedNumberEntity tAccgDocLastUsedNumberEntityTypeSeq = tAccgDocLastUsedNumberDao.selectByType(accgDocType, AccgNoNumberingType.SEQ.getCd());

		if (tAccgDocLastUsedNumberEntityTypeSeq == null) {
			// 新規登録
			tAccgDocLastUsedNumberEntityTypeSeq = new TAccgDocLastUsedNumberEntity();
			tAccgDocLastUsedNumberEntityTypeSeq.setAccgDocType(accgDocType);
			tAccgDocLastUsedNumberEntityTypeSeq.setNumberingType(AccgNoNumberingType.SEQ.getCd());
			tAccgDocLastUsedNumberEntityTypeSeq.setNumberingLastNo(1L);
			tAccgDocLastUsedNumberEntityTypeSeq.setNumberingLastDate(LocalDate.now());
			int insertCount = tAccgDocLastUsedNumberDao.insert(tAccgDocLastUsedNumberEntityTypeSeq);
			if (insertCount != 1) {
				// 登録処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00012, null);
			}
		} else {
			// 更新（カウントアップ）
			Long updateNo = tAccgDocLastUsedNumberEntityTypeSeq.getNumberingLastNo();
			tAccgDocLastUsedNumberEntityTypeSeq.setNumberingLastNo(++updateNo);
			tAccgDocLastUsedNumberEntityTypeSeq.setNumberingLastDate(LocalDate.now());
			tAccgDocLastUsedNumberDao.update(tAccgDocLastUsedNumberEntityTypeSeq);
		}
		// 連番タイプの連番を設定（マスタ設定している採番タイプが「連番」以外の場合は後続で値が変わる）
		useLastUsedNumber = tAccgDocLastUsedNumberEntityTypeSeq.getNumberingLastNo();

		// 採番タイプが連番以外をマスタ設定している場合は、設定中の採番カウントを更新する
		if (!AccgNoNumberingType.SEQ.equalsByCode(numberingType)) {
			// 採番タイプ（設定中）を取得
			TAccgDocLastUsedNumberEntity tAccgDocLastUsedNumberEntityTypeSelected = tAccgDocLastUsedNumberDao.selectByType(accgDocType, numberingType);

			if (tAccgDocLastUsedNumberEntityTypeSelected == null) {
				// 新規登録
				tAccgDocLastUsedNumberEntityTypeSelected = new TAccgDocLastUsedNumberEntity();
				tAccgDocLastUsedNumberEntityTypeSelected.setAccgDocType(accgDocType);
				tAccgDocLastUsedNumberEntityTypeSelected.setNumberingType(numberingType);
				tAccgDocLastUsedNumberEntityTypeSelected.setNumberingLastNo(1L);
				tAccgDocLastUsedNumberEntityTypeSelected.setNumberingLastDate(LocalDate.now());
				int insertCount = tAccgDocLastUsedNumberDao.insert(tAccgDocLastUsedNumberEntityTypeSelected);
				if (insertCount != 1) {
					// 登録処理に失敗した場合
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
					throw new AppException(MessageEnum.MSG_E00012, null);
				}
			} else {
				// 更新（カウントアップ）
				LocalDate now = LocalDate.now();
				LocalDate numberingLastDate = tAccgDocLastUsedNumberEntityTypeSelected.getNumberingLastDate();
				Long updateNo = tAccgDocLastUsedNumberEntityTypeSelected.getNumberingLastNo();

				switch (AccgNoNumberingType.of(numberingType)) {
				case SEQ:
					// 何もしない
					break;
				case YEAR_DELIMITER:
					// 年の場合、年が変わっているかチェック
					if (numberingLastDate.getYear() != now.getYear()) {
						// 年が変わっているため、1番目に戻る
						updateNo = 0L;
					}
					break;
				case MONTH_DELIMITER:
					// 月の場合、年月が変わっているかチェック
					if (DateUtils.isCorrectTimeContextYearMonth(numberingLastDate, now, true)) {
						// 月が変わっているため、1番目に戻る
						updateNo = 0L;
					}
					break;
				case DAYS_DELIMITER:
					// 日の場合、日が変わっているかチェック
					if (DateUtils.isCorrectDate(numberingLastDate, now, true)) {
						// 日が変わっているため、1番目に戻る
						updateNo = 0L;
					}
					break;
				default:
					// 何もしない
					break;
				}
				tAccgDocLastUsedNumberEntityTypeSelected.setNumberingLastNo(++updateNo);
				tAccgDocLastUsedNumberEntityTypeSelected.setNumberingLastDate(now);
				tAccgDocLastUsedNumberDao.update(tAccgDocLastUsedNumberEntityTypeSelected);
			}
			// 設定している採番タイプの最終採番番号を設定
			useLastUsedNumber = tAccgDocLastUsedNumberEntityTypeSelected.getNumberingLastNo();
		}

		return useLastUsedNumber;

	}

	/**
	 * 名簿ID、案件IDに紐づく回収不能データを更新します。<br>
	 * 紐づく回収不能詳細データの回収不能額を合計し回収不能テーブルに反映。<br>
	 * 紐づく回収不能詳細データがない場合は回収不能データを削除します。<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	private void updateOrDeleteUncollectible(Long personId, Long ankenId) throws AppException {
		// 回収不能金データ取得
		TUncollectibleEntity tUncollectibleEntity = tUncollectibleDao.selectUncollectibleByPersonIdAndAnkenId(personId, ankenId);
		if (tUncollectibleEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 回収不能SEQ取得
		Long uncollectibleSeq = tUncollectibleEntity.getUncollectibleSeq();

		// 回収不能SEQに紐づく回収不能詳細データを取得
		List<TUncollectibleDetailEntity> tUncollectibleDetailEntityList = tUncollectibleDetailDao.selectUncollectibleDetailByUncollectibleSeq(uncollectibleSeq);
		if (LoiozCollectionUtils.isNotEmpty(tUncollectibleDetailEntityList)) {
			// 詳細データがあれば、回収不能額合計を回収不能金テーブルに反映する
			List<BigDecimal> uncollectibleAmountList = tUncollectibleDetailEntityList.stream().map(entity -> entity.getUncollectibleAmount()).collect(Collectors.toList());
			BigDecimal totalUncollectibleAmount = AccountingUtils.calcTotal(uncollectibleAmountList);
			tUncollectibleEntity.setTotalUncollectibleAmount(totalUncollectibleAmount);
			int updateCount = 0;
			try {
				updateCount = tUncollectibleDao.update(tUncollectibleEntity);
			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
			if (updateCount != 1) {
				// 更新に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00013, null);
			}
		} else {
			// 詳細データがないので、回収不能金データを削除する
			int deleteUncollectibleCount = 0;
			try {
				deleteUncollectibleCount = tUncollectibleDao.delete(tUncollectibleEntity);
			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
			if (deleteUncollectibleCount != 1) {
				// 削除処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00014, null);
			}
		}
	}

	// =========================================================================
	// ▼ 登録／更新／削除の実行メソッド（ロジックをほぼ含まないもの）
	// =========================================================================

	/**
	 * 請求預り金項目テーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocInvoiceDepositEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocInvoiceDeposit(List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList) throws AppException {
		int[] deleteAccgDocInvoiceDepositCount = null;
		try {
			deleteAccgDocInvoiceDepositCount = tAccgDocInvoiceDepositDao.batchDelete(tAccgDocInvoiceDepositEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceDepositEntityList.size() != deleteAccgDocInvoiceDepositCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 請求項目テーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocInvoiceEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocInvoice(List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList) throws AppException {
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
	 * 請求項目-消費税テーブルの登録をおこないます
	 * 
	 * @param tAccgInvoiceTaxEntity
	 * @return
	 * @throws AppException
	 */
	private TAccgInvoiceTaxEntity insertAccgInvoiceTax(TAccgInvoiceTaxEntity tAccgInvoiceTaxEntity) throws AppException {
		int insertCount = tAccgInvoiceTaxDao.insert(tAccgInvoiceTaxEntity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return tAccgInvoiceTaxEntity;
	}

	/**
	 * 請求項目-源泉徴収テーブルの登録をおこないます
	 * 
	 * @param tAccgInvoiceWithholdingEntity
	 * @throws AppException
	 */
	private void insertAccgInvoiceWithholding(TAccgInvoiceWithholdingEntity tAccgInvoiceWithholdingEntity) throws AppException {
		int insertCount = tAccgInvoiceWithholdingDao.insert(tAccgInvoiceWithholdingEntity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 請求項目テーブルの一括登録をおこないます
	 * 
	 * @param accgDocInvoiceEntityList
	 * @return
	 * @throws AppExcpetion
	 */
	private List<TAccgDocInvoiceEntity> batchInsertTAccgDocInvoice(List<TAccgDocInvoiceEntity> accgDocInvoiceEntityList) throws AppException {
		int[] insertAccgDocInvoiceCount = tAccgDocInvoiceDao.batchInsert(accgDocInvoiceEntityList);
		if (accgDocInvoiceEntityList.size() != insertAccgDocInvoiceCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return accgDocInvoiceEntityList;
	}

	/**
	 * 請求報酬項目テーブルの一括登録をおこないます
	 * 
	 * @param accgDocInvoiceFeeEntityList
	 * @return
	 * @throws AppException
	 */
	private List<TAccgDocInvoiceFeeEntity> batchInsertAccgDocInvoiceFee(List<TAccgDocInvoiceFeeEntity> accgDocInvoiceFeeEntityList) throws AppException {
		int[] insertAccgDocInvoiceFeeCount = tAccgDocInvoiceFeeDao.batchInsert(accgDocInvoiceFeeEntityList);
		if (accgDocInvoiceFeeEntityList.size() != insertAccgDocInvoiceFeeCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return accgDocInvoiceFeeEntityList;
	}

	/**
	 * 請求項目-消費税テーブルを更新します
	 * 
	 * @param tAccgInvoiceTaxEntity
	 * @throws AppException
	 */
	private void updateAccgInvoiceTax(TAccgInvoiceTaxEntity tAccgInvoiceTaxEntity) throws AppException {
		int updateCount = 0;
		try {
			updateCount = tAccgInvoiceTaxDao.update(tAccgInvoiceTaxEntity);
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
	 * 請求項目-源泉徴収テーブルの更新をおこないます
	 * 
	 * @param tAccgInvoiceWithholdingEntity
	 * @throws AppException
	 */
	private void updateAccgInvoiceWithholding(TAccgInvoiceWithholdingEntity tAccgInvoiceWithholdingEntity) throws AppException {
		int updateCount = 0;
		try {
			updateCount = tAccgInvoiceWithholdingDao.update(tAccgInvoiceWithholdingEntity);
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
	 * 報酬テーブルの一括更新をおこないます
	 * 
	 * @param tFeeEntityList
	 * @throws AppException
	 */
	private void batchUpdateTFee(List<TFeeEntity> tFeeEntityList) throws AppException {
		int[] updateFeeCount = null;
		try {
			updateFeeCount = tFeeDao.batchUpdate(tFeeEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tFeeEntityList.size() != updateFeeCount.length) {
			// 更新に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 会計書類ファイルの一括更新をおこないます
	 * 
	 * @param tAccgDocFileEntityList
	 * @throws AppException
	 */
	private void batchUpdateTAccgDocFile(List<TAccgDocFileEntity> tAccgDocFileEntityList) throws AppException {
		int[] updateCount = null;
		try {
			updateCount = tAccgDocFileDao.batchUpdate(tAccgDocFileEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocFileEntityList.size() != updateCount.length) {
			// 更新に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 請求項目-消費税テーブルの削除をおこないます
	 * 
	 * @param tAccgInvoiceTaxEntity
	 * @throws AppException
	 */
	private void deleteAccgInvoiceTax(TAccgInvoiceTaxEntity tAccgInvoiceTaxEntity) throws AppException {
		int deleteCount = 0;
		try {
			deleteCount = tAccgInvoiceTaxDao.delete(tAccgInvoiceTaxEntity);
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
	 * 取引実績テーブルの削除をおこないます
	 * 
	 * @param tAccgRecordEntity
	 * @throws AppException
	 */
	private void deleteTAccgRecord(TAccgRecordEntity tAccgRecordEntity) throws AppException {
		if (tAccgRecordEntity == null) {
			return;
		}

		int deleteCount = 0;
		try {
			deleteCount = tAccgRecordDao.delete(tAccgRecordEntity);
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
	 * 請求項目-源泉徴収テーブルの登録をおこないます
	 * 
	 * @param tAccgInvoiceWithholdingEntity
	 * @throws AppException
	 */
	private void deleteAccgInvoiceWithholding(TAccgInvoiceWithholdingEntity tAccgInvoiceWithholdingEntity) throws AppException {
		int deleteCount = 0;
		try {
			deleteCount = tAccgInvoiceWithholdingDao.delete(tAccgInvoiceWithholdingEntity);
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
	 * 売上明細SEQに紐づく売上明細データを削除します
	 * 
	 * @param salesDetailSeq
	 */
	private void deleteSalesDetail(Long salesDetailSeq) throws AppException {
		if (salesDetailSeq == null) {
			return;
		}
		TSalesDetailEntity tSalesDetailEntity = tSalesDetailDao.selectSalesDetailBySalesDetailSeq(salesDetailSeq);
		if (tSalesDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		int deleteCount = 0;
		try {
			deleteCount = tSalesDetailDao.delete(tSalesDetailEntity);
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
	 * 売上明細SEQに紐づく売上明細-消費税データを削除します
	 * 
	 * @param salesDetailSeq
	 * @throws AppException
	 */
	private void deleteSalesDetailTax(Long salesDetailSeq) throws AppException {
		// 売上明細-消費税データ取得
		List<TSalesDetailTaxEntity> tSalesDetailTaxEntityList = tSalesDetailTaxDao.selectSalesDetailTaxBySalesDetailSeq(salesDetailSeq);
		if (CollectionUtils.isEmpty(tSalesDetailTaxEntityList)) {
			return;
		}

		int[] deleteFeeCount = null;
		try {
			deleteFeeCount = tSalesDetailTaxDao.batchDelete(tSalesDetailTaxEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tSalesDetailTaxEntityList.size() != deleteFeeCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 回収不能詳細SEQに紐づく回収不能詳細データを削除します
	 * 
	 * @param uncollectibleDetailSeq
	 * @throws AppException
	 */
	private void deleteUncollectibleDetail(Long uncollectibleDetailSeq) throws AppException {
		if (uncollectibleDetailSeq == null) {
			return;
		}
		TUncollectibleDetailEntity tUncollectibleDetailEntity = tUncollectibleDetailDao.selectUncollectibleDetailByUncollectibleDetailSeq(uncollectibleDetailSeq);
		if (tUncollectibleDetailEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		int deleteCount = 0;
		try {
			deleteCount = tUncollectibleDetailDao.delete(tUncollectibleDetailEntity);
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
	 * 取引実績明細テーブルの一括削除をおこないます
	 * 
	 * @param tAccgRecordDetailEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgRecordDetail(List<TAccgRecordDetailEntity> tAccgRecordDetailEntityList) throws AppException {
		if (CollectionUtils.isEmpty(tAccgRecordDetailEntityList)) {
			return;
		}

		int[] deleteCount = null;
		try {
			deleteCount = tAccgRecordDetailDao.batchDelete(tAccgRecordDetailEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgRecordDetailEntityList.size() != deleteCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 取引実績明細-過入金テーブルの一括削除をおこないます
	 * 
	 * @param tAccgRecordDetailOverPaymentEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgRecordDetailOverPayment(List<TAccgRecordDetailOverPaymentEntity> tAccgRecordDetailOverPaymentEntityList) throws AppException {
		if (CollectionUtils.isEmpty(tAccgRecordDetailOverPaymentEntityList)) {
			return;
		}

		int[] deleteFeeCount = null;
		try {
			deleteFeeCount = tAccgRecordDetailOverPaymentDao.batchDelete(tAccgRecordDetailOverPaymentEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgRecordDetailOverPaymentEntityList.size() != deleteFeeCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

}