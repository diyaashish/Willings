package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExcelJippiMeisaiShukkinListDto {

	/** 発生日 */
	private LocalDate hasseiDate;

	/** 出金額 */
	private BigDecimal shukkinGaku;

	/** 出金額(表示用) */
	private String dispShukkinGaku;

	/** 消費税 */
	private BigDecimal tax;

	/** 消費税(表示用) */
	private String dispTax;

	/** 出金項目(入出金項目:出金) */
	private String shukkinKomokuName;

	/** 税区分 */
	private String taxType;

	/** 摘要 */
	private String tekiyo;

}
