package jp.loioz.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * レスポンスのコード値を表現するクラス
 */
@Getter
@AllArgsConstructor
public enum StatusCodeEnum {
	
	// ---------------------------------------------------------
	// ファイル管理
	// ---------------------------------------------------------
	/** アップロード容量超過 */
	ERROR_UPLOAD_VOLUME_EXCEEDING("1001")
	
	/** ファイル・フォルダ名重複 */
	, WARN_FILE_DUPLICATE("1002")
	
	/** アップロード失敗 */
	, ERROR_UPLOAD_FAILED("1003")
	
	/** フォルダ作成失敗 */
	, ERROR_FOLDER_CREATE_FAILED("1005")
	
	/** バリデーションチェックエラー */
	, ERROR_VALIDATION_RESULT_IS_ERROR("1006")
	
	/** ゴミ箱移動失敗 */
	, ERROR_MOVE_TRASH_BOX_FAILED("1007")
	
	/** ファイル復元エラー */
	, ERROR_RESTORE_FILE_FAILED("1008")
	
	/** ファイル削除エラー */
	, ERROR_FILE_DELETE_FAILED("1009")
	
	/** 名称変更エラー */
	, ERROR_NAME_CHANGE_FAILED("1010")
	
	/** フォルダ名重複(エラー) */
	, ERROR_FOLDER_DUPLICATE("1011")
	
	/** ファイル移動エラー */
	, ERROR_MOVE_TO_FOLDER_FAILED("1012")
	
	/** ファイルダウンロード事前チェック移動エラー */
	, ERROR_DOWNLOAD_CHECK_FAILED("1013")

	/** 1ファイル名が256バイト以上エラー */
	,ERROR_UPLOAD_MAX_FILE_NAME_SIZE("1014")
	
	/** ファイルダウンロードエラー */
	,ERROR_DOWNLOAD_FILE("1015")
	
	/** ステータスコードのキー */
	;private final String codeKey;

}
