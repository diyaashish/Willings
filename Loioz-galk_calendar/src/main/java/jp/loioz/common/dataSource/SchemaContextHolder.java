package jp.loioz.common.dataSource;

/**
 * スキーマ情報の保持クラス
 */
public class SchemaContextHolder {

	private static ThreadLocal<Long> tenantSeqHolder = new ThreadLocal<Long>();

	public static void setTenantSeq(Long tenantSeq) {
		tenantSeqHolder.set(tenantSeq);
	}

	public static Long getTenantSeq() {
		return tenantSeqHolder.get();
	}

	public static void clear() {
		tenantSeqHolder.remove();
	}
}
