package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * プラン画面へ受け渡すセッション情報
 */
@Entity(listener = TPlanSettingSessionEntityListener.class)
@Table(schema ="service_mgt", name = "t_plan_setting_session")
public class TPlanSettingSessionEntity extends DefaultEntity {

    /** テナント連番 */
    @Id
    @Column(name = "tenant_seq")
    Long tenantSeq;

    /** アカウント連番 */
    @Id
    @Column(name = "account_seq")
    Long accountSeq;

    /** 認証キー */
    @Column(name = "auth_key")
    String authKey;

    /** プラン画面のみにアクセス可能な状態か */
    @Column(name = "only_plan_setting_accessible_flg")
    String onlyPlanSettingAccessibleFlg;

    /** 作成日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the tenantSeq.
     * 
     * @return the tenantSeq
     */
    public Long getTenantSeq() {
        return tenantSeq;
    }

    /** 
     * Sets the tenantSeq.
     * 
     * @param tenantSeq the tenantSeq
     */
    public void setTenantSeq(Long tenantSeq) {
        this.tenantSeq = tenantSeq;
    }

    /** 
     * Returns the accountSeq.
     * 
     * @return the accountSeq
     */
    public Long getAccountSeq() {
        return accountSeq;
    }

    /** 
     * Sets the accountSeq.
     * 
     * @param accountSeq the accountSeq
     */
    public void setAccountSeq(Long accountSeq) {
        this.accountSeq = accountSeq;
    }

    /** 
     * Returns the authKey.
     * 
     * @return the authKey
     */
    public String getAuthKey() {
        return authKey;
    }

    /** 
     * Sets the authKey.
     * 
     * @param authKey the authKey
     */
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    /** 
     * Returns the onlyPlanSettingAccessibleFlg.
     * 
     * @return the onlyPlanSettingAccessibleFlg
     */
    public String getOnlyPlanSettingAccessibleFlg() {
        return onlyPlanSettingAccessibleFlg;
    }

    /** 
     * Sets the onlyPlanSettingAccessibleFlg.
     * 
     * @param onlyPlanSettingAccessibleFlg the onlyPlanSettingAccessibleFlg
     */
    public void setOnlyPlanSettingAccessibleFlg(String onlyPlanSettingAccessibleFlg) {
        this.onlyPlanSettingAccessibleFlg = onlyPlanSettingAccessibleFlg;
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