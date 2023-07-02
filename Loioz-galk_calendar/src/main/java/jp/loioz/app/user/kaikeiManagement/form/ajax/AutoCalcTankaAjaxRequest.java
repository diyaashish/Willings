package jp.loioz.app.user.kaikeiManagement.form.ajax;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.LocalTimePattern;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 単価の自動計算用 リクエストオブジェクト
 */
@Data
public class AutoCalcTankaAjaxRequest {

	/** 報酬項目ID */
	@Required
	private String hoshuKomokuId;

	/** 源泉徴収フラグ */
	@Required
	@EnumType(value = SystemFlg.class)
	private String gensenFlg;

	/** 単価 */
	@Required
	private String tanka;

	/** 時間の指定方法 */
	@EnumType(value = TimeChargeTimeShitei.class)
	private String timeChargeTimeShitei;

	/** 日付From */
	@LocalDatePattern
	private String dateFrom;

	/** 時間From */
	@LocalTimePattern
	private String timeFrom;

	/** 日付To */
	@LocalDatePattern
	private String dateTo;

	/** 時間To */
	@LocalTimePattern
	private String timeTo;

	/** 経過時間(分) */
	private Long timeChargeTime;

	/** 「税抜、税込」選択 */
	@Required
	@EnumType(value = TaxFlg.class)
	private String taxFlg;
	
	/** 消費税率 */
	@Required
	@EnumType(value = TaxRate.class)
	private String taxRate;

	/** FromをLocalDateTime型に変更 */
	public LocalDateTime getLocalDateFrom() {

		LocalDate dateFrom = DateUtils.parseToLocalDate(this.dateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		LocalTime timeFrom = LocalTime.parse(this.timeFrom);

		return LocalDateTime.of(dateFrom, timeFrom);
	}

	/** ToをLocalDateTime型に変更 */
	public LocalDateTime getLocalDateTo() {
		LocalTime.parse("", DateTimeFormatter.ofPattern("").withResolverStyle(ResolverStyle.STRICT));

		LocalDate dateTo = DateUtils.parseToLocalDate(this.dateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		LocalTime timeTo = LocalTime.parse(this.timeTo);

		return LocalDateTime.of(dateTo, timeTo);
	}
}
