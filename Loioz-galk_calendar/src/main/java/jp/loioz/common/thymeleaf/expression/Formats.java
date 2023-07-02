package jp.loioz.common.thymeleaf.expression;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 文字列をフォーマットするExpressionObject
 */
public class Formats {

	public static DecimalFormat MONETARY_AMOUNT_FORMAT = new DecimalFormat("#,###.##");

	public String formatMonetaryAmount(Object target) {
		if (target == null) {
			return null;
		}
		try {
			String targetStr = target.toString();
			BigDecimal value = new BigDecimal(targetStr);
			return MONETARY_AMOUNT_FORMAT.format(value);

		} catch (IllegalArgumentException e) {
			if (target instanceof String) {
				return (String) target;
			} else {
				return null;
			}
		}
	}
}
