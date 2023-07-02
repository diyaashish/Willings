package jp.loioz.dto;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.NumericHyphen;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import jp.loioz.common.validation.annotation.ZipPattern;
import lombok.Data;

@Data
public class SosakikanEditDto {

	/** 捜査機関管理ID */
	private Long sosakikanId;

	/** 捜査機関区分 */
	@Required
	private String sosakikanType;

	/** 都道府県 */
	@Required
	private String todofukenId;

	/** 捜査機関郵便番号 */
	@MaxDigit(max = 8)
	@NumericHyphen
	@ZipPattern
	private String sosakikanZip;

	/** 捜査機関住所1 */
	@MaxDigit(max = 128)
	private String sosakikanAddress1;

	/** 捜査機関住所2 */
	@MaxDigit(max = 128)
	private String sosakikanAddress2;

	/** 捜査機関名 */
	@Required
	@MaxDigit(max = 30)
	private String sosakikanName;

	/** 電話番号 */
	@TelPattern
	private String sosakikanTelNo;

	/** FAX番号 */
	@TelPattern
	private String sosakikanFaxNo;

	/** バージョンNo **/
	private Long versionNo;

}
