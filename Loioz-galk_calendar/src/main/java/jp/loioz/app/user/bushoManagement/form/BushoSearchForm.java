package jp.loioz.app.user.bushoManagement.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

/**
 * 部門データ画面の検索条件フォームクラス
 */
@Data
public class BushoSearchForm implements PagerForm {

	/* 検索用の部署ID */
	private Long bushoId;

	/* 検索用の部署名 */
	private String bushoName;

	/* ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/* 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.bushoId = null;
		this.bushoName = null;
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