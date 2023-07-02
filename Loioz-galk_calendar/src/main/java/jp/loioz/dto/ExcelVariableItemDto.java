package jp.loioz.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExcelVariableItemDto {

	/** 先頭項目フラグ（コピーが必要か） */
	boolean firstItemFlg = false;

	/** コピー元開始列情報 */
	private int copyStartCol;

	/** コピー元開始行情報 */
	private int copyStartRow;

	/** コピー元終了列情報 */
	private int copyEndCol;

	/** コピー元終了行情報 */
	private int copyEndRow;

	/** 設定先開始列情報 */
	private int pastStartCol;

	/** 設定先開始行情報 */
	private int pastStartRow;

	/** 背景色設定 */
	private String colorCd;

	/** 項目への設定情報 */
	private List<ExcelItemDto> excelItemDtoList;

	/** 可変項目の1レコード目のタイトル列情報（1件目が新規以外の場合） */
	private List<String> firstTitleList;
}