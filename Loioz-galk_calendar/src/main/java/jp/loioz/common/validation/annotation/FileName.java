package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.validation.validator.FileNameValidator;

@Documented
@Constraint(validatedBy = { FileNameValidator.class })
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface FileName {
	
	String message() default "次の文字は使用できません。 " + CommonConstant.FILENAME_PROHIBITION_CHARACTER;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ FIELD })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		FileName[] value();
	}
}
