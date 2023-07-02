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
 * 接続中外部サービス
 */
@Entity(listener = TConnectedExternalServiceEntityListener.class)
@Table(name = "t_connected_external_service")
public class TConnectedExternalServiceEntity extends DefaultEntity {

    /** 接続中外部サービス連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connected_external_service_seq")
    Long connectedExternalServiceSeq;

    /** サービス種別 */
    @Column(name = "service_type")
    String serviceType;

    /** 外部サービスID */
    @Column(name = "external_service_id")
    String externalServiceId;

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
     * Returns the connectedExternalServiceSeq.
     * 
     * @return the connectedExternalServiceSeq
     */
    public Long getConnectedExternalServiceSeq() {
        return connectedExternalServiceSeq;
    }

    /** 
     * Sets the connectedExternalServiceSeq.
     * 
     * @param connectedExternalServiceSeq the connectedExternalServiceSeq
     */
    public void setConnectedExternalServiceSeq(Long connectedExternalServiceSeq) {
        this.connectedExternalServiceSeq = connectedExternalServiceSeq;
    }

    /** 
     * Returns the serviceType.
     * 
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /** 
     * Sets the serviceType.
     * 
     * @param serviceType the serviceType
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /** 
     * Returns the externalServiceId.
     * 
     * @return the externalServiceId
     */
    public String getExternalServiceId() {
        return externalServiceId;
    }

    /** 
     * Sets the externalServiceId.
     * 
     * @param externalServiceId the externalServiceId
     */
    public void setExternalServiceId(String externalServiceId) {
        this.externalServiceId = externalServiceId;
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