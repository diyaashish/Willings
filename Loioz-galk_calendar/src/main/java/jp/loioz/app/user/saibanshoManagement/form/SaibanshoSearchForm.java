package jp.loioz.app.user.saibanshoManagement.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

@Data
public class SaibanshoSearchForm implements PagerForm {

	/** 都道府県ID **/
	private String todofukenId;

	/** 裁判所名 */
	private String saibanshoName;

	/** 住所 */
	private String saibanshoAddress;

	// ページ番号
	private Integer page = DEFAULT_PAGE;

	// 表示件数
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.todofukenId = null;
		this.saibanshoName = null;
		this.saibanshoAddress = null;
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
