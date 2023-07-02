package jp.loioz.app.user.saibanManagement.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 裁判管理画面(民事)：反訴モーダルの表示フォームクラス
 */
@Data
public class SaibanHansoEditInputForm {

	/** 裁判SEQ */
	@Required
	private Long saibanSeq;

	/** 案件ID */
	private Long ankenId;

	/** 事件SEQ */
	private Long jikenSeq;

	/** 事件番号元号 */
	@Required
	private String jikenGengo;

	/** 事件番号年 */
	@MaxDigit(max = 2)
	private String jikenYear;

	/** 事件番号符号 */
	@MaxDigit(max = 2)
	private String jikenMark;

	/** 事件番号 */
	@MaxDigit(max = 7)
	private String jikenNo;

	/** 事件名 */
	@MaxDigit(max = 100)
	private String jikenName;

}
