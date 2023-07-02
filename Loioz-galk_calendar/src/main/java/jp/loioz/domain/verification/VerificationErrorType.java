package jp.loioz.domain.verification;

/**
 * 認証エラー種別
 */
public enum VerificationErrorType {
	/** 認証情報が取得できない */
	NOT_EXISTS,
	/** 認証キーが有効期限切れ */
	EXPIRED,
	/** 認証完了済み */
	COMPLETED;
}