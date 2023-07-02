package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 *  裁判に関する顧客情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanSeqCustomerBean {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 名簿ID */
	private Long personId;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客姓（かな） */
	private String customerNameSeiKana;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客名（かな） */
	private String customerNameMeiKana;

	/** 顧客数 */
	private Long numberOfCustomer;

}