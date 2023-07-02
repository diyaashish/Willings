package jp.loioz.common.utility;

/**
 * 配列、プリミティブ配列用Utilクラス。
 */
public class LoiozArrayUtils {

	/**
	 * return org.apache.commons.lang3.ArrayUtils.contains(final Object[] array, final Object objectToFind)
	 * 
	 * @param array
	 * @param objectToFind
	 * @return
	 */
	public static boolean contains(final Object[] array, final Object objectToFind) {
		return org.apache.commons.lang3.ArrayUtils.contains(array, objectToFind);
	}

	/**
	 * return org.apache.commons.lang3.ArrayUtils.isEmpty(final Object[] array)
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(final Object[] array) {
		return org.apache.commons.lang3.ArrayUtils.isEmpty(array);
	}

	/**
	 * return org.apache.commons.lang3.ArrayUtils.addAll(final T[] array1, final T... array2)
	 * 
	 * @param <T>
	 * @param array1
	 * @param array2
	 * @return
	 */
	@SafeVarargs
	public static <T> T[] addAll(final T[] array1, final T... array2) {
		return org.apache.commons.lang3.ArrayUtils.addAll(array1, array2);
	}
}
