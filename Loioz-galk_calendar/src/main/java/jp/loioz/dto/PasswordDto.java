package jp.loioz.dto;

import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.Password;
import jp.loioz.common.validation.annotation.PasswordConfirm;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;
import lombok.ToString;

/**
 * パスワードのフォーム入力値
 */
@Data
@ToString(exclude = { "password", "confirm" })
@PasswordConfirm
public class PasswordDto {

	/** パスワード */
	@Required
	@DigitRange(min = 8, max = 72)
	@Password
	String password;

	/** パスワード確認 */
	@Required
	String confirm;
}