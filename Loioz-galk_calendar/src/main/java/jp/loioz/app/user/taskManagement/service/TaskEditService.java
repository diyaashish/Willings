package jp.loioz.app.user.taskManagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.user.taskManagement.dto.TaskCheckItemDto;
import jp.loioz.app.user.taskManagement.form.CommentForm;
import jp.loioz.app.user.taskManagement.form.edit.TaskEditInputForm;
import jp.loioz.app.user.taskManagement.form.edit.TaskEditInputForm.TaskTanto;
import jp.loioz.app.user.taskManagement.form.edit.TaskEditViewForm;
import jp.loioz.app.user.taskManagement.form.edit.TaskListScheduleViewForm;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenReletedUpdateKbn;
import jp.loioz.common.constant.CommonConstant.CheckItemStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaskCheckItemUpdateKbn;
import jp.loioz.common.constant.CommonConstant.TaskHistoryType;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import jp.loioz.common.constant.CommonConstant.TaskUpdateItem;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TBushoShozokuAcctDao;
import jp.loioz.dao.TTaskCheckItemDao;
import jp.loioz.dao.TTaskDao;
import jp.loioz.dao.TTaskHistoryDao;
import jp.loioz.dao.TTaskWorkerDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AccountJoiningBushoDto;
import jp.loioz.dto.ActivityHistoryDto;
import jp.loioz.dto.TaskHistoryDto;
import jp.loioz.dto.TaskListBean;
import jp.loioz.dto.TaskListDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TBushoShozokuAcctEntity;
import jp.loioz.entity.TTaskCheckItemEntity;
import jp.loioz.entity.TTaskEntity;
import jp.loioz.entity.TTaskHistoryEntity;
import jp.loioz.entity.TTaskWorkerEntity;

/**
 * タスク編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskEditService extends DefaultService {

	/** 共通処理 selectBoxの取得 */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	/** 共通アカウントサービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** タスク共通サービス */
	@Autowired
	private TaskCommonService taskCommonService;

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 部署所属Daoクラス */
	@Autowired
	private TBushoShozokuAcctDao tBushoShozokuAcctDao;

	/** タスクのDaoクラス */
	@Autowired
	private TTaskDao tTaskDao;

	/** タスク-作業者Daoクラス */
	@Autowired
	private TTaskWorkerDao tTaskWorkerDao;

	/** タスク-履歴Daoクラス */
	@Autowired
	private TTaskHistoryDao tTaskHistoryDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件-顧客用Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** タスクチェックアイテムDaoクラス */
	@Autowired
	private TTaskCheckItemDao tTaskCheckItemDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * viewFormの作成処理
	 *
	 * <pre>
	 * 入力情報の初期化処理を行います。
	 * TaskEditController内のメソッドを経由するときは、必ずviewFormが作成されます。
	 * (@ModelAttribute)
	 * </pre>
	 *
	 * @return
	 */
	public TaskEditViewForm createViewForm(Long taskSeq) {

		// 入力フォームの作成
		TaskEditViewForm viewForm = new TaskEditViewForm();

		// ユーザー情報とそのユーザーが所属する部署情報を取得
		if (Objects.isNull(taskSeq)) {
			List<AccountJoiningBushoDto> accountJoiningBushoDto = mAccountDao.selectAccountAndJoiningBusho();
			viewForm.setAccountJoiningBushoList(accountJoiningBushoDto);
		} else {
			List<TTaskWorkerEntity> wokerList = tTaskWorkerDao.selectByParentSeq(taskSeq);
			List<Long> workerSeqList = wokerList.stream().map(TTaskWorkerEntity::getWorkerAccountSeq).collect(Collectors.toList());
			List<AccountJoiningBushoDto> accountJoiningBushoDtoIncludeWorker = mAccountDao.selectAccountAndJoiningBushoIncludeDeleted(workerSeqList);
			viewForm.setAccountJoiningBushoList(accountJoiningBushoDtoIncludeWorker);
		}

		// 部署セレクトボックスの取得
		List<SelectOptionForm> bushoList = new ArrayList<>(commonSelectBoxService.getBushoSelectBox());

		// 部門未所属ユーザーが存在する場合のみ、「部門未所属」を追加
		Set<Long> accountSeqList = mAccountDao.selectEnabledAccount().stream().map(MAccountEntity::getAccountSeq).collect(Collectors.toSet());
		Set<Long> bushoShozokuAcctSeq = tBushoShozokuAcctDao.selectByEnabledAccount().stream().map(TBushoShozokuAcctEntity::getAccountSeq)
				.collect(Collectors.toSet());
		if (!LoiozCollectionUtils.subtract(accountSeqList, bushoShozokuAcctSeq).isEmpty()) {
			bushoList.add(new SelectOptionForm(0, CommonConstant.BUMON_MI_SHOZOKU));
		}

		viewForm.setBushoList(bushoList);

		return viewForm;
	}

	/**
	 * 表示フォームの作成
	 *
	 * <pre>
	 * 追加画面の場合
	 * </pre>
	 *
	 * @param inputForm
	 */
	public void setCreateData(TaskEditInputForm inputForm) throws AppException {

		// 案件ID、案件名
		if (inputForm.getAnkenId() != null) {
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(inputForm.getAnkenId().asLong());
			if (tAnkenEntity == null) {
				// 案件が削除されていたら、楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			inputForm.setAnkenName(tAnkenEntity.getAnkenName());
			inputForm.setAnkenNameAndCustomerName(getAnkenNameAndCustomerName(inputForm.getAnkenId().asLong()));
		}

		// ステータス（初期は未着手）
		inputForm.setTaskStatus(CommonConstant.TaskStatus.PREPARING.getCd());

		// アカウント取得（有効なアカウント）
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();

		// タスク担当セレクトリスト
		List<SelectOptionForm> sof = new ArrayList<>();
		accountEntityList.forEach(entity -> sof.add(new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName())));
		inputForm.setTantoOptions(sof);

		// タスク担当者は未設定
		List<TTaskWorkerEntity> taskTantoList = Collections.emptyList();

		// 担当者を取得
		List<TaskTanto> tantoList = this.createTantoList(taskTantoList, accountEntityList);
		inputForm.setTaskTantoList(tantoList);

	}

	/**
	 * 表示フォームの作成
	 *
	 * <pre>
	 * 編集画面の場合
	 * </pre>
	 *
	 * @param inputForm
	 * @param taskSeq
	 * @param accountSeq
	 * タスク担当者者のアカウントSEQ（案件ダッシュボード画面の場合は、担当しないタスクを表示する場合もあるためNullにする）
	 * @throws AppException
	 */
	public void setEditData(TaskEditInputForm inputForm, Long taskSeq, Long accountSeq) throws AppException {

		// タスクSEQをキーとして、タスクの入力情報を取得します。
		TaskListBean taskBean = tTaskDao.selectBeanBySeq(accountSeq, taskSeq);
		if (taskBean == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// ログインユーザがタスク作成者OR担当者であれば、今日のタスク項目は変更可能
		if (checkTaskCreatedOrTanto(taskBean)) {
			inputForm.setTodayTaskChangeableFlag(true);
		} else {
			inputForm.setTodayTaskChangeableFlag(false);
		}

		/** 各項目に値をセットします。 */
		// タスクSEQ
		inputForm.setTaskSeq(taskBean.getTaskSeq());

		// 案件ID
		if (taskBean.getAnkenId() == null) {
			inputForm.setAnkenId(null);
		} else {
			inputForm.setAnkenId(AnkenId.of(taskBean.getAnkenId()));
		}

		// 案件名
		Long ankenId = taskBean.getAnkenId();
		if (ankenId != null) {
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
			if (tAnkenEntity == null) {
				// 案件が削除されていたら、楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			inputForm.setAnkenName(tAnkenEntity.getAnkenName());
			inputForm.setAnkenNameAndCustomerName(getAnkenNameAndCustomerName(ankenId));
		}

		// 件名
		inputForm.setTitle(taskBean.getTitle());

		// 内容
		inputForm.setContent(taskBean.getContent());

		// 期日
		if (taskBean.getLimitDtTo() != null) {
			inputForm.setLimitDateByLocalDate(taskBean.getLimitDtTo().toLocalDate());
			if (!SystemFlg.codeToBoolean(taskBean.getAllDayFlg())) {
				inputForm.setLimitTimeHour(String.valueOf(taskBean.getLimitDtTo().toLocalTime().getHour()));
				inputForm.setLimitTimeMinute(String.valueOf(taskBean.getLimitDtTo().toLocalTime().getMinute()));
			}
		}

		// タスクステータス
		inputForm.setTaskStatus(taskBean.getTaskStatus());

		// タイトル
		inputForm.setTitle(taskBean.getTitle());

		// 今日のタスク日付
		// 案件ダッシュボードからの場合、アカウントSEQをNULLでタスク情報を取得しているため、別ユーザの情報を取得している場合もあるので今日の日付だけ取得しなおす
		LocalDate todayTaskDate = getTodayTaskDate(taskSeq);
		inputForm.setTodayTaskDate(todayTaskDate);
		if (todayTaskDate != null) {
			inputForm.setTodayTaskFlg(true);
		} else {
			inputForm.setTodayTaskFlg(false);
		}

		// タスク担当者を取得
		List<TTaskWorkerEntity> taskTantoList = tTaskWorkerDao.selectByParentSeq(taskSeq);

		// 担当者を取得
		List<MAccountEntity> allAcountEntity = mAccountDao.selectAll();
		List<TaskTanto> tantoList = this.createTantoList(taskTantoList, allAcountEntity);
		inputForm.setTaskTantoList(tantoList);
		List<Long> tantoSeqList = taskTantoList.stream().filter(entity -> SystemFlg.FLG_OFF.equalsByCode(entity.getEntrustFlg())).map(entity -> entity.getWorkerAccountSeq()).collect(Collectors.toList());
		inputForm.setWorkerAccountSeq(tantoSeqList);

		// アカウント取得（有効なアカウント）
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();

		// 担当者の中に無効なアカウントがいる場合は、有効なアカウントの中に追加する。
		List<Long> enableAccountList = accountEntityList.stream().map(entity -> entity.getAccountSeq()).collect(Collectors.toList());
		for (Long seq : tantoSeqList) {
			if (!enableAccountList.contains(seq)) {
				MAccountEntity addAcountEntity = mAccountDao.selectBySeq(seq);
				accountEntityList.add(addAcountEntity);
			}
		}

		// タスク担当セレクトリスト
		List<SelectOptionForm> sof = new ArrayList<>();
		accountEntityList.forEach(entity -> sof.add(new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName())));
		inputForm.setTantoOptions(sof);

		// サブタスク取得
		List<TTaskCheckItemEntity> tTaskCheckItemEntityList = tTaskCheckItemDao.selectByTaskSeq(taskSeq);
		List<TaskCheckItemDto> taskCheckItemDtoList = convertTaskCheckItemDtoForTaskCheckItemEntity(tTaskCheckItemEntityList);
		inputForm.setCheckList(taskCheckItemDtoList);

	}

	/**
	 * 
	 * /** タスクSEQをキーとして、タスク-履歴情報のセットします
	 *
	 * @param viewForm
	 * @param taskSeq
	 */

	public void setTaskChildHistory(TaskEditViewForm viewForm, Long taskSeq) {
		// タスク履歴情報を取得し種類ごとにフォームにセット
		List<TTaskHistoryEntity> taskHistoryList = tTaskHistoryDao.selectByTaskSeqList(List.of(taskSeq));

		// コメント履歴を取得
		List<TTaskHistoryEntity> commentEntityList = taskHistoryList.stream()
				.filter(entity -> CommonConstant.TaskHistoryType.COMMENT.equalsByCode(entity.getTaskHistoryType())).collect(Collectors.toList());
		viewForm.setNumberOfComment(commentEntityList.size());
		List<TaskHistoryDto> commentDtoList = convertTaskHistoryDtoForTaskHistoryEntity(commentEntityList);
		viewForm.setCommentList(commentDtoList);

		// アクティビティ履歴を取得
		List<TTaskHistoryEntity> activityEntityList = taskHistoryList.stream()
				.filter(entity -> !CommonConstant.TaskHistoryType.COMMENT.equalsByCode(entity.getTaskHistoryType())).collect(Collectors.toList());
		List<ActivityHistoryDto> activityDtoList = convertTaskHistoryEntityForAcitivityHistoryDto(activityEntityList);
		viewForm.setActivityList(activityDtoList);

		// タスク履歴情報を取得
		List<TTaskHistoryEntity> taskHistoryEntity = tTaskHistoryDao.selectByParentSeq(taskSeq);
		viewForm.setTaskHistoryList(convertTaskHistoryDtoForTaskHistoryEntity(taskHistoryEntity));
	}

	/**
	 * 一覧用の表示フォームの作成
	 *
	 * @param viewForm
	 * @param taskSeq
	 * @param accountSeq
	 * @param assignTaskFlg
	 */
	public void setTaskListScheduleData(TaskListScheduleViewForm viewForm, Long taskSeq, Long accountSeq, boolean assignTaskFlg) {

		// 表示するタスク情報取得
		TaskListBean allTaskBean = tTaskDao.selectRelatedTaskByAccountId(accountSeq, assignTaskFlg, taskSeq);

		// タスクSEQをキーとして、タスクの入力情報を取得します。
		if (allTaskBean != null) {
			TaskListBean taskBean = tTaskDao.selectBeanBySeq(accountSeq, taskSeq);

			// タスクDto
			TaskListDto taskDto = new TaskListDto();

			// DB取得データの設定
			taskDto.setTaskSeq(taskBean.getTaskSeq());
			taskDto.setAnkenId(AnkenId.of(taskBean.getAnkenId()));
			// 案件名 + 顧客名のセット取得し設定する
			taskDto.setAnkenNameAndCustomerName(this.getAnkenNameAndCustomerName(taskBean.getAnkenId()));
			taskDto.setAnkenName(AnkenName.of(taskBean.getAnkenName(), taskBean.getBunyaId()));
			taskDto.setTitle(taskBean.getTitle());
			taskDto.setContent(taskBean.getContent());
			taskDto.setLimitDtTo(taskBean.getLimitDtTo());
			if (taskBean.getLimitDtTo() != null) {
				taskDto.setLimitDateForDisplay(taskBean.getLimitDtTo().toLocalDate());
				taskDto.setLimitDate(DateUtils.parseToString(taskBean.getLimitDtTo().toLocalDate(), "yyyy/MM/dd"));
				if (!SystemFlg.codeToBoolean(taskBean.getAllDayFlg())) {
					taskDto.setLimitTime(taskBean.getLimitDtTo().toLocalTime());
				}
			}

			taskDto.setCreaterFlg(SystemFlg.of(taskBean.getCreaterFlg()));
			taskDto.setEntrustFlg(SystemFlg.of(taskBean.getEntrustFlg()));
			taskDto.setTaskStatus(TaskStatus.of(taskBean.getTaskStatus()));
			taskDto.setNewTaskKakuninFlg(SystemFlg.of(taskBean.getNewTaskKakuninFlg()));
			taskDto.setNewHistoryKakuninFlg(SystemFlg.of(taskBean.getNewHistoryKakuninFlg()));
			taskDto.setDispOrder(taskBean.getDispOrder());
			taskDto.setTodayTaskDispOrder(taskBean.getTodayTaskDispOrder());
			taskDto.setTodayTaskDate(taskBean.getTodayTaskDate());
			taskDto.setCommentCount(taskBean.getCommentCount());
			if (taskBean.getAssignAccountSeq() != null) {
				taskDto.setAssignAccountSeqList(StringUtils.toArray(taskBean.getAssignAccountSeq()));
			}
			taskDto.setAssignedAccountSeq(taskBean.getAssignedAccountSeq());

			// アカウント情報の取得
			List<MAccountEntity> accountList = mAccountDao.selectAll();
			List<Long> accountSeqList = StringUtils.toLongArray(taskBean.getWorkerAccountSeq());
			if (!LoiozCollectionUtils.isEmpty(accountSeqList)) {
				taskDto.setWorkerAccountList(taskCommonService.setWorker(accountList, accountSeqList));
				taskDto.setAssignTaskTantoList(taskCommonService.createTantoListWithAccountSeqList(accountSeqList, accountList));
			}

			// 作成者名
			String createDispName = new PersonName(
					taskBean.getAccountNameSei(), taskBean.getAccountNameMei(),
					null, null).getName();
			taskDto.setCreateName(createDispName);

			// 期限日の文字色指定
			taskDto.setLimitDateStyle(taskCommonService.getLimitDateStyle(taskDto));

			// サブタスク件数
			taskDto.setCheckItemCount(taskBean.getCheckItemCount());
			taskDto.setCompleteCheckItemCount(taskBean.getCompleteCheckItemCount());

			// フォームに設定
			viewForm.setTaskDto(taskDto);
		}
	}

	/**
	 * ログインユーザーの各フラグをONにします
	 *
	 * <pre>
	 * 新着タスク確認フラグ
	 * 新着履歴確認フラグ
	 * </pre>
	 *
	 * @param taskSeq
	 */
	public void openModalUpdate(Long taskSeq) throws AppException {

		TTaskEntity tTaskEntity = tTaskDao.selectBySeq(taskSeq);
		TTaskWorkerEntity updateEntity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(taskSeq, SessionUtils.getLoginAccountSeq());

		// 排他エラーチェック
		if (tTaskEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (updateEntity == null) {
			// 開いたユーザーが作業者に登録されていない場合 -> 何もせずに返却
			return;
		}

		// フラグを設定する
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

	/**
	 * タスクの新規登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public Long insertTask(TaskEditInputForm inputForm) throws AppException {

		if (StringUtils.isEmpty(inputForm.getContent())) {
			// 詳細が無い場合空にする（空文字の場合もnullで統一させる）
			inputForm.setContent(null);
		}

		TTaskEntity taskEntity = new TTaskEntity();

		if (!Objects.isNull(inputForm.getAnkenId())) {
			TAnkenEntity entity = tAnkenDao.selectById(inputForm.getAnkenId().asLong());
			if (Objects.isNull(entity)) {
				// 紐づけようとした案件が削除されている場合は楽観ロックエラー
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
		}
		taskEntity.setAnkenId(!Objects.isNull(inputForm.getAnkenId()) ? inputForm.getAnkenId().asLong() : null);
		taskEntity.setTitle(inputForm.getTitle());
		taskEntity.setContent(inputForm.getContent());

		// 期限(日)が入力されている場合のみ、設定を行う
		if (inputForm.getLimitLocalDate() != null) {
			// 時間が入力されていない場合
			if (inputForm.getLimitTime() == null) {
				taskEntity.setLimitDtTo(LocalDateTime.of(inputForm.getLimitLocalDate(), LocalTime.MIN));
				taskEntity.setAllDayFlg(SystemFlg.FLG_ON.getCd());
			} else {
				taskEntity.setLimitDtTo(LocalDateTime.of(inputForm.getLimitLocalDate(), inputForm.getLimitTime()));
				taskEntity.setAllDayFlg(SystemFlg.FLG_OFF.getCd());
			}
		}

		// ステータス設定
		taskEntity.setTaskStatus(inputForm.getTaskStatus());

		try {
			// タスク(親)の登録処理
			tTaskDao.insert(taskEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
		// タスク-履歴(子)の登録処理
		taskCommonService.insertHistory(taskEntity.getTaskSeq(), null, true);
		// タスク-作業者(子)の登録処理
		insertTaskWorker(taskEntity.getTaskSeq(), taskEntity.getTaskStatus(), inputForm.getWorkerAccountSeq(), true, inputForm.isTodayTaskFlg());
		// サブタスクの登録処理
		taskCommonService.saveTaskCheckItemList(taskEntity.getTaskSeq(), inputForm.getCheckList());

		return taskEntity.getTaskSeq();
	}

	/**
	 * タスクの更新処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void updateTask(TaskEditInputForm inputForm) throws AppException {

		if (StringUtils.isEmpty(inputForm.getContent())) {
			// 詳細が無い場合空にする（空文字の場合もnullで統一させる）
			inputForm.setContent(null);
		}

		// 更新前のタスク情報を取得
		TTaskEntity taskEntity = tTaskDao.selectBySeq(inputForm.getTaskSeq());
		if (taskEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 案件が削除されている場合は楽観ロックエラー
		if (!Objects.isNull(inputForm.getAnkenId())) {
			TAnkenEntity entity = tAnkenDao.selectById(inputForm.getAnkenId().asLong());
			if (Objects.isNull(entity)) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
		}

		// 更新前の作業者情報を取得
		List<Long> taskWorkerSeqList = tTaskWorkerDao.selectByParentSeq(inputForm.getTaskSeq())
				.stream()
				.filter(entity -> SystemFlg.FLG_OFF.equalsByCode(entity.getEntrustFlg()))
				.map(TTaskWorkerEntity::getWorkerAccountSeq)
				.collect(Collectors.toList());

		// どの項目が更新されるかを管理
		Map<TaskUpdateItem, String> updateItemMap = getDataUpdateForEachItem(inputForm, taskEntity, taskWorkerSeqList);

		// 更新されたかの判定 更新後タスクステータスは除く
		boolean updateFlg = Arrays.asList(TaskUpdateItem.values()).stream()
				.filter(item -> !Objects.equals(item, TaskUpdateItem.UPDATED_STATUS))
				.anyMatch(item -> SystemFlg.codeToBoolean(updateItemMap.get(item)));

		// 更新内容がない場合は何もせずにリターンする
		if (!updateFlg) {
			return;
		}

		// 更新するデータを設定
		taskEntity.setTitle(inputForm.getTitle());
		taskEntity.setContent(inputForm.getContent());
		taskEntity.setTaskStatus(inputForm.getTaskStatus());
		taskEntity.setAnkenId(inputForm.getAnkenId() == null ? null : inputForm.getAnkenId().asLong());

		try {
			// タスク情報の更新
			tTaskDao.update(taskEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// タスク-履歴登録
		if (updateFlg) {
			taskCommonService.insertHistory(inputForm.getTaskSeq(), updateItemMap, false);
		}

		// タスク作業者情報を更新
		updateTaskWorker(inputForm, taskEntity);
	}

	/**
	 * コメントの更新処理
	 *
	 * @param taskSeq
	 * @param taskHistorySeq
	 * @param comment
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
		taskCommonService.historyKauninFlgUpdateToFlgOff(commentForm.getTaskSeq());

	}

	/**
	 * コメントの削除処理
	 *
	 * @param taskSeq
	 * @param taskHistorySeq
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
	 * 今日のタスクへ更新
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateToTaskToday(TaskEditInputForm inputForm) throws AppException {

		// タスク詳細情報を取得
		TaskListBean taskBean = tTaskDao.selectBeanBySeq(SessionUtils.getLoginAccountSeq(), inputForm.getTaskSeq());
		if (taskBean == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新前のタスク作業者情報を取得
		TTaskWorkerEntity tTaskWorkerEntity = tTaskWorkerDao.selectBySeq(taskBean.getTaskWorkerSeq());
		if (tTaskWorkerEntity == null) {
			// タスクの担当者、作成者でないアカウントが「今日のタスク」にしようとした場合はエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00155, null);
		}

		// 今日のタスク
		boolean isTodayTask = false;
		if (tTaskWorkerEntity.getTodayTaskDispOrder() != null && LocalDate.now().isEqual(tTaskWorkerEntity.getTodayTaskDate())) {
			isTodayTask = true;
		}
		// 変更がなければ終了
		if (isTodayTask == inputForm.isTodayTaskFlg()) {
			return;
		}

		// 更新する値をセット
		if (inputForm.isTodayTaskFlg()) {
			tTaskWorkerEntity.setTodayTaskDate(LocalDate.now());
			tTaskWorkerEntity.setTodayTaskDispOrder(tTaskWorkerDao.selectMaxTodayTaskDispOrderByAccountSeq(SessionUtils.getLoginAccountSeq()) + 1);
		} else {
			tTaskWorkerEntity.setTodayTaskDate(null);
			tTaskWorkerEntity.setTodayTaskDispOrder(null);
		}

		// 更新
		tTaskWorkerDao.update(tTaskWorkerEntity);
	}

	/**
	 * SEQキーのカード情報のみ取得・設定します。<br>
	 * 案件ダッシュボードのタスク更新用。
	 *
	 * @param taskSeq
	 */
	public TaskListDto getReplaceCardData(Long taskSeq) throws AppException {

		// タスク情報の取得
		TaskListBean taskBean = tTaskDao.selectBeanBySeq(null, taskSeq);
		if (taskBean == null) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// アカウント情報の取得
		List<MAccountEntity> accountList = mAccountDao.selectAll();

		// 取得したデータをセット
		return taskCommonService.convertBeanForDto(accountList, taskBean);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * タスク-履歴の変換 Entity → Dto
	 *
	 * @param taskHistoryEntity
	 * @param accountList
	 * @return 変換したDto
	 */
	private List<ActivityHistoryDto> convertTaskHistoryEntityForAcitivityHistoryDto(List<TTaskHistoryEntity> taskHistoryEntity) {

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// 作成日降順で並び替え
		List<TTaskHistoryEntity> sortedTaskHistoryList = taskHistoryEntity.stream()
				.sorted(Comparator.comparing(TTaskHistoryEntity::getCreatedAt).reversed()).collect(Collectors.toList());
		List<ActivityHistoryDto> list = new ArrayList<>();
		for (TTaskHistoryEntity entity : sortedTaskHistoryList) {
			// タスク作成履歴
			if (CommonConstant.TaskHistoryType.CREATE.equalsByCode(entity.getTaskHistoryType())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TASK_ACTIVITY_TASK_CREATE_ITEM_NAME);
				dto.setAction(CommonConstant.TASK_ACTIVITY_CREATE_ACTION);
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				list.add(dto);
			}

			// タイトル更新
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(entity.getTitleUpdateFlg())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TaskUpdateItem.TITLE.getVal());
				dto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				list.add(dto);
			}

			// 詳細更新
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(entity.getContentUpdateFlg())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TaskUpdateItem.CONTENT.getVal());
				dto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				list.add(dto);
			}

			// 担当更新
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(entity.getWorkerUpdateFlg())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TaskUpdateItem.WORKER.getVal());
				dto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				list.add(dto);
			}

			// 期限更新
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(entity.getLimitDtUpdateFlg())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TaskUpdateItem.LIMIT_DATE.getVal());
				dto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				list.add(dto);
			}

			// 進捗
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(entity.getStatusUpdateFlg())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TaskUpdateItem.STATUS.getVal());
				dto.setTaskStatus(CommonConstant.TaskStatus.of(entity.getUpdatedStatus()).getVal());
				dto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				list.add(dto);
			}

			// 案件設定
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(entity.getAnkenReletedUpdateFlg())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TaskUpdateItem.ANKEN.getVal());
				dto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				list.add(dto);
			}

			// サブタスク
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(entity.getCheckItemUpdateFlg())) {
				ActivityHistoryDto dto = new ActivityHistoryDto();
				dto.setItemName(CommonConstant.TaskUpdateItem.CHECK_ITEM.getVal());
				dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
				dto.setCheckItemName(entity.getCheckItemName());
				dto.setCheckItemUpdateKbn(entity.getCheckItemUpdateKbn());
				// アクション
				if (TaskCheckItemUpdateKbn.REGIST.equalsByCode(entity.getCheckItemUpdateKbn())) {
					dto.setAction(CommonConstant.TASK_ACTIVITY_CREATE_ACTION);
				} else if (TaskCheckItemUpdateKbn.DELETE.equalsByCode(entity.getCheckItemUpdateKbn())) {
					dto.setAction(CommonConstant.TASK_ACTIVITY_DELETE_ACTION);
				} else {
					dto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				}
				list.add(dto);
			}

			// 更新時間
			ActivityHistoryDto dto = new ActivityHistoryDto();
			dto.setCreatedAt(entity.getCreatedAt());
			list.add(dto);

		}

		return list;
	}

	/**
	 * タスク-作業者の登録処理
	 *
	 * @param taskSeq 親テーブルのシーケンス
	 * @param taskStatus 親テーブルのステータス
	 * @param workerSeqList 登録する作業者のアカウントシーケンス
	 * @param isNewRegistTask
	 * @param isTodayTask 今日やるタスクかどうか
	 * @throws AppException
	 */
	private void insertTaskWorker(Long taskSeq, String taskStatus, List<Long> workerSeqList, boolean isNewRegistTask, boolean isTodayTask) throws AppException {

		// 登録データの作成
		List<TTaskWorkerEntity> taskWorkerEntityList = new ArrayList<>();
		workerSeqList.forEach(account -> {
			TTaskWorkerEntity entity = new TTaskWorkerEntity();
			entity.setTaskSeq(taskSeq);
			entity.setWorkerAccountSeq(account);
			if (isNewRegistTask && account.equals(SessionUtils.getLoginAccountSeq())) {
				entity.setCreaterFlg(SystemFlg.FLG_ON.getCd());
			} else {
				entity.setCreaterFlg(SystemFlg.FLG_OFF.getCd());
			}
			if (account.equals(SessionUtils.getLoginAccountSeq())) {
				entity.setNewTaskKakuninFlg(SystemFlg.FLG_ON.getCd());
			} else {
				entity.setNewTaskKakuninFlg(SystemFlg.FLG_OFF.getCd());
			}
			// 新規に作成した場合は、すでに紐づけられている物は考慮しない
			entity.setNewHistoryKakuninFlg(SystemFlg.FLG_ON.getCd());
			// 指定されたユーザーは作業を行うのでフラグをOFFにする
			entity.setEntrustFlg(SystemFlg.FLG_OFF.getCd());
			// 表示順のセット
			if (TaskStatus.COMPLETED.equalsByCode(taskStatus)) {
				entity.setDispOrder(null);
			} else {
				// ステータスが完了以外で、作業者が作成したタスクなら表示順に最大値+1
				if (account.equals(SessionUtils.getLoginAccountSeq())) {
					entity.setDispOrder(tTaskWorkerDao.selectMaxDispOrderByAccountSeq(account) + 1);
				} else {
					entity.setDispOrder(null);
				}
			}
			// 「今日のタスク」で担当者に作成者が選択されている場合は、今日のタスクの実施日と表示順をセットする
			if (isTodayTask && SessionUtils.getLoginAccountSeq().equals(account)) {
				entity.setTodayTaskDate(LocalDate.now());
				entity.setTodayTaskDispOrder(tTaskWorkerDao.selectMaxTodayTaskDispOrderByAccountSeq(SessionUtils.getLoginAccountSeq()) + 1);
			}
			taskWorkerEntityList.add(entity);
		});

		// タスクを新規登録するときに、作成者が画面で選択されていない場合、
		// 作業はしないがカードは確認できるようにするため、タスク-作業者に登録を行うが、委任フラグをオンにする。
		// 今日のタスクの場合は、今日のタスクの実施日と表示順をセットする。
		if (isNewRegistTask && !workerSeqList.contains(SessionUtils.getLoginAccountSeq())) {
			TTaskWorkerEntity entity = new TTaskWorkerEntity();
			entity.setTaskSeq(taskSeq);
			entity.setWorkerAccountSeq(SessionUtils.getLoginAccountSeq());
			entity.setEntrustFlg(SystemFlg.FLG_ON.getCd());
			entity.setCreaterFlg(SystemFlg.FLG_ON.getCd());
			entity.setNewTaskKakuninFlg(SystemFlg.FLG_ON.getCd());
			entity.setNewHistoryKakuninFlg(SystemFlg.FLG_ON.getCd());
			if (isTodayTask) {
				entity.setTodayTaskDate(LocalDate.now());
				entity.setTodayTaskDispOrder(tTaskWorkerDao.selectMaxTodayTaskDispOrderByAccountSeq(SessionUtils.getLoginAccountSeq()) + 1);
			}
			// 表示順のセット
			if (TaskStatus.COMPLETED.equalsByCode(taskStatus)) {
				entity.setDispOrder(null);
			} else {
				if (LoiozCollectionUtils.isEmpty(workerSeqList)) {
					// ステータスが完了以外で作業者が誰もいなければ表示順に最大値+1
					entity.setDispOrder(tTaskWorkerDao.selectMaxDispOrderByAccountSeq(SessionUtils.getLoginAccountSeq()) + 1);
				} else {
					// ステータスが完了以外で作業者が他にいれば表示順は空
					entity.setDispOrder(null);
				}
			}
			taskWorkerEntityList.add(entity);
		}

		try {
			// 登録処理
			tTaskWorkerDao.insert(taskWorkerEntityList);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * タスク-作業者の削除処理
	 *
	 * @param taskSeq 親テーブルのシーケンス
	 * @param workerSeqList 登録する作業者のアカウントシーケンス
	 * @throws AppException
	 */
	private void deleteTaskWorker(Long taskSeq, List<Long> workerSeqList) throws AppException {

		// 削除するデータを取得する
		List<TTaskWorkerEntity> deleteEntityList = tTaskWorkerDao.selectByParentSeqAndWorkerSeqList(taskSeq, workerSeqList);

		// 楽観エラーチェック
		if (deleteEntityList.isEmpty()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 削除処理
			tTaskWorkerDao.delete(deleteEntityList);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}

	/**
	 * 案件IDに該当する案件名、顧客名を取得
	 * 
	 * @param ankenId
	 * @return 案件名（案-XX） 顧客名 , 顧客名, 顧客名・・・
	 */
	private String getAnkenNameAndCustomerName(Long ankenId) {
		if (ankenId == null) {
			return "(案件名未入力)";
		} else {
			AnkenCustomerRelationBean bean = tAnkenCustomerDao.selectCustomerNameAnkenNameByAnkenId(ankenId);
			if (bean == null) {
				return "(案件名未入力)";
			}
			return (StringUtils.isEmpty(bean.getAnkenName()) ? "(案件名未入力)" : bean.getAnkenName()) + " (案-" + bean.getAnkenId() + ")　" + bean.getCustomerName();

		}
	}

	/**
	 * 割り当て欄のフォーム情報を作成する<br>
	 *
	 * @param workerList タスク担当者リスト
	 * @param acountEntityList 全アカウントリスト
	 * @return 割り当てフォーム情報
	 */
	private List<TaskTanto> createTantoList(List<TTaskWorkerEntity> workerList, List<MAccountEntity> acountEntityList) {

		// アカウントリストをベースにリストを作成。タスク担当者の場合は担当者フラグをtrueにする。
		List<Long> workerSeqList = workerList.stream()
				.filter(entity -> SystemFlg.FLG_OFF.equalsByCode(entity.getEntrustFlg())).map(entity -> entity.getWorkerAccountSeq()).collect(Collectors.toList());
		List<TaskTanto> itemList = new ArrayList<TaskTanto>();
		for (MAccountEntity entity : acountEntityList) {
			TaskTanto tanto = new TaskTanto(entity.getAccountSeq(), workerSeqList.contains(entity.getAccountSeq()), entity.getAccountColor());
			itemList.add(tanto);
		}
		return itemList;
	}

	/*
	 * タスク-履歴の変換 Entity → Dto
	 *
	 * @param taskHistoryEntity
	 * @param accountList
	 * @return 変換したDto
	 */
	private List<TaskHistoryDto> convertTaskHistoryDtoForTaskHistoryEntity(List<TTaskHistoryEntity> taskHistoryEntity) {

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		List<TaskHistoryDto> list = taskHistoryEntity.stream()
				.map(entity -> {
					TaskHistoryDto dto = new TaskHistoryDto();
					dto.setTaskHistorySeq(entity.getTaskHistorySeq());
					dto.setTaskSeq(entity.getTaskSeq());
					// コメント文のURLをリンクコードへ変換する
					dto.setCommentForDisplay((taskCommonService.convTaskCommentURLLink(entity.getComment())));
					dto.setComment(entity.getComment());
					dto.setCreatedAt(entity.getCreatedAt());
					dto.setUpdatedAt(entity.getUpdatedAt());
					dto.setCreatedBy(entity.getCreatedBy());
					dto.setVersionNo(entity.getVersionNo());
					dto.setTaskHistoryType(TaskHistoryType.of(entity.getTaskHistoryType()));

					// 履歴情報を設定します。
					if (TaskHistoryType.HISTORY.equalsByCode(entity.getTaskHistoryType())) {
						dto.setTitleUpdateFlg(SystemFlg.of(entity.getTitleUpdateFlg()));
						dto.setContentUpdateFlg(SystemFlg.of(entity.getContentUpdateFlg()));
						dto.setWorkerUpdateFlg(SystemFlg.of(entity.getWorkerUpdateFlg()));
						dto.setLimitDtUpdateFlg(SystemFlg.of(entity.getLimitDtUpdateFlg()));
						dto.setStatusUpdateFlg(SystemFlg.of(entity.getStatusUpdateFlg()));
						if (SystemFlg.codeToBoolean(entity.getStatusUpdateFlg())) {
							dto.setUpdatedStatus(TaskStatus.of(entity.getUpdatedStatus()));
						}
						dto.setAnkenReletedUpdateFlg(SystemFlg.of(entity.getAnkenReletedUpdateFlg()));
						if (SystemFlg.codeToBoolean(entity.getAnkenReletedUpdateFlg())) {
							dto.setAnkenReletedUpdateKbn(AnkenReletedUpdateKbn.of(entity.getAnkenReletedUpdateKbn()));
						}
					}

					// アカウントSEQから名前を取得し、設定する
					dto.setAccountName(accountNameMap.get(entity.getCreatedBy()));
					dto.setMyComment(entity.getCreatedBy().equals(SessionUtils.getLoginAccountSeq()));
					return dto;
				})
				// 更新日でソート（降順）
				.sorted(Comparator.comparing(TaskHistoryDto::getCreatedAt).reversed())
				.collect(Collectors.toList());
		return list;
	}

	/**
	 * ログインユーザがタスクの作成者か担当者かを判定します。
	 * 
	 * @param taskListBean
	 * @param accountSeq
	 * @return true:作成者か担当者である false:作成者、担当者ではない
	 */
	private boolean checkTaskCreatedOrTanto(TaskListBean taskListBean) {

		Long accountSeq = SessionUtils.getLoginAccountSeq();

		List<String> workerAccountList = new ArrayList<>();
		if (!StringUtils.isEmpty((taskListBean.getWorkerAccountSeq()))) {
			workerAccountList = Arrays.asList(taskListBean.getWorkerAccountSeq().split(","));
		}

		if (taskListBean.getCreaterAccountSeq().equals(accountSeq) || workerAccountList.contains(String.valueOf(accountSeq))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ログインユーザがtaskSeqのタスクを「今日のタスク」として設定している場合の日付を取得する。<br>
	 * 今日のタスクでない場合はnullを返却。
	 * 
	 * @param taskSeq
	 * @return
	 */
	private LocalDate getTodayTaskDate(Long taskSeq) {

		TaskListBean taskBean = tTaskDao.selectBeanBySeq(SessionUtils.getLoginAccountSeq(), taskSeq);
		if (taskBean == null) {
			return null;
		}

		return taskBean.getTodayTaskDate();
	}

	/**
	 * タスク-チェックアイテムの変換 Entity → Dto
	 * 
	 * @param tTaskCheckItemEntityList
	 * @return
	 */
	private List<TaskCheckItemDto> convertTaskCheckItemDtoForTaskCheckItemEntity(List<TTaskCheckItemEntity> tTaskCheckItemEntityList) {

		List<TaskCheckItemDto> list = tTaskCheckItemEntityList.stream()
				.map(entity -> {
					TaskCheckItemDto dto = new TaskCheckItemDto();
					dto.setTaskCheckItemSeq(entity.getTaskCheckItemSeq());
					dto.setTaskSeq(entity.getTaskSeq());
					dto.setItemName(entity.getItemName());
					dto.setCompleteFlg(CheckItemStatus.COMPLETED.equalsByCode(entity.getCompleteFlg()) ? true : false);
					dto.setDispOrder(entity.getDispOrder());
					dto.setCreatedAt(entity.getCreatedAt());
					dto.setCreatedBy(entity.getCreatedBy());
					dto.setUpdatedAt(entity.getUpdatedAt());
					dto.setUpdatedBy(entity.getUpdatedBy());

					return dto;
				})
				.collect(Collectors.toList());
		return list;
	}

	/**
	 * タスクのどのデータが更新されたかを項目ごとに確認する。<br>
	 * 
	 * @param inputForm
	 * @param taskEntity
	 * @param taskWorkerSeqList
	 * @return Map<項目, 変更有無>
	 */
	private Map<TaskUpdateItem, String> getDataUpdateForEachItem(TaskEditInputForm inputForm, TTaskEntity taskEntity, List<Long> taskWorkerSeqList) {
		Map<TaskUpdateItem, String> updateItemMap = new HashMap<>();
		updateItemMap.put(TaskUpdateItem.TITLE, SystemFlg.booleanToCode(!Objects.equals(inputForm.getTitle(), taskEntity.getTitle())));
		updateItemMap.put(TaskUpdateItem.CONTENT, SystemFlg.booleanToCode(!Objects.equals(inputForm.getContent(), taskEntity.getContent())));
		updateItemMap.put(TaskUpdateItem.WORKER, SystemFlg.booleanToCode(!Objects.deepEquals(inputForm.getWorkerAccountSeq(), taskWorkerSeqList)));
		updateItemMap.put(TaskUpdateItem.ANKEN, SystemFlg.booleanToCode(!Objects.deepEquals(inputForm.getAnkenId() == null ? null : inputForm.getAnkenId().asLong(), taskEntity.getAnkenId())));
		if (inputForm.getAnkenId() == null) {
			updateItemMap.put(TaskUpdateItem.UPDATED_ANKEN_KBN, AnkenReletedUpdateKbn.ANKEN_KAIJO.getCd());
		} else {
			if (taskEntity.getAnkenId() == null) {
				updateItemMap.put(TaskUpdateItem.UPDATED_ANKEN_KBN, AnkenReletedUpdateKbn.ANKEN_TOUROKU.getCd());
			} else {
				updateItemMap.put(TaskUpdateItem.UPDATED_ANKEN_KBN, AnkenReletedUpdateKbn.ANKEN_HENKOU.getCd());
			}
		}

		// 期限(日)が入力されている場合のみ、設定を行う
		if (inputForm.getLimitLocalDate() != null) {
			// 時間が入力されていない場合
			if (inputForm.getLimitTime() == null) {
				boolean isChange = !Objects.equals(
						LocalDateTime.of(inputForm.getLimitLocalDate(), LocalTime.MIN), taskEntity.getLimitDtTo());
				updateItemMap.put(TaskUpdateItem.LIMIT_DATE, SystemFlg.booleanToCode(isChange));
				taskEntity.setAllDayFlg(SystemFlg.FLG_ON.getCd());
				taskEntity.setLimitDtTo(LocalDateTime.of(inputForm.getLimitLocalDate(), LocalTime.MIN));
			} else {
				boolean isChange = !Objects.equals(
						LocalDateTime.of(inputForm.getLimitLocalDate(), inputForm.getLimitTime()), taskEntity.getLimitDtTo());
				updateItemMap.put(TaskUpdateItem.LIMIT_DATE, SystemFlg.booleanToCode(isChange));
				taskEntity.setAllDayFlg(SystemFlg.FLG_OFF.getCd());
				taskEntity.setLimitDtTo(LocalDateTime.of(inputForm.getLimitLocalDate(), inputForm.getLimitTime()));
			}
		} else {
			updateItemMap.put(TaskUpdateItem.LIMIT_DATE, SystemFlg.booleanToCode(Objects.nonNull(taskEntity.getLimitDtTo())));
			taskEntity.setLimitDtTo(null);
		}
		updateItemMap.put(TaskUpdateItem.STATUS, SystemFlg.booleanToCode(!Objects.equals(inputForm.getTaskStatus(), taskEntity.getTaskStatus())));
		updateItemMap.put(TaskUpdateItem.UPDATED_STATUS, inputForm.getTaskStatus());

		return updateItemMap;
	}

	/**
	 * タスク作業者情報を更新します。
	 * 
	 * @param inputForm
	 * @param taskEntity
	 * @throws AppException
	 */
	private void updateTaskWorker(TaskEditInputForm inputForm, TTaskEntity taskEntity) throws AppException {

		// 変更前の作業者情報を取得
		List<TTaskWorkerEntity> beforeWorkerList = tTaskWorkerDao.selectByParentSeq(inputForm.getTaskSeq());

		// 作業者から外す人の抽出
		List<Long> outWorker = beforeWorkerList
				.stream()
				.filter(entity -> SystemFlg.FLG_OFF.equalsByCode(entity.getEntrustFlg()))
				.filter(entity -> {
					return !inputForm.getWorkerAccountSeq()
							.stream()
							.anyMatch(seq -> Objects.equals(seq, entity.getWorkerAccountSeq()));
				})
				.map(TTaskWorkerEntity::getWorkerAccountSeq)
				.collect(Collectors.toList());

		// 作業者に追加された人のSEQを抽出
		List<Long> addWorker = inputForm.getWorkerAccountSeq()
				.stream()
				.filter(seq -> {
					return !beforeWorkerList
							.stream()
							.filter(entity -> SystemFlg.FLG_OFF.equalsByCode(entity.getEntrustFlg()))
							.anyMatch(beforeWorker -> Objects.equals(seq, beforeWorker.getWorkerAccountSeq()));
				})
				.collect(Collectors.toList());

		// 作成者情報の更新
		TTaskWorkerEntity createrUpdateEntity = new TTaskWorkerEntity();
		boolean updateCreaterEntityFlg = false;
		if (outWorker.contains(taskEntity.getCreatedBy())) {
			// 外す作業者(outWorker)に登録者が含まれている場合、登録者のデータはタスク作成時に作成されているため、
			// 外す作業者(outWorker)から除外し、委任フラグをONにする。履歴は更新されているのでフラグOFFにする
			outWorker.remove(taskEntity.getCreatedBy());
			createrUpdateEntity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(inputForm.getTaskSeq(), taskEntity.getCreatedBy());
			createrUpdateEntity.setEntrustFlg(SystemFlg.FLG_ON.getCd());
			createrUpdateEntity.setNewHistoryKakuninFlg(SystemFlg.FLG_OFF.getCd());
			// タスクステータスが完了以外で表示順が空なら表示順をセットする
			// if (!TaskStatus.COMPLETED.equalsByCode(inputForm.getTaskStatus()) && createrUpdateEntity.getDispOrder() == null) {
			if (!TaskStatus.COMPLETED.equalsByCode(inputForm.getTaskStatus())) {
				List<TTaskWorkerEntity> workerList = tTaskWorkerDao.selectByParentSeq(inputForm.getTaskSeq());
				Long otherWorkerCount = workerList.stream().filter(entity -> !entity.getWorkerAccountSeq().equals(taskEntity.getCreatedBy())).count();
				//作業者を追加しないかつ作業者に登録者以外を登録していない場合
				if (LoiozCollectionUtils.isEmpty(addWorker) && otherWorkerCount.equals(0L)) {
					// 表示順が空の場合は、表示順に最大値+1をセットする
					if (createrUpdateEntity.getDispOrder() == null) {
						createrUpdateEntity.setDispOrder(tTaskWorkerDao.selectMaxDispOrderByAccountSeq(taskEntity.getCreatedBy()) + 1);
					}
				} else {
					// 作業者が他に存在する場合、「すべてのタスク」で表示しないため表示順を空にする
					createrUpdateEntity.setDispOrder(null);
				}
			} else {
				// タスクを完了にする場合、表示順を空にする
				createrUpdateEntity.setDispOrder(null);
			}
			updateCreaterEntityFlg = true;
		} else if (addWorker.contains(taskEntity.getCreatedBy())) {
			// 追加する作業者(addWorker)に登録者が含まれている場合、登録者のデータはタスク作成時に作成されているため、
			// 追加する作業者(addWorker)から除外し、委任フラグと履歴確認フラグをOFFにする
			addWorker.remove(taskEntity.getCreatedBy());
			createrUpdateEntity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(inputForm.getTaskSeq(), taskEntity.getCreatedBy());
			createrUpdateEntity.setEntrustFlg(SystemFlg.FLG_OFF.getCd());
			createrUpdateEntity.setNewHistoryKakuninFlg(SystemFlg.FLG_OFF.getCd());
			// タスクステータスが完了以外で表示順が空なら、表示順に最大値+1をセットする
			if (!TaskStatus.COMPLETED.equalsByCode(inputForm.getTaskStatus()) && createrUpdateEntity.getDispOrder() == null) {
				createrUpdateEntity.setDispOrder(tTaskWorkerDao.selectMaxDispOrderByAccountSeq(taskEntity.getCreatedBy()) + 1);
			}
			updateCreaterEntityFlg = true;
		} else if (LoiozCollectionUtils.isNotEmpty(addWorker)) {
			// 登録者以外の作業者を追加する場合、登録者の委任フラグがONであれば、履歴フラグOFFにする、「すべてのタスク」で表示しないため表示順を空にする
			createrUpdateEntity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(inputForm.getTaskSeq(), taskEntity.getCreatedBy());
			if (SystemFlg.FLG_ON.equalsByCode(createrUpdateEntity.getEntrustFlg())) {
				createrUpdateEntity.setNewHistoryKakuninFlg(SystemFlg.FLG_OFF.getCd());
				createrUpdateEntity.setDispOrder(null);
				updateCreaterEntityFlg = true;
			}
		} else if (LoiozCollectionUtils.isNotEmpty(outWorker)) {
			// 作業者を全員外した場合、委任フラグと履歴確認フラグをOFFにする
			List<TTaskWorkerEntity> workerList = tTaskWorkerDao.selectByParentSeq(inputForm.getTaskSeq());
			Long workerCount = workerList.stream().filter(entity -> !(entity.getWorkerAccountSeq().equals(taskEntity.getCreatedBy()) && entity.getEntrustFlg().equals(SystemFlg.FLG_ON.getCd()))).count();
			if (outWorker.size() == workerCount.intValue()) {
				createrUpdateEntity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(inputForm.getTaskSeq(), taskEntity.getCreatedBy());
				createrUpdateEntity.setEntrustFlg(SystemFlg.FLG_ON.getCd());
				createrUpdateEntity.setNewHistoryKakuninFlg(SystemFlg.FLG_OFF.getCd());
				// タスクステータスが完了以外で表示順が空なら、表示順に最大値+1をセットする
				if (!TaskStatus.COMPLETED.equalsByCode(inputForm.getTaskStatus()) && createrUpdateEntity.getDispOrder() == null) {
					createrUpdateEntity.setDispOrder(tTaskWorkerDao.selectMaxDispOrderByAccountSeq(taskEntity.getCreatedBy()) + 1);
				}
				updateCreaterEntityFlg = true;
			}
		}

		try {
			// 作成者だけ更新処理
			if (updateCreaterEntityFlg) {
				tTaskWorkerDao.update(createrUpdateEntity);
			}
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// 作業者を削除します。
		if (!outWorker.isEmpty()) {
			deleteTaskWorker(inputForm.getTaskSeq(), outWorker);
		}
		// 作業者を追加します
		if (!addWorker.isEmpty()) {
			insertTaskWorker(inputForm.getTaskSeq(), inputForm.getTaskStatus(), addWorker, false, inputForm.isTodayTaskFlg());
		}
	}
}
