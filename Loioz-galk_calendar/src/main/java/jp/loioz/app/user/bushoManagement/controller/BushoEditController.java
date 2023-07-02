package jp.loioz.app.user.bushoManagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.bushoManagement.form.BushoEditForm;
import jp.loioz.app.user.bushoManagement.service.BushoEditService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;

/**
 * 部門編集画面のcontrollerクラス
 */
@Controller
@RequestMapping("user/bushoManagement/")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class BushoEditController extends DefaultController {
	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/bushoManagement/bushoEdit";

	/** コントローラーに対するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::select-modal";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "bushoEditForm";

	/** 部署 **/
	@Autowired
	private BushoEditService service;

	@ModelAttribute(VIEW_FORM_NAME)
	BushoEditForm setUpEditForm() {
		return service.createViewForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 新規登録モーダルの表示
	 *
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ModelAndView create(BushoEditForm viewForm) {
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * DBアクセスを伴うチェック
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBRegistValidated(BushoEditForm viewForm, Map<String, Object> response) {
		boolean error = false;
		// 登録時件数上限値チェック
		if (!service.registLimitValidate()) {
			// 表示順がすでに使用されている
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.BUSHO_REGIST_LIMIT)));
			error = true;
		}
		return error;
	}

	/**
	 * 新規登録
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	public Map<String, Object> regist(@Validated @ModelAttribute(VIEW_FORM_NAME) BushoEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// DBアクセスチェック
		if (this.accessDBRegistValidated(form, response)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 登録処理
			service.regist(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "部門"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 更新モーダル表示
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "edit", method = RequestMethod.POST)
	public ModelAndView edit(BushoEditForm viewForm, MessageHolder msgHolder) {
		try {
			// 更新するデータをセットする
			viewForm = service.setData(viewForm);

		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 更新
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public Map<String, Object> update(@Validated @ModelAttribute(VIEW_FORM_NAME) BushoEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			// 更新
			service.update(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "部門"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 削除
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Map<String, Object> delete(@Validated @ModelAttribute(VIEW_FORM_NAME) BushoEditForm form, BindingResult result) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 削除
			service.delete(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}
}