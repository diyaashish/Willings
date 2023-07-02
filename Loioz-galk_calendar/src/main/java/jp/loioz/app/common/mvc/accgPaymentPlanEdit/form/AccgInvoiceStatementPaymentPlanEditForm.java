package jp.loioz.app.common.mvc.accgPaymentPlanEdit.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 支払計画編集モーダルフォームクラス
 */
@Data
public class AccgInvoiceStatementPaymentPlanEditForm {

	// 表示用フィールド

	/** 請求額 */
	private String invoiceAmount;

	/** 入金額 */
	private String salesAmount;

	/** 残額 */
	private String zankin;

	/** 精算完了かどうか */
	private boolean isSeisanCompleted = false;

	// 入力用フィールド
	/** 請求書SEQ */
	@Required
	private Long invoiceSeq;

	/** 支払計画リスト */
	@Valid
	@Size(max = 60, message = "分割予定の登録上限を超過しています")
	private List<PaymentPlanRowInputForm> paymentPlanList = new ArrayList<>();

	/**
	 * 入力金額の合計を取得
	 * 
	 * @return
	 */
	public BigDecimal getTotalInputAmount() {
		List<PaymentPlanRowInputForm> accgPaymentPlanEditDto = new ArrayList<>(this.paymentPlanList);
		return accgPaymentPlanEditDto.stream()
				.map(PaymentPlanRowInputForm::getPaymentScheduleAmountDecimal)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * 差額金額の表示
	 * 
	 * @return
	 */
	public String getSagaku() {
		BigDecimal invoiceAmount = LoiozNumberUtils.nullToZero(LoiozNumberUtils.parseAsBigDecimal(this.invoiceAmount));
		BigDecimal scheduleDecimalTotal = getTotalInputAmount();

		return AccountingUtils.toDispAmountLabel(invoiceAmount.subtract(scheduleDecimalTotal));
	}

	/**
	 * 支払計画の行入力フォーム
	 */
	@Data
	public static class PaymentPlanRowInputForm {

		/** 入金予定日の日付フォーマット */
		private static final String PAYMENT_SCHEDULE_DATE_FORMAT = DateUtils.DATE_FORMAT_SLASH_DELIMITED;

		/** 入金予定日 */
		@Required
		@LocalDatePattern(format = PAYMENT_SCHEDULE_DATE_FORMAT)
		private String paymentScheduleDate;

		/** 入金予定金額 */
		@Required
		@Numeric
		@MaxNumericValue(max = 999999999)
		@MinNumericValue(min = 1)
		private String paymentScheduleAmount;

		/** 摘要 */
		@MaxDigit(max = 10000)
		private String sumText;

		/** 入金予定額Decimal */
		public BigDecimal getPaymentScheduleAmountDecimal() {
			return LoiozNumberUtils.parseAsBigDecimal(this.paymentScheduleAmount);
		}

		/** 入金予定日LocalDate */
		public LocalDate getPaymentScheduleLocalDate() {
			return DateUtils.parseToLocalDate(this.paymentScheduleDate, PAYMENT_SCHEDULE_DATE_FORMAT);
		}

	}

}
