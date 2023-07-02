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
 * 預り金
 */
@Entity(listener = TDepositRecvEntityListener.class)
@Table(name = "t_deposit_recv")
public class TDepositRecvEntity extends DefaultEntity {

    /** 預り金SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_recv_seq")
    Long depositRecvSeq;

    /** 名簿ID */
    @Column(name = "person_id")
    Long personId;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 会計書類SEQ（作成元） */
    @Column(name = "created_accg_doc_seq")
    Long createdAccgDocSeq;

    /** 会計書類SEQ（使用先） */
    @Column(name = "using_accg_doc_seq")
    Long usingAccgDocSeq;

    /** 作成タイプ */
    @Column(name = "created_type")
    String createdType;

    /** 実費入金フラグ */
    @Column(name = "expense_invoice_flg")
    String expenseInvoiceFlg;

    /** 発生日 */
    @Column(name = "deposit_date")
    LocalDate depositDate;

    /** 預り金項目名 */
    @Column(name = "deposit_item_name")
    String depositItemName;

    /** 入金額 */
    @Column(name = "deposit_amount")
    BigDecimal depositAmount;

    /** 出金額 */
    @Column(name = "withdrawal_amount")
    BigDecimal withdrawalAmount;

    /** 入出金タイプ */
    @Column(name = "deposit_type")
    String depositType;

    /** 摘要 */
    @Column(name = "sum_text")
    String sumText;

    /** メモ */
    @Column(name = "deposit_recv_memo")
    String depositRecvMemo;

    /** 事務所負担フラグ */
    @Column(name = "tenant_bear_flg")
    String tenantBearFlg;

    /** 入出金完了フラグ */
    @Column(name = "deposit_complete_flg")
    String depositCompleteFlg;

    /** 回収不能フラグ */
    @Column(name = "uncollectible_flg")
    String uncollectibleFlg;

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
     * Returns the personId.
     * 
     * @return the personId
     */
    public Long getPersonId() {
        return personId;
    }

    /** 
     * Sets the personId.
     * 
     * @param personId the personId
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /** 
     * Returns the ankenId.
     * 
     * @return the ankenId
     */
    public Long getAnkenId() {
        return ankenId;
    }

    /** 
     * Sets the ankenId.
     * 
     * @param ankenId the ankenId
     */
    public void setAnkenId(Long ankenId) {
        this.ankenId = ankenId;
    }

    /** 
     * Returns the createdAccgDocSeq.
     * 
     * @return the createdAccgDocSeq
     */
    public Long getCreatedAccgDocSeq() {
        return createdAccgDocSeq;
    }

    /** 
     * Sets the createdAccgDocSeq.
     * 
     * @param createdAccgDocSeq the createdAccgDocSeq
     */
    public void setCreatedAccgDocSeq(Long createdAccgDocSeq) {
        this.createdAccgDocSeq = createdAccgDocSeq;
    }

    /** 
     * Returns the usingAccgDocSeq.
     * 
     * @return the usingAccgDocSeq
     */
    public Long getUsingAccgDocSeq() {
        return usingAccgDocSeq;
    }

    /** 
     * Sets the usingAccgDocSeq.
     * 
     * @param usingAccgDocSeq the usingAccgDocSeq
     */
    public void setUsingAccgDocSeq(Long usingAccgDocSeq) {
        this.usingAccgDocSeq = usingAccgDocSeq;
    }

    /** 
     * Returns the createdType.
     * 
     * @return the createdType
     */
    public String getCreatedType() {
        return createdType;
    }

    /** 
     * Sets the createdType.
     * 
     * @param createdType the createdType
     */
    public void setCreatedType(String createdType) {
        this.createdType = createdType;
    }

    /** 
     * Returns the expenseInvoiceFlg.
     * 
     * @return the expenseInvoiceFlg
     */
    public String getExpenseInvoiceFlg() {
        return expenseInvoiceFlg;
    }

    /** 
     * Sets the expenseInvoiceFlg.
     * 
     * @param expenseInvoiceFlg the expenseInvoiceFlg
     */
    public void setExpenseInvoiceFlg(String expenseInvoiceFlg) {
        this.expenseInvoiceFlg = expenseInvoiceFlg;
    }

    /** 
     * Returns the depositDate.
     * 
     * @return the depositDate
     */
    public LocalDate getDepositDate() {
        return depositDate;
    }

    /** 
     * Sets the depositDate.
     * 
     * @param depositDate the depositDate
     */
    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    /** 
     * Returns the depositItemName.
     * 
     * @return the depositItemName
     */
    public String getDepositItemName() {
        return depositItemName;
    }

    /** 
     * Sets the depositItemName.
     * 
     * @param depositItemName the depositItemName
     */
    public void setDepositItemName(String depositItemName) {
        this.depositItemName = depositItemName;
    }

    /** 
     * Returns the depositAmount.
     * 
     * @return the depositAmount
     */
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    /** 
     * Sets the depositAmount.
     * 
     * @param depositAmount the depositAmount
     */
    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    /** 
     * Returns the withdrawalAmount.
     * 
     * @return the withdrawalAmount
     */
    public BigDecimal getWithdrawalAmount() {
        return withdrawalAmount;
    }

    /** 
     * Sets the withdrawalAmount.
     * 
     * @param withdrawalAmount the withdrawalAmount
     */
    public void setWithdrawalAmount(BigDecimal withdrawalAmount) {
        this.withdrawalAmount = withdrawalAmount;
    }

    /** 
     * Returns the depositType.
     * 
     * @return the depositType
     */
    public String getDepositType() {
        return depositType;
    }

    /** 
     * Sets the depositType.
     * 
     * @param depositType the depositType
     */
    public void setDepositType(String depositType) {
        this.depositType = depositType;
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
     * Returns the depositRecvMemo.
     * 
     * @return the depositRecvMemo
     */
    public String getDepositRecvMemo() {
        return depositRecvMemo;
    }

    /** 
     * Sets the depositRecvMemo.
     * 
     * @param depositRecvMemo the depositRecvMemo
     */
    public void setDepositRecvMemo(String depositRecvMemo) {
        this.depositRecvMemo = depositRecvMemo;
    }

    /** 
     * Returns the tenantBearFlg.
     * 
     * @return the tenantBearFlg
     */
    public String getTenantBearFlg() {
        return tenantBearFlg;
    }

    /** 
     * Sets the tenantBearFlg.
     * 
     * @param tenantBearFlg the tenantBearFlg
     */
    public void setTenantBearFlg(String tenantBearFlg) {
        this.tenantBearFlg = tenantBearFlg;
    }

    /** 
     * Returns the depositCompleteFlg.
     * 
     * @return the depositCompleteFlg
     */
    public String getDepositCompleteFlg() {
        return depositCompleteFlg;
    }

    /** 
     * Sets the depositCompleteFlg.
     * 
     * @param depositCompleteFlg the depositCompleteFlg
     */
    public void setDepositCompleteFlg(String depositCompleteFlg) {
        this.depositCompleteFlg = depositCompleteFlg;
    }

    /** 
     * Returns the uncollectibleFlg.
     * 
     * @return the uncollectibleFlg
     */
    public String getUncollectibleFlg() {
        return uncollectibleFlg;
    }

    /** 
     * Sets the uncollectibleFlg.
     * 
     * @param uncollectibleFlg the uncollectibleFlg
     */
    public void setUncollectibleFlg(String uncollectibleFlg) {
        this.uncollectibleFlg = uncollectibleFlg;
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