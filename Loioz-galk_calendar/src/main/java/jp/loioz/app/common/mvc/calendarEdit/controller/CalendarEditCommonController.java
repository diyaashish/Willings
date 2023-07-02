package jp.loioz.app.common.mvc.calendarEdit.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.calendarEdit.form.CalendarEditModalInputForm;
import jp.loioz.app.common.mvc.calendarEdit.service.CalendarEditCommonService;
import jp.loioz.common.constant.MessageEnum;

/**
 * カレンダー予定編集の共通コントローラー
 */
@Controller
@RequestMapping(value = "common/mvc/calendarEdit")
public class CalendarEditCommonController extends DefaultController {

	/** カレンダー予定編集処理後のレスポンスヘッダー設定値 */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_CALENDAR_EDIT_REDIRECT = "100";

	/** カレンダー予定編集モーダルのコントローラーパス */
	private static final String CALENDAR_EDIT_MODAL_PATH = "common/mvc/calendarEdit/calendarEditModal";
	/** カレンダー予定編集モーダルフラグメントコントローラーパス */
	private static final String CALENDAR_EDIT_MODAL_FRAGMENT_PATH = CALENDAR_EDIT_MODAL_PATH + "::calendarEditModalFragment";

	/** カレンダー予定編集モーダルのフラグメントフォームオブジェクト名 */
	private static final String CALENDAR_EDIT_MODAL_INPUT_FORM_NAME = "calendarEditModalInputForm";

	/** 予定（基本情報）の入力用フォームフラグメントオブジェクト名 */
	private static final String CALENDAR_BASIC_INPUT_FRAGMENT_FORM_NAME = "calendarBasicInputForm";

	/** 予定（施設設定）の入力用フォームフラグメントオブジェクト名 */
	private static final String CALENDAR_SELECTED_OPTION_ROOM_INPUT_FRAGMENT_FORM_NAME = "calendarSelectedOptionRoomListInputForm";

	/** 予定（紐づけ設定）の入力用フォームフラグメントオブジェクト名 */
	private static final String CALENDAR_RELATED_SETTING_INPUT_FRAGMENT_FORM_NAME = "calendarRelatedSettingInputForm";

	/** 予定（招待者）の入力用フォームフラグメントオブジェクト名 */
	private static final String CALENDAR_GUEST_ACCOUNT_INPUT_FRAGMENT_FORM_NAME = "calendarGuestAccountInputForm";

	/** カレンダー予定編集モーダルのサービスクラス */
	@Autowired
	private CalendarEditCommonService service;

	/**
	 * カレンダー予定モーダルの新規作成用
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCalendarEditModalFragment", method = RequestMethod.GET)
	public ModelAndView createCalendarEditModal() {

		ModelAndView mv = null;

		CalendarEditModalInputForm inputForm = service.createCalendarEditModalInputForm();
		mv = getMyModelAndView(inputForm, CALENDAR_EDIT_MODAL_FRAGMENT_PATH, CALENDAR_EDIT_MODAL_INPUT_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		mv.addObject(CALENDAR_BASIC_INPUT_FRAGMENT_FORM_NAME, inputForm.getCalendarBasicInputForm());
		mv.addObject(CALENDAR_SELECTED_OPTION_ROOM_INPUT_FRAGMENT_FORM_NAME, inputForm.getCalendarSelectedOptionRoomListInputForm());
		mv.addObject(CALENDAR_RELATED_SETTING_INPUT_FRAGMENT_FORM_NAME, inputForm.getCalendarRelatedSettingInputForm());
		mv.addObject(CALENDAR_GUEST_ACCOUNT_INPUT_FRAGMENT_FORM_NAME, inputForm.getCalendarGuestAccountInputForm());

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * カレンダー予定-新規登録
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/registSchedule", method = RequestMethod.POST)
	public Map<String, Object> registSchedule(@Validated CalendarEditModalInputForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			service.registCalendarSchedule(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "予定"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
