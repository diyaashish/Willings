package jp.loioz.domain.mail.builder;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 会計書類送付 メール送付 顧客宛
 *
 */
@Component
public class M0015MailBuilder extends AbstractMailBuilder {

	private static final long serialVersionUID = 1L;

	@Override
	public void makeAddressFrom(String from, String fromName) {
	}

	@Override
	public void makeAddressTo(List<String> mailAddrlist) {
	}

	@Override
	public void makeAddressCc(List<String> mailAddrlist) {
	}

	@Override
	public void makeAddressBcc(List<String> mailAddrlist) {
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
		this.workTitle = java.text.MessageFormat.format(this.workTitle, args[0]);
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
		this.workBody = java.text.MessageFormat.format(this.workBody, args[0], args[1]);
	}

}
