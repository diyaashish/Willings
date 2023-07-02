package jp.loioz.app.user.saibanManagement.dto;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Builder;
import lombok.Data;

/**
 * 裁判顧客一覧のDto
 */
@Builder
@Data
public class SaibanCustomerDto {

	/** 裁判-顧客 */

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 名簿ID */
	private PersonId personId;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客タイプ */
	private CustomerType customerType;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 当事者表記 */
	private String saibanTojishaHyoki;

	/** 筆頭フラグ */
	private boolean mainFlg;

	/** 裁判-顧客 */

	/** 顧客名 */
	private String customerName;
}
