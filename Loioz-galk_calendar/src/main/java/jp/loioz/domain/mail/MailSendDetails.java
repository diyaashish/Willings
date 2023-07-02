package jp.loioz.domain.mail;

import java.time.LocalDateTime;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MailConstants.MailType;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.domain.mail.builder.AbstractMailBuilder;
import lombok.Data;

@Data
public class MailSendDetails {

	/**
	 * メール送信情報
	 **/
	/** メールID */
	private String mailId;

	/** メール名 */
	private String mailName;

	/** メール形式 */
	private String mailType;

	/** メール形式(送る形式) */
	private String mailTypeSend;

	/** 送信元表示名(From) */
	private String sendFromName;

	/** 送信元(From) */
	private InternetAddress sendFrom;

	/** 返信先 */
	private InternetAddress replyTo;

	/** 送信先(to) */
	private List<InternetAddress> sendToList;

	/** 送信先(cc) */
	private List<InternetAddress> sendCcList;

	/** 送信先(bcc) */
	private List<InternetAddress> sendBccList;

	/** メールタイトル */
	private String mailTitle;

	/** メール本文 */
	private String mailBody;

	/** 添付ファイル名 */
	private String fileName;

	/** ファイルバイト */
	private byte[] fileContents;

	/** 送信日時 */
	private LocalDateTime sendDt;

	/** メール成功可否 */
	private String sendStatus;

	/** メール送信成功 */
	private Address[] sentAddresses;
	/** メール送信失敗 */
	private Address[] invalidAddresses;
	/** メール送信失敗 */
	private Address[] unsentAddresses;

	/**
	 * メール送信情報を設定する
	 * 
	 * @param builder
	 */
	public void setMailInfo(AbstractMailBuilder builder) {
		this.mailId = builder.getMailId();
		this.mailName = builder.getMailName();
		this.mailType = builder.getMailType();
		this.mailTypeSend = DefaultEnum.getEnum(MailType.class, builder.getMailType()).getVal();
		this.sendFromName = builder.getWorkFromName();
		this.sendFrom = CommonUtils.convertAddress(builder.getWorkFrom(), builder.getWorkFromName());
		this.replyTo = CommonUtils.convertAddress(builder.getWorkReplyTo());
		this.sendToList = CommonUtils.convertAddressList(builder.getWorkTo(), builder.getWorkToNameMap());
		this.sendCcList = CommonUtils.convertAddressList(builder.getWorkCc());
		this.sendBccList = CommonUtils.convertAddressList(builder.getWorkBcc());
		this.mailTitle = builder.getWorkTitle();
		this.mailBody = builder.getWorkBody();
		this.fileName = builder.getWorkFileName();
		this.fileContents = builder.getWorkFileContents();
	}

	/**
	 * 添付ファイルが存在する場合
	 * 
	 * @return
	 */
	public boolean existsSendFile() {
		if (this.fileContents == null) {
			return false;
		}
		return true;
	}

}
