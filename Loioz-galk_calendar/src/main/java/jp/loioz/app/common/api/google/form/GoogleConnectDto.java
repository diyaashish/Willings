package jp.loioz.app.common.api.google.form;

import lombok.Data;

/**
 * GoogleのApi接続情報Dto
 */
@Data
public class GoogleConnectDto {

	/** アクセストークン */
	private String accessToken;

	/** リフレッシュトークン */
	private String refreshToken;

	/** リフレッシュをしたかどうか */
	private boolean refreshed = false;

}
