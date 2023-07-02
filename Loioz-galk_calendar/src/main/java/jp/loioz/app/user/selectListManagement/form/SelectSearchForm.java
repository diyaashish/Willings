package jp.loioz.app.user.selectListManagement.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.MaxDigit;
import lombok.Data;

/**
 * 選択肢一覧画面の検索条件フォームクラス
 */
@Data
public class SelectSearchForm implements PagerForm {

	/** 選択肢名 */
	@MaxDigit(max = 30)
	private String selectVal;

	// ページ番号
	private Integer page = DEFAULT_PAGE;

	// 表示件数
	private PageSize pageSize = PageSize.HANDRED;

	/** ソートが可能かどうか */
	public boolean canSort() {
		// 検索条件がすべて空の場合のみ、ソート可能とする
		return StringUtils.isAllEmpty(this.selectVal);
	}

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.selectVal = null;
		this.page = DEFAULT_PAGE;
		this.pageSize = PageSize.HANDRED;
	}

	/**
	 * 最初のページ番号に設定
	 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
	}
}