package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.MaxNumericValue;

/**
 * MaxNumericValueValidator
 * 数値最大値バリデーション
 */
public class MaxNumericValueValidator implements ConstraintValidator<MaxNumericValue, String> {
	long max;
	String item;

	@Override
	public void initialize(MaxNumericValue maxNumericValue) {
		max = maxNumericValue.max();
		item = maxNumericValue.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isMaxNumericValueValid(value);
	}

	/**
	 * 引数で指定した文字列が数値の最大数以下の値かを検証します。
	 *
	 * @param valueStr
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isMaxNumericValueValid(String valueStr) {

		try {

			long valueLong = Long.parseLong(valueStr);

			if (valueLong > max) {
				// 最大値オーバー
				return false;
			} else {
				// 最大値以下（正常系）
				return true;
			}

		} catch(NumberFormatException ex) {
			// 対象文字列が数値ではない場合
			return true;
		}
	}

}