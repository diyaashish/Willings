package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 *
 */
@Entity(listener = TMailAddressChangeVerificationEntityListener.class)
@Table(name = "t_mail_address_change_verification")
public class TMailAddressChangeVerificationEntity extends DefaultEntity {

	/** 認証キー */
	@Id
	@Column(name = "verification_key")
	String verificationKey;

	/** 旧メールアドレス */
	@Column(name = "old_mail_address")
	String oldMailAddress;

	/** 新メールアドレス */
	@Column(name = "new_mail_address")
	String newMailAddress;

	/** 有効期限 */
	@Column(name = "temp_limit_date")
	LocalDateTime tempLimitDate;

	/** 完了フラグ */
	@Column(name = "complete_flg")
	String completeFlg;

	/** 作成日時 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 更新日時 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the verificationKey.
	 *
	 * @return the verificationKey
	 */
	public String getVerificationKey() {
		return verificationKey;
	}

	/**
	 * Sets the verificationKey.
	 *
	 * @param verificationKey the verificationKey
	 */
	public void setVerificationKey(String verificationKey) {
		this.verificationKey = verificationKey;
	}

	/**
	 * Returns the oldMailAddress.
	 *
	 * @return the oldMailAddress
	 */
	public String getOldMailAddress() {
		return oldMailAddress;
	}

	/**
	 * Sets the oldMailAddress.
	 *
	 * @param oldMailAddress the oldMailAddress
	 */
	public void setOldMailAddress(String oldMailAddress) {
		this.oldMailAddress = oldMailAddress;
	}

	/**
	 * Returns the newMailAddress.
	 *
	 * @return the newMailAddress
	 */
	public String getNewMailAddress() {
		return newMailAddress;
	}

	/**
	 * Sets the newMailAddress.
	 *
	 * @param newMailAddress the newMailAddress
	 */
	public void setNewMailAddress(String newMailAddress) {
		this.newMailAddress = newMailAddress;
	}

	/**
	 * Returns the tempLimitDate.
	 *
	 * @return the tempLimitDate
	 */
	public LocalDateTime getTempLimitDate() {
		return tempLimitDate;
	}

	/**
	 * Sets the tempLimitDate.
	 *
	 * @param tempLimitDate the tempLimitDate
	 */
	public void setTempLimitDate(LocalDateTime tempLimitDate) {
		this.tempLimitDate = tempLimitDate;
	}

	/**
	 * Returns the completeFlg.
	 *
	 * @return the completeFlg
	 */
	public String getCompleteFlg() {
		return completeFlg;
	}

	/**
	 * Sets the completeFlg.
	 *
	 * @param completeFlg the completeFlg
	 */
	public void setCompleteFlg(String completeFlg) {
		this.completeFlg = completeFlg;
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