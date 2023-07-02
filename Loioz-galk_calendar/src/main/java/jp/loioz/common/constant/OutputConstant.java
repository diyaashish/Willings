package jp.loioz.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Common処理系の定数クラス
 */
public class OutputConstant {

	/** 定数 start */
	public static final String JS_SPACE_ENCODE = "%20";
	public static final String FONT_MINCHO = "ＭＳ 明朝";
	public static final int FONT_DEFAULT_SIZE = 12;
	public static final String FONT_MEIRYO = "メイリオ";
	public static final String BINARY_UTF8 = "application/octet-stream; charset=UTF-8";
	public static final String LAWYER = "弁護士";
	public static final String TANTO_JIMU = "担当事務";
	public static final String YEN = "円";
	public static final String YEN_MARK = "￥";
	public static final String KAKKO_MINUTE = "(分)";

	/** Excelでデータ出力可能な最大行数 */
	public static final int MAX_EXCEL_OUTPUT_DATA = 10000;

	/** Csvでデータ出力可能な最大行数 */
	public static final int MAX_CSV_OUTPUT_DATA = 10000;

	/** タイトル文字サイズ */
	public static final short TITLE_FONT_SIZE = 12;

	/** 出力日の和暦表示スタイル */
	public static final short WAREKI_DATE_CELL_STYLE = 28;

	/** 一覧の文字サイズ */
	public static final short LIST_FONT_SIZE = 9;

	/** 定数 end */

	// -----------------------------------------------------------------------------
	// セル設定時の型コード
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum OutputType implements DefaultEnum {
		STR("1", "String"),
		NUM("2", "double"),
		CAL("3", "Calendar"),
		DATE("4", "Date"),
		BOOL("5", "boolean"),
		RICH("6", "RichTextString");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

	}

	// -----------------------------------------------------------------------------
	// エクセル設定キー
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum ExcelMapKey implements DefaultEnum {
		CELL("cell", "セル情報"),
		VALUE("value", "設定値"),
		TYPE("type", "値の型コード"), // OutputTypeのcd値を設定
		BASE_CELL("baseCell", "コピー元セル情報");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

	}

}
