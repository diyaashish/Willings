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
@Entity(listener = TBatchExecuteHistoryEntityListener.class)
@Table(schema = "service_mgt", name = "t_batch_execute_history")
public class TBatchExecuteHistoryEntity extends DefaultEntity {

    /** バッチ実行履歴連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "t_batch_execute_history_seq")
    Long tBatchExecuteHistorySeq;

    /** バッチID */
    @Column(name = "batch_id")
    String batchId;

    /** 実行ステータス */
    @Column(name = "execute_status")
    String executeStatus;

    /** メッセージ */
    @Column(name = "message")
    String message;

    /** バッチ実行開始日時 */
    @Column(name = "batch_start_dt")
    LocalDateTime batchStartDt;

    /** バッチ実行終了日時 */
    @Column(name = "batch_end_dt")
    LocalDateTime batchEndDt;

    /** 作成日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 作成者ID */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新者ID */
    @Column(name = "updated_by")
    Long updatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the tBatchExecuteHistorySeq.
     * 
     * @return the tBatchExecuteHistorySeq
     */
    public Long getTBatchExecuteHistorySeq() {
        return tBatchExecuteHistorySeq;
    }

    /** 
     * Sets the tBatchExecuteHistorySeq.
     * 
     * @param tBatchExecuteHistorySeq the tBatchExecuteHistorySeq
     */
    public void setTBatchExecuteHistorySeq(Long tBatchExecuteHistorySeq) {
        this.tBatchExecuteHistorySeq = tBatchExecuteHistorySeq;
    }

    /** 
     * Returns the batchId.
     * 
     * @return the batchId
     */
    public String getBatchId() {
        return batchId;
    }

    /** 
     * Sets the batchId.
     * 
     * @param batchId the batchId
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    /** 
     * Returns the executeStatus.
     * 
     * @return the executeStatus
     */
    public String getExecuteStatus() {
        return executeStatus;
    }

    /** 
     * Sets the executeStatus.
     * 
     * @param executeStatus the executeStatus
     */
    public void setExecuteStatus(String executeStatus) {
        this.executeStatus = executeStatus;
    }

    /** 
     * Returns the message.
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /** 
     * Sets the message.
     * 
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /** 
     * Returns the batchStartDt.
     * 
     * @return the batchStartDt
     */
    public LocalDateTime getBatchStartDt() {
        return batchStartDt;
    }

    /** 
     * Sets the batchStartDt.
     * 
     * @param batchStartDt the batchStartDt
     */
    public void setBatchStartDt(LocalDateTime batchStartDt) {
        this.batchStartDt = batchStartDt;
    }

    /** 
     * Returns the batchEndDt.
     * 
     * @return the batchEndDt
     */
    public LocalDateTime getBatchEndDt() {
        return batchEndDt;
    }

    /** 
     * Sets the batchEndDt.
     * 
     * @param batchEndDt the batchEndDt
     */
    public void setBatchEndDt(LocalDateTime batchEndDt) {
        this.batchEndDt = batchEndDt;
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