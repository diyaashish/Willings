package jp.loioz.app.user.ankenManagement.dto;

import lombok.Data;

/**
 * 捜査機関の選択肢用オブジェクト
 */
@Data
public class SosakikanSelectOptionDto {

	/** 捜査機関ID */
	private Long sosakikanId;

	/** 捜査機関名 */
	private String sosakikanName;

	/** 捜査機関電話番号 */
	private String sosakikanTelNo;

	/** 捜査機関FAX番号 */
	private String sosakikanFaxNo;

}
