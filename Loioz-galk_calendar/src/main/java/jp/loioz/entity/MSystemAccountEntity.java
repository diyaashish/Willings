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
 *
 */
@Entity(listener = MSystemAccountEntityListener.class)
@Table(schema ="service_mgt", name = "m_system_account")
public class MSystemAccountEntity extends DefaultEntity {

	/** アカウント連番 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "system_account_seq")
	Long systemAccountSeq;

	/** アカウントID */
	@Column(name = "system_account_id")
	String systemAccountId;

	/** パスワード */
	@Column(name = "system_account_password")
	String systemAccountPassword;

	/** アカウント名 */
	@Column(name = "system_account_name")
	String systemAccountName;

	/** アカウント名（カナ） */
	@Column(name = "system_account_name_kana")
	String systemAccountNameKana;

	/** アカウントステータス */
	@Column(name = "system_account_status")
	String systemAccountStatus;

	/** 作成日時 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 作成者ID */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日時 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新者ID */
	@Column(name = "updated_by")
	Long updatedBy;

	/** 削除日時 */
	@Column(name = "deleted_at")
	LocalDateTime deletedAt;

	/** 削除者ID */
	@Column(name = "deleted_by")
	Long deletedBy;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the systemAccountSeq.
	 *
	 * @return the systemAccountSeq
	 */
	public Long getSystemAccountSeq() {
		return systemAccountSeq;
	}

	/**
	 * Sets the systemAccountSeq.
	 *
	 * @param systemAccountSeq the systemAccountSeq
	 */
	public void setSystemAccountSeq(Long systemAccountSeq) {
		this.systemAccountSeq = systemAccountSeq;
	}

	/**
	 * Returns the systemAccountId.
	 *
	 * @return the systemAccountId
	 */
	public String getSystemAccountId() {
		return systemAccountId;
	}

	/**
	 * Sets the systemAccountId.
	 *
	 * @param systemAccountId the systemAccountId
	 */
	public void setSystemAccountId(String systemAccountId) {
		this.systemAccountId = systemAccountId;
	}

	/**
	 * Returns the systemAccountPassword.
	 *
	 * @return the systemAccountPassword
	 */
	public String getSystemAccountPassword() {
		return systemAccountPassword;
	}

	/**
	 * Sets the systemAccountPassword.
	 *
	 * @param systemAccountPassword the systemAccountPassword
	 */
	public void setSystemAccountPassword(String systemAccountPassword) {
		this.systemAccountPassword = systemAccountPassword;
	}

	/**
	 * Returns the systemAccountName.
	 *
	 * @return the systemAccountName
	 */
	public String getSystemAccountName() {
		return systemAccountName;
	}

	/**
	 * Sets the systemAccountName.
	 *
	 * @param systemAccountName the systemAccountName
	 */
	public void setSystemAccountName(String systemAccountName) {
		this.systemAccountName = systemAccountName;
	}

	/**
	 * Returns the systemAccountNameKana.
	 *
	 * @return the systemAccountNameKana
	 */
	public String getSystemAccountNameKana() {
		return systemAccountNameKana;
	}

	/**
	 * Sets the systemAccountNameKana.
	 *
	 * @param systemAccountNameKana the systemAccountNameKana
	 */
	public void setSystemAccountNameKana(String systemAccountNameKana) {
		this.systemAccountNameKana = systemAccountNameKana;
	}

	/**
	 * Returns the systemAccountStatus.
	 *
	 * @return the systemAccountStatus
	 */
	public String getSystemAccountStatus() {
		return systemAccountStatus;
	}

	/**
	 * Sets the systemAccountStatus.
	 *
	 * @param systemAccountStatus the systemAccountStatus
	 */
	public void setSystemAccountStatus(String systemAccountStatus) {
		this.systemAccountStatus = systemAccountStatus;
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