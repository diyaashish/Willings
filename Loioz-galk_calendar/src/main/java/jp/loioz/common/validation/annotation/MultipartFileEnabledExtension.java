package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.validation.validator.MultipartFileEnabledExtensionValidator;

@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = MultipartFileEnabledExtensionValidator.class)
public @interface MultipartFileEnabledExtension {

	FileExtension[] enabledExtension();

	String message() default "ファイル形式に誤りがあります";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({FIELD})
	@Retention(RUNTIME)
	@Documented
	@interface List {
		MultipartFileRequired[] value();
	}

}
