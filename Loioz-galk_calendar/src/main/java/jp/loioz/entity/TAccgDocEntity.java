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
 * 会計書類
 */
@Entity(listener = TAccgDocEntityListener.class)
@Table(name = "t_accg_doc")
public class TAccgDocEntity extends DefaultEntity {

    /** 会計書類SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 名簿ID */
    @Column(name = "person_id")
    Long personId;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** ドキュメントタイプ */
    @Column(name = "accg_doc_type")
    String accgDocType;

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
     * Returns the accgDocSeq.
     * 
     * @return the accgDocSeq
     */
    public Long getAccgDocSeq() {
        return accgDocSeq;
    }

    /** 
     * Sets the accgDocSeq.
     * 
     * @param accgDocSeq the accgDocSeq
     */
    public void setAccgDocSeq(Long accgDocSeq) {
        this.accgDocSeq = accgDocSeq;
    }

    /** 
     * Returns the personId.
     * 
     * @return the personId
     */
    public Long getPersonId() {
        return personId;
    }

    /** 
     * Sets the personId.
     * 
     * @param personId the personId
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
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
     * Returns the accgDocType.
     * 
     * @return the accgDocType
     */
    public String getAccgDocType() {
        return accgDocType;
    }

    /** 
     * Sets the accgDocType.
     * 
     * @param accgDocType the accgDocType
     */
    public void setAccgDocType(String accgDocType) {
        this.accgDocType = accgDocType;
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