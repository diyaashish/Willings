package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.Hasu;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanShiharaiMonthDay;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class ShiharaiPlanDto {

	/** 精算SEQ */
	@Required
	private Long seisanSeq;

	/** 請求方法 */
	@Required
	@EnumType(value = SeikyuType.class)
	private String seikyuType;

	/** 月々の支払額 */
	@Numeric
	@MaxNumericValue(max = 999999999)
	@MinNumericValue(min = 1)
	private String monthShiharaiGaku;

	/** 月々の支払額 : 表示用 */
	private String dispMonthShiharaiGaku;

	/** 端数月 */
	@EnumType(value = Hasu.class)
	private String hasu;

	/** 月末か日付指定か */
	@EnumType(value = SeisanShiharaiMonthDay.class)
	private String shiharaiDayType;

	/** 支払日(一括) */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String shiharaiDate;

	/** 支払日(分割：年) */
	@Numeric
	@MaxDigit(max = 4)
	private String shiharaiYear;

	/** 支払日(分割：月) */
	@Numeric
	@MaxNumericValue(max = 12)
	@MinNumericValue(min = 1)
	private String shiharaiMonth;

	/** 支払日(分割：日) */
	@Numeric
	@MinNumericValue(min = 1)
	private String shiharaiDay;

	/** 残金をまとめる */
	private boolean summarizing;

	// 以下、表示用
	/** 請求額 */
	private String seikyuGaku;

	/** 入金額 */
	private String nyukinGaku;

	/** 残金 */
	private String zankin;

	/** 差額 */
	private String sagaku;

	/** 精算額分すべて実績登録済 (残金が0円) */
	private boolean isPlanComplete;

	/** 差額0円の計画かどうか */
	private boolean isExpected;

}