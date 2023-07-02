package jp.loioz.app.common.api.dropbox.form;

import lombok.Data;

/**
 * DropboxのApi接続情報Dto
 */
@Data
public class DropboxConnectDto {

	/** アクセストークン */
	private String accessToken;

	/** リフレッシュトークン */
	private String refreshToken;

	/** リフレッシュをしたかどうか */
	private boolean refreshed = false;

}
