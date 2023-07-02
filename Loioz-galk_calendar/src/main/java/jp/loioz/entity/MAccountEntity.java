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
 * アカウント
 */
@Entity(listener = MAccountEntityListener.class)
@Table(name = "m_account")
public class MAccountEntity extends DefaultEntity {

    /** アカウントSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_seq")
    Long accountSeq;

    /** アカウントID */
    @Column(name = "account_id")
    String accountId;

    /** テナント連番 */
    @Column(name = "tenant_seq")
    Long tenantSeq;

    /** パスワード */
    @Column(name = "password")
    String password;

    /** アカウント姓 */
    @Column(name = "account_name_sei")
    String accountNameSei;

    /** アカウント姓（かな） */
    @Column(name = "account_name_sei_kana")
    String accountNameSeiKana;

    /** アカウント名 */
    @Column(name = "account_name_mei")
    String accountNameMei;

    /** アカウント名（かな） */
    @Column(name = "account_name_mei_kana")
    String accountNameMeiKana;

    /** アカウント種別 */
    @Column(name = "account_type")
    String accountType;

    /** アカウントステータス */
    @Column(name = "account_status")
    String accountStatus;

    /** アカウントメールアドレス */
    @Column(name = "account_mail_address")
    String accountMailAddress;

    /** アカウント権限 */
    @Column(name = "account_kengen")
    String accountKengen;

    /** アカウントオーナフラグ */
    @Column(name = "account_owner_flg")
    String accountOwnerFlg;

    /** アカウント色 */
    @Column(name = "account_color")
    String accountColor;

    /** 適格請求書発行事業者登録番号 */
    @Column(name = "account_invoice_registration_no")
    String accountInvoiceRegistrationNo;

    /** 弁護士職印画像SEQ */
    @Column(name = "account_lawyer_stamp_img_seq")
    Long accountLawyerStampImgSeq;

    /** 帳票印影表示フラグ */
    @Column(name = "account_lawyer_stamp_print_flg")
    String accountLawyerStampPrintFlg;

    /** メール署名 */
    @Column(name = "account_mail_signature")
    String accountMailSignature;

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

    /** 削除日時 */
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    /** 削除アカウントSEQ */
    @Column(name = "deleted_by")
    Long deletedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the accountSeq.
     * 
     * @return the accountSeq
     */
    public Long getAccountSeq() {
        return accountSeq;
    }

    /** 
     * Sets the accountSeq.
     * 
     * @param accountSeq the accountSeq
     */
    public void setAccountSeq(Long accountSeq) {
        this.accountSeq = accountSeq;
    }

    /** 
     * Returns the accountId.
     * 
     * @return the accountId
     */
    public String getAccountId() {
        return accountId;
    }

    /** 
     * Sets the accountId.
     * 
     * @param accountId the accountId
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /** 
     * Returns the tenantSeq.
     * 
     * @return the tenantSeq
     */
    public Long getTenantSeq() {
        return tenantSeq;
    }

    /** 
     * Sets the tenantSeq.
     * 
     * @param tenantSeq the tenantSeq
     */
    public void setTenantSeq(Long tenantSeq) {
        this.tenantSeq = tenantSeq;
    }

    /** 
     * Returns the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /** 
     * Sets the password.
     * 
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** 
     * Returns the accountNameSei.
     * 
     * @return the accountNameSei
     */
    public String getAccountNameSei() {
        return accountNameSei;
    }

    /** 
     * Sets the accountNameSei.
     * 
     * @param accountNameSei the accountNameSei
     */
    public void setAccountNameSei(String accountNameSei) {
        this.accountNameSei = accountNameSei;
    }

    /** 
     * Returns the accountNameSeiKana.
     * 
     * @return the accountNameSeiKana
     */
    public String getAccountNameSeiKana() {
        return accountNameSeiKana;
    }

    /** 
     * Sets the accountNameSeiKana.
     * 
     * @param accountNameSeiKana the accountNameSeiKana
     */
    public void setAccountNameSeiKana(String accountNameSeiKana) {
        this.accountNameSeiKana = accountNameSeiKana;
    }

    /** 
     * Returns the accountNameMei.
     * 
     * @return the accountNameMei
     */
    public String getAccountNameMei() {
        return accountNameMei;
    }

    /** 
     * Sets the accountNameMei.
     * 
     * @param accountNameMei the accountNameMei
     */
    public void setAccountNameMei(String accountNameMei) {
        this.accountNameMei = accountNameMei;
    }

    /** 
     * Returns the accountNameMeiKana.
     * 
     * @return the accountNameMeiKana
     */
    public String getAccountNameMeiKana() {
        return accountNameMeiKana;
    }

    /** 
     * Sets the accountNameMeiKana.
     * 
     * @param accountNameMeiKana the accountNameMeiKana
     */
    public void setAccountNameMeiKana(String accountNameMeiKana) {
        this.accountNameMeiKana = accountNameMeiKana;
    }

    /** 
     * Returns the accountType.
     * 
     * @return the accountType
     */
    public String getAccountType() {
        return accountType;
    }

    /** 
     * Sets the accountType.
     * 
     * @param accountType the accountType
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /** 
     * Returns the accountStatus.
     * 
     * @return the accountStatus
     */
    public String getAccountStatus() {
        return accountStatus;
    }

    /** 
     * Sets the accountStatus.
     * 
     * @param accountStatus the accountStatus
     */
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    /** 
     * Returns the accountMailAddress.
     * 
     * @return the accountMailAddress
     */
    public String getAccountMailAddress() {
        return accountMailAddress;
    }

    /** 
     * Sets the accountMailAddress.
     * 
     * @param accountMailAddress the accountMailAddress
     */
    public void setAccountMailAddress(String accountMailAddress) {
        this.accountMailAddress = accountMailAddress;
    }

    /** 
     * Returns the accountKengen.
     * 
     * @return the accountKengen
     */
    public String getAccountKengen() {
        return accountKengen;
    }

    /** 
     * Sets the accountKengen.
     * 
     * @param accountKengen the accountKengen
     */
    public void setAccountKengen(String accountKengen) {
        this.accountKengen = accountKengen;
    }

    /** 
     * Returns the accountOwnerFlg.
     * 
     * @return the accountOwnerFlg
     */
    public String getAccountOwnerFlg() {
        return accountOwnerFlg;
    }

    /** 
     * Sets the accountOwnerFlg.
     * 
     * @param accountOwnerFlg the accountOwnerFlg
     */
    public void setAccountOwnerFlg(String accountOwnerFlg) {
        this.accountOwnerFlg = accountOwnerFlg;
    }

    /** 
     * Returns the accountColor.
     * 
     * @return the accountColor
     */
    public String getAccountColor() {
        return accountColor;
    }

    /** 
     * Sets the accountColor.
     * 
     * @param accountColor the accountColor
     */
    public void setAccountColor(String accountColor) {
        this.accountColor = accountColor;
    }

    /** 
     * Returns the accountInvoiceRegistrationNo.
     * 
     * @return the accountInvoiceRegistrationNo
     */
    public String getAccountInvoiceRegistrationNo() {
        return accountInvoiceRegistrationNo;
    }

    /** 
     * Sets the accountInvoiceRegistrationNo.
     * 
     * @param accountInvoiceRegistrationNo the accountInvoiceRegistrationNo
     */
    public void setAccountInvoiceRegistrationNo(String accountInvoiceRegistrationNo) {
        this.accountInvoiceRegistrationNo = accountInvoiceRegistrationNo;
    }

    /** 
     * Returns the accountLawyerStampImgSeq.
     * 
     * @return the accountLawyerStampImgSeq
     */
    public Long getAccountLawyerStampImgSeq() {
        return accountLawyerStampImgSeq;
    }

    /** 
     * Sets the accountLawyerStampImgSeq.
     * 
     * @param accountLawyerStampImgSeq the accountLawyerStampImgSeq
     */
    public void setAccountLawyerStampImgSeq(Long accountLawyerStampImgSeq) {
        this.accountLawyerStampImgSeq = accountLawyerStampImgSeq;
    }

    /** 
     * Returns the accountLawyerStampPrintFlg.
     * 
     * @return the accountLawyerStampPrintFlg
     */
    public String getAccountLawyerStampPrintFlg() {
        return accountLawyerStampPrintFlg;
    }

    /** 
     * Sets the accountLawyerStampPrintFlg.
     * 
     * @param accountLawyerStampPrintFlg the accountLawyerStampPrintFlg
     */
    public void setAccountLawyerStampPrintFlg(String accountLawyerStampPrintFlg) {
        this.accountLawyerStampPrintFlg = accountLawyerStampPrintFlg;
    }

    /** 
     * Returns the accountMailSignature.
     * 
     * @return the accountMailSignature
     */
    public String getAccountMailSignature() {
        return accountMailSignature;
    }

    /** 
     * Sets the accountMailSignature.
     * 
     * @param accountMailSignature the accountMailSignature
     */
    public void setAccountMailSignature(String accountMailSignature) {
        this.accountMailSignature = accountMailSignature;
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
     * Returns the deletedAt.
     * 
     * @return the deletedAt
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /** 
     * Sets the deletedAt.
     * 
     * @param deletedAt the deletedAt
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /** 
     * Returns the deletedBy.
     * 
     * @return the deletedBy
     */
    public Long getDeletedBy() {
        return deletedBy;
    }

    /** 
     * Sets the deletedBy.
     * 
     * @param deletedBy the deletedBy
     */
    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
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