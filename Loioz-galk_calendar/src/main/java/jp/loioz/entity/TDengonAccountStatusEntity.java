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
@Entity(listener = TDengonAccountStatusEntityListener.class)
@Table(name = "t_dengon_account_status")
public class TDengonAccountStatusEntity extends DefaultEntity {

    /** アカウントSEQ */
    @Id
    @Column(name = "account_seq")
    Long accountSeq;

    /** 伝言SEQ */
    @Id
    @Column(name = "dengon_seq")
    Long dengonSeq;

    /** 伝言ステータスID */
    @Column(name = "dengon_status_id")
    String dengonStatusId;

    /** 重要 */
    @Column(name = "important_flg")
    String importantFlg;

    /** 既読 */
    @Column(name = "open_flg")
    String openFlg;

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
     * Returns the dengonStatusId.
     * 
     * @return the dengonStatusId
     */
    public String getDengonStatusId() {
        return dengonStatusId;
    }

    /** 
     * Sets the dengonStatusId.
     * 
     * @param dengonStatusId the dengonStatusId
     */
    public void setDengonStatusId(String dengonStatusId) {
        this.dengonStatusId = dengonStatusId;
    }

    /** 
     * Returns the importantFlg.
     * 
     * @return the importantFlg
     */
    public String getImportantFlg() {
        return importantFlg;
    }

    /** 
     * Sets the importantFlg.
     * 
     * @param importantFlg the importantFlg
     */
    public void setImportantFlg(String importantFlg) {
        this.importantFlg = importantFlg;
    }

    /** 
     * Returns the openFlg.
     * 
     * @return the openFlg
     */
    public String getOpenFlg() {
        return openFlg;
    }

    /** 
     * Sets the openFlg.
     * 
     * @param openFlg the openFlg
     */
    public void setOpenFlg(String openFlg) {
        this.openFlg = openFlg;
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