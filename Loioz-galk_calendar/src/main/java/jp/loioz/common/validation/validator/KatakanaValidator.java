package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.Katakana;

/**
 * KatakanaValidator
 * カタカタバリデーション
 */
public class KatakanaValidator implements ConstraintValidator<Katakana, String> {

	String item;

	@Override
	public void initialize(Katakana katakana) {
		item = katakana.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isKatakanaValid(value);
	}

	/**
	 * 引数で指定した文字列がカタカナかを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isKatakanaValid(String value) {

		if (value.matches("^[\\u30A0-\\u30FF\\u0020\\u00A0\\u3000]+$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}
}