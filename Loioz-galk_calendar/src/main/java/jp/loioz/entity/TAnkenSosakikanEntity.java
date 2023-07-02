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
 * 案件-捜査機関
 */
@Entity(listener = TAnkenSosakikanEntityListener.class)
@Table(name = "t_anken_sosakikan")
public class TAnkenSosakikanEntity extends DefaultEntity {

	/** 捜査機関SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sosakikan_seq")
	Long sosakikanSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 施設ID */
	@Column(name = "sosakikan_id")
	Long sosakikanId;

	/** 捜査機関名 */
	@Column(name = "sosakikan_name")
	String sosakikanName;

	/** 担当部 */
	@Column(name = "sosakikan_tanto_bu")
	String sosakikanTantoBu;

	/** 電話番号 */
	@Column(name = "sosakikan_tel_no")
	String sosakikanTelNo;

	/** 内線番号 */
	@Column(name = "sosakikan_extension_no")
	String sosakikanExtensionNo;

	/** FAX番号 */
	@Column(name = "sosakikan_fax_no")
	String sosakikanFaxNo;

	/** 号室 */
	@Column(name = "sosakikan_room_no")
	String sosakikanRoomNo;

	/** 担当者① */
	@Column(name = "tantosha1_name")
	String tantosha1Name;

	/** 担当者② */
	@Column(name = "tantosha2_name")
	String tantosha2Name;

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
	 * Returns the sosakikanSeq.
	 *
	 * @return the sosakikanSeq
	 */
	public Long getSosakikanSeq() {
		return sosakikanSeq;
	}

	/**
	 * Sets the sosakikanSeq.
	 *
	 * @param sosakikanSeq the sosakikanSeq
	 */
	public void setSosakikanSeq(Long sosakikanSeq) {
		this.sosakikanSeq = sosakikanSeq;
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
	 * Returns the sosakikanId.
	 *
	 * @return the sosakikanId
	 */
	public Long getSosakikanId() {
		return sosakikanId;
	}

	/**
	 * Sets the sosakikanId.
	 *
	 * @param sosakikanId the sosakikanId
	 */
	public void setSosakikanId(Long sosakikanId) {
		this.sosakikanId = sosakikanId;
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
	 * Returns the sosakikanTantoBu.
	 *
	 * @return the sosakikanTantoBu
	 */
	public String getSosakikanTantoBu() {
		return sosakikanTantoBu;
	}

	/**
	 * Sets the sosakikanTantoBu.
	 *
	 * @param sosakikanTantoBu the sosakikanTantoBu
	 */
	public void setSosakikanTantoBu(String sosakikanTantoBu) {
		this.sosakikanTantoBu = sosakikanTantoBu;
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
	 * Returns the sosakikanExtensionNo.
	 *
	 * @return the sosakikanExtensionNo
	 */
	public String getSosakikanExtensionNo() {
		return sosakikanExtensionNo;
	}

	/**
	 * Sets the sosakikanExtensionNo.
	 *
	 * @param sosakikanExtensionNo the sosakikanExtensionNo
	 */
	public void setSosakikanExtensionNo(String sosakikanExtensionNo) {
		this.sosakikanExtensionNo = sosakikanExtensionNo;
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
	 * Returns the sosakikanRoomNo.
	 *
	 * @return the sosakikanRoomNo
	 */
	public String getSosakikanRoomNo() {
		return sosakikanRoomNo;
	}

	/**
	 * Sets the sosakikanRoomNo.
	 *
	 * @param sosakikanRoomNo the sosakikanRoomNo
	 */
	public void setSosakikanRoomNo(String sosakikanRoomNo) {
		this.sosakikanRoomNo = sosakikanRoomNo;
	}

	/**
	 * Returns the tantosha1Name.
	 *
	 * @return the tantosha1Name
	 */
	public String getTantosha1Name() {
		return tantosha1Name;
	}

	/**
	 * Sets the tantosha1Name.
	 *
	 * @param tantosha1Name the tantosha1Name
	 */
	public void setTantosha1Name(String tantosha1Name) {
		this.tantosha1Name = tantosha1Name;
	}

	/**
	 * Returns the tantosha2Name.
	 *
	 * @return the tantosha2Name
	 */
	public String getTantosha2Name() {
		return tantosha2Name;
	}

	/**
	 * Sets the tantosha2Name.
	 *
	 * @param tantosha2Name the tantosha2Name
	 */
	public void setTantosha2Name(String tantosha2Name) {
		this.tantosha2Name = tantosha2Name;
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