package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 伝言詳細表示用のDtoクラス
 */
@Entity
@Data
public class DengonDetailBean {

	/** 伝言SEQ */
	@Column(name = "dengon_seq")
	private Long dengonSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	private AnkenId ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 顧客ID */
	@Column(name = "customer_id")
	private CustomerId customerId;

	/** 顧客名 */
	@Column(name = "customer_name")
	private String customerName;

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 裁判枝番 */
	@Column(name = "saiban_branch_no")
	private Long saibanBranchNo;

	/** 業務履歴SEQ */
	@Column(name = "gyomu_history_seq")
	private Long gyomuHistorySeq;

	/** タイトル */
	@Column(name = "title")
	private String title;

	/** 本文 */
	@Column(name = "body")
	private String body;

	/** 送信者名 */
	@Column(name = "send_account_name")
	private String sendAccountName;

	/** 受信者名リスト */
	@Column(name = "receive_account_seq_list")
	private String receiveAccountSeqList;

	/** 下書きフラグ */
	@Column(name = "draft_flg")
	private Boolean draftFlg;

	/** 送信者ごみ箱フラグ */
	@Column(name = "send_trashed_flg")
	private Boolean sendTrashedFlg;

	/** 伝言ステータスID */
	@Column(name = "dengon_status_id")
	private String dengonStatusId;

	/** 重要フラグ */
	@Column(name = "important_flg")
	private Boolean importantFlg;

	/** 既読フラグ */
	@Column(name = "open_flg")
	private Boolean openFlg;

	/** ごみ箱フラグ */
	@Column(name = "trashed_flg")
	private Boolean trashedFlg;

	/** 送信日時 */
	@Column(name = "created_at")
	private String createdAt;

}