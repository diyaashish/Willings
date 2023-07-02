package jp.loioz.dto;

import lombok.Data;

@Data
public class PaymentCardDto {

	/** カード番号の下4桁 */
	private String cardNumberLast4;
	/** 有効期限（年） */
	private String expiredYear;
	/** 有効期限（月） */
	private String expiredMonth;
	/** 名義（姓） */
	private String lastName;
	/** 名義（名） */
	private String firstName;
}
