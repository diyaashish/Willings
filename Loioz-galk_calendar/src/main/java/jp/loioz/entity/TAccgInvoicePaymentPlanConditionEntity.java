package jp.loioz.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 請求書-支払分割条件
 */
@Entity(listener = TAccgInvoicePaymentPlanConditionEntityListener.class)
@Table(name = "t_accg_invoice_payment_plan_condition")
public class TAccgInvoicePaymentPlanConditionEntity extends DefaultEntity {

    /** 請求書SEQ */
    @Id
    @Column(name = "invoice_seq")
    Long invoiceSeq;

    /** 月々の支払額 */
    @Column(name = "month_payment_amount")
    BigDecimal monthPaymentAmount;

    /** 月々の支払日（日のみ） */
    @Column(name = "month_payment_dd")
    String monthPaymentDd;

    /** 支払開始日 */
    @Column(name = "payment_start_date")
    LocalDate paymentStartDate;

    /** 端数金額の支払月種別 */
    @Column(name = "fractional_month_type")
    String fractionalMonthType;

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
     * Returns the invoiceSeq.
     * 
     * @return the invoiceSeq
     */
    public Long getInvoiceSeq() {
        return invoiceSeq;
    }

    /** 
     * Sets the invoiceSeq.
     * 
     * @param invoiceSeq the invoiceSeq
     */
    public void setInvoiceSeq(Long invoiceSeq) {
        this.invoiceSeq = invoiceSeq;
    }

    /** 
     * Returns the monthPaymentAmount.
     * 
     * @return the monthPaymentAmount
     */
    public BigDecimal getMonthPaymentAmount() {
        return monthPaymentAmount;
    }

    /** 
     * Sets the monthPaymentAmount.
     * 
     * @param monthPaymentAmount the monthPaymentAmount
     */
    public void setMonthPaymentAmount(BigDecimal monthPaymentAmount) {
        this.monthPaymentAmount = monthPaymentAmount;
    }

    /** 
     * Returns the monthPaymentDd.
     * 
     * @return the monthPaymentDd
     */
    public String getMonthPaymentDd() {
        return monthPaymentDd;
    }

    /** 
     * Sets the monthPaymentDd.
     * 
     * @param monthPaymentDd the monthPaymentDd
     */
    public void setMonthPaymentDd(String monthPaymentDd) {
        this.monthPaymentDd = monthPaymentDd;
    }

    /** 
     * Returns the paymentStartDate.
     * 
     * @return the paymentStartDate
     */
    public LocalDate getPaymentStartDate() {
        return paymentStartDate;
    }

    /** 
     * Sets the paymentStartDate.
     * 
     * @param paymentStartDate the paymentStartDate
     */
    public void setPaymentStartDate(LocalDate paymentStartDate) {
        this.paymentStartDate = paymentStartDate;
    }

    /** 
     * Returns the fractionalMonthType.
     * 
     * @return the fractionalMonthType
     */
    public String getFractionalMonthType() {
        return fractionalMonthType;
    }

    /** 
     * Sets the fractionalMonthType.
     * 
     * @param fractionalMonthType the fractionalMonthType
     */
    public void setFractionalMonthType(String fractionalMonthType) {
        this.fractionalMonthType = fractionalMonthType;
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