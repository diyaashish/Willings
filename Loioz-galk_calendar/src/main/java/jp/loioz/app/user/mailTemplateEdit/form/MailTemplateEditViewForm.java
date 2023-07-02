package jp.loioz.app.user.mailTemplateEdit.form;

import lombok.Data;

/**
 * メールテンプレートの作成画面の画面表示フォームクラス
 */
@Data
public class MailTemplateEditViewForm {

	/** メールテンプレートSEQ */
	private Long mailTemplateSeq;

	/** 新規作成かどうか */
	public boolean isNew() {
		return mailTemplateSeq == null;
	}

}
