package jp.loioz.common.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import jp.loioz.common.command.exception.CommandException;
import jp.loioz.common.command.exception.CommandTimeoutException;
import jp.loioz.common.command.exception.UnexpectedExitCodeException;

/**
 * コマンドを実行するクラス
 */
public class CommandExecutor {

	/** コマンド */
	private List<String> command;

	/** タイムアウト時間 */
	private long timeout = 60;

	/** タイムアウト時間単位 */
	private TimeUnit timeoutUnit = TimeUnit.SECONDS;

	/** 終了コード検証ルール */
	private IntPredicate exitCodeValidator = (exitCode -> {
		return exitCode == 0;
	});

	/** コマンド標準出力コールバック */
	private Consumer<String> stdOutCallback = (line -> {
	});

	/**
	 * コンストラクタ
	 *
	 * @param command コマンド
	 */
	private CommandExecutor(List<String> command) {
		this.command = command;
	}

	/**
	 * コマンドを指定する
	 *
	 * @param command コマンド
	 * @return コマンド実行オブジェクト
	 */
	public static CommandExecutor command(String... command) {
		return new CommandExecutor(Arrays.asList(command));
	}

	/**
	 * コマンドを指定する
	 *
	 * @param command コマンド
	 * @return コマンド実行オブジェクト
	 */
	public static CommandExecutor command(List<String> command) {
		return new CommandExecutor(command);
	}

	/**
	 * デフォルトの設定でコマンドを実行する
	 *
	 * @see
	 *
	 * @param command コマンド
	 * @return 終了コード
	 * @throws CommandException コマンド実行に失敗した場合
	 * @throws CommandTimeoutException コマンド実行がタイムアウトした場合
	 * @throws UnexpectedExitCodeException 終了コードが異常終了の場合
	 */
	public static int execute(String... command)
			throws CommandException, CommandTimeoutException, UnexpectedExitCodeException {
		return execute(Arrays.asList(command));
	}

	/**
	 * デフォルトの設定でコマンドを実行する
	 *
	 * @param command コマンド
	 * @return 終了コード
	 * @throws CommandException コマンド実行に失敗した場合
	 * @throws CommandTimeoutException コマンド実行がタイムアウトした場合
	 * @throws UnexpectedExitCodeException 終了コードが異常終了の場合
	 */
	public static int execute(List<String> command)
			throws CommandException, CommandTimeoutException, UnexpectedExitCodeException {
		return CommandExecutor
				.command(command)
				.execute();
	}

	/**
	 * タイムアウト時間を秒で設定する
	 *
	 * @param timeout タイムアウト時間(秒)
	 * @return このオブジェクト
	 */
	public CommandExecutor timeoutSeconds(long timeout) {
		this.timeout = timeout;
		this.timeoutUnit = TimeUnit.SECONDS;
		return this;
	}

	/**
	 * タイムアウト時間を設定する
	 *
	 * @param timeout タイムアウト時間
	 * @param timeUnit タイムアウト時間単位
	 * @return このオブジェクト
	 */
	public CommandExecutor timeout(long timeout, TimeUnit timeUnit) {
		this.timeout = timeout;
		this.timeoutUnit = timeUnit;
		return this;
	}

	/**
	 * 終了コード検証ルールを設定する
	 *
	 * @param validator 検証ルール<br>
	 *            終了コードが想定通りである場合にtrueを返す
	 * @return このオブジェクト
	 */
	public CommandExecutor exitCodeValidator(IntPredicate validator) {
		this.exitCodeValidator = validator;
		return this;
	}

	/**
	 * コマンド標準出力コールバックを設定する
	 *
	 * @param callback コールバック
	 * @return このオブジェクト
	 */
	public CommandExecutor stdOutCallback(Consumer<String> callback) {
		this.stdOutCallback = callback;
		return this;
	}

	/**
	 * コマンドを実行する
	 *
	 * @return 終了コード
	 * @throws CommandException コマンド実行に失敗した場合
	 * @throws CommandTimeoutException コマンド実行がタイムアウトした場合
	 * @throws UnexpectedExitCodeException 終了コードが異常終了の場合
	 */
	public int execute() throws CommandException, CommandTimeoutException, UnexpectedExitCodeException {
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process process;

		// 外部プロセス実行
		try {
			process = pb.start();
		} catch (Exception e) {
			throw new CommandException(command, e);
		}

		try {
			CompletableFuture.runAsync(() -> {
				// 外部プロセスの標準出力を受け取る
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(
								process.getInputStream()))) {
					String line = null;
					while ((line = br.readLine()) != null) {
						stdOutCallback.accept(line);
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			})
			.get(timeout, timeoutUnit);
			process.waitFor();

		} catch (TimeoutException e) {
			// タイムアウトした場合
			process.destroy();
			throw new CommandTimeoutException(command, timeoutUnit.toMillis(timeout), e);

		} catch (Exception e) {
			// その他実行失敗
			process.destroy();
			throw new CommandException(command, e);
		}

		int exitCode = process.exitValue();

		// 終了コードチェック
		if (!exitCodeValidator.test(exitCode)) {
			throw new UnexpectedExitCodeException(command, exitCode);
		}

		return exitCode;
	}
}
