package jp.loioz.bean;

import java.math.BigDecimal;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class DepositRecvSummaryBean {

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 預り金：預り金残高 */
	private BigDecimal totalDepositBalanceAmount;

	/** 預り金：入金合計 */
	private BigDecimal totalDepositAmount;

	/** 預り金：出金合計 */
	private BigDecimal totalWithdrawalAmount;

	/** 預り金：事務所負担合計 */
	private BigDecimal totalTenantBearAmount;

}
