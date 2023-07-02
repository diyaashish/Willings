package jp.loioz.dto;

import lombok.Data;

/**
 * 案件情報(業務履歴用)
 */
@Data
public class AnkenDtoForGyomuHistory {

	/** 案件ID */
	private String ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private String bunyaName;

	/** 顧客ID */
	private String customerId;

	/** 顧客名 */
	private String customerName;
}
