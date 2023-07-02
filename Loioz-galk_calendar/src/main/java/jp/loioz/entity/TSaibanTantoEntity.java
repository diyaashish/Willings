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
@Entity(listener = TSaibanTantoEntityListener.class)
@Table(name = "t_saiban_tanto")
public class TSaibanTantoEntity extends DefaultEntity {

	/** 裁判担当者SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "saiban_tanto_seq")
	Long saibanTantoSeq;

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	Long saibanSeq;

	/** アカウントSEQ */
	@Column(name = "account_seq")
	Long accountSeq;

	/** 担当種別 */
	@Column(name = "tanto_type")
	String tantoType;

	/** 担当種別連番 */
	@Column(name = "tanto_type_branch_no")
	Long tantoTypeBranchNo;

	/** 裁判主担当フラグ */
	@Column(name = "saiban_main_tanto_flg")
	String saibanMainTantoFlg;

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

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the saibanTantoSeq.
	 * 
	 * @return the saibanTantoSeq
	 */
	public Long getSaibanTantoSeq() {
		return saibanTantoSeq;
	}

	/**
	 * Sets the saibanTantoSeq.
	 * 
	 * @param saibanTantoSeq the saibanTantoSeq
	 */
	public void setSaibanTantoSeq(Long saibanTantoSeq) {
		this.saibanTantoSeq = saibanTantoSeq;
	}

	/**
	 * Returns the saibanSeq.
	 * 
	 * @return the saibanSeq
	 */
	public Long getSaibanSeq() {
		return saibanSeq;
	}

	/**
	 * Sets the saibanSeq.
	 * 
	 * @param saibanSeq the saibanSeq
	 */
	public void setSaibanSeq(Long saibanSeq) {
		this.saibanSeq = saibanSeq;
	}

	/**
	 * Returns the accountSeq.
	 * 
	 * @return the accountSeq
	 */
	public Long getAccountSeq() {
		return accountSeq;
	}

	/**
	 * Sets the accountSeq.
	 * 
	 * @param accountSeq the accountSeq
	 */
	public void setAccountSeq(Long accountSeq) {
		this.accountSeq = accountSeq;
	}

	/**
	 * Returns the tantoType.
	 * 
	 * @return the tantoType
	 */
	public String getTantoType() {
		return tantoType;
	}

	/**
	 * Sets the tantoType.
	 * 
	 * @param tantoType the tantoType
	 */
	public void setTantoType(String tantoType) {
		this.tantoType = tantoType;
	}

	/**
	 * Returns the tantoTypeBranchNo.
	 * 
	 * @return the tantoTypeBranchNo
	 */
	public Long getTantoTypeBranchNo() {
		return tantoTypeBranchNo;
	}

	/**
	 * Sets the tantoTypeBranchNo.
	 * 
	 * @param tantoTypeBranchNo the tantoTypeBranchNo
	 */
	public void setTantoTypeBranchNo(Long tantoTypeBranchNo) {
		this.tantoTypeBranchNo = tantoTypeBranchNo;
	}

	/**
	 * Returns the saibanMainTantoFlg.
	 * 
	 * @return the saibanMainTantoFlg
	 */
	public String getSaibanMainTantoFlg() {
		return saibanMainTantoFlg;
	}

	/**
	 * Sets the saibanMainTantoFlg.
	 * 
	 * @param saibanMainTantoFlg the saibanMainTantoFlg
	 */
	public void setSaibanMainTantoFlg(String saibanMainTantoFlg) {
		this.saibanMainTantoFlg = saibanMainTantoFlg;
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