package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class StatementListBean {

	/** 会計書類SEQ */
	@Column(name = "accg_doc_seq")
	private Long accgDocSeq;

	/** 精算SEQ */
	@Column(name = "statement_seq")
	private Long statementSeq;

	/** 名簿ID */
	@Column(name = "person_id")
	private Long personId;

	/** 顧客姓 */
	@Column(name = "person_name_sei")
	private String personNameSei;

	/** 顧客名 */
	@Column(name = "person_name_mei")
	private String personNameMei;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 発行ステータス */
	@Column(name = "statement_issue_status")
	private String statementIssueStatus;

	/** 精算日 */
	@Column(name = "statement_date")
	private LocalDate statementDate;

	/** 精算番号 */
	@Column(name = "statement_no")
	private String statementNo;

	/** 返金期日 */
	@Column(name = "refund_date")
	private LocalDate refundDate;

	/** 精算額 */
	@Column(name = "statement_amount")
	private BigDecimal statementAmount;

	/** 精算状況 */
	@Column(name = "statement_refund_status")
	private String statementRefundStatus;

	/** 担当弁護士 */
	@Column(name = "tanto_laywer_name")
	private String tantoLaywerName;

	/** 担当弁護士 */
	@Column(name = "tanto_jimu_name")
	private String tantoJimuName;

	/** メモ */
	@Column(name = "statement_memo")
	private String statementMemo;

	/** 取引実績SEQ */
	@Column(name = "accg_record_seq")
	private Long accgRecordSeq;

}
