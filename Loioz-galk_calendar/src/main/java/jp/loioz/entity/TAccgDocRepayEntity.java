package jp.loioz.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * 既入金項目
 */
@Entity(listener = TAccgDocRepayEntityListener.class)
@Table(name = "t_accg_doc_repay")
public class TAccgDocRepayEntity extends DefaultEntity {

    /** 既入金項目SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_repay_seq")
    Long docRepaySeq;

    /** 会計書類SEQ */
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 取引日 */
    @Column(name = "repay_transaction_date")
    LocalDate repayTransactionDate;

    /** 項目名 */
    @Column(name = "repay_item_name")
    String repayItemName;

    /** 既入金金額 */
    @Column(name = "repay_amount")
    BigDecimal repayAmount;

    /** 摘要 */
    @Column(name = "sum_text")
    String sumText;

    /** 並び順 */
    @Column(name = "doc_repay_order")
    Long docRepayOrder;

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
     * Returns the accgDocSeq.
     * 
     * @return the accgDocSeq
     */
    public Long getAccgDocSeq() {
        return accgDocSeq;
    }

    /** 
     * Sets the accgDocSeq.
     * 
     * @param accgDocSeq the accgDocSeq
     */
    public void setAccgDocSeq(Long accgDocSeq) {
        this.accgDocSeq = accgDocSeq;
    }

    /** 
     * Returns the repayTransactionDate.
     * 
     * @return the repayTransactionDate
     */
    public LocalDate getRepayTransactionDate() {
        return repayTransactionDate;
    }

    /** 
     * Sets the repayTransactionDate.
     * 
     * @param repayTransactionDate the repayTransactionDate
     */
    public void setRepayTransactionDate(LocalDate repayTransactionDate) {
        this.repayTransactionDate = repayTransactionDate;
    }

    /** 
     * Returns the repayItemName.
     * 
     * @return the repayItemName
     */
    public String getRepayItemName() {
        return repayItemName;
    }

    /** 
     * Sets the repayItemName.
     * 
     * @param repayItemName the repayItemName
     */
    public void setRepayItemName(String repayItemName) {
        this.repayItemName = repayItemName;
    }

    /** 
     * Returns the repayAmount.
     * 
     * @return the repayAmount
     */
    public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    /** 
     * Sets the repayAmount.
     * 
     * @param repayAmount the repayAmount
     */
    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    /** 
     * Returns the sumText.
     * 
     * @return the sumText
     */
    public String getSumText() {
        return sumText;
    }

    /** 
     * Sets the sumText.
     * 
     * @param sumText the sumText
     */
    public void setSumText(String sumText) {
        this.sumText = sumText;
    }

    /** 
     * Returns the docRepayOrder.
     * 
     * @return the docRepayOrder
     */
    public Long getDocRepayOrder() {
        return docRepayOrder;
    }

    /** 
     * Sets the docRepayOrder.
     * 
     * @param docRepayOrder the docRepayOrder
     */
    public void setDocRepayOrder(Long docRepayOrder) {
        this.docRepayOrder = docRepayOrder;
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