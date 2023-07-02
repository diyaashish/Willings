package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.AzukariItemStatus;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class AzukariItemEditDto {

	/** 預り品SEQ */
	private Long ankenItemSeq;

	/** ステータス */
	@EnumType(value = AzukariItemStatus.class)
	@MaxDigit(max = 1, item = "ステータス")
	private String azukariStatus;

	/** 品目 */
	@Required
	@MaxDigit(max = 50, item = "品目")
	private String hinmoku;

	/** 数量 */
	@MaxDigit(max = 30, item = "数量")
	private String azukariCount;

	/** 保管場所 */
	@MaxDigit(max = 100, item = "保管場所")
	private String hokanPlace;

	/** 預り日 */
	@LocalDatePattern(item = "預り日")
	private String azukariDate;

	/** 預り元種別 */
	@Required
	private String azukariFromType;

	/** 預り元顧客ID */
	private Long azukariFromCustomerId;

	/** 預り元関与者SEQ */
	private Long azukariFromKanyoshaSeq;

	/** 預り元 */
	@MaxDigit(max = 50, item = "預り元")
	private String azukariFrom;

	/** 返却期限 */
	@LocalDatePattern(item = "返却期限")
	private String returnLimitDate;

	/** 返却日 */
	@LocalDatePattern(item = "返却日")
	private String returnDate;

	/** 返却先 */
	@MaxDigit(max = 50, item = "返却先")
	private String returnTo;

	/** 返却先種別 */
	@Required
	private String returnToType;

	/** 返却先顧客ID */
	private Long returnToCustomerId;

	/** 返却先関与者Seq */
	private Long returnToKanyoshaSeq;

	/** 備考 */
	@MaxDigit(max = 3000)
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

}
