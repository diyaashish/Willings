package jp.loioz.app.common.api.box.controller;

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
import jp.loioz.app.common.api.box.constants.BoxConstants;
import jp.loioz.app.common.api.box.service.BoxService;
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

/**
 * Boxコントローラー
 */
@Controller
@RequestMapping(UrlConstant.BOX_API_URL)
public class BoxController extends DefaultController {

	@Autowired
	private BoxService boxService;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** クッキーサービス */
	@Autowired
	private CookieService cookieService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * boxの認証サーバーからリダイレクトの受け口<br>
	 * 
	 * <pre>
	 * boxのAuth認証では許容できるRedirectUrlは完全一致(マルチドメインは考慮されていない)のため、
	 * subdomainなしのURLを受け取り、subdomain付きのURLにリダイレクトする
	 * ※このメソッドはURL直アクセスでの認証エラーも考慮し、未ログイン状態でのアクセスも可能とする（WebSecurityConfig.javaで定義）
	 * </pre>
	 * 
	 * @return
	 */
	@RequestMapping(value = BoxConstants.BOX_AUTH_GATEWAY_REDIRECT_PATH, method = RequestMethod.GET)
	public ModelAndView boxAuthGatewayRedirect(
			@RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state") String state,
			HttpServletRequest request) {

		// リクエストにsubdomainが無いのでcookieから取得
		String subDomain = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, request);

		// URIを設定
		String uri = ModelAndViewUtils.getRedirectPath(OAuthController.class, controller -> controller.boxAuthRedirect(error, code, state));

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
			commonOAuthService.refresh(ExternalService.BOX);
			isDeleted = boxService.isDeletedRootFolder();

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

		String rootFoloderUrl = boxService.getRootFolderUrl();
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

		boolean existsAnkenRootFolder = boxService.existsAnkenRootFoloderDB(ankenId);
		if (!existsAnkenRootFolder) {
			response.put("successed", false);
			response.put("needCreate", true);
			response.put("needReCreate", false);
			response.put("needReCreateAnkenRoot", false);
			return response;
		}

		try {
			commonOAuthService.refresh(ExternalService.BOX);
			boolean isDeletedRootFoloder = boxService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boolean isDeletedRootAnkenFoloder = boxService.isDeletedAnkenRootFolder(ankenId);
			if (isDeletedRootAnkenFoloder) {
				// 案件ルートフォルダが削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", true);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

			boolean existsAnkenFolder = boxService.existsAnkenFoloderDB(ankenId);
			if (!existsAnkenFolder) {
				response.put("successed", false);
				response.put("needCreate", true);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", false);
				return response;
			}

			boolean isDeletedAnkenFoloder = boxService.isDeletedAnkenFolder(ankenId);
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

		String rootFoloderUrl = boxService.getAnkenFolderUrl(ankenId);
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

		boolean existsCustomerRootFolder = boxService.existsCustomerRootFoloderDB(customerId);
		if (!existsCustomerRootFolder) {
			response.put("successed", false);
			response.put("needCreate", true);
			response.put("needReCreate", false);
			response.put("needReCreateCustomerRoot", false);
			return response;
		}

		try {
			commonOAuthService.refresh(ExternalService.BOX);
			boolean isDeletedRootFoloder = boxService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boolean isDeletedRootCustomerFoloder = boxService.isDeletedCustomerRootFolder(customerId);
			if (isDeletedRootCustomerFoloder) {
				// 顧客ルートフォルダが削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", true);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

			boolean existsCustomerFolder = boxService.existsCustomerFoloderDB(customerId);
			if (!existsCustomerFolder) {
				response.put("successed", false);
				response.put("needCreate", true);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", false);
				return response;
			}

			boolean isDeletedCustomerFoloder = boxService.isDeletedCustomerFolder(customerId);
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

		String rootFoloderUrl = boxService.getCustomerFolderUrl(customerId);
		response.put("successed", true);
		response.put("url", rootFoloderUrl);
		return response;
	}

	/**
	 * 顧客フォルダを作成する
	 * 
	 * @param customerId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createBoxCustomerFolder", method = RequestMethod.POST)
	public Map<String, Object> createBoxCustomerFolder(
			@RequestParam(name = "customerId") Long customerId,
			@RequestParam(name = "reCreateCustomerRootFlg", defaultValue = "false") boolean reCreateCustomerRootFlg,
			@RequestParam(name = "reCreateFlg", defaultValue = "false") boolean reCreateFlg) {

		Map<String, Object> response = new HashMap<>();

		try {
			// トークンのリフレッシュ処理
			commonOAuthService.refresh(ExternalService.BOX);

			boolean isDeletedRootFoloder = boxService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boxService.createCustomerFolder(customerId, reCreateCustomerRootFlg, reCreateFlg);
			String boxCustomerUrl = boxService.getCustomerFolderUrl(customerId);

			response.put("successed", true);
			response.put("url", boxCustomerUrl);
			if (reCreateFlg) {
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
	 * 案件フォルダを作成する
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createBoxAnkenFolder", method = RequestMethod.POST)
	public Map<String, Object> createBoxAnkenFolder(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "reCreateAnkenRootFlg", defaultValue = "false") boolean reCreateAnkenRootFlg,
			@RequestParam(name = "reCreateFlg", defaultValue = "false") boolean reCreateFlg) {

		Map<String, Object> response = new HashMap<>();

		try {
			// トークンのリフレッシュ処理
			commonOAuthService.refresh(ExternalService.BOX);

			boolean isDeletedRootFoloder = boxService.isDeletedRootFolder();
			if (isDeletedRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boxService.createAnkenFolder(ankenId, reCreateAnkenRootFlg, reCreateFlg);
			String boxAnkenUrl = boxService.getAnkenFolderUrl(ankenId);

			response.put("successed", true);
			response.put("url", boxAnkenUrl);
			if (reCreateFlg) {
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
