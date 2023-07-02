package jp.loioz.app.user.ankenManagement.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 案件顧客-接見情報一覧
 */
@Builder
@Data
public class AnkenCustomerSekkenDto {

	/** 接見SEQ */
	private Long sekkenSeq;

	/** 案件ID */
	private Long ankenId;

	/** 顧客ID */
	private Long customerId;

	/** 接見開始日時 */
	private String sekkenStartAt;

	/** 接見終了日時 */
	private String sekkenEndAt;

	/** 場所 */
	private String place;

	/** 備考 */
	private String remarks;

}
