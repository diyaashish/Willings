package jp.loioz.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 会計書類-最終採番番号
 */
@Entity(listener = TAccgDocLastUsedNumberEntityListener.class)
@Table(name = "t_accg_doc_last_used_number")
public class TAccgDocLastUsedNumberEntity extends DefaultEntity {

    /** ドキュメントタイプ */
    @Id
    @Column(name = "accg_doc_type")
    String accgDocType;

    /** 採番タイプ */
    @Id
    @Column(name = "numbering_type")
    String numberingType;

    /** 最終採番番号 */
    @Column(name = "numbering_last_no")
    Long numberingLastNo;

    /** 最終採番日 */
    @Column(name = "numbering_last_date")
    LocalDate numberingLastDate;

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
     * Returns the numberingType.
     * 
     * @return the numberingType
     */
    public String getNumberingType() {
        return numberingType;
    }

    /** 
     * Sets the numberingType.
     * 
     * @param numberingType the numberingType
     */
    public void setNumberingType(String numberingType) {
        this.numberingType = numberingType;
    }

    /** 
     * Returns the numberingLastNo.
     * 
     * @return the numberingLastNo
     */
    public Long getNumberingLastNo() {
        return numberingLastNo;
    }

    /** 
     * Sets the numberingLastNo.
     * 
     * @param numberingLastNo the numberingLastNo
     */
    public void setNumberingLastNo(Long numberingLastNo) {
        this.numberingLastNo = numberingLastNo;
    }

    /** 
     * Returns the numberingLastDate.
     * 
     * @return the numberingLastDate
     */
    public LocalDate getNumberingLastDate() {
        return numberingLastDate;
    }

    /** 
     * Sets the numberingLastDate.
     * 
     * @param numberingLastDate the numberingLastDate
     */
    public void setNumberingLastDate(LocalDate numberingLastDate) {
        this.numberingLastDate = numberingLastDate;
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