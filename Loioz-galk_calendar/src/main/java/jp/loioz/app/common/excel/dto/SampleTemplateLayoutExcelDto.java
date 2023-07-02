package jp.loioz.app.common.excel.dto;

import java.util.List;

import jp.loioz.app.user.personManagement.document.ExcelCustomerListData;
import lombok.Data;

/**
 * ExcelBuilderサンプル(指定フォーマット出力用)
 */
@Data
public class SampleTemplateLayoutExcelDto {
	
	/**
	 * Excelに出力するデータ
	 */
	private List<ExcelCustomerListData> excelCustomerListData;

}
