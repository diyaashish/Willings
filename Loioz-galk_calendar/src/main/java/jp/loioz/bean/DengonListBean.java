package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class DengonListBean {

	/** 伝言SEQ */
	@Column(name = "dengon_seq")
	private Long dengonSeq;

	/** 業務履歴 業務履歴seq */
	@Column(name = "gyomu_history_seq")
	private Long gyomuHistorySeq;

	/** 業務履歴 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 業務履歴 裁判枝番 */
	@Column(name = "saiban_branch_no")
	private Long saibanBranchNo;

	/** タイトル */
	@Column(name = "title")
	private String title;

	/** 送信者名 */
	@Column(name = "send_account_name")
	private String sendAccountName;

	/**
	 * 受信者SEQリスト DB上ではアカウントSEQがカンマ区切りで保存されている
	 */
	@Column(name = "receive_account_seq_list")
	private String receiveAccountSeqList;

	/** 伝言ステータスID */
	@Column(name = "dengon_status_id")
	private String dengonStatusId;

	/** 下書きフラグ */
	@Column(name = "draft_flg")
	private Boolean draftFlg;

	/** 既読フラグ */
	@Column(name = "open_flg")
	private Boolean openFlg;

	/** 重要フラグ */
	@Column(name = "important_flg")
	private Boolean importantFlg;

	/** 作成日時 */
	@Column(name = "created_at")
	private String createdAt;

}