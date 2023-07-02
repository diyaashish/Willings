package jp.loioz.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.domain.UriService;

/**
 * サブドメイン付きアクセスの制限を行うアスペクトクラス
 */
@Aspect
@Component
public class SubDomainAspect {

	/** URIサービスクラス */
	@Autowired
	UriService uriService;

	/** ロガークラス */
	@Autowired
	Logger logger;

	/**
	 * &#064;DenySubDomainアノテーションを付与した処理が<br>
	 * サブドメイン付きでアクセスされた場合にエラーにする
	 *
	 * @param pjp 対象メソッド情報
	 * @return 対象メソッドの値
	 * @throws Throwable
	 */
	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)"
			+ " && (@annotation(jp.loioz.common.aspect.annotation.DenySubDomain)"
			+ " || @within(jp.loioz.common.aspect.annotation.DenySubDomain))")
	public Object denySubDomain(ProceedingJoinPoint pjp) throws Throwable {
		// @DenySubDomainが付与されていて戻り値がModelAndViewのコントローラメソッドが対象

		// サブドメイン付きでアクセスされているか判定
		if (uriService.isSubDomainAccess()) {
			// サブドメイン付きの場合はエラーページに遷移
			logger.info(" [Access Denied] {}",
					ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
			return errorPage();
		}

		// コントローラメソッドの実行
		return pjp.proceed();
	}

	/**
	 * &#064;RequireSubDomainアノテーションを付与した処理が<br>
	 * サブドメインなしでアクセスされた場合にエラーにする
	 *
	 * @param pjp 対象メソッド情報
	 * @return 対象メソッドの値
	 * @throws Throwable
	 */
	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)"
			+ " && (@annotation(jp.loioz.common.aspect.annotation.RequireSubDomain)"
			+ " || @within(jp.loioz.common.aspect.annotation.RequireSubDomain))")
	public Object requireSubDomain(ProceedingJoinPoint pjp) throws Throwable {
		// @RequireSubDomainが付与されていて戻り値がModelAndViewのコントローラメソッドが対象

		// サブドメイン付きでアクセスされているか判定
		if (!uriService.isSubDomainAccess()) {
			// サブドメインなしの場合はエラーページに遷移
			logger.info(" [Access Denied] {}",
					ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
			return errorPage();
		}

		// コントローラメソッドの実行
		return pjp.proceed();
	}

	/**
	 * userフォルダ配下の全てのコントローラのリクエストを受付けるメソッドへの差し込み処理。<br>
	 * リクエスト情報のサブドメインが自身のテナントのサブドメインと一致しない（他のテナントのサブドメインを利用した不正アクセスの）場合はエラーとする。
	 *
	 * @param pjp 対象メソッド情報
	 * @throws Throwable
	 */
	@Around("execution(public * jp.loioz.app.user.*..*Controller.*(..))"
			+ " && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object cantAccessOtherTenantSubDomain (ProceedingJoinPoint pjp) throws Throwable {

		// セッションが保持するサブドメイン（ログイン時に設定）
		String myTenantSubDomain = SessionUtils.getSubDomain();
		// 現在のリクエストURLのサブドメイン
		String urlSubDomain = uriService.getSubDomainName();
		
		if (StringUtils.isEmpty(myTenantSubDomain) || StringUtils.isEmpty(urlSubDomain)) {
			// Sessionのサブドメインが空（未ログイン）の場合や、リクエストのサブドメインが空（未ログインでもアクセス可能な画面へのアクセス）の場合は
			// サブドメインによるアクセス制御チェックは行わない。
			
			// コントローラメソッドの実行
			return pjp.proceed();
		}
		
		if (urlSubDomain.equals(PlanConstant.PLAN_SETTING_SUB_DOMAIN)) {
			// リクエストがプラン設定画面用のサブドメインの場合はアクセスを許可
			// （プラン設定画面はすべてのユーザーで共通のサブドメインを利用するため、サブドメインチェックは行わない）
			
			// コントローラメソッドの実行
			return pjp.proceed();
		}
		
		if (!myTenantSubDomain.equals(urlSubDomain)) {
			// 自分のサブドメイン以外のサブドメインでのリクエストの場合はアクセス不可とする。
			
			return this.errorPage();
		}
		
		// コントローラメソッドの実行
		return pjp.proceed();
	}
	
	/**
	 * user/planSettingフォルダを除くuserフォルダ配下の全てのコントローラのリクエストを受付けるメソッドへの差し込み処理。<br>
	 * プラン設定画面用のサブドメインでプラン設定系以外の画面にアクセスした場合はエラーとする。
	 *
	 * @param pjp 対象メソッド情報
	 * @throws Throwable
	 */
	@Around("execution(public * jp.loioz.app.user.*..*Controller.*(..))"
			+ " && !execution(public * jp.loioz.app.user.planSetting.*..*Controller.*(..)))"
			+ " && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object cantAccessUsingPlanSettingSubDomain (ProceedingJoinPoint pjp) throws Throwable {

		// 現在のリクエストURLのサブドメイン
		String urlSubDomain = uriService.getSubDomainName();
		
		if (urlSubDomain.equals(PlanConstant.PLAN_SETTING_SUB_DOMAIN)) {
			// プラン設定系以外の画面に対して、プラン設定画面用のサブドメインでアクセスした場合はエラーとする
			
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
