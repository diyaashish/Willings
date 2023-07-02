package jp.loioz.entity;

import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 
 */
@Entity(listener = TDengonFolderEntityListener.class)
@Table(name = "t_dengon_folder")
public class TDengonFolderEntity extends DefaultEntity {

    /** 伝言フォルダSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dengon_folder_seq")
    Long dengonFolderSeq;

    /** アカウントSEQ */
    @Column(name = "account_seq")
    Long accountSeq;

    /** 伝言フォルダ名 */
    @Column(name = "dengon_folder_name")
    String dengonFolderName;

    /** 親伝言フォルダ */
    @Column(name = "parent_dengon_folder_seq")
    Long parentDengonFolderSeq;

    /** ゴミ箱 */
    @Column(name = "trashed_flg")
    String trashedFlg;

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

    /** 削除日 */
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    /** 削除アカウントSEQ */
    @Column(name = "deleted_by")
    Long deletedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the dengonFolderSeq.
     * 
     * @return the dengonFolderSeq
     */
    public Long getDengonFolderSeq() {
        return dengonFolderSeq;
    }

    /** 
     * Sets the dengonFolderSeq.
     * 
     * @param dengonFolderSeq the dengonFolderSeq
     */
    public void setDengonFolderSeq(Long dengonFolderSeq) {
        this.dengonFolderSeq = dengonFolderSeq;
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
     * Returns the dengonFolderName.
     * 
     * @return the dengonFolderName
     */
    public String getDengonFolderName() {
        return dengonFolderName;
    }

    /** 
     * Sets the dengonFolderName.
     * 
     * @param dengonFolderName the dengonFolderName
     */
    public void setDengonFolderName(String dengonFolderName) {
        this.dengonFolderName = dengonFolderName;
    }

    /** 
     * Returns the parentDengonFolderSeq.
     * 
     * @return the parentDengonFolderSeq
     */
    public Long getParentDengonFolderSeq() {
        return parentDengonFolderSeq;
    }

    /** 
     * Sets the parentDengonFolderSeq.
     * 
     * @param parentDengonFolderSeq the parentDengonFolderSeq
     */
    public void setParentDengonFolderSeq(Long parentDengonFolderSeq) {
        this.parentDengonFolderSeq = parentDengonFolderSeq;
    }

    /** 
     * Returns the trashedFlg.
     * 
     * @return the trashedFlg
     */
    public String getTrashedFlg() {
        return trashedFlg;
    }

    /** 
     * Sets the trashedFlg.
     * 
     * @param trashedFlg the trashedFlg
     */
    public void setTrashedFlg(String trashedFlg) {
        this.trashedFlg = trashedFlg;
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
     * Returns the deletedAt.
     * 
     * @return the deletedAt
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /** 
     * Sets the deletedAt.
     * 
     * @param deletedAt the deletedAt
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /** 
     * Returns the deletedBy.
     * 
     * @return the deletedBy
     */
    public Long getDeletedBy() {
        return deletedBy;
    }

    /** 
     * Sets the deletedBy.
     * 
     * @param deletedBy the deletedBy
     */
    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
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