package jp.loioz.app.user.invoiceSetting.form;

import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 請求書の設定画面の画面入力フォームクラス
 */
@Data
public class InvoiceSettingInputForm {

	/**
	 * 請求書設定画面 入力フォームオブジェクト
	 */
	@Data
	public static class InvoiceSettingFragmentInputForm {

		// 表示用プロパティ
		/** プレビュー */
		private String preview;

		// 入力用プロパティ

		/** 請求書設定SEQ */
		@Required
		private Long invoiceSettingSeq;

		/** 帳票タイトル */
		@Required
		@MaxDigit(max = 20)
		private String defaultTitle;

		/** 挿入文 */
		@MaxDigit(max = 20)
		private String defaultSubText;

		/** 件名表示 プレフィクス */
		@MaxDigit(max = 5)
		private String subjectPrefix;

		/** 件名表示 サフィクス */
		@MaxDigit(max = 5)
		private String subjectSuffix;

		/** 請求書番号：接頭辞 */
		@MaxDigit(max = 3)
		private String invoiceNoPrefix;

		/** 請求書番号：年 */
		@Required
		private String invoiceNoYFmtCd;

		/** 請求書番号：月 */
		@Required
		private String invoiceNoMFmtCd;

		/** 請求書番号：日 */
		@Required
		private String invoiceNoDFmtCd;

		/** 請求書番号：区切り文字 */
		@MaxDigit(max = 1)
		private String invoiceNoDelimiter;

		/** 請求書番号：連番種別 */
		@Required
		private String invoiceNoNumberingTypeCd;

		/** 請求書番号：ゼロ埋め 有/無 */
		private boolean isInvoiceNoZeroPadEnabled;

		/** 請求書番号： ゼロ埋め桁数 */
		@Numeric
		@MaxNumericValue(max = 9)
		private String invoiceNoZeroPadDigits;

		/** 取引日：表示有無 */
		private boolean isTransactionDatePrintEnabled;

		/** 振込期日：表示有無 */
		private boolean isDueDatePrintEnabled;

		/** 備考 */
		@MaxDigit(max = AccgConstant.MX_LEN_REMARKS)
		private String defaultRemarks;

	}

}
