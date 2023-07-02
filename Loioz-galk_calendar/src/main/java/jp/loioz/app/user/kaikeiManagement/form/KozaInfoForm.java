package jp.loioz.app.user.kaikeiManagement.form;

import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 案件明細のフォームクラス
 */
@Data
public class KozaInfoForm {

	/** 銀行口座SEQ */
	@Required
	private Long ginkoAccountSeq;
}
