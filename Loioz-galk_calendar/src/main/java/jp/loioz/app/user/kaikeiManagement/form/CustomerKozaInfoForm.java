package jp.loioz.app.user.kaikeiManagement.form;

import lombok.Data;

/**
 * 案件明細のフォームクラス
 */
@Data
public class CustomerKozaInfoForm {

	/** 顧客ID */
	private Long customerId;
	
	/** 関与者ID */
	private Long kanyoshaSeq;
}
