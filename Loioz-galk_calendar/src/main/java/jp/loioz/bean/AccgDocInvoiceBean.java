package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 請求項目情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AccgDocInvoiceBean {

	//
	// ▼ 請求項目に関するデータ
	//
	
	/** 請求項目SEQ */
	private Long docInvoiceSeq;

	/** 並び順 */
	private Long docInvoiceOrder;
	
	//
	// ▼ 請求項目-報酬に関するデータ
	//
	
	/** 請求報酬項目SEQ */
	private Long docInvoiceFeeSeq;

	/** 報酬取引日 */
	private LocalDate feeTransactionDate;
	
	/** 報酬摘要 */
	private String feeSumText;
	
	//
	// ▼ 報酬に関するデータ
	//
	
	/** 報酬SEQ */
	private Long feeSeq;

	/** 報酬項目名 */
	private String feeItemName;

	/** 報酬金額 */
	private BigDecimal feeAmount;

	/** 課税フラグ */
	private String taxFlg;
	
	/** 消費税率 */
	private String taxRateType;

	/** 源泉徴収フラグ */
	private String withholdingFlg;

	/** 源泉徴収額 */
	private BigDecimal withholdingAmount;
	
	/** タイムチャージフラグ */
	private String feeTimeChargeFlg;

	/** タイムチャージ：時間単価 */
	private BigDecimal hourPrice;

	/** タイムチャージ：時間（分） */
	private Long workTimeMinute;

	//
	// ▼ 請求項目-預り金に関するデータ
	//
	
	/** 預り金SEQ（カンマ区切り） */
	private String depositRecvSeq;

	/** 請求預り金項目SEQ */
	private Long docInvoiceDepositSeq;
	
	/** 預り金タイプ */
	private String invoiceDepositType;
	
	/** 預り金取引日 */
	private LocalDate depositTransactionDate;

	/** 預り金項目名 */
	private String depositItemName;

	/** 預り金金額 */
	private BigDecimal depositAmount;

	/** 預り金摘要 */
	private String depositSumText;

	//
	// ▼ 請求項目-その他に関するデータ
	//
	
	/** 請求その他項目SEQ */
	private Long docInvoiceOtherSeq;

	/** その他種類 */
	private String otherItemType;
	
	/** その他取引日 */
	private LocalDate otherTransactionDate;

	/** その他項目名 */
	private String otherItemName;

	/** その他金額 */
	private BigDecimal otherAmount;

	/** 値引き消費税率 */
	private String discountTaxRateType;

	/** 値引き源泉徴収フラグ */
	private String discountWithholdingFlg;

	/** その他摘要 */
	private String otherSumText;

}