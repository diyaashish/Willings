package jp.loioz.app.user.taskManagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenSelectListDto;
import jp.loioz.app.user.taskManagement.dto.TaskCheckItemDto;
import jp.loioz.app.user.taskManagement.form.CommentForm;
import jp.loioz.app.user.taskManagement.form.TaskCheckItemForm;
import jp.loioz.app.user.taskManagement.form.edit.TaskEditInputForm;
import jp.loioz.app.user.taskManagement.form.edit.TaskEditViewForm;
import jp.loioz.app.user.taskManagement.form.edit.TaskListScheduleViewForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListViewForm;
import jp.loioz.app.user.taskManagement.service.TaskCommonService;
import jp.loioz.app.user.taskManagement.service.TaskEditService;
import jp.loioz.common.constant.CommonConstant.CheckItemStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import jp.loioz.common.constant.AccountSettingConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.groups.TaskCheckItem;
import jp.loioz.dto.ActivityHistoryDto;
import jp.loioz.dto.TaskListDto;

/**
 * タスク編集画面のコントローラークラス
 */
@Controller
@RequestMapping("user/taskManagement")
public class TaskEditController extends DefaultController {

	/** タスクモーダル viewパス */
	public static final String TASK_EDIT_MODAL_PATH = "user/taskManagement/taskEditModal::select-task-modal";

	/** タスク入力Formのviewパス */
	public static final String TASK_EDIT_INPUT_FORM_PATH = "user/taskManagement/taskEditModal::taskEditInput";

	/** 紐づくコメント一覧のviewパス */
	public static final String COMMENT_FORM_VIEW_PATH = "user/taskManagement/taskEditModal::commentForm";

	/** タスク一覧（1つ）のviewパス */
	public static final String TASK_LIST_FORM_VIEW_PATH = "user/taskManagement/taskEditModal::taskListScheduleViewForm";

	/** サブタスク一覧の1行 AjaxViewのパス */
	public static final String CHECK_ITEM_AJAX_PATH = "user/taskManagement/taskEditModal::checkItemFragment";

	/** アクティビティの AjaxViewのパス */
	public static final String ACTIVITY_LIST_AJAX_PATH = "user/taskManagement/taskEditModal::activityListFragment";

	/** タスク詳細情報案件設定のデータリストfragmentのパス */
	private static final String TASK_ANKEN_DATA_LIST_INPUT_FRAGMENT_PATH = TaskController.TASK_ANKEN_DATA_LIST_INPUT_FRAGMENT_PATH;

	/** モーダルからのタスク一覧 AjaxViewのパス */
	public static final String MODAL_TASK_LIST_AJAX_PATH = "user/taskManagement/task::taskListData";

	/** viewで使用するフォームオブジェクト名 */
	public static final String EDIT_FORM_NAME = "taskEditViewForm";

	/** viewで使用する入力値Formオブジェクト */
	public static final String INPUT_FORM_NAME = "inputForm";

	/** タスク編集：案件を設定する時に案件選択データオブジェクト名 */
	private static final String TASK_ANKEN_DATA_LIST_NAME = TaskController.TASK_ANKEN_DATA_LIST_NAME;

	/** viewで使用するコメント追加用Formオブジェクト */
	public static final String COMMENT_FORM_NAME = "commentForm";

	/** viewで使用するコメント追加用Formオブジェクト */
	public static final String TASK_FORM_NAME = "taskListScheduleViewForm";

	/** viewで使用する画面表示用フォームオブジェクト名 */
	public static final String VIEW_FORM_NAME = "taskListViewForm";

	/** アクティビティの表示用データオブジェクト名 */
	public static final String ACTIVITY_LIST_DTO_NAME = "activityList";

	/** サブタスクの1行分の表示用データオブジェクト名 */
	public static final String CHECK_ITEM_DTO_NAME = "checkItem";

	/** タスク管理画面用のサービスクラス */
	@Autowired
	private TaskEditService service;

	/** タスク共通サービス */
	@Autowired
	private TaskCommonService taskCommonService;

	/** 入力用フォームクラスの初期化 */
	@ModelAttribute(EDIT_FORM_NAME)
	TaskEditViewForm setUpEditViewForm(@RequestParam(name = "taskSeq", required = false) Long taskSeq) {
		return service.createViewForm(taskSeq);
	}

	/**
	 * 新規登録モーダル表示処理
	 * 
	 * @param taskSeq
	 * @return モーダル画面情報
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createModal() {

		TaskEditInputForm inputForm = new TaskEditInputForm();
		// 今日のタスクは変更可能
		inputForm.setTodayTaskChangeableFlag(true);
		try {
			service.setCreateData(inputForm);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getMessageKey())));
		}


		return getMyModelAndView(inputForm, TASK_EDIT_MODAL_PATH, INPUT_FORM_NAME);
	}

	/**
	 * 編集モーダル表示処理
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public ModelAndView editModal(
			@ModelAttribute(EDIT_FORM_NAME) TaskEditViewForm viewForm,
			@RequestParam(name = "taskSeq", required = false) Long taskSeq,
			TaskEditInputForm inputForm) {

		/** 編集のため、入力情報、コメント追加、履歴情報を取得・作成する */
		// 入力情報の取得
		try {
			service.setEditData(inputForm, taskSeq, SessionUtils.getLoginAccountSeq());
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getMessageKey())));
		}

		// 履歴Formを作成し、タスクSEQに紐づく履歴情報を取得する
		service.setTaskChildHistory(viewForm, taskSeq);

		try {
			// 既読フラグの更新を行う
			service.openModalUpdate(taskSeq);
		} catch (AppException ex) {
			// エラーメッセージをセットして、エラー処理に飛ばします。
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getMessageKey())));
		}

		// 作成したFormのMapを作成
		Map<String, Object> modelMap = new HashMap<>();
		modelMap.put(INPUT_FORM_NAME, inputForm);
		modelMap.put(EDIT_FORM_NAME, viewForm);
		modelMap.put(COMMENT_FORM_NAME, new CommentForm());

		// modelにFormをaddしてリダイレクト
		return ModelAndViewUtils.getModelAndView(TASK_EDIT_MODAL_PATH).addAllObjects(modelMap);
	}

	/**
	 * タスクの新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return responseMap
	 */
	@ResponseBody
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public Map<String, Object> regist(@Validated TaskEditInputForm inputForm, BindingResult result) {

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
			// 入力情報を元に登録処理を行います。
			if (inputForm.getAnkenId() != null && inputForm.getAnkenId().asLong().equals(Long.valueOf("0"))) {
				// 案件IDが0のときは空にする
				inputForm.setAnkenId(null);
			}
			Long taskSeq = service.insertTask(inputForm);
			response.put("succeeded", true);
			response.put("taskSeq", taskSeq);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "タスク"));
			return response;
		} catch (AppException ex) {
			// 登録時にエラーがあった場合、エラーメッセージをセットして画面を返却します。
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * タスク情報の更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return responseMap
	 */
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@Validated TaskEditInputForm inputForm, BindingResult result) {

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
			// 「今日のタスク」更新、更新履歴を作らないため別メソッドで更新する。
			if (inputForm.isTodayTaskChangeableFlag()) {
				// 画面から更新可能な場合
				service.updateToTaskToday(inputForm);
			}

			// 入力情報を元に登録処理を行います。
			if (inputForm.getAnkenId() != null && inputForm.getAnkenId().asLong().equals(Long.valueOf("0"))) {
				// 案件IDが0のときは空にする
				inputForm.setAnkenId(null);
			}
			service.updateTask(inputForm);

			try {
				// 既読フラグの更新を行う
				service.openModalUpdate(inputForm.getTaskSeq());
			} catch (AppException ex) {
				// エラーメッセージをセットして、エラー処理に飛ばします。
				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00013));
				response.put("errors", result.getFieldErrors());
				return response;
			}

			response.put("succeeded", true);
			response.put("taskSeq", inputForm.getTaskSeq());
			response.put("message", getMessage(MessageEnum.MSG_I00023, "タスク"));
			return response;

		} catch (AppException ex) {
			// 登録時にエラーがあった場合、エラーメッセージをセットして画面を返却します。
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

	}

	/**
	 * 削除処理
	 * 
	 * @param form
	 * @param result
	 * @return responseMap
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Map<String, Object> delete(TaskEditInputForm inputForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 入力情報を元に削除処理を行います。
			taskCommonService.deleteTask(inputForm.getTaskSeq());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;
		} catch (AppException ex) {
			// 削除時にエラーがあった場合、エラーメッセージをセットして画面を返却します。
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

	}

	/**
	 * コメントの追加処理
	 * 
	 * @param taskSeq
	 * @param comment
	 * @return 追加後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/addComment", method = RequestMethod.POST)
	public Map<String, Object> addComment(@Validated CommentForm commentForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// バリデーションエラーがある場合
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			// 履歴(コメント)の追加処理
			taskCommonService.insertTaskComment(commentForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00014, "コメントを"));

			// 画面の再取得のために、登録したタスク履歴SEQをリスエストに詰める
			response.put("taskHistorySeq", commentForm.getTaskHistorySeq());
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * コメントの更新処理
	 * 
	 * @param taskSeq
	 * @param taskHistorySeq
	 * @param comment
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/updateComment", method = RequestMethod.POST)
	public Map<String, Object> updateComment(@Validated CommentForm commentForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// バリデーションエラーがある場合
		if (result.hasErrors()) {

			// エラーフィールド名に履歴SEQを追加
			List<FieldError> errors = result.getFieldErrors().stream().map(error -> {
				return new FieldError(result.getObjectName(), error.getField() + "[" + commentForm.getTaskHistorySeq() + "]", error.getDefaultMessage());
			}).collect(Collectors.toList());

			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", errors);
			return response;
		}

		try {
			// 更新処理
			service.updateComment(commentForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00033, "コメント"));

			// 画面の再取得のために、更新したタスク履歴SEQをリスエストに詰める
			response.put("taskHistorySeq", commentForm.getTaskHistorySeq());
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * コメントの削除処理を行います
	 * 
	 * @param taskSeq
	 * @param taskHistorySeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteComment", method = RequestMethod.POST)
	public Map<String, Object> deleteComment(CommentForm commentForm) {

		Map<String, Object> response = new HashMap<>();

		// 削除処理
		try {
			service.deleteComment(commentForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "コメント"));
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * コメント登録・更新・削除完了後に表示している履歴を更新する
	 * 
	 * @param viewForm
	 * @param taskHistorySeq
	 * @return
	 */
	@RequestMapping(value = "/getCommentHistory", method = RequestMethod.POST)
	public ModelAndView getCommentedHisory(
			@ModelAttribute(EDIT_FORM_NAME) TaskEditViewForm viewForm,
			@RequestParam(name = "taskSeq") Long taskSeq) {

		// 追加した履歴のみ取得します
		service.setTaskChildHistory(viewForm, taskSeq);

		// 登録完了後はコメント追加Formを初期化します。
		CommentForm commentForm = new CommentForm();

		// コメント関連にページ情報を返す
		return getMyModelAndView(commentForm, COMMENT_FORM_VIEW_PATH, COMMENT_FORM_NAME)
				.addObject(EDIT_FORM_NAME, viewForm);
	}

	/**
	 * 外部からの登録モーダル表示処理
	 * 
	 * @return モーダル画面情報
	 */
	@RequestMapping(value = "/otherRoot/create", method = RequestMethod.GET)
	public ModelAndView createOtherRootModal(TaskEditInputForm inputForm) {
		try {
			service.setCreateData(inputForm);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getMessageKey())));
		}
		// 今日のタスクは変更可能（作成者が操作ユーザになるため）
		inputForm.setTodayTaskChangeableFlag(true);
		return getMyModelAndView(inputForm, TASK_EDIT_MODAL_PATH, INPUT_FORM_NAME);
	}

	/**
	 * 外部からの編集モーダル表示処理
	 * 
	 * @return モーダル画面情報
	 */
	@RequestMapping(value = "/otherRoot/edit", method = RequestMethod.POST)
	public ModelAndView editOtherRootModal(
			@ModelAttribute(EDIT_FORM_NAME) TaskEditViewForm viewForm,
			@RequestParam(name = "taskSeq", required = false) Long taskSeq,
			TaskEditInputForm inputForm,
			MessageHolder msgHolder) {

		/** 編集のため、入力情報、コメント追加、履歴情報を取得・作成する */
		// 入力情報の取得
		try {
			service.setEditData(inputForm, taskSeq, null);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}

		// 履歴Formを作成し、タスクSEQに紐づく履歴情報を取得する
		service.setTaskChildHistory(viewForm, taskSeq);

		try {
			// 既読フラグの更新を行う
			service.openModalUpdate(taskSeq);
		} catch (AppException ex) {
			// エラーメッセージをセットして、エラー処理に飛ばします。
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getMessageKey())));
		}

		// 作成したFormのMapを作成
		Map<String, Object> modelMap = new HashMap<>();
		modelMap.put(INPUT_FORM_NAME, inputForm);
		modelMap.put(EDIT_FORM_NAME, viewForm);
		modelMap.put(COMMENT_FORM_NAME, new CommentForm());

		// modelにFormをaddしてリダイレクト
		return ModelAndViewUtils.getModelAndView(TASK_EDIT_MODAL_PATH).addAllObjects(modelMap);
	}

	/**
	 * タスク1行分のデータを取得する
	 * 
	 * @param taskSeq
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "/getTaskEditListRow", method = RequestMethod.POST)
	public ModelAndView getTaskEditListRow(
			@RequestParam(name = "taskSeq", required = false) Long taskSeq,
			TaskListScheduleViewForm viewForm) {

		// タスク一覧の表示条件をセッションから取得する
		String viewKeyCd = SessionUtils.getAccountSettingValue(AccountSettingConstant.SettingType.CALENDAR_TASK_VIEW);
		boolean assignTaskFlg = false;
		if (StringUtils.isEmpty(viewKeyCd) || AccountSettingConstant.CalendarTaskViewOption.DEFAULT.equalsByCode(viewKeyCd)) {
			assignTaskFlg = false;
		} else {
			assignTaskFlg = true;
		}
		// タスクを取得
		service.setTaskListScheduleData(viewForm, taskSeq, SessionUtils.getLoginAccountSeq(), assignTaskFlg);

		// コメント関連にページ情報を返す
		return getMyModelAndView(viewForm, TASK_LIST_FORM_VIEW_PATH, TASK_FORM_NAME);
	}

	/**
	 * タスクモーダルのサブタスク保存処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveTaskModalCheckItem", method = RequestMethod.POST)
	public Map<String, Object> saveTaskModalCheckItem(
			@Validated({TaskCheckItem.class}) TaskEditInputForm inputForm,
			BindingResult result) {

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
			// サブタスク登録
			TaskCheckItemDto taskCheckItemDto = taskCommonService.saveTaskCheckItem(inputForm.getTaskSeq(), inputForm.getCheckItem(), SystemFlg.FLG_OFF.getCd());

			response.put("succeeded", true);
			response.put("taskSeq", inputForm.getTaskSeq());
			response.put("taskCheckItemSeq", taskCheckItemDto.getTaskCheckItemSeq());
			response.put("message", getMessage(MessageEnum.MSG_I00034, "サブタスク"));
			return response;

		} catch (AppException ex) {
			// 登録時にエラーがあった場合、エラーメッセージをセットしてレスポンスを返却します。
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

	}

	/**
	 * タスクモーダルで表示するチェック項目（サブタスク1行分）を取得する
	 * 
	 * @param viewForm
	 * @param taskHistorySeq
	 * @return
	 */
	@RequestMapping(value = "/getTaskModalCheckItem", method = RequestMethod.GET)
	public ModelAndView getTaskModalCheckItem(@RequestParam(name = "taskCheckItemSeq") Long taskCheckItemSeq) {

		ModelAndView mv = null;

		try {
			// 追加したチェック項目のみ取得します
			TaskCheckItemDto taskCheckItemDto = taskCommonService.getCheckItem(taskCheckItemSeq);
			mv = getMyModelAndView(taskCheckItemDto, CHECK_ITEM_AJAX_PATH, CHECK_ITEM_DTO_NAME);
		} catch (AppException ex) {
			// 想定されるエラーのケース
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
	 * 案件設定のセレクトボックス候補を取得
	 * 
	 * @param searchWord
	 * @return
	 */
	@RequestMapping(value = "/searchTaskModalAnkenList", method = RequestMethod.GET)
	public ModelAndView searchTaskModalAnkenList(
			@RequestParam(name = "searchWord") String searchWord) {

		ModelAndView mv = null;

		try {
			List<TaskAnkenSelectListDto> taskAnkenSelectListDtoList = taskCommonService.searchAnkenList(searchWord);
			mv = getMyModelAndView(taskAnkenSelectListDtoList, TASK_ANKEN_DATA_LIST_INPUT_FRAGMENT_PATH, TASK_ANKEN_DATA_LIST_NAME);
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * チェック項目名変更処理
	 * 
	 * @param checkItemForm
	 * @param result
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/changeTaskModalCheckItemName", method = RequestMethod.POST)
	public Map<String, Object> changeTaskModalCheckItemName(@Validated TaskCheckItemForm checkItemForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// バリデーションエラーがある場合
		if (result.hasErrors()) {

			// エラーフィールド名にSEQを追加
			List<FieldError> errors = result.getFieldErrors().stream().map(error -> {
				return new FieldError(result.getObjectName(), error.getField() + "[" + checkItemForm.getTaskCheckItemSeq() + "]", error.getDefaultMessage());
			}).collect(Collectors.toList());

			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", errors);
			return response;
		}

		try {
			// 更新処理
			taskCommonService.updateCheckItemName(checkItemForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00033, "サブタスク名"));
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * チェック項目ステータス変更処理
	 * 
	 * @param checkItemForm
	 * @param result
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/changeTaskModalCheckItemStatus", method = RequestMethod.POST)
	public Map<String, Object> changeTaskModalCheckItemStatus(TaskCheckItemForm checkItemForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 更新処理
			taskCommonService.updateCheckItemStatus(checkItemForm);
			response.put("succeeded", true);
			if (CheckItemStatus.INCOMPLETE.equalsByCode(checkItemForm.getNewCompleteFlg())) {
				// サブタスクを未完了にしました
				response.put("message", getMessage(MessageEnum.MSG_I00134, CheckItemStatus.INCOMPLETE.getVal()));
			} else {
				// サブタスクを完了にしました
				response.put("message", getMessage(MessageEnum.MSG_I00134, CheckItemStatus.COMPLETED.getVal()));
			}
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * サブタスクの表示順を保存する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page がないとエラーとなるため作成。
	 * @param checkItemSeq
	 * @return
	 */
	@RequestMapping(value = "/changeTaskModalCheckItemDispOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changeTaskModalCheckItemDispOrder(
			@RequestParam(name = "checkItemSeq") String checkItemSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 表示順変更
			taskCommonService.changeCheckItemDispOrder(checkItemSeq);

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}
		response.put("succeeded", true);
		response.put("message", getMessage(MessageEnum.MSG_I00023, "表示順"));
		return response;
	}

	/**
	 * チェック項目削除処理
	 * 
	 * @param checkItemForm
	 * @param result
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteTaskModalCheckItem", method = RequestMethod.POST)
	public Map<String, Object> deleteTaskModalCheckItem(TaskCheckItemForm checkItemForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 更新処理
			taskCommonService.deleteCheckItem(checkItemForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "サブタスク"));
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * タスクモーダルのアクティビティタブ表示処理
	 * 
	 * @param taskSeq
	 * @return
	 */
	@RequestMapping(value = "/displayTaskModalActivity", method = RequestMethod.GET)
	public ModelAndView displayTaskModalActivity(
			@RequestParam(name = "taskSeq") Long taskSeq) {

		ModelAndView mv = null;

		List<ActivityHistoryDto> activityList = taskCommonService.getActivity(taskSeq);

		mv = getMyModelAndView(activityList, ACTIVITY_LIST_AJAX_PATH, ACTIVITY_LIST_DTO_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * カレンダー画面でタスクステータスを変更した時の処理
	 * 
	 * @param taskSeq
	 * @param taskStatus
	 * @return
	 */
	@RequestMapping(value = "/taskCompBySchedule", method = RequestMethod.POST)
	public ModelAndView taskCompBySchedule(
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "taskStatus") String taskStatus) {

		ModelAndView mv = null;

		try {
			// ステータスの更新を行います。
			taskCommonService.updateTaskStatus(taskSeq, taskStatus);
		} catch (AppException ex) {
			// 排他エラーのときにメッセージを表示する
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getErrorType())));
		}

		// viewFormの作成
		TaskListViewForm viewForm = new TaskListViewForm();

		// 入れ替えるデータの取得と設定を行います。
		try {
			TaskListDto responseData = taskCommonService.getTaskDataRelatedToAccount(taskSeq);
			List<TaskListDto> taskDtoList = new ArrayList<>();
			taskDtoList.add(responseData);
			viewForm.setTaskList(taskDtoList);
			mv = getMyModelAndView(viewForm, MODAL_TASK_LIST_AJAX_PATH, VIEW_FORM_NAME);
		} catch (AppException ex) {
			// 排他エラーのときにメッセージを表示する
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getErrorType())));
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00133, "完了"));
		return mv;
	}

	/**
	 * 案件ダッシュボード画面でタスクステータスを変更した時の処理
	 * 
	 * @param taskSeq
	 * @param taskStatus
	 * @return
	 */
	@RequestMapping(value = "/changeStatusAnkenDashbord", method = RequestMethod.POST)
	public ModelAndView changeStatusAnkenDashbord(
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "taskStatus") String taskStatus) {

		ModelAndView mv = null;

		try {
			// ステータスの更新を行います。
			taskCommonService.updateTaskStatus(taskSeq, taskStatus);
		} catch (AppException ex) {
			// 排他エラーのときにメッセージを表示する
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getErrorType())));
		}

		// viewFormの作成
		TaskListViewForm viewForm = new TaskListViewForm();

		// 入れ替えるデータの取得と設定を行います。
		try {
			TaskListDto responseData = service.getReplaceCardData(taskSeq);
			List<TaskListDto> taskDtoList = new ArrayList<>();
			taskDtoList.add(responseData);
			viewForm.setTaskList(taskDtoList);
			mv = getMyModelAndView(viewForm, MODAL_TASK_LIST_AJAX_PATH, VIEW_FORM_NAME);
		} catch (AppException ex) {
			// 排他エラーのときにメッセージを表示する
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getErrorType())));
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00133, TaskStatus.of(taskStatus).getVal()));
		return mv;
	}

	/**
	 * カレンダー画面でモーダルを閉じた時の処理
	 * 
	 * <pre>
	 * モーダルを開くときにタスク情報が更新(確認フラグ等)がされているので、
	 * タスク情報を取得しなおして、取得したタスクのみHTMLを返却します。
	 * </pre>
	 * 
	 * @param taskSeq
	 * @return
	 */
	@RequestMapping(value = "/closeModal", method = RequestMethod.POST)
	public ModelAndView closeModal(
			@RequestParam(name = "taskSeq") Long taskSeq) {

		// viewFormの作成
		TaskListViewForm viewForm = new TaskListViewForm();

		// 入れ替えるデータの取得と設定を行います。
		try {
			TaskListDto responseData = taskCommonService.getTaskDataRelatedToAccount(taskSeq);
			List<TaskListDto> taskDtoList = new ArrayList<>();
			taskDtoList.add(responseData);
			viewForm.setTaskList(taskDtoList);
		} catch (AppException ex) {
			// 排他エラーのときにメッセージを表示する
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(ex.getErrorType())));
		}

		return getMyModelAndView(viewForm, MODAL_TASK_LIST_AJAX_PATH, VIEW_FORM_NAME);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
