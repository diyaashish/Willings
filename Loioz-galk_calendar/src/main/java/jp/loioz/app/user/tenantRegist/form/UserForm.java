package jp.loioz.app.user.tenantRegist.form;

import javax.validation.Valid;

import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.validation.annotation.AccountId;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.Hiragana;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.dto.PasswordDto;
import lombok.Data;

/**
 * アカウント情報詳細入力画面のユーザー情報のフォームクラス
 */
@Data
public class UserForm {

	/** アカウント種別 */
	@Required
	AccountType accountType;

	/** アカウントID */
	@Required
	@AccountId
	@DigitRange(min = 4, max = 50, message = "4桁以上の半角英数文字を入力してください。")
	String accountId;

	/** パスワード */
	@Valid
	PasswordDto password = new PasswordDto();

	/** 姓 */
	@Required
	@MaxDigit(max = 24)
	String accountNameSei;

	/** 姓（かな） */
	@Required
	@MaxDigit(max = 64)
	@Hiragana
	String accountNameSeiKana;

	/** 名 */
	@Required
	@MaxDigit(max = 24)
	String accountNameMei;

	/** 名（かな） */
	@Required
	@MaxDigit(max = 64)
	@Hiragana
	String accountNameMeiKana;

	/** メールアドレス */
	@Required
	@MaxDigit(max = 256)
	@EmailPattern
	String accountMailAddress;

}
