package jp.loioz.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 *
 */
@Entity(listener = TAnkenEntityListener.class)
@Table(name = "t_anken")
public class TAnkenEntity extends DefaultEntity {

    /** 案件ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anken_id")
    Long ankenId;

    /** 案件名 */
    @Column(name = "anken_name")
    String ankenName;

    /** 分野ID */
    @Column(name = "bunya_id")
    Long bunyaId;

    /** 案件登録日 */
    @Column(name = "anken_created_date")
    LocalDate ankenCreatedDate;

    /** 区分 */
    @Column(name = "anken_type")
    String ankenType;

    /** 事案概要・方針 */
    @Column(name = "jian_summary")
    String jianSummary;

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
     * Returns the ankenName.
     *
     * @return the ankenName
     */
    public String getAnkenName() {
        return ankenName;
    }

    /**
     * Sets the ankenName.
     *
     * @param ankenName the ankenName
     */
    public void setAnkenName(String ankenName) {
        this.ankenName = ankenName;
    }

    /**
     * Returns the bunyaId.
     *
     * @return the bunyaId
     */
    public Long getBunyaId() {
        return bunyaId;
    }

    /**
     * Sets the bunyaId.
     *
     * @param bunyaId the bunyaId
     */
    public void setBunyaId(Long bunyaId) {
        this.bunyaId = bunyaId;
    }

    /**
     * Returns the ankenCreatedDate.
     *
     * @return the ankenCreatedDate
     */
    public LocalDate getAnkenCreatedDate() {
        return ankenCreatedDate;
    }

    /**
     * Sets the ankenCreatedDate.
     *
     * @param ankenCreatedDate the ankenCreatedDate
     */
    public void setAnkenCreatedDate(LocalDate ankenCreatedDate) {
        this.ankenCreatedDate = ankenCreatedDate;
    }

    /**
     * Returns the ankenType.
     *
     * @return the ankenType
     */
    public String getAnkenType() {
        return ankenType;
    }

    /**
     * Sets the ankenType.
     *
     * @param ankenType the ankenType
     */
    public void setAnkenType(String ankenType) {
        this.ankenType = ankenType;
    }

    /**
     * Returns the jianSummary.
     *
     * @return the jianSummary
     */
    public String getJianSummary() {
        return jianSummary;
    }

    /**
     * Sets the jianSummary.
     *
     * @param jianSummary the jianSummary
     */
    public void setJianSummary(String jianSummary) {
        this.jianSummary = jianSummary;
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