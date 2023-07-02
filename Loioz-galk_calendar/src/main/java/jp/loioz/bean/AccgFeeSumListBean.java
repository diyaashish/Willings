package jp.loioz.bean;

import java.math.BigDecimal;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class AccgFeeSumListBean {

	/** 名簿ID */
	@Column(name = "person_id")
	private Long personId;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 入金ステータス */
	@Column(name = "fee_payment_status")
	private String feePaymentStatus;

	/** 報酬額 */
	@Column(name = "fee_amount")
	private BigDecimal feeAmount;

	/** 消費税金額 */
	@Column(name = "tax_amount")
	private BigDecimal taxAmount;

	/** 源泉徴収額 */
	@Column(name = "withholding_amount")
	private BigDecimal withholdingAmount;

}
