package jp.loioz.common.command.exception;

import java.util.List;
import java.util.stream.Collectors;

import jp.loioz.common.constant.CommonConstant;
import lombok.Getter;

/**
 * コマンド実行時の例外
 */
public class CommandException extends Exception {

	/** シリアルバージョンID. */
	private static final long serialVersionUID = 1L;

	/** コマンド */
	@Getter
	private final List<String> command;

	/**
	 * コンストラクタ
	 *
	 * @param command コマンド
	 */
	public CommandException(List<String> command) {
		this.command = command;
	}

	/**
	 * コンストラクタ
	 *
	 * @param command コマンド
	 * @param cause 発生原因
	 */
	public CommandException(List<String> command, Throwable cause) {
		super(cause);
		this.command = command;
	}

	/**
	 * コマンド文字列を取得する
	 *
	 * @return コマンド文字列
	 */
	public String getCommandAsString() {
		return command.stream().collect(Collectors.joining(CommonConstant.SPACE));
	}

}
