package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class DengonCustomerBean {

	/** 伝言SEQ */
	private Long dengonSeq;

	/** 顧客ID */
	private Long customerId;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客姓（かな） */
	private String customerNameSeiKana;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客名（かな） */
	private String customerNameMeiKana;

}
