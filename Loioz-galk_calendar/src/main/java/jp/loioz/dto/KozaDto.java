package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.validation.annotation.Katakana;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 口座情報Dto
 */
@Data
public class KozaDto {

	/** 銀行口座SEQ */
	private Long ginkoAccountSeq;

	/** 口座連番 */
	private Long branchNo;

	/** 表示名 */
	@Required
	@MaxDigit(max = 30, item = "表示名")
	private String labelName;

	/** 銀行名 */
	@MaxDigit(max = 20, item = "銀行名")
	private String ginkoName;

	/** 支店名 */
	@MaxDigit(max = 20, item = "支店名")
	private String shitenName;

	/** 支店番号 */
	@Numeric
	@MaxDigit(max = 6, item = "支店番号")
	private String shitenNo;

	/** 口座種類 */
	private KozaType kozaType;

	/** 口座種類名 */
	private String kozaTypeName;

	/** 口座番号 */
	@Numeric
	@MaxDigit(max = 9, item = "口座番号")
	private String kozaNo;

	/** 口座名義 */
	@MaxDigit(max = 30, item = "口座名義")
	private String kozaName;

	/** 口座名義(フリガナ) */
	@Katakana
	@MaxDigit(max = 100, item = "口座名義カナ")
	private String kozaNameKana;

	/** 既定使用フラグ */
	private boolean isDefaultUse;

	/** テナントSEQ */
	private Long tenantSeq;

	/** 停止フラグ */
	private Boolean deletedFlg = false;

}