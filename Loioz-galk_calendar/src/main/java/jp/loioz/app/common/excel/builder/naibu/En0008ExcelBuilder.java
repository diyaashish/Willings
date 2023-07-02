package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.ExcelTimeChargeDto;
import jp.loioz.dto.ExcelTimeChargeListDto;
import lombok.Setter;

/**
 * タイムチャージ計算書のExcelBuilder
 */
@Setter
public class En0008ExcelBuilder extends AbstractMakeExcelBuilder<ExcelTimeChargeDto> {

	/** タイムチャージ計算書の表示開始行(Excel : 6行目) */
	public static final int START_ROW_IDX = 5;

	/** 標準の最大データ行数 */
	public static final int MAX_DATA_ROW_SIZE = 23;

	/** テンプレートの行数 */
	public static final int DEFAULT_LAST_ROW_NUM = 27;

	/** 合計の行位置 */
	public static final int SUM_ROW_NUM_IDX = 29;

	/**
	 * Builder用DTO
	 */
	private ExcelTimeChargeDto timeChargeData;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExcelTimeChargeDto createNewTargetBuilderDto() {
		return new ExcelTimeChargeDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_8, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook workbook) {
		// シート情報の取得
		Sheet sheet = workbook.getSheetAt(0);

		// タイムチャージデータ
		ExcelTimeChargeDto timeChargeData = this.timeChargeData;

		// 出力日
		setVal(sheet, 1, "E", LocalDate.now());

		// 宛名を設定
		setVal(sheet, 2, "A", StringUtils.defaultString(timeChargeData.getName()));

		// タイムチャージリスト設定
		List<ExcelTimeChargeListDto> dataList = timeChargeData.getTimeChargeList();
		int timeChargeListSize = dataList.size();

		int addRowSize = 0;
		if (timeChargeListSize > MAX_DATA_ROW_SIZE) {
			// タイムチャージの実績 > 帳票のテンプレートの行数より多い場合：行を追加
			addRowSize = timeChargeListSize - MAX_DATA_ROW_SIZE;

			// 行追加
			addRowsFormatCopy(DEFAULT_LAST_ROW_NUM, START_ROW_IDX, workbook, sheet, addRowSize + 1);

		}

		int rowIdx = START_ROW_IDX;
		for (ExcelTimeChargeListDto dto : dataList) {
			// データの設定
			int colIdx = 0;

			// 日付
			if (dto.getHasseiDate() != null) {
				setVal(sheet, rowIdx, colIdx++, dto.getHasseiDate());
			} else {
				colIdx++;
			}
			
			// 時間(From~To)
			String time = CommonConstant.BLANK;
			if (StringUtils.isNoneEmpty(dto.getTimeChargeStartTime()) || StringUtils.isNoneEmpty(dto.getTimeChargeEndTime())) {
				time = dto.getTimeChargeStartTime().toString().substring(11)
						+ "～"
						+ dto.getTimeChargeEndTime().toString().substring(11);
			}
			setVal(sheet, rowIdx, colIdx++, time);
			
			// 活動設定
			setVal(sheet, rowIdx, colIdx++, dto.getKatsudo());
			// 時間設定(分)
			setVal(sheet, rowIdx, colIdx++, dto.getTime());
			// 単価設定
			setVal(sheet, rowIdx, colIdx++, dto.getTimeChargeTanka().doubleValue());
			// 小計設定
			setVal(sheet, rowIdx, colIdx++, dto.getShukkinGaku().doubleValue());

			rowIdx++;
		}

		// 合計時間
		setVal(sheet, SUM_ROW_NUM_IDX + addRowSize, "E", timeChargeData.getTotalTime());
		// 報酬額
		setVal(sheet, SUM_ROW_NUM_IDX + addRowSize + 1, "E", timeChargeData.getHoshuTotal().doubleValue());
	}

}
