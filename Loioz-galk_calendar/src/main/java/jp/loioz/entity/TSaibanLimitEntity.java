package jp.loioz.entity;

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
@Entity(listener = TSaibanLimitEntityListener.class)
@Table(name = "t_saiban_limit")
public class TSaibanLimitEntity extends DefaultEntity {

    /** 裁判期日SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saiban_limit_seq")
    Long saibanLimitSeq;

    /** 期日 */
    @Column(name = "limit_at")
    LocalDateTime limitAt;

    /** 場所 */
    @Column(name = "place")
    String place;

    /** 出廷(要/不要) */
    @Column(name = "shuttei_type")
    String shutteiType;

    /** 手続き内容 */
    @Column(name = "content")
    String content;

    /** 期日結果 */
    @Column(name = "result")
    String result;

    /** 回数 */
    @Column(name = "limit_date_count")
    Long limitDateCount;

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
     * Returns the saibanLimitSeq.
     *
     * @return the saibanLimitSeq
     */
    public Long getSaibanLimitSeq() {
        return saibanLimitSeq;
    }

    /**
     * Sets the saibanLimitSeq.
     *
     * @param saibanLimitSeq the saibanLimitSeq
     */
    public void setSaibanLimitSeq(Long saibanLimitSeq) {
        this.saibanLimitSeq = saibanLimitSeq;
    }

    /**
     * Returns the limitAt.
     *
     * @return the limitAt
     */
    public LocalDateTime getLimitAt() {
        return limitAt;
    }

    /**
     * Sets the limitAt.
     *
     * @param limitAt the limitAt
     */
    public void setLimitAt(LocalDateTime limitAt) {
        this.limitAt = limitAt;
    }

    /**
     * Returns the place.
     *
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * Sets the place.
     *
     * @param place the place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * Returns the shutteiType.
     *
     * @return the shutteiType
     */
    public String getShutteiType() {
        return shutteiType;
    }

    /**
     * Sets the shutteiType.
     *
     * @param shutteiType the shutteiType
     */
    public void setShutteiType(String shutteiType) {
        this.shutteiType = shutteiType;
    }

    /**
     * Returns the content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the result.
     *
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param result the result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Returns the limitDateCount.
     *
     * @return the limitDateCount
     */
    public Long getLimitDateCount() {
        return limitDateCount;
    }

    /**
     * Sets the limitDateCount.
     *
     * @param limitDateCount the limitDateCount
     */
    public void setLimitDateCount(Long limitDateCount) {
        this.limitDateCount = limitDateCount;
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