package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.AnkenListExcelDto;
import jp.loioz.app.common.excel.dto.AnkenListExcelDto.ExcelAnkenListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 案件一覧出力用クラス
 *
 */
@Setter
public class En0010ExcelBuilder extends AbstractMakeExcelBuilder<AnkenListExcelDto> {

	/** 案件一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "案件情報";

	/** タイトル名 */
	public static final String TITLE_NAME = "案件情報";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		ANKEN_ID(0, "案件ID"),
		BUNYA(1, "分野"),
		ANKEN_NAME(2, "案件名"),
		CUSTOMER_NAME(3, "顧客名"),
		AITEGATA(4, "相手方"),
		SHINTYOKU(5, "顧客ステータス"),
		TANTO_LAWYER_NAME1(6, "担当弁護士１"),
		TANTO_LAWYER_NAME2(7, "担当弁護士２"),
		TANTO_LAWYER_NAME3(8, "担当弁護士３"),
		TANTO_LAWYER_NAME4(9, "担当弁護士４"),
		TANTO_LAWYER_NAME5(10, "担当弁護士５"),
		TANTO_LAWYER_NAME6(11, "担当弁護士６"),
		TANTO_JIMU1(12, "担当事務１"),
		TANTO_JIMU2(13, "担当事務２"),
		TANTO_JIMU3(14, "担当事務３"),
		TANTO_JIMU4(15, "担当事務４"),
		TANTO_JIMU5(16, "担当事務５"),
		TANTO_JIMU6(17, "担当事務６"),
		JYUNIN_DATE(18, "受任日"),
		ANKEN_CREATE_CREATE_DATE(19, "登録日"),
		;

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private AnkenListExcelDto ankenListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnkenListExcelDto createNewTargetBuilderDto() {
		return new AnkenListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_10, response);
	}

	/**
	 * Excelファイルに出力データを設定する
	 *
	 * @param workbook
	 */
	@Override
	public void setExcelData(XSSFWorkbook workbook) throws Exception {

		// シート情報の取得
		Sheet sheet = workbook.getSheetAt(0);

		// A1タイトル設定
		setVal(sheet, 0, "A", TITLE_NAME);

		// 出力日
		setVal(sheet, 0, "T", LocalDate.now());

		// データ部分作成
		makeBody(workbook, sheet);

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 一覧出力のデータ部分を作成する
	 * 
	 * @param book
	 * @param sheet
	 */
	private void makeBody(XSSFWorkbook book, Sheet sheet) throws IOException {

		// 案件一覧データ取得
		List<ExcelAnkenListRowData> excelAnkenDataList = ankenListExcelDto.getAnkenListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, excelAnkenDataList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelAnkenListRowData rowData : excelAnkenDataList) {

			// 案件ID
			setVal(sheet, rowIdx, TableContent.ANKEN_ID.getColIndex(), rowData.getAnkenId());

			// 分野
			setVal(sheet, rowIdx, TableContent.BUNYA.getColIndex(), rowData.getBunyaName());

			// 案件名
			setVal(sheet, rowIdx, TableContent.ANKEN_NAME.getColIndex(), rowData.getAnkenName());

			// 顧客名
			setVal(sheet, rowIdx, TableContent.CUSTOMER_NAME.getColIndex(), rowData.getCustomerName());

			// 相手方
			if (StringUtils.isEmpty(StringUtils.trim(rowData.getNumberOfAitegata()))) {
				setVal(sheet, rowIdx, TableContent.AITEGATA.getColIndex(), rowData.getAitegataName());
			} else {
				setVal(sheet, rowIdx, TableContent.AITEGATA.getColIndex(), rowData.getAitegataName()
						+ CommonConstant.LINE_FEED_CODE + StringUtils.trim(rowData.getNumberOfAitegata()));
			}

			// 顧客ステータス
			setVal(sheet, rowIdx, TableContent.SHINTYOKU.getColIndex(), rowData.getAnkenStatusName());

			// 担当弁護士１
			setVal(sheet, rowIdx, TableContent.TANTO_LAWYER_NAME1.getColIndex(), rowData.getTantoLawyerName1());

			// 担当弁護士２
			setVal(sheet, rowIdx, TableContent.TANTO_LAWYER_NAME2.getColIndex(), rowData.getTantoLawyerName2());

			// 担当弁護士３
			setVal(sheet, rowIdx, TableContent.TANTO_LAWYER_NAME3.getColIndex(), rowData.getTantoLawyerName3());

			// 担当弁護士４
			setVal(sheet, rowIdx, TableContent.TANTO_LAWYER_NAME4.getColIndex(), rowData.getTantoLawyerName4());

			// 担当弁護士５
			setVal(sheet, rowIdx, TableContent.TANTO_LAWYER_NAME5.getColIndex(), rowData.getTantoLawyerName5());

			// 担当弁護士６
			setVal(sheet, rowIdx, TableContent.TANTO_LAWYER_NAME6.getColIndex(), rowData.getTantoLawyerName6());

			// 担当事務１
			setVal(sheet, rowIdx, TableContent.TANTO_JIMU1.getColIndex(), rowData.getTantoJimuName1());

			// 担当事務２
			setVal(sheet, rowIdx, TableContent.TANTO_JIMU2.getColIndex(), rowData.getTantoJimuName2());

			// 担当事務３
			setVal(sheet, rowIdx, TableContent.TANTO_JIMU3.getColIndex(), rowData.getTantoJimuName3());

			// 担当事務４
			setVal(sheet, rowIdx, TableContent.TANTO_JIMU4.getColIndex(), rowData.getTantoJimuName4());

			// 担当事務５
			setVal(sheet, rowIdx, TableContent.TANTO_JIMU5.getColIndex(), rowData.getTantoJimuName5());

			// 担当事務６
			setVal(sheet, rowIdx, TableContent.TANTO_JIMU6.getColIndex(), rowData.getTantoJimuName6());

			// 受任日
			setVal(sheet, rowIdx, TableContent.JYUNIN_DATE.getColIndex(), rowData.getJuninDateFormat());

			// 登録日
			setVal(sheet, rowIdx, TableContent.ANKEN_CREATE_CREATE_DATE.getColIndex(), rowData.getAnkenCreatedDateFormat());

			rowIdx++;
		}

	}

}
