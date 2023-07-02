package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 採番
 */
@Entity(listener = MNumberingEntityListener.class)
@Table(name = "m_numbering")
public class MNumberingEntity extends DefaultEntity {

    /** 採番ID */
    @Id
    @Column(name = "numbering_id")
    String numberingId;

    /** 採番対象名 */
    @Column(name = "numbering_name")
    String numberingName;

    /** 次の番号 */
    @Column(name = "next_no")
    Long nextNo;

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
     * Returns the numberingId.
     * 
     * @return the numberingId
     */
    public String getNumberingId() {
        return numberingId;
    }

    /** 
     * Sets the numberingId.
     * 
     * @param numberingId the numberingId
     */
    public void setNumberingId(String numberingId) {
        this.numberingId = numberingId;
    }

    /** 
     * Returns the numberingName.
     * 
     * @return the numberingName
     */
    public String getNumberingName() {
        return numberingName;
    }

    /** 
     * Sets the numberingName.
     * 
     * @param numberingName the numberingName
     */
    public void setNumberingName(String numberingName) {
        this.numberingName = numberingName;
    }

    /** 
     * Returns the nextNo.
     * 
     * @return the nextNo
     */
    public Long getNextNo() {
        return nextNo;
    }

    /** 
     * Sets the nextNo.
     * 
     * @param nextNo the nextNo
     */
    public void setNextNo(Long nextNo) {
        this.nextNo = nextNo;
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