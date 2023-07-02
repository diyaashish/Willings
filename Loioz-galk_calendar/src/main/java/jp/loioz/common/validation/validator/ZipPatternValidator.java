package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.ZipPattern;

/**
 * ZipPatternValidator
 * 郵便番号形式バリデーション
 */
public class ZipPatternValidator implements ConstraintValidator<ZipPattern, String> {

	String item;

	@Override
	public void initialize(ZipPattern zipPattern) {
		item = zipPattern.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isZipPatternValidatorValid(value);
	}

	/**
	 * 引数で指定した文字列が郵便番号の形式かを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isZipPatternValidatorValid(String value) {

		if (value.matches("^[0-9]{3}-[0-9]{4}$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}
}