package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.AdvisorMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.AdvisorMeiboListExcelDto.ExcelAdvisorMeiboListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 顧問取引先 出力用クラス
 */
@Setter
public class En0019ExcelBuilder extends AbstractMakeExcelBuilder<AdvisorMeiboListExcelDto> {

	/** 名簿一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "顧問取引先";

	/** タイトル名 */
	public static final String TITLE_NAME = "顧問取引先";

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
	private AdvisorMeiboListExcelDto advisorMeiboListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdvisorMeiboListExcelDto createNewTargetBuilderDto() {
		return new AdvisorMeiboListExcelDto();
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

		// 名簿一覧：顧問データ取得
		List<ExcelAdvisorMeiboListRowData> advisorMeiboListDtoList = advisorMeiboListExcelDto.getAdvisorMeiboListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, advisorMeiboListDtoList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelAdvisorMeiboListRowData excelAdvisorMeiboListRowData : advisorMeiboListDtoList) {

			// 名簿ID
			if (excelAdvisorMeiboListRowData.getPersonId() != null) {
				setVal(sheet, rowIdx, TableContent.PERSON_ID.getColIndex(), excelAdvisorMeiboListRowData.getPersonId());
			}

			// 名前
			if (StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getCustomerName())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_NAME.getColIndex(), excelAdvisorMeiboListRowData.getCustomerName());
			}

			// 郵便番号
			if (StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getZipCode())) {
				setVal(sheet, rowIdx, TableContent.ZIP_CODE.getColIndex(), excelAdvisorMeiboListRowData.getZipCode());
			}

			// 住所
			if (StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getAddress1()) || StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getAddress2())) {
				setVal(sheet, rowIdx, TableContent.ADDRESS.getColIndex(), StringUtils.null2blank(excelAdvisorMeiboListRowData.getAddress1()) + StringUtils.null2blank(excelAdvisorMeiboListRowData.getAddress2()));
			}

			// 電話番号
			if (StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getTelNo())) {
				setVal(sheet, rowIdx, TableContent.TEL_NO.getColIndex(), excelAdvisorMeiboListRowData.getTelNo());
			}

			// メールアドレス
			if (StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getMailAddress())) {
				setVal(sheet, rowIdx, TableContent.MAIL_ADDRESS.getColIndex(), excelAdvisorMeiboListRowData.getMailAddress());
			}

			// 登録日
			if (StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getCustomerCreateDate())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_CREATE_DATE.getColIndex(), excelAdvisorMeiboListRowData.getCustomerCreateDate());
			}

			// 特記事項
			if (StringUtils.isNotEmpty(excelAdvisorMeiboListRowData.getRemarks())) {
				setVal(sheet, rowIdx, TableContent.REMARKS.getColIndex(), excelAdvisorMeiboListRowData.getRemarks());
			}

			rowIdx++;
		}

	}

}
