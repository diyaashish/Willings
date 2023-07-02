package jp.loioz.common.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

/**
 * Http用のUtilクラス。
 */
public class HttpUtils {

	/**
	 * 指定文字列をUTF-8でエンコードする
	 * 
	 * @param target
	 * @return エンコード後の文字列<br>
	 * UnsupportedEncodingExceptionが発生した場合、空文字を返却
	 */
	public static String encodeUTF8(String target) {
		try {
			return URLEncoder.encode(target, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * UTF-8文字列をデコードする
	 * 
	 * @param encodedStr
	 * @return エンコード後の文字列<br>
	 * UnsupportedEncodingExceptionが発生した場合、空文字を返却
	 */
	public static String decodeUTF8(String encodedStr) {
		try {
			return URLDecoder.decode(encodedStr, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * base64のURLセーフ文字列にエンコード
	 * 
	 * @param stateJson
	 * @return
	 */
	public static String base64UrlEncode(String stateJson) {
		return Base64.getUrlEncoder().encodeToString(stateJson.getBytes());
	}

	/**
	 * base64のURLセーフ文字列をデコード
	 * 
	 * @param stateJson
	 * @return
	 */
	public static String base64UrlDecode(String encoded) {
		return new String(Base64.getUrlDecoder().decode(encoded));
	}

	/**
	 * HttpRequestからAjax通信かどうかを判定する
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

}
