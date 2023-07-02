package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.NyushukkinMeisaiExcelDto;
import jp.loioz.app.user.kaikeiManagement.form.ExcelNyushukkinMeisaiData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.ExcelUtils;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 入出金明細のビルダークラス
 *
 */
@Setter
public class En0006ExcelBuilder extends AbstractMakeExcelBuilder<NyushukkinMeisaiExcelDto> {

	/** 入出金明細の一覧表示開始行(Excel : 5行目) */
	public static final int LIST_START_ROW = 4;

	/** 案件名が1行で表示できる長さ */
	private static final int LENGTH_OF_ANKEN_NAME_THAT_FITS_IN_ONE_CELL = 110;

	private static final String CUSTOMER_ID = "名簿ID";

	private static final String CUSTOMER_NAME = "顧客名";

	/**
	 * Excel内で扱うカラムの範囲を定義
	 */
	@Getter
	@AllArgsConstructor
	public enum ColumnAddress {

		// シートが増えるならここに追加
		Nyushukkin_MEISAI("A", "M"),;

		// 開始カラム
		private String startCol;

		// 最終カラム
		private String endCol;
	}

	/**
	 * Builder用DTO
	 */
	private NyushukkinMeisaiExcelDto nyushukkinMeisaiExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NyushukkinMeisaiExcelDto createNewTargetBuilderDto() {
		return new NyushukkinMeisaiExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_6, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook book) {

		Sheet sheet = book.getSheetAt(0);

		// 表示顧客 or 案件をセット(B2)
		Cell dispHeaderCell = ExcelUtils.createCell(sheet, new CellAddress("B2"));
		dispHeaderCell.setCellValue(this.nyushukkinMeisaiExcelDto.getHeaderCellValue());
		// B列の文字数が長い場合は行の高さを増やす
		if (this.nyushukkinMeisaiExcelDto.getHeaderCellValue().length() > LENGTH_OF_ANKEN_NAME_THAT_FITS_IN_ONE_CELL) {
			sheet.getRow(1).setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		}


		// ***********************************
		// 表示顧客 or 表示案件
		// ***********************************
		CellStyle clacTotal = book.createCellStyle();
		clacTotal.cloneStyleFrom(sheet.getRow(5).getCell(0).getCellStyle());

		// ***********************************
		// データの設定
		// ***********************************
		int countRow = 0;
		Row rowOne = sheet.getRow(1);

		// 顧客軸の場合
		if (CommonConstant.TransitionType.CUSTOMER.equals(nyushukkinMeisaiExcelDto.getTransitonType())) {
			// 顧客ID入力
			Cell customerIdCell = rowOne.getCell(0);
			customerIdCell.setCellValue(this.nyushukkinMeisaiExcelDto.getCustomerId().toString());
			// 顧客名入力
			Cell customerNameCell = rowOne.getCell(1);
			customerNameCell.setCellValue(this.nyushukkinMeisaiExcelDto.getCustomerName());

		} else if (CommonConstant.TransitionType.ANKEN.equals(nyushukkinMeisaiExcelDto.getTransitonType())) {
			// 案件ID入力
			Cell ankenIdCell = rowOne.getCell(0);
			ankenIdCell.setCellValue(this.nyushukkinMeisaiExcelDto.getAnkenId().toString());
			// 案件名入力
			Cell ankenNameCell = rowOne.getCell(1);
			ankenNameCell.setCellValue(StringUtils.isEmpty(this.nyushukkinMeisaiExcelDto.getAnkenName()) ? "(案件名未入力)" : this.nyushukkinMeisaiExcelDto.getAnkenName());
			// 案件IDに列名変更
			Row changeName = sheet.getRow(LIST_START_ROW - 1);
			int changeCellNum = 4;
			// 案件名に列名変更
			changeName.getCell(changeCellNum).setCellValue(CUSTOMER_ID);
			changeName.getCell(changeCellNum + 1).setCellValue(CUSTOMER_NAME);

		} else {
			// ここには来ない想定
		}

		// 日付の入力
		Cell dateCell = rowOne.getCell(8);
		dateCell.setCellValue(LocalDate.now());

		for (ExcelNyushukkinMeisaiData data : this.nyushukkinMeisaiExcelDto.getExcelNyushukkinMeisaiData()) {

			// Rowの取得
			Row row;
			if (0 < countRow) {
				row = ExcelUtils.insertRow(sheet, LIST_START_ROW + countRow);
			} else {
				row = CellUtil.getRow(LIST_START_ROW + countRow, sheet);
			}

			// CELLの取得と設定
			for (int i = 0; i < ExcelNyushukkinMeisaiData.COLUMN_NUM; i++) {
				Cell cell = CellUtil.getCell(row, i);
				String val = data.getCellValue(i, this.nyushukkinMeisaiExcelDto.getTransitonType());
				if (i == 1 || i == 2) {
					// i == 1：入金額、i == 2：出金額の場合
					if (StringUtils.isNotEmpty(val)) {
						cell.setCellValue(Double.parseDouble(val));
					}

				} else {
					// その他
					cell.setCellValue(val);
				}
				cell.setCellStyle(sheet.getRow(LIST_START_ROW).getCell(i).getCellStyle());
			}

			countRow++;
		}

		int totalRowIndex = LIST_START_ROW + countRow;

		if (countRow == 0) {
			// 出力する内容がない場合は、未入力の行を表示するため +1 する
			totalRowIndex++;
		}

		// 合計行の取得
		Row totalRow = CellUtil.getRow(totalRowIndex, sheet);

		// 入金列の合計
		Cell totalNyukingakuCell = totalRow.getCell(1);
		String totalNyukingaku = StringUtils.removeChars(this.nyushukkinMeisaiExcelDto.getTotalNyukingaku(),
				CommonConstant.COMMA);
		if (StringUtils.isNotEmpty(totalNyukingaku)) {
			totalNyukingakuCell.setCellValue(Double.parseDouble(totalNyukingaku));
		}

		// 出金列の合計
		Cell totalShukkingakuCell = totalRow.getCell(2);
		String totalShukkingaku = StringUtils.removeChars(this.nyushukkinMeisaiExcelDto.getTotalShukkingaku(),
				CommonConstant.COMMA);
		if (StringUtils.isNotEmpty(totalShukkingaku)) {
			totalShukkingakuCell.setCellValue(Double.parseDouble(totalShukkingaku));
		}

		// 差引合計行の取得
		totalRow = CellUtil.getRow(totalRowIndex + 1, sheet);
		Cell target = totalRow.getCell(1);

		// 数値のみを抽出して出力
		BigDecimal totalN = new BigDecimal(totalNyukingaku);
		BigDecimal totalS = new BigDecimal(totalShukkingaku);
		BigDecimal sashihikiTotal = totalN.subtract(totalS);

		if (sashihikiTotal != null) {
			// 差し引き合計
			target.setCellValue(sashihikiTotal.doubleValue());
		}

	}

}
