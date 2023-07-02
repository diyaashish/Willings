package jp.loioz.app.common.mvc.person.popover.form;

import jp.loioz.common.constant.CommonConstant.AllowType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * 名簿情報ポップオーバー画面オブジェクト
 */
@Data
public class PersonPopoverViewForm {

	/** 名簿ID */
	private PersonId personId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 顧客種別 */
	private String customerType;

	/** 顧客名 */
	private String customerName;

	/** 顧客名かな */
	private String customerNameKana;

	// 住所情報（居住地 or 所在地）
	
	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;
	
	// 連絡先情報
	
	/** 連絡可否 */
	private AllowType allowType;

	/** 連絡可否備考 */
	private String allowTypeRemarks;
	
	/** 電話番号 */
	private String telNo;

	/** FAX番号 */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;

	/** 性別 */
	private String gender;

	// 個人のみ
	/** 年齢 */
	private Integer age;

	/** 死亡済か否か */
	private boolean isDead;

	// 法人のみ
	/** 代表名 */
	private String daihyoName;

	/** 代表名かな */
	private String daihyoNameKana;

	/** 代表役職 */
	private String daihyoPositionName;

	/** 担当名 */
	private String tantoName;

	/** 担当名かな */
	private String tantoNameKana;

	// 弁護士のみ
	/** 事務所名 */
	private String jimushoName;

	/** 名簿属性 */
	public PersonAttribute getPersonAttribute() {
		return PersonAttribute.of(this.customerFlg, this.advisorFlg, this.customerType);
	}

	/** 顧客種別 */
	public CustomerType getCustomerTypeEnum() {
		return CustomerType.of(this.customerType);
	}

}
