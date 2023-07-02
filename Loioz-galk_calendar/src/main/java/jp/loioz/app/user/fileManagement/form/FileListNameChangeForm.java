package jp.loioz.app.user.fileManagement.form;

import jp.loioz.common.validation.annotation.FileName;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class FileListNameChangeForm {
	
	/** ファイル構成管理ID */
	private Long fileConfigurationManagementId;
	
	/** 現在階層のフォルダパス */
	private String currentFolderPath;
	
	/** ルートフォルダ関連情報管理ID */
	private Long rootFolderRelatedInfoManagementId;
	
	/** 現在階層がサブフォルダかの判定用フラグ */
	private boolean currenIsSubFolder;
	
	/** フォルダ名 */
	@Required(message = "変更後の名称を入力してください")
	@MaxDigit(max = 255, message = "変更後の名称は255文字以内で入力してください")
	@FileName
	private String renameInputText;

	/** ファイル拡張子 */
	private String fileExtension;
}
