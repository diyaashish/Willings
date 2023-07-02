package jp.loioz.dto;

import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import lombok.Data;

/**
 * メールアドレス情報
 */
@Data
public class MailDto {

	/** アカウントID表示用のプロパティ */
	@MaxDigit(max = 256)
	@EmailPattern
	private String mailAddress;
}
