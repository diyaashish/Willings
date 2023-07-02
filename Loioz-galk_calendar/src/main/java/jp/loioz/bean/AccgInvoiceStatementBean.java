package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 会計画面：請求書/精算書一覧用Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AccgInvoiceStatementBean {

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 会計書類種別 */
	private String accgDocType;

	/**************************
	 * 請求
	 **************************/

	/** 請求書SEQ */
	private Long invoiceSeq;

	/** 請求書発行ステータス */
	private String invoiceIssueStatus;

	/** 請求書入金ステータス */
	private String invoicePaymentStatus;

	/** 請求番号 */
	private String invoiceNo;

	/** 請求日 */
	private LocalDate invoiceDate;

	/** 支払期日 */
	private LocalDate dueDate;

	/** 請求金額 */
	private BigDecimal invoiceAmount;

	/** 請求方法 */
	private String invoiceType;

	/** メモ */
	private String invoiceMemo;

	/**************************
	 * 精算
	 **************************/

	/** 精算書SEQ */
	private Long statementSeq;

	/** 精算発行ステータス */
	private String statementIssueStatus;

	/** 精算返金ステータス */
	private String statementRefundStatus;

	/** 精算番号 */
	private String statementNo;

	/** 精算金額 */
	private BigDecimal statementAmount;

	/** 精算日 */
	private LocalDate statementDate;

	/** 返済期限 */
	private LocalDate refundDate;

	/** メモ */
	private String statementMemo;

	/**************************
	 * 取引実績
	 **************************/

	/** 取引実績SEQ */
	private Long accgRecordSeq;

}
