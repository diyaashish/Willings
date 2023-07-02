package jp.loioz.app.user.statementList.dto;

import java.time.LocalDate;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * 精算書一覧表示用Dto
 */
@Data
public class StatementListDto {

	/** 精算SEQ */
	private Long statementSeq;

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

	/** 発行ステータスCD */
	private String statementIssueStatus;

	/** 発行ステータス（表示用） */
	private String statementIssueStatusName;

	/** 精算日 */
	private LocalDate statementDate;

	/** 精算番号 */
	private String statementNo;

	/** 期日 */
	private LocalDate refundDate;

	/** 精算額 */
	private String statementAmount;

	/** 精算状況CD */
	private String statementRefundStatus;

	/** 精算状況（表示用） */
	private String statementRefundStatusName;

	/** 担当弁護士 */
	private String tantoLaywerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** メモ */
	private String statementMemo;

	/** 取引実績SEQ */
	private Long accgRecordSeq;

	/**
	 * -区切りに編集した精算日を取得します。
	 * 
	 * @return
	 */
	public String getStatementDateFormat() {
		return DateUtils.parseToString(this.statementDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * -区切りに編集した返金期日を取得します。
	 * 
	 * @return
	 */
	public String getRefundDateFormat() {
		return DateUtils.parseToString(this.refundDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * 返金期日を超えているか判定する
	 * 
	 * @return
	 */
	public boolean isOverRefund() {
		return DateUtils.isCorrectDate(LocalDate.now(), this.refundDate);
	}

}
