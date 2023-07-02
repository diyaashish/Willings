package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.CommonConstant.CompanyTodofuken;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.NumericHyphen;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.ZipPattern;
import lombok.Data;

/**
 * 裁判所マスタ編集用のDto
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanshoEditDto {

	/** 裁判所ID **/
	private Long saibanshoId;

	/** 都道府県ID **/
	@Required
	@EnumType(value = CompanyTodofuken.class)
	private String todofukenId;

	/** 裁判所郵便番号 **/
	@MaxDigit(max = 8, item = "裁判所郵便番号")
	@NumericHyphen
	@ZipPattern
	private String saibanshoZip;

	/** 裁判所住所1 **/
	@MaxDigit(max = 128, item = "裁判所住所")
	private String saibanshoAddress1;

	/** 裁判所住所2 **/
	@MaxDigit(max = 128, item = "裁判所住所")
	private String saibanshoAddress2;

	/** 裁判所名 **/
	@Required
	@MaxDigit(max = 64, item = "裁判所名")
	private String saibanshoName;

	/** バージョンNo **/
	private Long versionNo;

}
