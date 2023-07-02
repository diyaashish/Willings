package jp.loioz.app.common.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 1引数関数によって初期化される遅延読込フォーム
 */
public class FunctionalLazyLoadForm<T, R> {

	/** フォーム作成処理 */
	private final Function<T, R> formCreator;

	private final Map<T, R> cache = new HashMap<>();

	public FunctionalLazyLoadForm(Function<T, R> formCreator) {
		this.formCreator = Objects.requireNonNull(formCreator);
	}

	synchronized public R get(T key) {
		if (!cache.containsKey(key)) {
			cache.put(key, formCreator.apply(key));
		}
		return cache.get(key);
	}
}