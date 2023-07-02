package jp.loioz.app.user.recordDetail.form;

import java.math.BigDecimal;

import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 取引実績画面：入力用フォームオブジェクト
 */
@Data
public class RecordDetailInputForm {

	/**
	 * 取引実績画面：詳細入力用フォームオブジェクト
	 */
	@Data
	public static class RecordDetailRowInputForm {

		// 表示用プロパティ
		/** 会計書類種別 */
		private AccgDocType accgDocType;

		// 入力用プロパティ

		/** 取引実績SEQ */
		@Required
		private Long accgRecordSeq;

		/** 会計書類SEQ */
		@Required
		private Long accgDocSeq;

		/** 取引実績明細SEQ */
		private Long accgRecordDetailSeq;

		/** 決済日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String recordDate;

		/** 個別入力の可否 */
		private boolean isSeparateInputEnable;

		/** 個別入力を行うかどうか */
		private boolean isSeparateInput;

		/** 過入金が発生しているかどうか */
		private boolean isOverPayment;

		/** 実績入金額 */
		@Numeric
		@MaxDigit(max = 10)
		@MinNumericValue(min = 1L)
		private String recordAmount;

		/** 報酬入金額 */
		@Numeric
		@MaxDigit(max = 9)
		@MinNumericValue(min = 0L)
		private String recordFeeAmount;

		/** 預り金入金額 */
		@Numeric
		@MaxDigit(max = 9)
		private String recordDepositRecvAmount;

		/** 預り金出金額 */
		@Numeric
		@MaxDigit(max = 10)
		private String recordDepositPaymentAmount;

		/** 備考 */
		@MaxDigit(max = 1000)
		private String remarks;

		/** 新規登録かどうか */
		public boolean isNew() {
			return this.accgRecordDetailSeq == null;
		}

		/** 実績入金額Decimal取得 */
		public BigDecimal getRecordAmountDecimal() {
			return LoiozNumberUtils.parseAsBigDecimal(this.recordAmount);
		}

		/** 報酬入金額Decimal取得 */
		public BigDecimal getRecordFeeAmountDecimal() {
			return LoiozNumberUtils.parseAsBigDecimal(this.recordFeeAmount);
		}

		/** 預り金入金額Decimal取得 */
		public BigDecimal getRecordDepositRecvAmountDecimal() {
			return LoiozNumberUtils.parseAsBigDecimal(this.recordDepositRecvAmount);
		}

		/** 預り金出金額Decimal取得 */
		public BigDecimal getRecordDepositPaymentAmountDecimal() {
			return LoiozNumberUtils.parseAsBigDecimal(this.recordDepositPaymentAmount);
		}

	}

	/**
	 * 取引実績：過入金返金用
	 */
	@Data
	public static class RecordOverpayRefundRowInputForm {

		// 表示用プロパティ

		/** 過入金出金額 */
		private String overPaymentRecvAmount;

		// 入力用プロパティ

		/** 取引実績SEQ */
		@Required
		private Long accgRecordSeq;

		/** 会計書類SEQ */
		@Required
		private Long accgDocSeq;

		/** 取引実績明細SEQ (過入金出金データSEQ ※過入金が発生したレコードのSEQではない) */
		private Long accgRecordDetailSeq;

		/** 決済日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String recordDate;

		/** 備考 */
		@MaxDigit(max = 1000)
		private String remarks;

		/** 新規登録かどうか */
		public boolean isNew() {
			return this.accgRecordDetailSeq == null;
		}

	}

}
