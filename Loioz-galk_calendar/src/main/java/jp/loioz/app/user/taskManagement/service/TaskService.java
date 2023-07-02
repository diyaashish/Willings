package jp.loioz.app.user.taskManagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenDto;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenDto.Accomplice;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenDto.OpposingParty;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenDto.Victim;
import jp.loioz.app.user.taskManagement.dto.TaskCheckItemDto;
import jp.loioz.app.user.taskManagement.form.CommentForm;
import jp.loioz.app.user.taskManagement.form.edit.TaskEditInputForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListInputForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListInputForm.TaskTanto;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.AllTaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.CloseTaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.OverdueTaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.SearchResultsTaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.TaskAnkenListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListSearchForm.TodayTaskListSearchForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListViewForm;
import jp.loioz.bean.AnkenKanyoshaBean;
import jp.loioz.bean.AnkenPersonCntBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaskMenu;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TTaskAnkenDao;
import jp.loioz.dao.TTaskCheckItemDao;
import jp.loioz.dao.TTaskDao;
import jp.loioz.dao.TTaskHistoryDao;
import jp.loioz.dao.TTaskWorkerDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.ActivityHistoryDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.NumberOfTaskPerAnkenIdBean;
import jp.loioz.dto.PersonInfoForAnkenDto;
import jp.loioz.dto.TaskCountBean;
import jp.loioz.dto.TaskCountDto;
import jp.loioz.dto.TaskHistoryDto;
import jp.loioz.dto.TaskListBean;
import jp.loioz.dto.TaskListDto;
import jp.loioz.dto.TaskListForTaskManagementDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TTaskAnkenEntity;
import jp.loioz.entity.TTaskCheckItemEntity;
import jp.loioz.entity.TTaskHistoryEntity;
import jp.loioz.entity.TTaskWorkerEntity;

/**
 * タスク管理画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskService extends DefaultService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** タスクDaoクラス */
	@Autowired
	private TTaskDao tTaskDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件 - 顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 案件 - 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 共通アカウントサービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 共通分野サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** タスク編集画面のサービス */
	@Autowired
	private TaskEditService taskEditService;

	/** タスク共通のサービス */
	@Autowired
	private TaskCommonService taskCommonService;

	/** タスク履歴Daoクラス */
	@Autowired
	private TTaskHistoryDao tTaskHistoryDao;

	/** タスク担当者Daoクラス */
	@Autowired
	private TTaskWorkerDao tTaskWorkerDao;

	/** タスクチェックアイテムDaoクラス */
	@Autowired
	private TTaskCheckItemDao tTaskCheckItemDao;

	/** 案件タスクDaoクラス */
	@Autowired
	private TTaskAnkenDao tTaskAnkenDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * ログインユーザーに紐づく、下記タスク件数を取得します<br>
	 * 
	 * @return
	 */
	public TaskCountDto getTaskCount() {
		List<TaskCountBean> taskCountList = tTaskDao.selectTaskCount(SessionUtils.getLoginAccountSeq(), LocalDate.now(), LocalDate.now().minusDays(1));
		int index = 0;

		TaskCountDto taskCountDto = new TaskCountDto();
		// 今日のタスク件数
		taskCountDto.setNumberOfTodayTask(taskCountList.get(index++).getCount());
		// 今日のタスクの新着、未確認タスク数
		taskCountDto.setNumberOfTodayNewTask(taskCountList.get(index++).getCount());
		// 期限付きのタスク数
		taskCountDto.setNumberOfFutureTask(taskCountList.get(index++).getCount());
		// 期限付きのタスクの新着、未確認タスク数
		taskCountDto.setNumberOfFutureNewTask(taskCountList.get(index++).getCount());
		// すべてのタスク数
		taskCountDto.setNumberOfAllTask(taskCountList.get(index++).getCount());
		// すべてのタスクの新着、未確認タスク数
		taskCountDto.setNumberOfAllNewTask(taskCountList.get(index++).getCount());
		// 期限を過ぎたタスク数
		taskCountDto.setNumberOfOverdueTask(taskCountList.get(index++).getCount());
		// 期限を過ぎたタスクの新着、未確認タスク数
		taskCountDto.setNumberOfOverdueNewTask(taskCountList.get(index++).getCount());
		// 割り当てられたタスク数
		taskCountDto.setNumberOfAssignedTask(taskCountList.get(index++).getCount());
		// 割り当てられたタスクの新着、未確認タスク数
		taskCountDto.setNumberOfAssignedNewTask(taskCountList.get(index++).getCount());
		// 割り当てたタスク数
		taskCountDto.setNumberOfAssignTask(taskCountList.get(index++).getCount());
		// 割り当てたタスクの中の新着、未確認タスク数
		taskCountDto.setNumberOfAssignNewTask(taskCountList.get(index++).getCount());
		// 完了したタスクの中の新着、未確認タスク数
		taskCountDto.setNumberOfCompletedNewTask(taskCountList.get(index++).getCount());

		return taskCountDto;
	}

	/**
	 * 今日のタスクリストを取得します（ページング）
	 * 
	 * @return
	 */
	public Page<TaskListDto> getTodayTaskList(TodayTaskListSearchForm todayTaskListSearchForm) {
		// ページャー
		Pageable pageable = todayTaskListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectTodayTaskByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now(), options);

		// タスクSEQに関するコメント数、作業者を取得
		taskCommonService.setTaskCommentCountWorker(taskBeanList);

		List<TaskListDto> taskListDtoList = taskCommonService.convertBeanListForDtoList(taskBeanList);

		Page<TaskListDto> page = new PageImpl<>(taskListDtoList, pageable, options.getCount());
		return page;
	}

	/**
	 * すべてのタスクリストを取得します（ページング）<br>
	 * 検索条件フォームのsortKeyCdで並び順を指定します
	 * 
	 * @param allTaskListSearchForm
	 * @return
	 */
	public Page<TaskListDto> getAllTaskList(AllTaskListSearchForm allTaskListSearchForm) {
		// ページャー
		Pageable pageable = allTaskListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectAllTaskByAccountId(SessionUtils.getLoginAccountSeq(), allTaskListSearchForm.getSortKeyCd(), options);

		// タスクSEQに関するコメント数、作業者を取得
		taskCommonService.setTaskCommentCountWorker(taskBeanList);

		List<TaskListDto> taskListDtoList = taskCommonService.convertBeanListForDtoList(taskBeanList);

		Page<TaskListDto> page = new PageImpl<>(taskListDtoList, pageable, options.getCount());
		return page;
	}

	/**
	 * 期限を過ぎたタスクリストを取得します（ページング）
	 * 
	 * @return
	 */
	public Page<TaskListDto> getOverdueTaskList(OverdueTaskListSearchForm overdueTaskListSearchForm) {
		// ページャー
		Pageable pageable = overdueTaskListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectOverdueTaskByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now(), options);

		// タスクSEQに関するコメント数、作業者を取得
		taskCommonService.setTaskCommentCountWorker(taskBeanList);

		List<TaskListDto> taskListDtoList = taskCommonService.convertBeanListForDtoList(taskBeanList);

		Page<TaskListDto> page = new PageImpl<>(taskListDtoList, pageable, options.getCount());
		return page;
	}

	/**
	 * 完了したタスクリストを取得します（ページング）<br>
	 * 検索条件フォームのsortKeyCdで並び順を指定します
	 * 
	 * @param closeTaskListSearchForm
	 * @return
	 */
	public Page<TaskListDto> getAllCloseTaskList(CloseTaskListSearchForm closeTaskListSearchForm) {
		// ページャー
		Pageable pageable = closeTaskListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectCloseTaskByAccountId(SessionUtils.getLoginAccountSeq(), closeTaskListSearchForm.getSortKeyCd(), options);

		// タスクSEQに関するコメント数、作業者を取得
		taskCommonService.setTaskCommentCountWorker(taskBeanList);

		List<TaskListDto> taskListDtoList = taskCommonService.convertBeanListForDtoList(taskBeanList);

		Page<TaskListDto> page = new PageImpl<>(taskListDtoList, pageable, options.getCount());
		return page;
	}

	/**
	 * 検索ワードにタイトル、本文、サブタスク、案件名、顧客名、担当者、コメントが部分一致するタスクリストを取得します（ページング）<br>
	 * 
	 * 
	 * @param searchResultsTaskListSearchForm
	 * @return
	 */
	public Page<TaskListDto> getSearchResultsTaskList(SearchResultsTaskListSearchForm searchResultsTaskListSearchForm) {
		// ページャー
		Pageable pageable = searchResultsTaskListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectSearchResultsTaskByAccountIdAndSearchWord(SessionUtils.getLoginAccountSeq(), searchResultsTaskListSearchForm.getSearchWord(), options);

		// タスクSEQに関するコメント数、作業者を取得
		taskCommonService.setTaskCommentCountWorker(taskBeanList);

		List<TaskListDto> taskListDtoList = taskCommonService.convertBeanListForDtoList(taskBeanList);

		Page<TaskListDto> page = new PageImpl<>(taskListDtoList, pageable, options.getCount());
		return page;
	}

	/**
	 * 案件タスクリストを取得します（ページング）<br>
	 * 検索条件フォームのsortKeyCdで並び順を指定します
	 * 
	 * @param taskAnkenListSearchForm
	 * @return
	 */
	public Page<TaskListDto> getTaskAnkenList(TaskAnkenListSearchForm taskAnkenListSearchForm) {
		// ページャー
		Pageable pageable = taskAnkenListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectTaskAnkenByAnkenId(taskAnkenListSearchForm.getAnkenId(), taskAnkenListSearchForm.getDispKeyCd(), taskAnkenListSearchForm.getSortKeyCd(), options);

		// タスクSEQに関するコメント数、作業者を取得
		taskCommonService.setTaskCommentCountWorker(taskBeanList);

		List<TaskListDto> taskListDtoList = taskCommonService.convertBeanListForDtoList(taskBeanList);

		Page<TaskListDto> page = new PageImpl<>(taskListDtoList, pageable, options.getCount());
		return page;
	}

	/**
	 * 対象タスクの一覧の1行分のデータを取得する
	 * 
	 * @param taskSeq
	 * @return
	 */
	public TaskListDto getTaskListRowData(Long taskSeq) {

		TaskListBean taskBean = tTaskDao.selectBeanBySeq(SessionUtils.getLoginAccountSeq(), taskSeq);
		if (taskBean == null) {
			// taskSeqのデータが存在しない場合
			return null;
		}

		TaskListDto dto = taskCommonService.convertBeanForDto(taskBean);
		dto.setTaskWorkerSeq(taskBean.getTaskWorkerSeq());

		// アカウント情報の取得
		List<MAccountEntity> accountList = mAccountDao.selectAll();

		// 担当者
		dto.setTaskWorkerSeq(taskBean.getTaskWorkerSeq());
		if (taskBean.getWorkerAccountSeq() != null) {
			// 文字列結合されたSEQをList型に変換
			List<Long> accountSeqList = StringUtils.toLongArray(taskBean.getWorkerAccountSeq());
			dto.setWorkerAccountList(taskCommonService.setWorker(accountList, accountSeqList));
			dto.setAssignTaskTantoList(taskCommonService.createTantoListWithAccountSeqList(accountSeqList, accountList));
		}

		return dto;
	}

	/**
	 * 案件タスクを固定します。
	 * 
	 * @param ankenId
	 * @param loginAccountSeq
	 * @throws AppException
	 */
	public void fixTaskAnken(Long ankenId, Long loginAccountSeq) throws AppException {

		// 固定前のタスク案件情報を取得
		List<TTaskAnkenEntity> taskAnken = tTaskAnkenDao.selectTaskAnkenByAccountSeqAnkenId(loginAccountSeq, ankenId);
		if (LoiozCollectionUtils.isEmpty(taskAnken)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 案件タスク情報に表示順を設定
		TTaskAnkenEntity entity = taskAnken.get(0);
		entity.setDispOrder(Long.valueOf(tTaskAnkenDao.selectMaxDispOrderByAccountSeq(loginAccountSeq) + 1));

		tTaskAnkenDao.update(entity);
	}

	/**
	 * 案件タスクの固定を解除します。
	 * 
	 * @param ankenId
	 * @param loginAccountSeq
	 * @throws AppException
	 */
	public void unFixTaskAnken(Long ankenId, Long loginAccountSeq) throws AppException {

		// 対象のタスク案件情報を取得
		List<TTaskAnkenEntity> taskAnken = tTaskAnkenDao.selectTaskAnkenByAccountSeqAnkenId(loginAccountSeq, ankenId);
		if (LoiozCollectionUtils.isEmpty(taskAnken)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 案件タスク情報に表示順を設定
		TTaskAnkenEntity entity = taskAnken.get(0);
		entity.setDispOrder(null);

		tTaskAnkenDao.update(entity);
	}

	/**
	 * 案件タスクを解除します。
	 * 
	 * @param ankenId
	 * @param loginAccountSeq
	 * @throws AppException
	 */
	public void cancelTaskAnken(Long ankenId, Long loginAccountSeq) throws AppException {

		// 解除前のタスク案件情報を取得
		List<TTaskAnkenEntity> taskAnken = tTaskAnkenDao.selectTaskAnkenByAccountSeqAnkenId(loginAccountSeq, ankenId);
		if (LoiozCollectionUtils.isEmpty(taskAnken)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 案件タスク情報の削除
		tTaskAnkenDao.delete(taskAnken);
	}

	/**
	 * 前ページ最終行タスク1行分のデータを取得する<br>
	 * 
	 * @param selectedTabClass 選択中メニュー
	 * @param dispPageTaskSeqList 表示しているページのタスクSEQリスト
	 * @param taskListSearchForm
	 * @return
	 */
	public TaskListDto getTaskRowOnPrevPage(String selectedTabClass, List<Long> dispPageTaskSeqList, TaskListSearchForm taskListSearchForm) throws AppException {

		// 表示してるメニューの前ページ最終行タスクSEQを取得する
		Long prevTaskSeq = -1L;
		List<Long> taskSeqList = List.of();
		int page = 0;
		if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
			// 今日のタスク画面
			taskSeqList = tTaskDao.selectTodayTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());
			page = taskListSearchForm.getTodayTaskListSearchForm().getPage();
		} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
			// すべてのタスク画面
			taskSeqList = tTaskDao.selectAllTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getAllTaskListSearchForm().getSortKeyCd());
			page = taskListSearchForm.getAllTaskListSearchForm().getPage();
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(selectedTabClass)) {
			// 期限を過ぎたタスク画面
			taskSeqList = tTaskDao.selectOverdueTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());
			page = taskListSearchForm.getOverdueTaskListSearchForm().getPage().intValue();
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(selectedTabClass)) {
			// 完了したタスク画面
			taskSeqList = tTaskDao.selectCloseTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getCloseTaskListSearchForm().getSortKeyCd());
			page = taskListSearchForm.getCloseTaskListSearchForm().getPage().intValue();
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(selectedTabClass)) {
			// タスク検索画面
			taskSeqList = tTaskDao.selectSearchResultsTaskSeqByAccountIdAndSearchWord(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getSearchResultsTaskListSearchForm().getSearchWord());
			page = taskListSearchForm.getSearchResultsTaskListSearchForm().getPage().intValue();
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
			// 案件タスク画面
			TaskAnkenListSearchForm searchForm = taskListSearchForm.getTaskAnkenListSearchForm();
			taskSeqList = tTaskDao.selectTaskAnkenTaskSeqByAnkenId(searchForm.getAnkenId(), searchForm.getDispKeyCd(), searchForm.getSortKeyCd());
			page = searchForm.getPage().intValue();
		}

		// ページが0の場合、前ページが無いので処理終了
		if (page == PagerForm.DEFAULT_PAGE) {
			return null;
		}

		// 表示ページのタスクSEQが無いので処理終了
		if (LoiozCollectionUtils.isEmpty(dispPageTaskSeqList)) {
			return null;
		}

		// 前ページの最終行index
		for (Long taskSeq : dispPageTaskSeqList) {
			if (taskSeqList.contains(taskSeq)) {
				// 前行のタスクSEQを取得
				int prevTaskSeqIndex = taskSeqList.indexOf(taskSeq) - 1;
				if (prevTaskSeqIndex >= 0 && prevTaskSeqIndex < taskSeqList.size()) {
					prevTaskSeq = taskSeqList.get(prevTaskSeqIndex);
					// 現在表示しているタスクと違うSEQならそれを表示。同じなら別のタスクSEQにする。
					if (!dispPageTaskSeqList.contains(prevTaskSeq)) {
						break;
					} else {
						prevTaskSeq = -1L;
					}
				}
			}
		}

		if (prevTaskSeq < 0) {
			// 前タスクSEQが取得できないため処理終了
			return null;
		}

		TaskListBean taskBean = tTaskDao.selectBeanBySeq(SessionUtils.getLoginAccountSeq(), prevTaskSeq);
		if (taskBean == null) {
			// taskSeqのデータが存在しない場合
			return null;
		}

		TaskListDto dto = taskCommonService.convertBeanForDto(taskBean);
		dto.setTaskWorkerSeq(taskBean.getTaskWorkerSeq());

		// アカウント情報の取得
		List<MAccountEntity> accountList = mAccountDao.selectAll();

		// 担当者
		dto.setTaskWorkerSeq(taskBean.getTaskWorkerSeq());
		if (taskBean.getWorkerAccountSeq() != null) {
			// 文字列結合されたSEQをList型に変換
			List<Long> accountSeqList = StringUtils.toLongArray(taskBean.getWorkerAccountSeq());
			dto.setWorkerAccountList(taskCommonService.setWorker(accountList, accountSeqList));
			dto.setAssignTaskTantoList(taskCommonService.createTantoListWithAccountSeqList(accountSeqList, accountList));
		}

		return dto;
	}

	/**
	 * 次ページ先頭行のタスクデータを取得する<br>
	 * 
	 * @param selectedTabClass 選択中メニュー
	 * @param dispPageTaskSeqList 表示しているページのタスクSEQリスト
	 * @param taskListSearchForm
	 * @return
	 */
	public TaskListDto getTaskRowOnNextPage(String selectedTabClass, List<Long> dispPageTaskSeqList, TaskListSearchForm taskListSearchForm) {

		// 表示してるメニューの次ページ先頭行タスクSEQを取得する
		Long nextTaskSeq = -1L;
		List<Long> taskSeqList = List.of();
		if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectTodayTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());
		} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectAllTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getAllTaskListSearchForm().getSortKeyCd());
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectOverdueTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectCloseTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getCloseTaskListSearchForm().getSortKeyCd());
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectSearchResultsTaskSeqByAccountIdAndSearchWord(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getSearchResultsTaskListSearchForm().getSearchWord());
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
			TaskAnkenListSearchForm searchForm = taskListSearchForm.getTaskAnkenListSearchForm();
			taskSeqList = tTaskDao.selectTaskAnkenTaskSeqByAnkenId(searchForm.getAnkenId(), searchForm.getDispKeyCd(), searchForm.getSortKeyCd());
		}

		// 表示ページのタスクSEQが無いので処理終了
		if (LoiozCollectionUtils.isEmpty(dispPageTaskSeqList)) {
			return null;
		}

		// 次ページの先頭行index
		Collections.reverse(dispPageTaskSeqList);
		for (Long taskSeq : dispPageTaskSeqList) {
			if (taskSeqList.contains(taskSeq)) {
				// 後行のタスクSEQを取得
				int nextTaskSeqIndex = taskSeqList.indexOf(taskSeq) + 1;
				if (nextTaskSeqIndex >= 0 && nextTaskSeqIndex < taskSeqList.size()) {
					nextTaskSeq = taskSeqList.get(nextTaskSeqIndex);
					// 現在表示しているタスクと違うSEQならそれを表示。同じなら別のタスクSEQにする。
					if (!dispPageTaskSeqList.contains(nextTaskSeq)) {
						break;
					} else {
						nextTaskSeq = -1L;
					}
				}
			}
		}

		if (nextTaskSeq < 0) {
			// 次タスクSEQが取得できないため処理終了
			return null;
		}

		TaskListBean taskBean = tTaskDao.selectBeanBySeq(SessionUtils.getLoginAccountSeq(), nextTaskSeq);
		if (taskBean == null) {
			// taskSeqのデータが存在しない場合
			return null;
		}

		TaskListDto dto = taskCommonService.convertBeanForDto(taskBean);
		dto.setTaskWorkerSeq(taskBean.getTaskWorkerSeq());

		// アカウント情報の取得
		List<MAccountEntity> accountList = mAccountDao.selectAll();

		// 担当者
		dto.setTaskWorkerSeq(taskBean.getTaskWorkerSeq());
		if (taskBean.getWorkerAccountSeq() != null) {
			// 文字列結合されたSEQをList型に変換
			List<Long> accountSeqList = StringUtils.toLongArray(taskBean.getWorkerAccountSeq());
			dto.setWorkerAccountList(taskCommonService.setWorker(accountList, accountSeqList));
			dto.setAssignTaskTantoList(taskCommonService.createTantoListWithAccountSeqList(accountSeqList, accountList));
		}

		return dto;
	}

	/**
	 * タスク一覧の表示用Formを作成する
	 *
	 * @return viewForm
	 */
	public TaskListViewForm createViewForm() {

		// 表示用フォームの作成
		TaskListViewForm viewForm = new TaskListViewForm();
		return viewForm;
	}

	/**
	 * タスク一覧の入力用Formを作成する
	 *
	 * @return inputForm
	 */
	public TaskListInputForm createInputForm() {

		// 入力用フォームの作成
		TaskListInputForm inputForm = new TaskListInputForm();
		return inputForm;
	}

	/**
	 * 今日のタスク情報リストを加工しFormにセットします
	 *
	 * @param inputForm
	 * @param todayTaskListSearchForm
	 */
	public void setTodayTaskBeanList(TaskListInputForm inputForm, TodayTaskListSearchForm todayTaskListSearchForm) {

		List<TaskListForTaskManagementDto> taskList = new ArrayList<TaskListForTaskManagementDto>();

		// タスク一覧情報を取得する
		Page<TaskListDto> page = getTodayTaskList(todayTaskListSearchForm);
		TaskListForTaskManagementDto preparingTaskList = new TaskListForTaskManagementDto();
		preparingTaskList.setTaskTypeTitle(CommonConstant.TASK_STATUS_INCOMPLETE_NAME);
		preparingTaskList.setPage(page);
		preparingTaskList.setTaskList(page.getContent());
		taskList.add(preparingTaskList);

		// Dto情報をFormにセット
		inputForm.setTaskListForTaskManagement(taskList);
	}

	/**
	 * 期限付きのタスク情報リストを加工しFormにセットします
	 *
	 * @param inputForm
	 */
	public void setFutureTaskBeanList(TaskListInputForm inputForm) {

		List<TaskListForTaskManagementDto> taskList = new ArrayList<TaskListForTaskManagementDto>();

		// 期限付きのタスクリスト情報をDto型に加工
		List<TaskListDto> taskDtoList = taskCommonService.convertBeanListForDtoList(taskCommonService.getFutureTaskList());

		// 期限日ごとにセット
		// 今日
		TaskListForTaskManagementDto todayDeadlineTaskList = new TaskListForTaskManagementDto();
		todayDeadlineTaskList.setTaskTypeTitle(CommonConstant.FUTURE_TASK_TODAY);
		LocalDate todayCategoryDate = LocalDate.now();
		todayDeadlineTaskList.setLimitDateCategoryDate(todayCategoryDate);
		todayDeadlineTaskList.setTaskList(taskDtoList.stream()
				.filter(dto -> dto.getLimitDateForDisplay().isEqual(todayCategoryDate))
				.collect(Collectors.toList()));
		taskList.add(todayDeadlineTaskList);

		// 明日
		TaskListForTaskManagementDto tomorrowDeadlineTaskList = new TaskListForTaskManagementDto();
		tomorrowDeadlineTaskList.setTaskTypeTitle(CommonConstant.FUTURE_TASK_TOMORROW);
		LocalDate tomorrowCategoryDate = LocalDate.now().plusDays(1);
		tomorrowDeadlineTaskList.setLimitDateCategoryDate(tomorrowCategoryDate);
		tomorrowDeadlineTaskList.setTaskList(taskDtoList.stream()
				.filter(dto -> dto.getLimitDateForDisplay().isEqual(tomorrowCategoryDate))
				.collect(Collectors.toList()));
		taskList.add(tomorrowDeadlineTaskList);

		// 明後日
		TaskListForTaskManagementDto dayAfterTomorrowDeadlineTaskList = new TaskListForTaskManagementDto();
		dayAfterTomorrowDeadlineTaskList.setTaskTypeTitle(CommonConstant.FUTURE_TASK_DAY_AFTER_TOMORROW);
		LocalDate dayAfterTomorrowCategoryDate = LocalDate.now().plusDays(2);
		dayAfterTomorrowDeadlineTaskList.setLimitDateCategoryDate(dayAfterTomorrowCategoryDate);
		dayAfterTomorrowDeadlineTaskList.setTaskList(taskDtoList.stream()
				.filter(dto -> dto.getLimitDateForDisplay().isEqual(dayAfterTomorrowCategoryDate))
				.collect(Collectors.toList()));
		taskList.add(dayAfterTomorrowDeadlineTaskList);

		// 以降
		TaskListForTaskManagementDto subsequentDeadlineTaskList = new TaskListForTaskManagementDto();
		subsequentDeadlineTaskList.setTaskTypeTitle(CommonConstant.FUTURE_TASK_SUB_SEQUENT);
		// 以降のデータは、カテゴリーの基準日は設定しない
		subsequentDeadlineTaskList.setTaskList(taskDtoList.stream()
				.filter(dto -> dto.getLimitDateForDisplay().isAfter(LocalDate.now().plusDays(2)))
				.collect(Collectors.toList()));
		taskList.add(subsequentDeadlineTaskList);

		// 取得した情報をセット
		inputForm.setTaskListForTaskManagement(taskList);
	}

	/**
	 * すべてのタスク情報リストを加工しFormにセットします。<br>
	 * 並び順を検索条件フォームのsortKeyCdで指定します
	 *
	 * @param inputForm
	 * @param allTaskListSearchForm
	 */
	public void setAllTaskBeanList(TaskListInputForm inputForm, AllTaskListSearchForm allTaskListSearchForm) {

		List<TaskListForTaskManagementDto> taskList = new ArrayList<TaskListForTaskManagementDto>();

		// すべてのタスクリスト情報をDto型に加工
		Page<TaskListDto> page = getAllTaskList(allTaskListSearchForm);
		TaskListForTaskManagementDto preparingTaskList = new TaskListForTaskManagementDto();
		preparingTaskList.setTaskTypeTitle(CommonConstant.TASK_STATUS_INCOMPLETE_NAME);
		preparingTaskList.setPage(page);
		preparingTaskList.setTaskList(page.getContent());
		taskList.add(preparingTaskList);

		// 取得した情報をFormにセットします
		inputForm.setTaskListForTaskManagement(taskList);
		inputForm.setTaskListSortKeyCd(allTaskListSearchForm.getSortKeyCd());
	}

	/**
	 * 完了したタスク情報リストを加工しFormにセットします。<br>
	 * 並び順を検索条件フォームのsortKeyCdで指定します
	 *
	 * @param inputForm
	 * @param closeTaskListSearchForm
	 */
	public void setCloseTaskBeanList(TaskListInputForm inputForm, CloseTaskListSearchForm closeTaskListSearchForm) {

		List<TaskListForTaskManagementDto> taskList = new ArrayList<TaskListForTaskManagementDto>();

		// すべてのタスクリスト情報をDto型に加工
		Page<TaskListDto> page = getAllCloseTaskList(closeTaskListSearchForm);

		// 完了
		TaskListForTaskManagementDto completedTaskList = new TaskListForTaskManagementDto();
		completedTaskList.setTaskTypeTitle(TaskStatus.COMPLETED.getVal());
		completedTaskList.setTaskStatusCd(TaskStatus.COMPLETED.getCd());
		completedTaskList.setPage(page);
		completedTaskList.setTaskList(page.getContent());
		taskList.add(completedTaskList);

		// 取得した情報をセットします
		inputForm.setTaskListForTaskManagement(taskList);
		inputForm.setTaskListSortKeyCd(closeTaskListSearchForm.getSortKeyCd());
	}

	/**
	 * 検索ワードに該当するタスク情報リストを加工しFormにセットします。<br>
	 * 
	 * @param inputForm
	 * @param searchResultsTaskListSearchForm
	 */
	public void setSearchResultsTaskBeanList(TaskListInputForm inputForm, SearchResultsTaskListSearchForm searchResultsTaskListSearchForm) {

		List<TaskListForTaskManagementDto> taskList = new ArrayList<TaskListForTaskManagementDto>();

		// すべてのタスクリスト情報をDto型に加工
		Page<TaskListDto> page = getSearchResultsTaskList(searchResultsTaskListSearchForm);

		// 検索結果タスク
		TaskListForTaskManagementDto searchResultsTaskList = new TaskListForTaskManagementDto();
		searchResultsTaskList.setTaskTypeTitle(TaskStatus.COMPLETED.getVal());
		searchResultsTaskList.setPage(page);
		searchResultsTaskList.setTaskList(page.getContent());
		taskList.add(searchResultsTaskList);

		// 取得した情報をセットします
		inputForm.setTaskListForTaskManagement(taskList);
	}

	/**
	 * 期限を過ぎたタスク情報リストを加工しFormにセットします
	 *
	 * @param inputForm
	 */
	public void setOverdueTaskBeanList(TaskListInputForm inputForm, OverdueTaskListSearchForm overdueTaskListSearchForm) {

		List<TaskListForTaskManagementDto> taskList = new ArrayList<TaskListForTaskManagementDto>();

		// タスク一覧情報を取得する
		Page<TaskListDto> page = getOverdueTaskList(overdueTaskListSearchForm);
		TaskListForTaskManagementDto overdueTaskList = new TaskListForTaskManagementDto();
		overdueTaskList.setTaskTypeTitle(CommonConstant.TaskMenu.OVERDUE_TASK.getVal());
		overdueTaskList.setPage(page);
		overdueTaskList.setTaskList(page.getContent());
		taskList.add(overdueTaskList);

		// 取得した情報をセット
		inputForm.setTaskListForTaskManagement(taskList);
	}

	/**
	 * 割り当てられたタスク情報リストを加工しFormにセットします
	 *
	 * @param inputForm
	 */
	public void setAssignedTaskBeanList(TaskListInputForm inputForm) {

		// タスク一覧情報を取得する
		List<TaskListDto> taskDtoList = taskCommonService.convertBeanListForDtoList(taskCommonService.getAssignedTaskList());

		List<TaskListForTaskManagementDto> taskList = taskCommonService.organizeAssignedTasks(taskDtoList);

		// 取得した情報をセット
		inputForm.setTaskListForTaskManagement(taskList);
	}

	/**
	 * 割り当てたタスク情報リストを加工しFormにセットします
	 *
	 * @param inputForm
	 */
	public void setAssignTaskBeanList(TaskListInputForm inputForm) {

		// タスク一覧情報を取得する
		List<TaskListDto> taskDtoList = taskCommonService.convertBeanListForDtoList(taskCommonService.getAssignTaskList());

		List<TaskListForTaskManagementDto> taskList = taskCommonService.organizeAssignTasks(taskDtoList);

		// 取得した情報をセット
		inputForm.setTaskListForTaskManagement(taskList);
	}

	/**
	 * 案件タスク情報リストを加工しFormにセットします。<br>
	 * 並び順を検索条件フォームのsortKeyCdで指定します
	 *
	 * @param inputForm
	 * @param taskAnkenListSearchForm
	 */
	public void setTaskAnkenBeanList(TaskListInputForm inputForm, TaskAnkenListSearchForm taskAnkenListSearchForm) {

		List<TaskListForTaskManagementDto> taskListForTaskManagementList = new ArrayList<TaskListForTaskManagementDto>();

		// 案件タスクのタスクリスト情報をDto型に加工
		Page<TaskListDto> page = getTaskAnkenList(taskAnkenListSearchForm);
		TaskListForTaskManagementDto preparingTaskList = new TaskListForTaskManagementDto();
		preparingTaskList.setTaskTypeTitle(CommonConstant.TASK_STATUS_INCOMPLETE_NAME);
		preparingTaskList.setPage(page);
		preparingTaskList.setTaskList(page.getContent());
		taskListForTaskManagementList.add(preparingTaskList);

		// 取得した情報をFormにセットします
		inputForm.setTaskListForTaskManagement(taskListForTaskManagementList);
		inputForm.setTaskListSortKeyCd(taskAnkenListSearchForm.getSortKeyCd());
		inputForm.setTaskAnkenListDispKeyCd(taskAnkenListSearchForm.getDispKeyCd());
	}

	/**
	 * タスク詳細情報フォームを取得する
	 * 
	 * @param taskSeq
	 * @param isUpdateConfirmFlg タスクを確認済みにするか（新着フラグ、履歴確認フラグの更新）
	 * @return
	 */
	public TaskListInputForm.TaskDetailInputForm createTaskDetailInputForm(Long taskSeq, boolean isUpdateConfirmFlg) throws AppException {

		TaskListInputForm.TaskDetailInputForm inputForm = new TaskListInputForm.TaskDetailInputForm();

		// アカウント取得すべて
		List<MAccountEntity> accountListAll = mAccountDao.selectAll();

		// タスク詳細情報を取得
		TaskListBean taskBean = tTaskDao.selectBeanBySeq(SessionUtils.getLoginAccountSeq(), taskSeq);
		if (taskBean == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 取得したタスク詳細をDto型に加工しフォームにセット
		inputForm.setTaskDetail(taskCommonService.convertBeanForDto(accountListAll, taskBean));

		// アカウント名のMapを抽出
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap(accountListAll);

		// タスク履歴情報を取得
		List<TTaskHistoryEntity> taskAllHistoryList = tTaskHistoryDao.selectByTaskSeqList(List.of(taskBean.getTaskSeq()));
		// DTOに変換
		List<TaskHistoryDto> taskAllHistoryDtoList = taskCommonService.convertTaskHistoryDtoForTaskHistoryEntity(taskAllHistoryList, accountNameMap);

		// すべて履歴からコメントを抽出
		List<TaskHistoryDto> commentDtoList = taskAllHistoryDtoList.stream()
				.filter(dto -> CommonConstant.TaskHistoryType.COMMENT.equalsByCode(dto.getTaskHistoryType().getCd())).collect(Collectors.toList());
		inputForm.setNumberOfComment(commentDtoList.size());
		// コメントを設定
		inputForm.setCommentList(commentDtoList);

		// アクティビティ履歴を抽出（コメント以外）
		List<TaskHistoryDto> activityDtoList = taskAllHistoryDtoList.stream()
				.filter(dto -> !CommonConstant.TaskHistoryType.COMMENT.equalsByCode(dto.getTaskHistoryType().getCd())).collect(Collectors.toList());
		List<ActivityHistoryDto> activityDisplayList = taskCommonService.convertTaskHistoryDtoForAcitivityHistoryDto(activityDtoList);
		// アクティビティを設定
		inputForm.setActivityList(activityDisplayList);

		// タスク担当者を取得
		List<TTaskWorkerEntity> tTaskWorkerEntityList = tTaskWorkerDao.selectByParentSeq(taskSeq);

		// 担当者を取得
		List<TaskTanto> tantoList = this.createTantoListWithWorkerEntityList(tTaskWorkerEntityList, accountListAll);
		inputForm.setTaskTantoList(tantoList);
		List<Long> tantoSeqList = tTaskWorkerEntityList.stream().filter(entity -> SystemFlg.FLG_OFF.equalsByCode(entity.getEntrustFlg())).map(entity -> entity.getWorkerAccountSeq()).collect(Collectors.toList());

		// ログインユーザーが担当OR作成者フラグをセット
		TTaskWorkerEntity tTaskWorkerEntity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(taskSeq, SessionUtils.getLoginAccountSeq());
		if (tTaskWorkerEntity != null) {
			inputForm.setTaskTanto(true);
		} else {
			inputForm.setTaskTanto(false);
		}

		// 有効なアカウントのみ抽出
		List<MAccountEntity> enabledAccountEntityList = commonAccountService.extractEnabledAccountList(accountListAll);

		// 担当者の中に無効なアカウントがいる場合は、有効なアカウントの中に追加する。
		List<Long> enableAccountList = enabledAccountEntityList.stream().map(entity -> entity.getAccountSeq()).collect(Collectors.toList());
		for (Long seq : tantoSeqList) {
			if (!enableAccountList.contains(seq)) {
				MAccountEntity addAcountEntity = mAccountDao.selectBySeq(seq);
				enabledAccountEntityList.add(addAcountEntity);
			}
		}

		// タスク担当セレクトリスト
		List<SelectOptionForm> sof = new ArrayList<>();
		enabledAccountEntityList.forEach(entity -> sof.add(new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName())));
		inputForm.setTantoOptions(sof);

		// サブタスク取得
		List<TTaskCheckItemEntity> tTaskCheckItemEntityList = tTaskCheckItemDao.selectByTaskSeq(taskSeq);
		List<TaskCheckItemDto> taskCheckItemDtoList = taskCommonService.convertTaskCheckItemDtoForTaskCheckItemEntity(tTaskCheckItemEntityList);
		inputForm.getTaskDetail().setCheckList(taskCheckItemDtoList);

		if (isUpdateConfirmFlg) {
			// 新着タスク確認フラグ、新着履歴確認フラグ更新
			TTaskWorkerEntity updateEntity = null;
			for (TTaskWorkerEntity entity : tTaskWorkerEntityList) {
				// ログインユーザーが担当者に割り当てられているか確認
				if (SessionUtils.getLoginAccountSeq().equals(entity.getWorkerAccountSeq())) {
					// 担当者に設定されているため更新対象
					updateEntity = entity;
				}
			}

			// ログインユーザーのタスク担当者情報を更新するか判定
			if (updateEntity != null) {

				// タスクを確認済みに設定
				updateEntity.setNewTaskKakuninFlg(SystemFlg.FLG_ON.getCd());
				updateEntity.setNewHistoryKakuninFlg(SystemFlg.FLG_ON.getCd());

				try {
					// 更新処理
					tTaskWorkerDao.update(updateEntity);

				} catch (OptimisticLockingFailureException e) {
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + e.getMessage());
					throw new AppException(MessageEnum.MSG_E00013, e);
				}

			}
		}

		return inputForm;
	}

	/**
	 * taskSeqのタスクコメント数を取得する。
	 * 
	 * @param taskSeq
	 * @return
	 */
	public long getNumberOfComment(Long taskSeq) {

		// タスク履歴情報を取得し種類ごとにフォームにセット
		List<TTaskHistoryEntity> taskAllHistoryList = tTaskHistoryDao.selectByTaskSeqList(List.of(taskSeq));

		// コメント件数を返却
		return taskAllHistoryList.stream().filter(entity -> CommonConstant.TaskHistoryType.COMMENT.equalsByCode(entity.getTaskHistoryType())).count();
	}

	/**
	 * タスクの期限更新処理
	 * 
	 * @param taskSeq
	 * @param limitDate
	 * @throws AppException
	 */
	public void updateTaskLimitDate(Long taskSeq, String limitDate) throws AppException {

		// 更新前のタスク情報を取得
		TaskEditInputForm inputForm = new TaskEditInputForm();
		taskEditService.setEditData(inputForm, taskSeq, SessionUtils.getLoginAccountSeq());

		// 更新する値をFormにセット
		if (StringUtils.isEmpty(limitDate)) {
			inputForm.setLimitDate(null);
		} else {
			inputForm.setLimitDate(limitDate);
		}

		// タスク更新
		taskEditService.updateTask(inputForm);
	}

	/**
	 * タスクの新規登録処理
	 * 
	 * @param taskTitle タスクタイトル
	 * @param isTodayTaskFlg 今日やるタスクかどうか
	 * @param isDeadlineToday 期限が今日かどうか
	 * @param selectedTaskAnkenId 紐づける案件ID
	 * @throws AppException
	 */
	public Long insertTask(String taskTitle, boolean isTodayTaskFlg, boolean isDeadlineToday, Long selectedTaskAnkenId) throws AppException {

		// タスク情報作成
		TaskEditInputForm inputForm = new TaskEditInputForm();
		inputForm.setTitle(taskTitle);
		inputForm.setWorkerAccountSeq(List.of());
		inputForm.setTodayTaskFlg(isTodayTaskFlg);
		// サイドメニュー案件タスクを選択中の場合、対象案件のタスクとして登録する
		if (selectedTaskAnkenId != null) {
			inputForm.setAnkenId(AnkenId.of(selectedTaskAnkenId));
		}

		// ステータスは未着手をセット
		inputForm.setTaskStatus(CommonConstant.TaskStatus.PREPARING.getCd());

		// 期限が今日の場合は期限日をセット
		if (isDeadlineToday) {
			inputForm.setLimitDateByLocalDate(LocalDate.now());
		}

		// タスク更新
		return taskEditService.insertTask(inputForm);
	}

	/**
	 * タスクタイトルの変更処理
	 * 
	 * @param taskSeq
	 * @param taskTitle
	 * @throws AppException
	 */
	public void changeTaskTitle(Long taskSeq, String taskTitle) throws AppException {

		// 更新前のタスク情報作成
		TaskEditInputForm inputForm = new TaskEditInputForm();
		taskEditService.setEditData(inputForm, taskSeq, SessionUtils.getLoginAccountSeq());

		// 更新する値をFormにセット
		inputForm.setTitle(taskTitle);

		// タスク更新
		taskEditService.updateTask(inputForm);
	}

	/**
	 * タスクメモ（詳細）の保存処理
	 * 
	 * @param taskSeq
	 * @param taskContent
	 * @throws AppException
	 */
	public void saveContent(Long taskSeq, String taskContent) throws AppException {

		// 更新前のタスク情報を取得
		TaskEditInputForm inputForm = new TaskEditInputForm();
		taskEditService.setEditData(inputForm, taskSeq, SessionUtils.getLoginAccountSeq());

		// 更新する値をFormにセット
		inputForm.setContent(taskContent);

		// タスク更新
		taskEditService.updateTask(inputForm);
	}

	/**
	 * コメントの更新処理
	 *
	 * @param commentForm
	 * @throws AppException
	 */
	public void updateComment(CommentForm commentForm) throws AppException {

		TTaskHistoryEntity updateEntity = tTaskHistoryDao.selectBySeq(commentForm.getTaskHistorySeq());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 変更内容をセット
		updateEntity.setComment(commentForm.getComment());

		try {
			// コメントの更新処理
			tTaskHistoryDao.update(updateEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// 更新したタスク-履歴のSEQをコメントFormにセットする
		commentForm.setTaskHistorySeq(updateEntity.getTaskHistorySeq());

		// コメント確認フラグの更新処理
		historyKauninFlgUpdateToFlgOff(commentForm.getTaskSeq());
	}

	/**
	 * コメントの削除処理
	 *
	 * @param commentForm
	 * @throws AppException
	 */
	public void deleteComment(CommentForm commentForm) throws AppException {

		TTaskHistoryEntity deleteEntity = tTaskHistoryDao.selectBySeq(commentForm.getTaskHistorySeq());
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 削除処理
			tTaskHistoryDao.delete(deleteEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// 削除の場合は、履歴確認フラグの更新を行わない仕様
	}

	/**
	 * タスクの割り当てを変更する処理
	 * 
	 * @param taskSeq
	 * @param workerAccountSeq
	 * @throws AppException
	 */
	public void changeTaskTanto(Long taskSeq, List<Long> workerAccountSeq) throws AppException {

		// 更新前のタスク情報を取得
		TaskEditInputForm inputForm = new TaskEditInputForm();
		taskEditService.setEditData(inputForm, taskSeq, SessionUtils.getLoginAccountSeq());

		// 更新する値をFormにセット
		inputForm.setWorkerAccountSeq(workerAccountSeq);

		// タスク更新
		taskEditService.updateTask(inputForm);
	}

	/**
	 * タスクに案件を設定する
	 * 
	 * @param taskSeq
	 * @param ankenId
	 * @throws AppException
	 */
	public void addTaskAnken(Long taskSeq, Long ankenId) throws AppException {

		// 更新前のタスク情報を取得
		TaskEditInputForm inputForm = new TaskEditInputForm();
		taskEditService.setEditData(inputForm, taskSeq, SessionUtils.getLoginAccountSeq());

		// 更新する値をFormにセット
		inputForm.setAnkenId(AnkenId.of(ankenId));

		// タスク更新
		taskEditService.updateTask(inputForm);
	}

	/**
	 * タスクに設定してある案件を外す
	 * 
	 * @param taskSeq
	 * @throws AppException
	 */
	public void deleteTaskAnken(Long taskSeq) throws AppException {

		// 更新前のタスク情報を取得
		TaskEditInputForm inputForm = new TaskEditInputForm();
		taskEditService.setEditData(inputForm, taskSeq, SessionUtils.getLoginAccountSeq());

		// 案件を空にする
		inputForm.setAnkenId(null);

		// タスク更新
		taskEditService.updateTask(inputForm);
	}

	/**
	 * 今日やるタスクへ変更する
	 * 
	 * @param taskWorkerSeq
	 * @throws AppException
	 */
	public void updateToTaskToday(Long taskWorkerSeq) throws AppException {

		// 更新前のタスク作業者情報を取得
		TTaskWorkerEntity tTaskWorkerEntity = tTaskWorkerDao.selectBySeq(taskWorkerSeq);
		if (tTaskWorkerEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新する値をセット
		tTaskWorkerEntity.setTodayTaskDate(LocalDate.now());
		tTaskWorkerEntity.setTodayTaskDispOrder(tTaskWorkerDao.selectMaxTodayTaskDispOrderByAccountSeq(SessionUtils.getLoginAccountSeq()) + 1);

		// 更新
		tTaskWorkerDao.update(tTaskWorkerEntity);
	}

	/**
	 * 今日やるタスクから外す
	 * 
	 * @param taskWorkerSeq
	 * @throws AppException
	 */
	public void updateToTaskDoneLater(Long taskWorkerSeq) throws AppException {

		// 更新前のタスク作業者情報を取得
		TTaskWorkerEntity tTaskWorkerEntity = tTaskWorkerDao.selectBySeq(taskWorkerSeq);
		if (tTaskWorkerEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新する値をセット
		tTaskWorkerEntity.setTodayTaskDate(null);
		tTaskWorkerEntity.setTodayTaskDispOrder(null);

		// 更新
		tTaskWorkerDao.update(tTaskWorkerEntity);
	}

	/**
	 * タスク一覧表示順変更処理<br>
	 * 「並び替え後のタスクWorkerSeq」の中で最小の表示順を取得し、並び替え後の順に表示順をインクリメントしながら設定します。
	 * 
	 * @param taskWorkerSeqListStr 並び替え後のタスクWorkerSeq
	 * @param selectedTabClass 選択中メニュー
	 * @throws AppException
	 */
	public void dispOrder(String taskWorkerSeqListStr, String selectedTabClass) throws AppException {
		String[] taskWorkerSeqStringArray = taskWorkerSeqListStr.split(",");

		// 並び替え前の状態でデータを取得
		int minDispOrder;
		if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
			minDispOrder = tTaskWorkerDao.selectMinTodayTaskDispOrderByTaskWorkerSeq(SessionUtils.getLoginAccountSeq(), Arrays.asList(taskWorkerSeqStringArray));
		} else {
			minDispOrder = tTaskWorkerDao.selectMinDispOrderByTaskWorkerSeq(SessionUtils.getLoginAccountSeq(), Arrays.asList(taskWorkerSeqStringArray));
		}

		// 変更するentityを取得
		long[] taskWorkerSeqLongArray = Stream.of(taskWorkerSeqStringArray).mapToLong(Long::parseLong).toArray();
		ArrayList<Long> taskWorkerSeqList = (ArrayList<Long>) Arrays.stream(taskWorkerSeqLongArray).boxed().collect(Collectors.toList());
		List<TTaskWorkerEntity> tTaskWorkerEntityList = tTaskWorkerDao.selectBySeqList(taskWorkerSeqList);

		// タスク一覧のデータを並び替え後の状態で逆から順次表示順をセットしていく
		for (int i = (taskWorkerSeqStringArray.length - 1); i >= 0; i--) {

			// workerSeqの取得
			Long taskWorkerSeq = Long.parseLong(taskWorkerSeqStringArray[i]);

			// 変更するentityを取得
			TTaskWorkerEntity tTaskWorkerEntity;
			try {
				tTaskWorkerEntity = tTaskWorkerEntityList.stream().filter(entity -> entity.getTaskWorkerSeq().equals(taskWorkerSeq)).findFirst().get();
			} catch (NoSuchElementException e) {
				// 既にデータがなければ排他エラーを呼ぶ
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 表示順を再設定
			if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
				tTaskWorkerEntity.setTodayTaskDispOrder(minDispOrder++);
			} else {
				tTaskWorkerEntity.setDispOrder(minDispOrder++);
			}

			try {
				tTaskWorkerDao.update(tTaskWorkerEntity);
			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
		}
	}

	/**
	 * タスクSEQに紐づくチェックアイテムが登録上限件数に達しているか確認する<br>
	 * 
	 * @param taskSeq
	 * @return true：上限に達している false:上限に達していない
	 */
	public boolean validateCheckItemLimit(Long taskSeq) {
		List<TTaskCheckItemEntity> tTaskCheckItemEntityList = tTaskCheckItemDao.selectByTaskSeq(taskSeq);
		if (!LoiozCollectionUtils.isEmpty(tTaskCheckItemEntityList)) {
			if (tTaskCheckItemEntityList.size() >= CommonConstant.TASK_CHECK_ITEM_ADD_LIMIT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 選択中の次のタスクSEQを取得する<br>
	 * 無い場合は、先頭のタスクSEQを取得する<br>
	 * 取得できなかった場合は-1を返す
	 * 
	 * @param selectedTaskSeq 選択中タスクSEQ
	 * @param taskListSearchForm
	 * @return
	 */
	public Long getTaskSeq(Long selectedTaskSeq, TaskListSearchForm taskListSearchForm) {

		Long nextTaskSeq = -1L;

		// 選択中メニュー
		String selectedTabClass = taskListSearchForm.getSelectedTaskListMenu();

		// 表示してるメニューのタスクSEQを取得する
		List<Long> taskSeqList = List.of();
		if (TaskMenu.TODAY_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectTodayTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());
		} else if (TaskMenu.ALL_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectAllTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getAllTaskListSearchForm().getSortKeyCd());
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectOverdueTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());
		} else if (TaskMenu.CLOSE_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectCloseTaskSeqByAccountId(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getCloseTaskListSearchForm().getSortKeyCd());
		} else if (TaskMenu.SEARCH_RESULTS_TASK.equalsByCode(selectedTabClass)) {
			taskSeqList = tTaskDao.selectSearchResultsTaskSeqByAccountIdAndSearchWord(SessionUtils.getLoginAccountSeq(), taskListSearchForm.getSearchResultsTaskListSearchForm().getSearchWord());
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(selectedTabClass)) {
			TaskAnkenListSearchForm searchForm = taskListSearchForm.getTaskAnkenListSearchForm();
			taskSeqList = tTaskDao.selectTaskAnkenTaskSeqByAnkenId(searchForm.getAnkenId(), searchForm.getDispKeyCd(), searchForm.getSortKeyCd());
		}

		if (taskSeqList.contains(selectedTaskSeq)) {
			// 次のタスクSEQを取得
			int nextTaskSeqIndex = taskSeqList.indexOf(selectedTaskSeq) + 1;
			if (nextTaskSeqIndex < taskSeqList.size()) {
				nextTaskSeq = taskSeqList.get(nextTaskSeqIndex);
			} else {
				// 次のタスクSEQが無い場合は先頭のタスクSEQを返す
				nextTaskSeq = taskSeqList.get(0);
			}
		} else if (!LoiozCollectionUtils.isEmpty(taskSeqList)) {
			// 先頭のタスクSEQを返す
			nextTaskSeq = taskSeqList.get(0);
		}

		return nextTaskSeq;
	}

	/**
	 * パラメータのアカウントSEQに紐づく案件タスクデータを取得
	 * 
	 * @param accountSeq
	 * @return
	 */
	public List<TaskAnkenDto> getTaskAnkenAddList(Long accountSeq) {

		List<TTaskAnkenEntity> taskAnkenList = tTaskAnkenDao.selectTaskAnkenByAccountSeq(accountSeq);

		// 案件タスクEntityを案件タスクDTOに変換
		List<TaskAnkenDto> taskAnkenDtoList = convertTaskEntity2TaskAnkenDto(taskAnkenList);

		// 案件の先頭１人分の顧客名、顧客数を取得する
		this.setAnkenListPersonName(taskAnkenDtoList);

		// 案件の案件名を取得する
		this.setAnkenName(taskAnkenDtoList);

		// タスク数を取得する
		this.setNumberOfTask(taskAnkenDtoList);

		return taskAnkenDtoList;
	}

	/**
	 * 案件タスクの一覧部分に表示するタイトルの情報を取得します。
	 * 
	 * @param ankenId
	 * @param selectedTabClass
	 * @return
	 */
	public TaskAnkenDto getTaskAnkenDetails(Long ankenId, String selectedTabClass) {

		TaskAnkenDto taskAnkenDto = new TaskAnkenDto();

		// 案件情報取得
		TAnkenEntity entity = tAnkenDao.selectByAnkenId(ankenId);
		taskAnkenDto.setAnkenId(AnkenId.of(ankenId));
		taskAnkenDto.setAnkenName(entity.getAnkenName());
		taskAnkenDto.setAnkenType(AnkenType.of(entity.getAnkenType()));

		// 分野の詳細情報取得
		BunyaDto bunyaDto = commonBunyaService.getBunya(entity.getBunyaId());
		taskAnkenDto.setKeiji(bunyaDto.isKeiji());

		// ------------------------------------------------------------
		// 関与者情報
		// ------------------------------------------------------------
		// 案件IDに紐づく関与者情報の取得
		List<AnkenKanyoshaBean> ankenKanyoshaList = tKanyoshaDao.selectAnkenRelatedKanyoshaByAnkenIdList(ankenId);

		// 相手方
		List<AnkenKanyoshaBean> aitegataListBean = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.AITEGATA.equalsByCode(bean.getKanyoshaType()) &&
						SystemFlg.FLG_OFF.equalsByCode(bean.getDairiFlg()))
				.collect(Collectors.toList());

		List<OpposingParty> opposingPartyList = aitegataListBean.stream()
				.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
				.map(bean -> OpposingParty.builder().kanyoshaSeq(bean.getKanyoshaSeq())
						.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
						.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
						.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
						.lawyerName(this.getLawyerName(ankenKanyoshaList, bean.getRelatedKanyoshaSeq()))
						.build())
				.collect(Collectors.toList());
		// 相手方を設定
		taskAnkenDto.setOpposingPartyList(opposingPartyList);

		// 共犯者
		List<AnkenKanyoshaBean> kyohanshaListBean = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.KYOHANSHA.equalsByCode(bean.getKanyoshaType()) &&
						SystemFlg.FLG_OFF.equalsByCode(bean.getDairiFlg()))
				.collect(Collectors.toList());

		List<Accomplice> accompliceList = kyohanshaListBean.stream()
				.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
				.map(bean -> Accomplice.builder().kanyoshaSeq(bean.getKanyoshaSeq())
						.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
						.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
						.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
						.lawyerName(this.getLawyerName(ankenKanyoshaList, bean.getRelatedKanyoshaSeq()))
						.build())
				.collect(Collectors.toList());
		// 共犯者を設定
		taskAnkenDto.setAccompliceList(accompliceList);

		// 被害者
		List<AnkenKanyoshaBean> higaishaListBean = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.HIGAISHA.equalsByCode(bean.getKanyoshaType()) &&
						SystemFlg.FLG_OFF.equalsByCode(bean.getDairiFlg()))
				.collect(Collectors.toList());

		List<Victim> victimList = higaishaListBean.stream()
				.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
				.map(bean -> Victim.builder().kanyoshaSeq(bean.getKanyoshaSeq())
						.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
						.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
						.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
						.lawyerName(this.getLawyerName(ankenKanyoshaList, bean.getRelatedKanyoshaSeq()))
						.build())
				.collect(Collectors.toList());
		// 被害者を設定
		taskAnkenDto.setVictimList(victimList);

		return taskAnkenDto;
	}

	/**
	 * 案件情報を取得します。
	 * 
	 * @param ankenId
	 * @return
	 */
	public TAnkenEntity getAnken(Long ankenId) {
		return tAnkenDao.selectByAnkenId(ankenId);
	}

	/**
	 * 案件IDの顧客情報を取得します。
	 * 
	 * @param ankenId
	 * @return
	 */
	public List<PersonInfoForAnkenDto> getAnkenCustomerName(Long ankenId) {
		List<PersonInfoForAnkenDto> ankenCustomerNameDtoList = tAnkenCustomerDao.selectPersonInfoForAnkenByAnkenId(List.of(ankenId));
		List<PersonInfoForAnkenDto> sortedAnkenCustomerNameDtoList = ankenCustomerNameDtoList.stream()
				.sorted(Comparator.comparing(PersonInfoForAnkenDto::getPersonId))
				.collect(Collectors.toList());
		return sortedAnkenCustomerNameDtoList;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 担当リストのフォーム情報を作成する
	 *
	 * @param entityList タスク担当DB取得値
	 * @param acountEntityList 全アカウントリスト
	 * @return 担当フォーム情報
	 */
	private List<TaskTanto> createTantoListWithWorkerEntityList(List<TTaskWorkerEntity> entityList, List<MAccountEntity> acountEntityList) {

		// DB取得値をフォーム情報に変換
		List<TaskTanto> itemList = entityList.stream()
				.filter(entity -> SystemFlg.FLG_OFF.equalsByCode(entity.getEntrustFlg()))
				.map(entity -> TaskTanto.builder()
						.workerAccountSeq(entity.getWorkerAccountSeq())
						.build())
				.filter(item -> !item.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));

		// 色を再設定する
		itemList.stream().forEach(item -> {
			for (MAccountEntity entity : acountEntityList) {
				if (item.getWorkerAccountSeq().equals(entity.getAccountSeq())) {
					item.setAccountColor(entity.getAccountColor());
				}
			}
		});

		return itemList;
	}

	/**
	 * タスクSEQをキーとして、紐づく作業者のコメント確認フラグをOFFにします。
	 *
	 * <pre>
	 * 操作したユーザー(ログインユーザー)は除きます。
	 * </pre>
	 *
	 * @param taskSeq
	 * @throws AppException
	 */
	private void historyKauninFlgUpdateToFlgOff(Long taskSeq) throws AppException {

		// 作業者情報を取得
		List<TTaskWorkerEntity> workerList = tTaskWorkerDao.selectByParentSeq(taskSeq);

		// 新規登録時は、まだ作業者は登録されていないのでなにもしない。
		if (workerList.isEmpty()) {
			return;
		}

		for (TTaskWorkerEntity entity : workerList) {
			// 登録者以外の履歴確認フラグをOFFにする
			if (!Objects.equals(entity.getWorkerAccountSeq(), SessionUtils.getLoginAccountSeq())) {
				entity.setNewHistoryKakuninFlg(SystemFlg.FLG_OFF.getCd());
			}

			// 作業者情報の更新
			try {
				tTaskWorkerDao.update(entity);
			} catch (Exception e) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + e.getMessage());
				throw new AppException(MessageEnum.MSG_E00013, e);
			}
		}
	}

	/**
	 * 案件タスクEntityを案件タスクDtoに変換します。
	 * 
	 * @param taskAnkenList
	 * @return
	 */
	private List<TaskAnkenDto> convertTaskEntity2TaskAnkenDto(List<TTaskAnkenEntity> taskAnkenList) {
		return taskAnkenList.stream().map(bean -> {
			TaskAnkenDto dto = new TaskAnkenDto();

			dto.setAnkenId(AnkenId.of(bean.getAnkenId()));
			dto.setDispOrder(bean.getDispOrder());

			return dto;
		}).collect(Collectors.toList());
	}

	/**
	 * 案件に関する顧客情報をセットします。<br>
	 * （顧客先頭１人分の名前、顧客の合計数）
	 * 
	 * @param ankenList
	 */
	private void setAnkenListPersonName(List<TaskAnkenDto> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenPersonCntBean> customerBeanList = tAnkenDao.selectAnkenPersonByAnkenId(ankenIdList);
		for (TaskAnkenDto dto : ankenList) {
			Optional<AnkenPersonCntBean> optBean = customerBeanList.stream().filter(customerBean -> customerBean.getAnkenId().equals(dto.getAnkenId())).findFirst();
			if (optBean.isPresent()) {
				dto.setCustomerName(optBean.get().getCustomerName());
				dto.setNumberOfCustomer(optBean.get().getNumberOfCustomer());
			}
		}
	}

	/**
	 * 案件に関する案件名をセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenName(List<TaskAnkenDto> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		// 案件IDから案件情報を取得
		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<TAnkenEntity> ankenEntityList = tAnkenDao.selectById(ankenIdList);
		for (TaskAnkenDto dto : ankenList) {
			Optional<TAnkenEntity> optBean = ankenEntityList.stream().filter(ankenEntity -> ankenEntity.getAnkenId().equals(dto.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				dto.setAnkenName(optBean.get().getAnkenName());
			} else {
				dto.setAnkenName("案件が削除されました");
			}
		}
	}

	/**
	 * 案件に関する案件名をセットします。
	 * 
	 * @param ankenList
	 */
	private void setNumberOfTask(List<TaskAnkenDto> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		// 案件IDからタスク数を取得
		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<NumberOfTaskPerAnkenIdBean> taskCountBean = tTaskDao.selectTaskCountByAnkenId(ankenIdList);
		for (TaskAnkenDto dto : ankenList) {
			Optional<NumberOfTaskPerAnkenIdBean> optBean = taskCountBean.stream().filter(bean -> bean.getAnkenId().equals(dto.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				dto.setNumberOfTask(optBean.get().getNumberOfTask());
			} else {
				dto.setNumberOfTask(0L);
			}
		}
	}

	/**
	 * 代理人名を抽出します
	 * 
	 * @param aitegataListBean
	 * @param relatedKanyoshaSeq
	 * @return
	 */
	private String getLawyerName(List<AnkenKanyoshaBean> aitegataListBean, Long relatedKanyoshaSeq) {
		if (LoiozCollectionUtils.isEmpty(aitegataListBean) || relatedKanyoshaSeq == null) {
			return null;
		}

		// 案件IDからタスク数を取得
		Optional<AnkenKanyoshaBean> optBean = aitegataListBean.stream().filter(
				bean -> bean.getKanyoshaSeq().equals(relatedKanyoshaSeq)).findFirst();
		String lawyerName = null;
		if (optBean.isPresent()) {
			lawyerName = optBean.get().getKanyoshaName();
		}
		return lawyerName;
	}

}
