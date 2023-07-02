package jp.loioz.app.user.fileManagement.form;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;

/**
 * ファイル管理画面アップロード前チェック用Form
 */
@Data
public class FileUploadPreCheckForm {

	/** アップロード合計サイズ */
	private BigInteger uploadTotalSize;
	
	/** アップロードファイル名 */
	private List<String> uploadFileNameList;
	
	/** ファイルフルパスリスト */
	private List<String> uploadFileFullPathList;
	
	/** ルートフォルダ関連情報管理ID */
	private Long rootFolderRelatedInfoManagementId;

	/** アップロード時の現在階層 */
	private String currentFolderPath;
	
}
