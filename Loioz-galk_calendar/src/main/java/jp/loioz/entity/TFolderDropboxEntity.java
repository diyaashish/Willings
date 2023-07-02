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
 * Dropboxフォルダ
 */
@Entity(listener = TFolderDropboxEntityListener.class)
@Table(name = "t_folder_dropbox")
public class TFolderDropboxEntity extends DefaultEntity {

    /** フォルダDropbox連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_dropbox_seq")
    Long folderDropboxSeq;

    /** ルートフォルダDropbox連番 */
    @Column(name = "root_folder_dropbox_seq")
    Long rootFolderDropboxSeq;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 顧客ID */
    @Column(name = "customer_id")
    Long customerId;

    /** 案件顧客IDFrom */
    @Column(name = "anken_customer_id_from")
    Long ankenCustomerIdFrom;

    /** 案件顧客IDTo */
    @Column(name = "anken_customer_id_to")
    Long ankenCustomerIdTo;

    /** フォルダID */
    @Column(name = "folder_id")
    String folderId;

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
     * Returns the folderDropboxSeq.
     * 
     * @return the folderDropboxSeq
     */
    public Long getFolderDropboxSeq() {
        return folderDropboxSeq;
    }

    /** 
     * Sets the folderDropboxSeq.
     * 
     * @param folderDropboxSeq the folderDropboxSeq
     */
    public void setFolderDropboxSeq(Long folderDropboxSeq) {
        this.folderDropboxSeq = folderDropboxSeq;
    }

    /** 
     * Returns the rootFolderDropboxSeq.
     * 
     * @return the rootFolderDropboxSeq
     */
    public Long getRootFolderDropboxSeq() {
        return rootFolderDropboxSeq;
    }

    /** 
     * Sets the rootFolderDropboxSeq.
     * 
     * @param rootFolderDropboxSeq the rootFolderDropboxSeq
     */
    public void setRootFolderDropboxSeq(Long rootFolderDropboxSeq) {
        this.rootFolderDropboxSeq = rootFolderDropboxSeq;
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
     * Returns the ankenCustomerIdFrom.
     * 
     * @return the ankenCustomerIdFrom
     */
    public Long getAnkenCustomerIdFrom() {
        return ankenCustomerIdFrom;
    }

    /** 
     * Sets the ankenCustomerIdFrom.
     * 
     * @param ankenCustomerIdFrom the ankenCustomerIdFrom
     */
    public void setAnkenCustomerIdFrom(Long ankenCustomerIdFrom) {
        this.ankenCustomerIdFrom = ankenCustomerIdFrom;
    }

    /** 
     * Returns the ankenCustomerIdTo.
     * 
     * @return the ankenCustomerIdTo
     */
    public Long getAnkenCustomerIdTo() {
        return ankenCustomerIdTo;
    }

    /** 
     * Sets the ankenCustomerIdTo.
     * 
     * @param ankenCustomerIdTo the ankenCustomerIdTo
     */
    public void setAnkenCustomerIdTo(Long ankenCustomerIdTo) {
        this.ankenCustomerIdTo = ankenCustomerIdTo;
    }

    /** 
     * Returns the folderId.
     * 
     * @return the folderId
     */
    public String getFolderId() {
        return folderId;
    }

    /** 
     * Sets the folderId.
     * 
     * @param folderId the folderId
     */
    public void setFolderId(String folderId) {
        this.folderId = folderId;
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