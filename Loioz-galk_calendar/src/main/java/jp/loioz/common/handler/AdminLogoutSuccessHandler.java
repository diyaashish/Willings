package jp.loioz.common.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jp.loioz.common.constant.UrlConstant;

/**
 * ログアウト成功時のハンドラークラス（システム管理者用）
 */
@Component
public class AdminLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		response.sendRedirect("/" + UrlConstant.ADMIN_LOGIN_URL + "/");
	}
}
