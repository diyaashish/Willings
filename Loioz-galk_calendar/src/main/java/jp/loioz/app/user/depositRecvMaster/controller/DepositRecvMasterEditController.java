package jp.loioz.app.user.depositRecvMaster.controller;

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
import jp.loioz.app.user.depositRecvMaster.form.DepositRecvMasterInputFormForm;
import jp.loioz.app.user.depositRecvMaster.service.DepositRecvMasterEditService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 預り金項目の設定：編集
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping("user/depositRecvMaster/")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class DepositRecvMasterEditController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyDepositRecvMasterEditModalInputForm";

	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/depositRecvMaster/depositRecvMasterFragment";

	/** 預り金項目編集モーダルフラグメントパス */
	private static final String DEPOSIT_RECV_MASTER_EDIT_MODAL_FRAGMENT_PATH = MY_VIEW_PATH + "::depositRecvMasterEditModalFragment";
	/** 預り金項目編集モーダル入力フォーム名 */
	private static final String DEPOSIT_RECV_MASTER_EDIT_MODAL_INPUT_FORM_NAME = "depositRecvMasterEditModalInputForm";

	/** 預り金項目マスタ画面編集用のserviceクラス */
	@Autowired
	private DepositRecvMasterEditService service;

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
		DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm = service.createDepositRecvMasterEditModalInputForm();

		super.setAjaxProcResultSuccess();
		return getMyModelAndView(inputForm, DEPOSIT_RECV_MASTER_EDIT_MODAL_FRAGMENT_PATH, DEPOSIT_RECV_MASTER_EDIT_MODAL_INPUT_FORM_NAME);
	}

	/**
	 * 更新モーダル表示
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam Long depositItemSeq) {

		ModelAndView mv = null;

		// 更新するデータをセットする
		try {
			// viewFormの作成
			DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm = service.createDepositRecvMasterEditModalInputForm(depositItemSeq);
			mv = getMyModelAndView(inputForm, DEPOSIT_RECV_MASTER_EDIT_MODAL_FRAGMENT_PATH, DEPOSIT_RECV_MASTER_EDIT_MODAL_INPUT_FORM_NAME);
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
	public ModelAndView regist(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm,
			BindingResult result) {

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndView(inputForm, DEPOSIT_RECV_MASTER_EDIT_MODAL_FRAGMENT_PATH, DEPOSIT_RECV_MASTER_EDIT_MODAL_INPUT_FORM_NAME);
		}

		try {
			// 登録処理
			service.regist(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "預り金／実費項目"));
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
	public ModelAndView update(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm,
			BindingResult result) {

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndView(inputForm, DEPOSIT_RECV_MASTER_EDIT_MODAL_FRAGMENT_PATH, DEPOSIT_RECV_MASTER_EDIT_MODAL_INPUT_FORM_NAME);
		}

		try {
			// 更新処理
			service.update(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "預り金／実費項目"));
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
	public ModelAndView delete(@RequestParam Long depositItemSeq) {

		try {
			// 更新処理
			service.delete(depositItemSeq);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "預り金／実費項目"));
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