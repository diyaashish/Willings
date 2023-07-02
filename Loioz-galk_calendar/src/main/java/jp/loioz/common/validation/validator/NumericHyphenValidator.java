package jp.loioz.common.validation.validator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.NumericHyphen;

/**
 * NumericHyphenValidator
 * 数値バリデーション
 */
public class NumericHyphenValidator implements ConstraintValidator<NumericHyphen, String> {

	String item;

	@Override
	public void initialize(NumericHyphen numericHyphen) {
		item = numericHyphen.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isNumericHyphenValid(value);
	}

	/**
	 * 引数で指定した文字列が数字またはハイフンかを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isNumericHyphenValid(String value) {

		if (value.matches("^[-]?[0-9\\-]+$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}
}