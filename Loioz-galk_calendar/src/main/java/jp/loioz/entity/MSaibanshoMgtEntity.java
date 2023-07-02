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
 * 【管理DB】裁判所
 */
@Entity(listener = MSaibanshoMgtEntityListener.class)
@Table(schema = "service_mgt", name = "m_saibansho_mgt")
public class MSaibanshoMgtEntity extends DefaultEntity {

	/** 裁判所管理ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "saibansho_mgt_id")
	Long saibanshoMgtId;

	/** 都道府県ID */
	@Column(name = "todofuken_id")
	String todofukenId;

	/** 裁判所郵便番号 */
	@Column(name = "saibansho_zip")
	String saibanshoZip;

	/** 裁判所住所1 */
	@Column(name = "saibansho_address1")
	String saibanshoAddress1;

	/** 裁判所住所２ */
	@Column(name = "saibansho_address2")
	String saibanshoAddress2;

	/** 裁判所名 */
	@Column(name = "saibansho_name")
	String saibanshoName;

	/** 登録日時 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 登録アカウントSEQ */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日時 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新アカウントSEQ */
	@Column(name = "updated_by")
	Long updatedBy;

	/** 削除日時 */
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
	 * Returns the todofukenId.
	 * 
	 * @return the todofukenId
	 */
	public String getTodofukenId() {
		return todofukenId;
	}

	/**
	 * Sets the todofukenId.
	 * 
	 * @param todofukenId the todofukenId
	 */
	public void setTodofukenId(String todofukenId) {
		this.todofukenId = todofukenId;
	}

	/**
	 * Returns the saibanshoZip.
	 * 
	 * @return the saibanshoZip
	 */
	public String getSaibanshoZip() {
		return saibanshoZip;
	}

	/**
	 * Sets the saibanshoZip.
	 * 
	 * @param saibanshoZip the saibanshoZip
	 */
	public void setSaibanshoZip(String saibanshoZip) {
		this.saibanshoZip = saibanshoZip;
	}

	/**
	 * Returns the saibanshoAddress1.
	 * 
	 * @return the saibanshoAddress1
	 */
	public String getSaibanshoAddress1() {
		return saibanshoAddress1;
	}

	/**
	 * Sets the saibanshoAddress1.
	 * 
	 * @param saibanshoAddress1 the saibanshoAddress1
	 */
	public void setSaibanshoAddress1(String saibanshoAddress1) {
		this.saibanshoAddress1 = saibanshoAddress1;
	}

	/**
	 * Returns the saibanshoAddress2.
	 * 
	 * @return the saibanshoAddress2
	 */
	public String getSaibanshoAddress2() {
		return saibanshoAddress2;
	}

	/**
	 * Sets the saibanshoAddress2.
	 * 
	 * @param saibanshoAddress2 the saibanshoAddress2
	 */
	public void setSaibanshoAddress2(String saibanshoAddress2) {
		this.saibanshoAddress2 = saibanshoAddress2;
	}

	/**
	 * Returns the saibanshoName.
	 * 
	 * @return the saibanshoName
	 */
	public String getSaibanshoName() {
		return saibanshoName;
	}

	/**
	 * Sets the saibanshoName.
	 * 
	 * @param saibanshoName the saibanshoName
	 */
	public void setSaibanshoName(String saibanshoName) {
		this.saibanshoName = saibanshoName;
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