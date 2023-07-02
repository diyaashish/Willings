package jp.loioz.common.validation.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.Input4ByteCheck;

public class Input4ByteCheckValidator implements ConstraintValidator<Input4ByteCheck, String> {

	@Override
	public void initialize(Input4ByteCheck input4ByteCheck) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			// 値が空の場合、チェックしない
			return true;
		}
		return is4ByteCheck(value);
	}

	/**
	 * 引数で指定した文字列に4バイト文字が含まれていないか検証します。
	 *
	 * @param value
	 *            検証する文字列
	 * @return チェック結果
	 */
	private boolean is4ByteCheck(String value) {
		Pattern pattern = Pattern.compile("[\\u0000-\\uFFFF]*");
		Matcher m = pattern.matcher(value);
		if(!m.matches()){
			//UTF-8の範囲を超える文字が存在する
			return false;
		}
		return true;
	}
}