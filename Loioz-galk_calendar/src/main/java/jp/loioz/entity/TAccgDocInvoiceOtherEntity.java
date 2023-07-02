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
 * 請求項目-その他
 */
@Entity(listener = TAccgDocInvoiceOtherEntityListener.class)
@Table(name = "t_accg_doc_invoice_other")
public class TAccgDocInvoiceOtherEntity extends DefaultEntity {

    /** 請求その他項目SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_invoice_other_seq")
    Long docInvoiceOtherSeq;

    /** 請求項目SEQ */
    @Column(name = "doc_invoice_seq")
    Long docInvoiceSeq;

    /** 取引日 */
    @Column(name = "other_transaction_date")
    LocalDate otherTransactionDate;

    /** 種類 */
    @Column(name = "other_item_type")
    String otherItemType;

    /** 項目名 */
    @Column(name = "other_item_name")
    String otherItemName;

    /** 金額 */
    @Column(name = "other_amount")
    BigDecimal otherAmount;

    /** 値引き消費税率 */
    @Column(name = "discount_tax_rate_type")
    String discountTaxRateType;

    /** 値引き源泉徴収フラグ */
    @Column(name = "discount_withholding_flg")
    String discountWithholdingFlg;

    /** 摘要 */
    @Column(name = "sum_text")
    String sumText;

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
     * Returns the docInvoiceOtherSeq.
     * 
     * @return the docInvoiceOtherSeq
     */
    public Long getDocInvoiceOtherSeq() {
        return docInvoiceOtherSeq;
    }

    /** 
     * Sets the docInvoiceOtherSeq.
     * 
     * @param docInvoiceOtherSeq the docInvoiceOtherSeq
     */
    public void setDocInvoiceOtherSeq(Long docInvoiceOtherSeq) {
        this.docInvoiceOtherSeq = docInvoiceOtherSeq;
    }

    /** 
     * Returns the docInvoiceSeq.
     * 
     * @return the docInvoiceSeq
     */
    public Long getDocInvoiceSeq() {
        return docInvoiceSeq;
    }

    /** 
     * Sets the docInvoiceSeq.
     * 
     * @param docInvoiceSeq the docInvoiceSeq
     */
    public void setDocInvoiceSeq(Long docInvoiceSeq) {
        this.docInvoiceSeq = docInvoiceSeq;
    }

    /** 
     * Returns the otherTransactionDate.
     * 
     * @return the otherTransactionDate
     */
    public LocalDate getOtherTransactionDate() {
        return otherTransactionDate;
    }

    /** 
     * Sets the otherTransactionDate.
     * 
     * @param otherTransactionDate the otherTransactionDate
     */
    public void setOtherTransactionDate(LocalDate otherTransactionDate) {
        this.otherTransactionDate = otherTransactionDate;
    }

    /** 
     * Returns the otherItemType.
     * 
     * @return the otherItemType
     */
    public String getOtherItemType() {
        return otherItemType;
    }

    /** 
     * Sets the otherItemType.
     * 
     * @param otherItemType the otherItemType
     */
    public void setOtherItemType(String otherItemType) {
        this.otherItemType = otherItemType;
    }

    /** 
     * Returns the otherItemName.
     * 
     * @return the otherItemName
     */
    public String getOtherItemName() {
        return otherItemName;
    }

    /** 
     * Sets the otherItemName.
     * 
     * @param otherItemName the otherItemName
     */
    public void setOtherItemName(String otherItemName) {
        this.otherItemName = otherItemName;
    }

    /** 
     * Returns the otherAmount.
     * 
     * @return the otherAmount
     */
    public BigDecimal getOtherAmount() {
        return otherAmount;
    }

    /** 
     * Sets the otherAmount.
     * 
     * @param otherAmount the otherAmount
     */
    public void setOtherAmount(BigDecimal otherAmount) {
        this.otherAmount = otherAmount;
    }

    /** 
     * Returns the discountTaxRateType.
     * 
     * @return the discountTaxRateType
     */
    public String getDiscountTaxRateType() {
        return discountTaxRateType;
    }

    /** 
     * Sets the discountTaxRateType.
     * 
     * @param discountTaxRateType the discountTaxRateType
     */
    public void setDiscountTaxRateType(String discountTaxRateType) {
        this.discountTaxRateType = discountTaxRateType;
    }

    /** 
     * Returns the discountWithholdingFlg.
     * 
     * @return the discountWithholdingFlg
     */
    public String getDiscountWithholdingFlg() {
        return discountWithholdingFlg;
    }

    /** 
     * Sets the discountWithholdingFlg.
     * 
     * @param discountWithholdingFlg the discountWithholdingFlg
     */
    public void setDiscountWithholdingFlg(String discountWithholdingFlg) {
        this.discountWithholdingFlg = discountWithholdingFlg;
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