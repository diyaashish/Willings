package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.MultipartFileMaxSize;

/**
 * ファイルアップロード 入力必須バリデーター
 */
public class MultipartFileMaxSizeValidator implements ConstraintValidator<MultipartFileMaxSize, MultipartFile> {

	long maxSize;

	@Override
	public void initialize(MultipartFileMaxSize constraint) {
		maxSize = constraint.max();
	}

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

		if (multipartFile == null || StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
			// ファイルが存在しない場合は、有効とする
			return true;
		}

		if (maxSize >= multipartFile.getSize()) {
			// ファイルアップロード上限以下の場合は有効とする
			return true;
		}

		return false;
	}

}
