package jp.loioz.domain.condition;

import java.util.Collection;
import java.util.stream.Stream;

import jp.loioz.common.utility.StringUtils;

/**
 * データソート条件
 */
public abstract class SortCondition {

	/**
	 * ソート条件を保持しているか判定する
	 *
	 * @return true:ソート条件あり / false:ソート条件なし
	 */
	public boolean hasCondition() {
		return Stream
				.of(this.getClass().getDeclaredFields()) // フィールド定義を取得
				.map(field -> {
					// フィールドの値を取得
					try {
						field.setAccessible(true);
						return field.get(this);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
				// 値が設定されている項目が存在するか判定する
				.anyMatch(this::isNotEmpty);
	}

	/**
	 * ソート条件項目が設定されているか判定する
	 *
	 * @param value ソート条件項目
	 * @return true:設定されている / false:未設定
	 */
	public boolean isNotEmpty(Object value) {
		if (value instanceof SortCondition) {
			// ネストした検索条件
			return value != null && ((SortCondition) value).hasCondition();

		} else if (value instanceof Collection) {
			// 複数指定が可能な項目は中身が空でないか判定
			return value != null && ((Collection<?>) value)
					.stream()
					.anyMatch(this::isNotEmpty);

		} else if (value instanceof String) {
			// 空文字列は未設定扱いとする
			return StringUtils.isNotEmpty((String) value);

		} else if (value instanceof Boolean) {
			// Boolean項目のfalseは未設定扱いとする
			return Boolean.TRUE.equals((Boolean) value);

		} else {
			return value != null;
		}
	}
}
