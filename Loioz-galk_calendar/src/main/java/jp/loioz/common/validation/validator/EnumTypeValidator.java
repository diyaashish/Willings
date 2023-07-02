package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.validation.annotation.EnumType;

/**
 * Enumコード値バリデーション
 */
public class EnumTypeValidator implements ConstraintValidator<EnumType, String> {

	private Class<? extends DefaultEnum> clazz;

	@Override
	public void initialize(EnumType annotation) {
		this.clazz = annotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			return true;
		}
		return DefaultEnum.hasCode(clazz, value);
	}
}