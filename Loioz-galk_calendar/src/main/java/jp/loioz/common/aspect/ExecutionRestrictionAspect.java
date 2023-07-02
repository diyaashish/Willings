package jp.loioz.common.aspect;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.aspect.annotation.DenyAccountType;
import jp.loioz.common.aspect.annotation.PermitAccountKengen;
import jp.loioz.common.aspect.annotation.PermitAccountType;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.exception.PermissionException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.LoiozArrayUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.UriUtils;

/**
 * 実行制限を行うアスペクトクラス
 */
@Aspect
@Component
public class ExecutionRestrictionAspect {

	private static final String USER_EXECUTED_NO_PERMISSION_PROC = " {}が実行権限のない処理を実行しました";

	/** ロガークラス */
	@Autowired
	Logger logger;

	/**
	 * &#064;PermitOnlyManagerアノテーションを付与した処理が<br>
	 * システム管理権限を持たないユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @throws Throwable
	 */
	@Before("@annotation(org.springframework.web.bind.annotation.RequestMapping)"
			+ " && (@annotation(jp.loioz.common.aspect.annotation.PermitOnlyManager)"
			+ " || @within(jp.loioz.common.aspect.annotation.PermitOnlyManager))")
	public void permitOnlyManager(JoinPoint jp) {
		// @PermitOnlyManagerが付与されているコントローラメソッドが対象
		// メソッドの実行者がシステム管理者でない場合はエラーとする
		if (!SessionUtils.isManager()) {
			throwPermissionException(jp, "システム管理者でないユーザ");
		}
	}

	/**
	 * &#064;DenyAccountTypeアノテーションを付与したメソッドが<br>
	 * 指定されたアカウント種別のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param methodAnnotation
	 * @throws Throwable
	 */
	@Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(methodAnnotation)")
	public void denyAccountTypeMethod(JoinPoint jp, DenyAccountType methodAnnotation) {
		// @DenyAccountTypeが付与されているコントローラメソッドが対象
		denyAccountType(jp, methodAnnotation);
	}

	/**
	 * &#064;DenyAccountTypeアノテーションを付与したクラスのメソッドが<br>
	 * 指定されたアカウント種別のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param classAnnotation
	 * @throws Throwable
	 */
	@Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) && @within(classAnnotation) && !@annotation(jp.loioz.common.aspect.annotation.DenyAccountType)")
	public void denyAccountTypeClass(JoinPoint jp, DenyAccountType classAnnotation) {
		// @DenyAccountTypeが付与されているコントローラクラスが対象
		denyAccountType(jp, classAnnotation);
	}

	/**
	 * 指定されたアカウント種別のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param annotation
	 * @throws PermissionException
	 */
	private void denyAccountType(JoinPoint jp, DenyAccountType annotation) {
		AccountType accountType = SessionUtils.getAccountType();

		// メソッドの実行者が指定されたアカウント種別の場合はエラーとする
		if (accountType == null || LoiozArrayUtils.contains(annotation.value(), accountType)) {
			throwPermissionException(jp, accountType.getVal());
		}
	}

	/**
	 * &#064;PermitAccountTypeアノテーションを付与したメソッドが<br>
	 * 指定されたアカウント種別以外のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param methodAnnotation
	 * @throws Throwable
	 */
	@Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(methodAnnotation)")
	public void permitAccountTypeMethod(JoinPoint jp, PermitAccountType methodAnnotation) {
		// @PermitAccountTypeが付与されているコントローラメソッドが対象
		permitAccountType(jp, methodAnnotation);
	}

	/**
	 * &#064;PermitAccountTypeアノテーションを付与したクラスのメソッドが<br>
	 * 指定されたアカウント種別以外のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param classAnnotation
	 * @throws Throwable
	 */
	@Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) && @within(classAnnotation) && !@annotation(jp.loioz.common.aspect.annotation.PermitAccountType)")
	public void permitAccountTypeClass(JoinPoint jp, PermitAccountType classAnnotation) {
		// @PermitAccountTypeが付与されているコントローラクラスが対象
		permitAccountType(jp, classAnnotation);
	}

	/**
	 * 指定されたアカウント種別以外のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param annotation
	 * @throws PermissionException
	 */
	private void permitAccountType(JoinPoint jp, PermitAccountType annotation) {
		AccountType accountType = SessionUtils.getAccountType();

		// メソッドの実行者が指定されたアカウント種別以外の場合はエラーとする
		if (accountType == null || !LoiozArrayUtils.contains(annotation.value(), accountType)) {
			throwPermissionException(jp, accountType.getVal());
		}
	}

	/**
	 * &#064;DenyAccountKengenアノテーションを付与したクラスもしくはメソッドが<br>
	 * 指定されたアカウント権限のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param annotation
	 * @throws PermissionException
	 */
	@Before("(@annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(annotation))"
			+ "|| (@annotation(org.springframework.web.bind.annotation.RequestMapping) && @within(annotation) && !@annotation(jp.loioz.common.aspect.annotation.DenyAccountKengen))")
	public void denyAccountKengen(JoinPoint jp, DenyAccountKengen annotation) {
		AccountKengen accountKengen = SessionUtils.getAccountKengen();

		// メソッドの実行者が指定されたアカウント種別の場合はエラーとする
		if (accountKengen == null || LoiozArrayUtils.contains(annotation.value(), accountKengen)) {
			throwPermissionException(jp, accountKengen.getVal());
		}
	}

	/**
	 * &#064;PermitAccountKengenアノテーションを付与したクラスもしくはメソッドが<br>
	 * 指定されたアカウント権限以外のユーザーに実行された場合はエラーにする。
	 *
	 * @param jp
	 * @param annotation
	 * @throws PermissionException
	 */
	@Before("@annotation(org.springframework.web.bind.annotation.RequestMapping)"
			+ "&& (@annotation(annotation) || (@within(annotation) && !@annotation(jp.loioz.common.aspect.annotation.PermitAccountKengen)))")
	public void permitAccountKengen(JoinPoint jp, PermitAccountKengen annotation) {
		AccountKengen accountKengen = SessionUtils.getAccountKengen();

		// メソッドの実行者が指定されたアカウント種別以外の場合はエラーとする
		if (accountKengen == null || !LoiozArrayUtils.contains(annotation.value(), accountKengen)) {
			throwPermissionException(jp, accountKengen.getVal());
		}
	}

	/**
	 * &#064;PermitAlsoDisabledPlanStatusUserアノテーションが付与されていない処理が<br>
	 * プラン契約状態が無効なユーザーに実行された場合はエラーとする。<br>
	 * （&#064;PermitAlsoDisabledPlanStatusUserアノテーションが付与された処理は、プラン契約状態が無効なユーザーでも実行可能とする。）
	 *
	 *<pre>
	 *※プラン画面用のコントローラーへのアクセスは対象外とする
	 *  （プラン画面はログインSessionがなくてもアクセス可能な状態としており、ここで行う共通の権限チェックは利用できなため、
	 *    画面側で独自に権限チェックを行うため。）
	 *</pre>
	 *
	 * @param jp
	 * @throws Throwable
	 */
	@Around("execution(public * jp.loioz.app.user.*..*Controller.*(..))"
			+ " && !execution(public * jp.loioz.app.user.*..PlanSettingController.*(..))"
			+ " && @annotation(org.springframework.web.bind.annotation.RequestMapping)"
			+ " && !@annotation(jp.loioz.common.aspect.annotation.PermitAlsoDisabledPlanStatusUser)")
	public Object nonPermitAlsoDisabledPlanStatusUserMethod(ProceedingJoinPoint pjp) throws Throwable {
		// @PermitAlsoDisabledPlanStatusUserが付与されていないコントローラメソッドが対象

		AccountType accountType = SessionUtils.getAccountType();

		if (accountType == null) {
			// 未ログイン状態で実行する処理のケース -> 判定処理は行わない
			
			// コントローラメソッドの実行し処理終了
			Object mv = pjp.proceed();
			return mv;
		}

		if (!SessionUtils.isOnlyPlanSettingAccessible()) {
			// 契約状態が無効なユーザー（プラン設定画面以外利用不可のユーザー）ではない場合 -> 判定処理は行わない
			
			// コントローラメソッドの実行し処理終了
			Object mv = pjp.proceed();
			return mv;
		}
		
		// 契約状態が無効なユーザー（プラン設定画面以外利用不可のユーザー）の場合
		
		// 未ログイン状態でアクセス可能なパスへのアクセスかどうか
		URI requestUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
		boolean isNoLoginAccessibleUri = UriUtils.checkUriIsAccessibleNotLoggedInUser(requestUri);
		
		if (isNoLoginAccessibleUri) {
			// -> ログアウト状態にし、そのままパスへアクセスさせる
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			SessionUtils.logout(request.getSession());
			// リダイレクトでアクセスさせる
			//（上記でログアウトを行っているため、そのままコントローラメソッドを実行しても、返却されるHTMLのcsrfトークンを保持するSessionがない状態となる(HTML内の値とサーバー側の値で不整合が起きる)ため、
			// リダレクトすることで、HTMLに埋め込まれるcsrfトークンの値と、サーバー側で管理するcsrfトークンの値を一致させるようにする。）
			ModelAndView mv = new ModelAndView("redirect:" + requestUri.getPath());
			return mv;
		} else {
			// プラン設定画面以外の、ログインしていないとアクセス不可能な画面へのアクセス -> 権限エラーとする
			throwPermissionException(pjp, accountType.getVal());
			return null;
		}
	}
	
	/**
	 * 権限エラーをログ出力して、PermissionExceptionを発生させる
	 *
	 * @param jp
	 * @param messageParams
	 * @throws PermissionException
	 */
	private void throwPermissionException(JoinPoint jp, Object... messageParams) {
		Signature sig = jp.getSignature();
		String classAndMethodName = sig.getName() + " - " + sig.getDeclaringType().getSimpleName();

		logger.warn(classAndMethodName + USER_EXECUTED_NO_PERMISSION_PROC, messageParams);

		throw new PermissionException();
	}

}
