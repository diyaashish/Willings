package jp.loioz.dto;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * グループ（旧：部署）管理用のDto
 */
@Data
public class BushoEditDto {

	/** 部署ID */
	private Long bushoId;

	/** 親部署ID */
	private Long parentBushoId;

	/** 部署名 */
	@Required
	@MaxDigit(max = 128, item = "部署名")
	private String bushoName;

	/** 親部署名 */
	private String parentBushoName;

	/** 所属アカウント */
	private List<Long> accountSeqList = new ArrayList<Long>();

	/** 表示順 */
	private String dispOrder;

	/** バージョンNo */
	private Long versionNo;
}