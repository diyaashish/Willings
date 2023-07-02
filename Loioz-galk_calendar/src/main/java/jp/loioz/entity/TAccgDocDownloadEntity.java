package jp.loioz.entity;

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
 * 会計書類-ダウンロード情報
 */
@Entity(listener = TAccgDocDownloadEntityListener.class)
@Table(name = "t_accg_doc_download")
public class TAccgDocDownloadEntity extends DefaultEntity {

    /** 会計書類ダウンロード情報SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_doc_download_seq")
    Long accgDocDownloadSeq;

    /** 会計書類対応送付SEQ */
    @Column(name = "accg_doc_act_send_seq")
    Long accgDocActSendSeq;

    /** ダウンロード画面URLキー */
    @Column(name = "download_view_url_key")
    String downloadViewUrlKey;

    /** ダウンロード画面パスワード */
    @Column(name = "download_view_password")
    String downloadViewPassword;

    /** 検証トークン */
    @Column(name = "verification_token")
    String verificationToken;

    /** 事務所名（差出人名） */
    @Column(name = "tenant_name")
    String tenantName;

    /** 宛先メールアドレス */
    @Column(name = "send_to")
    String sendTo;

    /** 発行日 */
    @Column(name = "issue_date")
    LocalDate issueDate;

    /** ダウンロード期限 */
    @Column(name = "download_limit_date")
    LocalDate downloadLimitDate;

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
     * Returns the accgDocDownloadSeq.
     * 
     * @return the accgDocDownloadSeq
     */
    public Long getAccgDocDownloadSeq() {
        return accgDocDownloadSeq;
    }

    /** 
     * Sets the accgDocDownloadSeq.
     * 
     * @param accgDocDownloadSeq the accgDocDownloadSeq
     */
    public void setAccgDocDownloadSeq(Long accgDocDownloadSeq) {
        this.accgDocDownloadSeq = accgDocDownloadSeq;
    }

    /** 
     * Returns the accgDocActSendSeq.
     * 
     * @return the accgDocActSendSeq
     */
    public Long getAccgDocActSendSeq() {
        return accgDocActSendSeq;
    }

    /** 
     * Sets the accgDocActSendSeq.
     * 
     * @param accgDocActSendSeq the accgDocActSendSeq
     */
    public void setAccgDocActSendSeq(Long accgDocActSendSeq) {
        this.accgDocActSendSeq = accgDocActSendSeq;
    }

    /** 
     * Returns the downloadViewUrlKey.
     * 
     * @return the downloadViewUrlKey
     */
    public String getDownloadViewUrlKey() {
        return downloadViewUrlKey;
    }

    /** 
     * Sets the downloadViewUrlKey.
     * 
     * @param downloadViewUrlKey the downloadViewUrlKey
     */
    public void setDownloadViewUrlKey(String downloadViewUrlKey) {
        this.downloadViewUrlKey = downloadViewUrlKey;
    }

    /** 
     * Returns the downloadViewPassword.
     * 
     * @return the downloadViewPassword
     */
    public String getDownloadViewPassword() {
        return downloadViewPassword;
    }

    /** 
     * Sets the downloadViewPassword.
     * 
     * @param downloadViewPassword the downloadViewPassword
     */
    public void setDownloadViewPassword(String downloadViewPassword) {
        this.downloadViewPassword = downloadViewPassword;
    }

    /** 
     * Returns the verificationToken.
     * 
     * @return the verificationToken
     */
    public String getVerificationToken() {
        return verificationToken;
    }

    /** 
     * Sets the verificationToken.
     * 
     * @param verificationToken the verificationToken
     */
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    /** 
     * Returns the tenantName.
     * 
     * @return the tenantName
     */
    public String getTenantName() {
        return tenantName;
    }

    /** 
     * Sets the tenantName.
     * 
     * @param tenantName the tenantName
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /** 
     * Returns the sendTo.
     * 
     * @return the sendTo
     */
    public String getSendTo() {
        return sendTo;
    }

    /** 
     * Sets the sendTo.
     * 
     * @param sendTo the sendTo
     */
    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    /** 
     * Returns the issueDate.
     * 
     * @return the issueDate
     */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /** 
     * Sets the issueDate.
     * 
     * @param issueDate the issueDate
     */
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    /** 
     * Returns the downloadLimitDate.
     * 
     * @return the downloadLimitDate
     */
    public LocalDate getDownloadLimitDate() {
        return downloadLimitDate;
    }

    /** 
     * Sets the downloadLimitDate.
     * 
     * @param downloadLimitDate the downloadLimitDate
     */
    public void setDownloadLimitDate(LocalDate downloadLimitDate) {
        this.downloadLimitDate = downloadLimitDate;
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