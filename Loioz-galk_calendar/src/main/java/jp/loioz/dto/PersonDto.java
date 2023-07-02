package jp.loioz.dto;

import jp.loioz.domain.value.PersonId;
import lombok.Data;

@Data
public class PersonDto {

	/** 名簿ID */
	private PersonId personId;

	/** 名簿名 */
	private String personName;

}
