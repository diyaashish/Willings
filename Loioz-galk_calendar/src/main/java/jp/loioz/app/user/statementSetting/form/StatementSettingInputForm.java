package jp.loioz.app.user.statementSetting.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 精算書の設定画面の画面入力フォームクラス
 */
@Data
public class StatementSettingInputForm {

	/**
	 * 精算書設定画面 入力フォームオブジェクト
	 */
	@Data
	public static class StatementSettingFragmentInputForm {

		// 表示用プロパティ
		/** プレビュー */
		private String preview;

		// 入力用プロパティ

		/** 精算書設定SEQ */
		@Required
		private Long statementSettingSeq;

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
		private String statementNoPrefix;

		/** 請求書番号：年 */
		@Required
		private String statementNoYFmtCd;

		/** 請求書番号：月 */
		@Required
		private String statementNoMFmtCd;

		/** 請求書番号：日 */
		@Required
		private String statementNoDFmtCd;

		/** 請求書番号：区切り文字 */
		@MaxDigit(max = 1)
		private String statementNoDelimiter;

		/** 請求書番号：連番種別 */
		@Required
		private String statementNoNumberingTypeCd;

		/** 請求書番号：ゼロ埋め 有/無 */
		private boolean isStatementNoZeroPadEnabled;

		/** 請求書番号： ゼロ埋め桁数 */
		@Numeric
		@MaxNumericValue(max = 9)
		private String statementNoZeroPadDigits;

		/** 取引日：表示有無 */
		private boolean isTransactionDatePrintEnabled;

		/** 返金期日：表示有無 */
		private boolean isRefundDatePrintEnabled;

		/** 備考 */
		@MaxDigit(max = 5000)
		private String defaultRemarks;

	}

}
