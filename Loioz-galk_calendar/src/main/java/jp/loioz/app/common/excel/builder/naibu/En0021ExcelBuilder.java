package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.DepositRecvDetailListExcelDto;
import jp.loioz.app.common.excel.dto.DepositRecvDetailListExcelDto.ExcelDepositRecvDetailListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 預り金明細出力用クラス
 *
 */
@Setter
public class En0021ExcelBuilder extends AbstractMakeExcelBuilder<DepositRecvDetailListExcelDto> {

	/** 預り金明細のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 4;

	/** タイトル名 */
	public static final String TITLE_NAME = "預り金／実費明細";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		DEPOSIT_TYPE(0, "種別"),
		DEPOSIT_ITEM_NAME(1, "項目"),
		DEPOSIT_DATE(2, "発生日"),
		DEPOSIT_AMOUNT(3, "入金額"),
		WITHDRAWAL_AMOUNT(4, "出金額"),
		TENANT_BEAR(5, "事務所負担"),
		SUM_TEXT(6, "摘要"),
		DEPOSIT_RECV_MEMO(7, "メモ"),
		INVOICE_STATEMENT_NO(8, "請求書／精算書");

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private DepositRecvDetailListExcelDto depositRecvDetailListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DepositRecvDetailListExcelDto createNewTargetBuilderDto() {
		return new DepositRecvDetailListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_21, response);
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
		setVal(sheet, 0, "I", LocalDate.now());

		// 名簿名
		setVal(sheet, 1, "A", depositRecvDetailListExcelDto.getPersonName());

		// 案件名
		setVal(sheet, 2, "A", depositRecvDetailListExcelDto.getAnkenName());

		// データ部分作成
		makeBody(workbook, sheet);

	}

	/**
	 * ダウンロードファイル名を作成します。
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	public String createFileName(String personName, String ankenName) {
		String fileName = "預り金／実費明細";
		
		// 顧客名
		if (!StringUtils.isEmpty(personName)) {
			fileName = fileName + "_" + personName;
		}
		
		// 案件名
		if (!StringUtils.isEmpty(ankenName)) {
			fileName = fileName + "_" + ankenName;
		}
		
		return fileName;
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

		// 預り金明細データ取得
		List<ExcelDepositRecvDetailListRowData> excelDepositRecvDetailList = depositRecvDetailListExcelDto
				.getDepositRecvDetailListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, excelDepositRecvDetailList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelDepositRecvDetailListRowData rowData : excelDepositRecvDetailList) {
			
			// 種別（入出金タイプ）
			setVal(sheet, rowIdx, TableContent.DEPOSIT_TYPE.getColIndex(), rowData.getDepositType());
			
			// 項目
			setVal(sheet, rowIdx, TableContent.DEPOSIT_ITEM_NAME.getColIndex(), rowData.getDepositItemName());
			
			// 発生日
			setVal(sheet, rowIdx, TableContent.DEPOSIT_DATE.getColIndex(), rowData.getDateFormat());
			
			// 入金額
			if (rowData.getDepositAmount() != null) {
				setVal(sheet, rowIdx, TableContent.DEPOSIT_AMOUNT.getColIndex(), rowData.getDepositAmount().doubleValue());
			}
			
			// 出金額
			if (rowData.getWithdrawalAmount() != null) {
				setVal(sheet, rowIdx, TableContent.WITHDRAWAL_AMOUNT.getColIndex(), rowData.getWithdrawalAmount().doubleValue());
			}
			
			// 事務所負担
			setVal(sheet, rowIdx, TableContent.TENANT_BEAR.getColIndex(), rowData.isTenantBear() ? "事務所負担" : "");
			
			// 請求書／精算書
			setVal(sheet, rowIdx, TableContent.INVOICE_STATEMENT_NO.getColIndex(), rowData.getInvoiceStatementNo());
			
			// 摘要
			setVal(sheet, rowIdx, TableContent.SUM_TEXT.getColIndex(), rowData.getSumText());
			
			// メモ
			setVal(sheet, rowIdx, TableContent.DEPOSIT_RECV_MEMO.getColIndex(), rowData.getDepositRecvMemo());
			
			rowIdx++;
		}
	}

}
