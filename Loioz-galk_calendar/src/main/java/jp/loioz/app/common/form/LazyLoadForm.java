package jp.loioz.app.common.form;

import java.util.Objects;
import java.util.function.Supplier;

import org.thymeleaf.context.LazyContextVariable;

/**
 * 遅延読込フォーム
 */
public class LazyLoadForm<T> extends LazyContextVariable<T> {

	/** フォーム作成処理 */
	private final Supplier<T> formCreator;

	public LazyLoadForm(Supplier<T> formCreator) {
		this.formCreator = Objects.requireNonNull(formCreator);
	}

	@Override
	protected T loadValue() {
		return formCreator.get();
	}
}