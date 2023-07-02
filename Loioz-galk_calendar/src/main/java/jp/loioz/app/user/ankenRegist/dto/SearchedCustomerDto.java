package jp.loioz.app.user.ankenRegist.dto;

import lombok.Data;

/**
 * 検索した顧客DTO
 */
@Data
public class SearchedCustomerDto {

	/** 顧客ID */
	private Long customerId;

	/** 顧客ID (顧-123) */
	private String customerIdDisp;

	/** 顧客名 */
	private String customerName;

}
