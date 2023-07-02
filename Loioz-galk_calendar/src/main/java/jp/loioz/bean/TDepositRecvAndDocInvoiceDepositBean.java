package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 預り金データと請求項目預り金マッピングに関するSEQ
 *
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class TDepositRecvAndDocInvoiceDepositBean {

	/** 預り金SEQ */
	private Long depositRecvSeq;

	/** 会計書類SEQ（使用先） */
	private Long usingAccgDocSeq;

	/** 作成タイプ */
	private String createdType;

	/** 実費入金フラグ */
	private String expenseInvoiceFlg;

	/** 発生日 */
	private LocalDate depositDate;

	/** 項目名 */
	private String depositItemName;

	/** 入金額 */
	private BigDecimal depositAmount;

	/** 出金額 */
	private BigDecimal withdrawalAmount;

	/** 入出金タイプ */
	private String depositType;

	/** 摘要 */
	private String sumText;

	/** メモ */
	private String depositRecvMemo;

	/** 事務所負担フラグ */
	private String tenantBearFlg;

	/** 請求預り金項目SEQ */
	private Long docInvoiceDepositSeq;

	/** 請求項目SEQ */
	private Long docInvoiceSeq;
}
