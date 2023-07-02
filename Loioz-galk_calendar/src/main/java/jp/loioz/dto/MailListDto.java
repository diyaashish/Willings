package jp.loioz.dto;

import lombok.Data;

/**
 * メール一覧情報
 */
@Data
public class MailListDto {

	/** メール連番 */
	private Long mailSeq;

	/** メールID */
	private String mailId;

	/** メール名 */
	private String mailName;

	/** メール形式 */
	private String mailType;

	/** 送信元表示名(From) */
	private String sendFromName;

	/** 送信元(From) */
	private String sendFrom;

	/** 送信先(to) */
	private String sendTo;

	/** 送信先(cc) */
	private String sendCc;

	/** 送信先(bcc) */
	private String sendBcc;

	/** メールタイトル */
	private String mailTitle;

	/** メール本文 */
	private String mailBody;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** 削除日時 */
	private String deletedAt;

	/** バージョンNo */
	private Long versionNo;
}
