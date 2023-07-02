package jp.loioz.common.validation.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.utility.FileUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.MultipartFileEnabledExtension;

/**
 * MultiPartFile ファイル拡張子Validator
 */
public class MultipartFileEnabledExtensionValidator implements ConstraintValidator<MultipartFileEnabledExtension, MultipartFile> {

	FileExtension[] enabledExtension;

	@Override
	public void initialize(MultipartFileEnabledExtension constraint) {
		enabledExtension = constraint.enabledExtension();
	}

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

		if (multipartFile == null || StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
			// ファイルが存在しない場合は、有効とする
			return true;
		}

		String fileName = multipartFile.getOriginalFilename();
		String extension = FileUtils.getExtension(fileName);
		if (Arrays.stream(enabledExtension).anyMatch(e -> e.equalsByValue(extension))) {
			// 有効な拡張子の場合
			return true;
		}

		return false;
	}

}
