package jp.loioz.app.common.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.amazonaws.services.s3.model.S3Object;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BaseToInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.InvoiceRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm.DocActivityRowForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm.InvoiceRowViewForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm.RepayRowViewForm;
import jp.loioz.bean.AccgDocActSendBean;
import jp.loioz.bean.AccgDocFileBean;
import jp.loioz.bean.AccgDocInvoiceBean;
import jp.loioz.bean.AnkenTantoAccountBean;
import jp.loioz.bean.DepositRecvSummaryBean;
import jp.loioz.bean.FeeDetailListBean;
import jp.loioz.bean.RepayBean;
import jp.loioz.bean.TDepositRecvAndDocInvoiceDepositBean;
import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocActType;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.DepositRecvCreatedType;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.FeePaymentStatus;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.constant.CommonConstant.InvoiceDepositType;
import jp.loioz.common.constant.CommonConstant.InvoiceOtherItemType;
import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.NameEnd;
import jp.loioz.common.constant.CommonConstant.RecordType;
import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.FileUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozIOUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.ValidateUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAccgDocActDao;
import jp.loioz.dao.TAccgDocActSendDao;
import jp.loioz.dao.TAccgDocActSendFileDao;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAccgDocFileDao;
import jp.loioz.dao.TAccgDocFileDetailDao;
import jp.loioz.dao.TAccgDocInvoiceDao;
import jp.loioz.dao.TAccgDocInvoiceDepositDao;
import jp.loioz.dao.TAccgDocInvoiceDepositTDepositRecvMappingDao;
import jp.loioz.dao.TAccgDocInvoiceFeeDao;
import jp.loioz.dao.TAccgDocInvoiceOtherDao;
import jp.loioz.dao.TAccgDocRepayDao;
import jp.loioz.dao.TAccgDocRepayTDepositRecvMappingDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgInvoiceTaxDao;
import jp.loioz.dao.TAccgInvoiceWithholdingDao;
import jp.loioz.dao.TAccgRecordDao;
import jp.loioz.dao.TAccgRecordDetailDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dao.TFeeAddTimeChargeDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSalesDao;
import jp.loioz.dao.TSalesDetailDao;
import jp.loioz.dao.TSalesDetailTaxDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AccgInvoiceStatementAmountDto;
import jp.loioz.dto.FileContentsDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAccgDocActEntity;
import jp.loioz.entity.TAccgDocActSendEntity;
import jp.loioz.entity.TAccgDocActSendFileEntity;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAccgDocFileDetailEntity;
import jp.loioz.entity.TAccgDocFileEntity;
import jp.loioz.entity.TAccgDocInvoiceDepositEntity;
import jp.loioz.entity.TAccgDocInvoiceDepositTDepositRecvMappingEntity;
import jp.loioz.entity.TAccgDocInvoiceEntity;
import jp.loioz.entity.TAccgDocInvoiceFeeEntity;
import jp.loioz.entity.TAccgDocInvoiceOtherEntity;
import jp.loioz.entity.TAccgDocRepayEntity;
import jp.loioz.entity.TAccgDocRepayTDepositRecvMappingEntity;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgInvoiceTaxEntity;
import jp.loioz.entity.TAccgInvoiceWithholdingEntity;
import jp.loioz.entity.TAccgRecordDetailEntity;
import jp.loioz.entity.TAccgRecordEntity;
import jp.loioz.entity.TAccgStatementEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TDepositRecvEntity;
import jp.loioz.entity.TFeeAddTimeChargeEntity;
import jp.loioz.entity.TFeeEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSalesDetailEntity;
import jp.loioz.entity.TSalesDetailTaxEntity;
import jp.loioz.entity.TSalesEntity;

/**
 * 会計管理：請求書詳細、精算書詳細の共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonAccgInvoiceStatementService extends DefaultService {

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

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

	/** 会計書類対応-送付-ファイルDaoクラス */
	@Autowired
	private TAccgDocActSendFileDao tAccgDocActSendFileDao;

	/** 請求項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceDao tAccgDocInvoiceDao;

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

	/** タイムチャージ-報酬付帯情報Daoクラス */
	@Autowired
	private TFeeAddTimeChargeDao tFeeAddTimeChargeDao;

	/** 請求項目-報酬Daoクラス */
	@Autowired
	private TAccgDocInvoiceFeeDao tAccgDocInvoiceFeeDao;

	/** 請求項目-預り金Daoクラス */
	@Autowired
	private TAccgDocInvoiceDepositDao tAccgDocInvoiceDepositDao;

	/** 請求その他項目Daoクラス */
	@Autowired
	private TAccgDocInvoiceOtherDao tAccgDocInvoiceOtherDao;

	/** 会計ファイル情報Daoクラス */
	@Autowired
	private TAccgDocFileDao tAccgDocFileDao;

	/** 会計ファイル詳細情報Daoクラス */
	@Autowired
	private TAccgDocFileDetailDao tAccgDocFileDetailDao;

	/** 既入金項目_預り金テーブルマッピング情報Daoクラス */
	@Autowired
	private TAccgDocRepayTDepositRecvMappingDao tAccgDocRepayTDepositRecvMappingDao;

	/** 請求項目-預り金（実費）_預り金テーブルマッピング情報Daoクラス */
	@Autowired
	private TAccgDocInvoiceDepositTDepositRecvMappingDao tAccgDocInvoiceDepositTDepositRecvMappingDao;

	/** 案件担当者情報用のDaoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 案件情報用のDao */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 顧客情報用のDao */
	@Autowired
	private TPersonDao tPersonDao;

	/** 共通：ファイルストレージサービスクラス */
	@Autowired
	private FileStorageService fileStorageService;

	/** アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 会計管理のサービスクラス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 会計管理の金額を扱う共通サービス */
	@Autowired
	private CommonAccgAmountService commonAccgAmountService;

	/** 共通：案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 共通：メッセージサービス */
	@Autowired
	private MessageService messageService;
	
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
	 * 対象の会計書類（請求書or精算書）で設定されている売上計上先のアカウントSEQを取得する
	 * 
	 * @param accgDocSeq
	 * @return 売上計上先のアカウントSEQ
	 */
	public Long getSalesAccountSeq(Long accgDocSeq) {
		
		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		Long salesAccountSeq = null;
		
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合

			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			salesAccountSeq = tAccgInvoiceEntity.getSalesAccountSeq();

		} else if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合

			// 精算書データ
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			salesAccountSeq = tAccgStatementEntity.getSalesAccountSeq();

		} else {
			// 想定外の入力値
			throw new RuntimeException("想定外のEnum値");
		}
		
		return salesAccountSeq;
	}
	
	/**
	 * 会計書類：印刷して送付画面表示用オブジェクトを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.FilePrintSendViewForm getFilePrintSendViewForm(Long accgDocSeq) throws DataNotFoundException {

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が見つかりません");
		}

		var viewForm = new AccgInvoiceStatementViewForm.FilePrintSendViewForm();

		viewForm.setAccgDocSeq(accgDocSeq);
		viewForm.setAccgDocType(AccgDocType.of(tAccgDocEntity.getAccgDocType()));

		return viewForm;
	}

	/**
	 * 会計書類BeanからS3ファイルを取得・Zip化
	 * FileContentsDtoを作成し、返却する
	 * 
	 * @param accgDocFileBeanList
	 * @return Zip化したファイル情報
	 */
	public FileContentsDto toZipFileContents(List<AccgDocFileBean> accgDocFileBeanList) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);) {

			for (AccgDocFileBean accgDocFileBean : accgDocFileBeanList) {

				String entryFileName = this.createAccgDocFileName(accgDocFileBean.getAccgDocSeq(),
						AccgDocFileType.of(accgDocFileBean.getAccgDocFileType()),
						FileExtension.ofExtension(accgDocFileBean.getFileExtension()));
				ZipEntry zipEntry = new ZipEntry(entryFileName);
				zos.putNextEntry(zipEntry);
				LoiozIOUtils.copy(fileStorageService.fileDownload(accgDocFileBean.getS3ObjectKey()).getObjectContent(), zos);
				zos.closeEntry();
			}

			// 明示的にZipOutputStreamを閉じる(try-with-resourcesのcloseだと、springが先にレスポンスを返してしまうのでzipファイルデータが欠損した状態になってしまう)
			zos.close();

			// ファイルコンテンツオブジェクトとして返却
			return new FileContentsDto(baos.toByteArray(), FileExtension.ZIP.getVal());
		} catch (Exception ex) {
			// 予期せぬエラー
			throw new RuntimeException("ファイルオブジェクトの生成時にエラーが発生しました。", ex);
		}
	}

	/**
	 * 名簿IDに紐づく請求先の名称、敬称、詳細情報を取得します
	 * 
	 * @param personId
	 * @return
	 */
	public BaseToInputForm getBaseToNameAndDetail(Long personId) {
		BaseToInputForm form = new BaseToInputForm();
		
		// 名簿情報取得
		TPersonEntity tPersonEntity = tPersonDao.selectPersonByPersonId(personId);
		if (tPersonEntity == null) {
			throw new DataNotFoundException("名簿情報が存在しません。[personId=" + personId + "]");
		}
		PersonName personName = PersonName.fromEntity(tPersonEntity);
		String baseToName = personName.getName();
		String baseToNameEnd = commonAccgService.personTypeToNameEnd(tPersonEntity.getCustomerType());
		String baseToDetail = commonAccgService.personInfoToDetail(tPersonEntity);

		form.setBaseToName(baseToName);
		form.setBaseToNameEnd(baseToNameEnd);
		form.setBaseToDetail(baseToDetail);
		
		return form;
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の既入金項目表示用情報を取得します。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.RepayViewForm getRepayViewForm(Long accgDocSeq) {
		// 返却用Form
		AccgInvoiceStatementViewForm.RepayViewForm form = new AccgInvoiceStatementViewForm.RepayViewForm();
		
		// 既入金情報の取得処理
		List<RepayBean> repayBeanList = tAccgDocRepayDao.selectRepayBeanListByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(repayBeanList)) {
			return form;
		}
		
		// 取得したデータから既入金合計額、RepayRowViewFormのリストを作成
		List<RepayRowViewForm> formList = repayBeanList.stream().map(e -> {
			RepayRowViewForm row = new RepayRowViewForm();
			row.setRepayTransactionDate(e.getRepayTransactionDate());
			row.setRepayItemName(e.getRepayItemName());
			row.setSumText(e.getSumText());
			row.setRepayAmount(AccountingUtils.toDispAmountLabel(e.getRepayAmount()));
			row.setDepositRecvSeqList(StringUtils.toListLong(e.getDepositRecvSeq()));
			return row;
		}).collect(Collectors.toList());
		
		// 合計金額
		List<BigDecimal> repayAmountList = repayBeanList.stream().map(RepayBean::getRepayAmount).collect(Collectors.toList());
		BigDecimal totalRepayAmount = AccountingUtils.calcTotal(repayAmountList);
		
		form.setRepayRowList(formList);
		form.setTotalRepayAmount(AccountingUtils.toDispAmountLabel(totalRepayAmount));
		return form;
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の既入金項目入力用情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementInputForm.RepayInputForm getRepayInputForm(Long accgDocSeq) throws DataNotFoundException {
		// 返却用form
		AccgInvoiceStatementInputForm.RepayInputForm form = new AccgInvoiceStatementInputForm.RepayInputForm();

		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		// 会計書類SEQ
		form.setAccgDocSeq(accgDocSeq);

		// 案件IDセット
		Long ankenId = tAccgDocEntity.getAnkenId();
		form.setAnkenId(ankenId);

		// 名簿IDセット
		Long personId = tAccgDocEntity.getPersonId();
		form.setPersonId(personId);

		// 請求書-印字フラグ（既入金）、既入金項目合算フラグ（既入金）セット
		String accgDocType = tAccgDocEntity.getAccgDocType();
		this.setRepayTransactionDatePrintFlgAndRepaySumFlg(form, accgDocSeq, accgDocType);

		// 既入金データを一覧表示用に、RepayRowInputFormのリスト作成
		boolean isRepaySumFlg = form.isRepaySumFlg();
		List<RepayRowInputForm> formList = this.getRepayRowInputFormList(accgDocSeq, isRepaySumFlg);
		form.setRepayRowList(formList);

		// 既入金合算時の既入金項目SEQを取得
		if (isRepaySumFlg && !LoiozCollectionUtils.isEmpty(formList)) {
			Long docRepaySeq = formList.get(0).getDocRepaySeq();
			form.setDocRepaySeqWhenSummed(docRepaySeq);
		}

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
		// 会計書類SEQ
		Long accgDocSeq = repayInputForm.getAccgDocSeq();
		
		// 会計書類の既入金項目合算フラグがONかどうか
		boolean isOnRepaySumFlg = repayInputForm.isRepaySumFlg();
		
		// 未使用の預り金データ取得
		List<TDepositRecvEntity> tDepositRecvEntityList = this.getUnusedDepositRecvByDepositRecv(depositRecvSeqList, accgDocSeq);
		
		// 既入金項目リストから空行を省く
		List<RepayRowInputForm> enabledRepayList = this.getEnabledRepayList(repayInputForm.getRepayRowList());
		repayInputForm.setRepayRowList(enabledRepayList);
		
		if (isOnRepaySumFlg) {
			// 既入金項目合算フラグがONの場合は、表示する預り金用に1つにまとめる。また、まとめた預り金を登録・更新処理用に個々にrowInputFormを作成する。

			// 画面既入金項目のまとめ表示用既入金情報を取得
			List<RepayRowInputForm> forDisplayRepayFormList = repayInputForm.getRepayRowList().stream()
					.filter(form -> !form.isDeleteFlg() && form.isRowRepaySumFlg()).collect(Collectors.toList());

			// まとめ表示で表示する既入金が複数ある
			if (forDisplayRepayFormList.size() > 1) {
				throw new RuntimeException("既入金項目まとめ表示状態で表示している既入金が複数存在します。");
			}

			if (LoiozCollectionUtils.isEmpty(forDisplayRepayFormList)) {
				// 既入金項目にまとめ表示用の既入金がなければ、新たにまとめ表示用のrowInputFormを作成し、登録・更新処理用に個々の預り金rowInputFormを作成する。
				List<RepayRowInputForm> formList = this.createSumRepayRowInputFormList(null, tDepositRecvEntityList, repayInputForm);
				return formList;
			} else {
				// 既入金項目にまとめ表示用の既入金があれば、表示中の既入金額と今回追加する預り金を加算し、表示中の登録・更新処理用rowInputFormリストに今回追加する預り金rowInputFormを作成し追加する。

				// 画面で表示していたまとめ用既入金情報取得
				RepayRowInputForm forDisplayRepayForm = forDisplayRepayFormList.get(0);
				
				List<RepayRowInputForm> formList = this.createSumRepayRowInputFormList(forDisplayRepayForm, tDepositRecvEntityList, repayInputForm);
				
				return formList;
			}
		} else {
			// 既入金項目合算フラグがOFFの場合は、預り金を個々にrowInputFormを作成する。

			// 返却用formリスト
			List<RepayRowInputForm> formList = new ArrayList<>();

			// 既存の登録・更新処理用の既入金を取得
			List<RepayRowInputForm> forProcessingRepayList = repayInputForm.getRepayRowList();
			Long repayRowCount = 0L;
			Optional<RepayRowInputForm> optRepayRowInputForm = forProcessingRepayList.stream()
					.collect(Collectors.maxBy(Comparator.comparing(RepayRowInputForm::getRepayRowCount)));
			if (optRepayRowInputForm.isPresent()) {
				// 画面で表示していた既入金をセット
				formList.addAll(forProcessingRepayList);
				repayRowCount = optRepayRowInputForm.get().getRepayRowCount() + 1;
			}

			// 追加分の預り金データを個々にrowInputFormを作成する。
			for (TDepositRecvEntity entity : tDepositRecvEntityList) {
				RepayRowInputForm row = new RepayRowInputForm();
				row.setAccgDocSeq(accgDocSeq);
				row.setDepositRecvSeqList(List.of(entity.getDepositRecvSeq()));
				row.setRepayAmount(AccountingUtils.toDispAmountLabel(entity.getDepositAmount()));
				row.setRepayItemName(entity.getDepositItemName());
				row.setRepayRowCount(repayRowCount++);
				row.setRepayTransactionDate(DateUtils.parseToString(entity.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				row.setSumText(entity.getSumText());
				row.setAddFlg(true);
				row.setRowRepaySumFlg(false);
				row.setDepositMadeFromAccgDocFlg(entity.getCreatedAccgDocSeq() == null ? false : true);
				row.setDisplayFlg(true);
				formList.add(row);
			}

			return formList;
		}
	}

	/**
	 * 既入金項目の一覧をdocRepayOrder昇順に並び替えます。
	 * 
	 * @param repayRowList
	 * @return
	 */
	public List<RepayRowInputForm> sortRepayList(List<RepayRowInputForm> repayRowList) {
		if (LoiozCollectionUtils.isEmpty(repayRowList)) {
			return repayRowList;
		}
		return repayRowList.stream().sorted(Comparator.comparing(RepayRowInputForm::getDocRepayOrder)).collect(Collectors.toList());
	}

	/**
	 * 既入金項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 
	 * @param repayInputForm
	 * @return
	 */
	public List<RepayRowInputForm> groupOrUngroupSimilarRepayItems(AccgInvoiceStatementInputForm.RepayInputForm repayInputForm) {
		// 既入金項目をまとめるかどうか
		boolean isRepaySumFlg = repayInputForm.isRepaySumFlg();
		
		// 画面表示中の既入金項目取得
		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();
		if (LoiozCollectionUtils.isEmpty(repayRowList) || repayRowList.stream()
				.filter(form -> form.isDeleteFlg() == false)
				.collect(Collectors.toList()).size() == 0) {
			// まとめるものが無い場合は、formの値をそのまま返却
			return repayInputForm.getRepayRowList();
		}
		
		// 空行を省いた既入金項目リストをformにセット
		repayInputForm.setRepayRowList(repayRowList);

		if (isRepaySumFlg) {
			// 既入金項目をまとめる場合、削除していない既入金データの金額を集計して、まとめ表示用行を作成する。削除フラグのたっていない行の表示フラグをfalseにする。
			List<RepayRowInputForm> formList = this.createSumRepayRowInputFormList(null, Collections.emptyList(), repayInputForm);
			return formList;
		} else {
			// 既入金項目のまとめを解除する場合、新規のまとめ表示行は削除、既存のまとめ表示行そのまま、削除フラグの立っていない個々の行は表示フラグをtrueにする。
			List<RepayRowInputForm> formList = repayRowList.stream().filter(form -> !(form.isAddFlg() && form.isRowRepaySumFlg())).collect(Collectors.toList());
			
			formList.stream().forEach(form -> {
				if (form.isRowRepaySumFlg() && !form.isDeleteFlg()) {
					form.setDisplayFlg(false);
				} else if (!form.isRowRepaySumFlg() && !form.isDeleteFlg()) {
					form.setDisplayFlg(true);
				} else {
					// 何もしない
				}
			});
			
			return formList;
		}
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求項目表示用情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public AccgInvoiceStatementViewForm.InvoiceViewForm getInvoiceViewForm(Long accgDocSeq) {
		// 請求書データ取得
		List<AccgDocInvoiceBean> beanList = tAccgDocInvoiceDao.selectAccgDocInvoiceBeanByAccgDocSeq(accgDocSeq);
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		AccgInvoiceStatementViewForm.InvoiceViewForm form = new AccgInvoiceStatementViewForm.InvoiceViewForm();

		if (LoiozCollectionUtils.isEmpty(beanList)) {
			return form;
		}

		// 取得したデータから、1件ずつInvoiceRowViewFormを作成しリストにする
		List<InvoiceRowViewForm> formList = new ArrayList<>();
		for (AccgDocInvoiceBean bean : beanList) {
			InvoiceRowViewForm row = new InvoiceRowViewForm();
			// 報酬項目
			if (bean.getDocInvoiceFeeSeq() != null) {
				row.setAmount(AccountingUtils.toDispAmountLabel(bean.getFeeAmount()));
				row.setItemName(bean.getFeeItemName());
				row.setTransactionDate(bean.getFeeTransactionDate());
				row.setSumText(bean.getFeeSumText());
			}

			// 預り金項目
			if (bean.getDocInvoiceDepositSeq() != null) {
				row.setAmount(AccountingUtils.toDispAmountLabel(bean.getDepositAmount()));
				row.setItemName(bean.getDepositItemName());
				row.setTransactionDate(bean.getDepositTransactionDate());
				row.setSumText(bean.getDepositSumText());
				row.setDepositRecvSeqList(StringUtils.toListLong(bean.getDepositRecvSeq()));
			}

			// その他項目
			if (bean.getDocInvoiceOtherSeq() != null) {
				if (InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType())) {
					// 値引きの表示は金額にマイナスを付与する
					row.setAmount(bean.getOtherAmount() == null ? "" : "-" + AccountingUtils.toDispAmountLabel(bean.getOtherAmount()));
				} 
				row.setItemName(bean.getOtherItemName());
				row.setTransactionDate(bean.getOtherTransactionDate());
				row.setSumText(bean.getOtherSumText());
			}
			formList.add(row);
		}
		form.setInvoiceRowList(formList);

		// 税率8%の対象額（報酬）
		BigDecimal target8Fee = accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFee();
		form.setTarget8(AccountingUtils.toDispAmountLabel(target8Fee));

		// 税率10%の対象額（報酬）
		BigDecimal target10Fee = accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFee();
		form.setTarget10(AccountingUtils.toDispAmountLabel(target10Fee));

		// 小計
		BigDecimal subTotal = accgInvoiceStatementAmountDto.getSubTotal();
		form.setSubTotal(AccountingUtils.toDispAmountLabel(subTotal));

		// 税率8%の消費税
		BigDecimal totalTaxAmount8 = accgInvoiceStatementAmountDto.getTotalTaxAmount8();
		form.setTarget8Tax(AccountingUtils.toDispAmountLabel(totalTaxAmount8));

		// 税率10%の消費税
		BigDecimal totalTaxAmount10 = accgInvoiceStatementAmountDto.getTotalTaxAmount10();
		form.setTarget10Tax(AccountingUtils.toDispAmountLabel(totalTaxAmount10));

		// 消費税
		BigDecimal tax = accgInvoiceStatementAmountDto.getTotalTaxAmount();
		form.setTax(AccountingUtils.toDispAmountLabel(tax));

		// 源泉徴収額の合計
		BigDecimal totalWithholdingAmount = accgInvoiceStatementAmountDto.getTotalWithholdingAmount();
		form.setWithholding(AccountingUtils.toDispAmountLabel(totalWithholdingAmount));

		// 合計
		BigDecimal total = accgInvoiceStatementAmountDto.getTotalAmount();
		form.setTotal(AccountingUtils.toDispAmountLabel(total));

		return form;
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求項目入力用情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementInputForm.InvoiceInputForm getInvoiceInputForm(Long accgDocSeq) throws DataNotFoundException {
		// 返却用Form
		AccgInvoiceStatementInputForm.InvoiceInputForm form = new AccgInvoiceStatementInputForm.InvoiceInputForm();

		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// 会計書類SEQ
		form.setAccgDocSeq(accgDocSeq);

		// 案件IDセット
		form.setAnkenId(tAccgDocEntity.getAnkenId());

		// 名簿IDセット
		form.setPersonId(tAccgDocEntity.getPersonId());

		// 取引日-印字フラグ（請求）
		boolean invoiceTransactionDatePrintFlg = false;

		// 請求項目合算フラグ（請求）
		boolean expenseSumFlg = false;

		// ドキュメントタイプ
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合

			// 請求書データ
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			if (tAccgInvoiceEntity != null) {
				invoiceTransactionDatePrintFlg = SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getInvoiceTransactionDatePrintFlg());
				expenseSumFlg = SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getExpenseSumFlg());
			}

		} else if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合

			// 精算書データ
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			if (tAccgStatementEntity != null) {
				invoiceTransactionDatePrintFlg = SystemFlg.FLG_ON.equalsByCode(tAccgStatementEntity.getInvoiceTransactionDatePrintFlg());
				expenseSumFlg = SystemFlg.FLG_ON.equalsByCode(tAccgStatementEntity.getExpenseSumFlg());
			}

		} else {
			// 想定外の入力値
			throw new RuntimeException("想定外のEnum値");
		}

		// 取引日-印字フラグ（請求）
		form.setInvoiceTransactionDatePrintFlg(invoiceTransactionDatePrintFlg);
		
		// 表示項目まとめフラグ
		form.setExpenseSumFlg(expenseSumFlg);
		
		// 請求項目、金額データの取得
		
		// 請求項目データ
		List<InvoiceRowInputForm> formList = this.getInvoiceRowInputFormList(accgDocSeq, expenseSumFlg);
		if (LoiozCollectionUtils.isEmpty(formList)) {
			// 請求項目データがない場合
			// 空の合計額フラグメント用フォームをセット
			AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm invoiceTotalAmountInputForm = new AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm();
			form.setInvoiceTotalAmountInputForm(invoiceTotalAmountInputForm);
			return form;
		}
		form.setInvoiceRowList(formList);
		
		// 合算時の請求項目SEQ、請求預り金項目SEQを取得
		List<InvoiceRowInputForm> expenseList = formList.stream()
				.filter(row -> row.isDepositRecvFlg())
				.filter(row -> InvoiceDepositType.EXPENSE.equalsByCode(row.getInvoieType()))
				.collect(Collectors.toList());
		if (expenseSumFlg && expenseList.size() > 0) {
			Long docInvoiceSeq = expenseList.get(0).getDocInvoiceSeq();
			Long docInvoiceDepositSeq = expenseList.get(0).getDocInvoiceDepositSeq();
			form.setDocInvoiceSeqWhenSummed(docInvoiceSeq);
			form.setDocInvoiceDepositSeqWhenSummed(docInvoiceDepositSeq);
		}
		
		// 金額データ
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// 合計額フラグメント用フォーム
		AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm invoiceTotalAmountInputForm = new AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm();

		// 税率8%の対象額（報酬）
		BigDecimal target8Fee = accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFee();
		invoiceTotalAmountInputForm.setTarget8(AccountingUtils.toDispAmountLabel(target8Fee));

		// 税率10%の対象額（報酬）
		BigDecimal target10Fee = accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFee();
		invoiceTotalAmountInputForm.setTarget10(AccountingUtils.toDispAmountLabel(target10Fee));

		// 小計
		BigDecimal subTotal = accgInvoiceStatementAmountDto.getSubTotal();
		invoiceTotalAmountInputForm.setSubTotal(AccountingUtils.toDispAmountLabel(subTotal));

		// 税率8%の消費税
		BigDecimal totalTaxAmount8 = accgInvoiceStatementAmountDto.getTotalTaxAmount8();
		invoiceTotalAmountInputForm.setTarget8Tax(AccountingUtils.toDispAmountLabel(totalTaxAmount8));

		// 税率10%の消費税
		BigDecimal totalTaxAmount10 = accgInvoiceStatementAmountDto.getTotalTaxAmount10();
		invoiceTotalAmountInputForm.setTarget10Tax(AccountingUtils.toDispAmountLabel(totalTaxAmount10));

		// 消費税
		BigDecimal tax = accgInvoiceStatementAmountDto.getTotalTaxAmount();
		invoiceTotalAmountInputForm.setTax(AccountingUtils.toDispAmountLabel(tax));

		// 源泉徴収額の合計
		BigDecimal totalWithholdingAmount = accgInvoiceStatementAmountDto.getTotalWithholdingAmount();
		invoiceTotalAmountInputForm.setWithholding(AccountingUtils.toDispAmountLabel(totalWithholdingAmount));

		// 合計
		BigDecimal total = accgInvoiceStatementAmountDto.getTotalAmount();
		invoiceTotalAmountInputForm.setTotal(AccountingUtils.toDispAmountLabel(total));

		form.setInvoiceTotalAmountInputForm(invoiceTotalAmountInputForm);

		return form;
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求項目合計額入力用情報を取得します
	 *  
	 * @param accgDocSeq
	 * @param invoiceRowInputFormList
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm getInvoiceTotalAmountInputForm(Long accgDocSeq, List<InvoiceRowInputForm> invoiceRowInputFormList) {
		// 返却用Form
		AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm invoiceTotalAmountInputForm = new AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm();

		// 請求項目行データから金額算出
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(invoiceRowInputFormList, accgDocSeq);

		// 税率8%の対象額（報酬）
		BigDecimal target8Fee = accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFee();
		invoiceTotalAmountInputForm.setTarget8(AccountingUtils.toDispAmountLabel(target8Fee));

		// 税率10%の対象額（報酬）
		BigDecimal target10Fee = accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFee();
		invoiceTotalAmountInputForm.setTarget10(AccountingUtils.toDispAmountLabel(target10Fee));

		// 小計
		BigDecimal subTotal = accgInvoiceStatementAmountDto.getSubTotal();
		invoiceTotalAmountInputForm.setSubTotal(AccountingUtils.toDispAmountLabel(subTotal));

		// 税率8%の消費税
		BigDecimal totalTaxAmount8 = accgInvoiceStatementAmountDto.getTotalTaxAmount8();
		invoiceTotalAmountInputForm.setTarget8Tax(AccountingUtils.toDispAmountLabel(totalTaxAmount8));

		// 税率10%の消費税
		BigDecimal totalTaxAmount10 = accgInvoiceStatementAmountDto.getTotalTaxAmount10();
		invoiceTotalAmountInputForm.setTarget10Tax(AccountingUtils.toDispAmountLabel(totalTaxAmount10));

		// 消費税
		BigDecimal tax = accgInvoiceStatementAmountDto.getTotalTaxAmount();
		invoiceTotalAmountInputForm.setTax(AccountingUtils.toDispAmountLabel(tax));

		// 源泉徴収額の合計
		BigDecimal totalWithholdingAmount = accgInvoiceStatementAmountDto.getTotalWithholdingAmount();
		invoiceTotalAmountInputForm.setWithholding(AccountingUtils.toDispAmountLabel(totalWithholdingAmount));

		// 合計
		BigDecimal total = accgInvoiceStatementAmountDto.getTotalAmount();
		invoiceTotalAmountInputForm.setTotal(AccountingUtils.toDispAmountLabel(total));

		return invoiceTotalAmountInputForm;
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
		// 会計書類SEQ
		Long accgDocSeq = invoiceInputForm.getAccgDocSeq();
		
		// 会計書類の請求項目合算フラグがONかどうか
		boolean isOnExpenseSumFlg = invoiceInputForm.isExpenseSumFlg();
		
		// 未使用の預り金データ取得
		List<TDepositRecvEntity> tDepositRecvEntityList = this.getUnusedDepositRecvByDepositRecv(depositRecvSeqList, accgDocSeq);
		
		// 請求項目リストから空行を省く
		List<InvoiceRowInputForm> enabledInvoiceList = this.getEnabledInvoiceList(invoiceInputForm.getInvoiceRowList());
		invoiceInputForm.setInvoiceRowList(enabledInvoiceList);
		
		if (isOnExpenseSumFlg) {
			// 請求項目合算フラグがONの場合は、実費を1つにまとめる。また、まとめた実費を登録・更新処理用に個々にrowInputFormを作成する。
			
			// 画面請求項目まとめ表示用の実費情報を取得
			List<InvoiceRowInputForm> forDisplayInvoiceFormList = invoiceInputForm.getInvoiceRowList().stream()
					.filter(form -> !form.isDeleteFlg() && form.isRowExpenseSumFlg()).collect(Collectors.toList());
			
			// まとめ表示で表示する実費が複数ある
			if (forDisplayInvoiceFormList.size() > 1) {
				throw new RuntimeException("請求項目まとめ表示状態で表示している実費が複数存在します。");
			}
			
			if (LoiozCollectionUtils.isEmpty(forDisplayInvoiceFormList)) {
				// 請求項目にまとめ表示用の実費がなければ、新たにまとめ表示用のrowInputFormを作成し、登録・更新処理用に個々の預り金rowInputFormを作成する。
				List<InvoiceRowInputForm> formList = createSumInvoiceRowInputFormList(null, tDepositRecvEntityList, invoiceInputForm);
				return formList;
			} else {
				// 請求項目にまとめ表示用の実費があれば、表示中の実費と今回追加する預り金を加算し、表示中の登録・更新処理用rowInputFormリストに今回追加する預り金rowInputFormを作成し追加する。
				
				// 画面で表示していたまとめ用実費情報取得
				InvoiceRowInputForm forDisplayInoivceForm = forDisplayInvoiceFormList.get(0);
				
				List<InvoiceRowInputForm> formList = createSumInvoiceRowInputFormList(forDisplayInoivceForm, tDepositRecvEntityList, invoiceInputForm);
				return formList;
			}
		} else {
			// 請求項目合算フラグがOFFの場合は、預り金を個々にrowInputFormを作成する。
			
			// 返却用formリスト
			List<InvoiceRowInputForm> formList = new ArrayList<>();
			
			// 既存の登録・更新処理用の請求項目情報を取得
			List<InvoiceRowInputForm> forProcessingInvoiceList = invoiceInputForm.getInvoiceRowList();
			Long invoiceRowCount = 0L;
			Optional<InvoiceRowInputForm> optInvoiceRowInputForm = forProcessingInvoiceList.stream()
					.collect(Collectors.maxBy(Comparator.comparing(InvoiceRowInputForm::getInvoiceRowCount)));
			if (optInvoiceRowInputForm.isPresent()) {
				// 画面で表示していた請求項目をセット
				formList.addAll(forProcessingInvoiceList);
				invoiceRowCount = optInvoiceRowInputForm.get().getInvoiceRowCount() + 1;
			}
			
			// 追加分の預り金データを個々にrowInputFormを作成する。
			for (TDepositRecvEntity entity : tDepositRecvEntityList) {
				InvoiceRowInputForm row = new InvoiceRowInputForm();
				row.setAccgDocSeq(accgDocSeq);
				row.setDepositRecvSeqList(List.of(entity.getDepositRecvSeq()));
				row.setAmount(AccountingUtils.toDispAmountLabel(entity.getWithdrawalAmount()));
				row.setItemName(entity.getDepositItemName());
				row.setInvoiceRowCount(invoiceRowCount++);
				row.setTransactionDate(DateUtils.parseToString(entity.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				row.setSumText(entity.getSumText());
				row.setInvoieType(InvoiceDepositType.EXPENSE.getCd());
				row.setDepositRecvFlg(true);
				row.setAddFlg(true);
				row.setRowExpenseSumFlg(false);
				row.setDisplayFlg(true);
				formList.add(row);
			}
			
			return formList;
		}
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
		InvoiceRowInputForm row = new InvoiceRowInputForm();
		row.setAccgDocSeq(accgDocSeq);
		row.setTaxRateType(TaxRate.TEN_PERCENT.getCd());
		row.setFeeFlg(true);
		row.setAddFlg(true);
		row.setInvoiceRowCount(invoiceRowCount);
		row.setDisplayFlg(true);
		
		return row;
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
		// 返却用Form
		List<InvoiceRowInputForm> invoiceRowInputFormList = new ArrayList<>();
		
		// 報酬データ取得
		List<FeeDetailListBean> beanList = tFeeDao.selectFeeDetailByFeeSeqList(unPaidFeeSeqList);
		if (LoiozCollectionUtils.isEmpty(beanList) || beanList.size() != unPaidFeeSeqList.size()) {
			throw new DataNotFoundException("報酬情報が存在しません。[unPaidFeeSeqList=" + unPaidFeeSeqList + "]");
		}
		
		for (FeeDetailListBean bean : beanList) {
			InvoiceRowInputForm row = new InvoiceRowInputForm();
			row.setAccgDocSeq(accgDocSeq);
			row.setAmount(AccountingUtils.toDispAmountLabel(bean.getFeeAmount()));
			row.setDocInvoiceSeq(null);
			row.setFeeSeq(bean.getFeeSeq());
			row.setFeeTimeChargeFlg(SystemFlg.FLG_ON.equalsByCode(bean.getFeeTimeChargeFlg()));
			row.setInvoieType(null);
			row.setItemName(bean.getFeeItemName());
			row.setSumText(bean.getSumText());
			row.setTaxRateType(bean.getTaxRateType());
			row.setHourPrice(AccountingUtils.toDispAmountLabel(bean.getHourPrice()));
			row.setWorkTimeMinute(bean.getWorkTimeMinute());
			row.setTransactionDate(DateUtils.parseToString(bean.getFeeDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			row.setWithholdingFlg(SystemFlg.FLG_ON.equalsByCode(bean.getWithholdingFlg()));
			row.setInvoiceRowCount(invoiceRowCount++);
			row.setUnPaidFeeFlg(true);
			row.setDisplayFlg(true);
			
			invoiceRowInputFormList.add(row);
		}
		
		return invoiceRowInputFormList;
	}

	/**
	 * 請求項目入力の預り金用Formを新規作成します。
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewDepositRecvRowInputForm(Long accgDocSeq, Long invoiceRowCount) {
		// 請求項目入力の預り金用Form新規作成
		AccgInvoiceStatementInputForm.InvoiceRowInputForm row = new AccgInvoiceStatementInputForm.InvoiceRowInputForm();
		row.setAccgDocSeq(accgDocSeq);
		row.setInvoiceRowCount(invoiceRowCount);
		row.setInvoieType(InvoiceDepositType.DEPOSIT.getCd());
		row.setDepositRecvFlg(true);
		row.setAddFlg(true);
		row.setDisplayFlg(true);
		
		return row;
	}

	/**
	 * 新規追加用の請求項目-値引きのInputFormを作成する
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewDiscountRowInputForm(Long accgDocSeq, Long invoiceRowCount) {
		InvoiceRowInputForm row = new InvoiceRowInputForm();
		row.setAccgDocSeq(accgDocSeq);
		row.setTaxRateType(TaxRate.TEN_PERCENT.getCd());
		row.setAddFlg(true);
		row.setDiscountFlg(true);
		row.setInvoiceRowCount(invoiceRowCount);
		row.setInvoieType(InvoiceOtherItemType.DISCOUNT.getCd());
		row.setDisplayFlg(true);
		
		return row;
	}

	/**
	 * 新規追加用の請求項目-テキストのInputFormを作成する
	 * 
	 * @param accgDocSeq
	 * @param invoiceRowCount
	 * @return
	 */
	public AccgInvoiceStatementInputForm.InvoiceRowInputForm createNewTextRowInputForm(Long accgDocSeq, Long invoiceRowCount) {
		InvoiceRowInputForm row = new InvoiceRowInputForm();
		row.setAccgDocSeq(accgDocSeq);
		row.setTextFlg(true);
		row.setAddFlg(true);
		row.setInvoiceRowCount(invoiceRowCount);
		row.setInvoieType(InvoiceOtherItemType.TEXT.getCd());
		row.setDisplayFlg(true);
		
		return row;
	}

	/**
	 * 請求項目の一覧をdocInvoiceOrder昇順に並び替えます。
	 * 
	 * @param invoiceRowList
	 * @return
	 */
	public List<InvoiceRowInputForm> sortInvoiceList(List<InvoiceRowInputForm> invoiceRowList) {
		if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
			return invoiceRowList;
		}
		return invoiceRowList.stream().sorted(Comparator.comparing(InvoiceRowInputForm::getDocInvoiceOrder)).collect(Collectors.toList());
	}

	/**
	 * 請求項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 
	 * @param invoiceInputForm
	 * @return
	 */
	public List<InvoiceRowInputForm> groupOrUngroupSimilarInvoiceItems(
			AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) {
		// 請求項目をまとめるかどうか
		boolean isExpenseSumFlg = invoiceInputForm.isExpenseSumFlg();
		
		// 画面表示中の請求項目取得
		List<InvoiceRowInputForm> invoiceRowList = this.getEnabledInvoiceList(invoiceInputForm.getInvoiceRowList());
		if (LoiozCollectionUtils.isEmpty(invoiceRowList) || invoiceRowList.stream()
				.filter(form -> form.isDepositRecvFlg())
				.filter(form -> InvoiceDepositType.EXPENSE.equalsByCode(form.getInvoieType()))
				.filter(form -> form.isRowExpenseSumFlg() == false)
				.filter(form -> form.isDeleteFlg() == false)
				.collect(Collectors.toList()).size() == 0) {
			// まとめるものが無い場合は、formの値をそのまま返却
			return invoiceInputForm.getInvoiceRowList();
		}
		
		// 空行を省いた請求項目リストをformにセット
		invoiceInputForm.setInvoiceRowList(invoiceRowList);
		
		if (isExpenseSumFlg) {
			// 請求項目をまとめる場合、削除していない実費データの金額を集計して、まとめ表示用行を作成する。削除フラグのたっていない行の表示フラグをfalseにする。
			List<InvoiceRowInputForm> formList = this.createSumInvoiceRowInputFormList(null, Collections.emptyList(), invoiceInputForm);
			return formList;
		} else {
			// 請求項目のまとめを解除する場合、新規のまとめ表示行は削除、既存のまとめ表示行そのまま、削除フラグの立っていない個々の行は表示フラグをtrueにする。
			List<InvoiceRowInputForm> formList = invoiceRowList.stream()
					.filter(form -> !(form.isAddFlg() && form.isRowExpenseSumFlg())).collect(Collectors.toList());
			
			formList.stream().forEach(form -> {
				if (form.isRowExpenseSumFlg() && !form.isDeleteFlg()) {
					form.setDisplayFlg(false);
				} else if (!form.isRowExpenseSumFlg() && !form.isDeleteFlg()) {
					form.setDisplayFlg(true);
				} else {
					// 何もしない
				}
			});
			
			return formList;
		}
	}

	/**
	 * 請求書/精算書画面_実費明細タブ内画面表示用オブジェクトを取得
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.DipositRecordPdfViewForm getDipositRecordPdfViewForm(Long accgDocSeq) throws DataNotFoundException {

		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 会計書類データが存在しない場合
			throw new DataNotFoundException("会計書類データが存在しません。");
		}

		var dipositRecordPdfViewForm = new AccgInvoiceStatementViewForm.DipositRecordPdfViewForm();
		List<String> pngHtmlImgSrcList = getAccgPdfImgHtmlSrcList(accgDocSeq, AccgDocFileType.DEPOSIT_DETAIL);

		dipositRecordPdfViewForm.setAccgDocSeq(accgDocSeq);
		dipositRecordPdfViewForm.setAccgDocType(AccgDocType.of(tAccgDocEntity.getAccgDocType()));
		dipositRecordPdfViewForm.setDepositRecordPngSrc(pngHtmlImgSrcList);

		return dipositRecordPdfViewForm;
	}

	/**
	 * 各ファイル種別の現時点で表示する画像の生成元PDFのファイル詳細SEQを取得する
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @return
	 */
	public AccgDocFileBean getCurrentPdfAccgDocFileBean(Long accgDocSeq, AccgDocFileType accgDocFileType) {

		// 会計書類ファイルBean情報を取得する(送付済みを含む)
		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanByAccgDocSeqAndAccgDocFileType(accgDocSeq, accgDocFileType);

		// PDFファイルのみに絞り込み
		List<AccgDocFileBean> accgDocFilePdfBeans = accgDocFileBeans.stream().filter(e -> Objects.equals(e.getFileExtension(), FileExtension.PDF.getVal())).collect(Collectors.toList());

		if (LoiozCollectionUtils.isEmpty(accgDocFilePdfBeans)) {
			// 対象ファイル種別のPDFファイルが存在しない場合 -> nullを返却
			return null;
		}

		AccgDocFileBean currentPdfAccgDoocFileBean = null;

		// 未送付のデータを取得
		currentPdfAccgDoocFileBean = accgDocFilePdfBeans.stream().filter(e -> e.getAccgDocActSendFileSeq() == null).findFirst().orElse(null);
		if (currentPdfAccgDoocFileBean != null) {
			// 未送付のPDFデータが存在する場合は、そのPDFを現在のPDFとする
			return currentPdfAccgDoocFileBean;
		}

		// すべて送付済みの場合(会計書類対応送付SEQが最も新しいもの(SEQが大きいもの)が現在のPDFとする)
		currentPdfAccgDoocFileBean = accgDocFilePdfBeans.stream().max(Comparator.comparing(AccgDocFileBean::getAccgDocActSendFileSeq)).orElseThrow();
		return currentPdfAccgDoocFileBean;
	}

	/**
	 * 画面表示用会計書類PNGを取得する
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @return
	 */
	public List<String> getAccgPdfImgHtmlSrcList(Long accgDocSeq, AccgDocFileType accgDocFileType) {

		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanByAccgDocSeqAndAccgDocFileType(accgDocSeq, accgDocFileType);
		List<String> pngS3ObjectKeys = accgDocFileBeans.stream()
				.filter(e -> Objects.equals(e.getFileExtension(), FileExtension.PNG.getVal()))
				.sorted(Comparator.comparing(AccgDocFileBean::getFileBranchNo))
				.map(AccgDocFileBean::getS3ObjectKey)
				.collect(Collectors.toList());

		return getPngHtmlImgSrcListByS3ObjectKey(pngS3ObjectKeys);
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の進行状況情報表示用オブジェクトを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	public AccgInvoiceStatementViewForm.DocActivityForm getDocActivityForm(Long accgDocSeq) throws DataNotFoundException {

		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		AccgInvoiceStatementViewForm.DocActivityForm activityForm = new AccgInvoiceStatementViewForm.DocActivityForm();
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合

			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			activityForm.setIssueStatus(tAccgInvoiceEntity.getInvoiceIssueStatus());

		} else if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合

			// 精算書データ
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			activityForm.setIssueStatus(tAccgStatementEntity.getStatementIssueStatus());

		} else {
			// 想定外の入力値
			throw new RuntimeException("想定外のEnum値");
		}

		// 会計書類-対応情報を取得
		List<TAccgDocActEntity> accgDocActList = tAccgDocActDao.selectAccgDocActByAccgDocSeq(accgDocSeq);

		// 対応者のアカウント名Mapを作成
		Set<Long> actBySeqList = accgDocActList.stream().map(TAccgDocActEntity::getActBy).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
		List<MAccountEntity> mAccountEntities = mAccountDao.selectBySeq(List.copyOf(actBySeqList));

		// アカウント名Map
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap(mAccountEntities);

		// 「新規作成」対応のデータ
		List<DocActivityRowForm> newAccgDocActList = this.getNewAccgDocActList(accgDocActList, accountNameMap);
		// 「新規作成」を実行しているか
		boolean isCreate = this.getNewAccgDocActExecFlg(accgDocActList);

		// 「発行」対応のデータ
		List<DocActivityRowForm> issueAccgDocActList = this.getIssueAccgDocActList(accgDocActList, accountNameMap);
		// 「発行」を実行しているか
		boolean isIssue = this.getIssueAccgDocActExecFlg(accgDocActList);

		// 「送信」対応のデータ
		List<DocActivityRowForm> sendAccgDocActList = this.getSendAccgDocActList(accgDocActList, accountNameMap);
		// 「送信」を実行しているか
		boolean isSend = this.getSendAccgDocActExecFlg(accgDocActList);

		// formにセット
		activityForm.setNewDocActivityList(newAccgDocActList);
		activityForm.setCreated(isCreate);
		activityForm.setIssueDocActivityList(issueAccgDocActList);
		activityForm.setIssued(isIssue);
		activityForm.setSendDocActivityList(sendAccgDocActList);
		activityForm.setSend(isSend);

		return activityForm;
	}

	/**
	 * 案件ID、名簿IDに紐づく、入金額を超えた分の出金額を取得します
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	public BigDecimal getWithdrawalAmountInExcessOfDepositAmount(Long ankenId, Long personId) {
		BigDecimal withDrawalAmount = this.getDifferenceBetweenWithdrawalAndDeposit(ankenId, personId, true);
		return withDrawalAmount;
	}

	/**
	 * 請求書/精算書 の売上計上ユーザー候補のプルダウンを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public List<SelectOptionForm> generateAccgSalesOwnerOptionForm(Long accgDocSeq) {
		// 会計情報取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 会計書類情報の取得ができない場合 -> 空を返却
			return Collections.emptyList();
		}
		
		// 案件情報取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(tAccgDocEntity.getAnkenId());
		if (tAnkenEntity == null) {
			// 案件情報の取得ができない場合 -> 空を返却
			return Collections.emptyList();
		}
		
		// 案件担当取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(tAccgDocEntity.getAnkenId());
		// 案件担当SEQ一覧
		List<Long> ankenTantoAccountSeqList = ankenTantoAccountBeanList.stream().map(bean -> bean.getAccountSeq()).collect(Collectors.toList());
		
		// 請求書、精算書に設定してある売上計上先を取得し、案件担当者一覧に無い場合は追加する（ 売上計上先に登録していたアカウントを担当から外していた場合でも、売上計上先プルダウンに表示する）
		AccgDocType docType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (docType.isInvoice()) {
			// 請求書情報取得
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			// 請求の売上計上先アカウントSEQが案件担当に存在しない場合、「売上計上先」として追加
			if (tAccgInvoiceEntity != null
					&& tAccgInvoiceEntity.getSalesAccountSeq() != null
					&& !ankenTantoAccountSeqList.contains(tAccgInvoiceEntity.getSalesAccountSeq())) {
				AnkenTantoAccountBean bean = new AnkenTantoAccountBean();
				bean.setAccountSeq(tAccgInvoiceEntity.getSalesAccountSeq());
				bean.setTantoType(TantoType.SALES_OWNER.getCd());
				ankenTantoAccountBeanList.add(bean);
			}
		} else if (docType.isStatement()) {
			// 精算書情報取得
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			// 精算の売上計上先アカウントSEQが案件担当に存在しない場合、「売上計上先」として追加
			if (tAccgStatementEntity != null
					&& tAccgStatementEntity.getSalesAccountSeq() != null
					&& !ankenTantoAccountSeqList.contains(tAccgStatementEntity.getSalesAccountSeq())) {
				AnkenTantoAccountBean bean = new AnkenTantoAccountBean();
				bean.setAccountSeq(tAccgStatementEntity.getSalesAccountSeq());
				bean.setTantoType(TantoType.SALES_OWNER.getCd());
				ankenTantoAccountBeanList.add(bean);
			}
		} else {
			// 想定外のEnum値
			throw new RuntimeException("想定外のEnum値");
		}
		
		// アカウント取得
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();
		
		// 案件区分
		AnkenType ankenType = AnkenType.of(tAnkenEntity.getAnkenType());
		
		// 売上計上先プルダウン
		return commonAnkenService.getSalesOwnerOptionList(ankenType, ankenTantoAccountBeanList, accountEntityList);
	}

	/**
	 * 既入金項目リストの有効行だけを取得します。<br>
	 * 
	 * @param repayRowList
	 * @return
	 */
	public List<RepayRowInputForm> getEnabledRepayList(List<RepayRowInputForm> repayRowList) {
		if (LoiozCollectionUtils.isEmpty(repayRowList)) {
			return Collections.emptyList();
		}

		// 空行を省く
		List<RepayRowInputForm> enabledRepayList = repayRowList.stream().filter(
				// 新規追加したが、✕で削除した行の場合、
				// フロントのDOM上は削除しているが、残っているDOMのname属性について、Listのインデックス番号が歯抜けになることで、歯抜け分のinputFormが自動でListに追加されるため、それを除外する
				form -> !(form.isRowRepaySumFlg() == false && LoiozCollectionUtils.isEmpty(form.getDepositRecvSeqList())))
				.collect(Collectors.toList());

		return enabledRepayList;
	}
	
	/**
	 * 請求項目リストの有効行だけを取得します。<br>
	 * 
	 * @param invoiceRowList
	 * @return
	 */
	public List<InvoiceRowInputForm> getEnabledInvoiceList(List<InvoiceRowInputForm> invoiceRowList) {
		if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
			return Collections.emptyList();
		}
		
		//空行を省く
		List<InvoiceRowInputForm> enabledInvoiceList = invoiceRowList.stream()
				// 新規追加したが、✕で削除した行の場合、
				// フロントのDOM上は削除しているが、残っているDOMのname属性について、Listのインデックス番号が歯抜けになることで、歯抜け分のinputFormが自動でListに追加されるため、それを除外する
				.filter(form -> !form.isEmpty())
				.collect(Collectors.toList());
		
		return enabledInvoiceList;
	}
	
	/**
	 * invoiceInputFormの単価と時間から報酬額を算出しinvoiceInputFormにセットします。
	 * 
	 * @param invoiceInputForm
	 * @throws AppException
	 */
	public void calculateTimeCharge(AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) throws AppException {
		List<InvoiceRowInputForm> invoiceRowInputFormList = invoiceInputForm.getInvoiceRowList();
		if (LoiozCollectionUtils.isEmpty(invoiceRowInputFormList)) {
			return;
		}
		for (InvoiceRowInputForm form : invoiceRowInputFormList) {
			if (form.isFeeTimeChargeFlg()) {
				if (!StringUtils.isEmpty(form.getHourPrice()) && form.getWorkTimeMinute() != null) {
					// タイムチャージ額の計算
					BigDecimal tanka = new BigDecimal(form.getHourPrice());
					BigDecimal decimalMinutes = new BigDecimal(form.getWorkTimeMinute());
					BigDecimal feeAmount = commonAccgAmountService.calculateTimeCharge(tanka, decimalMinutes);
					if (feeAmount != null) {
						form.setAmount(feeAmount.toPlainString());
					} else {
						form.setAmount("");
					}
				} else {
					form.setAmount("");
				}
			}
		}
	}
	
	/**
	 * 請求書詳細画面、精算書詳細画面：既入金項目の並び順採番
	 * 
	 * @param repayRowList
	 */
	public void numberingDocRepayOrder(List<RepayRowInputForm> repayRowList) {
		if (LoiozCollectionUtils.isEmpty(repayRowList)) {
			return;
		}
		List<RepayRowInputForm> targetList = repayRowList.stream()
				.filter(form -> !form.isDeleteFlg())
				.collect(Collectors.toList());
		long docRepayOrder = 1;
		for (RepayRowInputForm form : targetList) {
			form.setDocRepayOrder(docRepayOrder++);
		}
	}

	/**
	 * 請求書詳細画面、精算書詳細画面：請求項目の並び順採番
	 * 
	 * @param invoiceRowList
	 */
	public void numberingDocInvoiceOrder(List<InvoiceRowInputForm> invoiceRowList) {
		if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
			return;
		}
		List<InvoiceRowInputForm> targetList = invoiceRowList.stream()
				.filter(form -> !form.isDeleteFlg())
				.collect(Collectors.toList());
		long docInvoiceOrder = 1;
		for (InvoiceRowInputForm form : targetList) {
			form.setDocInvoiceOrder(docInvoiceOrder++);
		}
	}
	
	//=========================================================================
	// ▼ チェック、バリデーション系
	//=========================================================================

	/**
	 * 既入金フラグメント入力情報相関チェック<br>
	 * ・請求書／精算書の発行ステータスチェック<br>
	 * ・既入金額の桁チェック<br>
	 * ・既入金、預り金の存在チェック<br>
	 * ・既入金行データ内の必須チェック <br>
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param repayRowList
	 * @param result
	 * @return
	 * @throws AppException
	 */
	public BindingResult validateRepayRowList(Long accgDocSeq, Long ankenId, Long personId,
			List<RepayRowInputForm> repayRowList, BindingResult result) throws AppException {
		
		// 行データが無い場合は、チェック終了
		if (LoiozCollectionUtils.isEmpty(repayRowList)) {
			return result;
		}
		
		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 既入金項目の更新、削除データがDBに存在するかチェック
		boolean isTargetDataExistsInDb = checkIfTargetDocRepayDataExistsInDb(repayRowList);
		if (!isTargetDataExistsInDb) {
			// 対象データが無い場合は、楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 会計書類のステータスが下書きかどうか
		AccgDocType docType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (docType.isInvoice()) {
			// 請求書の場合
			
			// ステータスチェック
			if (!this.isIssueStatusDraft(accgDocSeq, docType)) {
				// 下書きではない
				throw new AppException(MessageEnum.MSG_W00016, null, "請求書", "編集");
			}
		} else if (docType.isStatement()) {
			// 精算書の場合
			
			// ステータスチェック
			if (!this.isIssueStatusDraft(accgDocSeq, docType)) {
				// 下書きではない
				throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
			}
		} else {
			// 想定外のEnum値
			throw new RuntimeException("想定外のEnum値");
		}
		
		// 既入金額の桁チェック
		this.checkRepayAmountDigits(repayRowList, accgDocSeq);
		
		// 請求項目-預り金情報と既入金が存在するかチェック
		boolean isInvoiceDepositAndRepayExist = false;
		long repayCount = repayRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getRepayAmount()))
				.count();
		if (repayCount > 0 && commonAccgService.chekcIfInvoiceDepositExists(accgDocSeq)) {
			isInvoiceDepositAndRepayExist = true;
		}
		
		// 行データの必須チェック、既入金エラーのセット
		if (LoiozCollectionUtils.isNotEmpty(repayRowList)) {
			int idx = 0;
			for (RepayRowInputForm form : repayRowList) {
				// 空行はチェックしない
				if (!form.isRowRepaySumFlg() && LoiozCollectionUtils.isEmpty(form.getDepositRecvSeqList())) {
					idx++;
					continue;
				}
				
				// 削除以外の場合
				if (!form.isDeleteFlg()) {
					// 項目が無い
					if (StringUtils.isEmpty(form.getRepayItemName())) {
						result.rejectValue("repayRowList[" + idx + "].repayItemName", null,
								messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
					}
					
					// 金額が無い
					if (StringUtils.isEmpty(form.getRepayAmount())) {
						result.rejectValue("repayRowList[" + idx + "].repayAmount", null,
								messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
					}
					
					// 既入金と預り金の両方が存在する場合、エラーをセット
					if (isInvoiceDepositAndRepayExist && !StringUtils.isEmpty(form.getRepayAmount())) {
						result.rejectValue("repayRowList[" + idx + "].repayAmount", null,
								messageService.getMessage(MessageEnum.MSG_E00180, SessionUtils.getLocale()));
					}
				}
				idx++;
			}
		}
		return result;
	}
	
	/**
	 * 請求フラグメント入力情報相関チェック<br>
	 * ・報酬データのステータスチェック<br>
	 * ・請求書、精算書の発行ステータスチェック<br>
	 * ・請求額、精算額上限チェック<br>
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
	public BindingResult validateInvoiceRowList(Long accgDocSeq, Long ankenId, Long personId,
			List<InvoiceRowInputForm> invoiceRowList, BindingResult result) throws AppException {
		
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 請求項目の更新、削除対象データがDBに存在するかチェック
		boolean isTargetDataExistsInDb = checkIfTargetDocInvoiceDataExistsInDb(invoiceRowList);
		if (!isTargetDataExistsInDb) {
			// 対象データが無い場合は楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 報酬データのステータスが「未請求」かチェック
		boolean isFeePaymentStatusOfAllUnclaimed = this.checkFeePaymentStatusIsUnclaimed(invoiceRowList);
		if (!isFeePaymentStatusOfAllUnclaimed) {
			// 未請求以外の報酬がある場合は楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 請求書、精算書のステータスチェック、請求額、精算額の上限チェック
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = this.checkIssuingStatusIsDraftAndWithinMaxAmount(
				accgDocSeq, tAccgDocEntity.getAccgDocType(), invoiceRowList);
		
		// Form情報が空の場合はチェック終了
		if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
			return result;
		}
		
		// 値引き（0%）が報酬額（0%）を超えていないかチェック
		boolean isLargerDiscountTaxNon = this.checkIfDiscountTaxNonExceedsFeeAmount(accgInvoiceStatementAmountDto);
		// 値引き（8%）が報酬額（8%）を超えていないかチェック
		boolean isLargerDiscountTax8 = this.checkIfDiscountTax8ExceedsFeeAmount(accgInvoiceStatementAmountDto);
		// 値引き（10%）が報酬額（10%）を超えていないかチェック
		boolean isLargerDiscountTax10 = this.checkIfDiscountTax10ExceedsFeeAmount(accgInvoiceStatementAmountDto);

		// 源泉徴収対象額がマイナスにならないかチェック
		boolean isWithholdingTargetFeeIsMinus = this.checkWithholdingTargetFeeIsMinus(accgInvoiceStatementAmountDto);
		// 源泉徴収税引後に金額がマイナスにならないかチェック
		boolean isLargerDiscount = this.checkIfAfterTaxWithholdingIsMinus(accgInvoiceStatementAmountDto);
		
		// 既入金と請求項目-預り金の両方が存在するかチェック
		boolean isInvoiceDepositAndRepayExist = false;
		long depositCount = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg() && !StringUtils.isEmpty(row.getAmount()))
				.filter(row -> row.isDepositRecvFlg())
				.filter(row -> InvoiceDepositType.DEPOSIT.equalsByCode(row.getInvoieType()))
				.count();
		if (depositCount > 0 && this.checkIfReapyExists(accgDocSeq)) {
			isInvoiceDepositAndRepayExist = true;
		}

		// 行データの必須チェック、既入金と預り金同時存在エラーのセット、値引きエラーのセット
		int idx = 0;
		for (InvoiceRowInputForm form : invoiceRowList) {
			// 空, 未入力, 削除はチェックしない
			if (form.isEmpty() || form.isNotEntered() || form.isDeleteFlg()) {
				idx++;
				continue;
			}

			// ********************************************************
			// 以下、バリデーションチェックが必要なケース (空, 未入力, 削除以外)
			// ********************************************************

			if (StringUtils.isEmpty(form.getItemName())) {
				// 項目名入力チェック
				result.rejectValue("invoiceRowList[" + idx + "].itemName", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(),
						messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
			}

			// 金額の入力チェック
			BigDecimal amount = LoiozNumberUtils.parseAsBigDecimal(form.getAmount());
			if (form.isDepositRecvFlg()) {
				// 預り金レコードの場合
				if (StringUtils.isEmpty(form.getAmount())) {
					// 金額が未入力の場合 -> 入力必須チェック
					result.rejectValue("invoiceRowList[" + idx + "].amount", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(),
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
				} else if (amount == null) {
					// BigDecimal変換でnullになるケース
					result.rejectValue("invoiceRowList[" + idx + "].amount", MessageEnum.VARIDATE_MSG_E00001.getMessageKey(),
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00001, SessionUtils.getLocale()));
				}

			} else if (form.isInvoiceFee() || InvoiceOtherItemType.DISCOUNT == form.getInvoiceOtherItemType()) {
				// 報酬レコード種類「値引き以外」の場合
				if (StringUtils.isEmpty(form.getAmount())) {
					// 金額が未入力の場合 -> 入力必須チェック
					result.rejectValue("invoiceRowList[" + idx + "].amount", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(),
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
				} else if (amount == null) {
					// BigDecimal変換でnullになるケース
					result.rejectValue("invoiceRowList[" + idx + "].amount", MessageEnum.VARIDATE_MSG_E00001.getMessageKey(),
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00001, SessionUtils.getLocale()));
				} else if (LoiozNumberUtils.isGreaterThan(BigDecimal.ZERO, amount)) {
					// 金額は0以上のみ入力可能
					result.rejectValue(
							"invoiceRowList[" + idx + "].amount",
							MessageEnum.MSG_E00042.getMessageKey(),
							new String[]{"0円以上"},
							messageService.getMessage(MessageEnum.MSG_E00042, SessionUtils.getLocale(), "0円以上"));
				}
			} else {
				// なにもしない
			}

			// タイムチャージ入力時
			if (form.isFeeTimeChargeFlg()) {
				// タイムチャージ単価が無い
				if (StringUtils.isEmpty(form.getHourPrice())) {
					result.rejectValue("invoiceRowList[" + idx + "].hourPrice", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(),
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
				}
				// タイムチャージ時間が無い
				if (form.getWorkTimeMinute() == null) {
					result.rejectValue("invoiceRowList[" + idx + "].workTimeMinute", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(),
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
				}
			}

			// 源泉徴収後に合計がマイナスになる場合は、全ての値引きでエラーを表示
			if (isLargerDiscount && !form.isDepositRecvFlg()
					&& InvoiceOtherItemType.DISCOUNT.equalsByCode(form.getInvoieType())
					&& !StringUtils.isEmpty(form.getAmount())) {

				result.rejectValue("invoiceRowList[" + idx + "].amount", null,
						messageService.getMessage(MessageEnum.MSG_E00176, SessionUtils.getLocale(), "値引き"));
			} else if (isWithholdingTargetFeeIsMinus
					&& InvoiceOtherItemType.DISCOUNT.equalsByCode(form.getInvoieType())
					&& !StringUtils.isEmpty(form.getAmount()) && form.isWithholdingFlg()) {

				// 源泉徴収対象額がマイナスになる場合は、源泉徴収ありにしている値引きにエラーをセット
				result.rejectValue("invoiceRowList[" + idx + "].amount", null,
						messageService.getMessage(MessageEnum.MSG_E00181, SessionUtils.getLocale()));
			} else {
				// 値引き（0%）エラーの場合は値引き（0%）にエラーをセット
				if (isLargerDiscountTaxNon && !form.isDepositRecvFlg()
						&& InvoiceOtherItemType.DISCOUNT.equalsByCode(form.getInvoieType())
						&& TaxRate.TAX_FREE.equalsByCode(form.getTaxRateType())
						&& !StringUtils.isEmpty(form.getAmount())) {
					result.rejectValue("invoiceRowList[" + idx + "].amount", null,
							messageService.getMessage(MessageEnum.MSG_E00176, SessionUtils.getLocale(), "値引き"));
				}

				// 値引き（8%）エラーの場合は値引き（8%）にエラーをセット
				if (isLargerDiscountTax8 && !form.isDepositRecvFlg()
						&& InvoiceOtherItemType.DISCOUNT.equalsByCode(form.getInvoieType())
						&& TaxRate.EIGHT_PERCENT.equalsByCode(form.getTaxRateType())
						&& !StringUtils.isEmpty(form.getAmount())) {
					result.rejectValue("invoiceRowList[" + idx + "].amount", null,
							messageService.getMessage(MessageEnum.MSG_E00176, SessionUtils.getLocale(), "値引き"));
				}

				// 値引き（10%）エラーの場合は値引き（10%）にエラーをセット
				if (isLargerDiscountTax10 && !form.isDepositRecvFlg()
						&& InvoiceOtherItemType.DISCOUNT.equalsByCode(form.getInvoieType())
						&& TaxRate.TEN_PERCENT.equalsByCode(form.getTaxRateType())
						&& !StringUtils.isEmpty(form.getAmount())) {
					result.rejectValue("invoiceRowList[" + idx + "].amount", null,
							messageService.getMessage(MessageEnum.MSG_E00176, SessionUtils.getLocale(), "値引き"));
				}
			}

			// 既入金と請求項目-預り金の両方が存在する場合は全ての預り金でエラーをセット
			if (isInvoiceDepositAndRepayExist && form.isDepositRecvFlg()
					&& InvoiceDepositType.DEPOSIT.equalsByCode(form.getInvoieType())
					&& !StringUtils.isEmpty(form.getAmount())) {
				result.rejectValue("invoiceRowList[" + idx + "].amount", null,
						messageService.getMessage(MessageEnum.MSG_E00180, SessionUtils.getLocale()));
			}

			idx++;
		}
		return result;
	}

	/**
	 * 請求フラグメントまとめ表示前相関チェック<br>
	 * ・請求書、精算書の発行ステータスチェック<br>
	 * ・請求額、精算額の上限チェック<br>
	 * ・実費データの必須チェック <br>
	 * ・実費データの日付フォーマットチェック<br>
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param invoiceRowList
	 * @param result
	 * @return
	 * @throws AppException
	 */
	public BindingResult validationBeforeGroupOrUngroupSimilarInvoiceItems(Long accgDocSeq, Long ankenId, Long personId,
			List<InvoiceRowInputForm> invoiceRowList, BindingResult result) throws AppException {
		
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 請求書、精算書のステータスチェック、請求額、精算額の上限チェック
		this.checkIssuingStatusIsDraftAndWithinMaxAmount(accgDocSeq, tAccgDocEntity.getAccgDocType(), invoiceRowList);
		
		// 実費データの必須チェック、
		int idx = 0;
		for (InvoiceRowInputForm form : invoiceRowList) {
			// 実費データ以外はチェックしない
			if (!(form.isDepositRecvFlg() && InvoiceDepositType.EXPENSE.equalsByCode(form.getInvoieType()))) {
				idx++;
				continue;
			}
			
			// 削除以外の場合
			if (!form.isDeleteFlg()) {
				// 日付が正しくない
				if (!StringUtils.isEmpty(form.getTransactionDate()) && !ValidateUtils
						.isLocalDatePatternValid(form.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED)) {
					result.rejectValue("invoiceRowList[" + idx + "].transactionDate", null,
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00001, SessionUtils.getLocale()));
				}
				
				// 項目が無い
				if (StringUtils.isEmpty(form.getItemName())) {
					result.rejectValue("invoiceRowList[" + idx + "].itemName", null,
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
				}
				
				// 金額が無い
				if (StringUtils.isEmpty(form.getAmount())) {
					result.rejectValue("invoiceRowList[" + idx + "].amount", null,
							messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
				}
			}
			idx++;
		}
		return result;
	}

	/**
	 * 請求書発行前のチェック処理
	 * 
	 * @param requestSourceAccgDocType リクエスト元の会計書類種別
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void checkOfBeforeIssue(AccgDocType requestSourceAccgDocType, Long accgDocSeq) throws AppException {

		// 会計書類情報を取得する
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null || !requestSourceAccgDocType.equalsByCode(tAccgDocEntity.getAccgDocType())) {
			// 会計書類が存在しない || 送信元の会計書類種別と現在の会計書類種別が異なる場合 -> 排他エラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 値引きが報酬を超えていないかチェック
		if (this.checkIfDiscountAmountExceedsFeeAmount(accgDocSeq)) {
			throw new AppException(MessageEnum.MSG_E00178, null, "値引き", "報酬");
		}

		// 既入金と預り金の存在チェック
		if (this.checkIfInvoiceDepositsAndRepayExists(accgDocSeq)) {
			throw new AppException(MessageEnum.MSG_E00180, null);
		}

		// 売上計上先チェック
		if (requestSourceAccgDocType.isInvoice()) {
			// 請求書の場合
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			if (tAccgInvoiceEntity.getSalesAccountSeq() == null) {
				throw new AppException(MessageEnum.MSG_E00186, null, "売上計上先");
			}

		} else if (requestSourceAccgDocType.isStatement()) {
			// 精算書
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			if (tAccgStatementEntity.getSalesAccountSeq() == null) {
				throw new AppException(MessageEnum.MSG_E00186, null, "売上計上先");
			}

		} else {
			// 想定外の場合
			throw new RuntimeException("想定外のEnum");
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
		// 返却値
		boolean hasBeenSent = false;
		
		// 会計書類-対応情報を取得
		List<TAccgDocActEntity> accgDocActList = tAccgDocActDao.selectAccgDocActByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(accgDocActList)) {
			return hasBeenSent;
		}
		
		// 「送信」対応の件数
		long sendAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.SEND.equalsByCode(e.getActType())).count();
		if (sendAccgDocActCount > 0) {
			hasBeenSent = true;
		}
		
		return hasBeenSent;
	}

	/**
	 * 引数に入力されたアカウントSEQが、売上計上先として有効な値か検証する
	 * 
	 * @param salesAccountSeq 検証対象
	 * @param accgDocSeq 登録対象の会計書類SEQ
	 * @return
	 */
	public boolean isSalesAccountValid(Long salesAccountSeq, Long accgDocSeq) {

		if (salesAccountSeq == null) {
			// Nullの場合は、当メソッドでは有効として扱う
			return true;
		}

		// 有効無効関係なくアカウント情報を取得
		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(salesAccountSeq);
		if (mAccountEntity == null) {
			// アカウント情報の取得ができない場合、無効な値
			return false;
		}

		// 会計書類の売上計上先選択肢情報を取得
		List<SelectOptionForm> accgSalesOwnerOptions = generateAccgSalesOwnerOptionForm(accgDocSeq);
		if (!accgSalesOwnerOptions.stream().anyMatch(e -> Objects.equals(salesAccountSeq, e.getValueAsLong()))) {
			// 選択プルダウン内に存在しない場合 -> 無効
			return false;
		}

		return true;
	}

	/**
	 * 値引き額（0%）が報酬額（0%）を超えているかチェックします<br>
	 * 値引き額が多い場合はtrueを返します。
	 * 
	 * @param accgInvoiceStatementAmountDto
	 * @return
	 */
	public boolean checkIfDiscountTaxNonExceedsFeeAmount(AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) {
		boolean isLargerDiscountTaxNon = false;
		if (accgInvoiceStatementAmountDto == null) {
			return isLargerDiscountTaxNon;
		}
		// 報酬額（0%）源泉徴収ありの合計
		BigDecimal feeWithholdingTotalAmount = accgInvoiceStatementAmountDto.getTotalFeeAmountTaxNonWithholding() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalFeeAmountTaxNonWithholding();
		// 報酬額（0%）源泉徴収なしの合計
		BigDecimal feeTotalAmount = accgInvoiceStatementAmountDto.getTotalFeeAmountTaxNon() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalFeeAmountTaxNon();

		// 値引き（0%）源泉徴収ありの合計
		BigDecimal discountWithholdingTotalAmount = accgInvoiceStatementAmountDto.getTotalDiscountAmountTaxNonWithholding() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalDiscountAmountTaxNonWithholding();
		// 値引き（0%）源泉徴収なしの合計
		BigDecimal discountTotalAmount = accgInvoiceStatementAmountDto.getTotalDiscountAmountTaxNon() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalDiscountAmountTaxNon();

		// 値引きの合計が報酬額の合計を超えていないかチェック
		BigDecimal feeTotal = feeWithholdingTotalAmount.add(feeTotalAmount);
		BigDecimal discount = discountWithholdingTotalAmount.add(discountTotalAmount);
		if (LoiozNumberUtils.isLessThan(feeTotal, discount)) {
			isLargerDiscountTaxNon = true;
		}

		return isLargerDiscountTaxNon;
	}

	/**
	 * 値引き額（8%）が報酬額（8%）を超えているかチェックします<br>
	 * 値引き額が多い場合はtrueを返します。
	 * 
	 * @param accgInvoiceStatementAmountDto
	 * @return
	 */
	public boolean checkIfDiscountTax8ExceedsFeeAmount(AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) {
		boolean isLargerDiscountTax8 = false;
		if (accgInvoiceStatementAmountDto == null) {
			return isLargerDiscountTax8;
		}
		// 報酬額（8%）源泉徴収ありの合計
		BigDecimal feeWithholdingTotalAmount = accgInvoiceStatementAmountDto.getTotalFeeAmountTax8Withholding() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalFeeAmountTax8Withholding();
		// 報酬額（8%）源泉徴収なしの合計
		BigDecimal feeTotalAmount = accgInvoiceStatementAmountDto.getTotalFeeAmountTax8() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalFeeAmountTax8();

		// 値引き（8%）源泉徴収ありの合計
		BigDecimal discountWithholdingTotalAmount = accgInvoiceStatementAmountDto.getTotalDiscountAmountTax8Withholding() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalDiscountAmountTax8Withholding();
		// 値引き（8%）源泉徴収なしの合計
		BigDecimal discountTotalAmount = accgInvoiceStatementAmountDto.getTotalDiscountAmountTax8() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalDiscountAmountTax8();

		// 値引きの合計が報酬額の合計を超えていないかチェック
		BigDecimal feeTotal = feeWithholdingTotalAmount.add(feeTotalAmount);
		BigDecimal discount = discountWithholdingTotalAmount.add(discountTotalAmount);
		if (LoiozNumberUtils.isLessThan(feeTotal, discount)) {
			isLargerDiscountTax8 = true;
		}

		return isLargerDiscountTax8;
	}

	/**
	 * 値引き額（10%）が報酬額（10%）を超えているかチェックします<br>
	 * 値引き額が多い場合はtrueを返します。
	 * 
	 * @param accgInvoiceStatementAmountDto
	 * @return
	 */
	public boolean checkIfDiscountTax10ExceedsFeeAmount(AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) {
		boolean isLargerDiscountTax10 = false;
		if (accgInvoiceStatementAmountDto == null) {
			return isLargerDiscountTax10;
		}
		// 報酬額（10%）源泉徴収ありの合計
		BigDecimal feeWithholdingTotalAmount = accgInvoiceStatementAmountDto.getTotalFeeAmountTax10Withholding() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalFeeAmountTax10Withholding();
		// 報酬額（10%）源泉徴収なしの合計
		BigDecimal feeTotalAmount = accgInvoiceStatementAmountDto.getTotalFeeAmountTax10() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalFeeAmountTax10();

		// 値引き（10%）源泉徴収ありの合計
		BigDecimal discountWithholdingTotalAmount = accgInvoiceStatementAmountDto.getTotalDiscountAmountTax10Withholding() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalDiscountAmountTax10Withholding();
		// 値引き（10%）源泉徴収なしの合計
		BigDecimal discountTotalAmount = accgInvoiceStatementAmountDto.getTotalDiscountAmountTax10() == null ? BigDecimal.ZERO : accgInvoiceStatementAmountDto.getTotalDiscountAmountTax10();

		// 値引きの合計が報酬額の合計を超えていないかチェック
		BigDecimal feeTotal = feeWithholdingTotalAmount.add(feeTotalAmount);
		BigDecimal discount = discountWithholdingTotalAmount.add(discountTotalAmount);
		if (LoiozNumberUtils.isLessThan(feeTotal, discount)) {
			isLargerDiscountTax10 = true;
		}

		return isLargerDiscountTax10;
	}

	/**
	 * 請求書画面、精算書画面の請求フラグメントに入力した源泉徴収対象額がマイナスかチェックします<br>
	 * マイナスの場合はtrueを返します。
	 * 
	 * @param dto
	 * @return
	 */
	public boolean checkWithholdingTargetFeeIsMinus(AccgInvoiceStatementAmountDto dto) {
		boolean isMinus = false;
		if (dto == null) {
			return isMinus;
		}

		// 源泉徴収の対象額
		BigDecimal totalWithholdingTargetFee = dto.getTotalWithholdingTargetFee();

		if (LoiozNumberUtils.isLessThan(totalWithholdingTargetFee, BigDecimal.ZERO)) {
			// 源泉徴収の対象額がマイナス
			isMinus = true;
		}

		return isMinus;
	}

	/**
	 * 請求書画面、精算書画面の請求フラグメントに入力した報酬や値引きの合計が源泉徴収後にマイナスかチェックします<br>
	 * マイナスの場合はtrueを返します。
	 * 
	 * @param dto
	 * @return
	 */
	public boolean checkIfAfterTaxWithholdingIsMinus(AccgInvoiceStatementAmountDto dto) {
		boolean isTotalAmountMinus = false;
		if (dto == null) {
			return isTotalAmountMinus;
		}
		BigDecimal totalAmount = dto.getTotalAmount();
		
		if (LoiozNumberUtils.isLessThan(totalAmount, BigDecimal.ZERO)) {
			isTotalAmountMinus = true;
		}

		return isTotalAmountMinus;
	}

	/**
	 * 預り金テーブルの合計額（入金-出金）が同じか確認します。<br>
	 * 同じであれば true を返します。
	 * 
	 * @param depositAmount
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	public boolean checkTotalDepositAmount(BigDecimal depositAmount, Long ankenId, Long personId) {
		boolean isTheSame = false;

		// 預り金データ取得
		DepositRecvSummaryBean depositRecvSummaryBean = tDepositRecvDao.selectSummaryByParams(ankenId, personId);
		if (depositRecvSummaryBean == null) {
			return isTheSame;
		}

		BigDecimal totalDepositAmount = depositRecvSummaryBean.getTotalDepositAmount();
		BigDecimal totalWithdrawalAmount = depositRecvSummaryBean.getTotalWithdrawalAmount();

		// 入金額の比較
		if (totalDepositAmount.subtract(totalWithdrawalAmount).compareTo(depositAmount) == 0) {
			isTheSame = true;
		}
		return isTheSame;
	}
	
	/**
	 * 報酬データの登録上限チェック<br>
	 * ※保存処理後に実施するチェックメソッドになるので注意（保存処理前のチェックではない）
	 * 
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void checkFeeRegistLimitAfterSaveAccgDocInvoice(Long ankenId, Long personId) throws AppException {

		// DBに登録してある報酬データ数
		List<TFeeEntity> feeList = tFeeDao.selectFeeEntityByParams(ankenId, personId);
		int numberOfFeeInDB = feeList.size();

		// 報酬の登録上限チェック
		if (numberOfFeeInDB > CommonConstant.FEE_ADD_REGIST_LIMIT) {
			// 報酬の登録上限に達しているためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00174, null, "報酬", String.valueOf(CommonConstant.FEE_ADD_REGIST_LIMIT));
		}
	}
	
	//=========================================================================
	// ▼ 登録／更新／削除系
	//=========================================================================
	
	/**
	 * 発行処理をします。
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param accgDocType
	 * @throws AppException
	 */
	public void issue(Long accgDocSeq, Long ankenId, Long personId, AccgDocType accgDocType) throws AppException {

		// 請求情報取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// 報酬入金額【見込】（Dtoの値をもとに算出）
		BigDecimal feeAmountExpect = commonAccgAmountService.calcFeeAmountExpect(accgInvoiceStatementAmountDto);
		// 預り金入金額【見込】（Dtoの値をもとに算出）
		BigDecimal depositRecvAmountExpect = commonAccgAmountService.calcDepositRecvAmountExpect(accgInvoiceStatementAmountDto);
		// 預り金出金額【見込】（Dtoの値をもとに算出）
		BigDecimal depositPaymentAmountExpect = commonAccgAmountService.calcDepositPaymentAmountExpect(accgInvoiceStatementAmountDto);
		// 【請求項目】預り金の合計
		BigDecimal totalDepositAmount = accgInvoiceStatementAmountDto.getTotalDepositAmount();
		// 振替額（既入金の報酬への振替額）（Dtoの値をもとに算出）
		BigDecimal refundAmount = commonAccgAmountService.calcRefundAmount(accgInvoiceStatementAmountDto);

		// 会計書類-対応 「2:発行」を登録
		commonAccgService.registAccgDocAct(accgDocSeq, AccgDocActType.ISSUE);

		// 取引実績を登録
		TAccgRecordEntity tAccgRecordEntity = this.registAccgRecord(accgDocSeq, feeAmountExpect, depositRecvAmountExpect, depositPaymentAmountExpect);
		// 取引実績明細を登録
		this.registRepayAmountAsFee(tAccgRecordEntity.getAccgRecordSeq(), refundAmount);

		// 報酬データの入金ステータス更新
		commonAccgService.updateFeeStatusByAccgDoc(accgDocSeq);

		// 振替金額分を預り金に出金として登録
		this.registDepositIfThereRepayAmount(accgDocSeq, ankenId, personId, refundAmount);

		// 預り金データに入金or出金の予定を登録
		this.registDepositOrWithdrawalSchedule(accgDocSeq, ankenId, personId, depositRecvAmountExpect, depositPaymentAmountExpect, accgDocType, totalDepositAmount);

		// 請求書、精算書のステータス更新。発行ステータス（送付待ち）、入出金ステータス（待ち）。
		this.updateDocStatusWhenIssue(accgDocSeq, accgDocType);

		// 案件ID、名簿IDで紐づく発行ステータスが下書きの全ての実費明細書に対して再作成フラグを立てる
		commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		// 売上に関する情報を登録
		TSalesDetailEntity tSalesDetailEntity = this.registSalesInformation(accgDocSeq, ankenId, personId, accgDocType, accgInvoiceStatementAmountDto);

		// 請求書、精算書に売上明細SEQを設定
		this.updateFeeSalesDetailSeq(tSalesDetailEntity.getSalesDetailSeq(), accgDocSeq, accgDocType);
	}

	/**
	 * 送付モーダルの内容デフォルト値を作成する
	 * 
	 * @param name
	 * @param nameEndCd
	 * @param bodyStr
	 * @return
	 */
	public String generateDefaultInputSendBody(String tenantName, @Nullable String toName, @Nullable String nameEndCd, @Nullable String bodyStr) {

		StringBuilder defaultSendBody = new StringBuilder("");
		if (!StringUtils.isEmpty(toName)) {
			defaultSendBody.append(String.join(CommonConstant.SPACE, toName, DefaultEnum.getVal(NameEnd.of(nameEndCd))));
			defaultSendBody.append(CommonConstant.CRLF_CODE);
			// 一行空ける
			defaultSendBody.append(CommonConstant.CRLF_CODE);
		}

		defaultSendBody.append(String.format(AccgConstant.ACCG_FILE_SEND_INPUT_MODAL_BODY_ADD_TENANT_NAME_MSG, tenantName));
		defaultSendBody.append(CommonConstant.CRLF_CODE);
		// 一行空ける
		defaultSendBody.append(CommonConstant.CRLF_CODE);

		if (!StringUtils.isEmpty(bodyStr)) {
			defaultSendBody.append(bodyStr);
			defaultSendBody.append(CommonConstant.CRLF_CODE);
		}

		return defaultSendBody.toString();
	}

	/**
	 * 会計書類-対応-送付の登録処理
	 * 
	 * @param inputForm
	 * @return
	 * @throws AppException
	 */
	public Long registAccgDocActSendRecord(Long accgDocActSeq, AccgInvoiceStatementInputForm.FileSendInputForm inputForm) throws AppException {

		// 送付データの作成ステータス
		TAccgDocActSendEntity tAccgDocActSendEntity = new TAccgDocActSendEntity();
		tAccgDocActSendEntity.setAccgDocActSeq(accgDocActSeq);
		tAccgDocActSendEntity.setSendType(inputForm.getSendType());
		tAccgDocActSendEntity.setSendTo(inputForm.getSendTo());
		tAccgDocActSendEntity.setSendCc(inputForm.getSendCc());
		tAccgDocActSendEntity.setSendBcc(inputForm.getSendBcc());
		tAccgDocActSendEntity.setReplyTo(inputForm.getReplyTo());
		tAccgDocActSendEntity.setSendFromName(inputForm.getSendFromName());
		tAccgDocActSendEntity.setSendSubject(inputForm.getSendSubject());
		tAccgDocActSendEntity.setSendBody(inputForm.getSendBody());
		if (AccgDocSendType.WEB.equalsByCode(inputForm.getSendType())) {
			LocalDate downloadLimitDate = commonAccgService.getAccgWebShareDownloadLimitDate();
			tAccgDocActSendEntity.setDownloadLimitDate(downloadLimitDate);
		}

		try {
			// 登録処理
			tAccgDocActSendDao.insert(tAccgDocActSendEntity);

			return tAccgDocActSendEntity.getAccgDocActSendSeq();

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * メール送付せずに、送付済みにする際の登録処理
	 * 
	 * @param accgDocActSeq
	 * @return
	 * @throws AppException
	 */
	public Long registAccgDocActSendForChangeToSend(Long accgDocActSeq) throws AppException {

		// 送付データの作成ステータス
		TAccgDocActSendEntity tAccgDocActSendEntity = new TAccgDocActSendEntity();
		tAccgDocActSendEntity.setAccgDocActSeq(accgDocActSeq);
		tAccgDocActSendEntity.setSendType(AccgDocSendType.CHANGE_TO_SEND.getCd());
		tAccgDocActSendEntity.setSendTo(CommonConstant.BLANK);
		tAccgDocActSendEntity.setReplyTo(CommonConstant.BLANK);
		tAccgDocActSendEntity.setSendFromName(CommonConstant.BLANK);
		tAccgDocActSendEntity.setSendSubject(CommonConstant.BLANK);
		tAccgDocActSendEntity.setSendBody(CommonConstant.BLANK);

		try {
			// 登録処理
			tAccgDocActSendDao.insert(tAccgDocActSendEntity);

			return tAccgDocActSendEntity.getAccgDocActSendSeq();

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 請求書詳細詳細画面、精算書詳細画面の既入金データの登録処理
	 * 
	 * @param insertRepayList
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	public List<TAccgDocRepayEntity> insertRepay(List<RepayRowInputForm> insertRepayList, Long accgDocSeq) throws AppException {
		if (LoiozCollectionUtils.isEmpty(insertRepayList)) {
			return List.of();
		}
		// 既入金の登録
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = new ArrayList<>();
		for (RepayRowInputForm row : insertRepayList) {
			TAccgDocRepayEntity entity = new TAccgDocRepayEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setRepayTransactionDate(DateUtils.parseToLocalDate(row.getRepayTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setRepayItemName(row.getRepayItemName());
			entity.setRepayAmount(LoiozNumberUtils.parseAsBigDecimal(row.getRepayAmount()));
			entity.setSumText(row.getSumText());
			entity.setDocRepayOrder(row.getDocRepayOrder());
			tAccgDocRepayEntityList.add(entity);
		}
		return this.batchInsertAccgDocRepay(tAccgDocRepayEntityList);
	}

	/**
	 * 請求項目の登録処理
	 * 
	 * @param invoiceDepositList
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	public List<TAccgDocInvoiceEntity> insertAccgDocInvoice(List<InvoiceRowInputForm> invoiceDepositList, Long accgDocSeq) throws AppException {
		List<TAccgDocInvoiceEntity> accgDocInvoiceEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : invoiceDepositList) {
			TAccgDocInvoiceEntity entity = new TAccgDocInvoiceEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setDocInvoiceOrder(row.getDocInvoiceOrder());
			accgDocInvoiceEntityList.add(entity);
		}
		return this.batchInsertTAccgDocInvoice(accgDocInvoiceEntityList);
	}

	/**
	 * 請求項目-預り金データの登録処理
	 * 
	 * @param invoiceDepositList
	 * @return
	 * @throws AppException
	 */
	public List<TAccgDocInvoiceDepositEntity> insertAccgDocInvoiceDeposit(List<InvoiceRowInputForm> invoiceDepositList) throws AppException {
		List<TAccgDocInvoiceDepositEntity> accgDocInvoiceDepositEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : invoiceDepositList) {
			TAccgDocInvoiceDepositEntity entity = new TAccgDocInvoiceDepositEntity();
			entity.setDepositAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount()));
			entity.setDepositItemName(row.getItemName());
			entity.setDepositTransactionDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setDocInvoiceSeq(row.getDocInvoiceSeq());
			entity.setInvoiceDepositType(row.getInvoieType());
			entity.setSumText(row.getSumText());
			accgDocInvoiceDepositEntityList.add(entity);
		}
		return this.batchInsertAccgDocInvoiceDeposit(accgDocInvoiceDepositEntityList);
	}

	/**
	 * 既入金データと預り金データのマッピングデータを作成します
	 * 
	 * @param tAccgDocRepayEntityList
	 * @param depositNyukinList
	 * @throws AppException
	 */
	public void insertMappingOfRepayAndDeposit(List<TAccgDocRepayEntity> tAccgDocRepayEntityList,
			List<RepayRowInputForm> depositNyukinList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(tAccgDocRepayEntityList) && LoiozCollectionUtils.isEmpty(depositNyukinList)) {
			return;
		}

		// 既入金データ数と預り金データ数が異なる場合はマッピングできないためエラー
		int repaySize = tAccgDocRepayEntityList.size();
		int depositSize = depositNyukinList.size();
		if (repaySize != depositSize) {
			throw new RuntimeException("既入金データ数と預り金データ数が合わないためマッピング不可");
		}

		// マッピングデータentity作成
		List<TAccgDocRepayTDepositRecvMappingEntity> mappingEntityList = new ArrayList<>();
		for (int i = 0; i < depositSize; i++) {
			TAccgDocRepayTDepositRecvMappingEntity mappingEntity = new TAccgDocRepayTDepositRecvMappingEntity();
			mappingEntity.setDocRepaySeq(tAccgDocRepayEntityList.get(i).getDocRepaySeq());
			List<Long> depositRevSeqList = depositNyukinList.get(i).getDepositRecvSeqList();
			for (Long seq : depositRevSeqList) {
				mappingEntity.setDepositRecvSeq(seq);
				mappingEntityList.add(mappingEntity);
			}
		}
		
		// 預り金が他の請求書、精算書で使用されている場合は楽観ロックエラー
		List<Long> mapingDepositSeqList = mappingEntityList.stream().map(e -> e.getDepositRecvSeq())
				.collect(Collectors.toList());
		List<TAccgDocRepayTDepositRecvMappingEntity> depositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
				.selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeqList(mapingDepositSeqList);
		if (!LoiozCollectionUtils.isEmpty(depositRecvMappingEntityList)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		this.batchInsertRepayDepositMapping(mappingEntityList);
	}

	/**
	 * 請求項目-預り金データと預り気データのマッピングデータを作成します
	 * 
	 * @param accgDocInvoiceDepositEntity
	 * @param invoiceDepositList
	 * @throws AppException
	 */
	public void insertMappingOfInvoiceDepositAndDeposit(List<TAccgDocInvoiceDepositEntity> accgDocInvoiceDepositEntity,
			List<InvoiceRowInputForm> invoiceDepositList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(accgDocInvoiceDepositEntity) && LoiozCollectionUtils.isEmpty(invoiceDepositList)) {
			return;
		}

		// 請求項目-預り金データ数と預り金データ数が異なる場合はマッピングできないためエラー
		int invoiceDepositSize = accgDocInvoiceDepositEntity.size();
		int depositSize = invoiceDepositList.size();
		if (invoiceDepositSize != depositSize) {
			throw new RuntimeException("請求項目-預り金データ数と預り金データ数が合わないためマッピング不可");
		}
		
		// マッピングデータentity作成
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> mappingEntityList = new ArrayList<>();
		for (int i = 0; i < depositSize; i++) {
			TAccgDocInvoiceDepositTDepositRecvMappingEntity mappingEntity = new TAccgDocInvoiceDepositTDepositRecvMappingEntity();
			mappingEntity.setDocInvoiceDepositSeq(accgDocInvoiceDepositEntity.get(i).getDocInvoiceDepositSeq());
			List<Long> depositRecvSeqList = invoiceDepositList.get(i).getDepositRecvSeqList();
			for (Long seq : depositRecvSeqList) {
				mappingEntity.setDepositRecvSeq(seq);
				mappingEntityList.add(mappingEntity);
			}
		}
		
		// 預り金が他の請求書、精算書で使用されている場合は楽観ロックエラー
		List<Long> mapingDepositSeqList = mappingEntityList.stream().map(e -> e.getDepositRecvSeq())
				.collect(Collectors.toList());
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> depositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
				.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeqList(mapingDepositSeqList);
		if (!LoiozCollectionUtils.isEmpty(depositRecvMappingEntityList)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 請求項目-預り金（実費）_預り金テーブルマッピング登録
		this.batchInsertInvoiceDepositTDepositMapping(mappingEntityList);
	}
	
	/**
	 * 既入金データと預り金データのマッピングデータを作成する<br>
	 * ※主に、複数の預り金を1つの既入金項目に紐づける場合を想定（預り金は1つでもOK）
	 * 
	 * @param docRepaySeq 既入金項目のSEQ
	 * @param depositRecvSeqList 預り金のSEQ
	 * @param accgDocSeq 会計書類SEQ
	 * @throws AppException
	 */
	public void insertMappingOfRepayAndDeposit(Long docRepaySeq, List<Long> depositRecvSeqList, Long accgDocSeq)
			throws AppException {

		if (docRepaySeq == null) {
			// 紐づける対象が特定できないので、エラーとする
			throw new IllegalArgumentException("docRepaySeqがNULLのためマッピングデータを作成できない。");
		}

		if (LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			// 紐づけるデータがないので何もしない
			return;
		}

		// 登録データリスト
		List<TAccgDocRepayTDepositRecvMappingEntity> insertEntityList = new ArrayList<>();

		// 登録データリストに追加
		depositRecvSeqList.forEach(depositRecvSeq -> {
			TAccgDocRepayTDepositRecvMappingEntity insertEntity = new TAccgDocRepayTDepositRecvMappingEntity();
			insertEntity.setDocRepaySeq(docRepaySeq);
			insertEntity.setDepositRecvSeq(depositRecvSeq);
			insertEntityList.add(insertEntity);
		});
		
		// 預り金が他の請求書、精算書で使用されている場合は楽観ロックエラー
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao
				.selectDocRepayByDepositRecvSeq(depositRecvSeqList);
		List<TAccgDocRepayEntity> otherAccgDocRepayList = tAccgDocRepayEntityList.stream()
				.filter(entity -> !entity.getAccgDocSeq().equals(accgDocSeq))
				.collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(otherAccgDocRepayList)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// マッピングを登録
		this.batchInsertRepayDepositMapping(insertEntityList);

		// マッピングを登録した預り金の「会計書類SEQ（使用先）」に値を設定（更新）
		this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, accgDocSeq);
	}

	/**
	 * 請求項目-預り金データと預り金データのマッピングデータを作成する<br>
	 * ※主に、複数の預り金を1つの請求項目に紐づける場合を想定（預り金は1つでもOK）
	 * 
	 * @param docInvoiceDepositSeq 請求項目-預り金のSEQ
	 * @param depositRecvSeqList 預り金のSEQ
	 * @param accgDocSeq 会計書類SEQ
	 * @throws AppException
	 */
	public void insertMappingOfInvoiceAndDeposit(Long docInvoiceDepositSeq, List<Long> depositRecvSeqList,
			Long accgDocSeq) throws AppException {

		if (docInvoiceDepositSeq == null) {
			// 紐づける対象が特定できないので、エラーとする
			throw new IllegalArgumentException("docInvoiceDepositSeqがNULLのためマッピングデータを作成できない。");
		}

		if (LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			// 紐づけるデータがないので何もしない
			return;
		}

		// 登録データリスト
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> insertEntityList = new ArrayList<>();

		// 登録データリストに追加
		depositRecvSeqList.forEach(depositRecvSeq -> {
			TAccgDocInvoiceDepositTDepositRecvMappingEntity insertEntity = new TAccgDocInvoiceDepositTDepositRecvMappingEntity();
			insertEntity.setDocInvoiceDepositSeq(docInvoiceDepositSeq);
			insertEntity.setDepositRecvSeq(depositRecvSeq);

			insertEntityList.add(insertEntity);
		});
		
		// 預り金が他の請求書、精算書で使用されている場合は楽観ロックエラー
		List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao
				.selectAccgDocInvoiceByDepositRecvSeqList(depositRecvSeqList);
		List<TAccgDocInvoiceEntity> otherAccgDocInvoiceList = tAccgDocInvoiceEntityList.stream()
				.filter(entity -> !entity.getAccgDocSeq().equals(accgDocSeq))
				.collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(otherAccgDocInvoiceList)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// マッピングを登録
		this.batchInsertInvoiceDepositTDepositMapping(insertEntityList);

		// マッピングを登録した預り金の「会計書類SEQ（使用先）」に値を設定（更新）
		this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, accgDocSeq);
	}

	/**
	 * 請求書詳細詳細画面、精算書詳細画面の請求データ（報酬）の登録処理
	 * 
	 * @param newFeeList
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void insertInvoiceFee(List<InvoiceRowInputForm> newFeeList, Long accgDocSeq, Long ankenId, Long personId) throws AppException {
		if (LoiozCollectionUtils.isEmpty(newFeeList)) {
			return;
		}
		// 報酬の登録
		List<TFeeEntity> tFeeEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : newFeeList) {
			TFeeEntity entity = new TFeeEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setAnkenId(ankenId);
			entity.setFeeAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount()));
			entity.setFeeItemName(row.getItemName());
			entity.setFeePaymentStatus(FeePaymentStatus.UNCLAIMED.getCd());
			entity.setFeeTimeChargeFlg(row.isFeeTimeChargeFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
			entity.setPersonId(personId);
			entity.setTaxAmount(commonAccgAmountService.calcTaxAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount()), row.getTaxRateType(), TaxFlg.FOREIGN_TAX.getCd()));
			entity.setTaxFlg(TaxFlg.FOREIGN_TAX.getCd());
			entity.setTaxRateType(row.getTaxRateType());
			entity.setWithholdingFlg(row.isWithholdingFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
			if (row.isWithholdingFlg()) {
				entity.setWithholdingAmount(commonAccgAmountService.calcWithholdingAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount())));
			}
			// 取引日、摘要も連動する
			entity.setFeeDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setSumText(row.getSumText());
			tFeeEntityList.add(entity);
		}
		List<TFeeEntity> afterInsertFeeEntityList = this.batchInsertTFee(tFeeEntityList);
		// 登録時の報酬SEQをformリストにセット
		for (int i = 0; i < afterInsertFeeEntityList.size(); i++) {
			newFeeList.get(i).setFeeSeq(afterInsertFeeEntityList.get(i).getFeeSeq());
		}

		// タイムチャージ-報酬付帯情報の登録
		List<InvoiceRowInputForm> newTimeChargeFeeList = newFeeList.stream()
				.filter(form -> form.isFeeTimeChargeFlg())
				.collect(Collectors.toList());
		if (LoiozCollectionUtils.isNotEmpty(newTimeChargeFeeList)) {
			List<TFeeAddTimeChargeEntity> tFeeAddTimeChargeEntityList = new ArrayList<>();
			for (InvoiceRowInputForm row : newTimeChargeFeeList) {
				TFeeAddTimeChargeEntity entity = new TFeeAddTimeChargeEntity();
				entity.setFeeSeq(row.getFeeSeq());
				if (!StringUtils.isEmpty(row.getHourPrice())) {
					entity.setHourPrice(LoiozNumberUtils.parseAsBigDecimal(row.getHourPrice()));
				}
				if (row.getWorkTimeMinute() != null) {
					entity.setWorkTimeMinute(Integer.valueOf(row.getWorkTimeMinute().toString()));
				}
				tFeeAddTimeChargeEntityList.add(entity);
			}
			this.batchInsertTFeeAddTimeCharge(tFeeAddTimeChargeEntityList);
		}

		// 請求項目の登録
		List<TAccgDocInvoiceEntity> accgDocInvoiceEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : newFeeList) {
			TAccgDocInvoiceEntity entity = new TAccgDocInvoiceEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setDocInvoiceOrder(row.getDocInvoiceOrder());
			accgDocInvoiceEntityList.add(entity);
		}
		List<TAccgDocInvoiceEntity> afterInsertAccgDocInvoiceEntityList = this.batchInsertTAccgDocInvoice(accgDocInvoiceEntityList);

		// 登録時の請求項目SEQをformリストにセット
		for (int i = 0; i < afterInsertAccgDocInvoiceEntityList.size(); i++) {
			newFeeList.get(i).setDocInvoiceSeq(afterInsertAccgDocInvoiceEntityList.get(i).getDocInvoiceSeq());
		}

		// 請求項目-報酬の登録
		List<TAccgDocInvoiceFeeEntity> accgDocInvoiceFeeEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : newFeeList) {
			TAccgDocInvoiceFeeEntity entity = new TAccgDocInvoiceFeeEntity();
			entity.setDocInvoiceSeq(row.getDocInvoiceSeq());
			entity.setFeeSeq(row.getFeeSeq());
			entity.setFeeTransactionDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setSumText(row.getSumText());
			accgDocInvoiceFeeEntityList.add(entity);
		}
		this.batchInsertAccgDocInvoiceFee(accgDocInvoiceFeeEntityList);
	}

	/**
	 * 請求書詳細詳細画面、精算書詳細画面の請求その他項目の登録処理
	 * 
	 * @param newOtherList
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void insertInvoiceOther(List<InvoiceRowInputForm> newOtherList, Long accgDocSeq) throws AppException {
		if (LoiozCollectionUtils.isEmpty(newOtherList)) {
			return;
		}
		// 請求項目の登録
		List<TAccgDocInvoiceEntity> accgDocInvoiceEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : newOtherList) {
			TAccgDocInvoiceEntity entity = new TAccgDocInvoiceEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setDocInvoiceOrder(row.getDocInvoiceOrder());
			accgDocInvoiceEntityList.add(entity);
		}
		List<TAccgDocInvoiceEntity> afterInsertAccgDocInvoiceEntityList = this.batchInsertTAccgDocInvoice(accgDocInvoiceEntityList);
		// 登録時の請求項目SEQをformリストにセット
		for (int i = 0; i < afterInsertAccgDocInvoiceEntityList.size(); i++) {
			newOtherList.get(i).setDocInvoiceSeq(afterInsertAccgDocInvoiceEntityList.get(i).getDocInvoiceSeq());
		}

		// 請求その他項目の登録
		List<TAccgDocInvoiceOtherEntity> accgDocInvoiceOtherEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : newOtherList) {
			TAccgDocInvoiceOtherEntity entity = new TAccgDocInvoiceOtherEntity();
			entity.setDocInvoiceSeq(row.getDocInvoiceSeq());
			entity.setOtherTransactionDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setOtherItemType(row.getInvoieType());
			entity.setOtherItemName(row.getItemName());
			entity.setSumText(row.getSumText());
			if (InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType())) {
				entity.setOtherAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount()));
				entity.setDiscountTaxRateType(row.getTaxRateType());
				entity.setDiscountWithholdingFlg(row.isWithholdingFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
			}
			accgDocInvoiceOtherEntityList.add(entity);
		}
		this.batchInsertAccgDocInvoiceOther(accgDocInvoiceOtherEntityList);
	}

	/**
	 * 未精算の報酬を会計書類SEQに紐づけ、請求項目データを登録します（請求書、精算書への紐づけ）。<br>
	 * 未精算報酬情報（取引日、摘要以外）を報酬情報とタイムチャージ-報酬付帯情報へ反映します。<br>
	 * 
	 * @param unPaidFeeList
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void insertUnpaidFeeAndUpdateInvoiceFee(List<InvoiceRowInputForm> unPaidFeeList, Long accgDocSeq,
			Long ankenId, Long personId) throws AppException {
		// 未精算報酬を請求項目に登録（請求書、精算書への紐づけ）。
		commonAccgService.registUnPaidFee(unPaidFeeList, accgDocSeq, ankenId, personId);
		
		// 報酬情報とタイムチャージ-報酬付帯情報を更新
		this.updateFeeAndFeeAddTimeCharge(unPaidFeeList);
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求データ（預り金）の登録処理
	 * 
	 * @param newDpositList
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void insertInvoiceDeposit(List<InvoiceRowInputForm> newDepositList, Long accgDocSeq) throws AppException {
		if (LoiozCollectionUtils.isEmpty(newDepositList)) {
			return;
		}
		// 請求項目の登録
		List<TAccgDocInvoiceEntity> accgDocInvoiceEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : newDepositList) {
			TAccgDocInvoiceEntity entity = new TAccgDocInvoiceEntity();
			entity.setAccgDocSeq(accgDocSeq);
			entity.setDocInvoiceOrder(row.getDocInvoiceOrder());
			accgDocInvoiceEntityList.add(entity);
		}
		List<TAccgDocInvoiceEntity> afterInsertAccgDocInvoiceEntityList = this.batchInsertTAccgDocInvoice(accgDocInvoiceEntityList);
		// 登録時の請求項目SEQをformリストにセット
		for (int i = 0; i < afterInsertAccgDocInvoiceEntityList.size(); i++) {
			newDepositList.get(i).setDocInvoiceSeq(afterInsertAccgDocInvoiceEntityList.get(i).getDocInvoiceSeq());
		}

		// 請求項目-預り金の登録
		List<TAccgDocInvoiceDepositEntity> accgDocInvoiceDepositEntityList = new ArrayList<>();
		for (InvoiceRowInputForm row : newDepositList) {
			TAccgDocInvoiceDepositEntity entity = new TAccgDocInvoiceDepositEntity();
			entity.setDocInvoiceSeq(row.getDocInvoiceSeq());
			entity.setDepositTransactionDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setDepositItemName(row.getItemName());
			entity.setDepositAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount()));
			entity.setInvoiceDepositType(row.getInvoieType());
			entity.setSumText(row.getSumText());
			accgDocInvoiceDepositEntityList.add(entity);
		}
		this.batchInsertAccgDocInvoiceDeposit(accgDocInvoiceDepositEntityList);
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の既入金項目登録
	 * 
	 * @param depositNyukinList
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void registRepay(List<RepayRowInputForm> depositNyukinList, Long accgDocSeq) throws AppException {
		if (LoiozCollectionUtils.isEmpty(depositNyukinList)) {
			return;
		}

		// 既入金登録
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = this.insertRepay(depositNyukinList, accgDocSeq);

		// 既入金項目_預り金テーブルマッピング登録
		this.insertMappingOfRepayAndDeposit(tAccgDocRepayEntityList, depositNyukinList);

		// 預り金データに使用先会計書類SEQをセット
		List<Long> depositRecvSeqList = new ArrayList<>();
		depositNyukinList.stream().forEach(row -> {
			List<Long> seqList = row.getDepositRecvSeqList();
			if (!LoiozCollectionUtils.isEmpty(seqList)) {
				for (Long seq : seqList) {
					depositRecvSeqList.add(seq);
				}
			}
		});

		// マッピングを登録した預り金の「会計書類SEQ（使用先）」を設定（更新）
		this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, accgDocSeq);
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求項目：預り金登録
	 * 
	 * @param invoiceDepositList
	 * @param accgDocSeq
	 * @throws AppException
	 */
	public void registInvoiceDeposit(List<InvoiceRowInputForm> invoiceDepositList, Long accgDocSeq) throws AppException {
		if (LoiozCollectionUtils.isEmpty(invoiceDepositList)) {
			return;
		}

		// 請求項目の登録
		List<TAccgDocInvoiceEntity> afterInsertAccgDocInvoiceEntityList = insertAccgDocInvoice(invoiceDepositList, accgDocSeq);

		// 登録時の請求項目SEQをformリストにセット
		for (int i = 0; i < afterInsertAccgDocInvoiceEntityList.size(); i++) {
			invoiceDepositList.get(i).setDocInvoiceSeq(afterInsertAccgDocInvoiceEntityList.get(i).getDocInvoiceSeq());
		}

		// 請求項目-預り金の登録
		List<TAccgDocInvoiceDepositEntity> afterInsertAccgDocInvoiceDepositEntity = insertAccgDocInvoiceDeposit(invoiceDepositList);

		// 請求項目-預り金データと預り金データのマッピングを作成
		insertMappingOfInvoiceDepositAndDeposit(afterInsertAccgDocInvoiceDepositEntity, invoiceDepositList);

		// 預り金データに使用先会計書類SEQをセット
		List<Long> depositRecvSeqList = new ArrayList<>();
		invoiceDepositList.stream().forEach(row -> {
			List<Long> seqList = row.getDepositRecvSeqList();
			if (!LoiozCollectionUtils.isEmpty(seqList)) {
				for (Long seq : seqList) {
					depositRecvSeqList.add(seq);
				}
			}
		});
		
		// マッピングを登録した預り金の「会計書類SEQ（使用先）」を設定（更新）
		this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, accgDocSeq);
	}

	/**
	 * 既入金項目の入力エリアの保存処理
	 * 
	 * <pre>
	 * 画面サービスに記載するような処理だが、
	 * 請求書と精算書の画面サービスで同じ処理となるためここに定義している。
	 * </pre>
	 * 
	 * @param repayInputForm
	 * @throws AppException
	 */
	public void saveRepay(AccgInvoiceStatementInputForm.RepayInputForm repayInputForm) throws AppException {
		
		Long accgDocSeq = repayInputForm.getAccgDocSeq();
		
		// 既入金項目の各行データ
		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();
		
		// 並び順を採番
		this.numberingDocRepayOrder(repayRowList);
		
		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		//
		// ▼ 請求書テーブル／精算書テーブルの更新
		//
		
		boolean beforeRepaySumFlgOn = false;
		boolean afterRepaySumFlgOn = false;
		
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合

			// 更新前の請求書テーブルのまとめるチェックの状態を取得しておく
			TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntityByAccgDocSeq(accgDocSeq);
			beforeRepaySumFlgOn = SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getRepaySumFlg());
			
			// まとめるチェックONかどうか
			afterRepaySumFlgOn = repayInputForm.isRepaySumFlg();
			
			// 請求書テーブルの既入金項目まとめるチェック、印字フラグ更新
			this.updateTAccgInvoiceRepayDatePringFlgAndRepaySumFlg(repayInputForm.isRepayTransactionDatePrintFlg(), afterRepaySumFlgOn, accgDocSeq);

		} else if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合

			// 更新前の精算書テーブルのまとめるチェックの状態を取得しておく
			TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityByAccgDocSeq(accgDocSeq);
			beforeRepaySumFlgOn = SystemFlg.FLG_ON.equalsByCode(tAccgStatementEntity.getRepaySumFlg());

			// まとめるチェックONかどうか
			afterRepaySumFlgOn = repayInputForm.isRepaySumFlg();
			
			// 精算書テーブルの既入金項目まとめるチェック、印字フラグ更新
			this.updateTAccgStatementRepayDatePringFlgAndRepaySumFlg(repayInputForm.isRepayTransactionDatePrintFlg(), afterRepaySumFlgOn, accgDocSeq);
			
		} else {
			// 想定外の入力値
			throw new RuntimeException("想定外のEnum値");
		}
		
		//
		// ▼ 既入金項目、マッピングテーブルの更新
		//
		
		// まとめられている既入金項目データ（まとめられて1つになっている行）
		List<RepayRowInputForm> sumRepayRowList = repayRowList.stream()
				.filter(row -> row.isRowRepaySumFlg()) // まとめる行
				.collect(Collectors.toList());

		RepayRowInputForm sumRepayRow = null;
		if (!LoiozCollectionUtils.isEmpty(sumRepayRowList)) {
			// まとめる行がある場合
			if (sumRepayRowList.size() > 1) {
				// まとめる行は、0行か1行しかないため、2行以上存在する場合はエラーとする
				throw new RuntimeException("最大で1行しか存在しないまとめ行が2行以上存在している");
			}
			sumRepayRow = sumRepayRowList.get(0);
		}

		// まとめられていない状態の既入金項目データ
		// （※まとめるチェックありの場合、まとめられている行を構成する各行データもここに含まれる）

		// 削除となる既入金項目データ
		List<RepayRowInputForm> deleteRepayRowList = repayRowList.stream()
				.filter(row -> !row.isRowRepaySumFlg()) // まとめる行ではない
				.filter(row -> row.isDeleteFlg())
				.collect(Collectors.toList());

		// 登録となる既入金項目データ
		List<RepayRowInputForm> registRepayRowList = repayRowList.stream()
				.filter(row -> !row.isRowRepaySumFlg()) // まとめる行ではない
				.filter(row -> row.isAddFlg())
				.collect(Collectors.toList());

		// 更新となる既入金項目データ（削除でも、登録でもないもの）
		List<RepayRowInputForm> updateRepayRowList = repayRowList.stream()
				.filter(row -> !row.isRowRepaySumFlg()) // まとめる行ではない
				.filter(row -> !row.isDeleteFlg() && !row.isAddFlg())
				.collect(Collectors.toList());

		if (LoiozCollectionUtils.isEmpty(deleteRepayRowList)
				&& LoiozCollectionUtils.isEmpty(registRepayRowList)
				&& LoiozCollectionUtils.isEmpty(updateRepayRowList)) {
			// 既入金項目の削除、登録、更新がない場合
			// -> 既入金項目、マッピングのデータ更新はない。
			
			// 更新後の状態でのデータの整合性チェック 
			this.checkDataIntegrityAfterSaveRepayNoData(accgDocSeq);
			
			// 請求額／精算額の更新と、再作成フラグの更新（金額は変わらないが、印字フラグの変更をPDFに反映するために行う）
			commonAccgService.updateInvoiceStatementAmount(accgDocSeq);
			
			// 処理終了
			return;
		}

		Long personId = repayInputForm.getPersonId();
		Long ankenId = repayInputForm.getAnkenId();
		
		if (beforeRepaySumFlgOn && afterRepaySumFlgOn) {
			// まとめるチェックが 「ON→ON」 の変更の場合
			
			// 既入金項目の更新（ON→ONのケース）
			this.updateRepayCaseSumCheckONtoON(sumRepayRow, deleteRepayRowList, registRepayRowList, updateRepayRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態でのデータの整合性チェック
			this.checkDataIntegrityAfterSaveRepaySumON(accgDocSeq);

		} else if (!beforeRepaySumFlgOn && afterRepaySumFlgOn) {
			// まとめるチェックが 「OFF→ON」 の変更の場合

			// 既入金項目の更新（OFF→ONのケース）
			this.updateRepayCaseSumCheckOFFtoON(sumRepayRow, deleteRepayRowList, registRepayRowList, updateRepayRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態でのデータの整合性チェック
			this.checkDataIntegrityAfterSaveRepaySumON(accgDocSeq);
			
		} else if (beforeRepaySumFlgOn && !afterRepaySumFlgOn) {
			// まとめるチェックが 「ON→OFF」 の変更の場合

			// 既入金項目の更新（ON→OFFのケース）
			this.updateRepayCaseSumCheckONtoOFF(sumRepayRow, registRepayRowList, updateRepayRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態でのデータの整合性チェック
			this.checkDataIntegrityAfterSaveRepaySumOFF(accgDocSeq);

		} else {
			// まとめるチェックが 「OFF→OFF」 の変更の場合

			// 既入金項目の更新（OFF→OFFのケース）
			this.updateRepayCaseSumCheckOFFtoOFF(deleteRepayRowList, registRepayRowList, updateRepayRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態でのデータの整合性チェック
			this.checkDataIntegrityAfterSaveRepaySumOFF(accgDocSeq);
		}
		
		// 請求額／精算額の更新と、再作成フラグの更新
		commonAccgService.updateInvoiceStatementAmount(accgDocSeq);
	}
	
	/**
	 * 請求項目の入力エリアの保存処理
	 * 
	 * <pre>
	 * 画面サービスに記載するような処理だが、
	 * 請求書と精算書の画面サービスで同じ処理となるためここに定義している。
	 * </pre>
	 * 
	 * @param invoiceInputForm
	 * @throws AppException
	 */
	public void saveInvoice(AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) throws AppException {
		
		Long accgDocSeq = invoiceInputForm.getAccgDocSeq();
		
		// 請求項目の各行データ
		List<InvoiceRowInputForm> invoiceRowList = invoiceInputForm.getInvoiceRowList();
		
		// 未入力行を除外する
		List<InvoiceRowInputForm> validInvoiceRowList = invoiceRowList.stream()
				.filter(form -> !form.isNotEntered()).collect(Collectors.toList());
		
		// 並び順の採番
		this.numberingDocInvoiceOrder(validInvoiceRowList);
		
		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		
		//
		// ▼ 請求書テーブル／精算書テーブルの更新
		//
		
		boolean beforeExpenseSumFlgOn = false;
		boolean afterExpenseSumFlgOn = false;
		
		AccgDocType accgDocType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (accgDocType == AccgDocType.INVOICE) {
			// 請求書の場合
			
			// 更新前の請求書テーブルのまとめるチェックの状態を取得しておく
			TAccgInvoiceEntity tAccgInvoiceEntity = this.getAccgInvoiceEntityByAccgDocSeq(accgDocSeq);
			beforeExpenseSumFlgOn = SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getExpenseSumFlg());
			
			// まとめるチェックONかどうか
			afterExpenseSumFlgOn = invoiceInputForm.isExpenseSumFlg();

			// 請求書テーブルの請求項目（実費）まとめるチェック、印字フラグ更新
			this.updateTAccgInvoiceInvoiceDatePringFlgAndExpenseSumFlg(invoiceInputForm.isInvoiceTransactionDatePrintFlg(), afterExpenseSumFlgOn, accgDocSeq);
			
		} else if (accgDocType == AccgDocType.STATEMENT) {
			// 精算書の場合
			
			// 更新前の精算書テーブルのまとめるチェックの状態を取得しておく
			TAccgStatementEntity tAccgStatementEntity = this.getAccgStatementEntityByAccgDocSeq(accgDocSeq);
			beforeExpenseSumFlgOn = SystemFlg.FLG_ON.equalsByCode(tAccgStatementEntity.getExpenseSumFlg());
			
			// まとめるチェックONかどうか
			afterExpenseSumFlgOn = invoiceInputForm.isExpenseSumFlg();

			// 精算書テーブルの請求項目（実費）まとめるチェック、印字フラグ更新
			this.updateTAccgStatementInvoiceDatePringFlgAndExpenseSumFlg(invoiceInputForm.isInvoiceTransactionDatePrintFlg(), afterExpenseSumFlgOn, accgDocSeq);
			
		} else {
			// 想定外の入力値
			throw new RuntimeException("想定外のEnum値");
		}
		
		Long personId = invoiceInputForm.getPersonId();
		Long ankenId = invoiceInputForm.getAnkenId();
		
		//
		// ▼ 報酬項目、その他項目、預り金請求項目の更新
		//
		
		// 報酬項目か、その他項目のリスト
		List<InvoiceRowInputForm> feeOrOtherRowList = this.getFeeOrOtherRowList(validInvoiceRowList);
		
		// 【削除、登録、更新】報酬、その他（テキスト、値引き）のデータの更新
		this.saveInvoiceFeeOrOther(feeOrOtherRowList, ankenId, personId, accgDocSeq);

		// 預り金項目のリスト
		List<InvoiceRowInputForm> depositRowList = this.getDepositRowList(validInvoiceRowList);

		// 【削除、登録、更新】預り金項目のデータの更新
		this.saveInvoiceDeposit(depositRowList, accgDocSeq);

		// 報酬データの登録上限チェック
		this.checkFeeRegistLimitAfterSaveAccgDocInvoice(ankenId, personId);
		
		// 請求書／精算書の金額情報Dtoを取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);
		
		// Dtoの値をもとに金額を再計算し、
		// 請求項目-消費税、請求項目-源泉徴収税のデータを更新
		commonAccgService.recalcAndUpdateAccgInvoiceTaxAndWithholding(accgDocSeq, accgInvoiceStatementAmountDto);
		
		//
		// ▼ 実費請求項目の更新
		//
		
		// 実費項目のリスト
		List<InvoiceRowInputForm> invoiceExpenseRowList = validInvoiceRowList.stream()
				.filter(form -> form.isDepositRecvFlg())
				.filter(form -> InvoiceDepositType.EXPENSE.equalsByCode(form.getInvoieType()))
				.collect(Collectors.toList());

		// まとめられている実費項目データ（まとめられて1つになっている行）
		List<InvoiceRowInputForm> sumInvoiceExpenseRowList = invoiceExpenseRowList.stream()
				.filter(row -> row.isRowExpenseSumFlg()) // まとめる行
				.collect(Collectors.toList());

		InvoiceRowInputForm sumInvoiceExpenseRow = null;
		if (!LoiozCollectionUtils.isEmpty(sumInvoiceExpenseRowList)) {
			// まとめる行がある場合
			if (sumInvoiceExpenseRowList.size() > 1) {
				// まとめる行は、0行か1行しかないため、2行以上存在する場合はエラーとする
				throw new RuntimeException("最大で1行しか存在しないまとめ行が2行以上存在している");
			}
			sumInvoiceExpenseRow = sumInvoiceExpenseRowList.get(0);
		}

		// まとめられていない状態の実費項目データ
		// （※まとめるチェックありの場合、まとめられている行を構成する各行データもここに含まれる）

		// 削除となる実費項目データ
		List<InvoiceRowInputForm> deleteInvoiceExpenseRowList = invoiceExpenseRowList.stream()
				.filter(row -> !row.isRowExpenseSumFlg()) // まとめる行ではない
				.filter(row -> row.isDeleteFlg())
				.collect(Collectors.toList());
		
		// 登録となる実費項目データ
		List<InvoiceRowInputForm> registInvoiceExpenseRowList = invoiceExpenseRowList.stream()
				.filter(row -> !row.isRowExpenseSumFlg()) // まとめる行ではない
				.filter(row -> row.isAddFlg())
				.collect(Collectors.toList());
		
		// 更新となる実費項目データ（削除でも、登録でもないもの）
		List<InvoiceRowInputForm> updateInvoiceExpenseRowList = invoiceExpenseRowList.stream()
				.filter(row -> !row.isRowExpenseSumFlg()) // まとめる行ではない
				.filter(row -> !row.isDeleteFlg() && !row.isAddFlg())
				.collect(Collectors.toList());

		if (LoiozCollectionUtils.isEmpty(deleteInvoiceExpenseRowList)
					&& LoiozCollectionUtils.isEmpty(registInvoiceExpenseRowList)
					&& LoiozCollectionUtils.isEmpty(updateInvoiceExpenseRowList)) {
			// 実費項目の削除、登録、更新がない場合
			// -> 実費項目、マッピングのデータ更新はない。（ここで処理終了）

			// 更新後の状態での、請求項目-預り金データの整合性チェック
			this.checkDataIntegrityAfterSaveInvoiceExpenseNoData(accgDocSeq);

			// 請求額／精算額の更新と、再作成フラグの更新
			commonAccgService.updateInvoiceStatementAmount(accgDocSeq);
			
			return;
		}

		if (beforeExpenseSumFlgOn && afterExpenseSumFlgOn) {
			// まとめるチェックが 「ON→ON」 の変更の場合

			// 請求項目（実費）の更新（ON→ONのケース）
			this.updateInvoiceExpenseCaseSumCheckONtoON(sumInvoiceExpenseRow, deleteInvoiceExpenseRowList, registInvoiceExpenseRowList, updateInvoiceExpenseRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態での、請求項目-預り金データの整合性チェック
			this.checkDataIntegrityAfterSaveInvoiceExpenseSumON(accgDocSeq);

		} else if (!beforeExpenseSumFlgOn && afterExpenseSumFlgOn) {
			// まとめるチェックが 「OFF→ON」 の変更の場合

			// 請求項目（実費）の更新（OFF→ONのケース）
			this.updateInvoiceExpenseCaseSumCheckOFFtoON(sumInvoiceExpenseRow, deleteInvoiceExpenseRowList, registInvoiceExpenseRowList, updateInvoiceExpenseRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態での、請求項目-預り金データの整合性チェック
			this.checkDataIntegrityAfterSaveInvoiceExpenseSumON(accgDocSeq);
			
		} else if (beforeExpenseSumFlgOn && !afterExpenseSumFlgOn) {
			// まとめるチェックが 「ON→OFF」 の変更の場合
			
			// 請求項目（実費）の更新（ON→OFFのケース）
			this.updateInvoiceExpenseCaseSumCheckONtoOFF(sumInvoiceExpenseRow, registInvoiceExpenseRowList, updateInvoiceExpenseRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態での、請求項目-預り金データの整合性チェック
			this.checkDataIntegrityAfterSaveInvoiceExpenseSumOFF(accgDocSeq);

		} else {
			// まとめるチェックが 「OFF→OFF」 の変更の場合

			// 請求項目（実費）の更新（OFF→OFFのケース）
			this.updateInvoiceExpenseCaseSumCheckOFFtoOFF(deleteInvoiceExpenseRowList, registInvoiceExpenseRowList, updateInvoiceExpenseRowList,
					personId, ankenId, accgDocSeq);
			// 更新後の状態での、請求項目-預り金データの整合性チェック
			this.checkDataIntegrityAfterSaveInvoiceExpenseSumOFF(accgDocSeq);
			
		}
		
		// 請求額／精算額の更新と、再作成フラグの更新
		commonAccgService.updateInvoiceStatementAmount(accgDocSeq);
	}
	
	/**
	 * 請求書詳細画面、精算書詳細画面の既入金データの更新処理
	 * 
	 * @param updateFeeList
	 * @throws AppException
	 */
	public void updateRepay(List<RepayRowInputForm> updateRepayList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(updateRepayList)) {
			return;
		}
		// 既入金の更新
		List<Long> docRepaySeqList = updateRepayList.stream()
				.filter(form -> form.getDocRepaySeq() != null)
				.map(row -> row.getDocRepaySeq())
				.collect(Collectors.toList());
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByDocRepaySeqList(docRepaySeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocRepayEntityList)) {
			throw new DataNotFoundException("既入金情報が存在しません。[docRepaySeqList=" + docRepaySeqList + "]");
		}
		// 更新データをentityにセット
		for (TAccgDocRepayEntity entity : tAccgDocRepayEntityList) {
			Optional<RepayRowInputForm> repayRowInputFormOpt = updateRepayList.stream().filter(row -> row.getDocRepaySeq().equals(entity.getDocRepaySeq())).findFirst();
			if (repayRowInputFormOpt.isPresent()) {
				RepayRowInputForm repayRowInputForm = repayRowInputFormOpt.get();
				entity.setRepayTransactionDate(DateUtils.parseToLocalDate(repayRowInputForm.getRepayTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				entity.setRepayItemName(repayRowInputForm.getRepayItemName());
				entity.setRepayAmount(LoiozNumberUtils.parseAsBigDecimal(repayRowInputForm.getRepayAmount()));
				entity.setSumText(repayRowInputForm.getSumText());
				entity.setDocRepayOrder(repayRowInputForm.getDocRepayOrder());
			}
		}
		this.batchUpdateTAccgDocRepay(tAccgDocRepayEntityList);
	}

	/**
	 * 既入金のまとめ行の更新を行う<br>
	 * （現在のマッピングデータから金額を算出、取引日／項目名／摘要は引数のInputFormの値で更新する）
	 * 
	 * @param sumDocRepaySeq
	 * @param sumRepayRow
	 * @throws AppException
	 */
	public void updateSumRepayRow(Long sumDocRepaySeq, RepayRowInputForm sumRepayRow) throws AppException {

		//
		// ▼ 現在のマッピング状態から、まとめ行に設定する金額を算出
		//

		// 既入金項目SEQに紐づく既入金預り金マッピングデータ取得
		List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
				.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(sumDocRepaySeq);
		if (LoiozCollectionUtils.isEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// まとめられている預り金SEQを取得
		List<Long> depositRecvSeqList = tAccgDocRepayTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
		// 対象の預り金データを取得
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (LoiozCollectionUtils.isEmpty(tDepositRecvEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 預り金の入金額合計
		List<BigDecimal> depositAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getDepositAmount()).collect(Collectors.toList());
		BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);

		//
		// ▼ 更新処理
		//

		// 更新対象取得（既入金項目データ）
		TAccgDocRepayEntity tAccgDocRepayEntity = tAccgDocRepayDao.selectDocRepayByDocRepaySeq(sumDocRepaySeq);
		if (tAccgDocRepayEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 金額
		tAccgDocRepayEntity.setRepayAmount(totalDepositAmount);
		// 取引日
		tAccgDocRepayEntity.setRepayTransactionDate(DateUtils.parseToLocalDate(sumRepayRow.getRepayTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		// 項目名
		tAccgDocRepayEntity.setRepayItemName(sumRepayRow.getRepayItemName());
		// 摘要
		tAccgDocRepayEntity.setSumText(sumRepayRow.getSumText());

		// 更新
		this.updateTAccgDocRepay(tAccgDocRepayEntity);
	}
	
	/**
	 * 請求項目-預り金（実費）のまとめ行の更新を行う<br>
	 * （現在のマッピングデータから金額を算出、取引日／項目名／摘要は引数のInputFormの値で更新する）<br>
	 * ※請求項目テーブルの並び順の値も更新する
	 * 
	 * @param sumDocInvoiceDepositSeq まとめ行の請求項目-預り金（実費）のSEQ
	 * @param sumInvoiceRow まとめ行のInputForm
	 * @throws AppException
	 */
	public void updateSumInvoiceRow(Long sumDocInvoiceDepositSeq, InvoiceRowInputForm sumInvoiceRow) throws AppException {
		
		//
		// ▼ 現在のマッピング状態から、まとめ行に設定する金額を算出
		//

		// 請求項目-預り金SEQに紐づく請求項目-預り金（実費）マッピングデータ取得
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
				.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(sumDocInvoiceDepositSeq);
		if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// まとめられている預り金SEQを取得
		List<Long> depositRecvSeqList = tAccgDocInvoiceDepositTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
		// 対象の預り金データを取得
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (LoiozCollectionUtils.isEmpty(tDepositRecvEntityList)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 預り金の出金額合計
		List<BigDecimal> depositAmountList = tDepositRecvEntityList.stream().map(entity -> entity.getWithdrawalAmount()).collect(Collectors.toList());
		BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);

		//
		// ▼  請求項目-預り金の更新処理
		//

		// 更新対象取得（請求項目-預り金データ）
		TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceDepositSeq(sumDocInvoiceDepositSeq);
		if (tAccgDocInvoiceDepositEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 金額
		tAccgDocInvoiceDepositEntity.setDepositAmount(totalDepositAmount);
		// 取引日
		tAccgDocInvoiceDepositEntity.setDepositTransactionDate(DateUtils.parseToLocalDate(sumInvoiceRow.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		// 項目名
		tAccgDocInvoiceDepositEntity.setDepositItemName(sumInvoiceRow.getItemName());
		// 摘要
		tAccgDocInvoiceDepositEntity.setSumText(sumInvoiceRow.getSumText());

		// 更新
		this.updateTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntity);
		
		//
		// ▼  請求項目の更新（並び順の値の更新）
		//
		
		// 更新対象
		Long docInvoiceSeq = tAccgDocInvoiceDepositEntity.getDocInvoiceSeq();
		TAccgDocInvoiceEntity tAccgDocInvoiceEntity = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeq(docInvoiceSeq);
		if (tAccgDocInvoiceEntity == null) {
			throw new DataNotFoundException("請求項目情報が存在しません。[docInvoiceSeq=" + docInvoiceSeq + "]");
		}
		
		// 並び順
		tAccgDocInvoiceEntity.setDocInvoiceOrder(sumInvoiceRow.getDocInvoiceOrder());
		
		// 更新
		this.updateTAccgDocInvoice(tAccgDocInvoiceEntity);
	}

	/**
	 * 既入金のまとめ行の登録を行う<br>
	 * （各行のInputFormから金額を算出、取引日／項目名／摘要はまとめ行のInputFormの値で登録する）
	 * 
	 * <pre>
	 * 注意：本処理（まとめ行の登録）は、まとめ行以外の既入金項目データを全て削除したあとに行うこと。
	 * 　　　本処理実行時に、他の既入金項目が存在する場合は楽観ロックエラーとする。
	 * </pre>
	 * 
	 * @param sumRepayRow まとめ行のInputForm
	 * @param repayRowInputFormList まとめ行を構成する各行のInputForm
	 * @return 登録したEntity
	 * @throws AppException まとめ行を登録しようとしている会計書類に、既入金項目データがすでに存在する場合
	 */
	public TAccgDocRepayEntity insertSumRepayRow(RepayRowInputForm sumRepayRow, List<RepayRowInputForm> repayRowInputFormList) throws AppException {

		Long accgDocSeq = sumRepayRow.getAccgDocSeq();

		// 登録されている既入金項目データ取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		if (!LoiozCollectionUtils.isEmpty(tAccgDocRepayEntityList)) {
			// 他の既入金項目データが登録されている場合 -> 楽観ロックエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 並び順（既入金項目にまとめ行が存在する場合、必ずその行1つしか存在しないため並び順は1になる。）
		Long docRepayOrder = 1L;

		// 金額を算出

		List<BigDecimal> repayAmountList = repayRowInputFormList.stream().map(inputForm -> new BigDecimal(inputForm.getRepayAmount())).collect(Collectors.toList());
		BigDecimal repayAmountSum = AccountingUtils.calcTotal(repayAmountList);

		// 登録処理

		TAccgDocRepayEntity tAccgDocRepayEntity = new TAccgDocRepayEntity();

		// 金額
		tAccgDocRepayEntity.setRepayAmount(repayAmountSum);

		// 会計書類SEQ
		tAccgDocRepayEntity.setAccgDocSeq(sumRepayRow.getAccgDocSeq());
		// 取引日
		tAccgDocRepayEntity.setRepayTransactionDate(DateUtils.parseToLocalDate(sumRepayRow.getRepayTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		// 項目名
		tAccgDocRepayEntity.setRepayItemName(sumRepayRow.getRepayItemName());
		// 摘要
		tAccgDocRepayEntity.setSumText(sumRepayRow.getSumText());

		// 並び順
		tAccgDocRepayEntity.setDocRepayOrder(docRepayOrder);

		// 登録
		return this.registTAccgDocRepay(tAccgDocRepayEntity);
	}
	
	/**
	 * 請求項目（実費）のまとめ行の登録を行う<br>
	 * （各行のInputFormから金額を算出、取引日／項目名／摘要はまとめ行のInputFormの値で登録する）
	 * 
	 * <pre>
	 * 注意：本処理（まとめ行の登録）は、まとめ行以外の請求項目（実費）データを全て削除したあとに行うこと。
	 * 　　　本処理実行時に、他の請求項目（実費）が存在する場合は楽観ロックエラーとする。
	 * </pre>
	 * 
	 * @param sumInvoiceRow まとめ行のInputForm
	 * @param invoiceRowInputFormList まとめ行を構成する各行のInputForm
	 * @return 登録したEntity
	 * @throws AppException まとめ行を登録しようとしている会計書類に、請求項目（実費）のデータがすでに存在する場合
	 */
	public TAccgDocInvoiceDepositEntity insertSumInvoiceRow(InvoiceRowInputForm sumInvoiceRow, List<InvoiceRowInputForm> invoiceRowInputFormList) throws AppException {

		Long accgDocSeq = sumInvoiceRow.getAccgDocSeq();
		Long docInvoiceOrder = sumInvoiceRow.getDocInvoiceOrder();

		// 登録されている請求項目-預り金データ取得
		List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByAccgDocSeq(accgDocSeq);
		
		// 取得した請求項目-預り金データに、実費データが存在していないことをチェック
		Optional<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityExpenseOpt = tAccgDocInvoiceDepositEntityList.stream()
			.filter(e -> InvoiceDepositType.EXPENSE.equalsByCode(e.getInvoiceDepositType()))
			.findAny();
		if (tAccgDocInvoiceDepositEntityExpenseOpt.isPresent()) {
			// 請求項目に、他の実費データが登録されている場合 -> 楽観ロックエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 請求項目の登録
		//
		
		TAccgDocInvoiceEntity insertAccgDocInvoiceEntity = new TAccgDocInvoiceEntity();
		insertAccgDocInvoiceEntity.setAccgDocSeq(accgDocSeq);
		insertAccgDocInvoiceEntity.setDocInvoiceOrder(docInvoiceOrder);
		// 登録
		this.insertTAccgDocInvoice(insertAccgDocInvoiceEntity);
		
		//
		// ▼ 請求項目-預り金の登録
		//

		// 金額を算出

		List<BigDecimal> amountList = invoiceRowInputFormList.stream().map(inputForm -> new BigDecimal(inputForm.getAmount())).collect(Collectors.toList());
		BigDecimal amountSum = AccountingUtils.calcTotal(amountList);

		// 登録処理

		TAccgDocInvoiceDepositEntity tAccgDocInvoiceDepositEntity = new TAccgDocInvoiceDepositEntity();

		// 請求項目SEQ
		tAccgDocInvoiceDepositEntity.setDocInvoiceSeq(insertAccgDocInvoiceEntity.getDocInvoiceSeq());
		// 預り金タイプは固定で「実費」とする
		tAccgDocInvoiceDepositEntity.setInvoiceDepositType(InvoiceDepositType.EXPENSE.getCd());
		
		// 金額
		tAccgDocInvoiceDepositEntity.setDepositAmount(amountSum);

		// 取引日
		tAccgDocInvoiceDepositEntity.setDepositTransactionDate(DateUtils.parseToLocalDate(sumInvoiceRow.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		// 項目名
		tAccgDocInvoiceDepositEntity.setDepositItemName(sumInvoiceRow.getItemName());
		// 摘要
		tAccgDocInvoiceDepositEntity.setSumText(sumInvoiceRow.getSumText());

		// 登録
		return this.registTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntity);
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求データ（報酬）の更新処理
	 * 
	 * @param updateFeeList
	 * @throws AppException
	 */
	public void updateInvoiceFee(List<InvoiceRowInputForm> updateFeeList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(updateFeeList)) {
			return;
		}
		
		// 報酬情報とタイムチャージ-報酬付帯情報を更新
		this.updateFeeAndFeeAddTimeCharge(updateFeeList);
		
		// 請求項目の更新
		List<Long> docInvoiceSeqList = updateFeeList.stream()
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toList());
		List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
			throw new DataNotFoundException("請求項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
		}
		
		// データをentityにセット
		for (TAccgDocInvoiceEntity entity : tAccgDocInvoiceEntityList) {
			Optional<InvoiceRowInputForm> invoiceRowInputFormOpt = updateFeeList.stream().filter(row -> row.getDocInvoiceSeq().equals(entity.getDocInvoiceSeq())).findFirst();
			if (invoiceRowInputFormOpt.isPresent()) {
				InvoiceRowInputForm invoiceRowInputForm = invoiceRowInputFormOpt.get();
				entity.setDocInvoiceOrder(invoiceRowInputForm.getDocInvoiceOrder());
			}
		}
		// 請求項目一括更新
		this.batchUpdateTAccgDocInvoice(tAccgDocInvoiceEntityList);
		
		// 請求項目-報酬の更新
		List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeBydocInvoiceSeqList(docInvoiceSeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceFeeEntityList)) {
			throw new DataNotFoundException("請求項目-報酬情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
		}
		
		// データをentityにセット
		for (TAccgDocInvoiceFeeEntity entity : tAccgDocInvoiceFeeEntityList) {
			Optional<InvoiceRowInputForm> invoiceRowInputFormOpt = updateFeeList.stream().filter(row -> row.getDocInvoiceSeq().equals(entity.getDocInvoiceSeq())).findFirst();
			invoiceRowInputFormOpt.ifPresent(row -> {
				entity.setFeeTransactionDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				entity.setSumText(row.getSumText());
			});
		}
		// 請求項目-報酬一括更新
		this.batchUpdateTAccgDocInvoiceFee(tAccgDocInvoiceFeeEntityList);
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求データ（預り金）の更新処理
	 * 
	 * @param updateDepositList
	 * @throws AppException
	 */
	public void updateInvoiceDeposit(List<InvoiceRowInputForm> updateDepositList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(updateDepositList)) {
			return;
		}
		// 請求項目の更新
		List<Long> docInvoiceSeqList = updateDepositList.stream()
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toList());
		if (LoiozCollectionUtils.isNotEmpty(docInvoiceSeqList)) {
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
				throw new DataNotFoundException("請求項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			// データをentityにセット
			for (TAccgDocInvoiceEntity entity : tAccgDocInvoiceEntityList) {
				Optional<InvoiceRowInputForm> invoiceRowInputFormOpt = updateDepositList.stream().filter(row -> row.getDocInvoiceSeq().equals(entity.getDocInvoiceSeq())).findFirst();
				if (invoiceRowInputFormOpt.isPresent()) {
					InvoiceRowInputForm invoiceRowInputForm = invoiceRowInputFormOpt.get();
					entity.setDocInvoiceOrder(invoiceRowInputForm.getDocInvoiceOrder());
				}
			}
			this.batchUpdateTAccgDocInvoice(tAccgDocInvoiceEntityList);
		}

		// 請求項目-預り金の更新
		if (LoiozCollectionUtils.isNotEmpty(docInvoiceSeqList)) {
			List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceSeqList(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositEntityList)) {
				throw new DataNotFoundException("請求項目-預り金情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			// データをentityにセット
			for (TAccgDocInvoiceDepositEntity entity : tAccgDocInvoiceDepositEntityList) {
				Optional<InvoiceRowInputForm> invoiceRowInputFormOpt = updateDepositList.stream().filter(row -> row.getDocInvoiceSeq().equals(entity.getDocInvoiceSeq())).findFirst();
				invoiceRowInputFormOpt.ifPresent(row -> {
					entity.setDepositAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount()));
					entity.setDepositItemName(row.getItemName());
					entity.setDepositTransactionDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
					entity.setInvoiceDepositType(row.getInvoieType());
					entity.setSumText(row.getSumText());
				});
			}
			this.batchUpdateTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntityList);
		}
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求その他項目の更新処理
	 * 
	 * @param updateOtherList
	 * @throws AppException
	 */
	public void updateInvoiceOther(List<InvoiceRowInputForm> updateOtherList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(updateOtherList)) {
			return;
		}
		// 請求項目の更新
		List<Long> docInvoiceSeqList = updateOtherList.stream()
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toList());
		List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
			throw new DataNotFoundException("請求項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
		}
		// データをentityにセット
		for (TAccgDocInvoiceEntity entity : tAccgDocInvoiceEntityList) {
			Optional<InvoiceRowInputForm> invoiceRowInputFormOpt = updateOtherList.stream().filter(row -> row.getDocInvoiceSeq().equals(entity.getDocInvoiceSeq())).findFirst();
			if (invoiceRowInputFormOpt.isPresent()) {
				InvoiceRowInputForm invoiceRowInputForm = invoiceRowInputFormOpt.get();
				entity.setDocInvoiceOrder(invoiceRowInputForm.getDocInvoiceOrder());
			}
		}
		this.batchUpdateTAccgDocInvoice(tAccgDocInvoiceEntityList);

		// 請求その他項目の更新
		List<TAccgDocInvoiceOtherEntity> tAccgDocInvoiceOtherEntityList = tAccgDocInvoiceOtherDao.selectAccgDocInvoiceOtherByDocInvoiceSeq(docInvoiceSeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceOtherEntityList)) {
			throw new DataNotFoundException("請求その他項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
		}
		// データをEntityにセット
		for (TAccgDocInvoiceOtherEntity entity : tAccgDocInvoiceOtherEntityList) {
			Optional<InvoiceRowInputForm> invoiceRowInputFormOpt = updateOtherList.stream().filter(row -> row.getDocInvoiceSeq().equals(entity.getDocInvoiceSeq())).findFirst();
			invoiceRowInputFormOpt.ifPresent(row -> {
				if (InvoiceOtherItemType.DISCOUNT.equalsByCode(row.getInvoieType())) {
					entity.setDiscountTaxRateType(row.getTaxRateType());
					entity.setDiscountWithholdingFlg(row.isWithholdingFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
					entity.setOtherAmount(LoiozNumberUtils.parseAsBigDecimal(row.getAmount()));
					entity.setSumText(row.getSumText());
				} else {
					entity.setDiscountTaxRateType(null);
					entity.setDiscountWithholdingFlg(null);
					entity.setOtherAmount(null);
					entity.setSumText(null);
				}
				entity.setOtherItemName(row.getItemName());
				entity.setOtherItemType(row.getInvoieType());
				entity.setOtherTransactionDate(DateUtils.parseToLocalDate(row.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			});
		}
		this.batchUpdateTAccgDocInvoiceOther(tAccgDocInvoiceOtherEntityList);
	}

	/**
	 * 会計情報のドキュメントタイプを変更します<br>
	 * 
	 * @param accgDocSeq
	 * @param accgDocType
	 * @throws AppException
	 */
	public void updateAccgDocType(Long accgDocSeq, AccgDocType accgDocType) throws AppException {
		// 会計情報取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			throw new DataNotFoundException("会計書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}

		// ドキュメントタイプ変更
		tAccgDocEntity.setAccgDocType(accgDocType.getCd());
		int updateCount = 0;
		try {
			updateCount = tAccgDocDao.update(tAccgDocEntity);
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
	 * 請求書を下書き状態に戻します。<br>
	 * 請求書に紐づく取引実績、売上、回収不能金を削除します。
	 * 
	 * @param accgDocSeq
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void updateInvoiceToDraftAndRemoveRelatedData(Long accgDocSeq, Long personId, Long ankenId) throws AppException {
		if (accgDocSeq == null) {
			return;
		}
		
		// 請求書情報更新前の情報を取得
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// この請求書から作成された預り金請求のデータが、他の請求書／精算書で利用されているか
		boolean isCreatedDepositInvoiceAndUsingOther = commonAccgService.isCreatedDepositInvoiceAndUsingOther(accgDocSeq);
		if (isCreatedDepositInvoiceAndUsingOther) {
			// 利用されている場合
			// -> 下書きに戻すことは不可とする
			throw new AppException(MessageEnum.MSG_E00190, null, "この請求書で請求を行った預り金が、<br>他の請求書／精算書で利用されている");
		}
		
		// 請求書情報から売上明細SEQ、回収不能詳細SEQを取得
		Long salesDetailSeq = tAccgInvoiceEntity.getSalesDetailSeq();
		Long uncollectibleDetailSeq = tAccgInvoiceEntity.getUncollectibleDetailSeq();

		// 請求書を下書きに変更
		this.updateInvoiceToDraft(tAccgInvoiceEntity);

		// 下書きに変更時の共通処理
		this.commonProcessWhenChangingToDraft(accgDocSeq, personId, ankenId, salesDetailSeq, uncollectibleDetailSeq);
	}

	/**
	 * 精算書を下書き状態に戻します。<br>
	 * 精算書に紐づく取引実績、売上を削除します。
	 * 
	 * @param accgDocSeq
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void updateStatementToDraftAndRemoveRelatedData(Long accgDocSeq, Long personId, Long ankenId) throws AppException {
		if (accgDocSeq == null) {
			return;
		}
		// 精算書情報取得
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 請求書情報から売上明細SEQを取得
		Long salesDetailSeq = tAccgStatementEntity.getSalesDetailSeq();

		// 精算書を下書きに変更
		this.updateStatementToDraft(tAccgStatementEntity);

		// 下書きに変更時の共通処理
		this.commonProcessWhenChangingToDraft(accgDocSeq, personId, ankenId, salesDetailSeq, null);
	}

	/**
	 * 既入金項目SEQに紐づく既入金項目情報を一括削除
	 * 
	 * @param deleteRepayList
	 * @throws AppException
	 */
	public void deleteTAccgDocRepay(List<Long> docRepaySeqList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(docRepaySeqList)) {
			return;
		}

		// 既入金情報取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByDocRepaySeqList(docRepaySeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocRepayEntityList)) {
			throw new DataNotFoundException("既入金情報が存在しません。[docRepaySeqList=" + docRepaySeqList + "]");
		}

		// 削除
		this.batchDeleteTAccgDocRepay(tAccgDocRepayEntityList);
	}

	/**
	 * 既入金項目SEQに紐づく既入金項目_預り金マッピング情報を一括削除
	 * 
	 * @param docRepaySeq
	 * @return
	 * @throws AppException
	 */
	public List<TAccgDocRepayTDepositRecvMappingEntity> deleteRepayDepositMappingByRepaySeq(Long docRepaySeq) throws AppException {

		if (docRepaySeq == null) {
			throw new IllegalArgumentException("引数のdocRepaySeqがNULL");
		}

		// 削除対象取得
		List<TAccgDocRepayTDepositRecvMappingEntity> repayDepositMappingEntityList = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(docRepaySeq);
		// マッピング削除
		this.batchDeleteTAccgDocRepayTDepositRecvMapping(repayDepositMappingEntityList);

		// マッピングが削除された預り金SEQ
		List<Long> mappingDeletedDepositRecvSeqList = repayDepositMappingEntityList.stream()
				.map(entity -> entity.getDepositRecvSeq())
				.collect(Collectors.toList());

		// マッピングが削除された預り金の「会計書類SEQ（使用先）」をNULLで更新
		Long usingAccgDocSeq = null;
		this.updateTDepositRecvUsingAccgDocSeq(mappingDeletedDepositRecvSeqList, usingAccgDocSeq);

		return repayDepositMappingEntityList;
	}
	
	/**
	 * 請求項目-預り金SEQに紐づく請求項目-預り金マッピング情報を一括削除
	 * 
	 * @param docInvoiceDepositSeq
	 * @return
	 * @throws AppException
	 */
	public List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> deleteInvoiceDepositMappingByInvoiceDepositSeq(Long docInvoiceDepositSeq) throws AppException {

		if (docInvoiceDepositSeq == null) {
			throw new IllegalArgumentException("引数のdocInvoiceDepositSeqがNULL");
		}

		// 削除対象取得
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> invoiceDepositMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(docInvoiceDepositSeq);
		// マッピング削除
		this.batchDeleteTAccgDocInvoiceDepositTDepositRecvMapping(invoiceDepositMappingEntityList);

		// マッピングが削除された預り金SEQ
		List<Long> mappingDeletedDepositRecvSeqList = invoiceDepositMappingEntityList.stream()
				.map(entity -> entity.getDepositRecvSeq())
				.collect(Collectors.toList());

		// マッピングが削除された預り金の「会計書類SEQ（使用先）」をNULLで更新
		Long usingAccgDocSeq = null;
		this.updateTDepositRecvUsingAccgDocSeq(mappingDeletedDepositRecvSeqList, usingAccgDocSeq);

		return invoiceDepositMappingEntityList;
	}

	/**
	 * 預り金SEQに紐づく既入金項目_預り金マッピング情報を一括削除します
	 * 
	 * @param depositRecvSeqList
	 * @throws AppException
	 */
	public void deleteRepayDepositMappingByDepositSeq(List<Long> depositRecvSeqList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			return;
		}

		// 既入金項目_預り金マッピング情報取得
		List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeqList(depositRecvSeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
			throw new DataNotFoundException("既入金項目_預り金マッピング情報が存在しません。[depositRecvSeqList=" + depositRecvSeqList + "]");
		}

		// マッピングを削除削除
		this.batchDeleteTAccgDocRepayTDepositRecvMapping(tAccgDocRepayTDepositRecvMappingEntityList);

		// マッピングを削除した預り金の「会計書類SEQ（使用先）」をNULLで更新
		Long usingAccgDocSeq = null;
		this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, usingAccgDocSeq);
	}
	
	/**
	 * 預り金SEQに紐づく請求項目-預り金マッピング情報を一括削除します
	 * 
	 * @param depositRecvSeqList
	 * @throws AppException
	 */
	public void deleteInvoiceDepositMappingByDepositSeq(List<Long> depositRecvSeqList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			return;
		}

		// 請求項目-預り金マッピング情報取得
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeqList(depositRecvSeqList);
		if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
			throw new DataNotFoundException("請求項目-預り金マッピング情報が存在しません。[depositRecvSeqList=" + depositRecvSeqList + "]");
		}

		// マッピングを削除
		this.batchDeleteTAccgDocInvoiceDepositTDepositRecvMapping(tAccgDocInvoiceDepositTDepositRecvMappingEntityList);

		// マッピングを削除した預り金の「会計書類SEQ（使用先）」をNULLで更新
		Long usingAccgDocSeq = null;
		this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, usingAccgDocSeq);
	}

	/**
	 * 対象の預り金データの、「会計書類SEQ（使用先）」の値を更新する
	 * 
	 * @param depositRecvSeqList 更新対象の預り金のSEQ
	 * @param usingAccgDocSeq 更新値（NULLもOK。NULLで更新する動作になる。）
	 * @throws AppException
	 */
	public void updateTDepositRecvUsingAccgDocSeq(List<Long> depositRecvSeqList, @Nullable Long usingAccgDocSeq) throws AppException {

		if (LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			return;
		}

		// 更新対象を取得
		List<TDepositRecvEntity> updateDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);

		// 値を設定
		updateDepositRecvEntityList.forEach(entity -> {
			entity.setUsingAccgDocSeq(usingAccgDocSeq);
		});

		// 更新
		this.batchUpdateTDepositRecv(updateDepositRecvEntityList);
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求データ（報酬）の削除処理
	 * 
	 * @param deleteFeeList
	 * @throws AppException
	 */
	public void deleteInvoiceFee(List<InvoiceRowInputForm> deleteFeeList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(deleteFeeList)) {
			return;
		}
		// 請求書から外す報酬について会計書類SEQを外す
		List<Long> updateFeeSeqList = deleteFeeList.stream()
				.filter(form -> form.getFeeSeq() != null)
				.map(form -> form.getFeeSeq())
				.collect(Collectors.toList());
		if (LoiozCollectionUtils.isNotEmpty(updateFeeSeqList)) {
			List<TFeeEntity> tFeeEntityList = tFeeDao.selectFeeByFeeSeqList(updateFeeSeqList);
			if (LoiozCollectionUtils.isEmpty(tFeeEntityList)) {
				throw new DataNotFoundException("報酬情報が存在しません。[feeSeq=" + updateFeeSeqList + "]");
			}
			for (TFeeEntity entity : tFeeEntityList) {
				entity.setAccgDocSeq(null);
			}
			this.batchUpdateTFee(tFeeEntityList);
		}

		// 請求項目の削除
		List<Long> docInvoiceSeqList = deleteFeeList.stream()
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toList());
		if (LoiozCollectionUtils.isNotEmpty(docInvoiceSeqList)) {
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
				throw new DataNotFoundException("請求項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			this.batchDeleteTAccgDocInvoice(tAccgDocInvoiceEntityList);

			// 請求項目-報酬の削除
			List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeBydocInvoiceSeqList(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceFeeEntityList)) {
				throw new DataNotFoundException("請求項目-報酬情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			this.batchDeleteTAccgDocInvoiceFee(tAccgDocInvoiceFeeEntityList);
		}
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求データ（預り金）の削除処理
	 * 
	 * @param deleteDepositList
	 * @throws AppException
	 */
	public void deleteInvoiceDeposit(List<InvoiceRowInputForm> deleteDepositList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(deleteDepositList)) {
			return;
		}
		// 請求項目の削除
		List<Long> docInvoiceSeqList = deleteDepositList.stream()
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toList());
		if (LoiozCollectionUtils.isNotEmpty(docInvoiceSeqList)) {
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
				throw new DataNotFoundException("請求項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			this.batchDeleteTAccgDocInvoice(tAccgDocInvoiceEntityList);
		}

		// 請求項目-預り金の削除
		if (LoiozCollectionUtils.isNotEmpty(docInvoiceSeqList)) {
			List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceSeqList(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositEntityList)) {
				throw new DataNotFoundException("請求項目-預り金情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			this.batchDeleteTAccgDocInvoiceDeposit(tAccgDocInvoiceDepositEntityList);
		}
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求その他項目の削除処理
	 * 
	 * @param deleteOtherList
	 * @throws AppException
	 */
	public void deleteInvoiceOther(List<InvoiceRowInputForm> deleteOtherList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(deleteOtherList)) {
			return;
		}
		// 請求項目の削除
		List<Long> docInvoiceSeqList = deleteOtherList.stream()
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toList());
		if (LoiozCollectionUtils.isNotEmpty(docInvoiceSeqList)) {
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList)) {
				throw new DataNotFoundException("請求項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			this.batchDeleteTAccgDocInvoice(tAccgDocInvoiceEntityList);
		}

		// 請求その他項目の削除
		if (LoiozCollectionUtils.isNotEmpty(docInvoiceSeqList)) {
			List<TAccgDocInvoiceOtherEntity> tAccgDocInvoiceOtherEntityList = tAccgDocInvoiceOtherDao.selectAccgDocInvoiceOtherByDocInvoiceSeq(docInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceOtherEntityList)) {
				throw new DataNotFoundException("請求その他項目情報が存在しません。[docInvoiceSeqList=" + docInvoiceSeqList + "]");
			}
			this.batchDeleteTAccgDocInvoiceOther(tAccgDocInvoiceOtherEntityList);
		}
	}
	
	/**
	 * 請求書、精算書を下書きに戻す場合の共通処理です。<br>
	 *
	 * @param accgDocSeq
	 * @param personId
	 * @param ankenId
	 * @param salesDetailSeq
	 * @param uncollectibleDetailSeq
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 */
	public void commonProcessWhenChangingToDraft(Long accgDocSeq, Long personId, Long ankenId, Long salesDetailSeq,
			@Nullable Long uncollectibleDetailSeq) throws AppException {
		// 取引実績に関するデータを削除
		commonAccgService.deleteAccgRecordRelated(accgDocSeq);

		// 報酬データの入金ステータスを未請求に変更、回収不能フラグを0に変更
		this.updateFeeToUnclaimedPaymentStatusAndSetUncollectibleFlagToOff(accgDocSeq);

		// 請求書、精算書から作成した預り金を削除
		commonAccgService.deleteDepositRecv(accgDocSeq);

		// 売上に関するデータを削除
		commonAccgService.deleteSalesRelated(salesDetailSeq, personId, ankenId);

		// 回収不能に関するデータを削除
		if (uncollectibleDetailSeq != null) {
			commonAccgService.deleteUncollectibleRelated(uncollectibleDetailSeq, personId, ankenId);
		}

		// 案件ID、名簿IDで紐づく発行ステータスが下書きの全ての実費明細書に対して再作成フラグを立てる
		commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);
	}

	/**
	 * 請求書、精算書の共通削除処理
	 * 
	 * @param accgDocSeq
	 * @param personId
	 * @param ankenId
	 * @param salesDetailSeq
	 * @param uncollectibleDetailSeq
	 * @throws AppException
	 */
	public void commonDeleteForInvoiceAndStatements(Long accgDocSeq, Long personId, Long ankenId, Long salesDetailSeq,
			@Nullable Long uncollectibleDetailSeq) throws AppException {
		
		// 会計書類の削除
		this.deleteAccgDoc(accgDocSeq);

		// 報酬、請求項目に関するデータの削除
		this.deleteAccgDocInvoice(accgDocSeq);

		// 既入金項目の削除
		this.deleteAccgDocRepay(accgDocSeq);

		// 取引実績に関するデータを削除
		commonAccgService.deleteAccgRecordRelated(accgDocSeq);

		// 請求書、精算書から作成した預り金を削除
		commonAccgService.deleteDepositRecv(accgDocSeq);

		// 売上に関するデータを削除
		commonAccgService.deleteSalesRelated(salesDetailSeq, personId, ankenId);

		// 回収不能に関するデータを削除
		if (uncollectibleDetailSeq != null) {
			commonAccgService.deleteUncollectibleRelated(uncollectibleDetailSeq, personId, ankenId);
		}

		// 案件ID、名簿IDで紐づく発行ステータスが下書きの全ての実費明細書に対して再作成フラグを立てる
		commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		// 会計書類対応に関するデータ削除
		this.deleteAccgDocActRelated(accgDocSeq);

		// 報酬データの更新（ステータスを「未請求」、会計書類SEQをNULL、、回収不能フラグを0）
		this.disassociateFeeFromDoc(accgDocSeq);

		// 会計書類ファイルに関するデータ削除
		List<String> deleteS3ObjectKeys = new ArrayList<>();
		this.deleteAccgDocFileRelated(accgDocSeq, deleteS3ObjectKeys);

		// DBから削除したS3オブジェクトキーの実態ファイル削除
		if (LoiozCollectionUtils.isNotEmpty(deleteS3ObjectKeys)) {
			fileStorageService.deleteFile(deleteS3ObjectKeys);
		}
	}

	/**
	 * 既入金項目の入力フォームの値を、既入金項目が紐づく預り金データに反映する（同期させる）<br>
	 * ※預り金に反映するのは「金額」、「項目名」、「発生日」、「摘要」のみ
	 * 
	 * @param repayRowInputFormList 預り金と1対1で紐づく（紐づく預り金のSEQを1つ持つ）既入金項目データのリスト
	 * @throws AppException
	 */
	public void applyUpdateRepayToDepositRecv(List<RepayRowInputForm> repayRowInputFormList) throws AppException {

		// formが保持する預り金SEQをキーとして、InputFormをMap化する（formは必ず1つの預り金SEQを持つ）
		Map<Long, RepayRowInputForm> rowInputFormMap = repayRowInputFormList.stream()
				.collect(Collectors.toMap(
						form -> Long.valueOf(form.getDepositRecvSeqList().get(0)),
						form -> form,
						// キーに重複が合った場合、後の要素を採用する
						(f1, f2) -> f2));

		if (rowInputFormMap.size() != repayRowInputFormList.size()) {
			// Mapの要素数と、もとのListの要素数が合わない -> キーとした預り金SEQに重複がある場合
			throw new IllegalArgumentException("引数の既入金項目データに、同じ預り金SEQのものが存在している。");
		}

		// 更新対象の預り金を取得
		Set<Long> depositRecvSeqSet = rowInputFormMap.keySet();
		List<TDepositRecvEntity> updateDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(new ArrayList<Long>(depositRecvSeqSet));

		// フォームの値を設定
		updateDepositRecvEntityList.forEach(entity -> {

			// 対象の預り金データと対となるInputFormを取得
			RepayRowInputForm inputForm = rowInputFormMap.get(entity.getDepositRecvSeq());

			// 金額、項目名、発生日、摘要を設定
			entity.setDepositAmount(LoiozNumberUtils.parseAsBigDecimal(inputForm.getRepayAmount()));
			entity.setDepositItemName(inputForm.getRepayItemName());
			entity.setDepositDate(DateUtils.parseToLocalDate(inputForm.getRepayTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setSumText(inputForm.getSumText());
		});

		// 預り金を更新
		this.batchUpdateTDepositRecv(updateDepositRecvEntityList);
	}
	
	/**
	 * 請求項目（実費）の入力フォームの値を、請求項目（実費）が紐づく預り金データに反映する（同期させる）<br>
	 * ※預り金に反映するのは「金額」、「項目名」、「取引日」、「摘要」
	 * 
	 * @param invoiceRowInputFormList 預り金と1対1で紐づく（紐づく預り金のSEQを1つ持つ）請求項目（実費）データのリスト
	 * @throws AppException
	 */
	public void applyUpdateInvoiceToDepositRecv(List<InvoiceRowInputForm> invoiceRowInputFormList) throws AppException {

		// formが保持する預り金SEQをキーとして、InputFormをMap化する（formは必ず1つの預り金SEQを持つ）
		Map<Long, InvoiceRowInputForm> rowInputFormMap = invoiceRowInputFormList.stream()
				.collect(Collectors.toMap(
						form -> Long.valueOf(form.getDepositRecvSeqList().get(0)),
						form -> form,
						// キーに重複が合った場合、後の要素を採用する
						(f1, f2) -> f2));

		if (rowInputFormMap.size() != invoiceRowInputFormList.size()) {
			// Mapの要素数と、もとのListの要素数が合わない -> キーとした預り金SEQに重複がある場合
			throw new IllegalArgumentException("引数の請求項目（実費）データに、同じ預り金SEQのものが存在している。");
		}

		// 更新対象の預り金を取得
		Set<Long> depositRecvSeqSet = rowInputFormMap.keySet();
		List<TDepositRecvEntity> updateDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(new ArrayList<Long>(depositRecvSeqSet));

		// フォームの値を設定
		updateDepositRecvEntityList.forEach(entity -> {

			// 対象の預り金データと対となるInputFormを取得
			InvoiceRowInputForm inputForm = rowInputFormMap.get(entity.getDepositRecvSeq());

			// 金額、項目名、取引日、摘要を設定
			entity.setWithdrawalAmount(LoiozNumberUtils.parseAsBigDecimal(inputForm.getAmount()));
			entity.setDepositItemName(inputForm.getItemName());
			entity.setDepositDate(DateUtils.parseToLocalDate(inputForm.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setSumText(inputForm.getSumText());
		});

		// 預り金を更新
		this.batchUpdateTDepositRecv(updateDepositRecvEntityList);
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
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	private TAccgInvoiceEntity getAccgInvoiceEntityByAccgDocSeq(Long accgDocSeq) throws DataNotFoundException {

		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書が存在しません");
		}
		return tAccgInvoiceEntity;
	}
	
	/**
	 * 精算書データの取得
	 * 
	 * @param accgDocSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	private TAccgStatementEntity getAccgStatementEntityByAccgDocSeq(Long accgDocSeq) throws DataNotFoundException {

		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity == null) {
			throw new DataNotFoundException("精算書が存在しません");
		}
		return tAccgStatementEntity;
	}
	
	/**
	 * PNGファイルのS3オブジェクトキーから画像情報を取得し、HTMLのIMGタグのSrc属性値に変換する
	 * 
	 * @param pngS3ObjectKeys ページ番号でソート済みを想定
	 * @return
	 */
	private List<String> getPngHtmlImgSrcListByS3ObjectKey(List<String> pngS3ObjectKeys) {

		List<String> pngHtmlImgSrc = new ArrayList<>();
		for (String pngFileS3ObjectKey : pngS3ObjectKeys) {
			try (
					S3Object s3Object = fileStorageService.fileDownload(pngFileS3ObjectKey);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					InputStream is = s3Object.getObjectContent();) {

				// アウトプットストリームに書き込み
				LoiozIOUtils.copy(is, baos);
				String htmlImgSrc = FileUtils.toHtmlImgSrc(baos.toByteArray(), FileExtension.PNG.getVal());
				pngHtmlImgSrc.add(htmlImgSrc);
			} catch (Exception ex) {
				throw new RuntimeException("PNG画像の取得・表示時にエラーが発生しました", ex);
			}
		}
		return pngHtmlImgSrc;
	}


	/**
	 * 会計書類-対応情報の「新規作成」対応データ取得
	 * 
	 * @param accgDocActList
	 * @param accountNameMap
	 * @return
	 */
	private List<DocActivityRowForm> getNewAccgDocActList(List<TAccgDocActEntity> accgDocActList, Map<Long, String> accountNameMap) {
		List<DocActivityRowForm> newAccgDocActList = new ArrayList<>();

		// 「新規作成」対応をしていない場合は「新規作成」の未処理データを作成
		if (LoiozCollectionUtils.isEmpty(accgDocActList)) {
			DocActivityRowForm docActivityRowForm = new DocActivityRowForm();
			docActivityRowForm.setActivityTypeName(AccgDocActType.NEW.getVal());
			newAccgDocActList.add(docActivityRowForm);
			return newAccgDocActList;
		}

		// 会計書類-対応情報の「新規作成」対応データ取得
		newAccgDocActList = accgDocActList.stream()
				.filter(e -> AccgDocActType.NEW.equalsByCode(e.getActType()))
				.sorted(Comparator.comparing(TAccgDocActEntity::getActAt))
				.map(e -> DocActivityRowForm.builder()
						.activityTypeName(AccgDocActType.of(e.getActType()).getVal())
						.activityByName(accountNameMap.get(e.getActBy()))
						.activityAt(e.getActAt())
						.build())
				.collect(Collectors.toList());
		return newAccgDocActList;
	}

	/**
	 * 会計書類-対応情報の「発行」対応データ取得
	 * 
	 * @param accgDocActList
	 * @param accountNameMap
	 * @return
	 */
	private List<DocActivityRowForm> getIssueAccgDocActList(List<TAccgDocActEntity> accgDocActList, Map<Long, String> accountNameMap) {
		List<DocActivityRowForm> issueAccgDocActList = new ArrayList<>();
		if (LoiozCollectionUtils.isEmpty(accgDocActList)) {
			return issueAccgDocActList;
		}

		// 「新規作成」対応の件数
		long newAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.NEW.equalsByCode(e.getActType())).count();
		// 「発行」対応の件数
		long issueAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.ISSUE.equalsByCode(e.getActType())).count();

		// 「発行」対応をしていないが、「新規作成」対応をしている場合は「発行」の未処理データを作成
		if (newAccgDocActCount > 0 && issueAccgDocActCount == 0) {
			DocActivityRowForm docActivityRowForm = new DocActivityRowForm();
			docActivityRowForm.setActivityTypeName(AccgDocActType.ISSUE.getVal());
			issueAccgDocActList.add(docActivityRowForm);
			return issueAccgDocActList;
		} else {
			// 会計書類-対応情報の「発行」対応データ取得
			issueAccgDocActList = accgDocActList.stream()
					.filter(e -> AccgDocActType.ISSUE.equalsByCode(e.getActType()))
					.sorted(Comparator.comparing(TAccgDocActEntity::getActAt))
					.map(e -> DocActivityRowForm.builder()
							.activityTypeName(AccgDocActType.of(e.getActType()).getVal())
							.activityByName(accountNameMap.get(e.getActBy()))
							.activityAt(e.getActAt())
							.build())
					.collect(Collectors.toList());
			return issueAccgDocActList;
		}
	}

	/**
	 * 会計書類-対応情報の「送信」対応データ取得
	 * 
	 * @param accgDocActList
	 * @param accountNameMap
	 * @return
	 */
	private List<DocActivityRowForm> getSendAccgDocActList(List<TAccgDocActEntity> accgDocActList, Map<Long, String> accountNameMap) {
		if (LoiozCollectionUtils.isEmpty(accgDocActList)) {
			return Collections.emptyList();
		}

		// 「発行」対応の件数
		long issueAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.ISSUE.equalsByCode(e.getActType())).count();
		// 「送信」対応の件数
		long sendAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.SEND.equalsByCode(e.getActType())).count();

		if (issueAccgDocActCount > 0 && sendAccgDocActCount == 0) {
			// 「送信」対応をしていないが、「発行」対応をしている場合 -> 「送付」の未処理データを作成
			return Arrays.asList(DocActivityRowForm.builder().activityTypeName(AccgDocActType.SEND.getVal()).build());
		}

		// 「送信」対応のデータ
		List<Long> accgDocActSeqList = accgDocActList.stream()
				.filter(e -> AccgDocActType.SEND.equalsByCode(e.getActType()))
				.map(e -> e.getAccgDocActSeq())
				.collect(Collectors.toList());

		// 会計書類の送付情報Beanを取得
		List<AccgDocActSendBean> accgDocActSendBeanList = tAccgDocActSendDao.selectAccgDocActSendBeanByAccgDocActSeq(accgDocActSeqList);

		// 送付ファイルのファイル名とダウンロード状況を取得する
		List<Long> accgDocActSendSeq = accgDocActSendBeanList.stream().map(AccgDocActSendBean::getAccgDocActSendSeq).collect(Collectors.toList());
		List<TAccgDocActSendFileEntity> tAccgDocActSendFileEntities = tAccgDocActSendFileDao.selectAccgDocActSendFileByAccgDocActSendSeq(accgDocActSendSeq);

		// 同じファイルを送付するケースがあるので、重複は排除
		Set<Long> accgDocFileSeqSet = tAccgDocActSendFileEntities.stream().map(TAccgDocActSendFileEntity::getAccgDocFileSeq).collect(Collectors.toSet());
		List<TAccgDocFileEntity> tAccgDocFileEntities = tAccgDocFileDao.selectAccgDocFileByAccgDocFileSeq(new ArrayList<>(accgDocFileSeqSet));

		// 会計書類SEQをキーとした、ファイルタイプ名Mapを作成
		Map<Long, String> accgDocFileSeqToFileTypeNameMap = tAccgDocFileEntities.stream()
				.collect(Collectors.toUnmodifiableMap(TAccgDocFileEntity::getAccgDocFileSeq,
						e -> AccgDocFileType.of(e.getAccgDocFileType()).getVal()
				));

		// TAccgDocActSendFileEntity -> DocActivityDownloadStatusのMapper
		Function<TAccgDocActSendFileEntity, AccgInvoiceStatementViewForm.DocActivityDownloadStatus> downloadStatusMapper = e -> {
			return AccgInvoiceStatementViewForm.DocActivityDownloadStatus.builder()
					.accgDocFileSeq(e.getAccgDocFileSeq())
					.fileTypeName(accgDocFileSeqToFileTypeNameMap.get(e.getAccgDocFileSeq()))
					.downloadedDate(DateUtils.parseToString(e.getDownloadLastAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM))
					.isDownloaded(e.getDownloadLastAt() != null)
					.build();
		};

		// アクティビティのダウンロード状況には「請求書」と「精算書」のみ表示する
		Set<Long> invoiceStatementPdfAccgDocFileSeqSet = tAccgDocFileEntities.stream()
				.filter(e -> {
					return AccgDocFileType.INVOICE.equalsByCode(e.getAccgDocFileType())
							|| AccgDocFileType.STATEMENT.equalsByCode(e.getAccgDocFileType());
				})
				.map(TAccgDocFileEntity::getAccgDocFileSeq)
				.collect(Collectors.toSet());

		// AccgDocActSendSeqをキーとした、送付ファイルとダウンロード状況Mapを作成
		Map<Long, List<AccgInvoiceStatementViewForm.DocActivityDownloadStatus>> accgDocActSendSeqToDownLoadStatusMapper = tAccgDocActSendFileEntities.stream()
				.filter(e -> invoiceStatementPdfAccgDocFileSeqSet.contains(e.getAccgDocFileSeq())) // ダウンロード状況には「請求書」と「精算書」のみ表示する
				.collect(Collectors.groupingBy(
						TAccgDocActSendFileEntity::getAccgDocActSendSeq,
						Collectors.mapping(downloadStatusMapper, Collectors.toList())));

		return accgDocActSendBeanList.stream()
				.filter(e -> AccgDocActType.SEND.equalsByCode(e.getActType()))
				.sorted(Comparator.comparing(AccgDocActSendBean::getActAt))
				.map(e -> DocActivityRowForm.builder()
						.activityTypeName(AccgDocActType.of(e.getActType()).getVal())
						.activityByName(accountNameMap.get(e.getActBy()))
						.activityAt(e.getActAt())
						.accgDocActSendSeq(e.getAccgDocActSendSeq())
						.accgDocSendType(AccgDocSendType.of(e.getSendType()))
						.downloadStatus(accgDocActSendSeqToDownLoadStatusMapper.get(e.getAccgDocActSendSeq()))
						.build())
				.collect(Collectors.toList());
	}

	/**
	 * 出金額と預り金の差額を取得します。<br>
	 * 
	 * @param ankenId
	 * @param personId
	 * @param differenceAcquisitionFlg true：出金額が多ければ差額分を取得、 false：入金額が多ければ差額分を取得
	 * @return
	 */
	private BigDecimal getDifferenceBetweenWithdrawalAndDeposit(Long ankenId, Long personId, boolean differenceAcquisitionFlg) {
		BigDecimal returnAmount = BigDecimal.ZERO;

		// 預り金データ取得
		DepositRecvSummaryBean depositRecvSummaryBean = tDepositRecvDao.selectSummaryByParams(ankenId, personId);
		BigDecimal totalWithdrawalAmount = depositRecvSummaryBean == null ? BigDecimal.ZERO : depositRecvSummaryBean.getTotalWithdrawalAmount();
		BigDecimal totalDepositAmount = depositRecvSummaryBean == null ? BigDecimal.ZERO : depositRecvSummaryBean.getTotalDepositAmount();

		if (differenceAcquisitionFlg) {
			// 出金額のほうが多ければ入金額を引いた額を返す
			if (totalWithdrawalAmount.compareTo(totalDepositAmount) > 0) {
				returnAmount = totalWithdrawalAmount.subtract(totalDepositAmount);
			}
		} else {
			// 入金額のほうが多ければ出金額を引いた額を返す
			if (totalDepositAmount.compareTo(totalWithdrawalAmount) > 0) {
				returnAmount = totalDepositAmount.subtract(totalWithdrawalAmount);
			}
		}

		return returnAmount;
	}

	/**
	 * 既入金項目入力用のリストデータを作成します。
	 * 
	 * @param accgDocSeq
	 * @param isRepaySumFlg
	 * @return
	 */
	private List<RepayRowInputForm> getRepayRowInputFormList(Long accgDocSeq, boolean isRepaySumFlg) {
		// 既入金データ取得
		List<RepayBean> repayBeanList = tAccgDocRepayDao.selectRepayBeanListByAccgDocSeq(accgDocSeq);

		// 既入金項目を画面に表示するRepayRowInputFormリストを作成する。
		List<RepayRowInputForm> formList = new ArrayList<>();
		Long repayRowCount = 0L;
		for (RepayBean bean : repayBeanList) {
			RepayRowInputForm row = new RepayRowInputForm();
			row.setDocRepaySeq(bean.getDocRepaySeq());
			row.setAccgDocSeq(bean.getAccgDocSeq());
			row.setRepayTransactionDate(
				DateUtils.parseToString(bean.getRepayTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			row.setRepayItemName(bean.getRepayItemName());
			row.setSumText(bean.getSumText());
			row.setRepayAmount(AccountingUtils.toDispAmountLabel(bean.getRepayAmount()));
			row.setDocRepayOrder(bean.getDocRepayOrder());
			// まとめ表示時は登録・更新処理用にhiddenの行データを別途作成するため、まとめ行自体に預り金SEQを持たせない
			row.setDepositRecvSeqList(isRepaySumFlg ? null : StringUtils.toListLong(bean.getDepositRecvSeq()));
			row.setRowRepaySumFlg(isRepaySumFlg);
			if (isRepaySumFlg) {
				// まとめ表示行自体は、請求書や精算書から作成されることは無いため、depositMadeFromAccgDocFlgにfalseをセット
				row.setDepositMadeFromAccgDocFlg(false);
			} else {
				// まとめ表示ではない場合は、作成元会計情報SEQの有無で、depositMadeFromAccgDocFlgにセットする
				row.setDepositMadeFromAccgDocFlg(StringUtils.isEmpty(bean.getCreatedAccgDocSeq()) ? false : true);
			}
			row.setDisplayFlg(true);
			row.setRepayRowCount(repayRowCount++);
			formList.add(row);
		}

		// 既入金項目をまとめて表示している場合、登録・更新処理用に紐づく預り金情報でrowInputFormリストを作成する。
		if (isRepaySumFlg && !LoiozCollectionUtils.isEmpty(repayBeanList)) {
			RepayBean repayBean = repayBeanList.get(0);
			List<Long> depositRecvSeqList = StringUtils.toListLong(repayBean.getDepositRecvSeq());
			List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao
					.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
			for (TDepositRecvEntity tDepositRecvEntity : tDepositRecvEntityList) {
				RepayRowInputForm row = new RepayRowInputForm();
				row.setDocRepaySeq(repayBean.getDocRepaySeq());
				row.setAccgDocSeq(accgDocSeq);
				row.setRepayTransactionDate(DateUtils.parseToString(tDepositRecvEntity.getDepositDate(),
						DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				row.setRepayItemName(tDepositRecvEntity.getDepositItemName());
				row.setSumText(tDepositRecvEntity.getSumText());
				row.setRepayAmount(AccountingUtils.toDispAmountLabel(tDepositRecvEntity.getDepositAmount()));
				row.setDepositRecvSeqList(List.of(tDepositRecvEntity.getDepositRecvSeq()));
				row.setRowRepaySumFlg(false);
				row.setDepositMadeFromAccgDocFlg(tDepositRecvEntity.getCreatedAccgDocSeq() == null ? false : true);
				row.setDisplayFlg(false);
				row.setRepayRowCount(repayRowCount++);
				formList.add(row);
			}
		}

		return formList;
	}

	/**
	 * 請求書詳細画面、精算書詳細画面の請求項目入力用情報を取得します。<br>
	 * 
	 * @param accgDocSeq
	 * @param expenseSumFlg 請求項目まとめ表示フラグ
	 * @return
	 */
	private List<InvoiceRowInputForm> getInvoiceRowInputFormList(Long accgDocSeq, boolean expenseSumFlg) {
		// 返却用Formリスト
		List<InvoiceRowInputForm> formList = new ArrayList<>();

		// 請求項目データ
		List<AccgDocInvoiceBean> beanList = tAccgDocInvoiceDao.selectAccgDocInvoiceBeanByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(beanList)) {
			// 請求項目データがない場合
			return formList;
		}

		// 取得したデータから、1件ずつInvoiceRowInputFormを作成しリストにする
		Long invoiceRowCount = 0L;
		for (AccgDocInvoiceBean bean : beanList) {
			// 報酬項目
			if (bean.getDocInvoiceFeeSeq() != null) {
				InvoiceRowInputForm row = new InvoiceRowInputForm();
				row.setAmount(AccountingUtils.toDispAmountLabel(bean.getFeeAmount()));
				row.setDocInvoiceSeq(bean.getDocInvoiceSeq());
				row.setAccgDocSeq(accgDocSeq);
				row.setDocInvoiceOrder(bean.getDocInvoiceOrder());
				row.setFeeSeq(bean.getFeeSeq());
				row.setDepositRecvSeqList(Collections.emptyList());
				row.setFeeTimeChargeFlg(SystemFlg.FLG_ON.equalsByCode(bean.getFeeTimeChargeFlg()));
				row.setInvoieType(null);
				row.setItemName(bean.getFeeItemName());
				row.setSumText(bean.getFeeSumText());
				row.setTaxRateType(bean.getTaxRateType());
				row.setHourPrice(AccountingUtils.toDispAmountLabel(bean.getHourPrice()));
				row.setWorkTimeMinute(bean.getWorkTimeMinute());
				row.setTransactionDate(DateUtils.parseToString(bean.getFeeTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				row.setWithholdingFlg(SystemFlg.FLG_ON.equalsByCode(bean.getWithholdingFlg()));
				row.setFeeFlg(true);
				row.setDepositRecvFlg(false);
				row.setDiscountFlg(false);
				row.setTextFlg(false);
				row.setDisplayFlg(true);
				row.setInvoiceRowCount(invoiceRowCount++);
				formList.add(row);
			}

			// 預り金項目、実費項目
			if (bean.getDocInvoiceDepositSeq() != null) {
				InvoiceRowInputForm row = new InvoiceRowInputForm();
				row.setAmount(AccountingUtils.toDispAmountLabel(bean.getDepositAmount()));
				row.setDocInvoiceSeq(bean.getDocInvoiceSeq());
				row.setDocInvoiceDepositSeq(bean.getDocInvoiceDepositSeq());
				row.setAccgDocSeq(accgDocSeq);
				row.setDocInvoiceOrder(bean.getDocInvoiceOrder());
				row.setFeeSeq(null);
				// まとめ表示時は登録・更新処理用にhiddenの行データを別途作成するため、まとめ行自体に預り金SEQを持たせない
				row.setDepositRecvSeqList(expenseSumFlg ? null : StringUtils.toListLong(bean.getDepositRecvSeq()));
				row.setInvoieType(bean.getInvoiceDepositType());
				row.setItemName(bean.getDepositItemName());
				row.setSumText(bean.getDepositSumText());
				row.setTransactionDate(DateUtils.parseToString(bean.getDepositTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				row.setFeeFlg(false);
				row.setDepositRecvFlg(true);
				row.setDiscountFlg(false);
				row.setTextFlg(false);
				if (InvoiceDepositType.EXPENSE.equalsByCode(bean.getInvoiceDepositType())) {
					row.setRowExpenseSumFlg(expenseSumFlg);
				} else {
					row.setRowExpenseSumFlg(false);
				}
				row.setDisplayFlg(true);
				row.setInvoiceRowCount(invoiceRowCount++);
				formList.add(row);
			}

			// 請求項目をまとめて表示時、実費データ行が存在する場合に登録・更新処理用に紐づく預り金情報でrowInputFormリストを作成する。
			if (expenseSumFlg && bean.getDocInvoiceDepositSeq() != null
					&& InvoiceDepositType.EXPENSE.equalsByCode(bean.getInvoiceDepositType())) {
				// カンマ区切りの預り金SEQをリストに変換
				List<Long> depositRecvSeqList = StringUtils.toListLong(bean.getDepositRecvSeq());
				// 預り金データ取得
				List<TDepositRecvAndDocInvoiceDepositBean> tDepositRecvAndDocInvoiceDepositBeanList = tDepositRecvDao
						.selectDepositRecvAndDocInvoiceDepositByDepositRecvSeqList(depositRecvSeqList);
				for (TDepositRecvAndDocInvoiceDepositBean tDepositRecvAndDocInvoiceDepositBean : tDepositRecvAndDocInvoiceDepositBeanList) {
					InvoiceRowInputForm row = new InvoiceRowInputForm();
					row.setAccgDocSeq(accgDocSeq);
					row.setDocInvoiceSeq(tDepositRecvAndDocInvoiceDepositBean.getDocInvoiceSeq());
					row.setDocInvoiceDepositSeq(tDepositRecvAndDocInvoiceDepositBean.getDocInvoiceDepositSeq());
					row.setDepositRecvSeqList(List.of(tDepositRecvAndDocInvoiceDepositBean.getDepositRecvSeq()));
					row.setTransactionDate(
							DateUtils.parseToString(tDepositRecvAndDocInvoiceDepositBean.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
					row.setItemName(tDepositRecvAndDocInvoiceDepositBean.getDepositItemName());
					row.setSumText(tDepositRecvAndDocInvoiceDepositBean.getSumText());
					row.setAmount(
							AccountingUtils.toDispAmountLabel(tDepositRecvAndDocInvoiceDepositBean.getWithdrawalAmount()));
					row.setInvoieType(InvoiceDepositType.EXPENSE.getCd());
					row.setFeeFlg(false);
					row.setDepositRecvFlg(true);
					row.setDiscountFlg(false);
					row.setTextFlg(false);
					row.setRowExpenseSumFlg(false);
					row.setDisplayFlg(false);
					row.setInvoiceRowCount(invoiceRowCount++);
					formList.add(row);
				}
			}

			// その他項目
			if (bean.getDocInvoiceOtherSeq() != null) {
				InvoiceRowInputForm row = new InvoiceRowInputForm();
				row.setAmount(AccountingUtils.toDispAmountLabel(bean.getOtherAmount()));
				row.setDocInvoiceSeq(bean.getDocInvoiceSeq());
				row.setAccgDocSeq(accgDocSeq);
				row.setDocInvoiceOrder(bean.getDocInvoiceOrder());
				row.setFeeSeq(null);
				row.setDepositRecvSeqList(Collections.emptyList());
				row.setInvoieType(bean.getOtherItemType());
				row.setItemName(bean.getOtherItemName());
				row.setSumText(bean.getOtherSumText());
				row.setTaxRateType(bean.getDiscountTaxRateType());
				row.setTransactionDate(DateUtils.parseToString(bean.getOtherTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				row.setWithholdingFlg(SystemFlg.FLG_ON.equalsByCode(bean.getDiscountWithholdingFlg()));
				row.setFeeFlg(false);
				row.setDepositRecvFlg(false);
				if (InvoiceOtherItemType.DISCOUNT.equalsByCode(bean.getOtherItemType())) {
					row.setDiscountFlg(true);
					row.setTextFlg(false);
				} else if (InvoiceOtherItemType.TEXT.equalsByCode(bean.getOtherItemType())) {
					row.setDiscountFlg(false);
					row.setTextFlg(true);
				} else {
					row.setDiscountFlg(false);
					row.setTextFlg(false);
				}
				row.setDisplayFlg(true);
				row.setInvoiceRowCount(invoiceRowCount++);
				formList.add(row);
			}
		}
		return formList;
	}

	/**
	 * 預り金出金と請求項目の実費を合算します。<br>
	 * 
	 * @param depositRecvEntityList
	 * @param expenseRowInputFormList
	 * @return
	 */
	private BigDecimal getTotalExpenseAmount(List<TDepositRecvEntity> depositRecvEntityList,
			List<InvoiceRowInputForm> expenseRowInputFormList) {
		// 預り金の出金を取得
		List<BigDecimal> withdrawalAmountList = depositRecvEntityList.stream()
				.map(entity -> entity.getWithdrawalAmount())
				.collect(Collectors.toList());
		
		// 請求項目の実費を取得
		if (!LoiozCollectionUtils.isEmpty(expenseRowInputFormList)) {
			List<BigDecimal> expenseAmountList = expenseRowInputFormList.stream()
					.filter(form -> !form.isDeleteFlg())
					.map(form -> form == null ? BigDecimal.ZERO
							: LoiozNumberUtils.parseAsBigDecimal(form.getAmount()))
					.collect(Collectors.toList());
			withdrawalAmountList.addAll(expenseAmountList);
		}
		
		// 預り金合計と実費を合算
		BigDecimal totalExpenseAmount = AccountingUtils.calcTotal(withdrawalAmountList);
		
		return totalExpenseAmount;
	}

	/**
	 * 預り金入金と既入金情報の金額を合算します。<br>
	 * 
	 * @param addDepositRecvEntityList
	 * @param forRegistUpdateRepayList
	 * @return
	 */
	private BigDecimal getTotalDepositAmount(List<TDepositRecvEntity> depositRecvEntityList,
			List<RepayRowInputForm> repayRowInputFormList) {
		// 預り金の入金を取得
		List<BigDecimal> depositAmountList = depositRecvEntityList.stream()
				.map(entity -> entity.getDepositAmount())
				.collect(Collectors.toList());

		// 既入金項目の既入金を取得
		if (!LoiozCollectionUtils.isEmpty(repayRowInputFormList)) {
			List<BigDecimal> repayAmountList = repayRowInputFormList.stream()
					.filter(form -> !form.isDeleteFlg())
					.map(form -> form == null ? BigDecimal.ZERO : LoiozNumberUtils.parseAsBigDecimal(form.getRepayAmount()))
					.collect(Collectors.toList());
			depositAmountList.addAll(repayAmountList);
		}

		// 預り金合計と既入金額を合算
		BigDecimal totalDepositAmount = AccountingUtils.calcTotal(depositAmountList);

		return totalDepositAmount;
	}

	/**
	 * 未使用の預り金情報を取得します。<br>
	 * 
	 * @param depositRecvSeqList
	 * @param accgDocSeq
	 * @return
	 * @throws AppException
	 */
	private List<TDepositRecvEntity> getUnusedDepositRecvByDepositRecv(List<Long> depositRecvSeqList, Long AccgDocSeq) throws AppException {
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositRecvByDepositRecvSeqList(depositRecvSeqList);
		if (LoiozCollectionUtils.isEmpty(tDepositRecvEntityList) || tDepositRecvEntityList.size() != depositRecvSeqList.size()) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 預り金が他の会計書類で使用されていたら楽観ロックエラー
		if (tDepositRecvEntityList.stream()
				.filter(entity -> entity.getUsingAccgDocSeq() != null && !AccgDocSeq.equals(entity.getUsingAccgDocSeq()))
				.collect(Collectors.toList()).size() > 0) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		return tDepositRecvEntityList;
	}

	/**
	 * 請求項目の行データリストから、報酬、値引き、テキストの行データを取得します
	 * 
	 * @param validInvoiceRowList
	 * @return
	 */
	private List<InvoiceRowInputForm> getFeeOrOtherRowList(List<InvoiceRowInputForm> validInvoiceRowList) {
		if (LoiozCollectionUtils.isEmpty(validInvoiceRowList)) {
			return Collections.emptyList();
		}
		
		List<InvoiceRowInputForm> feeOrOtherRowList = validInvoiceRowList.stream().filter(form -> !form.isDepositRecvFlg()).collect(Collectors.toList());
		
		return feeOrOtherRowList;
	}

	/**
	 * 請求項目の行データリストから、預り金の行データを取得します
	 * 
	 * @param validInvoiceRowList
	 * @return
	 */
	private List<InvoiceRowInputForm> getDepositRowList(List<InvoiceRowInputForm> validInvoiceRowList) {
		if (LoiozCollectionUtils.isEmpty(validInvoiceRowList)) {
			return Collections.emptyList();
		}
		
		List<InvoiceRowInputForm> depositRowList = validInvoiceRowList.stream().filter(form -> form.isDepositRecvFlg()).filter(form -> InvoiceDepositType.DEPOSIT.equalsByCode(form.getInvoieType())).collect(Collectors.toList());
		
		return depositRowList;
	}

	/**
	 * 請求項目の行データリストから、実費の行データを取得します
	 * 
	 * @param validInvoiceRowList
	 * @return
	 */
	private List<InvoiceRowInputForm> getInvoiceExpenseRowList(List<InvoiceRowInputForm> validInvoiceRowList) {
		if (LoiozCollectionUtils.isEmpty(validInvoiceRowList)) {
			return Collections.emptyList();
		}
		
		List<InvoiceRowInputForm> invoiceExpenseRowList = validInvoiceRowList.stream().filter(form -> form.isDepositRecvFlg()).filter(form -> InvoiceDepositType.EXPENSE.equalsByCode(form.getInvoieType())).collect(Collectors.toList());
		
		return invoiceExpenseRowList;
	}

	/**
	 * 請求書／精算書から請求書-印字フラグ（既入金）、既入金項目合算フラグ（既入金）を取得しformにセットします。
	 * 
	 * @param form
	 * @param accgDocSeq
	 * @param accgDocType
	 */
	private void setRepayTransactionDatePrintFlgAndRepaySumFlg(AccgInvoiceStatementInputForm.RepayInputForm form, Long accgDocSeq, String accgDocType) {
		if (AccgDocType.INVOICE.equalsByCode(accgDocType)) {
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			if (tAccgInvoiceEntity == null) {
				throw new DataNotFoundException("請求書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
			}
			form.setRepayTransactionDatePrintFlg(SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getRepayTransactionDatePrintFlg()));
			form.setRepaySumFlg(SystemFlg.FLG_ON.equalsByCode(tAccgInvoiceEntity.getRepaySumFlg()));
		} else if (AccgDocType.STATEMENT.equalsByCode(accgDocType)) {
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			if (tAccgStatementEntity == null) {
				throw new DataNotFoundException("精算書類情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
			}
			form.setRepayTransactionDatePrintFlg(SystemFlg.FLG_ON.equalsByCode(tAccgStatementEntity.getRepayTransactionDatePrintFlg()));
			form.setRepaySumFlg(SystemFlg.FLG_ON.equalsByCode(tAccgStatementEntity.getRepaySumFlg()));
		} else {
			throw new RuntimeException("想定外の会計書類タイプ");
		}
	}

	/**
	 * 請求書発行時の預り金予定スケジュール を設定する
	 * 
	 * @param tDepositRecvInsertEntities
	 * @param depositRecvAmountExpect 預り金入金額【見込】
	 * @param totalDepositAmount 【請求項目】預り金合計
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 */
	private void setDepositRecvScheduleEntityForInvoices(List<TDepositRecvEntity> tDepositRecvEntities,
			BigDecimal depositRecvAmountExpect, BigDecimal totalDepositAmount, Long personId, Long ankenId,
			Long accgDocSeq) {
		if (LoiozNumberUtils.isLessThan(depositRecvAmountExpect, BigDecimal.ONE)) {
			return;
		}

		// 実費額計算
		BigDecimal expenseAmount = depositRecvAmountExpect.subtract(totalDepositAmount);

		// 実費用entity作成
		if (LoiozNumberUtils.isGreaterThan(expenseAmount, BigDecimal.ZERO)) {
			TDepositRecvEntity entity = new TDepositRecvEntity();
			entity.setPersonId(personId);
			entity.setAnkenId(ankenId);
			entity.setCreatedType(DepositRecvCreatedType.CREATED_BY_ISSUANCE.getCd());
			entity.setExpenseInvoiceFlg(SystemFlg.FLG_ON.getCd());
			entity.setDepositAmount(expenseAmount);
			entity.setDepositType(DepositType.NYUKIN.getCd());
			entity.setDepositItemName(AccgConstant.ACCG_ITEM_NAME_OF_EXPENSE_PAYMENT_SCHEDULE);
			entity.setCreatedAccgDocSeq(accgDocSeq);
			entity.setDepositCompleteFlg(SystemFlg.FLG_OFF.getCd());
			entity.setSumText(AccgConstant.ACCG_SUM_TEXT_OF_EXPENSE_PAYMENT_SCHEDULE);
			tDepositRecvEntities.add(entity);
		}

		// 預り金用entity作成
		if (LoiozNumberUtils.isGreaterThan(totalDepositAmount, BigDecimal.ZERO)) {
			TDepositRecvEntity entity = new TDepositRecvEntity();
			entity.setPersonId(personId);
			entity.setAnkenId(ankenId);
			entity.setCreatedType(DepositRecvCreatedType.CREATED_BY_ISSUANCE.getCd());
			entity.setExpenseInvoiceFlg(SystemFlg.FLG_OFF.getCd());
			entity.setDepositAmount(totalDepositAmount);
			entity.setDepositType(DepositType.NYUKIN.getCd());
			entity.setDepositItemName(AccgConstant.ACCG_ITEM_NAME_OF_DEPOSIT_PAYMENT_SCHEDULE);
			entity.setCreatedAccgDocSeq(accgDocSeq);
			entity.setDepositCompleteFlg(SystemFlg.FLG_OFF.getCd());
			tDepositRecvEntities.add(entity);
		}
	}

	/**
	 * 請求書、精算書に売上明細SEQを設定します<br>
	 * 
	 * @param salesDetailSeq
	 * @param accgDocSeq
	 * @param accgDocType
	 * @throws AppException
	 */
	private void updateFeeSalesDetailSeq(Long salesDetailSeq, Long accgDocSeq, AccgDocType accgDocType) throws AppException {
		if (salesDetailSeq == null) {
			return;
		}

		// 売上明細SEQの設定
		if (AccgDocType.INVOICE.equals(accgDocType)) {
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			if (tAccgInvoiceEntity == null) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			// entityにセット
			tAccgInvoiceEntity.setSalesDetailSeq(salesDetailSeq);

			// 請求書情報の更新
			this.updateAccgInvoice(tAccgInvoiceEntity);

		} else if (AccgDocType.STATEMENT.equals(accgDocType)) {
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			if (tAccgStatementEntity == null) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			// entityにセット
			tAccgStatementEntity.setSalesDetailSeq(salesDetailSeq);

			// 精算書情報の更新
			this.updateAccgStatement(tAccgStatementEntity);
		}
	}
	
	//=========================================================================
	// ▼ チェック、バリデーション系
	//=========================================================================

	/**
	 * 既入金データがDBに存在するかチェックします。<br>
	 * 登録してある場合はtrueを返します。
	 * 
	 * @param repayRowList
	 * @return
	 */
	private boolean checkIfTargetDocRepayDataExistsInDb(List<RepayRowInputForm> repayRowList) {
		boolean isTargetDataExists = true;
		
		// 既入金の行データが無い場合はチェック終了
		if (LoiozCollectionUtils.isEmpty(repayRowList)) {
			return isTargetDataExists;
		}
		
		// 削除となる既入金項目データ
		List<RepayRowInputForm> deleteRepayRowList = repayRowList.stream()
				.filter(row -> !row.isRowRepaySumFlg())
				.filter(row -> !LoiozCollectionUtils.isEmpty(row.getDepositRecvSeqList()))
				.filter(row -> row.isDeleteFlg())
				.collect(Collectors.toList());
		
		// 削除する既入金データ確認
		if (!this.checkIfDocRepayDataDeletedExpense(deleteRepayRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		// 更新となる既入金項目データ（削除でも、登録でもないもの）
		List<RepayRowInputForm> updateRepayRowList = repayRowList.stream()
				.filter(row -> !row.isRowRepaySumFlg())
				.filter(row -> !LoiozCollectionUtils.isEmpty(row.getDepositRecvSeqList()))
				.filter(row -> !row.isDeleteFlg() && !row.isAddFlg())
				.collect(Collectors.toList());
		
		// 更新する既入金データ確認
		if (!this.checkIfDocRepayDataUpdatedExpense(updateRepayRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		return isTargetDataExists;
	}

	/**
	 * 既入金項目で削除する預り金データがDBに存在するかチェックします
	 * 
	 * @param deleteRepayRowList
	 * @return
	 */
	private boolean checkIfDocRepayDataDeletedExpense(List<RepayRowInputForm> deleteRepayRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(deleteRepayRowList)) {
			return isTargetDataExists;
		}
		
		// 削除する既入金の既入金項目SEQ
		List<Long> deleteRepayDocRepaySeqList = deleteRepayRowList.stream().map(row -> row.getDocRepaySeq()).collect(Collectors.toList());
		// 削除する既入金の預り金SEQ
		List<Long> deleteRepayDepositRecvSeqList = deleteRepayRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
		
		// 既入金の削除対象があれば既入金項目_預り金データの存在確認、削除対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(deleteRepayDocRepaySeqList)) {
			// 既入金項目_預り金データ取得
			List<TAccgDocRepayTDepositRecvMappingEntity> repayDepositMappingList = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByParams(deleteRepayDocRepaySeqList, deleteRepayDepositRecvSeqList);
			if (LoiozCollectionUtils.isEmpty(repayDepositMappingList) || deleteRepayDocRepaySeqList.size() != repayDepositMappingList.size()) {
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 既入金項目で更新する預り金データがDBに存在するかチェックします
	 * 
	 * @param updateRepayRowList
	 * @return
	 */
	private boolean checkIfDocRepayDataUpdatedExpense(List<RepayRowInputForm> updateRepayRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(updateRepayRowList)) {
			return isTargetDataExists;
		}
		
		// 更新する既入金の既入金項目SEQ
		List<Long> updateRepayDocRepaySeqList = updateRepayRowList.stream().map(row -> row.getDocRepaySeq()).collect(Collectors.toList());
		// 更新する既入金の預りSEQ
		List<Long> updateRepayDepositRecvSeqList = updateRepayRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
		
		// 既入金の更新対象があれば既入金項目_預り金データの存在確認、更新対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(updateRepayDocRepaySeqList)) {
			// 既入金項目_預り金データ取得
			List<TAccgDocRepayTDepositRecvMappingEntity> repayDepositMappingList = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByParams(updateRepayDocRepaySeqList, updateRepayDepositRecvSeqList);
			if (LoiozCollectionUtils.isEmpty(repayDepositMappingList) || updateRepayDocRepaySeqList.size() != repayDepositMappingList.size()) {
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目データがDBに存在するかチェックします。
	 * 登録してある場合はtrueを返します。
	 * 
	 * @param invoiceRowList
	 * @return
	 * @throws AppException
	 */
	private boolean checkIfTargetDocInvoiceDataExistsInDb(List<InvoiceRowInputForm> invoiceRowList) {
		boolean isTargetDataExists = true;
		
		// 請求項目の行データが無い場合はチェック終了
		if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
			return isTargetDataExists;
		}
		
		// 未入力行を除外する
		List<InvoiceRowInputForm> validInvoiceRowList = invoiceRowList.stream().filter(form -> !form.isNotEntered()).collect(Collectors.toList());
		
		//
		// ▼ 報酬、その他の存在チェック
		//
		
		// 報酬項目か、その他項目のリスト
		List<InvoiceRowInputForm> feeOrOtherRowList = this.getFeeOrOtherRowList(validInvoiceRowList);
		
		// 請求項目から削除する報酬項目がDBに登録してあるか
		if (!this.checkIfDocInvoiceFeeDataToDeletedExistsInDb(feeOrOtherRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		// 請求項目で更新する報酬項目がDBに登録してあるか
		if (!this.checkIfDocInvoiceFeeDataToUpdatedExistsInDb(feeOrOtherRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		// 請求項目から削除するその他項目がDBに登録してあるか
		if (!this.checkIfDocInvoiceOtherDataToDeletedExistsInDb(feeOrOtherRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		// 請求項目で更新するその他項目がDBに登録してあるか
		if (!this.checkIfDocInvoiceOtherDataToUpdatedExistsInDb(feeOrOtherRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		//
		// ▼ 預り金の存在チェック
		//
		
		// 預り金項目のリスト
		List<InvoiceRowInputForm> depositRowList = this.getDepositRowList(validInvoiceRowList);
		
		// 請求項目から削除する預り金がDBに登録してあるか
		if (!this.checkIfDocInvoiceDepositDataToDeletedExistsInDb(depositRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		// 請求項目から更新する預り金がDBに登録してあるか
		if (!this.checkIfDocInvoiceDepositDataToUpdatedExistsInDb(depositRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		//
		// ▼ 実費の存在チェック
		//
		
		// 実費項目のリスト
		List<InvoiceRowInputForm> invoiceExpenseRowList = this.getInvoiceExpenseRowList(validInvoiceRowList);
		
		// 請求項目から削除する実費がDBに登録してあるか
		if (!this.checkIfDocInvoiceDataDeletedExpense(invoiceExpenseRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		// 請求項目で更新する実費がDBに登録してあるか
		if (!this.checkIfDocInvoiceDataUpdatedExpense(invoiceExpenseRowList)) {
			isTargetDataExists = false;
			return isTargetDataExists;
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目から削除する報酬データがDBに存在するかチェックします
	 * 
	 * @param feeOrOtherRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceFeeDataToDeletedExistsInDb(List<InvoiceRowInputForm> feeOrOtherRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(feeOrOtherRowList)) {
			return isTargetDataExists;
		}
		
		// 削除する報酬の請求項目SEQ
		Set<Long> deleteFeeDocInvoiceSeqSet = feeOrOtherRowList.stream()
				.filter(form -> form.getFeeSeq() != null)
				.filter(form -> form.isDeleteFlg())
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toSet());
		
		// 報酬の削除対象があれば請求項目データの存在確認、削除対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(deleteFeeDocInvoiceSeqSet)) {
			List<Long> deleteFeeDocInvoiceSeqList = new ArrayList<Long>(deleteFeeDocInvoiceSeqSet);
			
			// 請求項目データ取得
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(deleteFeeDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList) || deleteFeeDocInvoiceSeqList.size() != tAccgDocInvoiceEntityList.size()) {
				// 請求項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
			
			// 請求項目-報酬データ取得
			List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeBydocInvoiceSeqList(deleteFeeDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceFeeEntityList) || deleteFeeDocInvoiceSeqList.size() != tAccgDocInvoiceFeeEntityList.size()) {
				// 請求項目-報酬データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目から削除するその他データがDBに存在するかチェックします
	 * 
	 * @param feeOrOtherRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceOtherDataToDeletedExistsInDb(List<InvoiceRowInputForm> feeOrOtherRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(feeOrOtherRowList)) {
			return isTargetDataExists;
		}
		
		// 削除するその他項目の請求項目SEQ
		Set<Long> deleteOtherDocInvoiceSeqSet = feeOrOtherRowList.stream()
				.filter(form -> (form.getFeeSeq() == null && form.isDeleteFlg()))
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toSet());
		
		// その他項目の削除対象があれば請求項目データの存在確認、削除対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(deleteOtherDocInvoiceSeqSet)) {
			List<Long> deleteOtherDocInvoiceSeqList = new ArrayList<Long>(deleteOtherDocInvoiceSeqSet);
			
			// 請求項目データ取得
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(deleteOtherDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList) || deleteOtherDocInvoiceSeqList.size() != tAccgDocInvoiceEntityList.size()) {
				// 請求項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
			
			// 請求その他項目取得
			List<TAccgDocInvoiceOtherEntity> tAccgDocInvoiceOtherEntityList = tAccgDocInvoiceOtherDao.selectAccgDocInvoiceOtherByDocInvoiceSeq(deleteOtherDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceOtherEntityList) || deleteOtherDocInvoiceSeqList.size() != tAccgDocInvoiceOtherEntityList.size()) {
				// 請求その他項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目で更新する報酬データがDBに存在するかチェックします
	 * 
	 * @param feeOrOtherRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceFeeDataToUpdatedExistsInDb(List<InvoiceRowInputForm> feeOrOtherRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(feeOrOtherRowList)) {
			return isTargetDataExists;
		}
		
		// 更新する報酬の請求項目SEQ
		Set<Long> updateFeeDocInvoiceSeqSet = feeOrOtherRowList.stream()
				.filter(form -> StringUtils.isEmpty(form.getInvoieType()))
				.filter(form -> !form.isUnPaidFeeFlg())
				.filter(form -> !form.isAddFlg() && !form.isDeleteFlg())
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toSet());
		
		// 報酬の更新対象があれば請求項目データの存在確認、更新対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(updateFeeDocInvoiceSeqSet)) {
			List<Long> updateFeeDocInvoiceSeqList = new ArrayList<Long>(updateFeeDocInvoiceSeqSet);
			
			// 請求項目データ取得
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(updateFeeDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList) || updateFeeDocInvoiceSeqList.size() != tAccgDocInvoiceEntityList.size()) {
				// 請求項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
			
			// 請求項目-報酬データ取得
			List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeBydocInvoiceSeqList(updateFeeDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceFeeEntityList) || updateFeeDocInvoiceSeqList.size() != tAccgDocInvoiceFeeEntityList.size()) {
				// 請求項目-報酬データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目で更新するその他項目データがDBに存在するかチェックします
	 * 
	 * @param feeOrOtherRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceOtherDataToUpdatedExistsInDb(List<InvoiceRowInputForm> feeOrOtherRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(feeOrOtherRowList)) {
			return isTargetDataExists;
		}
		
		// 更新するその他項目の請求項目SEQ
		Set<Long> updateOtherDocInvoiceSeqSet = feeOrOtherRowList.stream()
				.filter(form -> form.getFeeSeq() == null)
				.filter(form -> !StringUtils.isEmpty(form.getInvoieType()))
				.filter(form -> !form.isAddFlg() && !form.isDeleteFlg())
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toSet());
		
		// その他の更新対象があれば請求項目データの存在確認、更新対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(updateOtherDocInvoiceSeqSet)) {
			List<Long> updateOtherDocInvoiceSeqList = new ArrayList<Long>(updateOtherDocInvoiceSeqSet);
			
			// 請求項目データ取得
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(updateOtherDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList) || updateOtherDocInvoiceSeqList.size() != tAccgDocInvoiceEntityList.size()) {
				// 請求項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
			
			// 請求その他項目データ取得
			List<TAccgDocInvoiceOtherEntity> tAccgDocInvoiceOtherEntityList = tAccgDocInvoiceOtherDao.selectAccgDocInvoiceOtherByDocInvoiceSeq(updateOtherDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceOtherEntityList) || updateOtherDocInvoiceSeqList.size() != tAccgDocInvoiceOtherEntityList.size()) {
				// 請求その他項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目で削除する預り金データがDBに存在するかチェックします
	 * 
	 * @param depositRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceDepositDataToDeletedExistsInDb(List<InvoiceRowInputForm> depositRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(depositRowList)) {
			return isTargetDataExists;
		}
		
		// 削除する預り金の請求項目SEQ
		Set<Long> deleteDepositDocInvoiceSeqSet = depositRowList.stream()
				.filter(form -> form.isDeleteFlg())
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toSet());
		
		// 預り金の削除対象があれば請求項目データの存在確認、削除対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(deleteDepositDocInvoiceSeqSet)) {
			List<Long> deleteDepositDocInvoiceSeqList = new ArrayList<Long>(deleteDepositDocInvoiceSeqSet);
			
			// 請求項目データ取得
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(deleteDepositDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList) || deleteDepositDocInvoiceSeqList.size() != tAccgDocInvoiceEntityList.size()) {
				// 請求項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
			
			// 請求項目-預り金データ取得
			List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceSeqList(deleteDepositDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositEntityList) || deleteDepositDocInvoiceSeqList.size() != tAccgDocInvoiceDepositEntityList.size()) {
				// 請求項目-預り金データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目で更新する預り金データがDBに存在するかチェックします
	 * 
	 * @param depositRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceDepositDataToUpdatedExistsInDb(List<InvoiceRowInputForm> depositRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(depositRowList)) {
			return isTargetDataExists;
		}
		
		// 更新する預り金の請求項目SEQ
		Set<Long> updateDepositDocInvoiceSeqSet = depositRowList.stream()
				.filter(form -> !form.isAddFlg() && !form.isDeleteFlg())
				.filter(form -> form.getDocInvoiceSeq() != null)
				.map(form -> form.getDocInvoiceSeq())
				.collect(Collectors.toSet());
		
		// 預り金の更新対象があれば請求項目データの存在確認、更新対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(updateDepositDocInvoiceSeqSet)) {
			List<Long> updateDepositDocInvoiceSeqList = new ArrayList<Long>(updateDepositDocInvoiceSeqSet);
			
			// 請求項目データ取得
			List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByDocInvoiceSeqList(updateDepositDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceEntityList) || updateDepositDocInvoiceSeqList.size() != tAccgDocInvoiceEntityList.size()) {
				// 請求項目データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
			
			// 請求項目-預り金データ取得
			List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceSeqList(updateDepositDocInvoiceSeqList);
			if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositEntityList) || updateDepositDocInvoiceSeqList.size() != tAccgDocInvoiceDepositEntityList.size()) {
				// 請求項目-預り金データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目で削除する実費データがDBに存在するかチェックします
	 * 
	 * @param invoiceExpenseRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceDataDeletedExpense(List<InvoiceRowInputForm> invoiceExpenseRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(invoiceExpenseRowList)) {
			return isTargetDataExists;
		}
		
		// 削除する実費項目の請求預り金項目SEQ
		List<Long> deleteDocInvoiceDepositSeqList = invoiceExpenseRowList.stream()
				.filter(row -> !row.isRowExpenseSumFlg())
				.filter(row -> row.isDeleteFlg())
				.filter(form -> form.getDocInvoiceDepositSeq() != null)
				.map(form -> form.getDocInvoiceDepositSeq())
				.collect(Collectors.toList());
		
		// 削除する実費項目の預り金SEQ
		List<Long> deleteDepositRecvSeqList = invoiceExpenseRowList.stream()
				.filter(row -> !row.isRowExpenseSumFlg())
				.filter(row -> row.isDeleteFlg())
				.filter(form -> !LoiozCollectionUtils.isEmpty(form.getDepositRecvSeqList()))
				.map(form -> form.getDepositRecvSeqList().get(0))
				.collect(Collectors.toList());
		
		// 実費の削除対象があれば請求項目-預り金（実費）データの存在確認、削除対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(deleteDocInvoiceDepositSeqList)) {
			// 請求項目-預り金（実費）データ取得
			List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> depositRecvMappingList = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByParams(deleteDocInvoiceDepositSeqList, deleteDepositRecvSeqList);
			if (LoiozCollectionUtils.isEmpty(depositRecvMappingList) || deleteDocInvoiceDepositSeqList.size() != depositRecvMappingList.size()) {
				// 請求項目-預り金（実費）データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 請求項目で更新する実費データがDBに存在するかチェックします
	 * 
	 * @param invoiceExpenseRowList
	 * @return
	 */
	private boolean checkIfDocInvoiceDataUpdatedExpense(List<InvoiceRowInputForm> invoiceExpenseRowList) {
		boolean isTargetDataExists = true;
		if (LoiozCollectionUtils.isEmpty(invoiceExpenseRowList)) {
			return isTargetDataExists;
		}
		
		// 更新する実費項目の請求預り金項目SEQ
		List<Long> updateDocInvoiceDepositSeqList = invoiceExpenseRowList.stream()
				.filter(row -> !row.isRowExpenseSumFlg())
				.filter(row -> !row.isDeleteFlg() && !row.isAddFlg())
				.filter(form -> form.getDocInvoiceDepositSeq() != null)
				.map(form -> form.getDocInvoiceDepositSeq())
				.collect(Collectors.toList());
		
		// 更新する実費項目の預り金SEQ
		List<Long> updateDepositRecvSeqList = invoiceExpenseRowList.stream()
				.filter(row -> !row.isRowExpenseSumFlg())
				.filter(row -> !row.isDeleteFlg() && !row.isAddFlg())
				.filter(form -> !LoiozCollectionUtils.isEmpty(form.getDepositRecvSeqList()))
				.map(form -> form.getDepositRecvSeqList().get(0))
				.collect(Collectors.toList());
		
		// 実費の更新対象があれば請求項目-預り金（実費）データの存在確認、削除対象が無ければチェックしない
		if (LoiozCollectionUtils.isNotEmpty(updateDocInvoiceDepositSeqList)) {
			// 請求項目-預り金（実費）データ取得
			List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> depositRecvMappingList = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByParams(updateDocInvoiceDepositSeqList, updateDepositRecvSeqList);
			if (LoiozCollectionUtils.isEmpty(depositRecvMappingList) || updateDocInvoiceDepositSeqList.size() != depositRecvMappingList.size()) {
				// 請求項目-預り金（実費）データが無い、対象件数と合わない場合
				isTargetDataExists = false;
			}
		}
		
		return isTargetDataExists;
	}

	/**
	 * 値引きが報酬額を超えているかチェックします<br>
	 * 値引きが多い場合はtrueを返します。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private boolean checkIfDiscountAmountExceedsFeeAmount(Long accgDocSeq) {
		boolean isMoreDiscountAmountThanFeeAmount = false;

		// 会計情報取得
		AccgInvoiceStatementAmountDto dto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// 税率0%の値引きが報酬額を超えていないかチェック
		if (this.checkIfDiscountTaxNonExceedsFeeAmount(dto)) {
			isMoreDiscountAmountThanFeeAmount = true;
		}

		// 税率8%の値引きが報酬額を超えていないかチェック
		if (this.checkIfDiscountTax8ExceedsFeeAmount(dto)) {
			isMoreDiscountAmountThanFeeAmount = true;
		}

		// 税率10%の値引きが報酬額を超えていないかチェック
		if (this.checkIfDiscountTax10ExceedsFeeAmount(dto)) {
			isMoreDiscountAmountThanFeeAmount = true;
		}

		// 源泉徴収対象額がマイナスにならないかチェック
		if (this.checkWithholdingTargetFeeIsMinus(dto)) {
			isMoreDiscountAmountThanFeeAmount = true;
		}

		// 源泉徴収税引後に金額がマイナスにならないかチェック
		if (this.checkIfAfterTaxWithholdingIsMinus(dto)) {
			isMoreDiscountAmountThanFeeAmount = true;
		}

		return isMoreDiscountAmountThanFeeAmount;
	}

	/**
	 * 既入金と請求項目-預り金が1つの請求書に存在するかチェックします<br>
	 * 両方存在する場合はtrueを返します。
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private boolean checkIfInvoiceDepositsAndRepayExists(Long accgDocSeq) {
		boolean isBothExist = false;
		// 既入金情報取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(tAccgDocRepayEntityList)) {
			return isBothExist;
		}

		// 請求項目-預り金情報取得
		List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositEntityList)) {
			return isBothExist;
		}

		// 既入金と預り金の両方が存在する場合はエラー
		long depositDataCount = tAccgDocInvoiceDepositEntityList.stream().filter(entity -> InvoiceDepositType.DEPOSIT.equalsByCode(entity.getInvoiceDepositType())).count();
		if (depositDataCount > 0) {
			isBothExist = true;
		}

		return isBothExist;
	}

	/**
	 * データの整合性チェック（既入金項目の保存処理後）（登録、更新、削除の対象データがなかった場合）<br>
	 *
	 * <pre>
	 * 保存のリクエストで、既入金項目の登録、更新、削除の対象データがない場合は、
	 * 既入金項目を空のまま保存（まとめのチェックや日付印字のチェックの状態を保存するのみ）となるため、保存処理後では、既入金項目のデータは存在しない状態になる。
	 * 
	 * 主に以下の内容を確認する
	 * ・既入金項目：存在しないこと
	 * ・マッピング：存在しないこと
	 *  （※既入金項目と同じ会計処理で利用されている預り金テーブルデータがなければ、マッピングデータは存在しないと判断する（既入金項目データも、預り金データもない場合、マッピングデータを取得することができないため。））
	 * </pre>
	 * 
	 * @param accgDocSeq 保存処理を行った会計書類のSEQ
	 * @throws AppException
	 */
	private void checkDataIntegrityAfterSaveRepayNoData(Long accgDocSeq) throws AppException {
		
		//
		// ▼ 既入金項目の確認
		//
		
		// 既入金項目のSEQを取得（存在しないはず）
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		List<Long> docRepaySeqList = tAccgDocRepayEntityList.stream().map(e -> e.getDocRepaySeq()).collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(docRepaySeqList)) {
			// 既入金項目が存在している場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 会計書類で利用されている預り金の確認（これをマッピングの存在確認とする）
		//
		
		// 保存を行った会計書類で利用されている預り金データのSEQを取得（存在しないはず）
		// ※既入金項目として利用されているデータのため、入出金タイプは「入金」が対象
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositUsedInAccgDocByParams(accgDocSeq, DepositType.NYUKIN.getCd());
		List<Long> depositRecvSeqList = tDepositRecvEntityList.stream().map(e -> e.getDepositRecvSeq()).collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			// 会計書類で利用されている（既入金項目と紐づく）預り金データが存在している場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
	}
	
	/**
	 * データの整合性チェック（請求項目の保存処理後）（請求項目-預り金（実費）について登録、更新、削除の対象データがなかった場合）<br>
	 *
	 * <pre>
	 * 保存のリクエストで、請求項目-預り金（実費）について登録、更新、削除の対象データがない場合は、
	 * 請求項目-預り金（実費）を空のまま保存となるため、保存処理後では、請求項目-預り金（実費）のデータは存在しない状態になる。
	 * 
	 * 主に以下の内容を確認する
	 * ・請求項目-預り金：入出金タイプが実費のデータが存在しないこと
	 * ・マッピング：存在しないこと
	 *  （※請求項目-預り金と同じ会計処理で利用されている預り金テーブルデータがなければ、マッピングデータは存在しないと判断する（請求項目-預り金（実費）データも、預り金データもない場合、マッピングデータを取得することができないため。））
	 * </pre>
	 * 
	 * @param accgDocSeq 保存処理を行った会計書類のSEQ
	 * @throws AppException
	 */
	private void checkDataIntegrityAfterSaveInvoiceExpenseNoData(Long accgDocSeq) throws AppException {
		
		//
		// ▼ 実費の請求項目の確認
		//
		
		// 実費の請求項目-預り金のSEQを取得（存在しないはず）
		List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByAccgDocSeq(accgDocSeq);
		List<Long> docInvoiceDepositSeqListOnlyTypeDeposit = tAccgDocInvoiceDepositEntityList.stream()
			.filter(e -> InvoiceDepositType.EXPENSE.equalsByCode(e.getInvoiceDepositType()))
			.map(e -> e.getDocInvoiceDepositSeq())
			.collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit)) {
			// 実費の請求項目-預り金が存在している場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 会計書類で利用されている預り金の確認（これをマッピングの存在確認とする）
		//
		
		// 保存を行った会計書類で利用されている預り金データのSEQを取得（存在しないはず）
		// ※請求項目-預り金（実費）として利用されているデータのため、入出金タイプは「出金」が対象
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositUsedInAccgDocByParams(accgDocSeq, DepositType.SHUKKIN.getCd());
		List<Long> depositRecvSeqList = tDepositRecvEntityList.stream().map(e -> e.getDepositRecvSeq()).collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			// 会計書類で利用されている（請求項目-預り金（実費）と紐づく）預り金データが存在している場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
	}
	
	/**
	 * データの整合性チェック（既入金項目の保存処理後）（まとめONの場合）<br>
	 * ※他のユーザーの操作と保存処理が競合した場合のための楽観チェック
	 * 
	 * <pre>
	 * 主に以下の内容を確認する
	 * ・既入金項目：まとめ行の1行しかないこと
	 * ・マッピング：まとめ行のものしかないこと（※取得する際には、既入金項目と同じ会計処理で利用されている預り金テーブルデータをもとにマッピングデータを取得している）
	 * </pre>
	 * 
	 * @param accgDocSeq 保存処理を行った会計書類のSEQ
	 * @throws AppException 
	 */
	private void checkDataIntegrityAfterSaveRepaySumON(Long accgDocSeq) throws AppException {
		
		//
		// ▼ 既入金項目の確認
		//
		
		// 既入金項目のSEQを取得（最大でまとめ行の1つしかない）
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		List<Long> docRepaySeqList = tAccgDocRepayEntityList.stream().map(e -> e.getDocRepaySeq()).collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(docRepaySeqList) && docRepaySeqList.size() != 1) {
			// 2つ以上ある（まとめ行以外が存在している）
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 預り金から → マッピング → マッピングの既入金項目SEQ の確認
		//
		
		// 既入金の保存を行った会計書類で利用されている（まとめ行と紐づいている）預り金データのSEQを取得（まとめ行があれば、少なくとも1つ以上は存在する）
		// ※既入金項目として利用されているデータのため、入出金タイプは「入金」が対象
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositUsedInAccgDocByParams(accgDocSeq, DepositType.NYUKIN.getCd());
		List<Long> depositRecvSeqList = tDepositRecvEntityList.stream().map(e -> e.getDepositRecvSeq()).collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(docRepaySeqList) && LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			// まとめ行が存在するが、まとめ行に紐づく預り金データが存在しない
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// マッピング情報（まとめ行と紐づく預り金SEQと同じ数だけある）
		List<TAccgDocRepayTDepositRecvMappingEntity> repayMappingEntityList = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeqList(depositRecvSeqList);
		if (depositRecvSeqList.size() != repayMappingEntityList.size()) {
			// 紐づく預り金とマッピングデータの数が一致しない
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// マッピング情報の既入金項目SEQ（Setでまとめた場合、最大でまとめ行のSEQである1つ分しかないはず）
		Set<Long> docRepaySeqSet = repayMappingEntityList.stream().map(e -> e.getDocRepaySeq()).collect(Collectors.toSet());
		if (!LoiozCollectionUtils.isEmpty(docRepaySeqSet) && docRepaySeqSet.size() != 1) {
			// 2つ以上ある場合（まとめ行以外のマッピング情報が存在する場合）
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 既入金項目の既入金項目SEQと、マッピングの既入金項目SEQの確認
		//
		
		if (LoiozCollectionUtils.isEmpty(docRepaySeqList) && LoiozCollectionUtils.isEmpty(docRepaySeqSet)) {
			// まとめデータがない場合（まとめONだったが、まとめデータが作成されない状態で保存された場合）
			// -> チェックはここで終了
			return;
		}
		
		// まとめデータがある場合
		
		if (LoiozCollectionUtils.isEmpty(docRepaySeqList) || LoiozCollectionUtils.isEmpty(docRepaySeqSet)) {
			// まとめデータがある場合は、必ず両方データがある -> ない場合はエラー
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 既入金項目の既入金項目SEQ
		Long docRepaySeq = docRepaySeqList.get(0);
		
		// マッピングの既入金項目SEQ
		Optional<Long> docRepaySeqOpt = docRepaySeqSet.stream().findFirst();
		Long mappingDocRepaySeq = docRepaySeqOpt.get();
		
		if (!docRepaySeq.equals(mappingDocRepaySeq)) {
			// 既入金項目と、マッピングの既入金項目SEQの値が一致していない場合
			// ※まとめ行が登録されている場合、まとめ行の既入金項目のSEQが、マッピングデータの既入金項目SEQとして登録されているはずだが、そうなっていない
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
	}
	
	/**
	 * データの整合性チェック（請求項目の保存処理後）（まとめONの場合）<br>
	 * ※他のユーザーの操作と保存処理が競合した場合のための楽観チェック
	 * 
	 * <pre>
	 * 主に以下の内容を確認する
	 * ・実費の請求項目-預り金：まとめ行の1行しかないこと
	 * ・マッピング：まとめ行のものしかないこと（※取得する際には、請求項目-預り金と同じ会計処理で利用されている預り金テーブルデータをもとにマッピングデータを取得している）
	 * </pre>
	 * 
	 * @param accgDocSeq 保存処理を行った会計書類のSEQ
	 * @throws AppException 
	 */
	private void checkDataIntegrityAfterSaveInvoiceExpenseSumON(Long accgDocSeq) throws AppException {
		
		//
		// ▼ 実費の請求項目の確認
		//
		
		// 実費の請求項目-預り金のSEQを取得（最大でまとめ行の1つしかない）
		List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByAccgDocSeq(accgDocSeq);
		List<Long> docInvoiceDepositSeqListOnlyTypeDeposit = tAccgDocInvoiceDepositEntityList.stream()
			.filter(e -> InvoiceDepositType.EXPENSE.equalsByCode(e.getInvoiceDepositType()))
			.map(e -> e.getDocInvoiceDepositSeq())
			.collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit) && docInvoiceDepositSeqListOnlyTypeDeposit.size() != 1) {
			// 2つ以上ある（まとめ行以外が存在している）
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 預り金から → マッピング → マッピングの既入金項目SEQ の確認
		//
		
		// 保存を行った会計書類で利用されている預り金データのSEQを取得
		// ※請求項目-預り金（実費）として利用されているデータのため、入出金タイプは「出金」が対象
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositUsedInAccgDocByParams(accgDocSeq, DepositType.SHUKKIN.getCd());
		List<Long> depositRecvSeqList = tDepositRecvEntityList.stream().map(e -> e.getDepositRecvSeq()).collect(Collectors.toList());
		if (!LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit) && LoiozCollectionUtils.isEmpty(depositRecvSeqList)) {
			// まとめ行が存在するが、まとめ行に紐づく預り金データが存在しない
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// マッピング情報（まとめ行に紐づく預り金SEQと同じ数だけある）
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> invoiceMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeqList(depositRecvSeqList);
		if (depositRecvSeqList.size() != invoiceMappingEntityList.size()) {
			// 紐づく預り金とマッピングデータの数が一致しない
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// マッピング情報の請求預り金項目SEQ（Setでまとめた場合、最大でまとめ行のSEQである1つ分しかないはず）
		Set<Long> docInvoiceDepositSeqSet = invoiceMappingEntityList.stream().map(e -> e.getDocInvoiceDepositSeq()).collect(Collectors.toSet());
		if (!LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqSet) && docInvoiceDepositSeqSet.size() != 1) {
			// 2つ以上ある場合（まとめ行以外のマッピング情報が存在する場合）
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 請求項目-預り金の請求項目-預り金SEQと、マッピングの請求項目-預り金SEQの確認
		//
		
		if (LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit) && LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqSet)) {
			// まとめデータがない場合（まとめONだったが、まとめデータが作成されない状態で保存された場合）
			// -> チェックはここで終了
			return;
		}
		
		// まとめデータがある場合
		
		if (LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit) || LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqSet)) {
			// まとめデータがある場合は、必ず両方データがある -> ない場合はエラー
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 請求項目-預り金の請求項目-預り金SEQ
		Long docInvoiceDepositSeq = docInvoiceDepositSeqListOnlyTypeDeposit.get(0);
		
		// マッピングの請求項目-預り金SEQ
		Optional<Long> docInvoiceDepositSeqOpt = docInvoiceDepositSeqSet.stream().findFirst();
		Long mappingDocInvoiceDepositSeq = docInvoiceDepositSeqOpt.get();
		
		if (!docInvoiceDepositSeq.equals(mappingDocInvoiceDepositSeq)) {
			// 請求項目-預り金と、マッピングの請求項目-預り金SEQの値が一致していない場合
			// ※まとめ行が登録されている場合、まとめ行の請求項目-預り金SEQが、マッピングデータの請求項目-預り金SEQとして登録されているはずだが、そうなっていない
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
	
	/**
	 * データの整合性チェック（既入金項目の保存処理後）（まとめOFFの場合）<br>
	 * ※他のユーザーの操作と保存処理が競合した場合のための楽観チェック
	 * 
	 * <pre>
	 * 主に以下の内容を確認する
	 * ・既入金項目：マッピングテーブルと同じ数、同じ既入金項目SEQであること
	 * ・マッピング：既入金項目と同じ数、同じ既入金項目SEQであること（※取得する際には、既入金項目と同じ会計処理で利用されている預り金テーブルデータをもとにマッピングデータを取得している）
	 * </pre>
	 * 
	 * @param accgDocSeq 保存処理を行った会計書類のSEQ
	 * @throws AppException 
	 */
	private void checkDataIntegrityAfterSaveRepaySumOFF(Long accgDocSeq) throws AppException {
		
		// 既入金項目のSEQを取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		List<Long> docRepaySeqList = tAccgDocRepayEntityList.stream().map(e -> e.getDocRepaySeq()).collect(Collectors.toList());
		
		// 既入金項目として利用されている預り金のSEQを取得
		// ※既入金項目として利用されているデータのため、入出金タイプは「入金」が対象
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositUsedInAccgDocByParams(accgDocSeq, DepositType.NYUKIN.getCd());
		List<Long> depositRecvSeqList = tDepositRecvEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
		
		// マッピング情報の既入金項目SEQ（既入金項目と同じ数だけあるはず）
		List<TAccgDocRepayTDepositRecvMappingEntity> repayMappingEntityList = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeqList(depositRecvSeqList);
		Set<Long> docRepaySeqSet = repayMappingEntityList.stream().map(e -> e.getDocRepaySeq()).collect(Collectors.toSet());
		
		if (LoiozCollectionUtils.isEmpty(docRepaySeqList) && LoiozCollectionUtils.isEmpty(docRepaySeqSet)) {
			// 既入金項目データがない場合
			// -> チェックはここで終了
			return;
		}
		
		//
		// ▼ 既入金項目の既入金項目SEQと、マッピングの既入金項目SEQの数が同じことの確認
		//
		
		// 既入金項目データがある場合
		
		if (LoiozCollectionUtils.isEmpty(docRepaySeqList) || LoiozCollectionUtils.isEmpty(docRepaySeqSet)) {
			// 既入金項目データがある場合は、必ず両方データがある -> ない場合はエラー
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		if (docRepaySeqList.size() != docRepaySeqSet.size()) {
			// 登録されている既入金項目のSEQと、マッピングに登録されている既入金項目のSEQの数が異なる場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 既入金項目の既入金項目SEQと、マッピングの既入金項目SEQの値が同じことの確認
		//
		
		// 一致するSEQを削除
		docRepaySeqList.removeAll(docRepaySeqSet);
		
		if (!LoiozCollectionUtils.isEmpty(docRepaySeqList)) {
			// 削除されなかったSEQがある = 既入金項目とマッピングで異なる既入金項目SEQのデータが存在する状態だった
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
	}
	
	/**
	 * データの整合性チェック（請求項目の保存処理後）（まとめOFFの場合）<br>
	 * ※他のユーザーの操作と保存処理が競合した場合のための楽観チェック
	 * 
	 * <pre>
	 * 主に以下の内容を確認する
	 * ・実費の請求項目-預り金：マッピングテーブルと同じ数、同じ請求項目-預り金SEQであること
	 * ・マッピング：実費の請求項目-預り金と同じ数、同じ請求項目-預り金SEQであること（※取得する際には、請求項目-預り金と同じ会計処理で利用されている預り金テーブルデータをもとにマッピングデータを取得している）
	 * </pre>
	 * 
	 * @param accgDocSeq 保存処理を行った会計書類のSEQ
	 * @throws AppException 
	 */
	private void checkDataIntegrityAfterSaveInvoiceExpenseSumOFF(Long accgDocSeq) throws AppException {
		
		// 実費の請求項目-預り金のSEQを取得
		List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByAccgDocSeq(accgDocSeq);
		List<Long> docInvoiceDepositSeqListOnlyTypeDeposit = tAccgDocInvoiceDepositEntityList.stream()
			.filter(e -> InvoiceDepositType.EXPENSE.equalsByCode(e.getInvoiceDepositType()))
			.map(e -> e.getDocInvoiceDepositSeq())
			.collect(Collectors.toList());
		
		// 請求項目-預り金として利用されている預り金のSEQを取得
		// ※請求項目-預り金（実費）として利用されているデータのため、入出金タイプは「出金」が対象
		List<TDepositRecvEntity> tDepositRecvEntityList = tDepositRecvDao.selectDepositUsedInAccgDocByParams(accgDocSeq, DepositType.SHUKKIN.getCd());
		List<Long> depositRecvSeqList = tDepositRecvEntityList.stream().map(e -> e.getDepositRecvSeq()).collect(Collectors.toList());
		
		// マッピング情報の請求項目-預り金SEQ（実費の請求項目-預り金と同じ数だけある）
		List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> invoiceMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeqList(depositRecvSeqList);
		Set<Long> docInvoiceDepositSeqSet = invoiceMappingEntityList.stream().map(e -> e.getDocInvoiceDepositSeq()).collect(Collectors.toSet());
		
		if (LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit) && LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqSet)) {
			// 実費の請求項目-預り金データがない場合
			// -> チェックはここで終了
			return;
		}
		
		//
		// ▼ 請求項目-預り金の請求項目-預り金SEQと、マッピングの請求項目-預り金SEQの数が同じことの確認
		//
		
		// 実費の請求項目-預り金データがある場合
		
		if (LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit) || LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqSet)) {
			// 実費の請求項目-預り金データがある場合は、必ず両方データがある -> ない場合はエラー
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		if (docInvoiceDepositSeqListOnlyTypeDeposit.size() != docInvoiceDepositSeqSet.size()) {
			// 実費の請求項目-預り金のSEQと、マッピングに登録されている請求項目-預り金のSEQの数が異なる場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		//
		// ▼ 実費の請求項目-預り金のSEQと、マッピングの請求項目-預り金のSEQの値が同じことの確認
		//
		
		// 一致するSEQを削除
		docInvoiceDepositSeqListOnlyTypeDeposit.removeAll(docInvoiceDepositSeqSet);
		
		if (!LoiozCollectionUtils.isEmpty(docInvoiceDepositSeqListOnlyTypeDeposit)) {
			// 削除されなかったSEQがある = 実費の請求項目-預り金とマッピングで異なる請求項目-預り金SEQのデータが存在する状態だった
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
	}
	
	/**
	 * 既入金項目テーブルに既入金情報が存在するかチェックします。<br>
	 * 存在する場合はtrueを返します。<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	private boolean checkIfReapyExists(Long accgDocSeq) {
		boolean isRepayExists = false;
		// 既入金項目情報取得
		List<TAccgDocRepayEntity> tAccgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(tAccgDocRepayEntityList)) {
			return isRepayExists;
		} else {
			isRepayExists = true;
			return isRepayExists;
		}
	}
	
	/**
	 * 請求書詳細、精算書詳細：既入金入力フォームの金額合計が請求書、精算書で使用できる金額桁数かをチェックします。<br>
	 * 使用可能な金額桁数を超えている場合はAppExceptionをthrowします。
	 * 
	 * @param repayRowList
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void checkRepayAmountDigits(List<RepayRowInputForm> repayRowList, Long accgDocSeq) throws AppException {
		// 会計書類データ取得
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// フォームの値を元にした金額情報Dtoを取得
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService
				.getAccgInvoiceStatementAmountDtoUsingRepayRowInputForm(repayRowList, accgDocSeq);
		
		AccgDocType docType = AccgDocType.of(tAccgDocEntity.getAccgDocType());
		if (docType.isInvoice()) {
			// 請求書の場合
			
			// 請求額のチェック
			BigDecimal invoiceAmount = accgInvoiceStatementAmountDto.getInvoiceAmount();
			if (invoiceAmount.precision() > CommonConstant.MAX_KINGAKU_KETA) {
				throw new AppException(MessageEnum.MSG_E00188, null, "請求書", "請求");
			}
		} else if (docType.isStatement()) {
			// 精算書の場合
			
			// 精算額のチェック
			BigDecimal statementAmount = accgInvoiceStatementAmountDto.getStatementAmount();
			if (statementAmount.precision() > CommonConstant.MAX_KINGAKU_KETA) {
				throw new AppException(MessageEnum.MSG_E00188, null, "精算書", "精算");
			}
		} else {
			// 想定外のEnum値
			throw new RuntimeException("想定外のEnum値");
		}
	}
	
	/**
	 * 会計書類SEQに紐づく請求書、精算書が下書きかどうか、請求額、精算額の上限チェックをおこないます。<br>
	 * エラーがある場合は AppExceptionをthrowします。<br>
	 * 
	 * @param accgDocSeq
	 * @param accgDocType
	 * @param invoiceRowList
	 * @return invoiceRowListを元にした金額情報DTO
	 * @throws AppException
	 */
	private AccgInvoiceStatementAmountDto checkIssuingStatusIsDraftAndWithinMaxAmount(Long accgDocSeq, String accgDocType,
			List<InvoiceRowInputForm> invoiceRowList) throws AppException {
		// 返却用DTO
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = null;
		
		AccgDocType docType = AccgDocType.of(accgDocType);
		if (docType.isInvoice()) {
			// 請求書の場合
			
			// ステータスチェック
			if (!this.isIssueStatusDraft(accgDocSeq, docType)) {
				// 下書きではない
				throw new AppException(MessageEnum.MSG_W00016, null, "請求書", "編集");
			}
			
			// 行データが無い場合は、チェック終了
			if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
				return accgInvoiceStatementAmountDto;
			}
			
			// フォームの値を元にした金額情報Dtoを取得
			accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(invoiceRowList, accgDocSeq);
			// 請求額のチェック
			BigDecimal invoiceAmount = accgInvoiceStatementAmountDto.getInvoiceAmount();
			if (invoiceAmount.precision() > CommonConstant.MAX_KINGAKU_KETA) {
				throw new AppException(MessageEnum.MSG_E00188, null, "請求書", "請求");
			}
		} else if (docType.isStatement()) {
			// 精算書の場合
			
			// ステータスチェック
			if (!this.isIssueStatusDraft(accgDocSeq, docType)) {
				// 下書きではない
				throw new AppException(MessageEnum.MSG_W00016, null, "精算書", "編集");
			}
			
			// 行データが無い場合は、チェック終了
			if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
				return accgInvoiceStatementAmountDto;
			}
			
			// フォームの値を元にした金額情報Dtoを取得
			accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(invoiceRowList, accgDocSeq);
			// 精算額のチェック
			BigDecimal statementAmount = accgInvoiceStatementAmountDto.getStatementAmount();
			if (statementAmount.precision() > CommonConstant.MAX_KINGAKU_KETA) {
				throw new AppException(MessageEnum.MSG_E00188, null, "精算書", "精算");
			}
		} else {
			// 想定外のEnum値
			throw new RuntimeException("想定外のEnum値");
		}
		
		return accgInvoiceStatementAmountDto;
	}

	/**
	 * 請求項目入力データの報酬について、ステータスが全て「未請求」かチェックします<br>
	 * 
	 * @param invoiceRowList
	 * @return
	 * @throws AppException
	 */
	private boolean checkFeePaymentStatusIsUnclaimed(List<InvoiceRowInputForm> invoiceRowList) throws AppException {
		boolean isFeePaymentStatusOfAllUnclaimed = true;
		if (LoiozCollectionUtils.isEmpty(invoiceRowList)) {
			// データがない場合は「未請求」とする
			return isFeePaymentStatusOfAllUnclaimed;
		}
		
		// 登録済み報酬SEQを取得
		List<Long> feeSeqList = invoiceRowList.stream()
				.filter(row -> !row.isDeleteFlg())
				.filter(row -> row.getFeeSeq() != null)
				.map(row -> row.getFeeSeq())
				.collect(Collectors.toList());
		
		// 報酬が全て「未請求」か
		isFeePaymentStatusOfAllUnclaimed = commonAccgService.checkFeePaymentStatusIsUnclaimed(feeSeqList);
		
		return isFeePaymentStatusOfAllUnclaimed;
	}

	/**
	 * 対象の会計書類（精算書/請求書）の発行ステータスが「下書き」かどうかを判定
	 * 
	 * @param accgDocSeq
	 * @param docType
	 * @return 発行ステータスが「下書き」の場合true, 「下書き」ではない場合false
	 * @throws AppException
	 */
	private boolean isIssueStatusDraft(Long accgDocSeq, AccgDocType docType) throws AppException {
		
		IssueStatus issueStatus = null;
		
		if (docType.isInvoice()) {
			// 請求書の場合
			
			// 請求書情報取得
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			if (tAccgInvoiceEntity == null) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			
			issueStatus = IssueStatus.of(tAccgInvoiceEntity.getInvoiceIssueStatus());
			
		} else if (docType.isStatement()) {
			// 精算書の場合
			
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			if (tAccgStatementEntity == null) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_NO_DATA);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			
			issueStatus = IssueStatus.of(tAccgStatementEntity.getStatementIssueStatus());
			
		} else {
			// 想定外のEnum値
			throw new RuntimeException("想定外のEnum値");
		}
		
		if (issueStatus == IssueStatus.DRAFT) {
			// 下書き
			return true;
		} else {
			// 下書きではない
			return false;
		}
	}

	/**
	 * 会計書類-対応の「新規作成」を実行しているか<br>
	 * 実行している場合はtrueを返します。
	 * 
	 * @param accgDocActList
	 * @return
	 */
	private boolean getNewAccgDocActExecFlg(List<TAccgDocActEntity> accgDocActList) {
		boolean isExec = false;
		if (LoiozCollectionUtils.isEmpty(accgDocActList)) {
			return isExec;
		}

		// 「新規作成」対応の件数
		long newAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.NEW.equalsByCode(e.getActType())).count();
		if (newAccgDocActCount > 0) {
			isExec = true;
		}
		return isExec;
	}

	/**
	 * 会計書類-対応の「発行」を実行しているか<br>
	 * 実行している場合はtrueを返します。
	 * 
	 * @param accgDocActList
	 * @return
	 */
	private boolean getIssueAccgDocActExecFlg(List<TAccgDocActEntity> accgDocActList) {
		boolean isExec = false;
		if (LoiozCollectionUtils.isEmpty(accgDocActList)) {
			return isExec;
		}

		// 「発行」対応の件数
		long issueAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.ISSUE.equalsByCode(e.getActType())).count();
		if (issueAccgDocActCount > 0) {
			isExec = true;
		}
		return isExec;
	}

	/**
	 * 会計書類-対応の「送信」を実行しているか<br>
	 * 実行している場合はtrueを返します。
	 * 
	 * @param accgDocActList
	 * @return
	 */
	private boolean getSendAccgDocActExecFlg(List<TAccgDocActEntity> accgDocActList) {
		boolean isExec = false;
		if (LoiozCollectionUtils.isEmpty(accgDocActList)) {
			return isExec;
		}

		// 「送信」対応の件数
		long sendAccgDocActCount = accgDocActList.stream().filter(e -> AccgDocActType.SEND.equalsByCode(e.getActType())).count();
		if (sendAccgDocActCount > 0) {
			isExec = true;
		}
		return isExec;
	}


	//=========================================================================
	// ▼ 登録／更新／削除系
	//=========================================================================
	
	/**
	 * 会計書類SEQに紐づく報酬データから請求書、精算書を切り離します。<br>
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void disassociateFeeFromDoc(Long accgDocSeq) throws AppException {
		// 報酬データを取得
		List<TFeeEntity> tFeeEntityList = tFeeDao.selectFeeEntityByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(tFeeEntityList)) {
			return;
		}

		// 入金ステータスを「未請求」、会計書類SEQをnull、回収不能フラグに0をセット
		for (TFeeEntity entity : tFeeEntityList) {
			entity.setFeePaymentStatus(FeePaymentStatus.UNCLAIMED.getCd());
			entity.setAccgDocSeq(null);
			entity.setUncollectibleFlg(SystemFlg.FLG_OFF.getCd());
		}

		this.batchUpdateTFee(tFeeEntityList);
	}
	
	/**
	 * まとめ表示時の既入金項目入力用Formリストを作成します。
	 * 
	 * @param forDisplayRepayForm 画面で表示していたまとめ用既入金情報
	 * @param addDepositRecvEntityList 新たに紐づける預り金情報リスト
	 * @param repayInputForm 既入金入力フラグメント用フォーム
	 * @return
	 */
	private List<RepayRowInputForm> createSumRepayRowInputFormList(
			RepayRowInputForm forDisplayRepayForm, List<TDepositRecvEntity> addDepositRecvEntityList,
			AccgInvoiceStatementInputForm.RepayInputForm repayInputForm) {
		// 返却用Formリスト
		List<RepayRowInputForm> formList = new ArrayList<>();
		
		// 会計書類SEQ
		Long accgDocSeq = repayInputForm.getAccgDocSeq();
		
		// 既存の登録・更新処理用既入金情報リスト
		List<RepayRowInputForm> forProcessingRepayList = repayInputForm.getRepayRowList().stream()
				.filter(form -> !form.isRowRepaySumFlg())
				.collect(Collectors.toList());
		
		// 預り金合計と画面で表示していた既入金額を合算
		BigDecimal totalDepositAmount = this.getTotalDepositAmount(addDepositRecvEntityList, forProcessingRepayList);
		
		// まとめ表示時の既入金項目SEQ取得
		Long docRepaySeqWhenSummed = repayInputForm.getDocRepaySeqWhenSummed();
				
		// まとめ表示用form作成
		RepayRowInputForm viewRow = this.createForDisplaySumRepayForm(forDisplayRepayForm, accgDocSeq, docRepaySeqWhenSummed);

		// まとめ表示用formに既入金額セット
		viewRow.setRepayAmount(AccountingUtils.toDispAmountLabel(totalDepositAmount));

		// まとめ表示用formに行数セット
		Long repayRowCount = 0L;
		viewRow.setRepayRowCount(repayRowCount++);

		// formにセット
		formList.add(viewRow);

		// 登録・更新処理用に預り金のformを作成
		for (TDepositRecvEntity tDepositRecvEntity : addDepositRecvEntityList) {
			RepayRowInputForm row = new RepayRowInputForm();
			// 会計書類SEQ
			row.setAccgDocSeq(accgDocSeq);
			// 日付
			row.setRepayTransactionDate(DateUtils.parseToString(tDepositRecvEntity.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			// 預り金SEQ
			row.setDepositRecvSeqList(List.of(tDepositRecvEntity.getDepositRecvSeq()));
			// 預り金
			row.setRepayAmount(AccountingUtils.toDispAmountLabel(tDepositRecvEntity.getDepositAmount()));
			// 項目名
			row.setRepayItemName(tDepositRecvEntity.getDepositItemName());
			// 摘要
			row.setSumText(tDepositRecvEntity.getSumText());
			row.setAddFlg(true);
			row.setDeleteFlg(false);
			row.setDepositMadeFromAccgDocFlg(tDepositRecvEntity.getCreatedAccgDocSeq() == null ? false : true);
			row.setDisplayFlg(false);
			row.setRepayRowCount(repayRowCount++);

			formList.add(row);
		}

		// 既存の登録・更新処理用を再作成
		for (RepayRowInputForm form : forProcessingRepayList) {
			RepayRowInputForm row = new RepayRowInputForm();
			// 既入金項目SEQ
			row.setDocRepaySeq(form.getDocRepaySeq());
			// 会計書類SEQ
			row.setAccgDocSeq(form.getAccgDocSeq());
			// 日付
			row.setRepayTransactionDate(form.getRepayTransactionDate());
			// 預り金SEQ
			row.setDepositRecvSeqList(form.getDepositRecvSeqList());
			// 預り金
			row.setRepayAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.parseAsBigDecimal(form.getRepayAmount())));
			// 項目名
			row.setRepayItemName(form.getRepayItemName());
			// 摘要
			row.setSumText(form.getSumText());
			row.setAddFlg(form.isAddFlg());
			row.setDeleteFlg(form.isDeleteFlg());
			row.setDepositMadeFromAccgDocFlg(form.isDepositMadeFromAccgDocFlg());
			row.setDisplayFlg(false);
			row.setRepayRowCount(repayRowCount++);

			formList.add(row);
		}

		return formList;
	}

	/**
	 * まとめ表示時の請求項目入力用Formリストを作成します。
	 * 
	 * @param forDisplayInvoiceForm 画面で表示していたまとめ用請求項目（実費）情報
	 * @param addDepositRecvEntityList 新たに紐づける預り金（実費）情報リスト
	 * @param invoiceInputForm 請求項目入力フラグメント用フォーム
	 * @return
	 */
	private List<InvoiceRowInputForm> createSumInvoiceRowInputFormList(
			@Nullable InvoiceRowInputForm forDisplayInvoiceForm, List<TDepositRecvEntity> addDepositRecvEntityList,
			AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) {
		// 会計書類SEQ
		Long accgDocSeq = invoiceInputForm.getAccgDocSeq();
		
		// 返却用Formリスト
		List<InvoiceRowInputForm> formList = new ArrayList<>();
		
		// 実費以外のデータ
		List<InvoiceRowInputForm> notExpenseList = invoiceInputForm.getInvoiceRowList().stream()
				.filter(form -> !(form.isDepositRecvFlg() && InvoiceDepositType.EXPENSE.equalsByCode(form.getInvoieType())))
				.collect(Collectors.toList());
		
		// 実費以外のデータはそのまま返却(rowCountをふりなおし)
		Long invoiceRowCount = 0L;
		for (InvoiceRowInputForm invoiceRowInputForm : notExpenseList) {
			invoiceRowInputForm.setInvoiceRowCount(invoiceRowCount++);
			formList.add(invoiceRowInputForm);
		}
		
		// 既存の登録・更新処理用実費情報リスト
		List<InvoiceRowInputForm> forProcessingExpenseList = invoiceInputForm.getInvoiceRowList().stream()
				.filter(form -> !form.isRowExpenseSumFlg())
				.filter(form -> form.isDepositRecvFlg())
				.filter(form -> InvoiceDepositType.EXPENSE.equalsByCode(form.getInvoieType()))
				.collect(Collectors.toList());
		
		// 預り金合計と画面で表示していた実費を合算
		BigDecimal totalExpenseAmount = this.getTotalExpenseAmount(addDepositRecvEntityList, forProcessingExpenseList);
		
		// まとめ表示時の請求項目SEQ、請求預り金項目SEQを取得
		Long docInvoiceSeqWhenSummed = invoiceInputForm.getDocInvoiceSeqWhenSummed();
		Long docInvoiceDepositSeqWhenSummed = invoiceInputForm.getDocInvoiceDepositSeqWhenSummed();
		
		// まとめ表示用form作成
		InvoiceRowInputForm viewRow = this.createForDisplaySumInvoiceForm(forDisplayInvoiceForm, accgDocSeq, docInvoiceSeqWhenSummed, docInvoiceDepositSeqWhenSummed);
		
		// まとめ表示用formに既入金額セット
		viewRow.setAmount(AccountingUtils.toDispAmountLabel(totalExpenseAmount));
		
		// まとめ表示用formに行数セット
		viewRow.setInvoiceRowCount(invoiceRowCount++);
		
		// formにセット
		formList.add(viewRow);
		
		// 登録・更新処理用に預り金のformを作成
		for (TDepositRecvEntity tDepositRecvEntity : addDepositRecvEntityList) {
			InvoiceRowInputForm row = new InvoiceRowInputForm();
			// 会計書類SEQ
			row.setAccgDocSeq(accgDocSeq);
			// 日付
			row.setTransactionDate(DateUtils.parseToString(tDepositRecvEntity.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			// 預り金SEQ
			row.setDepositRecvSeqList(List.of(tDepositRecvEntity.getDepositRecvSeq()));
			// 預り金
			row.setAmount(AccountingUtils.toDispAmountLabel(tDepositRecvEntity.getWithdrawalAmount()));
			// 項目名
			row.setItemName(tDepositRecvEntity.getDepositItemName());
			// 摘要
			row.setSumText(tDepositRecvEntity.getSumText());
			row.setInvoieType(InvoiceDepositType.EXPENSE.getCd());
			row.setDepositRecvFlg(true);
			row.setAddFlg(true);
			row.setDeleteFlg(false);
			row.setDisplayFlg(false);
			row.setInvoiceRowCount(invoiceRowCount++);
			
			formList.add(row);
		}
		
		// 既存の登録・更新処理用を再作成
		for (InvoiceRowInputForm form : forProcessingExpenseList) {
			InvoiceRowInputForm row = new InvoiceRowInputForm();
			// 請求項目SEQ
			row.setDocInvoiceSeq(form.getDocInvoiceSeq());
			// 請求預り金項目SEQ
			row.setDocInvoiceDepositSeq(form.getDocInvoiceDepositSeq());
			// 会計書類SEQ
			row.setAccgDocSeq(form.getAccgDocSeq());
			// 日付
			row.setTransactionDate(form.getTransactionDate());
			// 預り金SEQ
			row.setDepositRecvSeqList(form.getDepositRecvSeqList());
			// 預り金
			row.setAmount(AccountingUtils.toDispAmountLabel(LoiozNumberUtils.parseAsBigDecimal(form.getAmount())));
			// 項目名
			row.setItemName(form.getItemName());
			// 摘要
			row.setSumText(form.getSumText());
			row.setInvoieType(InvoiceDepositType.EXPENSE.getCd());
			row.setDepositRecvFlg(true);
			row.setAddFlg(form.isAddFlg());
			row.setDeleteFlg(form.isDeleteFlg());
			row.setDisplayFlg(false);
			row.setInvoiceRowCount(invoiceRowCount++);
			
			formList.add(row);
		}
		
		return formList;
	}

	/**
	 * 既入金項目入力のまとめ表示用情報を作成します。<br>
	 * パラメータで受け取ったforDisplayRepayFormが空であれば新規作成、空でなければそれを使用します。<br>
	 * 
	 * @param forDisplayRepayForm
	 * @param accgDocSeq
	 * @param docRepaySeqWhenSummed 既入金合算時の既入金項目SEQ
	 * @return
	 */
	private RepayRowInputForm createForDisplaySumRepayForm(RepayRowInputForm forDisplayRepayForm, Long accgDocSeq,
			Long docRepaySeqWhenSummed) {
		//返却用Form
		RepayRowInputForm createForDisplaySumRepayForm;
		
		// まとめ表示行が画面上に無い場合は新規作成するが、元々画面表示時にまとめ表示行があった場合は、削除操作する前に存在していた既入金項目SEQを引継ぎ更新行とする
		if (forDisplayRepayForm == null) {
			// まとめ表示用のformを1つ作成
			createForDisplaySumRepayForm = new RepayRowInputForm();
			
			// 既入金項目SEQセット
			if (docRepaySeqWhenSummed != null) {
				createForDisplaySumRepayForm.setDocRepaySeq(docRepaySeqWhenSummed);
			}
			
			// 会計書類SEQセット
			createForDisplaySumRepayForm.setAccgDocSeq(accgDocSeq);
			
			// 項目名 -> 預り金
			createForDisplaySumRepayForm.setRepayItemName(InvoiceDepositType.DEPOSIT.getVal());
			
			// 追加フラグ -> 合算時の既入金項目SEQがある場合は更新データとして作成するためfalseにする
			if (docRepaySeqWhenSummed != null) {
				createForDisplaySumRepayForm.setAddFlg(false);
			} else {
				createForDisplaySumRepayForm.setAddFlg(true);
			}
			createForDisplaySumRepayForm.setDeleteFlg(false);
			createForDisplaySumRepayForm.setDisplayFlg(true);
			createForDisplaySumRepayForm.setRowRepaySumFlg(true);
		} else {
			createForDisplaySumRepayForm = forDisplayRepayForm;
		}
		return createForDisplaySumRepayForm;
	}

	/**
	 * 請求項目入力のまとめ表示用情報を作成します。<br>
	 * パラメータで受け取ったforDisplayInvoiceFormが空であれば新規作成、空でなければそれを使用します。
	 * 
	 * @param forDisplayInvoiceForm 画面で表示していたまとめ用請求項目（実費）情報
	 * @param accgDocSeq 会計情報SEQ
	 * @param docInvoiceSeqWhenSummed 合算時の請求項目SEQ
	 * @param docInvoiceDepositSeqWhenSummed 合算時の請求預り金項目SEQ
	 * @return
	 */
	private InvoiceRowInputForm createForDisplaySumInvoiceForm(@Nullable InvoiceRowInputForm forDisplayInvoiceForm,
			Long accgDocSeq, Long docInvoiceSeqWhenSummed, Long docInvoiceDepositSeqWhenSummed) {
		// 返却用Form
		InvoiceRowInputForm createForDisplaySumInvoiceForm;
		
		// まとめ表示行が画面上に無い場合は新規作成するが、元々画面表示時にまとめ表示行があった場合は、削除操作する前に存在していた既入金項目SEQを引継ぎ更新行とする
		if (forDisplayInvoiceForm == null) {
			// まとめ表示用のformを1つ作成
			createForDisplaySumInvoiceForm = new InvoiceRowInputForm();
			
			// 請求項目SEQセット
			if (docInvoiceSeqWhenSummed != null) {
				createForDisplaySumInvoiceForm.setDocInvoiceSeq(docInvoiceSeqWhenSummed);
			}
			
			// 請求預り金項目SEQセット
			if (docInvoiceDepositSeqWhenSummed != null) {
				createForDisplaySumInvoiceForm.setDocInvoiceDepositSeq(docInvoiceDepositSeqWhenSummed);
			}
			
			// 会計書類SEQセット
			createForDisplaySumInvoiceForm.setAccgDocSeq(accgDocSeq);
			
			// 項目名 -> 実費合計
			createForDisplaySumInvoiceForm.setItemName(AccgConstant.ACCG_ITEM_NAME_OF_SUM_EXPENSE);
			
			// 追加フラグ -> 合算時の請求項目SEQがある場合は更新データとして作成するためfalseにする
			if (docInvoiceSeqWhenSummed != null) {
				createForDisplaySumInvoiceForm.setAddFlg(false);
			} else {
				createForDisplaySumInvoiceForm.setAddFlg(true);
			}
			createForDisplaySumInvoiceForm.setInvoieType(InvoiceDepositType.EXPENSE.getCd());
			createForDisplaySumInvoiceForm.setDepositRecvFlg(true);
			createForDisplaySumInvoiceForm.setDeleteFlg(false);
			createForDisplaySumInvoiceForm.setDisplayFlg(true);
			createForDisplaySumInvoiceForm.setRowExpenseSumFlg(true);
			createForDisplaySumInvoiceForm.setSumText(AccgConstant.ACCG_SUM_TEXT_OF_SUM_EXPENSE);
		} else {
			createForDisplaySumInvoiceForm = forDisplayInvoiceForm;
		}
		return createForDisplaySumInvoiceForm;
	}

	/**
	 * 会計書類ファイル名を作成します
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @param fileExtension
	 * @return
	 */
	private String createAccgDocFileName(Long accgDocSeq,AccgDocFileType accgDocFileType, FileExtension fileExtension) {
		return commonAccgService.createAccgDocFileName(accgDocSeq, accgDocFileType, fileExtension);
	}

	/**
	 * 売上、売上明細の登録 or 更新をします<br>
	 * このメソッド実行前に売上に関する取引実績や報酬がある場合は先に登録をしてください。<br>
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param accgDocType
	 * @param accgInvoiceStatementAmountDto
	 * @return
	 * @throws AppException
	 */
	private TSalesDetailEntity registSalesInformation(Long accgDocSeq, Long ankenId, Long personId, AccgDocType accgDocType, AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) throws AppException {
		// 売上データ取得
		TSalesEntity tSalesEntity = tSalesDao.selectSalesEntityByPersonIdAndAnkenId(personId, ankenId);
		TSalesDetailEntity tSalesDetailEntity = new TSalesDetailEntity();
		if (tSalesEntity == null) {
			// 売上が登録されていなければ、売上データを登録し明細を登録する。
			tSalesEntity = commonAccgService.registSales(ankenId, personId);
			// 売上明細の登録
			tSalesDetailEntity = this.registSalesDetail(tSalesEntity.getSalesSeq(), accgDocSeq, accgInvoiceStatementAmountDto, accgDocType);
			// 売上データの更新
			commonAccgService.updateSalesAmount(personId, ankenId);
		} else {
			// 売上が登録してある場合は、売上明細を先に登録して売上データを更新する。売上合計【見込み】に反映させるため
			tSalesDetailEntity = this.registSalesDetail(tSalesEntity.getSalesSeq(), accgDocSeq, accgInvoiceStatementAmountDto, accgDocType);
			// 売上データの更新
			commonAccgService.updateSalesAmount(personId, ankenId);
		}
		// 売上明細-消費税の登録
		this.registerSalesDetailTax(tSalesDetailEntity.getSalesDetailSeq(), accgInvoiceStatementAmountDto);

		return tSalesDetailEntity;
	}

	/**
	 * 報酬があれば、売上明細を登録します<br>
	 * 
	 * @param salesSeq
	 * @param accgDocSeq
	 * @param accgInvoiceStatementAmountDto
	 * @param accgDocType
	 * @return
	 * @throws AppException
	 */
	private TSalesDetailEntity registSalesDetail(Long salesSeq, Long accgDocSeq,
			AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto, AccgDocType accgDocType) throws AppException {
		TSalesDetailEntity tSalesDetailEntity = new TSalesDetailEntity();
		// 報酬が無い場合は登録しない
		BigDecimal totalFeeAmount = accgInvoiceStatementAmountDto.getTotalFeeAmount();
		if (totalFeeAmount == null || LoiozNumberUtils.equalsZero(totalFeeAmount) || salesSeq == null) {
			return tSalesDetailEntity;
		}

		// 売上日、売上計上先SEQを取得
		LocalDate salesDate = null;
		Long salesAccountSeq = null;
		if (AccgDocType.INVOICE.equals(accgDocType)) {
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			if (tAccgInvoiceEntity == null) {
				throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
			}
			salesDate = tAccgInvoiceEntity.getSalesDate();
			salesAccountSeq = tAccgInvoiceEntity.getSalesAccountSeq();
		} else if (AccgDocType.STATEMENT.equals(accgDocType)) {
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			if (tAccgStatementEntity == null) {
				throw new DataNotFoundException("精算書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
			}
			salesDate = tAccgStatementEntity.getSalesDate();
			salesAccountSeq = tAccgStatementEntity.getSalesAccountSeq();
		}

		// 登録データをentityにセット
		tSalesDetailEntity.setSalesSeq(salesSeq);
		tSalesDetailEntity.setSalesAmount(accgInvoiceStatementAmountDto.getTotalFeeAmountWithoutTaxNonDiscount());
		tSalesDetailEntity.setSalesTaxAmount(accgInvoiceStatementAmountDto.getTotalTaxAmountNonDiscount());
		// 値引き_売上金額（税抜）が0円の場合はnullで登録する
		tSalesDetailEntity.setSalesDiscountAmount(
				LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalDiscountAmountWithoutTax())
						? null
						: accgInvoiceStatementAmountDto.getTotalDiscountAmountWithoutTax());
		// 値引き_消費税額が0円の場合はnullで登録する
		tSalesDetailEntity.setSalesDiscountTaxAmount(
				LoiozNumberUtils.equalsZero(accgInvoiceStatementAmountDto.getTotalTaxAmountDiscount())
						? null
						: accgInvoiceStatementAmountDto.getTotalTaxAmountDiscount());
		tSalesDetailEntity.setSalesWithholdingAmount(accgInvoiceStatementAmountDto.getTotalWithholdingAmount());
		tSalesDetailEntity.setSalesDate(salesDate);
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
	 * 売上明細-消費税データを作成します<br>
	 * 対象の請求書、精算書の請求消費税テーブルの値を反映します<br>
	 * 
	 * @param salesDetailSeq
	 * @param accgInvoiceStatementAmountDto
	 * @return
	 * @throws AppException
	 */
	private List<TSalesDetailTaxEntity> registerSalesDetailTax(Long salesDetailSeq,
			AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto) throws AppException {
		List<TSalesDetailTaxEntity> tSalesDetailTaxEntityList = new ArrayList<>();
		// 報酬が無い場合は登録しない
		BigDecimal totalFeeAmount = accgInvoiceStatementAmountDto.getTotalFeeAmount();
		if (totalFeeAmount == null || LoiozNumberUtils.equalsZero(totalFeeAmount) || salesDetailSeq == null) {
			return tSalesDetailTaxEntityList;
		}

		// 8%消費税データをentityにセット
		// 対象額
		BigDecimal totalTaxAmount8TargetFee = LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetFeeNonDiscount());
		// 税額
		BigDecimal totalTaxAmount8 = LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount8NonDiscount());
		// 値引き_対象額
		BigDecimal totalDiscountTaxAmount8TagetFee = accgInvoiceStatementAmountDto.getTotalTaxAmount8TargetDiscount();
		// 値引き_税額
		BigDecimal totalDiscountTaxAmount8 = accgInvoiceStatementAmountDto.getTotalTaxAmount8Discount();
		if (LoiozNumberUtils.isGreaterThan(totalTaxAmount8TargetFee, BigDecimal.ZERO)) {
			TSalesDetailTaxEntity tSalesDetailTaxEntity = new TSalesDetailTaxEntity();
			tSalesDetailTaxEntity.setSalesDetailSeq(salesDetailSeq);
			tSalesDetailTaxEntity.setTaxRateType(TaxRate.EIGHT_PERCENT.getCd());
			tSalesDetailTaxEntity.setTaxableAmount(totalTaxAmount8TargetFee);
			tSalesDetailTaxEntity.setTaxAmount(totalTaxAmount8);
			tSalesDetailTaxEntity.setDiscountTaxableAmount(totalDiscountTaxAmount8TagetFee);
			tSalesDetailTaxEntity.setDiscountTaxAmount(totalDiscountTaxAmount8);
			tSalesDetailTaxEntityList.add(tSalesDetailTaxEntity);
		}

		// 10%消費税データをentityにセット
		// 対象額
		BigDecimal totalTaxAmount10TargetFee = LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetFeeNonDiscount());
		// 税額
		BigDecimal totalTaxAmount10 = LoiozNumberUtils.nullToZero(accgInvoiceStatementAmountDto.getTotalTaxAmount10NonDiscount());
		// 値引き_対象額
		BigDecimal totalDiscountTaxAmount10TargetFee = accgInvoiceStatementAmountDto.getTotalTaxAmount10TargetDiscount();
		// 値引き_税額
		BigDecimal totalDiscountTaxAmount10 = accgInvoiceStatementAmountDto.getTotalTaxAmount10Discount();
		if (LoiozNumberUtils.isGreaterThan(totalTaxAmount10TargetFee, BigDecimal.ZERO)) {
			TSalesDetailTaxEntity tSalesDetailTaxEntity = new TSalesDetailTaxEntity();
			tSalesDetailTaxEntity.setSalesDetailSeq(salesDetailSeq);
			tSalesDetailTaxEntity.setTaxRateType(TaxRate.TEN_PERCENT.getCd());
			tSalesDetailTaxEntity.setTaxableAmount(totalTaxAmount10TargetFee);
			tSalesDetailTaxEntity.setTaxAmount(totalTaxAmount10);
			tSalesDetailTaxEntity.setDiscountTaxableAmount(totalDiscountTaxAmount10TargetFee);
			tSalesDetailTaxEntity.setDiscountTaxAmount(totalDiscountTaxAmount10);
			tSalesDetailTaxEntityList.add(tSalesDetailTaxEntity);
		}

		// 登録
		this.batchInsertSalesDetailTax(tSalesDetailTaxEntityList);
		return tSalesDetailTaxEntityList;
	}

	/**
	 * 取引実績テーブルにデータを登録します。
	 * 
	 * @param accgDocSeq
	 * @param feeAmountExpect 報酬入金額【見込】
	 * @param depositRecvAmountExpect 預り金入金額【見込】
	 * @param depositPaymentAmountExpect 預り金出金額【見込】
	 * @return
	 * @throws AppException
	 */
	private TAccgRecordEntity registAccgRecord(Long accgDocSeq, BigDecimal feeAmountExpect, BigDecimal depositRecvAmountExpect, BigDecimal depositPaymentAmountExpect) throws AppException {

		// 登録データをentityにセット
		TAccgRecordEntity entity = new TAccgRecordEntity();
		entity.setAccgDocSeq(accgDocSeq);
		entity.setFeeAmountExpect(feeAmountExpect == null ? BigDecimal.ZERO : feeAmountExpect);
		entity.setDepositRecvAmountExpect(depositRecvAmountExpect == null ? BigDecimal.ZERO : depositRecvAmountExpect);
		entity.setDepositPaymentAmountExpect(depositPaymentAmountExpect == null ? BigDecimal.ZERO : depositPaymentAmountExpect);

		int insertCount = tAccgRecordDao.insert(entity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return entity;
	}

	/**
	 * 振替額（報酬へ振り返る既入金）があれば、報酬への入金として取引実績明細テーブルに登録します。
	 * 
	 * @param accgRecordSeq
	 * @param refundAmount 振替額（既入金の報酬への振替額）
	 * @return
	 * @throws AppException
	 */
	private TAccgRecordDetailEntity registRepayAmountAsFee(Long accgRecordSeq, BigDecimal refundAmount) throws AppException {

		TAccgRecordDetailEntity entity = new TAccgRecordDetailEntity();

		// 振替額が0なら登録しない
		if (refundAmount == null || LoiozNumberUtils.equalsZero(refundAmount)) {
			return entity;
		}
		// 登録データをentityにセット
		entity.setAccgRecordSeq(accgRecordSeq);
		entity.setRecordDate(LocalDate.now());
		entity.setRecordType(RecordType.TRANSFER_DEPOSIT_INTO.getCd());
		entity.setRecordAmount(refundAmount);
		entity.setRecordFeeAmount(refundAmount);
		entity.setRecordSeparateInputFlg(SystemFlg.FLG_OFF.getCd());

		int insertCount = tAccgRecordDetailDao.insert(entity);
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return entity;
	}

	/**
	 * 報酬への振替がある場合は、振替金額分を出金として登録する。<br>
	 * (入出金完了フラグは「1」、預り金項目名は「預り金」、摘要は「報酬への振替」にする）
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param refundAmount 振替額（既入金の報酬への振替額） ※NULLは許可しない
	 * @return
	 * @throws AppException
	 */
	private TDepositRecvEntity registDepositIfThereRepayAmount(Long accgDocSeq, Long ankenId, Long personId, BigDecimal refundAmount) throws AppException {

		TDepositRecvEntity entity = new TDepositRecvEntity();

		// 報酬への振替があれば預り金テーブルに報酬への振替（出金）として登録する
		if (LoiozNumberUtils.isLessThan(refundAmount, BigDecimal.ONE)) {
			return entity;
		}

		// 登録データをentityへセット
		entity.setPersonId(personId);
		entity.setAnkenId(ankenId);
		entity.setCreatedAccgDocSeq(accgDocSeq);
		entity.setCreatedType(DepositRecvCreatedType.TRANFER_TO_FEE_UPON_ISSUANCE.getCd());
		entity.setExpenseInvoiceFlg(SystemFlg.FLG_OFF.getCd());
		entity.setDepositDate(LocalDate.now());
		entity.setDepositItemName(AccgConstant.ACCG_ITEM_NAME_OF_TRANSFER_TO_FEE);
		entity.setWithdrawalAmount(refundAmount);
		entity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());
		entity.setDepositType(DepositType.SHUKKIN.getCd());
		entity.setSumText(AccgConstant.ACCG_SUM_TEXT_OF_TRANSFER_TO_FEE);

		// 預り金登録
		int insertCount = tDepositRecvDao.insert(entity);
		if (insertCount != 1) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		
		return entity;
	}

	/**
	 * 預り金データを作成します<br>
	 * 会計書類タイプが「請求書」の場合は、入金データ（入出金完了フラグ=0）を作成します<br>
	 * 会計書類タイプが「精算書」の場合は、出金データ（入出金完了フラグ=0）を作成します<br>
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @param depositRecvAmountExpect 預り金入金額【見込】
	 * @param depositPaymentAmountExpect 預り金出金額【見込】
	 * @param accgDocType 会計書類タイプ
	 * @param totalDepositAmount 【請求項目】預り金合計
	 * @return
	 * @throws AppException
	 */
	private List<TDepositRecvEntity> registDepositOrWithdrawalSchedule(Long accgDocSeq, Long ankenId, Long personId,
			BigDecimal depositRecvAmountExpect, BigDecimal depositPaymentAmountExpect, AccgDocType accgDocType,
			BigDecimal totalDepositAmount) throws AppException {

		List<TDepositRecvEntity> tDepositRecvInsertEntities = new ArrayList<>();
		TDepositRecvEntity entity = new TDepositRecvEntity();

		if (accgDocType == null) {
			return tDepositRecvInsertEntities;
		}

		// 登録データをentityへセット
		if (AccgDocType.INVOICE.equals(accgDocType)) {
			// 請求書の場合

			if (LoiozNumberUtils.isLessThan(depositRecvAmountExpect, BigDecimal.ONE)) {
				return tDepositRecvInsertEntities;
			}
			setDepositRecvScheduleEntityForInvoices(tDepositRecvInsertEntities, depositRecvAmountExpect,
					totalDepositAmount, personId, ankenId, accgDocSeq);
		} else if (AccgDocType.STATEMENT.equals(accgDocType)) {
			// 精算書の場合

			if (LoiozNumberUtils.isLessThan(depositPaymentAmountExpect, BigDecimal.ONE)) {
				return tDepositRecvInsertEntities;
			}
			entity.setPersonId(personId);
			entity.setAnkenId(ankenId);
			entity.setCreatedType(DepositRecvCreatedType.CREATED_BY_ISSUANCE.getCd());
			entity.setExpenseInvoiceFlg(SystemFlg.FLG_OFF.getCd());
			entity.setWithdrawalAmount(depositPaymentAmountExpect);
			entity.setDepositType(DepositType.SHUKKIN.getCd());
			entity.setDepositItemName(AccgConstant.ACCG_ITEM_NAME_OF_WITHDRAWAL_SCHEDULE);
			entity.setCreatedAccgDocSeq(accgDocSeq);
			entity.setDepositCompleteFlg(SystemFlg.FLG_OFF.getCd());
			entity.setSumText(AccgConstant.ACCG_SUM_TEXT_OF_WITHDRAWAL_SCHEDULE);
			tDepositRecvInsertEntities.add(entity);
		}

		// 預り金登録
		int[] insertCount = null;
		insertCount = tDepositRecvDao.batchInsert(tDepositRecvInsertEntities);
		if (tDepositRecvInsertEntities.size() != insertCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return tDepositRecvInsertEntities;
	}

	/**
	 * 請求書の取引日-印字フラグ（既入金）と既入金項目合算フラグ（既入金）を変更します
	 * 
	 * @param isRepayTransactionDatePrintFlg
	 * @param isRepaySumFlg
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateTAccgInvoiceRepayDatePringFlgAndRepaySumFlg(boolean isRepayTransactionDatePrintFlg, boolean isRepaySumFlg, Long accgDocSeq) throws AppException {
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		tAccgInvoiceEntity.setRepayTransactionDatePrintFlg(isRepayTransactionDatePrintFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
		tAccgInvoiceEntity.setRepaySumFlg(isRepaySumFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}
	
	/**
	 * 精算書の取引日-印字フラグ（既入金）と既入金項目合算フラグ（既入金）を変更します
	 * 
	 * @param isRepayTransactionDatePrintFlg
	 * @param isRepaySumFlg
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateTAccgStatementRepayDatePringFlgAndRepaySumFlg(boolean isRepayTransactionDatePrintFlg, boolean isRepaySumFlg, Long accgDocSeq) throws AppException {
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity == null) {
			throw new DataNotFoundException("精算書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		tAccgStatementEntity.setRepayTransactionDatePrintFlg(isRepayTransactionDatePrintFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
		tAccgStatementEntity.setRepaySumFlg(isRepaySumFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());

		// 更新
		this.updateAccgStatement(tAccgStatementEntity);
	}
	
	/**
	 * 請求書の取引日-印字フラグ（請求）と実費項目合算フラグ（請求）を変更します
	 * 
	 * @param isInvoiceTransactionDatePrintFlg
	 * @param isExpenseSumFlg
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateTAccgInvoiceInvoiceDatePringFlgAndExpenseSumFlg(boolean isInvoiceTransactionDatePrintFlg, boolean isExpenseSumFlg, Long accgDocSeq) throws AppException {
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceEntity == null) {
			throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		tAccgInvoiceEntity.setInvoiceTransactionDatePrintFlg(isInvoiceTransactionDatePrintFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
		tAccgInvoiceEntity.setExpenseSumFlg(isExpenseSumFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}
	
	/**
	 * 精算書の取引日-印字フラグ（請求）と実費項目合算フラグ（請求）を変更します
	 * 
	 * @param isInvoiceTransactionDatePrintFlg
	 * @param isExpenseSumFlg
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateTAccgStatementInvoiceDatePringFlgAndExpenseSumFlg(boolean isInvoiceTransactionDatePrintFlg, boolean isExpenseSumFlg, Long accgDocSeq) throws AppException {
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity == null) {
			throw new DataNotFoundException("精算書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
		}
		tAccgStatementEntity.setInvoiceTransactionDatePrintFlg(isInvoiceTransactionDatePrintFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
		tAccgStatementEntity.setExpenseSumFlg(isExpenseSumFlg ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());

		// 更新
		this.updateAccgStatement(tAccgStatementEntity);
	}
	
	/**
	 * まとめるチェックが（保存前：ON、保存後：ON）のケースでの既入金項目、マッピングデータの保存処理
	 * 
	 * @param sumRepayRow 【まとめ行】既入金項目データ
	 * @param deleteRepayRowList 【個別行】削除対象データ
	 * @param registRepayRowList 【個別行】登録対象データ
	 * @param updateRepayRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateRepayCaseSumCheckONtoON(RepayRowInputForm sumRepayRow,
			List<RepayRowInputForm> deleteRepayRowList, List<RepayRowInputForm> registRepayRowList, List<RepayRowInputForm> updateRepayRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {
		
		if (sumRepayRow != null) {
			// リクエストにまとめられている既入金項目が存在する場合
			
			// 【まとめ行】既入金項目SEQ取得
			Long sumDocRepaySeq = sumRepayRow.getDocRepaySeq();
			
			// 【削除対象行】既入金項目_預り金マッピングデータを削除（削除対象行のdepositRecvSeqListには、必ず1つのdepositRecvSeqが存在している）
			List<Long> deleteDocRepayDepositRecvSeqList = deleteRepayRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
			this.deleteRepayDepositMappingByDepositSeq(deleteDocRepayDepositRecvSeqList);
			
			if (!LoiozCollectionUtils.isEmpty(registRepayRowList) || !LoiozCollectionUtils.isEmpty(updateRepayRowList)) {
				// マッピングへの登録、預り金への更新（同期）を行うものがある場合
				
				// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、発生日、摘要のみ反映
				List<RepayRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registRepayRowList.stream(), updateRepayRowList.stream()).collect(Collectors.toList());
				this.applyUpdateRepayToDepositRecv(joinedRegistAndUpdateRowList);
				
				if (sumDocRepaySeq == null) {
					// まとめ行が新規登録の場合
					
					// 【まとめ行】まとめ状態の既入金項目の登録と、登録した既入金項目に紐づくマッピングデータの登録
					this.insertRepayAndRepayDepositMappingForSumRow(sumRepayRow, joinedRegistAndUpdateRowList, accgDocSeq);
					
				} else {
					// まとめ行が更新の場合
					
					// 【登録対象行】マッピングデータ登録 (registRepayRowListの預り金SEQ)（登録対象行のdepositRecvSeqListには、必ず1つのdepositRecvSeqが存在している）
					List<Long> registDocRepayDepositRecvSeqList = registRepayRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
					this.insertMappingOfRepayAndDeposit(sumDocRepaySeq, registDocRepayDepositRecvSeqList, accgDocSeq);
					
					// 【まとめ行】まとめ状態の既入金項目を更新（※金額は現在のマッピングテーブルの状態から算出する）
					this.updateSumRepayRow(sumDocRepaySeq, sumRepayRow);
				}
				
				// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
				commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);
				
			} else {
				// ない場合（全て削除された場合）
				
				// sumDocRepaySeqのマッピングデータが存在しない（全て消えたことを確認する） データが残っている場合は楽観ロックエラー
				List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(sumDocRepaySeq);
				if (!LoiozCollectionUtils.isEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
					// 楽観ロックエラー
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
				
				if (sumDocRepaySeq != null) {
					// まとめ行の既入金項目が新規登録ではない -> すでに登録されている場合
					
					// 【まとめ行】まとめられている既入金項目を削除
					this.deleteTAccgDocRepay(List.of(sumDocRepaySeq));
				}
			}

		} else {
			// リクエストにまとめられている既入金項目が存在しない場合
			// -> 更新前もまとめるチェックONだったが、まとめデータがなく、今回登録するものもない場合
			// （※このケースになる場合は、登録、更新、削除のデータがない場合になるので、前段の処理終了の分岐側で処理されるので、おそらくここには来ない。）
			// -> なにもしない
		}
	}
	
	/**
	 * まとめるチェックが（保存前：OFF、保存後：ON）のケースでの既入金項目、マッピングデータの保存処理
	 * 
	 * @param sumRepayRow 【まとめ行】既入金項目データ
	 * @param deleteRepayRowList 【個別行】削除対象データ
	 * @param registRepayRowList 【個別行】登録対象データ
	 * @param updateRepayRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateRepayCaseSumCheckOFFtoON(RepayRowInputForm sumRepayRow,
			List<RepayRowInputForm> deleteRepayRowList, List<RepayRowInputForm> registRepayRowList, List<RepayRowInputForm> updateRepayRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {
		
		// 【削除対象行】既入金項目とマッピングを削除
		this.deleteRepayAndRepayDepositMappingForNotSumRow(deleteRepayRowList);
		
		if (!LoiozCollectionUtils.isEmpty(registRepayRowList) || !LoiozCollectionUtils.isEmpty(updateRepayRowList)) {
			// マッピングへの登録、預り金への更新（同期）を行うものがある場合
			
			// 【更新対象行】既入金項目とマッピングを削除
			this.deleteRepayAndRepayDepositMappingForNotSumRow(updateRepayRowList);
			
			// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、発生日、摘要のみ反映
			List<RepayRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registRepayRowList.stream(), updateRepayRowList.stream()).collect(Collectors.toList());
			this.applyUpdateRepayToDepositRecv(joinedRegistAndUpdateRowList);
			
			// 【まとめ行】まとめ状態の既入金項目の登録と、登録した既入金項目に紐づくマッピングデータの登録
			this.insertRepayAndRepayDepositMappingForSumRow(sumRepayRow, joinedRegistAndUpdateRowList, accgDocSeq);

			// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
			commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		} else {
			// ない場合（全て削除された場合）
			// なにもしない
		}
	}
	
	/**
	 * まとめるチェックが（保存前：ON、保存後：OFF）のケースでの既入金項目、マッピングデータの保存処理
	 * 
	 * @param sumRepayRow 【まとめ行】既入金項目データ
	 * @param registRepayRowList 【個別行】登録対象データ
	 * @param updateRepayRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateRepayCaseSumCheckONtoOFF(RepayRowInputForm sumRepayRow,
			List<RepayRowInputForm> registRepayRowList, List<RepayRowInputForm> updateRepayRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {
		
		if (sumRepayRow != null) {
			// リクエストにまとめられている既入金項目が存在する場合
			// -> まとめ行のデータは削除する
			
			Long docRepaySeq = sumRepayRow.getDocRepaySeq();
			
			// 【まとめ行】マッピングを削除
			this.deleteRepayDepositMappingByRepaySeq(docRepaySeq);
			// 【まとめ行】既入金項目削除
			this.deleteTAccgDocRepay(Arrays.asList(docRepaySeq));
		} else {
			// まとめ行がない場合
			// -> まとめチェックがONだったが、既入金項目のデータの登録がなかった場合
			// -> 削除処理は不要
		}
		
		if (!LoiozCollectionUtils.isEmpty(registRepayRowList) || !LoiozCollectionUtils.isEmpty(updateRepayRowList)) {
			// マッピングへの登録、預り金への更新（同期）を行うものがある場合

			// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、発生日、摘要のみ反映
			List<RepayRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registRepayRowList.stream(), updateRepayRowList.stream()).collect(Collectors.toList());
			this.applyUpdateRepayToDepositRecv(joinedRegistAndUpdateRowList);
			
			// 【登録対象行 + 更新対象行】既入金項目、マッピングデータの登録 (registRepayRowList + updateRepayRowList)
			this.registRepay(joinedRegistAndUpdateRowList, accgDocSeq);
			
			// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
			commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		} else {
			// ない場合（全て削除された場合）
			// なにもしない
		}
	}
	
	/**
	 * まとめるチェックが（保存前：OFF、保存後：OFF）のケースでの既入金項目、マッピングデータの保存処理
	 * 
	 * @param deleteRepayRowList 【個別行】削除対象データ
	 * @param registRepayRowList 【個別行】登録対象データ
	 * @param updateRepayRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateRepayCaseSumCheckOFFtoOFF(List<RepayRowInputForm> deleteRepayRowList, List<RepayRowInputForm> registRepayRowList, List<RepayRowInputForm> updateRepayRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {
		
		// 【削除対象行】既入金項目とマッピングを削除
		this.deleteRepayAndRepayDepositMappingForNotSumRow(deleteRepayRowList);
		
		if (!LoiozCollectionUtils.isEmpty(registRepayRowList) || !LoiozCollectionUtils.isEmpty(updateRepayRowList)) {
			// マッピングへの登録、預り金への更新（同期）を行うものがある場合
			
			// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、発生日、摘要のみ反映
			List<RepayRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registRepayRowList.stream(), updateRepayRowList.stream()).collect(Collectors.toList());
			this.applyUpdateRepayToDepositRecv(joinedRegistAndUpdateRowList);
			
			// 【登録対象行】既入金項目、マッピングデータの登録 (registRepayRowList )
			this.registRepay(registRepayRowList , accgDocSeq);
			
			// 【更新対象行】既入金項目を更新 (updateRepayRowList)
			this.updateRepay(updateRepayRowList);

			// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
			commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		} else {
			// ない場合（全て削除された場合）
			// なにもしない
		}
	}
	
	/**
	 * まとめるチェックが（保存前：ON、保存後：ON）のケースでの請求項目（実費）、マッピングデータの保存処理
	 * 
	 * @param sumInvoiceExpenseRow 【まとめ行】請求項目データ
	 * @param deleteInvoiceExpenseRowList 【個別行】削除対象データ
	 * @param registInvoiceExpenseRowList 【個別行】登録対象データ
	 * @param updateInvoiceExpenseRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateInvoiceExpenseCaseSumCheckONtoON(InvoiceRowInputForm sumInvoiceExpenseRow,
			List<InvoiceRowInputForm> deleteInvoiceExpenseRowList, List<InvoiceRowInputForm> registInvoiceExpenseRowList, List<InvoiceRowInputForm> updateInvoiceExpenseRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {

		if (sumInvoiceExpenseRow != null) {
			// リクエストにまとめられている請求項目が存在する場合

			// 【まとめ行】請求項目-預り金SEQ取得
			Long sumDocInvoiceDepositSeq = sumInvoiceExpenseRow.getDocInvoiceDepositSeq();
			
			// 【削除対象行】請求項目-預り金マッピングデータを削除（削除対象行のdepositRecvSeqListには、必ず1つのdepositRecvSeqが存在している）
			List<Long> deleteDocInvoiceDepositRecvSeqList = deleteInvoiceExpenseRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
			this.deleteInvoiceDepositMappingByDepositSeq(deleteDocInvoiceDepositRecvSeqList);

			if (!LoiozCollectionUtils.isEmpty(registInvoiceExpenseRowList) || !LoiozCollectionUtils.isEmpty(updateInvoiceExpenseRowList)) {
				// マッピングへの登録、預り金への更新（同期）を行うものがある場合

				// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、取引日、摘要
				List<InvoiceRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registInvoiceExpenseRowList.stream(), updateInvoiceExpenseRowList.stream()).collect(Collectors.toList());
				this.applyUpdateInvoiceToDepositRecv(joinedRegistAndUpdateRowList);
				
				if (sumDocInvoiceDepositSeq == null) {
					// まとめ行が新規登録の場合
					
					// 【まとめ行】まとめ状態の請求項目-預り金の登録と、登録した請求項目-預り金に紐づくマッピングデータの登録
					this.insertInvoiceAndInvoiceDepositMappingForSumRow(sumInvoiceExpenseRow, joinedRegistAndUpdateRowList, accgDocSeq);
					
				} else {
					// まとめ行が更新の場合
					
					// 【登録対象行】マッピングデータ登録 (registInvoiceExpenseRowListの預り金SEQ)（登録対象行のdepositRecvSeqListには、必ず1つのdepositRecvSeqが存在している）
					List<Long> registDocInvoiceDepositRecvSeqList = registInvoiceExpenseRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
					this.insertMappingOfInvoiceAndDeposit(sumDocInvoiceDepositSeq, registDocInvoiceDepositRecvSeqList, accgDocSeq);
					
					// 【まとめ行】まとめ状態の請求項目-預り金を更新（※金額は現在のマッピングテーブルの状態から算出する）
					this.updateSumInvoiceRow(sumDocInvoiceDepositSeq, sumInvoiceExpenseRow);
				}

				// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
				commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

			} else {
				// ない場合（全て削除された場合）

				// sumDocInvoiceDepositSeqのマッピングデータが存在しない（全て消えたことを確認する） データが残っている場合は楽観ロックエラー
				List<TAccgDocInvoiceDepositTDepositRecvMappingEntity>tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
						.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(sumDocInvoiceDepositSeq);
				if (!LoiozCollectionUtils.isEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
					// 楽観ロックエラー
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
				
				if (sumDocInvoiceDepositSeq != null) {
					// まとめ行の請求項目-預り金が新規登録ではない -> すでに登録されている場合
					
					// 【まとめ行】まとめられている請求項目-預り金を削除
					commonAccgService.deleteTAccgDocInvoiceDepositAndDocInvoice(List.of(sumDocInvoiceDepositSeq));
				}
			}

		} else {
			// リクエストにまとめられている請求項目-預り金が存在しない場合
			// -> 更新前もまとめるチェックONだったが、まとめデータがなく、今回登録するものもない場合
			// （※このケースになる場合は、登録、更新、削除のデータがない場合になるので、前段の処理終了の分岐側で処理されるので、おそらくここには来ない。）
			// -> なにもしない
		}
	}
	
	/**
	 * まとめるチェックが（保存前：OFF、保存後：ON）のケースでの請求項目（実費）、マッピングデータの保存処理
	 * 
	 * @param sumInvoiceExpenseRow 【まとめ行】請求項目データ
	 * @param deleteInvoiceExpenseRowList 【個別行】削除対象データ
	 * @param registInvoiceExpenseRowList 【個別行】登録対象データ
	 * @param updateInvoiceExpenseRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateInvoiceExpenseCaseSumCheckOFFtoON(InvoiceRowInputForm sumInvoiceExpenseRow,
			List<InvoiceRowInputForm> deleteInvoiceExpenseRowList, List<InvoiceRowInputForm> registInvoiceExpenseRowList, List<InvoiceRowInputForm> updateInvoiceExpenseRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {

		// 【削除対象行】請求項目-預り金とマッピングを削除
		this.deleteInvoiceAndInvoiceDepositMappingForNotSumRow(deleteInvoiceExpenseRowList);

		if (!LoiozCollectionUtils.isEmpty(registInvoiceExpenseRowList) || !LoiozCollectionUtils.isEmpty(updateInvoiceExpenseRowList)) {
			// マッピングへの登録、預り金への更新（同期）を行うものがある場合

			// 【更新対象行】請求項目-預り金とマッピングを削除
			this.deleteInvoiceAndInvoiceDepositMappingForNotSumRow(updateInvoiceExpenseRowList);
			
			// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、取引日、摘要
			List<InvoiceRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registInvoiceExpenseRowList.stream(), updateInvoiceExpenseRowList.stream()).collect(Collectors.toList());
			this.applyUpdateInvoiceToDepositRecv(joinedRegistAndUpdateRowList);
			
			// 【まとめ行】まとめ状態の請求項目-預り金の登録と、登録した請求項目_預り金に紐づくマッピングデータの登録
			this.insertInvoiceAndInvoiceDepositMappingForSumRow(sumInvoiceExpenseRow, joinedRegistAndUpdateRowList, accgDocSeq);
			
			// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
			commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		} else {
			// ない場合（全て削除された場合）
			// なにもしない
		}
	}
	
	/**
	 * まとめるチェックが（保存前：ON、保存後：OFF）のケースでの請求項目（実費）、マッピングデータの保存処理
	 * 
	 * @param sumInvoiceExpenseRow 【まとめ行】請求項目データ
	 * @param registInvoiceExpenseRowList 【個別行】登録対象データ
	 * @param updateInvoiceExpenseRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateInvoiceExpenseCaseSumCheckONtoOFF(InvoiceRowInputForm sumInvoiceExpenseRow,
			List<InvoiceRowInputForm> registInvoiceExpenseRowList, List<InvoiceRowInputForm> updateInvoiceExpenseRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {

		if (sumInvoiceExpenseRow != null) {
			// リクエストにまとめられている行が存在する場合
			// -> まとめ行のデータは削除する
			
			Long docInvoiceDepositSeq = sumInvoiceExpenseRow.getDocInvoiceDepositSeq();
			
			// 【まとめ行】マッピングを削除
			this.deleteInvoiceDepositMappingByInvoiceDepositSeq(docInvoiceDepositSeq);
			// 【まとめ行】請求項目-預り金削除
			commonAccgService.deleteTAccgDocInvoiceDepositAndDocInvoice(Arrays.asList(docInvoiceDepositSeq));
		} else {
			// まとめ行がない場合
			// -> まとめチェックがONだったが、請求項目-預り金（実費）のデータの登録がなかった場合
			// -> 削除処理は不要
		}
		
		if (!LoiozCollectionUtils.isEmpty(registInvoiceExpenseRowList) || !LoiozCollectionUtils.isEmpty(updateInvoiceExpenseRowList)) {
			// マッピングへの登録、預り金への更新（同期）を行うものがある場合

			// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、取引日、摘要
			List<InvoiceRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registInvoiceExpenseRowList.stream(), updateInvoiceExpenseRowList.stream()).collect(Collectors.toList());
			this.applyUpdateInvoiceToDepositRecv(joinedRegistAndUpdateRowList);
			
			// 【登録対象行 + 更新対象行】請求項目-預り金、マッピングデータの登録 (registRepayRowList + updateRepayRowList)
			this.registInvoiceDeposit(joinedRegistAndUpdateRowList, accgDocSeq);
			
			// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
			commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		} else {
			// ない場合（全て削除された場合）
			// なにもしない
		}
	}
	
	/**
	 * まとめるチェックが（保存前：OFF、保存後：OFF）のケースでの請求項目（実費）、マッピングデータの保存処理
	 * 
	 * @param deleteInvoiceExpenseRowList 【個別行】削除対象データ
	 * @param registInvoiceExpenseRowList 【個別行】登録対象データ
	 * @param updateInvoiceExpenseRowList 【個別行】更新対象データ
	 * @param personId
	 * @param ankenId
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateInvoiceExpenseCaseSumCheckOFFtoOFF(List<InvoiceRowInputForm> deleteInvoiceExpenseRowList, List<InvoiceRowInputForm> registInvoiceExpenseRowList, List<InvoiceRowInputForm> updateInvoiceExpenseRowList,
			Long personId, Long ankenId, Long accgDocSeq) throws AppException {

		// 【削除対象行】請求項目-預り金とマッピングを削除
		this.deleteInvoiceAndInvoiceDepositMappingForNotSumRow(deleteInvoiceExpenseRowList);

		if (!LoiozCollectionUtils.isEmpty(registInvoiceExpenseRowList) || !LoiozCollectionUtils.isEmpty(updateInvoiceExpenseRowList)) {
			// マッピングへの登録、預り金への更新（同期）を行うものがある場合

			// 【登録対象行 + 更新対象行】預り金にデータを反映（更新） ※金額、項目名、取引日、摘要
			List<InvoiceRowInputForm> joinedRegistAndUpdateRowList = Stream.concat(registInvoiceExpenseRowList.stream(), updateInvoiceExpenseRowList.stream()).collect(Collectors.toList());
			this.applyUpdateInvoiceToDepositRecv(joinedRegistAndUpdateRowList);
			
			// 【登録対象行】請求項目-預り金、マッピングデータの登録 (registRepayRowList)
			this.registInvoiceDeposit(registInvoiceExpenseRowList, accgDocSeq);
			
			// 【更新対象行】請求項目-預り金を更新 (updateExpenseRowList)
			this.updateInvoiceDeposit(updateInvoiceExpenseRowList);
			
			// PDFの再作成フラグを更新（実費明細） ※預り金データを変更しているため
			commonAccgService.updateDepositDetailRebuildFlg(ankenId, personId);

		} else {
			// ない場合（全て削除された場合）
			// なにもしない
		}
	}
	
	/**
	 * 既入金項目と、既入金項目-預り金のマッピングデータを削除する<br>
	 * ※注意：まとめられていない既入金項目の行（RepayRowInputForm）専用のメソッド
	 * 
	 * @param deleteRepayRowList
	 * @throws AppException 
	 */
	private void deleteRepayAndRepayDepositMappingForNotSumRow(List<RepayRowInputForm> deleteRepayRowList) throws AppException {
		
		// マッピングを削除（depositRecvSeqListには、必ず1つのdepositRecvSeqが存在している）
		List<Long> deleteDocRepayDepositRecvSeqList = deleteRepayRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
		this.deleteRepayDepositMappingByDepositSeq(deleteDocRepayDepositRecvSeqList);
		// 既入金項目削除
		Set<Long> deleteDocRepaySeqSet = deleteRepayRowList.stream().map(row -> row.getDocRepaySeq()).collect(Collectors.toSet());
		this.deleteTAccgDocRepay(new ArrayList<Long>(deleteDocRepaySeqSet));
	}
	
	/**
	 * 請求項目-預り金と、請求項目-預り金のマッピングデータを削除する<br>
	 * ※注意：まとめられていない請求項目-預り金の行（InvoiceRowInputForm）専用のメソッド
	 * 
	 * @param deleteInvoiceRowList
	 * @throws AppException 
	 */
	private void deleteInvoiceAndInvoiceDepositMappingForNotSumRow(List<InvoiceRowInputForm> deleteInvoiceRowList) throws AppException {
		
		// マッピングを削除（depositRecvSeqListには、必ず1つのdepositRecvSeqが存在している）
		List<Long> deleteDocInvoiceDepositRecvSeqList = deleteInvoiceRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
		this.deleteInvoiceDepositMappingByDepositSeq(deleteDocInvoiceDepositRecvSeqList);
		// 請求項目-預り金削除
		Set<Long> deleteInvoiceDepositSeqSet = deleteInvoiceRowList.stream().map(row -> row.getDocInvoiceDepositSeq()).collect(Collectors.toSet());
		commonAccgService.deleteTAccgDocInvoiceDepositAndDocInvoice(new ArrayList<Long>(deleteInvoiceDepositSeqSet));
	}
	
	/**
	 * 既入金項目と、既入金項目-預り金のマッピングデータを登録する<br>
	 * ※注意：まとめられている既入金項目の行（RepayRowInputForm）専用のメソッド
	 * 
	 * @param sumRepayRow まとめられている行データ
	 * @param joinedRegistAndUpdateRowList まとめられている行を構成する（登録or更新）既入金項目行データ
	 * @param accgDocSeq 会計書類SEQ
	 * @throws AppException
	 */
	private void insertRepayAndRepayDepositMappingForSumRow(RepayRowInputForm sumRepayRow, List<RepayRowInputForm> joinedRegistAndUpdateRowList, Long accgDocSeq) throws AppException {
		
		// 【まとめ行】まとめ状態の既入金項目を登録 （※金額は引数のRowListから算出して登録する）
		TAccgDocRepayEntity sumDocRepayEntity = this.insertSumRepayRow(sumRepayRow, joinedRegistAndUpdateRowList);
		Long docRepaySeq = sumDocRepayEntity.getDocRepaySeq();
		
		// 【登録対象行 + 更新対象行】まとめ行とのマッピングデータ登録 (registRepayRowList + updateRepayRowListの預り金SEQ)（対象行のjoinedRegistAndUpdateRowListには、必ず1つのdepositRecvSeqが存在している）
		List<Long> joinedRegistAndUpdateRowDepositRecvSeqList = joinedRegistAndUpdateRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
		this.insertMappingOfRepayAndDeposit(docRepaySeq, joinedRegistAndUpdateRowDepositRecvSeqList, accgDocSeq);
	}
	
	/**
	 * 請求項目-預り金と、請求項目-預り金のマッピングデータを登録する<br>
	 * ※注意：まとめられている請求項目-預り金の行（InvoiceRowInputForm）専用のメソッド
	 * 
	 * @param sumInvoiceRow まとめられている請求項目の行データ
	 * @param joinedRegistAndUpdateRowList まとめられている行を構成する（登録or更新）請求項目の行データ
	 * @param accgDocSeq 会計書類SEQ
	 * @throws AppException
	 */
	private void insertInvoiceAndInvoiceDepositMappingForSumRow(InvoiceRowInputForm sumInvoiceRow, List<InvoiceRowInputForm> joinedRegistAndUpdateRowList, Long accgDocSeq) throws AppException {
		
		// 【まとめ行】まとめ状態の請求項目（実費）を登録 （※金額は引数のRowListから算出して登録する）
		TAccgDocInvoiceDepositEntity sumDocInvoiceDepositEntity = this.insertSumInvoiceRow(sumInvoiceRow, joinedRegistAndUpdateRowList);
		Long docInvoiceDepositSeq = sumDocInvoiceDepositEntity.getDocInvoiceDepositSeq();
		
		// 【登録対象行 + 更新対象行】まとめ行とのマッピングデータ登録 (registRepayRowList + updateRepayRowListの預り金SEQ)（対象行のjoinedRegistAndUpdateRowListには、必ず1つのdepositRecvSeqが存在している）
		List<Long> joinedRegistAndUpdateRowDepositRecvSeqList = joinedRegistAndUpdateRowList.stream().map(row -> row.getDepositRecvSeqList().get(0)).collect(Collectors.toList());
		this.insertMappingOfInvoiceAndDeposit(docInvoiceDepositSeq, joinedRegistAndUpdateRowDepositRecvSeqList, accgDocSeq);
	}
	
	/**
	 * 請求項目の更新（報酬項目とその他項目）
	 * 
	 * @param feeOrOtherRowList
	 * @param ankenId
	 * @param personId
	 * @param accgDocSeq
	 * @throws AppException 
	 */
	private void saveInvoiceFeeOrOther(List<InvoiceRowInputForm> feeOrOtherRowList, Long ankenId, Long personId,
			Long accgDocSeq) throws AppException {
		
		// 報酬の削除
		List<InvoiceRowInputForm> deleteFeeList = feeOrOtherRowList.stream()
				.filter(form -> form.getFeeSeq() != null)
				.filter(form -> form.isDeleteFlg())
				.collect(Collectors.toList());
		this.deleteInvoiceFee(deleteFeeList);

		// その他項目の削除
		List<InvoiceRowInputForm> deleteOtherList = feeOrOtherRowList.stream()
				.filter(form -> (form.getFeeSeq() == null && form.isDeleteFlg()))
				.collect(Collectors.toList());
		this.deleteInvoiceOther(deleteOtherList);

		// 報酬の新規登録
		List<InvoiceRowInputForm> newFeeList = feeOrOtherRowList.stream()
				.filter(form -> StringUtils.isEmpty(form.getInvoieType()))
				.filter(form -> !form.isDeleteFlg())
				.filter(form -> form.isAddFlg())
				.collect(Collectors.toList());
		this.insertInvoiceFee(newFeeList, accgDocSeq, ankenId, personId);

		// 未精算報酬の登録
		List<InvoiceRowInputForm> unPaidFeeList = feeOrOtherRowList.stream()
				.filter(form -> form.isUnPaidFeeFlg())
				.filter(form -> !form.isAddFlg() && !form.isDeleteFlg())
				.collect(Collectors.toList());
		this.insertUnpaidFeeAndUpdateInvoiceFee(unPaidFeeList, accgDocSeq, ankenId, personId);

		// その他項目の新規登録
		List<InvoiceRowInputForm> newOtherList = feeOrOtherRowList.stream()
				.filter(form -> !StringUtils.isEmpty(form.getInvoieType()))
				.filter(form -> !form.isDeleteFlg())
				.filter(form -> form.isAddFlg())
				.collect(Collectors.toList());
		this.insertInvoiceOther(newOtherList, accgDocSeq);

		// 報酬の更新
		List<InvoiceRowInputForm> updateFeeList = feeOrOtherRowList.stream()
				.filter(form -> StringUtils.isEmpty(form.getInvoieType()))
				.filter(form -> !form.isUnPaidFeeFlg())
				.filter(form -> !form.isAddFlg() && !form.isDeleteFlg())
				.collect(Collectors.toList());
		this.updateInvoiceFee(updateFeeList);

		// その他項目の更新
		List<InvoiceRowInputForm> updateOtherList = feeOrOtherRowList.stream()
				.filter(form -> form.getFeeSeq() == null)
				.filter(form -> !StringUtils.isEmpty(form.getInvoieType()))
				.filter(form -> !form.isAddFlg() && !form.isDeleteFlg())
				.collect(Collectors.toList());
		this.updateInvoiceOther(updateOtherList);
	}
	
	/**
	 * 請求項目の更新（預り金請求の項目）
	 * 
	 * @param depositRowList
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void saveInvoiceDeposit(List<InvoiceRowInputForm> depositRowList, Long accgDocSeq) throws AppException {
		
		// 預り金項目の削除
		List<InvoiceRowInputForm> deleteDepositList = depositRowList.stream()
				.filter(form -> form.isDeleteFlg())
				.collect(Collectors.toList());
		this.deleteInvoiceDeposit(deleteDepositList);

		// 預り金項目の新規登録
		List<InvoiceRowInputForm> newDepositList = depositRowList.stream()
				.filter(form -> !form.isDeleteFlg())
				.filter(form -> form.isAddFlg())
				.collect(Collectors.toList());
		this.insertInvoiceDeposit(newDepositList, accgDocSeq);

		// 預り金項目の更新
		List<InvoiceRowInputForm> updateDepositList = depositRowList.stream()
				.filter(form -> !form.isAddFlg() && !form.isDeleteFlg())
				.collect(Collectors.toList());
		this.updateInvoiceDeposit(updateDepositList);
	}
	
	/**
	 * 請求書を下書き状態に変更します<br>
	 * ・発行ステータス=下書き<br>
	 * ・入金ステータス=発行待ち<br>
	 * ・売上明細SEQ=空<br>
	 * ・回収不能金詳細SEQ=空
	 * 
	 * @throws AppException
	 */
	private void updateInvoiceToDraft(TAccgInvoiceEntity tAccgInvoiceEntity) throws AppException {

		// 請求書の発行ステータスを「下書き」にする。入金ステータスを「発行待ち」にする。売上明細SEQ、回収不能金詳細SEQを空にする。
		tAccgInvoiceEntity.setInvoiceIssueStatus(IssueStatus.DRAFT.getCd());
		tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.DRAFT.getCd());
		tAccgInvoiceEntity.setSalesDetailSeq(null);
		tAccgInvoiceEntity.setUncollectibleDetailSeq(null);

		// 更新
		this.updateAccgInvoice(tAccgInvoiceEntity);
	}

	/**
	 * 会計書類SEQに紐づく報酬データの入金ステータスを「未請求」、回収不能フラグを0に変更します<br>
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateFeeToUnclaimedPaymentStatusAndSetUncollectibleFlagToOff(Long accgDocSeq) throws AppException {
		// 報酬データを取得
		List<TFeeEntity> tFeeEntityList = tFeeDao.selectFeeEntityByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(tFeeEntityList)) {
			return;
		}

		// 入金ステータスを「未請求」、回収不能フラグに0をセット
		for (TFeeEntity entity : tFeeEntityList) {
			entity.setFeePaymentStatus(FeePaymentStatus.UNCLAIMED.getCd());
			entity.setUncollectibleFlg(SystemFlg.FLG_OFF.getCd());
		}

		this.batchUpdateTFee(tFeeEntityList);
	}

	/**
	 * 精算書を下書き状態に変更します<br>
	 * ・発行ステータス=下書き<br>
	 * ・返金ステータス=発行待ち<br>
	 * ・売上明細SEQ=空<br>
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void updateStatementToDraft(TAccgStatementEntity tAccgStatementEntity) throws AppException {

		// 精算書の発行ステータスを「下書き」にする。入金ステータスを「発行待ち」にする。売上明細SEQ。
		tAccgStatementEntity.setStatementIssueStatus(IssueStatus.DRAFT.getCd());
		tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.DRAFT.getCd());
		tAccgStatementEntity.setSalesDetailSeq(null);

		// 更新
		this.updateAccgStatement(tAccgStatementEntity);
	}

	/**
	 * 発行処理時の請求書、精算書のステータスの更新。<br>
	 * 会計書類タイプが請求書の場合、発行ステータス：送付待ち、入金ステータス：入金待ち<br>
	 * 会計書類タイプが精算書の場合、発行ステータス：送付待ち、返金ステータス：精算待ち（精算額が0円の場合は、「精算済み」で更新する。）<br>
	 * 
	 * @param accgDocSeq
	 * @param accgDocType 会計書類タイプ
	 * @throws AppException
	 */
	private void updateDocStatusWhenIssue(Long accgDocSeq, AccgDocType accgDocType) throws AppException {

		if (AccgDocType.INVOICE.equals(accgDocType)) {
			// 請求書
			TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByAccgDocSeq(accgDocSeq);
			if (tAccgInvoiceEntity == null) {
				throw new DataNotFoundException("請求書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
			}
			// ステータスセット
			tAccgInvoiceEntity.setInvoiceIssueStatus(IssueStatus.WAIT_DELIV.getCd());
			tAccgInvoiceEntity.setInvoicePaymentStatus(InvoicePaymentStatus.AWAITING_PAYMENT.getCd());

			// 更新
			this.updateAccgInvoice(tAccgInvoiceEntity);

		} else if (AccgDocType.STATEMENT.equals(accgDocType)) {
			// 精算書
			TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
			if (tAccgStatementEntity == null) {
				throw new DataNotFoundException("精算書情報が存在しません。[accgDocSeq=" + accgDocSeq + "]");
			}
			// ステータスセット
			tAccgStatementEntity.setStatementIssueStatus(IssueStatus.WAIT_DELIV.getCd());
			if (LoiozNumberUtils.equalsZero(tAccgStatementEntity.getStatementAmount())) {
				// 精算額が0円の場合
				// -> 「精算済み」とする
				tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.PAID.getCd());
			} else {
				// 精算額が0円以外の場合
				// -> 「精算待ち」とする
				tAccgStatementEntity.setStatementRefundStatus(StatementRefundStatus.AWAITING_STATEMENT.getCd());
			}

			// 更新
			this.updateAccgStatement(tAccgStatementEntity);
		}
	}

	/**
	 * 報酬情報とタイムチャージ-報酬付帯情報を更新します。<br>
	 * 更新対象：項目、金額、タイムチャージフラグ、税区分、税額、源泉徴収フラグ、源泉徴収額、単価、時間。（摘要、取引日は更新しない）
	 * 
	 * @param updateFeeList
	 * @throws AppException
	 */
	private void updateFeeAndFeeAddTimeCharge(List<InvoiceRowInputForm> updateFeeList) throws AppException {
		if (LoiozCollectionUtils.isEmpty(updateFeeList)) {
			return;
		}
		
		// 対象の報酬SEQリスト
		List<Long> feeSeqList = updateFeeList.stream()
				.filter(form -> form.getFeeSeq() != null)
				.map(row -> row.getFeeSeq())
				.collect(Collectors.toList());
		
		// 報酬の更新
		List<TFeeEntity> tFeeEntityList = tFeeDao.selectFeeByFeeSeqList(feeSeqList);
		if (LoiozCollectionUtils.isEmpty(tFeeEntityList)) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 更新データをentityにセット
		for (TFeeEntity entity : tFeeEntityList) {
			Optional<InvoiceRowInputForm> invoiceRowInputFormOpt = updateFeeList.stream().filter(row -> row.getFeeSeq().equals(entity.getFeeSeq())).findFirst();
			if (invoiceRowInputFormOpt.isPresent()) {
				InvoiceRowInputForm invoiceRowInputForm = invoiceRowInputFormOpt.get();
				entity.setFeeItemName(invoiceRowInputForm.getItemName());
				entity.setFeeTimeChargeFlg(invoiceRowInputForm.isFeeTimeChargeFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
				BigDecimal taxAmount = commonAccgAmountService.calcTaxAmount(LoiozNumberUtils.parseAsBigDecimal(invoiceRowInputForm.getAmount()), invoiceRowInputForm.getTaxRateType(), TaxFlg.FOREIGN_TAX.getCd());
				entity.setTaxAmount(taxAmount);
				entity.setFeeAmount(LoiozNumberUtils.parseAsBigDecimal(invoiceRowInputForm.getAmount()));
				entity.setTaxRateType(invoiceRowInputForm.getTaxRateType());
				entity.setWithholdingFlg(invoiceRowInputForm.isWithholdingFlg() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
				if (invoiceRowInputForm.isWithholdingFlg()) {
					entity.setWithholdingAmount(commonAccgAmountService.calcWithholdingAmount(LoiozNumberUtils.parseAsBigDecimal(invoiceRowInputForm.getAmount())));
				} else {
					entity.setWithholdingAmount(null);
				}
				// 取引日、摘要も連動する
				entity.setFeeDate(DateUtils.parseToLocalDate(invoiceRowInputForm.getTransactionDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				entity.setSumText(invoiceRowInputForm.getSumText());
			}
		}
		this.batchUpdateTFee(tFeeEntityList);
		
		// 報酬SEQに紐づくタイムチャージ-報酬付帯情報の一括削除
		List<TFeeAddTimeChargeEntity> tFeeAddTimeChargeEntityList = tFeeAddTimeChargeDao.selectTimeChargeSettingByFeeSeqList(feeSeqList);
		if (LoiozCollectionUtils.isNotEmpty(tFeeAddTimeChargeEntityList)) {
			this.batchDeleteTFeeAddTimeCharge(tFeeAddTimeChargeEntityList);
		}
		
		// タイムチャージ-報酬付帯情報の登録
		List<InvoiceRowInputForm> registTimeChargeFeeList = updateFeeList.stream()
				.filter(form -> form.isFeeTimeChargeFlg())
				.collect(Collectors.toList());
		if (LoiozCollectionUtils.isNotEmpty(registTimeChargeFeeList)) {
			List<TFeeAddTimeChargeEntity> registFeeAddTimeChargeEntityList = new ArrayList<>();
			for (InvoiceRowInputForm row : registTimeChargeFeeList) {
				TFeeAddTimeChargeEntity entity = new TFeeAddTimeChargeEntity();
				entity.setFeeSeq(row.getFeeSeq());
				if (!StringUtils.isEmpty(row.getHourPrice())) {
					entity.setHourPrice(LoiozNumberUtils.parseAsBigDecimal(row.getHourPrice()));
				}
				if (row.getWorkTimeMinute() != null) {
					entity.setWorkTimeMinute(Integer.valueOf(row.getWorkTimeMinute().toString()));
				}
				registFeeAddTimeChargeEntityList.add(entity);
			}
			this.batchInsertTFeeAddTimeCharge(registFeeAddTimeChargeEntityList);
		}
	}

	/**
	 * 会計書類SEQに紐づく請求項目関連のデータを削除します。
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void deleteAccgDocInvoice(Long accgDocSeq) throws AppException {
		// 請求項目データがあれば削除
		List<TAccgDocInvoiceEntity> accgDocInvoiceEntityList = tAccgDocInvoiceDao.selectAccgDocInvoiceByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(accgDocInvoiceEntityList)) {
			return;
		}

		// 請求項目SEQリスト
		List<Long> docInvoiceSeqList = accgDocInvoiceEntityList.stream().map(entity -> entity.getDocInvoiceSeq()).collect(Collectors.toList());

		// 請求項目-報酬データがあれば削除
		List<TAccgDocInvoiceFeeEntity> accgDocInvoiceFeeEntityList = tAccgDocInvoiceFeeDao.selectAccgDocInvoiceFeeBydocInvoiceSeqList(docInvoiceSeqList);
		if (LoiozCollectionUtils.isNotEmpty(accgDocInvoiceFeeEntityList)) {
			// 請求項目-報酬データ削除
			this.batchDeleteTAccgDocInvoiceFee(accgDocInvoiceFeeEntityList);
		}

		// 請求項目-預り金データがあれば削除
		List<TAccgDocInvoiceDepositEntity> accgDocInvoiceDepositEntityList = tAccgDocInvoiceDepositDao.selectAccgDocInvoiceDepositByDocInvoiceSeqList(docInvoiceSeqList);
		if (LoiozCollectionUtils.isNotEmpty(accgDocInvoiceDepositEntityList)) {
			this.batchDeleteTAccgDocInvoiceDeposit(accgDocInvoiceDepositEntityList);
			
			// 請求項目-預り金（実費）_預り金テーブルマッピングデータがあれば削除
			List<Long> docInvoiceDepositSeqList = accgDocInvoiceDepositEntityList.stream()
					.filter(entity -> InvoiceDepositType.EXPENSE.equalsByCode(entity.getInvoiceDepositType()))
					.map(entity -> entity.getDocInvoiceDepositSeq()).collect(Collectors.toList());
			if (LoiozCollectionUtils.isNotEmpty(docInvoiceDepositSeqList)) {
				// マッピングデータ取得
				List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList = tAccgDocInvoiceDepositTDepositRecvMappingDao
						.selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeqList(docInvoiceDepositSeqList);
				if (LoiozCollectionUtils.isNotEmpty(tAccgDocInvoiceDepositTDepositRecvMappingEntityList)) {
					
					// 預り金SEQリスト
					List<Long> depositRecvSeqList = tAccgDocInvoiceDepositTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
					
					// マッピングを削除
					this.batchDeleteTAccgDocInvoiceDepositTDepositRecvMapping(tAccgDocInvoiceDepositTDepositRecvMappingEntityList);

					// マッピングを削除した預り金の「会計書類SEQ（使用先）」をNULLで更新
					Long usingAccgDocSeq = null;
					this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, usingAccgDocSeq);
				}
			}
		}

		// 請求その他項目データがあれば削除
		List<TAccgDocInvoiceOtherEntity> accgDocInvoiceOtherEntityList = tAccgDocInvoiceOtherDao.selectAccgDocInvoiceOtherByDocInvoiceSeq(docInvoiceSeqList);
		if (LoiozCollectionUtils.isNotEmpty(accgDocInvoiceOtherEntityList)) {
			this.batchDeleteTAccgDocInvoiceOther(accgDocInvoiceOtherEntityList);
		}

		// 請求項目データ削除
		this.batchDeleteTAccgDocInvoice(accgDocInvoiceEntityList);

		// 請求項目-消費税の削除
		this.deleteAccgInvoiceTax(accgDocSeq);

		// 請求項目-源泉徴収削除
		this.deleteAccgInvoiceWithholding(accgDocSeq);
	}

	/**
	 * 会計書類SEQに紐づく既入金項目データを削除します
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void deleteAccgDocRepay(Long accgDocSeq) throws AppException {
		// 既入金項目データがあれば削除する
		List<TAccgDocRepayEntity> accgDocRepayEntityList = tAccgDocRepayDao.selectDocRepayByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(accgDocRepayEntityList)) {
			return;
		}
		// 既入金項目一括削除
		this.batchDeleteTAccgDocRepay(accgDocRepayEntityList);
		
		// 既入金項目_預り金テーブルマッピングデータを削除する
		List<Long> docRepaySeqList = accgDocRepayEntityList.stream().map(entity -> entity.getDocRepaySeq()).collect(Collectors.toList());
		// 既入金項目_預り金テーブルマッピングデータ取得
		List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList = tAccgDocRepayTDepositRecvMappingDao
				.selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeqList(docRepaySeqList);
		if (LoiozCollectionUtils.isNotEmpty(tAccgDocRepayTDepositRecvMappingEntityList)) {
			
			// 預り金SEQリスト
			List<Long> depositRecvSeqList = tAccgDocRepayTDepositRecvMappingEntityList.stream().map(entity -> entity.getDepositRecvSeq()).collect(Collectors.toList());
			
			// マッピングを削除削除
			this.batchDeleteTAccgDocRepayTDepositRecvMapping(tAccgDocRepayTDepositRecvMappingEntityList);

			// マッピングを削除した預り金の「会計書類SEQ（使用先）」をNULLで更新
			Long usingAccgDocSeq = null;
			this.updateTDepositRecvUsingAccgDocSeq(depositRecvSeqList, usingAccgDocSeq);
		}
	}

	/**
	 * 会計書類SEQに紐づく請求項目-消費税データを削除します
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void deleteAccgInvoiceTax(Long accgDocSeq) throws AppException {
		// 請求項目-消費税データがあれば削除する
		List<TAccgInvoiceTaxEntity> accgInvoiceTaxEntityList = tAccgInvoiceTaxDao.selectAccgInvoiceTaxByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(accgInvoiceTaxEntityList)) {
			return;
		}

		this.batchDeleteTAccgInvoiceTax(accgInvoiceTaxEntityList);
	}

	/**
	 * 会計書類SEQに紐づく請求項目-源泉徴収データを削除します
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void deleteAccgInvoiceWithholding(Long accgDocSeq) throws AppException {
		// 請求項目-源泉徴収データがあれば削除する
		TAccgInvoiceWithholdingEntity tAccgInvoiceWithholdingEntity = tAccgInvoiceWithholdingDao.selectAccgInvoiceWithholdingByAccgDocSeq(accgDocSeq);
		if (tAccgInvoiceWithholdingEntity == null) {
			return;
		}

		this.deleteAccgInvoiceWithholding(tAccgInvoiceWithholdingEntity);
	}

	/**
	 * 会計書類-対応に関するデータを削除します。<br>
	 * ※注意：対象のaccgDocSeqを削除するとき用のメソッド。
	 * 
	 * <pre>
	 * 会計書類（accgDocSeq）を削除する際、送付処理を行っている会計書類は削除できないように、削除処理の前段でチェックを行うため、
	 * このメソッドの実行時に送付データが存在する場合（=会計書類が送付された状態の場合）は削除不可としてエラーとする。
	 * </pre>
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void deleteAccgDocActRelated(Long accgDocSeq) throws AppException {
		// 会計書類-対応データ取得
		List<TAccgDocActEntity> accgDocActEntityList = tAccgDocActDao.selectAccgDocActByAccgDocSeq(accgDocSeq);
		if (LoiozCollectionUtils.isEmpty(accgDocActEntityList)) {
			return;
		}

		// 会計書類-対応データ（送付対応）
		List<TAccgDocActEntity> sendAccgDocActEntityList = accgDocActEntityList.stream()
			.filter(e -> AccgDocActType.SEND.equalsByCode(e.getActType()))
			.collect(Collectors.toList());
		
		if (LoiozCollectionUtils.isNotEmpty(sendAccgDocActEntityList)) {
			// 「送付」の対応データが存在する場合
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 会計書類-対応データ削除
		this.batchDeleteTAccgDocAct(accgDocActEntityList);
		
		// 送付の会計書類対応SEQリスト
		List<Long> sendAccgDocActSeqList = sendAccgDocActEntityList.stream().map(entity -> entity.getAccgDocActSeq()).collect(Collectors.toList());
		
		// 送付の会計書類対応-送付データ取得
		List<TAccgDocActSendEntity> accgDocActSendEntityList = tAccgDocActSendDao.selectAccgDocActSendByAccgDocActSeq(sendAccgDocActSeqList);
		if (LoiozCollectionUtils.isNotEmpty(accgDocActSendEntityList)) {
			// 送付データが存在する場合
			// エラーとする
			throw new IllegalStateException("送付データが存在するのに、会計書類（請求書／精算書）の削除処理が実行されようとしている。");
		}
	}

	/**
	 * 会計書類ファイルの削除処理<br>
	 * 
	 * <pre>
	 * S3オブジェクトキーをDBから削除するため、S3オブジェクトファイルを削除を呼び出し元で実施すること
	 * </pre>
	 *
	 * @param accgDocSeq
	 * @param deleteS3ObjectKeys
	 * @throws AppException
	 */
	private void deleteAccgDocFileRelated(Long accgDocSeq, List<String> deleteS3ObjectKeys) throws AppException {

		List<TAccgDocFileEntity> tAccgDocFileEntityList = tAccgDocFileDao.selectAccgDocFileByAccgDocSeq(accgDocSeq);
		List<TAccgDocFileDetailEntity> tAccgDocFileDetailEntityList = tAccgDocFileDetailDao.selectAccgDocFileDetailByAccgDocSeq(accgDocSeq);

		// 削除予定のS3オブジェクトキーを引数の削除対象に追加
		tAccgDocFileDetailEntityList.stream().map(TAccgDocFileDetailEntity::getS3ObjectKey).forEach(deleteS3ObjectKeys::add);

		try {
			if (!LoiozCollectionUtils.isEmpty(tAccgDocFileEntityList)) {
				// 会計書類ファイル情報が存在する場合 -> 削除処理
				tAccgDocFileDao.batchDelete(tAccgDocFileEntityList);
			}

			if (!LoiozCollectionUtils.isEmpty(tAccgDocFileDetailEntityList)) {
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
	
	//=========================================================================
	// ▼ 登録／更新／削除の実行メソッド（ロジックをほぼ含まないもの）
	//=========================================================================
	
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
	 * 既入金項目テーブルの一括登録をおこないます
	 * 
	 * @param accgDocRepayEntityList
	 * @return
	 * @throws AppException
	 */
	private List<TAccgDocRepayEntity> batchInsertAccgDocRepay(List<TAccgDocRepayEntity> accgDocRepayEntityList) throws AppException {
		int[] insertAccgDocRepayCount = null;
		insertAccgDocRepayCount = tAccgDocRepayDao.batchInsert(accgDocRepayEntityList);
		if (accgDocRepayEntityList.size() != insertAccgDocRepayCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return accgDocRepayEntityList;
	}

	/**
	 * 売上明細-消費税テーブルの一括登録をおこないます
	 * 
	 * @param salesDetailTaxEntityList
	 * @return
	 * @throws AppException
	 */
	private List<TSalesDetailTaxEntity> batchInsertSalesDetailTax(List<TSalesDetailTaxEntity> salesDetailTaxEntityList) throws AppException {
		int[] insertSalesDetailCount = null;
		insertSalesDetailCount = tSalesDetailTaxDao.batchInsert(salesDetailTaxEntityList);
		if (salesDetailTaxEntityList.size() != insertSalesDetailCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return salesDetailTaxEntityList;
	}

	/**
	 * 請求預り金項目テーブルに一括登録をおこないます
	 * 
	 * @param accgDocInvoiceDepositEntityList
	 * @return
	 * @throws AppException
	 */
	private List<TAccgDocInvoiceDepositEntity> batchInsertAccgDocInvoiceDeposit(List<TAccgDocInvoiceDepositEntity> accgDocInvoiceDepositEntityList) throws AppException {
		int[] insertAccgDocInvoiceDepositCount = null;
		insertAccgDocInvoiceDepositCount = tAccgDocInvoiceDepositDao.batchInsert(accgDocInvoiceDepositEntityList);
		if (accgDocInvoiceDepositEntityList.size() != insertAccgDocInvoiceDepositCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return accgDocInvoiceDepositEntityList;
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
	 * 既入金項目_預り金テーブルマッピングテーブルの一括登録をおこないます
	 * 
	 * @param repayDepositMappingList
	 * @throws AppException
	 */
	private void batchInsertRepayDepositMapping(List<TAccgDocRepayTDepositRecvMappingEntity> repayDepositMappingList) throws AppException {
		int[] insertRepayDepositMappingCount = null;
		insertRepayDepositMappingCount = tAccgDocRepayTDepositRecvMappingDao.batchInsert(repayDepositMappingList);
		if (repayDepositMappingList.size() != insertRepayDepositMappingCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 請求項目-預り金（実費）_預り金テーブルマッピングの一括登録をおこないます
	 * 
	 * @param depositMappingList
	 * @throws AppException
	 */
	private void batchInsertInvoiceDepositTDepositMapping(List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> depositMappingList) throws AppException {
		int[] insertDepositMappingCount = null;
		insertDepositMappingCount = tAccgDocInvoiceDepositTDepositRecvMappingDao.batchInsert(depositMappingList);
		if (depositMappingList.size() != insertDepositMappingCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 報酬テーブルの一括登録をおこないます
	 * 
	 * @param tFeeEntityList
	 * @return
	 * @throws AppException
	 */
	private List<TFeeEntity> batchInsertTFee(List<TFeeEntity> tFeeEntityList) throws AppException {
		int[] insertCount = tFeeDao.batchInsert(tFeeEntityList);
		if (tFeeEntityList.size() != insertCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return tFeeEntityList;
	}

	/**
	 * タイムチャージテーブルの一括登録をおこないます
	 * 
	 * @param tFeeAddTimeChargeEntityList
	 * @return
	 * @throws AppException
	 */
	private List<TFeeAddTimeChargeEntity> batchInsertTFeeAddTimeCharge(List<TFeeAddTimeChargeEntity> tFeeAddTimeChargeEntityList) throws AppException {
		int[] insertTimeChargeCount = tFeeAddTimeChargeDao.batchInsert(tFeeAddTimeChargeEntityList);
		if (tFeeAddTimeChargeEntityList.size() != insertTimeChargeCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return tFeeAddTimeChargeEntityList;
	}

	/**
	 * 請求項目テーブルの登録を行う
	 * 
	 * @param tAccgDocInvoiceEntity
	 * @return
	 * @throws AppException
	 */
	private TAccgDocInvoiceEntity insertTAccgDocInvoice(TAccgDocInvoiceEntity tAccgDocInvoiceEntity) throws AppException {
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
	 * 請求その他項目テーブルの一括登録をおこないます
	 * 
	 * @param accgDocInvoiceOtherEntityList
	 * @return
	 * @throws AppException
	 */
	private List<TAccgDocInvoiceOtherEntity> batchInsertAccgDocInvoiceOther(List<TAccgDocInvoiceOtherEntity> accgDocInvoiceOtherEntityList) throws AppException {
		int[] insertAccgDocInvoiceOtherCount = null;
		insertAccgDocInvoiceOtherCount = tAccgDocInvoiceOtherDao.batchInsert(accgDocInvoiceOtherEntityList);
		if (accgDocInvoiceOtherEntityList.size() != insertAccgDocInvoiceOtherCount.length) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return accgDocInvoiceOtherEntityList;
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
	 * 請求項目_預り金を更新します
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
	 * 既入金項目テーブルの一括更新をおこないます
	 * 
	 * @param tAccgDocRepayEntityList
	 * @throws AppException
	 */
	private void batchUpdateTAccgDocRepay(List<TAccgDocRepayEntity> tAccgDocRepayEntityList) throws AppException {
		int[] updateAccgDocRepayCount = null;
		try {
			updateAccgDocRepayCount = tAccgDocRepayDao.batchUpdate(tAccgDocRepayEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocRepayEntityList.size() != updateAccgDocRepayCount.length) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 請求報酬項目テーブルの一括更新をおこないます
	 * 
	 * @param tAccgDocInvoiceFeeEntityList
	 * @param confirmationDataSize
	 * @throws AppException
	 */
	private void batchUpdateTAccgDocInvoiceFee(List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList) throws AppException {
		int[] updateAccgDocInvoiceFeeCount = null;
		try {
			updateAccgDocInvoiceFeeCount = tAccgDocInvoiceFeeDao.batchUpdate(tAccgDocInvoiceFeeEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceFeeEntityList.size() != updateAccgDocInvoiceFeeCount.length) {
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
	 * 請求項目テーブルの更新
	 * 
	 * @param tAccgDocInvoiceEntity
	 * @throws AppException
	 */
	private void updateTAccgDocInvoice(TAccgDocInvoiceEntity tAccgDocInvoiceEntity) throws AppException {
		int updateCount = 0;

		try {
			updateCount = tAccgDocInvoiceDao.update(tAccgDocInvoiceEntity);
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
	 * 請求項目テーブルの一括更新をおこないます
	 * 
	 * @param tAccgDocInvoiceEntityList
	 * @throws AppException
	 */
	private void batchUpdateTAccgDocInvoice(List<TAccgDocInvoiceEntity> tAccgDocInvoiceEntityList) throws AppException {
		int[] updateAccgDocInvoiceCount = null;
		try {
			updateAccgDocInvoiceCount = tAccgDocInvoiceDao.batchUpdate(tAccgDocInvoiceEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceEntityList.size() != updateAccgDocInvoiceCount.length) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 請求預り金項目テーブルの一括更新をおこないます
	 * 
	 * @param tAccgDocInvoiceDepositEntityList
	 * @throws AppException
	 */
	private void batchUpdateTAccgDocInvoiceDeposit(List<TAccgDocInvoiceDepositEntity> tAccgDocInvoiceDepositEntityList) throws AppException {
		int[] updateAccgDocInvoiceDepositCount = null;
		try {
			updateAccgDocInvoiceDepositCount = tAccgDocInvoiceDepositDao.batchUpdate(tAccgDocInvoiceDepositEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceDepositEntityList.size() != updateAccgDocInvoiceDepositCount.length) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 請求その他項目テーブルの一括更新をおこないます
	 * 
	 * @param tAccgDocInvoiceOtherEntityList
	 * @throws AppException
	 */
	private void batchUpdateTAccgDocInvoiceOther(List<TAccgDocInvoiceOtherEntity> tAccgDocInvoiceOtherEntityList) throws AppException {
		int[] updateAccgDocInvoiceOtherCount = null;
		try {
			updateAccgDocInvoiceOtherCount = tAccgDocInvoiceOtherDao.batchUpdate(tAccgDocInvoiceOtherEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceOtherEntityList.size() != updateAccgDocInvoiceOtherCount.length) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 預り金テーブルの一括更新をおこないます
	 * 
	 * @param tDepositRecvEntityList
	 * @throws AppException
	 */
	private void batchUpdateTDepositRecv(List<TDepositRecvEntity> tDepositRecvEntityList) throws AppException {
		int[] updateDepositRecvCount = null;
		try {
			updateDepositRecvCount = tDepositRecvDao.batchUpdate(tDepositRecvEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tDepositRecvEntityList.size() != updateDepositRecvCount.length) {
			// 更新に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 会計書類SEQに紐づく会計書類データを削除します。
	 * 
	 * @param accgDocSeq
	 * @throws AppException
	 */
	private void deleteAccgDoc(Long accgDocSeq) throws AppException {
		TAccgDocEntity tAccgDocEntity = tAccgDocDao.selectAccgDocByAccgDocSeq(accgDocSeq);
		if (tAccgDocEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		int deleteCount = 0;
		try {
			deleteCount = tAccgDocDao.delete(tAccgDocEntity);
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
	 * 請求書テーブルを更新する
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
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 精算書テーブルを更新する
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
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 請求項目-源泉徴収テーブルの削除をおこないます
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
	 * 既入金項目テーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocRepayEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocRepay(List<TAccgDocRepayEntity> tAccgDocRepayEntityList) throws AppException {
		int[] deleteAccgDocRepayCount = null;
		try {
			deleteAccgDocRepayCount = tAccgDocRepayDao.batchDelete(tAccgDocRepayEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocRepayEntityList.size() != deleteAccgDocRepayCount.length) {
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
	 * 請求報酬項目テーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocInvoiceFeeEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocInvoiceFee(List<TAccgDocInvoiceFeeEntity> tAccgDocInvoiceFeeEntityList) throws AppException {
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
	}

	/**
	 * 請求その他項目テーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocInvoiceOtherEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocInvoiceOther(List<TAccgDocInvoiceOtherEntity> tAccgDocInvoiceOtherEntityList) throws AppException {
		int[] deleteAccgDocInvoiceOtherCount = null;
		try {
			deleteAccgDocInvoiceOtherCount = tAccgDocInvoiceOtherDao.batchDelete(tAccgDocInvoiceOtherEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceOtherEntityList.size() != deleteAccgDocInvoiceOtherCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

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
	 * 既入金項目テーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocRepayEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgInvoiceTax(List<TAccgInvoiceTaxEntity> tAccgInvoiceTaxEntityList) throws AppException {
		int[] deleteAccgInvoiceTaxCount = null;
		try {
			deleteAccgInvoiceTaxCount = tAccgInvoiceTaxDao.batchDelete(tAccgInvoiceTaxEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgInvoiceTaxEntityList.size() != deleteAccgInvoiceTaxCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 会計書類-対応テーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocActEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocAct(List<TAccgDocActEntity> tAccgDocActEntityList) throws AppException {
		int[] deleteCount = null;
		try {
			deleteCount = tAccgDocActDao.batchDelete(tAccgDocActEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocActEntityList.size() != deleteCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 既入金項目_預り金テーブルマッピングテーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocRepayTDepositRecvMappingEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocRepayTDepositRecvMapping(List<TAccgDocRepayTDepositRecvMappingEntity> tAccgDocRepayTDepositRecvMappingEntityList) throws AppException {
		int[] deleteCount = null;
		try {
			deleteCount = tAccgDocRepayTDepositRecvMappingDao.batchDelete(tAccgDocRepayTDepositRecvMappingEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocRepayTDepositRecvMappingEntityList.size() != deleteCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}
	
	/**
	 * 請求項目-預り金テーブルマッピングテーブルの一括削除をおこないます
	 * 
	 * @param tAccgDocInvoiceDepositTDepositRecvMappingEntityList
	 * @throws AppException
	 */
	private void batchDeleteTAccgDocInvoiceDepositTDepositRecvMapping(List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> tAccgDocInvoiceDepositTDepositRecvMappingEntityList) throws AppException {
		int[] deleteCount = null;
		try {
			deleteCount = tAccgDocInvoiceDepositTDepositRecvMappingDao.batchDelete(tAccgDocInvoiceDepositTDepositRecvMappingEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tAccgDocInvoiceDepositTDepositRecvMappingEntityList.size() != deleteCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * タイムチャージテーブルの一括削除をおこないます
	 * 
	 * @param tFeeAddTimeChargeEntityList
	 * @throws AppException
	 */
	private void batchDeleteTFeeAddTimeCharge(List<TFeeAddTimeChargeEntity> tFeeAddTimeChargeEntityList) throws AppException {
		int[] deleteTimeChargeCount = null;
		try {
			deleteTimeChargeCount = tFeeAddTimeChargeDao.batchDelete(tFeeAddTimeChargeEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (tFeeAddTimeChargeEntityList.size() != deleteTimeChargeCount.length) {
			// 削除処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

}