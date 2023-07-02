package jp.loioz.common.constant;

import java.util.Arrays;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * コードと値を保持する形式のEnumの仕様
 */
public interface DefaultEnum {

	/** コード値を返却する */
	@JsonValue
	String getCd();

	/** 値を返却する */
	String getVal();

	/**
	 * [デフォルト実装] コード値が同一かどうかをチェックする
	 *
	 * @param code
	 * @return
	 */
	default boolean equalsByCode(String code) {
		return getCd().equals(code);
	}

	/**
	 * [staticメソッド] 指定されたCodeValueEnumを実装したEnumの、指定されたコード値の列挙子を返却する
	 *
	 * @param clazz
	 * @param code
	 * @return
	 */
	static <E extends DefaultEnum> E getEnum(Class<E> clazz, String code) {
		return Arrays.stream(clazz.getEnumConstants())
			.filter(e -> e.equalsByCode(code))
			.findFirst()
			.orElse(null);
	}

	/**
	 * [staticメソッド]　指定されたCodeValueEnumに、指定されたコード値を持つ列挙子が存在するかチェックする
	 *
	 * @param clazz
	 * @param code
	 * @return
	 */
	static boolean hasCode(Class<? extends DefaultEnum> clazz, String code) {
		return Arrays.stream(clazz.getEnumConstants())
			.anyMatch(e -> e.equalsByCode(code));
	}

	/**
	 * DefaultEnumのコード値をnullセーフで取得する
	 *
	 * @param defaultEnum Enum
	 * @return Enumの値<br>
	 * Enumがnullの場合はnull
	 */
	static String getCd(@Nullable DefaultEnum defaultEnum) {
		if (defaultEnum == null) {
			return null;
		}
		return defaultEnum.getCd();
	}

	/**
	 * DefaultEnumの値をnullセーフで取得する
	 *
	 * @param defaultEnum Enum
	 * @return Enumの値<br>
	 * Enumがnullの場合はnull
	 */
	static String getVal(@Nullable DefaultEnum defaultEnum) {
		if (defaultEnum == null) {
			return null;
		}
		return defaultEnum.getVal();
	}
}
