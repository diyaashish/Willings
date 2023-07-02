package jp.loioz.dto;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.Nyukin;
import jp.loioz.common.validation.groups.Shukkin;
import lombok.Data;

@Data
public class NyushukkinYoteiEditDto {

	/** 入出金予定SEQ */
	private Long nyushukkinYoteiSeq;

	/** 入出金種別 */
	@Required
	@EnumType(value = NyushukkinType.class)
	private String nyushukkinType;

	/** 顧客ID */
	@Required
	private Long customerId;

	/** 案件ID */
	@Required
	private Long ankenId;

	/** 入金項目ID */
	@Required(groups = Nyukin.class)
	private Long nyukinKomokuId;

	/** 入金-支払者種別 */
	@Required(groups = Nyukin.class)
	@EnumType(value = TargetType.class)
	private String nyukinShiharaishaType;

	/** 入金-支払者関与者SEQ */
	private Long nyukinShiharaishaKanyoshaSeq;

	/** 入金-入金先口座SEQ */
	@Required(groups = Nyukin.class)
	private Long nyukinSakiKozaSeq;

	/** 出金項目ID */
	@Required(groups = Shukkin.class)
	private Long shukkinKomokuId;

	/** 出金-支払者種別 */
	@Required(groups = Shukkin.class)
	@EnumType(value = TargetType.class)
	private String shukkinShiharaiSakiType;

	/** 出金-支払者関与者SEQ */
	private Long shukkinShiharaiSakiKanyoshaSeq;

	/** 出金-支払者口座SEQ */
	@Required(groups = Shukkin.class)
	private Long shukkinShiharaishaKozaSeq;

	/** 入出金予定日 */
	@Required
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String nyushukkinYoteiDate;

	/** 入出金予定額 */
	@Required
	@Numeric
	@MaxNumericValue(max = 999999999)
	@MinNumericValue(min = 1)
	private String nyushukkinYoteiGaku;

	/** 表示用入出予定額 */
	private String dispNyushukkinYoteiGaku;

	/** 入出金日 */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String nyushukkinDate;

	/** 入出金額 */
	@Numeric
	@MaxNumericValue(max = 999999999)
	@MinNumericValue(min = 1)
	private String nyushukkinGaku;

	/** 表示用入出金額 */
	private String dispNyushukkinGaku;

	/** 摘要 */
	@MaxDigit(max = 10000)
	private String tekiyo;

	/** 精算SEQ */
	private Long seisanSeq;

	/** 表示用登録日時 */
	private String dispCreatedAt;

	/** 表示用登録者名 */
	private String dispCreatedBy;

	/** 表示用更新日時 */
	private String dispLastEditAt;

	/** 表示用更新者名 */
	private String dispLastEditBy;

	/** 入出金予定日の日付型取得 */
	public LocalDate getNyushukkinYoteiLocalDate() {
		try {
			return DateUtils.parseToLocalDate(this.nyushukkinYoteiDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/** 入出金日の日付型取得 */
	public LocalDate getNyushukkinLocalDate() {
		try {
			return DateUtils.parseToLocalDate(this.nyushukkinDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

}
