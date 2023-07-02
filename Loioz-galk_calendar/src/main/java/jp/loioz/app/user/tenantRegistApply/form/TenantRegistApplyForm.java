package jp.loioz.app.user.tenantRegistApply.form;

import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 新規アカウント申込み画面のフォームクラス
 */
@Data
public class TenantRegistApplyForm {

	/** メールアドレス */
	@Required
	@MaxDigit(max = 256)
	@EmailPattern
	String mailAddress;

	/** 利用規約同意チェック */
	Boolean agreeCheck;
}
