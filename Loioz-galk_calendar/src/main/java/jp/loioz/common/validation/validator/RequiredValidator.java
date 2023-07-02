package jp.loioz.common.validation.validator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.Required;

/**
 * RequiredValidator
 * 必須バリデーション
 */
public class RequiredValidator implements ConstraintValidator<Required, Object> {

	String item;

	@Override
	public void initialize(Required required) {
		item = required.item();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		// nullチェック
		if (value == null) {
			return false;
		}

		// 文字列以外は空文字列チェックをしない
		if (!(value instanceof String)) {
			return true;
		}

		// トリム処理
		String valueTrimed = StringUtils.trim(value.toString());

		if (StringUtils.isEmpty(valueTrimed)) {
			// 値が空であればエラー
			return false;
		}
		// 値が空でなければOK
		return true;
	}
}