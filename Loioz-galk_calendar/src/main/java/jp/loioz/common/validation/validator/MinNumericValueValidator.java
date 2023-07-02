package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.MinNumericValue;

/**
 * MinNumericValueValidator
 * 数値最小値バリデーション
 */
public class MinNumericValueValidator implements ConstraintValidator<MinNumericValue, String> {

	long min;
	String item;

	@Override
	public void initialize(MinNumericValue minNumericValue) {
		min = minNumericValue.min();
		item = minNumericValue.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isMinNumericValueValid(value);
	}

	/**
	 * 引数で指定した文字列が数値の最小数以上の値かを検証します。
	 *
	 * @param valueStr
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isMinNumericValueValid(String valueStr) {

		try {

			long valueLong = Long.parseLong(valueStr);

			if (valueLong < min) {
				// 最小値より小さい
				return false;
			} else {
				// 最小値以上（正常系）
				return true;
			}

		} catch(NumberFormatException ex) {
			// 対象文字列が数値ではない場合
			return true;
		}
	}

}
