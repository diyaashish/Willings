package jp.loioz.common.message;

import jp.loioz.common.constant.CommonConstant.MessageLevel;
import lombok.Data;

/**
 * メッセージの保持クラス
 */
@Data
public class MessageHolder {

	/** INFOメッセージ. */
	private String infoMsg;
	/** WARNINGメッセージ. */
	private String warningMsg;
	/** ERRORメッセージ. */
	private String errorMsg;

	// =========================================================================
	// publicメソッド
	// =========================================================================
	/**
	 * 指定レベルのメッセージで初期化したメッセージ情報を取得する
	 *
	 * @param level メッセージレベル
	 * @param msg メッセージ
	 * @return メッセージ情報
	 */
	public static MessageHolder ofLevel(MessageLevel level, String msg) {

		switch(level) {
		case INFO:
			return MessageHolder.ofInfo(msg);
		case WARN:
			return MessageHolder.ofWarning(msg);
		case ERROR:
			return MessageHolder.ofError(msg);
		default:
			return MessageHolder.ofInfo(msg);
		}
	}

	/**
	 * INFOメッセージで初期化したメッセージ情報を取得する
	 *
	 * @param infoMsg INFOメッセージ
	 * @return メッセージ情報
	 */
	public static MessageHolder ofInfo(String infoMsg) {
		MessageHolder messageHolder = new MessageHolder();
		messageHolder.setInfoMsg(infoMsg);
		return messageHolder;
	}

	/**
	 * WARNINGメッセージで初期化したメッセージ情報を取得する
	 *
	 * @param warningMsg WARNINGメッセージ
	 * @return メッセージ情報
	 */
	public static MessageHolder ofWarning(String warningMsg) {
		MessageHolder messageHolder = new MessageHolder();
		messageHolder.setWarningMsg(warningMsg);
		return messageHolder;
	}

	/**
	 * ERRORメッセージで初期化したメッセージ情報を取得する
	 *
	 * @param errorMsg ERRORメッセージ
	 * @return メッセージ情報
	 */
	public static MessageHolder ofError(String errorMsg) {
		MessageHolder messageHolder = new MessageHolder();
		messageHolder.setErrorMsg(errorMsg);
		return messageHolder;
	}

	/**
	 * メッセージが設定されているかどうかを調べる
	 *
	 * @return true:設定されている、false:設定されていない
	 */
	public boolean hasAnyMessage() {

		if (this.hasInfo() || this.hasWarning() || this.hasError()) {
			return true;
		}

		return false;
	}

	/**
	 * INFOメッセージが設定されているかどうかを調べる
	 *
	 * @return true:設定されている、false:設定されていない
	 */
	public boolean hasInfo() {
		return infoMsg != null;
	}

	/**
	 * WARNINGメッセージが設定されているかどうかを調べる
	 *
	 * @return true:設定されている、false:設定されていない
	 */
	public boolean hasWarning() {
		return warningMsg != null;
	}

	/**
	 * ERRORメッセージが設定されているかどうかを調べる
	 *
	 * @return true:設定されている、false:設定されていない
	 */
	public boolean hasError() {
		return errorMsg != null;
	}

}
