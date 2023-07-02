package jp.loioz.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class AnkenSekkenEditDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	/** 接見SEQ */
	private Long sekkenSeq;

	/** 接見開始日時 */
	@Required
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
	private LocalDateTime sekkenStartAt;

	/** 接見終了日時 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
	private LocalDateTime sekkenEndAt;

	/** 場所 */
	@MaxDigit(max = 100)
	private String place;

	/** 備考 */
	@MaxDigit(max = 100)
	private String remarks;
}