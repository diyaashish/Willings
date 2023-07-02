package jp.loioz.common.async;

/**
 * スレッドプール種別
 */
public enum ThreadPoolGroup {
	/** デフォルト */
	DEFAULT,
	/** メール送信処理用 */
	MAIL_SEND,
}
