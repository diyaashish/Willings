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
 * タスク-履歴
 */
@Entity(listener = TTaskHistoryEntityListener.class)
@Table(name = "t_task_history")
public class TTaskHistoryEntity extends DefaultEntity {

    /** タスク履歴SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_history_seq")
    Long taskHistorySeq;

    /** タスクSEQ */
    @Column(name = "task_seq")
    Long taskSeq;

    /** タスク履歴種別 */
    @Column(name = "task_history_type")
    String taskHistoryType;

    /** 件名更新フラグ */
    @Column(name = "title_update_flg")
    String titleUpdateFlg;

    /** 本文更新フラグ */
    @Column(name = "content_update_flg")
    String contentUpdateFlg;

    /** 作業者更新フラグ */
    @Column(name = "worker_update_flg")
    String workerUpdateFlg;

    /** 期日更新フラグ */
    @Column(name = "limit_dt_update_flg")
    String limitDtUpdateFlg;

    /** タスクステータス更新フラグ */
    @Column(name = "status_update_flg")
    String statusUpdateFlg;

    /** 更新後ステータス */
    @Column(name = "updated_status")
    String updatedStatus;

    /** 案件関連付け更新区分 */
    @Column(name = "anken_releted_update_kbn")
    String ankenReletedUpdateKbn;

    /** チェックリスト項目更新フラグ */
    @Column(name = "check_item_update_flg")
    String checkItemUpdateFlg;

    /** チェックリスト項目更新区分 */
    @Column(name = "check_item_update_kbn")
    String checkItemUpdateKbn;

    /** チェックリスト項目名 */
    @Column(name = "check_item_name")
    String checkItemName;

    /** 案件関連付け更新フラグ */
    @Column(name = "anken_releted_update_flg")
    String ankenReletedUpdateFlg;

    /**  */
    @Column(name = "comment")
    String comment;

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
     * Returns the taskHistorySeq.
     * 
     * @return the taskHistorySeq
     */
    public Long getTaskHistorySeq() {
        return taskHistorySeq;
    }

    /** 
     * Sets the taskHistorySeq.
     * 
     * @param taskHistorySeq the taskHistorySeq
     */
    public void setTaskHistorySeq(Long taskHistorySeq) {
        this.taskHistorySeq = taskHistorySeq;
    }

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
     * Returns the taskHistoryType.
     * 
     * @return the taskHistoryType
     */
    public String getTaskHistoryType() {
        return taskHistoryType;
    }

    /** 
     * Sets the taskHistoryType.
     * 
     * @param taskHistoryType the taskHistoryType
     */
    public void setTaskHistoryType(String taskHistoryType) {
        this.taskHistoryType = taskHistoryType;
    }

    /** 
     * Returns the titleUpdateFlg.
     * 
     * @return the titleUpdateFlg
     */
    public String getTitleUpdateFlg() {
        return titleUpdateFlg;
    }

    /** 
     * Sets the titleUpdateFlg.
     * 
     * @param titleUpdateFlg the titleUpdateFlg
     */
    public void setTitleUpdateFlg(String titleUpdateFlg) {
        this.titleUpdateFlg = titleUpdateFlg;
    }

    /** 
     * Returns the contentUpdateFlg.
     * 
     * @return the contentUpdateFlg
     */
    public String getContentUpdateFlg() {
        return contentUpdateFlg;
    }

    /** 
     * Sets the contentUpdateFlg.
     * 
     * @param contentUpdateFlg the contentUpdateFlg
     */
    public void setContentUpdateFlg(String contentUpdateFlg) {
        this.contentUpdateFlg = contentUpdateFlg;
    }

    /** 
     * Returns the workerUpdateFlg.
     * 
     * @return the workerUpdateFlg
     */
    public String getWorkerUpdateFlg() {
        return workerUpdateFlg;
    }

    /** 
     * Sets the workerUpdateFlg.
     * 
     * @param workerUpdateFlg the workerUpdateFlg
     */
    public void setWorkerUpdateFlg(String workerUpdateFlg) {
        this.workerUpdateFlg = workerUpdateFlg;
    }

    /** 
     * Returns the limitDtUpdateFlg.
     * 
     * @return the limitDtUpdateFlg
     */
    public String getLimitDtUpdateFlg() {
        return limitDtUpdateFlg;
    }

    /** 
     * Sets the limitDtUpdateFlg.
     * 
     * @param limitDtUpdateFlg the limitDtUpdateFlg
     */
    public void setLimitDtUpdateFlg(String limitDtUpdateFlg) {
        this.limitDtUpdateFlg = limitDtUpdateFlg;
    }

    /** 
     * Returns the statusUpdateFlg.
     * 
     * @return the statusUpdateFlg
     */
    public String getStatusUpdateFlg() {
        return statusUpdateFlg;
    }

    /** 
     * Sets the statusUpdateFlg.
     * 
     * @param statusUpdateFlg the statusUpdateFlg
     */
    public void setStatusUpdateFlg(String statusUpdateFlg) {
        this.statusUpdateFlg = statusUpdateFlg;
    }

    /** 
     * Returns the updatedStatus.
     * 
     * @return the updatedStatus
     */
    public String getUpdatedStatus() {
        return updatedStatus;
    }

    /** 
     * Sets the updatedStatus.
     * 
     * @param updatedStatus the updatedStatus
     */
    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }

    /** 
     * Returns the ankenReletedUpdateKbn.
     * 
     * @return the ankenReletedUpdateKbn
     */
    public String getAnkenReletedUpdateKbn() {
        return ankenReletedUpdateKbn;
    }

    /** 
     * Sets the ankenReletedUpdateKbn.
     * 
     * @param ankenReletedUpdateKbn the ankenReletedUpdateKbn
     */
    public void setAnkenReletedUpdateKbn(String ankenReletedUpdateKbn) {
        this.ankenReletedUpdateKbn = ankenReletedUpdateKbn;
    }

    /** 
     * Returns the checkItemUpdateFlg.
     * 
     * @return the checkItemUpdateFlg
     */
    public String getCheckItemUpdateFlg() {
        return checkItemUpdateFlg;
    }

    /** 
     * Sets the checkItemUpdateFlg.
     * 
     * @param checkItemUpdateFlg the checkItemUpdateFlg
     */
    public void setCheckItemUpdateFlg(String checkItemUpdateFlg) {
        this.checkItemUpdateFlg = checkItemUpdateFlg;
    }

    /** 
     * Returns the checkItemUpdateKbn.
     * 
     * @return the checkItemUpdateKbn
     */
    public String getCheckItemUpdateKbn() {
        return checkItemUpdateKbn;
    }

    /** 
     * Sets the checkItemUpdateKbn.
     * 
     * @param checkItemUpdateKbn the checkItemUpdateKbn
     */
    public void setCheckItemUpdateKbn(String checkItemUpdateKbn) {
        this.checkItemUpdateKbn = checkItemUpdateKbn;
    }

    /** 
     * Returns the checkItemName.
     * 
     * @return the checkItemName
     */
    public String getCheckItemName() {
        return checkItemName;
    }

    /** 
     * Sets the checkItemName.
     * 
     * @param checkItemName the checkItemName
     */
    public void setCheckItemName(String checkItemName) {
        this.checkItemName = checkItemName;
    }

    /** 
     * Returns the ankenReletedUpdateFlg.
     * 
     * @return the ankenReletedUpdateFlg
     */
    public String getAnkenReletedUpdateFlg() {
        return ankenReletedUpdateFlg;
    }

    /** 
     * Sets the ankenReletedUpdateFlg.
     * 
     * @param ankenReletedUpdateFlg the ankenReletedUpdateFlg
     */
    public void setAnkenReletedUpdateFlg(String ankenReletedUpdateFlg) {
        this.ankenReletedUpdateFlg = ankenReletedUpdateFlg;
    }

    /** 
     * Returns the comment.
     * 
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /** 
     * Sets the comment.
     * 
     * @param comment the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
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