package jp.loioz.common.exception;

import jp.loioz.common.constant.MessageEnum;

/**
 * ServiceクラスからControllerクラスなどの上位クラスにエラーメッセージとException情報を連携するためのクラス。
 */
public class AppException extends Exception {

	/** シリアルバージョンID. */
	private static final long serialVersionUID = 1L;

	/** エラータイプ */
	private MessageEnum errorType;

	/** エラーメッセージ引数 */
	private String[] messageArgs;

	// ---------------------------------------------------------
	// コンストラクタ
	// ---------------------------------------------------------
	/**
	 * コンストラクタ
	 *
	 * @param errorType エラータイプのEnum
	 * @param cause 発生元のExceptionインスタンス
	 */
	public AppException(MessageEnum errorType, Throwable cause) {
		super(cause);
		this.errorType = errorType;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param errorType エラータイプのEnum
	 * @param messageArgs エラーメッセージのプレースホルダ引数
	 * @param cause 発生元のExceptionインスタンス
	 */
	public AppException(MessageEnum errorType, Throwable cause, String... messageArgs) {
		super(cause);
		this.errorType = errorType;
		this.messageArgs = messageArgs;
	}

	// ---------------------------------------------------------
	// publicメソッド
	// ---------------------------------------------------------
	/**
	 * Exceptionのエラーメッセージキーを取得
	 *
	 * @return メッセージプロパティのキー
	 */
	public String getMessageKey() {

		return errorType.getMessageKey();
	}

	/**
	 * エラータイプを返却する
	 *
	 * @return
	 */
	public MessageEnum getErrorType() {
		return this.errorType;
	}

	/**
	 * メッセージ引数を返却する
	 * 
	 * @return
	 */
	public String[] getMessageArgs() {
		return this.messageArgs;
	}

}
