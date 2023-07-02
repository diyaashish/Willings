package jp.loioz.dto;

import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import lombok.Data;
/**
 * アカウント管理(招待メール用)
 *
 */
@Data
public class AccountMailDto {
	
	/** 認証キー */
	private String verificationkey;
	
	/** メールアドレス */
	@MaxDigit(max = 256)
	@EmailPattern
	private String mailAddress;
	
	/** 今回更新対象フラグ */
	private boolean insertFlg;
}
