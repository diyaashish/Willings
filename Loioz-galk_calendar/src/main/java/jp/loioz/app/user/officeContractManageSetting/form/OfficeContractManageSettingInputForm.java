package jp.loioz.app.user.officeContractManageSetting.form;

import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.Hiragana;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 契約担当者情報設定入力フォーム
 */
@Data
public class OfficeContractManageSettingInputForm {

	/**
	 * 契約担当者入力フォームオブジェクト
	 */
	@Data
	public static class ContactManagerSettingInputForm {

		// 表示用パラメータ
		// 現状なし

		// 入力用プロパティ
		/** 代表者姓 */
		@Required
		@MaxDigit(max = 24, item = "代表者姓")
		private String tenantDaihyoNameSei;

		/** 代表者姓（かな） */
		@Required
		@MaxDigit(max = 64, item = "代表者姓（かな）")
		@Hiragana(item = "ふりがな ")
		private String tenantDaihyoNameSeiKana;

		/** 代表者名 */
		@Required
		@MaxDigit(max = 24, item = "代表者名")
		private String tenantDaihyoNameMei;

		/** 代表者名（かな） */
		@Required
		@MaxDigit(max = 64, item = "代表者名（かな）")
		@Hiragana(item = "ふりがな ")
		private String tenantDaihyoNameMeiKana;

		/** 代表者メールアドレス */
		@Required
		@MaxDigit(max = 256, item = "代表者メールアドレス ")
		@EmailPattern(item = "代表者メールアドレス ")
		private String tenantDaihyoMailAddress;

	}

}
