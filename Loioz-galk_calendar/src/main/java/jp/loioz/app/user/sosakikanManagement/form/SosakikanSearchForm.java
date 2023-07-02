package jp.loioz.app.user.sosakikanManagement.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

@Data
public class SosakikanSearchForm implements PagerForm {

	/** 捜査機関名 */
	private String sosakikanName;

	/** 都道府県ID **/
	private String todofukenId;

	/** 捜査機関区分 **/
	private String sosakikanType;

	/** 電話番号 **/
	private String sosakikanTelNo;

	/** FAX番号 **/
	private String sosakikanFaxNo;

	// ページ番号
	private Integer page = DEFAULT_PAGE;

	// 表示件数
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.sosakikanName = null;
		this.todofukenId = null;
		this.sosakikanType = null;
		this.sosakikanTelNo = null;
		this.sosakikanFaxNo = null;
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
