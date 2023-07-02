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
 * 伝言
 */
@Entity(listener = TDengonEntityListener.class)
@Table(name = "t_dengon")
public class TDengonEntity extends DefaultEntity {

	/** 伝言SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dengon_seq")
	Long dengonSeq;

	/** 送信者SEQ */
	@Column(name = "send_account_seq")
	Long sendAccountSeq;

	/** 受信者SEQ */
	@Column(name = "receive_account_seq")
	String receiveAccountSeq;

	/** 件名 */
	@Column(name = "title")
	String title;

	/** 本文 */
	@Column(name = "body")
	String body;

	/** 下書き */
	@Column(name = "draft_flg")
	String draftFlg;

	/** 送信者ごみ箱 */
	@Column(name = "send_trashed_flg")
	String sendTrashedFlg;

	/** 登録日 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 登録アカウントSEQ */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新アカウントSEQ */
	@Column(name = "updated_by")
	Long updatedBy;

	/** 削除日 */
	@Column(name = "deleted_at")
	LocalDateTime deletedAt;

	/** 削除アカウントSEQ */
	@Column(name = "deleted_by")
	Long deletedBy;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/** 業務履歴SEQ */
	@Column(name = "gyomu_history_seq")
	Long gyomuHistorySeq;

	/**
	 * Returns the dengonSeq.
	 *
	 * @return the dengonSeq
	 */
	public Long getDengonSeq() {
		return dengonSeq;
	}

	/**
	 * Sets the dengonSeq.
	 *
	 * @param dengonSeq the dengonSeq
	 */
	public void setDengonSeq(Long dengonSeq) {
		this.dengonSeq = dengonSeq;
	}

	/**
	 * Returns the sendAccountSeq.
	 *
	 * @return the sendAccountSeq
	 */
	public Long getSendAccountSeq() {
		return sendAccountSeq;
	}

	/**
	 * Sets the sendAccountSeq.
	 *
	 * @param sendAccountSeq the sendAccountSeq
	 */
	public void setSendAccountSeq(Long sendAccountSeq) {
		this.sendAccountSeq = sendAccountSeq;
	}

	/**
	 * Returns the receiveAccountSeq.
	 *
	 * @return the receiveAccountSeq
	 */
	public String getReceiveAccountSeq() {
		return receiveAccountSeq;
	}

	/**
	 * Sets the receiveAccountSeq.
	 *
	 * @param receiveAccountSeq the receiveAccountSeq
	 */
	public void setReceiveAccountSeq(String receiveAccountSeq) {
		this.receiveAccountSeq = receiveAccountSeq;
	}

	/**
	 * Returns the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the body.
	 *
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 *
	 * @param body the body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Returns the draftFlg.
	 *
	 * @return the draftFlg
	 */
	public String getDraftFlg() {
		return draftFlg;
	}

	/**
	 * Sets the draftFlg.
	 *
	 * @param draftFlg the draftFlg
	 */
	public void setDraftFlg(String draftFlg) {
		this.draftFlg = draftFlg;
	}

	/**
	 * Returns the sendTrashedFlg.
	 *
	 * @return the sendTrashedFlg
	 */
	public String getSendTrashedFlg() {
		return sendTrashedFlg;
	}

	/**
	 * Sets the sendTrashedFlg.
	 *
	 * @param sendTrashedFlg the sendTrashedFlg
	 */
	public void setSendTrashedFlg(String sendTrashedFlg) {
		this.sendTrashedFlg = sendTrashedFlg;
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

	/**
	 * Returns the gyomuHistorySeq.
	 *
	 * @return the gyomuHistorySeq
	 */
	public Long getGyomuHistorySeq() {
		return gyomuHistorySeq;
	}

	/**
	 * Sets the gyomuHistorySeq.
	 *
	 * @param gyomuHistorySeq the gyomuHistorySeq
	 */
	public void setGyomuHistorySeq(Long gyomuHistorySeq) {
		this.gyomuHistorySeq = gyomuHistorySeq;
	}
}