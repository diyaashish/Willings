package jp.loioz.app.common.form;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.utility.LoiozObjectUtils;

/**
 * ページャーの入力フォーム
 */
public interface AdminPagerForm {

	/** デフォルトページ */
	public static final Integer DEFAULT_PAGE = Integer.valueOf(0);

	/** デフォルト表示件数 */
	public static final PageSize DEFAULT_SIZE = PageSize.THIRTY;

	/**
	 * ページ番号を取得する
	 *
	 * @return ページ番号
	 */
	public Integer getPage();

	/**
	 * 表示件数を取得する
	 *
	 * @return 表示件数
	 */
	public PageSize getPageSize();

	/**
	 * このページング条件をPageableに変換する
	 *
	 * @return Pageable
	 */
	public default Pageable toPageable() {

		int page = DEFAULT_PAGE;
		if (getPage() != null && getPage() >= 0) {
			page = getPage();
		}

		PageSize sizeEnum = LoiozObjectUtils.defaultIfNull(getPageSize(), DEFAULT_SIZE);
		int size = sizeEnum.getIntVal();

		return PageRequest.of(page, size);
	}

}
