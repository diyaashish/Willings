package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.MaxDigit;

/**
 * MaxDigitValidator
 * 桁最大バリデーション
 */
public class MaxDigitValidator implements ConstraintValidator<MaxDigit, String> {
	int max;
	String item;

	@Override
	public void initialize(MaxDigit maxDigit) {
		max = maxDigit.max();
		item = maxDigit.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isMaxLengthValid(value);
	}

	/**
	 * 引数で指定した文字列が最大桁数に収まっているかを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isMaxLengthValid(String value) {

		if (value.length() > max) {
			// 最大桁数に収まっていない
			return false;
		} else {
			// 最大桁数に収まっている
			return true;
		}
	}
}