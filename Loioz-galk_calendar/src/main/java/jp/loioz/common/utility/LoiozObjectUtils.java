package jp.loioz.common.utility;

/**
 * Object用Utilクラス。
 */
public class LoiozObjectUtils {

	/**
	 * return org.apache.commons.lang3.ObjectUtils.defaultIfNull(final T object, final T defaultValue)
	 * 
	 * @param <T>
	 * @param object
	 * @param defaultValue
	 * @return
	 */
	public static <T> T defaultIfNull(final T object, final T defaultValue) {
		return org.apache.commons.lang3.ObjectUtils.defaultIfNull(object, defaultValue);
	}

	/**
	 * return org.apache.commons.lang3.ObjectUtils.allNotNull(final Object... values)
	 * 
	 * @param values
	 * @return
	 */
	public static boolean allNotNull(final Object... values) {
		return org.apache.commons.lang3.ObjectUtils.allNotNull(values);
	}

	/**
	 * return org.apache.commons.lang3.ObjectUtils.firstNonNull(final T... values)
	 * 
	 * @param <T>
	 * @param values
	 * @return
	 */
	@SafeVarargs
	public static <T> T firstNonNull(final T... values) {
		return org.apache.commons.lang3.ObjectUtils.firstNonNull(values);
	}

	/**
	 * return org.apache.commons.lang3.ObjectUtils.max(final T... values)
	 * 
	 * @param <T>
	 * @param values
	 * @return
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> T max(final T... values) {
		return org.apache.commons.lang3.ObjectUtils.max(values);
	}

	/**
	 * return org.apache.commons.lang3.ObjectUtils.min(final T... values)
	 * 
	 * @param <T>
	 * @param values
	 * @return
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> T min(final T... values) {
		return org.apache.commons.lang3.ObjectUtils.min(values);
	}

}
