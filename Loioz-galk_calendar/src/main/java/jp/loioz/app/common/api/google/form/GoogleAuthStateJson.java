package jp.loioz.app.common.api.google.form;

import java.util.UUID;

import jp.loioz.app.common.api.google.constants.GoogleConstants.GoogleAuthRequestCode;
import jp.loioz.app.common.api.oauth.form.OAuthStateJson;
import lombok.Data;

/**
 * GoogleのAuth認証で渡すstateプロパティを管理するオブジェクト
 */
@Data
public class GoogleAuthStateJson implements OAuthStateJson {

	/** UUID */
	private String uuid = UUID.randomUUID().toString();

	/** リクエスト元による判別 */
	private GoogleAuthRequestCode requestCode;

}
