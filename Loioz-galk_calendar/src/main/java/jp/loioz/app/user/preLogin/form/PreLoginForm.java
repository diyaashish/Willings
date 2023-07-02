package jp.loioz.app.user.preLogin.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.SubDomain;
import lombok.Data;

/**
 * プレログイン画面用のフォームクラス
 */
@Data
public class PreLoginForm {

	/** 事務所ID */
	@Required
	@MaxDigit(max = 30)
	@SubDomain
	private String jimushoId;
}
