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
 * 入出金項目
 */
@Entity(listener = MNyushukkinKomokuEntityListener.class)
@Table(name = "m_nyushukkin_komoku")
public class MNyushukkinKomokuEntity extends DefaultEntity {

    /** 入出金項目ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nyushukkin_komoku_id")
    Long nyushukkinKomokuId;

    /** 入出金タイプ */
    @Column(name = "nyushukkin_type")
    String nyushukkinType;

    /** 項目名 */
    @Column(name = "komoku_name")
    String komokuName;

    /** 課税フラグ */
    @Column(name = "tax_flg")
    String taxFlg;

    /** 表示順 */
    @Column(name = "disp_order")
    Long dispOrder;

    /** 無効フラグ */
    @Column(name = "disabled_flg")
    String disabledFlg;

    /** 削除不可フラグ */
    @Column(name = "undeletable_flg")
    String undeletableFlg;

    /** 登録日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントID */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントID */
    @Column(name = "updated_by")
    Long updatedBy;

    /** 削除日 */
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
     * Returns the nyushukkinKomokuId.
     * 
     * @return the nyushukkinKomokuId
     */
    public Long getNyushukkinKomokuId() {
        return nyushukkinKomokuId;
    }

    /** 
     * Sets the nyushukkinKomokuId.
     * 
     * @param nyushukkinKomokuId the nyushukkinKomokuId
     */
    public void setNyushukkinKomokuId(Long nyushukkinKomokuId) {
        this.nyushukkinKomokuId = nyushukkinKomokuId;
    }

    /** 
     * Returns the nyushukkinType.
     * 
     * @return the nyushukkinType
     */
    public String getNyushukkinType() {
        return nyushukkinType;
    }

    /** 
     * Sets the nyushukkinType.
     * 
     * @param nyushukkinType the nyushukkinType
     */
    public void setNyushukkinType(String nyushukkinType) {
        this.nyushukkinType = nyushukkinType;
    }

    /** 
     * Returns the komokuName.
     * 
     * @return the komokuName
     */
    public String getKomokuName() {
        return komokuName;
    }

    /** 
     * Sets the komokuName.
     * 
     * @param komokuName the komokuName
     */
    public void setKomokuName(String komokuName) {
        this.komokuName = komokuName;
    }

    /** 
     * Returns the taxFlg.
     * 
     * @return the taxFlg
     */
    public String getTaxFlg() {
        return taxFlg;
    }

    /** 
     * Sets the taxFlg.
     * 
     * @param taxFlg the taxFlg
     */
    public void setTaxFlg(String taxFlg) {
        this.taxFlg = taxFlg;
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
     * Returns the undeletableFlg.
     * 
     * @return the undeletableFlg
     */
    public String getUndeletableFlg() {
        return undeletableFlg;
    }

    /** 
     * Sets the undeletableFlg.
     * 
     * @param undeletableFlg the undeletableFlg
     */
    public void setUndeletableFlg(String undeletableFlg) {
        this.undeletableFlg = undeletableFlg;
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