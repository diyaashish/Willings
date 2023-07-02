package jp.loioz.app.user.recordDetail.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 取引実績保存処理：入力値パターンから金額項目の分割結果を格納する
 */
@Data
public class RecordDetailInputPatternDto {

	/** 個別入力を行うかどうか */
	private boolean isSeparateInput;

	/** 実績入金額 */
	private BigDecimal recordAmount;

	/** 報酬入金額 */
	private BigDecimal recordFeeAmount;

	/** 預り金入金額 */
	private BigDecimal recordDepositRecvAmount;

	/** 預り金出金額 */
	private BigDecimal recordDepositPaymentAmount;

	/** 過入金額 */
	private BigDecimal overPayment;

}
