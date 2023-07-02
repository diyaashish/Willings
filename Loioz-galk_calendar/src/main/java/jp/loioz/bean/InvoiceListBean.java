package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class InvoiceListBean {

	/** 会計書類SEQ */
	@Column(name = "accg_doc_seq")
	private Long accgDocSeq;

	/** 請求SEQ */
	@Column(name = "invoice_seq")
	private Long invoiceSeq;

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

	/** 請求方法 */
	@Column(name = "invoice_type")
	private String invoiceType;

	/** 発行ステータス */
	@Column(name = "invoice_issue_status")
	private String invoiceIssueStatus;

	/** 請求日 */
	@Column(name = "invoice_date")
	private LocalDate invoiceDate;

	/** 請求番号 */
	@Column(name = "invoice_no")
	private String invoiceNo;

	/** 支払期日 */
	@Column(name = "due_date")
	private LocalDate dueDate;

	/** 請求額 */
	@Column(name = "invoice_amount")
	private BigDecimal invoiceAmount;

	/** 入金状況 */
	@Column(name = "invoice_payment_status")
	private String invoicePaymentStatus;

	/** 担当弁護士 */
	@Column(name = "tanto_laywer_name")
	private String tantoLaywerName;

	/** 担当弁護士 */
	@Column(name = "tanto_jimu_name")
	private String tantoJimuName;

	/** メモ */
	@Column(name = "invoice_memo")
	private String invoiceMemo;

	/** 取引実績SEQ */
	@Column(name = "accg_record_seq")
	private Long accgRecordSeq;

}
