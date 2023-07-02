package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 刑事裁判付帯情報
 */
@Entity(listener = TSaibanAddKeijiEntityListener.class)
@Table(name = "t_saiban_add_keiji")
public class TSaibanAddKeijiEntity extends DefaultEntity {

	/** 裁判SEQ */
	@Id
	@Column(name = "saiban_seq")
	Long saibanSeq;

	/** 本起訴事件SEQ */
	@Column(name = "main_jiken_seq")
	Long mainJikenSeq;

	/** 捜査機関ID */
	@Column(name = "sosakikan_id")
	Long sosakikanId;

	/** 検察庁名 */
	@Column(name = "kensatsucho_name")
	String kensatsuchoName;

	/** 担当部 */
	@Column(name = "kensatsucho_tanto_bu_name")
	String kensatsuchoTantoBuName;

	/** 検察官名 */
	@Column(name = "kensatsukan_name")
	String kensatsukanName;

	/** 検察官名かな */
	@Column(name = "kensatsukan_name_kana")
	String kensatsukanNameKana;

	/** 事務官名 */
	@Column(name = "jimukan_name")
	String jimukanName;

	/** 事務官名かな */
	@Column(name = "jimukan_name_kana")
	String jimukanNameKana;

	/** 検察電話番号 */
	@Column(name = "kensatsu_tel_no")
	String kensatsuTelNo;

	/** 検察内線番号 */
	@Column(name = "kensatsu_extension_no")
	String kensatsuExtensionNo;

	/** 検察FAX番号 */
	@Column(name = "kensatsu_fax_no")
	String kensatsuFaxNo;

	/** 検察号室 */
	@Column(name = "kensatsu_room_no")
	String kensatsuRoomNo;

	/** 検察備考 */
	@Column(name = "kensatsu_remarks")
	String kensatsuRemarks;

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
	 * Returns the mainJikenSeq.
	 *
	 * @return the mainJikenSeq
	 */
	public Long getMainJikenSeq() {
		return mainJikenSeq;
	}

	/**
	 * Sets the mainJikenSeq.
	 *
	 * @param mainJikenSeq the mainJikenSeq
	 */
	public void setMainJikenSeq(Long mainJikenSeq) {
		this.mainJikenSeq = mainJikenSeq;
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
	 * Returns the kensatsuchoName.
	 *
	 * @return the kensatsuchoName
	 */
	public String getKensatsuchoName() {
		return kensatsuchoName;
	}

	/**
	 * Sets the kensatsuchoName.
	 *
	 * @param kensatsuchoName the kensatsuchoName
	 */
	public void setKensatsuchoName(String kensatsuchoName) {
		this.kensatsuchoName = kensatsuchoName;
	}

	/**
	 * Returns the kensatsuchoTantoBuName.
	 *
	 * @return the kensatsuchoTantoBuName
	 */
	public String getKensatsuchoTantoBuName() {
		return kensatsuchoTantoBuName;
	}

	/**
	 * Sets the kensatsuchoTantoBuName.
	 *
	 * @param kensatsuchoTantoBuName the kensatsuchoTantoBuName
	 */
	public void setKensatsuchoTantoBuName(String kensatsuchoTantoBuName) {
		this.kensatsuchoTantoBuName = kensatsuchoTantoBuName;
	}

	/**
	 * Returns the kensatsukanName.
	 *
	 * @return the kensatsukanName
	 */
	public String getKensatsukanName() {
		return kensatsukanName;
	}

	/**
	 * Sets the kensatsukanName.
	 *
	 * @param kensatsukanName the kensatsukanName
	 */
	public void setKensatsukanName(String kensatsukanName) {
		this.kensatsukanName = kensatsukanName;
	}

	/**
	 * Returns the kensatsukanNameKana.
	 *
	 * @return the kensatsukanNameKana
	 */
	public String getKensatsukanNameKana() {
		return kensatsukanNameKana;
	}

	/**
	 * Sets the kensatsukanNameKana.
	 *
	 * @param kensatsukanNameKana the kensatsukanNameKana
	 */
	public void setKensatsukanNameKana(String kensatsukanNameKana) {
		this.kensatsukanNameKana = kensatsukanNameKana;
	}

	/**
	 * Returns the jimukanName.
	 *
	 * @return the jimukanName
	 */
	public String getJimukanName() {
		return jimukanName;
	}

	/**
	 * Sets the jimukanName.
	 *
	 * @param jimukanName the jimukanName
	 */
	public void setJimukanName(String jimukanName) {
		this.jimukanName = jimukanName;
	}

	/**
	 * Returns the jimukanNameKana.
	 *
	 * @return the jimukanNameKana
	 */
	public String getJimukanNameKana() {
		return jimukanNameKana;
	}

	/**
	 * Sets the jimukanNameKana.
	 *
	 * @param jimukanNameKana the jimukanNameKana
	 */
	public void setJimukanNameKana(String jimukanNameKana) {
		this.jimukanNameKana = jimukanNameKana;
	}

	/**
	 * Returns the kensatsuTelNo.
	 *
	 * @return the kensatsuTelNo
	 */
	public String getKensatsuTelNo() {
		return kensatsuTelNo;
	}

	/**
	 * Sets the kensatsuTelNo.
	 *
	 * @param kensatsuTelNo the kensatsuTelNo
	 */
	public void setKensatsuTelNo(String kensatsuTelNo) {
		this.kensatsuTelNo = kensatsuTelNo;
	}

	/**
	 * Returns the kensatsuExtensionNo.
	 *
	 * @return the kensatsuExtensionNo
	 */
	public String getKensatsuExtensionNo() {
		return kensatsuExtensionNo;
	}

	/**
	 * Sets the kensatsuExtensionNo.
	 *
	 * @param kensatsuExtensionNo the kensatsuExtensionNo
	 */
	public void setKensatsuExtensionNo(String kensatsuExtensionNo) {
		this.kensatsuExtensionNo = kensatsuExtensionNo;
	}

	/**
	 * Returns the kensatsuFaxNo.
	 *
	 * @return the kensatsuFaxNo
	 */
	public String getKensatsuFaxNo() {
		return kensatsuFaxNo;
	}

	/**
	 * Sets the kensatsuFaxNo.
	 *
	 * @param kensatsuFaxNo the kensatsuFaxNo
	 */
	public void setKensatsuFaxNo(String kensatsuFaxNo) {
		this.kensatsuFaxNo = kensatsuFaxNo;
	}

	/**
	 * Returns the kensatsuRoomNo.
	 *
	 * @return the kensatsuRoomNo
	 */
	public String getKensatsuRoomNo() {
		return kensatsuRoomNo;
	}

	/**
	 * Sets the kensatsuRoomNo.
	 *
	 * @param kensatsuRoomNo the kensatsuRoomNo
	 */
	public void setKensatsuRoomNo(String kensatsuRoomNo) {
		this.kensatsuRoomNo = kensatsuRoomNo;
	}

	/**
	 * Returns the kensatsuRemarks.
	 *
	 * @return the kensatsuRemarks
	 */
	public String getKensatsuRemarks() {
		return kensatsuRemarks;
	}

	/**
	 * Sets the kensatsuRemarks.
	 *
	 * @param kensatsuRemarks the kensatsuRemarks
	 */
	public void setKensatsuRemarks(String kensatsuRemarks) {
		this.kensatsuRemarks = kensatsuRemarks;
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