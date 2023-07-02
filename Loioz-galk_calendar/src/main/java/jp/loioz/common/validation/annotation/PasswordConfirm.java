package jp.loioz.common.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.loioz.common.validation.validator.PasswordConfirmValidator;

@Documented
@Constraint(validatedBy = { PasswordConfirmValidator.class })
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface PasswordConfirm {

	String message() default "パスワードとパスワード確認が一致しません。";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ TYPE })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		PasswordConfirm[] value();
	}
}
