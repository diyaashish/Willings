package jp.loioz.app.user.feeMaster.dto;

import lombok.Data;

/**
 * 報酬項目一覧Dto
 */
@Data
public class FeeMasterListItemDto {

	/** 報酬項目SEQ */
	private Long feeItemSeq;

	/** 報酬項目名 */
	private String feeItemName;

	/** 備考 */
	private String remarks;

	/** 表示順 */
	private Long dispOrder;

}
