package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 *
 */
@Entity(listener = TSaibanCustomerEntityListener.class)
@Table(name = "t_saiban_customer")
public class TSaibanCustomerEntity extends DefaultEntity {

    /** 裁判SEQ */
    @Id
    @Column(name = "saiban_seq")
    Long saibanSeq;

    /** 顧客ID */
    @Id
    @Column(name = "customer_id")
    Long customerId;

    /** 裁判当事者表記 */
    @Column(name = "saiban_tojisha_hyoki")
    String saibanTojishaHyoki;

    /** 筆頭フラグ */
    @Column(name = "main_flg")
    String mainFlg;

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
     * Returns the customerId.
     *
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customerId.
     *
     * @param customerId the customerId
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the saibanTojishaHyoki.
     *
     * @return the saibanTojishaHyoki
     */
    public String getSaibanTojishaHyoki() {
        return saibanTojishaHyoki;
    }

    /**
     * Sets the saibanTojishaHyoki.
     *
     * @param saibanTojishaHyoki the saibanTojishaHyoki
     */
    public void setSaibanTojishaHyoki(String saibanTojishaHyoki) {
        this.saibanTojishaHyoki = saibanTojishaHyoki;
    }

    /**
     * Returns the mainFlg.
     *
     * @return the mainFlg
     */
    public String getMainFlg() {
        return mainFlg;
    }

    /**
     * Sets the mainFlg.
     *
     * @param mainFlg the mainFlg
     */
    public void setMainFlg(String mainFlg) {
        this.mainFlg = mainFlg;
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