package jp.loioz.domain.mail.builder;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 契約内容の変更完了メールBuilder
 *
 */
@Component
public class M0006MailBuilder extends AbstractMailBuilder {

	private static final long serialVersionUID = 1L;

	@Override
	public void makeAddressFrom(String from, String fromName) {
	}

	@Override
	public void makeAddressTo(List<String> mailAddrlist) {
		this.workTo = mailAddrlist;
	}

	@Override
	public void makeAddressCc(List<String> mailAddrlist) {
		// this.workCc = mailAddrlist;
	}

	@Override
	public void makeAddressBcc(List<String> mailAddrlist) {
		// this.workBcc = mailAddrlist;
	}

	/*
	 * (非 Javadoc)
	 * メールテンプレートに置換文字の分、引数に値を渡す
	 * メール本文に{0} {1}...を置換する
	 * 
	 * @see jp.loioz.domain.mail.builder.AbstractMailBuilder#makeTitle(java.lang.String[])
	 */
	@Override
	public void makeTitle(String... args) {
		// this.workTitle = java.text.MessageFormat.format(this.workTitle, args[0]);
	}

	/*
	 * (非 Javadoc)
	 * メールテンプレートに置換文字の分、引数に値を渡す
	 * メール本文に{0} {1}...を置換する
	 * 
	 * @see jp.loioz.domain.mail.builder.AbstractMailBuilder#makeBody(java.lang.String[])
	 */
	@Override
	public void makeBody(String... args) {
		// 置換文字が11個
		this.workBody = java.text.MessageFormat.format(this.workBody, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
	}
}