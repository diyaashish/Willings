package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.FeeListExcelDto;
import jp.loioz.app.common.excel.dto.FeeListExcelDto.ExcelFeeRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 報酬出力用クラス
 *
 */
@Setter
public class En0022ExcelBuilder extends AbstractMakeExcelBuilder<FeeListExcelDto> {

	/** 報酬のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "報酬管理";

	/** タイトル名 */
	public static final String TITLE_NAME = "報酬管理";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {
		
		PERSON_ID(0, "名簿ID"),
		NAME(1, "名前"),
		ANKEN_ID(2, "案件ID"),
		BUNYA_NAME(3, "分野"),
		ANKEN_NAME(4, "案件名"),
		STATUS_NAME(5, "ステータス"),
		FEE_TOTAL_AMOUNT(6, "報酬(税込)"),
		FEE_UNCLAIMED_TOTAL_AMOUNT(7, "未請求"),
		TANTO_LAWYER_NAME1(8, "担当弁護士１"),
		TANTO_LAWYER_NAME2(9, "担当弁護士２"),
		TANTO_LAWYER_NAME3(10, "担当弁護士３"),
		TANTO_LAWYER_NAME4(11, "担当弁護士４"),
		TANTO_LAWYER_NAME5(12, "担当弁護士５"),
		TANTO_LAWYER_NAME6(13, "担当弁護士６"),
		TANTO_JIMU1(14, "担当事務１"),
		TANTO_JIMU2(15, "担当事務２"),
		TANTO_JIMU3(16, "担当事務３"),
		TANTO_JIMU4(17, "担当事務４"),
		TANTO_JIMU5(18, "担当事務５"),
		TANTO_JIMU6(19, "担当事務６"),
		LAST_EDIT_AT(20, "最終更新日時"),
		;
		
		/** 列 */
		private int colIndex;
		
		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private FeeListExcelDto feeListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FeeListExcelDto createNewTargetBuilderDto() {
		return new FeeListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_22, response);
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
		setVal(sheet, 0, "U", LocalDate.now());
		
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
		
		// 報酬データ取得
		List<ExcelFeeRowData> excelFeeDataList = feeListExcelDto.getExcelfeeRowDataList();
		
		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, excelFeeDataList.size());
		
		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelFeeRowData rowData : excelFeeDataList) {
			
			// 名簿ID
			setVal(sheet, rowIdx, TableContent.PERSON_ID.getColIndex(), rowData.getPersonId());
			
			// 名前
			setVal(sheet, rowIdx, TableContent.NAME.getColIndex(), rowData.getName());
			
			// 案件ID
			setVal(sheet, rowIdx, TableContent.ANKEN_ID.getColIndex(), rowData.getAnkenId());
			
			// 分野名
			setVal(sheet, rowIdx, TableContent.BUNYA_NAME.getColIndex(), rowData.getBunyaName());
			
			// 案件名
			setVal(sheet, rowIdx, TableContent.ANKEN_NAME.getColIndex(), rowData.getAnkenName());
			
			// ステータス名
			setVal(sheet, rowIdx, TableContent.STATUS_NAME.getColIndex(), rowData.getAnkenStatusName());
			
			// 報酬合計(税込)
			setVal(sheet, rowIdx, TableContent.FEE_TOTAL_AMOUNT.getColIndex(), rowData.getFeeTotalAmount().doubleValue());
			
			// 未請求(合計)
			setVal(sheet, rowIdx, TableContent.FEE_UNCLAIMED_TOTAL_AMOUNT.getColIndex(), rowData.getFeeUnclaimedTotalAmount().doubleValue());
			
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
			
			// 最終更新日時
			setVal(sheet, rowIdx, TableContent.LAST_EDIT_AT.getColIndex(), rowData.getLastEditAtFormat());
			
			rowIdx++;
		}
	}
}
