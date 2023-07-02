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
 * メール設定
 */
@Entity(listener = MMailSettingEntityListener.class)
@Table(name = "m_mail_setting")
public class MMailSettingEntity extends DefaultEntity {

    /** メール設定SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_setting_seq")
    Long mailSettingSeq;

    /** ダウンロード可能日数 */
    @Column(name = "download_day_count")
    Long downloadDayCount;

    /** ダウンロード画面パスワード有効フラグ */
    @Column(name = "download_view_password_enable_flg")
    String downloadViewPasswordEnableFlg;

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
     * Returns the mailSettingSeq.
     * 
     * @return the mailSettingSeq
     */
    public Long getMailSettingSeq() {
        return mailSettingSeq;
    }

    /** 
     * Sets the mailSettingSeq.
     * 
     * @param mailSettingSeq the mailSettingSeq
     */
    public void setMailSettingSeq(Long mailSettingSeq) {
        this.mailSettingSeq = mailSettingSeq;
    }

    /** 
     * Returns the downloadDayCount.
     * 
     * @return the downloadDayCount
     */
    public Long getDownloadDayCount() {
        return downloadDayCount;
    }

    /** 
     * Sets the downloadDayCount.
     * 
     * @param downloadDayCount the downloadDayCount
     */
    public void setDownloadDayCount(Long downloadDayCount) {
        this.downloadDayCount = downloadDayCount;
    }

    /** 
     * Returns the downloadViewPasswordEnableFlg.
     * 
     * @return the downloadViewPasswordEnableFlg
     */
    public String getDownloadViewPasswordEnableFlg() {
        return downloadViewPasswordEnableFlg;
    }

    /** 
     * Sets the downloadViewPasswordEnableFlg.
     * 
     * @param downloadViewPasswordEnableFlg the downloadViewPasswordEnableFlg
     */
    public void setDownloadViewPasswordEnableFlg(String downloadViewPasswordEnableFlg) {
        this.downloadViewPasswordEnableFlg = downloadViewPasswordEnableFlg;
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