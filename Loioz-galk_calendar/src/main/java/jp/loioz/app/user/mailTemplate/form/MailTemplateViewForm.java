package jp.loioz.app.user.mailTemplate.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.mailTemplate.dto.MailTemplateListItemDto;
import lombok.Data;

/**
 * メールテンプレート画面の画面表示フォームクラス
 */
@Data
public class MailTemplateViewForm {

	/** メールテンプレート一覧 画面表示フォームオブジェクト */
	private MailTemlpateListViewForm mailTemlpateListViewForm;

	/**
	 * メールテンプレート一覧の画面表示フォームクラス
	 */
	@Data
	public static class MailTemlpateListViewForm {

		/** テンプレート一覧 */
		private List<MailTemplateListItemDto> templateList = Collections.emptyList();

	}

}
