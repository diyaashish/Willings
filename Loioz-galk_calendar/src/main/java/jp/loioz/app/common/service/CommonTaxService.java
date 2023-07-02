package jp.loioz.app.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.utility.CommonUtils;

/**
 * 税金に関する処理の共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonTaxService extends DefaultService {

	/**
	 * 対象日における対象額の税額を算出する
	 * 
	 * @param amountExcludingTax 税金を含まない金額 ※nullを許容しない
	 * @param targetDate 税額算出を行う日付 ※nullを許容しない
	 * @return 税額
	 */
	public Long calcTaxAmount(Long amountExcludingTax, LocalDate targetDate) {
		
		if (CommonUtils.anyNull(amountExcludingTax, targetDate)) {
			// 引数にnullがある場合はエラーとする
			throw new IllegalArgumentException();
		}
		
		// 対象金額
		BigDecimal amountExcludingTaxDecimal = new BigDecimal(amountExcludingTax);
		
		// 対象日の税率	
		TaxRate taxEnum = CommonConstant.TaxRate.of(targetDate);
		String taxPercent = taxEnum.getVal();
		BigDecimal taxPercentDecimal = new BigDecimal(taxPercent);
		
		// 税率を小数表記に変換（小数第２位まで取得）
		BigDecimal percentDenominator = new BigDecimal(100);
		BigDecimal taxNumDecimal = taxPercentDecimal.divide(percentDenominator, 2, RoundingMode.DOWN);
		
		// 税額を算出（端数は「切り捨て」）
		BigDecimal taxAmountDecimal = amountExcludingTaxDecimal.multiply(taxNumDecimal);
		taxAmountDecimal = taxAmountDecimal.setScale(0, RoundingMode.DOWN);
		
		return taxAmountDecimal.longValue();
	}
	
}
