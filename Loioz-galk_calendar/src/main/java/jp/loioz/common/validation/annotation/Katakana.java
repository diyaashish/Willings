package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.validation.validator.KatakanaValidator;

@Documented
@Constraint(validatedBy = {KatakanaValidator.class})
@Target({FIELD,ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Katakana {

	String item() default "カナ";

	String message() default "入力内容に誤りがあります。";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({FIELD})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		Katakana[] value();
	}
}
