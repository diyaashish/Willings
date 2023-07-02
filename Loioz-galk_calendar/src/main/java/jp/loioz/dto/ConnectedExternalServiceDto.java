package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.ExternalService;
import lombok.Data;

/**
 * 接続中の外部連携サービスDto
 */
@Data
public class ConnectedExternalServiceDto {

	/** 接続中外部連携SEQ */
	private Long connectedExternalServiceSeq;

	/** 外部連携サービス */
	private ExternalService externalService;

	/** 連携開始日時 */
	private String connectedStartDate;
}
