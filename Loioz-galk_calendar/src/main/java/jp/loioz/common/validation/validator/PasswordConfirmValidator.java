package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.PasswordConfirm;
import jp.loioz.dto.PasswordDto;

/**
 * パスワード相関チェック
 */
public class PasswordConfirmValidator implements ConstraintValidator<PasswordConfirm, PasswordDto> {

	@Override
	public void initialize(PasswordConfirm password) {
	}

	@Override
	public boolean isValid(PasswordDto passwordDto, ConstraintValidatorContext context) {

		if (passwordDto == null) {
			// パスワードオブジェクトそのものが存在しないケースは想定していないのでエラー扱い
			return false;
		}

		String password = passwordDto.getPassword();
		String confirm = passwordDto.getConfirm();
		if (StringUtils.isEmpty(password)) {
			// パスワードが空の場合はチェックしない
			return true;
		}

		return password.equals(confirm);
	}
}