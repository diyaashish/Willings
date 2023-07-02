package jp.loioz.common.async;

import java.util.Map;

import org.springframework.security.core.context.SecurityContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * スレッド情報
 */
@RequiredArgsConstructor
@Getter
public class ThreadLocalContext {
	/** スキーマ情報:テナント連番 */
	private final Long schemaContextTenantSeq;

	/** 認証情報 */
	private final SecurityContext securityContext;

	/** ロガーのMDC */
	private final Map<String, String> logContext;
}
