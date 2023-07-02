package jp.loioz.app.user.passwordForgetRequest.form;

import javax.validation.Valid;

import jp.loioz.common.validation.annotation.Required;
import jp.loioz.dto.PasswordDto;
import lombok.Data;

/**
 * パスワード忘れ申請のフォームクラス
 */
@Data
public class PasswordChangeForm {

	/** 認証キー */
	@Required
	String key;

	/** パスワード */
	@Valid
	PasswordDto password = new PasswordDto();
}