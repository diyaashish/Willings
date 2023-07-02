package jp.loioz.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class TaskListBean {

	/** レコード件数 */
	@Column(name = "count")
	private Integer count;

	// タスクテーブル(t_task)
	/** タスクSEQ */
	@Column(name = "task_seq")
	private Long taskSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 件名 */
	@Column(name = "title")
	private String title;

	/** 内容 */
	@Column(name = "content")
	private String content;

	/** 期限To */
	@Column(name = "limit_dt_to")
	private LocalDateTime limitDtTo;

	/** 期限終日フラグ */
	@Column(name = "all_day_flg")
	private String allDayFlg;

	/** タスクステータス */
	@Column(name = "task_status")
	private String taskStatus;

	/** 作成者ID */
	@Column(name = "created_by")
	private Long createdBy;

	// タスク-作業者(t_task_worker)
	/** タスク-作業者SEQ */
	@Column(name = "task_worker_seq")
	private Long taskWorkerSeq;

	/** タスク-作成者SEQ */
	@Column(name = "creater_account_seq")
	private Long createrAccountSeq;

	/** 作業者(アカウントSEQ) ※CONCAT */
	@Column(name = "worker_account_seq")
	private String workerAccountSeq;

	/** 作成者フラグ */
	@Column(name = "creater_flg")
	private String createrFlg;

	/** 委任フラグ */
	@Column(name = "entrust_flg")
	private String entrustFlg;

	/** 新着タスク確認フラグ */
	@Column(name = "new_task_kakunin_flg")
	private String newTaskKakuninFlg;

	/** 新着履歴確認フラグ */
	@Column(name = "new_history_kakunin_flg")
	private String newHistoryKakuninFlg;

	/** 表示順 */
	@Column(name = "disp_order")
	private Integer dispOrder;

	/** 今日のタスク表示順 */
	@Column(name = "today_task_disp_order")
	private Integer todayTaskDispOrder;

	/** 今日のタスク日付 */
	@Column(name = "today_task_date")
	private LocalDate todayTaskDate;

	/** 割り当てたアカウント(アカウントSEQ) */
	@Column(name = "assigned_account_seq")
	private Long assignedAccountSeq;

	/** 割り当てられた担当者(アカウントSEQ) ※CONCAT */
	@Column(name = "assign_account_seq")
	private String assignAccountSeq;

	// タスク-履歴
	/** コメント件数 */
	@Column(name = "comment_count")
	private Long commentCount;

	/** 作成者-姓 */
	@Column(name = "account_name_sei")
	private String accountNameSei;

	/** 作成者-名 */
	@Column(name = "account_name_mei")
	private String accountNameMei;

	// タスク-チェックアイテム
	/** チェックアイテム件数 */
	@Column(name = "check_item_count")
	private Long checkItemCount;

	/** 終了したチェックアイテム件数 */
	@Column(name = "complete_check_item_count")
	private Long completeCheckItemCount;

}
