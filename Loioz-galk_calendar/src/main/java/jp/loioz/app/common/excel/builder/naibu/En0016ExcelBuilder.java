package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.KeijiSaibanListExcelDto;
import jp.loioz.app.common.excel.dto.KeijiSaibanListExcelDto.ExcelKeijiSaibanListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 刑事裁判出力用クラス
 *
 */
@Setter
public class En0016ExcelBuilder extends AbstractMakeExcelBuilder<KeijiSaibanListExcelDto> {

	/** 刑事裁判のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "刑事裁判";

	/** タイトル名 */
	public static final String TITLE_NAME = "刑事裁判";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		ANKEN_ID(0, "案件ID"),
		BUNYA(1, "分野"),
		SAIBANSHO_NAME(2, "裁判所"),
		JIKEN_NUNBER(3, "事件番号"),
		JIKEN_NAME(4, "事件名"),
		CUSTOMER_NAME(5, "名前（被告人）"),
		KENSATSUCHO_NAME(6, "検察庁"),
		SAIBAN_STATUS(7, "裁判手続き"),
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
		SAIBAN_START_DATE(20, "起訴日／上訴日"),
		SAIBAN_END_DATE(21, "判決日"),
		;

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private KeijiSaibanListExcelDto saibanListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KeijiSaibanListExcelDto createNewTargetBuilderDto() {
		return new KeijiSaibanListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_16, response);
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

		// 裁判一覧データ取得
		List<ExcelKeijiSaibanListRowData> excelSaibanDataList = saibanListExcelDto.getSaibanListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, excelSaibanDataList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelKeijiSaibanListRowData rowData : excelSaibanDataList) {

			// 案件ID
			setVal(sheet, rowIdx, TableContent.ANKEN_ID.getColIndex(), rowData.getAnkenId());

			// 分野
			setVal(sheet, rowIdx, TableContent.BUNYA.getColIndex(), rowData.getBunyaName());

			// 裁判所
			setVal(sheet, rowIdx, TableContent.SAIBANSHO_NAME.getColIndex(), rowData.getSaibanshoNameMei());

			// 事件番号
			setVal(sheet, rowIdx, TableContent.JIKEN_NUNBER.getColIndex(), rowData.getJikenNumber());

			// 事件名
			setVal(sheet, rowIdx, TableContent.JIKEN_NAME.getColIndex(), rowData.getJikenName());

			// 名前（被告人）
			if (StringUtils.isEmpty(StringUtils.trim(rowData.getNumberOfCustomer()))) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_NAME.getColIndex(), rowData.getCustomerName());
			} else {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_NAME.getColIndex(), rowData.getCustomerName()
						+ CommonConstant.LINE_FEED_CODE + StringUtils.trim(rowData.getNumberOfCustomer()));
			}

			// 検察庁
			setVal(sheet, rowIdx, TableContent.KENSATSUCHO_NAME.getColIndex(), rowData.getKensatsuchoNameMei());

			// 裁判手続き
			setVal(sheet, rowIdx, TableContent.SAIBAN_STATUS.getColIndex(), rowData.getSaibanStatusName());

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

			// 申立日／起訴日
			setVal(sheet, rowIdx, TableContent.SAIBAN_START_DATE.getColIndex(), rowData.getSaibanStartDateFormat());

			// 終了日／判決日
			setVal(sheet, rowIdx, TableContent.SAIBAN_END_DATE.getColIndex(), rowData.getSaibanEndDateFormat());

			rowIdx++;
		}

	}

}
