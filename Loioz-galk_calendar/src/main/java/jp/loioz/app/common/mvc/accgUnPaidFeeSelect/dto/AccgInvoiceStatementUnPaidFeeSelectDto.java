package jp.loioz.app.common.mvc.accgUnPaidFeeSelect.dto;

import lombok.Data;

/**
 * 未精算報酬選択モーダル表示用Dto
 */
@Data
public class AccgInvoiceStatementUnPaidFeeSelectDto {

	/** 報酬SEQ */
	private Long feeSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 項目 */
	private String feeItemName;

	/** 発生日 */
	private String feeDate;

	/** 報酬ステータス */
	private String feePaymentStatus;

	/** 報酬額 */
	private String feeAmount;

	/** 消費税金額 */
	private String taxAmount;

	/** 源泉徴収額 */
	private String withholdingAmount;

	/** 報酬額（報酬（税抜）+ 消費税 - 源泉徴収税） */
	private String afterWithholdingTax;

	/** 報酬額（報酬（税抜）+ 消費税） */
	private String feeAmountTaxIn;

	/** 請求書番号 */
	private String invoiceNo;

	/** 入金ステータス */
	private String invoicePaymentStatus;

	/** 精算書番号 */
	private String statementNo;

	/** 返金ステータス */
	private String statementRefundStatus;

	/** メモ */
	private String feeMemo;

	/** 摘要 */
	private String sumText;

	/** 選択チェックボックス */
	private boolean isChecked;

	/** タイムチャージフラグ */
	private boolean isTimeCharge;

	/** タイムチャージ 単価（1h） */
	private String hourPrice;

	/** タイムチャージ 時間 */
	private Long workTimeMinute;

}
