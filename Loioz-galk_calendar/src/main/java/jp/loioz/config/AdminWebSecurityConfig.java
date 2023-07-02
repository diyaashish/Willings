package jp.loioz.config;

import org.springframework.beans.factory.annotation.Autowired;
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

import jp.loioz.app.common.service.UserDetailsServiceImpl;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.handler.AdminLoginFailureHandler;
import jp.loioz.common.handler.AdminLoginSuccessHandler;
import jp.loioz.common.handler.AdminLogoutSuccessHandler;
import jp.loioz.common.handler.UserAccessDeniedHandler;
import jp.loioz.common.handler.UserAuthenticationEntryPoint;

/**
 * SpringSecurityの設定(システム管理者用)
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {

	/** ログイン認証用のサービスクラス */
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	/** 認証成功時のハンドラ */
	@Autowired
	private AdminLoginSuccessHandler successHandler;
	/** 認証失敗時のハンドラ */
	@Autowired
	private AdminLoginFailureHandler failureHandler;
	/** ログアウト成功時のハンドラ */
	@Autowired
	private AdminLogoutSuccessHandler logoutSuccessHandler;

	/**
	 * セキュリティ設定を無視するリクエスト設定
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(
				"/img/**",
				"/css/**",
				"/js/**",
				"/favicon.ico",
				"/webjars/**",
				"/error",
				"/systemError");
	}

	/**
	 * セキュリティ設定
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.antMatcher("/admin/**")
				.authorizeRequests()
				.antMatchers("/" + UrlConstant.ADMIN_LOGIN_URL + "/")
				.permitAll()
				.anyRequest().authenticated()
				.and()
				.exceptionHandling()
				.defaultAuthenticationEntryPointFor(
						authenticationEntryPoint("/" + UrlConstant.ADMIN_LOGIN_URL),
						new AntPathRequestMatcher("/admin/**"))
				.accessDeniedHandler(accessDeniedHandler())
				.and()
				.formLogin()
				.loginProcessingUrl(UrlConstant.ADMIN_LOGIN_POST_REQUEST_URL)
				.loginPage("/" + UrlConstant.ADMIN_LOGIN_URL + "/")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(successHandler)
				.failureHandler(failureHandler)
				.permitAll()
				.and();
		http.logout()
				.logoutUrl(UrlConstant.ADMIN_LOGOUT_POST_REQUEST_URL)
				.logoutSuccessHandler(logoutSuccessHandler)
				// セッションを破棄する
				.invalidateHttpSession(true)
				.permitAll();
	}

	/**
	 * パスワードの暗号化方式
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint(String url) {
		// 実際のインスタンスはWebSecurityConfig側に記載されているAuthenticationEntryPointのBeanで上書きされる
		// （本ConfigクラスよりWebSecurityConfigの優先度の方が低く、後に実行されるため）
		return new UserAuthenticationEntryPoint(url);
	}

	@Bean
	AccessDeniedHandler accessDeniedHandler() {
		// 実際のインスタンスはWebSecurityConfig側に記載されているAuthenticationEntryPointのBeanで上書きされる
		// （本ConfigクラスよりWebSecurityConfigの優先度の方が低く、後に実行されるため）
		return new UserAccessDeniedHandler();
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

}
