package jp.loioz.dto;

import lombok.Data;

@Data
public class DengonDto {

	// 伝言Seq
	private Long dengonSeq;

	// 送信者アカウントseq
	private Long sendAccountSeq;

	// 受信者アカウントseq
	private String receiveAccountSeq;

	// 件名
	private String title;

	// 本文
	private String body;

}
