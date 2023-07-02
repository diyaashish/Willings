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
 * ファイル管理表示優先順位マスタ
 */
@Entity(listener = MFileManagementDisplayPriorityEntityListener.class)
@Table(name = "m_file_management_display_priority")
public class MFileManagementDisplayPriorityEntity extends DefaultEntity {

    /** ファイル管理表示優先順位ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_management_display_priority_id")
    Long fileManagementDisplayPriorityId;

    /** 表示優先順位 */
    @Column(name = "display_priority")
    Long displayPriority;

    /** 備考 */
    @Column(name = "remarks")
    String remarks;

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
     * Returns the displayPriority.
     * 
     * @return the displayPriority
     */
    public Long getDisplayPriority() {
        return displayPriority;
    }

    /** 
     * Sets the displayPriority.
     * 
     * @param displayPriority the displayPriority
     */
    public void setDisplayPriority(Long displayPriority) {
        this.displayPriority = displayPriority;
    }

    /** 
     * Returns the remarks.
     * 
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /** 
     * Sets the remarks.
     * 
     * @param remarks the remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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