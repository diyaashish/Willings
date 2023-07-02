package jp.loioz.dto;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanshoBuListDto {

	/** 裁判所ID */
	@Column(name = "saibansho_id")
	private Long saibanshoId;

	/** 都道府県ID */
	@Column(name = "todofuken_id")
	private String todofukenId;

	/** 裁判所名 */
	@Column(name = "saibansho_name")
	private String saibanshoName;

	/** 係属部ID */
	@Column(name = "keizoku_bu_id")
	private Long keizokuBuId;

	/** 係属部名 */
	@Column(name = "keizoku_bu_name")
	private String keizokuBuName;

	/** 電話番号 */
	@Column(name = "keizoku_bu_tel_no")
	private String keizokuBuTelNo;

	/** FAX番号 */
	@Column(name = "keizoku_bu_fax_no")
	private String keizokuBuFaxNo;

	/** 作成日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	/** 更新日時 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/** 削除日時 */
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	/** バージョンNo */
	@Column(name = "version_no")
	private Long versionNo;

	/**
	 * 作成日時取得
	 *
	 * @return
	 */
	public String getCreatedAtStr() {
		String createdAtStr = null;
		if (this.createdAt != null) {
			createdAtStr = DateUtils.parseToString(this.createdAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED);
		}
		return createdAtStr;
	}

	/**
	 * 更新日時取得
	 *
	 * @return
	 */
	public String getUpdatedAtStr() {
		String updatedAtStr = null;
		if (this.updatedAt != null) {
			updatedAtStr = DateUtils.parseToString(this.updatedAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED);
		}
		return updatedAtStr;
	}

	/**
	 * 削除日時取得
	 *
	 * @return
	 */
	public String getDeletedAtStr() {
		String deletedAtStr = null;
		if (this.deletedAt != null) {
			deletedAtStr = DateUtils.parseToString(this.deletedAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED);
		}
		return deletedAtStr;
	}
}