package jp.loioz.app.global.common.exception;

/**
 * 認証タイムアウトエラー
 */
public class GlobalTokenTimeOutException extends RuntimeException {
	/** シリアルバージョンID. */
	private static final long serialVersionUID = 1L;

	public GlobalTokenTimeOutException() {
		super();
	}

	public GlobalTokenTimeOutException(String message, Throwable cause) {
		super(message, cause);
	}

	public GlobalTokenTimeOutException(String message) {
		super(message);
	}

	public GlobalTokenTimeOutException(Throwable cause) {
		super(cause);
	}
}
