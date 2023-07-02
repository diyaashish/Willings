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
 * 支払計画
 */
@Entity(listener = TAccgInvoicePaymentPlanEntityListener.class)
@Table(name = "t_accg_invoice_payment_plan")
public class TAccgInvoicePaymentPlanEntity extends DefaultEntity {

    /** 支払計画SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_payment_plan_seq")
    Long invoicePaymentPlanSeq;

    /** 請求書SEQ */
    @Column(name = "invoice_seq")
    Long invoiceSeq;

    /** 支払予定日 */
    @Column(name = "payment_schedule_date")
    LocalDate paymentScheduleDate;

    /** 支払予定金額 */
    @Column(name = "payment_schedule_amount")
    BigDecimal paymentScheduleAmount;

    /** 摘要 */
    @Column(name = "sum_text")
    String sumText;

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
     * Returns the invoicePaymentPlanSeq.
     * 
     * @return the invoicePaymentPlanSeq
     */
    public Long getInvoicePaymentPlanSeq() {
        return invoicePaymentPlanSeq;
    }

    /** 
     * Sets the invoicePaymentPlanSeq.
     * 
     * @param invoicePaymentPlanSeq the invoicePaymentPlanSeq
     */
    public void setInvoicePaymentPlanSeq(Long invoicePaymentPlanSeq) {
        this.invoicePaymentPlanSeq = invoicePaymentPlanSeq;
    }

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
     * Returns the paymentScheduleDate.
     * 
     * @return the paymentScheduleDate
     */
    public LocalDate getPaymentScheduleDate() {
        return paymentScheduleDate;
    }

    /** 
     * Sets the paymentScheduleDate.
     * 
     * @param paymentScheduleDate the paymentScheduleDate
     */
    public void setPaymentScheduleDate(LocalDate paymentScheduleDate) {
        this.paymentScheduleDate = paymentScheduleDate;
    }

    /** 
     * Returns the paymentScheduleAmount.
     * 
     * @return the paymentScheduleAmount
     */
    public BigDecimal getPaymentScheduleAmount() {
        return paymentScheduleAmount;
    }

    /** 
     * Sets the paymentScheduleAmount.
     * 
     * @param paymentScheduleAmount the paymentScheduleAmount
     */
    public void setPaymentScheduleAmount(BigDecimal paymentScheduleAmount) {
        this.paymentScheduleAmount = paymentScheduleAmount;
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