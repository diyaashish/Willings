package jp.loioz.app.user.azukariItem.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.azukariItem.form.AzukariItemEditForm;
import jp.loioz.app.user.azukariItem.service.AzukariItemEditService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.StringUtils;

/**
 * 預り品登録/編集画面のcontrollerクラス
 */
@Controller
@RequestMapping("user/azukariItem")
public class AzukariItemEditController extends DefaultController {

	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/azukariItem/azukariItemEdit";

	/** コントローラーに対するモーダルのviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::azukariEditModal";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "azukariItemEditForm";

	/** 預り品一覧のserviceクラス */
	@Autowired
	private AzukariItemEditService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 新規登録モーダルの表示
	 *
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ModelAndView create(@RequestParam(name = "ankenId") Long ankenId, MessageHolder msgHolder) {
		// viewFormの作成
		AzukariItemEditForm viewForm = new AzukariItemEditForm();
		try {
			viewForm = service.setInitData(ankenId, null);

		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(getMessage(ex.getErrorType()));
			return getMyModelAndViewByModalError(msgHolder);
		}
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 編集画面
	 *
	 * @param ankenId
	 * @param ankenItemSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView edit(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "ankenItemSeq") Long ankenItemSeq,
			MessageHolder msgHolder) {

		AzukariItemEditForm viewForm = new AzukariItemEditForm();
		try {
			/* 登録データを呼び出しFormに渡す */
			viewForm = service.setInitData(ankenId, ankenItemSeq);

		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(getMessage(ex.getErrorType()));
			return getMyModelAndViewByModalError(msgHolder);
		}
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 登録処理
	 *
	 * @param form
	 * @param result
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(@Validated AzukariItemEditForm form, BindingResult result, MessageHolder msgHolder) {

		Map<String, Object> response = new HashMap<>();
		// 相関バリデート
		this.validateAzukarimoto(form, result);
		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			// 保存処理
			service.save(form);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "預り品"));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 預かり元の必須Validation
	 *
	 * @param form
	 * @param result
	 */
	private void validateAzukarimoto(AzukariItemEditForm form, BindingResult result) {
		// 預かり元が顧客の場合
		if (CommonConstant.TargetType.CUSTOMER.equalsByCode(form.getAzukariItemDto().getAzukariFromType())) {
			if (form.getAzukariItemDto().getAzukariFromCustomerId() == null) {
				result.rejectValue("azukariItemDto.azukariFromCustomerId", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				return;
			}
		}
		// 預かり元が関与者の場合
		else if (CommonConstant.TargetType.KANYOSHA.equalsByCode(form.getAzukariItemDto().getAzukariFromType())) {
			if (form.getAzukariItemDto().getAzukariFromKanyoshaSeq() == null) {
				result.rejectValue("azukariItemDto.azukariFromKanyoshaSeq", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				return;
			}
		}
		// 預かり元が自由入力の場合
		else {
			if (StringUtils.isEmpty(form.getAzukariItemDto().getAzukariFrom())) {
				result.rejectValue("azukariItemDto.azukariFrom", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				return;
			}
		}

	}

	/**
	 * 更新処理
	 *
	 * @param form
	 * @param result
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> update(@Validated AzukariItemEditForm form, BindingResult result, MessageHolder msgHolder) {

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
			response.put("message", getMessage(MessageEnum.MSG_I00023, "預り品"));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 削除処理
	 *
	 * @param form
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(AzukariItemEditForm form, MessageHolder msgHolder) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 削除処理
			service.delete(form);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "預り品"));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

}