package jp.loioz.common.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.UriService;

/**
 * SpringSecurityが管理する不正アクセスのハンドリングクラス
 */
public class UserAccessDeniedHandler implements AccessDeniedHandler {

	/** クッキーサービス */
	@Autowired
	private CookieService cookieService;

	/** URIサービス */
	@Autowired
	UriService uriService;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
			throws IOException, ServletException {

		if (isRequestedSessionInvalid(request)) {
			// セッションタイムアウトの場合はログイン画面にリダイレクトする

			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				// ajaxリクエストの場合のセッションタイムアウト(リダイレクト処理はクライアント側で行う)

				response.setStatus(CommonConstant.CUSTOM_SESSION_TIMEOUT_ERROR_CODE);

				// サブドメインを設定したコンテキストまでのUri情報
				String subDomain = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, request);
				if (StringUtils.isEmpty(subDomain)) {
					// Cookieにサブドメインが存在しない場合 -> 他のテナントのログイン画面を開いたり、他のテナントでログイアウトした後の操作の場合。
					// URL情報からサブドメインを取得する。
					subDomain = uriService.getSubDomainName();
				}
				String uriUntilContextPath = ServletUriComponentsBuilder.fromCurrentContextPath()
						.host(subDomain + "." + uriService.getDomain())
						.toUriString();
				
				response.setHeader(CommonConstant.HEADER_NAME_OF_URI_UNTIL_CONTEXT_PATH, uriUntilContextPath);
				
				return;
			}

			// ログイン画面のURLを設定
			response.sendRedirect(uriService.getUserLoginUrl());

			return;
		}

		response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
	}

	/**
	 * リクエスト情報より、セッション切れかどうか（セッションID自体は存在しているが、有効ではなくなった状態かどうか）を判定する
	 * 
	 * @param request
	 * @return
	 */
	private boolean isRequestedSessionInvalid(HttpServletRequest request) {
		return request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();
	}
}
