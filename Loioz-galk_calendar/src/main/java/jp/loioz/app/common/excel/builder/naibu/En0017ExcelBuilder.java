package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.BengoshiMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.BengoshiMeiboListExcelDto.ExcelBengoshiMeiboListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 弁護士名簿 出力用クラス
 */
@Setter
public class En0017ExcelBuilder extends AbstractMakeExcelBuilder<BengoshiMeiboListExcelDto> {

	/** 名簿一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "弁護士名簿";

	/** タイトル名 */
	public static final String TITLE_NAME = "弁護士名簿";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		PERSON_ID(0, "名簿ID"),
		NAME(1, "名前"),
		JIMUSHO_NAME(2, "事務所名"),
		ZIP_CODE(3, "郵便番号"),
		ADDRESS(4, "住所"),
		TEL_NO(5, "電話番号"),
		MAIL_ADDRESS(6, "メールアドレス"),
		CREATE_DATE(7, "登録日"),
		REMARKS(8, "特記事項"),;

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private BengoshiMeiboListExcelDto bengoshiMeiboListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BengoshiMeiboListExcelDto createNewTargetBuilderDto() {
		return new BengoshiMeiboListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_17, response);
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

		// 名簿一覧：弁護士データ取得
		List<ExcelBengoshiMeiboListRowData> bengoshiMeiboListDtoList = bengoshiMeiboListExcelDto.getBengoshiMeiboListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, bengoshiMeiboListDtoList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelBengoshiMeiboListRowData excelBengoshiMeiboListRowData : bengoshiMeiboListDtoList) {

			// 名簿ID
			if (excelBengoshiMeiboListRowData.getPersonId() != null) {
				setVal(sheet, rowIdx, TableContent.PERSON_ID.getColIndex(), excelBengoshiMeiboListRowData.getPersonId());
			}

			// 名前
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getBengoshiName())) {
				setVal(sheet, rowIdx, TableContent.NAME.getColIndex(), excelBengoshiMeiboListRowData.getBengoshiName());
			}

			// 事務所名
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getJimushoName())) {
				setVal(sheet, rowIdx, TableContent.JIMUSHO_NAME.getColIndex(), excelBengoshiMeiboListRowData.getJimushoName());
			}

			// 郵便番号
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getZipCode())) {
				setVal(sheet, rowIdx, TableContent.ZIP_CODE.getColIndex(), excelBengoshiMeiboListRowData.getZipCode());
			}

			// 住所
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getAddress1()) || StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getAddress2())) {
				setVal(sheet, rowIdx, TableContent.ADDRESS.getColIndex(), StringUtils.null2blank(excelBengoshiMeiboListRowData.getAddress1()) + StringUtils.null2blank(excelBengoshiMeiboListRowData.getAddress2()));
			}

			// 電話番号
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getTelNo())) {
				setVal(sheet, rowIdx, TableContent.TEL_NO.getColIndex(), excelBengoshiMeiboListRowData.getTelNo());
			}

			// メールアドレス
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getMailAddress())) {
				setVal(sheet, rowIdx, TableContent.MAIL_ADDRESS.getColIndex(), excelBengoshiMeiboListRowData.getMailAddress());
			}

			// 登録日
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getCustomerCreatedDate())) {
				setVal(sheet, rowIdx, TableContent.CREATE_DATE.getColIndex(), excelBengoshiMeiboListRowData.getCustomerCreatedDate());
			}

			// 特記事項
			if (StringUtils.isNotEmpty(excelBengoshiMeiboListRowData.getRemarks())) {
				setVal(sheet, rowIdx, TableContent.REMARKS.getColIndex(), excelBengoshiMeiboListRowData.getRemarks());
			}

			rowIdx++;
		}

	}

}
