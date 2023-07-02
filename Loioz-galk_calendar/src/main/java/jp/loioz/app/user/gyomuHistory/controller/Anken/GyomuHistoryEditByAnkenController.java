package jp.loioz.app.user.gyomuHistory.controller.Anken;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryEditByAnkenInputForm;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryEditByAnkenViewForm;
import jp.loioz.app.user.gyomuHistory.service.Anken.GyomuHistoryEditByAnkenService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;

/**
 * 業務履歴 案件側 登録・編集Controller
 * 
 * <pre>
 * Sessionに保持する(右から出てくる)業務履歴は別Controllerを用意する想定
 * </pre>
 */
@Controller
@RequestMapping("user/gyomuHistory/anken")
public class GyomuHistoryEditByAnkenController extends DefaultController {

	/** 業務履歴 案件側のモーダルAjaxパス **/
	public static final String MODAL_CONTENT_PATH = "user/gyomuHistory/anken/gyomuHistoryEditByAnken::select-modal";

	/** viewで使用するフォームオブジェクト名 **/
	public static final String VIEW_FORM_NAME = "gyomuHistoryEditByAnkenViewForm";

	/** 業務履歴 案件側 編集画面のサービスクラス */
	@Autowired
	private GyomuHistoryEditByAnkenService service;

	/** 編集画面のModelAttribute */
	@ModelAttribute(VIEW_FORM_NAME)
	GyomuHistoryEditByAnkenViewForm setUpViewForm(@RequestParam(name = "ankenId") Long ankenId) {
		return service.createViewForm(ankenId);
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 新規登録モーダル表示処理
	 * 
	 * @param viewForm
	 * @return Modal画面
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView create(
			@ModelAttribute(VIEW_FORM_NAME) GyomuHistoryEditByAnkenViewForm viewForm) {

		// 初期値の設定をする
		GyomuHistoryEditByAnkenInputForm inputForm = service.createInputForm(viewForm.getAnkenId());
		viewForm.setInputForm(inputForm);

		return getMyModelAndView(viewForm, MODAL_CONTENT_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 編集モーダル表示処理
	 * 
	 * @param viewForm
	 * @return Modal画面
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public ModelAndView edit(
			@ModelAttribute(VIEW_FORM_NAME) GyomuHistoryEditByAnkenViewForm viewForm, MessageHolder msgHolder) {

		GyomuHistoryEditByAnkenInputForm inputForm = new GyomuHistoryEditByAnkenInputForm();

		// 業務履歴SEQをキーとして、編集情報を設定する
		try {
			service.setGyomuHistoryEditDatas(viewForm.getGyomuHistorySeq(), viewForm.getAnkenId(), inputForm);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}
		viewForm.setInputForm(inputForm);

		return getMyModelAndView(viewForm, MODAL_CONTENT_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public Map<String, Object> regist(@Validated @ModelAttribute(VIEW_FORM_NAME) GyomuHistoryEditByAnkenViewForm viewForm, BindingResult result) {

		// 各Formの設定
		GyomuHistoryEditByAnkenInputForm inputForm = viewForm.getInputForm(); // 入力値
		Map<String, Object> response = new HashMap<>(); // 返却値

		// バリデーション処理
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 登録処理
		try {
			service.createGyomuHistory(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "業務履歴"));
			return response;
		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 更新処理
	 * 
	 * @param viewForm
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@Validated @ModelAttribute(VIEW_FORM_NAME) GyomuHistoryEditByAnkenViewForm viewForm, BindingResult result) {

		// 各Formの設定
		GyomuHistoryEditByAnkenInputForm inputForm = viewForm.getInputForm(); // 入力値
		Map<String, Object> response = new HashMap<>(); // 返却値

		// バリデーション処理
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 更新処理
		try {
			service.updateGyomuHistory(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "業務履歴"));
			return response;
		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * ステータス更新処理
	 * 
	 * @param viewForm
	 * @param inputForm
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
	public Map<String, Object> updateStatus(@ModelAttribute(VIEW_FORM_NAME) GyomuHistoryEditByAnkenViewForm viewForm) {

		// 各Formの設定
		GyomuHistoryEditByAnkenInputForm inputForm = viewForm.getInputForm(); // 入力値
		Map<String, Object> response = new HashMap<>(); // 返却値

		try {
			// 更新処理
			service.updateGyomuHistoryStatus(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "業務履歴"));
			return response;
		} catch (AppException e) {
			// エラー処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 削除処理
	 * 
	 * <pre>
	 * 削除処理の為、バリデーション処理は行わない
	 * </pre>
	 * 
	 * @param inputForm
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Map<String, Object> delete(@ModelAttribute(VIEW_FORM_NAME) GyomuHistoryEditByAnkenViewForm viewForm) {

		// 各Formの設定
		GyomuHistoryEditByAnkenInputForm inputForm = viewForm.getInputForm(); // 入力値
		Map<String, Object> response = new HashMap<>(); // 返却値

		// SEQが受け取れなかった場合
		if (Objects.isNull(viewForm.getGyomuHistorySeq())) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00004));
			return response;
		}

		// 削除処理
		try {
			service.deleteGyomuHistory(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "業務履歴"));
			return response;
		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
