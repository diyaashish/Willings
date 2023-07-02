package jp.loioz.app.user.funcOldKaikeiSetting.controller;

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
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingInputForm;
import jp.loioz.app.user.funcOldKaikeiSetting.service.FuncOldKaikeiSettingEditService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 入出金項目の設定：編集
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.FUNC_OLD_KAIKEI_SETTING_URL + "/")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class FuncOldKaikeiSettingEditController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyFuncOldKaikeiSettingEditModalInputForm";

	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/funcOldKaikeiSetting/funcOldKaikeiSettingFragment";

	/** 入出金項目編集モーダルフラグメントパス */
	private static final String FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_FRAGMENT_PATH = MY_VIEW_PATH + "::funcOldKaikeiSettingEditModalFragment";
	/** 入出金項目編集モーダル入力フォーム名 */
	private static final String FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_INPUT_FORM_NAME = "funcOldKaikeiSettingEditModalInputForm";

	/** 入出金項目マスタ画面編集用のserviceクラス */
	@Autowired
	private FuncOldKaikeiSettingEditService service;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 新規登録モーダルの表示
	 *
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.GET)
	public ModelAndView create() {

		// viewFormの作成
		FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm = service.createFuncOldKaikeiSettingEditModalInputForm();

		super.setAjaxProcResultSuccess();
		return getMyModelAndView(inputForm, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_FRAGMENT_PATH, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_INPUT_FORM_NAME);
	}

	/**
	 * 更新モーダル表示
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam Long nyushukkinKomokuId) {

		ModelAndView mv = null;

		// 更新するデータをセットする
		try {
			// viewFormの作成
			FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm = service.createFuncOldKaikeiSettingEditModalInputForm(nyushukkinKomokuId);
			mv = getMyModelAndView(inputForm, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_FRAGMENT_PATH, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_INPUT_FORM_NAME);
		} catch (AppException ex) {
			// エラーメッセージを
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 新規登録
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	public ModelAndView regist(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm,
			BindingResult result) {

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndView(inputForm, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_FRAGMENT_PATH, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_INPUT_FORM_NAME);
		}

		try {
			// 登録処理
			service.regist(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "入出金項目"));
			return null;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 更新
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public ModelAndView update(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm,
			BindingResult result) {

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndView(inputForm, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_FRAGMENT_PATH, FUNC_OLD_KAIKEI_SETTING_EDIT_MODAL_INPUT_FORM_NAME);
		}

		try {
			// 更新処理
			service.update(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "入出金項目"));
			return null;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}
	}

	/**
	 * 利用停止／再開
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "using", method = RequestMethod.POST)
	public ModelAndView using(@RequestParam Long nyushukkinKomokuId) {

		try {
			// 更新処理
			service.using(nyushukkinKomokuId);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00054, "入出金項目"));
			return null;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}
	}

	/**
	 * 削除（論理削除）
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public ModelAndView delete(@RequestParam Long nyushukkinKomokuId) {

		try {
			// 更新処理
			service.delete(nyushukkinKomokuId);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "入出金項目"));
			return null;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}