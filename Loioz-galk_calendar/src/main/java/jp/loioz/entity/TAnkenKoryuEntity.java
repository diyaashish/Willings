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
 * 案件-勾留
 */
@Entity(listener = TAnkenKoryuEntityListener.class)
@Table(name = "t_anken_koryu")
public class TAnkenKoryuEntity extends DefaultEntity {

    /** 勾留SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "koryu_seq")
    Long koryuSeq;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 顧客ID */
    @Column(name = "customer_id")
    Long customerId;

    /** 勾留日 */
    @Column(name = "koryu_date")
    LocalDate koryuDate;

    /** 保釈日 */
    @Column(name = "hoshaku_date")
    LocalDate hoshakuDate;

    /** 捜査機関ID */
    @Column(name = "sosakikan_id")
    Long sosakikanId;

    /** 勾留場所名 */
    @Column(name = "koryu_place_name")
    String koryuPlaceName;

    /** 捜査機関電話番号 */
    @Column(name = "sosakikan_tel_no")
    String sosakikanTelNo;

    /** 備考 */
    @Column(name = "remarks")
    String remarks;

    /** 登録日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントSEQ */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントSEQ */
    @Column(name = "updated_by")
    Long updatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the koryuSeq.
     * 
     * @return the koryuSeq
     */
    public Long getKoryuSeq() {
        return koryuSeq;
    }

    /** 
     * Sets the koryuSeq.
     * 
     * @param koryuSeq the koryuSeq
     */
    public void setKoryuSeq(Long koryuSeq) {
        this.koryuSeq = koryuSeq;
    }

    /** 
     * Returns the ankenId.
     * 
     * @return the ankenId
     */
    public Long getAnkenId() {
        return ankenId;
    }

    /** 
     * Sets the ankenId.
     * 
     * @param ankenId the ankenId
     */
    public void setAnkenId(Long ankenId) {
        this.ankenId = ankenId;
    }

    /** 
     * Returns the customerId.
     * 
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /** 
     * Sets the customerId.
     * 
     * @param customerId the customerId
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /** 
     * Returns the koryuDate.
     * 
     * @return the koryuDate
     */
    public LocalDate getKoryuDate() {
        return koryuDate;
    }

    /** 
     * Sets the koryuDate.
     * 
     * @param koryuDate the koryuDate
     */
    public void setKoryuDate(LocalDate koryuDate) {
        this.koryuDate = koryuDate;
    }

    /** 
     * Returns the hoshakuDate.
     * 
     * @return the hoshakuDate
     */
    public LocalDate getHoshakuDate() {
        return hoshakuDate;
    }

    /** 
     * Sets the hoshakuDate.
     * 
     * @param hoshakuDate the hoshakuDate
     */
    public void setHoshakuDate(LocalDate hoshakuDate) {
        this.hoshakuDate = hoshakuDate;
    }

    /** 
     * Returns the sosakikanId.
     * 
     * @return the sosakikanId
     */
    public Long getSosakikanId() {
        return sosakikanId;
    }

    /** 
     * Sets the sosakikanId.
     * 
     * @param sosakikanId the sosakikanId
     */
    public void setSosakikanId(Long sosakikanId) {
        this.sosakikanId = sosakikanId;
    }

    /** 
     * Returns the koryuPlaceName.
     * 
     * @return the koryuPlaceName
     */
    public String getKoryuPlaceName() {
        return koryuPlaceName;
    }

    /** 
     * Sets the koryuPlaceName.
     * 
     * @param koryuPlaceName the koryuPlaceName
     */
    public void setKoryuPlaceName(String koryuPlaceName) {
        this.koryuPlaceName = koryuPlaceName;
    }

    /** 
     * Returns the sosakikanTelNo.
     * 
     * @return the sosakikanTelNo
     */
    public String getSosakikanTelNo() {
        return sosakikanTelNo;
    }

    /** 
     * Sets the sosakikanTelNo.
     * 
     * @param sosakikanTelNo the sosakikanTelNo
     */
    public void setSosakikanTelNo(String sosakikanTelNo) {
        this.sosakikanTelNo = sosakikanTelNo;
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