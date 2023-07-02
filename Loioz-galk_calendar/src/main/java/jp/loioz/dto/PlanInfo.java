package jp.loioz.dto;

import java.math.BigInteger;
import java.time.LocalDateTime;

import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import lombok.Data;

/**
 * 契約プラン情報のDtoクラス
 */
@Data
public class PlanInfo {

	/** テナント名 */
	private String tenantName;

	/** サブドメイン */
	private String subDomain;

	/** プランのステータス */
	private PlanStatus planStatus;
	/** プランの利用期限 */
	private LocalDateTime expiredAt;
	
	/** プランタイプ */
	private PlanType planType;
	/** 1ライセンスの料金（税抜）（月額） */
	private Long oneLicenseCharge;
	
	/** ライセンス数 */
	private Long licenseCount;
	/** ライセンス料（税抜）（月額） */
	private Long licenseCharge;

	/** ストレージ容量 */
	private Long storageCapacity;
	/** ストレージ料（税抜）（月額） */
	private Long storageCharge;

	/** 合計金額（税抜）（月額） */
	private Long sumCharge;

	/** 合計金額（月額）の消費税額 */
	private Long taxAmountOfSumCharge;

	/** 自動課金番号 */
	private BigInteger autoChargeNumber;

}
