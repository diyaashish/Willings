package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Entity
@Data
public class GyomuHistoryHeaderDto {

	/** ID */
	@Column(name = "id")
	private Long Id;

	/** 名前 */
	@Column(name = "name")
	private String Name;

	/** 分野 */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 案件ID */
	private AnkenId ankenId;

	/** 顧客ID */
	private CustomerId customerId;

}
