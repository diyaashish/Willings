package jp.loioz.app.global.download.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.global.common.controller.GlobalController;
import jp.loioz.app.global.common.exception.GlobalAuthException;
import jp.loioz.app.global.common.exception.GlobalTokenTimeOutException;
import jp.loioz.app.global.common.service.CommonGlobalService;
import jp.loioz.app.global.download.form.DownloadViewForm;
import jp.loioz.app.global.download.service.DownloadService;
import jp.loioz.app.global.downloadAuth.controller.DownloadAuthController;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * ダウンロード画面のコントローラークラス
 */
@Controller
@RequestMapping(value = "global/download/{tenantAuthKey}")
public class DownloadController extends DefaultController implements GlobalController {

	/** パスワード認証成功HTTPHeader */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_AUTH_TIME_OUT = "auth_time_out";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "global/download/download";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** テナントSEQ */
	private static final String TENANT_SEQ = "tenantSeq";

	/** Cookieサービス */
	@Autowired
	private CookieService cookieService;

	/** 共通：グローバルサービス */
	@Autowired
	private CommonGlobalService commonGlobalService;

	/** 画面サービスクラス */
	@Autowired
	private DownloadService service;

	/** ロガー */
	@Autowired
	private Logger logger;
	
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
	 * 認証エラー<br>
	 *
	 * @param ex
	 * @return NotFoundエラーページに飛ばす
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
		return GlobalController.super.globalErrorPage(errorTitle, errorMsg);
	}

	/**
	 * ダウンロード画面の表示
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{key}/downloadList", method = RequestMethod.GET)
	public ModelAndView downloadList(
			@PathVariable("tenantAuthKey") String tenantAuthKey,
			@PathVariable("key") String key,
			@ModelAttribute(TENANT_SEQ) Long tenantSeq,
			HttpServletRequest request) {

		try {
			// テナントSEQを設定
			GlobalController.super.useTenantDB(tenantSeq);

			// アクセスが有効かどうかのチェック
			service.varificationAccessEnableCheck(key);

			// 認証トークンの取得
			String verificationToken = this.getVerificationTokenForCookie(request);

			// 認証トークンの有効チェック
			service.varificationTokenEnabledCheck(key, verificationToken);

			// 画面情報の作成
			DownloadViewForm viewForm = service.createDownloadViewForm(tenantAuthKey, key);

			// 一覧オブジェクトの設定
			DownloadViewForm.DownloadListViewForm downloadListViewForm = service.getDownloadListViewForm(key);
			viewForm.setDownloadListViewForm(downloadListViewForm);

			return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		} catch (GlobalTokenTimeOutException ex) {
			// トークン認証エラーの場合

			// ダウンロード画面に遷移する
			return ModelAndViewUtils.getRedirectModelAndView(DownloadAuthController.class, (controller) -> controller.verify(tenantAuthKey, key, null, null));

		} finally {
			// テナントSEQをクリア
			GlobalController.super.clearSchemaContext();
		}

	}

	/**
	 * ダウンロード処理
	 *
	 * @param key
	 * @param tenantSeq
	 * @param accgDocActSendFileSeq
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/{key}/downloadFile", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void downloadFile(
			@PathVariable("tenantAuthKey") String tenantAuthKey,
			@PathVariable("key") String key,
			@ModelAttribute(TENANT_SEQ) Long tenantSeq,
			@RequestParam("accgDocActSendFileSeq") Long accgDocActSendFileSeq,
			HttpServletRequest request,
			HttpServletResponse response) {

		try {
			// テナントSEQを設定
			GlobalController.super.useTenantDB(tenantSeq);

			// アクセスが有効かどうかのチェック
			service.varificationAccessEnableCheck(key);

			// 認証トークンの取得
			String verificationToken = this.getVerificationTokenForCookie(request);

			// 認証トークンの有効チェック
			service.varificationTokenEnabledCheck(key, verificationToken);

			// ダウンロード処理
			service.downloadFile(key, accgDocActSendFileSeq, response);

		} catch (GlobalTokenTimeOutException ex) {
			// トークン認証エラーの場合

			// ダウンロード画面のURLを設定 -> クライアント側で画面遷移
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_AUTH_TIME_OUT, ModelAndViewUtils.getRedirectPath(DownloadAuthController.class, (controller) -> controller.verify(tenantAuthKey, key, null, null)));

		} catch (AppException ex) {
			// エラー情報の設定
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));

		} finally {
			// テナントSEQをクリア
			GlobalController.super.clearSchemaContext();
		}

	}

	/**
	 * 認証トークンをCookieから取得する
	 *
	 * @param request
	 * @return
	 * @throws GlobalTokenTimeOutException
	 */
	private String getVerificationTokenForCookie(HttpServletRequest request) throws GlobalTokenTimeOutException {
		String verificationToken = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_GLOBAL_DOWNLOAD_AUTH_ID, request);
		if (StringUtils.isEmpty(verificationToken)) {
			logger.warn("グローバルダウンロード画面で、認証トークンの期限切れなどで、Cookieから認証トークンを取得できなかった。");
			throw new GlobalTokenTimeOutException("認証トークンの取得ができませんでした。");
		}

		return verificationToken;
	}

}
