package jp.loioz.app.user.taskManagement.form.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.TaskCommentTabComment;
import jp.loioz.common.validation.groups.TaskDetailsTabComment;
import jp.loioz.dto.ActivityHistoryDto;
import jp.loioz.dto.TaskHistoryDto;
import jp.loioz.dto.TaskListDto;
import jp.loioz.dto.TaskListForTaskManagementDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * タスク管理画面のフォームクラス
 */
@Data
public class TaskListInputForm {

	/** タスク一覧情報 */
	private List<TaskListForTaskManagementDto> taskListForTaskManagement = new ArrayList<TaskListForTaskManagementDto>();

	/** タスク詳細情報フォーム */
	private TaskDetailInputForm taskDetailInputForm;

	/** タスクタイトル（新規登録用） */
	@Required
	private String taskTitle;

	/** タスク一覧のソートキーコード 1:設定順 、2:期限日-昇順、3:期限日-降順 */
	private String taskListSortKeyCd;

	/** 完了タスク表示切替 1:未完了タスク、9：完了タスク（案件タスク用） */
	private String taskAnkenListDispKeyCd;

	/**
	 * タスクが存在するかを判定する
	 * 
	 * @return
	 */
	public boolean isExistTask() {
		for (TaskListForTaskManagementDto taskManagementDto : taskListForTaskManagement) {
			if (taskManagementDto.getTaskList().size() > 0) {
				// 1件でも存在すればtrue
				return true;
			}
		}
		return false;
	}

	/**
	 * 割り当てたタスク・割り当てられたタスクの、人や完了の領域のタスクの件数を取得する（その領域を表示するかどうかの判定で利用）<br>
	 * ※人の領域のタスクの件数は、未完了のタスクと完了のタスクの合計数とする
	 * 
	 * @param taskStatusCd
	 * @param index
	 * @return
	 */
	public int getTaskCountForAssignedTask(String taskStatusCd, int index) {

		return taskListForTaskManagement.get(index).getTaskList().size();
	}

	/**
	 * タスク詳細情報フォーム
	 */
	@Data
	public static class TaskDetailInputForm {

		/** タスク詳細情報 */
		@Valid
		private TaskListDto taskDetail;

		/** 担当者リスト */
		private List<SelectOptionForm> tantoOptions = Collections.emptyList();

		/** 担当者 */
		private List<TaskTanto> taskTantoList = new ArrayList<>();

		/** コメント数 */
		private int numberOfComment;

		/** コメント（詳細タブの） */
		@Required(groups = TaskDetailsTabComment.class)
		@MaxDigit(groups = TaskDetailsTabComment.class, max = 2000)
		private String detailsTabComment;

		/** コメント（コメントタブの） */
		@Required(groups = TaskCommentTabComment.class)
		@MaxDigit(groups = TaskCommentTabComment.class, max = 2000)
		private String commentTabComment;

		/** コメント履歴 */
		private List<TaskHistoryDto> commentList;

		/** アクティビティ履歴 */
		private List<ActivityHistoryDto> activityList;

		/** タスクの担当 OR 作成者 フラグ */
		private boolean isTaskTanto;

		/**
		 * アカウント名取得
		 * 
		 * @param accountSeq
		 * @return
		 */
		public String getAccountName(Long accountSeq) {
			Map<Long, String> seqToMap = new HashMap<>();

			seqToMap.putAll(this.tantoOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));

			return seqToMap.get(accountSeq);
		}

	}

	/**
	 * 担当
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TaskTanto {

		/** 担当者 */
		private Long workerAccountSeq;

		/** アカウントカラー */
		private String accountColor;

		/**
		 * 空であるか判定する
		 *
		 * @return 空の場合はtrue
		 */
		public boolean isEmpty() {
			return workerAccountSeq == null;
		}
	}

}
