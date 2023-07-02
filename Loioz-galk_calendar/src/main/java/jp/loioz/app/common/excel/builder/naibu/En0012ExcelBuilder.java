package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.AllMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.AllMeiboListExcelDto.ExcelAllMeiboListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 名簿一覧:すべての出力用クラス
 *
 */
@Setter
public class En0012ExcelBuilder extends AbstractMakeExcelBuilder<AllMeiboListExcelDto> {

	/** 名簿一覧:すべてのデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "名簿情報";

	/** タイトル名 */
	public static final String TITLE_NAME = "名簿情報";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		PERSON_ID(0, "名簿ID"),
		ATTRIBUTE(1, "属性"),
		NAME(2, "名前"),
		ZIP_CODE(3, "郵便番号"),
		ADDRESS(4, "住所"),
		TEL_NO(5, "電話番号"),
		MAIL_ADDRESS(6, "メールアドレス"),
		ANKEN(7, "案件"),
		CUSTOMER_CREATE_DATE(8, "登録日"),
		;

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private AllMeiboListExcelDto allMeiboListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AllMeiboListExcelDto createNewTargetBuilderDto() {
		return new AllMeiboListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_12, response);
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

		// すべての名簿一覧データ取得
		List<ExcelAllMeiboListRowData> excelAllMeiboListRowDataList = allMeiboListExcelDto.getAllMeiboListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, excelAllMeiboListRowDataList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelAllMeiboListRowData excelAllMeiboListRowData : excelAllMeiboListRowDataList) {

			// 名簿ID
			if (excelAllMeiboListRowData.getPersonId() != null) {
				setVal(sheet, rowIdx, TableContent.PERSON_ID.getColIndex(), excelAllMeiboListRowData.getPersonId().toString());
			}

			// 属性
			if (excelAllMeiboListRowData.getPersonAttribute() != null) {
				String attribute = excelAllMeiboListRowData.getPersonAttribute().getName();
				setVal(sheet, rowIdx, TableContent.ATTRIBUTE.getColIndex(), attribute);
			}

			// 名前
			if (StringUtils.isNotEmpty(excelAllMeiboListRowData.getName())) {
				setVal(sheet, rowIdx, TableContent.NAME.getColIndex(), excelAllMeiboListRowData.getName());
			}

			// 郵便番号
			if (StringUtils.isNotEmpty(excelAllMeiboListRowData.getZipCode())) {
				setVal(sheet, rowIdx, TableContent.ZIP_CODE.getColIndex(), excelAllMeiboListRowData.getZipCode());
			}

			// 住所
			if (StringUtils.isNotEmpty(excelAllMeiboListRowData.getAddress1()) || StringUtils.isNotEmpty(excelAllMeiboListRowData.getAddress2())) {
				setVal(sheet, rowIdx, TableContent.ADDRESS.getColIndex(), StringUtils.null2blank(excelAllMeiboListRowData.getAddress1()) + StringUtils.null2blank(excelAllMeiboListRowData.getAddress2()));
			}

			// 電話番号
			if (StringUtils.isNotEmpty(excelAllMeiboListRowData.getTelNo())) {
				setVal(sheet, rowIdx, TableContent.TEL_NO.getColIndex(), excelAllMeiboListRowData.getTelNo());
			}

			// メールアドレス
			if (StringUtils.isNotEmpty(excelAllMeiboListRowData.getMailAddress())) {
				setVal(sheet, rowIdx, TableContent.MAIL_ADDRESS.getColIndex(), excelAllMeiboListRowData.getMailAddress());
			}

			// 案件
			if (excelAllMeiboListRowData.isExistsAnkenCustomer() || excelAllMeiboListRowData.isExistsKanyosha()) {
				setVal(sheet, rowIdx, TableContent.ANKEN.getColIndex(), "案件あり");
			}

			// 登録日
			if (StringUtils.isNotEmpty(excelAllMeiboListRowData.getCustomerCreatedDate())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_CREATE_DATE.getColIndex(), excelAllMeiboListRowData.getCustomerCreatedDate());
			}

			rowIdx++;
		}

	}

}
