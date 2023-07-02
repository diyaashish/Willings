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
 * 会計書類-対応-送付
 */
@Entity(listener = TAccgDocActSendEntityListener.class)
@Table(name = "t_accg_doc_act_send")
public class TAccgDocActSendEntity extends DefaultEntity {

    /** 会計書類対応送付SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_doc_act_send_seq")
    Long accgDocActSendSeq;

    /** 会計書類対応SEQ */
    @Column(name = "accg_doc_act_seq")
    Long accgDocActSeq;

    /** 送付種別 */
    @Column(name = "send_type")
    String sendType;

    /** TO */
    @Column(name = "send_to")
    String sendTo;

    /** CC */
    @Column(name = "send_cc")
    String sendCc;

    /** BCC */
    @Column(name = "send_bcc")
    String sendBcc;

    /** REPLY-TO */
    @Column(name = "reply_to")
    String replyTo;

    /** 送信元名 */
    @Column(name = "send_from_name")
    String sendFromName;

    /** 件名 */
    @Column(name = "send_subject")
    String sendSubject;

    /** 本文 */
    @Column(name = "send_body")
    String sendBody;

    /** ダウンロード期限 */
    @Column(name = "download_limit_date")
    LocalDate downloadLimitDate;

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
     * Returns the accgDocActSeq.
     * 
     * @return the accgDocActSeq
     */
    public Long getAccgDocActSeq() {
        return accgDocActSeq;
    }

    /** 
     * Sets the accgDocActSeq.
     * 
     * @param accgDocActSeq the accgDocActSeq
     */
    public void setAccgDocActSeq(Long accgDocActSeq) {
        this.accgDocActSeq = accgDocActSeq;
    }

    /** 
     * Returns the sendType.
     * 
     * @return the sendType
     */
    public String getSendType() {
        return sendType;
    }

    /** 
     * Sets the sendType.
     * 
     * @param sendType the sendType
     */
    public void setSendType(String sendType) {
        this.sendType = sendType;
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
     * Returns the sendCc.
     * 
     * @return the sendCc
     */
    public String getSendCc() {
        return sendCc;
    }

    /** 
     * Sets the sendCc.
     * 
     * @param sendCc the sendCc
     */
    public void setSendCc(String sendCc) {
        this.sendCc = sendCc;
    }

    /** 
     * Returns the sendBcc.
     * 
     * @return the sendBcc
     */
    public String getSendBcc() {
        return sendBcc;
    }

    /** 
     * Sets the sendBcc.
     * 
     * @param sendBcc the sendBcc
     */
    public void setSendBcc(String sendBcc) {
        this.sendBcc = sendBcc;
    }

    /** 
     * Returns the replyTo.
     * 
     * @return the replyTo
     */
    public String getReplyTo() {
        return replyTo;
    }

    /** 
     * Sets the replyTo.
     * 
     * @param replyTo the replyTo
     */
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    /** 
     * Returns the sendFromName.
     * 
     * @return the sendFromName
     */
    public String getSendFromName() {
        return sendFromName;
    }

    /** 
     * Sets the sendFromName.
     * 
     * @param sendFromName the sendFromName
     */
    public void setSendFromName(String sendFromName) {
        this.sendFromName = sendFromName;
    }

    /** 
     * Returns the sendSubject.
     * 
     * @return the sendSubject
     */
    public String getSendSubject() {
        return sendSubject;
    }

    /** 
     * Sets the sendSubject.
     * 
     * @param sendSubject the sendSubject
     */
    public void setSendSubject(String sendSubject) {
        this.sendSubject = sendSubject;
    }

    /** 
     * Returns the sendBody.
     * 
     * @return the sendBody
     */
    public String getSendBody() {
        return sendBody;
    }

    /** 
     * Sets the sendBody.
     * 
     * @param sendBody the sendBody
     */
    public void setSendBody(String sendBody) {
        this.sendBody = sendBody;
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