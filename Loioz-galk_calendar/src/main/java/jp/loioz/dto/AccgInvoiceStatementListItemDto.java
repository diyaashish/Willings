package jp.loioz.dto;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import jp.loioz.common.constant.CommonConstant.InvoiceType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * 会計管理画面用Dto
 */
@Data
public class AccgInvoiceStatementListItemDto {

	/** 会計書類種別 */
	private AccgDocType accgDocType;

	/** 請求／精算SEQ */
	private Long invoiceStatementSeq;

	/** 請求番号／精算番号 */
	private String invoiceStatementNo;

	/** 発行ステータス */
	private IssueStatus issueStatus;

	/** 請求方法 */
	private InvoiceType invoiceType;

	/** 入金ステータス */
	private InvoicePaymentStatus invoicePaymentStatus;

	/** 出金ステータス */
	private StatementRefundStatus statementRefundStatus;

	/** 請求日／精算日 */
	private LocalDate invoceStatementDate;

	/** 支払期日（請求／精算） */
	private LocalDate invoceStatementDueDate;

	/** 請求金額 / 精算金額 */
	private String accgAmount;

	/** 請求 / 精算メモ */
	private String invoiceStatementMemo;

	/** 取引実績SEQ */
	private Long accgRecordSeq;

	/**
	 * -区切りに編集した日付を取得します。
	 * 
	 * @return
	 */
	public String getInvoiceStatementeDateFormat() {
		return DateUtils.parseToString(this.invoceStatementDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * -区切りに編集した日付を取得します。
	 * 
	 * @return
	 */
	public String getInvoceStatementDueDateFormat() {
		return DateUtils.parseToString(this.invoceStatementDueDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * 支払期日を超えているか判定する
	 * 
	 * @return
	 */
	public boolean isOverdue() {
		return DateUtils.isCorrectDate(LocalDate.now(), this.invoceStatementDueDate);
	}

	/**
	 * 請求書か判定する
	 * 
	 * @return
	 */
	public boolean isInvoice() {
		return AccgDocType.INVOICE.equals(this.accgDocType);
	}

}
