package jp.loioz.app.global.common.exception;

import jp.loioz.common.constant.MessageEnum;

/**
 * グローバルアクセスの認証エラー
 */
public class GlobalAuthException extends RuntimeException {
	
	/** シリアルバージョンID. */
	private static final long serialVersionUID = 1L;

	/** エラータイプ */
	private MessageEnum errorType;
	
	
	// ---------------------------------------------------------
	// コンストラクタ
	// ---------------------------------------------------------
	public GlobalAuthException(MessageEnum errorType) {
		super();
		this.errorType = errorType;
	}

	public GlobalAuthException(MessageEnum errorType, String message, Throwable cause) {
		super(message, cause);
		this.errorType = errorType;
	}

	public GlobalAuthException(MessageEnum errorType, String message) {
		super(message);
		this.errorType = errorType;
	}

	public GlobalAuthException(MessageEnum errorType, Throwable cause) {
		super(cause);
		this.errorType = errorType;
	}
	
	// ---------------------------------------------------------
	// publicメソッド
	// ---------------------------------------------------------
	/**
	 * エラータイプを返却する
	 *
	 * @return
	 */
	public MessageEnum getErrorType() {
		return this.errorType;
	}
	
}
