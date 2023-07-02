package jp.loioz.common.async;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.domain.LoginUserDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * スレッド情報管理クラス
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadLocalContextManager {

	/**
	 * スレッド情報を取得する
	 *
	 * @return スレッド情報
	 */
	public static ThreadLocalContext exportContext() {
		// 現在のスレッド情報を取得
		Long tenantSeq = SchemaContextHolder.getTenantSeq();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Map<String, String> logContext = MDC.getCopyOfContextMap();

		// スレッド情報のコピーを作成
		SecurityContext copySecurityContext = SecurityContextHolder.createEmptyContext();

		// 認証情報のコピーを作成
		Authentication orgAuthentication = securityContext.getAuthentication();
		if (orgAuthentication != null) {
			Object principal = orgAuthentication.getPrincipal();
			if (principal instanceof LoginUserDetails) {
				principal = ((LoginUserDetails) principal).clone();
			}

			PreAuthenticatedAuthenticationToken copyAuthentication = new PreAuthenticatedAuthenticationToken(principal, orgAuthentication.getCredentials(), orgAuthentication.getAuthorities());
			copyAuthentication.setAuthenticated(orgAuthentication.isAuthenticated());
			copyAuthentication.setDetails(orgAuthentication.getDetails());
			copySecurityContext.setAuthentication(copyAuthentication);
		}

		return new ThreadLocalContext(tenantSeq, copySecurityContext, logContext);
	}

	/**
	 * スレッド情報を設定する
	 *
	 * @param context スレッド情報
	 */
	public static void importContext(ThreadLocalContext context) {
		SchemaContextHolder.setTenantSeq(context.getSchemaContextTenantSeq());
		SecurityContextHolder.setContext(context.getSecurityContext());
		MDC.setContextMap(context.getLogContext());
	}

	/**
	 * スレッド情報を破棄する
	 */
	public static void clearContext() {
		SchemaContextHolder.clear();
		SecurityContextHolder.clearContext();
		MDC.clear();
	}
}
