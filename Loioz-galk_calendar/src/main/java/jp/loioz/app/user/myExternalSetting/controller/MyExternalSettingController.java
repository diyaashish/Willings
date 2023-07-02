package jp.loioz.app.user.myExternalSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.user.myExternalSetting.form.MyExternalSettingViewForm;
import jp.loioz.app.user.myExternalSetting.service.MyExternalSettingService;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.entity.TAuthTokenEntity;

/**
 * 外部サービス接続画面のコントローラークラス
 */
@Controller
@RequestMapping(UrlConstant.MY_EXTERNAL_SETTING_URL)
public class MyExternalSettingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = UrlConstant.MY_EXTERNAL_SETTING_URL + "/myExternalSetting";
	/** コントローラと対応するview名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** アカウント外部連携情報の表示用Fragmentパス */
	private static final String ACCOUNT_EXTERNAL_CONNECT_VIEW_PATH = UrlConstant.MY_EXTERNAL_SETTING_URL + "/myExternalSettingFragment::accountExternalConnectViewFragment";
	/** アカウント外部連携情報の表示用フォーム名 */
	private static final String ACCOUNT_EXTERNAL_CONNECT_VIEW_FORM_NAME = "accountExternalConnectViewForm";

	/** 別タブでの接続認証で失敗した場合の親タブのJSを発火する処理のviewPath */
	private static final String FAILER_CONNECT_FNC = UrlConstant.MY_EXTERNAL_SETTING_URL + "/failerConnectFnc";
	/** 別タブでの接続認証で成功した場合の親タブのJSを発火する処理のviewPath */
	private static final String SUCCESS_CONNECT_FNC = UrlConstant.MY_EXTERNAL_SETTING_URL + "/successConnectFnc";

	@Autowired
	private MyExternalSettingService service;

	@Autowired
	private CommonOAuthService commonOAuthService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView init() {

		MyExternalSettingViewForm viewForm = service.createViewForm();

		MyExternalSettingViewForm.AccountExternalConnectViewForm accountExternalConnectViewForm = service.createAccountExternalConnectViewForm();
		viewForm.setAccountExternalConnectViewForm(accountExternalConnectViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * アカウント外部連携情報の表示用Viewを取得する
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAccountExternalConnectView", method = RequestMethod.GET)
	public ModelAndView getAccountExternalConnectView() {

		ModelAndView mv = null;

		// アカウント外部連携情報の表示用オブジェクト
		MyExternalSettingViewForm.AccountExternalConnectViewForm accountExternalConnectViewForm = service.createAccountExternalConnectViewForm();
		mv = getMyModelAndView(accountExternalConnectViewForm, ACCOUNT_EXTERNAL_CONNECT_VIEW_PATH, ACCOUNT_EXTERNAL_CONNECT_VIEW_FORM_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * アカウント画面 Google認証処理
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/accountGoogleAuthConnect", method = RequestMethod.GET)
	public ModelAndView accountGoogleAuthConnect(@RequestParam("code") String code) {

		try {
			// Googleの連携処理 （アカウント連携のみ）
			service.accountGoogleAuthConnect(code);

			return getMyModelAndView(getMessage(MessageEnum.MSG_I00129, "Google"), SUCCESS_CONNECT_FNC, "message");
		} catch (AppException e) {
			// エラーハンドリング
			return getMyModelAndView(getMessage(e.getErrorType(), e.getMessageArgs()), FAILER_CONNECT_FNC, "message");
		}

	}

	/**
	 * アカウント画面 Box認証処理
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/accountBoxAuthConnect", method = RequestMethod.GET)
	public ModelAndView accountBoxAuthConnect(@RequestParam("code") String code) {

		try {
			// Boxの連携処理 （アカウント連携のみ）
			service.accountBoxAuthConnect(code);

			return getMyModelAndView(getMessage(MessageEnum.MSG_I00129, "Box"), SUCCESS_CONNECT_FNC, "message");
		} catch (AppException e) {
			// エラーハンドリング
			return getMyModelAndView(getMessage(e.getErrorType(), e.getMessageArgs()), FAILER_CONNECT_FNC, "message");
		}

	}

	/**
	 * アカウント画面 Dropbox認証処理
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/accountDropboxAuthConnect", method = RequestMethod.GET)
	public ModelAndView accountDropboxAuthConnect(@RequestParam("code") String code) {

		try {
			// Dropboxの連携処理 （アカウント連携のみ）
			service.accountDropboxAuthConnect(code);

			return getMyModelAndView(getMessage(MessageEnum.MSG_I00129, "Dropbox"), SUCCESS_CONNECT_FNC, "message");
		} catch (AppException e) {
			// エラーハンドリング
			return getMyModelAndView(getMessage(e.getErrorType(), e.getMessageArgs()), FAILER_CONNECT_FNC, "message");
		}

	}

	/**
	 * 外部連携の連携解除処理
	 * 
	 * @param externalServiceCd
	 * @return
	 */
	@RequestMapping(value = "/disconnectAccountExternalService", method = RequestMethod.POST)
	public ModelAndView disconnectAccountExternalService(@RequestParam(name = "externalService") String externalServiceCd) {

		ExternalService externalService = ExternalService.of(externalServiceCd);
		if (externalService == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		// 連携中のサービスを解除する
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(externalService);
		commonOAuthService.delete(tAuthTokenEntity);

		// 完了メッセージ
		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00130, externalService.getVal()));
		return null;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
