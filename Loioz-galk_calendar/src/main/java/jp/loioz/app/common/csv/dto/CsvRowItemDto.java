package jp.loioz.app.common.csv.dto;

import lombok.Data;

/**
 * CSVファイルのデータ1つ（カンマ区切りになるデータの1つ）分を保持するDto
 */
@Data
public class CsvRowItemDto {
	
	/** データ */
	private String val = "";
	
	/**
	 * コンストラクタ（デフォルト）
	 */
	public CsvRowItemDto() {
	};
	
	/**
	 * コンストラクタ（引数あり）
	 * 
	 * @param val
	 */
	public CsvRowItemDto(String val) {
		this.val = val;
	}
}
