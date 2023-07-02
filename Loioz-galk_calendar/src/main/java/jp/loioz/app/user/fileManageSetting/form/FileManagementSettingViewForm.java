package jp.loioz.app.user.fileManageSetting.form;

import lombok.Data;

/**
 * ファイル管理設定用画面表示オブジェクト
 */
@Data
public class FileManagementSettingViewForm {

	/** 外部連携フォルダ選択エリア表示用 */
	private ExternalStorageSelectViewForm externalStorageSelectViewForm;

	@Data
	public static class ExternalStorageSelectViewForm {

		/** ファイル管理種別 */
		private String storageType;

		/** 画面に表示するインフォメーションメッセージを表示する */
		private String infoMsg;

		/** API取得で発生したエラーメッセージを画面に表示する */
		private String apiErrorMsg;

		/** ルートフォルダが削除されているかどうか */
		private boolean isNotFount = false;

		/** ルートフォルダ情報 */
		private RootFolderInfoDto rootFolderInfo;

		/** 現在のloiozストレージが無料の状態かどうか */
		private boolean nowLoiozStorageIsFree;
	}

}
