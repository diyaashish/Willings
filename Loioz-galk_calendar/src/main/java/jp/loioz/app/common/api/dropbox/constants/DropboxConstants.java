package jp.loioz.app.common.api.dropbox.constants;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Dropbox関連のConstants
 */
public class DropboxConstants {

	/** DropboxアプリのbaseURL */
	public static final String DROPBOX_APP_BASE = "https://www.dropbox.com/home";

	/** DropboxのOAuth認証用URLのテンプレート */
	public static final String AUTHORIZATION_BASE = "https://www.dropbox.com/oauth2/authorize?client_id=%s&redirect_uri=%s&state=%s&response_type=code&token_access_type=offline";

	/** Dropbox ※Dropbox側のシステムにも登録しているため、値は変えないこと */
	public static final String DROPBOX_AUTH_GATEWAY_REDIRECT_PATH = "dropboxAuthGatewayRedirect";

	/**
	 * DropboxのOAuth認証時にredirect先を決定するEnum
	 */
	@Getter
	@AllArgsConstructor
	public enum DropboxAuthRequestCode implements DefaultEnum {

		/** ファイル管理設定画面からのDropboxを選択した時 */
		SYSTEM_CONNECT("1", "システム連携"),
		/** アカウント画面から連携を行う時 */
		USER_CONNECT("2", "ユーザー連携");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コード値からEnumを取得
		 * 
		 * @param cd
		 * @return
		 */
		public static DropboxAuthRequestCode of(String cd) {
			return DefaultEnum.getEnum(DropboxAuthRequestCode.class, cd);
		}
	}

}
