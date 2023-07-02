package jp.loioz.app.user.gyomuHistory.form.Anken;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

@Data
public class GyomuHistoryListByAnkenSearchForm implements PagerForm {

	/** 遷移元の案件ID **/
	private Long transitionAnkenId;
	/** 遷移元の顧客ID **/
	private Long transitionCustomerId;

	// 検索条件
	/** 検索ボックス */
	private String searchText;

	/** 顧客ID */
	private Long searchCustomerId;

	/** 重要 */
	private boolean isImportant;

	// ページャー
	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		// 遷移元IDは初期化しない
		this.searchText = "";
		this.searchCustomerId = null;
		this.isImportant = false;
		this.page = DEFAULT_PAGE;
		this.pageSize = DEFAULT_SIZE;
	}

	/**
	 * 全体検索時に顧客IDをnullにする
	 */
	public void resetSearchCustomerId() {
		this.searchCustomerId = null;
	}

	/**
	 * 最初のページ番号に設定
	 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
	}

}
