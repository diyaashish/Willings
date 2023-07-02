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
@Entity(listener = MServicePlanEntityListener.class)
@Table(name = "m_service_plan")
public class MServicePlanEntity extends DefaultEntity {

	/** プラン連番 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "service_plan_seq")
	Long servicePlanSeq;

	/** プラン名 */
	@Column(name = "service_plan_name")
	String servicePlanName;

	/** 月額課金金額（税込） */
	@Column(name = "service_plan_monthly_charge")
	Long servicePlanMonthlyCharge;

	/** プラン詳細 */
	@Column(name = "service_plan_detail")
	String servicePlanDetail;

	/** 公開フラグ */
	@Column(name = "open_flg")
	String openFlg;

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
	 * Returns the servicePlanSeq.
	 * 
	 * @return the servicePlanSeq
	 */
	public Long getServicePlanSeq() {
		return servicePlanSeq;
	}

	/**
	 * Sets the servicePlanSeq.
	 * 
	 * @param servicePlanSeq the servicePlanSeq
	 */
	public void setServicePlanSeq(Long servicePlanSeq) {
		this.servicePlanSeq = servicePlanSeq;
	}

	/**
	 * Returns the servicePlanName.
	 * 
	 * @return the servicePlanName
	 */
	public String getServicePlanName() {
		return servicePlanName;
	}

	/**
	 * Sets the servicePlanName.
	 * 
	 * @param servicePlanName the servicePlanName
	 */
	public void setServicePlanName(String servicePlanName) {
		this.servicePlanName = servicePlanName;
	}

	/**
	 * Returns the servicePlanMonthlyCharge.
	 * 
	 * @return the servicePlanMonthlyCharge
	 */
	public Long getServicePlanMonthlyCharge() {
		return servicePlanMonthlyCharge;
	}

	/**
	 * Sets the servicePlanMonthlyCharge.
	 * 
	 * @param servicePlanMonthlyCharge the servicePlanMonthlyCharge
	 */
	public void setServicePlanMonthlyCharge(Long servicePlanMonthlyCharge) {
		this.servicePlanMonthlyCharge = servicePlanMonthlyCharge;
	}

	/**
	 * Returns the servicePlanDetail.
	 * 
	 * @return the servicePlanDetail
	 */
	public String getServicePlanDetail() {
		return servicePlanDetail;
	}

	/**
	 * Sets the servicePlanDetail.
	 * 
	 * @param servicePlanDetail the servicePlanDetail
	 */
	public void setServicePlanDetail(String servicePlanDetail) {
		this.servicePlanDetail = servicePlanDetail;
	}

	/**
	 * Returns the openFlg.
	 * 
	 * @return the openFlg
	 */
	public String getOpenFlg() {
		return openFlg;
	}

	/**
	 * Sets the openFlg.
	 * 
	 * @param openFlg the openFlg
	 */
	public void setOpenFlg(String openFlg) {
		this.openFlg = openFlg;
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