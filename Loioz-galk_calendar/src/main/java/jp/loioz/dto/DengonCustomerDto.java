package jp.loioz.dto;

import lombok.Data;

/**
 * 伝言-顧客DTO
 */
@Data
public class DengonCustomerDto {

	/** 伝言SEQ */
	private Long dengonSeq;

	/** 顧客ID */
	private Long customerId;

	/** 顧客ID (顧-123) */
	private String customerIdDisp;

	/** 顧客名 */
	private String customerName;

}
