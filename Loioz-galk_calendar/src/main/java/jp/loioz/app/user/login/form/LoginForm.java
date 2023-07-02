package jp.loioz.app.user.login.form;

import lombok.Data;

/**
 * ログイン画面用のフォームクラス
 */
@Data
public class LoginForm {

	/** ユーザー名 */
	private String username;

	/** パスワード */
	private String password;

	/** アカウントIDを保存する */
	private boolean keepAccountFlg = false;

	/** ログイン状態を保存する */
	private boolean keepAuthenticationFlg = false;

}
