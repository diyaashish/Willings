package jp.loioz.dto;

import lombok.Data;

/**
 * 精算書一式を帳票出力為のDTO
 */
@Data
public class ExcelSeisanshoSetDto {

	/** 送付書 */
	private ExcelSofushoDto sofushoData;

	/** 精算書 */
	private ExcelSeisanshoDto seisanshoData;

	/** 請求書 */
	private ExcelSeikyushoDto seikyushoData;

	/** 実費明細 */
	private ExcelJippiMeisaiDto jippiMeisaiData;

	/** 支払い計画 */
	private ExcelShiharaiKeikakuDto shiharaiKeikakuData;

	/** タイムチャージ */
	private ExcelTimeChargeDto timeChargeData;

}
