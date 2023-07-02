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
 * メールテンプレートマスタ
 */
@Entity(listener = MMailTemplateEntityListener.class)
@Table(name = "m_mail_template")
public class MMailTemplateEntity extends DefaultEntity {

    /** メールテンプレートSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_template_seq")
    Long mailTemplateSeq;

    /** テンプレート種別 */
    @Column(name = "template_type")
    String templateType;

    /** テンプレートタイトル */
    @Column(name = "template_title")
    String templateTitle;

    /** CC */
    @Column(name = "mail_cc")
    String mailCc;

    /** BCC */
    @Column(name = "mail_bcc")
    String mailBcc;

    /** REPLY-TO */
    @Column(name = "mail_reply_to")
    String mailReplyTo;

    /** 件名 */
    @Column(name = "subject")
    String subject;

    /** 本文 */
    @Column(name = "contents")
    String contents;

    /** 既定利用フラグ */
    @Column(name = "default_use_flg")
    String defaultUseFlg;

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
     * Returns the mailTemplateSeq.
     * 
     * @return the mailTemplateSeq
     */
    public Long getMailTemplateSeq() {
        return mailTemplateSeq;
    }

    /** 
     * Sets the mailTemplateSeq.
     * 
     * @param mailTemplateSeq the mailTemplateSeq
     */
    public void setMailTemplateSeq(Long mailTemplateSeq) {
        this.mailTemplateSeq = mailTemplateSeq;
    }

    /** 
     * Returns the templateType.
     * 
     * @return the templateType
     */
    public String getTemplateType() {
        return templateType;
    }

    /** 
     * Sets the templateType.
     * 
     * @param templateType the templateType
     */
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    /** 
     * Returns the templateTitle.
     * 
     * @return the templateTitle
     */
    public String getTemplateTitle() {
        return templateTitle;
    }

    /** 
     * Sets the templateTitle.
     * 
     * @param templateTitle the templateTitle
     */
    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    /** 
     * Returns the mailCc.
     * 
     * @return the mailCc
     */
    public String getMailCc() {
        return mailCc;
    }

    /** 
     * Sets the mailCc.
     * 
     * @param mailCc the mailCc
     */
    public void setMailCc(String mailCc) {
        this.mailCc = mailCc;
    }

    /** 
     * Returns the mailBcc.
     * 
     * @return the mailBcc
     */
    public String getMailBcc() {
        return mailBcc;
    }

    /** 
     * Sets the mailBcc.
     * 
     * @param mailBcc the mailBcc
     */
    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
    }

    /** 
     * Returns the mailReplyTo.
     * 
     * @return the mailReplyTo
     */
    public String getMailReplyTo() {
        return mailReplyTo;
    }

    /** 
     * Sets the mailReplyTo.
     * 
     * @param mailReplyTo the mailReplyTo
     */
    public void setMailReplyTo(String mailReplyTo) {
        this.mailReplyTo = mailReplyTo;
    }

    /** 
     * Returns the subject.
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /** 
     * Sets the subject.
     * 
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** 
     * Returns the contents.
     * 
     * @return the contents
     */
    public String getContents() {
        return contents;
    }

    /** 
     * Sets the contents.
     * 
     * @param contents the contents
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    /** 
     * Returns the defaultUseFlg.
     * 
     * @return the defaultUseFlg
     */
    public String getDefaultUseFlg() {
        return defaultUseFlg;
    }

    /** 
     * Sets the defaultUseFlg.
     * 
     * @param defaultUseFlg the defaultUseFlg
     */
    public void setDefaultUseFlg(String defaultUseFlg) {
        this.defaultUseFlg = defaultUseFlg;
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