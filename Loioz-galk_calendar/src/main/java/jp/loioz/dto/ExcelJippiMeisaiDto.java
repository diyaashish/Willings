package jp.loioz.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExcelJippiMeisaiDto {

	/** 出金リスト */
	private List<ExcelJippiMeisaiShukkinListDto> shukkinList = new ArrayList<>();

	/** 出金額計 */
	private BigDecimal shukkinTotal;

	/** 出金額計(表示) */
	private String dispShukkinTotal;

	/** 消費税計 */
	private BigDecimal taxTotal;

	/** 消費税計(表示) */
	private String dispTaxTotal;

}
