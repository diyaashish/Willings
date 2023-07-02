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
 * タスク
 */
@Entity(listener = TTaskEntityListener.class)
@Table(name = "t_task")
public class TTaskEntity extends DefaultEntity {

    /** タスクSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_seq")
    Long taskSeq;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 件名 */
    @Column(name = "title")
    String title;

    /** 内容 */
    @Column(name = "content")
    String content;

    /** 期限To */
    @Column(name = "limit_dt_to")
    LocalDateTime limitDtTo;

    /** 期限終日フラグ */
    @Column(name = "all_day_flg")
    String allDayFlg;

    /** タスクステータス */
    @Column(name = "task_status")
    String taskStatus;

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
     * Returns the taskSeq.
     * 
     * @return the taskSeq
     */
    public Long getTaskSeq() {
        return taskSeq;
    }

    /** 
     * Sets the taskSeq.
     * 
     * @param taskSeq the taskSeq
     */
    public void setTaskSeq(Long taskSeq) {
        this.taskSeq = taskSeq;
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
     * Returns the title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /** 
     * Sets the title.
     * 
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * Returns the limitDtTo.
     * 
     * @return the limitDtTo
     */
    public LocalDateTime getLimitDtTo() {
        return limitDtTo;
    }

    /** 
     * Sets the limitDtTo.
     * 
     * @param limitDtTo the limitDtTo
     */
    public void setLimitDtTo(LocalDateTime limitDtTo) {
        this.limitDtTo = limitDtTo;
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
     * Returns the taskStatus.
     * 
     * @return the taskStatus
     */
    public String getTaskStatus() {
        return taskStatus;
    }

    /** 
     * Sets the taskStatus.
     * 
     * @param taskStatus the taskStatus
     */
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
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