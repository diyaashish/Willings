package jp.loioz.app.common.form.accg;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 案件顧客の選択顧客の「報酬合計、預り金残高」の表示フォーム
 */
@Data
public class AccgCaseSummaryForm {

	/*****************************
	 * 報酬
	 *****************************/

	/** 報酬-合計（報酬 + 消費税額 ） */
	private BigDecimal feeTotalAmount;

	/** 報酬-未請求（報酬 + 消費税額 ） */
	private BigDecimal feeUnclaimedTotalAmount;

	/*****************************
	 * 売上
	 *****************************/

	/** 売上合計【見込】（税込） */
	private BigDecimal salesAmountExpect;

	/** 売上合計【実績】（税込） */
	private BigDecimal salesAmountResult;

	/** 売上金額（税込） */
	private BigDecimal salesTaxIncludedAmount;

	/** 売上金額（税抜） */
	private BigDecimal salesAmount;

	/** 消費税額 */
	private BigDecimal salesTaxAmount;

	/** 源泉徴収税額 */
	private BigDecimal salesWithholdingAmount;

	/** 入金待ち */
	private BigDecimal salesAwaitingPaymentAmount;

	/** 値引き合計（税込） */
	private BigDecimal salesDiscountAmount;

	/*****************************
	 * 預り金
	 *****************************/

	/** 預り金：預り金残高 */
	private BigDecimal totalDepositBalanceAmount;

	/** 預り金：入金合計 */
	private BigDecimal totalDepositAmount;

	/** 預り金：出金合計 */
	private BigDecimal totalWithdrawalAmount;

	/** 預り金：事務所負担金額 */
	private BigDecimal totalTenantBearAmount;

	/*****************************
	 * 案件情報
	 *****************************/

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 名前 */
	private String personName;

}
