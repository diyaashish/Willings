package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.TelPattern;

/**
 * TelPatternValidator 電話番号バリデーション
 */
public class TelPatternValidator implements ConstraintValidator<TelPattern, String> {

	String item;

	@Override
	public void initialize(TelPattern telPattern) {
		item = telPattern.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isTelPatternValid(value);
	}

	/**
	 * 引数で指定した文字列が電話番号かを検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	private boolean isTelPatternValid(String value) {

		if (value.matches("^[0-9\\-]{2,5}-[0-9]{1,4}-[0-9]{2,4}$")
				|| value.matches("[0-9]{10}$") || value.matches("[0-9]{11}$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}
}