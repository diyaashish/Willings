package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.EmailPattern;

/**
 * EmailPatternalidator
 * メールアドレスバリデーション
 */
public class EmailPatternValidator implements ConstraintValidator<EmailPattern, String> {

	String item;

	@Override
	public void initialize(EmailPattern emailPattern) {
		item = emailPattern.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isEmailPatternValid(value);
	}

	/**
	 * 引数で指定した文字列がメールアドレス形式かを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isEmailPatternValid(String value) {

		if (value.matches("^([\\w])+([\\w\\._\\+-])*\\@([\\w])+([\\w\\._\\+-])*\\.([a-zA-Z])+$")) {
			// 一致する
			return true;
		} else {
			// 一致しない
			return false;

		}
	}
}