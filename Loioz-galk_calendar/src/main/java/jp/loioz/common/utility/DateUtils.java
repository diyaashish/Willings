package jp.loioz.common.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.Month;
import jp.loioz.common.constant.DefaultEnum;

/**
 * 日付用のUtilクラス
 */
public class DateUtils {

	public static final String DATE_YYYY = "uuuu";
	public static final String DATE_YY = "uu";
	public static final String DATE_MM = "MM";
	public static final String DATE_DD = "dd";
	public static final String DATE_M = "M";
	public static final String DATE_D = "d";

	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String TIME_FORMAT_HHMM = "HH:mm";
	public static final String TIME_FORMAT_HH = "HH";
	public static final String TIME_FORMAT_MM = "mm";
	public static final String TIME_FORMAT_SS = "ss";
	public static final String TIME_FORMAT_HMM = "H:mm";
	public static final String TIME_FORMAT_H = "H";
	public static final String TIME_FORMAT_M = "m";
	public static final String TIME_FORMAT_S = "s";
	public static final String ZERO_TIME = "00:00:00";

	public static final String DATE_JP_YYYY_MM = "uuuu年MM月";
	public static final String DATE_JP_YYYY_MM_DD = "uuuu年MM月dd日";
	public static final String DATE_JP_YYYY_M = "uuuu年M月";
	public static final String DATE_JP_YYYY_M_D = "uuuu年M月d日";
	public static final String DATE_JP_YYYY_MM_DD_ZERO_FILL = "uuuu年 M月 d日";
	public static final String DATE_JP_M_D = "M月d日";

	public static final String DATE_FORMAT_NON_DELIMITED = "uuuuMMdd";
	public static final String DATE_FORMAT_HYPHEN_DELIMITED = "uuuu-MM-dd";
	public static final String DATE_FORMAT_SLASH_DELIMITED = "uuuu/MM/dd";

	public static final String DATE_TIME_FORMAT_HYPHEN_DELIMITED = DATE_FORMAT_HYPHEN_DELIMITED + " " + TIME_FORMAT;

	public static final String HYPHEN_YYYY_MM_DD_HHMM = DATE_FORMAT_HYPHEN_DELIMITED + " " + TIME_FORMAT_HHMM;
	public static final String NON_DELIMITED_YYYY_MM_DD_HHMM = DATE_FORMAT_NON_DELIMITED + "_" + TIME_FORMAT_HH + TIME_FORMAT_MM;

	public static final String DATE_TIME_FORMAT_SLASH_DELIMITED = DATE_FORMAT_SLASH_DELIMITED + " " + TIME_FORMAT;
	public static final String DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM = DATE_FORMAT_SLASH_DELIMITED + " " + TIME_FORMAT_HHMM;
	public static final String DATE_TIME_FORMAT_CHAR_JA_DELIMITED = DATE_JP_YYYY_MM_DD + " " + TIME_FORMAT;

	// 月初の定義を1日とする
	public static final String START_OF_MONTH = "1";
	// 月末の定義を31日とする。
	public static final String END_OF_MONTH = "31";

	/**
	 * 指定の日付インスタンスを複製したインスタンスを返却する
	 *
	 * @param localDate
	 * @return
	 */
	public static LocalDate clone(LocalDate localDate) {

		if (localDate == null) {
			return null;
		}

		return LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
	}

	/**
	 * LocalDateTimeをLocalDateに変換する
	 *
	 * @param localDateTime
	 * @return LocalDate
	 */
	public static LocalDate convertLocalDate(LocalDateTime localDateTime) {

		if (localDateTime == null) {
			return null;
		}

		return LocalDate.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth());
	}

	/**
	 * 現在日付を和暦文字列で取得
	 *
	 * @throws ParseException
	 */
	public static String getDateToJaDate() {

		// ロケールを指定してCalendarインスタンスを取得
		Locale locale = new Locale("ja", "JP", "JP");
		Calendar calendar = Calendar.getInstance(locale);

		// calendar.getTime()で現在日時を取得して和暦にフォーマット
		DateFormat japaneseFormat = new SimpleDateFormat("Gy年M月d日", locale);

		return japaneseFormat.format(calendar.getTime());

	}

	/**
	 * 現在年を和暦文字列で取得
	 *
	 * <pre>
	 * 例
	 * 現在2016年の場合 → 28(平成28年)
	 * 現在2020年の場合 → 2(令和2年)
	 * </pre>
	 *
	 */
	public static String getYearToJa() {

		// ロケールを指定してCalendarインスタンスを取得
		Locale locale = new Locale("ja", "JP", "JP");
		Calendar calendar = Calendar.getInstance(locale);

		// calendar.getTime()で現在日時を取得して和暦にフォーマット
		SimpleDateFormat japaneseFormat = new SimpleDateFormat("y", locale);

		return japaneseFormat.format(calendar.getTime());
	}

	/**
	 * LocalDateを和暦文字列にパースする
	 *
	 * @param target
	 * @return
	 */
	public static String parseLocalDateToJaDate(LocalDate target) {
		Date parsedDate = Date.from(target.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return parseDateToJaDate(parsedDate);
	}

	/**
	 * LocalDateTimeを和暦文字列にパースする
	 *
	 * @param target
	 * @return
	 */
	public static String parseLocalDateTimeToJaDate(LocalDateTime target) {
		LocalDate localDate = target.toLocalDate();
		LocalTime localTime = target.toLocalTime();
		Date parsedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		String jaDate = parseDateToJaDate(parsedDate);
		return jaDate + CommonConstant.SPACE + localTime;
	}

	/**
	 * Dateを和暦文字列にパースする
	 *
	 * @throws ParseException
	 */
	public static String parseDateToJaDate(Date target) {

		ZonedDateTime zdt = ZonedDateTime.ofInstant(target.toInstant(), ZoneId.systemDefault());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("Gy年M月d日");
		JapaneseDate jpDate = JapaneseDate.from(zdt);

		return jpDate.format(dtf);
	}

	/**
	 * 和暦文字列をDateにパースする
	 *
	 * @param target
	 * @return
	 * @throws ParseException
	 */
	public static Date parseJaDateToDate(String target) throws ParseException {
		Locale locale = new Locale("ja", "JP", "JP");
		DateFormat sdf = new SimpleDateFormat("GGGGy年M月d日", locale);
		return sdf.parse(target);
	}

	/**
	 * 年号・年・月・日から西暦、和暦を生成する
	 *
	 * @param eraTypeCd
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static LocalDate parseToLocalDate(String eraTypeCd, Long year, Long month, Long day) {

		if (CommonConstant.EraType.of(eraTypeCd) == null) {
			return null;
		}

		if (!EraType.SEIREKI.getCd().equals(eraTypeCd)) {
			// 和暦の場合
			// 厳密な変換を行うため『令和元年1月1日』などはエラーになる
			try {
				DateTimeFormatter kanjiFormat = DateTimeFormatter.ofPattern("GGGGy年M月d日", Locale.JAPAN);
				DateTimeFormatter kanjiParseFormat = kanjiFormat.withChronology(JapaneseChronology.INSTANCE).withResolverStyle(ResolverStyle.STRICT);
				LocalDate parseToSerekiDate = kanjiParseFormat.parse(
						CommonConstant.EraType.of(eraTypeCd).getVal() + year + "年" + month + "月" + day + "日", LocalDate::from);

				return LocalDate.of(parseToSerekiDate.getYear(), month.intValue(), day.intValue());
			} catch (Exception e) {

				return null;
			}
		} else {
			// 西暦の場合
			return LocalDate.of(year.intValue(), month.intValue(), day.intValue());
		}
	}

	// ここまで追加

	/**
	 * 日付文字列をLocalDateTimeに変換する
	 *
	 * @param dateTimeStr 日付文字列(時分秒部分は任意)
	 * @param dateFormat 日付列のフォーマット形式（時分秒部分を含んだフォーマット形式）
	 * @return 日付文字列をパースしたLocalDateTime ※日付文字列がnull or ブランクの場合は nullを返却
	 */
	public static LocalDateTime parseToLocalDateTime(String dateTimeStr, String dateFormat) {

		if (StringUtils.isEmpty(dateTimeStr)) {
			return null;
		}

		if (dateTimeStr.length() == DATE_FORMAT_HYPHEN_DELIMITED.length()) {
			// 渡された日付文字列が年月日しかない場合は、デフォルトの時分秒を付与する
			dateTimeStr = dateTimeStr + " " + ZERO_TIME;
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
		return LocalDateTime.parse(dateTimeStr, dtf);
	}

	/**
	 * Date型 から LocalDate型に変換します。
	 * 
	 * @param target
	 * @return
	 */
	public static LocalDate parseDateToLocalDate(Date target) {
		return target.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * 日付文字列をLocalDateに変換する
	 *
	 * @param dateStr 日付文字列
	 * @param dateFormat 日付列のフォーマット形式
	 * @return 日付文字列をパースしたLocalDate ※日付文字列がnull or ブランクの場合は nullを返却
	 */
	public static LocalDate parseToLocalDate(String dateStr, String dateFormat) {

		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
		return LocalDate.parse(dateStr, dtf);
	}

	/**
	 * 日付文字列をLocalDateに変換する
	 *
	 * @param dateStr 日付文字列
	 * @param dateFormat 日付列のフォーマット形式
	 * @param resolverStyle
	 * @return 日付文字列をパースしたLocalDate ※日付文字列がnull or ブランクの場合は nullを返却
	 */
	public static LocalDate parseToLocalDate(String dateStr, String dateFormat, ResolverStyle resolverStyle) {

		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
		dtf.withResolverStyle(resolverStyle);

		return dtf.parse(dateStr, LocalDate::from);
	}

	/**
	 * 時間文字列をLocalTimeに変換
	 *
	 * @param timeStr 日付文字列
	 * @param timeFormat 日付列のフォーマット形式
	 * @return 時間文字列をパースしたLocalTime ※日付文字列がnull or ブランクの場合は nullを返却
	 */
	public static LocalTime parseToLocalTime(String timeStr, String dateFormat) {

		if (StringUtils.isEmpty(timeStr)) {
			return null;
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
		return LocalTime.parse(timeStr, dtf);
	}

	/**
	 * LocalDateTimeを日付文字列に変換する
	 *
	 * @param dateTime LocalDateTime
	 * @param format 変換したい文字列のフォーマット形式
	 * @return LocalDateTimeを変換した指定フォーマットの日付文字列 ※引数のLocalDateTimeがnullの場合はブランク文字を返却
	 */
	public static String parseToString(LocalDateTime dateTime, String format) {

		if (dateTime == null) {
			return "";
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
		return dateTime.format(dtf);
	}

	/**
	 * LocalDateを日付文字列に変換する
	 *
	 * @param date LocalDate
	 * @param format 変換したい文字列のフォーマット形式
	 * @return LocalDateを変換した指定フォーマットの日付文字列 ※引数のLocalDateがnullの場合はブランク文字を返却
	 */
	public static String parseToString(LocalDate date, String format) {

		if (date == null) {
			return "";
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
		return date.format(dtf);
	}

	/**
	 * LocalTimeを時間文字列に変換する
	 *
	 * @param time LocalTime
	 * @param format 変換したい文字列のフォーマット形式
	 * @return LocalTimeを変換した指定フォーマットの日付文字列 ※引数のLocalTimeがnullの場合はブランク文字を返却
	 */
	public static String parseToString(LocalTime time, String format) {

		if (time == null) {
			return "";
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
		return time.format(dtf);
	}

	/**
	 * 現在の月を取得する
	 *
	 * @return 月
	 */
	public static Month getCurrentMonth() {
		return DefaultEnum.getEnum(Month.class, String.valueOf(LocalDateTime.now().getMonthValue()));
	}

	/**
	 * 年度に現在の月を付加する
	 *
	 * @param nendo 年度
	 * @return 年月
	 */
	public static String nendoToNendoYmAtCurrentMonth(String nendo) {
		if (StringUtils.isEmpty(nendo)) {
			return CommonConstant.BLANK;
		}
		return nendo + DateUtils.getCurrentMonth().getVal();
	}

	/**
	 * 年月文字列を月に変換する
	 *
	 * @param nendoYm 年月
	 * @return 月
	 */
	public static Month nendoYmToMonth(String nendoYm) {
		if (nendoYm == null) {
			return null;
		}
		if (nendoYm.length() < 5) {
			return null;
		}
		String monthStr = nendoYm.substring(4);
		return DefaultEnum.getEnum(Month.class, monthStr);
	}

	/**
	 * 日付文字列を指定形式の日付文字列に変換する
	 *
	 * @param dateStr
	 * @param fromFormat
	 * @param toFormat
	 * @return
	 */
	public static String convertStringDate(String dateStr, String fromFormat, String toFormat) {

		if (StringUtils.isEmpty(dateStr)) {
			return "";
		}

		LocalDate date = DateUtils.parseToLocalDate(dateStr, fromFormat);

		return DateUtils.parseToString(date, toFormat);
	}

	/**
	 * 日付文字列を指定形式の日付文字列に変換する
	 *
	 * @param dateStr
	 * @param hh
	 * @param mm
	 * @return
	 */
	public static String convertStringDateTime(String dateStr, String hh, String mm) {
		if (StringUtils.isEmpty(dateStr)) {
			return "";
		}
		String dateTime = dateStr + " " + hh + ":" + mm + ":" + "00";
		return dateTime;
	}

	/**
	 * 日付文字列を指定形式の日付文字列に変換する
	 *
	 * @param dateStr
	 * @param hh
	 * @param mm
	 * @param ss
	 * @return
	 */
	public static String convertStringDateTime(String dateStr, String hh, String mm, String ss) {
		if (StringUtils.isEmpty(dateStr)) {
			return "";
		}
		String dateTime = dateStr + " " + hh + ":" + mm + ":" + ss;
		return dateTime;
	}

	/**
	 * 基準日を基準とした当月の月初の日付情報を取得する<br>
	 * （時分秒は 00:00:00 とする）
	 *
	 * @param baseDateTime
	 * @return
	 */
	public static LocalDateTime getFirstDateTimeOfThisMonth(LocalDateTime baseDateTime) {

		LocalDateTime thisMonthFirst = baseDateTime.with(TemporalAdjusters.firstDayOfMonth());

		return thisMonthFirst.withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	/**
	 * 基準日を基準とした当月の月初の日付情報を取得する<br>
	 * （時分秒は 00:00:00 とする）
	 *
	 * @param baseDate
	 * @return
	 */
	public static LocalDateTime getFirstDateTimeOfThisMonth(LocalDate baseDate) {

		LocalDate thisMonthFirst = baseDate.with(TemporalAdjusters.firstDayOfMonth());

		return LocalDateTime.of(thisMonthFirst, LocalTime.of(0, 0, 0));
	}

	/**
	 * 基準日より13日後（基準日を含めた2週間後）の日付情報を取得する<br>
	 * （時分秒は 23:59:59 とする）
	 * 
	 * @param baseDateTime
	 * @return
	 */
	public static LocalDateTime get13DaysAfterDateTimeFrom(LocalDateTime baseDateTime) {

		LocalDateTime _13DaysAfterDateTime = baseDateTime
				.plusDays(13);

		return _13DaysAfterDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
	}

	/**
	 * 基準日を基準とした当月末の日付情報を取得する<br>
	 * （時分秒は 23:59:59 とする）
	 *
	 * @param baseDateTime
	 * @return
	 */
	public static LocalDateTime getEndDateTimeOfThisMonth(LocalDateTime baseDateTime) {

		LocalDateTime thisMonthEnd = baseDateTime.with(TemporalAdjusters.lastDayOfMonth());

		return thisMonthEnd.withHour(23).withMinute(59).withSecond(59).withNano(0);
	}

	/**
	 * 基準日を基準とした当月末の日付情報を取得する<br>
	 * （時分秒は 23:59:59 とする）
	 *
	 * @param baseDate
	 * @return
	 */
	public static LocalDateTime getEndDateTimeOfThisMonth(LocalDate baseDate) {

		LocalDate thisMonthEnd = baseDate.with(TemporalAdjusters.lastDayOfMonth());

		return LocalDateTime.of(thisMonthEnd, LocalTime.of(23, 59, 59));
	}

	/**
	 * 基準日を基準とした翌月末の日付情報を取得する<br>
	 * （時分秒は 23:59:59 とする）
	 *
	 * @param baseDateTime
	 * @return
	 */
	public static LocalDateTime getEndDateTimeOfNextMonth(LocalDateTime baseDateTime) {

		LocalDateTime nextMonthEnd = baseDateTime
				.plusMonths(1)
				.with(TemporalAdjusters.lastDayOfMonth());

		return nextMonthEnd.withHour(23).withMinute(59).withSecond(59).withNano(0);
	}

	/**
	 * 基準日の翌日の日付情報を取得する
	 * 
	 * @param baseDate
	 * @return
	 */
	public static LocalDate getNextDate(LocalDate baseDate) {

		return baseDate.plusDays(1);
	}

	/**
	 * 基準日を基準とした当月の月初の日付情報を取得する
	 *
	 * @param targetDate
	 * @return
	 */
	public static LocalDate getFirstDateOfThisMonth(Integer year, Integer month) {

		LocalDate baseDate = LocalDate.of(year, month, 1);
		return getFirstDateOfThisMonth(baseDate);
	}

	/**
	 * 基準日を基準とした当月の月初の日付情報を取得する
	 *
	 * @param targetDate
	 * @return
	 */
	public static LocalDate getFirstDateOfThisMonth(LocalDate baseDate) {

		return baseDate
				.with(TemporalAdjusters.firstDayOfMonth());
	}

	/**
	 * 基準日を基準とした当月の月末日の日付情報を取得する
	 *
	 * @param targetDate
	 * @return
	 */
	public static LocalDate getLastDateOfThisMonth(Integer year, Integer month) {

		LocalDate baseDate = LocalDate.of(year, month, 1);
		return getLastDateOfThisMonth(baseDate);
	}

	/**
	 * 基準日を基準とした当月の月末日の日付情報を取得する
	 *
	 * @param targetDate
	 * @return
	 */
	public static LocalDate getLastDateOfThisMonth(LocalDate baseDate) {

		return baseDate
				.with(TemporalAdjusters.lastDayOfMonth());
	}

	/**
	 * 基準日を基準とした翌月1日の日付情報を取得する
	 *
	 * @param targetDate
	 * @return
	 */
	public static LocalDate getFirstDateOfNextMonth(LocalDate baseDate) {

		return baseDate
				.plusMonths(1)
				.with(TemporalAdjusters.firstDayOfMonth());
	}

	/**
	 * 基準日を基準とした翌月の月末日の日付情報を取得する
	 *
	 * @param targetDate
	 * @return
	 */
	public static LocalDate getLastDateOfNextMonth(LocalDate baseDate) {

		return baseDate
				.plusMonths(1)
				.with(TemporalAdjusters.lastDayOfMonth());
	}

	/**
	 * 月初から対象日付までの日数を取得する。
	 *
	 * @param endDate 日数計算の終了日
	 * @param isIncludeCountOfEndDate 日数計算の終了日を日数として含めるかどうか（true:含める）
	 * @return
	 */
	public static Long getDayCountFromStartOfMonth(LocalDate endDate, boolean isIncludeCountOfEndDate) {

		// 月初から終了日までの日数
		Long diffDayCount = DateUtils.getDayCountFromFirstDayOfMonth(endDate);

		Long dayCountFromStartOfMonth = diffDayCount;

		if (isIncludeCountOfEndDate) {
			// 対象日の日数を加算
			dayCountFromStartOfMonth = dayCountFromStartOfMonth + 1;
		}

		return dayCountFromStartOfMonth;
	}

	/**
	 * 対象日付から月末までの日数を取得する。
	 *
	 * @param startDate 日数計算の開始日
	 * @param isIncludeCountOfStartDate 日数計算の開始日を日数として含めるかどうか（true:含める）
	 * @return
	 */
	public static Long getDayCountUntilEndOfMonth(LocalDate startDate, boolean isIncludeCountOfStartDate) {

		// 開始日から月末までの日数
		Long diffDayCount = DateUtils.getDayCountUntilLastDayOfMonth(startDate);

		Long dayCountUntilEndOfMonth = diffDayCount;

		if (isIncludeCountOfStartDate) {
			// 対象日の日数を加算
			dayCountUntilEndOfMonth = dayCountUntilEndOfMonth + 1;
		}

		return dayCountUntilEndOfMonth;
	}

	/**
	 * 開始日から終了日までの日数を取得する。
	 *
	 * @param startDate 日数計算の開始日
	 * @param endDate 日数計算の終了
	 * @param isIncludeCountOfStartDate 日数計算の開始日を日数として含めるかどうか（true:含める）
	 * @param isIncludeCountOfEndDate 日数計算の終了日を日数として含めるかどうか（true:含める）
	 * @return
	 */
	public static Long getDayCountStartToEnd(LocalDate startDate, LocalDate endDate, boolean isIncludeCountOfStartDate,
			boolean isIncludeCountOfEndDate) {

		// 開始日から終了日までの日数（開始日自体の1日分のカウントは含まない）
		Long diffDayCount = DateUtils.getDayCountDiff(startDate, endDate);

		Long dayCountStartToEnd = diffDayCount;

		if (isIncludeCountOfStartDate && isIncludeCountOfEndDate) {
			// 開始日と終了日の両方の日付のカウントを含める場合は、カウントされていない開始日自体の1日分のカウントを加算する
			dayCountStartToEnd = dayCountStartToEnd + 1;
		} else if (!isIncludeCountOfStartDate && !isIncludeCountOfEndDate) {
			// 開始日と終了日の両方の日付のカウントを含めるない場合は、カウントされている終了日自体の1日分のカウントを減算する
			dayCountStartToEnd = dayCountStartToEnd - 1;
		} else {
			// 開始日か終了日のどちらかの日数を含める場合は、すでにカウントされている開始日自体の1日分のカウントを適用することとし、何もしない。
		}

		return dayCountStartToEnd;
	}

	/**
	 * 対象日の月の日数を取得する
	 *
	 * @param targetDate 対象日
	 * @return
	 */
	public static Long getThisMonthDayCount(LocalDate targetDate) {

		// 1日から月末までの差分日数
		LocalDate firstDayOfMonth = targetDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate lastDayOfMonth = targetDate.with(TemporalAdjusters.lastDayOfMonth());
		Long diffDayCount = ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth);

		// 1日（ついたち）分の日数を加算
		int firstDayCount = 1;
		Long thisMonthDayCount = firstDayCount + diffDayCount;

		return thisMonthDayCount;
	}

	/**
	 * 現在日から対象日までの残日数を取得する<br>
	 * （現在日と対象日も日数のカウントに含む）
	 * 
	 * <pre>
	 * ※対象日が現在日より過去日の場合は0を返却する。
	 * </pre>
	 * 
	 * @param toDate
	 * @return
	 */
	public static Long getDaysCountFromNow(LocalDate toDate) {

		if (toDate == null) {
			return 0L;
		}

		LocalDate nowDate = LocalDate.now();

		boolean isIncludeCountOfStartDate = true;
		boolean isIncludeCountOfEndDate = true;
		Long dayCountStartToEnd = DateUtils.getDayCountStartToEnd(nowDate, toDate, isIncludeCountOfStartDate, isIncludeCountOfEndDate);

		if (dayCountStartToEnd < 0) {
			// カウントがマイナスの場合は0とする。
			dayCountStartToEnd = 0L;
		}

		return dayCountStartToEnd;
	}

	/**
	 * 対象日が今月の日時かどうかを判定する
	 *
	 * @param targetDateTime
	 * @return
	 */
	public static boolean isThisMonth(LocalDateTime targetDateTime) {

		boolean isThisMonth = false;

		// 対象日
		LocalDate targetDate = DateUtils.convertLocalDate(targetDateTime);

		LocalDate nowDate = LocalDate.now();
		// 現在月の月初
		LocalDate firstDayOfThisMonth = nowDate.with(TemporalAdjusters.firstDayOfMonth());
		// 現在月の月末
		LocalDate lastDayOfThisMonth = nowDate.with(TemporalAdjusters.lastDayOfMonth());

		if (!targetDate.isBefore(firstDayOfThisMonth) && !targetDate.isAfter(lastDayOfThisMonth)) {
			// 対象日が今月中の場合
			isThisMonth = true;
		}

		return isThisMonth;
	}

	/**
	 * 対象日が月初の日付かどうかを判定する
	 *
	 * @param targetDate
	 * @return
	 */
	public static boolean isMonthFirstDate(LocalDate targetDate) {

		boolean isMonthFirst = false;

		// 月初
		LocalDate firstDayOfTargetMonth = targetDate.with(TemporalAdjusters.firstDayOfMonth());

		if (targetDate.isEqual(firstDayOfTargetMonth)) {
			isMonthFirst = true;
		}

		return isMonthFirst;
	}

	/**
	 * 対象日が月末の日付かどうかを判定する
	 *
	 * @param targetDate
	 * @return
	 */
	public static boolean isMonthLastDate(LocalDate targetDate) {

		boolean isMonthLast = false;

		// 月末
		LocalDate lastDayOfTargetMonth = targetDate.with(TemporalAdjusters.lastDayOfMonth());

		if (targetDate.isEqual(lastDayOfTargetMonth)) {
			isMonthLast = true;
		}

		return isMonthLast;
	}

	/**
	 * 対象日付が同じ年月かを判定する
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isEqualYearMonth(TemporalAccessor date1, TemporalAccessor date2) {

		if (date1 == null || date2 == null) {
			return false;
		}

		YearMonth date1YM = YearMonth.from(date1);
		YearMonth date2YM = YearMonth.from(date2);

		if (date1YM.equals(date2YM)) {
			return true;
		}

		return false;
	}

	/**
	 * 日付の前後関係が正しいかを判定する（年月で比較する）<br>
	 *
	 * @param beforeDate
	 * @param afterDate
	 * @return beforeDateとafterDateの年月での前後関係が正しい場合（同じ年月の場合を含む）:true 左記ではない場合:false
	 */
	public static boolean isCorrectTimeContextYearMonth(LocalDate beforeDate, LocalDate afterDate) {
		return isCorrectTimeContextYearMonth(beforeDate, afterDate, false);
	}

	/**
	 * 日付の前後関係が正しいかを判定する（年月で比較する）<br>
	 * 
	 * @param beforeDate
	 * @param afterDate
	 * @param thisMonthExclusionFlg：当月を除外する:true、当月を含める:false
	 * @return
	 */
	public static boolean isCorrectTimeContextYearMonth(LocalDate beforeDate, LocalDate afterDate, boolean thisMonthExclusionFlg) {

		if (beforeDate == null || afterDate == null) {
			return false;
		}

		YearMonth beforeYM = YearMonth.from(beforeDate);
		YearMonth afterYM = YearMonth.from(afterDate);

		if (beforeYM.isBefore(afterYM) || beforeYM.equals(afterYM)) {
			// 当月かつ当月除外の場合はfalse
			if (beforeYM.equals(afterYM) && thisMonthExclusionFlg) {
				return false;
			}
			return true;
		}

		return false;
	}

	/**
	 * 日付の前後関係が正しいかを判定する (年月日で比較する) beforeDate, afterDateがどちらか片方でもNULLの場合はTRUEを返す
	 *
	 * @param beforeDate
	 * @param afterDate
	 * @return beforeDateとafterDateの日付での前後関係が正しい場合（同じ年月日の場合を含む）:true<br>
	 * 左記ではない場合:false
	 */
	public static boolean isCorrectDate(LocalDate beforeDate, LocalDate afterDate) {
		return isCorrectDate(beforeDate, afterDate, false);
	}

	/**
	 * 日付の前後関係が正しいかを判定する (年月日で比較する) beforeDate, afterDateがどちらか片方でもNULLの場合はTRUEを返す
	 * 
	 * @param beforeDate
	 * @param afterDate
	 * @param todayExclusionFlg：当日を除外する:true、当日を含める:false
	 * @return
	 */
	public static boolean isCorrectDate(LocalDate beforeDate, LocalDate afterDate, boolean todayExclusionFlg) {
		if (beforeDate != null && afterDate != null) {
			if (afterDate.isAfter(beforeDate) || afterDate.isEqual(beforeDate)) {
				// 当日かつ当日除外か判定
				if (afterDate.isEqual(beforeDate) && todayExclusionFlg) {
					// 当日除外する
					return false;
				}
				// afterDateがbeforeDateよりも後ろの日付、もしくは同じ日付の場合
				return true;
			} else {
				// afterDateがbeforeDateより前の場合
				return false;
			}
		}
		// beforeDateとafterDate両方、もしくはどちらかがNULLだった場合
		return true;
	}

	/**
	 * 時間の前後関係が正しいかを判定する (年月日時間で比較する) beforeTime, afterTimeがどちらか片方でもNULLの場合はTRUEを返す
	 *
	 * @param beforeTime
	 * @param afterTime
	 * @return beforeTimeとafterTimeの日付での前後関係が正しい場合（同じ時間の場合を含む）:true<br>
	 * 左記ではない場合:false
	 */
	public static boolean isCorrectTime(LocalTime beforeTime, LocalTime afterTime) {
		if (beforeTime != null && afterTime != null) {
			if (afterTime.isAfter(beforeTime) || afterTime.equals(beforeTime)) {
				// afterTimeがbeforeTimeよりも後ろの日付、もしくは同じ日付の場合
				return true;
			} else {
				// afterTimeがbeforeTimeより前の場合
				return false;
			}
		}
		// beforeTimeとafterTime両方、もしくはどちらかがNULLだった場合
		return true;
	}

	/**
	 * 日時の前後関係が正しいかを判定する beforeDateTime, afterDateTimeがどちらか片方でもNULLの場合はTRUEを返す
	 *
	 * @param beforeDateTime
	 * @param afterDateTime
	 * @return beforeDateTimeとafterDateTimeの日時での前後関係が正しい場合（同じ年月の場合を含む）:true<br>
	 * 左記ではない場合:false
	 */
	public static boolean isCorrectDateTime(LocalDateTime beforeDateTime, LocalDateTime afterDateTime) {
		if (beforeDateTime != null && afterDateTime != null) {
			if (afterDateTime.isAfter(beforeDateTime) || afterDateTime.isEqual(beforeDateTime)) {
				// afterDateTimeがbeforeDateTimeよりも後ろの日時、もしくは同じ日時の場合
				return true;
			} else {
				// afterDateTimeがbeforeDateTimeより前の場合
				return false;
			}
		}
		// beforeDateTimeとafterDateTime両方、もしくはどちらかがNULLだった場合
		return true;
	}

	/**
	 * 「From」 ~ 「To」の文字列に変換します。
	 *
	 * <pre>
	 * 例.
	 * 2018-04 , 2018-09 -> 「2018年4月 ~ 9月」
	 * 2017-04 , 2018-09 -> 「2017年4月 ~ 2018年9月」
	 * null , 2018-09 -> 「~ 2018年9月」
	 * </pre>
	 *
	 * @param from
	 * @param to
	 * @return 変換後文字列
	 */
	public static String parseToRangeMonth(LocalDate from, LocalDate to) {

		// どっちもNULL
		if (CommonUtils.allNull(from, to)) {
			return "";
		}

		// どっちかNULLの時
		if (CommonUtils.anyNull(from, to)) {
			return String.format("%s~%s", parseToString(from, DATE_JP_YYYY_MM), parseToString(to, DATE_JP_YYYY_MM));
		}

		boolean equalsYear = Objects.equals(from.getYear(), to.getYear());
		boolean equalsMonth = Objects.equals(from.getMonth(), to.getMonth());

		if (equalsYear && equalsMonth) {
			// 年月が同じ
			return parseToString(from, DATE_JP_YYYY_MM);
		} else if (equalsYear && !equalsMonth) {
			// 年が同じ 月は別
			return String.format("%s~%s", parseToString(from, DATE_JP_YYYY_MM), parseToString(to, DATE_MM) + "月");
		} else {
			// 年が別なのですべて表示
			return String.format("%s~%s", parseToString(from, DATE_JP_YYYY_MM), parseToString(to, DATE_JP_YYYY_MM));
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 月初から対象日までの日数を取得する。<br>
	 * （月初日は日数に含まない）<br>
	 * （対象日は日数に含む）
	 *
	 * @param targetDate
	 * @return 月初からの日数（targetDateが月初日の場合は0を返却）
	 */
	private static Long getDayCountFromFirstDayOfMonth(LocalDate targetDate) {

		LocalDate firstDayOfMonth = targetDate.with(TemporalAdjusters.firstDayOfMonth());
		Long diffDayCount = ChronoUnit.DAYS.between(firstDayOfMonth, targetDate);

		return diffDayCount;
	}

	/**
	 * 対象日から月末までの残日数を取得する。<br>
	 * （対象日は残日数に含まない）<br>
	 * （月末日は残日数に含む）
	 *
	 * @param targetDate
	 * @return 月末までの残日数（targetDateが月末日の場合は0を返却）
	 */
	private static Long getDayCountUntilLastDayOfMonth(LocalDate targetDate) {

		LocalDate lastDayOfMonth = targetDate.with(TemporalAdjusters.lastDayOfMonth());
		Long diffDayCount = ChronoUnit.DAYS.between(targetDate, lastDayOfMonth);

		return diffDayCount;
	}

	/**
	 * 開始日から終了日までの日数を取得する。<br>
	 * （開始日は日数に含まない）<br>
	 * （終了日は日数に含む）
	 **
	 * @param startDate
	 * @param endDate
	 * @return 開始日から終了日までの日数
	 */
	private static Long getDayCountDiff(LocalDate startDate, LocalDate endDate) {

		Long diffDayCount = ChronoUnit.DAYS.between(startDate, endDate);

		return diffDayCount;
	}

	/**
	 * YYYY年MM月DD日の形式で返す
	 *
	 * @param birthday
	 * @return
	 */
	public static String getJpDay(LocalDate ldt) {
		String val = null;
		if (ldt != null) {
			val = parseToString(ldt, DateUtils.DATE_JP_YYYY_MM_DD);
		}
		return val;
	}

	/**
	 * 年齢を返却
	 *
	 * @param birthDay 生年月日
	 * @param deathDate 死亡年月日
	 * @return
	 */
	public static Integer getAgeNumber(LocalDate birthDay, LocalDate deathDate) {
		Integer val = null;
		if (birthDay != null) {
			val = Period.between(birthDay, deathDate == null ? LocalDate.now() : deathDate).getYears();
		}
		return val;
	}

	/**
	 * (○歳)の形式で返す
	 *
	 * @param birthDay 生年月日
	 * @param deathDate 死亡年月日
	 * @return
	 */
	public static String getAge(LocalDate birthDay, LocalDate deathDate) {
		String val = null;
		if (birthDay != null) {
			val = "(" + String.valueOf(Period.between(birthDay, deathDate == null ? LocalDate.now() : deathDate).getYears()) + "歳)";
		}
		return val;
	}

	/**
	 * (和暦○年)の形式で返す
	 *
	 * @param ldt
	 * @return
	 */
	public static String getWarekiYear(LocalDate ldt) {
		String val = null;
		if (ldt != null) {
			DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
			builder.appendText(ChronoField.ERA, TextStyle.FULL);
			builder.appendText(ChronoField.YEAR_OF_ERA, Collections.singletonMap(1L, "元"));
			builder.appendLiteral("年");
			DateTimeFormatter formatter = builder.toFormatter(Locale.JAPAN).withChronology(JapaneseChronology.INSTANCE);
			val = "(" + formatter.format(JapaneseDate.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth())) + ")";
		}
		return val;
	}

	/**
	 * 和暦○の形式で返す<br>
	 * ※和暦は令和：R、平成：H、昭和：S、大正：T
	 * 
	 * @param ldt
	 * @return
	 */
	public static String getWarekiYearAlphabet(LocalDate ld) {
		var format = DateTimeFormatter.ofPattern("GGGGGy", Locale.JAPAN);
		var eraY = JapaneseDate.of(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
		return format.format(eraY);
	}

	/**
	 * 和暦○年○月○日の形式で返す
	 *
	 * @param ldt
	 * @return
	 */
	public static String getFullWarekiYear(LocalDate ldt) {
		String val = null;
		if (ldt != null) {
			DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
			builder.appendText(ChronoField.ERA, TextStyle.FULL);
			builder.appendText(ChronoField.YEAR_OF_ERA, Collections.singletonMap(1L, "元"));
			builder.appendLiteral("年");
			builder.appendValue(ChronoField.MONTH_OF_YEAR);
			builder.appendLiteral("月");
			builder.appendValue(ChronoField.DAY_OF_MONTH);
			builder.appendLiteral("日");
			DateTimeFormatter formatter = builder.toFormatter(Locale.JAPAN).withChronology(JapaneseChronology.INSTANCE);
			val = formatter.format(JapaneseDate.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth()));
		}
		return val;
	}

	/**
	 * 日付を和暦形式とした場合の年の値を取得する
	 * 
	 * @param localDate
	 * @return
	 */
	public static long getWarekiYearLong(LocalDate localDate) {
		JapaneseDate japaneseDate = JapaneseDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
		return japaneseDate.get(ChronoField.YEAR_OF_ERA);
	}

	/**
	 * 日付に対応したEraTypeのEnum値を取得する
	 * 
	 * @param now
	 * @return
	 */
	public static EraType getWarekiEraType(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		JapaneseDate japaneseDate = JapaneseDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
		JapaneseEra era = japaneseDate.getEra();
		return EraType.of(era);
	}

	/**
	 * datepickerの祝日Fromを取得する<br>
	 *
	 * @return 今年-2年の1月1日
	 */
	public static LocalDate getDatePickerHolidaysFrom() {
		var now = LocalDate.now();
		return LocalDate.of(now.minusYears(2).getYear(), 1, 1);
	}

	/**
	 * datepickerの祝日Fromを取得する<br>
	 *
	 * @return 今年-2年の12月31日
	 */
	public static LocalDate getDatePickerHolidaysTo() {
		var now = LocalDate.now();
		return LocalDate.of(now.plusYears(2).getYear(), 12, 31);
	}

}
