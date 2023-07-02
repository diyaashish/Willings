package jp.loioz.app.common.mvc.ankenCustomer.dto;

import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * 名簿検索結果Dto
 */
@Data
public class PersonSearchResultDto {

	/** 名簿ID */
	private PersonId personId;

	/** 顧客名 */
	private String customerName;

	/** 顧客名ふりがな */
	private String customerNameKana;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 生年月日 */
	private String birthday;

	/** 郵便番号 */
	private String zipCode;

	/** 住所1 */
	private String address1;

	/** 住所2 */
	private String address2;

}
