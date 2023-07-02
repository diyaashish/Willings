package jp.loioz.common.utility;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import jp.loioz.common.constant.UrlConstant;
import jp.loioz.config.WebSecurityConfig;

/**
 * URIを扱うUtilクラス
 */
public class UriUtils {

	/**
	 * 指定のURIのパスが、未ログイン状態でもアクセス可能なリクエストパスかどうかを検証する。
	 * 
	 * @param uri
	 * @return true:未ログイン状態でもアクセス可能なパス false:未ログイン状態ではアクセス不可なパス
	 */
	public static boolean checkUriIsAccessibleNotLoggedInUser(URI uri) {
		
		String path = uri.getPath();
		
		PathMatcher matcher = new AntPathMatcher();
		
		// ログイン状態ではなくてもアクセス可能なURLパターン
		String[] urlPatternsAccessibleNoLogin = WebSecurityConfig.URL_PATTERNS_ACCESSIBLE_NO_LOGIN;
		
		// uriのパスが上記のパターンのいずれかに一致するかを検証
		for (String urlPattern : urlPatternsAccessibleNoLogin) {
			boolean isMatch= matcher.match(urlPattern, path);
			if (isMatch) {
				return true;
			}
		}

		return false;
	}

	/**
	 * URLのクエリパラメータ文字列を作成する
	 * 
	 * @param query
	 * @return
	 */
	public static String createGetRequestQueryParamStr(Map<String, String> query) {

		if (CollectionUtils.isEmpty(query)) {
			return "";
		}

		StringBuilder sb = new StringBuilder(UrlConstant.QUESTION);
		int counta = 0;
		for (Entry<String, String> entry : query.entrySet()) {
			if (counta != 0) {
				// クエリパラメータが複数存在する場合の2つ目以降は「&」を追加
				sb.append(UrlConstant.AMPERSAND);
			}

			// 文字列を追加
			sb.append(entry.getKey());
			sb.append(UrlConstant.EQUAL);
			sb.append(entry.getValue());

			// インクリメント処理
			counta++;
		}

		return sb.toString();
	}
}
