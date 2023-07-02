package jp.loioz.dto;

import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * メール編集情報
 */
@Data
public class MailEditDto {

	/** メール連番 */
	private Long mailSeq;

	/** メールID */
	@Required
	private String mailId;

	/** メール名 */
	@Required
	private String mailName;

	/** メール形式 */
	@Required
	private String mailType;

	/** 送信元表示名(From) */
	@Required
	private String sendFromName;

	/** 送信元(From) */
	@Required
	@MaxDigit(max = 256)
	@EmailPattern
	private String sendFrom;

	/** 送信先(to) */
	@MaxDigit(max = 256)
	@EmailPattern
	private String sendTo;

	/** 送信先(cc) */
	@MaxDigit(max = 256)
	@EmailPattern
	private String sendCc;

	/** 送信先(bcc) */
	@MaxDigit(max = 256)
	@EmailPattern
	private String sendBcc;

	/** メールタイトル */
	@Required
	private String mailTitle;

	/** メール本文 */
	@Required
	private String mailBody;

	/** バージョンNo */
	private Long versionNo;
}
