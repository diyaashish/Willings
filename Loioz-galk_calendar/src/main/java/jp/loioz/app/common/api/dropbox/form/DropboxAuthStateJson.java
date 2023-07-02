package jp.loioz.app.common.api.dropbox.form;

import java.util.UUID;

import jp.loioz.app.common.api.dropbox.constants.DropboxConstants.DropboxAuthRequestCode;
import jp.loioz.app.common.api.oauth.form.OAuthStateJson;
import lombok.Data;

/**
 * DropboxのAuth認証で渡すstateプロパティを管理するオブジェクト
 */
@Data
public class DropboxAuthStateJson implements OAuthStateJson {

	/** UUID */
	private String uuid = UUID.randomUUID().toString();

	/** リクエスト元による判別 */
	private DropboxAuthRequestCode requestCode;

}
