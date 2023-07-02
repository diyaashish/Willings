package jp.loioz.app.user.login.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.login.form.LoginForm;
import jp.loioz.app.user.login.service.LoginService;
import jp.loioz.common.aspect.annotation.PermitAlsoDisabledPlanStatusUser;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * ログイン画面用のコントローラークラス
 */
@Controller
@RequestMapping(UrlConstant.LOGIN_URL)
public class LoginController extends DefaultController {

	/** viewで使用するフォームオブジェクト名 **/
	private static final String LOGIN_FORM_NAME = "loginForm";

	/** viewで使用するログインエラーメッセージパラメータ名 **/
	private static final String LOGIN_ERROR_MSG = "loginErrorMsg";

	/** ログインエラーメッセージのSessionAttributeKey **/
	private static final String LOGIN_FAILURE_MSG_KEY = SessionAttrKeyEnum.LOGIN_FAILURE_MSG.getCodeKey();

	/** Cookieのセキュリティ設定 */
	@Value("${server.servlet.session.cookie.secure}")
	private boolean cookieSecure;

	/** ログイン画面のサービスクラス */
	@Autowired
	private LoginService loginService;

	/** Cookieサービスクラス */
	@Autowired
	private CookieService cookieService;

	/**
	 * 初期表示
	 *
	 * @param encodedId アカウントID初期値(URLエンコード済)
	 * @return 遷移先の画面
	 */
	@PermitAlsoDisabledPlanStatusUser
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(HttpSession session, HttpServletResponse response, HttpServletRequest request) {

		if (SessionUtils.isAlreadyLoggedUser()) {
			// 既にログインしている場合
			if (SessionUtils.isOnlyPlanSettingAccessible()) {
				// プラン設定画面にしかアクセスできない場合
				return super.getRedirectModelAndView(loginService.getPlanSettingUrl());
			} else {
				return super.getRedirectModelAndView(UrlConstant.LOGIN_SUCCESS_URL);
			}
		}

		// サブドメインのCookie情報が存在する場合は削除する
		this.removeSubDomainCookie(request, response);
		LoginForm loginForm = new LoginForm();

		String cookieAccountId = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_LONGIN_ACCOUNT_ID, request);
		if (StringUtils.isNotEmpty(cookieAccountId)) {
			// 復号化処理
			TextEncryptor text = Encryptors.text(CookieConstant.COOKIE_ENCRYPTION_KEY, CookieConstant.COOKIE_ENCRYPTION_SALT);
			String deluxDecryptText = text.decrypt(cookieAccountId);

			loginForm.setUsername(deluxDecryptText);
			loginForm.setKeepAccountFlg(true);
		}

		// ログイン処理のエラーメッセージを取得する
		String loginErrorMsg = this.getAndClearLoginErrorMsg(session);

		ModelAndView mv = getMyModelAndView(loginForm, "user/login/login", LOGIN_FORM_NAME);
		mv = mv.addObject(LOGIN_ERROR_MSG, loginErrorMsg);

		return mv;
	}

	/**
	 * サブドメインのCookieを削除する
	 * 
	 * @param response
	 */
	private void removeSubDomainCookie(HttpServletRequest request, HttpServletResponse response) {

		final String COOKIE_NAME_OF_SUBDOMAIN = CookieConstant.COOKIE_NAME_OF_SUBDOMAIN;

		String subdomainCookieValue = cookieService.getCookieValue(COOKIE_NAME_OF_SUBDOMAIN, request);

		if (StringUtils.isNotEmpty(subdomainCookieValue)) {
			// サブドメインのCookieが存在する場合は削除
			cookieService.removeCookie(COOKIE_NAME_OF_SUBDOMAIN, response);
		}
	}

	/**
	 * ログインエラーメッセージをsessionから取得し、sessionの値はclearする
	 * 
	 * @param session
	 * @return
	 */
	private String getAndClearLoginErrorMsg(HttpSession session) {
		String loginErrorMsg = null;

		Object lastExceptionMessageObj = session.getAttribute(LOGIN_FAILURE_MSG_KEY);
		if (Objects.nonNull(lastExceptionMessageObj)) {
			loginErrorMsg = (String) lastExceptionMessageObj;
		}
		session.removeAttribute(LOGIN_FAILURE_MSG_KEY);

		return loginErrorMsg;
	}

}
