package jp.loioz.app.common.api.box.form;

import lombok.Data;

/**
 * BoxAPIConnectionクラスの状態を表す
 */
@Data
public class BoxConnectDto {

	/** アクセストークン */
	private String accessToken;

	/** リフレッシュトークン */
	private String refreshToken;

	/** リフレッシュをしたかどうか */
	private boolean refreshed = false;

}
