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
 * 分野
 */
@Entity(listener = MBunyaEntityListener.class)
@Table(name = "m_bunya")
public class MBunyaEntity extends DefaultEntity {

    /** 分野ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bunya_id")
    Long bunyaId;

    /** 分野区分 */
    @Column(name = "bunya_type")
    String bunyaType;

    /** 分野名 */
    @Column(name = "bunya_name")
    String bunyaName;

    /** 表示順 */
    @Column(name = "disp_order")
    Long dispOrder;

    /** 無効フラグ */
    @Column(name = "disabled_flg")
    String disabledFlg;

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

    /** 削除日時 */
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    /** 削除アカウントSEQ */
    @Column(name = "deleted_by")
    Long deletedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the bunyaId.
     * 
     * @return the bunyaId
     */
    public Long getBunyaId() {
        return bunyaId;
    }

    /** 
     * Sets the bunyaId.
     * 
     * @param bunyaId the bunyaId
     */
    public void setBunyaId(Long bunyaId) {
        this.bunyaId = bunyaId;
    }

    /** 
     * Returns the bunyaType.
     * 
     * @return the bunyaType
     */
    public String getBunyaType() {
        return bunyaType;
    }

    /** 
     * Sets the bunyaType.
     * 
     * @param bunyaType the bunyaType
     */
    public void setBunyaType(String bunyaType) {
        this.bunyaType = bunyaType;
    }

    /** 
     * Returns the bunyaName.
     * 
     * @return the bunyaName
     */
    public String getBunyaName() {
        return bunyaName;
    }

    /** 
     * Sets the bunyaName.
     * 
     * @param bunyaName the bunyaName
     */
    public void setBunyaName(String bunyaName) {
        this.bunyaName = bunyaName;
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
     * Returns the disabledFlg.
     * 
     * @return the disabledFlg
     */
    public String getDisabledFlg() {
        return disabledFlg;
    }

    /** 
     * Sets the disabledFlg.
     * 
     * @param disabledFlg the disabledFlg
     */
    public void setDisabledFlg(String disabledFlg) {
        this.disabledFlg = disabledFlg;
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
     * Returns the deletedAt.
     * 
     * @return the deletedAt
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /** 
     * Sets the deletedAt.
     * 
     * @param deletedAt the deletedAt
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /** 
     * Returns the deletedBy.
     * 
     * @return the deletedBy
     */
    public Long getDeletedBy() {
        return deletedBy;
    }

    /** 
     * Sets the deletedBy.
     * 
     * @param deletedBy the deletedBy
     */
    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
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