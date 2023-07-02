package jp.loioz.dto;

import java.math.BigDecimal;

import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import lombok.Data;

/**
 * 請求書・精算書出力用の報酬明細情報Dto
 */
@Data
public class ExcelLawyerHoshuDto {

	/** 消費税率 */
	private String taxRate;

	/** 弁護士報酬の合計金額 */
	private BigDecimal totalKingaku;

	/** 消費税率（表示用） */
	private BigDecimal taxRateDisp;

	/** 弁護士報酬(enum) */
	private LawyerHoshu lawyerHoshu;

}