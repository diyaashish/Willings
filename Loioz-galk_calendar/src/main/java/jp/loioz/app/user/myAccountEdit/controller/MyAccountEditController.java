package jp.loioz.app.user.myAccountEdit.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.common.validation.accessDB.CommonAccountValidator;
import jp.loioz.app.user.myAccountEdit.form.MyAccountEditInputForm;
import jp.loioz.app.user.myAccountEdit.form.MyAccountEditViewForm;
import jp.loioz.app.user.myAccountEdit.service.MyAccountEditService;
import jp.loioz.app.user.schedule.controller.ScheduleController;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * 「アカウント情報の設定」画面のコントローラークラス
 */
@Controller
@RequestMapping("user/myAccountEdit")
public class MyAccountEditController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyAccountSettingInputForm";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/myAccountEdit/myAccountEdit";

	/** 個人情報設定入力フラグメントビューパス */
	private static final String MY_ACCOUNT_SETTING_INPUT_FRAGMENT_PATH = "user/myAccountEdit/myAccountEditFragment::myAccountSettingInputFragment";

	/** 弁護士職印フラグメントビューパス */
	private static final String LAWYER_STAMP_INPUT_FRAGMENT_PATH = "user/myAccountEdit/myAccountEditFragment::lawyerStampInputFragment";

	/** コントローラと対応するview名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 個人設定フラグメントの入力フォームオブジェクト */
	private static final String ACCOUNT_SETTING_FRAGMENT_INPUT_FORM_NAME = "accountSettingInputForm";

	/** 弁護士職印フラグメントの入力フォームオブジェクト */
	private static final String LAWYER_STAMP_FRAGMENT_INPUT_FORM_NAME = "lawyerStampInputForm";

	/** アカウント編集画面用一覧画面のサービスクラス */
	@Autowired
	private MyAccountEditService service;

	/** 共通アカウントDB整合性バリデーター */
	@Autowired
	private CommonAccountValidator commonAccountValidator;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView init() {

		ModelAndView mv = null;
		try {
			// 親画面の入力フォームオブジェクト
			MyAccountEditViewForm viewForm = new MyAccountEditViewForm();

			// 個人設定フラグメント入力フォームの作成
			MyAccountEditInputForm.AccountSettingInputForm accountSettingInputForm = service.createAccountSettingInputForm();

			// 弁護士職印フラグメント入力フォームの作成
			MyAccountEditInputForm.LawyerStampInputForm lawyerStampInputForm = service.createLawyerStampInputForm();

			mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
			mv.addObject(ACCOUNT_SETTING_FRAGMENT_INPUT_FORM_NAME, accountSettingInputForm);
			mv.addObject(LAWYER_STAMP_FRAGMENT_INPUT_FORM_NAME, lawyerStampInputForm);

			return mv;

		} catch (AppException e) {
			// エラーが発生した場合、スケジュール画面に遷移する
			return ModelAndViewUtils.getRedirectModelAndView(ScheduleController.class, controller -> controller.index(null));
		}
	}

	/**
	 * 個人情報設定入力フラグメント情報の取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getMyAccountSettingInputFragment", method = RequestMethod.GET)
	public ModelAndView getMyAccountSettingInputFragment() {

		ModelAndView mv = null;
		try {
			// 個人設定フラグメント入力フォームの作成
			MyAccountEditInputForm.AccountSettingInputForm accountSettingInputForm = service.createAccountSettingInputForm();
			mv = getMyModelAndView(accountSettingInputForm, MY_ACCOUNT_SETTING_INPUT_FRAGMENT_PATH, ACCOUNT_SETTING_FRAGMENT_INPUT_FORM_NAME);

		} catch (AppException e) {
			// エラーが発生した場合、スケジュール画面に遷移する
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		if (mv == null) {
			// 画面情報の取得失敗の場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		// 画面情報を返却
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 個人情報設定入力フラグメントの保存処理
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveAccountSetting", method = RequestMethod.POST)
	public ModelAndView saveAccountSetting(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) MyAccountEditInputForm.AccountSettingInputForm inputForm,
			BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispProperties(inputForm);
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, MY_ACCOUNT_SETTING_INPUT_FRAGMENT_PATH, ACCOUNT_SETTING_FRAGMENT_INPUT_FORM_NAME, result);
		}

		// DBアクセスチェック
		Map<String, String> errorsMap = new HashMap<>();
		if (this.accessDBValidated(inputForm, errorsMap)) {
			// 整合性エラーが発生した場合
			service.setDispProperties(inputForm);
			this.setAjaxProcResultFailure(errorsMap.get("message"));
			return getMyModelAndViewWithErrors(inputForm, MY_ACCOUNT_SETTING_INPUT_FRAGMENT_PATH, ACCOUNT_SETTING_FRAGMENT_INPUT_FORM_NAME, result);
		}

		try {
			// 保存処理
			service.saveAccountSetting(inputForm);

			this.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "アカウント情報"));
			return null;

		} catch (AppException e) {
			// エラーが発生した場合
			this.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

	}

	/**
	 * 個人情報設定入力フラグメント情報の取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getLawyerStampInputFragment", method = RequestMethod.GET)
	public ModelAndView getLawyerStampInputFragment() {

		ModelAndView mv = null;
		try {
			// 個人設定フラグメント入力フォームの作成
			MyAccountEditInputForm.LawyerStampInputForm lawyerStampInputForm = service.createLawyerStampInputForm();
			mv = getMyModelAndView(lawyerStampInputForm, LAWYER_STAMP_INPUT_FRAGMENT_PATH, LAWYER_STAMP_FRAGMENT_INPUT_FORM_NAME);

		} catch (AppException e) {
			// エラーが発生した場合、スケジュール画面に遷移する
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		if (mv == null) {
			// 画面情報の取得失敗の場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		// 画面情報を返却
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 個人情報設定入力フラグメントの保存処理
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registLawyerStamp", method = RequestMethod.POST)
	public ModelAndView registLawyerStamp(@Validated MyAccountEditInputForm.LawyerStampInputForm inputForm, BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispProperties(inputForm);
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, LAWYER_STAMP_INPUT_FRAGMENT_PATH, LAWYER_STAMP_FRAGMENT_INPUT_FORM_NAME, result);
		}

		try {
			// 保存処理
			service.registLawyerStamp(inputForm);

			this.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "アップロード"));
			return null;

		} catch (AppException e) {
			// エラーが発生した場合
			this.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

	}

	/**
	 * 更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateLawyerStamp", method = RequestMethod.POST)
	public ModelAndView updateLawyerStamp(@Validated MyAccountEditInputForm.LawyerStampInputForm inputForm, BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispProperties(inputForm);
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, LAWYER_STAMP_INPUT_FRAGMENT_PATH, LAWYER_STAMP_FRAGMENT_INPUT_FORM_NAME, result);
		}

		try {
			// 保存処理
			service.updateLawyerStamp(inputForm);

			this.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "アップロード"));
			return null;

		} catch (AppException e) {
			// エラーが発生した場合
			this.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

	}

	/**
	 * 弁護士職印の削除処理
	 * 
	 * @param accountImgSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteLawyerStamp", method = RequestMethod.POST)
	public ModelAndView deleteLawyerStamp(@RequestParam(name = "accountImgSeq") Long accountImgSeq) {

		try {
			// 削除処理
			service.deleteLawyerStamp(accountImgSeq);

			this.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "弁護士職印"));
			return null;

		} catch (AppException e) {
			// エラーが発生した場合
			this.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * DBアクセスを伴うチェック
	 *
	 * @param inputForm
	 * @param errorsMap
	 * @return
	 */
	private boolean accessDBValidated(MyAccountEditInputForm.AccountSettingInputForm inputForm, Map<String, String> errorsMap) {
		// アカウントSEQ
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		// アカウントIDの重複チェック
		if (commonAccountValidator.checkAccountIdExists(inputForm.getAccountId(), accountSeq)) {
			// アカウントIDがすでに使用されている
			errorsMap.put("message", getMessage(MessageEnum.MSG_E00031, "アカウントID"));
			return true;
		}

		return false;
	}
}