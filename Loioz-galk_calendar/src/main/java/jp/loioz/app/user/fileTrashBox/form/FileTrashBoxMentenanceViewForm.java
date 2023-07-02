package jp.loioz.app.user.fileTrashBox.form;

import lombok.Data;

/**
 * ファイル管理画面表示Form
 */
@Data
public class FileTrashBoxMentenanceViewForm {

	/** ファイル構成管理ID */
	private Long fileConfigurationManagementId;

	/** ファイル区分 */
	private String fileKubun;

	/** フォルダ・ファイル名 */
	private String fileName;

}
