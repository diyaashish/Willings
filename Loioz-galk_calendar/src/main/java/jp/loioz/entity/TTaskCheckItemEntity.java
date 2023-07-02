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
 * タスク-チェックリスト項目
 */
@Entity(listener = TTaskCheckItemEntityListener.class)
@Table(name = "t_task_check_item")
public class TTaskCheckItemEntity extends DefaultEntity {

    /** タスク-チェックリストSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_check_item_seq")
    Long taskCheckItemSeq;

    /** タスクSEQ */
    @Column(name = "task_seq")
    Long taskSeq;

    /** チェックリスト項目名 */
    @Column(name = "item_name")
    String itemName;

    /** 完了フラグ */
    @Column(name = "complete_flg")
    String completeFlg;

    /** チェックリスト表示順 */
    @Column(name = "disp_order")
    Long dispOrder;

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
     * Returns the taskCheckItemSeq.
     * 
     * @return the taskCheckItemSeq
     */
    public Long getTaskCheckItemSeq() {
        return taskCheckItemSeq;
    }

    /** 
     * Sets the taskCheckItemSeq.
     * 
     * @param taskCheckItemSeq the taskCheckItemSeq
     */
    public void setTaskCheckItemSeq(Long taskCheckItemSeq) {
        this.taskCheckItemSeq = taskCheckItemSeq;
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
     * Returns the itemName.
     * 
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /** 
     * Sets the itemName.
     * 
     * @param itemName the itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /** 
     * Returns the completeFlg.
     * 
     * @return the completeFlg
     */
    public String getCompleteFlg() {
        return completeFlg;
    }

    /** 
     * Sets the completeFlg.
     * 
     * @param completeFlg the completeFlg
     */
    public void setCompleteFlg(String completeFlg) {
        this.completeFlg = completeFlg;
    }

    /** 
     * Returns the dispOrder.
     * 
     * @return the dispOrder
     */
    public Long getDispOrder() {
        return dispOrder;
    }

    /** 
     * Sets the dispOrder.
     * 
     * @param dispOrder the dispOrder
     */
    public void setDispOrder(Long dispOrder) {
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