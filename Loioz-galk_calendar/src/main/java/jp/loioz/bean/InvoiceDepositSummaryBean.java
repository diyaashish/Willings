package jp.loioz.bean;

import java.math.BigDecimal;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class InvoiceDepositSummaryBean {

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 預り金合計 */
	private BigDecimal totalDepositAmount;

	/** 実費合計 */
	private BigDecimal totalAdvanceMoney;

}
