package jp.loioz.app.user.saibanManagement.dto;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * 裁判関与者（相手方、その他当事者）表示用Dto
 */
@Data
public class SaibanKanyoshaViewDto {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 名簿ID */
	private PersonId personId;

	/** 代理人関与者SEQ */
	private Long relatedKanyoshaSeq;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客種別 */
	private CustomerType customerType;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 関与者種別 */
	private String kanyoshaType;

	/** 関与者名 */
	private String kanyoshaName;

	/** 当事者表記（Cd） */
	private String saibanTojishaHyoki;

	/** 筆頭フラグ */
	private boolean mainFlg;

	/** 代理人フラグ */
	private String dairiFlg;

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

	/** 裁判関与者代理人情報 */
	private SaibanKanyoshaDairininViewDto saibanKanyoshaDairininViewDto;

}
