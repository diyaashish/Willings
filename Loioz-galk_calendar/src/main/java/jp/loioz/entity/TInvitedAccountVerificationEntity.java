package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 招待アカウント認証
 */
@Entity(listener = TInvitedAccountVerificationEntityListener.class)
@Table(name = "t_invited_account_verification")
public class TInvitedAccountVerificationEntity extends DefaultEntity {

    /** 認証キー */
    @Id
    @Column(name = "verification_key")
    String verificationKey;

    /** メールアドレス */
    @Column(name = "mail_address")
    String mailAddress;

    /** 有効期限 */
    @Column(name = "temp_limit_date")
    LocalDateTime tempLimitDate;

    /** 完了フラグ */
    @Column(name = "complete_flg")
    String completeFlg;

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
     * Returns the verificationKey.
     * 
     * @return the verificationKey
     */
    public String getVerificationKey() {
        return verificationKey;
    }

    /** 
     * Sets the verificationKey.
     * 
     * @param verificationKey the verificationKey
     */
    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }

    /** 
     * Returns the mailAddress.
     * 
     * @return the mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /** 
     * Sets the mailAddress.
     * 
     * @param mailAddress the mailAddress
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /** 
     * Returns the tempLimitDate.
     * 
     * @return the tempLimitDate
     */
    public LocalDateTime getTempLimitDate() {
        return tempLimitDate;
    }

    /** 
     * Sets the tempLimitDate.
     * 
     * @param tempLimitDate the tempLimitDate
     */
    public void setTempLimitDate(LocalDateTime tempLimitDate) {
        this.tempLimitDate = tempLimitDate;
    }

    /** 
     * Returns the completeFlg.
     * 
     * @return the completeFlg
     */
    public String getCompleteFlg() {
        return completeFlg;
    }

    /** 
     * Sets the completeFlg.
     * 
     * @param completeFlg the completeFlg
     */
    public void setCompleteFlg(String completeFlg) {
        this.completeFlg = completeFlg;
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