package jp.loioz.app.user.taskManagement.service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0011ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.TaskListExcelDto;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.TaskAnkenListSearchForm;
import jp.loioz.common.constant.CommonConstant.TaskMenu;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TTaskDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.TaskListDto;
import jp.loioz.entity.TAnkenEntity;

/**
 * タスク一覧画面のExcel出力サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** タスク共通サービス */
	@Autowired
	private TaskCommonService taskCommonService;

	/** メッセージサービス */
	@Autowired
	MessageService messageService;

	/** タスクDaoクラス */
	@Autowired
	private TTaskDao tTaskDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面で表示中のタスク一覧情報をExcelで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputTaskList(HttpServletResponse response, TaskListSearchForm searchForm) throws Exception {

		// ■1.Builderを定義
		En0011ExcelBuilder en0011excelBuilder = new En0011ExcelBuilder();
		en0011excelBuilder.setConfig(excelConfig);
		en0011excelBuilder.setMessageService(messageService);
		// ファイル名設定
		if (TaskMenu.TODAY_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 今日のタスク画面
			String todayStr = DateUtils.parseToString(LocalDate.now(), DateUtils.DATE_JP_YYYY_M_D);
			en0011excelBuilder.setFileName(MessageFormat.format(En0011ExcelBuilder.TODAY_TASK_TITLE, todayStr));
		} else if (TaskMenu.FUTURE_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 期限付きのタスク画面
			en0011excelBuilder.setFileName(TaskMenu.FUTURE_TASK.getVal());
		} else if (TaskMenu.ALL_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// すべてのタスク画面
			en0011excelBuilder.setFileName(TaskMenu.ALL_TASK.getVal());
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 期限を過ぎたタスク画面
			en0011excelBuilder.setFileName(TaskMenu.OVERDUE_TASK.getVal());
		} else if (TaskMenu.ASSIGNED_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 割り当てられたタスク画面
			en0011excelBuilder.setFileName(TaskMenu.ASSIGNED_TASK.getVal());
		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 割り当てたタスク画面
			en0011excelBuilder.setFileName(TaskMenu.ASSIGN_TASK.getVal());
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 完了したタスク画面
			en0011excelBuilder.setFileName(TaskMenu.CLOSE_TASK.getVal());
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 検索結果タスク画面
			en0011excelBuilder.setFileName(TaskMenu.SEARCH_RESULTS_TASK.getVal() + "のタスク");
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// 案件タスク画面
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(searchForm.getTaskAnkenListSearchForm().getAnkenId());
			if (tAnkenEntity == null) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			String ankenName = StringUtils.isEmpty(tAnkenEntity.getAnkenName())? "(案件名未入力)" : tAnkenEntity.getAnkenName();
			String ankenId = AnkenId.of(tAnkenEntity.getAnkenId()).toString();
			en0011excelBuilder.setFileName(ankenName + "(" + ankenId + ")");
		}

		// ■2.ダウンロードデータ件数チェック
		if (!en0011excelBuilder.validDataCountOver(getTaskCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		TaskListExcelDto taskListExcelDto = en0011excelBuilder.createNewTargetBuilderDto();

		// ■4.DTOに設定するデータ取得と設定
		taskListExcelDto.setSelectedTaskListMenu(searchForm.getSelectedTaskListMenu());
		List<TaskListDto> TaskListDtoList = getTaskList(searchForm);
		if (TaskMenu.ASSIGNED_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// タスク一覧データ取得
			taskListExcelDto.setTaskListForTaskManagementDtoList(taskCommonService.organizeAssignedTasks(TaskListDtoList));
		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(searchForm.getSelectedTaskListMenu())) {
			// タスク一覧データ取得
			taskListExcelDto.setTaskListForTaskManagementDtoList(taskCommonService.organizeAssignTasks(TaskListDtoList));
		} else {
			// タスク一覧データ取得
			taskListExcelDto.setTaskListDtoList(TaskListDtoList);
		}
		en0011excelBuilder.setTaskListExcelDto(taskListExcelDto);

		try {
			// Excelファイルの出力処理
			en0011excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}

	}


	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * タスク数を取得する。選択中メニューで出力するデータを決定。
	 * 
	 * @param searchForm
	 */
	private int getTaskCount(TaskListSearchForm searchForm) {
		String taskMenu = searchForm.getSelectedTaskListMenu();

		if (TaskMenu.TODAY_TASK.equalsByCode(taskMenu)) {
			// 「今日のタスク」データ取得
			return tTaskDao.selectTodayTaskByAccountIdCount(SessionUtils.getLoginAccountSeq(), LocalDate.now());
		} else if (TaskMenu.FUTURE_TASK.equalsByCode(taskMenu)) {
			// 「期限付きのタスク」データ取得
			return tTaskDao.selectFutureTaskByAccountIdCount(SessionUtils.getLoginAccountSeq(), LocalDate.now().minusDays(1));
		} else if (TaskMenu.ALL_TASK.equalsByCode(taskMenu)) {
			// 「すべてのタスク」データ取得
			return tTaskDao.selectAllTaskCountByAccountId(SessionUtils.getLoginAccountSeq());
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(taskMenu)) {
			// 「期限を過ぎたタスク」データ取得
			return tTaskDao.selectOverdueTaskByAccountIdCount(SessionUtils.getLoginAccountSeq(), LocalDate.now());
		} else if (TaskMenu.ASSIGNED_TASK.equalsByCode(taskMenu)) {
			// 「割り当てられたタスク」データ取得
			return tTaskDao.selectAssignedTaskByAccountIdCount(SessionUtils.getLoginAccountSeq());
		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(taskMenu)) {
			// 「割り当てたタスク」データ取得
			return tTaskDao.selectAssignTaskByAccountIdCount(SessionUtils.getLoginAccountSeq());
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(taskMenu)) {
			// 「完了したタスク」データ取得
			return tTaskDao.selectCloseTaskByAccountIdCount(SessionUtils.getLoginAccountSeq());
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(taskMenu)) {
			// 「検索結果タスク」データ取得
			return tTaskDao.selectSearchResultsTaskByAccountIdAndSearchWordCount(SessionUtils.getLoginAccountSeq(), searchForm.getSearchResultsTaskListSearchForm().getSearchWord());
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(taskMenu)) {
			// 「案件タスク」データ取得
			TaskAnkenListSearchForm taskAnkenListSearchForm = searchForm.getTaskAnkenListSearchForm();
			return tTaskDao.selectTaskAnkenCountByAnkenId(taskAnkenListSearchForm.getAnkenId(), taskAnkenListSearchForm.getDispKeyCd());
		}
		return 0;
	}

	/**
	 * タスク一覧データを取得する。選択中メニューで出力するデータを決定。
	 * 
	 * @param searchForm
	 */
	private List<TaskListDto> getTaskList(TaskListSearchForm searchForm) {
		String taskMenu = searchForm.getSelectedTaskListMenu();

		if (TaskMenu.TODAY_TASK.equalsByCode(taskMenu)) {
			// 「今日のタスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getTodayTaskList());
		} else if (TaskMenu.FUTURE_TASK.equalsByCode(taskMenu)) {
			// 「期限付きのタスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getFutureTaskList());
		} else if (TaskMenu.ALL_TASK.equalsByCode(taskMenu)) {
			// 「すべてのタスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getAllTaskList(searchForm.getAllTaskListSearchForm().getSortKeyCd()));
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(taskMenu)) {
			// 「期限を過ぎたタスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getOverdueTaskList());
		} else if (TaskMenu.ASSIGNED_TASK.equalsByCode(taskMenu)) {
			// 「割り当てられたタスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getAssignedTaskList());
		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(taskMenu)) {
			// 「割り当てたタスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getAssignTaskList());
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(taskMenu)) {
			// 「完了したタスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getCloseTaskList(searchForm.getCloseTaskListSearchForm().getSortKeyCd()));
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(taskMenu)) {
			// 「検索結果タスク」データ取得
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getSearchResultsTaskList(searchForm.getSearchResultsTaskListSearchForm().getSearchWord()));
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(taskMenu)) {
			// 「案件タスク」データ取得
			TaskAnkenListSearchForm taskAnkenListSearchForm = searchForm.getTaskAnkenListSearchForm();
			return taskCommonService.convertBeanListForDtoList(taskCommonService.getTaskAnkenList(taskAnkenListSearchForm.getAnkenId(), taskAnkenListSearchForm.getDispKeyCd(), taskAnkenListSearchForm.getSortKeyCd()));
		}
		return new ArrayList<TaskListDto>();
	}




}