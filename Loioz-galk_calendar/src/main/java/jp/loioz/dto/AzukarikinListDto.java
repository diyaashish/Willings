package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 預り金・未収入金管理-検索用のDtoクラス
 */
@Data
public class AzukarikinListDto {

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 分野名 */
	private Long bunyaId;

	/** 案件名 */
	private String ankenName;

	/** 売上計上先 */
	private String salesOwnerName;

	/** 担当弁護士 */
	private String tantoLawyerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** 案件ステータス */
	private String ankenStatus;

	/** 入金 */
	private BigDecimal totalNyukin;

	/** 出金 */
	private BigDecimal totalShukkin;

	/** 報酬 */
	private BigDecimal hoshu;

	/** 消費税 */
	private BigDecimal totalTax;

	/** 源泉徴収 */
	private BigDecimal totalGensen;

	/** 案件計 */
	private BigDecimal totalAnkenKaikei;

	/** 顧客計 */
	private BigDecimal totalCustomerKaikei;

	/** 業歴最終更新日 */
	private LocalDate gyomuHistoryLastUpdate;

	/** 最終入金日 */
	private LocalDate lastNyukinDate;

}