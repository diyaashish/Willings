package jp.loioz.app.user.taskManagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenDto;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenSelectListDto;
import jp.loioz.app.user.taskManagement.dto.TaskCheckItemDto;
import jp.loioz.app.user.taskManagement.form.CommentForm;
import jp.loioz.app.user.taskManagement.form.TaskCheckItemForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListInputForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListInputForm.TaskDetailInputForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.TaskAnkenListSearchForm;
import jp.loioz.app.user.taskManagement.service.TaskCommonService;
import jp.loioz.app.user.taskManagement.service.TaskService;
import jp.loioz.common.constant.CommonConstant.CheckItemStatus;
import jp.loioz.common.constant.CommonConstant.CloseTaskListSortKey;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaskAnkenListDispKey;
import jp.loioz.common.constant.CommonConstant.TaskAnkenListSortKey;
import jp.loioz.common.constant.CommonConstant.TaskMenu;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.groups.TaskCheckItem;
import jp.loioz.common.validation.groups.TaskCommentTabComment;
import jp.loioz.common.validation.groups.TaskDetailsTabComment;
import jp.loioz.common.validation.groups.TaskLimitDate;
import jp.loioz.common.validation.groups.TaskMemo;
import jp.loioz.common.validation.groups.TaskTitle;
import jp.loioz.dto.ActivityHistoryDto;
import jp.loioz.dto.PersonInfoForAnkenDto;
import jp.loioz.dto.TaskCountDto;
import jp.loioz.dto.TaskListDto;

/**
 * タスク管理画面のコントローラークラス
 */
@Controller
@RequestMapping("user/taskManagement")
@SessionAttributes(TaskController.SEARCH_FORM_NAME)
public class TaskController extends DefaultController implements PageableController {

	/** タスク管理画面のパス */
	public static final String MY_VIEW_PATH = "user/taskManagement/task";

	/** 一覧のAjaxView */

	/** 検索結果一覧AjaxViewのパス */
	public static final String SEARCH_MATCH_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::searchMatchTaskListInputFragment";
	/** 今日のタスク一覧 AjaxViewのパス */
	public static final String TODAY_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::todayTaskListInputFragment";
	/** 期限付きのタスク一覧 AjaxViewのパス */
	public static final String FUTURE_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::futureTaskListInputFragment";
	/** すべてのタスク一覧 AjaxViewのパス */
	public static final String ALL_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::allTaskListInputFragment";
	/** 期限を過ぎたタスク一覧 AjaxViewのパス */
	public static final String OVERDUE_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::overdueTaskListInputFragment";
	/** 割り当てられたタスク一覧 AjaxViewのパス */
	public static final String ASSIGNED_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::assignedTaskListInputFragment";
	/** 割り当てたタスク一覧 AjaxViewのパス */
	public static final String ASSIGN_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::assignTaskListInputFragment";
	/** 完了したタスク一覧 AjaxViewのパス */
	public static final String CLOSE_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::closeTaskListInputFragment";
	/** 検索結果タスク一覧 AjaxViewのパス */
	public static final String SEARCH_RESULTS_TASK_LIST_AJAX_PATH = "user/taskManagement/taskFragment::searchResultsTaskListInputFragment";
	/** 案件タスク一覧 AjaxViewのパス */
	public static final String TASK_ANKEN_LIST_AJAX_PATH = "user/taskManagement/taskFragment::taskAnkenListInputFragment";

	/** 一覧の1行のAjaxView */

	/** 今日のタスク一覧の1行 AjaxViewのパス */
	public static final String TODAY_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::todayTaskListRowFragment";
	/** 期限付きのタスク一覧の1行 AjaxViewのパス */
	public static final String FUTURE_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::futureTaskListRowFragment";
	/** すべてのタスク一覧の1行 AjaxViewのパス */
	public static final String ALL_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::allTaskListRowFragment";
	/** 期限を過ぎたタスク一覧の1行 AjaxViewのパス */
	public static final String OVERDUE_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::overdueTaskListRowFragment";
	/** 割り当てられたタスク一覧の1行 AjaxViewのパス */
	public static final String ASSIGNED_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::assignedTaskListRowFragment";
	/** 割り当てたタスク一覧の1行 AjaxViewのパス */
	public static final String ASSIGN_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::assignTaskListRowFragment";
	/** 完了したタスク一覧の1行 AjaxViewのパス */
	public static final String CLOSE_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::closeTaskListRowFragment";
	/** 検索結果タスク一覧の1行 AjaxViewのパス */
	public static final String SEARCH_RESULTS_TASK_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::searchResultsTaskListRowFragment";
	/** 案件タスク一覧の1行 AjaxViewのパス */
	public static final String TASK_ANKEN_LIST_ROW_AJAX_PATH = "user/taskManagement/taskFragment::taskAnkenListRowFragment";

	/** タスクサイドメニューのパス */
	public static final String TASK_MENU_FRAGMENT_PATH = "user/taskManagement/taskFragment::menuList";

	/** messageAreaのviewパス ※説明はredirectMessageAreaメソッドに記載 */
	public static final String MESSAGE_AREA_PATH = "/common/messageArea::messageArea";

	/** タスク詳細情報入力fragmentのパス */
	private static final String TASK_DETAIL_INPUT_FRAGMENT_PATH = "user/taskManagement/taskFragment::taskDetailInputFragment";

	/** タスク詳細情報案件設定のデータリストfragmentのパス */
	public static final String TASK_ANKEN_DATA_LIST_INPUT_FRAGMENT_PATH = "user/taskManagement/taskFragment::taskAnkenDataListInputFragment";

	/** 詳細タブコメント一覧のviewパス */
	public static final String TASK_DETAIL_TAB_COMMENT_FORM_VIEW_PATH = "user/taskManagement/taskFragment::taskDetailsTabCommentForm";

	/** コメントタブコメント一覧のviewパス */
	public static final String TASK_COMMENT_TAB_COMMENT_FORM_VIEW_PATH = "user/taskManagement/taskFragment::taskCommentTabCommentForm";

	/** サブタスク一覧の1行 AjaxViewのパス */
	public static final String CHECK_ITEM_AJAX_PATH = "user/taskManagement/taskFragment::checkItemFragment";

	/** アクティビティの AjaxViewのパス */
	public static final String ACTIVITY_LIST_AJAX_PATH = "user/taskManagement/taskFragment::activityListFragment";

	/** タスク詳細情報フォームオブジェクト名 */
	private static final String TASK_DETAIL_INPUT_FORM_NAME = "taskDetailInputForm";

	/** viewで使用する画面表示用フォームオブジェクト名 */
	public static final String VIEW_FORM_NAME = "taskListViewForm";

	/** inputで使用する画面表示用フォームオブジェクト名 */
	public static final String INPUT_FORM_NAME = "taskListInputForm";

	/** タスク編集：案件を設定する時に案件選択データオブジェクト名 */
	public static final String TASK_ANKEN_DATA_LIST_NAME = "taskAnkenSelectListDtoList";

	/** 各タスク一覧の1行分の表示用データオブジェクト名 */
	public static final String TASK_LIST_ROW_DTO_NAME = "task";

	/** サブタスクの1行分の表示用データオブジェクト名 */
	public static final String CHECK_ITEM_DTO_NAME = "checkItem";

	/** アクティビティの表示用データオブジェクト名 */
	public static final String ACTIVITY_LIST_DTO_NAME = "activityList";

	/** 案件タスク（サイドメニューの案件）表示用データオブジェクト名 */
	private static final String TASK_ANKEN_ADD_DTO_NAME = "taskAnkenAddList";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "taskListSearchForm";

	/** タスク一覧ページャのviewパス */
	public static final String TASK_LIST_PAGER_FRAGMENT = "common/pagerAjax::pagerAjax";

	/** タスク一覧ページャのページ情報（定義） */
	public static final String TASK_LIST_PAGER_LIST_PAGE = "page";

	/** タスク一覧ページャフラグメントID（定義） */
	public static final String TASK_LIST_PAGER_LIST_FRAGMENT_ID = "listFragmentId";

	/** タスク一覧ページャフラグメントID（値） */
	public static final String TASK_LIST_PAGER_LIST_FRAGMENT_ID_VALUE = "taskListTitle";

	/** タスク一覧ページャAjaxファンクション名（定義） */
	public static final String TASK_LIST_PAGER_AJAX_FUNC_NAME = "pagingAjaxFuncName";

	/** タスク一覧ページャAjaxファンクション名（値） */
	public static final String TASK_LIST_PAGER_AJAX_FUNC_NAME_VALUE = "taskListPaging";

	/** タスク一覧のServiceクラス */
	@Autowired
	private TaskService taskService;

	/** タスク共通サービス */
	@Autowired
	private TaskCommonService taskCommonService;

	/** ダミーページ メソッドのパラメータでRequestParam使用時、エラー回避のための不要なpage用で使用する */
	private static final int DUMMY_PAGE_NUMBER = 0;
	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * タスクの初期表示処理<br>
	 * セッションから元々表示していたタスク画面が取得できたときは、その画面を表示する<br>
	 * セッションから表示していたタスク画面が取得できない場合は、今日のタスク画面を表示する<br>
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(TaskListSearchForm taskListSearchForm) {

		ModelAndView mv = null;

		// セッションから開いていたタスク画面を取得
		String openTaskMenu = taskListSearchForm.getSelectedTaskListMenu();

		// セッション情報が無い場合は、今日のタスク画面を表示する
		if (StringUtils.isEmpty(openTaskMenu) || taskListSearchForm.getAllTaskListSearchForm() == null) {

			// 検索条件フォーム初期化
			taskListSearchForm.initForm();

			// 検索条件：選択中メニューに「今日のタスク」をセット
			taskListSearchForm.setSelectedTaskListMenu(TaskMenu.TODAY_TASK.getCd());

			// 今日のタスク画面を表示する
			TaskListInputForm inputForm = taskService.createInputForm();

			// 画面情報の取得と設定を行います。
			taskService.setTodayTaskBeanList(inputForm, taskListSearchForm.getTodayTaskListSearchForm());

			try {
				// 画面情報を返します。
				mv = getMyModelAndView(inputForm, MY_VIEW_PATH, INPUT_FORM_NAME);
				mv.addObject("selectedTabClass", TaskMenu.TODAY_TASK.getCd());
				mv.addObject("listTitle", TaskMenu.TODAY_TASK.getVal());
			} catch (Exception ex) {
				return ajaxException(ex);
			}

		} else {

			// DBアクセスバリデーション 「案件タスク」メニュー選択時、選択中の案件タスクが無くなっている場合は「すべてのタスク」を表示する
			if (TaskMenu.TASK_ANKEN.equalsByCode(openTaskMenu)) {
				String errorMsg = this.accessDBValidatedForTaskAnken(taskListSearchForm.getTaskAnkenListSearchForm().getAnkenId());
				if (StringUtils.isNotEmpty(errorMsg)) {
					openTaskMenu = TaskMenu.ALL_TASK.getCd();
					taskListSearchForm.setSelectedTaskListMenu(TaskMenu.ALL_TASK.getCd());
				}
			}

			// セッションから取得したタスク画面を表示する
			try {
				TaskListInputForm inputForm = taskService.createInputForm();
				setTaskBeanList(openTaskMenu, inputForm, taskListSearchForm);
				mv = getMyModelAndView(inputForm, MY_VIEW_PATH, INPUT_FORM_NAME);
			} catch (Exception ex) {
				return ajaxException(ex);
			}

			if (mv == null) {
				// 画面の取得に失敗した場合
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
				return mv;
			}

			// 処理後に一覧画面を再表示するためタスクメニューに関する情報を設定
			mv.addObject("selectedTabClass", TaskMenu.of(openTaskMenu).getCd());
			mv.addObject("selectedTaskAnkenId", taskListSearchForm.getTaskAnkenListSearchForm().getAnkenId());
			this.setTaskListTitle(mv, taskListSearchForm, openTaskMenu);

			super.setAjaxProcResultSuccess();
		}

		// 各タスク件数をModelAndViewにobjectとしてセットする
		mv = setTaskCount(mv);

		// 案件タスク追加情報をModelAndViewにobjectとしてセットする
		mv = setAnkenTaskAdd(mv);

		return mv;
	}

	/**
	 * 任意のタスク一覧画面の表示<br>
	 * selectedTabClassの値で表示するタスク一覧画面の種類を決定する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param selectedAssignSeq
	 * @param pageForSelectedTaskSeq
	 * @param selectedTaskAnkenId
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/displayAnyTaskList", method = RequestMethod.GET)
	public ModelAndView displayAnyTaskList(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@RequestParam(name = "pageForSelectedTaskSeq") Long pageForSelectedTaskSeq,
			@RequestParam(name = "selectedTaskAnkenId") Long selectedTaskAnkenId,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 検索条件フォームにパラメータをセットする
		taskListSearchForm.setSelectedTaskListMenu(selectedTabClass);
		taskListSearchForm.getTaskAnkenListSearchForm().setAnkenId(selectedTaskAnkenId);

		// タスク一覧情報表示部分を返却する
		TaskListInputForm inputForm = taskService.createInputForm();
		setTaskBeanList(selectedTabClass, inputForm, taskListSearchForm);

		// ページングする画面で、データが少なくなっていて該当ページが開けない場合は現時点の最終ページで開く
		if (!inputForm.isExistTask()) {
			if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getTodayTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getTodayTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getAllTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getAllTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.OVERDUE_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getOverdueTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getOverdueTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.CLOSE_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getCloseTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getCloseTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getSearchResultsTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getSearchResultsTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getTaskAnkenListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getTaskAnkenListSearchForm().setPage(pageNumber);
				}
			}
			// 検索条件に該当するタスク一覧情報を取得
			setTaskBeanList(selectedTabClass, inputForm, taskListSearchForm);
		}

		mv = getMyModelAndView(inputForm, getTaskListPath(selectedTabClass), INPUT_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		// 処理後に一覧画面を再表示するためタスクメニューに関する情報を設定
		mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
		this.setTaskListTitle(mv, taskListSearchForm, selectedTabClass);
		mv.addObject("selectedTaskSeq", selectedTaskSeq);
		mv.addObject("selectedAssignSeq", selectedAssignSeq);
		mv.addObject("pageForSelectedTaskSeq", pageForSelectedTaskSeq);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 指定のタスクタブ（タスクのメニュー）のタスク一覧の1行分のHTMLを取得する<br>
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param taskSeq
	 * @param selectedAssignSeq
	 * @param selectedTaskSeq
	 * @param selectedTaskAnkenId
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getTaskListRow", method = RequestMethod.GET)
	public ModelAndView getTaskListRow(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			TaskListDto dto = taskService.getTaskListRowData(taskSeq);
			if (dto != null) {
				String taskListRowViewPath = this.getTaskListRowViewPath(selectedTabClass);
				mv = getMyModelAndView(dto, taskListRowViewPath, TASK_LIST_ROW_DTO_NAME);
			}
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		// 「すべてのタスク」メニューの一覧表示可否
		if (taskCommonService.isAllTask(taskSeq, SessionUtils.getLoginAccountSeq())) {
			mv.addObject("isAllTask", true);
		} else {
			mv.addObject("isAllTask", false);
		}

		// 現在のタスクの選択状態
		mv.addObject("selectedTaskSeq", selectedTaskSeq);
		mv.addObject("selectedAssignSeq", selectedAssignSeq);
		mv.addObject("taskAssignSeq", "");

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 指定のタスクタブ（タスクのメニュー）のタスク一覧の前ページ最終行1行分のHTMLを取得する<br>
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param selectedAssignSeq
	 * @param dispPageTaskSeqList
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getTaskRowOnPrevPage", method = RequestMethod.GET)
	public ModelAndView getTaskRowOnPrevPage(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@RequestParam(name = "dispPageTaskSeqList") List<Long> dispPageTaskSeqList,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			TaskListDto dto = taskService.getTaskRowOnPrevPage(selectedTabClass, dispPageTaskSeqList, taskListSearchForm);
			if (dto != null) {
				String taskListRowViewPath = this.getTaskListRowViewPath(selectedTabClass);
				mv = getMyModelAndView(dto, taskListRowViewPath, TASK_LIST_ROW_DTO_NAME);
			} else {
				// 前ページ最終行データが取得できない
				return null;
			}
		} catch (AppException aex) {
			// 想定内エラー、エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(aex.getErrorType()));
			return null;
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		// 現在のタスクの選択状態
		mv.addObject("selectedTaskSeq", selectedTaskSeq);
		mv.addObject("selectedAssignSeq", selectedAssignSeq);
		mv.addObject("taskAssignSeq", "");

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 指定のタスクタブ（タスクのメニュー）タスク一覧の次ページ先頭行1行分のHTMLを取得する<br>
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param dispPageTaskSeqList
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getTaskRowOnNextPage", method = RequestMethod.GET)
	public ModelAndView getTaskRowOnNextPage(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "dispPageTaskSeqList") List<Long> dispPageTaskSeqList,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		TaskListDto dto = taskService.getTaskRowOnNextPage(selectedTabClass, dispPageTaskSeqList, taskListSearchForm);
		if (dto != null) {
			String taskListRowViewPath = this.getTaskListRowViewPath(selectedTabClass);
			mv = getMyModelAndView(dto, taskListRowViewPath, TASK_LIST_ROW_DTO_NAME);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タスク詳細画面の表示
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/taskDetail", method = RequestMethod.GET)
	public ModelAndView taskDetail(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// タスク詳細の画面取得
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm = taskService.createTaskDetailInputForm(taskSeq, true);
			mv = getMyModelAndView(taskDetailInputForm, TASK_DETAIL_INPUT_FRAGMENT_PATH, TASK_DETAIL_INPUT_FORM_NAME);
			mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
			mv.addObject("selectedAssignSeq", selectedAssignSeq);
			mv.addObject("selectedTaskAnkenId", taskListSearchForm.getTaskAnkenListSearchForm().getAnkenId());
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タスク画面のサイドメニュー画面を取得
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskAnkenId
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getTaskMenu", method = RequestMethod.GET)
	public ModelAndView getTaskMenuView(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskAnkenId") Long selectedTaskAnkenId,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForTaskAnken(selectedTaskAnkenId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			return mv;
		}

		try {
			// 案件タスク選択の場合
			if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)
					&& selectedTaskAnkenId != null
					&& !selectedTaskAnkenId.equals(taskListSearchForm.getTaskAnkenListSearchForm().getAnkenId())) {
				// 選択中メニューをセット
				taskListSearchForm.setSelectedTaskListMenu(selectedTabClass);
				// 選択した案件IDをセット
				taskListSearchForm.getTaskAnkenListSearchForm().setAnkenId(Long.valueOf(selectedTaskAnkenId));
				// 案件タスク表示で案件IDが変わった場合、ページ、並び順、完了タスク表示フラグを初期化する
				taskListSearchForm.getTaskAnkenListSearchForm().setDefaultPage();
				taskListSearchForm.getTaskAnkenListSearchForm().setSortKeyCd(TaskAnkenListSortKey.DEFAULT.getCd());
				taskListSearchForm.getTaskAnkenListSearchForm().setDispKeyCd(TaskAnkenListDispKey.INCOMPLETE.getCd());
			}
			// サイドメニュー画面の取得
			mv = ModelAndViewUtils.getModelAndView(TASK_MENU_FRAGMENT_PATH);
			// パラメータ情報を設定
			mv.addObject("selectedTabClass", selectedTabClass);
			mv.addObject("selectedTaskAnkenId", selectedTaskAnkenId);
			// 各タスク件数をModelAndViewにobjectとしてセットする
			mv = setTaskCount(mv);
			// 案件タスク情報をModelAndViewにobjectとしてセットする
			mv = setAnkenTaskAdd(mv);

		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タスク一覧画面、タスク詳細画面でステータスを変更した時の処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param taskStatus
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param selectedAssignSeq
	 * @param isChangeStatusInDetail
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
	public ModelAndView changeStatus(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "taskStatus") String taskStatus,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@RequestParam(name = "isChangeStatusInDetail") boolean isChangeStatusInDetail,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// ステータスの更新を行います。
			taskCommonService.updateTaskStatus(taskSeq, taskStatus);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		if (isChangeStatusInDetail) {
			// 詳細領域でのステータス変更の場合は、新しいタスクの1行データを返却する
			// 一覧からステータス変更の場合は、ここで行データを取得しない。別functionで行更新処理を実行する。
			mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, selectedTaskSeq, taskListSearchForm, httpMethod);
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00133, TaskStatus.of(taskStatus).getVal()));
		return mv;
	}

	/**
	 * タスクの削除ボタンを押下した時の処理。 タスクSEQに紐づくタスクデータを削除します。
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param selectedAssignSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/deleteTask", method = RequestMethod.POST)
	public ModelAndView deleteTask(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// タスクデータを削除します。
			taskCommonService.deleteTask(taskSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "タスク"));
		return mv;
	}

	/**
	 * タスク一覧画面でのタスク追加処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param selectedAssignSeq
	 * @param selectedTaskAnkenId
	 * @param inputForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addTask", method = RequestMethod.POST)
	public Map<String, Object> addTask(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@RequestParam(name = "selectedTaskAnkenId") Long selectedTaskAnkenId,
			@Validated TaskListInputForm inputForm, BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		boolean isTodayTask = false;
		boolean isDeadlineToday = false;
		// 今日のタスク画面からタスクを作成するときは、今日やるタスクとして登録する
		if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
			isTodayTask = true;
		}

		// 期限付きのタスク画面からタスクを作成するときは、期限を今日として登録する
		if (TaskMenu.FUTURE_TASK.equalsByCode(selectedTabClass)) {
			isDeadlineToday = true;
		}

		Long insertTaskSeq = null;

		try {
			// タスク新規登録
			insertTaskSeq = taskService.insertTask(inputForm.getTaskTitle(), isTodayTask, isDeadlineToday, selectedTaskAnkenId);
			response.put("newTaskSeq", insertTaskSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00034, "タスク"));
			return response;
		} catch (AppException ex) {
			// 想定されるエラーのケース
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 表示中のページ先頭行にタスクデータを追加する。<br>
	 * 1ページ目を表示中の場合は、新規登録したタスクデータなどを追加する。2ページ目以降を表示中の場合は、前ページのラスト行タスクデータを追加する。<br>
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param selectedAssignSeq
	 * @param dispPageTaskSeqList
	 * @param selectedTaskAnkenId
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/addTaskFirstRow", method = RequestMethod.GET)
	public ModelAndView addTaskFirstRow(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@RequestParam(name = "dispPageTaskSeqList") List<Long> dispPageTaskSeqList,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 「今日のタスク」メニューの場合
		if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
			// 現在1ページ目を表示しているのであれば新しいタスクの1行データを返却する
			if (taskListSearchForm.getTodayTaskListSearchForm().getPage().intValue() <= PagerForm.DEFAULT_PAGE.intValue()) {
				mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, selectedTaskSeq, selectedAssignSeq, selectedTaskSeq, taskListSearchForm, httpMethod);
			} else {
				// 現在2ページ目以降を表示しているのであれば前ページの最終行タスクデータを返却する
				mv = this.getTaskRowOnPrevPage(DUMMY_PAGE_NUMBER, selectedTabClass, selectedTaskSeq, selectedAssignSeq, dispPageTaskSeqList, taskListSearchForm, httpMethod);
			}
		} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
			// 「すべてのタスク」メニューの場合
			// 現在1ページ目を表示しているのであれば新しいタスクの1行データを返却する
			if (taskListSearchForm.getAllTaskListSearchForm().getPage().intValue() <= PagerForm.DEFAULT_PAGE.intValue()) {
				mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, selectedTaskSeq, selectedAssignSeq, selectedTaskSeq, taskListSearchForm, httpMethod);
			} else {
				// 現在2ページ目以降を表示しているのであれば前ページの最終行タスクデータを返却する
				mv = this.getTaskRowOnPrevPage(DUMMY_PAGE_NUMBER, selectedTabClass, selectedTaskSeq, selectedAssignSeq, dispPageTaskSeqList, taskListSearchForm, httpMethod);
			}
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
			// 「案件タスク」メニューの場合
			// 現在1ページ目を表示しているのであれば新しいタスクの1行データを返却する
			if (taskListSearchForm.getTaskAnkenListSearchForm().getPage().intValue() <= PagerForm.DEFAULT_PAGE.intValue()) {
				mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, selectedTaskSeq, selectedAssignSeq, selectedTaskSeq, taskListSearchForm, httpMethod);
			} else {
				// 現在2ページ目以降を表示しているのであれば前ページの最終行タスクデータを返却する
				mv = this.getTaskRowOnPrevPage(DUMMY_PAGE_NUMBER, selectedTabClass, selectedTaskSeq, selectedAssignSeq, dispPageTaskSeqList, taskListSearchForm, httpMethod);
			}
		} else {
			// 「今日のタスク」、「すべてのタスク」、「案件タスク」メニュー以外の場合、新しいタスクの1行データを返却する
			mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, selectedTaskSeq, selectedAssignSeq, selectedTaskSeq, taskListSearchForm, httpMethod);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タスク詳細画面のタイトル変更処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param inputForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/changeTaskTitle", method = RequestMethod.POST)
	public ModelAndView changeTaskTitle(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@Validated({TaskTitle.class}) TaskDetailInputForm inputForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm;
			try {
				taskDetailInputForm = taskService.createTaskDetailInputForm(inputForm.getTaskDetail().getTaskSeq(), true);
			} catch (AppException ex) {
				// 想定されるエラーのケース
				super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
				return mv;
			}
			mv = getMyModelAndViewWithErrors(taskDetailInputForm, TASK_DETAIL_INPUT_FRAGMENT_PATH, TASK_DETAIL_INPUT_FORM_NAME, result);
			mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
			return mv;
		}

		try {
			// タスクタイトル変更
			taskService.changeTaskTitle(inputForm.getTaskDetail().getTaskSeq(), inputForm.getTaskDetail().getTitle());
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、新しいタスクの1行データを返却する
		mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, inputForm.getTaskDetail().getTaskSeq(),
				selectedAssignSeq, inputForm.getTaskDetail().getTaskSeq(), taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "タイトル"));
		return mv;
	}

	/**
	 * タスク詳細画面の詳細保存処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param inputForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/saveContent", method = RequestMethod.POST)
	public ModelAndView saveContent(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@Validated({TaskMemo.class}) TaskDetailInputForm inputForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm;
			try {
				taskDetailInputForm = taskService.createTaskDetailInputForm(inputForm.getTaskDetail().getTaskSeq(), true);
			} catch (AppException ex) {
				// 想定されるエラーのケース
				super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
				return mv;
			}
			mv = getMyModelAndViewWithErrors(taskDetailInputForm, TASK_DETAIL_INPUT_FRAGMENT_PATH, TASK_DETAIL_INPUT_FORM_NAME, result);
			mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
			return mv;
		}

		try {
			// タスクタイトル変更
			taskService.saveContent(inputForm.getTaskDetail().getTaskSeq(), inputForm.getTaskDetail().getContent());
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、特に何も返さない
		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "詳細"));
		return mv;
	}

	/**
	 * 詳細タブのコメント保存処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param inputForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveDetailsTabComment", method = RequestMethod.POST)
	public Map<String, Object> saveDetailsTabComment(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@Validated({TaskDetailsTabComment.class}) TaskDetailInputForm inputForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		CommentForm commentForm = new CommentForm();
		try {
			// タスクコメント登録
			commentForm.setTaskSeq(inputForm.getTaskDetail().getTaskSeq());
			commentForm.setComment(inputForm.getDetailsTabComment());
			taskCommonService.insertTaskComment(commentForm);
			// 画面で登録コメントを表示するため、タスク履歴SEQをリスエストに詰める
			response.put("taskHistorySeq", commentForm.getTaskHistorySeq());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00014, "コメントを"));
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 詳細タブのコメントの更新処理
	 * 
	 * @param commentForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/updateDetailComment", method = RequestMethod.POST)
	public Map<String, Object> updateDetailComment(@Validated CommentForm commentForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

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
			taskService.updateComment(commentForm);
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
	 * 詳細画面のコメントの削除処理を行います
	 * 
	 * @param commentForm
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteDetailComment", method = RequestMethod.POST)
	public Map<String, Object> deleteDetailComment(CommentForm commentForm,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 削除処理
		try {
			taskService.deleteComment(commentForm);
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
	 * コメントタブのコメント保存処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param inputForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/saveCommentTabComment", method = RequestMethod.POST)
	public ModelAndView saveCommentTabComment(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@Validated({TaskCommentTabComment.class}) TaskDetailInputForm inputForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm;
			try {
				taskDetailInputForm = taskService.createTaskDetailInputForm(inputForm.getTaskDetail().getTaskSeq(), true);
			} catch (AppException ex) {
				// 想定されるエラーのケース
				super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
				return mv;
			}
			mv = getMyModelAndViewWithErrors(taskDetailInputForm, TASK_DETAIL_INPUT_FRAGMENT_PATH, TASK_DETAIL_INPUT_FORM_NAME, result);
			mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
			return mv;
		}

		try {
			// タスクコメント登録
			CommentForm commentForm = new CommentForm();
			commentForm.setTaskSeq(inputForm.getTaskDetail().getTaskSeq());
			commentForm.setComment(inputForm.getCommentTabComment());
			taskCommonService.insertTaskComment(commentForm);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、新しいタスクの1行データを返却する
		mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, inputForm.getTaskDetail().getTaskSeq(),
				selectedAssignSeq, inputForm.getTaskDetail().getTaskSeq(), taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00014, "コメントを"));
		return mv;
	}

	/**
	 * 詳細タブのコメント履歴を取得する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getDetailsTabCommentHistory", method = RequestMethod.GET)
	public ModelAndView getDetailsTabCommentHistory(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// タスク詳細の画面取得
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm = taskService.createTaskDetailInputForm(taskSeq, false);
			// コメント関連にページ情報を返す
			return getMyModelAndView(taskDetailInputForm, TASK_DETAIL_TAB_COMMENT_FORM_VIEW_PATH, TASK_DETAIL_INPUT_FORM_NAME);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		} catch (Exception ex) {
			return ajaxException(ex);
		}
	}

	/**
	 * コメントタブのコメント履歴を取得する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getCommentTabCommentHistory", method = RequestMethod.GET)
	public ModelAndView getCommentTabCommentHistory(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// タスク詳細の画面取得
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm = taskService.createTaskDetailInputForm(taskSeq, false);
			// コメント関連にページ情報を返す
			return getMyModelAndView(taskDetailInputForm, TASK_COMMENT_TAB_COMMENT_FORM_VIEW_PATH, TASK_DETAIL_INPUT_FORM_NAME);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		} catch (Exception ex) {
			return ajaxException(ex);
		}
	}

	/**
	 * タスクのコメント件数を取得
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getNumberOfComment", method = RequestMethod.GET)
	public Map<String, Object> getNumberOfComment(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 削除処理
		try {
			long numberOfComment = taskService.getNumberOfComment(taskSeq);
			response.put("succeeded", true);
			response.put("numberOfComment", numberOfComment);
			return response;
		} catch (Exception ex) {
			response.put("succeeded", false);
			response.put("message", ex.getMessage());
			return response;
		}
	}

	/**
	 * タスク詳細画面の期限変更処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param inputForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/changeLimitDate", method = RequestMethod.POST)
	public ModelAndView changeLimitDate(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@Validated({TaskLimitDate.class}) TaskDetailInputForm inputForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		Long taskSeq = inputForm.getTaskDetail().getTaskSeq();

		// 入力バリデーション
		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm;
			try {
				taskDetailInputForm = taskService.createTaskDetailInputForm(taskSeq, true);
			} catch (AppException ex) {
				// 想定されるエラーのケース
				super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
				return mv;
			}
			taskDetailInputForm.getTaskDetail().setLimitDate(inputForm.getTaskDetail().getLimitDate());
			mv = getMyModelAndViewWithErrors(taskDetailInputForm, TASK_DETAIL_INPUT_FRAGMENT_PATH, TASK_DETAIL_INPUT_FORM_NAME, result);
			mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass));
			return mv;
		}

		try {
			// 期限日の更新を行います。
			taskService.updateTaskLimitDate(taskSeq, inputForm.getTaskDetail().getLimitDate());
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、新しいタスクの1行データを返却する
		mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, taskSeq, taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "期限"));
		return mv;
	}

	/**
	 * タスク詳細画面で期限の削除ボタンを押下した時の処理。 タスクSEQに紐づくタスク期限日を空にします。
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param pageForSelectedTaskSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/deleteLimitDtTo", method = RequestMethod.POST)
	public ModelAndView deleteLimitDtTo(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			@RequestParam(name = "pageForSelectedTaskSeq") Long pageForSelectedTaskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// 期限を空にします。
			taskService.updateTaskLimitDate(taskSeq, null);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、タスク一覧情報表示部分を返却する
		mv = displayAnyTaskList(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, pageForSelectedTaskSeq,
				taskListSearchForm.getTaskAnkenListSearchForm().getAnkenId(), taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "期限"));
		return mv;
	}

	/**
	 * タスク詳細画面で割り当てを変更する処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param workerAccountSeq
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/changeTaskTanto", method = RequestMethod.POST)
	public ModelAndView changeTaskTanto(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "workerAccountSeq") List<Long> workerAccountSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// 担当者の更新を行います。
			taskService.changeTaskTanto(taskSeq, workerAccountSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、タスク詳細情報表示部分を返却する
		try {
			taskService.createTaskDetailInputForm(taskSeq, true);
			// 処理成功後は、新しいタスクの1行データを返却する
			mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, taskSeq, taskListSearchForm, httpMethod);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "割り当て"));
		return mv;
	}

	/**
	 * タスク詳細画面で案件を設定する処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param ankenId
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param taskListSearchForm
	 * @papram httpMethod
	 * @return
	 */
	@RequestMapping(value = "/addTaskAnken", method = RequestMethod.POST)
	public ModelAndView addTaskAnken(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// タスクに設定されている案件を更新します。
			taskService.addTaskAnken(taskSeq, ankenId);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、新しいタスクの1行データを返却する
		mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, taskSeq, taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00042, "案件"));
		return mv;
	}

	/**
	 * タスク詳細画面で案件を外す処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/deleteTaskAnken", method = RequestMethod.POST)
	public ModelAndView deleteTaskAnken(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// タスクに設定されている案件を外します。
			taskService.deleteTaskAnken(taskSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、新しいタスクの1行データを返却する
		mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, taskSeq, taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00043, "案件紐付け"));
		return mv;
	}

	/**
	 * 今日のタスクに変更する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param selectedTaskWorkerSeq
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/doTaskToday", method = RequestMethod.POST)
	public ModelAndView doTaskToday(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "selectedTaskWorkerSeq") Long selectedTaskWorkerSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			taskService.updateToTaskToday(selectedTaskWorkerSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、新しいタスクの1行データを返却する
		mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, taskSeq, taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00034, "今日のタスク"));
		return mv;
	}

	/**
	 * 今日のタスクから外す
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param selectedTaskWorkerSeq
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/doTaskLater", method = RequestMethod.POST)
	public ModelAndView doTaskLater(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			@RequestParam(name = "selectedTaskWorkerSeq") Long selectedTaskWorkerSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedAssignSeq") Long selectedAssignSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			taskService.updateToTaskDoneLater(selectedTaskWorkerSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		// 処理成功後は、新しいタスクの1行データを返却する
		mv = this.getTaskListRow(DUMMY_PAGE_NUMBER, selectedTabClass, taskSeq, selectedAssignSeq, taskSeq, taskListSearchForm, httpMethod);

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "今日のタスク"));
		return mv;
	}

	/**
	 * タスク一覧の表示順を保存する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskWorkerSeq
	 * @param selectedTabClass
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/dispOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> dispOrder(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskWorkerSeq") String taskWorkerSeq,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// 表示順変更
			taskService.dispOrder(taskWorkerSeq, selectedTabClass);

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
	 * すべてのタスク一覧を並び替える
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param sortKeyCd
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sortAllTaskList", method = RequestMethod.GET)
	public ModelAndView sortAllTaskList(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "sortKeyCd") String sortKeyCd,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// ソートする場合は、最初のページを表示する
		taskListSearchForm.getAllTaskListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
		taskListSearchForm.getAllTaskListSearchForm().setSortKeyCd(sortKeyCd);

		// タスク一覧情報表示部分を返却する
		try {
			TaskListInputForm inputForm = taskService.createInputForm();
			taskService.setAllTaskBeanList(inputForm, taskListSearchForm.getAllTaskListSearchForm());
			mv = getMyModelAndView(inputForm, getTaskListPath(selectedTabClass), INPUT_FORM_NAME);
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		// 処理後に一覧画面を再表示するためタスクメニューに関する情報を設定
		mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
		this.setTaskListTitle(mv, taskListSearchForm, selectedTabClass);
		mv.addObject("selectedTaskSeq", selectedTaskSeq);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 完了したタスク一覧を並び替える
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param sortKeyCd
	 * @param taskListSearchForm
	 * @param HttpMethod httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sortCloseTaskList", method = RequestMethod.GET)
	public ModelAndView sortCloseTaskList(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "sortKeyCd") String sortKeyCd,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// ソートする場合は、最初のページを表示する
		taskListSearchForm.getCloseTaskListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
		taskListSearchForm.getCloseTaskListSearchForm().setSortKeyCd(sortKeyCd);

		// タスク一覧情報表示部分を返却する
		try {
			TaskListInputForm inputForm = taskService.createInputForm();
			taskService.setCloseTaskBeanList(inputForm, taskListSearchForm.getCloseTaskListSearchForm());
			mv = getMyModelAndView(inputForm, getTaskListPath(selectedTabClass), INPUT_FORM_NAME);
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		// 処理後に一覧画面を再表示するためタスクメニューに関する情報を設定
		mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
		this.setTaskListTitle(mv, taskListSearchForm, selectedTabClass);
		mv.addObject("selectedTaskSeq", selectedTaskSeq);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件タスク一覧を並び替える
	 * 
	 * @param pageNumber
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param sortKeyCd
	 * @param selectedTaskAnkenId
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sortTaskAnkenList", method = RequestMethod.GET)
	public ModelAndView sortTaskAnkenList(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "sortKeyCd") String sortKeyCd,
			@RequestParam(name = "selectedTaskAnkenId") Long selectedTaskAnkenId,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForTaskAnken(selectedTaskAnkenId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			return mv;
		}

		// ソートする場合は、最初のページを表示する
		TaskAnkenListSearchForm taskAnkenListSearchForm = taskListSearchForm.getTaskAnkenListSearchForm();
		taskAnkenListSearchForm.setPage(PagerForm.DEFAULT_PAGE);
		taskAnkenListSearchForm.setSortKeyCd(sortKeyCd);
		taskAnkenListSearchForm.setAnkenId(selectedTaskAnkenId);

		// タスク一覧情報表示部分を返却する
		try {
			TaskListInputForm inputForm = taskService.createInputForm();
			taskService.setTaskAnkenBeanList(inputForm, taskAnkenListSearchForm);
			mv = getMyModelAndView(inputForm, getTaskListPath(selectedTabClass), INPUT_FORM_NAME);
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		// 処理後に一覧画面を再表示するためタスクメニューに関する情報を設定
		mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
		this.setTaskListTitle(mv, taskListSearchForm, selectedTabClass);
		mv.addObject("selectedTaskSeq", selectedTaskSeq);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タスク一覧のページング処理
	 * 
	 * @param pageNumber
	 * @param selectedTabClass
	 * @param selectedTaskSeq
	 * @param pageForSelectedTaskSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/pager", method = RequestMethod.GET)
	public ModelAndView pager(
			@RequestParam(name = "page", required = true) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskSeq") Long selectedTaskSeq,
			@RequestParam(name = "pageForSelectedTaskSeq") Long pageForSelectedTaskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// ページ番号を検索フォームにセット 「期限付きのタスク」、「割り当てたタスク」、「割り当てられたタスク」はページングをしない
		if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
			taskListSearchForm.getTodayTaskListSearchForm().setPage(pageNumber);
		} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
			taskListSearchForm.getAllTaskListSearchForm().setPage(pageNumber);
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(selectedTabClass)) {
			taskListSearchForm.getOverdueTaskListSearchForm().setPage(pageNumber);
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(selectedTabClass)) {
			taskListSearchForm.getCloseTaskListSearchForm().setPage(pageNumber);
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(selectedTabClass)) {
			taskListSearchForm.getSearchResultsTaskListSearchForm().setPage(pageNumber);
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
			taskListSearchForm.getTaskAnkenListSearchForm().setPage(pageNumber);
		}

		// タスク一覧情報表示部分を返却する
		TaskListInputForm inputForm = taskService.createInputForm();
		setTaskBeanList(selectedTabClass, inputForm, taskListSearchForm);

		// ページングする画面で、データが少なくなっていて該当ページが開けない場合は現時点の最終ページで開く
		if (!inputForm.isExistTask()) {
			if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getTodayTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getTodayTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getAllTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getAllTaskListSearchForm().setPage(pageNumber);
				}

			} else if (TaskMenu.OVERDUE_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getOverdueTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getOverdueTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.CLOSE_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getCloseTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getCloseTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getSearchResultsTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getSearchResultsTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getTaskAnkenListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getTaskAnkenListSearchForm().setPage(pageNumber);
				}
			}
			// 検索条件に該当するタスク一覧情報を取得
			setTaskBeanList(selectedTabClass, inputForm, taskListSearchForm);
		}

		if (LoiozCollectionUtils.isEmpty(inputForm.getTaskListForTaskManagement())) {
			// 画面の取得に失敗した場合、リロードメッセージを表示
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		mv = getMyModelAndView(inputForm, getTaskListPath(selectedTabClass), INPUT_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		// 処理後に一覧画面を再表示するためタスクメニューに関する情報を設定
		mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
		this.setTaskListTitle(mv, taskListSearchForm, selectedTabClass);
		mv.addObject("selectedTaskSeq", selectedTaskSeq);
		mv.addObject("pageForSelectedTaskSeq", pageForSelectedTaskSeq);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タスク一覧画面のページャ部分を取得
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getTaskPager", method = RequestMethod.GET)
	public ModelAndView getTaskPager(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// タスク一覧情報表示部分を返却する
		TaskListInputForm inputForm = taskService.createInputForm();
		setTaskBeanList(selectedTabClass, inputForm, taskListSearchForm);

		// ページングする画面で、データが少なくなっていて該当ページが開けない場合は現時点の最終ページで開く
		if (!inputForm.isExistTask()) {
			if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getTodayTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getTodayTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getAllTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getAllTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.OVERDUE_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getOverdueTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getOverdueTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.CLOSE_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getCloseTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getCloseTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getSearchResultsTaskListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getSearchResultsTaskListSearchForm().setPage(pageNumber);
				}
			} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
				pageNumber = inputForm.getTaskListForTaskManagement().get(0).getPage().getTotalPages() - 1;
				if (pageNumber < 0) {
					taskListSearchForm.getTaskAnkenListSearchForm().setPage(0);
				} else {
					taskListSearchForm.getTaskAnkenListSearchForm().setPage(pageNumber);
				}
			}
			// 検索条件に該当するタスク一覧情報を取得
			setTaskBeanList(selectedTabClass, inputForm, taskListSearchForm);
		}

		if (LoiozCollectionUtils.isEmpty(inputForm.getTaskListForTaskManagement())) {
			// 画面の取得に失敗した場合、リロードメッセージを表示
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		mv = getMyModelAndView(inputForm.getTaskListForTaskManagement().get(0).getPage(), TASK_LIST_PAGER_FRAGMENT, TASK_LIST_PAGER_LIST_PAGE);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		mv.addObject(TASK_LIST_PAGER_LIST_FRAGMENT_ID_VALUE, TASK_LIST_PAGER_LIST_FRAGMENT_ID);
		mv.addObject(TASK_LIST_PAGER_AJAX_FUNC_NAME_VALUE, TASK_LIST_PAGER_AJAX_FUNC_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件タスクの固定ボタンを押下したときの処理。案件タスクに表示順を設定します。<br>
	 * 
	 * @param pageNumber
	 * @param fixAnkenId
	 * @param loginAccountSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/fixTaskAnken", method = RequestMethod.POST)
	public ModelAndView fixTaskAnken(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "fixAnkenId") Long fixAnkenId,
			@RequestParam(name = "loginAccountSeq") Long loginAccountSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForTaskAnken(fixAnkenId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			return mv;
		}

		try {
			// 案件タスクを固定します。
			taskService.fixTaskAnken(fixAnkenId, loginAccountSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "案件タスクを固定"));
		return mv;
	}

	/**
	 * 案件タスクの固定解除ボタン押下したときの処理。案件タスクの表示順を空にします。<br>
	 * 
	 * @param pageNumber
	 * @param fixAnkenId
	 * @param loginAccountSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/unFixTaskAnken", method = RequestMethod.POST)
	public ModelAndView unFixTaskAnken(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "unFixAnkenId") Long unFixAnkenId,
			@RequestParam(name = "loginAccountSeq") Long loginAccountSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForTaskAnken(unFixAnkenId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			return mv;
		}

		try {
			// 案件タスクの固定を解除します。
			taskService.unFixTaskAnken(unFixAnkenId, loginAccountSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00043, "案件タスクの固定"));
		return mv;
	}

	/**
	 * 案件タスクを解除するボタンを押下した時の処理。 ログインユーザーの案件タスクデータから案件IDデータを削除します。
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param cancelAnkenId
	 * @param loginAccountSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/cancelTaskAnken", method = RequestMethod.POST)
	public ModelAndView cancelTaskAnken(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "cancelAnkenId") Long cancelAnkenId,
			@RequestParam(name = "loginAccountSeq") Long loginAccountSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForTaskAnken(cancelAnkenId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			return mv;
		}

		try {
			// 案件タスクデータを削除します。
			taskService.cancelTaskAnken(cancelAnkenId, loginAccountSeq);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00043, "案件タスク"));
		return mv;
	}

	/**
	 * 案件タスク一覧で「完了したタスクの表示」と「未完了のタスクの表示」を切り替える。<br>
	 * 完了したタスクを
	 * 
	 * @param pageNumber
	 * @param selectedTabClass
	 * @param selectedTaskAnkenId
	 * @param selectedTaskAnkenId
	 * @param dispKeyCd
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/changeCompleteTask", method = RequestMethod.GET)
	public ModelAndView changeCompleteTask(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@RequestParam(name = "selectedTaskAnkenId") Long selectedTaskAnkenId,
			@RequestParam(name = "dispKeyCd") String dispKeyCd,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 完了タスクと未完了タスクの表示を切り替える場合は、最初のページを表示する
		TaskAnkenListSearchForm taskAnkenListSearchForm = new TaskAnkenListSearchForm();

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForTaskAnken(selectedTaskAnkenId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			return mv;
		}

		taskAnkenListSearchForm = taskListSearchForm.getTaskAnkenListSearchForm();
		taskAnkenListSearchForm.setPage(PagerForm.DEFAULT_PAGE);
		taskAnkenListSearchForm.setAnkenId(selectedTaskAnkenId);
		taskAnkenListSearchForm.setDispKeyCd(dispKeyCd);
		// ソートキーを初期値に設定する（デフォルトのコード「1」に設定する）
		taskAnkenListSearchForm.setSortKeyCd(CloseTaskListSortKey.DEFAULT.getCd());

		// タスク一覧情報表示部分を返却する
		try {
			TaskListInputForm inputForm = taskService.createInputForm();
			taskService.setTaskAnkenBeanList(inputForm, taskAnkenListSearchForm);
			mv = getMyModelAndView(inputForm, getTaskListPath(selectedTabClass), INPUT_FORM_NAME);
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		// 処理後に一覧画面を再表示するためタスクメニューに関する情報を設定
		mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
		this.setTaskListTitle(mv, taskListSearchForm, selectedTabClass);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タスク詳細画面のサブタスク保存処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param selectedTabClass
	 * @param selectedAssignSeq
	 * @param inputForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/saveTaskCheckItem", method = RequestMethod.POST)
	public ModelAndView saveTaskCheckItem(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "selectedTabClass") String selectedTabClass,
			@Validated({TaskCheckItem.class}) TaskDetailInputForm inputForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			TaskListInputForm.TaskDetailInputForm taskDetailInputForm;
			try {
				taskDetailInputForm = taskService.createTaskDetailInputForm(inputForm.getTaskDetail().getTaskSeq(), true);
			} catch (AppException ex) {
				// 想定されるエラーのケース
				super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
				return mv;
			}
			mv = getMyModelAndViewWithErrors(taskDetailInputForm, TASK_DETAIL_INPUT_FRAGMENT_PATH, TASK_DETAIL_INPUT_FORM_NAME, result);
			mv.addObject("selectedTabClass", TaskMenu.of(selectedTabClass).getCd());
			return mv;
		}

		// サブタスク上限チェック
		if (taskService.validateCheckItemLimit(inputForm.getTaskDetail().getTaskSeq())) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return mv;
		}

		try {
			// サブタスク登録
			TaskCheckItemDto taskCheckItemDto = taskCommonService.saveTaskCheckItem(inputForm.getTaskDetail().getTaskSeq(), inputForm.getTaskDetail().getCheckItem(), SystemFlg.FLG_OFF.getCd());
			mv = getMyModelAndView(taskCheckItemDto, CHECK_ITEM_AJAX_PATH, CHECK_ITEM_DTO_NAME);
		} catch (AppException ex) {
			// 想定されるエラーのケース
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00034, "サブタスク"));
		return mv;
	}

	/**
	 * サブタスク1行分のデータを取得する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskCheckItemSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/getCheckItem", method = RequestMethod.GET)
	public ModelAndView getCheckItem(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskCheckItemSeq") Long taskCheckItemSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
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
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param searchWord
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/searchAnkenList", method = RequestMethod.GET)
	public ModelAndView searchAnkenList(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "searchWord") String searchWord,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			// タスク詳細の画面取得
			List<TaskAnkenSelectListDto> taskAnkenSelectListDtoList = taskCommonService.searchAnkenList(searchWord);
			mv = getMyModelAndView(taskAnkenSelectListDtoList, TASK_ANKEN_DATA_LIST_INPUT_FRAGMENT_PATH, TASK_ANKEN_DATA_LIST_NAME);
		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * サブタスク名変更処理
	 * 
	 * @param checkItemForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/changeCheckItemName", method = RequestMethod.POST)
	public Map<String, Object> changeCheckItemName(@Validated TaskCheckItemForm checkItemForm,
			BindingResult result,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

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
	 * サブタスクステータス変更処理
	 * 
	 * @param checkItemForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/changeCheckItemStatus", method = RequestMethod.POST)
	public Map<String, Object> changeCheckItemStatus(TaskCheckItemForm checkItemForm,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

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
	 * タスク詳細画面のアクティビティタブ表示処理
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/displayActivity", method = RequestMethod.GET)
	public ModelAndView displayActivity(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

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
	 * サブタスクの表示順を保存する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param checkItemSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/changeCheckItemDispOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changeCheckItemDispOrder(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "checkItemSeq") String checkItemSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

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
	 * サブタスク削除処理
	 * 
	 * @param checkItemForm
	 * @param result
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return 更新後のhtml
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteCheckItem", method = RequestMethod.POST)
	public Map<String, Object> deleteCheckItem(TaskCheckItemForm checkItemForm,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

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
	 * パラメータのタスクSEQの一覧で表示する次タスクSEQを取得する<br>
	 * 次タスクが無い場合は一覧の先頭で表示するタスクSEQを取得する
	 * 
	 * @param pageNumber 不要なパラメータ。PageableControllerでRequestParamを使用するときは page
	 * がないとエラーとなるため作成。
	 * @param taskSeq
	 * @param taskListSearchForm
	 * @param httpMethod
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getNextTaskSeq", method = RequestMethod.GET)
	public Map<String, Object> getNextTaskSeq(
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "taskSeq") Long taskSeq,
			TaskListSearchForm taskListSearchForm,
			HttpMethod httpMethod) {

		Map<String, Object> response = new HashMap<>();

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, taskListSearchForm)) {
			return null;
		}

		try {
			Long nextTaskSeq = taskService.getTaskSeq(taskSeq, taskListSearchForm);
			response.put("succeeded", true);
			response.put("nextTaskSeq", nextTaskSeq);
		} catch (Exception ex) {
			response.put("succeeded", false);
			response.put("message", ex.getMessage());
		}

		return response;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * タスクメニューによって、画面で表示するデータを決定し、inputFormにセットする。
	 * 
	 * @param taskMenu タスクメニュー
	 * @param inputForm
	 * @param taskListSearchForm
	 */
	private void setTaskBeanList(String taskMenu, TaskListInputForm inputForm, TaskListSearchForm taskListSearchForm) {
		if (TaskMenu.TODAY_TASK.equalsByCode(taskMenu)) {
			taskService.setTodayTaskBeanList(inputForm, taskListSearchForm.getTodayTaskListSearchForm());
		} else if (TaskMenu.FUTURE_TASK.equalsByCode(taskMenu)) {
			taskService.setFutureTaskBeanList(inputForm);
		} else if (TaskMenu.ALL_TASK.equalsByCode(taskMenu)) {
			taskService.setAllTaskBeanList(inputForm, taskListSearchForm.getAllTaskListSearchForm());
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(taskMenu)) {
			taskService.setOverdueTaskBeanList(inputForm, taskListSearchForm.getOverdueTaskListSearchForm());
		} else if (TaskMenu.ASSIGNED_TASK.equalsByCode(taskMenu)) {
			taskService.setAssignedTaskBeanList(inputForm);
		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(taskMenu)) {
			taskService.setAssignTaskBeanList(inputForm);
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(taskMenu)) {
			taskService.setCloseTaskBeanList(inputForm, taskListSearchForm.getCloseTaskListSearchForm());
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(taskMenu)) {
			taskService.setSearchResultsTaskBeanList(inputForm, taskListSearchForm.getSearchResultsTaskListSearchForm());
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(taskMenu)) {
			taskService.setTaskAnkenBeanList(inputForm, taskListSearchForm.getTaskAnkenListSearchForm());
		}
	}

	/**
	 * タスクメニューから表示する画面パスを取得する
	 * 
	 * @param taskMenu タスクメニュー
	 * @param inputForm
	 */
	private String getTaskListPath(String taskMenu) {
		if (TaskMenu.TODAY_TASK.equalsByCode(taskMenu)) {
			return TODAY_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.FUTURE_TASK.equalsByCode(taskMenu)) {
			return FUTURE_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.ALL_TASK.equalsByCode(taskMenu)) {
			return ALL_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(taskMenu)) {
			return OVERDUE_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.ASSIGNED_TASK.equalsByCode(taskMenu)) {
			return ASSIGNED_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(taskMenu)) {
			return ASSIGN_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(taskMenu)) {
			return CLOSE_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(taskMenu)) {
			return SEARCH_RESULTS_TASK_LIST_AJAX_PATH;
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(taskMenu)) {
			return TASK_ANKEN_LIST_AJAX_PATH;
		}
		return "";
	}

	/**
	 * タスクメニューから表示する一覧画面の1行分のフラグメントのパスを取得する
	 * 
	 * @param taskMenu タスクメニュー
	 */
	private String getTaskListRowViewPath(String taskMenu) {
		if (TaskMenu.TODAY_TASK.equalsByCode(taskMenu)) {
			return TODAY_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.FUTURE_TASK.equalsByCode(taskMenu)) {
			return FUTURE_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.ALL_TASK.equalsByCode(taskMenu)) {
			return ALL_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(taskMenu)) {
			return OVERDUE_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.ASSIGNED_TASK.equalsByCode(taskMenu)) {
			return ASSIGNED_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(taskMenu)) {
			return ASSIGN_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(taskMenu)) {
			return CLOSE_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(taskMenu)) {
			return SEARCH_RESULTS_TASK_LIST_ROW_AJAX_PATH;
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(taskMenu)) {
			return TASK_ANKEN_LIST_ROW_AJAX_PATH;
		}
		return "";
	}

	/**
	 * ModelAndViewに各タスク件数をObjectとしてセットします
	 * 
	 * @param mv
	 * @return
	 */
	private ModelAndView setTaskCount(ModelAndView mv) {
		TaskCountDto taskCountDto = taskService.getTaskCount();
		// 今日のタスク数
		mv.addObject("numberOfTodayTask", taskCountDto.getNumberOfTodayTask());
		// 今日のタスクの新着、未確認タスク数
		mv.addObject("numberOfTodayNewTask", taskCountDto.getNumberOfTodayNewTask());
		// 期限付きのタスク数
		mv.addObject("numberOfFutureTask", taskCountDto.getNumberOfFutureTask());
		// 期限付きのタスクの新着、未確認タスク数
		mv.addObject("numberOfFutureNewTask", taskCountDto.getNumberOfFutureNewTask());
		// すべてのタスク数
		mv.addObject("numberOfAllTask", taskCountDto.getNumberOfAllTask());
		// すべてのタスクの新着、未確認タスク数
		mv.addObject("numberOfAllNewTask", taskCountDto.getNumberOfAllNewTask());
		// 期限を過ぎたタスク数
		mv.addObject("numberOfOverdueTask", taskCountDto.getNumberOfOverdueTask());
		// 期限を過ぎたタスクの新着、未確認タスク数
		mv.addObject("numberOfOverdueNewTask", taskCountDto.getNumberOfOverdueNewTask());
		// 割り当てられたタスク数
		mv.addObject("numberOfAssignedTask", taskCountDto.getNumberOfAssignedTask());
		// 割り当てられたタスクの新着、未確認タスク数
		mv.addObject("numberOfAssignedNewTask", taskCountDto.getNumberOfAssignedNewTask());
		// 割り当てたタスク数
		mv.addObject("numberOfAssignTask", taskCountDto.getNumberOfAssignTask());
		// 割り当てたタスクの中の新着、未確認タスク数
		mv.addObject("numberOfAssignNewTask", taskCountDto.getNumberOfAssignNewTask());
		// 完了したタスクの中の新着、未確認タスク数
		mv.addObject("numberOfCompletedNewTask", taskCountDto.getNumberOfCompletedNewTask());

		return mv;
	}

	/**
	 * ModelAndViewに案件タスク情報をObjectとしてセットします
	 * 
	 * @param mv
	 * @return
	 */
	private ModelAndView setAnkenTaskAdd(ModelAndView mv) {
		List<TaskAnkenDto> taskAnkenList = taskService.getTaskAnkenAddList(SessionUtils.getLoginAccountSeq());

		mv.addObject(TASK_ANKEN_ADD_DTO_NAME, taskAnkenList);

		return mv;
	}

	/**
	 * セッション情報が有るか確認する。
	 * 
	 * <pre>
	 * 複数タブで操作している場合、いずれかのタブで再ログインをし、Session（ブラウザが保持するCookie）が変わった場合、
	 * 開いていたタスク画面で再操作をしようとすると、Sessionが変わっているため、TaskListSearchFormの中の値がNULLになっている状態が発生する。
	 * そのまま後続処理を行うとNullPointerExceptionなどエラーが発生するため、Sessionが変わった（TaskListSearchFormの中の値がNULL）などの場合は
	 * 後続処理を行わないようにする。
	 * 
	 * ※POSTリクエストの場合、上記のSessionが変わった状態でリクエストすると、
	 * 　フレームワーク側のCSRFトークンチェックでNGとなり、Controllerの処理までこないため、ここではPOSTの処理は考慮しない。
	 * </pre>
	 * 
	 * @param httpMethod
	 * @param taskListSearchForm
	 * @return
	 */
	private boolean existSession(HttpMethod httpMethod, TaskListSearchForm taskListSearchForm) {

		// POSTの場合はセッション有無を確認しない
		if (httpMethod == null || httpMethod == HttpMethod.POST) {
			return true;
		}

		// セッションにフォームが有るか確認する
		if (taskListSearchForm == null || taskListSearchForm.getAllTaskListSearchForm() == null) {
			super.setAjaxProcResultFailure(MessageEnum.MSG_E00161.getMessageKey());
			return false;
		} else {
			return true;
		}
	}

	/**
	 * タスクの一覧部分に表示するタイトルを設定します。
	 * 
	 * @param mv
	 * @param taskListSearchForm
	 * @param selectedTabClass
	 */
	private void setTaskListTitle(ModelAndView mv, TaskListSearchForm taskListSearchForm, String selectedTabClass) {

		Long ankenId = taskListSearchForm.getTaskAnkenListSearchForm().getAnkenId();
		if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass) && ankenId != null) {
			// 案件タスク

			// 案件情報を取得
			TaskAnkenDto taskAnkenDto = taskService.getTaskAnkenDetails(ankenId, selectedTabClass);

			// 案件名、案件ID、顧客名をタイトルに表示する
			mv.addObject("listTitle", taskAnkenDto.getAnkenName());
			mv.addObject("listTitleAnkenId", taskAnkenDto.getAnkenId());
			mv.addObject("listAnkenType", taskAnkenDto.getAnkenType());
			mv.addObject("listKeiji", taskAnkenDto.isKeiji());
			// 相手方
			mv.addObject("listOpposingParty", taskAnkenDto.getOpposingPartyList());
			// 共犯者
			mv.addObject("listAccomplice", taskAnkenDto.getAccompliceList());
			// 被害者
			mv.addObject("listVictim", taskAnkenDto.getVictimList());

			// 案件タスクリストを取得
			List<PersonInfoForAnkenDto> ankenCustomerNameList = taskService.getAnkenCustomerName(ankenId);
			mv.addObject("listTitleCustomerName", ankenCustomerNameList);

		} else {
			// 通常のタスク
			mv.addObject("listTitle", TaskMenu.of(selectedTabClass).getVal());

		}
	}

	/**
	 * 案件タスクに関する処理のDBアクセスValidation
	 * 
	 * @param ankenId
	 * @return
	 */
	private String accessDBValidatedForTaskAnken(Long ankenId) {

		if (ankenId == null) {
			return null;
		}

		try {
			taskCommonService.accessDBValidatedForTaskAnken(ankenId);
		} catch (AppException ex) {
			// エラーメッセージを返却
			return getMessage(ex.getErrorType());
		}

		return null;
	}

}
