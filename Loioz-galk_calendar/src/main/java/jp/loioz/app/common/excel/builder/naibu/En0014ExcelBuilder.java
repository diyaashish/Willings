package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.CustomerHojinMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.CustomerHojinMeiboListExcelDto.ExcelCustomerHojinMeiboListRowData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 顧客名簿（企業・団体） 出力用クラス
 */
@Setter
public class En0014ExcelBuilder extends AbstractMakeExcelBuilder<CustomerHojinMeiboListExcelDto> {

	/** 名簿一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** ファイル名 */
	public static final String FILE_NAME = "顧客名簿（企業・団体）";

	/** タイトル名 */
	public static final String TITLE_NAME = "顧客名簿（企業・団体）";

	/**
	 * 一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum TableContent {

		PERSON_ID(0, "名簿ID"),
		CUSTOMER_HOJIN_NAME(1, "名前"),
		ZIP_CODE(2, "郵便番号"),
		ADDRESS(3, "住所"),
		DAIHYOSHA_POSITION(4, "役職（代表者）"),
		DAIHYOSHA_NAME(5, "代表者"),
		TANTOSHA_NAME(6, "担当者"),
		TEL_NO(7, "電話番号"),
		MAIL_ADDRESS(8, "メールアドレス"),
		CUSTOMER_CREATE_DATE(9, "登録日"),
		REMARKS(10, "特記事項"),;

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private CustomerHojinMeiboListExcelDto customerHojinMeiboListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomerHojinMeiboListExcelDto createNewTargetBuilderDto() {
		return new CustomerHojinMeiboListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_14, response);
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

		// 名簿一覧：法人顧客データ取得
		List<ExcelCustomerHojinMeiboListRowData> customerHojinMeiboListDtoList = customerHojinMeiboListExcelDto.getCustomerHojinMeiboListDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, customerHojinMeiboListDtoList.size());

		// 一覧データ部分出力
		int rowIdx = START_ROW_IDX;
		for (ExcelCustomerHojinMeiboListRowData excelCustomerHojinMeiboListRowData : customerHojinMeiboListDtoList) {

			// 名簿ID
			if (excelCustomerHojinMeiboListRowData.getPersonId() != null) {
				setVal(sheet, rowIdx, TableContent.PERSON_ID.getColIndex(), excelCustomerHojinMeiboListRowData.getPersonId());
			}

			// 会社名
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getCustomerName())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_HOJIN_NAME.getColIndex(), excelCustomerHojinMeiboListRowData.getCustomerName());
			}

			// 郵便番号
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getZipCode())) {
				setVal(sheet, rowIdx, TableContent.ZIP_CODE.getColIndex(), excelCustomerHojinMeiboListRowData.getZipCode());
			}

			// 住所
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getAddress1()) || StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getAddress2())) {
				setVal(sheet, rowIdx, TableContent.ADDRESS.getColIndex(), StringUtils.null2blank(excelCustomerHojinMeiboListRowData.getAddress1()) + StringUtils.null2blank(excelCustomerHojinMeiboListRowData.getAddress2()));
			}

			// 役職
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getDaihyoPositionName())) {
				setVal(sheet, rowIdx, TableContent.DAIHYOSHA_POSITION.getColIndex(), excelCustomerHojinMeiboListRowData.getDaihyoPositionName());
			}

			// 代表者
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getDaihyoName())) {
				setVal(sheet, rowIdx, TableContent.DAIHYOSHA_NAME.getColIndex(), excelCustomerHojinMeiboListRowData.getDaihyoName());
			}

			// 担当者
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getTantoName())) {
				setVal(sheet, rowIdx, TableContent.TANTOSHA_NAME.getColIndex(), excelCustomerHojinMeiboListRowData.getTantoName());
			}

			// 電話番号
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getTelNo())) {
				setVal(sheet, rowIdx, TableContent.TEL_NO.getColIndex(), excelCustomerHojinMeiboListRowData.getTelNo());
			}

			// メールアドレス
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getMailAddress())) {
				setVal(sheet, rowIdx, TableContent.MAIL_ADDRESS.getColIndex(), excelCustomerHojinMeiboListRowData.getMailAddress());
			}

			// 登録日
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getCustomerCreatedDate())) {
				setVal(sheet, rowIdx, TableContent.CUSTOMER_CREATE_DATE.getColIndex(), excelCustomerHojinMeiboListRowData.getCustomerCreatedDate());
			}

			// 特記事項
			if (StringUtils.isNotEmpty(excelCustomerHojinMeiboListRowData.getRemarks())) {
				setVal(sheet, rowIdx, TableContent.REMARKS.getColIndex(), excelCustomerHojinMeiboListRowData.getRemarks());
			}

			rowIdx++;
		}

	}

}
