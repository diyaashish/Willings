package jp.loioz.common.validation.validator;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.LocalDateTimePattern;

/**
 * LocalDateTimePatternValidator
 * 日時形式バリデーション
 */
public class LocalDateTimePatternValidator implements ConstraintValidator<LocalDateTimePattern, String> {

	String format;

	String item;

	@Override
	public void initialize(LocalDateTimePattern localDateTimePattern) {
		format=localDateTimePattern.format();
		item = localDateTimePattern.item();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}

		return isLocalDateTimePatternValidatorValid(value);
	}

	/**
	 * 引数で指定した文字列がformatで指定している形式かを検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean isLocalDateTimePatternValidatorValid(String value) {

		try {
			// 日時への変換処理が行えればフォーマットは適正と判断
			LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT));
			return true;
		} catch(DateTimeParseException e) {
			// 不正なフォーマット
			return false;
		}
	}
}
