package jp.loioz.common.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jp.loioz.app.common.service.UserDetailsServiceImpl;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.LoginAccountStatusException;
import jp.loioz.common.exception.LoginTenantPlanStatusException;
import jp.loioz.common.log.Logger;

/**
 * Spring Securityの認証失敗時のハンドラクラス
 */
@Component
public class UserLoginFailureHandler implements AuthenticationFailureHandler {

	/** ログインエラーメッセージのSessionAttributeKey **/
	private static final String LOGIN_FAILURE_MSG_KEY = SessionAttrKeyEnum.LOGIN_FAILURE_MSG.getCodeKey();
	
	@Autowired
	Logger log;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException, ServletException {

		HttpSession session = request.getSession();
		
		if (authenticationException instanceof BadCredentialsException) {
			// アカウントIDかパスワードの認証エラーの場合
			// ログイン画面にエラーメッセージを表示する
			session.setAttribute(LOGIN_FAILURE_MSG_KEY, UserDetailsServiceImpl.NOT_FOUND_ACCOUNT_MSG);
		} else if (authenticationException instanceof LoginTenantPlanStatusException || authenticationException instanceof UsernameNotFoundException
				|| authenticationException instanceof LoginAccountStatusException) {
			// ステータスのチェック等のバリデーションチェックによるログインエラー
			// ログイン画面にエラーメッセージを表示する
			session.setAttribute(LOGIN_FAILURE_MSG_KEY, authenticationException.getMessage());
		} else {
			// 上記以外の意図しないエラーの場合
			session.setAttribute(LOGIN_FAILURE_MSG_KEY, "ただいまサーバーが込み合っております。再度本サービスにアクセスしてご利用をお願いいたします。");
			log.error("Login authenticationFailure error: " + authenticationException.getMessage());
		}

		// ログイン画面にリダイレクトする
		response.sendRedirect("/" + UrlConstant.LOGIN_URL + "/");
	}
}
