package jp.loioz.app.user.taskManagement.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * タスク-コメントのフォームクラス
 *
 */
@Data
public class CommentForm {

	/** タスクSEQ */
	private Long taskSeq;

	/** タスク履歴SEQ */
	private Long taskHistorySeq;

	/** コメント */
	@Required
	@MaxDigit(max = 200)
	private String comment;

}
