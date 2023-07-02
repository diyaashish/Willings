package jp.loioz.app.common.excel.dto;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.dto.ExcelShiharaiKeikakuListDto;
import lombok.Data;

@Data
public class ShiharaiKeikakuExcelDto {

	/** 銀行名 */
	private String ginkoName;

	/** 支店名 */
	private String shitenName;

	/** 支店番号 */
	private String shitenNo;

	/** 口座種類 */
	private String kozaType;

	/** 口座番号 */
	private String kozaNo;

	/** 口座名 */
	private String kozaName;

	/** 宛名 */
	private String name;

	/** 支払い計画一覧 */
	private List<ExcelShiharaiKeikakuListDto> shiharaiYoteiList = new ArrayList<>();
}
