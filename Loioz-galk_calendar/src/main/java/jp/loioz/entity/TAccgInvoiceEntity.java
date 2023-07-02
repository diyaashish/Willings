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
 * 請求書
 */
@Entity(listener = TAccgInvoiceEntityListener.class)
@Table(name = "t_accg_invoice")
public class TAccgInvoiceEntity extends DefaultEntity {

    /** 請求書SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_seq")
    Long invoiceSeq;

    /** 会計書類SEQ */
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 売上明細SEQ */
    @Column(name = "sales_detail_seq")
    Long salesDetailSeq;

    /** 回収不能金詳細SEQ */
    @Column(name = "uncollectible_detail_seq")
    Long uncollectibleDetailSeq;

    /** 発行ステータス */
    @Column(name = "invoice_issue_status")
    String invoiceIssueStatus;

    /** 入金ステータス */
    @Column(name = "invoice_payment_status")
    String invoicePaymentStatus;

    /** 請求額 */
    @Column(name = "invoice_amount")
    BigDecimal invoiceAmount;

    /** 請求方法 */
    @Column(name = "invoice_type")
    String invoiceType;

    /** 請求先名簿ID */
    @Column(name = "bill_to_person_id")
    Long billToPersonId;

    /** 売上日 */
    @Column(name = "sales_date")
    LocalDate salesDate;

    /** 売上計上先 */
    @Column(name = "sales_account_seq")
    Long salesAccountSeq;

    /** 実費明細添付フラグ */
    @Column(name = "deposit_detail_attach_flg")
    String depositDetailAttachFlg;

    /** 支払計画添付フラグ */
    @Column(name = "payment_plan_attach_flg")
    String paymentPlanAttachFlg;

    /** タイトル */
    @Column(name = "invoice_title")
    String invoiceTitle;

    /** 日付 */
    @Column(name = "invoice_date")
    LocalDate invoiceDate;

    /** 請求番号 */
    @Column(name = "invoice_no")
    String invoiceNo;

    /** 請求先名称 */
    @Column(name = "invoice_to_name")
    String invoiceToName;

    /** 請求先敬称 */
    @Column(name = "invoice_to_name_end")
    String invoiceToNameEnd;

    /** 請求先詳細 */
    @Column(name = "invoice_to_detail")
    String invoiceToDetail;

    /** 差出人事務所名 */
    @Column(name = "invoice_from_tenant_name")
    String invoiceFromTenantName;

    /** 差出人詳細 */
    @Column(name = "invoice_from_detail")
    String invoiceFromDetail;

    /** 印影フラグ */
    @Column(name = "tenant_stamp_print_flg")
    String tenantStampPrintFlg;

    /** 挿入文 */
    @Column(name = "invoice_sub_text")
    String invoiceSubText;

    /** 件名 */
    @Column(name = "invoice_subject")
    String invoiceSubject;

    /** 支払期限 */
    @Column(name = "due_date")
    LocalDate dueDate;

    /** 支払期限印字フラグ */
    @Column(name = "due_date_print_flg")
    String dueDatePrintFlg;

    /** 振込先 */
    @Column(name = "tenant_bank_detail")
    String tenantBankDetail;

    /** 備考 */
    @Column(name = "invoice_remarks")
    String invoiceRemarks;

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
    @Column(name = "invoice_memo")
    String invoiceMemo;

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
     * Returns the invoiceSeq.
     * 
     * @return the invoiceSeq
     */
    public Long getInvoiceSeq() {
        return invoiceSeq;
    }

    /** 
     * Sets the invoiceSeq.
     * 
     * @param invoiceSeq the invoiceSeq
     */
    public void setInvoiceSeq(Long invoiceSeq) {
        this.invoiceSeq = invoiceSeq;
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
     * Returns the uncollectibleDetailSeq.
     * 
     * @return the uncollectibleDetailSeq
     */
    public Long getUncollectibleDetailSeq() {
        return uncollectibleDetailSeq;
    }

    /** 
     * Sets the uncollectibleDetailSeq.
     * 
     * @param uncollectibleDetailSeq the uncollectibleDetailSeq
     */
    public void setUncollectibleDetailSeq(Long uncollectibleDetailSeq) {
        this.uncollectibleDetailSeq = uncollectibleDetailSeq;
    }

    /** 
     * Returns the invoiceIssueStatus.
     * 
     * @return the invoiceIssueStatus
     */
    public String getInvoiceIssueStatus() {
        return invoiceIssueStatus;
    }

    /** 
     * Sets the invoiceIssueStatus.
     * 
     * @param invoiceIssueStatus the invoiceIssueStatus
     */
    public void setInvoiceIssueStatus(String invoiceIssueStatus) {
        this.invoiceIssueStatus = invoiceIssueStatus;
    }

    /** 
     * Returns the invoicePaymentStatus.
     * 
     * @return the invoicePaymentStatus
     */
    public String getInvoicePaymentStatus() {
        return invoicePaymentStatus;
    }

    /** 
     * Sets the invoicePaymentStatus.
     * 
     * @param invoicePaymentStatus the invoicePaymentStatus
     */
    public void setInvoicePaymentStatus(String invoicePaymentStatus) {
        this.invoicePaymentStatus = invoicePaymentStatus;
    }

    /** 
     * Returns the invoiceAmount.
     * 
     * @return the invoiceAmount
     */
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /** 
     * Sets the invoiceAmount.
     * 
     * @param invoiceAmount the invoiceAmount
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /** 
     * Returns the invoiceType.
     * 
     * @return the invoiceType
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /** 
     * Sets the invoiceType.
     * 
     * @param invoiceType the invoiceType
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    /** 
     * Returns the billToPersonId.
     * 
     * @return the billToPersonId
     */
    public Long getBillToPersonId() {
        return billToPersonId;
    }

    /** 
     * Sets the billToPersonId.
     * 
     * @param billToPersonId the billToPersonId
     */
    public void setBillToPersonId(Long billToPersonId) {
        this.billToPersonId = billToPersonId;
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
     * Returns the paymentPlanAttachFlg.
     * 
     * @return the paymentPlanAttachFlg
     */
    public String getPaymentPlanAttachFlg() {
        return paymentPlanAttachFlg;
    }

    /** 
     * Sets the paymentPlanAttachFlg.
     * 
     * @param paymentPlanAttachFlg the paymentPlanAttachFlg
     */
    public void setPaymentPlanAttachFlg(String paymentPlanAttachFlg) {
        this.paymentPlanAttachFlg = paymentPlanAttachFlg;
    }

    /** 
     * Returns the invoiceTitle.
     * 
     * @return the invoiceTitle
     */
    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    /** 
     * Sets the invoiceTitle.
     * 
     * @param invoiceTitle the invoiceTitle
     */
    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    /** 
     * Returns the invoiceDate.
     * 
     * @return the invoiceDate
     */
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    /** 
     * Sets the invoiceDate.
     * 
     * @param invoiceDate the invoiceDate
     */
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /** 
     * Returns the invoiceNo.
     * 
     * @return the invoiceNo
     */
    public String getInvoiceNo() {
        return invoiceNo;
    }

    /** 
     * Sets the invoiceNo.
     * 
     * @param invoiceNo the invoiceNo
     */
    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    /** 
     * Returns the invoiceToName.
     * 
     * @return the invoiceToName
     */
    public String getInvoiceToName() {
        return invoiceToName;
    }

    /** 
     * Sets the invoiceToName.
     * 
     * @param invoiceToName the invoiceToName
     */
    public void setInvoiceToName(String invoiceToName) {
        this.invoiceToName = invoiceToName;
    }

    /** 
     * Returns the invoiceToNameEnd.
     * 
     * @return the invoiceToNameEnd
     */
    public String getInvoiceToNameEnd() {
        return invoiceToNameEnd;
    }

    /** 
     * Sets the invoiceToNameEnd.
     * 
     * @param invoiceToNameEnd the invoiceToNameEnd
     */
    public void setInvoiceToNameEnd(String invoiceToNameEnd) {
        this.invoiceToNameEnd = invoiceToNameEnd;
    }

    /** 
     * Returns the invoiceToDetail.
     * 
     * @return the invoiceToDetail
     */
    public String getInvoiceToDetail() {
        return invoiceToDetail;
    }

    /** 
     * Sets the invoiceToDetail.
     * 
     * @param invoiceToDetail the invoiceToDetail
     */
    public void setInvoiceToDetail(String invoiceToDetail) {
        this.invoiceToDetail = invoiceToDetail;
    }

    /** 
     * Returns the invoiceFromTenantName.
     * 
     * @return the invoiceFromTenantName
     */
    public String getInvoiceFromTenantName() {
        return invoiceFromTenantName;
    }

    /** 
     * Sets the invoiceFromTenantName.
     * 
     * @param invoiceFromTenantName the invoiceFromTenantName
     */
    public void setInvoiceFromTenantName(String invoiceFromTenantName) {
        this.invoiceFromTenantName = invoiceFromTenantName;
    }

    /** 
     * Returns the invoiceFromDetail.
     * 
     * @return the invoiceFromDetail
     */
    public String getInvoiceFromDetail() {
        return invoiceFromDetail;
    }

    /** 
     * Sets the invoiceFromDetail.
     * 
     * @param invoiceFromDetail the invoiceFromDetail
     */
    public void setInvoiceFromDetail(String invoiceFromDetail) {
        this.invoiceFromDetail = invoiceFromDetail;
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
     * Returns the invoiceSubText.
     * 
     * @return the invoiceSubText
     */
    public String getInvoiceSubText() {
        return invoiceSubText;
    }

    /** 
     * Sets the invoiceSubText.
     * 
     * @param invoiceSubText the invoiceSubText
     */
    public void setInvoiceSubText(String invoiceSubText) {
        this.invoiceSubText = invoiceSubText;
    }

    /** 
     * Returns the invoiceSubject.
     * 
     * @return the invoiceSubject
     */
    public String getInvoiceSubject() {
        return invoiceSubject;
    }

    /** 
     * Sets the invoiceSubject.
     * 
     * @param invoiceSubject the invoiceSubject
     */
    public void setInvoiceSubject(String invoiceSubject) {
        this.invoiceSubject = invoiceSubject;
    }

    /** 
     * Returns the dueDate.
     * 
     * @return the dueDate
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /** 
     * Sets the dueDate.
     * 
     * @param dueDate the dueDate
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /** 
     * Returns the dueDatePrintFlg.
     * 
     * @return the dueDatePrintFlg
     */
    public String getDueDatePrintFlg() {
        return dueDatePrintFlg;
    }

    /** 
     * Sets the dueDatePrintFlg.
     * 
     * @param dueDatePrintFlg the dueDatePrintFlg
     */
    public void setDueDatePrintFlg(String dueDatePrintFlg) {
        this.dueDatePrintFlg = dueDatePrintFlg;
    }

    /** 
     * Returns the tenantBankDetail.
     * 
     * @return the tenantBankDetail
     */
    public String getTenantBankDetail() {
        return tenantBankDetail;
    }

    /** 
     * Sets the tenantBankDetail.
     * 
     * @param tenantBankDetail the tenantBankDetail
     */
    public void setTenantBankDetail(String tenantBankDetail) {
        this.tenantBankDetail = tenantBankDetail;
    }

    /** 
     * Returns the invoiceRemarks.
     * 
     * @return the invoiceRemarks
     */
    public String getInvoiceRemarks() {
        return invoiceRemarks;
    }

    /** 
     * Sets the invoiceRemarks.
     * 
     * @param invoiceRemarks the invoiceRemarks
     */
    public void setInvoiceRemarks(String invoiceRemarks) {
        this.invoiceRemarks = invoiceRemarks;
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
     * Returns the invoiceMemo.
     * 
     * @return the invoiceMemo
     */
    public String getInvoiceMemo() {
        return invoiceMemo;
    }

    /** 
     * Sets the invoiceMemo.
     * 
     * @param invoiceMemo the invoiceMemo
     */
    public void setInvoiceMemo(String invoiceMemo) {
        this.invoiceMemo = invoiceMemo;
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