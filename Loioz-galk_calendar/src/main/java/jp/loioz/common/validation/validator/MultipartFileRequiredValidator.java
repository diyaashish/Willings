package jp.loioz.common.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.validation.annotation.MultipartFileRequired;

/**
 * ファイルアップロード 入力必須バリデーター
 */
public class MultipartFileRequiredValidator implements ConstraintValidator<MultipartFileRequired, MultipartFile> {

	@Override
	public void initialize(MultipartFileRequired constraint) {
	}

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
		return multipartFile != null
				&& !multipartFile.getOriginalFilename().isEmpty();
	}

}
