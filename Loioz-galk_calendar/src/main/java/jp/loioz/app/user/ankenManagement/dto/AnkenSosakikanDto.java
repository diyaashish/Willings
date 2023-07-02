package jp.loioz.app.user.ankenManagement.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 案件-捜査機関一覧のDto
 */
@Builder
@Data
public class AnkenSosakikanDto {

	/** 捜査機関SEQ */
	private Long sosakikanSeq;

	/** 案件ID */
	private Long ankenId;

	/** 捜査機関ID */
	private Long sosakikanId;

	/** 捜査機関名 */
	private String sosakikanName;

	/** 担当部署 */
	private String sosakikanTantoBu;

	/** 電話番号 */
	private String sosakikanTelNo;

	/** 内線番号 */
	private String sosakikanExtensionNo;

	/** FAX番号 */
	private String sosakikanFaxNo;

	/** 号室 */
	private String sosakikanRoomNo;

	/** 担当者①氏名 */
	private String tantosha1Name;

	/** 担当者②氏名 */
	private String tantosha2Name;

	/** 備考 */
	private String remarks;

}
