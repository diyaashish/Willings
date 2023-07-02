
package jp.loioz.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import jp.loioz.app.global.common.exception.GlobalAuthException;
import jp.loioz.app.user.planSetting.exception.PlanSettingAuthException;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;

/**
 * ログの出力を行うアスペクトクラス
 */
@Aspect
@Component
public class LogAspect {

	/** DBサーバーURL */
	@Value("${app.devMode}")
	private int devMode;

	/** ロガークラス */
	@Autowired
	Logger logger;

	/**
	 * 主要メソッドの開始／終了ログを出力する差し込み処理
	 *
	 * @param pjp 対象メソッド情報
	 * @return 対象メソッドの値
	 * @throws Throwable
	 */
	@Around("execution(public * jp.loioz.app.admin.*..*Controller.*(..)) || within(jp.loioz.app.admin.*..*Service)"
			+ " || execution(public * jp.loioz.app.user.*..*Controller.*(..)) || within(jp.loioz.app.user.*..*Service)"
			+ " || execution(public * jp.loioz.app.common.mvc.*..*Controller.*(..)) || within(jp.loioz.app.common.mvc.*..*Service)"
			+ " || within(jp.loioz.app.common.service.*..*)"
			+ " || within(jp.loioz.dao.*)")
	public Object infoLogOutputAroundMethod(ProceedingJoinPoint pjp) throws Throwable {

		// 実行されたメソッドの情報（クラス名・メソッド名・引数）を取得
		Signature sig = pjp.getSignature();
		String classAndMethodName = sig.getName() + " - " + sig.getDeclaringType().getSimpleName();

		// 引数名
		String[] argNames = ((CodeSignature) sig).getParameterNames();
		// 引数値
		Object[] argValues = pjp.getArgs();

		if (devMode == 1) {
			// 開発モードでの動作

			// メソッドの引数ログ
			String arguments = "\n★★★==========================\n";
			for (int i = 0; i < argNames.length; i++) {
				arguments += argNames[i] + "=" + argValues[i];
				arguments += "\n";
			}
			arguments += "==========================>★★★";

			// メソッド開始ログ
			logger.info("[START] " + classAndMethodName + arguments);

		} else {
			// 本番環境モードでの動作

			// メソッドの引数ログ
			String arguments = "";
			int lastIdx = argNames.length - 1;
			for (int i = 0; i < argNames.length; i++) {
				arguments += argNames[i] + "=" + argValues[i];

				if (i != lastIdx) {
					arguments += ", ";
				}
			}

			// メソッド開始ログ
			logger.info("[START] " + classAndMethodName + " [arguments] " + arguments);
		}

		// コントローラメソッドの実行
		Object mv = pjp.proceed();

		// メソッド終了ログ
		logger.info("[ END ] " + classAndMethodName);

		return mv;
	}

	/**
	 * appフォルダ配下の全てのクラスで例外がスローされた場合の差し込み処理
	 *
	 * @param jp 対象メソッド情報
	 * @param e エラー情報
	 */
	@AfterThrowing(pointcut = "within(jp.loioz.common.filter.*) || within(jp.loioz.common.filter.*..*)"
			+ " || within(jp.loioz.common.handlerInterceptor.*) || within(jp.loioz.common.handlerInterceptor.*..*)"
			+ " || within(jp.loioz.common.handler.*) || within(jp.loioz.common.handler.*..*)"
			+ " || execution(public * jp.loioz.app.*..*.*(..))", throwing = "e")
	public void afterThrowing(JoinPoint jp, Throwable e) {

		if (e instanceof AppException || e instanceof UsernameNotFoundException || e instanceof PlanSettingAuthException || e instanceof GlobalAuthException) {
			// AppExceptionは意図してスローを行っている（Controllerクラスなどで例外処理を行う）ためエラーログは出力しない
			// UsernameNotFoundExceptionはログイン時のID、パスワード、企業・アカウントのステータスチェック時に発生する例外（意図してスローしている例外）のためエラーログは出力しない
			// PlanSettingAuthExceptionはプラン画面のアクセス時の認証エラー用の例外で、意図してスローしているためエラーログは出力しない
			// GlobalAuthExceptionはグローバルユーザー画面のアクセス時の認証エラー用の例外で、意図してスローしているためエラーログは出力しない
			return;
		}

		// 実行されたメソッドの情報（クラス名・メソッド名・引数）を取得
		Signature sig = jp.getSignature();
		String classAndMethodName = sig.getName() + " - " + sig.getDeclaringType().getSimpleName();

		// 引数名
		String[] argNames = ((CodeSignature) sig).getParameterNames();
		// 引数値
		Object[] argValues = jp.getArgs();

		// メソッドの引数ログ
		String arguments = "";
		int lastIdx = argNames.length - 1;
		for (int i = 0; i < argNames.length; i++) {
			arguments += argNames[i] + "=" + argValues[i];

			if (i != lastIdx) {
				arguments += ", ";
			}
		}
		
		if (e instanceof DataNotFoundException) {
			// DataNotFoundExceptionはID検索などでデータが見つからなかったときに独自にスローしている例外のため、warnログの出力とする
			// warnログ
			logger.warn(classAndMethodName + " [arguments]" + arguments, e);
		} else {
			// errorログ
			logger.error(classAndMethodName + " [arguments]" + arguments, e);
		}
	}

}
