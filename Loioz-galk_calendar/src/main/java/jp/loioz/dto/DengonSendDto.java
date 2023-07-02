package jp.loioz.dto;

import lombok.Data;

@Data
public class DengonSendDto {

	// 受信者アカウント名
	private String receiveAccountName;

	// 受信者アカウントメールアドレス
	private String receiveAccountMailAdress;
}
