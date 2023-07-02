package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class KaikeiManagementRelationBean {

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 分野ID */
	@Column(name = "bunya_id")
	private String bunyaId;

	/** 顧客ID **/
	@Column(name = "customer_id")
	private Long customerId;

	/** 顧客名 **/
	@Column(name = "customer_name")
	private String customerName;
}
