package jp.loioz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class MailSendConfig {

	/**
	 * SMTP情報
	 **/
	/** プロトコル */
	@Value("${mail.transport.protocol}")
	private String protocol;

	/** ポート */
	@Value("${mail.smtp.port}")
	private int port;

	/** 送信 */
	@Value("${mail.smtp.starttls.enable}")
	private String enable;

	/** 認証 */
	@Value("${mail.smtp.auth}")
	private String auth;

	/** ユーザー名 */
	@Value("${mail.smtp.username}")
	private String username;

	/** パスワード */
	@Value("${mail.smtp.password}")
	private String password;

	/** 開封/到達不能など設定名 */
	@Value("${mail.smtp.configset}")
	private String configset;

	/** ホスト名 */
	@Value("${mail.smtp.host}")
	private String host;

	/** コネクション確立までのタイムアウト時間 */
	@Value("${mail.smtp.connectiontimeout}")
	private String connectiontimeout;

	/** SMTPサーバとの通信（read）のタイムアウト時間 */
	@Value("${mail.smtp.timeout}")
	private String timeout;

}
