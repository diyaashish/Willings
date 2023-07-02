package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class FeeDetailListBean {

	/** 報酬SEQ */
	private Long feeSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 項目 */
	private String feeItemName;

	/** 発生日 */
	private LocalDate feeDate;

	/** 報酬ステータス */
	private String feePaymentStatus;

	/** 課税フラグ */
	private String taxFlg;

	/** 消費税率 */
	private String taxRateType;

	/** 源泉徴収フラグ */
	private String withholdingFlg;

	/** 報酬額 */
	private BigDecimal feeAmount;

	/** 消費税金額 */
	private BigDecimal taxAmount;

	/** 源泉徴収額 */
	private BigDecimal withholdingAmount;

	/** 報酬額（税込） */
	private BigDecimal feeAmountTaxIn;

	/** 源泉徴収税引後 */
	private BigDecimal afterWithholdingTax;

	/** 請求書SEQ */
	private Long invoiceSeq;

	/** 請求書番号 */
	private String invoiceNo;

	/** 入金ステータス */
	private String invoicePaymentStatus;

	/** 精算書SEQ */
	private Long statementSeq;

	/** 精算書番号 */
	private String statementNo;

	/** 返金ステータス */
	private String statementRefundStatus;

	/** タイムチャージフラグ */
	private String feeTimeChargeFlg;

	/** タイムチャージ 単価（1h） */
	private BigDecimal hourPrice;

	/** タイムチャージ 時間 */
	private Long workTimeMinute;

	/** メモ */
	private String feeMemo;

	/** 回収不能フラグ */
	private String uncollectibleFlg;

	/** 摘要 */
	private String sumText;

	/** 登録時間 */
	private LocalDateTime createdAt;

	/** 登録者seq */
	private Long createdBy;

	/** 更新時間 */
	private LocalDateTime updatedAt;

	/** 更新者seq */
	private Long updatedBy;

}
