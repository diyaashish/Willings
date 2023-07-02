package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 *  案件に関する顧客情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenPersonCntBean {

	/** 案件ID */
	private AnkenId ankenId;

	/** 顧客名 */
	private String customerName;

	/** 顧客数 */
	private Long numberOfCustomer;

}