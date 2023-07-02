package jp.loioz.app.user.taskManagement.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * タスク-チェック項目のフォームクラス
 */
@Data
public class TaskCheckItemForm {

	/** タスクSEQ */
	private Long taskSeq;

	/** タスク-チェック項目SEQ */
	private Long taskCheckItemSeq;

	/** チェック項目名 */
	@Required
	@MaxDigit(max = 100)
	private String checkItemName;

	/** チェック項目ステータス */
	private String newCompleteFlg;
}
