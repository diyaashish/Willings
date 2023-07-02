package jp.loioz.dto;

import lombok.Data;

@Data
public class SosakikanListDto {

	/** 施設ID */
	private Long sosakikanId;

	/** 施設区分 */
	private String sosakikanType;

	/** 都道府県名 */
	private String todofukenId;

	/** 都道府県名 */
	private String todofukenName;

	/** 施設住所 */
	private String sosakikanZip;

	/** 施設住所 */
	private String sosakikanAddress1;

	/** 施設住所 */
	private String sosakikanAddress2;

	/** 施設名 */
	private String sosakikanName;

	/** 電話番号 */
	private String sosakikanTelNo;

	/** FAX番号 */
	private String sosakikanFaxNo;

	/** バージョンNo */
	private Long VersionNo;
}
