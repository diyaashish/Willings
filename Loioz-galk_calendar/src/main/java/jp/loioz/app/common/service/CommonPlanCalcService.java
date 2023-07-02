package jp.loioz.app.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.utility.DateUtils;

/**
 * 契約プランの計算処理共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonPlanCalcService extends DefaultService {

	/** プラン料金計算時に利用する丸め方 */
	private static final RoundingMode PLAN_CALC_ROUNDING_MODE = RoundingMode.DOWN;
	
	/** 少数を残す場合の桁数 */
	private static final int DECIMAL_DIGITS = 10;
	
	/**
	 * 指定の値を整数に丸める
	 * 
	 * @param val
	 * @return
	 */
	public Long roudToLong(BigDecimal val) {
		if (val == null) {
			return 0L;
		}
		
		BigDecimal truncateDecimalVal = val.setScale(0, PLAN_CALC_ROUNDING_MODE);
		
		return truncateDecimalVal.longValue();
	}
	
	/**
	 * 単位あたりの料金と単位数を元に、全ての単位の合計金額を計算する。<br>
	 * （少数以下は切り捨て）
	 * 
	 * @param chargePerUnitDecimal
	 * @param countOfUnit
	 * @return
	 */
	public Long calcSumChargeOfAllUnits(BigDecimal chargePerUnitDecimal, Long countOfUnit) {
		
		BigDecimal countOfUnitDecimal = new BigDecimal(countOfUnit);
		
		BigDecimal sumChargeOfAllUnits = chargePerUnitDecimal.multiply(countOfUnitDecimal);
		BigDecimal sumChargeOfAllUnitsTruncateDecimal = sumChargeOfAllUnits.setScale(0, PLAN_CALC_ROUNDING_MODE);
		
		Long sumChargeOfAllUnitsResult = Long.valueOf(sumChargeOfAllUnitsTruncateDecimal.toPlainString());
		
		return sumChargeOfAllUnitsResult;
	}
	
	/**
	 * 対象日の月の1日分の料金を計算する。<br>
	 * <br>
	 * ※対象日の月によって1カ月の日数が変わるため、monthlyChargeが同じ値でも、<br>
	 * 　targetDateが異なる月の日付の場合は計算結果は変動する。
	 * 
	 * @param targetDate 対象日（対象日の月の1日分の料金を計算する）
	 * @param monthlyCharge 月額料金
	 * @param isTruncateDecimal 計算結果の少数以下を切り捨てるかどうか（true: 切り捨てる）
	 * @return
	 */
	public BigDecimal calcOneDayCharge(LocalDate targetDate, Long monthlyCharge, boolean isTruncateDecimal) {
		
		// 1カ月の料金
		BigDecimal monthlyChargeDecimal = new BigDecimal(monthlyCharge);
		
		// 1カ月の日数
		Long thisMonthDayCount = DateUtils.getThisMonthDayCount(targetDate);
		BigDecimal thisMonthDayCountDecimal = new BigDecimal(thisMonthDayCount);

		BigDecimal oneDayChargeDecimal = null;
		
		if (isTruncateDecimal) {
			// 1日の料金（少数以下は切り捨て）
			oneDayChargeDecimal = monthlyChargeDecimal.divide(thisMonthDayCountDecimal, 0, PLAN_CALC_ROUNDING_MODE);
		} else {
			// 1日の料金（少数を10桁まで保持）
			oneDayChargeDecimal = monthlyChargeDecimal.divide(thisMonthDayCountDecimal, DECIMAL_DIGITS, PLAN_CALC_ROUNDING_MODE);
		}
		
		return oneDayChargeDecimal;
	}
	
	/**
	 * 指定日数分の料金を計算する。<br>
	 * （少数以下は切り捨てる）
	 * 
	 * @param oneDayChargeDecimal 1日分の料金
	 * @param daysCount 料金がかかる日数
	 * @return oneDayChargeDecimal、daysCountのいずれかがnullの場合はnullを返却する
	 */
	public Long calcLongChargeForDays(BigDecimal oneDayChargeDecimal, Long daysCount) {
		
		if (oneDayChargeDecimal == null || daysCount == null) {
			return null;
		}
		
		BigDecimal daysCountDecimal = new BigDecimal(daysCount);
		
		// 日数分の料金を計算（少数以下は切り捨て）
		BigDecimal chargeForDaysDecimal = oneDayChargeDecimal.multiply(daysCountDecimal);
		BigDecimal chargeForDaysTruncateDecimal = chargeForDaysDecimal.setScale(0, PLAN_CALC_ROUNDING_MODE);
		
		// Long型に変換
		Long chargeForDaysResult = Long.valueOf(chargeForDaysTruncateDecimal.toPlainString());
		
		return chargeForDaysResult;
	}
	
	/**
	 * 指定日数分の料金を計算する。<br>
	 * （少数をそのまま残す）
	 * 
	 * @param oneDayChargeDecimal 1日分の料金
	 * @param daysCount 料金がかかる日数
	 * @return oneDayChargeDecimal、daysCountのいずれかがnullの場合はnullを返却する
	 */
	public BigDecimal calcDecimalChargeForDays(BigDecimal oneDayChargeDecimal, Long daysCount) {
		
		if (oneDayChargeDecimal == null || daysCount == null) {
			return null;
		}
		
		BigDecimal daysCountDecimal = new BigDecimal(daysCount);
		
		// 日数分の料金を計算（指定桁以下は切り捨て）
		BigDecimal chargeForDaysDecimal = oneDayChargeDecimal.multiply(daysCountDecimal);
		BigDecimal chargeForDaysTruncateDecimal = chargeForDaysDecimal.setScale(DECIMAL_DIGITS, PLAN_CALC_ROUNDING_MODE);
		
		return chargeForDaysTruncateDecimal;
	}
}
