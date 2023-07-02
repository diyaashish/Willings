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
 * 関与者
 */
@Entity(listener = TKanyoshaEntityListener.class)
@Table(name = "t_kanyosha")
public class TKanyoshaEntity extends DefaultEntity {

    /** 関与者SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kanyosha_seq")
    Long kanyoshaSeq;

    /** 名簿ID */
    @Column(name = "person_id")
    Long personId;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 表示順 */
    @Column(name = "disp_order")
    Long dispOrder;

    /** 関係 */
    @Column(name = "kankei")
    String kankei;

    /** 備考 */
    @Column(name = "remarks")
    String remarks;

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
     * Returns the dispOrder.
     * 
     * @return the dispOrder
     */
    public Long getDispOrder() {
        return dispOrder;
    }

    /** 
     * Sets the dispOrder.
     * 
     * @param dispOrder the dispOrder
     */
    public void setDispOrder(Long dispOrder) {
        this.dispOrder = dispOrder;
    }

    /** 
     * Returns the kankei.
     * 
     * @return the kankei
     */
    public String getKankei() {
        return kankei;
    }

    /** 
     * Sets the kankei.
     * 
     * @param kankei the kankei
     */
    public void setKankei(String kankei) {
        this.kankei = kankei;
    }

    /** 
     * Returns the remarks.
     * 
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /** 
     * Sets the remarks.
     * 
     * @param remarks the remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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