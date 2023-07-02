package jp.loioz.app.common.mvc.kanyosha.dto;

import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * 関与者検索モーダルの結果情報用Dto
 */
@Data
public class PersonSearchResultDto {

	/** 名簿ID */
	private PersonId personId;

	/** 名簿属性 */
	private PersonAttribute personAttribute;
	
	/** 名前 */
	private String personName;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 関与者SEQ すでに案件に紐づいているデータを表示する際にのみ */
	private Long kanyoshaSeq;

}
