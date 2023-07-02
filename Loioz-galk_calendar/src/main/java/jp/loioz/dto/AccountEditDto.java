package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.validation.annotation.AccountId;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.Hiragana;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * アカウント管理
 *
 */
@Data
public class AccountEditDto {

	/** アカウント連番 */
	private Long accountSeq;

	/** アカウントID */
	@Required
	@AccountId
	@DigitRange(min = 4, max = 50, message = "4桁以上の半角英数文字を入力してください。")
	private String accountId;

	/** パスワード */
	// 更新時と新規登録でバリデーション条件が異なるので、各コントローラーで制御を行う
	private PasswordDto password;

	/** アカウント姓 */
	@Required
	@MaxDigit(max = 24, item = "アカウント姓")
	private String accountNameSei;

	/** アカウント姓（かな） */
	@Required
	@Hiragana
	@MaxDigit(max = 64, item = "アカウント姓（かな）")
	private String accountNameSeiKana;

	/** アカウント名 */
	@Required
	@MaxDigit(max = 24, item = "アカウント名")
	private String accountNameMei;

	/** アカウント名（かな） */
	@Required
	@Hiragana
	@MaxDigit(max = 64, item = "アカウント名（かな）")
	private String accountNameMeiKana;

	/** アカウント種別 */
	@Required
	@EnumType(value = AccountType.class)
	private String accountType;

	/** アカウントステータス */
	@Required
	@EnumType(value = AccountStatus.class)
	private String accountStatus;

	/** メールアドレス */
	@EmailPattern
	@MaxDigit(max = 256, item = "メールアドレス")
	private String accountMailAddress;

	/** アカウント権限 */
	@EnumType(value = AccountKengen.class)
	private String accountKengen;

	/** オーナーフラグ */
	@EnumType(value = SystemFlg.class)
	private String accountOwnerFlg;

	/** アカウント利用停止日時-年 */
	private String accountLimitDateYear;

	/** アカウント利用停止日時-月 */
	private String accountLimitDateMonth;

	/** アカウント色 */
	private String accountColor = CommonConstant.LOIOZ_BULE;

	/** バージョンNo */
	private Long versionNo;
}