package jp.loioz.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExcelShiharaiKeikakuDto {

	/** 作成有無フラグ */
	private Boolean createFlg = false;

	/**
	 */
	private List<ExcelShiharaiKeikakuListDto> shiharaiYoteiList = new ArrayList<>();

}
