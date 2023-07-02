package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 【管理DB】裁判所係属部
 */
@Entity(listener = MSaibanshoBuMgtEntityListener.class)
@Table(schema = "service_mgt", name = "m_saibansho_bu_mgt")
public class MSaibanshoBuMgtEntity extends DefaultEntity {

	/** 係属部管理ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "keizoku_bu_mgt_id")
	Long keizokuBuMgtId;

	/** 裁判所管理ID */
	@Column(name = "saibansho_mgt_id")
	Long saibanshoMgtId;

	/** 係属部名 */
	@Column(name = "keizoku_bu_name")
	String keizokuBuName;

	/** 電話番号 */
	@Column(name = "keizoku_bu_tel_no")
	String keizokuBuTelNo;

	/** FAX番号 */
	@Column(name = "keizoku_bu_fax_no")
	String keizokuBuFaxNo;

	/** 登録日 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 登録アカウントSEQ */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新アカウントSEQ */
	@Column(name = "updated_by")
	Long updatedBy;

	/** 削除日 */
	@Column(name = "deleted_at")
	LocalDateTime deletedAt;

	/** 削除アカウントSEQ */
	@Column(name = "deleted_by")
	Long deletedBy;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the keizokuBuMgtId.
	 *
	 * @return the keizokuBuMgtId
	 */
	public Long getKeizokuBuMgtId() {
		return keizokuBuMgtId;
	}

	/**
	 * Sets the keizokuBuMgtId.
	 *
	 * @param keizokuBuMgtId the keizokuBuMgtId
	 */
	public void setKeizokuBuMgtId(Long keizokuBuMgtId) {
		this.keizokuBuMgtId = keizokuBuMgtId;
	}

	/**
	 * Returns the saibanshoMgtId.
	 *
	 * @return the saibanshoMgtId
	 */
	public Long getSaibanshoMgtId() {
		return saibanshoMgtId;
	}

	/**
	 * Sets the saibanshoMgtId.
	 *
	 * @param saibanshoMgtId the saibanshoMgtId
	 */
	public void setSaibanshoMgtId(Long saibanshoMgtId) {
		this.saibanshoMgtId = saibanshoMgtId;
	}

	/**
	 * Returns the keizokuBuName.
	 *
	 * @return the keizokuBuName
	 */
	public String getKeizokuBuName() {
		return keizokuBuName;
	}

	/**
	 * Sets the keizokuBuName.
	 *
	 * @param keizokuBuName the keizokuBuName
	 */
	public void setKeizokuBuName(String keizokuBuName) {
		this.keizokuBuName = keizokuBuName;
	}

	/**
	 * Returns the keizokuBuTelNo.
	 *
	 * @return the keizokuBuTelNo
	 */
	public String getKeizokuBuTelNo() {
		return keizokuBuTelNo;
	}

	/**
	 * Sets the keizokuBuTelNo.
	 *
	 * @param keizokuBuTelNo the keizokuBuTelNo
	 */
	public void setKeizokuBuTelNo(String keizokuBuTelNo) {
		this.keizokuBuTelNo = keizokuBuTelNo;
	}

	/**
	 * Returns the keizokuBuFaxNo.
	 *
	 * @return the keizokuBuFaxNo
	 */
	public String getKeizokuBuFaxNo() {
		return keizokuBuFaxNo;
	}

	/**
	 * Sets the keizokuBuFaxNo.
	 *
	 * @param keizokuBuFaxNo the keizokuBuFaxNo
	 */
	public void setKeizokuBuFaxNo(String keizokuBuFaxNo) {
		this.keizokuBuFaxNo = keizokuBuFaxNo;
	}

	/**
	 * Returns the createdAt.
	 *
	 * @return the createdAt
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the createdAt.
	 *
	 * @param createdAt the createdAt
	 */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Returns the createdBy.
	 *
	 * @return the createdBy
	 */
	public Long getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the createdBy.
	 *
	 * @param createdBy the createdBy
	 */
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Returns the updatedAt.
	 *
	 * @return the updatedAt
	 */
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Sets the updatedAt.
	 *
	 * @param updatedAt the updatedAt
	 */
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * Returns the updatedBy.
	 *
	 * @return the updatedBy
	 */
	public Long getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Sets the updatedBy.
	 *
	 * @param updatedBy the updatedBy
	 */
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Returns the deletedAt.
	 *
	 * @return the deletedAt
	 */
	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	/**
	 * Sets the deletedAt.
	 *
	 * @param deletedAt the deletedAt
	 */
	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	/**
	 * Returns the deletedBy.
	 *
	 * @return the deletedBy
	 */
	public Long getDeletedBy() {
		return deletedBy;
	}

	/**
	 * Sets the deletedBy.
	 *
	 * @param deletedBy the deletedBy
	 */
	public void setDeletedBy(Long deletedBy) {
		this.deletedBy = deletedBy;
	}

	/**
	 * Returns the versionNo.
	 *
	 * @return the versionNo
	 */
	public Long getVersionNo() {
		return versionNo;
	}

	/**
	 * Sets the versionNo.
	 *
	 * @param versionNo the versionNo
	 */
	public void setVersionNo(Long versionNo) {
		this.versionNo = versionNo;
	}
}