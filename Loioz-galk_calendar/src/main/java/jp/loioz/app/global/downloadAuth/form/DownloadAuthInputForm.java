package jp.loioz.app.global.downloadAuth.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Password;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 顧問契約一覧画面（名簿）表示フォーム
 */
@Data
public class DownloadAuthInputForm {

	/**
	 * パスワード認証入力フォームオブジェクト
	 */
	@Data
	public static class DownloadPasswordInputForm {

		// 表示用プロパティ

		// 入力用プロパティ

		/** パスワード */
		@Required
		@Password
		@MaxDigit(max = 128)
		private String passWord;

	}

}
