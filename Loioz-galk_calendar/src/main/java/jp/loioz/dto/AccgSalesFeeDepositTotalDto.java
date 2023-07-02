package jp.loioz.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 会計：報酬金額、預り金のサマリー情報Dto
 */
@Data
public class AccgSalesFeeDepositTotalDto {

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/***********************************
	 * 報酬
	 ***********************************/

	/** 報酬-未請求（報酬 + 消費税額 ） */
	private BigDecimal feeUnclaimedTotalAmount;

	/** 報酬-入金待ち（報酬 + 消費税額 ） */
	private BigDecimal feeWaitingPaymentTotalAmount;

	/** 報酬-入金済み（報酬 + 消費税額 ） */
	private BigDecimal feeDepositedTotalAmount;

	/** 報酬-一部入金（報酬 + 消費税額 ） */
	private BigDecimal feePartialDepositTotalAmount;

	/** 報酬-請求済（報酬 + 消費税額 ） */
	private BigDecimal feeAlreadyClaimedTotalAmount;

	/** 報酬-合計（報酬 + 消費税額 ） */
	private BigDecimal feeTotalAmount;

	/***********************************
	 * 預り金
	 ***********************************/

	/** 預り金：預り金残高 */
	private BigDecimal totalDepositBalanceAmount;

	/** 預り金：入金合計 */
	private BigDecimal totalDepositAmount;

	/** 預り金：出金合計 */
	private BigDecimal totalWithdrawalAmount;

	/** 預り金：事務所負担合計 */
	private BigDecimal totalTenantBearAmount;

	/***********************************
	 * 売上
	 ***********************************/

	/** 売上合計【見込】（税込） */
	private BigDecimal salesAmountExpect;

	/** 売上合計【実績】（税込） */
	private BigDecimal salesAmountResult;

	/** 売上金額（税込）※値引き含まない */
	private BigDecimal salesTaxIncludedTotalAmount;

	/** 売上金額（税抜） */
	private BigDecimal salesTotalAmount;

	/** 消費税額 */
	private BigDecimal salesTaxTotalAmount;

	/** 値引き_売上金額（税込） */
	private BigDecimal salesDiscountTaxIncludedTotalAmount;

	/** 値引き_売上金額（税抜） */
	private BigDecimal salesDiscountTotalAmount;

	/** 値引き_消費税額 */
	private BigDecimal salesDiscountTaxTotalAmount;

	/** 源泉徴収税額 */
	private BigDecimal salesWithholdingTotalAmount;

	/** 入金待ち */
	private BigDecimal salesAwaitingPaymentAmount;

}
