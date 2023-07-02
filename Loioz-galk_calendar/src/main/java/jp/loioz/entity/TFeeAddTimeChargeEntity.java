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
 * タイムチャージ-報酬付帯情報
 */
@Entity(listener = TFeeAddTimeChargeEntityListener.class)
@Table(name = "t_fee_add_time_charge")
public class TFeeAddTimeChargeEntity extends DefaultEntity {

    /** 報酬SEQ */
    @Id
    @Column(name = "fee_seq")
    Long feeSeq;

    /** 時間単価 */
    @Column(name = "hour_price")
    BigDecimal hourPrice;

    /** 作業時間（分） */
    @Column(name = "work_time_minute")
    Integer workTimeMinute;

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
     * Returns the hourPrice.
     * 
     * @return the hourPrice
     */
    public BigDecimal getHourPrice() {
        return hourPrice;
    }

    /** 
     * Sets the hourPrice.
     * 
     * @param hourPrice the hourPrice
     */
    public void setHourPrice(BigDecimal hourPrice) {
        this.hourPrice = hourPrice;
    }

    /** 
     * Returns the workTimeMinute.
     * 
     * @return the workTimeMinute
     */
    public Integer getWorkTimeMinute() {
        return workTimeMinute;
    }

    /** 
     * Sets the workTimeMinute.
     * 
     * @param workTimeMinute the workTimeMinute
     */
    public void setWorkTimeMinute(Integer workTimeMinute) {
        this.workTimeMinute = workTimeMinute;
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