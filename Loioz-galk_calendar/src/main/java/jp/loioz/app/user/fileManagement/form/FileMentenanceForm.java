package jp.loioz.app.user.fileManagement.form;

import lombok.Data;

@Data
public class FileMentenanceForm {

	/** ファイル構成管理ID */
	private Long fileConfigurationManagementId;

	/** ファイル区分 */
	private String fileKubun;

	/** ファイル名 */
	private String fileName;

	/** ファイルタイプ */
	private String fileType;

	/** 閲覧権限 */
	private Boolean ableLimit;
}
