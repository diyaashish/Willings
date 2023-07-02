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
 * 預り金項目マスタ
 */
@Entity(listener = MDepositItemEntityListener.class)
@Table(name = "m_deposit_item")
public class MDepositItemEntity extends DefaultEntity {

    /** 預り金項目マスタSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_item_seq")
    Long depositItemSeq;

    /** 入出金タイプ */
    @Column(name = "deposit_type")
    String depositType;

    /** 項目名 */
    @Column(name = "deposit_item_name")
    String depositItemName;

    /** 備考 */
    @Column(name = "remarks")
    String remarks;

    /** 表示順 */
    @Column(name = "disp_order")
    Long dispOrder;

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
     * Returns the depositItemSeq.
     * 
     * @return the depositItemSeq
     */
    public Long getDepositItemSeq() {
        return depositItemSeq;
    }

    /** 
     * Sets the depositItemSeq.
     * 
     * @param depositItemSeq the depositItemSeq
     */
    public void setDepositItemSeq(Long depositItemSeq) {
        this.depositItemSeq = depositItemSeq;
    }

    /** 
     * Returns the depositType.
     * 
     * @return the depositType
     */
    public String getDepositType() {
        return depositType;
    }

    /** 
     * Sets the depositType.
     * 
     * @param depositType the depositType
     */
    public void setDepositType(String depositType) {
        this.depositType = depositType;
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
     * Returns the dispOrder.
     * 
     * @return the dispOrder
     */
    public Long getDispOrder() {
        return dispOrder;
    }

    /** 
     * Sets the dispOrder.
     * 
     * @param dispOrder the dispOrder
     */
    public void setDispOrder(Long dispOrder) {
        this.dispOrder = dispOrder;
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