package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.Password;

/**
 * PasswordValidator
 * パスワード形式バリデーション
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

	@Override
	public void initialize(Password password) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		if (!value.matches("^[a-zA-Z0-9]*$")) {
			// 半角英数字のみ使用可能
			return false;
		}
		if (!value.matches(".*[a-zA-Z].*") || !value.matches(".*[0-9].*")) {
			// 英字と数字の混在が必須
			return false;
		}

		return true;
	}
}