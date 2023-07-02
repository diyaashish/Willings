package jp.loioz.entity;

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
 * 請求項目
 */
@Entity(listener = TAccgDocInvoiceEntityListener.class)
@Table(name = "t_accg_doc_invoice")
public class TAccgDocInvoiceEntity extends DefaultEntity {

    /** 請求項目SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_invoice_seq")
    Long docInvoiceSeq;

    /** 会計書類SEQ */
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 並び順 */
    @Column(name = "doc_invoice_order")
    Long docInvoiceOrder;

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
     * Returns the docInvoiceOrder.
     * 
     * @return the docInvoiceOrder
     */
    public Long getDocInvoiceOrder() {
        return docInvoiceOrder;
    }

    /** 
     * Sets the docInvoiceOrder.
     * 
     * @param docInvoiceOrder the docInvoiceOrder
     */
    public void setDocInvoiceOrder(Long docInvoiceOrder) {
        this.docInvoiceOrder = docInvoiceOrder;
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