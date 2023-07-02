package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 既入金項目_預り金テーブルマッピング
 */
@Entity(listener = TAccgDocRepayTDepositRecvMappingEntityListener.class)
@Table(name = "t_accg_doc_repay_t_deposit_recv_mapping")
public class TAccgDocRepayTDepositRecvMappingEntity extends DefaultEntity {

    /** 既入金項目SEQ */
    @Id
    @Column(name = "doc_repay_seq")
    Long docRepaySeq;

    /** 預り金SEQ */
    @Id
    @Column(name = "deposit_recv_seq")
    Long depositRecvSeq;

    /** 登録日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントID */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントID */
    @Column(name = "updated_by")
    Long updatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the docRepaySeq.
     * 
     * @return the docRepaySeq
     */
    public Long getDocRepaySeq() {
        return docRepaySeq;
    }

    /** 
     * Sets the docRepaySeq.
     * 
     * @param docRepaySeq the docRepaySeq
     */
    public void setDocRepaySeq(Long docRepaySeq) {
        this.docRepaySeq = docRepaySeq;
    }

    /** 
     * Returns the depositRecvSeq.
     * 
     * @return the depositRecvSeq
     */
    public Long getDepositRecvSeq() {
        return depositRecvSeq;
    }

    /** 
     * Sets the depositRecvSeq.
     * 
     * @param depositRecvSeq the depositRecvSeq
     */
    public void setDepositRecvSeq(Long depositRecvSeq) {
        this.depositRecvSeq = depositRecvSeq;
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