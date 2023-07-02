package jp.loioz.entity;

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
 * 請求項目-報酬
 */
@Entity(listener = TAccgDocInvoiceFeeEntityListener.class)
@Table(name = "t_accg_doc_invoice_fee")
public class TAccgDocInvoiceFeeEntity extends DefaultEntity {

    /** 請求報酬項目SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_invoice_fee_seq")
    Long docInvoiceFeeSeq;

    /** 請求項目SEQ */
    @Column(name = "doc_invoice_seq")
    Long docInvoiceSeq;

    /** 報酬SEQ */
    @Column(name = "fee_seq")
    Long feeSeq;

    /** 取引日 */
    @Column(name = "fee_transaction_date")
    LocalDate feeTransactionDate;

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
     * Returns the docInvoiceFeeSeq.
     * 
     * @return the docInvoiceFeeSeq
     */
    public Long getDocInvoiceFeeSeq() {
        return docInvoiceFeeSeq;
    }

    /** 
     * Sets the docInvoiceFeeSeq.
     * 
     * @param docInvoiceFeeSeq the docInvoiceFeeSeq
     */
    public void setDocInvoiceFeeSeq(Long docInvoiceFeeSeq) {
        this.docInvoiceFeeSeq = docInvoiceFeeSeq;
    }

    /** 
     * Returns the docInvoiceSeq.
     * 
     * @return the docInvoiceSeq
     */
    public Long getDocInvoiceSeq() {
        return docInvoiceSeq;
    }

    /** 
     * Sets the docInvoiceSeq.
     * 
     * @param docInvoiceSeq the docInvoiceSeq
     */
    public void setDocInvoiceSeq(Long docInvoiceSeq) {
        this.docInvoiceSeq = docInvoiceSeq;
    }

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
     * Returns the feeTransactionDate.
     * 
     * @return the feeTransactionDate
     */
    public LocalDate getFeeTransactionDate() {
        return feeTransactionDate;
    }

    /** 
     * Sets the feeTransactionDate.
     * 
     * @param feeTransactionDate the feeTransactionDate
     */
    public void setFeeTransactionDate(LocalDate feeTransactionDate) {
        this.feeTransactionDate = feeTransactionDate;
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