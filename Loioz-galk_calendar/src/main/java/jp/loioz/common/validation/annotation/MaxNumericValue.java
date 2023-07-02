package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.validation.validator.MaxNumericValueValidator;

@Documented
@Constraint(validatedBy = {MaxNumericValueValidator.class})
@Target({FIELD,ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface MaxNumericValue {
	long max() default Long.MAX_VALUE;

	String item() default "最大値";

	String message() default "入力できる最大値を超えています";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({FIELD})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		MaxNumericValue[] value();
	}
}
