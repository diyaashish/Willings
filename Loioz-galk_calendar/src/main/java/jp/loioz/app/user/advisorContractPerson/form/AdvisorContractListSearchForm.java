package jp.loioz.app.user.advisorContractPerson.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

/**
 * 顧問契約一覧画面（名簿）の検索フォームクラス
 */
@Data
public class AdvisorContractListSearchForm implements PagerForm {

	/** 名簿ID */
	private Long personId;
	
	// ページャー
	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.page = DEFAULT_PAGE;
		this.pageSize = DEFAULT_SIZE;
	}
}
