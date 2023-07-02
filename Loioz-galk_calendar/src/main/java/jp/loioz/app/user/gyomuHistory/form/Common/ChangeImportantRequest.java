package jp.loioz.app.user.gyomuHistory.form.Common;

import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class ChangeImportantRequest {

	/** 業務履歴SEQ */
	@Required
	private Long gyomuHistorySeq;

	/** 重要フラグ */
	@Required
	private String importantFlg;

}
