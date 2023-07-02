package jp.loioz.dto;

import java.time.LocalDate;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * プラン履歴一覧のDtoクラス
 */
@Data
public class PlanHistoryDto {

	/** 請求日 */
	private LocalDate chargeDate;

	/** プランタイプ */
	private PlanType planType;
	
	/** ライセンス数 */
	private Long licenseCount;
	
	/** ストレージ容量 */
	private Long storageCapacity;
	
	/** 請求金額 */
	private Long chargeThisMonth;
	
	/** 請求金額（税込） */
	private Long chargeThisMonthIncludedTax;
	
	/** 翌月以降の請求金額 */
	private Long chargeAfterNextMonth;
	
	/** 契約ステータス */
	private String planStatus;
	
	/**
	 * 請求日を文字列で取得する
	 * 
	 * @return
	 */
	public String getChargeDateStr() {
		
		LocalDate date = this.chargeDate;
		
		if (date == null) {
			return "";
		}
		
		return DateUtils.parseToString(date, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
	}
	
	/**
	 * プランタイプの表示文字を取得
	 * 
	 * @return
	 */
	public String getPlanTypeTitle() {
		if (this.planType == null) {
			return "";
		}
		return this.planType.getTitle();
	}
	
	/**
	 * 利用プランのステータス文字列を取得する
	 * 
	 * @return
	 */
	public String getPlanStatusStr() {
		
		String planStatus = this.planStatus;
		
		if (StringUtils.isEmpty(planStatus)) {
			return "";
		}
		
		PlanStatus status = DefaultEnum.getEnum(PlanStatus.class, planStatus);
		
		return status != null ? status.getVal() : "";
	}
}
