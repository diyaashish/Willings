package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanshoBuEditDto {

	/** 係属部ID */
	private Long keizokuBuId;

	/** 裁判所ID */
	@Required
	private String saibanshoId;

	/** 係属部名 */
	@Required
	@MaxDigit(max = 100)
	private String keizokuBuName;

	/** 電話番号 */
	@TelPattern
	private String keizokuBuTelNo;

	/** FAX番号 */
	@TelPattern
	private String keizokuBuFaxNo;

	/** バージョンNo */
	private Long versionNo;
}