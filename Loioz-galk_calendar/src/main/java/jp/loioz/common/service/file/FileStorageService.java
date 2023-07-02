package jp.loioz.common.service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.S3Object;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.FileStorageConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.s3.S3AccessService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.file.Directory;
import jp.loioz.dto.DeleteFileInfoDto;
import jp.loioz.dto.FileContentsDto;
import jp.loioz.dto.UploadFileInfoDto;

/**
 * ファイルストレージサービスクラス
 */
@Service
public class FileStorageService extends DefaultService {

	/** ファイルアップロード時にオブジェクト名に付与する日付フォーマット */
	private final String UPLOAD_FILE_NAME_DATE_PART_FORMAT = "uuuu-MM-dd'T'HH-mm-ss-SSS";

	/** 一時ファイル格納先ディレクトリパス */
	@Value("${uploadFile.tmp}")
	private String uploadTempPath;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/** S3アクセスオブジェクト */
	@Autowired
	private S3AccessService s3Access;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * ファイルのアップロード
	 *
	 * @param dirPath
	 * @param file
	 * @return
	 * @throws IllegalStateException
	 * @throws AppException
	 * @throws IOException
	 */
	public UploadFileInfoDto uploadPrivate(String dirPath, MultipartFile file) throws IllegalStateException, AppException, IOException {
		// S3へのアップロード
		Directory dir = new Directory(dirPath);
		return uploadPrivate(dir, file);
	}

	/**
	 * ファイルをアップロードする ※プライベートアップロード
	 *
	 * @param dir
	 * @param file
	 * @throws AppException
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public UploadFileInfoDto uploadPrivate(Directory dir, MultipartFile multiPartFile) throws AppException, IllegalStateException, IOException {

		String extension = this.getFileExtension(multiPartFile.getOriginalFilename());
		String objectKey = this.generateS3ObjectKey(dir, extension);
		String contentType = multiPartFile.getContentType();

		// multipartToFileをFile型に変換する
		File file = this.multipartToFile(multiPartFile);

		try {
			// アップロード処理
			this.uploadPrivateObjectAtMultiPart(objectKey, file, contentType);

			// アップロードファイル情報生成
			return this.getFileObjectInfoDto(objectKey);

		} finally {
			// 一時ファイルの削除
			if (!file.delete()) {
				// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
				file.deleteOnExit();
			}
		}

	}

	/**
	 * ファイルのアップロード
	 *
	 * @param dirPath
	 * @param byteArray
	 * @return
	 * @throws IllegalStateException
	 * @throws AppException
	 * @throws IOException
	 */
	public UploadFileInfoDto uploadPrivate(String dirPath, FileContentsDto byteArray) throws IllegalStateException, AppException, IOException {
		// S3へのアップロード
		Directory dir = new Directory(dirPath);
		return uploadPrivate(dir, byteArray);
	}

	/**
	 * ファイルをアップロードする ※プライベートアップロード
	 *
	 * @param dir
	 * @param file
	 * @throws AppException
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public UploadFileInfoDto uploadPrivate(Directory dir, FileContentsDto byteArray) throws AppException, IllegalStateException, IOException {

		// S3オブジェクトキーを生成
		String objectKey = this.generateS3ObjectKey(dir, byteArray.getExtension());

		// Byte[]をFile型に変換する
		File file = this.byteArrayToFile(byteArray);

		try {
			// アップロード処理
			this.uploadPrivateObjectAtMultiPart(objectKey, file, null);

			// アップロードファイル情報生成
			return this.getFileObjectInfoDto(objectKey);

		} finally {
			// 一時ファイルの削除
			if (!file.delete()) {
				// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
				file.deleteOnExit();
			}
		}
	}

	/**
	 * オブジェクトキーを指定してファイルをアップロードする
	 * 
	 * @param objectKey S3オブジェクトキー
	 * @param multiPartFile アップロードファイル情報
	 * @return
	 * @throws AppException
	 * @throws IOException
	 */
	public UploadFileInfoDto uploadPrivateSpecifyObjectKey(String objectKey, MultipartFile multiPartFile) throws AppException, IOException {

		// ContentTypeを取得
		String contentType = multiPartFile.getContentType();

		// multipartToFileをFile型に変換する
		File file = this.multipartToFile(multiPartFile);

		try {
			// アップロード処理
			this.uploadPrivateObjectAtMultiPart(objectKey, file, contentType);

			// アップロードファイル情報生成
			return this.getFileObjectInfoDto(objectKey);

		} finally {
			// 一時ファイルの削除
			if (!file.delete()) {
				// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
				file.deleteOnExit();
			}
		}
	}

	/**
	 * オブジェクトキーを指定してファイルをアップロードする
	 * 
	 * @param objectKey S3オブジェクトキー
	 * @param multiPartFile アップロードファイル情報
	 * @return
	 * @throws AppException
	 * @throws IOException
	 */
	public UploadFileInfoDto uploadPrivateSpecifyObjectKey(String objectKey, FileContentsDto byteArray) throws AppException, IOException {

		// multipartToFileをFile型に変換する
		File file = this.byteArrayToFile(byteArray);

		try {
			// アップロード処理
			this.uploadPrivateObjectAtMultiPart(objectKey, file, null);

			// アップロードファイル情報生成
			return this.getFileObjectInfoDto(objectKey);

		} finally {
			// 一時ファイルの削除
			if (!file.delete()) {
				// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
				file.deleteOnExit();
			}
		}
	}

	/**
	 * ファイルをアップロードする ※プライベートアップロード
	 * 
	 * <pre>
	 * すべてのファイルアップロードが正常終了しない場合、エラーとなる
	 * エラーが途中で発生した場合、すでにアップロード完了したファイルは一度だけ削除リクエストを投げる
	 * </pre>
	 * 
	 * @param dirPath
	 * @param fileList
	 * @return
	 * @throws AppException
	 * @throws IOException
	 */
	public List<UploadFileInfoDto> multiUploadPrivate(String dirPath, List<FileContentsDto> fileList) throws AppException, IOException {
		Directory dir = new Directory(dirPath);
		return multiUploadPrivate(dir, fileList);
	}

	/**
	 * ファイルをアップロードする ※プライベートアップロード
	 * 
	 * <pre>
	 * すべてのファイルアップロードが正常終了しない場合、エラーとなる
	 * エラーが途中で発生した場合、すでにアップロード完了したファイルは一度だけ削除リクエストを投げる
	 * </pre>
	 * 
	 * @param dir
	 * @param fileList
	 * @return
	 * @throws AppException
	 * @throws IOException
	 */
	public List<UploadFileInfoDto> multiUploadPrivate(Directory dir, List<FileContentsDto> fileList) throws AppException, IOException {

		if (CollectionUtils.isEmpty(fileList)) {
			return Collections.emptyList();
		}

		List<String> uploadedS3ObjectKey = new ArrayList<>();
		try {
			for (FileContentsDto fileContentsDto : fileList) {

				// S3オブジェクトキーを生成
				String objectKey = this.generateS3ObjectKey(dir, fileContentsDto.getExtension());

				// Byte[]をFile型に変換する
				File file = this.byteArrayToFile(fileContentsDto);

				try {
					// アップロード処理
					this.uploadPrivateObjectAtMultiPart(objectKey, file, null);

					// アップロードファイル情報生成
					uploadedS3ObjectKey.add(objectKey);

				} finally {
					// 一時ファイルの削除
					if (!file.delete()) {
						// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
						file.deleteOnExit();
					}
				}
			}
		} catch (Exception e) {
			// エラーは発生した場合、途中までアップロードしたファイルを削除する
			try {
				// 削除処理
				s3Access.deleteObjects(uploadedS3ObjectKey);
			} catch (MultiObjectDeleteException ex) {

				String deletedKey = ex.getDeletedObjects().stream().map(DeleteObjectsResult.DeletedObject::getKey).reduce("", (sum, key) -> sum + " ," + key);
				String uploatedKeyListStr = StringUtils.list2CommaSP(uploadedS3ObjectKey);

				String log = "複数ファイルのアップロード失敗時のゴミデータ削除が失敗しました。\n削除対象キー：「%s」\n削除完了対象キー：「%s」";
				logger.error(String.format(log, uploatedKeyListStr, deletedKey), ex);

			} catch (Exception ex) {
				logger.error("複数ファイルのアップロード失敗時のゴミデータ削除が失敗しました。", ex);
			}

			throw e;
		}

		return uploadedS3ObjectKey.stream().map(this::getFileObjectInfoDto).collect(Collectors.toList());
	}

	/**
	 * ファイルをアップロードする ※プライベートアップロード
	 * 
	 * <pre>
	 * すべてのファイルアップロードが正常終了しない場合、エラーとなる
	 * エラーが途中で発生した場合、すでにアップロード完了したファイルは一度だけ削除リクエストを投げる
	 * </pre>
	 *
	 * @param s3ObjectKeyFileMap キー：s3オブジェクトキー, バリュー：ファイルデータ
	 * @throws AppException
	 * @throws IOException
	 */
	public void multiUploadPrivateSpecifyObjectKey(Map<String, FileContentsDto> s3ObjectKeyFileMap) throws AppException, IOException {

		if (CollectionUtils.isEmpty(s3ObjectKeyFileMap)) {
			return;
		}

		List<String> uploadedS3ObjectKey = new ArrayList<>();
		try {
			for (Entry<String, FileContentsDto> entry : s3ObjectKeyFileMap.entrySet()) {

				String objectKey = entry.getKey();
				FileContentsDto dto = entry.getValue();

				// Byte[]をFile型に変換する
				File file = this.byteArrayToFile(dto);

				try {
					// アップロード処理
					this.uploadPrivateObjectAtMultiPart(objectKey, file, null);

					// アップロードファイル情報生成
					uploadedS3ObjectKey.add(objectKey);

				} finally {
					// 一時ファイルの削除
					if (!file.delete()) {
						// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
						file.deleteOnExit();
					}
				}

			}
		} catch (Exception e) {
			// エラーは発生した場合、途中までアップロードしたファイルを削除する
			try {
				// 削除処理
				s3Access.deleteObjects(uploadedS3ObjectKey);
			} catch (MultiObjectDeleteException ex) {

				String deletedKey = ex.getDeletedObjects().stream().map(DeleteObjectsResult.DeletedObject::getKey).reduce("", (sum, key) -> sum + " ," + key);
				String uploatedKeyListStr = StringUtils.list2CommaSP(uploadedS3ObjectKey);

				String log = "複数ファイルのアップロード失敗時のゴミデータ削除が失敗しました。\n削除対象キー：「%s」\n削除完了対象キー：「%s」";
				logger.error(String.format(log, uploatedKeyListStr, deletedKey), ex);

			} catch (Exception ex) {
				logger.error("複数ファイルのアップロード失敗時のゴミデータ削除が失敗しました。", ex);
			}

			throw e;
		}

	}

	/**
	 * ファイルのアップロード ※パブリックアップロード
	 *
	 * @param file
	 * @param dirPath
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws AppException
	 */
	public UploadFileInfoDto uploadPublic(String dirPath, MultipartFile file) throws AppException, IllegalStateException, IOException {
		// S3へのアップロード
		Directory dir = new Directory(dirPath);
		return uploadPublic(dir, file);
	}

	/**
	 * ファイルをアップロードする ※パブリックアップロード
	 *
	 * @param dir
	 * @param file
	 * @throws AppException
	 * @throws IOException
	 */
	public UploadFileInfoDto uploadPublic(Directory dir, MultipartFile multiPartFile) throws AppException, IllegalStateException, IOException {

		String extension = this.getFileExtension(multiPartFile.getOriginalFilename());
		String objectKey = this.generateS3ObjectKey(dir, extension);
		String contentType = multiPartFile.getContentType();

		// multipartToFileをFile型に変換する
		File file = this.multipartToFile(multiPartFile);

		try (InputStream is = multiPartFile.getInputStream();) {

			// ファイルアップロードを実行
			String objectUrl = s3Access.putPublicObjectForMultiPartUpload(objectKey, contentType, is);

			// アップロードファイル情報生成
			return this.getFileObjectInfoDto(objectKey, objectUrl);

		} catch (Exception ex) {
			// ファイルアップロード処理に失敗
			logger.warn("ファイルのアップロードに失敗しました。：" + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00032, ex);

		} finally {
			// 一時ファイルの削除
			if (!file.delete()) {
				// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
				file.deleteOnExit();
			}
		}
	}

	/**
	 * ファイルを削除する
	 *
	 * @param deleteFileInfoDtoList
	 * @throws AppException
	 */
	public List<DeleteFileInfoDto> delete(List<DeleteFileInfoDto> deleteFileInfoDtoList) throws AppException {

		if (deleteFileInfoDtoList.isEmpty()) {
			return Collections.emptyList();
		}

		// 削除対象のオブジェクトキーを取得
		List<String> deleteTargetKeys = deleteFileInfoDtoList.stream()
				.map(dto -> dto.getObjectKey())
				.collect(Collectors.toList());

		List<String> deletedFileObjKeys = new ArrayList<>();

		try {
			// 削除
			deletedFileObjKeys = s3Access.deleteObjects(deleteTargetKeys);

		} catch (Exception ex) {
			// ファイル削除処理に失敗
			logger.warn("ファイルの削除に失敗しました。：" + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

		// 削除が成功したオブジェクトを取得
		List<String> deletedKeys = deletedFileObjKeys;
		List<DeleteFileInfoDto> deletedFileInfoDtoList = deleteFileInfoDtoList.stream()
				.filter(dto -> deletedKeys.contains(dto.getObjectKey()))
				.collect(Collectors.toList());

		return deletedFileInfoDtoList;
	}

	/**
	 * ファイルを削除する
	 *
	 * @param deleteTargetKeys
	 * @return
	 * @throws AppException
	 */
	public List<String> deleteFile(List<String> deleteTargetKeys) throws AppException {

		List<String> deletedFileObjKeys = new ArrayList<>();

		try {
			// 削除
			deletedFileObjKeys = s3Access.deleteObjects(deleteTargetKeys);

		} catch (Exception ex) {
			// ファイル削除処理に失敗
			logger.warn("ファイルの削除に失敗しました。：" + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

		return deletedFileObjKeys;
	}

	/**
	 * ファイルダウンロード
	 *
	 * @param s3ObjectKey ダウンロード対象のオブジェクトキー
	 * @return S3Object
	 * @throws Exception
	 */
	public S3Object fileDownload(String s3ObjectKey) throws Exception {
		return s3Access.getObject(s3ObjectKey);
	}

	/**
	 * 各テナント用に置換されたディレクトリパスを取得する
	 *
	 * @return dirPath
	 */
	public String getRepDirPath(String dirPath, Long tenantSeq, String subDomainName) {
		// ディレクトリパスを置換する
		String repDirPath = dirPath.replace(FileStorageConstant.REPLACE_TENANT_DOMAIN_DIRECTORY, tenantSeq.toString() + "_" + subDomainName);
		return repDirPath;
	}

	/**
	 * ファイル名から拡張子を返します。
	 * 
	 * @param fileName ファイル名
	 * @return ファイルの拡張子
	 */
	public String getFileExtension(String fileName) {
		String extension = "";
		if (StringUtils.isEmpty(fileName)) {
			return extension;
		}

		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			extension = fileName.substring(point + 1);
		}

		return extension;
	}

	/**
	 * アップロードファイルのオブジェクトキーを作成する。
	 * この値はS3のバケット内で一意の値となる。<br>
	 * 
	 * @param dir
	 * @param fileExtension
	 * @return
	 */
	public String generateS3ObjectKey(Directory dir, String fileExtension) {
		String objectName = this.getUploadFileNameForStorage(fileExtension);
		return dir.getDirPath() + objectName;
	}

	/**
	 * MultipartFileをFile型に変換する
	 *
	 * ※ 呼び出し元では該当ファイルの{@link java.io.File#delete() delete} を行うこと<br>
	 * このメソッドを呼び出さないとサーバーに該当ファイルの一時ファイルが残ってしまう。
	 *
	 * @param file
	 * @return convFile
	 */
	public File multipartToFile(MultipartFile file) throws IllegalStateException, IOException {

		String originalFileName = file.getOriginalFilename();
		Pattern p = Pattern.compile("/");
		Matcher m = p.matcher(originalFileName);

		if (m.find()) {
			logger.error("「" + originalFileName + "」は許容していないファイル名のパターンです");
			throw new RuntimeException("ファイル名が不正です。");
		}

		File convFile = new File(uploadTempPath, originalFileName);
		convFile.createNewFile();

		try (FileOutputStream fos = new FileOutputStream(convFile);) {
			fos.write(file.getBytes());
		}

		return convFile;
	}

	/**
	 * byte[]をFile型に変換する<br>
	 * 
	 * ※ 呼び出し元では該当ファイルの{@link java.io.File#delete() delete} を行うこと<br>
	 * このメソッドを呼び出さないとサーバーに該当ファイルの一時ファイルが残ってしまう。
	 * 
	 * @param byteArrayDto
	 * @param generateFileName 拡張子は含まない, NULL許容
	 * @return
	 */
	private File byteArrayToFile(FileContentsDto byteArrayDto) throws IllegalStateException, IOException {

		String fileName = UUID.randomUUID().toString();

		File convFile = new File(uploadTempPath, fileName + StringUtils.defaultString(byteArrayDto.getExtensionIncludeDots()));
		convFile.createNewFile();

		try (FileOutputStream fos = new FileOutputStream(convFile);) {
			fos.write(byteArrayDto.getByteArray());
		}

		return convFile;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * S3へマルチパートアップロードを行う
	 * 
	 * @param objectKey
	 * @param file
	 * @param contentType システムで作成したファイルのみNULLを指定する。<br>
	 * エンドユーザーからのアップロードファイルがMultiPartFile型であるはずなのでそこから取得すること
	 * @throws AppException
	 */
	private void uploadPrivateObjectAtMultiPart(String objectKey, File file, String contentType) throws AppException {

		String s3PutContentType = contentType;
		if (StringUtils.isEmpty(s3PutContentType)) {
			s3PutContentType = this.getContentType(file);
		}

		try (InputStream is = new FileInputStream(file);) {
			// アップロード処理を行う
			s3Access.putPrivateObjectForMultiPartUpload(objectKey, s3PutContentType, is);
		} catch (Exception ex) {
			// ファイルアップロード処理に失敗
			logger.warn("ファイルのアップロードに失敗しました。：" + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00032, ex);
		}
	}

	/**
	 * FileからContentTypeを取得する
	 * 
	 * @param file
	 * @return
	 */
	private String getContentType(File file) {
		try {
			return Files.probeContentType(file.toPath());
		} catch (Exception e) {
			return Mimetypes.getInstance().getMimetype(file);
		}
	}

	/**
	 * ストレージに保存する用のファイル名を取得する。<br>
	 * 
	 * ※S3ストレージにて一意となるファイル名を生成し、返却する。
	 * ※S3ストレージ側ではこのファイル名でファイルが保存される。
	 *
	 * @param fileExtension
	 * @param prependStr
	 * @return
	 */
	private String getUploadFileNameForStorage(String fileExtension) {

		LocalDateTime now = LocalDateTime.now();
		String formatedNow = DateUtils.parseToString(now, this.UPLOAD_FILE_NAME_DATE_PART_FORMAT);

		// 一意となるランダム文字列
		String uuid = UUID.randomUUID().toString();
		String uploadFileName = formatedNow + "_" + uuid;

		// 拡張子がない場合
		if (StringUtils.isEmpty(fileExtension)) {
			return uploadFileName;
		}

		Pattern p = Pattern.compile("\\.");
		Matcher m = p.matcher(fileExtension);

		if (m.find()) {
			throw new RuntimeException(fileExtension + "にドットが含まれています");
		}

		return uploadFileName + "." + fileExtension;
	}

	/**
	 * アップロードファイルオブジェクトを取得
	 *
	 * @param objectKey
	 * @param objectUrl
	 * @return
	 */
	private UploadFileInfoDto getFileObjectInfoDto(String objectKey, String objectUrl) {

		return new UploadFileInfoDto(objectKey, objectUrl);
	}

	/**
	 * アップロードファイルオブジェクトを取得 ※プライベートアップロードでの使用を想定
	 *
	 * @param objectKey
	 * @return
	 */
	private UploadFileInfoDto getFileObjectInfoDto(String objectKey) {
		return new UploadFileInfoDto(objectKey);
	}

}