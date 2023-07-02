package jp.loioz.domain.mail.builder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;

import groovy.util.logging.Slf4j;
import jp.loioz.common.log.Logger;
import lombok.Data;

/**
 * メールBuilder
 *
 */
@Data
@Slf4j
public abstract class AbstractMailBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	/** ロガークラス */
	@Autowired
	Logger logger;

	/**
	 * 作成中のメールID
	 */
	protected String mailId;

	/**
	 * 作成中のメール名
	 */
	protected String mailName;

	/**
	 * 作成中のメール形式
	 */
	protected String mailType;

	/**
	 * 作成中の送信元
	 */
	protected String workFrom;

	/**
	 * 作成中の送信元
	 */
	protected String workFromName;

	/**
	 * 作成中の返信先
	 */
	protected String workReplyTo;

	/** 送信先名Map (key:workToのmailAddress) */
	protected Map<String, String> workToNameMap;

	/**
	 * 作成中の送信先（TO）
	 */
	protected List<String> workTo;

	/**
	 * 作成中の送信先（CC）
	 */
	protected List<String> workCc;

	/**
	 * 作成中の送信先（BCC）
	 */
	protected List<String> workBcc;

	/**
	 * 作成中の件名
	 */
	protected String workTitle;

	/**
	 * 作成中のメール本文
	 */
	protected String workBody;

	/**
	 * 作成中の添付ファイル名
	 */
	protected String workFileName;

	/**
	 * 作成中の添付ファイル
	 */
	protected byte[] workFileContents;

	// ------------------------------------------------
	// publicメソッド
	// ------------------------------------------------

	/**
	 * 送信元を作成
	 * 
	 * @param from
	 * @param fromName
	 */
	public abstract void makeAddressFrom(String from, String fromName);

	/**
	 * 送信先（TO）を作成
	 *
	 * @throws AddressException アドレス変換例外
	 */
	public abstract void makeAddressTo(List<String> mailAddrlist);

	/**
	 * 送信先（CC）を作成
	 *
	 * @throws AddressException アドレス変換例外
	 */
	public abstract void makeAddressCc(List<String> mailAddrlist);

	/**
	 * 送信先（BCC）を作成
	 *
	 * @throws AddressException アドレス変換例外
	 */
	public abstract void makeAddressBcc(List<String> mailAddrlist);

	/**
	 * 件名を作成
	 */
	public abstract void makeTitle(String... args);

	/**
	 * メール本文を作成
	 */
	public abstract void makeBody(String... args);

}
