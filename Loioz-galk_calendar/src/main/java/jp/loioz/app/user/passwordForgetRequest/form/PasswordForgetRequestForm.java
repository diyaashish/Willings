package jp.loioz.app.user.passwordForgetRequest.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * パスワード忘れ申請のフォームクラス
 */
@Data
public class PasswordForgetRequestForm {

	/** アカウントID */
	@Required
	@MaxDigit(max = 256)
	String accountId;

}
