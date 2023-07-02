package jp.loioz.common.service.mail;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.MailConstants.MailSendStatus;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.config.MailSendConfig;
import jp.loioz.domain.mail.MailSendDetails;

/**
 * メール送信サービスクラス
 */
@Service
public class MailSendService extends DefaultService {

	/** ロガークラス */
	@Autowired
	Logger logger;

	/** SMTP情報 */
	@Autowired
	MailSendConfig mailSendConfig;

	/**
	 * メール送信
	 * 
	 * @param mailSendDetails
	 * @return
	 * @throws MessagingException
	 */
	public void sendMail(MailSendDetails mailSendDetails) throws MessagingException {
		// 送信処理中のステータスを予め設定
		mailSendDetails.setSendStatus(MailSendStatus.SEND_NG99.getCd());

		// SMTPの設定を行う
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", mailSendConfig.getProtocol());
		props.put("mail.smtp.port", mailSendConfig.getPort());
		props.put("mail.smtp.starttls.enable", mailSendConfig.getEnable());
		props.put("mail.smtp.auth", mailSendConfig.getAuth());
		props.put("mail.smtp.connectiontimeout", mailSendConfig.getConnectiontimeout());
		props.put("mail.smtp.timeout", mailSendConfig.getTimeout());

		// メール送信設定情報を保持するセッションを生成
		Session session = Session.getDefaultInstance(props);

		// セッションからメッセージを生成
		MimeMessage msg = new MimeMessage(session);

		// 送信元メールアドレス
		msg.setFrom(mailSendDetails.getSendFrom());
		if (mailSendDetails.getReplyTo() != null && !StringUtils.isEmpty(mailSendDetails.getReplyTo().getAddress())) {
			// 返信先が存在する場合、その値を設定
			msg.setReplyTo(new Address[]{mailSendDetails.getReplyTo()});
		} else {
			// 返信先が存在しない場合、送信元を設定
			msg.setReplyTo(new Address[]{mailSendDetails.getSendFrom()});
		}

		// 送信先のメールアドレス設定
		// TO
		msg.setRecipients(Message.RecipientType.TO, CommonUtils.addressListToArray(mailSendDetails.getSendToList()));
		// CC
		msg.setRecipients(Message.RecipientType.CC, CommonUtils.addressListToArray(mailSendDetails.getSendCcList()));
		// BCC
		msg.setRecipients(Message.RecipientType.BCC, CommonUtils.addressListToArray(mailSendDetails.getSendBccList()));

		// 件名
		msg.setSubject(mailSendDetails.getMailTitle());

		if (mailSendDetails.existsSendFile()) {
			// 添付ファイルが存在する場合
			final MimeMultipart multipart = new MimeMultipart("mixed");

			// 本文の設定
			final MimeBodyPart attPart1 = new MimeBodyPart();
			attPart1.setText(mailSendDetails.getMailBody(), "utf-8", mailSendDetails.getMailTypeSend());

			String fileName;
			try {
				fileName = MimeUtility.encodeText(mailSendDetails.getFileName());
			} catch (UnsupportedEncodingException ex) {
				logger.error(CommonUtils.getCurrentExecuteClassAndMethodName() + " メール送信に失敗しました", ex);
				throw new MessagingException("添付ファイル名のエンコードに失敗しました。", ex);
			}

			// 添付ファイルの設定
			final MimeBodyPart attPart2 = new MimeBodyPart();
			DataSource dataSource = new ByteArrayDataSource(mailSendDetails.getFileContents(), "application/octet-stream");
			attPart2.setDataHandler(new DataHandler(dataSource));
			attPart2.setFileName(fileName);
			attPart2.setDescription(MimeBodyPart.ATTACHMENT, "utf-8");

			// コンテンツに設定
			multipart.addBodyPart(attPart1);
			multipart.addBodyPart(attPart2);
			msg.setContent(multipart);

		} else {
			// 本文
			msg.setText(mailSendDetails.getMailBody(), "utf-8", mailSendDetails.getMailTypeSend());
		}

		// 開封確認の設定(AWSの設定次第でコメントアウトを削除)
		// msg.setHeader("X-SES-CONFIGURATION-SET", "benzo-law");
		Transport transport = session.getTransport();

		// 自クラス情報
		String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
		try {
			logger.info(classAndMethodName + "メール送信開始");

			// AWSのSESに接続
			transport.connect(mailSendConfig.getHost(), mailSendConfig.getPort(), mailSendConfig.getUsername(), mailSendConfig.getPassword());

			// 送信時刻設定
			mailSendDetails.setSendDt(LocalDateTime.now());
			// 送信実行
			transport.sendMessage(msg, msg.getAllRecipients());

			// 送信成功
			mailSendDetails.setSendStatus(MailSendStatus.SEND_OK.getCd());
			logger.info(classAndMethodName + "メール送信に成功しました");

		} catch (SendFailedException e) {
			e.printStackTrace();
			mailSendDetails.setSendStatus(MailSendStatus.SEND_NG90.getCd());
			logger.error(classAndMethodName + " メール送信に失敗しました", e);
			// 送信成功メールアドレス
			mailSendDetails.setSentAddresses(e.getValidSentAddresses());
			// 送信失敗メールアドレス
			mailSendDetails.setInvalidAddresses(e.getInvalidAddresses());
			// 送信失敗メールアドレス
			mailSendDetails.setUnsentAddresses(e.getValidUnsentAddresses());

		} catch (MessagingException e) {
			e.printStackTrace();
			mailSendDetails.setSendStatus(MailSendStatus.SEND_NG91.getCd());
			logger.error(classAndMethodName + " メール送信に失敗しました", e);

		} finally {
			// コネクションクローズ
			transport.close();
		}
	}

}
