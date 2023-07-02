package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * ロイオズ管理者制御
 */
@Entity(listener = MLoiozAdminControlEntityListener.class)
@Table(name = "m_loioz_admin_control")
public class MLoiozAdminControlEntity extends DefaultEntity {

    /** 管理者制御ID */
    @Id
    @Column(name = "admin_control_id")
    String adminControlId;

    /** 管理者制御名 */
    @Column(name = "admin_control_name")
    String adminControlName;

    /** 設定値 */
    @Column(name = "admin_control_value")
    String adminControlValue;

    /** メモ */
    @Column(name = "admin_control_memo")
    String adminControlMemo;

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
     * Returns the adminControlId.
     * 
     * @return the adminControlId
     */
    public String getAdminControlId() {
        return adminControlId;
    }

    /** 
     * Sets the adminControlId.
     * 
     * @param adminControlId the adminControlId
     */
    public void setAdminControlId(String adminControlId) {
        this.adminControlId = adminControlId;
    }

    /** 
     * Returns the adminControlName.
     * 
     * @return the adminControlName
     */
    public String getAdminControlName() {
        return adminControlName;
    }

    /** 
     * Sets the adminControlName.
     * 
     * @param adminControlName the adminControlName
     */
    public void setAdminControlName(String adminControlName) {
        this.adminControlName = adminControlName;
    }

    /** 
     * Returns the adminControlValue.
     * 
     * @return the adminControlValue
     */
    public String getAdminControlValue() {
        return adminControlValue;
    }

    /** 
     * Sets the adminControlValue.
     * 
     * @param adminControlValue the adminControlValue
     */
    public void setAdminControlValue(String adminControlValue) {
        this.adminControlValue = adminControlValue;
    }

    /** 
     * Returns the adminControlMemo.
     * 
     * @return the adminControlMemo
     */
    public String getAdminControlMemo() {
        return adminControlMemo;
    }

    /** 
     * Sets the adminControlMemo.
     * 
     * @param adminControlMemo the adminControlMemo
     */
    public void setAdminControlMemo(String adminControlMemo) {
        this.adminControlMemo = adminControlMemo;
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