package jp.loioz.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ExcelSeisanshoDto {

	/** お客様名 */
	private String name;

	/** 返金額 */
	private BigDecimal henkinGaku;

	/** 返金額(表示) */
	private String dispHenkinGaku;

	/** 入金額計 */
	private BigDecimal nyukinTotal;

	/** 入金額計(表示) */
	private String dispNyukinTotal;

	/** 出金額計 */
	private BigDecimal shukkinTotal;

	/** 出金額計(表示) */
	private String dispShukkinTotal;

	/** 源泉徴収額計 */
	private BigDecimal gensenTotal;

	/** 源泉徴収額計(表示) */
	private String dispGensenTotal;

	/** 消費税計 */
	private BigDecimal taxTotal;

	/** 消費税計(表示) */
	private String dispTaxTotal;

	/** 消費税8%計 */
	private BigDecimal taxTotalEight;

	/** 消費税8%計(表示) */
	private String dispTaxTotalEight;

	/** 消費税10%計 */
	private BigDecimal taxTotalTen;

	/** 消費税10%計(表示) */
	private String dispTaxTotalTen;

	/** 小計 */
	private BigDecimal shokei;

	/** 小計(表示) */
	private String dispshokei;

	/** 差し引き合計 */
	private BigDecimal saishihikiTotal;

	/** 差し引き合計(表示) */
	private String dispSashihikiTotal;

	/** 弁護士報酬項目 */
	private List<ExcelLawyerHoshuDto> hoshuMeisaiDtoList;

	/** 銀行名 */
	private String ginkoName;

	/** 支店名 */
	private String shitenName;

	/** 支店番号 */
	private String shitenNo;

	/** 口座タイプ */
	private String kozaType;

	/** 口座番号 */
	private String kozaNo;

	/** 口座名 */
	private String kozaName;

}
