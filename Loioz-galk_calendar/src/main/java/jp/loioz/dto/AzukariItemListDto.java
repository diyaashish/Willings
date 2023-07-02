package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 預り品を管理するDtoクラス
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AzukariItemListDto {

	/** 預り品SEQ */
	private Long ankenItemSeq;

	/** ステータス */
	private String azukariStatus;

	/** 品目 */
	private String hinmoku;

	/** 数量 */
	private String azukariCount;

	/** 保管場所 */
	private String hokanPlace;

	/** 預り日 */
	private String azukariDate;

	/** 預り元種別 */
	private String azukariFromType;

	/** 預り元顧客ID */
	private Long azukariFromCustomerId;

	/** 預り元関与者SEQ */
	private Long azukariFromKanyoshaSeq;

	/** 預り元 */
	private String azukariFrom;

	/** 返却期限 */
	private String returnLimitDate;

	/** 返却日 */
	private String returnDate;

	/** 返却先 */
	private String returnTo;

	/** 返却先種別 */
	private String returnToType;

	/** 返却先顧客ID */
	private Long returnToCustomerId;

	/** 返却先関与者SEQ */
	private Long returnToKanyoshaSeq;

	/** 備考 */
	private String remarks;

	/** バージョンNo */
	private Long versionNo;

	/** 登録日時 */
	private String createdAt;

	/** 登録アカウント名 */
	private String createdByName;

	/** 最終編集日時 */
	private String lastEditAt;

	/** 最終編集アカウント名 */
	private String lastEditByName;

	/** 表示用登録者名、登録日時 */
	private String dispCreatedByNameDateTime;

	/** 表示用更新者名、更新日時 */
	private String dispLastEditByNameDateTime;

	/** 比較不要フラグ */
	private Boolean noComparisonRequired = false;
}