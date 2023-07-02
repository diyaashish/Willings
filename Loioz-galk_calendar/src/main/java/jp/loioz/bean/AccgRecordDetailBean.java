package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 取引実績詳細Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AccgRecordDetailBean {

	/** 取引実績明細SEQ */
	private Long accgRecordDetailSeq;

	/** 取引実績SEQ */
	private Long accgRecordSeq;

	/** 決済日 */
	private LocalDate recordDate;

	/** 取引種別 */
	private String recordType;

	/** 個別入力フラグ */
	private String recordSeparateInputFlg;

	/** 実績入金額 */
	private BigDecimal recordAmount;

	/** 報酬入金額 */
	private BigDecimal recordFeeAmount;

	/** 報酬入金額（既入金のみ） */
	private BigDecimal recordFeeRepayAmount;

	/** 預り金入金額 */
	private BigDecimal recordDepositRecvAmount;

	/** 預り金出金額 */
	private BigDecimal recordDepositPaymentAmount;

	/** 備考 */
	private String remarks;

	/** 取引実績明細過入金SEQ */
	private Long accgRecordDetailOverPaymentSeq;

	/** 過入金額 */
	private BigDecimal overPaymentAmount;

	/** 過入金返金フラグ */
	private String overPaymentRefundFlg;

}
