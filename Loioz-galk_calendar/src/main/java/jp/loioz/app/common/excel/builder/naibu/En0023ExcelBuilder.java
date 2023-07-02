package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.DepositRecvListExcelDto;
import jp.loioz.app.common.excel.dto.DepositRecvListExcelDto.ExcelDepositRecvRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 預り金／実費出力用クラス
 *
 */
@Setter
public class En0023ExcelBuilder extends AbstractMakeExcelBuilder<DepositRecvListExcelDto> {

	/** 預り金／実費のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "預り金／実費管理";

	/** タイトル名 */
	public static final String TITLE_NAME = "預り金／実費管理";

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
		NYUKIN_TOTAL_AMOUNT(6, "入金額"),
		SHUKKIN__TOTAL_AMOUNT(7, "出金額"),
		ZANDAKA_TOTAL_AMOUNT(8, "預り金残高"),
		TANTO_LAWYER_NAME1(9, "担当弁護士１"),
		TANTO_LAWYER_NAME2(10, "担当弁護士２"),
		TANTO_LAWYER_NAME3(11, "担当弁護士３"),
		TANTO_LAWYER_NAME4(12, "担当弁護士４"),
		TANTO_LAWYER_NAME5(13, "担当弁護士５"),
		TANTO_LAWYER_NAME6(14, "担当弁護士６"),
		TANTO_JIMU1(15, "担当事務１"),
		TANTO_JIMU2(16, "担当事務２"),
		TANTO_JIMU3(17, "担当事務３"),
		TANTO_JIMU4(18, "担当事務４"),
		TANTO_JIMU5(19, "担当事務５"),
		TANTO_JIMU6(20, "担当事務６"),
		LAST_EDIT_AT(21, "最終更新日時"),
		;
		
		/** 列 */
		private int colIndex;
		
		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private DepositRecvListExcelDto depositRecvListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DepositRecvListExcelDto createNewTargetBuilderDto() {
		return new DepositRecvListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_23, response);
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
		setVal(sheet, 0, "V", LocalDate.now());
		
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
		
		// 預り金／実費データ取得
		List<ExcelDepositRecvRowData> excelDepositRecvDataList = depositRecvListExcelDto.getExcelDepositRecvRowDataList();
		
		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, excelDepositRecvDataList.size());
		
		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelDepositRecvRowData rowData : excelDepositRecvDataList) {
			
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
			
			// 入金額
			setVal(sheet, rowIdx, TableContent.NYUKIN_TOTAL_AMOUNT.getColIndex(), rowData.getNyukinTotal().doubleValue());
			
			// 出金額
			setVal(sheet, rowIdx, TableContent.SHUKKIN__TOTAL_AMOUNT.getColIndex(), rowData.getShukkinTotal().doubleValue());
			
			// 預り金残高
			setVal(sheet, rowIdx, TableContent.ZANDAKA_TOTAL_AMOUNT.getColIndex(), rowData.getZandakaTotal().doubleValue());
			
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
