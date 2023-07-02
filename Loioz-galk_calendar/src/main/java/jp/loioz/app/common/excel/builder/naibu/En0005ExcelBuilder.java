package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.HoshuMeisaiExcelDto;
import jp.loioz.app.user.kaikeiManagement.form.ExcelHoshuMeisaiData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 報酬明細一覧のbuilderクラス
 */
@Setter
public class En0005ExcelBuilder extends AbstractMakeExcelBuilder<HoshuMeisaiExcelDto> {

	/** 報酬明細の一覧表示開始行(Excel : 5行目) */
	public static final int START_ROW_IDX = 4;

	/** 案件名が1行で表示できる長さ */
	private static final int LENGTH_OF_ANKEN_NAME_THAT_FITS_IN_ONE_CELL = 123;

	/**
	 * Excel内で扱うカラムの範囲を定義
	 */
	@Getter
	@AllArgsConstructor
	public enum ColumnAddress {

		// シートが増えるならここに追加
		HOSHU_MEISAI("A", "M"),;

		// 開始カラム
		private String stratCol;

		// 最終カラム
		private String endCol;
	}

	/**
	 * Builder用DTO
	 */
	private HoshuMeisaiExcelDto hoshuMeisaiExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HoshuMeisaiExcelDto createNewTargetBuilderDto() {
		return new HoshuMeisaiExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_5, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook book) {

		Sheet sheet;
		HoshuMeisaiExcelDto hoshuMesaiExcelDto = this.hoshuMeisaiExcelDto;
		if (TransitionType.CUSTOMER == hoshuMesaiExcelDto.getTransitonType()) {
			sheet = book.getSheetAt(0);
			book.removeSheetAt(1);
			// 出力日
			setVal(sheet, 1, "K", LocalDate.now());
		} else {
			sheet = book.getSheetAt(1);
			book.removeSheetAt(0);
			// 出力日
			setVal(sheet, 1, "K", LocalDate.now());
		}
		// 表示顧客 or 案件をセット
		setVal(sheet, 1, "A", hoshuMesaiExcelDto.getHeaderCellValueId());
		setVal(sheet, 1, "B", hoshuMesaiExcelDto.getHeaderCellValue());
		// B列の文字数が長い場合は行の高さを増やす
		if (hoshuMesaiExcelDto.getHeaderCellValue().length() > LENGTH_OF_ANKEN_NAME_THAT_FITS_IN_ONE_CELL) {
			sheet.getRow(1).setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		}
		// 報酬一覧のデータをセットする

		List<ExcelHoshuMeisaiData> hoshuMesaidataList = this.hoshuMeisaiExcelDto.getExcelHoshuMeisaiData();
		if (CollectionUtils.isEmpty(hoshuMesaidataList)) {
			return;
		}

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, hoshuMesaidataList.size());

		int rowIdx = START_ROW_IDX;
		for (ExcelHoshuMeisaiData data : hoshuMesaidataList) {
			// データの設定
			int colIdx = 0;
			setVal(sheet, rowIdx, colIdx++, data.getHasseiDate());
			setVal(sheet, rowIdx, colIdx++, data.getHoshuKomoku().getVal());
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(data.getDispHoshuGaku()));
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(data.getDispTaxGaku()));

			if (BigDecimal.ZERO.equals(data.getGensenGaku())) {
				setVal(sheet, rowIdx, colIdx++, "-");
			} else {
				setVal(sheet, rowIdx, colIdx++, Double.parseDouble(data.getDispGensenGaku()));
			}
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(data.getDispTotal()));

			if (TransitionType.ANKEN == hoshuMesaiExcelDto.getTransitonType()) {
				setVal(sheet, rowIdx, colIdx++, data.getCustomerId().asLong());
				setVal(sheet, rowIdx, colIdx++, data.getCustomerName());
			} else {
				setVal(sheet, rowIdx, colIdx++, data.getAnkenId().asLong());
				setVal(sheet, rowIdx, colIdx++, StringUtils.isEmpty(data.getAnkenName()) ? "(案件名未入力)" : data.getAnkenName());
			}
			setVal(sheet, rowIdx, colIdx++, data.getTekiyo());
			setVal(sheet, rowIdx, colIdx++, data.getSeisanDate());
			setVal(sheet, rowIdx, colIdx++, data.getSeisanId());

			rowIdx++;
		}

		setVal(sheet, rowIdx, "C", Double.parseDouble(hoshuMesaiExcelDto.getDispHoshuGakuTotal()));
		setVal(sheet, rowIdx, "D", Double.parseDouble(hoshuMesaiExcelDto.getDispTaxGakuTotal()));
		if (BigDecimal.ZERO.equals(hoshuMesaiExcelDto.getGensenGakuTotal())) {
			setVal(sheet, rowIdx, "E", "-");
		} else {
			setVal(sheet, rowIdx, "E", Double.parseDouble(hoshuMesaiExcelDto.getDispGensenGakuTotal()));
		}
		setVal(sheet, rowIdx, "F", Double.parseDouble(hoshuMesaiExcelDto.getDispHoshuTotal()));

	}
}
