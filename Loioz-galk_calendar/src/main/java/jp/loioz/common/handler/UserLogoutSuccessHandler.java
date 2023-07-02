package jp.loioz.common.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.service.http.CookieService;

/**
 * ログアウト成功時のハンドラークラス（ユーザー用）
 */
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {

	/** Cookieを扱うサービスクラス */
	@Autowired
	private CookieService cookieService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		// ログインやアクセス制御に関するCookieを削除
		cookieService.removeCookie(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, response);
		cookieService.removeCookie(CookieConstant.COOKIE_NAME_OF_SESSION_ID, response);
		cookieService.removeCookie(CookieConstant.COOKIE_NAME_OF_PLAN_SETTING_ACCESS_AUTH_KEY, response);

		response.sendRedirect("/" + UrlConstant.LOGIN_URL + "/");
	}
}
