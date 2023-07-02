package jp.loioz.app.user.mailBaseSetting.form;

import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * メール設定 基本設定画面の入力用オブジェクトフォーム
 */
@Data
public class MailBaseSettingInputForm {

	/**
	 * ダウンロード期限入力フォームクラス
	 */
	@Data
	public static class DownloadPeriodSettingInputForm {

		// 表示用プロパティ
		// 現状なし

		// 入力用プロパティ
		/** 帳票ダウンロード日数 */
		@Required
		@Numeric
		@MinNumericValue(min = 1)
		@MaxNumericValue(max = 999)
		private String reportDownloadPeroidDays;

	}

	/**
	 * メールパスワード設定入力フォームクラス
	 */
	@Data
	public static class MailPasswordSettingInputForm {

		// 表示用プロパティ
		// 現状なし

		// 入力用プロパティ
		/** 規定で有効かどうか */
		private boolean isDownloadViewPasswordEnabled;

	}

}
