package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 
 */
@Entity(listener = TMailSendHistoryEntityListener.class)
@Table(name = "t_mail_send_history")
public class TMailSendHistoryEntity extends DefaultEntity {

	/** メール送信履歴連番 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mail_send_history_seq")
	Long mailSendHistorySeq;

	/** メールID */
	@Column(name = "mail_id")
	String mailId;

	/** メール名 */
	@Column(name = "mail_name")
	String mailName;

	/** メール形式 */
	@Column(name = "mail_type")
	String mailType;

	/** 送信元表示名(From) */
	@Column(name = "send_from_name")
	String sendFromName;

	/** 送信元(From) */
	@Column(name = "send_from")
	String sendFrom;

	/** 送信先(to) */
	@Column(name = "send_to")
	String sendTo;

	/** 送信先(cc) */
	@Column(name = "send_cc")
	String sendCc;

	/** 送信先(bcc) */
	@Column(name = "send_bcc")
	String sendBcc;

	/** メールタイトル */
	@Column(name = "mail_title")
	String mailTitle;

	/** メール本文 */
	@Column(name = "mail_body")
	String mailBody;

	/** メール送信ステータス */
	@Column(name = "send_status")
	String sendStatus;

	/** メール送信日時 */
	@Column(name = "send_dt")
	LocalDateTime sendDt;

	/** 作成日時 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 作成者ID */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日時 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新者ID */
	@Column(name = "updated_by")
	Long updatedBy;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the mailSendHistorySeq.
	 * 
	 * @return the mailSendHistorySeq
	 */
	public Long getMailSendHistorySeq() {
		return mailSendHistorySeq;
	}

	/**
	 * Sets the mailSendHistorySeq.
	 * 
	 * @param mailSendHistorySeq the mailSendHistorySeq
	 */
	public void setMailSendHistorySeq(Long mailSendHistorySeq) {
		this.mailSendHistorySeq = mailSendHistorySeq;
	}

	/**
	 * Returns the mailId.
	 * 
	 * @return the mailId
	 */
	public String getMailId() {
		return mailId;
	}

	/**
	 * Sets the mailId.
	 * 
	 * @param mailId the mailId
	 */
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	/**
	 * Returns the mailName.
	 * 
	 * @return the mailName
	 */
	public String getMailName() {
		return mailName;
	}

	/**
	 * Sets the mailName.
	 * 
	 * @param mailName the mailName
	 */
	public void setMailName(String mailName) {
		this.mailName = mailName;
	}

	/**
	 * Returns the mailType.
	 * 
	 * @return the mailType
	 */
	public String getMailType() {
		return mailType;
	}

	/**
	 * Sets the mailType.
	 * 
	 * @param mailType the mailType
	 */
	public void setMailType(String mailType) {
		this.mailType = mailType;
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
	 * Returns the sendFrom.
	 * 
	 * @return the sendFrom
	 */
	public String getSendFrom() {
		return sendFrom;
	}

	/**
	 * Sets the sendFrom.
	 * 
	 * @param sendFrom the sendFrom
	 */
	public void setSendFrom(String sendFrom) {
		this.sendFrom = sendFrom;
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
	 * Returns the mailTitle.
	 * 
	 * @return the mailTitle
	 */
	public String getMailTitle() {
		return mailTitle;
	}

	/**
	 * Sets the mailTitle.
	 * 
	 * @param mailTitle the mailTitle
	 */
	public void setMailTitle(String mailTitle) {
		this.mailTitle = mailTitle;
	}

	/**
	 * Returns the mailBody.
	 * 
	 * @return the mailBody
	 */
	public String getMailBody() {
		return mailBody;
	}

	/**
	 * Sets the mailBody.
	 * 
	 * @param mailBody the mailBody
	 */
	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	/**
	 * Returns the sendStatus.
	 * 
	 * @return the sendStatus
	 */
	public String getSendStatus() {
		return sendStatus;
	}

	/**
	 * Sets the sendStatus.
	 * 
	 * @param sendStatus the sendStatus
	 */
	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}

	/**
	 * Returns the sendDt.
	 * 
	 * @return the sendDt
	 */
	public LocalDateTime getSendDt() {
		return sendDt;
	}

	/**
	 * Sets the sendDt.
	 * 
	 * @param sendDt the sendDt
	 */
	public void setSendDt(LocalDateTime sendDt) {
		this.sendDt = sendDt;
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