package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 案件-関与者関係者
 */
@Entity(listener = TAnkenRelatedKanyoshaEntityListener.class)
@Table(name = "t_anken_related_kanyosha")
public class TAnkenRelatedKanyoshaEntity extends DefaultEntity {

	/** 案件ID */
    @Id
    @Column(name = "anken_id")
    Long ankenId;

    /** 関与者SEQ */
    @Id
    @Column(name = "kanyosha_seq")
    Long kanyoshaSeq;

    /** 関連関与者SEQ */
    @Column(name = "related_kanyosha_seq")
    Long relatedKanyoshaSeq;

    /** 関与者種別 */
    @Column(name = "kanyosha_type")
    String kanyoshaType;

    /** 代理フラグ */
    @Column(name = "dairi_flg")
    String dairiFlg;

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
     * Returns the kanyoshaSeq.
     * 
     * @return the kanyoshaSeq
     */
    public Long getKanyoshaSeq() {
        return kanyoshaSeq;
    }

    /** 
     * Sets the kanyoshaSeq.
     * 
     * @param kanyoshaSeq the kanyoshaSeq
     */
    public void setKanyoshaSeq(Long kanyoshaSeq) {
        this.kanyoshaSeq = kanyoshaSeq;
    }

    /** 
     * Returns the relatedKanyoshaSeq.
     * 
     * @return the relatedKanyoshaSeq
     */
    public Long getRelatedKanyoshaSeq() {
        return relatedKanyoshaSeq;
    }

    /** 
     * Sets the relatedKanyoshaSeq.
     * 
     * @param relatedKanyoshaSeq the relatedKanyoshaSeq
     */
    public void setRelatedKanyoshaSeq(Long relatedKanyoshaSeq) {
        this.relatedKanyoshaSeq = relatedKanyoshaSeq;
    }

    /** 
     * Returns the kanyoshaType.
     * 
     * @return the kanyoshaType
     */
    public String getKanyoshaType() {
        return kanyoshaType;
    }

    /** 
     * Sets the kanyoshaType.
     * 
     * @param kanyoshaType the kanyoshaType
     */
    public void setKanyoshaType(String kanyoshaType) {
        this.kanyoshaType = kanyoshaType;
    }

    /** 
     * Returns the dairiFlg.
     * 
     * @return the dairiFlg
     */
    public String getDairiFlg() {
        return dairiFlg;
    }

    /** 
     * Sets the dairiFlg.
     * 
     * @param dairiFlg the dairiFlg
     */
    public void setDairiFlg(String dairiFlg) {
        this.dairiFlg = dairiFlg;
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