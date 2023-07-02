package jp.loioz.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.taskManagement.dto.TaskCheckItemDto;
import jp.loioz.app.user.taskManagement.form.list.TaskListInputForm.TaskTanto;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.TaskCheckItem;
import jp.loioz.common.validation.groups.TaskLimitDate;
import jp.loioz.common.validation.groups.TaskMemo;
import jp.loioz.common.validation.groups.TaskTitle;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import lombok.Data;

@Data
public class TaskListDto {

	// タスクテーブル(t_task)
	/** タスクSEQ */
	private Long taskSeq;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private AnkenName ankenName;

	/** 件名 */
	@Required(groups = TaskTitle.class)
	@MaxDigit(groups = TaskTitle.class, max = 255)
	private String title;

	/** メモ（内容） 表示用（一部） */
	private String shortContentForDisplay;

	/** メモ（内容） 表示用（全文字） */
	private String allContentForDisplay;

	/** メモ（内容） 入力用 */
	@MaxDigit(groups = TaskMemo.class, max = 2000)
	private String content;

	/** チェックアイテム 入力用 */
	@Required(groups = TaskCheckItem.class)
	@MaxDigit(groups = TaskCheckItem.class, max = 100)
	private String checkItem;

	/** サブタスク */
	private List<TaskCheckItemDto> checkList;

	/** 期限To（日時） */
	private LocalDateTime limitDtTo;

	/** 期限To（日） 表示用  */
	private LocalDate limitDateForDisplay;

	/** 期限To(日) 入力用 */
	@LocalDatePattern(groups = TaskLimitDate.class, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String limitDate;

	/** 期限To（時） */
	private LocalTime limitTime;

	/** ステータス */
	private TaskStatus taskStatus;

	// タスク-作業者(t_task_worker)
	/** タスク-作業者SEQ */
	private Long taskWorkerSeq;

	/** 作業者 */
	private List<SelectOptionForm> workerAccountList;

	/** 作成者フラグ */
	private SystemFlg createrFlg;

	/** 作成者名 */
	private String createName;

	/** 委任フラグ */
	private SystemFlg entrustFlg;

	/** 新着タスク確認フラグ */
	private SystemFlg newTaskKakuninFlg;

	/** 更新タスク確認フラグ */
	private SystemFlg newHistoryKakuninFlg;

	/** 表示順 */
	private Integer dispOrder;

	/** 今日のタスク表示順 */
	private Integer todayTaskDispOrder;

	/** 今日のタスク日付 */
	private LocalDate todayTaskDate;

	/** 割り当てたアカウントSEQ */
	private Long assignedAccountSeq;

	/** 割り当てられた担当者SEQリスト */
	private List<String> assignAccountSeqList;

	/** 割り当てられた担当者情報リスト */
	private List<TaskTanto> assignTaskTantoList;

	// タスク-履歴
	/** コメント件数 */
	private Long commentCount;
	
	// 以下、一件一件に対しての表示用プロパティ
	/** 期限To（日）文字のスタイル */
	private String limitDateStyle;

	/** タスク詳細：案件設定用　案件名 + 顧客名 */
	private String ankenNameAndCustomerName;

	/** 案件マスタリスト */
	List<SelectOptionForm> ankenOptionList = new ArrayList<>();

	// タスク-チェックアイテム
	/** チェックアイテム件数 */
	private Long checkItemCount;

	/** 終了したチェックアイテム件数 */
	private Long completeCheckItemCount;

	/**
	 * アカウント名取得
	 * 
	 * @param accountSeq
	 * @return
	 */
	public String getAccountName(Long accountSeq) {
		Map<Long, String> seqToMap = new HashMap<>();

		seqToMap.putAll(this.workerAccountList.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));

		return seqToMap.get(accountSeq);
	}

}
