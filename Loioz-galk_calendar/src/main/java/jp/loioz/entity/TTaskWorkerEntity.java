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
 * タスク-作業者
 */
@Entity(listener = TTaskWorkerEntityListener.class)
@Table(name = "t_task_worker")
public class TTaskWorkerEntity extends DefaultEntity {

    /** タスク-作業者SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_worker_seq")
    Long taskWorkerSeq;

    /** タスクSEQ */
    @Column(name = "task_seq")
    Long taskSeq;

    /** 作業者(アカウントSEQ) */
    @Column(name = "worker_account_seq")
    Long workerAccountSeq;

    /** 作成者フラグ */
    @Column(name = "creater_flg")
    String createrFlg;

    /** 委任フラグ */
    @Column(name = "entrust_flg")
    String entrustFlg;

    /** 新着タスク確認フラグ */
    @Column(name = "new_task_kakunin_flg")
    String newTaskKakuninFlg;

    /** 新着履歴確認フラグ */
    @Column(name = "new_history_kakunin_flg")
    String newHistoryKakuninFlg;

    /** 今日のタスク日付 */
    @Column(name = "today_task_date")
    LocalDate todayTaskDate;

    /** 今日のタスク表示順 */
    @Column(name = "today_task_disp_order")
    Integer todayTaskDispOrder;

    /** 表示順 */
    @Column(name = "disp_order")
    Integer dispOrder;

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
     * Returns the taskWorkerSeq.
     * 
     * @return the taskWorkerSeq
     */
    public Long getTaskWorkerSeq() {
        return taskWorkerSeq;
    }

    /** 
     * Sets the taskWorkerSeq.
     * 
     * @param taskWorkerSeq the taskWorkerSeq
     */
    public void setTaskWorkerSeq(Long taskWorkerSeq) {
        this.taskWorkerSeq = taskWorkerSeq;
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
     * Returns the workerAccountSeq.
     * 
     * @return the workerAccountSeq
     */
    public Long getWorkerAccountSeq() {
        return workerAccountSeq;
    }

    /** 
     * Sets the workerAccountSeq.
     * 
     * @param workerAccountSeq the workerAccountSeq
     */
    public void setWorkerAccountSeq(Long workerAccountSeq) {
        this.workerAccountSeq = workerAccountSeq;
    }

    /** 
     * Returns the createrFlg.
     * 
     * @return the createrFlg
     */
    public String getCreaterFlg() {
        return createrFlg;
    }

    /** 
     * Sets the createrFlg.
     * 
     * @param createrFlg the createrFlg
     */
    public void setCreaterFlg(String createrFlg) {
        this.createrFlg = createrFlg;
    }

    /** 
     * Returns the entrustFlg.
     * 
     * @return the entrustFlg
     */
    public String getEntrustFlg() {
        return entrustFlg;
    }

    /** 
     * Sets the entrustFlg.
     * 
     * @param entrustFlg the entrustFlg
     */
    public void setEntrustFlg(String entrustFlg) {
        this.entrustFlg = entrustFlg;
    }

    /** 
     * Returns the newTaskKakuninFlg.
     * 
     * @return the newTaskKakuninFlg
     */
    public String getNewTaskKakuninFlg() {
        return newTaskKakuninFlg;
    }

    /** 
     * Sets the newTaskKakuninFlg.
     * 
     * @param newTaskKakuninFlg the newTaskKakuninFlg
     */
    public void setNewTaskKakuninFlg(String newTaskKakuninFlg) {
        this.newTaskKakuninFlg = newTaskKakuninFlg;
    }

    /** 
     * Returns the newHistoryKakuninFlg.
     * 
     * @return the newHistoryKakuninFlg
     */
    public String getNewHistoryKakuninFlg() {
        return newHistoryKakuninFlg;
    }

    /** 
     * Sets the newHistoryKakuninFlg.
     * 
     * @param newHistoryKakuninFlg the newHistoryKakuninFlg
     */
    public void setNewHistoryKakuninFlg(String newHistoryKakuninFlg) {
        this.newHistoryKakuninFlg = newHistoryKakuninFlg;
    }

    /** 
     * Returns the todayTaskDate.
     * 
     * @return the todayTaskDate
     */
    public LocalDate getTodayTaskDate() {
    	return todayTaskDate;
    }

    /** 
     * Sets the todayTaskDate.
     * 
     * @param dispOrder the todayTaskDate
     */
    public void setTodayTaskDate(LocalDate todayTaskDate) {
    	this.todayTaskDate = todayTaskDate;
    }

    /** 
     * Returns the todayTaskDispOrder.
     * 
     * @return the todayTaskDispOrder
     */
    public Integer getTodayTaskDispOrder() {
    	return todayTaskDispOrder;
    }

    /** 
     * Sets the todayTaskDispOrder.
     * 
     * @param dispOrder the todayTaskDispOrder
     */
    public void setTodayTaskDispOrder(Integer todayTaskDispOrder) {
    	this.todayTaskDispOrder = todayTaskDispOrder;
    }

    /** 
     * Returns the dispOrder.
     * 
     * @return the dispOrder
     */
    public Integer getDispOrder() {
        return dispOrder;
    }

    /** 
     * Sets the dispOrder.
     * 
     * @param dispOrder the dispOrder
     */
    public void setDispOrder(Integer dispOrder) {
        this.dispOrder = dispOrder;
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