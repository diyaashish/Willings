package jp.loioz.app.user.taskManagement.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm;
import jp.loioz.app.user.taskManagement.service.TaskCommonService;
import jp.loioz.app.user.taskManagement.service.TaskDocService;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.CommonConstant.TaskMenu;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.StringUtils;

/**
 * タスク管理画面の一覧出力コントローラークラス
 */
@Controller
@RequestMapping("user/taskManagement")
@SessionAttributes(TaskDocController.SEARCH_FORM_NAME)
public class TaskDocController extends DefaultController {

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "taskListSearchForm";

	/** タスク一覧出力用のサービスクラス */
	@Autowired
	private TaskDocService taskDocService;

	/** タスク共通サービス */
	@Autowired
	private TaskCommonService taskCommonService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * タスク一覧 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputTaskList", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputTaskList(HttpServletResponse response, TaskListSearchForm searchForm) throws Exception {

		if (TaskMenu.TASK_ANKEN.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 「案件タスク」からダウンロードの場合、DBアクセスバリデーション
			String errorMsg = this.accessDBValidatedForTaskAnken(searchForm.getTaskAnkenListSearchForm().getAnkenId());
			if (StringUtils.isNotEmpty(errorMsg)) {
				super.setAjaxProcResultFailure(errorMsg);
			}
		}

		try {
			taskDocService.outputTaskList(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件タスクに関する処理のDBアクセスValidation
	 * 
	 * @param selectedTaskAnkenId
	 * @return
	 */
	private String accessDBValidatedForTaskAnken(Long selectedTaskAnkenId) {

		if (selectedTaskAnkenId == null) {
			return null;
		}

		try {
			taskCommonService.accessDBValidatedForTaskAnken(selectedTaskAnkenId);
		} catch (AppException ex) {
			// エラーメッセージを返却
			return getMessage(ex.getErrorType());
		}

		return null;
	}
}
