package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.validation.validator.MinNumericValueValidator;

@Documented
@Constraint(validatedBy = {MinNumericValueValidator.class})
@Target({FIELD,ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface MinNumericValue {
	long min() default Long.MIN_VALUE;

	String item() default "最小桁";

	String message() default "入力可能範囲外です。";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({FIELD})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		MinNumericValue[] value();
	}
}
