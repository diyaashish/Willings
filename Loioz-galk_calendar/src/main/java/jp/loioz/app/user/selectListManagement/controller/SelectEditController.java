package jp.loioz.app.user.selectListManagement.controller;

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
import jp.loioz.app.user.selectListManagement.form.SelectEditForm;
import jp.loioz.app.user.selectListManagement.service.SelectEditService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;

/**
 * 選択肢マスタ登録/編集画面のcontrollerクラス
 */
@Controller
@RequestMapping("user/selectListManagement")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class SelectEditController extends DefaultController {
	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/selectListManagement/selectEdit";

	/** コントローラーに対するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::select-modal";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "selectEditForm";

	/** 選択肢マスタ画面用のserviceクラス */
	@Autowired
	private SelectEditService service;

	@ModelAttribute(VIEW_FORM_NAME)
	SelectEditForm setUpEditForm() {
		return new SelectEditForm();
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
	public ModelAndView create() {
		// viewFormの作成
		SelectEditForm viewForm = service.createViewForm();

		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 更新モーダル表示
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "edit", method = RequestMethod.POST)
	public ModelAndView edit(SelectEditForm viewForm, MessageHolder msgHolder) {
		// 更新するデータをセットする
		try {
			viewForm = service.setData(viewForm);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * DBアクセスを伴うチェック
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBRegistValidated(SelectEditForm viewForm, Map<String, Object> response) {
		boolean error = false;
		// 登録上限チェック
		if (!service.registLimitValidate()) {
			// 登録上限値を超えている場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.SODAN_KEIRO_REGIST_LIMIT)));
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
	public Map<String, Object> regist(@Validated @ModelAttribute(VIEW_FORM_NAME) SelectEditForm form, BindingResult result) {

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
			response.put("message", getMessage(MessageEnum.MSG_I00020, "相談経路"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
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
	public Map<String, Object> update(@Validated @ModelAttribute(VIEW_FORM_NAME) SelectEditForm form, BindingResult result) {

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
			// 更新処理
			service.update(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "相談経路"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 利用停止処理を行います。
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/stopUsingOk", method = RequestMethod.POST)
	public Map<String, Object> stopUsingOk(@ModelAttribute(VIEW_FORM_NAME) SelectEditForm form, BindingResult result) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 利用停止します。
			service.stopUsing(form.getSelectEditData().getSelectSeq());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00002));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 利用再開処理を行います。
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/restartUsingOk", method = RequestMethod.POST)
	public Map<String, Object> restartUsingOk(@ModelAttribute(VIEW_FORM_NAME) SelectEditForm form, BindingResult result) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 利用再開します。
			service.restartUsing(form.getSelectEditData().getSelectSeq());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00002));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}
}