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
 * 請求書設定
 */
@Entity(listener = MInvoiceSettingEntityListener.class)
@Table(name = "m_invoice_setting")
public class MInvoiceSettingEntity extends DefaultEntity {

    /** 請求書設定SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_setting_seq")
    Long invoiceSettingSeq;

    /** タイトル */
    @Column(name = "default_title")
    String defaultTitle;

    /** 挿入文 */
    @Column(name = "default_sub_text")
    String defaultSubText;

    /** 件名-プレフィックス */
    @Column(name = "subject_prefix")
    String subjectPrefix;

    /** 件名-サフィックス */
    @Column(name = "subject_suffix")
    String subjectSuffix;

    /** 請求番号-接頭辞 */
    @Column(name = "invoice_no_prefix")
    String invoiceNoPrefix;

    /** 請求番号-年フォーマット */
    @Column(name = "invoice_no_y_fmt")
    String invoiceNoYFmt;

    /** 請求番号-月フォーマット */
    @Column(name = "invoice_no_m_fmt")
    String invoiceNoMFmt;

    /** 請求番号-日フォーマット */
    @Column(name = "invoice_no_d_fmt")
    String invoiceNoDFmt;

    /** 請求番号-区切り文字 */
    @Column(name = "invoice_no_delimiter")
    String invoiceNoDelimiter;

    /** 請求番号-連番タイプ */
    @Column(name = "invoice_no_numbering_type")
    String invoiceNoNumberingType;

    /** 請求番号-連番ゼロ埋めフラグ */
    @Column(name = "invoice_no_zero_pad_flg")
    String invoiceNoZeroPadFlg;

    /** 請求番号-連番ゼロ埋め桁数 */
    @Column(name = "invoice_no_zero_pad_digits")
    String invoiceNoZeroPadDigits;

    /** 取引日表示フラグ */
    @Column(name = "transaction_date_print_flg")
    String transactionDatePrintFlg;

    /** 振込期日表示フラグ */
    @Column(name = "due_date_print_flg")
    String dueDatePrintFlg;

    /** 事務所印表示フラグ */
    @Column(name = "tenant_stamp_print_flg")
    String tenantStampPrintFlg;

    /** 備考 */
    @Column(name = "default_remarks")
    String defaultRemarks;

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
     * Returns the invoiceSettingSeq.
     * 
     * @return the invoiceSettingSeq
     */
    public Long getInvoiceSettingSeq() {
        return invoiceSettingSeq;
    }

    /** 
     * Sets the invoiceSettingSeq.
     * 
     * @param invoiceSettingSeq the invoiceSettingSeq
     */
    public void setInvoiceSettingSeq(Long invoiceSettingSeq) {
        this.invoiceSettingSeq = invoiceSettingSeq;
    }

    /** 
     * Returns the defaultTitle.
     * 
     * @return the defaultTitle
     */
    public String getDefaultTitle() {
        return defaultTitle;
    }

    /** 
     * Sets the defaultTitle.
     * 
     * @param defaultTitle the defaultTitle
     */
    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    /** 
     * Returns the defaultSubText.
     * 
     * @return the defaultSubText
     */
    public String getDefaultSubText() {
        return defaultSubText;
    }

    /** 
     * Sets the defaultSubText.
     * 
     * @param defaultSubText the defaultSubText
     */
    public void setDefaultSubText(String defaultSubText) {
        this.defaultSubText = defaultSubText;
    }

    /** 
     * Returns the subjectPrefix.
     * 
     * @return the subjectPrefix
     */
    public String getSubjectPrefix() {
        return subjectPrefix;
    }

    /** 
     * Sets the subjectPrefix.
     * 
     * @param subjectPrefix the subjectPrefix
     */
    public void setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
    }

    /** 
     * Returns the subjectSuffix.
     * 
     * @return the subjectSuffix
     */
    public String getSubjectSuffix() {
        return subjectSuffix;
    }

    /** 
     * Sets the subjectSuffix.
     * 
     * @param subjectSuffix the subjectSuffix
     */
    public void setSubjectSuffix(String subjectSuffix) {
        this.subjectSuffix = subjectSuffix;
    }

    /** 
     * Returns the invoiceNoPrefix.
     * 
     * @return the invoiceNoPrefix
     */
    public String getInvoiceNoPrefix() {
        return invoiceNoPrefix;
    }

    /** 
     * Sets the invoiceNoPrefix.
     * 
     * @param invoiceNoPrefix the invoiceNoPrefix
     */
    public void setInvoiceNoPrefix(String invoiceNoPrefix) {
        this.invoiceNoPrefix = invoiceNoPrefix;
    }

    /** 
     * Returns the invoiceNoYFmt.
     * 
     * @return the invoiceNoYFmt
     */
    public String getInvoiceNoYFmt() {
        return invoiceNoYFmt;
    }

    /** 
     * Sets the invoiceNoYFmt.
     * 
     * @param invoiceNoYFmt the invoiceNoYFmt
     */
    public void setInvoiceNoYFmt(String invoiceNoYFmt) {
        this.invoiceNoYFmt = invoiceNoYFmt;
    }

    /** 
     * Returns the invoiceNoMFmt.
     * 
     * @return the invoiceNoMFmt
     */
    public String getInvoiceNoMFmt() {
        return invoiceNoMFmt;
    }

    /** 
     * Sets the invoiceNoMFmt.
     * 
     * @param invoiceNoMFmt the invoiceNoMFmt
     */
    public void setInvoiceNoMFmt(String invoiceNoMFmt) {
        this.invoiceNoMFmt = invoiceNoMFmt;
    }

    /** 
     * Returns the invoiceNoDFmt.
     * 
     * @return the invoiceNoDFmt
     */
    public String getInvoiceNoDFmt() {
        return invoiceNoDFmt;
    }

    /** 
     * Sets the invoiceNoDFmt.
     * 
     * @param invoiceNoDFmt the invoiceNoDFmt
     */
    public void setInvoiceNoDFmt(String invoiceNoDFmt) {
        this.invoiceNoDFmt = invoiceNoDFmt;
    }

    /** 
     * Returns the invoiceNoDelimiter.
     * 
     * @return the invoiceNoDelimiter
     */
    public String getInvoiceNoDelimiter() {
        return invoiceNoDelimiter;
    }

    /** 
     * Sets the invoiceNoDelimiter.
     * 
     * @param invoiceNoDelimiter the invoiceNoDelimiter
     */
    public void setInvoiceNoDelimiter(String invoiceNoDelimiter) {
        this.invoiceNoDelimiter = invoiceNoDelimiter;
    }

    /** 
     * Returns the invoiceNoNumberingType.
     * 
     * @return the invoiceNoNumberingType
     */
    public String getInvoiceNoNumberingType() {
        return invoiceNoNumberingType;
    }

    /** 
     * Sets the invoiceNoNumberingType.
     * 
     * @param invoiceNoNumberingType the invoiceNoNumberingType
     */
    public void setInvoiceNoNumberingType(String invoiceNoNumberingType) {
        this.invoiceNoNumberingType = invoiceNoNumberingType;
    }

    /** 
     * Returns the invoiceNoZeroPadFlg.
     * 
     * @return the invoiceNoZeroPadFlg
     */
    public String getInvoiceNoZeroPadFlg() {
        return invoiceNoZeroPadFlg;
    }

    /** 
     * Sets the invoiceNoZeroPadFlg.
     * 
     * @param invoiceNoZeroPadFlg the invoiceNoZeroPadFlg
     */
    public void setInvoiceNoZeroPadFlg(String invoiceNoZeroPadFlg) {
        this.invoiceNoZeroPadFlg = invoiceNoZeroPadFlg;
    }

    /** 
     * Returns the invoiceNoZeroPadDigits.
     * 
     * @return the invoiceNoZeroPadDigits
     */
    public String getInvoiceNoZeroPadDigits() {
        return invoiceNoZeroPadDigits;
    }

    /** 
     * Sets the invoiceNoZeroPadDigits.
     * 
     * @param invoiceNoZeroPadDigits the invoiceNoZeroPadDigits
     */
    public void setInvoiceNoZeroPadDigits(String invoiceNoZeroPadDigits) {
        this.invoiceNoZeroPadDigits = invoiceNoZeroPadDigits;
    }

    /** 
     * Returns the transactionDatePrintFlg.
     * 
     * @return the transactionDatePrintFlg
     */
    public String getTransactionDatePrintFlg() {
        return transactionDatePrintFlg;
    }

    /** 
     * Sets the transactionDatePrintFlg.
     * 
     * @param transactionDatePrintFlg the transactionDatePrintFlg
     */
    public void setTransactionDatePrintFlg(String transactionDatePrintFlg) {
        this.transactionDatePrintFlg = transactionDatePrintFlg;
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
     * Returns the defaultRemarks.
     * 
     * @return the defaultRemarks
     */
    public String getDefaultRemarks() {
        return defaultRemarks;
    }

    /** 
     * Sets the defaultRemarks.
     * 
     * @param defaultRemarks the defaultRemarks
     */
    public void setDefaultRemarks(String defaultRemarks) {
        this.defaultRemarks = defaultRemarks;
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