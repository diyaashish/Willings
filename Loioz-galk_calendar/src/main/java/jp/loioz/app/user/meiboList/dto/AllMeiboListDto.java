package jp.loioz.app.user.meiboList.dto;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Data;

/**
 * 「名簿一覧：すべて」の一覧表示用Dto
 */
@Data
public class AllMeiboListDto {

	/** 名簿ID */
	private Long personId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 案件顧客として登録されているかどうか */
	private boolean existsAnkenCustomer;

	/** 関与者として登録されているかどうか */
	private boolean existsKanyosha;

	/** 顧客区分 */
	private CustomerType customerType;

	/** 名前 */
	private String name;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 電話番号 */
	private String telNo;

	/** FAX番号 */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;

	/** 顧客登録日 */
	private String customerCreatedDate;

	/**
	 * 住所が存在するか
	 * 
	 * @return
	 */
	public boolean existsAddress() {
		if (StringUtils.isEmpty(address1) && StringUtils.isEmpty(address2)) {
			return false;
		}
		return true;
	}

}
