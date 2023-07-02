package jp.loioz.app.common.api.dropbox.controller;

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
import jp.loioz.app.common.api.dropbox.constants.DropboxConstants;
import jp.loioz.app.common.api.dropbox.service.DropboxService;
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
 * Dropboxのコントローラー
 */
@Controller
@RequestMapping(UrlConstant.DROPBOX_API_URL)
public class DropboxController extends DefaultController {

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** クッキーサービス */
	@Autowired
	private CookieService cookieService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	@Autowired
	private DropboxService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * Dropboxの認証サーバーからリダイレクトの受け口<br>
	 * 
	 * <pre>
	 * DropboxのAuth認証では許容できるRedirectUrlは完全一致(マルチドメインは考慮されていない)のため、
	 * subdomainなしのURLを受け取り、subdomain付きのURLにリダイレクトする
	 * ※このメソッドはURL直アクセスでの認証エラーも考慮し、未ログイン状態でのアクセスも可能とする（WebSecurityConfig.javaで定義）
	 * </pre>
	 * 
	 * @return
	 */
	@RequestMapping(value = DropboxConstants.DROPBOX_AUTH_GATEWAY_REDIRECT_PATH, method = RequestMethod.GET)
	public ModelAndView dropboxAuthGatewayRedirect(
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state") String state,
			HttpServletRequest request) {

		// リクエストにsubdomainが無いのでcookieから取得
		String subDomain = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, request);

		// URIを設定
		String uri = ModelAndViewUtils.getRedirectPath(OAuthController.class, controller -> controller.dropboxAuthRedirect(code, state));

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

		boolean isNotFound = false;
		try {
			commonOAuthService.refresh(ExternalService.DROPBOX);
			isNotFound = service.isNotFoundRootFolder();

		} catch (AppException e) {
			// エラー内容
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		}

		if (isNotFound) {
			// 削除されている場合
			response.put("successed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00142));
			return response;
		}

		String rootFoloderUrl = service.getRootFolderUrl();
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

		boolean existsAnkenRootFolder = service.existsAnkenRootFoloderDB(ankenId);
		if (!existsAnkenRootFolder) {
			response.put("successed", false);
			response.put("needCreate", true);
			response.put("needReCreate", false);
			response.put("needReCreateAnkenRoot", false);
			return response;
		}

		try {
			commonOAuthService.refresh(ExternalService.DROPBOX);
			boolean isNotFoundRootRootFoloder = service.isNotFoundRootFolder();
			if (isNotFoundRootRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boolean isNotFoundRootAnkenFoloder = service.isNotFoundAnkenRootFolder(ankenId);
			if (isNotFoundRootAnkenFoloder) {
				// 案件ルートフォルダが削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", true);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

			boolean existsAnkenFolder = service.existsAnkenFoloderDB(ankenId);
			if (!existsAnkenFolder) {
				response.put("successed", false);
				response.put("needCreate", true);
				response.put("needReCreate", false);
				response.put("needReCreateAnkenRoot", false);
				return response;
			}

			boolean isNotFoundAnkenFoloder = service.isNotFoundAnkenFolder(ankenId);
			if (isNotFoundAnkenFoloder) {
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

		String rootFoloderUrl = service.getAnkenFolderUrl(ankenId);
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

		// ルートフォルダを一度も作成していない場合(DBにデータがない)場合は、ルートフォルダから作成ので、「recreate」はfalse
		boolean existsCustomerRootFolder = service.existsCustomerRootFoloderDB(customerId);
		if (!existsCustomerRootFolder) {
			response.put("successed", false);
			response.put("needCreate", true);
			response.put("needReCreate", false);
			response.put("needReCreateCustomerRoot", false);
			return response;
		}

		try {
			commonOAuthService.refresh(ExternalService.DROPBOX);
			boolean isNotFoundRootFoloder = service.isNotFoundRootFolder();
			if (isNotFoundRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			boolean isNotFoundRootCustomerFoloder = service.isNotFoundCustomerRootFolder(customerId);
			if (isNotFoundRootCustomerFoloder) {
				// 顧客ルートフォルダが削除されている場合は、ルートフォルダの再作成 → 顧客フォルダの作成を行う
				response.put("successed", false);
				response.put("needCreate", false);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", true);
				response.put("message", getMessage(MessageEnum.MSG_W00012));
				return response;
			}

			// ルートフォルダが見つかったが、顧客フォルダが未作成(DBにデータなし)の場合、顧客フォルダを作成
			boolean existsCustomerFolder = service.existsCustomerFoloderDB(customerId);
			if (!existsCustomerFolder) {
				response.put("successed", false);
				response.put("needCreate", true);
				response.put("needReCreate", false);
				response.put("needReCreateCustomerRoot", false);
				return response;
			}

			boolean isNotFoundCustomerFoloder = service.isNotFoundCustomerFolder(customerId);
			if (isNotFoundCustomerFoloder) {
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

		String rootFoloderUrl = service.getCustomerFolderUrl(customerId);
		response.put("successed", true);
		response.put("url", rootFoloderUrl);
		return response;
	}

	/**
	 * 案件フォルダを作成する
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createDropboxAnkenFolder", method = RequestMethod.POST)
	public Map<String, Object> createDropboxAnkenFolder(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "reCreateAnkenRootFlg", defaultValue = "false") boolean reCreateAnkenRootFlg,
			@RequestParam(name = "reCreateFlg", defaultValue = "false") boolean reCreateFlg) {

		Map<String, Object> response = new HashMap<>();

		try {
			// トークンのリフレッシュ処理
			commonOAuthService.refresh(ExternalService.DROPBOX);

			boolean isNotFoundRootFoloder = service.isNotFoundRootFolder();
			if (isNotFoundRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			service.createAnkenFolder(ankenId, reCreateAnkenRootFlg, reCreateFlg);
			String dropboxAnkenUrl = service.getAnkenFolderUrl(ankenId);

			response.put("successed", true);
			response.put("url", dropboxAnkenUrl);
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
	 * 顧客フォルダを作成する
	 * 
	 * @param customerId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createDropboxCustomerFolder", method = RequestMethod.POST)
	public Map<String, Object> createDropboxCustomerFolder(
			@RequestParam(name = "customerId") Long customerId,
			@RequestParam(name = "reCreateCustomerRootFlg", defaultValue = "false") boolean reCreateCustomerRootFlg,
			@RequestParam(name = "reCreateFlg", defaultValue = "false") boolean reCreateFlg) {

		Map<String, Object> response = new HashMap<>();

		try {
			// トークンのリフレッシュ処理
			commonOAuthService.refresh(ExternalService.DROPBOX);

			boolean isNotFoundRootFoloder = service.isNotFoundRootFolder();
			if (isNotFoundRootFoloder) {
				// ロイオズルートフォルダ削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			service.createCustomerFolder(customerId, reCreateCustomerRootFlg, reCreateFlg);
			String dropboxCustomerUrl = service.getCustomerFolderUrl(customerId);

			response.put("successed", true);
			response.put("url", dropboxCustomerUrl);
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
