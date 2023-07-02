package jp.loioz.common.utility;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.loioz.common.constant.DefaultEnum;

/**
 * バリデーションのUtilクラス アノテーションチェックできない場合のみ利用する
 */
public class ValidateUtils {

	/**
	 * 引数で指定した文字列が最大桁数に収まっているかを検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	public static boolean isMaxLengthValid(String value, int max) {

		if (StringUtils.isEmpty(value)) {
			// 値が空の場合
			return true;
		}

		if (value.length() > max) {
			// 最大桁数に収まっていない
			return false;
		} else {
			// 最大桁数に収まっている
			return true;
		}
	}

	/**
	 * 引数で指定した文字列が電話番号かを検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	public static boolean isTelPatternValid(String value) {

		if (StringUtils.isEmpty(value)) {
			// 値が空の場合
			return true;
		}

		if (value.matches("^[0-9\\-]{2,5}-[0-9]{1,4}-[0-9]{2,4}$") || value.matches("[0-9]{10}$")
				|| value.matches("[0-9]{11}$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}

	/**
	 * 引数で指定した文字列がメールアドレス形式かを検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	public static boolean isEmailPatternValid(String value) {

		if (StringUtils.isEmpty(value)) {
			// 値が空の場合
			return true;
		}

		if (value.matches("^([\\w])+([\\w\\._\\+-])*\\@([\\w])+([\\w\\._\\+-])*\\.([a-zA-Z])+$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;

		}
	}

	/**
	 * 引数で指定した文字列がひらがなかを検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	public static boolean isHiraganaValid(String value) {

		if (StringUtils.isEmpty(value)) {
			// 値が空の場合
			return true;
		}

		if (value.matches("^[\\u3040-\\u309F\\u0020\\u00A0\\u3000\\u30FC]+$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}

	/**
	 * 引数で指定した文字列に4バイト文字が含まれていないか検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	public static boolean is4ByteCheck(String value) {

		if (StringUtils.isEmpty(value)) {
			// 値が空の場合
			return true;
		}

		Pattern pattern = Pattern.compile("[\\u0000-\\uFFFF]*");
		Matcher m = pattern.matcher(value);
		if (!m.matches()) {
			// UTF-8の範囲を超える文字が存在する
			return false;
		}
		return true;
	}

	/**
	 * 0円ではないかを検証します
	 * 
	 * @param money
	 * @return 検証結果
	 */
	public static boolean isValidDecimalZero(BigDecimal money) {

		// nullの場合は検証しない
		if (money == null) {
			return true;
		}

		// 金額の同値チェック
		if (money.compareTo(BigDecimal.ZERO) == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 金額が一致するか検証します。
	 * 
	 * <pre>
	 * 例.予定額と入力金額の比較
	 * </pre>
	 * 
	 * @param 金額a
	 * @param 金額b
	 * @return 検証結果
	 */
	public static boolean isDecimalEqualValid(BigDecimal source, BigDecimal target) {

		// nullの場合は検証しない
		if (Objects.isNull(source) || Objects.isNull(target)) {
			return true;
		}

		// 金額の同値チェック
		if (source.compareTo(target) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * 金額の比較 比較対象が比較元よりも多いかどうかを検証します<br>
	 * 
	 * <pre>
	 * 比較元 < 比較対象 = true
	 * </pre>
	 * 
	 * @param source 比較元
	 * @param targer 比較対象
	 * @return 検証結果
	 */
	public static boolean isDecimalHigh(BigDecimal source, BigDecimal target) {

		// nullの場合は検証しない
		if (Objects.isNull(source) || Objects.isNull(target)) {
			return true;
		}

		// 金額の同値チェック
		if (source.compareTo(target) != -1) {
			return false;
		}
		return true;
	}

	/**
	 * 金額の比較 比較対象が比較元よりも少ないかどうかを検証します<br>
	 * 
	 * <pre>
	 * 比較元 > 比較対象 = true
	 * </pre>
	 * 
	 * @param source 比較元
	 * @param target 比較対象
	 * @return 検証結果
	 */
	public static boolean isDecimalLow(BigDecimal source, BigDecimal target) {

		// nullの場合は検証しない
		if (Objects.isNull(source) || Objects.isNull(target)) {
			return true;
		}

		// 金額の同値チェック
		if (source.compareTo(target) != 1) {
			return false;
		}
		return true;
	}

	/**
	 * 引数が金額値かどうかを検証します(カンマも許容とする)
	 * 
	 * @param decimalStr
	 * @return 検証結果
	 */
	public static boolean isDecimalFormatValid(String decimalStr) {

		// nullの場合は
		if (StringUtils.isEmpty(decimalStr)) {
			return true;
		}

		if (Objects.isNull(LoiozNumberUtils.parseAsBigDecimal(decimalStr))) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 引数で指定した文字列がformatで指定している形式かを検証します。
	 *
	 * @param value 検証する文字列
	 * @param format 日付フォーマット文字列
	 * @return チェック結果
	 */
	public static boolean isLocalDatePatternValid(String value, String format) {

		DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern(format);

		try {
			// 日付への変換処理が行えればフォーマットは適正と判断
			LocalDate.parse(value, localDateFormat.withResolverStyle(ResolverStyle.STRICT));
			return true;
		} catch (DateTimeParseException e) {
			// 不正なフォーマット
			return false;
		}
	}

	/**
	 * 引数の数値が0以下かを判定する
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isLess0NumValid(Number num) {

		if (num == null) {
			return true;
		}

		Long souce = num.longValue();

		// 0分との比較
		Long zero = Long.valueOf(0);
		int compareResult = souce.compareTo(Long.valueOf(zero));
		if (compareResult == -1 || compareResult == 0) {
			// 経過時間がマイナスの場合 or 経過時間が0分の場合
			return true;
		}

		return false;
	}

	/**
	 * 日付の前後関係が正しいかを検証します
	 *
	 * @param beforeDate
	 * @param afterDate
	 * @param acceptEquals 同日を許容するかどうか
	 * @return 検証結果
	 */
	public static boolean isCorrectDateValid(LocalDate beforeDate, LocalDate afterDate, boolean acceptEquals) {

		// どちらかがnullの場合、検証しない
		if (beforeDate == null || afterDate == null) {
			return true;
		}

		if (afterDate.isBefore(beforeDate)) {
			// afterDateがbeforeDateよりも前の日付 -> エラーケース
			return false;
		}

		if (!acceptEquals && afterDate.isEqual(beforeDate)) {
			// 同日を許容しない場合 -> エラーケース
			return false;
		}

		return true;
	}

	/**
	 * 日付の前後関係が正しいかを検証します
	 *
	 * @param beforeDate
	 * @param afterDate
	 * @param acceptEquals 同日時を許容するかどうか
	 * @return 検証結果
	 */
	public static boolean isCorrectDateTimeValid(LocalDateTime beforeDate, LocalDateTime afterDate, boolean acceptEquals) {

		// どちらかがnullの場合、検証しない
		if (beforeDate == null || afterDate == null) {
			return true;
		}

		if (afterDate.isBefore(beforeDate)) {
			// afterDateがbeforeDateよりも前の日付 -> エラーケース
			return false;
		}

		if (!acceptEquals && afterDate.isEqual(beforeDate)) {
			// 同日を許容しない場合 -> エラーケース
			return false;
		}

		return true;
	}

	/**
	 * 引数が存在するかの検証する(String考慮)
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isRequriedValid(Object object) {

		if (object == null) {
			return false; // nullなのでエラー
		}

		if (object instanceof String) {
			String valueTrimed = StringUtils.trim(object.toString());
			if (StringUtils.isEmpty(valueTrimed)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Enum値の整合性を検証する
	 * 
	 * @param <E>
	 * @param code
	 * @param clazz
	 * @return
	 */
	public static <E extends DefaultEnum> boolean isEnumTypeValid(String code, Class<E> clazz) {
		// 入力値が無い場合、検証しない
		if (StringUtils.isEmpty(code)) {
			return true;
		}
		boolean isMatched = Arrays.stream(clazz.getEnumConstants())
				.anyMatch(e -> e.equalsByCode(code));

		if (!isMatched) {
			// Enum値マッチしない場合はエラー
			return false;
		}

		return true;
	}

}