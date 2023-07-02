package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.CustomerKojinMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.CustomerKojinMeiboListExcelDto.ExcelCustomerKojinMeiboListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 顧客名簿（個人） 出力用クラス
 *
 */
@Setter
public class En0013ExcelBuilder extends AbstractMakeExcelBuilder<CustomerKojinMeiboListExcelDto> {

	/** 名簿一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "顧客名簿（個人）";

	/** タイトル名 */
	public static final String TITLE_NAME = "顧客名簿（個人）";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		PERSON_ID(0, "名簿ID"),
		CUSTOMER_NAME(1, "名前"),
		ZIP_CODE(2, "郵便番号"),
		ADDRESS(3, "住所"),
		TEL_NO(4, "電話番号"),
		MAIL_ADDRESS(5, "メールアドレス"),
		CUSTOMER_CREATE_DATE(6, "登録日"),
		REMARKS(7, "特記事項");

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private CustomerKojinMeiboListExcelDto customerKojinMeiboListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomerKojinMeiboListExcelDto createNewTargetBuilderDto() {
		return new CustomerKojinMeiboListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_13, response);
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
		setVal(sheet, 0, "H", LocalDate.now());

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

		// 名簿一覧：個人顧客データ取得
		List<ExcelCustomerKojinMeiboListRowData> customerKojinMeiboListDtoList = customerKojinMeiboListExcelDto.getCustomerKojinMeiboListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, customerKojinMeiboListDtoList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelCustomerKojinMeiboListRowData excelCustomerKojinMeiboListRowData : customerKojinMeiboListDtoList) {

			// 名簿ID
			if (excelCustomerKojinMeiboListRowData.getPersonId() != null) {
				setVal(sheet, rowIdx, TableContent.PERSON_ID.getColIndex(), excelCustomerKojinMeiboListRowData.getPersonId());
			}

			// 顧客名
			if (StringUtils.isNotEmpty(excelCustomerKojinMeiboListRowData.getCustomerName())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_NAME.getColIndex(), excelCustomerKojinMeiboListRowData.getCustomerName());
			}

			// 郵便番号
			if (StringUtils.isNotEmpty(excelCustomerKojinMeiboListRowData.getZipCode())) {
				setVal(sheet, rowIdx, TableContent.ZIP_CODE.getColIndex(), excelCustomerKojinMeiboListRowData.getZipCode());
			}

			// 住所
			if (excelCustomerKojinMeiboListRowData.existsAddress()) {
				setVal(sheet, rowIdx, TableContent.ADDRESS.getColIndex(), StringUtils.null2blank(excelCustomerKojinMeiboListRowData.getAddress1()) + StringUtils.null2blank(excelCustomerKojinMeiboListRowData.getAddress2()));
			}

			// 電話番号
			if (StringUtils.isNotEmpty(excelCustomerKojinMeiboListRowData.getTelNo())) {
				setVal(sheet, rowIdx, TableContent.TEL_NO.getColIndex(), excelCustomerKojinMeiboListRowData.getTelNo());
			}

			// メールアドレス
			if (StringUtils.isNotEmpty(excelCustomerKojinMeiboListRowData.getMailAddress())) {
				setVal(sheet, rowIdx, TableContent.MAIL_ADDRESS.getColIndex(), excelCustomerKojinMeiboListRowData.getMailAddress());
			}

			// 登録日
			if (StringUtils.isNotEmpty(excelCustomerKojinMeiboListRowData.getCustomerCreatedDate())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_CREATE_DATE.getColIndex(), excelCustomerKojinMeiboListRowData.getCustomerCreatedDate());
			}

			// 特記事項
			if (StringUtils.isNotEmpty(excelCustomerKojinMeiboListRowData.getRemarks())) {
				setVal(sheet, rowIdx, TableContent.REMARKS.getColIndex(), excelCustomerKojinMeiboListRowData.getRemarks());
			}

			rowIdx++;
		}

	}

}
