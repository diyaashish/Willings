package jp.loioz.bean;

import java.math.BigDecimal;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class RepaySummaryBean {

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 既入金合計 */
	private BigDecimal totalRepayAmount;

}
