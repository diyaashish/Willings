package jp.loioz.common.log;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * ログの出力を行うクラス
 */
@Slf4j
@Component
public class Logger {

	/** TRACEログに付与するプレフィックス */
	private static final String PREFIX_TRACE_LOG = "===[TRACE]=== ";
	/** DEBUGログに付与するプレフィックス */
	private static final String PREFIX_DEBUG_LOG = "===[DEBUG]=== ";
	/** INFOログに付与するプレフィックス */
	private static final String PREFIX_INFO_LOG = "===[INFO]=== ";
	/** WARNログに付与するプレフィックス */
	private static final String PREFIX_WARN_LOG = "*******[WARN]******* ";
	/** ERRORログに付与するプレフィックス */
	private static final String PREFIX_ERROR_LOG = "*******[ERROR]******* ";

	/**
	 * TRACEレベルのログ出力が有効かどうかを調べる
	 */
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	};

	/**
	 * 指定の文字列をログに出力する
	 *
	 * @param msg the message string to be logged
	 */
	public void trace(String msg) {
		log.trace(PREFIX_TRACE_LOG + msg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg    the argument
	 */
	public void trace(String format, Object arg) {
		log.trace(PREFIX_TRACE_LOG + format, arg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg1   the first argument
	 * @param arg2   the second argument
	 */
	public void trace(String format, Object arg1, Object arg2) {
		log.trace(PREFIX_TRACE_LOG + format, arg1, arg2);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format    the format string
	 * @param arguments a list of 3 or more arguments
	 */
	public void trace(String format, Object... arguments) {
		log.trace(PREFIX_TRACE_LOG + format, arguments);
	};

	/**
	 * 指定の文字列とエラー情報をログに出力する
	 *
	 * @param msg the message accompanying the exception
	 * @param t   the exception (throwable) to log
	 */
	public void trace(String msg, Throwable t) {
		log.trace(PREFIX_TRACE_LOG + msg, t);
	};

	/**
	 * DEBUGレベルのログ出力が有効かどうかを調べる
	 */
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	};

	/**
	 * 指定の文字列をログに出力する
	 *
	 * @param msg the message string to be logged
	 */
	public void debug(String msg) {
		log.debug(PREFIX_DEBUG_LOG + msg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg    the argument
	 */
	public void debug(String format, Object arg) {
		log.debug(PREFIX_DEBUG_LOG + format, arg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg1   the first argument
	 * @param arg2   the second argument
	 */
	public void debug(String format, Object arg1, Object arg2) {
		log.debug(PREFIX_DEBUG_LOG + format, arg1, arg2);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format    the format string
	 * @param arguments a list of 3 or more arguments
	 */
	public void debug(String format, Object... arguments) {
		log.debug(PREFIX_DEBUG_LOG + format, arguments);
	};

	/**
	 * 指定の文字列とエラー情報をログに出力する
	 *
	 * @param msg the message accompanying the exception
	 * @param t   the exception (throwable) to log
	 */
	public void debug(String msg, Throwable t) {
		log.debug(PREFIX_DEBUG_LOG + msg, t);
	};

	/**
	 * INFOレベルのログ出力が有効かどうかを調べる
	 */
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	};

	/**
	 * 指定の文字列をログに出力する
	 *
	 * @param msg the message string to be logged
	 */
	public void info(String msg) {
		log.info(PREFIX_INFO_LOG + msg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg    the argument
	 */
	public void info(String format, Object arg) {
		log.info(PREFIX_INFO_LOG + format, arg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg1   the first argument
	 * @param arg2   the second argument
	 */
	public void info(String format, Object arg1, Object arg2) {
		log.info(PREFIX_INFO_LOG + format, arg1, arg2);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format    the format string
	 * @param arguments a list of 3 or more arguments
	 */
	public void info(String format, Object... arguments) {
		log.info(PREFIX_INFO_LOG + format, arguments);
	};

	/**
	 * 指定の文字列とエラー情報をログに出力する
	 *
	 * @param msg the message accompanying the exception
	 * @param t   the exception (throwable) to log
	 */
	public void info(String msg, Throwable t) {
		log.info(PREFIX_INFO_LOG + msg, t);
	};

	/**
	 * WARNレベルのログ出力が有効かどうかを調べる
	 */
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	};

	/**
	 * 指定の文字列をログに出力する
	 *
	 * @param msg the message string to be logged
	 */
	public void warn(String msg) {
		log.warn(PREFIX_WARN_LOG + msg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg    the argument
	 */
	public void warn(String format, Object arg) {
		log.warn(PREFIX_WARN_LOG + format, arg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format    the format string
	 * @param arguments a list of 3 or more arguments
	 */
	public void warn(String format, Object... arguments) {
		log.warn(PREFIX_WARN_LOG + format, arguments);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg1   the first argument
	 * @param arg2   the second argument
	 */
	public void warn(String format, Object arg1, Object arg2) {
		log.warn(PREFIX_WARN_LOG + format, arg1, arg2);
	};

	/**
	 * 指定の文字列とエラー情報をログに出力する
	 *
	 * @param msg the message accompanying the exception
	 * @param t   the exception (throwable) to log
	 */
	public void warn(String msg, Throwable t) {
		log.warn(PREFIX_WARN_LOG + msg, t);
	};

	/**
	 * ERRORレベルのログ出力が有効かどうかを調べる
	 */
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	};

	/**
	 * 指定の文字列をログに出力する
	 *
	 * @param msg the message string to be logged
	 */
	public void error(String msg) {
		log.error(PREFIX_ERROR_LOG + msg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg    the argument
	 */
	public void error(String format, Object arg) {
		log.error(PREFIX_ERROR_LOG + format, arg);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format the format string
	 * @param arg1   the first argument
	 * @param arg2   the second argument
	 */
	public void error(String format, Object arg1, Object arg2) {
		log.error(PREFIX_ERROR_LOG + format, arg1, arg2);
	};

	/**
	 * 指定のフォーマットに引数を埋め込んでログを出力する
	 *
	 * @param format    the format string
	 * @param arguments a list of 3 or more arguments
	 */
	public void error(String format, Object... arguments) {
		log.error(PREFIX_ERROR_LOG + format, arguments);
	};

	/**
	 * 指定の文字列とエラー情報をログに出力する
	 *
	 * @param msg the message accompanying the exception
	 * @param t   the exception (throwable) to log
	 */
	public void error(String msg, Throwable t) {
		log.error(PREFIX_ERROR_LOG + msg, t);
	};
}
