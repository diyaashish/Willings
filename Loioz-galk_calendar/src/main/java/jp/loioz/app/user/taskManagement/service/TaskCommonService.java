package jp.loioz.app.user.taskManagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenSelectListDto;
import jp.loioz.app.user.taskManagement.dto.TaskCheckItemDto;
import jp.loioz.app.user.taskManagement.form.CommentForm;
import jp.loioz.app.user.taskManagement.form.TaskCheckItemForm;
import jp.loioz.app.user.taskManagement.form.list.TaskListInputForm.TaskTanto;
import jp.loioz.bean.AnkenCustomerGroupByAnkenBean;
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
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TTaskAnkenDao;
import jp.loioz.dao.TTaskCheckItemDao;
import jp.loioz.dao.TTaskDao;
import jp.loioz.dao.TTaskHistoryDao;
import jp.loioz.dao.TTaskWorkerDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.ActivityHistoryDto;
import jp.loioz.dto.TaskCommentCountWorkerBean;
import jp.loioz.dto.TaskHistoryDto;
import jp.loioz.dto.TaskListBean;
import jp.loioz.dto.TaskListDto;
import jp.loioz.dto.TaskListForTaskManagementDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TTaskAnkenEntity;
import jp.loioz.entity.TTaskCheckItemEntity;
import jp.loioz.entity.TTaskEntity;
import jp.loioz.entity.TTaskHistoryEntity;
import jp.loioz.entity.TTaskWorkerEntity;

/**
 * タスク共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskCommonService extends DefaultService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 共通アカウントサービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** タスク-作業者Daoクラス */
	@Autowired
	private TTaskWorkerDao tTaskWorkerDao;

	/** タスクのDaoクラス */
	@Autowired
	private TTaskDao tTaskDao;

	/** タスク-履歴Daoクラス */
	@Autowired
	private TTaskHistoryDao tTaskHistoryDao;

	/** タスクチェックアイテムDaoクラス */
	@Autowired
	private TTaskCheckItemDao tTaskCheckItemDao;

	/** 案件タスクDaoクラス */
	@Autowired
	private TTaskAnkenDao tTaskAnkenDao;

	/** 案件-顧客用Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/**
	 * タスクを割り当てられたアカウントごとにまとめます
	 * 
	 * @param taskDtoList
	 * @return
	 */
	public List<TaskListForTaskManagementDto> organizeAssignedTasks(List<TaskListDto> taskDtoList) {

		// 割り当て元のアカウントSEQを取得
		List<Long> assignedAccountList = taskDtoList.stream().sorted(Comparator.comparing(TaskListDto::getAssignedAccountSeq)).map(TaskListDto::getAssignedAccountSeq).distinct().collect(Collectors.toList());

		// 割り当て元のアカウントごとにセット
		List<TaskListForTaskManagementDto> taskList = new ArrayList<>();
		assignedAccountList.stream().forEach(assignedAccountSeq -> {

			MAccountEntity mAccountEntity = mAccountDao.selectBySeq(assignedAccountSeq);

			// 完了以外タスク（未着手、進行中）
			TaskListForTaskManagementDto taskDto = new TaskListForTaskManagementDto();
			taskDto.setTaskAssignSeq(mAccountEntity.getAccountSeq());
			taskDto.setTaskAssignName(PersonName.fromEntity(mAccountEntity).getName());
			taskDto.setTaskTypeTitle(CommonConstant.TASK_STATUS_INCOMPLETE_NAME);
			taskDto.setTaskList(taskDtoList.stream()
					.filter(dto -> dto.getAssignedAccountSeq().equals(assignedAccountSeq))
					.collect(Collectors.toList()));

			// 追加
			taskList.add(taskDto);
		});

		return taskList;
	}

	/**
	 * タスクを割り当てたアカウントごとにまとめます
	 * 
	 * @param taskDtoList
	 * @return
	 */
	public List<TaskListForTaskManagementDto> organizeAssignTasks(List<TaskListDto> taskDtoList) {

		// 割り当て先のアカウントSEQを取得
		Set<String> assignAccount = new HashSet<String>();
		for (TaskListDto dto : taskDtoList) {
			assignAccount.addAll(dto.getAssignAccountSeqList());
		}

		// 有効なアカウントのSeqリスト
		List<MAccountEntity> enabledAccountEntityList = mAccountDao.selectEnabledAccount();
		Set<String> enabledAccountSeqSet = enabledAccountEntityList.stream()
				.map(e -> String.valueOf(e.getAccountSeq()))
				.collect(Collectors.toSet());
		// 自分自身のSeqは除外する（割り当てたタスク画面には、自分は表示しないため）
		enabledAccountSeqSet.remove(String.valueOf(SessionUtils.getLoginAccountSeq()));

		// 追加
		assignAccount.addAll(enabledAccountSeqSet);

		// 割り当て先のアカウントごとにセット
		List<TaskListForTaskManagementDto> taskList = new ArrayList<>();
		assignAccount.stream().forEach(assignAccountSeq -> {

			MAccountEntity mAccountEntity = mAccountDao.selectBySeq(Long.valueOf(assignAccountSeq));

			// 完了以外タスク（未着手、進行中）
			TaskListForTaskManagementDto taskDto = new TaskListForTaskManagementDto();
			taskDto.setTaskAssignSeq(mAccountEntity.getAccountSeq());
			taskDto.setTaskAssignName(PersonName.fromEntity(mAccountEntity).getName());
			taskDto.setTaskTypeTitle(CommonConstant.TASK_STATUS_INCOMPLETE_NAME);
			taskDto.setTaskList(taskDtoList.stream()
					.filter(dto -> dto.getAssignAccountSeqList().contains(assignAccountSeq))
					.collect(Collectors.toList()));

			// 追加
			taskList.add(taskDto);
		});

		return taskList;
	}

	/**
	 * 期限日の文字スタイルを取得します。<br>
	 * 期限日が過ぎていたら文字色がtext-danger返します。
	 * 
	 * @param dto
	 * @return
	 */
	public String getLimitDateStyle(TaskListDto dto) {

		final String overTaskStyle = "text-danger";

		// 完了したタスク
		if (Objects.equals(dto.getTaskStatus(), TaskStatus.COMPLETED)) {
			return dto.getTaskStatus().getStyle();
		}

		// 期日を超えていた場合
		if (Objects.nonNull(dto.getLimitDtTo())) {
			if (Objects.nonNull(dto.getLimitTime()) && dto.getLimitDtTo().isBefore(LocalDateTime.now())) {
				return overTaskStyle;
			} else if (Objects.isNull(dto.getLimitTime()) && dto.getLimitDateForDisplay().isBefore(LocalDate.now()))
				return overTaskStyle;
		}

		// ステータスによるカードの背景を設定
		return dto.getTaskStatus().getStyle();
	}

	/**
	 * カレンダー画面で表示するタスク一覧を取得します<br>
	 * 取得するタスクは「すべてのタスク」 + 「割り当てられたタスク」 です。<br>
	 * isAllRelatedTask が true で  「割り当てたタスク」が追加されます。<br>
	 * ソートキーで「設定順」を選択すると「すべてのタスク（表示順でソート）」が先頭になり、その次に「割り当てたタスク」、「割り当てられたタスク」で取得されます。<br>
	 * （割り当てたタスク、割り当てられたタスクに「表示順」が設定されていないため）<br>
	 *
	 * @param sortKeyCd 1:設定順 、2:期限日-昇順、3:期限日-降順
	 * @param isAllRelatedTask
	 * @return
	 */
	public List<TaskListDto> getTaskListBySchedule(String sortKeyCd, boolean isAllRelatedTask) {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectRelatedTaskByAccountId(SessionUtils.getLoginAccountSeq(), sortKeyCd, isAllRelatedTask);

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return convertBeanListForDtoList(taskBeanList);
	}

	/**
	 * 今日のタスクリストを取得します
	 * 
	 * @return
	 */
	public List<TaskListBean> getTodayTaskList() {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectTodayTaskByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * 期限付きのタスクリストを取得します
	 * 
	 * @return
	 */
	public List<TaskListBean> getFutureTaskList() {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectFutureTaskByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now().minusDays(1));

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * すべてのタスクリストを取得します<br>
	 * パラメータのsortKeyCdで並び順を指定します
	 * 
	 * @param sortKeyCd 1:設定順 、2:期限日-昇順、3:期限日-降順
	 * @return
	 */
	public List<TaskListBean> getAllTaskList(String sortKeyCd) {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectAllTaskByAccountId(SessionUtils.getLoginAccountSeq(), sortKeyCd);

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * 期限を過ぎたタスクリストを取得します
	 * 
	 * @return
	 */
	public List<TaskListBean> getOverdueTaskList() {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectOverdueTaskByAccountId(SessionUtils.getLoginAccountSeq(), LocalDate.now());

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * 割り当てられたタスクリストを取得します
	 * 
	 * @return
	 */
	public List<TaskListBean> getAssignedTaskList() {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectAssignedTaskByAccountId(SessionUtils.getLoginAccountSeq());

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * 割り当てたタスクリストを取得します
	 * 
	 * @return
	 */
	public List<TaskListBean> getAssignTaskList() {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectAssignTaskByAccountId(SessionUtils.getLoginAccountSeq());

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * 完了したタスクリストを取得します
	 * 
	 * @param sortKeyCd 1:完了順 、2:期限日-昇順、3:期限日-降順
	 * @return
	 */
	public List<TaskListBean> getCloseTaskList(String sortKeyCd) {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectCloseTaskByAccountId(SessionUtils.getLoginAccountSeq(), sortKeyCd);

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * 検索ワードに紐づくタスクデータを取得します
	 * 
	 * @param searchWord
	 * @return
	 */
	public List<TaskListBean> getSearchResultsTaskList(String searchWord) {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectSearchResultsTaskByAccountIdAndSearchWord(SessionUtils.getLoginAccountSeq(), searchWord);

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * 案件タスクのタスクリストを取得します<br>
	 * パラメータのsortKeyCdで並び順を指定します
	 * 
	 * @param ankenId
	 * @param dispKeyCd
	 * @param sortKeyCd 1:登録順 or 完了順、2:期限日-昇順、3:期限日-降順
	 * @return
	 */
	public List<TaskListBean> getTaskAnkenList(Long ankenId, String dispKeyCd, String sortKeyCd) {
		// タスクデータ取得
		List<TaskListBean> taskBeanList = tTaskDao.selectTaskAnkenByAnkenId(ankenId, dispKeyCd, sortKeyCd);

		// タスクSEQに関するコメント数、作業者を取得
		setTaskCommentCountWorker(taskBeanList);

		return taskBeanList;
	}

	/**
	 * タスクに関するコメント数と作業者情報をセットする
	 * 
	 * @param taskBeanList
	 */
	public void setTaskCommentCountWorker(List<TaskListBean> taskBeanList) {
		if (LoiozCollectionUtils.isEmpty(taskBeanList)) {
			return;
		}

		List<Long> taskSeqList = taskBeanList.stream().map(bean -> bean.getTaskSeq()).collect(Collectors.toList());
		List<TaskCommentCountWorkerBean> TaskCommentCountWorkerBeanList = tTaskDao.selectTaskCommentCountAndWorkerByTaskSeq(taskSeqList);
		for (TaskListBean taskListBean : taskBeanList) {
			Optional<TaskCommentCountWorkerBean> optBean = TaskCommentCountWorkerBeanList.stream().filter(taskCommentCountWorkerBean -> taskCommentCountWorkerBean.getTaskSeq().equals(taskListBean.getTaskSeq())).findFirst();
			if (optBean.isPresent()) {
				taskListBean.setCommentCount(optBean.get().getCommentCount());
				taskListBean.setWorkerAccountSeq(optBean.get().getWorkerAccountSeq());
				taskListBean.setCheckItemCount(optBean.get().getCheckItemCount());
				taskListBean.setCompleteCheckItemCount(optBean.get().getCompleteCheckItemCount());
			}
		}
	}

	/**
	 * 担当リストのフォーム情報を作成する
	 *
	 * @param accountSeqList タスク担当者SEQリスト
	 * @param acountEntityList 全アカウントリスト
	 * @return 担当フォーム情報
	 */
	public List<TaskTanto> createTantoListWithAccountSeqList(List<Long> accountSeqList, List<MAccountEntity> acountEntityList) {

		// DB取得値をフォーム情報に変換
		List<TaskTanto> itemList = accountSeqList.stream()
				.map(accountSeq -> TaskTanto.builder()
						.workerAccountSeq(accountSeq)
						.build())
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
	 * 作業者の取得
	 *
	 * @param accountList
	 * @param accountSeqList
	 * @return 作業者一覧
	 */
	public List<SelectOptionForm> setWorker(List<MAccountEntity> accountList, List<Long> accountSeqList) {

		// 変換さえたリストを元に作業者Form(List)を作成
		List<SelectOptionForm> worker = accountSeqList.stream().map(accountSeq -> {
			return new SelectOptionForm(accountSeq, getAccountName(accountList, accountSeq));
		}).collect(Collectors.toList());

		return worker;
	}

	/**
	 * アカウント名の取得
	 *
	 * @param accountList
	 * @param accountSeq
	 * @return 名前
	 */
	public String getAccountName(List<MAccountEntity> accountList, Long accountSeq) {
		// 引数をキーとして、アカウント名を取得する
		return accountList.stream()
				.filter(entity -> entity.getAccountSeq().equals(accountSeq))
				.map(entity -> PersonName.fromEntity(entity).getName())
				.collect(Collectors.toList()).get(0);
	}

	/**
	 * タスク詳細のURLを、正規表現を使用し、 リンクタグ（<a href=...）に変換し<br>
	 * 文字列内の改行コードをbrタグに変換する。
	 *
	 * @param str 指定の文字列。
	 * @return リンクに変換された文字列。
	 */
	public String convTaskContentURLLink(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		String escapeStr = HtmlUtils.htmlEscape(str);
		Matcher matcher = CommonConstant.CONV_URL_LINK_PTN.matcher(escapeStr);
		String text = matcher.replaceAll("<a target=\"_blank\" href=\"$0\" rel=\"noopener\" class=\"taskDetailContentLink\">$0</a>");
		return text.replaceAll("\\r\\n|\\r|\\n", "<br>");
	}

	/**
	 * タスクコメント内のURLを、正規表現を使用し、 リンクタグ（<a href=...）に変換し<br>
	 * 文字列内の改行コードをbrタグに変換する。
	 *
	 * @param str 指定の文字列。
	 * @return リンクに変換された文字列。
	 */
	public String convTaskCommentURLLink(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		Matcher matcher = CommonConstant.CONV_URL_LINK_PTN.matcher(str);
		String text = matcher.replaceAll("<a target=\"_blank\" href=\"$0\" rel=\"noopener\" class=\"comment_link\">$0</a>");
		return text.replaceAll("\\r\\n|\\r|\\n", "<br>");
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
	public void historyKauninFlgUpdateToFlgOff(Long taskSeq) throws AppException {

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
	 * タスク-履歴(コメント)の登録処理
	 *
	 * @param taskSeq
	 * @param comment
	 * @throws AppException
	 */
	public void insertTaskComment(CommentForm commentForm) throws AppException {

		TTaskHistoryEntity entity = new TTaskHistoryEntity();

		TTaskEntity tTaskEntity = tTaskDao.selectBySeq(commentForm.getTaskSeq());
		if (tTaskEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力されたコメントを登録
		entity.setTaskSeq(commentForm.getTaskSeq());
		entity.setTaskHistoryType(TaskHistoryType.COMMENT.getCd());
		entity.setComment(commentForm.getComment());

		try {
			// 登録処理
			tTaskHistoryDao.insert(entity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// 登録したタスク-履歴のSEQをコメントFormにセットする
		commentForm.setTaskHistorySeq(entity.getTaskHistorySeq());

		// 履歴確認フラグの更新処理
		historyKauninFlgUpdateToFlgOff(commentForm.getTaskSeq());
	}

	/**
	 * タスク画面の案件データリストの取得
	 *
	 * <pre>
	 * 案件のデータリストを作成します。<br>
	 * 対象案件：アカウントSEQが担当する案件、検索ワードが案件名、顧客名に該当する案件<br>
	 * 並び順：顧客ステータスが進行中、精算待ち、完了待ち、相談中、完了、不受任<br>
	 * 取得結果がなければnullを返します。<br>
	 *
	 * データリストの値 = 案件名（案-XX）　顧客名, 顧客名, 顧客名・・・
	 * </pre>
	 * 
	 * @param searchWord 検索ワード
	 * @param accountSeq アカウントSEQ
	 * @return
	 */
	public List<TaskAnkenSelectListDto> searchAnkenList(String searchWord) throws AppException {

		List<AnkenCustomerGroupByAnkenBean> ankenCustomerList = tAnkenCustomerDao.selectCustomerNameAnkenNameGroupByAnken(StringUtils.removeSpaceCharacter(searchWord), SessionUtils.getLoginAccountSeq());
		// データが取得できない場合は空リストを返却
		if (LoiozCollectionUtils.isEmpty(ankenCustomerList)) {
			return Collections.emptyList();
		}

		// １人でも未完了（完了、不受任以外）のステータスの顧客がいる案件 かつ 自分が担当の案件
		List<AnkenCustomerGroupByAnkenBean> incompleteAnkenInChargeList = ankenCustomerList.stream()
				.filter(bean -> CommonConstant.AnkenStatus.SODAN.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.SHINKOCHU.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.MENDAN_YOTEI.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.SEISAN_MACHI.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.KANRYO_MACHI.equalsByCode(bean.getAnkenStatus()))
				.filter(bean -> CommonConstant.SystemFlg.FLG_ON.equalsByCode(bean.getTantoAnken()))
				.collect(Collectors.toList());
		// １人でも未完了（完了、不受任以外）のステータスの顧客がいる案件 かつ 自分が担当ではない案件
		List<AnkenCustomerGroupByAnkenBean> incompleteAnkenNotInChargeList = ankenCustomerList.stream()
				.filter(bean -> CommonConstant.AnkenStatus.SODAN.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.SHINKOCHU.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.MENDAN_YOTEI.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.SEISAN_MACHI.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.KANRYO_MACHI.equalsByCode(bean.getAnkenStatus()))
				.filter(bean -> CommonConstant.SystemFlg.FLG_OFF.equalsByCode(bean.getTantoAnken()))
				.collect(Collectors.toList());
		// 全顧客が完了か不受任のステータスの顧客がいる案件 かつ 自分が担当の案件
		List<AnkenCustomerGroupByAnkenBean> completedAnkenInChargeList = ankenCustomerList.stream()
				.filter(bean -> CommonConstant.AnkenStatus.KANRYO.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.FUJUNIN.equalsByCode(bean.getAnkenStatus()))
				.filter(bean -> CommonConstant.SystemFlg.FLG_ON.equalsByCode(bean.getTantoAnken()))
				.collect(Collectors.toList());
		// 全顧客が完了か不受任のステータスの顧客がいる案件 かつ 自分が担当ではない案件
		List<AnkenCustomerGroupByAnkenBean> completedAnkenNotInChargeList = ankenCustomerList.stream()
				.filter(bean -> CommonConstant.AnkenStatus.KANRYO.equalsByCode(bean.getAnkenStatus())
						|| CommonConstant.AnkenStatus.FUJUNIN.equalsByCode(bean.getAnkenStatus()))
				.filter(bean -> CommonConstant.SystemFlg.FLG_OFF.equalsByCode(bean.getTantoAnken()))
				.collect(Collectors.toList());

		List<AnkenCustomerGroupByAnkenBean> ankenDataList = new ArrayList<AnkenCustomerGroupByAnkenBean>();
		ankenDataList.addAll(incompleteAnkenInChargeList);
		ankenDataList.addAll(incompleteAnkenNotInChargeList);
		ankenDataList.addAll(completedAnkenInChargeList);
		ankenDataList.addAll(completedAnkenNotInChargeList);

		List<TaskAnkenSelectListDto> taskAnkenSelectListDtoList = ankenDataList.stream()
				.map(entity -> {
					TaskAnkenSelectListDto dto = new TaskAnkenSelectListDto();
					dto.setAnkenId(AnkenId.of(entity.getAnkenId()));
					dto.setAnkenName(entity.getAnkenName());
					dto.setCustomerName(entity.getCustomerName());
					dto.setCustomerNameKana(entity.getCustomerNameKana());
					return dto;
				}).collect(Collectors.toList());
		return taskAnkenSelectListDtoList;
	}

	/**
	 * チェックアイテムの新規登録処理
	 * 
	 * @param taskSeq
	 * @param checkItemName
	 * @param completeFlg
	 * @return 登録後のチェックアイテム情報
	 * @throws AppException
	 */
	public TaskCheckItemDto saveTaskCheckItem(Long taskSeq, String checkItemName, String completeFlg) throws AppException {

		// タスク-チェックアイテムの登録処理
		TTaskCheckItemEntity tTaskCheckItemEntity = new TTaskCheckItemEntity();
		tTaskCheckItemEntity.setTaskSeq(taskSeq);
		tTaskCheckItemEntity.setItemName(checkItemName);
		tTaskCheckItemEntity.setCompleteFlg(completeFlg);

		// タスクSEQに関するサブタスクの最大表示順を取得し1加算して表示順をセット
		long maxDispOrder = tTaskCheckItemDao.selectMaxCheckItemDispOrderByTaskSeq(taskSeq);
		tTaskCheckItemEntity.setDispOrder(maxDispOrder + 1);

		try {
			tTaskCheckItemDao.insert(tTaskCheckItemEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// タスク-履歴の登録処理
		saveTaskHistoryForCheckItem(taskSeq, TaskCheckItemUpdateKbn.REGIST.getCd(), checkItemName);

		// コメント確認フラグの更新処理
		historyKauninFlgUpdateToFlgOff(taskSeq);

		return convertTaskCheckItemDtoForTaskCheckItemEntity(tTaskCheckItemEntity);
	}

	/**
	 * 複数のチェックアイテム新規登録処理
	 * 
	 * @param taskSeq
	 * @param checkList
	 * @throws AppException
	 */
	public void saveTaskCheckItemList(Long taskSeq, List<TaskCheckItemDto> checkList) throws AppException {

		if (LoiozCollectionUtils.isEmpty(checkList)) {
			return;
		}

		// チェックアイテム登録用entityリスト
		List<TTaskCheckItemEntity> tTaskCheckItemEntityList = new ArrayList<TTaskCheckItemEntity>();

		// データ登録用entity作成
		TTaskCheckItemEntity tTaskCheckItemEntity;
		for (TaskCheckItemDto dto : checkList) {
			// チェックアイテム登録entity
			tTaskCheckItemEntity = new TTaskCheckItemEntity();
			tTaskCheckItemEntity.setTaskSeq(taskSeq);
			tTaskCheckItemEntity.setItemName(dto.getItemName());
			tTaskCheckItemEntity.setCompleteFlg(dto.getCompleteFlg() ? CheckItemStatus.COMPLETED.getCd() : CheckItemStatus.INCOMPLETE.getCd());

			// タスクSEQに関するサブタスクの最大表示順を取得し1加算して表示順をセット
			long maxDispOrder = tTaskCheckItemDao.selectMaxCheckItemDispOrderByTaskSeq(taskSeq);
			tTaskCheckItemEntity.setDispOrder(maxDispOrder + 1);
			tTaskCheckItemEntityList.add(tTaskCheckItemEntity);
		}

		// チェックアイテムの登録処理
		try {
			tTaskCheckItemDao.insert(tTaskCheckItemEntityList);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * チェック項目名の更新処理
	 *
	 * @param checkItemForm
	 * @throws AppException
	 */
	public void updateCheckItemName(TaskCheckItemForm checkItemForm) throws AppException {

		TTaskCheckItemEntity tTaskCheckItemEntity = tTaskCheckItemDao.selectBySeq(checkItemForm.getTaskCheckItemSeq());
		if (tTaskCheckItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 変更内容をセット
		tTaskCheckItemEntity.setItemName(checkItemForm.getCheckItemName());

		try {
			// チェック項目名の更新
			tTaskCheckItemDao.update(tTaskCheckItemEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// タスク-履歴の登録処理
		saveTaskHistoryForCheckItem(checkItemForm.getTaskSeq(), TaskCheckItemUpdateKbn.UPDATE.getCd(), checkItemForm.getCheckItemName());

		// コメント確認フラグの更新処理
		historyKauninFlgUpdateToFlgOff(checkItemForm.getTaskSeq());
	}

	/**
	 * サブタスク1行分のデータを取得する
	 * 
	 * @param taskCheckItemSeq
	 * @return
	 * @throws AppException
	 */
	public TaskCheckItemDto getCheckItem(Long taskCheckItemSeq) throws AppException {
		TTaskCheckItemEntity tTaskCheckItemEntity = tTaskCheckItemDao.selectBySeq(taskCheckItemSeq);
		if (tTaskCheckItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		return convertTaskCheckItemDtoForTaskCheckItemEntity(tTaskCheckItemEntity);
	}

	/**
	 * サブタスクに関する変更をタスク-履歴テーブルに登録する
	 * 
	 * @param taskSeq
	 * @param taskCheckItemUpdateKbnCd
	 * @param checkItemName
	 * @throws AppException
	 */
	public void saveTaskHistoryForCheckItem(Long taskSeq, String taskCheckItemUpdateKbnCd, String checkItemName) throws AppException {

		TTaskHistoryEntity tTaskHistoryEntity = new TTaskHistoryEntity();
		tTaskHistoryEntity.setTaskSeq(taskSeq);
		tTaskHistoryEntity.setTaskHistoryType(TaskHistoryType.HISTORY.getCd());
		tTaskHistoryEntity.setTitleUpdateFlg(SystemFlg.FLG_OFF.getCd());
		tTaskHistoryEntity.setContentUpdateFlg(SystemFlg.FLG_OFF.getCd());
		tTaskHistoryEntity.setWorkerUpdateFlg(SystemFlg.FLG_OFF.getCd());
		tTaskHistoryEntity.setLimitDtUpdateFlg(SystemFlg.FLG_OFF.getCd());
		tTaskHistoryEntity.setStatusUpdateFlg(SystemFlg.FLG_OFF.getCd());
		tTaskHistoryEntity.setCheckItemUpdateFlg(SystemFlg.FLG_ON.getCd());
		tTaskHistoryEntity.setCheckItemUpdateKbn(taskCheckItemUpdateKbnCd);
		tTaskHistoryEntity.setCheckItemName(checkItemName);
		tTaskHistoryEntity.setAnkenReletedUpdateFlg(SystemFlg.FLG_OFF.getCd());

		try {
			tTaskHistoryDao.insert(tTaskHistoryEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}

	/**
	 * サブタスクに関する変更をタスク-履歴テーブルに登録する
	 * 
	 * @param taskSeq
	 * @param checkList
	 * @param taskCheckItemUpdateKbnCd
	 * @throws AppException
	 */
	public void saveTaskHistoryForCheckItemList(Long taskSeq, List<TaskCheckItemDto> checkList, String taskCheckItemUpdateKbnCd) throws AppException {

		if (LoiozCollectionUtils.isEmpty(checkList)) {
			return;
		}

		// タスク-履歴データ登録用entityリスト
		List<TTaskHistoryEntity> tTaskHistoryEntityList = new ArrayList<TTaskHistoryEntity>();

		// データ登録用entity作成
		TTaskHistoryEntity tTaskHistoryEntity;
		for (TaskCheckItemDto dto : checkList) {
			tTaskHistoryEntity = new TTaskHistoryEntity();
			tTaskHistoryEntity.setTaskSeq(taskSeq);
			tTaskHistoryEntity.setTaskHistoryType(TaskHistoryType.HISTORY.getCd());
			tTaskHistoryEntity.setTitleUpdateFlg(SystemFlg.FLG_OFF.getCd());
			tTaskHistoryEntity.setContentUpdateFlg(SystemFlg.FLG_OFF.getCd());
			tTaskHistoryEntity.setWorkerUpdateFlg(SystemFlg.FLG_OFF.getCd());
			tTaskHistoryEntity.setLimitDtUpdateFlg(SystemFlg.FLG_OFF.getCd());
			tTaskHistoryEntity.setStatusUpdateFlg(SystemFlg.FLG_OFF.getCd());
			tTaskHistoryEntity.setCheckItemUpdateFlg(SystemFlg.FLG_ON.getCd());
			tTaskHistoryEntity.setCheckItemUpdateKbn(taskCheckItemUpdateKbnCd);
			tTaskHistoryEntity.setCheckItemName(dto.getItemName());
			tTaskHistoryEntity.setAnkenReletedUpdateFlg(SystemFlg.FLG_OFF.getCd());
			tTaskHistoryEntityList.add(tTaskHistoryEntity);
		}

		try {
			tTaskHistoryDao.insert(tTaskHistoryEntityList);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}

	/**
	 * チェック項目ステータスの更新処理
	 *
	 * @param checkItemForm
	 * @throws AppException
	 */
	public void updateCheckItemStatus(TaskCheckItemForm checkItemForm) throws AppException {

		TTaskCheckItemEntity tTaskCheckItemEntity = tTaskCheckItemDao.selectBySeq(checkItemForm.getTaskCheckItemSeq());
		if (tTaskCheckItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 変更内容をセット
		tTaskCheckItemEntity.setCompleteFlg(checkItemForm.getNewCompleteFlg());

		try {
			// チェック項目ステータスの更新
			tTaskCheckItemDao.update(tTaskCheckItemEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// タスク-履歴の登録処理
		if (SystemFlg.FLG_OFF.equalsByCode(checkItemForm.getNewCompleteFlg())) {
			// チェック項目のステータスを未完了にした履歴を作成
			saveTaskHistoryForCheckItem(checkItemForm.getTaskSeq(), TaskCheckItemUpdateKbn.IN_COMPLETE.getCd(), checkItemForm.getCheckItemName());
		} else {
			// チェック項目のステータスを完了にした履歴を作成
			saveTaskHistoryForCheckItem(checkItemForm.getTaskSeq(), TaskCheckItemUpdateKbn.COMPLETE.getCd(), checkItemForm.getCheckItemName());
		}

		// コメント確認フラグの更新処理
		historyKauninFlgUpdateToFlgOff(checkItemForm.getTaskSeq());
	}

	/**
	 * サブタスクの表示順変更処理<br>
	 * 
	 * @param taskWorkerSeqListStr 並び替え後のタスクWorkerSeq
	 * @throws AppException
	 */
	public void changeCheckItemDispOrder(String checkItemSeqListStr) throws AppException {
		String[] checkItemSeqStringArray = checkItemSeqListStr.split(",");
		long checkItemDispOrder = 0;

		// サブタスクのデータを並び替え後の状態で順次表示順をセットしていく
		for (int i = 0; i < checkItemSeqStringArray.length; i++) {

			// checkItemSeqの取得
			Long checkItemSeq = Long.parseLong(checkItemSeqStringArray[i]);

			// 変更するentityを取得
			TTaskCheckItemEntity tTaskCheckItemEntity = tTaskCheckItemDao.selectBySeq(checkItemSeq);

			// 既にデータがなければ排他エラーを呼ぶ
			if (tTaskCheckItemEntity == null) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 表示順を再設定
			tTaskCheckItemEntity.setDispOrder(checkItemDispOrder++);

			try {
				tTaskCheckItemDao.update(tTaskCheckItemEntity);
			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());

				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
		}

	}

	/**
	 * チェック項目の削除処理
	 *
	 * @param checkItemForm
	 * @throws AppException
	 */
	public void deleteCheckItem(TaskCheckItemForm checkItemForm) throws AppException {

		TTaskCheckItemEntity tTaskCheckItemEntity = tTaskCheckItemDao.selectBySeq(checkItemForm.getTaskCheckItemSeq());
		if (tTaskCheckItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// チェック項目ステータスの更新
			tTaskCheckItemDao.delete(tTaskCheckItemEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, e);
		}

		// タスク-履歴の登録処理
		saveTaskHistoryForCheckItem(checkItemForm.getTaskSeq(), TaskCheckItemUpdateKbn.DELETE.getCd(), checkItemForm.getCheckItemName());

		// コメント確認フラグの更新処理
		historyKauninFlgUpdateToFlgOff(checkItemForm.getTaskSeq());
	}

	/**
	 * アクティビティ情報を取得する
	 * 
	 * @param taskSeq
	 * @return
	 */
	public List<ActivityHistoryDto> getActivity(Long taskSeq) {

		// アカウント取得すべて
		List<MAccountEntity> accountListAll = mAccountDao.selectAll();

		// アカウント名のMapを抽出
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap(accountListAll);

		// タスク履歴情報を取得
		List<TTaskHistoryEntity> taskAllHistoryList = tTaskHistoryDao.selectByTaskSeqList(List.of(taskSeq));
		// DTOに変換
		List<TaskHistoryDto> taskAllHistoryDtoList = convertTaskHistoryDtoForTaskHistoryEntity(taskAllHistoryList, accountNameMap);

		// アクティビティ履歴を抽出（コメント以外）
		List<TaskHistoryDto> activityDtoList = taskAllHistoryDtoList.stream()
				.filter(dto -> !CommonConstant.TaskHistoryType.COMMENT.equalsByCode(dto.getTaskHistoryType().getCd())).collect(Collectors.toList());
		return convertTaskHistoryDtoForAcitivityHistoryDto(activityDtoList);
	}

	/**
	 * タスクの削除処理を行います。
	 *
	 * @param form
	 * @throws AppException
	 */
	public void deleteTask(Long taskSeq) throws AppException {

		TTaskEntity taskEntity = tTaskDao.selectBySeq(taskSeq);
		if (taskEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐づくテーブルも削除する
		List<TTaskWorkerEntity> taskWorkerEntityList = tTaskWorkerDao.selectByParentSeq(taskEntity.getTaskSeq());
		List<TTaskHistoryEntity> taskHistoryEntityList = tTaskHistoryDao.selectByParentSeq(taskEntity.getTaskSeq());
		List<TTaskCheckItemEntity> taskCheckItemEntityList = tTaskCheckItemDao.selectByTaskSeq(taskEntity.getTaskSeq());

		try {
			// タスクの削除
			tTaskDao.delete(taskEntity);
			// タスク-作業者の削除
			Optional.ofNullable(taskWorkerEntityList).ifPresent(tTaskWorkerDao::delete);
			// タスク-履歴の削除
			Optional.ofNullable(taskHistoryEntityList).ifPresent(tTaskHistoryDao::delete);
			// タスク-チェックアイテムの削除
			Optional.ofNullable(taskCheckItemEntityList).ifPresent(tTaskCheckItemDao::batchDelete);

		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}

	/**
	 * タスクSEQをキーとして、タスクのステータスを更新します。
	 *
	 * @param taskSeq
	 * @param taskStatus
	 */
	public void updateTaskStatus(Long taskSeq, String taskStatus) throws AppException {

		// 更新するタスク情報を取得します。
		TTaskEntity taskEntity = tTaskDao.selectBySeq(taskSeq);
		if (taskEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 更新前のステータスを取得します
		String beforeTaskStatus = taskEntity.getTaskStatus();

		// ステータスを更新します。
		taskEntity.setTaskStatus(taskStatus);

		// 更新履歴を作成します
		Map<TaskUpdateItem, String> updateItem = new HashMap<>();
		Arrays.asList(TaskUpdateItem.values()).forEach(item -> {
			if (item.equals(TaskUpdateItem.STATUS)) {
				updateItem.put(item, SystemFlg.FLG_ON.getCd());
			} else if (item.equals(TaskUpdateItem.UPDATED_STATUS)) {
				updateItem.put(item, taskStatus);
			} else {
				updateItem.put(item, SystemFlg.FLG_OFF.getCd());
			}
		});

		try {
			// 更新処理
			tTaskDao.update(taskEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// タスク履歴登録処理
		insertHistory(taskSeq, updateItem, false);

		// タスク担当者更新処理
		updateTaskWorker(taskSeq, taskStatus, beforeTaskStatus);
	}

	/**
	 * SEQをキーとして、操作しているユーザに関するタスク情報のみ取得・設定します。
	 *
	 * @param taskSeq
	 */
	public TaskListDto getTaskDataRelatedToAccount(Long taskSeq) throws AppException {

		// アカウント情報の取得
		List<MAccountEntity> accountList = mAccountDao.selectAll();

		// タスク情報の取得
		TaskListBean taskBean = tTaskDao.selectBeanBySeqAccountId(taskSeq, SessionUtils.getLoginAccountSeq());
		if (taskBean == null) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 取得したデータをセット
		return convertBeanForDto(accountList, taskBean);
	}

	/**
	 * タスク-履歴の登録処理
	 *
	 * @param taskSeq
	 * @param updateItem
	 * @param isNewRegistTask
	 * @throws AppException
	 */
	public void insertHistory(Long taskSeq, Map<TaskUpdateItem, String> updateItem, boolean isNewRegistTask) throws AppException {

		TTaskHistoryEntity entity = new TTaskHistoryEntity();

		// 排他チェック 親データの存在するか
		TTaskEntity tTaskEntity = tTaskDao.selectBySeq(taskSeq);
		if (tTaskEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 値を設定
		entity.setTaskSeq(taskSeq);

		if (isNewRegistTask) {
			entity.setTaskHistoryType(TaskHistoryType.CREATE.getCd());
		} else {
			entity.setTaskHistoryType(TaskHistoryType.HISTORY.getCd());
			entity.setTitleUpdateFlg(updateItem.get(TaskUpdateItem.TITLE));
			entity.setContentUpdateFlg(updateItem.get(TaskUpdateItem.CONTENT));
			entity.setWorkerUpdateFlg(updateItem.get(TaskUpdateItem.WORKER));
			entity.setLimitDtUpdateFlg(updateItem.get(TaskUpdateItem.LIMIT_DATE));
			entity.setStatusUpdateFlg(updateItem.get(TaskUpdateItem.STATUS));
			if (SystemFlg.codeToBoolean(updateItem.get(TaskUpdateItem.STATUS))) {
				entity.setUpdatedStatus(updateItem.get(TaskUpdateItem.UPDATED_STATUS));
			}
			entity.setAnkenReletedUpdateFlg(updateItem.get(TaskUpdateItem.ANKEN));
			if (SystemFlg.codeToBoolean(updateItem.get(TaskUpdateItem.ANKEN))) {
				entity.setAnkenReletedUpdateKbn(updateItem.get(TaskUpdateItem.UPDATED_ANKEN_KBN));
			}
		}

		try {
			// 登録件数の初期化
			tTaskHistoryDao.insert(entity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// コメント確認フラグの更新処理
		historyKauninFlgUpdateToFlgOff(taskSeq);
	}

	/**
	 * パラメーターのステータスに合わせて、タスクの割り当ての表示順、タスクの確認フラグを変更する
	 * 
	 * @param taskSeq タスクSEQ
	 * @param taskStatus 変更後のタスクステータス
	 * @param beforeTaskStatus 変更前のタスクステータス
	 * @throws AppException
	 */
	public void updateTaskWorker(Long taskSeq, String taskStatus, String beforeTaskStatus) throws AppException {

		// タスクの表示順変更
		changeTaskWorkerDispOrder(taskSeq, taskStatus);

		// タスクの確認フラグ変更
		changeTaskWorkerKakuninFlg(taskSeq, taskStatus, beforeTaskStatus);
	}

	/**
	 * タスク-チェックアイテムの変換 Entity → Dto
	 * 
	 * @param tTaskCheckItemEntityList
	 * @return
	 */
	public List<TaskCheckItemDto> convertTaskCheckItemDtoForTaskCheckItemEntity(List<TTaskCheckItemEntity> tTaskCheckItemEntityList) {

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
	 * タスク-チェックアイテムの変換 Entity → Dto
	 * 
	 * @param tTaskCheckItemEntity
	 * @return
	 */
	public TaskCheckItemDto convertTaskCheckItemDtoForTaskCheckItemEntity(TTaskCheckItemEntity entity) {

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
	}

	/**
	 * タスク-履歴の変換 Entity → Dto
	 *
	 * @param taskHistoryEntity
	 * @param accountNameMap
	 * @return
	 */
	public List<TaskHistoryDto> convertTaskHistoryDtoForTaskHistoryEntity(List<TTaskHistoryEntity> taskHistoryEntity, Map<Long, String> accountNameMap) {

		List<TaskHistoryDto> list = taskHistoryEntity.stream()
				.map(entity -> {
					TaskHistoryDto dto = new TaskHistoryDto();
					dto.setTaskHistorySeq(entity.getTaskHistorySeq());
					dto.setTaskSeq(entity.getTaskSeq());
					// コメント文のURLをリンクコードへ変換する
					dto.setCommentForDisplay((convTaskCommentURLLink(entity.getComment())));
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
						dto.setCheckItemUpdateFlg(SystemFlg.of(entity.getCheckItemUpdateFlg()));
						if (SystemFlg.codeToBoolean(entity.getCheckItemUpdateFlg())) {
							dto.setCheckItemUpdateKbn(TaskCheckItemUpdateKbn.of(entity.getCheckItemUpdateKbn()));
							dto.setCheckItemName(entity.getCheckItemName());
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
	 * タスク-履歴を画面表示用に変換
	 *
	 * @param taskHistoryEntity
	 * @param accountList
	 * @return 変換したDto
	 */
	public List<ActivityHistoryDto> convertTaskHistoryDtoForAcitivityHistoryDto(List<TaskHistoryDto> taskHistoryDto) {

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// 作成日降順で並び替え
		List<TaskHistoryDto> sortedTaskHistoryList = taskHistoryDto.stream()
				.sorted(Comparator.comparing(TaskHistoryDto::getCreatedAt).reversed()).collect(Collectors.toList());
		List<ActivityHistoryDto> list = new ArrayList<>();
		for (TaskHistoryDto dto : sortedTaskHistoryList) {
			// タスク作成履歴
			if (dto.getTaskHistoryType() != null && CommonConstant.TaskHistoryType.CREATE.equalsByCode(dto.getTaskHistoryType().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TASK_ACTIVITY_TASK_CREATE_ITEM_NAME);
				newDto.setAction(CommonConstant.TASK_ACTIVITY_CREATE_ACTION);
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				list.add(newDto);
			}

			// タイトル更新
			if (dto.getTitleUpdateFlg() != null && CommonConstant.SystemFlg.FLG_ON.equalsByCode(dto.getTitleUpdateFlg().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TaskUpdateItem.TITLE.getVal());
				newDto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				list.add(newDto);
			}

			// 詳細更新
			if (dto.getContentUpdateFlg() != null && CommonConstant.SystemFlg.FLG_ON.equalsByCode(dto.getContentUpdateFlg().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TaskUpdateItem.CONTENT.getVal());
				newDto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				list.add(newDto);
			}

			// 担当更新
			if (dto.getWorkerUpdateFlg() != null && CommonConstant.SystemFlg.FLG_ON.equalsByCode(dto.getWorkerUpdateFlg().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TaskUpdateItem.WORKER.getVal());
				newDto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				list.add(newDto);
			}

			// 期限更新
			if (dto.getLimitDtUpdateFlg() != null && CommonConstant.SystemFlg.FLG_ON.equalsByCode(dto.getLimitDtUpdateFlg().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TaskUpdateItem.LIMIT_DATE.getVal());
				newDto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				list.add(newDto);
			}

			// 進捗
			if (dto.getStatusUpdateFlg() != null && CommonConstant.SystemFlg.FLG_ON.equalsByCode(dto.getStatusUpdateFlg().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TaskUpdateItem.STATUS.getVal());
				newDto.setTaskStatus(CommonConstant.TaskStatus.of(dto.getUpdatedStatus().getCd()).getVal());
				newDto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				list.add(newDto);
			}

			// 案件設定
			if (dto.getAnkenReletedUpdateFlg() != null && CommonConstant.SystemFlg.FLG_ON.equalsByCode(dto.getAnkenReletedUpdateFlg().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TaskUpdateItem.ANKEN.getVal());
				newDto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				list.add(newDto);
			}

			// サブタスク
			if (dto.getCheckItemUpdateFlg() != null && CommonConstant.SystemFlg.FLG_ON.equalsByCode(dto.getCheckItemUpdateFlg().getCd())) {
				ActivityHistoryDto newDto = new ActivityHistoryDto();
				newDto.setItemName(CommonConstant.TaskUpdateItem.CHECK_ITEM.getVal());
				newDto.setAccountName(accountNameMap.get(dto.getCreatedBy()));
				newDto.setCheckItemName(dto.getCheckItemName());
				newDto.setCheckItemUpdateKbn(dto.getCheckItemUpdateKbn().getCd());
				// アクション
				if (TaskCheckItemUpdateKbn.REGIST.equals(dto.getCheckItemUpdateKbn())) {
					newDto.setAction(CommonConstant.TASK_ACTIVITY_CREATE_ACTION);
				} else if (TaskCheckItemUpdateKbn.DELETE.equals(dto.getCheckItemUpdateKbn())) {
					newDto.setAction(CommonConstant.TASK_ACTIVITY_DELETE_ACTION);
				} else {
					newDto.setAction(CommonConstant.TASK_ACTIVITY_UPDATE_ACTION);
				}
				list.add(newDto);
			}

			// 更新時間
			ActivityHistoryDto newDto = new ActivityHistoryDto();
			newDto.setCreatedAt(dto.getCreatedAt());
			list.add(newDto);

		}

		return list;
	}

	/**
	 * List<Bean>型からList<Dto>型に変換します
	 *
	 * @param taskBeanList
	 * @return taskDtoList
	 */
	public List<TaskListDto> convertBeanListForDtoList(List<TaskListBean> taskBeanList) {

		// アカウント情報の取得 ※無効・削除済みを含む
		List<MAccountEntity> accountList = mAccountDao.selectAll();

		// Bean型をDto型に変換する
		List<TaskListDto> taskDtoList = new ArrayList<TaskListDto>();
		taskBeanList.forEach(bean -> {
			taskDtoList.add(convertBeanForDto(accountList, bean));
		});

		return taskDtoList;
	}

	/**
	 * Bean型からDto型に変換します<br>
	 * ※担当者の一覧情報を含む
	 *
	 * @param accountListAll
	 * @param taskBean
	 * @return taskDto
	 */
	public TaskListDto convertBeanForDto(List<MAccountEntity> accountListAll, TaskListBean taskBean) {

		TaskListDto taskDto = this.convertBeanForDto(taskBean);

		// 担当者
		taskDto.setTaskWorkerSeq(taskBean.getTaskWorkerSeq());
		if (taskBean.getWorkerAccountSeq() != null) {
			// 文字列結合されたSEQをList型に変換
			List<Long> accountSeqList = StringUtils.toLongArray(taskBean.getWorkerAccountSeq());
			taskDto.setWorkerAccountList(setWorker(accountListAll, accountSeqList));
			taskDto.setAssignTaskTantoList(createTantoListWithAccountSeqList(accountSeqList, accountListAll));
		}

		return taskDto;
	}

	/**
	 * Bean型からDto型に変換します<br>
	 * ※担当者の一覧情報は含まない
	 * 
	 * @param taskBean
	 * @return
	 */
	public TaskListDto convertBeanForDto(TaskListBean taskBean) {

		TaskListDto taskDto = new TaskListDto();

		// DB取得データの設定
		taskDto.setTaskSeq(taskBean.getTaskSeq());
		taskDto.setAnkenId(AnkenId.of(taskBean.getAnkenId()));
		// 案件名 + 顧客名のセット取得し設定する
		taskDto.setAnkenNameAndCustomerName(getAnkenNameAndCustomerName(taskBean.getAnkenId()));
		taskDto.setAnkenName(AnkenName.of(taskBean.getAnkenName(), taskBean.getBunyaId()));
		taskDto.setTitle(taskBean.getTitle());
		taskDto.setContent(taskBean.getContent());
		// 詳細文が長い場合は短く編集した詳細文もセットする
		if (!canAllContentDisplay(taskBean.getContent())) {
			// 詳細文のURLをリンクコードへ変換する
			taskDto.setShortContentForDisplay((convTaskContentURLLink(makeShortContent(taskBean.getContent()))));
		}
		// 詳細文のURLをリンクコードへ変換する
		taskDto.setAllContentForDisplay((convTaskContentURLLink(taskBean.getContent())));
		taskDto.setLimitDtTo(taskBean.getLimitDtTo());
		if (taskBean.getLimitDtTo() != null) {
			taskDto.setLimitDateForDisplay(taskBean.getLimitDtTo().toLocalDate());
			taskDto.setLimitDate(localDateToString(taskBean.getLimitDtTo().toLocalDate()));
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
		taskDto.setCheckItemCount(taskBean.getCheckItemCount());
		taskDto.setCompleteCheckItemCount(taskBean.getCompleteCheckItemCount());
		if (taskBean.getAssignAccountSeq() != null) {
			taskDto.setAssignAccountSeqList(StringUtils.toArray(taskBean.getAssignAccountSeq()));
		}
		taskDto.setAssignedAccountSeq(taskBean.getAssignedAccountSeq());

		// 作成者名
		String createDispName = new PersonName(
				taskBean.getAccountNameSei(), taskBean.getAccountNameMei(),
				null, null).getName();
		taskDto.setCreateName(createDispName);

		// 期限日の文字色指定
		taskDto.setLimitDateStyle(getLimitDateStyle(taskDto));

		// サブタスク件数セット
		taskDto.setCheckItemCount(taskBean.getCheckItemCount());
		taskDto.setCompleteCheckItemCount(taskBean.getCompleteCheckItemCount());

		return taskDto;
	}

	/**
	 * 案件IDに該当する案件名、顧客名を取得
	 * 
	 * @param ankenId
	 * @return 案件名（案-XX） 顧客名 , 顧客名, 顧客名・・・
	 */
	public String getAnkenNameAndCustomerName(Long ankenId) {
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
	 * 案件タスク処理時のDBアクセスValidation
	 * 
	 * @param ankenId
	 */
	public void accessDBValidatedForTaskAnken(Long ankenId) throws AppException {
		List<TTaskAnkenEntity> taskAnkenList = tTaskAnkenDao.selectTaskAnkenByAccountSeqAnkenId(SessionUtils.getLoginAccountSeq(), ankenId);
		// 選択した「案件タスク」の案件が存在しない場合、楽観ロックエラー
		if (LoiozCollectionUtils.isEmpty(taskAnkenList)) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * タスクSEQが「すべてのタスク」で表示できるタスクか判定する
	 * 
	 * @param taskSeq
	 * @param accountSeq
	 * @return true：表示可能 false：表示不可
	 */
	public boolean isAllTask(Long taskSeq, Long accountSeq) {
		List<Long> allTaskSeq = tTaskDao.selectAllTaskSeqByAccountId(accountSeq, null);
		if (allTaskSeq.contains(taskSeq)) {
			return true;
		}
		return false;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * タスク詳細文が全て表示できる長さか確認します。
	 * 
	 * @param content
	 * @return
	 */
	private boolean canAllContentDisplay(String content) {
		if (StringUtils.isEmpty(content)) {
			return true;
		}
		if (CommonConstant.INIT_SHOW_MAX_CHAR_COUNT_TASK_DETAIL_CONTENT >= content.length()) {
			return true;
		}
		return false;
	}

	/**
	 * タスク詳細文のショートVerを作成します。
	 * 
	 * @param content
	 * @return
	 */
	private String makeShortContent(String content) {
		if (StringUtils.isEmpty(content)) {
			return "";
		}

		// 短い文に編集した詳細文
		String shortContent = content;

		// 詳細文を初期表示最大文字数まで切り出す
		if (CommonConstant.INIT_SHOW_MAX_CHAR_COUNT_TASK_DETAIL_CONTENT < shortContent.length()) {
			shortContent = shortContent.substring(0, CommonConstant.INIT_SHOW_MAX_CHAR_COUNT_TASK_DETAIL_CONTENT);
		}

		return shortContent;
	}

	/**
	 * タスク担当者ごとにタスク表示順を変更します
	 * 
	 * @param taskSeq タスクSEQ
	 * @param taskStatus 変更後のタスクステータス
	 * @throws AppException
	 */
	private void changeTaskWorkerDispOrder(Long taskSeq, String taskStatus) throws AppException {

		// 更新するタスク情報を取得します。
		TaskListBean taskListBean = tTaskDao.selectBeanBySeq(SessionUtils.getLoginAccountSeq(), taskSeq);
		if (taskListBean == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 作業者のアカウントSEQを取得
		List<String> acoountSeqList;
		if (taskListBean.getWorkerAccountSeq() != null) {
			// 作業者全員分のアカウントSEQがカンマ区切りになっているので、カンマごとに分割してリストで保持する
			acoountSeqList = StringUtils.toArray(taskListBean.getWorkerAccountSeq());
		} else {
			// 作業者が設定されていない場合は、タスク作成者を作業者とする
			acoountSeqList = List.of(String.valueOf(taskListBean.getCreaterAccountSeq()));
		}

		// 作業者ごとにタスクの表示順を更新。タスクステータスを完了へ変更したときは作業者全員分の表示順を空へ変更、完了から未完了状態へ変更したときは作業者ごとに表示順を最大値+1をセットするため。
		for (String accountSeq : acoountSeqList) {
			try {
				if (TaskStatus.COMPLETED.equalsByCode(taskStatus)) {
					// ステータスが完了したものは、並び替え不要なので表示順を空にする
					TTaskWorkerEntity entity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(taskSeq, Long.valueOf(accountSeq));
					entity.setTodayTaskDispOrder(null);
					entity.setDispOrder(null);
					tTaskWorkerDao.update(entity);
				} else if (!TaskStatus.COMPLETED.equalsByCode(taskStatus)) {
					// 未着手・進行中は、「今日のタスク」画面、「すべてのタスク」画面の両方で並び替え可能なので完了からの変更の場合は表示順に最大値をセットする
					TTaskWorkerEntity entity = tTaskWorkerDao.selectByParentSeqAndAccountSeq(taskSeq, Long.valueOf(accountSeq));
					if (entity.getTodayTaskDate() != null && entity.getTodayTaskDispOrder() == null) {
						// 今日のタスクとして作成していたものには、今日のタスク表示順をセットする。todayTaskDateが当日であれば「今日のタスク」画面で表示され、異なれば表示されない。
						entity.setTodayTaskDispOrder(tTaskWorkerDao.selectMaxTodayTaskDispOrderByAccountSeq(Long.valueOf(accountSeq)) + 1);
					}
					if (this.isAllTask(taskSeq, Long.valueOf(accountSeq)) && entity.getDispOrder() == null) {
						// すべてのタスクとして作成していたものには、表示順をセットする。
						entity.setDispOrder(tTaskWorkerDao.selectMaxDispOrderByAccountSeq(Long.valueOf(accountSeq)) + 1);
					}
					tTaskWorkerDao.update(entity);
				}
			} catch (Exception e) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + e.getMessage());
				throw new AppException(MessageEnum.MSG_E00013, e);
			}
		}
	}

	/**
	 * タスク担当者ごとにタスク確認フラグを変更する<br>
	 * 
	 * @param taskSeq タスクSEQ
	 * @param taskStatus 変更前のタスクステータス
	 * @param beforeTaskStatus 変更後のタスクステータス
	 */
	private void changeTaskWorkerKakuninFlg(Long taskSeq, String taskStatus, String beforeTaskStatus) throws AppException {

		// タスク完了時、ログインユーザーが担当者であれば既読にする
		if (TaskStatus.COMPLETED.equalsByCode(taskStatus)) {
			List<TTaskWorkerEntity> tTaskWorkerEntityList = tTaskWorkerDao.selectByParentSeq(taskSeq);
			if (!LoiozCollectionUtils.isEmpty(tTaskWorkerEntityList)) {
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

					// タスクを既読に設定
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
		}

		// タスクステータスを完了から未着手・進行中へ戻すときは、変更者以外の担当者は未読、変更者は既読にする
		if (TaskStatus.COMPLETED.equalsByCode(beforeTaskStatus) && !TaskStatus.COMPLETED.equalsByCode(taskStatus)) {
			List<TTaskWorkerEntity> updateEntity = tTaskWorkerDao.selectByParentSeq(taskSeq);
			if (!LoiozCollectionUtils.isEmpty(updateEntity)) {
				for (TTaskWorkerEntity entity : updateEntity) {
					// 変更者以外の担当者を更新
					if (SessionUtils.getLoginAccountSeq().equals(entity.getWorkerAccountSeq())) {
						// タスクを既読に設定
						entity.setNewTaskKakuninFlg(SystemFlg.FLG_ON.getCd());
						entity.setNewHistoryKakuninFlg(SystemFlg.FLG_ON.getCd());
					} else {
						// タスクを未読に設定
						entity.setNewTaskKakuninFlg(SystemFlg.FLG_OFF.getCd());
						entity.setNewHistoryKakuninFlg(SystemFlg.FLG_OFF.getCd());
					}
					try {
						// 更新処理
						tTaskWorkerDao.update(entity);
					} catch (OptimisticLockingFailureException e) {
						String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
						logger.warn(classAndMethodName + e.getMessage());
						throw new AppException(MessageEnum.MSG_E00013, e);
					}
				}
			}
		}
	}

	/**
	 * LocalDateをStringに変換
	 *
	 * @param localDate
	 * @return
	 */
	private String localDateToString(LocalDate localDate) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return localDate.format(dateTimeFormatter);
	}

}