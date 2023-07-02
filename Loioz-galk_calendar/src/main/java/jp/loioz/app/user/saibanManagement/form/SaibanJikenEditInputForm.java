package jp.loioz.app.user.saibanManagement.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 事件情報の編集モーダル
 */
@Data
public class SaibanJikenEditInputForm {

	/** 事件SEQ */
	@Required
	private Long jikenSeq;

	/** 事件番号元号 */
	@Required
	private String jikenGengo;

	/** 事件番号年 */
	@Numeric
	@MaxDigit(max = 2)
	private String jikenYear;

	/** 事件番号符号 */
	@MaxDigit(max = 2)
	private String jikenMark;

	/** 事件番号 */
	@Numeric
	@MaxDigit(max = 7)
	private String jikenNo;

	/** 事件名 */
	@MaxDigit(max = 100)
	private String jikenName;

}
