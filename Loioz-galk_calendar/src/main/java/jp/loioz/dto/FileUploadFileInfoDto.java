package jp.loioz.dto;

import java.math.BigInteger;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * ファイル管理画面用アップロードファイル情報
 *
 */

@Data
public class FileUploadFileInfoDto {

	/** アップロードファイル情報 */
	private MultipartFile uploadFile;

	/** アップロードファイルのフルパス */
	private String uploadFileFullPath;

	/** アップロード対象ファイルのファイルサイズ */
	private BigInteger uploadFileSize;

	/** アップロード対象ファイルのファイル作成日時(文字列) */
	private String uploadFileCreateDateTimeString;

	/** アップロード対象ファイルのUUID */
	private String uuid;

}
