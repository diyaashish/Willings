package jp.loioz.common.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jp.loioz.common.constant.UrlConstant;

/**
 * Spring Securityの認証失敗時のハンドラクラス（システム管理者用）
 */
@Component
public class AdminLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException, ServletException {

		HttpSession session = request.getSession();

		if (authenticationException instanceof BadCredentialsException) {
			// アカウントIDかパスワードの認証エラーの場合
			// ログイン画面にエラーメッセージを表示する
			session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION_MESSAGE", "アカウントIDまたはパスワードに誤りがあります");
		} else {
			// 上記以外（ステータスのチェックによるログインエラー）の場合
			// ログイン画面にエラーメッセージを表示する
			session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION_MESSAGE", authenticationException.getMessage());
		}

		// ログイン画面にリダイレクトする
		response.sendRedirect("/" + UrlConstant.ADMIN_LOGIN_URL + "/");
	}
}
