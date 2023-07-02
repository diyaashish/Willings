package jp.loioz.config;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jp.loioz.app.common.api.box.constants.BoxConstants;
import jp.loioz.app.common.api.dropbox.constants.DropboxConstants;
import jp.loioz.app.common.api.google.constants.GoogleConstants;
import jp.loioz.app.common.service.UserDetailsServiceImpl;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.handler.UserAccessDeniedHandler;
import jp.loioz.common.handler.UserAuthenticationEntryPoint;
import jp.loioz.common.handler.UserLoginFailureHandler;
import jp.loioz.common.handler.UserLoginSuccessHandler;
import jp.loioz.common.handler.UserLogoutSuccessHandler;

/**
 * SpringSecurityの設定
 */
@Configuration
@EnableWebSecurity
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/** セキュリティチェックを行わないリソースのURLパターン */
	public static final String[] URL_PATTERNS_IGNOR_SECURITY_CHECK = new String[]{
			"/img/**",
			"/css/**",
			"/js/**",
			"/favicon.ico",
			"/webjars/**"
	};

	/** 未ログイン状態でもアクセスを許可するハンドラーのURLパターン */
	public static final String[] URL_PATTERNS_ACCESSIBLE_NO_LOGIN = new String[]{
			"/",
			"/user/static/**",
			"/" + UrlConstant.PRE_LOGIN_URL + "/**",
			"/" + UrlConstant.LOGIN_URL + "/**",
			"/user/accountRegist/**",
			"/user/accountDetailRegist/**",
			"/user/accountEdit/verify/**",
			"/user/passwordForgetRequest/**",
			"/user/invitedAccountRegist/**",
			"/" + UrlConstant.PLANSETTING_URL + "/**",
			"/" + UrlConstant.PLANSETTING_GATEWAY_URL + "/" + PlanConstant.PLAN_SETTING_AUTH_ERROR_PATH,
			"/" + UrlConstant.BOX_API_URL + "/" + BoxConstants.BOX_AUTH_GATEWAY_REDIRECT_PATH,
			"/" + UrlConstant.DROPBOX_API_URL + "/" + DropboxConstants.DROPBOX_AUTH_GATEWAY_REDIRECT_PATH,
			"/" + UrlConstant.GOOGLE_API_URL + "/" + GoogleConstants.GOOGLE_AUTH_GATEWAY_REDIRECT_PATH,
			"/common/getHolidays",
			"/" + UrlConstant.G_DOWNLOAD_AUTH_URL + "/**",
			"/" + UrlConstant.G_DOWNLOAD_URL + "/**",
			"/error",
			UrlConstant.MULTI_LOGIN_ERROR_URL,
			"/systemError"
	};

	/** ログイン認証用のサービスクラス */
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	/** 認証成功時のハンドラ */
	@Autowired
	private UserLoginSuccessHandler successHandler;
	/** 認証失敗時のハンドラ */
	@Autowired
	private UserLoginFailureHandler failureHandler;
	/** ログアウト成功時のハンドラ */
	@Autowired
	private UserLogoutSuccessHandler logoutSuccessHandler;

	/** アプリケーションの起動モード ※起動時のコマンドライン引数で渡す値のためapplication.propertiesには記載されていない */
	@Value("${app.bootMode}")
	private String appBootMode;

	@Value("${domain.name}")
	private String domainName;

	/**
	 * セキュリティ設定を無視するリクエスト設定
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(URL_PATTERNS_IGNOR_SECURITY_CHECK);
	}

	/**
	 * セキュリティ設定
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(URL_PATTERNS_ACCESSIBLE_NO_LOGIN)
				.permitAll()
				.anyRequest().authenticated()
				.and()
				.exceptionHandling()
				.defaultAuthenticationEntryPointFor(
						authenticationEntryPoint("/" + UrlConstant.ADMIN_LOGIN_URL),
						new AntPathRequestMatcher("/admin/**"))
				.defaultAuthenticationEntryPointFor(
						authenticationEntryPoint("/" + UrlConstant.LOGIN_URL),
						new AntPathRequestMatcher("/**"))
				.accessDeniedHandler(accessDeniedHandler())
				.and()
				.formLogin()
				.loginProcessingUrl(UrlConstant.LOGIN_POST_REQUEST_URL)
				.loginPage("/" + UrlConstant.LOGIN_URL + "/")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(successHandler)
				.failureHandler(failureHandler)
				.permitAll()
				.and();
		http.logout()
				.logoutUrl(UrlConstant.LOGOUT_POST_REQUEST_URL)
				.logoutSuccessHandler(logoutSuccessHandler)
				// セッションを破棄する
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.permitAll();
		// csrfチェック
		http.csrf()
				// 異なるドメインからPOSTリクエストを行う下記のケースのため、ログイン、ログアウト処理についてはcsrfのチェックを無効にする
				// ・テナント登録画面（テナント作成後）でのログインリクエスト（テナントのサブドメイン付きのURLへのリクエスト）
				// ・プラン画面のサブドメインの画面からテナントのサブドメインへのcorsでのでのログアウトリクエスト（テナントのサブドメイン付きのURLへのリクエスト）
				.ignoringAntMatchers(UrlConstant.LOGIN_POST_REQUEST_URL, UrlConstant.LOGOUT_POST_REQUEST_URL);
		// クロスオリジン
		http.cors()
				.configurationSource(corsConfigurationSource());
	}

	/**
	 * パスワードの暗号化方式
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint(String url) {
		return new UserAuthenticationEntryPoint(url);
	}

	@Bean
	AccessDeniedHandler accessDeniedHandler() {
		return new UserAccessDeniedHandler();
	}

	/**
	 * 認証処理<br>
	 * ※auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	 * という実装が行われるケースが多いが、
	 * daoAuthenticationProvider()メソッドないでsetHideUserNotFoundExceptions(false)を行うために下記のような実装を行っている。
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	/**
	 * 認証処理プロバイダーの生成
	 */
	@Bean
	public AuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
		impl.setUserDetailsService(userDetailsService);
		impl.setPasswordEncoder(passwordEncoder());
		// ※UserDetailsServiceでUsernameNotFoundExceptionがスローされた場合に、Springが内部的にBadCredentialsExceptionに変換してしまう動作を停止する。
		// 下記行うことで、userDetailsServiceで発生したExceptionにエラーメッセージを保持させ、ログインエラー時のメッセージのだし分けを可能にする。
		impl.setHideUserNotFoundExceptions(false);
		return impl;
	}

	/**
	 * サーブレットの初期化
	 */
	@Bean
	public ServletContextInitializer servletContextInitializer(@Value("${server.servlet.session.cookie.secure}") boolean secure) {

		ServletContextInitializer servletContextInitializer = new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {

				// SessionIdのcookieには、ドメインの設定（サブドメインを除いたドメインとする設定）は行わない。
				// -> テナントのサブドメインと、プラン画面のサブドメインとで、tomcatが作成するSesionIdを明確に分けるため。
				// -> （
				// -> プラン画面にアクセスした場合（ログインSessionのあるサーバーとは異なるサーバーに振り分けられた場合）、
				// -> tomcatが新規にSessionIdを発行し、ブラウザに返却することで、もともとのログインSession用のcookieが上書かれ、
				// -> もとのログインSessionIdを持つサーバーにリクエストが戻るときに、ブラウザが送信するSessionIdとサーバーが保持するSessionIdが
				// -> 一致しなくなってしまうことを防ぐため。
				// -> ）

				servletContext.getSessionCookieConfig().setName(CookieConstant.COOKIE_NAME_OF_SESSION_ID);
				servletContext.getSessionCookieConfig().setHttpOnly(true);
				servletContext.getSessionCookieConfig().setSecure(secure);
				servletContext.setSessionTrackingModes(
						Collections.singleton(SessionTrackingMode.COOKIE));
			}
		};
		return servletContextInitializer;
	}

	/**
	 * クロスオリジンリクエストの許可設定
	 * 
	 * @return
	 */
	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
		corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
		// プラン画面からのリクエストのみcorsを許可する（本番、ステージング、開発のどの環境でも対応できるよう、httpとhttpsの両方を許可）
		corsConfiguration.addAllowedOrigin("http://" + PlanConstant.PLAN_SETTING_SUB_DOMAIN + "." + domainName + ":8080");
		corsConfiguration.addAllowedOrigin("https://" + PlanConstant.PLAN_SETTING_SUB_DOMAIN + "." + domainName);
		// corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
		// 認証情報（cookieなど）も必要なので送信を許可する
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		// corsのリクエストを受付可能とするパス
		// プラン画面のコントローラーのすべてのメソッド
		corsConfigurationSource.registerCorsConfiguration("/" + UrlConstant.PLANSETTING_TENANT_ACCESS_URL + "/**", corsConfiguration);

		return corsConfigurationSource;
	}
}
