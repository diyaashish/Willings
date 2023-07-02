package jp.loioz.app.user.nyushukkinYotei.nyukin.form.ajax;

import java.math.BigDecimal;
import java.time.LocalDate;

import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class NyukinSaveRequest {

	/** 入出金予定SEQ */
	@Required
	private Long nyushukkinYoteiSeq;

	/** 入金日 */
	@Required
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String nyushukkinDate;

	/** 入金額 */
	@Required
	@Numeric
	@MaxDigit(max = 10)
	private String nyushukkinGaku;

	/** versionNo */
	@Required
	private Long versionNo;

	/** 入金日をLocalDate型で取得 */
	public LocalDate getNyushukkinLocalDate() {
		return DateUtils.parseToLocalDate(this.nyushukkinDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
	}

	/** 入金額のBigDecimal変換 */
	public BigDecimal getParsedDecimalNyukinGaku() {
		return LoiozNumberUtils.parseAsBigDecimal(this.nyushukkinGaku);
	}

}
