package jp.loioz.app.user.saibanManagement.dto;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * 裁判関与者代理人Dto
 */
@Data
public class SaibanKanyoshaDairininViewDto {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 名簿ID */
	private PersonId personId;

	/** 顧客種別 */
	private CustomerType customerType;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 関与者名 */
	private String kanyoshaName;

	/** 事務所名 */
	private String jimushoName;

	/** 関係 */
	private String kankei;

	/** 備考 */
	private String remarks;

}
