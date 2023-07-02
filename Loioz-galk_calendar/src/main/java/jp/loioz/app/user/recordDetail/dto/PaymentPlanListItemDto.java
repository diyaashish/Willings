package jp.loioz.app.user.recordDetail.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取引実績画面の支払計画一覧項目Dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentPlanListItemDto {

	/** 支払計画SEQ */
	private Long invoicePaymentPlanSeq;

	/** 入金予定日 */
	private String paymentScheduleDate;

	/** 入金予定金額 */
	private String paymentScheduleAmount;

	/** 摘要 */
	private String sumText;

}
