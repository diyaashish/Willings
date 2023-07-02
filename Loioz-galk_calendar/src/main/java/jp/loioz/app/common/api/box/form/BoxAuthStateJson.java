package jp.loioz.app.common.api.box.form;

import java.util.UUID;

import jp.loioz.app.common.api.box.constants.BoxConstants.BoxAuthRequestCode;
import jp.loioz.app.common.api.oauth.form.OAuthStateJson;
import lombok.Data;

/**
 * Box認証で利用するstateプロパティ用オブジェクト
 */
@Data
public class BoxAuthStateJson implements OAuthStateJson {

	/** UUID */
	private String uuid = UUID.randomUUID().toString();

	/** リクエスト元による判別 */
	private BoxAuthRequestCode requestCode;

}
