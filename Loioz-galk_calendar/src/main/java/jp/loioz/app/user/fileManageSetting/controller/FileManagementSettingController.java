package jp.loioz.app.user.fileManageSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.user.fileManageSetting.form.FileManagementSettingViewForm;
import jp.loioz.app.user.fileManageSetting.service.FileManagementSettingService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.TAuthTokenEntity;

/**
 * ファイル管理設定画面のコントローラークラス
 */
@Controller
@RequestMapping("user/fileManagementSetting")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class FileManagementSettingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/fileManagementSetting/fileManagementSetting";
	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 外部ストレージ選択用Fragmentパス */
	private static final String EXTERNAL_STORAGE_SELECT_VIEW_PATH = "user/fileManagementSetting/fileManagementSettingFragment::externalStorageSelectViewFragment";
	/** 外部ストレージ選択用フォーム名 */
	private static final String EXTERNAL_STORAGE_SELECT_VIEW_FORM_NAME = "externalStorageSelectViewForm";

	/** 別タブでの接続認証で失敗した場合の親タブのJSを発火する処理のviewPath */
	private static final String FAILER_CONNECT_FNC = "user/fileManagementSetting/failerConnectFnc";
	/** 別タブでの接続認証で成功した場合の親タブのJSを発火する処理のviewPath */
	private static final String SUCCESS_CONNECT_FNC = "user/fileManagementSetting/successConnectFnc";

	/** 事務所設定画面のサービスクラス */
	@Autowired
	private FileManagementSettingService service;

	@Autowired
	private CommonOAuthService commonOAuthService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * ファイル管理設定画面のインデックスメソッド
	 * 
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		FileManagementSettingViewForm viewForm = service.createViewForm();

		FileManagementSettingViewForm.ExternalStorageSelectViewForm externalStorageSelectViewForm = service.createExternalStorageSelectViewForm();
		this.setApiData(externalStorageSelectViewForm);

		viewForm.setExternalStorageSelectViewForm(externalStorageSelectViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * GoogleOAuth認証完了後のファイルサービス連携処理
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/googleAuthConnect", method = RequestMethod.GET)
	public ModelAndView googleAuthConnect(@RequestParam("code") String code) {

		try {
			// Google初期連携処理
			service.googleAuthConnect(code);

			return getMyModelAndView(getMessage(MessageEnum.MSG_I00129, "Google"), SUCCESS_CONNECT_FNC, "message");
		} catch (AppException e) {
			// エラーハンドリング
			return getMyModelAndView(getMessage(e.getErrorType(), e.getMessageArgs()), FAILER_CONNECT_FNC, "message");
		}
	}

	/**
	 * BoxOAuth認証完了後のファイルサービス連携処理
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/boxAuthConnect", method = RequestMethod.GET)
	public ModelAndView boxAuthConnect(@RequestParam("code") String code) {

		try {
			// Box初期連携処理
			service.boxAuthConnect(code);

			return getMyModelAndView(getMessage(MessageEnum.MSG_I00129, "Box"), SUCCESS_CONNECT_FNC, "message");
		} catch (AppException e) {
			// エラーハンドリング
			return getMyModelAndView(getMessage(e.getErrorType(), e.getMessageArgs()), FAILER_CONNECT_FNC, "message");
		}
	}

	/**
	 * DropboxOAuth認証完了後のファイルサービス連携処理
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/dropboxAuthConnect", method = RequestMethod.GET)
	public ModelAndView dropboxAuthConnect(@RequestParam("code") String code) {

		try {
			// Dropbox初期連携処理
			service.dropboxAuthConnect(code);

			return getMyModelAndView(getMessage(MessageEnum.MSG_I00129, "DropBox"), SUCCESS_CONNECT_FNC, "message");
		} catch (AppException e) {
			// エラーハンドリング
			return getMyModelAndView(getMessage(e.getErrorType(), e.getMessageArgs()), FAILER_CONNECT_FNC, "message");
		}

	}

	/**
	 * 外部ストレージサービスの連携解除処理
	 * 
	 * @return
	 */
	@RequestMapping(value = "/disconnectExternalStorage", method = RequestMethod.POST)
	public ModelAndView disconnectExternalStorage() {

		try {
			ExternalService connectedStorageService = service.getConnectStorageService();

			// 連携解除処理
			service.disconnectExternalStorage();

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00130, connectedStorageService.getVal()));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

	}

	/**
	 * フォルダの再作成処理
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reCreateRootFolder", method = RequestMethod.POST)
	public ModelAndView reCreateRootFolder() {

		ExternalService externalService = service.getConnectStorageService();
		if (externalService == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		try {
			// トークンのリフレッシュ処理
			commonOAuthService.refresh(externalService);

			// フォルダの再作成処理
			service.reCreateRootFolder(externalService);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00039, "フォルダの再作成"));
			return null;

		} catch (AppException e) {
			// エラーメッセージを画面に表示
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 外部ストレージ選択フラグメントの取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getExternalStorageSelectView", method = RequestMethod.GET)
	public ModelAndView getExternalStorageSelectView() {

		ModelAndView mv = null;

		FileManagementSettingViewForm.ExternalStorageSelectViewForm externalStorageSelectViewForm = service.createExternalStorageSelectViewForm();
		this.setApiData(externalStorageSelectViewForm);

		mv = getMyModelAndView(externalStorageSelectViewForm, EXTERNAL_STORAGE_SELECT_VIEW_PATH, EXTERNAL_STORAGE_SELECT_VIEW_FORM_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * APIによるルートフォルダ情報用フォームオブジェクトを作成
	 * 
	 * @return
	 */
	private void setApiData(FileManagementSettingViewForm.ExternalStorageSelectViewForm externalStorageSelectViewForm) {

		ExternalService externalService = ExternalService.of(externalStorageSelectViewForm.getStorageType());
		if (externalService != null) {

			TAuthTokenEntity auth = commonOAuthService.getMyAuthToken(externalService);
			if (auth == null) {
				// 認証情報がない場合はinfoメッセージを表示する
				String message = getMessage(MessageEnum.MSG_E00141, "/" + UrlConstant.MY_EXTERNAL_SETTING_URL + "/");
				// 改行コードをbrタグに置換（このメッセージはHTML形式で表示するため）
				message = StringUtils.lineBreakCode2Br(message);
				externalStorageSelectViewForm.setInfoMsg(message);
				return;
			}

			// このtry-catchではAPI処理のみ行う
			try {
				// トークンのリフレッシュ処理
				commonOAuthService.refresh(externalService);
				// APIデータ情報の取得
				service.setAPIData(externalService, externalStorageSelectViewForm);

			} catch (AppException e) {
				// エラーメッセージを画面に表示
				MessageEnum messageEnum = e.getErrorType();
				String message = null;
				if (MessageEnum.MSG_E00113 == messageEnum) {
					// Boxエラー：フォルダが見つからない場合（削除されているなど）
					message = getMessage(MessageEnum.MSG_E00142);
					// フォルダの再作成ボタン表示
					externalStorageSelectViewForm.setNotFount(true);
				} else if (MessageEnum.MSG_E00120 == messageEnum) {
					// Dropboxエラー：フォルダが見つからない場合（削除されているなど）
					message = getMessage(MessageEnum.MSG_E00142);
					// フォルダの再作成ボタン表示
					externalStorageSelectViewForm.setNotFount(true);
				} else if (MessageEnum.MSG_E00134 == messageEnum) {
					// Googleエラー：フォルダが見つからない場合（削除されているなど）
					message = getMessage(MessageEnum.MSG_E00142);
					// フォルダの再作成ボタン表示
					externalStorageSelectViewForm.setNotFount(true);
				} else {
					message = getMessage(messageEnum, e.getMessageArgs());
				}
				// 改行コードをbrタグに置換（このメッセージはHTML形式で表示するため）
				message = StringUtils.lineBreakCode2Br(message);
				externalStorageSelectViewForm.setApiErrorMsg(message);
			}

		}
	}
}
