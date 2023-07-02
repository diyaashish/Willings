package jp.loioz.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Entity(listener = TScheduleEntityListener.class)
@Table(name = "t_schedule")
public class TScheduleEntity extends DefaultEntity {

    /** 予定SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_seq")
    Long scheduleSeq;

    /** 件名 */
    @Column(name = "subject")
    String subject;

    /** 日付From */
    @Column(name = "date_from")
    LocalDate dateFrom;

    /** 日付To */
    @Column(name = "date_to")
    LocalDate dateTo;

    /** 時間Form */
    @Column(name = "time_from")
    LocalTime timeFrom;

    /** 時間To */
    @Column(name = "time_to")
    LocalTime timeTo;

    /** 終日フラグ */
    @Column(name = "all_day_flg")
    String allDayFlg;

    /** 繰返しフラグ */
    @Column(name = "repeat_flg")
    String repeatFlg;

    /** 繰返しタイプ */
    @Column(name = "repeat_type")
    String repeatType;

    /** 繰返し曜日 */
    @Column(name = "repeat_yobi")
    String repeatYobi;

    /** 繰返し日 */
    @Column(name = "repeat_day_of_month")
    Long repeatDayOfMonth;

    /** 会議室ID */
    @Column(name = "room_id")
    Long roomId;

    /** その他入力場所 */
    @Column(name = "place")
    String place;

    /** メモ */
    @Column(name = "memo")
    String memo;

    /** 公開範囲 */
    @Column(name = "open_range")
    String openRange;

    /** 編集許可 */
    @Column(name = "edit_range")
    String editRange;

    /** 顧客ID */
    @Column(name = "customer_id")
    Long customerId;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 裁判SEQ */
    @Column(name = "saiban_seq")
    Long saibanSeq;

    /** 裁判期日SEQ */
    @Column(name = "saiban_limit_seq")
    Long saibanLimitSeq;

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

    /** 削除日時 */
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    /** 削除アカウントSEQ */
    @Column(name = "deleted_by")
    Long deletedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /**
     * Returns the scheduleSeq.
     *
     * @return the scheduleSeq
     */
    public Long getScheduleSeq() {
        return scheduleSeq;
    }

    /**
     * Sets the scheduleSeq.
     *
     * @param scheduleSeq the scheduleSeq
     */
    public void setScheduleSeq(Long scheduleSeq) {
        this.scheduleSeq = scheduleSeq;
    }

    /**
     * Returns the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the dateFrom.
     *
     * @return the dateFrom
     */
    public LocalDate getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets the dateFrom.
     *
     * @param dateFrom the dateFrom
     */
    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * Returns the dateTo.
     *
     * @return the dateTo
     */
    public LocalDate getDateTo() {
        return dateTo;
    }

    /**
     * Sets the dateTo.
     *
     * @param dateTo the dateTo
     */
    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * Returns the timeFrom.
     *
     * @return the timeFrom
     */
    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    /**
     * Sets the timeFrom.
     *
     * @param timeFrom the timeFrom
     */
    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    /**
     * Returns the timeTo.
     *
     * @return the timeTo
     */
    public LocalTime getTimeTo() {
        return timeTo;
    }

    /**
     * Sets the timeTo.
     *
     * @param timeTo the timeTo
     */
    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    /**
     * Returns the allDayFlg.
     *
     * @return the allDayFlg
     */
    public String getAllDayFlg() {
        return allDayFlg;
    }

    /**
     * Sets the allDayFlg.
     *
     * @param allDayFlg the allDayFlg
     */
    public void setAllDayFlg(String allDayFlg) {
        this.allDayFlg = allDayFlg;
    }

    /**
     * Returns the repeatFlg.
     *
     * @return the repeatFlg
     */
    public String getRepeatFlg() {
        return repeatFlg;
    }

    /**
     * Sets the repeatFlg.
     *
     * @param repeatFlg the repeatFlg
     */
    public void setRepeatFlg(String repeatFlg) {
        this.repeatFlg = repeatFlg;
    }

    /**
     * Returns the repeatType.
     *
     * @return the repeatType
     */
    public String getRepeatType() {
        return repeatType;
    }

    /**
     * Sets the repeatType.
     *
     * @param repeatType the repeatType
     */
    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    /**
     * Returns the repeatYobi.
     *
     * @return the repeatYobi
     */
    public String getRepeatYobi() {
        return repeatYobi;
    }

    /**
     * Sets the repeatYobi.
     *
     * @param repeatYobi the repeatYobi
     */
    public void setRepeatYobi(String repeatYobi) {
        this.repeatYobi = repeatYobi;
    }

    /**
     * Returns the repeatDayOfMonth.
     *
     * @return the repeatDayOfMonth
     */
    public Long getRepeatDayOfMonth() {
        return repeatDayOfMonth;
    }

    /**
     * Sets the repeatDayOfMonth.
     *
     * @param repeatDayOfMonth the repeatDayOfMonth
     */
    public void setRepeatDayOfMonth(Long repeatDayOfMonth) {
        this.repeatDayOfMonth = repeatDayOfMonth;
    }

    /**
     * Returns the roomId.
     *
     * @return the roomId
     */
    public Long getRoomId() {
        return roomId;
    }

    /**
     * Sets the roomId.
     *
     * @param roomId the roomId
     */
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
     * Returns the memo.
     *
     * @return the memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * Sets the memo.
     *
     * @param memo the memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * Returns the openRange.
     *
     * @return the openRange
     */
    public String getOpenRange() {
        return openRange;
    }

    /**
     * Sets the openRange.
     *
     * @param openRange the openRange
     */
    public void setOpenRange(String openRange) {
        this.openRange = openRange;
    }

    /**
     * Returns the editRange.
     *
     * @return the editRange
     */
    public String getEditRange() {
        return editRange;
    }

    /**
     * Sets the editRange.
     *
     * @param editRange the editRange
     */
    public void setEditRange(String editRange) {
        this.editRange = editRange;
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
     * Returns the deletedAt.
     *
     * @return the deletedAt
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * Sets the deletedAt.
     *
     * @param deletedAt the deletedAt
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * Returns the deletedBy.
     *
     * @return the deletedBy
     */
    public Long getDeletedBy() {
        return deletedBy;
    }

    /**
     * Sets the deletedBy.
     *
     * @param deletedBy the deletedBy
     */
    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
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