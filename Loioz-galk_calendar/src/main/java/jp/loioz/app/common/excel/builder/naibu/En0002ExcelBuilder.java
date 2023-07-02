package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.ShukkinYoteiExcelDto;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ExcelShukkinData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 出金予定一覧のbuilderクラス
 */
@Setter
public class En0002ExcelBuilder extends AbstractMakeExcelBuilder<ShukkinYoteiExcelDto> {

	/** 出金予定一覧の表示開始行(Excel : 6行目) */
	public static final int START_ROW_IDX = 5;

	/**
	 * Excel内で扱うカラムの範囲を定義
	 */
	@Getter
	@AllArgsConstructor
	public enum ColumnAddress {

		// シートが増えるならここに追加
		SHUKKIN_LIST("A", "L"),;

		// 開始カラム
		private String stratCol;

		// 最終カラム
		private String endCol;
	}

	/**
	 * Builder用DTO
	 */
	private ShukkinYoteiExcelDto shukkinYoteiExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShukkinYoteiExcelDto createNewTargetBuilderDto() {
		return new ShukkinYoteiExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_2, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook book) {

		// 出力用データ
		ShukkinYoteiExcelDto shukkinYoteiExcelDto = this.shukkinYoteiExcelDto;

		// シート情報の取得
		Sheet sheet = book.getSheetAt(0);

		// タイトル
		setVal(sheet, 0, "A", shukkinYoteiExcelDto.getOutPutRange());

		// 出力日
		setVal(sheet, 2, "L", LocalDate.now());

		// 出金予定データを取得
		List<ExcelShukkinData> dataList = shukkinYoteiExcelDto.getExcelShukkinData();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, dataList.size());

		int rowIdx = START_ROW_IDX;
		for (ExcelShukkinData dto : dataList) {
			// データの設定
			int colIdx = 0;
			setVal(sheet, rowIdx, colIdx++, dto.getCustomerId());
			setVal(sheet, rowIdx, colIdx++, dto.getCustomerName());
			setVal(sheet, rowIdx, colIdx++, dto.getAnkenId());
			setVal(sheet, rowIdx, colIdx++, StringUtils.isEmpty(dto.getAnkenName()) ? "(案件名未入力)" : dto.getAnkenName());
			setVal(sheet, rowIdx, colIdx++, dto.getDispTantoLawer());
			setVal(sheet, rowIdx, colIdx++, dto.getDispTantoJimu());
			setVal(sheet, rowIdx, colIdx++, dto.getShukkinKoza());
			setVal(sheet, rowIdx, colIdx++, dto.getShiharaiSaki());
			setVal(sheet, rowIdx, colIdx++, dto.getShiharaiLimitDate());
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getSeisanGaku()));
			setVal(sheet, rowIdx, colIdx++, dto.getSeisanId());
			setVal(sheet, rowIdx, colIdx++, dto.getJotai());

			rowIdx++;
		}

	}

}
