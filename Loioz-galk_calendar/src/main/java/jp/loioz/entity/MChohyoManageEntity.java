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
 * 帳票管理
 */
@Entity(listener = MChohyoManageEntityListener.class)
@Table(schema ="service_mgt", name = "m_chohyo_manage")
public class MChohyoManageEntity extends DefaultEntity {

    /** 帳票SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chohyo_seq")
    Long chohyoSeq;

    /** 帳票タイプ */
    @Column(name = "chohyo_type")
    String chohyoType;

    /** オブジェクトキー */
    @Column(name = "object_key")
    String objectKey;

    /** ファイル名 */
    @Column(name = "file_name")
    String fileName;

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
     * Returns the chohyoSeq.
     * 
     * @return the chohyoSeq
     */
    public Long getChohyoSeq() {
        return chohyoSeq;
    }

    /** 
     * Sets the chohyoSeq.
     * 
     * @param chohyoSeq the chohyoSeq
     */
    public void setChohyoSeq(Long chohyoSeq) {
        this.chohyoSeq = chohyoSeq;
    }

    /** 
     * Returns the chohyoType.
     * 
     * @return the chohyoType
     */
    public String getChohyoType() {
        return chohyoType;
    }

    /** 
     * Sets the chohyoType.
     * 
     * @param chohyoType the chohyoType
     */
    public void setChohyoType(String chohyoType) {
        this.chohyoType = chohyoType;
    }

    /** 
     * Returns the objectKey.
     * 
     * @return the objectKey
     */
    public String getObjectKey() {
        return objectKey;
    }

    /** 
     * Sets the objectKey.
     * 
     * @param objectKey the objectKey
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
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