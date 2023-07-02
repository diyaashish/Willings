package jp.loioz.entity;

import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 
 */
@Entity(listener = TDengonFolderInEntityListener.class)
@Table(name = "t_dengon_folder_in")
public class TDengonFolderInEntity extends DefaultEntity {

    /** 伝言フォルダSEQ */
    @Id
    @Column(name = "dengon_folder_seq")
    Long dengonFolderSeq;

    /** 伝言SEQ */
    @Id
    @Column(name = "dengon_seq")
    Long dengonSeq;

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
     * Returns the dengonSeq.
     * 
     * @return the dengonSeq
     */
    public Long getDengonSeq() {
        return dengonSeq;
    }

    /** 
     * Sets the dengonSeq.
     * 
     * @param dengonSeq the dengonSeq
     */
    public void setDengonSeq(Long dengonSeq) {
        this.dengonSeq = dengonSeq;
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