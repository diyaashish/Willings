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
@Entity(listener = MSelectListEntityListener.class)
@Table(name = "m_select_list")
public class MSelectListEntity extends DefaultEntity {

	/** 選択肢SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "select_seq")
	Long selectSeq;

	/** 選択肢区分 */
	@Column(name = "select_type")
	String selectType;

	/** 選択肢名 */
	@Column(name = "select_val")
	String selectVal;

	/** 表示順 */
	@Column(name = "disp_order")
	Long dispOrder;

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
	 * Returns the selectSeq.
	 * 
	 * @return the selectSeq
	 */
	public Long getSelectSeq() {
		return selectSeq;
	}

	/**
	 * Sets the selectSeq.
	 * 
	 * @param selectSeq the selectSeq
	 */
	public void setSelectSeq(Long selectSeq) {
		this.selectSeq = selectSeq;
	}

	/**
	 * Returns the selectType.
	 * 
	 * @return the selectType
	 */
	public String getSelectType() {
		return selectType;
	}

	/**
	 * Sets the selectType.
	 * 
	 * @param selectType the selectType
	 */
	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	/**
	 * Returns the selectVal.
	 * 
	 * @return the selectVal
	 */
	public String getSelectVal() {
		return selectVal;
	}

	/**
	 * Sets the selectVal.
	 * 
	 * @param selectVal the selectVal
	 */
	public void setSelectVal(String selectVal) {
		this.selectVal = selectVal;
	}

	/**
	 * Returns the dispOrder.
	 * 
	 * @return the dispOrder
	 */
	public Long getDispOrder() {
		return dispOrder;
	}

	/**
	 * Sets the dispOrder.
	 * 
	 * @param dispOrder the dispOrder
	 */
	public void setDispOrder(Long dispOrder) {
		this.dispOrder = dispOrder;
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