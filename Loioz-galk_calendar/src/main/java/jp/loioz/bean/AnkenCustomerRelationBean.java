package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenCustomerRelationBean {

	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private Long ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private Long bunyaId;

	/** 案件ステータス */
	private String ankenStatus;

}
