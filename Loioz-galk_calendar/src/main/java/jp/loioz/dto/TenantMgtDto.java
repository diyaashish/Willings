package jp.loioz.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 *
 */
@Data
@Builder
public class TenantMgtDto {

	/** テナント連番 */
	private Long tenantSeq;

	/** サブドメイン名（事務所ID） */
	private String subDomain;

	/** 事務所名 */
	private String tenantName;

	/** 個人・法人区分 */
	private String tenantTypeName;

	/** 契約ステータス */
	private String planStatus;

	/** 契約ステータス（表示用） */
	private String planStatusName;

	/** 利用期限 */
	private LocalDateTime serviceLimitAt;

	/** 作成日時 */
	private String createdAtStr;

	/** 更新日時 */
	private String updatedAtStr;

	/** システム管理者_作成日時 */
	private String sysCreatedAt;

	/** システム管理者_更新日時 */
	private String sysUpdatedAt;

	/** バージョンNo */
	private Long versionNo;

}