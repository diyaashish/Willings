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
 * 精算書
 */
@Entity(listener = TAccgStatementEntityListener.class)
@Table(name = "t_accg_statement")
public class TAccgStatementEntity extends DefaultEntity {

    /** 精算書SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statement_seq")
    Long statementSeq;

    /** 会計書類SEQ */
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 売上明細SEQ */
    @Column(name = "sales_detail_seq")
    Long salesDetailSeq;

    /** 発行ステータス */
    @Column(name = "statement_issue_status")
    String statementIssueStatus;

    /** 返金ステータス */
    @Column(name = "statement_refund_status")
    String statementRefundStatus;

    /** 精算額 */
    @Column(name = "statement_amount")
    BigDecimal statementAmount;

    /** 返金先名簿ID */
    @Column(name = "refund_to_person_id")
    Long refundToPersonId;

    /** 売上日 */
    @Column(name = "sales_date")
    LocalDate salesDate;

    /** 売上計上先 */
    @Column(name = "sales_account_seq")
    Long salesAccountSeq;

    /** 実費明細添付フラグ */
    @Column(name = "deposit_detail_attach_flg")
    String depositDetailAttachFlg;

    /** タイトル */
    @Column(name = "statement_title")
    String statementTitle;

    /** 日付 */
    @Column(name = "statement_date")
    LocalDate statementDate;

    /** 精算番号 */
    @Column(name = "statement_no")
    String statementNo;

    /** 精算先名称 */
    @Column(name = "statement_to_name")
    String statementToName;

    /** 精算先敬称 */
    @Column(name = "statement_to_name_end")
    String statementToNameEnd;

    /** 精算先詳細 */
    @Column(name = "statement_to_detail")
    String statementToDetail;

    /** 差出人事務所名 */
    @Column(name = "statement_from_tenant_name")
    String statementFromTenantName;

    /** 差出人詳細 */
    @Column(name = "statement_from_detail")
    String statementFromDetail;

    /** 印影フラグ */
    @Column(name = "tenant_stamp_print_flg")
    String tenantStampPrintFlg;

    /** 挿入文 */
    @Column(name = "statement_sub_text")
    String statementSubText;

    /** 件名 */
    @Column(name = "statement_subject")
    String statementSubject;

    /** 返金期限 */
    @Column(name = "refund_date")
    LocalDate refundDate;

    /** 返金期限印字フラグ */
    @Column(name = "refund_date_print_flg")
    String refundDatePrintFlg;

    /** 返金先 */
    @Column(name = "refund_bank_detail")
    String refundBankDetail;

    /** 備考 */
    @Column(name = "statement_remarks")
    String statementRemarks;

    /** 既入金取引日印字フラグ */
    @Column(name = "repay_transaction_date_print_flg")
    String repayTransactionDatePrintFlg;

    /** 請求取引日印字フラグ */
    @Column(name = "invoice_transaction_date_print_flg")
    String invoiceTransactionDatePrintFlg;

    /** 既入金項目合算フラグ（既入金） */
    @Column(name = "repay_sum_flg")
    String repaySumFlg;

    /** 実費項目合算フラグ（請求） */
    @Column(name = "expense_sum_flg")
    String expenseSumFlg;

    /** メモ */
    @Column(name = "statement_memo")
    String statementMemo;

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
     * Returns the statementSeq.
     * 
     * @return the statementSeq
     */
    public Long getStatementSeq() {
        return statementSeq;
    }

    /** 
     * Sets the statementSeq.
     * 
     * @param statementSeq the statementSeq
     */
    public void setStatementSeq(Long statementSeq) {
        this.statementSeq = statementSeq;
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
     * Returns the salesDetailSeq.
     * 
     * @return the salesDetailSeq
     */
    public Long getSalesDetailSeq() {
        return salesDetailSeq;
    }

    /** 
     * Sets the salesDetailSeq.
     * 
     * @param salesDetailSeq the salesDetailSeq
     */
    public void setSalesDetailSeq(Long salesDetailSeq) {
        this.salesDetailSeq = salesDetailSeq;
    }

    /** 
     * Returns the statementIssueStatus.
     * 
     * @return the statementIssueStatus
     */
    public String getStatementIssueStatus() {
        return statementIssueStatus;
    }

    /** 
     * Sets the statementIssueStatus.
     * 
     * @param statementIssueStatus the statementIssueStatus
     */
    public void setStatementIssueStatus(String statementIssueStatus) {
        this.statementIssueStatus = statementIssueStatus;
    }

    /** 
     * Returns the statementRefundStatus.
     * 
     * @return the statementRefundStatus
     */
    public String getStatementRefundStatus() {
        return statementRefundStatus;
    }

    /** 
     * Sets the statementRefundStatus.
     * 
     * @param statementRefundStatus the statementRefundStatus
     */
    public void setStatementRefundStatus(String statementRefundStatus) {
        this.statementRefundStatus = statementRefundStatus;
    }

    /** 
     * Returns the statementAmount.
     * 
     * @return the statementAmount
     */
    public BigDecimal getStatementAmount() {
        return statementAmount;
    }

    /** 
     * Sets the statementAmount.
     * 
     * @param statementAmount the statementAmount
     */
    public void setStatementAmount(BigDecimal statementAmount) {
        this.statementAmount = statementAmount;
    }

    /** 
     * Returns the refundToPersonId.
     * 
     * @return the refundToPersonId
     */
    public Long getRefundToPersonId() {
        return refundToPersonId;
    }

    /** 
     * Sets the refundToPersonId.
     * 
     * @param refundToPersonId the refundToPersonId
     */
    public void setRefundToPersonId(Long refundToPersonId) {
        this.refundToPersonId = refundToPersonId;
    }

    /** 
     * Returns the salesDate.
     * 
     * @return the salesDate
     */
    public LocalDate getSalesDate() {
        return salesDate;
    }

    /** 
     * Sets the salesDate.
     * 
     * @param salesDate the salesDate
     */
    public void setSalesDate(LocalDate salesDate) {
        this.salesDate = salesDate;
    }

    /** 
     * Returns the salesAccountSeq.
     * 
     * @return the salesAccountSeq
     */
    public Long getSalesAccountSeq() {
        return salesAccountSeq;
    }

    /** 
     * Sets the salesAccountSeq.
     * 
     * @param salesAccountSeq the salesAccountSeq
     */
    public void setSalesAccountSeq(Long salesAccountSeq) {
        this.salesAccountSeq = salesAccountSeq;
    }

    /** 
     * Returns the depositDetailAttachFlg.
     * 
     * @return the depositDetailAttachFlg
     */
    public String getDepositDetailAttachFlg() {
        return depositDetailAttachFlg;
    }

    /** 
     * Sets the depositDetailAttachFlg.
     * 
     * @param depositDetailAttachFlg the depositDetailAttachFlg
     */
    public void setDepositDetailAttachFlg(String depositDetailAttachFlg) {
        this.depositDetailAttachFlg = depositDetailAttachFlg;
    }

    /** 
     * Returns the statementTitle.
     * 
     * @return the statementTitle
     */
    public String getStatementTitle() {
        return statementTitle;
    }

    /** 
     * Sets the statementTitle.
     * 
     * @param statementTitle the statementTitle
     */
    public void setStatementTitle(String statementTitle) {
        this.statementTitle = statementTitle;
    }

    /** 
     * Returns the statementDate.
     * 
     * @return the statementDate
     */
    public LocalDate getStatementDate() {
        return statementDate;
    }

    /** 
     * Sets the statementDate.
     * 
     * @param statementDate the statementDate
     */
    public void setStatementDate(LocalDate statementDate) {
        this.statementDate = statementDate;
    }

    /** 
     * Returns the statementNo.
     * 
     * @return the statementNo
     */
    public String getStatementNo() {
        return statementNo;
    }

    /** 
     * Sets the statementNo.
     * 
     * @param statementNo the statementNo
     */
    public void setStatementNo(String statementNo) {
        this.statementNo = statementNo;
    }

    /** 
     * Returns the statementToName.
     * 
     * @return the statementToName
     */
    public String getStatementToName() {
        return statementToName;
    }

    /** 
     * Sets the statementToName.
     * 
     * @param statementToName the statementToName
     */
    public void setStatementToName(String statementToName) {
        this.statementToName = statementToName;
    }

    /** 
     * Returns the statementToNameEnd.
     * 
     * @return the statementToNameEnd
     */
    public String getStatementToNameEnd() {
        return statementToNameEnd;
    }

    /** 
     * Sets the statementToNameEnd.
     * 
     * @param statementToNameEnd the statementToNameEnd
     */
    public void setStatementToNameEnd(String statementToNameEnd) {
        this.statementToNameEnd = statementToNameEnd;
    }

    /** 
     * Returns the statementToDetail.
     * 
     * @return the statementToDetail
     */
    public String getStatementToDetail() {
        return statementToDetail;
    }

    /** 
     * Sets the statementToDetail.
     * 
     * @param statementToDetail the statementToDetail
     */
    public void setStatementToDetail(String statementToDetail) {
        this.statementToDetail = statementToDetail;
    }

    /** 
     * Returns the statementFromTenantName.
     * 
     * @return the statementFromTenantName
     */
    public String getStatementFromTenantName() {
        return statementFromTenantName;
    }

    /** 
     * Sets the statementFromTenantName.
     * 
     * @param statementFromTenantName the statementFromTenantName
     */
    public void setStatementFromTenantName(String statementFromTenantName) {
        this.statementFromTenantName = statementFromTenantName;
    }

    /** 
     * Returns the statementFromDetail.
     * 
     * @return the statementFromDetail
     */
    public String getStatementFromDetail() {
        return statementFromDetail;
    }

    /** 
     * Sets the statementFromDetail.
     * 
     * @param statementFromDetail the statementFromDetail
     */
    public void setStatementFromDetail(String statementFromDetail) {
        this.statementFromDetail = statementFromDetail;
    }

    /** 
     * Returns the tenantStampPrintFlg.
     * 
     * @return the tenantStampPrintFlg
     */
    public String getTenantStampPrintFlg() {
        return tenantStampPrintFlg;
    }

    /** 
     * Sets the tenantStampPrintFlg.
     * 
     * @param tenantStampPrintFlg the tenantStampPrintFlg
     */
    public void setTenantStampPrintFlg(String tenantStampPrintFlg) {
        this.tenantStampPrintFlg = tenantStampPrintFlg;
    }

    /** 
     * Returns the statementSubText.
     * 
     * @return the statementSubText
     */
    public String getStatementSubText() {
        return statementSubText;
    }

    /** 
     * Sets the statementSubText.
     * 
     * @param statementSubText the statementSubText
     */
    public void setStatementSubText(String statementSubText) {
        this.statementSubText = statementSubText;
    }

    /** 
     * Returns the statementSubject.
     * 
     * @return the statementSubject
     */
    public String getStatementSubject() {
        return statementSubject;
    }

    /** 
     * Sets the statementSubject.
     * 
     * @param statementSubject the statementSubject
     */
    public void setStatementSubject(String statementSubject) {
        this.statementSubject = statementSubject;
    }

    /** 
     * Returns the refundDate.
     * 
     * @return the refundDate
     */
    public LocalDate getRefundDate() {
        return refundDate;
    }

    /** 
     * Sets the refundDate.
     * 
     * @param refundDate the refundDate
     */
    public void setRefundDate(LocalDate refundDate) {
        this.refundDate = refundDate;
    }

    /** 
     * Returns the refundDatePrintFlg.
     * 
     * @return the refundDatePrintFlg
     */
    public String getRefundDatePrintFlg() {
        return refundDatePrintFlg;
    }

    /** 
     * Sets the refundDatePrintFlg.
     * 
     * @param refundDatePrintFlg the refundDatePrintFlg
     */
    public void setRefundDatePrintFlg(String refundDatePrintFlg) {
        this.refundDatePrintFlg = refundDatePrintFlg;
    }

    /** 
     * Returns the refundBankDetail.
     * 
     * @return the refundBankDetail
     */
    public String getRefundBankDetail() {
        return refundBankDetail;
    }

    /** 
     * Sets the refundBankDetail.
     * 
     * @param refundBankDetail the refundBankDetail
     */
    public void setRefundBankDetail(String refundBankDetail) {
        this.refundBankDetail = refundBankDetail;
    }

    /** 
     * Returns the statementRemarks.
     * 
     * @return the statementRemarks
     */
    public String getStatementRemarks() {
        return statementRemarks;
    }

    /** 
     * Sets the statementRemarks.
     * 
     * @param statementRemarks the statementRemarks
     */
    public void setStatementRemarks(String statementRemarks) {
        this.statementRemarks = statementRemarks;
    }

    /** 
     * Returns the repayTransactionDatePrintFlg.
     * 
     * @return the repayTransactionDatePrintFlg
     */
    public String getRepayTransactionDatePrintFlg() {
        return repayTransactionDatePrintFlg;
    }

    /** 
     * Sets the repayTransactionDatePrintFlg.
     * 
     * @param repayTransactionDatePrintFlg the repayTransactionDatePrintFlg
     */
    public void setRepayTransactionDatePrintFlg(String repayTransactionDatePrintFlg) {
        this.repayTransactionDatePrintFlg = repayTransactionDatePrintFlg;
    }

    /** 
     * Returns the invoiceTransactionDatePrintFlg.
     * 
     * @return the invoiceTransactionDatePrintFlg
     */
    public String getInvoiceTransactionDatePrintFlg() {
        return invoiceTransactionDatePrintFlg;
    }

    /** 
     * Sets the invoiceTransactionDatePrintFlg.
     * 
     * @param invoiceTransactionDatePrintFlg the invoiceTransactionDatePrintFlg
     */
    public void setInvoiceTransactionDatePrintFlg(String invoiceTransactionDatePrintFlg) {
        this.invoiceTransactionDatePrintFlg = invoiceTransactionDatePrintFlg;
    }

    /** 
     * Returns the repaySumFlg.
     * 
     * @return the repaySumFlg
     */
    public String getRepaySumFlg() {
        return repaySumFlg;
    }

    /** 
     * Sets the repaySumFlg.
     * 
     * @param repaySumFlg the repaySumFlg
     */
    public void setRepaySumFlg(String repaySumFlg) {
        this.repaySumFlg = repaySumFlg;
    }

    /** 
     * Returns the expenseSumFlg.
     * 
     * @return the expenseSumFlg
     */
    public String getExpenseSumFlg() {
        return expenseSumFlg;
    }

    /** 
     * Sets the expenseSumFlg.
     * 
     * @param expenseSumFlg the expenseSumFlg
     */
    public void setExpenseSumFlg(String expenseSumFlg) {
        this.expenseSumFlg = expenseSumFlg;
    }

    /** 
     * Returns the statementMemo.
     * 
     * @return the statementMemo
     */
    public String getStatementMemo() {
        return statementMemo;
    }

    /** 
     * Sets the statementMemo.
     * 
     * @param statementMemo the statementMemo
     */
    public void setStatementMemo(String statementMemo) {
        this.statementMemo = statementMemo;
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