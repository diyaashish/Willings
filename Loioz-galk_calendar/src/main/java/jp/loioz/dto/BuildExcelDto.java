package jp.loioz.dto;

import java.util.List;

import lombok.Data;

/**
 * エクセル帳票のデータを管理するDtoクラス
 */
@Data
public class BuildExcelDto {

	/** 印刷範囲の設定有無フラグ */
	boolean printAreaFlg = false;

	/** 印刷範囲の設定先シート情報 */
	private int printSheetIdx;

	/** 印刷範囲の開始列情報 */
	private int printStartCol;

	/** 印刷範囲の開始行情報 */
	private int printStartRow;

	/** 印刷範囲の終了列情報 */
	private int printEndCol;

	/** 印刷範囲の終了行情報 */
	private int printEndRow;

	/** 固定項目への設定情報 */
	List<ExcelItemDto> excelItemDtoList;

	/** 可変項目への設定情報 */
	List<ExcelVariableItemDto> excelVariableItemDtoList;

	/** 結合セルの設定情報 */
	List<ExcelMergedInfoDto> excelMergedInfoDtoList;

}
