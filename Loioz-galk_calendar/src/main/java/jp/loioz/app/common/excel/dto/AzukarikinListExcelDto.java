package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.azukarikinManagement.form.ExcelAzukarikinData;
import lombok.Data;

@Data
public class AzukarikinListExcelDto {

	/** 預り金・未収金一覧の一覧表示データ */
	List<ExcelAzukarikinData> excelAzukarikinData = Collections.emptyList();

}
