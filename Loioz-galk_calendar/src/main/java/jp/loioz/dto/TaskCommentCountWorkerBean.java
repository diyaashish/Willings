package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

/**
 * タスクのコメント数、作業者SEQ
 *
 */
@Entity
@Data
public class TaskCommentCountWorkerBean {

	/** タスクSEQ */
	@Column(name = "task_seq")
	private Long taskSeq;

	/** 作業者(アカウントSEQ) ※CONCAT */
	@Column(name = "worker_account_seq")
	private String workerAccountSeq;

	/** コメント件数 */
	@Column(name = "comment_count")
	private Long commentCount;

	/** チェックアイテム件数 */
	@Column(name = "check_item_count")
	private Long checkItemCount;

	/** 終了したチェックアイテム件数 */
	@Column(name = "complete_check_item_count")
	private Long completeCheckItemCount;
}
