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
 * 裁判
 */
@Entity(listener = TSaibanEntityListener.class)
@Table(name = "t_saiban")
public class TSaibanEntity extends DefaultEntity {

	/** 裁判SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "saiban_seq")
	Long saibanSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 裁判枝番 */
	@Column(name = "saiban_branch_no")
	Long saibanBranchNo;

	/** 裁判ステータス */
	@Column(name = "saiban_status")
	String saibanStatus;

	/** 開始日 */
	@Column(name = "saiban_start_date")
	LocalDate saibanStartDate;

	/** 終了日 */
	@Column(name = "saiban_end_date")
	LocalDate saibanEndDate;

	/** 裁判所ID */
	@Column(name = "saibansho_id")
	Long saibanshoId;

	/** 裁判所名 */
	@Column(name = "saibansho_name")
	String saibanshoName;

	/** 係属部 */
	@Column(name = "keizoku_bu_name")
	String keizokuBuName;

	/** 係属部電話番号 */
	@Column(name = "keizoku_bu_tel_no")
	String keizokuBuTelNo;

	/** 係属部FAX番号 */
	@Column(name = "keizoku_bu_fax_no")
	String keizokuBuFaxNo;

	/** 係属係 */
	@Column(name = "keizoku_kakari_name")
	String keizokuKakariName;

	/** 担当書記官 */
	@Column(name = "tanto_shoki")
	String tantoShoki;

	/** 結果 */
	@Column(name = "saiban_result")
	String saibanResult;

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
	 * Returns the saibanBranchNo.
	 * 
	 * @return the saibanBranchNo
	 */
	public Long getSaibanBranchNo() {
		return saibanBranchNo;
	}

	/**
	 * Sets the saibanBranchNo.
	 * 
	 * @param saibanBranchNo the saibanBranchNo
	 */
	public void setSaibanBranchNo(Long saibanBranchNo) {
		this.saibanBranchNo = saibanBranchNo;
	}

	/**
	 * Returns the saibanStatus.
	 * 
	 * @return the saibanStatus
	 */
	public String getSaibanStatus() {
		return saibanStatus;
	}

	/**
	 * Sets the saibanStatus.
	 * 
	 * @param saibanStatus the saibanStatus
	 */
	public void setSaibanStatus(String saibanStatus) {
		this.saibanStatus = saibanStatus;
	}

	/**
	 * Returns the saibanStartDate.
	 * 
	 * @return the saibanStartDate
	 */
	public LocalDate getSaibanStartDate() {
		return saibanStartDate;
	}

	/**
	 * Sets the saibanStartDate.
	 * 
	 * @param saibanStartDate the saibanStartDate
	 */
	public void setSaibanStartDate(LocalDate saibanStartDate) {
		this.saibanStartDate = saibanStartDate;
	}

	/**
	 * Returns the saibanEndDate.
	 * 
	 * @return the saibanEndDate
	 */
	public LocalDate getSaibanEndDate() {
		return saibanEndDate;
	}

	/**
	 * Sets the saibanEndDate.
	 * 
	 * @param saibanEndDate the saibanEndDate
	 */
	public void setSaibanEndDate(LocalDate saibanEndDate) {
		this.saibanEndDate = saibanEndDate;
	}

	/**
	 * Returns the saibanshoId.
	 * 
	 * @return the saibanshoId
	 */
	public Long getSaibanshoId() {
		return saibanshoId;
	}

	/**
	 * Sets the saibanshoId.
	 * 
	 * @param saibanshoId the saibanshoId
	 */
	public void setSaibanshoId(Long saibanshoId) {
		this.saibanshoId = saibanshoId;
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
	 * Returns the keizokuBuName.
	 * 
	 * @return the keizokuBuName
	 */
	public String getKeizokuBuName() {
		return keizokuBuName;
	}

	/**
	 * Sets the keizokuBuName.
	 * 
	 * @param keizokuBuName the keizokuBuName
	 */
	public void setKeizokuBuName(String keizokuBuName) {
		this.keizokuBuName = keizokuBuName;
	}

	/**
	 * Returns the keizokuBuTelNo.
	 * 
	 * @return the keizokuBuTelNo
	 */
	public String getKeizokuBuTelNo() {
		return keizokuBuTelNo;
	}

	/**
	 * Sets the keizokuBuTelNo.
	 * 
	 * @param keizokuBuTelNo the keizokuBuTelNo
	 */
	public void setKeizokuBuTelNo(String keizokuBuTelNo) {
		this.keizokuBuTelNo = keizokuBuTelNo;
	}

	/**
	 * Returns the keizokuBuFaxNo.
	 * 
	 * @return the keizokuBuFaxNo
	 */
	public String getKeizokuBuFaxNo() {
		return keizokuBuFaxNo;
	}

	/**
	 * Sets the keizokuBuFaxNo.
	 * 
	 * @param keizokuBuFaxNo the keizokuBuFaxNo
	 */
	public void setKeizokuBuFaxNo(String keizokuBuFaxNo) {
		this.keizokuBuFaxNo = keizokuBuFaxNo;
	}

	/**
	 * Returns the keizokuKakariName.
	 * 
	 * @return the keizokuKakariName
	 */
	public String getKeizokuKakariName() {
		return keizokuKakariName;
	}

	/**
	 * Sets the keizokuKakariName.
	 * 
	 * @param keizokuKakariName the keizokuKakariName
	 */
	public void setKeizokuKakariName(String keizokuKakariName) {
		this.keizokuKakariName = keizokuKakariName;
	}

	/**
	 * Returns the tantoShoki.
	 * 
	 * @return the tantoShoki
	 */
	public String getTantoShoki() {
		return tantoShoki;
	}

	/**
	 * Sets the tantoShoki.
	 * 
	 * @param tantoShoki the tantoShoki
	 */
	public void setTantoShoki(String tantoShoki) {
		this.tantoShoki = tantoShoki;
	}

	/**
	 * Returns the saibanResult.
	 * 
	 * @return the saibanResult
	 */
	public String getSaibanResult() {
		return saibanResult;
	}

	/**
	 * Sets the saibanResult.
	 * 
	 * @param saibanResult the saibanResult
	 */
	public void setSaibanResult(String saibanResult) {
		this.saibanResult = saibanResult;
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