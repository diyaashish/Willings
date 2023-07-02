package jp.loioz.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 売上明細-消費税
 */
@Entity(listener = TSalesDetailTaxEntityListener.class)
@Table(name = "t_sales_detail_tax")
public class TSalesDetailTaxEntity extends DefaultEntity {

    /** 売上明細SEQ */
    @Id
    @Column(name = "sales_detail_seq")
    Long salesDetailSeq;

    /** 税率 */
    @Id
    @Column(name = "tax_rate_type")
    String taxRateType;

    /** 対象額 */
    @Column(name = "taxable_amount")
    BigDecimal taxableAmount;

    /** 税額 */
    @Column(name = "tax_amount")
    BigDecimal taxAmount;

    /** 値引き_対象額 */
    @Column(name = "discount_taxable_amount")
    BigDecimal discountTaxableAmount;

    /** 値引き_税額 */
    @Column(name = "discount_tax_amount")
    BigDecimal discountTaxAmount;

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
     * Returns the taxRateType.
     * 
     * @return the taxRateType
     */
    public String getTaxRateType() {
        return taxRateType;
    }

    /** 
     * Sets the taxRateType.
     * 
     * @param taxRateType the taxRateType
     */
    public void setTaxRateType(String taxRateType) {
        this.taxRateType = taxRateType;
    }

    /** 
     * Returns the taxableAmount.
     * 
     * @return the taxableAmount
     */
    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    /** 
     * Sets the taxableAmount.
     * 
     * @param taxableAmount the taxableAmount
     */
    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
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
     * Returns the discountTaxableAmount.
     * 
     * @return the discountTaxableAmount
     */
    public BigDecimal getDiscountTaxableAmount() {
        return discountTaxableAmount;
    }

    /** 
     * Sets the discountTaxableAmount.
     * 
     * @param discountTaxableAmount the discountTaxableAmount
     */
    public void setDiscountTaxableAmount(BigDecimal discountTaxableAmount) {
        this.discountTaxableAmount = discountTaxableAmount;
    }

    /** 
     * Returns the discountTaxAmount.
     * 
     * @return the discountTaxAmount
     */
    public BigDecimal getDiscountTaxAmount() {
        return discountTaxAmount;
    }

    /** 
     * Sets the discountTaxAmount.
     * 
     * @param discountTaxAmount the discountTaxAmount
     */
    public void setDiscountTaxAmount(BigDecimal discountTaxAmount) {
        this.discountTaxAmount = discountTaxAmount;
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