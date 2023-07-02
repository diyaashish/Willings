package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * テナント機能設定
 */
@Entity(listener = MTenantFuncSettingEntityListener.class)
@Table(name = "m_tenant_func_setting")
public class MTenantFuncSettingEntity extends DefaultEntity {

    /** 機能設定ID */
    @Id
    @Column(name = "func_setting_id")
    String funcSettingId;

    /** 機能設定名 */
    @Column(name = "func_setting_name")
    String funcSettingName;

    /** 設定値 */
    @Column(name = "func_setting_value")
    String funcSettingValue;

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
     * Returns the funcSettingId.
     * 
     * @return the funcSettingId
     */
    public String getFuncSettingId() {
        return funcSettingId;
    }

    /** 
     * Sets the funcSettingId.
     * 
     * @param funcSettingId the funcSettingId
     */
    public void setFuncSettingId(String funcSettingId) {
        this.funcSettingId = funcSettingId;
    }

    /** 
     * Returns the funcSettingName.
     * 
     * @return the funcSettingName
     */
    public String getFuncSettingName() {
        return funcSettingName;
    }

    /** 
     * Sets the funcSettingName.
     * 
     * @param funcSettingName the funcSettingName
     */
    public void setFuncSettingName(String funcSettingName) {
        this.funcSettingName = funcSettingName;
    }

    /** 
     * Returns the funcSettingValue.
     * 
     * @return the funcSettingValue
     */
    public String getFuncSettingValue() {
        return funcSettingValue;
    }

    /** 
     * Sets the funcSettingValue.
     * 
     * @param funcSettingValue the funcSettingValue
     */
    public void setFuncSettingValue(String funcSettingValue) {
        this.funcSettingValue = funcSettingValue;
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