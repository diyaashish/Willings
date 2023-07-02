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
 * ファイル構成管理
 */
@Entity(listener = TFileConfigurationManagementEntityListener.class)
@Table(name = "t_file_configuration_management")
public class TFileConfigurationManagementEntity extends DefaultEntity {

    /** ファイル構成管理ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_configuration_management_id")
    Long fileConfigurationManagementId;

    /** ルートフォルダ関連情報管理ID */
    @Column(name = "root_folder_related_info_management_id")
    Long rootFolderRelatedInfoManagementId;

    /** ファイル詳細情報管理ID */
    @Column(name = "file_detail_info_management_id")
    Long fileDetailInfoManagementId;

    /** ファイル管理表示優先順位ID */
    @Column(name = "file_management_display_priority_id")
    Long fileManagementDisplayPriorityId;

    /** 親ファイル構成管理ID */
    @Column(name = "parent_file_configuration_management_id")
    Long parentFileConfigurationManagementId;

    /** ファイル区分 */
    @Column(name = "file_kubun")
    String fileKubun;

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
     * Returns the fileDetailInfoManagementId.
     * 
     * @return the fileDetailInfoManagementId
     */
    public Long getFileDetailInfoManagementId() {
        return fileDetailInfoManagementId;
    }

    /** 
     * Sets the fileDetailInfoManagementId.
     * 
     * @param fileDetailInfoManagementId the fileDetailInfoManagementId
     */
    public void setFileDetailInfoManagementId(Long fileDetailInfoManagementId) {
        this.fileDetailInfoManagementId = fileDetailInfoManagementId;
    }

    /** 
     * Returns the fileManagementDisplayPriorityId.
     * 
     * @return the fileManagementDisplayPriorityId
     */
    public Long getFileManagementDisplayPriorityId() {
        return fileManagementDisplayPriorityId;
    }

    /** 
     * Sets the fileManagementDisplayPriorityId.
     * 
     * @param fileManagementDisplayPriorityId the fileManagementDisplayPriorityId
     */
    public void setFileManagementDisplayPriorityId(Long fileManagementDisplayPriorityId) {
        this.fileManagementDisplayPriorityId = fileManagementDisplayPriorityId;
    }

    /** 
     * Returns the parentFileConfigurationManagementId.
     * 
     * @return the parentFileConfigurationManagementId
     */
    public Long getParentFileConfigurationManagementId() {
        return parentFileConfigurationManagementId;
    }

    /** 
     * Sets the parentFileConfigurationManagementId.
     * 
     * @param parentFileConfigurationManagementId the parentFileConfigurationManagementId
     */
    public void setParentFileConfigurationManagementId(Long parentFileConfigurationManagementId) {
        this.parentFileConfigurationManagementId = parentFileConfigurationManagementId;
    }

    /** 
     * Returns the fileKubun.
     * 
     * @return the fileKubun
     */
    public String getFileKubun() {
        return fileKubun;
    }

    /** 
     * Sets the fileKubun.
     * 
     * @param fileKubun the fileKubun
     */
    public void setFileKubun(String fileKubun) {
        this.fileKubun = fileKubun;
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