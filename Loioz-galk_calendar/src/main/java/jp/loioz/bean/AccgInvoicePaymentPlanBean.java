package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AccgInvoicePaymentPlanBean {

	/** 支払計画SEQ */
	private Long invoicePaymentPlanSeq;

	/** 請求書SEQ */
	private Long invoiceSeq;

	/** 売上明細SEQ */
	private Long salesDetailSeq;

	/** 支払予定日 */
	private LocalDate paymentScheduleDate;

	/** 支払予定金額 */
	private BigDecimal paymentScheduleAmount;

	/** 摘要 */
	private String sumText;

	/** 請求額 */
	private BigDecimal salesAmountExpect;

	/** 売上額 */
	private BigDecimal salesAmountResult;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;
}