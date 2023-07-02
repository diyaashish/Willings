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
 * 捜査機関
 */
@Entity(listener = MSosakikanMgtEntityListener.class)
@Table(schema = "service_mgt", name = "m_sosakikan_mgt")
public class MSosakikanMgtEntity extends DefaultEntity {

	/** 捜査機関管理ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sosakikan_mgt_id")
	Long sosakikanMgtId;

	/** 捜査機関区分 */
	@Column(name = "sosakikan_type")
	String sosakikanType;

	/** 都道府県ID */
	@Column(name = "todofuken_id")
	String todofukenId;

	/** 捜査機関郵便番号 */
	@Column(name = "sosakikan_zip")
	String sosakikanZip;

	/** 捜査機関住所1 */
	@Column(name = "sosakikan_address1")
	String sosakikanAddress1;

	/** 捜査機関住所2 */
	@Column(name = "sosakikan_address2")
	String sosakikanAddress2;

	/** 捜査機関名 */
	@Column(name = "sosakikan_name")
	String sosakikanName;

	/** 電話番号 */
	@Column(name = "sosakikan_tel_no")
	String sosakikanTelNo;

	/** FAX番号 */
	@Column(name = "sosakikan_fax_no")
	String sosakikanFaxNo;

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
	 * Returns the sosakikanMgtId.
	 * 
	 * @return the sosakikanMgtId
	 */
	public Long getSosakikanMgtId() {
		return sosakikanMgtId;
	}

	/**
	 * Sets the sosakikanMgtId.
	 * 
	 * @param sosakikanMgtId the sosakikanMgtId
	 */
	public void setSosakikanMgtId(Long sosakikanMgtId) {
		this.sosakikanMgtId = sosakikanMgtId;
	}

	/**
	 * Returns the sosakikanType.
	 * 
	 * @return the sosakikanType
	 */
	public String getSosakikanType() {
		return sosakikanType;
	}

	/**
	 * Sets the sosakikanType.
	 * 
	 * @param sosakikanType the sosakikanType
	 */
	public void setSosakikanType(String sosakikanType) {
		this.sosakikanType = sosakikanType;
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
	 * Returns the sosakikanZip.
	 * 
	 * @return the sosakikanZip
	 */
	public String getSosakikanZip() {
		return sosakikanZip;
	}

	/**
	 * Sets the sosakikanZip.
	 * 
	 * @param sosakikanZip the sosakikanZip
	 */
	public void setSosakikanZip(String sosakikanZip) {
		this.sosakikanZip = sosakikanZip;
	}

	/**
	 * Returns the sosakikanAddress1.
	 * 
	 * @return the sosakikanAddress1
	 */
	public String getSosakikanAddress1() {
		return sosakikanAddress1;
	}

	/**
	 * Sets the sosakikanAddress1.
	 * 
	 * @param sosakikanAddress1 the sosakikanAddress1
	 */
	public void setSosakikanAddress1(String sosakikanAddress1) {
		this.sosakikanAddress1 = sosakikanAddress1;
	}

	/**
	 * Returns the sosakikanAddress2.
	 * 
	 * @return the sosakikanAddress2
	 */
	public String getSosakikanAddress2() {
		return sosakikanAddress2;
	}

	/**
	 * Sets the sosakikanAddress2.
	 * 
	 * @param sosakikanAddress2 the sosakikanAddress2
	 */
	public void setSosakikanAddress2(String sosakikanAddress2) {
		this.sosakikanAddress2 = sosakikanAddress2;
	}

	/**
	 * Returns the sosakikanName.
	 * 
	 * @return the sosakikanName
	 */
	public String getSosakikanName() {
		return sosakikanName;
	}

	/**
	 * Sets the sosakikanName.
	 * 
	 * @param sosakikanName the sosakikanName
	 */
	public void setSosakikanName(String sosakikanName) {
		this.sosakikanName = sosakikanName;
	}

	/**
	 * Returns the sosakikanTelNo.
	 * 
	 * @return the sosakikanTelNo
	 */
	public String getSosakikanTelNo() {
		return sosakikanTelNo;
	}

	/**
	 * Sets the sosakikanTelNo.
	 * 
	 * @param sosakikanTelNo the sosakikanTelNo
	 */
	public void setSosakikanTelNo(String sosakikanTelNo) {
		this.sosakikanTelNo = sosakikanTelNo;
	}

	/**
	 * Returns the sosakikanFaxNo.
	 * 
	 * @return the sosakikanFaxNo
	 */
	public String getSosakikanFaxNo() {
		return sosakikanFaxNo;
	}

	/**
	 * Sets the sosakikanFaxNo.
	 * 
	 * @param sosakikanFaxNo the sosakikanFaxNo
	 */
	public void setSosakikanFaxNo(String sosakikanFaxNo) {
		this.sosakikanFaxNo = sosakikanFaxNo;
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