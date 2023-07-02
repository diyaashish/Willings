package jp.loioz.app.common.excel.dto;

import java.util.List;

import jp.loioz.app.user.personManagement.document.ExcelCustomerListData;
import lombok.Data;

/**
 * 顧客一覧用のExcelDto
 */
@Data
public class CustomerListExcelDto {

	/**
	 * Excelに出力するデータ
	 */
	private List<ExcelCustomerListData> excelCustomerListData;
}