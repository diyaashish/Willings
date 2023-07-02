package jp.loioz.dto;

import java.math.BigDecimal;

import jp.loioz.common.utility.LoiozNumberUtils;
import lombok.Data;

/**
 * 請求書、精算書に関する金額情報を保持するDto
 */
@Data
public class AccgInvoiceStatementAmountDto {

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// ▼ 報酬に関する金額データ
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	// 各税率の報酬額（税抜き）

	/** 報酬（税率0%）源泉徴収あり */
	private BigDecimal totalFeeAmountTaxNonWithholding = BigDecimal.ZERO;

	/** 報酬（税率0%）源泉徴収なし */
	private BigDecimal totalFeeAmountTaxNon = BigDecimal.ZERO;

	/** 報酬（税率8%）源泉徴収あり */
	private BigDecimal totalFeeAmountTax8Withholding = BigDecimal.ZERO;

	/** 報酬（税率8%）源泉徴収なし */
	private BigDecimal totalFeeAmountTax8 = BigDecimal.ZERO;

	/** 報酬（税率10%）源泉徴収あり */
	private BigDecimal totalFeeAmountTax10Withholding = BigDecimal.ZERO;

	/** 報酬（税率10%）源泉徴収なし */
	private BigDecimal totalFeeAmountTax10 = BigDecimal.ZERO;

	/** 報酬合計（税抜き） */
	public BigDecimal getTotalFeeAmountWithoutTaxNonDiscount() {

		BigDecimal totalFeeAmountWithoutTaxNonDiscount = BigDecimal.ZERO
				// 報酬（税率0%）
				.add(this.totalFeeAmountTaxNonWithholding)
				.add(this.totalFeeAmountTaxNon)
				// 報酬（税率8%）
				.add(this.totalFeeAmountTax8Withholding)
				.add(this.totalFeeAmountTax8)
				// 報酬（税率10%）
				.add(this.totalFeeAmountTax10Withholding)
				.add(this.totalFeeAmountTax10);

		return totalFeeAmountWithoutTaxNonDiscount;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// ▼ 値引きに関する金額データ
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	// 各税率の値引き額（税抜き）

	/** 値引き（税率0%）源泉徴収ありの合計 */
	private BigDecimal totalDiscountAmountTaxNonWithholding = BigDecimal.ZERO;

	/** 値引き（税率0%）源泉徴収なしの合計 */
	private BigDecimal totalDiscountAmountTaxNon = BigDecimal.ZERO;

	/** 値引き（税率8%）源泉徴収ありの合計 */
	private BigDecimal totalDiscountAmountTax8Withholding = BigDecimal.ZERO;

	/** 値引き（税率8%）源泉徴収なしの合計 */
	private BigDecimal totalDiscountAmountTax8 = BigDecimal.ZERO;

	/** 値引き（税率10%）源泉徴収ありの合計 */
	private BigDecimal totalDiscountAmountTax10Withholding = BigDecimal.ZERO;

	/** 値引き（税率10%）源泉徴収なしの合計 */
	private BigDecimal totalDiscountAmountTax10 = BigDecimal.ZERO;

	/** 値引き合計（税抜き） */
	public BigDecimal getTotalDiscountAmountWithoutTax() {

		BigDecimal totalDiscountAmountWithoutTax = BigDecimal.ZERO
				// 値引き（税率0%）
				.add(this.totalDiscountAmountTaxNonWithholding)
				.add(this.totalDiscountAmountTaxNon)
				// 値引き（税率8%）
				.add(this.totalDiscountAmountTax8Withholding)
				.add(this.totalDiscountAmountTax8)
				// 値引き（税率10%）
				.add(this.totalDiscountAmountTax10Withholding)
				.add(this.totalDiscountAmountTax10);

		return totalDiscountAmountWithoutTax;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// ▼ 消費税額に関する金額データ
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	// 対象額

	/** 税率8%の消費税の対象額（税率8%の報酬の税抜きの合計。値引き前の値。） */
	private BigDecimal totalTaxAmount8TargetFeeNonDiscount = BigDecimal.ZERO;
	/** 税率10%の消費税の対象額（税率10%の報酬の税抜きの合計。値引き前の値。） */
	private BigDecimal totalTaxAmount10TargetFeeNonDiscount = BigDecimal.ZERO;
	
	/** 税率8%の消費税の対象額からの値引き額（税抜き） ※対応するDBのカラムがNULL許可のため初期値はNULLとする */
	private BigDecimal totalTaxAmount8TargetDiscount = null;
	/** 税率10%の消費税の対象額からの値引き額（税抜き） ※対応するDBのカラムがNULL許可のため初期値はNULLとする */
	private BigDecimal totalTaxAmount10TargetDiscount = null;
	
	/** 税率8%の消費税の対象額（報酬-値引き） */
	public BigDecimal getTotalTaxAmount8TargetFee() {
		
		BigDecimal totalTaxAmount8TargetFee = BigDecimal.ZERO
				// 対象額（値引き前）
				.add(this.totalTaxAmount8TargetFeeNonDiscount)
				// 対象額からの値引き額
				.subtract(LoiozNumberUtils.nullToZero(this.totalTaxAmount8TargetDiscount));
		
		return totalTaxAmount8TargetFee;
	}
	
	/** 税率10%の消費税の対象額（報酬-値引き） */
	public BigDecimal getTotalTaxAmount10TargetFee() {
		
		BigDecimal totalTaxAmount10TargetFee = BigDecimal.ZERO
				// 対象額（値引き前）
				.add(this.totalTaxAmount10TargetFeeNonDiscount)
				// 対象額からの値引き額
				.subtract(LoiozNumberUtils.nullToZero(this.totalTaxAmount10TargetDiscount));
		
		return totalTaxAmount10TargetFee;
	}
	
	// 税額

	/** 税率8%の消費税（税率8%の報酬の消費税。値引き前の値。） */
	private BigDecimal totalTaxAmount8NonDiscount = BigDecimal.ZERO;
	/** 税率10%の消費税（税率10%の報酬の消費税。値引き前の値。） */
	private BigDecimal totalTaxAmount10NonDiscount = BigDecimal.ZERO;
	
	/** 税率8%の消費税からの値引き額（値引きの消費税） ※対応するDBのカラムがNULL許可のため初期値はNULLとする */
	private BigDecimal totalTaxAmount8Discount = null;
	/** 税率10%の消費税からの値引き額（値引きの消費税） ※対応するDBのカラムがNULL許可のため初期値はNULLとする */
	private BigDecimal totalTaxAmount10Discount = null;

	/** 報酬の消費税の合計：税率8%の消費税（値引き前）+税率10%の消費税（値引き前） */
	public BigDecimal getTotalTaxAmountNonDiscount() {
		
		BigDecimal totalTaxAmountNonDisCount = BigDecimal.ZERO
				// 税率8%の消費税（値引き前）
				.add(this.totalTaxAmount8NonDiscount)
				// 税率10%の消費税（値引き前）
				.add(this.totalTaxAmount10NonDiscount);
		
		return totalTaxAmountNonDisCount;
	}

	/** 値引きの消費税の合計：税率8%の値引きの消費税+税りつ10%の値引きの消費税 */
	public BigDecimal getTotalTaxAmountDiscount() {
		
		BigDecimal totalTaxAmountDiscount = BigDecimal.ZERO
				// 税率8%の値引きの消費税
				.add(LoiozNumberUtils.nullToZero(this.totalTaxAmount8Discount))
				// 税率10%の値引きの消費税
				.add(LoiozNumberUtils.nullToZero(this.totalTaxAmount10Discount));
		
		return totalTaxAmountDiscount;
	}

	/** 税率8%の消費税（報酬の消費税-値引きの消費税） */
	public BigDecimal getTotalTaxAmount8() {
		
		BigDecimal totalTaxAmount8 = BigDecimal.ZERO
				// 税率8%の消費税（値引き前）
				.add(this.totalTaxAmount8NonDiscount)
				// 税率8%の消費税からの値引き額
				.subtract(LoiozNumberUtils.nullToZero(this.totalTaxAmount8Discount));
		
		return totalTaxAmount8;
	}
	
	/** 税率10%の消費税（報酬の消費税-値引きの消費税） */
	public BigDecimal getTotalTaxAmount10() {
		
		BigDecimal totalTaxAmount10 = BigDecimal.ZERO
				// 税率10%の消費税（値引き前）
				.add(this.totalTaxAmount10NonDiscount)
				// 税率10%の消費税からの値引き額
				.subtract(LoiozNumberUtils.nullToZero(this.totalTaxAmount10Discount));
		
		return totalTaxAmount10;
	}
	
	/** 税額の合計（値引き済み） */
	public BigDecimal getTotalTaxAmount() {

		BigDecimal totalTaxAmount = BigDecimal.ZERO
				// 税率8%の消費税
				.add(this.getTotalTaxAmount8())
				// 税率10%の消費税
				.add(this.getTotalTaxAmount10());

		return totalTaxAmount;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// ▼ 源泉徴収額に関する金額データ
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	// 対象額

	/** 源泉徴収対象の合計（源泉徴収ありの報酬-源泉徴収ありの値引き） */
	private BigDecimal totalWithholdingTargetFee = BigDecimal.ZERO;

	// 税額

	/** 源泉徴収額の合計 */
	private BigDecimal totalWithholdingAmount = BigDecimal.ZERO;

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// ▼ 預り金に関する金額データ
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/** 【既入金項目】既入金の合計 */
	private BigDecimal totalRepayAmount = BigDecimal.ZERO;

	/** 【請求項目】実費/預り金の合計 */
	private BigDecimal totalDepositAllAmount = BigDecimal.ZERO;

	/** 【請求項目】預り金の合計 */
	private BigDecimal totalDepositAmount = BigDecimal.ZERO;

	/** 【請求項目】実費の合計 */
	private BigDecimal totalAdvanceMoneyAmount = BigDecimal.ZERO;

	/** 既入金項目、請求項目の合計（既入金をプラス、請求をマイナスとする） */
	public BigDecimal getTotalRepayInvoiceDepositAmount() {

		BigDecimal totalRepayInvoice = BigDecimal.ZERO
				// 既入金の合計
				.add(totalRepayAmount)
				// 【請求項目】預り金の合計
				.subtract(totalDepositAmount)
				// 【請求項目】実費の合計
				.subtract(totalAdvanceMoneyAmount);

		return totalRepayInvoice;
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// ▼ 上記の金額データを利用して、各金額を算出するメソッド
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	// 報酬系

	/** 報酬：税抜きの合計（報酬-値引き） */
	public BigDecimal getTotalFeeAmountWithoutTax() {

		BigDecimal totalFeeAmountWithoutTax = BigDecimal.ZERO
				// 報酬合計（税抜き）
				.add(this.getTotalFeeAmountWithoutTaxNonDiscount())
				// 値引き合計（税抜き）
				.subtract(this.getTotalDiscountAmountWithoutTax());

		return totalFeeAmountWithoutTax;
	}

	/** 報酬：消費税込の合計（報酬-値引き)+(報酬の消費税-値引きの消費税) */
	public BigDecimal getTotalFeeAmount() {

		BigDecimal totalFeeAmount = BigDecimal.ZERO
				// 報酬合計（税抜き）
				.add(this.getTotalFeeAmountWithoutTax())
				// 税額の合計（値引き済み）
				.add(this.getTotalTaxAmount());

		return totalFeeAmount;
	}

	/** 報酬：消費税込、源泉引きの合計（報酬-値引き)+(報酬の消費税-値引きの消費税)-源泉徴収額 */
	public BigDecimal getTotalFeeAmountAfterWithholding() {

		BigDecimal totalFeeAmountAfterWithholding = BigDecimal.ZERO
				.add(this.getTotalFeeAmount())
				.subtract(this.totalWithholdingAmount);

		return totalFeeAmountAfterWithholding;
	}

	// 合計系

	/** 【請求項目】小計（報酬(税抜-値引き)+預り金+実費） ※請求項目の小計 */
	public BigDecimal getSubTotal() {

		BigDecimal subTotal = BigDecimal.ZERO
				// 報酬：税抜きの合計（報酬-値引き）
				.add(this.getTotalFeeAmountWithoutTax())
				// 預り金
				.add(this.totalDepositAmount)
				// 実費
				.add(this.totalAdvanceMoneyAmount);

		return subTotal;
	}

	/** 【請求項目】合計（小計+消費税-源泉徴収額） ※請求項目の合計 */
	public BigDecimal getTotalAmount() {

		BigDecimal totalAmount = BigDecimal.ZERO
				// 小計
				.add(this.getSubTotal())
				// 消費税
				.add(this.getTotalTaxAmount())
				// 源泉徴収額
				.subtract(this.totalWithholdingAmount);

		return totalAmount;
	}

	/** 請求額（【請求項目】合計 - 【既入金項目】既入金） */
	public BigDecimal getInvoiceAmount() {

		BigDecimal invoiceAmount = BigDecimal.ZERO
				// 【請求項目】合計
				.add(this.getTotalAmount())
				// 【既入金項目】既入金
				.subtract(this.totalRepayAmount);

		return invoiceAmount;
	}

	/** 精算額（【既入金項目】既入金 - 【請求項目】合計） */
	public BigDecimal getStatementAmount() {

		BigDecimal statementAmount = BigDecimal.ZERO
				// 【既入金項目】既入金
				.add(this.totalRepayAmount)
				// 【請求項目】合計
				.subtract(this.getTotalAmount());

		return statementAmount;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
