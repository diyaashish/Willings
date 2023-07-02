package jp.loioz.entity;

import java.time.LocalDate;
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
 * 案件-事件
 */
@Entity(listener = TAnkenJikenEntityListener.class)
@Table(name = "t_anken_jiken")
public class TAnkenJikenEntity extends DefaultEntity {

	/** 事件SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "jiken_seq")
	Long jikenSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 顧客ID */
	@Column(name = "customer_id")
	Long customerId;

	/** 事件名 */
	@Column(name = "jiken_name")
	String jikenName;

	/** 逮捕日 */
	@Column(name = "taiho_date")
	LocalDate taihoDate;

	/** 勾留請求日 */
	@Column(name = "koryu_seikyu_date")
	LocalDate koryuSeikyuDate;

	/** 満期日① */
	@Column(name = "koryu_expiration_date")
	LocalDate koryuExpirationDate;

	/** 満期日② */
	@Column(name = "koryu_extended_expiration_date")
	LocalDate koryuExtendedExpirationDate;

	/** 処分種別 */
	@Column(name = "shobun_type")
	String shobunType;

	/** 処分日 */
	@Column(name = "shobun_date")
	LocalDate shobunDate;

	/** 備考 */
	@Column(name = "remarks")
	String remarks;

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

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the jikenSeq.
	 * 
	 * @return the jikenSeq
	 */
	public Long getJikenSeq() {
		return jikenSeq;
	}

	/**
	 * Sets the jikenSeq.
	 * 
	 * @param jikenSeq the jikenSeq
	 */
	public void setJikenSeq(Long jikenSeq) {
		this.jikenSeq = jikenSeq;
	}

	/**
	 * Returns the ankenId.
	 * 
	 * @return the ankenId
	 */
	public Long getAnkenId() {
		return ankenId;
	}

	/**
	 * Sets the ankenId.
	 * 
	 * @param ankenId the ankenId
	 */
	public void setAnkenId(Long ankenId) {
		this.ankenId = ankenId;
	}

	/**
	 * Returns the customerId.
	 * 
	 * @return the customerId
	 */
	public Long getCustomerId() {
		return customerId;
	}

	/**
	 * Sets the customerId.
	 * 
	 * @param customerId the customerId
	 */
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	/**
	 * Returns the jikenName.
	 * 
	 * @return the jikenName
	 */
	public String getJikenName() {
		return jikenName;
	}

	/**
	 * Sets the jikenName.
	 * 
	 * @param jikenName the jikenName
	 */
	public void setJikenName(String jikenName) {
		this.jikenName = jikenName;
	}

	/**
	 * Returns the taihoDate.
	 * 
	 * @return the taihoDate
	 */
	public LocalDate getTaihoDate() {
		return taihoDate;
	}

	/**
	 * Sets the taihoDate.
	 * 
	 * @param taihoDate the taihoDate
	 */
	public void setTaihoDate(LocalDate taihoDate) {
		this.taihoDate = taihoDate;
	}

	/**
	 * Returns the koryuSeikyuDate.
	 * 
	 * @return the koryuSeikyuDate
	 */
	public LocalDate getKoryuSeikyuDate() {
		return koryuSeikyuDate;
	}

	/**
	 * Sets the koryuSeikyuDate.
	 * 
	 * @param koryuSeikyuDate the koryuSeikyuDate
	 */
	public void setKoryuSeikyuDate(LocalDate koryuSeikyuDate) {
		this.koryuSeikyuDate = koryuSeikyuDate;
	}

	/**
	 * Returns the koryuExpirationDate.
	 * 
	 * @return the koryuExpirationDate
	 */
	public LocalDate getKoryuExpirationDate() {
		return koryuExpirationDate;
	}

	/**
	 * Sets the koryuExpirationDate.
	 * 
	 * @param koryuExpirationDate the koryuExpirationDate
	 */
	public void setKoryuExpirationDate(LocalDate koryuExpirationDate) {
		this.koryuExpirationDate = koryuExpirationDate;
	}

	/**
	 * Returns the koryuExtendedExpirationDate.
	 * 
	 * @return the koryuExtendedExpirationDate
	 */
	public LocalDate getKoryuExtendedExpirationDate() {
		return koryuExtendedExpirationDate;
	}

	/**
	 * Sets the koryuExtendedExpirationDate.
	 * 
	 * @param koryuExtendedExpirationDate the koryuExtendedExpirationDate
	 */
	public void setKoryuExtendedExpirationDate(LocalDate koryuExtendedExpirationDate) {
		this.koryuExtendedExpirationDate = koryuExtendedExpirationDate;
	}

	/**
	 * Returns the shobunType.
	 * 
	 * @return the shobunType
	 */
	public String getShobunType() {
		return shobunType;
	}

	/**
	 * Sets the shobunType.
	 * 
	 * @param shobunType the shobunType
	 */
	public void setShobunType(String shobunType) {
		this.shobunType = shobunType;
	}

	/**
	 * Returns the shobunDate.
	 * 
	 * @return the shobunDate
	 */
	public LocalDate getShobunDate() {
		return shobunDate;
	}

	/**
	 * Sets the shobunDate.
	 * 
	 * @param shobunDate the shobunDate
	 */
	public void setShobunDate(LocalDate shobunDate) {
		this.shobunDate = shobunDate;
	}

	/**
	 * Returns the remarks.
	 * 
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 * 
	 * @param remarks the remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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