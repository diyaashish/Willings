package jp.loioz.app.common.excel.dto;

import java.util.List;

import lombok.Data;

/**
 * ExcelBuilderサンプル(テンプレート無しの標準的なテーブル形式）用のDTO
 */
@Data
public class SampleStandardTableLayoutExcelDto {

	/**
	 * Excelの出力開始行番号
	 */
	private int startRowNom = 0;
	
	/**
	 * Excelの出力開始列番号
	 */
	private int startColNum = 0;
	
	/**
	 * ヘッダ部の内容
	 */
	private List<String> headerDataList;
	
	/**
	 * ボディ部の内容
	 */
	private List<String> bodyDataList;
}
