package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.CustomerAllMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.CustomerAllMeiboListExcelDto.ExcelCustomerAllMeiboListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 名簿一覧：顧客 出力用クラス
 */
@Setter
public class En0018ExcelBuilder extends AbstractMakeExcelBuilder<CustomerAllMeiboListExcelDto> {

	/** 名簿一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "顧客名簿";

	/** タイトル名 */
	public static final String TITLE_NAME = "顧客名簿";

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
		REMARKS(7, "特記事項"),;

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private CustomerAllMeiboListExcelDto customerAllMeiboListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomerAllMeiboListExcelDto createNewTargetBuilderDto() {
		return new CustomerAllMeiboListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_18, response);
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

		// 名簿一覧：顧客データ取得
		List<ExcelCustomerAllMeiboListRowData> customerAllMeiboListDtoList = customerAllMeiboListExcelDto.getCustomerAllMeiboListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, customerAllMeiboListDtoList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelCustomerAllMeiboListRowData excelCustomerAllMeiboListRowData : customerAllMeiboListDtoList) {

			// 名簿ID
			if (excelCustomerAllMeiboListRowData.getPersonId() != null) {
				setVal(sheet, rowIdx, TableContent.PERSON_ID.getColIndex(), excelCustomerAllMeiboListRowData.getPersonId());
			}

			// 名前
			if (StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getCustomerName())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_NAME.getColIndex(), excelCustomerAllMeiboListRowData.getCustomerName());
			}

			// 郵便番号
			if (StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getZipCode())) {
				setVal(sheet, rowIdx, TableContent.ZIP_CODE.getColIndex(), excelCustomerAllMeiboListRowData.getZipCode());
			}

			// 住所
			if (StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getAddress1()) || StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getAddress2())) {
				setVal(sheet, rowIdx, TableContent.ADDRESS.getColIndex(), StringUtils.null2blank(excelCustomerAllMeiboListRowData.getAddress1()) + StringUtils.null2blank(excelCustomerAllMeiboListRowData.getAddress2()));
			}

			// 電話番号
			if (StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getTelNo())) {
				setVal(sheet, rowIdx, TableContent.TEL_NO.getColIndex(), excelCustomerAllMeiboListRowData.getTelNo());
			}

			// メールアドレス
			if (StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getMailAddress())) {
				setVal(sheet, rowIdx, TableContent.MAIL_ADDRESS.getColIndex(), excelCustomerAllMeiboListRowData.getMailAddress());
			}

			// 登録日
			if (StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getCustomerCreateDate())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_CREATE_DATE.getColIndex(), excelCustomerAllMeiboListRowData.getCustomerCreateDate());
			}

			// 特記事項
			if (StringUtils.isNotEmpty(excelCustomerAllMeiboListRowData.getRemarks())) {
				setVal(sheet, rowIdx, TableContent.REMARKS.getColIndex(), excelCustomerAllMeiboListRowData.getRemarks());
			}

			rowIdx++;
		}

	}

}
