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
 * 取引実績明細
 */
@Entity(listener = TAccgRecordDetailEntityListener.class)
@Table(name = "t_accg_record_detail")
public class TAccgRecordDetailEntity extends DefaultEntity {

    /** 取引実績明細SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_record_detail_seq")
    Long accgRecordDetailSeq;

    /** 取引実績SEQ */
    @Column(name = "accg_record_seq")
    Long accgRecordSeq;

    /** 決済日 */
    @Column(name = "record_date")
    LocalDate recordDate;

    /** 取引種別 */
    @Column(name = "record_type")
    String recordType;

    /** 個別入力フラグ */
    @Column(name = "record_separate_input_flg")
    String recordSeparateInputFlg;

    /** 実績入金額 */
    @Column(name = "record_amount")
    BigDecimal recordAmount;

    /** 報酬入金額 */
    @Column(name = "record_fee_amount")
    BigDecimal recordFeeAmount;

    /** 預り金入金額 */
    @Column(name = "record_deposit_recv_amount")
    BigDecimal recordDepositRecvAmount;

    /** 預り金出金額 */
    @Column(name = "record_deposit_payment_amount")
    BigDecimal recordDepositPaymentAmount;

    /** 備考 */
    @Column(name = "remarks")
    String remarks;

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
     * Returns the recordDate.
     * 
     * @return the recordDate
     */
    public LocalDate getRecordDate() {
        return recordDate;
    }

    /** 
     * Sets the recordDate.
     * 
     * @param recordDate the recordDate
     */
    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    /** 
     * Returns the recordType.
     * 
     * @return the recordType
     */
    public String getRecordType() {
        return recordType;
    }

    /** 
     * Sets the recordType.
     * 
     * @param recordType the recordType
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    /** 
     * Returns the recordSeparateInputFlg.
     * 
     * @return the recordSeparateInputFlg
     */
    public String getRecordSeparateInputFlg() {
        return recordSeparateInputFlg;
    }

    /** 
     * Sets the recordSeparateInputFlg.
     * 
     * @param recordSeparateInputFlg the recordSeparateInputFlg
     */
    public void setRecordSeparateInputFlg(String recordSeparateInputFlg) {
        this.recordSeparateInputFlg = recordSeparateInputFlg;
    }

    /** 
     * Returns the recordAmount.
     * 
     * @return the recordAmount
     */
    public BigDecimal getRecordAmount() {
        return recordAmount;
    }

    /** 
     * Sets the recordAmount.
     * 
     * @param recordAmount the recordAmount
     */
    public void setRecordAmount(BigDecimal recordAmount) {
        this.recordAmount = recordAmount;
    }

    /** 
     * Returns the recordFeeAmount.
     * 
     * @return the recordFeeAmount
     */
    public BigDecimal getRecordFeeAmount() {
        return recordFeeAmount;
    }

    /** 
     * Sets the recordFeeAmount.
     * 
     * @param recordFeeAmount the recordFeeAmount
     */
    public void setRecordFeeAmount(BigDecimal recordFeeAmount) {
        this.recordFeeAmount = recordFeeAmount;
    }

    /** 
     * Returns the recordDepositRecvAmount.
     * 
     * @return the recordDepositRecvAmount
     */
    public BigDecimal getRecordDepositRecvAmount() {
        return recordDepositRecvAmount;
    }

    /** 
     * Sets the recordDepositRecvAmount.
     * 
     * @param recordDepositRecvAmount the recordDepositRecvAmount
     */
    public void setRecordDepositRecvAmount(BigDecimal recordDepositRecvAmount) {
        this.recordDepositRecvAmount = recordDepositRecvAmount;
    }

    /** 
     * Returns the recordDepositPaymentAmount.
     * 
     * @return the recordDepositPaymentAmount
     */
    public BigDecimal getRecordDepositPaymentAmount() {
        return recordDepositPaymentAmount;
    }

    /** 
     * Sets the recordDepositPaymentAmount.
     * 
     * @param recordDepositPaymentAmount the recordDepositPaymentAmount
     */
    public void setRecordDepositPaymentAmount(BigDecimal recordDepositPaymentAmount) {
        this.recordDepositPaymentAmount = recordDepositPaymentAmount;
    }

    /** 
     * Returns the remarks.
     * 
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /** 
     * Sets the remarks.
     * 
     * @param remarks the remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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