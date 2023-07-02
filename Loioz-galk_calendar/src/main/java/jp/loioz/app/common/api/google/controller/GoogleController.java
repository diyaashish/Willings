package jp.loioz.app.common.api.google.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.api.google.constants.GoogleConstants;
import jp.loioz.app.common.api.google.service.GoogleService;
import jp.loioz.app.common.api.oauth.controller.OAuthController;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.domain.UriService;
import jp.loioz.entity.TAuthTokenEntity;

/**
 * Googleコントローラー
 */
@Controller
@RequestMapping(UrlConstant.GOOGLE_API_URL)
public class GoogleController extends DefaultController {

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** Cookieサービス */
	@Autowired
	private CookieService cookieService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	@Autowired
	private GoogleService googleService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * Googleの認証サーバーからリダイレクトの受け口<br>
	 * 
	 * <pre>
	 * GoogleのAuth認証では許容できるRedirectUrlは完全一致(マルチドメインは考慮されていない)のため、
	 * subdomainなしのURLを受け取り、subdomain付きのURLにリダイレクトする
	 * ※このメソッドはURL直アクセスでの認証エラーも考慮し、未ログイン状態でのアクセスも可能とする（WebSecurityConfig.javaで定義）
	 * </pre>
	 * 
	 * @return
	 */
	@RequestMapping(value = GoogleConstants.GOOGLE_AUTH_GATEWAY_REDIRECT_PATH, method = RequestMethod.GET)
	public ModelAndView googleAuthGatewayRedirect(
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state") String state,
			HttpServletRequest request) {

		// リクエストにsubdomainが無いのでcookieから取得
		String subDomain = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, request);

		// URIを設定
		String uri = ModelAndViewUtils.getRedirectPath(OAuthController.class, controller -> controller.googleAuthRedirect(code, state));

		// redirectUrlを作成する
		String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.host(subDomain + "." + uriService.getDomain())
				.path(uri)
				.build()
				.toUriString();

		// subdomainを付与して、redirectさせる
		return new ModelAndView("redirect:" + redirectUrl);
	}

	/**
	 * ルートフォルダのURLを取得する
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getRootFolderUrl", method = RequestMethod.GET)
	public Map<String, Object> getRootFolderUrl() {

		Map<String, Object> response = new HashMap<>();

		boolean isDeleted = false;
		try {
			commonOAuthService.refresh(ExternalService.GOOGLE);
			isDeleted = googleService.isDeletedRootFolder();

		} catch (AppException e) {
			// エラー内容
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		}

		if (isDeleted) {
			// 削除されている場合
			response.put("successed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00142));
			return response;
		}

		String rootFoloderUrl = googleService.getRootFolderUrl();
		response.put("successed", true);
		response.put("url", rootFoloderUrl);
		return response;
	}

	/**
	 * 案件フォルダURLを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getAnkenFolderUrl", method = RequestMethod.GET)
	public Map<String, Object> getAnkenFolderUrl(@RequestParam(name = "ankenId") Long ankenId) {

		Map<String, Object> response = new HashMap<>();

		boolean existsAnkenRootFolder = googleService.existsAnkenRootFoloderDB(ankenId);
		if (!existsAnkenRootFolder) {
			response.put("successed", false);
			response.put("needCreate", true);
			response.put("needReCreate", false);
			response.put("needReCreateAnkenRoot", false);
			return response;
		}

		try {
			commonOAuthService.refresh(ExternalService.GOOGLE);
			boolean isDeletedRootFoloder = googleService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boolean isDeletedRootAnkenFoloder = googleService.isDeletedAnkenRootFolder(ankenId);
			if (isDeletedRootAnkenFoloder) {
				// 案件ルートフォルダが削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", true);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

			boolean existsAnkenFolder = googleService.existsAnkenFoloderDB(ankenId);
			if (!existsAnkenFolder) {
				response.put("successed", false);
				response.put("needCreate", true);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", false);
				return response;
			}

			boolean isDeletedAnkenFoloder = googleService.isDeletedAnkenFolder(ankenId);
			if (isDeletedAnkenFoloder) {
				// 案件フォルダが削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", true);
				response.put("needReCreateAnkenRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

		} catch (AppException e) {
			// エラー内容
			response.put("successed", false);
			response.put("needCreate", false);
			response.put("needReCreate", false);
			response.put("needReCreateAnkenRoot", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		}

		String rootFoloderUrl = googleService.getAnkenFolderUrl(ankenId);
		response.put("successed", true);
		response.put("url", rootFoloderUrl);
		return response;
	}

	/**
	 * 顧客フォルダURLを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getCustomerFolderUrl", method = RequestMethod.GET)
	public Map<String, Object> getCustomerFolderUrl(@RequestParam(name = "customerId") Long customerId) {

		Map<String, Object> response = new HashMap<>();

		boolean existsCustomerRootFolder = googleService.existsCustomerRootFoloderDB(customerId);
		if (!existsCustomerRootFolder) {
			response.put("successed", false);
			response.put("needCreate", true);
			response.put("needReCreate", false);
			response.put("needReCreateCustomerRoot", false);
			return response;
		}

		try {
			commonOAuthService.refresh(ExternalService.GOOGLE);
			boolean isDeletedRootFoloder = googleService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boolean isDeletedRootCustomerFoloder = googleService.isDeletedCustomerRootFolder(customerId);
			if (isDeletedRootCustomerFoloder) {
				// 顧客ルートフォルダが削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", true);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

			boolean existsCustomerFolder = googleService.existsCustomerFoloderDB(customerId);
			if (!existsCustomerFolder) {
				response.put("successed", false);
				response.put("needCreate", true);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", false);
				return response;
			}

			boolean isDeletedCustomerFoloder = googleService.isDeletedCustomerFolder(customerId);
			if (isDeletedCustomerFoloder) {
				// 顧客フォルダが削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", true);
				response.put("needReCreateCustomerRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

		} catch (AppException e) {
			// エラー内容
			response.put("successed", false);
			response.put("needCreate", false);
			response.put("needReCreate", false);
			response.put("needReCreateCustomerRoot", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		}

		String rootFoloderUrl = googleService.getCustomerFolderUrl(customerId);
		response.put("successed", true);
		response.put("url", rootFoloderUrl);
		return response;
	}

	/**
	 * 案件フォルダを作成する
	 *
	 * @param ankenId
	 * @param reCreateFlg
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createGoogleAnkenFolder", method = RequestMethod.POST)
	public Map<String, Object> createGoogleAnkenFolder(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "reCreateAnkenRootFlg", defaultValue = "false") boolean reCreateAnkenRootFlg,
			@RequestParam(name = "reCreateFlg", defaultValue = "false") boolean reCreateFlg) {

		Map<String, Object> response = new HashMap<>();

		// アクセストークンが発行されていない場合
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		if (tAuthTokenEntity == null) {
			response.put("successed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00141, "/" + UrlConstant.MY_EXTERNAL_SETTING_URL + "/"));
			return response;
		}

		try {
			// トークンのリフレッシュ処理
			commonOAuthService.refresh(ExternalService.GOOGLE);

			boolean isDeletedRootFoloder = googleService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			googleService.createAnkenFolder(ankenId, reCreateAnkenRootFlg, reCreateFlg);
			String googleAnkenUrl = googleService.getAnkenFolderUrl(ankenId);

			response.put("successed", true);
			response.put("url", googleAnkenUrl);
			if (reCreateAnkenRootFlg || reCreateFlg) {
				response.put("message", getMessage(MessageEnum.MSG_I00039, "フォルダの再作成"));
			} else {
				response.put("message", getMessage(MessageEnum.MSG_I00039, "フォルダの作成"));
			}
			return response;

		} catch (AppException e) {
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		}

	}

	/**
	 * 顧客フォルダを作成する
	 * 
	 * @param customerId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createGoogleCustomerFolder", method = RequestMethod.POST)
	public Map<String, Object> createGoogleCustomerFolder(
			@RequestParam(name = "customerId") Long customerId,
			@RequestParam(name = "reCreateCustomerRootFlg", defaultValue = "false") boolean reCreateCustomerRootFlg,
			@RequestParam(name = "reCreateFlg", defaultValue = "false") boolean reCreateFlg) {

		Map<String, Object> response = new HashMap<>();

		try {
			// トークンのリフレッシュ処理
			commonOAuthService.refresh(ExternalService.GOOGLE);

			boolean isDeletedRootFoloder = googleService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			googleService.createCustomerFolder(customerId, reCreateCustomerRootFlg, reCreateFlg);
			String googleCustomerUrl = googleService.getCustomerFolderUrl(customerId);

			response.put("successed", true);
			response.put("url", googleCustomerUrl);
			if (reCreateCustomerRootFlg || reCreateFlg) {
				response.put("message", getMessage(MessageEnum.MSG_I00039, "フォルダの再作成"));
			} else {
				response.put("message", getMessage(MessageEnum.MSG_I00039, "フォルダの作成"));
			}
			return response;

		} catch (AppException e) {
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
