package jp.loioz.app.user.recordDetail.dto;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import lombok.Data;

/**
 * 取引実績：請求書サマリーDto
 */
@Data
public class RecordInvoiceSummaryDto {

	/** 請求書SEQ */
	private Long invoiceSeq;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 請求書ステータス */
	private InvoicePaymentStatus invoicePaymentStatus;

	/** 請求金額 */
	private String invoiceAmount;

	/** 請求方法 */
	private String invoiceTypeName;

	/** 請求番号 */
	private String invoiceNo;

	/** 請求日付 */
	private LocalDate invoiceDate;

	/** 支払期限 */
	private LocalDate dueDate;

	/** 請求合計 */
	private String totalAmount;

	/** 報酬 */
	private String feeAmount;

	/** 実費/預り金合計 */
	private String totalDepositAllAmount;

}
