package jp.loioz.dto;

import lombok.Data;

@Data
public class DeleteFileInfoDto {

	/** オブジェクトキー */
	private String objectKey;

	/** ファイル連番 (DB側の連番) */
	private Long fileSeq;

	// =========================================================================
	// コンストラクタ
	// =========================================================================
	public DeleteFileInfoDto(String objectKey, Long fileSeq) {
		this.objectKey = objectKey;
		this.fileSeq = fileSeq;
	}
}