package jp.loioz.app.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.OutputConstant.OutputType;
import jp.loioz.dto.BuildExcelDto;
import jp.loioz.dto.ExcelItemDto;

/**
 * Viewの基底クラス
 */
public abstract class DefaultBuildExcelView extends AbstractXlsxView {

	public static final String FORMAT_NOT_FOUND = "フォーマットファイルが見つかりません。";
	public static final String MSG_E00018 = "ワークブックの作成に失敗しました。";
	public static final String FILE_OUTPUT_FAILED = "ファイルの出力に失敗しました。";

	/**
	 * Excelファイルを作成する
	 *
	 * @param dataMap Map<セル名,設定値>のMapをもらい、対象のExcelに設定する。
	 */
	public void buildExcel(BuildExcelDto buildExcelDto, String inputFilePath,
			String sheetName, Workbook book, String outputFileName, HttpServletResponse res) throws IOException {

		// 固定項目の設定値リストを取得
		List<ExcelItemDto> excelDataDtoList = buildExcelDto.getExcelItemDtoList();

		// 変更元を取込
		FileInputStream fis = new FileInputStream(inputFilePath);

		// WorkbookFactoryを使って作成
		book = (XSSFWorkbook) WorkbookFactory.create(fis);

		// 先頭シートを取得
		Sheet sheet = book.getSheet(sheetName);

		// 固定項目の設定
		this.setExcelItem(sheet, excelDataDtoList);

		if (buildExcelDto.isPrintAreaFlg()) {
			// 印刷範囲の設定
			book.setPrintArea(
					//印刷範囲を設定するシートのインデックス
					buildExcelDto.getPrintSheetIdx(),
					//印刷範囲開始位置のカラムのインデックス
					buildExcelDto.getPrintStartCol(),
					//印刷範囲終了位置のカラムのインデックス
					buildExcelDto.getPrintEndCol(),
					//印刷範囲開始位置の行のインデックス
					buildExcelDto.getPrintStartRow(),
					//印刷範囲終了位置の行のインデックス
					buildExcelDto.getPrintEndRow());
		}

		// ここから出力処理
		OutputStream out = null;
		try {
			// 出力先のファイルを指定
			res.setContentType(OutputConstant.BINARY_UTF8);
			res.setHeader("Content-Disposition", "attachment; filename=" + outputFileName);

			out = res.getOutputStream();
			// 上記で作成したブックを出力先に書き込み
			book.write(out);

		} finally {
			// 最後はちゃんと閉じておきます
			out.close();
			book.close();
		}
	}

	/**
	 * エクセル設定情報をシートに設定
	 *
	 * @param sheet シート
	 * @param excelDataDtoList エクセル設定データ
	 */
	protected void setExcelItem(Sheet sheet, List<ExcelItemDto> excelDataDtoList) {
		for (ExcelItemDto excelItemDto : excelDataDtoList) {

			CellReference reference = new CellReference(excelItemDto.getCell()); // A1形式

			Row row = sheet.getRow(reference.getRow());

			Cell cell;

			if (row != null) {
				cell = row.getCell(reference.getCol());
			} else {
				cell = sheet.createRow(reference.getRow()).createCell(reference.getCol());
			}

			// 値の設定
			this.setCellContent(cell, DefaultEnum.getEnum(OutputType.class, excelItemDto.getType()), excelItemDto.getValue());
			if (row != null) {
				// 行の高さの調整
				row.setHeightInPoints(excelItemDto.getHeight());
			}
		}
	}

	/**
	 * セルに値を設定します。
	 *
	 * @param cell
	 * @param celltype
	 * @param obj
	 */
	protected  void setCellContent(Cell cell, OutputType celltype, Object obj) {

		// 設定情報の何れかが未設定の場合、何も設定しない
		if (null == cell || null == obj || null == celltype) {
			return;
		}

		switch (celltype) {
		// 設定されている値の型に合わせてキャストを変更する
		case STR:
			// String型
			cell.setCellValue((String) obj);
			break;
		case NUM:
			// double型
			cell.setCellValue((Double) obj);
			break;
		case DATE:
			// Date型
			cell.setCellValue((Date) obj);
			break;
		case CAL:
			// Calendar型
			cell.setCellValue((Calendar) obj);
			break;
		case BOOL:
			// boolean型
			cell.setCellValue((Boolean) obj);
			break;
		case RICH:
			// RichTextString型
			cell.setCellValue((RichTextString) obj);
			break;
		}
	}
}
