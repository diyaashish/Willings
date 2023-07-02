package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExcelShiharaiKeikakuListDto {

	/** 入出金予定SEQ */
	private Long nyushukkinYoteiSeq;

	/** 期限 */
	private LocalDate nyushukkinYoteiDate;

	/** 金額 */
	private BigDecimal kingaku;

	/** 金額(表示用) */
	private String dispKingaku;

	/** 残金 */
	private BigDecimal zankin;

	/** 残金(表示用) */
	private String dispZankin;

}
