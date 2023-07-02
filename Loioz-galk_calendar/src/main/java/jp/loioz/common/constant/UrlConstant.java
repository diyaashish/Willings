package jp.loioz.common.constant;

/**
 * URL定数クラス
 */
public class UrlConstant {

	/** URL区切り文字 */
	public static final String SLASH = "/";
	/** パラメータ区切り文字 */
	public static final String AMPERSAND = "&";
	/** ドット */
	public static final String DOT = ".";
	/** ? */
	public static final String QUESTION = "?";
	/** = */
	public static final String EQUAL = "=";
	/** 顧客・案件ID */
	public static final String TRANSITION_CUSTOMER_ID = "transitionCustomerId=";
	public static final String TRANSITION_ANKEN_ID = "transitionAnkenId=";

	/** 会計管理の関連画面 案件軸表示用クエリ名 */
	public static final String ACCG_REFERER_ANKEN_SIDE_PARAM_NAME = "isAccgAnkenSide";

	/**************************************
	 * ユーザURL
	 **************************************/
	/** プレログイン画面のURL */
	public static final String PRE_LOGIN_URL = "user/preLogin";
	/** ログイン画面のURL */
	public static final String LOGIN_URL = "user/login";
	/** アカウント申込画面のURL */
	public static final String ACCOUNT_REGIST_URL = "user/accountRegist";
	/** アカウント登録画面のURL */
	public static final String REGIST_URL = "user/accountDetailRegist";
	/** ログイン処理のPOSTURL */
	public static final String LOGIN_POST_REQUEST_URL = "/spring_security_login";
	/** ログアウト処理のPOSTURL */
	public static final String LOGOUT_POST_REQUEST_URL = "/user/logout";
	/** ログイン成功時に遷移する画面のURL */
	public static final String LOGIN_SUCCESS_URL = "/user/schedule/";
	/** プラン設定画面の入り口となるコントローラーのURL */
	public static final String PLANSETTING_GATEWAY_URL = "user/planSettingGateWay";
	/** プラン設定画面のURL */
	public static final String PLANSETTING_URL = "user/planSetting";
	/** プラン設定画面からテナント側（ログインSessionを持っている側）にアクセスして処理する用のコントローラーのURL */
	public static final String PLANSETTING_TENANT_ACCESS_URL = "user/planSettingTenantAccess";

	/** GoogleAPIのURL ※Google側のシステムにも登録しているため、値は変えないこと */
	public static final String GOOGLE_API_URL = "common/api/google";
	/** BoxAPIのURL ※Box側のシステムにも登録しているため、値は変えないこと */
	public static final String BOX_API_URL = "common/api/box";
	/** DropboxAPIのURL ※Dropbox側のシステムにも登録しているため、値は変えないこと */
	public static final String DROPBOX_API_URL = "common/api/dropbox";

	/** 個人設定 > 外部サービス接続 */
	public static final String MY_EXTERNAL_SETTING_URL = "user/myExternalSetting";

	/** 名簿管理 */
	public static final String PERSON_EDIT_URL = "/user/personManagement/edit/";
	/** 業務履歴（顧客） */
	public static final String GYOMU_HISTORY_EDIT_CUSTOMER_URL = "/user/gyomuHistory/customer/list?customerId=";
	/** ファイルストレージ（顧客） */
	public static final String FILE_STORAGE_EDIT_CUSTOMER_URL = "/user/fileManagement/list?" + TRANSITION_CUSTOMER_ID;

	/** 案件管理 */
	public static final String ANKEN_DASHBOARD_URL = "/user/ankenDashboard/";
	/** 案件管理 */
	public static final String ANKEN_EDIT_URL = "/user/ankenManagement/edit/";
	/** 業務履歴（案件） */
	public static final String GYOMU_HISTORY_EDIT_ANKEN_URL = "/user/gyomuHistory/anken/list?" + TRANSITION_ANKEN_ID;
	/** ファイルストレージ（案件） */
	public static final String FILE_STORAGE_EDIT_ANKEN_URL = "/user/fileManagement/list?" + TRANSITION_ANKEN_ID;

	/** 予定管理（案件） */
	public static final String YOTEI_MANEGE_ANKEN_URL = "/user/yoteiManagement/list?" + TRANSITION_ANKEN_ID;
	/** 関与者（案件） */
	public static final String KANYOSHA_ANKEN_URL = "/user/kanyosha/list?" + TRANSITION_ANKEN_ID;
	/** 預かり品（案件） */
	public static final String AZUKARI_ITEM_ANKEN_URL = "/user/azukariItem/list?" + TRANSITION_ANKEN_ID;

	/** 裁判管理（案件） */
	public static final String SAIBAN_MANAGE_ANKEN_URL = "/user/saibanManagement/";
	/** 裁判（民事） */
	public static final String SAIBAN_MANAGE_MINJI_URL = "/user/saibanMinjiManagement/";
	/** 裁判（刑事） */
	public static final String SAIBAN_MANAGE_KEIJI_URL = "/user/saibanKeijiManagement/";

	/** 権限エラー */
	public static final String PERMISSION_ERROR_URL = "/permissionError";

	/** プラン機能制限エラー */
	public static final String PLAN_FUNC_RESTRICT_ERROR_URL = "/planFuncRestrictError";

	/** 利用中にプランが変更した旨のエラー */
	public static final String PLAN_CHANGED_ERROR_URL = "/planChangedError";

	/** 二重ログインエラー */
	public static final String MULTI_LOGIN_ERROR_URL = "/multiLoginError";

	/**************************************
	 * 旧会計機能URL
	 **************************************/
	
	/** 会計管理一覧 */
	public static final String KAIKEI_EDIT_URL = "/user/kaikeiManagement/list";
	
	/** 会計管理画面系のControllerのRequestMappingUrl */
	public static final String KAIKEI_MANAGEMENT_URL = "user/kaikeiManagement";
	
	/** 精算書画面系のControllerのRequestMappingUrl */
	public static final String SEISANSHO_URL = "user/seisansho";
	
	/** 未収入金画面系のControllerのRequestMappingUrl */
	public static final String AZUKARIKIN_MANAGEMENT_URL = "user/azukarikinManagement";
	
	/** 入手金予定_入金予定画面系のControllerのRequestMappingUrl */
	public static final String NYUSHUKKIN_YOTEI_NYUKIN_URL = "user/nyushukkinYotei/nyukin";
	
	/** 入手金予定_出金予定画面系のControllerのRequestMappingUrl */
	public static final String NYUSHUKKIN_YOTEI_SHUKKIN_URL = "user/nyushukkinYotei/shukkin";
	
	/** 旧会計機能の設定画面系のControllerのRequestMappingUrl */
	public static final String FUNC_OLD_KAIKEI_SETTING_URL = "user/funcOldKaikeiSetting";
	
	/**************************************
	 * グローバルURL
	 **************************************/
	/** ダウンロード認証のURL */
	public static final String G_DOWNLOAD_AUTH_URL = "global/downloadAuth";

	/** ダウンロード画面のURL */
	public static final String G_DOWNLOAD_URL = "global/download";

	/**************************************
	 * システム管理URL
	 **************************************/

	/** システム管理者用ログイン画面のURL */
	public static final String ADMIN_LOGIN_URL = "admin/login";
	/** システム管理者用ログイン処理のPOSTURL */
	public static final String ADMIN_LOGIN_POST_REQUEST_URL = "/admin/spring_security_login";
	/** システム管理者用ログアウト処理のPOSTURL */
	public static final String ADMIN_LOGOUT_POST_REQUEST_URL = "/admin/logout";

}
