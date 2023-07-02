package jp.loioz.app.user.kanyosha.dto;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Data;

/**
 * 関与者一覧表示用Dto
 */
@Data
public class KanyoshaListDto {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 名簿ID */
	private Long personId;

	/** 属性 */
	private PersonAttribute personAttribute;

	/** 関与者種別 */
	private CustomerType kanyoshaType;

	/** 関与者名 */
	private String kanyoshaName;

	/** 関与者名かな */
	private String kanyoshaNameKana;

	/** 関係 */
	private String kankei;

	/** 郵便番号 */
	private String zipCode;

	/** 住所1（地域） */
	private String address1;

	/** 住所2（番地・建物） */
	private String address2;

	/** 住所備考 */
	private String addressRemarks;

	/** 電話番号 */
	private String telNo;

	/** FAX番号 */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;

	/** 口座（口座情報の登録有無） */
	private boolean hasKoza;

	/** 備考 */
	private String remarks;

	/** 弁護士名簿付帯情報：事務所名 */
	private String jimushoName;

	/** 表示順 */
	private Long dispOrder;

}
