package jp.loioz.dto;

import lombok.Data;

/**
 * アップロードファイル用のDtoクラス
 */
@Data
public class UploadFileInfoDto {

	/** オブジェクトキー */
	private String objectKey;

	/** オブジェクトURL */
	private String objectUrl;

	/** ファイル名 */
	private String fileName;

	// =========================================================================
	// コンストラクタ
	// =========================================================================
	public UploadFileInfoDto(String objectKey, String objectUrl) {
		this.objectKey = objectKey;
		this.objectUrl = objectUrl;
	}

	public UploadFileInfoDto(String objectKey) {
		this.objectKey = objectKey;
	}
}