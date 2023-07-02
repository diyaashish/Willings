package jp.loioz.app.common.mvc.anken.popover.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import lombok.Data;

/**
 * 案件ポップオーバー検索フォームオブジェクト
 * 
 */
@Data
public class AnkenPopoverSearchForm implements PagerForm {

	/** 名簿ID */
	private Long personId;

	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = PageSize.TEN;

}
