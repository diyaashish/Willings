package jp.loioz.common.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * ログイン時の事務所の契約ステータスの例外クラス
 */
public class LoginTenantPlanStatusException extends UsernameNotFoundException {

	private static final long serialVersionUID = 1L;

	public LoginTenantPlanStatusException(String msg) {
		super(msg);
	}
}
