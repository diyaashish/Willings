package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanshoListDto {

	/** 裁判所ID **/
	private Long saibanshoId;

	/** 都道府県ID **/
	private String todofukenId;

	/** 裁判所郵便番号 **/
	private String saibanshoZip;

	/** 裁判所住所 **/
	private String saibanshoAddress1;

	/** 裁判所住所2 **/
	private String saibanshoAddress2;

	/** 裁判所名 **/
	private String saibanshoName;

	/** バージョンNo */
	private Long versionNo;
}
