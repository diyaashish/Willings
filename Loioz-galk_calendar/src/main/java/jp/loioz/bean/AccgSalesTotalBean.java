package jp.loioz.bean;

import java.math.BigDecimal;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 売上合計Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AccgSalesTotalBean {

	/** 取引実績明細SEQ */
	private Long personId;

	/** 取引実績明細SEQ */
	private Long ankenId;

	/** 取引実績SEQ */
	private Long salesSeq;

	/** 売上合計【見込】（税込） */
	private BigDecimal salesAmountExpect;

	/** 売上合計【実績】（税込） */
	private BigDecimal salesAmountResult;

	/** 合計-売上金額（税抜） */
	private BigDecimal salesTotalAmount;

	/** 合計-消費税額 */
	private BigDecimal salesTaxTotalAmount;

	/** 値引き_売上金額（税抜） */
	private BigDecimal salesDiscountTotalAmount;

	/** 値引き_消費税額 */
	private BigDecimal salesDiscountTaxTotalAmount;

	/** 合計-源泉徴収税額 */
	private BigDecimal salesWithholdingTotalAmount;

}
