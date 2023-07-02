package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.validation.validator.NumericHyphenValidator;

@Documented
@Constraint(validatedBy = {NumericHyphenValidator.class})
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface NumericHyphen {

	String item() default "Number";

	String message() default "半角数字およびハイフンを入力してください";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({FIELD})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		NumericHyphen[] value();
	}
}
