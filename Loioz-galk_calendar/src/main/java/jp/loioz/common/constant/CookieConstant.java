package jp.loioz.common.constant;

public class CookieConstant {

	/** Cookie暗号化の暗号化キー */
	public static final String COOKIE_ENCRYPTION_KEY = "L0O1I0O6Z";
	/** Cookie暗号化のSalt */
	public static final String COOKIE_ENCRYPTION_SALT = "60100202";

	/** Cookieに保持する有効期限 = 30日 (日数 × 24時間 × 60分 × 60秒) */
	public static final int COOKIE_EXPRIRATION_TIME = 30 * 24 * 60 * 60;

	/** Cookieに保持する有効期限： 1日 (24時間 × 60分 × 60秒) */
	public static final int COOKIE_EXPRIRATION_ONE_DAYS = 1 * 24 * 60 * 60;

	/** Cookieに保持する有効期限： 10分 (10分 × 60秒) */
	public static final int COOKIE_EXPRIRATION_TEN_MIN = 10 * 60;

	// =================================================================
	// CookieName
	// =================================================================

	/** Cookie名 (グローバルダウンロードトークン) */
	public static final String COOKIE_NAME_OF_GLOBAL_DOWNLOAD_AUTH_ID = "GDAUTHID";

	/** Cookie名（セッションID） */
	public static final String COOKIE_NAME_OF_SESSION_ID = "JSESSIONID";
	/** Cookie名（サブドメイン） */
	public static final String COOKIE_NAME_OF_SUBDOMAIN = "SUBDOMAIN";
	/** Cookie名 プラン画面認証 */
	public static final String COOKIE_NAME_OF_PLAN_SETTING_ACCESS_AUTH_KEY = "PLAN_SETTING_ACCESS_AUTH_KEY";

	/** Cookie アカウントID情報 */
	public static final String COOKIE_NAME_OF_LONGIN_ACCOUNT_ID = "USERID";

	/** Cookie スケジュール情報 */
	public static final String COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_SELECTED_USERS = "SCHEDULEOPTIONAL_SELECTEDUSERS";
	public static final String COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_SELECTED_HOLIDAY = "SCHEDULEOPTIONAL_SELECTEDHOLIDAY";
	public static final String COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_TASKLIST_OPENED = "SCHEDULEOPTIONAL_TASKLISTOPENED";
}