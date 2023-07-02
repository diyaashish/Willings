package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.NyukinYoteiExcelDto;
import jp.loioz.app.user.nyushukkinYotei.nyukin.form.ExcelNyukinData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 入出金予定一覧(入金)のbuilderクラス
 */
@Setter
public class En0003ExcelBuilder extends AbstractMakeExcelBuilder<NyukinYoteiExcelDto> {

	/** 入金予定一覧の表示開始行(Excel : 6行目) */
	public static final int START_ROW_IDX = 5;

	/**
	 * Excel内で扱うカラムの範囲を定義
	 */
	@Getter
	@AllArgsConstructor
	public enum ColumnAddress {

		// シートが増えるならここに追加
		NYUKIN_LIST("A", "M"),;

		// 開始カラム
		private String stratCol;

		// 最終カラム
		private String endCol;
	}

	/**
	 * Builder用DTO
	 */
	private NyukinYoteiExcelDto nyushukkinYoteiExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NyukinYoteiExcelDto createNewTargetBuilderDto() {
		return new NyukinYoteiExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_3, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook book) {

		// 出力用データ
		NyukinYoteiExcelDto nyukinYoteiExcelDto = this.nyushukkinYoteiExcelDto;

		// シート情報の取得
		Sheet sheet = book.getSheetAt(0);

		// 一覧の表示範囲を出力する
		setVal(sheet, 0, "A", nyukinYoteiExcelDto.getOutPutRange());

		// 出力日
		setVal(sheet, 2, "M", LocalDate.now());

		// 入金予定データを取得
		List<ExcelNyukinData> dataList = nyukinYoteiExcelDto.getExcelNyukinData();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, dataList.size());

		int rowIdx = START_ROW_IDX;
		for (ExcelNyukinData dto : dataList) {
			// データの設定
			int colIdx = 0;
			setVal(sheet, rowIdx, colIdx++, dto.getCustomerId());
			setVal(sheet, rowIdx, colIdx++, dto.getCustomerName());
			setVal(sheet, rowIdx, colIdx++, dto.getAnkenId());
			setVal(sheet, rowIdx, colIdx++, StringUtils.isEmpty(dto.getAnkenName()) ? "(案件名未入力)" : dto.getAnkenName());
			setVal(sheet, rowIdx, colIdx++, dto.getDispTantoLawer());
			setVal(sheet, rowIdx, colIdx++, dto.getDispTantoJimu());
			setVal(sheet, rowIdx, colIdx++, dto.getSeisanSaki());
			setVal(sheet, rowIdx, colIdx++, dto.getNyukinKoza());
			setVal(sheet, rowIdx, colIdx++, dto.getNyukinYoteiDate());
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getNyukinYoteiGaku()));
			setVal(sheet, rowIdx, colIdx++, Double.parseDouble(dto.getZankin()));
			setVal(sheet, rowIdx, colIdx++, dto.getSeisanId());
			setVal(sheet, rowIdx, colIdx++, dto.getJotai());

			rowIdx++;
		}

	}

}
