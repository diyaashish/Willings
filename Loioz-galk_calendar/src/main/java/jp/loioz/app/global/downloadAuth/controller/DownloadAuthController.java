package jp.loioz.app.global.downloadAuth.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.global.common.controller.GlobalController;
import jp.loioz.app.global.common.exception.GlobalAuthException;
import jp.loioz.app.global.common.service.CommonGlobalService;
import jp.loioz.app.global.download.controller.DownloadController;
import jp.loioz.app.global.downloadAuth.form.DownloadAuthInputForm;
import jp.loioz.app.global.downloadAuth.form.DownloadAuthViewForm;
import jp.loioz.app.global.downloadAuth.service.DownloadAuthService;
import jp.loioz.common.aspect.annotation.DenySubDomain;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * ダウンロード画面のコントローラークラス
 */
@Controller
@RequestMapping(value = UrlConstant.G_DOWNLOAD_AUTH_URL + "/{tenantAuthKey}")
public class DownloadAuthController extends DefaultController implements GlobalController {

	/** パスワード認証成功HTTPHeader */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_PASSWORD_AUTH_SUCCESS = "password_auth_success";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "global/downloadAuth/downloadAuth";
	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 認証用フラグメントパス */
	private static final String DL_AUTH_FRAGMENT_PASS = "global/downloadAuth/downloadAuthFragment";

	/** 認証パスワード入力フラグメントパス */
	private static final String DL_AUTH_PASSWORD_INPUT_FRAGMENT = DL_AUTH_FRAGMENT_PASS + "::downloadPasswordAuthFragment";
	/** 認証パスワード入力オブジェクト名 */
	private static final String DL_AUTH_PASSWORD_INPUT_FORM_NAME = "downloadPasswordInputForm";

	/** テナントSEQ */
	private static final String TENANT_SEQ = "tenantSeq";

	/** 共通：グローバルサービス */
	@Autowired
	private CommonGlobalService commonGlobalService;

	/** Cookieサービス */
	@Autowired
	private CookieService cookieService;

	/** ダウンロード認証サービスクラス */
	@Autowired
	private DownloadAuthService downloadAuthService;

	/**
	 * テナントSEQの認証
	 * 
	 * @param tenantAuthKey
	 */
	@ModelAttribute(TENANT_SEQ)
	public Long decipherTenantSeq(@PathVariable("tenantAuthKey") String tenantAuthKey) {
		return commonGlobalService.decipherTenantSeq(tenantAuthKey);
	}

	/**
	 * ダウンロード認証画面：認証エラーの場合はNotFoundエラーページに飛ばす
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(GlobalAuthException.class)
	public ModelAndView globalAuthExceptionHandler(GlobalAuthException ex) {
		
		MessageEnum errorTypeEnum = ex.getErrorType();
		
		// エラー画面に表示するメッセージキー情報を取得
		List<String> errorMsgKeyList = this.getErrorMsgKeyList(errorTypeEnum);
		
		// 必ず[0]がタイトル、[1]がエラーメッセージ
		String errorTitle = getMessage(errorMsgKeyList.get(0));
		String errorMsg = getMessage(errorMsgKeyList.get(1));
		
		return this.globalErrorPage(errorTitle, errorMsg);
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示<br>
	 * 認証キーが指定されていない場合なので、エラー画面に遷移する
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {
		// ページが見つかりません
		String errorTitle = getMessage(MessageEnum.MSG_E00175.getMessageKey());
		// URLに誤りがないかご確認をお願いします。
		String errorMsg = getMessage(MessageEnum.MSG_E00192.getMessageKey());
		return this.globalErrorPage(errorTitle, errorMsg);
	}

	/**
	 * メール認証
	 *
	 * @param key 認証キー
	 * @param tenantSeq テナントSEQ
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/{key}/", method = RequestMethod.GET)
	@DenySubDomain
	public ModelAndView verify(
			@PathVariable("tenantAuthKey") String tenantAuthKey,
			@PathVariable("key") String key,
			@ModelAttribute(TENANT_SEQ) Long tenantSeq,
			HttpServletResponse response) {

		ModelAndView mv = null;
		try {
			// テナントDBへの接続
			GlobalController.super.useTenantDB(tenantSeq);

			// アクセスが有効かどうかのチェック
			downloadAuthService.varificationAccessEnableCheck(key);

			if (!downloadAuthService.needPassWord(key)) {
				// 認証トークンの取得
				String verificationToken = downloadAuthService.getVerificationTokenForPassRequired(key);

				// 認証トークンをCookieに追加
				cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_GLOBAL_DOWNLOAD_AUTH_ID, verificationToken, CookieConstant.COOKIE_EXPRIRATION_ONE_DAYS, response);

				// パスワード認証が不要のため、ダウンロード画面に遷移させる
				return ModelAndViewUtils.getRedirectModelAndView(DownloadController.class, (controller) -> controller.downloadList(tenantAuthKey, key, null, null));
			}

			// 画面情報の設定
			mv = getMyModelAndView(new DownloadAuthViewForm(), MY_VIEW_PATH, VIEW_FORM_NAME);

			// 入力フォームの設定
			DownloadAuthInputForm.DownloadPasswordInputForm downloadPasswordInputForm = downloadAuthService.createDownloadPasswordInputForm(key);
			mv.addObject(DL_AUTH_PASSWORD_INPUT_FORM_NAME, downloadPasswordInputForm);

			return mv;
		} finally {
			// カレントスキーマ設定をクリア
			GlobalController.super.clearSchemaContext();
		}

	}

	/**
	 * パスワード認証
	 * 
	 * @param tenantAuthKey
	 * @param key
	 * @param tenantSeq
	 * @param response
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/{key}/passwordAuth", method = RequestMethod.POST)
	@DenySubDomain
	public ModelAndView passwordAuth(
			@PathVariable("tenantAuthKey") String tenantAuthKey,
			@PathVariable("key") String key,
			@ModelAttribute(TENANT_SEQ) Long tenantSeq,
			HttpServletResponse response,
			@Validated DownloadAuthInputForm.DownloadPasswordInputForm inputForm, BindingResult result) {

		try {
			// テナントDBへの接続
			GlobalController.super.useTenantDB(tenantSeq);

			// アクセス有効かどうかのチェック
			downloadAuthService.varificationAccessEnableCheck(key);

			// 入力チェック
			if (result.hasErrors()) {
				inputForm.setPassWord("");

				downloadAuthService.setDispProperties(inputForm);
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndViewWithErrors(inputForm, DL_AUTH_PASSWORD_INPUT_FRAGMENT, DL_AUTH_PASSWORD_INPUT_FORM_NAME, result);
			}

			// 認証
			String verificationToken = downloadAuthService.passwordAuth(tenantSeq, key, inputForm);

			// 認証トークンをCookieに追加
			cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_GLOBAL_DOWNLOAD_AUTH_ID, verificationToken, CookieConstant.COOKIE_EXPRIRATION_ONE_DAYS, response);

			// 認証後、レスポンスにリダイレクトURLを設定
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_PASSWORD_AUTH_SUCCESS, ModelAndViewUtils.getRedirectPath(DownloadController.class, (controller) -> controller.downloadList(tenantAuthKey, key, null, null)));
			return null;

		} catch (AppException ex) {
			// エラー対応
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;

		} finally {
			// カレントスキーマ設定をクリア
			GlobalController.super.clearSchemaContext();
		}

	}

}
