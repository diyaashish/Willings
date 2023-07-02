package jp.loioz.app.user.groupManagement.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

/**
 * グループ一覧画面のFormクラス
 */
@Data
public class GroupSearchForm implements PagerForm {

	/* グループ名 */
	private String groupName;

	/* ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/* 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.page = DEFAULT_PAGE;
		this.pageSize = DEFAULT_SIZE;
	}

	/**
	 * 最初のページ番号に設定
	 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
	}

}