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
 * 請求項目-源泉徴収
 */
@Entity(listener = TAccgInvoiceWithholdingEntityListener.class)
@Table(name = "t_accg_invoice_withholding")
public class TAccgInvoiceWithholdingEntity extends DefaultEntity {

    /** 会計書類SEQ */
    @Id
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 対象額 */
    @Column(name = "source_withholding_amount")
    BigDecimal sourceWithholdingAmount;

    /** 源泉徴収額 */
    @Column(name = "withholding_amount")
    BigDecimal withholdingAmount;

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
     * Returns the sourceWithholdingAmount.
     * 
     * @return the sourceWithholdingAmount
     */
    public BigDecimal getSourceWithholdingAmount() {
        return sourceWithholdingAmount;
    }

    /** 
     * Sets the sourceWithholdingAmount.
     * 
     * @param sourceWithholdingAmount the sourceWithholdingAmount
     */
    public void setSourceWithholdingAmount(BigDecimal sourceWithholdingAmount) {
        this.sourceWithholdingAmount = sourceWithholdingAmount;
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