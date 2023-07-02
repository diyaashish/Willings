package jp.loioz.common.command.exception;

import java.util.List;

import lombok.Getter;

/**
 * コマンド実行時の終了コードが正常でない場合の例外
 */
public class UnexpectedExitCodeException extends CommandException {

	/** シリアルバージョンID. */
	private static final long serialVersionUID = 1L;

	/** 終了コード */
	@Getter
	final private int exitCode;

	/**
	 * コンストラクタ
	 *
	 * @param command コマンド
	 * @param exitCode 終了コード
	 */
	public UnexpectedExitCodeException(List<String> command, int exitCode) {
		super(command);
		this.exitCode = exitCode;
	}

}
