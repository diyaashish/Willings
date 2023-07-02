package jp.loioz.common.service.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jp.loioz.common.utility.StringUtils;

/**
 * Cookieを扱うサービスクラス
 */
@Service
public class CookieService {

	/** アプリケーションの起動モード ※起動時のコマンドライン引数で渡す値のためapplication.propertiesには記載されていない */
	@Value("${app.bootMode}")
	private String appBootMode;

	/** ユーザー側のドメイン */
	@Value("${domain.name}")
	private String domainName;

	/** Cookieのセキュリティ設定 */
	@Value("${server.servlet.session.cookie.secure}")
	private boolean cookieSecure;

	/**
	 * 指定の値をレスポンスのCookie情報に追加する。
	 * 
	 * @param cookieName
	 * @param cookieValue
	 * @param maxAge
	 * @param response
	 */
	public void addCookieForHttp(String cookieName, String cookieValue, Integer maxAge, HttpServletResponse response) {

		this.addCookie(cookieName, cookieValue, maxAge, response, true);
	}

	/**
	 * 指定の値をレスポンスのCookie情報に追加する。<br>
	 * ※フロントのJavaScriptでCookieが取得できるよう、HttpOnly属性がfalseのCookieを設定する。
	 *
	 * @param cookieName
	 * @param cookieValue
	 * @param maxAge
	 * @param response
	 */
	public void addCookieForJS(String cookieName, String cookieValue, Integer maxAge, HttpServletResponse response) {

		this.addCookie(cookieName, cookieValue, maxAge, response, false);
	}

	/**
	 * 指定した名前のCookie情報を削除する。
	 * 
	 * <pre>
	 * maxAgeに0を設定ことでcookieから削除する。<br>
	 * 削除されるためcookieValueは必要ないが、メソッド内の処理でNULLと空文字は処理がされないので「rm」を指定する
	 * </pre>
	 *
	 * @param cookieName
	 * @param response
	 */
	public void removeCookie(String cookieName, HttpServletResponse response) {

		// maxageが0なので削除が行われる
		this.addCookie(cookieName, "rm", 0, response, true);
	}

	/**
	 * 指定のCookieの値をリクエストから取得する
	 * 
	 * @param cookieName
	 * @param request
	 * @return
	 */
	public String getCookieValue(String cookieName, HttpServletRequest request) {

		if (request == null) {
			return null;
		}

		String cookieValue = "";

		// Cookieの値を取得
		Cookie[] cookies = request.getCookies();

		if (StringUtils.isEmpty(cookieName) || cookies == null) {
			return cookieValue;
		}

		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(cookieName)) {
				cookieValue = cookie.getValue();
			}
		}

		return cookieValue;
	}

	/**
	 * 指定の値をレスポンスのCookie情報に追加する
	 * 
	 * @param cookieName
	 * @param cookieValue
	 * @param response
	 * @param httpOnly
	 */
	private void addCookie(String cookieName, String cookieValue, Integer maxAge, HttpServletResponse response, boolean httpOnly) {

		if (StringUtils.isEmpty(cookieName) || StringUtils.isEmpty(cookieValue) || response == null) {
			return;
		}

		// ドメイン名
		String cookieDomainName = domainName;

		// Cookieを追加
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setDomain(cookieDomainName);
		cookie.setPath("/");
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(cookieSecure);

		if (maxAge != null) {
			cookie.setMaxAge(maxAge);
		}

		response.addCookie(cookie);
	}
}