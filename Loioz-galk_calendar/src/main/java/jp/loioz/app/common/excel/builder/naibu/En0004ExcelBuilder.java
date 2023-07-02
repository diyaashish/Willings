package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.AzukarikinListExcelDto;
import jp.loioz.app.user.azukarikinManagement.form.ExcelAzukarikinData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 未収入金一覧のbuilderクラス
 */
@Setter
public class En0004ExcelBuilder extends AbstractMakeExcelBuilder<AzukarikinListExcelDto> {

	/** 未収入金一覧の表示開始行(Excel : 6行目) */
	public static final int START_ROW_IDX = 5;

	/**
	 * Excel内で扱うカラムの範囲を定義
	 */
	@Getter
	@AllArgsConstructor
	public enum ColumnAddress {

		// シートが増えるならここに追加
		AZUKARIKIN_LIST("A", "O"),;

		// 開始カラム
		private String stratCol;

		// 最終カラム
		private String endCol;
	}

	/**
	 * Builder用DTO
	 */
	private AzukarikinListExcelDto azukarikinListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AzukarikinListExcelDto createNewTargetBuilderDto() {
		return new AzukarikinListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_4, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook book) {

		// シート情報を取得
		Sheet sheet = book.getSheetAt(0);

		// 出力日
		setVal(sheet, 2, "P", LocalDate.now());

		// 未収入金一覧を取得
		List<ExcelAzukarikinData> dataList = this.azukarikinListExcelDto.getExcelAzukarikinData();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, dataList.size());

		// 預り金一覧のデータをセットする
		int rowIdx = START_ROW_IDX;
		for (ExcelAzukarikinData dto : dataList) {
			// データの設定
			int colIdx = 0;
			setVal(sheet, rowIdx, colIdx++, dto.getCustomerId().asLong());
			setVal(sheet, rowIdx, colIdx++, dto.getCustomerName());
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getDispTotalCustomerKaikei()));
			setVal(sheet, rowIdx, colIdx++, dto.getAnkenId().asLong());
			setVal(sheet, rowIdx, colIdx++, dto.getBunya().getVal().toString());
			setVal(sheet, rowIdx, colIdx++, StringUtils.isEmpty(dto.getAnkenName()) ? "(案件名未入力)" : dto.getAnkenName());
			setVal(sheet, rowIdx, colIdx++, dto.getAnkenStatus().getVal());
			setVal(sheet, rowIdx, colIdx++, dto.getSalesOwnerName());
			setVal(sheet, rowIdx, colIdx++, dto.getTantoLawyerName());
			setVal(sheet, rowIdx, colIdx++, dto.getTantoZimuName());
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getDispTotalAnkenKaikei()));
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getDispTotalNyukin()));
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getDispHoshu()));
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getDispTotalShukkin()));
			setVal(sheet, rowIdx, colIdx++, DateUtils.parseToString(dto.getJuninDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			setVal(sheet, rowIdx, colIdx++, DateUtils.parseToString(dto.getLastNyukinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			rowIdx++;
		}

	}

}
