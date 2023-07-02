package jp.loioz.common.utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import jp.loioz.common.constant.CommonConstant;

/**
 * 共通系のUtilクラス。
 */
public class CommonUtils {

	/**
	 * 指定文字列が"true"なら"1"を返す。
	 *
	 * @param str 指定文字列
	 * @return 1 or 0
	 */
	public static String stringBoolanToFlg(String str) {
		return str.equals("true") ? "1" : "0";
	}

	/**
	 * 指定文字列が""1""なら"true"を返す。
	 *
	 * @param str 指定文字列
	 * @return "true" or "false"
	 */
	public static String flgToStringBoolan(String str) {
		return str.equals("1") ? "true" : "false";
	}

	/**
	 * 呼び出し元のクラス名とメソッド名を以下の形式で取得します。
	 *
	 * クラス名#メソッド名
	 *
	 * @return メソッド名 - クラス名
	 */
	public static String getCurrentExecuteClassAndMethodName() {

		// 対象メソッドのスタックトレース情報を取得
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];

		// 完全修飾クラス名から、クラス名のみを取得
		String fullyQualifiedClassName = stackTraceElement.getClassName();
		List<String> names = StringUtils.toArray(fullyQualifiedClassName, Pattern.quote("."));
		String className = names.get(names.size() - 1);

		// メソッド名を取得
		String methodName = stackTraceElement.getMethodName();

		return methodName + " - " + className;
	}

	/**
	 * メールアドレス形式に変換
	 *
	 * @param addrStr
	 * @return
	 */
	public static InternetAddress convertAddress(String addrStr) {
		return convertAddress(addrStr, null);
	}

	/**
	 * メールアドレス形式に変換
	 *
	 * @param addrStr
	 * @param fromName
	 * @return
	 */
	public static InternetAddress convertAddress(String addrStr, String fromName) {
		InternetAddress addr = null;
		try {
			addr = new InternetAddress(addrStr, fromName);
		} catch (Exception e) {
			// 何もしない
		}
		return addr;
	}

	/**
	 * メールアドレス形式に変換
	 *
	 * @param addr(複数の場合、カンマ区切り)
	 * @return
	 */
	public static List<InternetAddress> convertAddressList(String addr) {
		List<String> addrStrList = StringUtils.toArray(addr);
		return convertAddressList(addrStrList);
	}

	/**
	 * メールアドレス形式に変換
	 *
	 * @param addrStrList
	 * @return
	 */
	public static List<InternetAddress> convertAddressList(List<String> addrStrList) {
		return convertAddressList(addrStrList, Collections.emptyMap());
	}

	/**
	 * メールアドレス形式に変換
	 *
	 * @param addrStrList
	 * @param addrKeyNameMap
	 * @return
	 */
	public static List<InternetAddress> convertAddressList(List<String> addrStrList, Map<String, String> addressKeyNameMap) {

		if (addrStrList == null) {
			return null;
		}

		Map<String, String> addrKeyNameMap = addressKeyNameMap == null ? Collections.emptyMap() : addressKeyNameMap;

		// 入力値に応じて変換する
		List<InternetAddress> tmpList = new ArrayList<InternetAddress>();
		// メールアドレス形式に変換
		addrStrList.forEach(s -> {
			try {
				String name = addrKeyNameMap.get(s);
				if (StringUtils.isEmpty(name)) {
					InternetAddress addr = new InternetAddress(s);
					tmpList.add(addr);
				} else {
					try {
						// メールアドレスに名前を設定
						InternetAddress addr = new InternetAddress(s, name, "utf-8");
						tmpList.add(addr);
					} catch (UnsupportedEncodingException e) {
						// 名前のエンコーディングでコケた場合 -> 名前はアドレスのみ設定
						InternetAddress addr = new InternetAddress(s);
						tmpList.add(addr);
					}
				}
			} catch (AddressException e) {
				// 何もしない
			}
		});

		// メールアドレスを返す
		return tmpList;
	}

	/**
	 * メールアドレス形式に変換
	 *
	 * @param addrStrList
	 * @return
	 */
	public static InternetAddress[] addressListToArray(List<InternetAddress> addrList) {
		InternetAddress[] array = null;
		if (addrList != null) {
			array = addrList.toArray(new InternetAddress[]{});
		}
		return array;
	}

	/**
	 * メールアドレス形式 -> 文字列形式
	 *
	 * @param addr
	 * @return
	 */
	public static String addressListToStr(InternetAddress addr) {
		String mailAddrStr = null;
		if (addr != null) {
			mailAddrStr = addr.getAddress();
		}
		return mailAddrStr;
	}

	/**
	 * メールアドレス形式 -> 文字列形式(カンマ形式)
	 *
	 * @param addrList
	 * @return
	 */
	public static String addressListToStr(List<InternetAddress> addrList) {
		String mailAddrStr = null;
		if (addrList != null) {
			StringBuilder sb = new StringBuilder();
			addrList.forEach(s -> sb.append(s.toString() + ","));
			sb.deleteCharAt(sb.lastIndexOf(","));
			mailAddrStr = sb.toString();
		}
		return mailAddrStr;
	}

	/**
	 * 数値形式に変換
	 *
	 * @param moneyFormatStr
	 * @return
	 */
	public static String convertMoney2Num(String moneyFormatStr) {

		if (StringUtils.isEmpty(moneyFormatStr)) {
			return moneyFormatStr;
		}

		moneyFormatStr = StringUtils.removeChars(moneyFormatStr, ",");

		return moneyFormatStr;
	}

	/**
	 * 候補の中からnullでない最初の要素を取得する<br>
	 * 候補の中に優先値が含まれていれば、順番にかかわらず優先値を取得する<br>
	 * 候補が全てnullの場合は、nullを返却する
	 *
	 * @param primary 優先値
	 * @param candidates 候補
	 * @return nullでない最初の要素
	 */
	@SafeVarargs
	public static <T> T firstNonNullOrPrimary(T primary, T... candidates) {

		if (primary != null && LoiozArrayUtils.contains(candidates, primary)) {
			return primary;
		}

		return LoiozObjectUtils.firstNonNull(candidates);
	}

	/**
	 * 候補の中からnullでない最初の要素を取得する<br>
	 * 候補の中に優先値が含まれていれば、順番にかかわらず優先値を取得する<br>
	 * 候補が全てnullの場合は、nullを返却する
	 *
	 * @param primary 優先値
	 * @param candidates 候補
	 * @return nullでない最初の要素
	 */
	@SafeVarargs
	public static <T> T firstNonNullOrPrimary(List<T> primary, T... candidates) {

		if (candidates == null) {
			return null;
		}
		if (primary != null) {
			Collection<T> intersection = LoiozCollectionUtils.intersection(primary, Arrays.asList(candidates));
			if (LoiozCollectionUtils.isNotEmpty(intersection)) {
				Optional<T> firstNonNullIntersection = intersection.stream()
						.filter(Objects::nonNull)
						.findFirst();
				if (firstNonNullIntersection.isPresent()) {
					return firstNonNullIntersection.get();
				}
			}
		}

		return LoiozObjectUtils.firstNonNull(candidates);
	}

	/**
	 * 候補の中から空文字列でない最初の文字列を取得する<br>
	 * 候補が全て空文字列の場合は、nullを返却する
	 *
	 * @param candidates 候補
	 * @return 空文字列でない最初の文字列
	 */
	public static String firstNotEmpty(String... candidates) {
		if (candidates == null) {
			return null;
		}
		return Arrays.stream(candidates)
				.filter(StringUtils::isNotEmpty)
				.findFirst()
				.orElse(null);
	}

	/**
	 * 候補の中から空文字列でない最初の文字列を取得する<br>
	 * 候補の中に優先値が含まれていれば、順番にかかわらず優先値を取得する<br>
	 * 候補が全て空文字列の場合は、nullを返却する
	 *
	 * @param primary 優先値
	 * @param candidates 候補
	 * @return 空文字列でない最初の文字列
	 */
	public static String firstNotEmptyOrPrimary(String primary, String... candidates) {

		if (StringUtils.isNotEmpty(primary) && LoiozArrayUtils.contains(candidates, primary)) {
			return primary;
		}

		return firstNotEmpty(candidates);
	}

	/**
	 * すべての要素がnullであるか判定する
	 *
	 * @param values
	 * @return
	 */
	public static boolean allNull(Object... values) {

		if (values == null) {
			return true;
		}

		for (Object val : values) {
			if (!Objects.isNull(val)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * どれか一つにnullであるか判定する
	 *
	 * @param values
	 * @return
	 */
	public static boolean anyNull(Object... values) {

		if (values == null) {
			return true;
		}

		for (Object val : values) {
			if (Objects.isNull(val)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 名前の形式に変換して返す
	 *
	 * @param sei
	 * @param mei
	 * @return
	 */
	public static String nameFormat(String sei, String mei) {
		String name = String.format(
				"%s%s%s",
				jp.loioz.common.utility.StringUtils.null2blank(sei),
				CommonConstant.SPACE,
				jp.loioz.common.utility.StringUtils.null2blank(mei));
		return jp.loioz.common.utility.StringUtils.trim(name);
	}

}
