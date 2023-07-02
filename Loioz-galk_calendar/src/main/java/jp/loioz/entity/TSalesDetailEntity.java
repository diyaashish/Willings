package jp.loioz.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 売上明細
 */
@Entity(listener = TSalesDetailEntityListener.class)
@Table(name = "t_sales_detail")
public class TSalesDetailEntity extends DefaultEntity {

    /** 売上明細SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_detail_seq")
    Long salesDetailSeq;

    /** 売上SEQ */
    @Column(name = "sales_seq")
    Long salesSeq;

    /** 売上金額（税抜） */
    @Column(name = "sales_amount")
    BigDecimal salesAmount;

    /** 消費税額 */
    @Column(name = "sales_tax_amount")
    BigDecimal salesTaxAmount;

    /** 値引き_売上金額（税抜） */
    @Column(name = "sales_discount_amount")
    BigDecimal salesDiscountAmount;

    /** 値引き_消費税額 */
    @Column(name = "sales_discount_tax_amount")
    BigDecimal salesDiscountTaxAmount;

    /** 源泉徴収税額 */
    @Column(name = "sales_withholding_amount")
    BigDecimal salesWithholdingAmount;

    /** 売上日 */
    @Column(name = "sales_date")
    LocalDate salesDate;

    /** 売上計上先 */
    @Column(name = "sales_account_seq")
    Long salesAccountSeq;

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
     * Returns the salesSeq.
     * 
     * @return the salesSeq
     */
    public Long getSalesSeq() {
        return salesSeq;
    }

    /** 
     * Sets the salesSeq.
     * 
     * @param salesSeq the salesSeq
     */
    public void setSalesSeq(Long salesSeq) {
        this.salesSeq = salesSeq;
    }

    /** 
     * Returns the salesAmount.
     * 
     * @return the salesAmount
     */
    public BigDecimal getSalesAmount() {
        return salesAmount;
    }

    /** 
     * Sets the salesAmount.
     * 
     * @param salesAmount the salesAmount
     */
    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }

    /** 
     * Returns the salesTaxAmount.
     * 
     * @return the salesTaxAmount
     */
    public BigDecimal getSalesTaxAmount() {
        return salesTaxAmount;
    }

    /** 
     * Sets the salesTaxAmount.
     * 
     * @param salesTaxAmount the salesTaxAmount
     */
    public void setSalesTaxAmount(BigDecimal salesTaxAmount) {
        this.salesTaxAmount = salesTaxAmount;
    }

    /** 
     * Returns the salesDiscountAmount.
     * 
     * @return the salesDiscountAmount
     */
    public BigDecimal getSalesDiscountAmount() {
        return salesDiscountAmount;
    }

    /** 
     * Sets the salesDiscountAmount.
     * 
     * @param salesDiscountAmount the salesDiscountAmount
     */
    public void setSalesDiscountAmount(BigDecimal salesDiscountAmount) {
        this.salesDiscountAmount = salesDiscountAmount;
    }

    /** 
     * Returns the salesDiscountTaxAmount.
     * 
     * @return the salesDiscountTaxAmount
     */
    public BigDecimal getSalesDiscountTaxAmount() {
        return salesDiscountTaxAmount;
    }

    /** 
     * Sets the salesDiscountTaxAmount.
     * 
     * @param salesDiscountTaxAmount the salesDiscountTaxAmount
     */
    public void setSalesDiscountTaxAmount(BigDecimal salesDiscountTaxAmount) {
        this.salesDiscountTaxAmount = salesDiscountTaxAmount;
    }

    /** 
     * Returns the salesWithholdingAmount.
     * 
     * @return the salesWithholdingAmount
     */
    public BigDecimal getSalesWithholdingAmount() {
        return salesWithholdingAmount;
    }

    /** 
     * Sets the salesWithholdingAmount.
     * 
     * @param salesWithholdingAmount the salesWithholdingAmount
     */
    public void setSalesWithholdingAmount(BigDecimal salesWithholdingAmount) {
        this.salesWithholdingAmount = salesWithholdingAmount;
    }

    /** 
     * Returns the salesDate.
     * 
     * @return the salesDate
     */
    public LocalDate getSalesDate() {
        return salesDate;
    }

    /** 
     * Sets the salesDate.
     * 
     * @param salesDate the salesDate
     */
    public void setSalesDate(LocalDate salesDate) {
        this.salesDate = salesDate;
    }

    /** 
     * Returns the salesAccountSeq.
     * 
     * @return the salesAccountSeq
     */
    public Long getSalesAccountSeq() {
        return salesAccountSeq;
    }

    /** 
     * Sets the salesAccountSeq.
     * 
     * @param salesAccountSeq the salesAccountSeq
     */
    public void setSalesAccountSeq(Long salesAccountSeq) {
        this.salesAccountSeq = salesAccountSeq;
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