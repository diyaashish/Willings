package jp.loioz.dto;

import lombok.Data;

@Data
public class SosakikanMgtListDto {

	/** 捜査機関管理ID */
	private Long sosakikanMgtId;

	/** 捜査機関区分 */
	private String sosakikanType;

	/** 都道府県名 */
	private String todofukenId;

	/** 都道府県名 */
	private String todofukenName;

	/** 捜査機関住所 */
	private String sosakikanZip;

	/** 捜査機関住所 */
	private String sosakikanAddress1;

	/** 捜査機関住所 */
	private String sosakikanAddress2;

	/** 捜査機関名 */
	private String sosakikanName;

	/** 電話番号 */
	private String sosakikanTelNo;

	/** FAX番号 */
	private String sosakikanFaxNo;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** バージョンNo */
	private Long VersionNo;
}
