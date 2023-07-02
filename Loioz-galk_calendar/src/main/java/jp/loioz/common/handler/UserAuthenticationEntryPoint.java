package jp.loioz.common.handler;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.RedirectUrlBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.ConstantLists;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.UriService;

/**
 * 未ログイン時のアクセスに対するハンドリングクラス
 */
public class UserAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	/** ドメイン名（application.propertiesから値を取得） */
	@Value("${domain.name}")
	private String domainName;

	/** クッキーサービス */
	@Autowired
	private CookieService cookieService;
	
	/** URIサービス */
	@Autowired
	UriService uriService;

	/** コンストラクタ */
	public UserAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}

	/**
	 * Performs the redirect (or forward) to the login form URL.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")) && isRequestedSessionInvalid(request)) {
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

		super.commence(request, response, authException);
	}

	/**
	 * 未ログイン時にSeringSecurityでアクセス許可されていないページへアクセスした際に実行される処理
	 */
	@Override
	protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

		String redirectUrl = null;

		if (this.isRequestedSessionInvalidOrAccessingRequest(request)) {
			// セッションタイムアウトにより、未ログイン状態となった場合はログイン画面にリダイレクトする

			// セッションタイムアウトになる場合（ログインしていた場合）は必ずCookieにサブドメイン情報が存在しているはず
			String subDomain = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, request);
			if (StringUtils.isEmpty(subDomain)) {
				// Cookieにサブドメインが存在しない場合 -> 他のテナントのログイン画面を開いたり、他のテナントでログイアウトした後の操作の場合。
				// URL情報からサブドメインを取得する。
				subDomain = uriService.getSubDomainName();
			}

			redirectUrl = uriService.getUserLoginUrlWithSubDomain(subDomain);

			return redirectUrl;
		}

		// サブドメインの定義
		String subDomain = uriService.getSubDomainName();
		// URLを取得 ("http://test-domain.local-loioz.com:8080/user/dengon/ssss/bbbb" -> "/user/dengon/ssss/bbbb")
		String requestUrl = request.getRequestURI();

		// 定義されているRequestPathを取得
		List<String> requestMappingPath = ConstantLists.CONTROLLER_MAPPING_PATH;

		for (String mappingPath : requestMappingPath) {

			// 正規表現によるmacherパターンを作成
			String regex = "/" + StringUtils.removeCharAndAfter(mappingPath, "{") + "*";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(requestUrl);

			if (matcher.find()) {
				// 指定のPathに一致した場合、ログイン画面へリダイレクトする。
				redirectUrl = uriService.getUserLoginUrlWithSubDomain(subDomain);
				return redirectUrl;
			}
		}

		// 最初からログインしていない状態でのアクセスの場合は無料アカウント登録画面へリダイレクトする
		RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
		urlBuilder.setScheme(request.getScheme());
		urlBuilder.setServerName(domainName);
		urlBuilder.setPort(request.getServerPort());
		urlBuilder.setContextPath(request.getContextPath());
		urlBuilder.setServletPath("/" + UrlConstant.ACCOUNT_REGIST_URL + "/");

		redirectUrl = urlBuilder.getUrl();

		return redirectUrl;
	}

	/**
	 * セッション切れ、もしくは、すでにアクセスしていた場合でのリクエストかどうかを判定する
	 *
	 * @param request
	 * @return
	 */
	private boolean isRequestedSessionInvalidOrAccessingRequest(HttpServletRequest request) {

		boolean isRequestedSessionInvalid = this.isRequestedSessionInvalid(request);
		boolean isAccessingRequest = this.isAccessingRequest(request);

		return isRequestedSessionInvalid || isAccessingRequest;
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

	/**
	 * リクエスト情報より、既にアクセスをしていたユーザーによるリクエストなのかを判定する
	 *
	 * @param request
	 * @return
	 */
	private boolean isAccessingRequest(HttpServletRequest request) {

		String refere = request.getHeader("Referer");
		if (refere == null) {
			return false;
		}

		String serverName = request.getServerName();

		return refere.contains(serverName);
	}
}
