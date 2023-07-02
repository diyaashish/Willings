package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.FileName;

/**
 * FileNameValidator
 * ファイル名用バリデーション
 *
 */
public class FileNameValidator implements ConstraintValidator<FileName, String> {

	@Override
	public void initialize(FileName fileName) {
		// 処理なし
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if(StringUtils.isEmpty(value)) {
			// 未入力はチェック対象外
			return true;
		}
		
		// 禁則文字となる記号を取得
		String[] checkSymbol = CommonConstant.FILENAME_PROHIBITION_CHARACTER.split(CommonConstant.SPACE);
		
		for (String symbol : checkSymbol) {
			// 指定の記号が使用されている場合はエラー
			if(value.contains(symbol)) {
				return false;
			}
		}
		return true;
	}
}
