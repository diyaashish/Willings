package jp.loioz.dto;

import lombok.Data;

/**
 * 部署一覧用のDto
 */
@Data
public class BushoListDto {

	/** 部署ID */
	private Long bushoId;

	/** 親部署ID */
	private Long parentBushoId;

	/** 部署名 */
	private String bushoName;

	/** 所属ユーザ */
	private String shozokuAccountName;

	/** 親部署名 */
	private String parentBushoName;

	/** 表示順 */
	private Long dispOrder;

	/** バージョンNo */
	private Long versionNo;

}