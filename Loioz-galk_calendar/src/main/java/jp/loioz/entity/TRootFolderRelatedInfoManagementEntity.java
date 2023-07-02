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
 * ルートフォルダ関連情報管理
 */
@Entity(listener = TRootFolderRelatedInfoManagementEntityListener.class)
@Table(name = "t_root_folder_related_info_management")
public class TRootFolderRelatedInfoManagementEntity extends DefaultEntity {

    /** ルートフォルダ関連情報管理ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "root_folder_related_info_management_id")
    Long rootFolderRelatedInfoManagementId;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 顧客ID */
    @Column(name = "customer_id")
    Long customerId;

    /** 登録日 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントSEQ */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日 */
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
     * Returns the rootFolderRelatedInfoManagementId.
     * 
     * @return the rootFolderRelatedInfoManagementId
     */
    public Long getRootFolderRelatedInfoManagementId() {
        return rootFolderRelatedInfoManagementId;
    }

    /** 
     * Sets the rootFolderRelatedInfoManagementId.
     * 
     * @param rootFolderRelatedInfoManagementId the rootFolderRelatedInfoManagementId
     */
    public void setRootFolderRelatedInfoManagementId(Long rootFolderRelatedInfoManagementId) {
        this.rootFolderRelatedInfoManagementId = rootFolderRelatedInfoManagementId;
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