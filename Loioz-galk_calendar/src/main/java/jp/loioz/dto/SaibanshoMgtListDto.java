package jp.loioz.dto;

import lombok.Data;

@Data
public class SaibanshoMgtListDto {

	/** 裁判所管理ID **/
	private Long saibanshoMgtId;

	/** 都道府県ID **/
	private String todofukenId;

	/** 都道府県 **/
	private String todofukenName;

	/** 裁判所郵便番号 **/
	private String saibanshoZip;

	/** 裁判所住所 **/
	private String saibanshoAddress1;

	/** 裁判所住所2 **/
	private String saibanshoAddress2;

	/** 裁判所名 **/
	private String saibanshoName;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** バージョンNo */
	private Long VersionNo;
}
