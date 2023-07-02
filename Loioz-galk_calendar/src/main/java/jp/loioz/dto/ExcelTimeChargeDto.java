package jp.loioz.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExcelTimeChargeDto {

	/** 作成有無フラグ */
	private Boolean createFlg = false;

	/** 宛名 */
	private String name;

	/** 顧客名 */
	private String customerName;

	/**
	 * タイムチャージ情報
	 *
	 */
	private List<ExcelTimeChargeListDto> timeChargeList = new ArrayList<>();

	/** 合計時間 */
	private int totalTime;

	/** 報酬額 */
	private BigDecimal hoshuTotal;

	/** 報酬額(表示) */
	private String dispHoshuTotal;

}
