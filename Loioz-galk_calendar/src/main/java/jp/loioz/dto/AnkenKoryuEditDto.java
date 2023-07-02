package jp.loioz.dto;

import java.time.LocalDate;

import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.TelPattern;
import lombok.Data;

@Data
public class AnkenKoryuEditDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	/** 勾留SEQ */
	private Long koryuSeq;

	/** 勾留日 */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String koryuDate;

	/** 保釈日 */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String hoshakuDate;

	/** 捜査機関ID */
	private Long sosakikanId;

	/** 在監場所 */
	@MaxDigit(max = 30)
	private String zaikanPlace;

	/** 施設電話番号 */
	@TelPattern
	private String sosakikanTelNo;

	/** 備考 */
	@MaxDigit(max = 100)
	private String remarks;

	/** 勾留日 / LocalDate */
	public LocalDate getParsedKoryuDate() {
		try {
			return DateUtils.parseToLocalDate(koryuDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		} catch (Exception e) {
			return null;
		}
	}

	/** 保釈日 / LocalDate */
	public LocalDate getParsedHoshakuDate() {
		try {
			return DateUtils.parseToLocalDate(hoshakuDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		} catch (Exception e) {
			return null;
		}
	}
}