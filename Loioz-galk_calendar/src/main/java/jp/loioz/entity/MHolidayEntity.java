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
@Entity(listener = MHolidayEntityListener.class)
@Table(name = "m_holiday")
public class MHolidayEntity extends DefaultEntity {

    /** 祝日SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holiday_seq")
    Long holidaySeq;

    /** 祝日名称 */
    @Column(name = "holiday_name")
    String holidayName;

    /** 祝日日付 */
    @Column(name = "holiday_date")
    LocalDate holidayDate;

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
     * Returns the holidaySeq.
     * 
     * @return the holidaySeq
     */
    public Long getHolidaySeq() {
        return holidaySeq;
    }

    /** 
     * Sets the holidaySeq.
     * 
     * @param holidaySeq the holidaySeq
     */
    public void setHolidaySeq(Long holidaySeq) {
        this.holidaySeq = holidaySeq;
    }

    /** 
     * Returns the holidayName.
     * 
     * @return the holidayName
     */
    public String getHolidayName() {
        return holidayName;
    }

    /** 
     * Sets the holidayName.
     * 
     * @param holidayName the holidayName
     */
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    /** 
     * Returns the holidayDate.
     * 
     * @return the holidayDate
     */
    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    /** 
     * Sets the holidayDate.
     * 
     * @param holidayDate the holidayDate
     */
    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
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
