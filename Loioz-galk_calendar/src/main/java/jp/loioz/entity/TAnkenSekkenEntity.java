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
 * 案件-接見
 */
@Entity(listener = TAnkenSekkenEntityListener.class)
@Table(name = "t_anken_sekken")
public class TAnkenSekkenEntity extends DefaultEntity {

	/** 接見SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sekken_seq")
	Long sekkenSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 顧客ID */
	@Column(name = "customer_id")
	Long customerId;

	/** 接見開始日時 */
	@Column(name = "sekken_start_at")
	LocalDateTime sekkenStartAt;

	/** 接見終了日時 */
	@Column(name = "sekken_end_at")
	LocalDateTime sekkenEndAt;

	/** 場所 */
	@Column(name = "place")
	String place;

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
	 * Returns the sekkenSeq.
	 * 
	 * @return the sekkenSeq
	 */
	public Long getSekkenSeq() {
		return sekkenSeq;
	}

	/**
	 * Sets the sekkenSeq.
	 * 
	 * @param sekkenSeq the sekkenSeq
	 */
	public void setSekkenSeq(Long sekkenSeq) {
		this.sekkenSeq = sekkenSeq;
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
	 * Returns the sekkenStartAt.
	 * 
	 * @return the sekkenStartAt
	 */
	public LocalDateTime getSekkenStartAt() {
		return sekkenStartAt;
	}

	/**
	 * Sets the sekkenStartAt.
	 * 
	 * @param sekkenStartAt the sekkenStartAt
	 */
	public void setSekkenStartAt(LocalDateTime sekkenStartAt) {
		this.sekkenStartAt = sekkenStartAt;
	}

	/**
	 * Returns the sekkenEndAt.
	 * 
	 * @return the sekkenEndAt
	 */
	public LocalDateTime getSekkenEndAt() {
		return sekkenEndAt;
	}

	/**
	 * Sets the sekkenEndAt.
	 * 
	 * @param sekkenEndAt the sekkenEndAt
	 */
	public void setSekkenEndAt(LocalDateTime sekkenEndAt) {
		this.sekkenEndAt = sekkenEndAt;
	}

	/**
	 * Returns the place.
	 * 
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place.
	 * 
	 * @param place the place
	 */
	public void setPlace(String place) {
		this.place = place;
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