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
@Entity(listener = TTempAccountEntityListener.class)
@Table(schema ="service_mgt", name = "t_temp_account")
public class TTempAccountEntity extends DefaultEntity {

	/** 認証キー */
	@Id
	@Column(name = "temp_account_key")
	String tempAccountKey;

	/** アカウントID */
	@Column(name = "account_id")
	String accountId;

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
	 * Returns the tempAccountKey.
	 * 
	 * @return the tempAccountKey
	 */
	public String getTempAccountKey() {
		return tempAccountKey;
	}

	/**
	 * Sets the tempAccountKey.
	 * 
	 * @param tempAccountKey the tempAccountKey
	 */
	public void setTempAccountKey(String tempAccountKey) {
		this.tempAccountKey = tempAccountKey;
	}

	/**
	 * Returns the accountId.
	 * 
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * Sets the accountId.
	 * 
	 * @param accountId the accountId
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
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