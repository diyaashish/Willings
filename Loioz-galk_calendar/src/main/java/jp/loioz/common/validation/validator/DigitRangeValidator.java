package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.DigitRange;

/**
 * DigitRangeValidator
 * 桁範囲バリデーション
 */
public class DigitRangeValidator implements ConstraintValidator<DigitRange, String> {

	int min;
	int max;
	String item;

	@Override
	public void initialize(DigitRange digitRange) {
		min = digitRange.min();
		max = digitRange.max();
		item = digitRange.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isDigitRangeValid(value);
	}

	/**
	 * 引数で指定した文字列が最大桁数に収まっているかを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isDigitRangeValid(String value) {

		if (min <= value.length() && value.length() <= max) {
			// 最大桁数に収まっている
			return true;
		} else {
			// 最大桁数に収まっていない
			return false;

		}
	}
}