package jp.loioz.app.user.ankenManagement.dto;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Data;

/**
 * 案件民事関与者Dto
 */
@Data
public class AnkenKanyoshaViewDto {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 代理人関与者SEQ */
	private Long relatedKanyoshaSeq;

	/** 顧客ID */
	private CustomerId customerId;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 名簿種別 */
	private CustomerType customerType;

	/** 関与者種別 */
	private String kanyoshaType;

	/** 関与者名 */
	private String kanyoshaName;

	/** 関係 */
	private String kankei;

	/** 備考 */
	private String remarks;

	/** 電話番号 */
	private String telNo;

	/** FAX */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;

	/** 関与者の代理人 */
	private AnkenKanyoshaDairininViewDto ankenKanyoshaDairininViewDto;

}
