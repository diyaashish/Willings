package jp.loioz.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 案件-顧客
 */
@Entity(listener = TAnkenCustomerEntityListener.class)
@Table(name = "t_anken_customer")
public class TAnkenCustomerEntity extends DefaultEntity {

    /** 案件ID */
    @Id
    @Column(name = "anken_id")
    Long ankenId;

    /** 顧客ID */
    @Id
    @Column(name = "customer_id")
    Long customerId;

    /** 初回面談日 */
    @Column(name = "shokai_mendan_date")
    LocalDate shokaiMendanDate;

    /** 初回面談予定SEQ */
    @Column(name = "shokai_mendan_schedule_seq")
    Long shokaiMendanScheduleSeq;

    /** 受任日 */
    @Column(name = "junin_date")
    LocalDate juninDate;

    /** 事件処理完了日 */
    @Column(name = "jiken_kanryo_date")
    LocalDate jikenKanryoDate;

    /** 精算完了日 */
    @Column(name = "kanryo_date")
    LocalDate kanryoDate;

    /** 完了フラグ */
    @Column(name = "kanryo_flg")
    String kanryoFlg;

    /** 案件ステータス */
    @Column(name = "anken_status")
    String ankenStatus;

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
     * Returns the shokaiMendanDate.
     *
     * @return the shokaiMendanDate
     */
    public LocalDate getShokaiMendanDate() {
        return shokaiMendanDate;
    }

    /**
     * Sets the shokaiMendanDate.
     *
     * @param shokaiMendanDate the shokaiMendanDate
     */
    public void setShokaiMendanDate(LocalDate shokaiMendanDate) {
        this.shokaiMendanDate = shokaiMendanDate;
    }

    /**
     * Returns the shokaiMendanScheduleSeq.
     *
     * @return the shokaiMendanScheduleSeq
     */
    public Long getShokaiMendanScheduleSeq() {
        return shokaiMendanScheduleSeq;
    }

    /**
     * Sets the shokaiMendanScheduleSeq.
     *
     * @param shokaiMendanScheduleSeq the shokaiMendanScheduleSeq
     */
    public void setShokaiMendanScheduleSeq(Long shokaiMendanScheduleSeq) {
        this.shokaiMendanScheduleSeq = shokaiMendanScheduleSeq;
    }

    /**
     * Returns the juninDate.
     *
     * @return the juninDate
     */
    public LocalDate getJuninDate() {
        return juninDate;
    }

    /**
     * Sets the juninDate.
     *
     * @param juninDate the juninDate
     */
    public void setJuninDate(LocalDate juninDate) {
        this.juninDate = juninDate;
    }

    /**
     * Returns the jikenKanryoDate.
     *
     * @return the jikenKanryoDate
     */
    public LocalDate getJikenKanryoDate() {
        return jikenKanryoDate;
    }

    /**
     * Sets the jikenKanryoDate.
     *
     * @param jikenKanryoDate the jikenKanryoDate
     */
    public void setJikenKanryoDate(LocalDate jikenKanryoDate) {
        this.jikenKanryoDate = jikenKanryoDate;
    }

    /**
     * Returns the kanryoDate.
     *
     * @return the kanryoDate
     */
    public LocalDate getKanryoDate() {
        return kanryoDate;
    }

    /**
     * Sets the kanryoDate.
     *
     * @param kanryoDate the kanryoDate
     */
    public void setKanryoDate(LocalDate kanryoDate) {
        this.kanryoDate = kanryoDate;
    }

    /**
     * Returns the kanryoFlg.
     *
     * @return the kanryoFlg
     */
    public String getKanryoFlg() {
        return kanryoFlg;
    }

    /**
     * Sets the kanryoFlg.
     *
     * @param kanryoFlg the kanryoFlg
     */
    public void setKanryoFlg(String kanryoFlg) {
        this.kanryoFlg = kanryoFlg;
    }

    /**
     * Returns the ankenStatus.
     *
     * @return the ankenStatus
     */
    public String getAnkenStatus() {
        return ankenStatus;
    }

    /**
     * Sets the ankenStatus.
     *
     * @param ankenStatus the ankenStatus
     */
    public void setAnkenStatus(String ankenStatus) {
        this.ankenStatus = ankenStatus;
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