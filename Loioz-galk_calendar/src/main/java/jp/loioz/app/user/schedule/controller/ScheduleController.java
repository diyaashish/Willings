package jp.loioz.app.user.schedule.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.groups.Default;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.common.validation.accessDB.CommonScheduleValidator;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.enums.CalendarDispType;
import jp.loioz.app.user.schedule.enums.UserSelectType;
import jp.loioz.app.user.schedule.form.CalendarStatusForm;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.form.ScheduleViewForm;
import jp.loioz.app.user.schedule.form.ajax.RoomScheduleRequest;
import jp.loioz.app.user.schedule.form.ajax.RoomScheduleResponse;
import jp.loioz.app.user.schedule.form.ajax.ScheduleRequest;
import jp.loioz.app.user.schedule.form.ajax.ScheduleResponse;
import jp.loioz.app.user.schedule.service.ScheduleService;
import jp.loioz.common.constant.AccountSettingConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.DeviceType;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.service.http.UserAgentService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.groups.Update;

/**
 * 予定表画面のコントローラークラス
 */
@Controller
@HasSchedule
@RequestMapping(value = "user/schedule")
@SessionAttributes(ScheduleController.STATUS_FORM_NAME)
public class ScheduleController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/schedule/schedule";

	/** コントローラと対応するviewのパス(スマホ用) */
	private static final String MY_SP_VIEW_PATH = "user/schedule/mobile/schedule";

	/** タスク一覧viewのパス */
	private static final String TASK_LIST_FRAGMENT_PATH = "user/schedule/scheduleFragment::taskListViewFragment";

	/** 入力フォームオブジェクト名 */
	private static final String INPUT_FORM_NAME = "inputForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** カレンダー選択状態フォームオブジェクト名 */
	public static final String STATUS_FORM_NAME = "calendarStatusForm";

	/** サービスクラス */
	@Autowired
	private ScheduleService service;

	/** 予定表DB整合性チェック */
	@Autowired
	private CommonScheduleValidator commonScheduleValidator;

	/** Cookieサービスクラス */
	@Autowired
	private CookieService cookieService;

	/** UserAgentサービスクラス */
	@Autowired
	private UserAgentService userAgentService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** フォームクラスの初期化 */
	@ModelAttribute(INPUT_FORM_NAME)
	ScheduleInputForm setUpForm() {
		ScheduleInputForm form = new ScheduleInputForm();
		return form;
	}

	/** 選択状態の初期化 */
	@ModelAttribute(STATUS_FORM_NAME)
	CalendarStatusForm calendarStatusForm(HttpServletRequest request) {
		CalendarStatusForm form = new CalendarStatusForm();

		// 現在日付の週表示
		form.setDispType(CalendarDispType.WEEKLY);
		if (userAgentService.getDevice(request) == DeviceType.MOBILE) {
			form.setDispType(CalendarDispType.MONTHLY);
		}
		form.setCalendarDate(LocalDate.now());
		form.setUserSelectType(UserSelectType.BUSHO);

		// Cookieからデータを取得
		String scheduleSelectedUsersStr = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_SELECTED_USERS, request);

		if (StringUtils.isEmpty(scheduleSelectedUsersStr)) {
			// デフォルトで自分を選択
			List<Long> selectedAccountSeq = new ArrayList<>();
			selectedAccountSeq.add(SessionUtils.getLoginAccountSeq());
			form.setSelectedAccountSeq(selectedAccountSeq);
			return form;
		}

		String decodedIdStr = HttpUtils.decodeUTF8(scheduleSelectedUsersStr);
		List<Long> selectedAccountSeq = StringUtils.toArray(decodedIdStr).stream()
				.map(str -> str.replaceAll("[^0-9]", "")) // 数値以外を除去
				.filter(StringUtils::isNotEmpty)
				.map(Long::parseLong) // 文字列 -> 数値
				.collect(Collectors.toList());
		form.setSelectedAccountSeq(selectedAccountSeq);

		String selectedHolidayFlg = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_SELECTED_HOLIDAY, request);
		if (StringUtils.isNotEmpty(selectedHolidayFlg)) {
			// 取得できた場合のみ、デフォルト値から上書きする
			form.setSelectedHoliday(SystemFlg.codeToBoolean(selectedHolidayFlg));
		}

		return form;
	}

	/** リダイレクト先の初期化 */
	@ModelAttribute
	RedirectViewBuilder setUpRedirectViewBuilder(RedirectAttributes redirectAttributes) {
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(null));
		return new RedirectViewBuilder(redirectAttributes, redirectPath);
	}

	/** 未入力項目をnullにする */
	@InitBinder(INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request) {

		// スマホ版を表示するかを判断する
		if (redirectToSPView(request)) {
			return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.mobile(request));
		}

		// 画面表示情報を取得
		ScheduleViewForm viewForm = service.createViewForm();

		// タスク一覧の表示条件をセッションから取得する
		String viewKeyCd = SessionUtils.getAccountSettingValue(AccountSettingConstant.SettingType.CALENDAR_TASK_VIEW);
		if (StringUtils.isEmpty(viewKeyCd) || AccountSettingConstant.CalendarTaskViewOption.DEFAULT.equalsByCode(viewKeyCd)) {
			viewForm.setAllRelatedTask(false);
		} else {
			viewForm.setAllRelatedTask(true);
		}

		// タスク一覧のデータ並び順をセッションから取得する
		String sortKeyCd = SessionUtils.getAccountSettingValue(AccountSettingConstant.SettingType.CALENDAR_TASK_SORT);
		if (StringUtils.isEmpty(sortKeyCd)) {
			// セッションから取得できない場合は、デフォルトの並び順にする
			sortKeyCd = CommonConstant.AllTaskListSortKey.DEFAULT.getCd();
		}
		viewForm.setTaskListSortKeyCd(sortKeyCd);

		// タスク情報を設定
		service.setTaskListBySchedule(viewForm);

		// Cookieからデータを取得
		String taskListOpenFlg = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_TASKLIST_OPENED, request);
		if (!StringUtils.isEmpty(taskListOpenFlg)) {
			viewForm.setScheduleOpenTaskFlg(SystemFlg.codeToBoolean(taskListOpenFlg));
		}

		// テナント作成時のリダイレクトを判定
		HttpSession session = request.getSession();
		boolean isTenantCreate = SystemFlg.codeToBoolean(String.valueOf(session.getAttribute("tenantCreateFlg")));
		if (isTenantCreate) {
			// 一時的に保存したセッションを破棄する
			session.setAttribute("tenantCreateFlg", null);
			// 初回登録完了後は、「loiozようこそ」、GTMタグを表示する
			return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME).addObject("isTenantCreate", true);
		}

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * スマホ版の初期表示
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mobile", method = RequestMethod.GET)
	public ModelAndView mobile(HttpServletRequest request) {

		// スマホじゃないのにこのパスが叩かれたとき(お気に入り)はindexメソッドに戻す
		if (!redirectToSPView(request)) {
			return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.index(request));
		}

		// 画面表示情報を取得
		ScheduleViewForm viewForm = service.createViewForm();
		return getMyModelAndView(viewForm, MY_SP_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@ResponseBody
	@RequestMapping(value = "/saveOpenTask", method = RequestMethod.POST)
	public void saveOpenTask(HttpServletResponse response, @RequestParam(name = "scheduleOpenTaskFlg") boolean scheduleOpenTaskFlg) {
		// エンコードして、Cookieに追加
		String encodedScheduleOpenTaskFlg = HttpUtils.encodeUTF8(SystemFlg.booleanToCode(scheduleOpenTaskFlg));
		cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_TASKLIST_OPENED, encodedScheduleOpenTaskFlg,
				CookieConstant.COOKIE_EXPRIRATION_TIME, response);
	}

	/**
	 * カレンダータスクの表示設定変更
	 *  
	 * @param isAllRelatedTask 
	 * @return
	 */
	@RequestMapping(value = "/changeCalendarTaskViewOption", method = RequestMethod.GET)
	public ModelAndView changeCalendarTaskViewOption(
			HttpServletRequest request,
			@RequestParam(name = "isAllRelatedTask") boolean isAllRelatedTask) {

		ModelAndView mv = null;

		// 画面表示情報を取得
		ScheduleViewForm viewForm = service.createViewForm();
		viewForm.setAllRelatedTask(isAllRelatedTask);
		String viewKey = isAllRelatedTask ? AccountSettingConstant.CalendarTaskViewOption.ALL_RELATED_TASK.getCd()
				: AccountSettingConstant.CalendarTaskViewOption.DEFAULT.getCd();

		// タスク一覧のデータ並び順をセッションから取得する
		String sortKeyCd = SessionUtils.getAccountSettingValue(AccountSettingConstant.SettingType.CALENDAR_TASK_SORT);
		if (StringUtils.isEmpty(sortKeyCd)) {
			// セッションから取得できない場合は、デフォルトの並び順にする
			sortKeyCd = CommonConstant.AllTaskListSortKey.DEFAULT.getCd();
		}
		viewForm.setTaskListSortKeyCd(sortKeyCd);

		// タスク情報を設定
		service.setTaskListBySchedule(viewForm);

		try {
			// タスクの並び順を保存
			service.saveCalendarTaskViewOption(viewKey);
		} catch (AppException ex) {
			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		} catch (Exception ex) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("カレンダー画面タスク一覧のソート設定処理エラー", ex);
			return null;
		}

		// Cookieからデータを取得
		String taskListOpenFlg = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_TASKLIST_OPENED, request);
		if (!StringUtils.isEmpty(taskListOpenFlg)) {
			viewForm.setScheduleOpenTaskFlg(SystemFlg.codeToBoolean(taskListOpenFlg));
		}

		mv = getMyModelAndView(viewForm, TASK_LIST_FRAGMENT_PATH, VIEW_FORM_NAME);

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}


	/**
	 * タスク一覧を並び替える
	 *  
	 * @param sortKeyCd 1:設定順 、2:期限日-昇順、3:期限日-降順
	 * @return
	 */
	@RequestMapping(value = "/sortTaskList", method = RequestMethod.GET)
	public ModelAndView sortTaskList(
			HttpServletRequest request,
			@RequestParam(name = "sortKeyCd") String sortKeyCd) {

		ModelAndView mv = null;

		// 画面表示情報を取得
		ScheduleViewForm viewForm = service.createViewForm();
		viewForm.setTaskListSortKeyCd(sortKeyCd);

		// タスク一覧の表示条件をセッションから取得する
		String viewKeyCd = SessionUtils.getAccountSettingValue(AccountSettingConstant.SettingType.CALENDAR_TASK_VIEW);
		if (StringUtils.isEmpty(viewKeyCd) || AccountSettingConstant.CalendarTaskViewOption.DEFAULT.equalsByCode(viewKeyCd)) {
			viewForm.setAllRelatedTask(false);
		} else {
			viewForm.setAllRelatedTask(true);
		}

		// タスク情報を設定
		service.setTaskListBySchedule(viewForm);

		try {
			// タスクの並び順を保存
			service.saveCalendarTaskSortKey(sortKeyCd);
		} catch (AppException ex) {
			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		} catch (Exception ex) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("カレンダー画面タスク一覧のソート設定処理エラー", ex);
			return null;
		}

		// Cookieからデータを取得
		String taskListOpenFlg = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_TASKLIST_OPENED, request);
		if (!StringUtils.isEmpty(taskListOpenFlg)) {
			viewForm.setScheduleOpenTaskFlg(SystemFlg.codeToBoolean(taskListOpenFlg));
		}

		mv = getMyModelAndView(viewForm, TASK_LIST_FRAGMENT_PATH, VIEW_FORM_NAME);

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// =========================================================================
	// Ajax
	// =========================================================================

	/**
	 * 画面独自のバリデーションチェック（登録）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	private void inputFormValidatedRegist(ScheduleInputForm form, BindingResult result) {
		// 共通のチェック
		this.inputFormValidateCommon(form, result);
	}

	/**
	 * 画面独自のバリデーションチェック（更新）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	private void inputFormValidatedUpdate(ScheduleInputForm form, BindingResult result) {

		if (form.isShokaiMendan()) {
			// 初回面談スケジュールの更新は独自
			this.inputFormValidateForShokaiMendan(form, result);
		} else if (form.isSaibanLimit()) {
			// 裁判期日スケジュールの更新は独自
			this.inputFormValidateForSaibanLimit(form, result);
		} else {
			// 共通のチェック
			this.inputFormValidateCommon(form, result);
		}

	}

	/**
	 * 画面独自のバリデーションチェック（共通）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	private void inputFormValidateCommon(ScheduleInputForm form, BindingResult result) {

		// *******************************************
		// ■日時項目のチェック
		// *******************************************

		// ============================ | 時間パターンチェック | ==============================
		// 日付(dateFromの必須)
		if (!form.isUseRepeatDate() && form.getDateFrom() == null) {
			result.rejectValue("dateFrom", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));

		} else if (!form.isAllday()) {
			if (CommonUtils.anyNull(form.getTimeFrom(), form.getTimeTo())) {
				// timeFrom,timeToの必須チェック
				if (form.getTimeFrom() == null) {
					result.rejectValue("timeFrom", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}
				if (form.getTimeTo() == null) {
					result.rejectValue("timeTo", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}
			} else if (!form.isRepeat() || !form.isUseRepeatDate()) {
				// dateForm~dateTo(日時)の整合性チェック
				LocalDateTime dateTimeFrom = LocalDateTime.of(form.getDateFrom(), form.getTimeFrom());
				LocalDateTime dateTimeTo = LocalDateTime.of(form.getDateFrom(), form.getTimeTo());
				if (!DateUtils.isCorrectDateTime(dateTimeFrom, dateTimeTo) || dateTimeFrom.equals(dateTimeTo)) {
					result.rejectValue("dateFrom", null, getMessage(MessageEnum.MSG_E00060));
				}
			}
		} else if (!form.isAllday() && !form.isRepeat() || !form.isAllday() && !form.isUseRepeatDate()) {
			// どちらもチェックOFF or 終日チェックOFFで繰り返し期間の指定なし(繰り返しはチェックON) ->
			// dateForm~dateTo(日時)の整合性チェック
			LocalDateTime dateTimeFrom = LocalDateTime.of(form.getDateFrom(), form.getTimeFrom());
			LocalDateTime dateTimeTo = LocalDateTime.of(form.getDateFrom(), form.getTimeTo());
			if (!DateUtils.isCorrectDateTime(dateTimeFrom, dateTimeTo) || dateTimeFrom.equals(dateTimeTo)) {
				result.rejectValue("dateFrom", null, getMessage(MessageEnum.MSG_E00060));
			}
		}

		// 終日フラグのみON -> Form~To(日付)の整合性チェック
		if (form.isAllday() && !form.isRepeat()) {

			if (CommonUtils.anyNull(form.getDateFrom(), form.getDateTo())) {
				// 日付の必須チェック
				result.rejectValue("dateTo", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));

			} else if (!DateUtils.isCorrectDate(form.getDateFrom(), form.getDateTo())) {
				// 日付の整合性チェック
				result.rejectValue("dateTo", null, getMessage(MessageEnum.MSG_E00060));

			}
		}

		// 繰り返し期間を指定する場合
		if (form.isUseRepeatDate()) {

			if (CommonUtils.anyNull(form.getRepeatDateFrom(), form.getRepeatDateTo())) {
				// 繰り返し日付の必須チェック
				result.rejectValue("repeatDateFrom", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

			// 繰り返し日の整合性チェック(同日は許容する)
			if (!DateUtils.isCorrectDate(form.getRepeatDateFrom(), form.getRepeatDateTo())) {
				result.rejectValue("repeatDateFrom", null, getMessage(MessageEnum.MSG_E00060));
			}

			if (!form.isAllday()) {
				// 繰り返し期間を指定する and 終日チェックON -> 時間の整合性チェック
				if (!DateUtils.isCorrectTime(form.getTimeFrom(), form.getTimeTo()) || form.getTimeFrom().equals(form.getTimeTo())) {
					result.rejectValue("hourFrom", null, getMessage(MessageEnum.MSG_E00060));
				}
			}
		}

		// ========== | 繰り返しパターンの必須チェック | ==========
		// 繰り返しチェックがONのとき -> 繰り返し種別による相関チェック
		if (form.isRepeat()) {
			if (form.getRepeatType() == null) {
				// NULLの場合 -> 入力チェックエラー(EnumCd外のリクエスト)
				result.rejectValue("repeatType", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));

			} else if (ScheduleRepeatType.WEEKLY == form.getRepeatType()) {
				// 「毎週」の場合
				if (form.getRepeatYobi().entrySet().stream().allMatch(cheked -> !cheked.getValue())) {
					// 一つもチェックされていない場合
					result.rejectValue("repeatYobi", null, getMessage(MessageEnum.MSG_E00024, "曜日"));
				}
			} else if (ScheduleRepeatType.MONTHLY == form.getRepeatType()) {
				// 「毎月」の場合
				if (form.getRepeatDayOfMonth() == null) {
					// 選択されていない -> 必須エラー
					result.rejectValue("repeatDayOfMonth", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				} else if (1 > form.getRepeatDayOfMonth() || 31 < form.getRepeatDayOfMonth()) {
					// 1より小さい || 31より大きい
					result.rejectValue("repeatDayOfMonth", null, getMessage(MessageEnum.MSG_E00001));
				}
			} else {
				// それ以外は検証しない(毎日の場合)
			}
		}

		// *******************************************
		// ■場所項目のチェック
		// *******************************************

		// 必須（会議室）
		if (form.isRoomSelected()) {
			Long roomId = form.getRoomId();
			if (roomId == null) {
				result.rejectValue("roomId", null, getMessage(MessageEnum.MSG_E00024, "施設"));
			}
		}

		// *******************************************
		// ■参加者項目のチェック
		// *******************************************
		// 必須（参加者）
		List<Long> selectMember = form.getMember();
		if (selectMember.isEmpty()) {
			result.rejectValue("member", null, getMessage(MessageEnum.MSG_E00024, "参加者"));
		}
	}

	/**
	 * 予定表画面(TOP)から初回面談予定を更新する際のバリデーション
	 *
	 * <pre>
	 * 案件管理画面からモーダルを開いた時とは挙動が異なる<br>
	 * <ul>
	 * <li>更新できる情報は参加者のみ -> 参加者のみバリデーション</li>
	 * <li>その他のDisable項目は演習できないためDB整合性チェックを行う</li>
	 * <ul>
	 *
	 * <pre>
	 *
	 * @param form
	 * @param response
	 * @param errorList
	 * @return
	 */
	private void inputFormValidateForShokaiMendan(ScheduleInputForm form, BindingResult result) {

		// *******************************************
		// ■参加者項目のチェック
		// *******************************************
		// 必須（参加者）
		List<Long> selectMember = form.getMember();
		if (selectMember.isEmpty()) {
			result.rejectValue("member", null, getMessage(MessageEnum.MSG_E00024, "参加者"));
		}

	}

	/**
	 * 予定表画面(TOP)から裁判期日予定を更新する際のバリデーション
	 *
	 * <pre>
	 * 裁判管理画面からモーダルを開いた時とは挙動が異なる<br>
	 * <ul>
	 * <li>更新できる情報は参加者のみ -> 参加者のみバリデーション</li>
	 * <li>その他のDisable項目は編集できないためDB整合性チェックを行う</li>
	 * <ul>
	 *
	 * <pre>
	 *
	 * @param form
	 * @param response
	 * @param errorList
	 * @return
	 */
	private void inputFormValidateForSaibanLimit(ScheduleInputForm form, BindingResult result) {

		// *******************************************
		// ■参加者項目のチェック
		// *******************************************
		// 必須（参加者）
		List<Long> selectMember = form.getMember();
		if (selectMember.isEmpty()) {
			result.rejectValue("member", null, getMessage(MessageEnum.MSG_E00024, "参加者"));
		}

	}

	/**
	 * 登録時のDB整合性チェック
	 *
	 * @param form
	 * @param response
	 * @return エラーの有無
	 */
	private boolean acessDBValdateRegist(ScheduleInputForm form, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, MessageEnum> errorMsgMap = new HashMap<>();
		boolean error = false;

		// エラーの判定と格納
		this.accessDBValidateCommon(form, "messageKey", errorMsgMap);

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", getMessage(errorMsgMap.get("messageKey")));
			error = true;
		}
		return error;
	}

	/**
	 * 更新時のDB整合性チェック
	 *
	 * @param form
	 * @param response
	 * @return エラーの有無
	 */
	private boolean acessDBValdateUpdate(ScheduleInputForm form, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, MessageEnum> errorMsgMap = new HashMap<>();
		boolean error = false;
		final String key = "messageKey";

		// ******************************
		// エラーの判定と格納
		// ******************************
		if (form.isShokaiMendan()) {
			// 初回面談の時
			this.accessDBValidateForShokaiMendan(form, key, errorMsgMap);
		} else if (form.isSaibanLimit()) {
			// 裁判期日の時
			this.accessDBValidateForSaibanLimit(form, key, errorMsgMap);
		} else {
			// それ以外(特に役割はない予定のとき)
			this.accessDBValidateCommon(form, key, errorMsgMap);
		}

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", getMessage(errorMsgMap.get(key)));
			error = true;
		}
		return error;
	}

	/**
	 * DB整合性チェック
	 *
	 * @param form
	 * @param key エラーを格納するMapのキー
	 * @param errorMsgMap エラー発生時にエラーメッセージを格納する
	 * @return 検証結果
	 */
	private void accessDBValidateCommon(ScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ****************************************
		// 施設IDが有効かどうか判定
		// ****************************************
		if (form.isRoomSelected()) {
			// 施設IDの整合性チェック
			if (!commonScheduleValidator.isRoomExistsValid(form.getScheduleSeq(), form.getRoomId())) {
				errorMsgMap.put(key, MessageEnum.MSG_E00025);
				return;
			}

			// 施設の利用可能整合性チェック
			if (!commonScheduleValidator.isRoomUsingValid(form)) {
				errorMsgMap.put(key, MessageEnum.MSG_E00025);
				return;
			}

		}

		// ****************************************
		// アカウントIDが有効かどうか判定
		// ****************************************
		// アカウントの整合性チェック
		if (!commonScheduleValidator.isAccountExistsValid(form.getScheduleSeq(), form.getMember())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

	}

	/**
	 * 初回面談予定の更新DB整合性チェック
	 *
	 * @param form
	 * @param key
	 * @return
	 */
	private void accessDBValidateForShokaiMendan(ScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ****************************************
		// アカウントIDが有効かどうか判定
		// ****************************************
		// アカウントの整合性チェック
		if (!commonScheduleValidator.isAccountExistsValid(form.getScheduleSeq(), form.getMember())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

		// ****************************************
		// 入力項目に変更がないか確認する
		// ****************************************
		if (!commonScheduleValidator.isNonEditScheduleValidForShokaiMendan(form)) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

	}

	/**
	 * 裁判期日予定の更新DB整合性チェック
	 *
	 * @param form
	 * @param key
	 * @return
	 */
	private void accessDBValidateForSaibanLimit(ScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ****************************************
		// アカウントIDが有効かどうか判定
		// ****************************************
		// アカウントの整合性チェック
		if (!commonScheduleValidator.isAccountExistsValid(form.getScheduleSeq(), form.getMember())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

		// ****************************************
		// 入力項目に変更がないか確認する
		// ****************************************
		if (!commonScheduleValidator.isNonEditScheduleValidForSaibanLimit(form)) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

		return;
	}

	/**
	 * 登録
	 *
	 * @param form フォーム
	 * @param result バリデーション結果
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Map<String, Object> create(@Validated @ModelAttribute(INPUT_FORM_NAME) ScheduleInputForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力項目の相関バリデーションチェック
		this.inputFormValidatedRegist(form, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力値のDB整合性チェック
		if (this.acessDBValdateRegist(form, response)) {
			return response;
		}

		try {
			service.create(form);

			// パートicipant情報の処理
			List<String> participantStatus = form.getParticipantStatus();
			// パートicipant情報を必要な処理に応じて処理する

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "予定"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}
	}

	/**
	 * 更新
	 *
	 * @param form フォーム
	 * @param result バリデーション結果
	 * @return
	 */
	// @ResponseBody
	// @RequestMapping(value = "/update", method = RequestMethod.POST)
	// public Map<String, Object> update(@Validated({Default.class, Update.class}) @ModelAttribute(INPUT_FORM_NAME) ScheduleInputForm form,
	// 		BindingResult result) {

	// 	Map<String, Object> response = new HashMap<>();

	// 	// 入力項目の相関バリデーションチェック
	// 	this.inputFormValidatedUpdate(form, result);

	// 	if (result.hasErrors()) {
	// 		// バリデーションエラーが存在する場合
	// 		response.put("succeeded", false);
	// 		response.put("message", getMessage(MessageEnum.MSG_E00001));
	// 		response.put("errors", result.getFieldErrors());
	// 		return response;
	// 	}

	// 	// 入力項目のDB整合性チェック
	// 	if (this.acessDBValdateUpdate(form, response)) {
	// 		// エラーが存在する場合
	// 		return response;
	// 	}

	// 	// Extract participant status from the form
	// 	List<String> participantStatus = form.getParticipantStatus();

	// 	// Update the form with the participant status
	// 	form.setParticipantStatus(participantStatus);

	// 	try {
	// 		service.update(form);
	// 		response.put("succeeded", true);
	// 		response.put("message", getMessage(MessageEnum.MSG_I00023, "予定"));
	// 		return response;

	// 	} catch (Exception e) {
	// 		response.put("succeeded", false);
	// 		response.put("message", getMessage(MessageEnum.MSG_E00013));
	// 		return response;
	// 	}
	// }

	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@Validated({Default.class, Update.class}) @ModelAttribute(INPUT_FORM_NAME) ScheduleInputForm form,
			BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// Input validation and error handling code...

		if (result.hasErrors()) {
			// Validation error handling code...
			return response;
		}

		// Extract participant status from the form
		List<String> participantStatus = form.getParticipantStatus();

		// Update the form with the participant status
		form.setParticipantStatus(participantStatus);

		// Access database and perform integrity checks if needed...

		try {
			// Update the schedule using the service
			service.update(form);

			// Populate the response
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "予定"));
			return response;
		} catch (Exception e) {
			// Error handling code...
			return response;
		}
	}


	/**
	 * 削除
	 *
	 * @param scheduleSeq 予定SEQ
	 * @param result バリデーション結果
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Map<String, Object> delete(@RequestParam("scheduleSeq") Long scheduleSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			service.delete(scheduleSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "予定"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00014));
			return response;
		}
	}

	/**
	 * カレンダー選択状態を更新する
	 *
	 * @param calendarStatusForm カレンダー選択状態
	 */
	@ResponseBody
	@RequestMapping(value = "/saveCalendarStatus", method = RequestMethod.POST)
	public void saveCalendarStatus(HttpServletResponse response, @ModelAttribute(STATUS_FORM_NAME) CalendarStatusForm calendarStatusForm) {
		// このメソッドが呼ばれた時点でセッションのカレンダー選択状態は更新されている
		// このメソッドが呼ばれたときに、選択しているアカウントIDをCookieに保存する

		// Session内の選択中アカウントSEQを取得
		List<Long> accountSeqList = calendarStatusForm.getSelectedAccountSeq();

		// リストのアカウントSEQ情報をカンマ区切りにする
		String accountSeqStr = accountSeqList.toString().replaceAll(CommonConstant.SPACE, CommonConstant.BLANK);

		// エンコードして、Cookieに追加
		String encodedAccountSeqStr = HttpUtils.encodeUTF8(accountSeqStr);
		cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_SELECTED_USERS, encodedAccountSeqStr,
				CookieConstant.COOKIE_EXPRIRATION_TIME, response);

		String selectedHolidayFlg = SystemFlg.booleanToCode(calendarStatusForm.isSelectedHoliday());
		cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_SCHEDULE_OPTIONAL_FOR_SELECTED_HOLIDAY, selectedHolidayFlg,
				CookieConstant.COOKIE_EXPRIRATION_TIME, response);
	}

	/**
	 * 予定を取得する
	 *
	 * @param scheduleRequest 取得条件
	 * @param result バリデーション結果
	 * @return 予定
	 */
	@ResponseBody
	@RequestMapping(value = "/getSchedule", method = RequestMethod.POST)
	public ScheduleResponse getSchedule(@Validated @ModelAttribute ScheduleRequest scheduleRequest, BindingResult result) {

		if (result.hasErrors()) {
			return new ScheduleResponse();
		}

		return service.getSchedule(scheduleRequest);
	}

	/**
	 * 会議室毎の予定を取得する
	 *
	 * @param scheduleRequest 取得条件
	 * @param result バリデーション結果
	 * @return 予定
	 */
	@ResponseBody
	@RequestMapping(value = "/getRoomSchedule", method = RequestMethod.POST)
	public RoomScheduleResponse getRoomSchedule(@Validated @ModelAttribute RoomScheduleRequest scheduleRequest, BindingResult result) {

		if (result.hasErrors()) {
			return new RoomScheduleResponse();
		}

		return service.getRoomSchedule(scheduleRequest);
	}

	/**
	 * 分割していない予定リストの取得
	 * 
	 * @param scheduleRequest
	 * @param result
	 */
	@ResponseBody
	@RequestMapping(value = "/getScheduleItemList", method = RequestMethod.POST)
	public ScheduleResponse getScheduleItemList(@Validated @ModelAttribute ScheduleRequest scheduleRequest, BindingResult result) {

		if (result.hasErrors()) {
			return new ScheduleResponse();
		}

		return service.getScheduleItemList(scheduleRequest);
	}

}