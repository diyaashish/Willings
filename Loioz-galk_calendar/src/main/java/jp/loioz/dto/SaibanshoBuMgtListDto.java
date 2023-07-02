package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanshoBuMgtListDto {

	/** 係属部ID */
	private Long keizokuBuMgtId;

	/** 裁判所ID */
	private Long saibanshoMgtId;

	/** 裁判所名 */
	private String saibanshoName;

	/** 〒番号 */
	private String saibanshoZip;

	/** 住所１ */
	private String saibanshoAddress1;

	/** 住所２ */
	private String saibanshoAddress2;

	/** 係属部名 */
	private String keizokuBuName;

	/** 都道府県ID */
	private String todofukenId;

	/** 電話番号 */
	private String keizokuBuTelNo;

	/** FAX番号 */
	private String keizokuBuFaxNo;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** 削除日時 */
	private String deletedAt;

	/** バージョンNo */
	private Long versionNo;

}