package jp.loioz.app.user.ankenManagement.dto;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * 案件民事関与者代理人Dto
 */
@Data
public class AnkenKanyoshaDairininViewDto {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 名簿ID */
	private PersonId personId;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 名簿種別 */
	private CustomerType customerType;

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

	/** 事務所名 */
	private String jimushoName;
}
