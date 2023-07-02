package jp.loioz.dto;

import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.TelPattern;
import lombok.Data;

/**
 * 新規登録画面独自バリデーションを実施するためのDTO
 */
@Data
public class NewRegistValidateCheckDto {

	/**
	 * 新規登録 顧客情報(個人)のバリデーションのためのクラス
	 */
	@Data
	public static class CustomerTypeKojin {
		/** 顧客姓 */
		@MaxDigit(max = 24)
		private String customerNameSei;
		/** 顧客姓かな */
		@MaxDigit(max = 64)
		private String customerNameSeiKana;
		/** 顧客姓 */
		@MaxDigit(max = 24)
		private String customerNameMei;
		/** 顧客姓かな */
		@MaxDigit(max = 64)
		private String customerNameMeiKana;
		/** 生年月日 */
		@LocalDatePattern
		private String birthDate;
	}

	/**
	 * 新規登録 顧客連絡先情報のバリデーションためのクラス
	 */
	@Data
	public static class KokyakuRenrakusaki {
		/** 電話番号 */
		@TelPattern
		private String telNo;
		/** FAX番号 */
		@TelPattern
		private String faxNo;
		/** メールアドレス */
		@EmailPattern
		private String emailAddress;
	}
}
