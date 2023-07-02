package jp.loioz.app.common.form.userHeader;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.PersonAttributeCd;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * ヘッダー検索：名簿一覧データ
 */
@Data
public class PersonListDto {

	/** 名簿ID */
	private PersonId personId;

	/** 顧客種別 */
	private CustomerType customerType;

	/** 顧客か否か */
	private boolean isCustomer;

	/** 顧問か否か */
	private boolean isAdvisor;

	/** 名前 */
	private String personName;

	/** 名前かな */
	private String personNameKana;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 電話番号 */
	private String telNo;

	/** 名簿属性Enumの取得 */
	public PersonAttribute getPersonAttribute() {
		return PersonAttribute.of(SystemFlg.booleanToCode(this.isCustomer), SystemFlg.booleanToCode(this.isAdvisor), this.customerType.getCd());
	}

	/** 名簿属性CdEnumを取得する */
	public PersonAttributeCd getPersonAttributeCd() {
		return getPersonAttribute().getCd();
	}

}
