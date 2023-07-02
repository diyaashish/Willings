package jp.loioz.common.utility;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.unbescape.html.HtmlEscape;

import com.google.common.primitives.Longs;

import jp.loioz.common.constant.CommonConstant;

/**
 * 文字列のUtilクラス。
 */
public class StringUtils {

	/**
	 * 空文字判定
	 *
	 * @param str 文字列
	 * @return 判定結果
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * 空文字判定(文字配列)
	 *
	 * @param strs 文字配列
	 * @return 判定結果
	 */
	public static boolean isEmpty(String[] strs) {
		return strs == null || strs.length == 0;
	}

	/**
	 * null時に自動空文字変換
	 *
	 * <pre>
	 * StringUtils.defaultString(null)  = ""
	 * StringUtils.defaultString("")    = ""
	 * StringUtils.defaultString("bat") = "bat"
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public static String defaultString(String str) {
		return str == null ? CommonConstant.BLANK : str;
	}

	/**
	 * null時に自動Default文字変換
	 *
	 * <pre>
	 * StringUtils.defaultString(null, "NULL")  = "NULL"
	 * StringUtils.defaultString("", "NULL")    = ""
	 * StringUtils.defaultString("bat", "NULL") = "bat"
	 * </pre>
	 *
	 * @param str
	 * @param defaultStr
	 * @return
	 */
	public static String defaultString(String str, String defaultStr) {
		return str == null ? defaultStr : str;
	}

	/**
	 * すべての引数が空、もしくはnullがある場合にtureを返却する。
	 *
	 * @param strs
	 * @return
	 */
	public static boolean isAllEmpty(String... strs) {

		if (isEmpty(strs)) {
			return true;
		}

		for (String str : strs) {
			if (!isEmpty(str)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * どれか引数に一つでも空、もしくはnullがある場合にtureを返却する。
	 *
	 * @param strs
	 * @return
	 */
	public static boolean isExsistEmpty(String... strs) {

		if (isEmpty(strs)) {
			return true;
		}

		for (String str : strs) {
			if (isEmpty(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 空白除去
	 *
	 * @param str 文字列
	 * @return 空白を除去した文字列
	 */
	public static String trim(String str) {
		if (isEmpty(str)) {
			return str;
		}
		char[] charList = str.toCharArray();
		int length = str.length();
		int startIndex = 0;
		while (startIndex < length && (charList[startIndex] <= ' ' || charList[startIndex] == '　')) {
			startIndex++;
		}
		while (startIndex < length && (charList[length - 1] <= ' ' || charList[length - 1] == '　')) {
			length--;
		}
		if (startIndex == 0 && length == str.length()) {
			return str;
		}
		return str.substring(startIndex, length);
	}

	/**
	 * 全角スペース、半角スペース除去
	 *
	 * @param str 文字列
	 * @return 空白（全角スペース、半角スペース）を除去した文字列
	 */
	public static String removeSpaceCharacter(String str) {
		if (isEmpty(str)) {
			return str;
		}
		return removeAll(str, "　| ");
	}

	/**
	 * 文字列が空文字でないか判定する
	 *
	 * @param str 文字列
	 */
	public static boolean isNotEmpty(String str) {
		return (str != null && !"".equals(str));
	}

	/**
	 * org.apache.commons.lang3.StringUtils.isNoneEmpty(final CharSequence... css)
	 * 
	 * @param css
	 * @return
	 */
	public static boolean isNoneEmpty(final CharSequence... css) {
		return org.apache.commons.lang3.StringUtils.isNoneEmpty(css);
	}

	/**
	 * 文字列をリストに変換する(カンマ区切り)
	 *
	 * @param str
	 * @return
	 */
	public static List<String> toArray(String str) {
		if (isEmpty(str)) {
			return null;
		}
		// 空白区切りで分割
		return Arrays.asList(
				trim(str.replaceAll("　", " ")).split(","));
	}

	/**
	 * 文字列をLongのリストに変換する(カンマ区切り)
	 *
	 * @param str
	 * @return
	 */
	public static List<Long> toLongArray(String str) {
		if (isEmpty(str)) {
			return null;
		}
		// 空白区切りで分割
		return Longs.asList(
				Stream.of(str.replaceAll("　| ", "").split(",")).mapToLong(Long::parseLong).toArray());
	}

	/**
	 * リストを文字列に変換する
	 *
	 * @param strList
	 * @return
	 */
	public static String list2Str(List<String> strList) {
		if (LoiozCollectionUtils.isEmpty(strList)) {
			return null;
		}
		return String.join(" ", strList);
	}

	/**
	 * リストをカンマ区切りに変換する
	 *
	 * @param strList
	 * @return
	 */
	public static String list2Comma(List<String> strList) {
		if (LoiozCollectionUtils.isEmpty(strList)) {
			return null;
		}
		return String.join(CommonConstant.COMMA, strList);
	}

	/**
	 * リストをカンマ区切り+半角スペースに変換する
	 *
	 * @param strList
	 * @return
	 */
	public static String list2CommaSP(List<String> strList) {
		if (LoiozCollectionUtils.isEmpty(strList)) {
			return null;
		}
		return String.join(CommonConstant.COMMA_SP, strList);
	}

	/**
	 * 文字列をリストに変換する
	 *
	 * @param str
	 * @param delimiter
	 * @return
	 */
	public static List<String> toArray(String str, String delimiter) {
		if (isEmpty(str)) {
			return null;
		}

		return Arrays.asList(
				trim(str.replaceAll("　", " ")).split(delimiter));
	}

	/**
	 * 文字列をリストに変換する(トリム処理無し)
	 *
	 * @param str
	 * @param delimiter
	 * @return
	 */
	public static List<String> toArrayNoTrim(String str, String delimiter) {
		if (isEmpty(str)) {
			return null;
		}
		return Arrays.asList(str.split(delimiter));
	}

	/**
	 * 数値のカンマ区切り文字列をList<Long>型に変換する
	 *
	 * @param csvStr
	 * @return
	 */
	public static List<Long> toListLong(String csvStr) {
		/* 文字列がnullならnullを返却 */
		if (StringUtils.isEmpty(csvStr)) {
			return null;
		}
		return Arrays.asList(csvStr.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
	}

	/**
	 * nullを空文字に変換
	 *
	 * @param str
	 * @return
	 */
	public static String null2blank(String str) {
		if (str == null) {
			return CommonConstant.BLANK;
		}
		return str;
	}

	/**
	 * nullを空文字に変換
	 *
	 * @param l
	 * @return
	 */
	public static String null2blank(Long l) {
		if (l == null) {
			return CommonConstant.BLANK;
		}
		return l.toString();
	}

	/**
	 * ダブルコーテーションで囲む
	 *
	 * @param value
	 * @return
	 */
	public static String surroundDoubleCoat(String value) {
		String str = null;
		if (value == null) {
			return "\"\"";
		} else {
			str = "\"" + value + "\"";
		}
		return str;
	}

	/**
	 * カッコで囲む
	 *
	 * @param value
	 * @return
	 */
	public static String surroundBrackets(String value, boolean null2blank) {

		String str = null;
		if (null2blank) {
			str = null2blank(value);
		} else {
			str = value;
		}

		if (isEmpty(str)) {
			return str;
		} else {
			return "(" + value + ")";
		}
	}

	/**
	 * 左詰
	 *
	 * @param str 対象文字
	 * @param size サイズ
	 * @param padStr 埋める文字
	 * @return
	 */
	public static String leftPad(final String str, final int size, String padStr) {
		return org.apache.commons.lang3.StringUtils.leftPad(str, size, padStr);
	}

	/**
	 * 右詰
	 *
	 * @param str 対象文字
	 * @param size サイズ
	 * @param padStr 埋める文字
	 * @return
	 */
	public static String rightPad(final String str, final int size, String padStr) {
		return org.apache.commons.lang3.StringUtils.rightPad(str, size, padStr);
	}

	/**
	 * 全角から半角変換(数字)
	 *
	 * @param str 変換前文字列
	 * @return 変換後文字列
	 */
	public static String convertFullToHalfNum(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ('０' <= c && c <= '９') {
				sb.setCharAt(i, (char) (c - '０' + '0'));
			}
		}
		return sb.toString();
	}

	/**
	 * 半角から全角変換(数字)
	 *
	 * @param str 変換前文字列
	 * @return 変換後文字列
	 */
	public static String convertHalfToFullNum(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ('0' <= c && c <= '9') {
				sb.setCharAt(i, (char) (c - '0' + '０'));
			}
		}
		return sb.toString();
	}

	/**
	 * 区切り文字で文字列リストを結合する
	 *
	 * @param delimiter
	 * @param stringList
	 * @return
	 */
	public static String join(String delimiter, List<String> stringList) {

		if (delimiter == null || stringList == null || stringList.isEmpty()) {
			return "";
		}

		return String.join(delimiter, stringList);
	}

	/**
	 * 引数で指定した文字列に4バイト文字が含まれていないか検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	public static boolean is4ByteCheck(String value) {
		Pattern pattern = Pattern.compile("[\\u0000-\\uFFFF]*");
		Matcher m = pattern.matcher(value);
		if (!m.matches()) {
			// UTF-8の範囲を超える文字が存在する
			return false;
		}
		return true;
	}

	/**
	 * 引数が全て文字があるかを確認します。
	 *
	 * @param css
	 * @return
	 */
	public static boolean isAnyEmpty(String... css) {
		if (LoiozArrayUtils.isEmpty(css)) {
			return false;
		}
		for (String cs : css) {
			if (isEmpty(cs)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 対象文字を削除する
	 *
	 * @param originalStr
	 * @param removeChar
	 * @return
	 */
	public static String removeChars(String originalStr, String removeTargetChar) {
		if (StringUtils.isEmpty(originalStr)) {
			return "";
		}

		if (StringUtils.isEmpty(removeTargetChar)) {
			return originalStr;
		}

		return originalStr.replace(removeTargetChar, "");
	}

	/**
	 * 対象文字を削除する
	 *
	 * @param originalStr
	 * @param removeCharList
	 * @return
	 */
	public static String removeChars(String originalStr, List<String> removeTargetCharList) {
		if (StringUtils.isEmpty(originalStr)) {
			return "";
		}

		if (LoiozCollectionUtils.isEmpty(removeTargetCharList)) {
			return originalStr;
		}

		for (String removeTargetChar : removeTargetCharList) {
			originalStr = removeChars(originalStr, removeTargetChar);
		}
		return originalStr;
	}

	/**
	 * 指定文字数以降の文字列を削除する
	 *
	 * @param originalStr
	 * @param removeTargetIdx
	 * @param addStr
	 * @return
	 */
	public static String removeCharIdx(String originalStr, int removeTargetIdx) {
		return removeCharIdx(originalStr, removeTargetIdx, null);
	}

	/**
	 * 指定文字数以降の文字列を削除する ※オプションで文字を追加する
	 *
	 * @param originalStr
	 * @param removeTargetIdx
	 * @param addStr
	 * @return
	 */
	public static String removeCharIdx(String originalStr, int removeTargetIdx, String addStr) {

		String str = CommonConstant.BLANK;
		if (StringUtils.isEmpty(originalStr)) {
			return str;
		}
		int len = originalStr.length();
		if (len < removeTargetIdx) {
			str = originalStr;
		} else {
			str = originalStr.substring(0, removeTargetIdx);
			if (StringUtils.isNotEmpty(addStr)) {
				str += addStr;
			}
		}
		return str;
	}

	/**
	 * 指定文字以降の文字列を削除する
	 *
	 * @param originalStr
	 * @param removeTargetChar
	 * @return
	 */
	public static String removeCharAndAfter(String originalStr, String removeTargetChar) {

		if (StringUtils.isEmpty(originalStr)) {
			return "";
		}

		if (StringUtils.isEmpty(removeTargetChar)) {
			return originalStr;
		}

		int removeSteartIndex = originalStr.indexOf(removeTargetChar);

		if (removeSteartIndex == -1) {
			// 対象の文字が文字列に含まれていない
			return originalStr;
		} else if (removeSteartIndex == 0) {
			// 最初の文字からすべて削除
			return "";
		}

		return originalStr.substring(0, removeSteartIndex);
	}

	/**
	 * 改行コードを改行文字列に置換
	 *
	 * @param text
	 * @return
	 */
	public static String lineBreakCode2Str(String text) {

		if (StringUtils.isEmpty(text)) {
			return "";
		}

		return text.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
	}

	/**
	 * 改行文字を改行コードに置換
	 *
	 * @param text
	 * @return
	 */
	public static String lineBreakStr2Code(String text) {

		if (StringUtils.isEmpty(text)) {
			return "";
		}

		return text.replaceAll("\\\\r", "\r").replaceAll("\\\\n", "\n");
	}

	/**
	 * 改行コードを<br>
	 * に置換
	 * 
	 * @param text
	 * @return
	 */
	public static String lineBreakCode2Br(String text) {

		if (StringUtils.isEmpty(text)) {
			return "";
		}

		return Pattern.compile("(\r\n|\n)").matcher(text).replaceAll("<br>");
	}

	/**
	 * 文字列をthymeleafと同じ方式でエスケープする
	 *
	 * @param input エスケープ前の文字列
	 * @return エスケープ後の文字列
	 */
	public static String escapeHtml(String input) {

		if (StringUtils.isEmpty(input)) {
			return "";
		}

		return HtmlEscape.escapeHtml4Xml(input);
	}

	// ==================================================
	// 以下、本プロジェクトでしか使わない可能性がある。別クラスに分けたほうが良いかも？
	// ==================================================

	/**
	 * 郵便番号を画面表示用に変換
	 *
	 * <pre>
	 * 例 <br>
	 * "150-0011" -> "〒150-0011"<br>
	 * "" -> "" <br>
	 * null -> "" <br>
	 * </pre>
	 *
	 * @param zipCode
	 * @return 画面表示用郵便番号
	 */
	public static String convertToDispZipCode(String zipCode) {
		if (StringUtils.isEmpty(zipCode)) {
			return "";
		} else {
			return String.format("%s%s", "〒", zipCode);
		}
	}

	/**
	 * 文字を指定位置まで抽出して返す
	 * 
	 * @param input
	 * @param endIdx
	 * @return
	 */
	public static String subString(String input, int endIdx) {
		return subString(input, 0, endIdx);
	}

	/**
	 * 文字を指定位置から指定位置までを抽出して返す
	 * 
	 * @param input
	 * @param staIdx
	 * @param endIdx
	 * @return
	 */
	public static String subString(String input, int staIdx, int endIdx) {
		String val = null;
		if (StringUtils.isEmpty(input)) {
			val = CommonConstant.BLANK;
		} else {
			if (input.length() < staIdx) {
				staIdx = 0;
			}
			// endIdxに0を指定した場合は、文字の最後まで返す
			if (input.length() < endIdx || endIdx == 0) {
				endIdx = input.length();
			}
			val = input.substring(staIdx, endIdx);
		}
		return val;
	}

	/**
	 * 文字を指定位置まで抽出可能か返す
	 * 
	 * @param input
	 * @param endIdx
	 * @return
	 */
	public static boolean isSubString(String input, int endIdx) {
		boolean flg = false;
		if (!StringUtils.isEmpty(input)) {
			if (input.length() > endIdx || endIdx == 0) {
				flg = true;
			}
		}
		return flg;
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.repeat(final String str, final int repeat)
	 * 
	 * @param str
	 * @param repeat
	 * @return
	 */
	public static String repeat(final String str, final int repeat) {
		return org.apache.commons.lang3.StringUtils.repeat(str, repeat);
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.split(final String str, final String separatorChars)
	 * 
	 * @param str
	 * @param separatorChars
	 * @return
	 */
	public static String[] split(final String str, final String separatorChars) {
		return org.apache.commons.lang3.StringUtils.split(str, separatorChars);
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.replace(final String text, final String searchString, final String replacement)
	 * 
	 * @param text
	 * @param searchString
	 * @param replacement
	 * @return
	 */
	public static String replace(final String text, final String searchString, final String replacement) {
		return org.apache.commons.lang3.StringUtils.replace(text, searchString, replacement);
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.isBlank(final CharSequence cs)
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean isBlank(final CharSequence cs) {
		return org.apache.commons.lang3.StringUtils.isBlank(cs);
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.contains(final CharSequence seq, final CharSequence searchSeq)
	 * 
	 * @param seq
	 * @param searchSeq
	 * @return
	 */
	public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
		return org.apache.commons.lang3.StringUtils.contains(seq, searchSeq);
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.stripToNull(String str)
	 * @param str
	 * @return
	 */
	public static String stripToNull(String str) {
		return org.apache.commons.lang3.StringUtils.stripToNull(str);
	}

	/**
	 * return org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(final int count)
	 * 
	 * @param count
	 * @return
	 */
	public static String randomAlphanumeric(final int count) {
		return org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(count);
	}

	/**
	 * return org.apache.commons.lang3.RandomStringUtils.randomNumeric(final int count)
	 * 
	 * @param count
	 * @return
	 */
	public static String randomNumeric(final int count) {
		return org.apache.commons.lang3.RandomStringUtils.randomNumeric(count);
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.removeAll(final String text, final String regex)
	 * 
	 * @param text
	 * @param regex
	 * @return
	 */
	public static String removeAll(String text, String regex) {
		return org.apache.commons.lang3.StringUtils.removeAll(text, regex);
	}

	/**
	 * return org.apache.commons.lang3.StringUtils.defaultIfEmpty(final T str, final T defaultStr)
	 * 
	 * @param <T>
	 * @param str
	 * @param defaultStr
	 * @return
	 */
	public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultStr) {
		return org.apache.commons.lang3.StringUtils.defaultIfEmpty(str, defaultStr);
	}

	public static boolean isMinusDicimalStr(String dicemalStr) {
		BigDecimal dicimal = LoiozNumberUtils.parseAsBigDecimal(dicemalStr);
		if (dicimal == null) {
			return false;
		}
		return BigDecimal.ZERO.compareTo(dicimal) == 1;
	}

}