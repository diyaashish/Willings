package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.FeeDetailListExcelDto;
import jp.loioz.app.common.excel.dto.FeeDetailListExcelDto.ExcelFeeDetailListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 報酬明細出力用クラス
 *
 */
@Setter
public class En0020ExcelBuilder extends AbstractMakeExcelBuilder<FeeDetailListExcelDto> {

	/** 報酬明細のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 4;

	/** タイトル名 */
	public static final String TITLE_NAME = "報酬明細";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		FEE_ITEM_NAME(0, "項目"),
		FEE_DATE(1, "発生日"),
		FEE_PAYMENT_STATUS(2, "報酬ステータス"),
		FEE_AMOUNT_TAX_IN(3, "報酬額（税込み）"),
		HOUR_PRICE(4, "タイムチャージ単価"),
		WORK_TIME_MINUTE(5, "タイムチャージ時間"),
		TAX_RATE_TYPE(6, "消費税"),
		WITHHOLDING(7, "源泉徴収税"),
		SUM_TEXT(8, "摘要"),
		FEE_MEMO(9, "メモ"),
		INVOICE_STATEMENT_NO(10, "請求書／精算書");

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private FeeDetailListExcelDto feeDetailListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FeeDetailListExcelDto createNewTargetBuilderDto() {
		return new FeeDetailListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_20, response);
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
		setVal(sheet, 0, "K", LocalDate.now());

		// 名簿名
		setVal(sheet, 1, "A", feeDetailListExcelDto.getPersonName());

		// 案件名
		setVal(sheet, 2, "A", feeDetailListExcelDto.getAnkenName());

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
		String fileName = "報酬明細";
		
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

		// 報酬明細データ取得
		List<ExcelFeeDetailListRowData> excelFeeDetailList = feeDetailListExcelDto.getFeeDetailListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, excelFeeDetailList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelFeeDetailListRowData rowData : excelFeeDetailList) {

			// 項目
			setVal(sheet, rowIdx, TableContent.FEE_ITEM_NAME.getColIndex(), rowData.getFeeItemName());

			// 発生日
			setVal(sheet, rowIdx, TableContent.FEE_DATE.getColIndex(), rowData.getFeeDate());

			// 報酬ステータス
			setVal(sheet, rowIdx, TableContent.FEE_PAYMENT_STATUS.getColIndex(), rowData.getFeePaymentStatus());

			// 消費税率
			setVal(sheet, rowIdx, TableContent.TAX_RATE_TYPE.getColIndex(), rowData.getTaxRateType());

			// 源泉徴収
			setVal(sheet, rowIdx, TableContent.WITHHOLDING.getColIndex(), rowData.getWithholding());

			// 報酬額（税込）
			if (rowData.getFeeAmountTaxIn() != null) {
				setVal(sheet, rowIdx, TableContent.FEE_AMOUNT_TAX_IN.getColIndex(), rowData.getFeeAmountTaxIn().doubleValue());
			}

			// 請求書番号／精算書番号
			setVal(sheet, rowIdx, TableContent.INVOICE_STATEMENT_NO.getColIndex(), rowData.getInvoiceStatementNo());

			// タイムチャージ単価
			if (rowData.getHourPrice() != null) {
				setVal(sheet, rowIdx, TableContent.HOUR_PRICE.getColIndex(), rowData.getHourPrice().doubleValue());
			}

			// タイムチャージ時間
			setVal(sheet, rowIdx, TableContent.WORK_TIME_MINUTE.getColIndex(), rowData.getWorkTimeMinute());

			// メモ
			setVal(sheet, rowIdx, TableContent.FEE_MEMO.getColIndex(), rowData.getFeeMemo());

			// 摘要
			setVal(sheet, rowIdx, TableContent.SUM_TEXT.getColIndex(), rowData.getSumText());

			rowIdx++;
		}
	}

}
