package jp.loioz.common.utility;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel出力用Utilクラス
 */
public class ExcelUtils {

	/**
	 * シートを削除します
	 *
	 * @param sheetIndex 削除したいシート連番
	 */
	public static void removeSheetByIndex(XSSFWorkbook book, int sheetIndex) {
		// Excelからシートを削除
		book.removeSheetAt(sheetIndex);
	}

	/**
	 * シートを削除します
	 *
	 * @param name 削除したいシート名
	 */
	public static void removeSheetByName(XSSFWorkbook book, String name) {

		// 削除したいシートを取得
		Sheet sheet = book.getSheet(name);

		// Excelからシートを削除
		book.removeSheetAt(book.getSheetIndex(sheet));
	}

	/**
	 * 行の追加を行います。
	 *
	 * @param sheet
	 * @param rowIndex
	 * @return
	 */
	public static Row insertRow(Sheet sheet, int rowIndex) {

		int lastRowIndex = sheet.getLastRowNum();
		if (lastRowIndex < rowIndex) {
			// データが定義されている範囲外の場合は、行を新たに作成して返す。
			return CellUtil.getRow(rowIndex, sheet);
		}

		sheet.shiftRows(rowIndex, lastRowIndex + 1, 1);
		return CellUtil.getRow(rowIndex, sheet);

	}

	/**
	 * セルを作成します
	 *
	 * @param sheet シート情報
	 * @param column カラム名(A~...)
	 * @param rowNum 行番号
	 * @return 指定したセル
	 */
	public static Cell createCell(Sheet sheet, CellAddress cellAddress) {

		Row row = CellUtil.getRow(cellAddress.getRow(), sheet);
		Cell cell = CellUtil.getCell(row, cellAddress.getColumn());

		return cell;
	}

	/**
	 * セルを作成します
	 *
	 * @param sheet シート情報
	 * @param column カラム名(A~...)
	 * @param rowNum 行番号
	 * @param cellStyle セルのスタイル
	 * @return 指定したセル
	 */
	public static Cell createCell(Sheet sheet, String column, int rowNum) {
		CellAddress cellAddress = new CellAddress(CellReference.convertColStringToIndex(column), rowNum);
		Cell cell = createCell(sheet, cellAddress);
		return cell;
	}

	/**
	 * セルを作成します
	 *
	 * @param sheet シート情報
	 * @param column カラム名(A~...)
	 * @param rowNum 行番号
	 * @param cellStyle セルのスタイル
	 * @return 指定したセル
	 */
	public static Cell createCell(Sheet sheet, String column, int rowNum, CellStyle cellStyle) {
		CellAddress cellAddress = new CellAddress(CellReference.convertColStringToIndex(column), rowNum);
		Cell cell = createCell(sheet, cellAddress);
		cell.setCellStyle(cellStyle);
		return cell;
	}

	/**
	 * セルを作成します
	 *
	 * @param sheet シート情報
	 * @param column カラム名(A~...)
	 * @param rowNum 行番号
	 * @param cellStyle セルのスタイル
	 * @return 指定したセル
	 */
	public static Cell createCell(Sheet sheet, CellAddress cellAddress, CellStyle cellStyle) {
		Cell cell = createCell(sheet, cellAddress);
		cell.setCellStyle(cellStyle);
		return cell;
	}

}
