package jp.loioz.entity;

import java.math.BigDecimal;
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
 * 報酬
 */
@Entity(listener = TFeeEntityListener.class)
@Table(name = "t_fee")
public class TFeeEntity extends DefaultEntity {

	/** 報酬SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fee_seq")
	Long feeSeq;

	/** 名簿ID */
	@Column(name = "person_id")
	Long personId;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 会計書類SEQ */
	@Column(name = "accg_doc_seq")
	Long accgDocSeq;

	/** 売上明細SEQ */
	@Column(name = "sales_detail_seq")
	Long salesDetailSeq;

	/** 発生日 */
	@Column(name = "fee_date")
	LocalDate feeDate;

	/** 報酬項目名 */
	@Column(name = "fee_item_name")
	String feeItemName;

	/** タイムチャージフラグ */
	@Column(name = "fee_time_charge_flg")
	String feeTimeChargeFlg;

	/** 報酬額（税抜） */
	@Column(name = "fee_amount")
	BigDecimal feeAmount;

	/** 入金ステータス */
	@Column(name = "fee_payment_status")
	String feePaymentStatus;

	/** 消費税金額 */
	@Column(name = "tax_amount")
	BigDecimal taxAmount;

	/** 消費税率 */
	@Column(name = "tax_rate_type")
	String taxRateType;

	/** 課税フラグ */
	@Column(name = "tax_flg")
	String taxFlg;

	/** 源泉徴収フラグ */
	@Column(name = "withholding_flg")
	String withholdingFlg;

	/** 源泉徴収額 */
	@Column(name = "withholding_amount")
	BigDecimal withholdingAmount;

	/** メモ */
	@Column(name = "fee_memo")
	String feeMemo;

	/** 摘要 */
	@Column(name = "sum_text")
	String sumText;

	/** 回収不能フラグ */
	@Column(name = "uncollectible_flg")
	String uncollectibleFlg;

	/** 登録日時 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 登録アカウントID */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日時 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新アカウントID */
	@Column(name = "updated_by")
	Long updatedBy;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the feeSeq.
	 * 
	 * @return the feeSeq
	 */
	public Long getFeeSeq() {
		return feeSeq;
	}

	/**
	 * Sets the feeSeq.
	 * 
	 * @param feeSeq the feeSeq
	 */
	public void setFeeSeq(Long feeSeq) {
		this.feeSeq = feeSeq;
	}

	/**
	 * Returns the personId.
	 * 
	 * @return the personId
	 */
	public Long getPersonId() {
		return personId;
	}

	/**
	 * Sets the personId.
	 * 
	 * @param personId the personId
	 */
	public void setPersonId(Long personId) {
		this.personId = personId;
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
	 * Returns the accgDocSeq.
	 * 
	 * @return the accgDocSeq
	 */
	public Long getAccgDocSeq() {
		return accgDocSeq;
	}

	/**
	 * Sets the accgDocSeq.
	 * 
	 * @param accgDocSeq the accgDocSeq
	 */
	public void setAccgDocSeq(Long accgDocSeq) {
		this.accgDocSeq = accgDocSeq;
	}

	/**
	 * Returns the salesDetailSeq.
	 * 
	 * @return the salesDetailSeq
	 */
	public Long getSalesDetailSeq() {
		return salesDetailSeq;
	}

	/**
	 * Sets the salesDetailSeq.
	 * 
	 * @param salesDetailSeq the salesDetailSeq
	 */
	public void setSalesDetailSeq(Long salesDetailSeq) {
		this.salesDetailSeq = salesDetailSeq;
	}

	/**
	 * Returns the feeDate.
	 * 
	 * @return the feeDate
	 */
	public LocalDate getFeeDate() {
		return feeDate;
	}

	/**
	 * Sets the feeDate.
	 * 
	 * @param feeDate the feeDate
	 */
	public void setFeeDate(LocalDate feeDate) {
		this.feeDate = feeDate;
	}

	/**
	 * Returns the feeItemName.
	 * 
	 * @return the feeItemName
	 */
	public String getFeeItemName() {
		return feeItemName;
	}

	/**
	 * Sets the feeItemName.
	 * 
	 * @param feeItemName the feeItemName
	 */
	public void setFeeItemName(String feeItemName) {
		this.feeItemName = feeItemName;
	}

	/**
	 * Returns the feeTimeChargeFlg.
	 * 
	 * @return the feeTimeChargeFlg
	 */
	public String getFeeTimeChargeFlg() {
		return feeTimeChargeFlg;
	}

	/**
	 * Sets the feeTimeChargeFlg.
	 * 
	 * @param feeTimeChargeFlg the feeTimeChargeFlg
	 */
	public void setFeeTimeChargeFlg(String feeTimeChargeFlg) {
		this.feeTimeChargeFlg = feeTimeChargeFlg;
	}

	/**
	 * Returns the feeAmount.
	 * 
	 * @return the feeAmount
	 */
	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	/**
	 * Sets the feeAmount.
	 * 
	 * @param feeAmount the feeAmount
	 */
	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	/**
	 * Returns the feePaymentStatus.
	 * 
	 * @return the feePaymentStatus
	 */
	public String getFeePaymentStatus() {
		return feePaymentStatus;
	}

	/**
	 * Sets the feePaymentStatus.
	 * 
	 * @param feePaymentStatus the feePaymentStatus
	 */
	public void setFeePaymentStatus(String feePaymentStatus) {
		this.feePaymentStatus = feePaymentStatus;
	}

	/**
	 * Returns the taxAmount.
	 * 
	 * @return the taxAmount
	 */
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	/**
	 * Sets the taxAmount.
	 * 
	 * @param taxAmount the taxAmount
	 */
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	/**
	 * Returns the taxRate.
	 * 
	 * @return the taxRate
	 */
	public String getTaxRateType() {
		return taxRateType;
	}

	/**
	 * Sets the taxRate.
	 * 
	 * @param taxRate the taxRate
	 */
	public void setTaxRateType(String taxRateType) {
		this.taxRateType = taxRateType;
	}

	/**
	 * Returns the taxFlg.
	 * 
	 * @return the taxFlg
	 */
	public String getTaxFlg() {
		return taxFlg;
	}

	/**
	 * Sets the taxFlg.
	 * 
	 * @param taxFlg the taxFlg
	 */
	public void setTaxFlg(String taxFlg) {
		this.taxFlg = taxFlg;
	}

	/**
	 * Returns the withholdingFlg.
	 * 
	 * @return the withholdingFlg
	 */
	public String getWithholdingFlg() {
		return withholdingFlg;
	}

	/**
	 * Sets the withholdingFlg.
	 * 
	 * @param withholdingFlg the withholdingFlg
	 */
	public void setWithholdingFlg(String withholdingFlg) {
		this.withholdingFlg = withholdingFlg;
	}

	/**
	 * Returns the withholdingAmount.
	 * 
	 * @return the withholdingAmount
	 */
	public BigDecimal getWithholdingAmount() {
		return withholdingAmount;
	}

	/**
	 * Sets the withholdingAmount.
	 * 
	 * @param withholdingAmount the withholdingAmount
	 */
	public void setWithholdingAmount(BigDecimal withholdingAmount) {
		this.withholdingAmount = withholdingAmount;
	}

	/**
	 * Returns the feeMemo.
	 * 
	 * @return the feeMemo
	 */
	public String getFeeMemo() {
		return feeMemo;
	}

	/**
	 * Sets the feeMemo.
	 * 
	 * @param feeMemo the feeMemo
	 */
	public void setFeeMemo(String feeMemo) {
		this.feeMemo = feeMemo;
	}

	/**
	 * Returns the sumText.
	 * 
	 * @return the sumText
	 */
	public String getSumText() {
		return sumText;
	}

	/**
	 * Sets the sumText.
	 * 
	 * @param sumText the sumText
	 */
	public void setSumText(String sumText) {
		this.sumText = sumText;
	}

	/**
	 * Returns the uncollectibleFlg.
	 * 
	 * @return the uncollectibleFlg
	 */
	public String getUncollectibleFlg() {
		return uncollectibleFlg;
	}

	/**
	 * Sets the uncollectibleFlg.
	 * 
	 * @param uncollectibleFlg the uncollectibleFlg
	 */
	public void setUncollectibleFlg(String uncollectibleFlg) {
		this.uncollectibleFlg = uncollectibleFlg;
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