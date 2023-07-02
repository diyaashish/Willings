package jp.loioz.common.command.exception;

import java.util.List;

import lombok.Getter;

/**
 * コマンド実行がタイムアウトした場合の例外
 */
public class CommandTimeoutException extends CommandException {

	/** シリアルバージョンID. */
	private static final long serialVersionUID = 1L;

	/** タイムアウト時間(ミリ秒) */
	@Getter
	final private long timeoutMillis;

	/**
	 * コンストラクタ
	 *
	 * @param command コマンド
	 * @param timeoutMillis タイムアウト時間(ミリ秒)
	 * @param cause 発生原因
	 */
	public CommandTimeoutException(List<String> command, long timeoutMillis, Throwable cause) {
		super(command, cause);
		this.timeoutMillis = timeoutMillis;
	}

	/**
	 * タイムアウト時間(秒)を取得する
	 *
	 * @return タイムアウト時間
	 */
	public double getTimeoutSeconds() {
		return (double) timeoutMillis / 1000;
	}

}
