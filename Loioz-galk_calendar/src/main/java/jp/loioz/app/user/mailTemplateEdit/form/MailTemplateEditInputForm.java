package jp.loioz.app.user.mailTemplateEdit.form;

import jp.loioz.common.constant.CommonConstant.MailTemplateType;
import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * メールテンプレート編集：入力用フォームオブジェクト
 */
@Data
public class MailTemplateEditInputForm {

	/**
	 * メールテンプレート入力用オブジェクト
	 */
	@Data
	public static class TemplateInputForm {

		/** メールテンプレートSEQ */
		private Long mailTemplateSeq;

		/** テンプレート種別 */
		@Required
		@EnumType(value = MailTemplateType.class)
		private String templateType;

		/** テンプレートタイトル */
		@MaxDigit(max = 30)
		private String templateTitle;

		/** メールアドレス CC */
		@MaxDigit(max = 3000)
		private String mailCc;

		/** メールアドレス BCC */
		@MaxDigit(max = 3000)
		private String mailBcc;

		/** メールアドレス To */
		@MaxDigit(max = 256)
		@EmailPattern
		private String mailReplyTo;

		/** 件名 */
		@MaxDigit(max = 100)
		private String subject;

		/** 本文 */
		@MaxDigit(max = 10000)
		private String contents;

		/** 既定利用 */
		private boolean isDefaultUse;

		/** 新規登録かどうか */
		public boolean isNew() {
			return this.mailTemplateSeq == null;
		}

	}

}
