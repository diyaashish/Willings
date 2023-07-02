package jp.loioz.common.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * メッセージリソースの管理を行うクラス
 */
@Component
public class MessageManager {

	/** メッセージリソース */
	@Autowired
	private MessageSource msgSource;

	/**
	 * メッセージキーのに対応したメッセージを取得する
	 *
	 * @param key メッセージキー
	 * @return メッセージ
	 */
	public String getMessage(String key, Locale locale) {

		return msgSource.getMessage(key, null, locale);
	}

	/**
	 * メッセージキーのに対応したメッセージを取得する
	 *
	 * @param key メッセージキー
	 * @param args メッセージに埋め込むパラメータリスト
	 * @return メッセージ
	 */
	public String getMessage(String key, Object args[], Locale locale) {

		return msgSource.getMessage(key, args, locale);
	}
}
