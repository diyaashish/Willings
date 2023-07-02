package jp.loioz.app.user.myAccountEdit.form;

import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.validation.annotation.AccountId;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.Hiragana;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MultipartFileEnabledExtension;
import jp.loioz.common.validation.annotation.MultipartFileMaxSize;
import jp.loioz.common.validation.annotation.MultipartFileRequired;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * アカウント編集画面用のフォームクラス
 */
@Data
public class MyAccountEditInputForm {

	/**
	 * アカウント個人情報入力フォーム
	 */
	@Data
	public static class AccountSettingInputForm {

		// 表示用プロパティ
		// 現状なし

		// 入力用プロパティ

		/** アカウントID */
		@Required
		@AccountId
		@DigitRange(min = 4, max = 50, message = "4桁以上の半角英数文字を入力してください。")
		private String accountId;

		/** アカウント姓 */
		@Required
		@MaxDigit(max = 24)
		private String accountNameSei;

		/** アカウント姓（かな） */
		@Required
		@Hiragana
		@MaxDigit(max = 64)
		private String accountNameSeiKana;

		/** アカウント名 */
		@Required
		@MaxDigit(max = 24)
		private String accountNameMei;

		/** アカウント名（かな） */
		@Required
		@Hiragana
		@MaxDigit(max = 64)
		private String accountNameMeiKana;

		/** メールアドレス */
		@EmailPattern
		@MaxDigit(max = 256, item = "メールアドレス")
		private String accountMailAddress;

		/** アカウント色 */
		private String accountColor = CommonConstant.LOIOZ_BULE;

		/** 適格請求書発行事業者の登録番号 */
		@Numeric
		@MaxDigit(max = 13)
		private String accountInvoiceRegistrationNo;

		/** 弁護士職印印字の有効無効 */
		private boolean isLawyerStampPrintEnabled;

		/** メール署名 */
		@MaxDigit(max = 10000)
		private String accountMailSignature;

	}

	/**
	 * 弁護士職印
	 */
	@Data
	public static class LawyerStampInputForm {

		// 表示データ
		/** 弁護士職印 base64 Imageタグのソース */
		private String base64ImageSrc;

		// 入力データ
		/** アカウント画像SEQ */
		private Long accountImgSeq;

		/**
		 * 画像データ<br>
		 * ※エラーメッセージが固定値のため、ファイル上限のサイズを変更した場合は、文言も変更する
		 */
		@MultipartFileRequired
		@MultipartFileEnabledExtension(enabledExtension = {
				FileExtension.JPEG, FileExtension.JPG, FileExtension.PNG, FileExtension.GIF,
				FileExtension.JPEG_U, FileExtension.JPG_U, FileExtension.PNG_U, FileExtension.GIF_U
		})
		@MultipartFileMaxSize(max = CommonConstant.LAWYER_STAMP_MAX_SIZE, message = "ファイルサイズの上限は2MBまでです。")
		private MultipartFile multiPartFile;

		/** 新規作成かどうか */
		public boolean isNew() {
			return accountImgSeq == null;
		}

	}

}