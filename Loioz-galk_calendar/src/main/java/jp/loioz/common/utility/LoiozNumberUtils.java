package jp.loioz.common.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

import jp.loioz.common.utility.data.LoiozDecimalFormat;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 数値用のUtilクラス
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LoiozNumberUtils {

	/**
	 * 文字列をIntegerに変換する<br>
	 * 変換できない場合はnullを返却する
	 *
	 * @param str 数値文字列
	 * @return 数値
	 */
	public static Integer parseAsInteger(String str) {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 文字列をLongに変換する<br>
	 * 変換できない場合はnullを返却する
	 *
	 * @param str 数値文字列
	 * @return 数値
	 */
	public static Long parseAsLong(String str) {
		try {
			return Long.valueOf(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 文字列をBigDecimalに変換する<br>
	 * 変換できない場合はnullを返却する
	 *
	 * @param str 数値文字列(カンマ区切り考慮)
	 * @return Decimal
	 */
	public static BigDecimal parseAsBigDecimal(String str) {

		if (str == null) {
			return null;
		}

		LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
		decimalFormat.setParseBigDecimal(true);
		try {
			return (BigDecimal) decimalFormat.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 数値をStringに変換する<br>
	 * 変換できない場合は空文字を返却する<br>
	 * (toStringメソッドだと、nullの時 "null" を返却してしまうので)
	 *
	 * @param num 数値
	 * @return String
	 */
	public static String parseAsString(Number num) {

		if (num == null) {
			return "";
		} else {
			return String.valueOf(num);
		}

	}

	/**
	 * 引数がnullの場合、BigDecimal.ZEROを返却
	 * 
	 * @param decimal
	 * @return
	 */
	public static BigDecimal nullToZero(BigDecimal decimal) {
		if (decimal == null) {
			return BigDecimal.ZERO;
		}
		return decimal;
	}

	/**
	 * 引数が0の場合、nullを返却
	 * 
	 * @param decimal
	 * @return
	 */
	public static BigDecimal zeroToNull(BigDecimal decimal) {
		if (decimal == null) {
			// nullの場合 -> そのまま返却
			return decimal;
		} else if (decimal.compareTo(BigDecimal.ZERO) == 0) {
			// 0の場合 -> nullを返却
			return null;
		}
		return decimal;
	}

	/**
	 * 数値が０かどうか
	 * 
	 * @param decimal
	 * @return
	 */
	public static boolean equalsZero(@NonNull BigDecimal decimal) {
		return decimal.compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 * 数値が同値かどうか
	 * 
	 * @param decimal
	 * @return
	 */
	public static boolean equalsDecimal(@NonNull BigDecimal target, @NonNull BigDecimal source) {
		return target.compareTo(source) == 0;
	}

	/**
	 * 数値の比較結果が基準値より大きいかどうか<br>
	 * 
	 * <pre>
	 * target > source = true
	 * target == source = false
	 * target < source = false
	 * <pre>
	 * 
	 * @param target
	 * @param source
	 * @return
	 */
	public static boolean isGreaterThan(@NonNull BigDecimal target, @NonNull BigDecimal source) {
		return target.compareTo(source) == 1;
	}

	/**
	 * 数値の比較結果が基準値より小さいかどうか<br>
	 * 
	 * <pre>
	 * target > source = false
	 * target == source = false
	 * target < source = true
	 * <pre>
	 * 
	 * @param target
	 * @param source
	 * @return
	 */
	public static boolean isLessThan(@NonNull BigDecimal target, @NonNull BigDecimal source) {
		return target.compareTo(source) == -1;
	}

	/**
	 * From < target < To のFrom値を取得する<br>
	 * 
	 * <pre>
	 * 例えば、intervalが10000の場合なら、1 ~ 10000, 10001 ~ 20000... の区分に分け、そのFrom値を取得する
	 * 上記の場合で、targetNumが 1 ~ 10000の値だった場合、1が返却され、10001 ~ 20000の値なら10001が返却される
	 * </pre>
	 * 
	 * @param targetNum
	 * @param interval
	 * @return
	 */
	public static Long getFrom(Long targetNum, Long interval) {

		BigDecimal intervalDecimal = new BigDecimal(interval);
		BigDecimal targetDecimal = new BigDecimal(targetNum);

		if (targetNum % intervalDecimal.longValue() == 0) {
			return targetDecimal.subtract(intervalDecimal).add(BigDecimal.ONE).longValue();
		}

		BigDecimal divided = targetDecimal.divide(intervalDecimal, RoundingMode.DOWN);
		return divided.multiply(intervalDecimal).add(BigDecimal.ONE).longValue();
	}

	/**
	 * From < target < To のTo値を取得する<br>
	 * 
	 * <pre>
	 * 例えば、intervalが10000の場合なら、1 ~ 10000, 10001 ~ 20000... の区分に分け、そのFrom値を取得する
	 * 上記の場合で、targetNumが 1 ~ 10000の値だった場合、10000が返却され、10001 ~ 20000の値なら20000が返却される
	 * </pre>
	 * 
	 * @param targetNum
	 * @param interval
	 * @return
	 */
	public static Long getTo(Long targetNum, Long interval) {

		BigDecimal intervalDecimal = new BigDecimal(interval);
		BigDecimal targetDecimal = new BigDecimal(targetNum);

		if (targetNum % intervalDecimal.longValue() == 0) {
			return targetNum.longValue();
		}

		BigDecimal divided = targetDecimal.divide(intervalDecimal, RoundingMode.DOWN);
		return divided.multiply(intervalDecimal).add(intervalDecimal).longValue();
	}

}
