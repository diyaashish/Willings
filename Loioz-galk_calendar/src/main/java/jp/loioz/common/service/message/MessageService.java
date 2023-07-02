package jp.loioz.common.service.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.message.MessageManager;

/**
 * メッセージを取得するサービスクラス
 */
@Service
public class MessageService extends DefaultService {

	/** メッセージマネージャー */
	@Autowired
	MessageManager msgManager;
	
	/**
	 * メッセージリソースからメッセージを取得する
	 *
	 * @param messageEnum
	 * @param locale
	 * @return メッセージ
	 */
	public String getMessage(MessageEnum messageEnum, Locale locale) {
		return msgManager.getMessage(messageEnum.getMessageKey(), locale);
	}

	/**
	 * メッセージリソースからメッセージを取得する
	 *
	 * @param messageEnum
	 * @param locale
	 * @param args
	 * @return メッセージ
	 */
	public String getMessage(MessageEnum messageEnum, Locale locale, String... args) {
		return msgManager.getMessage(messageEnum.getMessageKey(), args, locale);
	}

	/**
	 * メッセージリソースからメッセージを取得する
	 * 
	 * @param messageKey
	 * @param locale
	 * @return メッセージ
	 */
	public String getMessage(String messageKey, Locale locale) {

		return msgManager.getMessage(messageKey, locale);
	}

	/**
	 * メッセージリソースからメッセージを取得する
	 * 
	 * @param messageKey
	 * @param locale
	 * @param args
	 * @return メッセージ
	 */
	public String getMessage(String messageKey, Locale locale, String... args) {

		return msgManager.getMessage(messageKey, args, locale);
	}
}
