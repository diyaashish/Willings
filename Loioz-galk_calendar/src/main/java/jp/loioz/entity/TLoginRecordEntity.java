package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * ログイン記録
 */
@Entity(listener = TLoginRecordEntityListener.class)
@Table(schema ="service_mgt", name = "t_login_record")
public class TLoginRecordEntity extends DefaultEntity {

    /** テナント連番 */
    @Id
    @Column(name = "tenant_seq")
    Long tenantSeq;

    /** アカウント連番 */
    @Id
    @Column(name = "account_seq")
    Long accountSeq;

    /** セッションID */
    @Column(name = "session_id")
    String sessionId;

    /** 作成日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 作成アカウントSEQ */
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
     * Returns the sessionId.
     * 
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /** 
     * Sets the sessionId.
     * 
     * @param sessionId the sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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