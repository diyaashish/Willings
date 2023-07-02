package jp.loioz.dto;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class GyomuHistoryListBean {

	/** 業務履歴SEQ */
	@Column(name = "gyomu_history_seq")
	private Long gyomuHistorySeq;

	/** 遷移元種別 */
	@Column(name = "transition_type")
	private String transitionType;

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 件名 */
	@Column(name = "subject")
	private String subject;

	/** 本文 */
	@Column(name = "main_text")
	private String mainText;

	/** 重要フラグ */
	@Column(name = "important_flg")
	private String importantFlg;

	/** 固定フラグ */
	@Column(name = "kotei_flg")
	private String koteiFlg;

	/** 対応日時 */
	@Column(name = "supported_at")
	private LocalDateTime supportedAt;

	/** 伝言送信済みフラグ */
	@Column(name = "dengon_sent_flg")
	private String dengonSentFlg;

	/** 作成者SEQ */
	@Column(name = "created_by")
	private Long createdBy;

	// 裁判情報
	/** 裁判SEQに紐づく枝番 */
	@Column(name = "saiban_branch_no")
	private String saibanBranchNo;

	// 以下、紐づきテーブル
	// 業務履歴-顧客
	/** 顧客ID */
	@Column(name = "customer_id")
	private Long customerId;

	/** 顧客IDに紐づいた顧客名 */
	@Column(name = "customer_name")
	private String customerName;

	/** 顧客IDに紐づいた顧客名 */
	@Column(name = "customer_type")
	private String customerType;

	// 業務履歴-案件
	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 案件IDに紐づく分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 案件IDに紐づく分野ID */
	@Column(name = "account_name")
	private String accountName;
}
