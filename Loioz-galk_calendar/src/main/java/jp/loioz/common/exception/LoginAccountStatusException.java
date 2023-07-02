package jp.loioz.common.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * ログイン時のアカウントステータスの例外クラス
 */
public class LoginAccountStatusException extends UsernameNotFoundException {

	private static final long serialVersionUID = 1L;

	public LoginAccountStatusException(String msg) {
		super(msg);
	}

}
