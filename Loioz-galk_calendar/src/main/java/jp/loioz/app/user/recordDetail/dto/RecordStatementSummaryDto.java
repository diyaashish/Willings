package jp.loioz.app.user.recordDetail.dto;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import lombok.Data;

/**
 * 取引実績：精算書サマリーDto
 */
@Data
public class RecordStatementSummaryDto {

	/** 精算書SEQ */
	private Long statementSeq;

	/** 精算書ステータス */
	private StatementRefundStatus statementRefundStatus;

	/** 精算金額 */
	private String statementAmount;

	/** 精算番号 */
	private String statementNo;

	/** 精算日付 */
	private LocalDate statementDate;

	/** 返金期限 */
	private LocalDate refundDate;

}
