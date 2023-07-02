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
 * 請求項目-預り金
 */
@Entity(listener = TAccgDocInvoiceDepositEntityListener.class)
@Table(name = "t_accg_doc_invoice_deposit")
public class TAccgDocInvoiceDepositEntity extends DefaultEntity {

    /** 請求預り金項目SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_invoice_deposit_seq")
    Long docInvoiceDepositSeq;

    /** 請求項目SEQ */
    @Column(name = "doc_invoice_seq")
    Long docInvoiceSeq;

    /** 取引日 */
    @Column(name = "deposit_transaction_date")
    LocalDate depositTransactionDate;

    /** 項目名 */
    @Column(name = "deposit_item_name")
    String depositItemName;

    /** 預り金金額 */
    @Column(name = "deposit_amount")
    BigDecimal depositAmount;

    /** 預り金タイプ */
    @Column(name = "invoice_deposit_type")
    String invoiceDepositType;

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
     * Returns the docInvoiceDepositSeq.
     * 
     * @return the docInvoiceDepositSeq
     */
    public Long getDocInvoiceDepositSeq() {
        return docInvoiceDepositSeq;
    }

    /** 
     * Sets the docInvoiceDepositSeq.
     * 
     * @param docInvoiceDepositSeq the docInvoiceDepositSeq
     */
    public void setDocInvoiceDepositSeq(Long docInvoiceDepositSeq) {
        this.docInvoiceDepositSeq = docInvoiceDepositSeq;
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
     * Returns the depositTransactionDate.
     * 
     * @return the depositTransactionDate
     */
    public LocalDate getDepositTransactionDate() {
        return depositTransactionDate;
    }

    /** 
     * Sets the depositTransactionDate.
     * 
     * @param depositTransactionDate the depositTransactionDate
     */
    public void setDepositTransactionDate(LocalDate depositTransactionDate) {
        this.depositTransactionDate = depositTransactionDate;
    }

    /** 
     * Returns the depositItemName.
     * 
     * @return the depositItemName
     */
    public String getDepositItemName() {
        return depositItemName;
    }

    /** 
     * Sets the depositItemName.
     * 
     * @param depositItemName the depositItemName
     */
    public void setDepositItemName(String depositItemName) {
        this.depositItemName = depositItemName;
    }

    /** 
     * Returns the depositAmount.
     * 
     * @return the depositAmount
     */
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    /** 
     * Sets the depositAmount.
     * 
     * @param depositAmount the depositAmount
     */
    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    /** 
     * Returns the invoiceDepositType.
     * 
     * @return the invoiceDepositType
     */
    public String getInvoiceDepositType() {
        return invoiceDepositType;
    }

    /** 
     * Sets the invoiceDepositType.
     * 
     * @param invoiceDepositType the invoiceDepositType
     */
    public void setInvoiceDepositType(String invoiceDepositType) {
        this.invoiceDepositType = invoiceDepositType;
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