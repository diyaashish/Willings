package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.validation.validator.NumericAlphabetValidator;

@Documented
@Constraint(validatedBy = {NumericAlphabetValidator.class})
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface NumericAlphabet {

	String item() default "NumericAlphabet";

	String message() default "半角英数字を入力してください";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({FIELD})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		NumericAlphabet[] value();
	}
}
