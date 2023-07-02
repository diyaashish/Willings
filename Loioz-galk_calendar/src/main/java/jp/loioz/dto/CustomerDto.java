package jp.loioz.dto;

import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Data
public class CustomerDto {

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

}
