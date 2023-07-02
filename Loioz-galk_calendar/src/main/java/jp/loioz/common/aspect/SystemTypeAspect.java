package jp.loioz.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.common.constant.CommonConstant.SystemType;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * システムタイプによるアクセスの制限を行うアスペクトクラス
 */
@Aspect
@Component
public class SystemTypeAspect {

	/**
	 * userフォルダ配下の全てのコントローラのメソッドへの差し込み処理
	 *
	 * @param pjp 対象メソッド情報
	 * @throws Throwable
	 */
	@Around("execution(public * jp.loioz.app.user.*..*Controller.*(..))")
	public Object beforeVisionalistControllerMethod(ProceedingJoinPoint pjp) throws Throwable {

		SystemType systemType = SessionUtils.getSystemType();

		if (systemType != null && systemType != SystemType.USER) {
			// システム種別がuserではない場合

			return this.errorPage();
		}

		// コントローラメソッドの実行
		return pjp.proceed();
	}

	/**
	 * adminフォルダ配下の全てのコントローラのメソッドへの差し込み処理
	 *
	 * @param pjp 対象メソッド情報
	 * @throws Throwable
	 */
	@Around("execution(public * jp.loioz.app.admin.*..*Controller.*(..))")
	public Object beforeAdminControllerMethod(ProceedingJoinPoint pjp) throws Throwable {

		SystemType systemType = SessionUtils.getSystemType();

		if (systemType != null && systemType != SystemType.ADMIN) {
			// システム種別がadminではない場合

			return this.errorPage();
		}

		// コントローラメソッドの実行
		return pjp.proceed();
	}

	/**
	 * エラー画面を取得する
	 *
	 * @return エラー画面
	 */
	private ModelAndView errorPage() {
		return ModelAndViewUtils.getCommonErrorPage();
	}
}
