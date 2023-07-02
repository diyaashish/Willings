package jp.loioz.dto;

import lombok.Data;

/**
 * ファイル管理 汎用Dto
 */
@Data
public class FileGeneralDto {

	/** ファイル詳細情報(ファイル用)作成用のフォルダパス要素 */
	private String uploadFolderPathElementForCreateFileDetailInfoOfFile;
	
	/** アップロードされたフォルダは多段構成か */
	private boolean isMultiLevelFolderStructure = false;
	
	/** アップロードしたフォルダのファイル構成管理ID */
	private Long uploadedFolderFileConfigurationManagementId;
	
}
