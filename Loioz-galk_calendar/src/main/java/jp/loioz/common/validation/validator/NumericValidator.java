package jp.loioz.common.validation.validator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.Numeric;

/**
 * NumericValidator
 * 数値バリデーション
 */
public class NumericValidator implements ConstraintValidator<Numeric, String> {

	String item;

	@Override
	public void initialize(Numeric numeric) {
		item = numeric.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isNumericValid(value);
	}

	/**
	 * 引数で指定した文字列が数字かを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isNumericValid(String value) {

		if (value.matches("^[-]?[0-9]+$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}
}