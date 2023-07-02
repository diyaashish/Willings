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
 * 会計書類ファイル-詳細
 */
@Entity(listener = TAccgDocFileDetailEntityListener.class)
@Table(name = "t_accg_doc_file_detail")
public class TAccgDocFileDetailEntity extends DefaultEntity {

    /** 会計書類ファイル詳細SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_doc_file_detail_seq")
    Long accgDocFileDetailSeq;

    /** 会計書類ファイルSEQ */
    @Column(name = "accg_doc_file_seq")
    Long accgDocFileSeq;

    /** ファイル枝番 */
    @Column(name = "file_branch_no")
    Long fileBranchNo;

    /** S3オブジェクトキー */
    @Column(name = "s3_object_key")
    String s3ObjectKey;

    /** 登録日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントID */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントID */
    @Column(name = "updated_by")
    Long updatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the accgDocFileDetailSeq.
     * 
     * @return the accgDocFileDetailSeq
     */
    public Long getAccgDocFileDetailSeq() {
        return accgDocFileDetailSeq;
    }

    /** 
     * Sets the accgDocFileDetailSeq.
     * 
     * @param accgDocFileDetailSeq the accgDocFileDetailSeq
     */
    public void setAccgDocFileDetailSeq(Long accgDocFileDetailSeq) {
        this.accgDocFileDetailSeq = accgDocFileDetailSeq;
    }

    /** 
     * Returns the accgDocFileSeq.
     * 
     * @return the accgDocFileSeq
     */
    public Long getAccgDocFileSeq() {
        return accgDocFileSeq;
    }

    /** 
     * Sets the accgDocFileSeq.
     * 
     * @param accgDocFileSeq the accgDocFileSeq
     */
    public void setAccgDocFileSeq(Long accgDocFileSeq) {
        this.accgDocFileSeq = accgDocFileSeq;
    }

    /** 
     * Returns the fileBranchNo.
     * 
     * @return the fileBranchNo
     */
    public Long getFileBranchNo() {
        return fileBranchNo;
    }

    /** 
     * Sets the fileBranchNo.
     * 
     * @param fileBranchNo the fileBranchNo
     */
    public void setFileBranchNo(Long fileBranchNo) {
        this.fileBranchNo = fileBranchNo;
    }

    /** 
     * Returns the s3ObjectKey.
     * 
     * @return the s3ObjectKey
     */
    public String getS3ObjectKey() {
        return s3ObjectKey;
    }

    /** 
     * Sets the s3ObjectKey.
     * 
     * @param s3ObjectKey the s3ObjectKey
     */
    public void setS3ObjectKey(String s3ObjectKey) {
        this.s3ObjectKey = s3ObjectKey;
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