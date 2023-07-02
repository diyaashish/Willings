package jp.loioz.dto;

import lombok.Data;

@Data
public class ExcelMergedInfoDto {

	/** マージ開始行 */
	int startRow;

	/** マージ開始列 */
	int startCol;

	/** マージ終了行 */
	int endRow;

	/** マージ終了列 */
	int endCol;

	/** 背景色設定 */
	String colorCd;

	/** 太字設定 初期値はfalse */
	boolean bold = false;

	/** 罫線（上）設定 初期値はfalse */
	boolean borderTop = false;

	/** 罫線（下）設定 初期値はfalse */
	boolean borderBottom = false;

	/** 罫線（左）設定 初期値はfalse */
	boolean borderLeft = false;

	/** 罫線（右）設定 初期値はfalse */
	boolean borderRight = false;

}