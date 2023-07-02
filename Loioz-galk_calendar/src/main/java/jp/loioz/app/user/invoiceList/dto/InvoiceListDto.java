package jp.loioz.app.user.invoiceList.dto;

import java.time.LocalDate;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * 請求書一覧表示用Dto
 */
@Data
public class InvoiceListDto {

	/** 請求SEQ */
	private Long invoiceSeq;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 名簿ID */
	private Long personId;

	/** 顧客名 */
	private String name;

	/** 案件ID */
	private Long ankenId;

	/** 案件名 */
	private String ankenName;

	/** 請求方法CD */
	private String invoiceType;

	/** 請求方法（表示用） */
	private String invoiceTypeName;

	/** 発行ステータスCD */
	private String invoiceIssueStatus;

	/** 発行ステータス（表示用） */
	private String invoiceIssueStatusName;

	/** 請求日 */
	private LocalDate invoiceDate;

	/** 請求番号 */
	private String invoiceNo;

	/** 支払期日 */
	private LocalDate dueDate;

	/** 請求額 */
	private String invoiceAmount;

	/** 入金状況CD */
	private String invoicePaymentStatus;

	/** 入金状況（表示用） */
	private String invoicePaymentStatusName;

	/** 担当弁護士 */
	private String tantoLaywerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** メモ */
	private String invoiceMemo;

	/** 取引実績SEQ */
	private Long accgRecordSeq;

	/**
	 * -区切りに編集した請求日を取得します。
	 * 
	 * @return
	 */
	public String getInvoiceDateFormat() {
		return DateUtils.parseToString(this.invoiceDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * -区切りに編集した支払期日を取得します。
	 * 
	 * @return
	 */
	public String getDueDateFormat() {
		return DateUtils.parseToString(this.dueDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * 支払期日を超えているか判定する
	 * 
	 * @return
	 */
	public boolean isOverdue() {
		return DateUtils.isCorrectDate(LocalDate.now(), this.dueDate);
	}

}
