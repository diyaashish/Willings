package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ExcelShukkinData;
import lombok.Data;

@Data
public class ShukkinYoteiExcelDto {

	/** 画面表示中の出力月 */
	private String outPutRange;

	/** 入金一覧の一覧表示データ */
	private List<ExcelShukkinData> excelShukkinData = Collections.emptyList();
}
