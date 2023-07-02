package jp.loioz.app.user.fileManagement.form;

import lombok.Data;

@Data
public class FileEditViewLimit {

	/** フォルダ権限情報管理ID */
	private Long folderPermissionInfoManagementId;

	/** ファイル構成管理ID */
	private Long fileConfigurationManagementId;

	/** 閲覧権限 */
	private String viewLimit;
}
