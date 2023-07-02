package jp.loioz.dto;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Data
public class KaikeiManagementRelationDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野名 */
	private String bunyaName;

	/** 顧客ID **/
	private CustomerId customerId;

	/** 顧客名 **/
	private String customerName;

	/** 会計情報が登録可能かどうか */
	private boolean canKaikeiCreate;

	/** 案件ステータス */
	private String ankenStatusCd;
}
