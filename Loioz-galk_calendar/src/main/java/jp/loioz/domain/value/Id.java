package jp.loioz.domain.value;

import java.util.function.BiFunction;

import jp.loioz.common.utility.LoiozNumberUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * ID
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public abstract class Id<T extends Id<T>> extends Number implements CharSequence, Comparable<T> {

	private static final long serialVersionUID = 1L;

	/** 数値 */
	protected final Long numValue;

	/** 文字列 */
	protected final String strValue;

	/**
	 * 数値からIDを作成する
	 *
	 * @param numValue 数値
	 * @param constructor コンストラクタ
	 * @return ID
	 */
	protected static <T> T of(Number numValue, BiFunction<Long, String, T> constructor, String idType) {
		if (numValue == null) {
			numValue = Long.valueOf(0);
		}
		return constructor.apply(numValue.longValue(), idType + "-" + numValue.longValue());
	}

	/**
	 * 文字列からIDを作成する
	 *
	 * @param strValue 文字列
	 * @return ID
	 */
	protected static <T> T of(String strValue, BiFunction<Long, String, T> constructor, String idType) {
		Long numValue = LoiozNumberUtils.parseAsLong(strValue);
		return Id.of(numValue, constructor, idType);
	}

	/**
	 * このIDを数値として取得する
	 *
	 * @return IDの数値
	 */
	public Long asLong() {
		return numValue;
	}

	/**
	 * このIDを文字列として取得する
	 *
	 * @return IDの文字列
	 */
	@Override
	public String toString() {
		return strValue.toString();
	}

	// Comparable

	@Override
	public int compareTo(T other) {
		return this.numValue.compareTo(other.numValue);
	}

	// Number

	@Override
	public int intValue() {
		return numValue.intValue();
	}

	@Override
	public long longValue() {
		return numValue.longValue();
	}

	@Override
	public float floatValue() {
		return numValue.floatValue();
	}

	@Override
	public double doubleValue() {
		return numValue.doubleValue();
	}

	// CharSequence

	@Override
	public int length() {
		return strValue.length();
	}

	@Override
	public char charAt(int index) {
		return strValue.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return strValue.subSequence(start, end);
	}
}
