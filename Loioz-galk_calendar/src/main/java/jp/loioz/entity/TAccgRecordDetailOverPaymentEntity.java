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
 * 取引実績明細-過入金
 */
@Entity(listener = TAccgRecordDetailOverPaymentEntityListener.class)
@Table(name = "t_accg_record_detail_over_payment")
public class TAccgRecordDetailOverPaymentEntity extends DefaultEntity {

    /** 取引実績明細過入金SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_record_detail_over_payment_seq")
    Long accgRecordDetailOverPaymentSeq;

    /** 取引実績明細SEQ */
    @Column(name = "accg_record_detail_seq")
    Long accgRecordDetailSeq;

    /** 過入金額 */
    @Column(name = "over_payment_amount")
    BigDecimal overPaymentAmount;

    /** 過入金返金フラグ */
    @Column(name = "over_payment_refund_flg")
    String overPaymentRefundFlg;

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
     * Returns the accgRecordDetailOverPaymentSeq.
     * 
     * @return the accgRecordDetailOverPaymentSeq
     */
    public Long getAccgRecordDetailOverPaymentSeq() {
        return accgRecordDetailOverPaymentSeq;
    }

    /** 
     * Sets the accgRecordDetailOverPaymentSeq.
     * 
     * @param accgRecordDetailOverPaymentSeq the accgRecordDetailOverPaymentSeq
     */
    public void setAccgRecordDetailOverPaymentSeq(Long accgRecordDetailOverPaymentSeq) {
        this.accgRecordDetailOverPaymentSeq = accgRecordDetailOverPaymentSeq;
    }

    /** 
     * Returns the accgRecordDetailSeq.
     * 
     * @return the accgRecordDetailSeq
     */
    public Long getAccgRecordDetailSeq() {
        return accgRecordDetailSeq;
    }

    /** 
     * Sets the accgRecordDetailSeq.
     * 
     * @param accgRecordDetailSeq the accgRecordDetailSeq
     */
    public void setAccgRecordDetailSeq(Long accgRecordDetailSeq) {
        this.accgRecordDetailSeq = accgRecordDetailSeq;
    }

    /** 
     * Returns the overPaymentAmount.
     * 
     * @return the overPaymentAmount
     */
    public BigDecimal getOverPaymentAmount() {
        return overPaymentAmount;
    }

    /** 
     * Sets the overPaymentAmount.
     * 
     * @param overPaymentAmount the overPaymentAmount
     */
    public void setOverPaymentAmount(BigDecimal overPaymentAmount) {
        this.overPaymentAmount = overPaymentAmount;
    }

    /** 
     * Returns the overPaymentRefundFlg.
     * 
     * @return the overPaymentRefundFlg
     */
    public String getOverPaymentRefundFlg() {
        return overPaymentRefundFlg;
    }

    /** 
     * Sets the overPaymentRefundFlg.
     * 
     * @param overPaymentRefundFlg the overPaymentRefundFlg
     */
    public void setOverPaymentRefundFlg(String overPaymentRefundFlg) {
        this.overPaymentRefundFlg = overPaymentRefundFlg;
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