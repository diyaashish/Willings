package jp.loioz.app.user.myAccountPassword.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.validation.accessDB.CommonAccountValidator;
import jp.loioz.app.user.myAccountPassword.form.MyAccountPasswordInputForm;
import jp.loioz.app.user.myAccountPassword.service.MyAccountPasswordService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.SessionUtils;

/**
 * パスワード変更 画面のコントローラークラス
 */
@Controller
@RequestMapping("user/myAccountPassword")
public class MyAccountPasswordController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/myAccountPassword/myAccountPassword";

	/** パスワード変更fragmentのパス */
	private static final String MY_ACCOUNT_PASSWORD_INPUT_FRAGMENT_PATH = "user/myAccountPassword/myAccountPasswordFragment::myAccountPasswordInputFragment";

	/** パスワード変更画面入力フォームオブジェクト名 */
	private static final String INPUT_FORM_NAME = "inputForm";

	/** パスワード変更フラグメントの入力用フォームオブジェクト名 */
	private static final String ACCOUNT_PASSWORD_INPUT_FORM_NAME = "accountPassWordInputForm";

	/** パスワード変更画面のサービスクラス */
	@Autowired
	private MyAccountPasswordService service;

	/** 共通アカウントDB整合性バリデーター */
	@Autowired
	private CommonAccountValidator commonAccountValidator;

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		MyAccountPasswordInputForm inputForm = new MyAccountPasswordInputForm();

		// アカウントパスワード変更入力オブジェクトの作成
		MyAccountPasswordInputForm.AccountPassWordInputForm accountPassWordInputForm = service.createAccountPassWordInputForm();

		// フォームオブジェクトを設定
		ModelAndView mv = getMyModelAndView(inputForm, MY_VIEW_PATH, INPUT_FORM_NAME);
		mv.addObject(ACCOUNT_PASSWORD_INPUT_FORM_NAME, accountPassWordInputForm);
		return mv;
	}

	/**
	 * アカウントパスワード入力フラグメント画面情報の取得
	 *
	 * @return
	 */
	@RequestMapping(value = "/getMyAccountPasswordInputFragment", method = RequestMethod.GET)
	public ModelAndView getMyAccountPasswordInputFragment() {

		// アカウントパスワード変更入力オブジェクトの作成
		MyAccountPasswordInputForm.AccountPassWordInputForm accountPassWordInputForm = service.createAccountPassWordInputForm();

		// フォームオブジェクトを設定
		this.setAjaxProcResultSuccess();
		return getMyModelAndView(accountPassWordInputForm, MY_ACCOUNT_PASSWORD_INPUT_FRAGMENT_PATH, ACCOUNT_PASSWORD_INPUT_FORM_NAME);
	}

	/**
	 * 保存処理
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/savePassWord", method = RequestMethod.POST)
	public ModelAndView savePassWord(@Validated MyAccountPasswordInputForm.AccountPassWordInputForm inputForm, BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合

			// 入力値は初期化するので、オブジェクトを再作成
			MyAccountPasswordInputForm.AccountPassWordInputForm returnForm = service.createAccountPassWordInputForm();
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(returnForm, MY_ACCOUNT_PASSWORD_INPUT_FRAGMENT_PATH, ACCOUNT_PASSWORD_INPUT_FORM_NAME, result);
		}

		// DBアクセスチェック
		Map<String, String> errorsMap = new HashMap<>();
		if (this.accessDBValidated(inputForm, errorsMap)) {
			// 整合性エラーが発生した場合

			// 入力値は初期化するので、オブジェクトを再作成
			MyAccountPasswordInputForm.AccountPassWordInputForm returnForm = service.createAccountPassWordInputForm();
			this.setAjaxProcResultFailure(errorsMap.get("message"));
			return getMyModelAndViewWithErrors(returnForm, MY_ACCOUNT_PASSWORD_INPUT_FRAGMENT_PATH, ACCOUNT_PASSWORD_INPUT_FORM_NAME, result);
		}

		try {
			// パスワード更新
			service.savePassWord(inputForm);

			setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "パスワード情報"));
			return null;

		} catch (AppException e) {
			setAjaxProcResultSuccess(getMessage(e.getErrorType()));
			return null;
		}

	}

	/**
	 * DBアクセスを伴うチェック
	 *
	 * @param inputForm
	 * @param errorsMap
	 * @return
	 */
	private boolean accessDBValidated(MyAccountPasswordInputForm.AccountPassWordInputForm inputForm, Map<String, String> errorsMap) {

		// パスワードの入力があった場合
		if (!inputForm.isAllEmpty()) {
			// 現在のパスワードの認証を行います
			if (!commonAccountValidator.passWordMatchesValidate(inputForm.getOldPassword(), SessionUtils.getLoginAccountSeq())) {
				errorsMap.put("message", getMessage(MessageEnum.MSG_E00002, "登録されているパスワード", "現在のパスワード"));
				return true;
			}
		}
		return false;
	}

}
