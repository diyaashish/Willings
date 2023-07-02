package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.Hiragana;

/**
 * HiraganaValidator ひらがなバリデーション
 */
public class HiraganaValidator implements ConstraintValidator<Hiragana, String> {

	String item;

	@Override
	public void initialize(Hiragana hiragana) {
		item = hiragana.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isHiraganaValid(value);
	}

	/**
	 * 引数で指定した文字列がひらがなかを検証します。
	 *
	 * @param value 検証する文字列
	 * @return チェック結果
	 */
	private boolean isHiraganaValid(String value) {

		if (value.matches("^[\\u3040-\\u309F\\u0020\\u00A0\\u3000\\u30FC]+$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;
		}
	}
}