package jp.loioz.app.user.feeMaster.controller;

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
import jp.loioz.app.user.feeMaster.form.FeeMasterInputForm;
import jp.loioz.app.user.feeMaster.service.FeeMasterEditService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 報酬項目の設定画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/feeMaster")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class FeeMasterEditController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyFeeMasterItemEditInputForm";

	/** 報酬項目：項目編集モーダルフラグメントビューパス */
	private static final String FEE_MASTER_ITEM_EDIT_MODAL_FRAGMENT_VIEW_PATH = "user/feeMaster/feeMasterFragment::feeMasterItemEditModalFragment";
	/** 報酬項目：項目編集モーダル入力フォーム名 */
	private static final String FEE_MASTER_ITEM_EDIT_MODAL_INPUT_FORM_NAME = "feeMasterItemEditInputForm";

	/** 報酬項目の設定のサービスクラス */
	@Autowired
	private FeeMasterEditService feeMasterEditService;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬項目の編集モーダル初期表示：作成
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {

		ModelAndView mv = null;

		FeeMasterInputForm.FeeMasterItemEditInputForm inputForm = feeMasterEditService.createFeeMasterItemEditInputForm();
		mv = getMyModelAndView(inputForm, FEE_MASTER_ITEM_EDIT_MODAL_FRAGMENT_VIEW_PATH, FEE_MASTER_ITEM_EDIT_MODAL_INPUT_FORM_NAME);

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬項目の編集モーダル初期表示：編集
	 * 
	 * @param feeItemSeq
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam("feeItemSeq") Long feeItemSeq) {

		ModelAndView mv = null;

		try {
			// 一覧フラグメントの作成
			FeeMasterInputForm.FeeMasterItemEditInputForm inputForm = feeMasterEditService.createFeeMasterItemEditInputForm(feeItemSeq);
			mv = getMyModelAndView(inputForm, FEE_MASTER_ITEM_EDIT_MODAL_FRAGMENT_VIEW_PATH, FEE_MASTER_ITEM_EDIT_MODAL_INPUT_FORM_NAME);

		} catch (AppException ex) {
			// エラーが発生した場合
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬項目の登録処理
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/feeItemRegist", method = RequestMethod.POST)
	public ModelAndView feeItemRegist(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) FeeMasterInputForm.FeeMasterItemEditInputForm inputForm, BindingResult result) {

		// 入力チェック
		if (result.hasErrors()) {
			feeMasterEditService.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, FEE_MASTER_ITEM_EDIT_MODAL_FRAGMENT_VIEW_PATH, FEE_MASTER_ITEM_EDIT_MODAL_INPUT_FORM_NAME, result);
		}

		try {
			// 新規登録
			feeMasterEditService.regist(inputForm);

			setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "報酬項目"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 報酬項目の更新処理
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/feeItemUpdate", method = RequestMethod.POST)
	public ModelAndView feeItemUpdate(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) FeeMasterInputForm.FeeMasterItemEditInputForm inputForm, BindingResult result) {

		// 入力チェック
		if (result.hasErrors()) {
			feeMasterEditService.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, FEE_MASTER_ITEM_EDIT_MODAL_FRAGMENT_VIEW_PATH, FEE_MASTER_ITEM_EDIT_MODAL_INPUT_FORM_NAME, result);
		}

		try {
			// 新規登録
			feeMasterEditService.update(inputForm);

			setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "報酬項目"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		}
	}

	/**
	 * 報酬項目の削除処理
	 * 
	 * @param feeItemSeq
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/feeItemDelete", method = RequestMethod.POST)
	public ModelAndView feeItemDelete(@RequestParam("feeItemSeq") Long feeItemSeq) {

		try {
			// 新規登録
			feeMasterEditService.delete(feeItemSeq);

			setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "報酬項目"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
