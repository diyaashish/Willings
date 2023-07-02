package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.validation.validator.EnumTypeValidator;

@Documented
@Constraint(validatedBy = { EnumTypeValidator.class })
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface EnumType {

	Class<? extends DefaultEnum> value();

	String message() default "入力内容に誤りがあります。";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ FIELD })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		EnumType[] value();
	}
}
