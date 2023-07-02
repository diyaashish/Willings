package jp.loioz.common.validation.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;

/**
 * DatePatternValidator
 * 日付形式バリデーション
 */
public class LocalDatePatternValidator implements ConstraintValidator<LocalDatePattern, String> {

	String format;

	String item;

	@Override
	public void initialize(LocalDatePattern localDatePattern) {
		format = localDatePattern.format();
		item = localDatePattern.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isLocalDatePatternValidatorValid(value);
	}

	/**
	 * 引数で指定した文字列がformatで指定している形式かを検証します。
	 *
	 * @param value
	 * 検証する文字列
	 * @return チェック結果
	 */
	private boolean isLocalDatePatternValidatorValid(String value) {

		try {
			// 日付への変換処理が行えればフォーマットは適正と判断
			LocalDate.parse(value, DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT));
			return true;
		} catch (DateTimeParseException e) {
			// 不正なフォーマット
			return false;
		}
	}
}
