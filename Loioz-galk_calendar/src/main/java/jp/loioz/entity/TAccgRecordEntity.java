package jp.loioz.entity;

import java.math.BigDecimal;
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
 * 取引実績
 */
@Entity(listener = TAccgRecordEntityListener.class)
@Table(name = "t_accg_record")
public class TAccgRecordEntity extends DefaultEntity {

    /** 取引実績SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_record_seq")
    Long accgRecordSeq;

    /** 会計書類SEQ */
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 報酬入金額【見込】 */
    @Column(name = "fee_amount_expect")
    BigDecimal feeAmountExpect;

    /** 預り金入金額【見込】 */
    @Column(name = "deposit_recv_amount_expect")
    BigDecimal depositRecvAmountExpect;

    /** 預り金出金額【見込】 */
    @Column(name = "deposit_payment_amount_expect")
    BigDecimal depositPaymentAmountExpect;

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
     * Returns the accgRecordSeq.
     * 
     * @return the accgRecordSeq
     */
    public Long getAccgRecordSeq() {
        return accgRecordSeq;
    }

    /** 
     * Sets the accgRecordSeq.
     * 
     * @param accgRecordSeq the accgRecordSeq
     */
    public void setAccgRecordSeq(Long accgRecordSeq) {
        this.accgRecordSeq = accgRecordSeq;
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
     * Returns the feeAmountExpect.
     * 
     * @return the feeAmountExpect
     */
    public BigDecimal getFeeAmountExpect() {
        return feeAmountExpect;
    }

    /** 
     * Sets the feeAmountExpect.
     * 
     * @param feeAmountExpect the feeAmountExpect
     */
    public void setFeeAmountExpect(BigDecimal feeAmountExpect) {
        this.feeAmountExpect = feeAmountExpect;
    }

    /** 
     * Returns the depositRecvAmountExpect.
     * 
     * @return the depositRecvAmountExpect
     */
    public BigDecimal getDepositRecvAmountExpect() {
        return depositRecvAmountExpect;
    }

    /** 
     * Sets the depositRecvAmountExpect.
     * 
     * @param depositRecvAmountExpect the depositRecvAmountExpect
     */
    public void setDepositRecvAmountExpect(BigDecimal depositRecvAmountExpect) {
        this.depositRecvAmountExpect = depositRecvAmountExpect;
    }

    /** 
     * Returns the depositPaymentAmountExpect.
     * 
     * @return the depositPaymentAmountExpect
     */
    public BigDecimal getDepositPaymentAmountExpect() {
        return depositPaymentAmountExpect;
    }

    /** 
     * Sets the depositPaymentAmountExpect.
     * 
     * @param depositPaymentAmountExpect the depositPaymentAmountExpect
     */
    public void setDepositPaymentAmountExpect(BigDecimal depositPaymentAmountExpect) {
        this.depositPaymentAmountExpect = depositPaymentAmountExpect;
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