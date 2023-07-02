package jp.loioz.app.common.api.oauth.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.api.box.constants.BoxConstants.BoxAuthRequestCode;
import jp.loioz.app.common.api.box.form.BoxAuthStateJson;
import jp.loioz.app.common.api.box.service.CommonBoxApiService;
import jp.loioz.app.common.api.dropbox.constants.DropboxConstants.DropboxAuthRequestCode;
import jp.loioz.app.common.api.dropbox.controller.DropboxController;
import jp.loioz.app.common.api.dropbox.form.DropboxAuthStateJson;
import jp.loioz.app.common.api.dropbox.service.CommonDropboxApiService;
import jp.loioz.app.common.api.google.constants.GoogleConstants.GoogleAuthRequestCode;
import jp.loioz.app.common.api.google.form.GoogleAuthStateJson;
import jp.loioz.app.common.api.google.service.CommonGoogleApiService;
import jp.loioz.app.common.api.oauth.form.OAuthStateJson;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.user.fileManageSetting.controller.FileManagementSettingController;
import jp.loioz.app.user.myExternalSetting.controller.MyExternalSettingController;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.ExternalServiceType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * OAuth認証用のコントローラークラス
 * ※@RequestMappingの値（common/api/auth）はBox側のシステムにも登録しているため、値は変えないこと
 */
@Controller
@RequestMapping("common/api/auth")
public class OAuthController extends DefaultController {

	/** 子タブでのみ利用 子タブを閉じて、親タブのリロードを行う */
	public static final String TAB_CLOSE_AND_PARENT_RELOAD_VIEW_PATH = "common/api/oauth/tabCloseAndParentReload";
	/** 子タブでのみ利用 子タブを閉じて、親タブにエラーメッセージを表示 */
	public static final String TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH = "common/api/oauth/tabCloseAndParentShowErrorMsg";

	@Autowired
	private CommonPlanService commonPlanService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	@Autowired
	private CommonGoogleApiService commonGoogleApiService;

	@Autowired
	private CommonBoxApiService commonBoxApiService;

	@Autowired
	private CommonDropboxApiService commonDropboxApiService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 外部サービス連携のプリチェックを行う
	 * 
	 * @param serviceCd
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/storageConnectPreCheck", method = RequestMethod.GET)
	public Map<String, Object> storageConnectPreCheck(@RequestParam("serviceCd") String serviceCd) {

		Map<String, Object> response = new HashMap<>();

		// loiozストレージが有料プランの場合、連携は不可とする
		if (!commonPlanService.nowIsFreeStorage(SessionUtils.getTenantSeq())) {
			response.put("canConnect", false);
			response.put("message", getMessage(MessageEnum.MSG_E00143));
			return response;
		}

		// すでに外部ストレージサービスを利用している場合は、連携は不可
		List<ExternalService> externalService = commonOAuthService.getConnectedExternalService(ExternalServiceType.STORAGE);
		if (!CollectionUtils.isEmpty(externalService)) {
			response.put("canConnect", false);
			response.put("message", getMessage(MessageEnum.MSG_E00140));
			return response;
		}

		response.put("canConnect", true);
		return response;
	}

	/**
	 * GoogleOAuthの認証リクエストをリダイレクトするメソッド
	 * 
	 * @return
	 */
	@RequestMapping(value = "/google", method = RequestMethod.GET)
	public ModelAndView google(@RequestParam("requestCode") String requestCode) {

		GoogleAuthStateJson stateJson = new GoogleAuthStateJson();
		stateJson.setRequestCode(GoogleAuthRequestCode.of(requestCode));

		// UUIDをセッションに設定(CSRF対策)
		this.setOAuthSessionVaule(stateJson.getUuid());

		String authRequestUrl = commonGoogleApiService.createAuthRequestUrl(stateJson);
		return new ModelAndView("redirect:" + authRequestUrl);
	}

	/**
	 * Google認証サーバーからリダイレクトの受け口
	 * 
	 * @param code
	 * @param state
	 * @return
	 */
	@RequestMapping(value = "/googleAuthRedirect", method = RequestMethod.GET)
	public ModelAndView googleAuthRedirect(
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state") String state) {

		if (StringUtils.isEmpty(code)) {
			// とりあえずアクセスの失敗なので、エラーメッセージを表示
			return getMyModelAndView("Googleの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

		// steteをdecodeする
		String jsonStr = HttpUtils.base64UrlDecode(state);

		// Jsonからjavaオブジェクトを作成
		GoogleAuthStateJson stateJson = OAuthStateJson.fromJson(jsonStr, GoogleAuthStateJson.class);

		if (this.equalsByOAuthSessionValue(stateJson.getUuid())) {
			// 有効なリクエストならSessionValueを削除
			this.clearOAuthSessionVaule();
		} else {
			// SessionValueとUUIDが一致しない場合
			return getMyModelAndView("Googleの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

		if (stateJson.getRequestCode() == GoogleAuthRequestCode.FILE_SETTING_CONNECT) {
			// ファイル管理設定側コントローラーに飛ばす
			return ModelAndViewUtils.getRedirectModelAndView(FileManagementSettingController.class, controller -> controller.googleAuthConnect(code));
		} else if (stateJson.getRequestCode() == GoogleAuthRequestCode.USER_SETTING_CONNECT) {
			// アカウント画面側コントローラーに飛ばす
			return ModelAndViewUtils.getRedirectModelAndView(MyExternalSettingController.class, controller -> controller.accountGoogleAuthConnect(code));
		} else {
			// 想定していないパラメータ
			return getMyModelAndView("Googleの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}
	}

	/**
	 * BoxOAuthの認証リクエストをリダイレクトするメソッド
	 * 
	 * @return
	 */
	@RequestMapping(value = "/box", method = RequestMethod.GET)
	public ModelAndView box(@RequestParam("requestCode") String requestCode) {

		BoxAuthStateJson stateJson = new BoxAuthStateJson();
		stateJson.setRequestCode(BoxAuthRequestCode.of(requestCode));

		// UUIDをセッションに設定(CSRF対策)
		this.setOAuthSessionVaule(stateJson.getUuid());

		URL authRequestUrl = commonBoxApiService.createAuthRequestUrl(stateJson);
		return new ModelAndView("redirect:" + authRequestUrl.toString());
	}

	/**
	 * Boxの認証サーバーからリダイレクトの受け口
	 * 
	 * ※@RequestMappingの値（/boxAuthRedirect）はBox側のシステムにも登録しているため、値は変えないこと
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/boxAuthRedirect", method = RequestMethod.GET)
	public ModelAndView boxAuthRedirect(
			@RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state") String state) {

		// エラーが返って来るとメッセージが返ってくるらしい
		if (StringUtils.isNotEmpty(error)) {
			if (error.equals("access_denied")) {
				// 画面で認証を拒否を選択したケース
			} else {
				// 調べたが出てこなかった。
			}
			// とりあえずアクセスの失敗なので、エラーメッセージを表示
			return getMyModelAndView("Boxの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

		// steteをdecodeする
		String jsonStr = HttpUtils.base64UrlDecode(state);

		// Jsonからjavaオブジェクトを作成
		BoxAuthStateJson stateJson = OAuthStateJson.fromJson(jsonStr, BoxAuthStateJson.class);

		if (this.equalsByOAuthSessionValue(stateJson.getUuid())) {
			// 有効なリクエストならSessionValueを削除
			this.clearOAuthSessionVaule();
		} else {
			// SessionValueとUUIDが一致しない場合
			return getMyModelAndView("Boxの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

		if (stateJson.getRequestCode() == BoxAuthRequestCode.SYSTEM_CONNECT) {
			// ファイル管理設定側コントローラーに飛ばす
			return ModelAndViewUtils.getRedirectModelAndView(FileManagementSettingController.class, controller -> controller.boxAuthConnect(code));
		} else if (stateJson.getRequestCode() == BoxAuthRequestCode.USER_CONNECT) {
			// アカウント画面側コントローラーに飛ばす
			return ModelAndViewUtils.getRedirectModelAndView(MyExternalSettingController.class, controller -> controller.accountBoxAuthConnect(code));
		} else {
			// 想定していないパラメータ
			return getMyModelAndView("Boxの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

	}

	/**
	 * DropBoxOAuthの認証リクエストをリダイレクトするメソッド
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dropbox", method = RequestMethod.GET)
	public ModelAndView dropbox(@RequestParam("requestCode") String requestCode) {

		DropboxAuthStateJson stateJson = new DropboxAuthStateJson();
		stateJson.setRequestCode(DropboxAuthRequestCode.of(requestCode));

		// UUIDをセッションに設定(CSRF対策)
		this.setOAuthSessionVaule(stateJson.getUuid());

		String authorizationUrlStr = commonDropboxApiService.createAuthRequestUrl(stateJson);

		return new ModelAndView("redirect:" + authorizationUrlStr);
	}

	/**
	 * Boxの認証サーバーからリダイレクトの受け口(※参照 {@link DropboxController#dropboxAuthGatewayRedirect DropboxController.dropboxAuthGatewayRedirect})
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/dropboxAuthRedirect", method = RequestMethod.GET)
	public ModelAndView dropboxAuthRedirect(
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state") String state) {

		if (StringUtils.isEmpty(code)) {
			// codeが取得できない場合は、エラーとして扱う。
			return getMyModelAndView("Dropboxの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

		// steteをdecodeする
		String jsonStr = HttpUtils.base64UrlDecode(state);

		// Jsonからjavaオブジェクトを作成
		DropboxAuthStateJson stateJson = OAuthStateJson.fromJson(jsonStr, DropboxAuthStateJson.class);

		if (this.equalsByOAuthSessionValue(stateJson.getUuid())) {
			// 有効なリクエストならSessionValueを削除
			this.clearOAuthSessionVaule();
		} else {
			// SessionValueとUUIDが一致しない場合
			return getMyModelAndView("Dropboxの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

		if (stateJson.getRequestCode() == DropboxAuthRequestCode.SYSTEM_CONNECT) {
			// ファイル管理設定側コントローラーに飛ばす
			return ModelAndViewUtils.getRedirectModelAndView(FileManagementSettingController.class, controller -> controller.dropboxAuthConnect(code));
		} else if (stateJson.getRequestCode() == DropboxAuthRequestCode.USER_CONNECT) {
			// アカウント画面側コントローラーに飛ばす
			return ModelAndViewUtils.getRedirectModelAndView(MyExternalSettingController.class, controller -> controller.accountDropboxAuthConnect(code));
		} else {
			// 想定していないパラメータ
			return getMyModelAndView("Dropboxの認証に失敗しました。", TAB_CLOSE_AND_PARENT_SHOW_ERROR_MSG_VIEW_PATH, "message");
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * セッションにUUIDを設定
	 * 
	 * @param uuid
	 */
	private void setOAuthSessionVaule(String uuid) {
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.OAUTH_STATE_PARAMS, uuid);
	}

	/**
	 * セッションのUUIDを削除
	 */
	private void clearOAuthSessionVaule() {
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.OAUTH_STATE_PARAMS, "");
	}

	/**
	 * セッションのUUIDと一致するか
	 * 
	 * @param responseUuid
	 * @return
	 */
	private boolean equalsByOAuthSessionValue(String responseUuid) {
		String sessionUuid = SessionUtils.getSessionVaule(SessionAttrKeyEnum.OAUTH_STATE_PARAMS);
		return responseUuid.equals(sessionUuid);
	}

}
