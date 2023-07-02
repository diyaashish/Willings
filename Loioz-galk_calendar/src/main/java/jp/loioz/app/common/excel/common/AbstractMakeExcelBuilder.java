package jp.loioz.app.common.excel.common;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;

import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * Excel出力用抽象クラス
 */

@Data
public abstract class AbstractMakeExcelBuilder<T> {

	/**
	 * 設定クラス
	 */
	private ExcelConfig config;

	/**
	 * 生成するExcelファイル定義
	 */
	private OutputExcelTemplate formTemplate;

	/**
	 * Excelファイル名<br>
	 *
	 * ※ファイル名を指定しない場合は、ChohyoConstantで定義したファイルとなる
	 */
	private String fileName;

	/**
	 * 指定テンプレートExcelファイルダウンロード用<br>
	 *
	 * ※Builder内ではAutowiredできないので代用
	 */
	protected FileStorageService fileStorageService;

	/** メッセージサービス */
	MessageService messageService;

	/**
	 * 対応するBuilderのDTOを生成する
	 *
	 * @return 対応するBuilderのDTO
	 */
	public abstract T createNewTargetBuilderDto();

	/**
	 * ダウンロード対象のデータ件数が適切か判定する<br>
	 * 
	 * @param dataCount
	 * @param response
	 * @return true:ダウンロード可能　false:ダウンロード不可
	 */
	public boolean validDataCountOver(int dataCount, HttpServletResponse response) {
		if (dataCount > OutputConstant.MAX_EXCEL_OUTPUT_DATA) {
			String message = messageService.getMessage(MessageEnum.MSG_E00158, Locale.JAPANESE, String.valueOf(OutputConstant.MAX_EXCEL_OUTPUT_DATA));

			// ダウンロード可能件数を超えている場合はエラーメッセージをセットする
			Charset charset = StandardCharsets.UTF_8;
			String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes(charset));

			response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT_MESSAGE, encodedMessage);
			return false;
		}
		return true;
	}

	/**
	 * 指定のフォーマットのExcelファイルを生成する
	 *
	 * @param HttpServletResponse
	 */
	protected void makeExcelFile(OutputExcelTemplate formTemplate, HttpServletResponse response) throws Exception {

		this.formTemplate = formTemplate;

		// ファイル名取得
		String fileName = this.formTemplate.getCd();
		// ファイルパス
		String dirPath = this.formTemplate.getDirPath();
		// ファイル拡張子
		String extension = this.formTemplate.getFileType();

		// ファイルパス生成
		String excelRoot = this.config.getExcelRoot();
		String inputFilePath = excelRoot + dirPath + fileName + extension;

		try (FileInputStream fileInputStream = new FileInputStream(inputFilePath);
			XSSFWorkbook doc = new XSSFWorkbook(fileInputStream);) {

			// データを設定する
			this.setExcelData(doc);

			// 作成したワークブックをダウンロード可能な状態にする
			this.makeDownloadableState(doc, response);
		}

	}

	/**
	 * Excelファイルに出力データを設定する
	 *
	 * @param doc
	 */
	protected abstract void setExcelData(XSSFWorkbook doc) throws Exception;

	/**
	 * 作成したExcelファイルをダウンロード可能な状態にする
	 *
	 * @param doc 作成したWordドキュメント
	 * @param HttpServletResponse
	 * @throws Exception
	 */
	protected void makeDownloadableState(XSSFWorkbook doc, HttpServletResponse response) throws Exception {
		// 出力日の取得
		String date = DateUtils.parseToString(LocalDateTime.now(),
				DateUtils.DATE_YYYY + DateUtils.DATE_MM + DateUtils.DATE_DD + DateUtils.TIME_FORMAT_HH + DateUtils.TIME_FORMAT_MM
						+ DateUtils.TIME_FORMAT_SS);

		String fineName = this.fileName;
		if (StringUtils.isEmpty(fineName)) {
			fineName = this.formTemplate.getVal();
		}
		String encodeFileName = fineName + CommonConstant.UNDER_BAR + date + this.formTemplate.getFileType();

		// ファイルダウンロードの準備
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename*=" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()));

		doc.write(response.getOutputStream());
	}

	/**
	 * 行削除
	 *
	 * @param sheet
	 * @param rowIdx
	 */
	protected void deleteRow(Sheet sheet, int rowIdx) {
		sheet.removeRow(CellUtil.getRow(rowIdx, sheet));
	}

	/**
	 * 行削除
	 *
	 * @param sheet
	 * @param rowIdx
	 */
	protected void deleteRowShift(Sheet sheet, int rowIdx) {
		int lastRow = sheet.getLastRowNum();
		sheet.shiftRows(rowIdx + 1, lastRow, -1);
	}

	/**
	 * エクセルに行追加
	 *
	 * @param destinationRowNum 追加する行の番号
	 * @param startRow コピー元の行番号
	 * @param workbook
	 * @param sheet
	 */
	protected void addRow(int destinationRowNum, int startRow, Workbook workbook, Sheet sheet) {
		// コピー元行のセルの型、スタイル、値などをすべてコピーする
		List<CellStyle> cellStyleList = getCellStyleList(startRow, workbook, sheet);
		this.addRow(destinationRowNum, startRow, sheet, true, cellStyleList);
	}

	/**
	 * エクセルに行追加
	 *
	 * @param destinationRowNum 追加する行の番号
	 * @param startRow コピー元の行番号
	 * @param sheet
	 * @param cellStyleList
	 */
	protected void addRow(int destinationRowNum, int startRow, Sheet sheet, boolean formatCopyFlg, List<CellStyle> cellStyleList) {

		// コピー元の行を取得
		Row sourceRow = sheet.getRow(startRow);

		// コピー先に行が既に存在する場合、１行下にずらす
		sheet.shiftRows(destinationRowNum, sheet.getLastRowNum(), 1);

		// 行追加
		Row newRow = sheet.createRow(destinationRowNum);
		if (formatCopyFlg) {
			// 行の高さコピー
			newRow.setHeight(sourceRow.getHeight());
		}

		// セルの型、スタイル、値などをすべてコピーする
		for (int i = 0; i < cellStyleList.size(); i++) {
			Cell newCell = newRow.createCell(i);
			newCell.setCellStyle(cellStyleList.get(i));
		}

	}

	/**
	 * エクセルに複数行追加（行の書式コピーする場合）
	 *
	 * @param destinationRowNum 追加する行の番号
	 * @param startRow コピー元の行番号
	 * @param workbook
	 * @param sheet
	 * @param addRows 追加する行数
	 */
	protected void addRowsFormatCopy(int destinationRowNum, int startRow, Workbook workbook, Sheet sheet, int addRows) {
		this.addRows(destinationRowNum, startRow, workbook, sheet, addRows, true);
	}

	/**
	 * エクセルに複数行追加
	 *
	 * @param destinationRowNum 追加する行の番号
	 * @param startRow コピー元の行番号
	 * @param workbook
	 * @param sheet
	 * @param addRows
	 */
	protected void addRows(int destinationRowNum, int startRow, Workbook workbook, Sheet sheet, int addRows) {
		this.addRows(destinationRowNum, startRow, workbook, sheet, addRows, false);
	}

	/**
	 * 行追加
	 *
	 * @param destinationRowNum 追加する行の番号
	 * @param startRow コピー元の行番号
	 * @param workbook
	 * @param sheet
	 * @param addRows
	 * @param formatCopyFlg
	 */
	private void addRows(int destinationRowNum, int startRow, Workbook workbook, Sheet sheet, int addRows, boolean formatCopyFlg) {

		// コピー元行のセルの型、スタイル、値などをすべてコピーする
		List<CellStyle> cellStyleList = getCellStyleList(startRow, workbook, sheet);

		// データの件数分の行を追加（コピー元の行はそのまま使用するため、-1する）
		for (int i = 0; i < addRows - 1; i++) {
			addRow(destinationRowNum + i, startRow, sheet, formatCopyFlg, cellStyleList);
		}
	}

	/**
	 * コピー元行のセルのスタイルをリストで取得する
	 * 
	 * @param startRow コピー元の行番号
	 * @param workbook
	 * @param sheet
	 * @return
	 */
	private List<CellStyle> getCellStyleList(int startRow, Workbook workbook, Sheet sheet) {
		// コピー元の行取得
		Row sourceRow = sheet.getRow(startRow);

		// セルの型、スタイル、値などをすべてコピーする
		List<CellStyle> cellStyleList = new ArrayList<>();
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			Cell oldCell = sourceRow.getCell(i);

			// コピー元の行が存在しない場合、処理を中断
			if (oldCell == null) {
				continue;
			}

			// スタイルのコピー
			CellStyle newCellStyle = workbook.createCellStyle();
			newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
			cellStyleList.add(newCellStyle);
		}

		return cellStyleList;
	}

	/**
	 * セルにデータ設定
	 *
	 * @param sheet 対象のシート
	 * @param rowIdx 行番号
	 * @param colIdx 列番号
	 * @param value 書き込む値
	 */
	protected void setVal(Sheet sheet, int rowIdx, int colIdx, double value) {
		sheet.getRow(rowIdx).getCell(colIdx).setCellValue(value);
	}

	/**
	 * セルにデータ設定
	 *
	 * @param sheet 対象のシート
	 * @param rowIdx 行番号
	 * @param colIdx 列番号
	 * @param value 書き込む値
	 */
	protected void setVal(Sheet sheet, int rowIdx, String colAlphabet, double value) {
		int colIdx = CellReference.convertColStringToIndex(colAlphabet);
		setVal(sheet, rowIdx, colIdx, value);
	}

	/**
	 * @param sheet
	 * @param rowIdx
	 * @param colIdx
	 * @param value
	 */
	protected void setVal(Sheet sheet, int rowIdx, int colIdx, Date value) {
		sheet.getRow(rowIdx).getCell(colIdx).setCellValue(value);
	}

	protected void setVal(Sheet sheet, int rowIdx, String colAlphabet, Date value) {
		int colIdx = CellReference.convertColStringToIndex(colAlphabet);
		setVal(sheet, rowIdx, colIdx, value);
	}

	/**
	 * @param sheet
	 * @param rowIdx
	 * @param colIdx
	 * @param value
	 */
	protected void setVal(Sheet sheet, int rowIdx, int colIdx, LocalDateTime value) {
		sheet.getRow(rowIdx).getCell(colIdx).setCellValue(value);
	}

	protected void setVal(Sheet sheet, int rowIdx, String colAlphabet, LocalDateTime value) {
		int colIdx = CellReference.convertColStringToIndex(colAlphabet);
		setVal(sheet, rowIdx, colIdx, value);
	}

	/**
	 * @param sheet
	 * @param rowIdx
	 * @param colIdx
	 * @param value
	 */
	protected void setVal(Sheet sheet, int rowIdx, int colIdx, LocalDate value) {
		sheet.getRow(rowIdx).getCell(colIdx).setCellValue(value);
	}

	protected void setVal(Sheet sheet, int rowIdx, String colAlphabet, LocalDate value) {
		int colIdx = CellReference.convertColStringToIndex(colAlphabet);
		setVal(sheet, rowIdx, colIdx, value);
	}

	/**
	 * @param sheet
	 * @param rowIdx
	 * @param colIdx
	 * @param value
	 */
	protected void setVal(Sheet sheet, int rowIdx, int colIdx, Calendar value) {
		sheet.getRow(rowIdx).getCell(colIdx).setCellValue(value);
	}

	protected void setVal(Sheet sheet, int rowIdx, String colAlphabet, Calendar value) {
		int colIdx = CellReference.convertColStringToIndex(colAlphabet);
		setVal(sheet, rowIdx, colIdx, value);
	}

	/**
	 * @param sheet
	 * @param rowIdx
	 * @param colIdx
	 * @param value
	 */
	protected void setVal(Sheet sheet, int rowIdx, int colIdx, RichTextString value) {
		sheet.getRow(rowIdx).getCell(colIdx).setCellValue(value);
	}

	protected void setVal(Sheet sheet, int rowIdx, String colAlphabet, RichTextString value) {
		int colIdx = CellReference.convertColStringToIndex(colAlphabet);
		setVal(sheet, rowIdx, colIdx, value);
	}

	/**
	 * @param sheet
	 * @param rowIdx
	 * @param colIdx
	 * @param value
	 */
	protected void setVal(Sheet sheet, int rowIdx, int colIdx, String value) {
		sheet.getRow(rowIdx).getCell(colIdx).setCellValue(value);
	}

	protected void setVal(Sheet sheet, int rowIdx, String colAlphabet, String value) {
		int colIdx = CellReference.convertColStringToIndex(colAlphabet);
		setVal(sheet, rowIdx, colIdx, value);
	}

}
