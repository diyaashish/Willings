package jp.loioz.app.user.bunyaManagement.controller;

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
import jp.loioz.app.user.bunyaManagement.form.BunyaEditForm;
import jp.loioz.app.user.bunyaManagement.service.BunyaEditService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.BunyaType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.dto.BunyaEditDto;

/**
 * 分野の設定編集画面のcontrollerクラス
 */
@Controller
@RequestMapping("user/bunyaManagement")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class BunyaEditController extends DefaultController {
	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/bunyaManagement/bunyaEdit";

	/** コントローラーに対するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::select-modal";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "bunyaEditForm";

	/** 分野マスタ画面用のserviceクラス */
	@Autowired
	private BunyaEditService service;

	@ModelAttribute(VIEW_FORM_NAME)
	BunyaEditForm setUpEditForm() {
		return new BunyaEditForm();
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
		BunyaEditForm viewForm = service.createViewForm();

		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 更新モーダル表示
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "edit", method = RequestMethod.POST)
	public ModelAndView edit(BunyaEditForm viewForm, MessageHolder msgHolder) {
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
	 * @return true:（チェックNG）, false:（チェックOK）
	 */
	private boolean accessDBValidatedForRegist(BunyaEditForm viewForm, Map<String, Object> response) {
		boolean error = false;
		// 分野名重複チェック
		if (service.duplicateByBunyaName(viewForm.getBunyaEditData().getBunyaName())) {
			// 同名の分野が存在する場合
			error = true;
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00038, "分野名"));
			return error;
		}

		// 登録上限チェック
		if (!service.registLimitValidate()) {
			// 登録上限値を超えている場合
			error = true;
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.BUNYA_REGIST_LIMIT)));
			return error;
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
	public Map<String, Object> regist(@Validated @ModelAttribute(VIEW_FORM_NAME) BunyaEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForRegist(form, response)) {
			// エラーが存在する場合
			return response;
		}

		// DBアクセスチェック
		if (this.accessDBValidatedForRegist(form, response)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 登録処理
			service.regist(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "分野"));
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
	public Map<String, Object> update(@Validated @ModelAttribute(VIEW_FORM_NAME) BunyaEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForUpdate(form, response)) {
			// エラーが存在する場合
			return response;
		}

		// DBアクセスチェック
		if (this.accessDBValidatedForUpdate(form, response)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 更新処理
			service.update(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "分野"));
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
	public Map<String, Object> stopUsingOk(@ModelAttribute(VIEW_FORM_NAME) BunyaEditForm form, BindingResult result) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 利用停止します。
			service.stopUsing(form.getBunyaEditData().getBunyaId());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00045, "利用を停止"));
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
	public Map<String, Object> restartUsingOk(@ModelAttribute(VIEW_FORM_NAME) BunyaEditForm form, BindingResult result) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 利用再開します。
			service.restartUsing(form.getBunyaEditData().getBunyaId());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00045, "利用を再開"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 削除（論理削除）
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Map<String, Object> delete(@ModelAttribute(VIEW_FORM_NAME) BunyaEditForm form, BindingResult result) {
		Map<String, Object> response = new HashMap<>();

		// DBアクセスチェック
		if (this.accessDBValidatedForDelete(form, response)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 更新処理
			service.delete(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "分野"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * // 入力項目値のバリデーションチェックを行います。
	 *
	 * @param editData
	 * @param response
	 * @return true:（チェックNG）, false:（チェックOK）
	 */
	private boolean inputFormValidatedForRegist(BunyaEditForm editForm, Map<String, Object> response) {
		boolean error = false;

		BunyaEditDto editData = editForm.getBunyaEditData();

		// 区分存在チェック（分野）
		if (this.validCheckByBunyaType(editData.getBunyaType())) {
			error = true;
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return error;
		}

		return error;
	}

	/**
	 * // 入力項目値のバリデーションチェックを行います。(更新用)
	 *
	 * @param editData
	 * @param response
	 * @return true:（チェックNG）, false:（チェックOK）
	 */
	private boolean inputFormValidatedForUpdate(BunyaEditForm editForm, Map<String, Object> response) {
		boolean error = false;

		// 更新データ取得
		BunyaEditDto editData = editForm.getBunyaEditData();

		// 区分存在チェック（分野）
		if (this.validCheckByBunyaType(editData.getBunyaType())) {
			// 区分が存在しない値
			error = true;
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return error;
		}

		return error;
	}

	/**
	 * DBアクセスを伴うチェック(更新用)
	 *
	 * @param viewForm
	 * @param response
	 * @return true:（チェックNG）, false:（チェックOK）
	 */
	private boolean accessDBValidatedForUpdate(BunyaEditForm viewForm, Map<String, Object> response) {
		boolean error = false;
		// 登録データ取得
		BunyaEditDto editData = viewForm.getBunyaEditData();

		// 分野名重複チェック
		if (service.duplicateByNotBunyaIdAndBunyaName(editData.getBunyaId(), editData.getBunyaName())) {
			// 同名の分野が存在する場合
			error = true;
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00038, "分野名"));
			return error;
		}

		// 分野区分変更可否チェック
		if (service.checkTAnkenExistBunyaIdForUpdate(editData)) {
			// 分野が案件テーブルに存在する場合
			error = true;
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00159));
			return error;
		}

		return error;
	}

	/**
	 * DBアクセスを伴うチェック(削除用)
	 *
	 * @param viewForm
	 * @param response
	 * @return true:（チェックNG）, false:（チェックOK）
	 */
	private boolean accessDBValidatedForDelete(BunyaEditForm viewForm, Map<String, Object> response) {
		boolean error = false;
		// 削除データ取得
		BunyaEditDto editData = viewForm.getBunyaEditData();

		// 案件テーブル分野存在チェック
		if (service.checkTAnkenExistBunyaId(editData.getBunyaId())) {
			// 分野が案件テーブルに存在する場合
			error = true;
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00160));
			return error;
		}

		return error;
	}

	/**
	 * 分野区分の値が正しい値かチェック処理を行います。
	 *
	 * @param bunyaType
	 * @param result
	 * @return true:（チェックNG）, false:（チェックOK）
	 */
	private boolean validCheckByBunyaType(String bunyaType) {
		return !DefaultEnum.hasCode(BunyaType.class, bunyaType);
	}

}