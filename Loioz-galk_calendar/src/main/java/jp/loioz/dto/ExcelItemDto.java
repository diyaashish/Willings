package jp.loioz.dto;

import lombok.Data;

@Data
public class ExcelItemDto {

	/** セル位置情報 */
	String cell;

	/** 設定値 */
	Object value;

	/** 値の型コード */
	String type;

	/** 行の基準となる高さ（デフォルトは自動調整） */
	float height = -1f;
}