package jp.loioz.app.global.download.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.model.S3Object;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.global.common.exception.GlobalAuthException;
import jp.loioz.app.global.common.service.CommonDownloadService;
import jp.loioz.app.global.download.dto.DownloadFileDto;
import jp.loioz.app.global.download.form.DownloadViewForm;
import jp.loioz.bean.AccgDocFileBean;
import jp.loioz.bean.DownloadListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TAccgDocActSendFileDao;
import jp.loioz.dao.TAccgDocDownloadDao;
import jp.loioz.dao.TAccgDocFileDao;
import jp.loioz.entity.TAccgDocActSendFileEntity;
import jp.loioz.entity.TAccgDocDownloadEntity;

/**
 * グローバルダウンロード画面
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DownloadService extends DefaultService {

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 共通：ダウンロードサービス */
	@Autowired
	private CommonDownloadService commonDownloadService;

	/** 共通ファイルストレージサービス */
	@Autowired
	private FileStorageService fileStorageService;

	/** 会計管理の共通サービス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** テナントDB：ファイルダウンロードDao */
	@Autowired
	private TAccgDocDownloadDao tAccgDocDownloadDao;

	/** テナントDB：会計ファイル情報 */
	@Autowired
	private TAccgDocFileDao tAccgDocFileDao;

	/** テナントDB：会計書類-対応-送付-ファイルDao */
	@Autowired
	private TAccgDocActSendFileDao tAccgDocActSendFileDao;

	/**
	 * アクセスが有効かどうか
	 *
	 * @param downloadUrlKey
	 * @return
	 * @throws GlobalAuthException
	 */
	public void varificationAccessEnableCheck(String downloadUrlKey) throws GlobalAuthException {
		// 認証キーからダウンロード情報を取得する。エラーが発生しなければ有効
		TAccgDocDownloadEntity tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadUrlKey);
		
		// 有効期限の検証。エラーが発生しなければ有効
		LocalDate now = LocalDate.now();
		commonDownloadService.validDownloadLimit(tAccgDocDownloadEntity, now);
	}

	/**
	 * 認証トークンの有効チェック
	 * 
	 * @param downloadUrlKey
	 * @param verificationToken
	 * @throws GlobalAuthException
	 */
	public void varificationTokenEnabledCheck(String downloadUrlKey, String verificationToken) throws GlobalAuthException {

		TAccgDocDownloadEntity tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadUrlKey);
		if (!Objects.equals(verificationToken, tAccgDocDownloadEntity.getVerificationToken())) {
			throw new GlobalAuthException(MessageEnum.MSG_E00195, "認証トークンが入力内容と一致しません。");
		}
	}

	/**
	 * ダウンロード画面表示用オブジェクトを作成する
	 * 
	 * @param tenantAuthKey
	 * @param downloadViewUrlKey
	 * @return
	 */
	public DownloadViewForm createDownloadViewForm(String tenantAuthKey, String downloadViewUrlKey) {

		TAccgDocDownloadEntity tAccgDocDownloadEntity;
		try {
			// ダウンロード情報を取得する
			tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadViewUrlKey);

		} catch (GlobalAuthException ex) {
			// 本メソッドでは、認証チェック後に呼ばれる想定なので、ここで発生するエラーは想定外
			throw new RuntimeException("想定外のエラーが発生しました。", ex);
		}

		var downloadViewForm = new DownloadViewForm();
		downloadViewForm.setTenantAuthKey(tenantAuthKey);
		downloadViewForm.setDownloadViewUrlKey(downloadViewUrlKey);
		downloadViewForm.setTenantName(tAccgDocDownloadEntity.getTenantName());
		downloadViewForm.setDownloadLimitDt(DateUtils.parseToString(tAccgDocDownloadEntity.getDownloadLimitDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		return downloadViewForm;
	}

	/**
	 * ダウンロード一覧画面情報を取得する
	 * 
	 * @param downloadViewUrlKey
	 * @return
	 */
	public DownloadViewForm.DownloadListViewForm getDownloadListViewForm(String downloadViewUrlKey) {

		TAccgDocDownloadEntity tAccgDocDownloadEntity;
		try {
			// ダウンロード情報を取得する
			tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadViewUrlKey);

		} catch (GlobalAuthException ex) {
			// 本メソッドでは、認証チェック後に呼ばれる想定なので、ここで発生するエラーは想定外
			throw new RuntimeException("想定外のエラーが発生しました。", ex);
		}

		var downloadListViewForm = new DownloadViewForm.DownloadListViewForm();

		// データ取得
		List<DownloadListBean> donwloadListBeanList = tAccgDocDownloadDao
				.selectDownloadListBeanByAccgDocDownloadSeq(tAccgDocDownloadEntity.getAccgDocDownloadSeq());
		List<DownloadFileDto> downloadFileDtoList = donwloadListBeanList.stream()
				.map(e -> {
					DownloadFileDto dto = new DownloadFileDto();
					var accgDocFileType = AccgDocFileType.of(e.getAccgDocFileType());
					dto.setIssueDate(
						DateUtils.parseToString(e.getIssueDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
					dto.setAccgDocActSendFileSeq(e.getAccgDocActSendFileSeq());
					dto.setAccgDocFileType(accgDocFileType);
					dto.setFileName(this.createAccgDocFileName(e.getAccgDocSeq(), accgDocFileType,
						FileExtension.ofExtension(e.getFileExtension())));
					dto.setDownloaded(e.getDownloadLastAt() != null);
					dto.setDownloadDate(DateUtils.parseToString(e.getDownloadLastAt(),
						DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED));
					return dto;
				})
				.sorted(Comparator.comparing(DownloadFileDto::getAccgDocFileType))
				.collect(Collectors.toList());

		// パラメータの設定
		LocalDate now = LocalDate.now();
		LocalDate downloadLimit = tAccgDocDownloadEntity.getDownloadLimitDate();
		downloadListViewForm.setDownloadExpired(now.isAfter(downloadLimit));
		downloadListViewForm.setDownloadFileList(downloadFileDtoList);

		return downloadListViewForm;
	}

	/**
	 * ダウンロード処理
	 * 
	 * @param downloadViewUrlKey
	 * @param accgDocActSendFileSeq
	 * @param response
	 * @throws AppException
	 */
	public void downloadFile(String downloadViewUrlKey, Long accgDocActSendFileSeq, HttpServletResponse response)
			throws AppException {

		TAccgDocDownloadEntity tAccgDocDownloadEntity;
		try {
			// ダウンロード情報を取得する
			tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadViewUrlKey);

		} catch (GlobalAuthException ex) {
			// 本メソッドでは、認証チェック後に呼ばれる想定なので、ここで発生するエラーは想定外
			throw new RuntimeException("想定外のエラーが発生しました。", ex);
		}

		LocalDateTime now = LocalDateTime.now();
		if (now.toLocalDate().isAfter(tAccgDocDownloadEntity.getDownloadLimitDate())) {
			// ダウンロード可能日時を過ぎている場合
			throw new AppException(MessageEnum.MSG_E00183, null);
		}

		TAccgDocActSendFileEntity tAccgDocActSendFileEntity = tAccgDocActSendFileDao
				.selectAccgDocActSendFileByAccgDocActSendFileSeq(accgDocActSendFileSeq);
		if (!Objects.equals(tAccgDocDownloadEntity.getAccgDocActSendSeq(),
				tAccgDocActSendFileEntity.getAccgDocActSendSeq())) {
			// リクエストパラメータのファイルSEQがダウンロード情報と紐づいていない場合
			logger.error("不正なリクエストパラメータ");
			throw new RuntimeException("リクエストパラメータのファイルSEQがダウンロード情報と紐づいていません");
		}

		// ダウンロード日時を設定
		tAccgDocActSendFileEntity.setDownloadLastAt(now);

		try {
			// 更新処理
			tAccgDocActSendFileDao.update(tAccgDocActSendFileEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// ファイル情報の取得
		List<AccgDocFileBean> accgDocFileBeanList = tAccgDocFileDao
				.selectAccgDocFileBeanByAccgDocFileSeq(tAccgDocActSendFileEntity.getAccgDocFileSeq());
		AccgDocFileBean downloadFileBean = accgDocFileBeanList.stream()
				.filter(e -> e.getFileBranchNo() == 1).findFirst().orElseThrow();

		// ファイルダウンロードの準備
		String fileName = this.createAccgDocFileName(downloadFileBean.getAccgDocSeq(),
				AccgDocFileType.of(downloadFileBean.getAccgDocFileType()),
				FileExtension.ofExtension(downloadFileBean.getFileExtension()));

		try (S3Object s3Object = fileStorageService.fileDownload(downloadFileBean.getS3ObjectKey());) {
			// S3からファイルをダウンロード
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + "\"");
			response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT, CommonConstant.HEADER_VALUE_OF_AJAX_PROC_RESULT_SUCCESS);

			// 書き込み処理
			IOUtils.copy(s3Object.getObjectContent(), response.getOutputStream());
		} catch (Exception ex) {
			// 想定外のエラー
			throw new RuntimeException(ex);
		}

	}

	/**
	 * 会計ファイルの名前を作成します
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @param fileExtension
	 * @return
	 */
	private String createAccgDocFileName(Long accgDocSeq, AccgDocFileType accgDocFileType, FileExtension fileExtension) {
		return commonAccgService.createAccgDocFileName(accgDocSeq, accgDocFileType, fileExtension);
	}
}
