package jp.loioz.app.user.recordDetail.dto;

import java.math.BigDecimal;

import jp.loioz.common.constant.CommonConstant.AccgDocType;
import lombok.Data;

/**
 * 取引実績画面：ヘッダー内サマリ
 */
@Data
public class RecordSummaryDto {

	/** 取引実績SEQ */
	private Long accgRecordSeq;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 会計書類種別 */
	private AccgDocType accgDocType;

	/** 入金額 */
	private String totalPayAmount;

	/** 残金 */
	private String totalBalance;

	/** 取引実績サマリー：請求書情報 */
	private RecordInvoiceSummaryDto invoiceSummary;

	/** 取引実績サマリー：精算書情報 */
	private RecordStatementSummaryDto statementSummary;


	/** 報酬入金額見込 */
	private BigDecimal feeAmountExpect;

	/** 預り金入金額見込 */
	private BigDecimal depositRecvAmountExpect;

	/** 報酬入金額 */
	private BigDecimal recordFeeAmount;

	/** 預り金入金額 */
	private BigDecimal recordDepositRecvAmount;

	/** 過入金額 */
	private BigDecimal overPaymentAmount;

	/** 報酬残金 */
	private BigDecimal feeBalance;

	/** 預り金残金 */
	private BigDecimal depositRecvBalance;

}
