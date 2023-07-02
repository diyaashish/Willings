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
 * フォルダ権限情報管理
 */
@Entity(listener = TFolderPermissionInfoManagementEntityListener.class)
@Table(name = "t_folder_permission_info_management")
public class TFolderPermissionInfoManagementEntity extends DefaultEntity {

    /** フォルダ権限情報管理ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_permission_info_management_id")
    Long folderPermissionInfoManagementId;

    /** ファイル構成管理ID */
    @Column(name = "file_configuration_management_id")
    Long fileConfigurationManagementId;

    /** 閲覧制限 */
    @Column(name = "view_limit")
    String viewLimit;

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
     * Returns the folderPermissionInfoManagementId.
     * 
     * @return the folderPermissionInfoManagementId
     */
    public Long getFolderPermissionInfoManagementId() {
        return folderPermissionInfoManagementId;
    }

    /** 
     * Sets the folderPermissionInfoManagementId.
     * 
     * @param folderPermissionInfoManagementId the folderPermissionInfoManagementId
     */
    public void setFolderPermissionInfoManagementId(Long folderPermissionInfoManagementId) {
        this.folderPermissionInfoManagementId = folderPermissionInfoManagementId;
    }

    /** 
     * Returns the fileConfigurationManagementId.
     * 
     * @return the fileConfigurationManagementId
     */
    public Long getFileConfigurationManagementId() {
        return fileConfigurationManagementId;
    }

    /** 
     * Sets the fileConfigurationManagementId.
     * 
     * @param fileConfigurationManagementId the fileConfigurationManagementId
     */
    public void setFileConfigurationManagementId(Long fileConfigurationManagementId) {
        this.fileConfigurationManagementId = fileConfigurationManagementId;
    }

    /** 
     * Returns the viewLimit.
     * 
     * @return the viewLimit
     */
    public String getViewLimit() {
        return viewLimit;
    }

    /** 
     * Sets the viewLimit.
     * 
     * @param viewLimit the viewLimit
     */
    public void setViewLimit(String viewLimit) {
        this.viewLimit = viewLimit;
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