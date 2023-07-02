package jp.loioz.app.common.api.oauth.form;

import com.google.gson.Gson;

/**
 * OAuth認証のstateパラメータを管理
 * 
 * @author hoshima
 *
 */
public interface OAuthStateJson {

	/** UUID */
	public String getUuid();

	/**
	 * 格納しているプロパティをjson文字列に変換
	 * 
	 * @return
	 */
	public default String getJsonStr() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	/**
	 * Json文字列から指定クラスのインスタンスを作成する
	 * 
	 * @param <T>
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	public static <T extends OAuthStateJson> T fromJson(String jsonStr, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(jsonStr, clazz);
	}

}
