package jp.loioz.app.user.toiawase.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.constant.CommonConstant.ToiawaseType;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 問い合わせ作成画面入力値用オブジェクト
 */
@Data
public class ToiawaseCreateInputForm {

	/** 件名 */
	@Required
	@MaxDigit(max = 100)
	private String subject;

	/** 問い合わせ種別 */
	@Required
	@EnumType(value = ToiawaseType.class)
	private String toiawaseType;

	/** 本文 */
	@Required
	@MaxDigit(max = 10000)
	private String body;

	/** 添付ファイル */
	private List<MultipartFile> uploadFile;

}
