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
 * 会計書類-対応
 */
@Entity(listener = TAccgDocActEntityListener.class)
@Table(name = "t_accg_doc_act")
public class TAccgDocActEntity extends DefaultEntity {

    /** 会計書類対応SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_doc_act_seq")
    Long accgDocActSeq;

    /** 会計書類SEQ */
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 対応種別 */
    @Column(name = "act_type")
    String actType;

    /** 対応アカウントID */
    @Column(name = "act_by")
    Long actBy;

    /** 対応日時 */
    @Column(name = "act_at")
    LocalDateTime actAt;

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
     * Returns the accgDocActSeq.
     * 
     * @return the accgDocActSeq
     */
    public Long getAccgDocActSeq() {
        return accgDocActSeq;
    }

    /** 
     * Sets the accgDocActSeq.
     * 
     * @param accgDocActSeq the accgDocActSeq
     */
    public void setAccgDocActSeq(Long accgDocActSeq) {
        this.accgDocActSeq = accgDocActSeq;
    }

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
     * Returns the actType.
     * 
     * @return the actType
     */
    public String getActType() {
        return actType;
    }

    /** 
     * Sets the actType.
     * 
     * @param actType the actType
     */
    public void setActType(String actType) {
        this.actType = actType;
    }

    /** 
     * Returns the actBy.
     * 
     * @return the actBy
     */
    public Long getActBy() {
        return actBy;
    }

    /** 
     * Sets the actBy.
     * 
     * @param actBy the actBy
     */
    public void setActBy(Long actBy) {
        this.actBy = actBy;
    }

    /** 
     * Returns the actAt.
     * 
     * @return the actAt
     */
    public LocalDateTime getActAt() {
        return actAt;
    }

    /** 
     * Sets the actAt.
     * 
     * @param actAt the actAt
     */
    public void setActAt(LocalDateTime actAt) {
        this.actAt = actAt;
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