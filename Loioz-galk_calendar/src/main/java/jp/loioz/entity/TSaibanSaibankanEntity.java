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
 * 裁判-裁判官
 */
@Entity(listener = TSaibanSaibankanEntityListener.class)
@Table(name = "t_saiban_saibankan")
public class TSaibanSaibankanEntity extends DefaultEntity {

    /** 裁判官SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saibankan_seq")
    Long saibankanSeq;

    /** 裁判SEQ */
    @Column(name = "saiban_seq")
    Long saibanSeq;

    /** 裁判官名 */
    @Column(name = "saibankan_name")
    String saibankanName;

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
     * Returns the saibankanSeq.
     * 
     * @return the saibankanSeq
     */
    public Long getSaibankanSeq() {
        return saibankanSeq;
    }

    /** 
     * Sets the saibankanSeq.
     * 
     * @param saibankanSeq the saibankanSeq
     */
    public void setSaibankanSeq(Long saibankanSeq) {
        this.saibankanSeq = saibankanSeq;
    }

    /** 
     * Returns the saibanSeq.
     * 
     * @return the saibanSeq
     */
    public Long getSaibanSeq() {
        return saibanSeq;
    }

    /** 
     * Sets the saibanSeq.
     * 
     * @param saibanSeq the saibanSeq
     */
    public void setSaibanSeq(Long saibanSeq) {
        this.saibanSeq = saibanSeq;
    }

    /** 
     * Returns the saibankanName.
     * 
     * @return the saibankanName
     */
    public String getSaibankanName() {
        return saibankanName;
    }

    /** 
     * Sets the saibankanName.
     * 
     * @param saibankanName the saibankanName
     */
    public void setSaibankanName(String saibankanName) {
        this.saibankanName = saibankanName;
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