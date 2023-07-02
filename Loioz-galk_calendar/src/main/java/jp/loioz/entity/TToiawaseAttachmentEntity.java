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
 * 問い合わせ-添付
 */
@Entity(listener = TToiawaseAttachmentEntityListener.class)
@Table(name = "t_toiawase_attachment")
public class TToiawaseAttachmentEntity extends DefaultEntity {

    /** 添付SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toiawase_attachment_seq")
    Long toiawaseAttachmentSeq;

    /** 問い合わせSEQ */
    @Column(name = "toiawase_seq")
    Long toiawaseSeq;

    /** 問い合わせ詳細SEQ */
    @Column(name = "toiawase_detail_seq")
    Long toiawaseDetailSeq;

    /** ファイル名 */
    @Column(name = "file_name")
    String fileName;

    /** ファイル拡張子 */
    @Column(name = "file_extension")
    String fileExtension;

    /** S3オブジェクトキー */
    @Column(name = "s3_object_key")
    String s3ObjectKey;

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
     * Returns the toiawaseAttachmentSeq.
     * 
     * @return the toiawaseAttachmentSeq
     */
    public Long getToiawaseAttachmentSeq() {
        return toiawaseAttachmentSeq;
    }

    /** 
     * Sets the toiawaseAttachmentSeq.
     * 
     * @param toiawaseAttachmentSeq the toiawaseAttachmentSeq
     */
    public void setToiawaseAttachmentSeq(Long toiawaseAttachmentSeq) {
        this.toiawaseAttachmentSeq = toiawaseAttachmentSeq;
    }

    /** 
     * Returns the toiawaseSeq.
     * 
     * @return the toiawaseSeq
     */
    public Long getToiawaseSeq() {
        return toiawaseSeq;
    }

    /** 
     * Sets the toiawaseSeq.
     * 
     * @param toiawaseSeq the toiawaseSeq
     */
    public void setToiawaseSeq(Long toiawaseSeq) {
        this.toiawaseSeq = toiawaseSeq;
    }

    /** 
     * Returns the toiawaseDetailSeq.
     * 
     * @return the toiawaseDetailSeq
     */
    public Long getToiawaseDetailSeq() {
        return toiawaseDetailSeq;
    }

    /** 
     * Sets the toiawaseDetailSeq.
     * 
     * @param toiawaseDetailSeq the toiawaseDetailSeq
     */
    public void setToiawaseDetailSeq(Long toiawaseDetailSeq) {
        this.toiawaseDetailSeq = toiawaseDetailSeq;
    }

    /** 
     * Returns the fileName.
     * 
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /** 
     * Sets the fileName.
     * 
     * @param fileName the fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** 
     * Returns the fileExtension.
     * 
     * @return the fileExtension
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /** 
     * Sets the fileExtension.
     * 
     * @param fileExtension the fileExtension
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
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