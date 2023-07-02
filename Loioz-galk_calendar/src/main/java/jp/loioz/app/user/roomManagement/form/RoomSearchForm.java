package jp.loioz.app.user.roomManagement.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

/**
 * 施設一覧画面の画面表示フォームクラス
 */
@Data
public class RoomSearchForm implements PagerForm {

	/** 会議室名 */
	private String roomName;

	// ページ番号
	private Integer page = DEFAULT_PAGE;

	// 表示件数
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.roomName = null;
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