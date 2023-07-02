package jp.loioz.app.user.depositRecvMaster.dto;

import jp.loioz.common.constant.CommonConstant.DepositType;
import lombok.Data;

/**
 * 預り金マスタ一覧のDto
 */
@Data
public class DepositRecvMasterListItemDto {

	/** 預り金項目SEQ */
	private Long depositItemSeq;

	/** 預り金種別タイプ */
	private DepositType depositType;

	/** 預り金項目名 */
	private String depositItemName;

	/** 備考 */
	private String remarks;

	/** 表示順 */
	private Long dispOrder;

}
