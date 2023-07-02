package jp.loioz.app.user.toiawase.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.URLCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.FileStorageConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozIOUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TToiawaseAttachmentDao;
import jp.loioz.domain.UriService;
import jp.loioz.dto.UploadFileInfoDto;
import jp.loioz.entity.TToiawaseAttachmentEntity;

/**
 * 問い合わせファイル用サービスクラス<br>
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ToiawaseFileService extends DefaultService {

	/** S3のアップロード先ディレクトリパス */
	private static final String TOIAWASE_S3_DIR_PATH = FileStorageConstant.S3ToiawaseDir.TE_DO_TOIAWASE.getPath();

	/** ファイルアップロード共通部品 */
	@Autowired
	private FileStorageService fileStorageService;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** 問い合わせ-添付Daoクラス */
	@Autowired
	private TToiawaseAttachmentDao tToiawaseAttachmentDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	/**
	 * ファイルのダウロード処理
	 *
	 * @param toiawaseAttachmentSeq
	 * @param response
	 * @throws AppException
	 */
	public void fileDownloadFromAmazonS3(Long toiawaseAttachmentSeq, HttpServletResponse response) throws AppException {

		TToiawaseAttachmentEntity tToiawaseAttachmentEntity = tToiawaseAttachmentDao.selectBySeq(toiawaseAttachmentSeq);

		try {
			// ファイルダウンロードの準備
			URLCodec codec = new URLCodec("UTF-8");
			String encodeFileName = codec.encode(tToiawaseAttachmentEntity.getFileName());
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()) + "\"");

			// s3のダウンロード処理
			S3Object s3Object = fileStorageService.fileDownload(tToiawaseAttachmentEntity.getS3ObjectKey());

			// 書き込み処理
			InputStream stream = s3Object.getObjectContent();
			LoiozIOUtils.copy(stream, response.getOutputStream());
			s3Object.close();

		} catch (Exception e) {
			// ダウンロードの失敗処理
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, e);
			throw new AppException(MessageEnum.MSG_E00034, e);
		}

	}

	/**
	 * ファイルのアップロード処理
	 *
	 * @param toiawaseSeq
	 * @param toiawaseDetailSeq
	 * @param uploadFile
	 * @throws AppException
	 */
	public void toiawaseFileUpload(Long toiawaseSeq, Long toiawaseDetailSeq, List<MultipartFile> uploadFile) throws AppException {

		for (MultipartFile file : uploadFile) {

			// ファイルサイズが0の場合は何もしない
			long fileSize = file.getSize();
			if (fileSize == 0) {
				continue;
			}

			UploadFileInfoDto uploadFileInfoDto;
			try {
				// ファイルのアップロード処理
				uploadFileInfoDto = this.fileUpload(file);
			} catch (Exception e) {
				// ファイルアップロードで発生するエラーをすべてAppExcetopmで投げる
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.error(classAndMethodName, e);
				throw new AppException(MessageEnum.MSG_E00032, e);
			}

			// アップロードファイル情報をDBに登録
			TToiawaseAttachmentEntity tToiawaseAttachmentEntity = new TToiawaseAttachmentEntity();
			tToiawaseAttachmentEntity.setToiawaseSeq(toiawaseSeq);
			tToiawaseAttachmentEntity.setToiawaseDetailSeq(toiawaseDetailSeq);
			tToiawaseAttachmentEntity.setS3ObjectKey(uploadFileInfoDto.getObjectKey());
			tToiawaseAttachmentEntity.setFileName(file.getOriginalFilename());
			if (file.getOriginalFilename().lastIndexOf(".") != -1) {
				String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
				tToiawaseAttachmentEntity.setFileExtension(extension);
			}

			try {
				// 問い合わせ - 添付テーブルに登録処理
				tToiawaseAttachmentDao.insert(tToiawaseAttachmentEntity);

			} catch (OptimisticLockingFailureException e) {
				// ファイルアップロードで発生するエラーをすべてAppExcetopmで投げる
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + e.getMessage());
				throw new AppException(MessageEnum.MSG_E00032, e);
			}

		}

	}

	/**
	 * 問い合わせ添付ファイルのアップロード処理
	 *
	 * @param uploadFile
	 * @param uuid
	 * @return アップロードしたファイル情報
	 * @throws IllegalStateException
	 * @throws AppException
	 * @throws IOException
	 */
	private UploadFileInfoDto fileUpload(MultipartFile uploadFile) throws IllegalStateException, AppException, IOException {

		// dirPathの作成
		String dirPath = fileStorageService.getRepDirPath(TOIAWASE_S3_DIR_PATH, SessionUtils.getTenantSeq(), uriService.getSubDomainName());

		// アップロード処理
		return fileStorageService.uploadPrivate(dirPath, uploadFile);
	}

}
