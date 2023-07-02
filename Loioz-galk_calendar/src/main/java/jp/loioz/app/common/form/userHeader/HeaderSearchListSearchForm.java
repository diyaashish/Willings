package jp.loioz.app.common.form.userHeader;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.HeaderSearchTab;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.condition.userHeader.UserHeaderAnkenSearchCondition;
import jp.loioz.domain.condition.userHeader.UserHeaderPersonSearchCondition;
import jp.loioz.domain.condition.userHeader.UserHeaderSaibanSearchCondition;
import lombok.Data;

/**
 * ヘッダー検索一覧検索フォームオブジェクト
 */
@Data
public class HeaderSearchListSearchForm implements PagerForm {

	/** キーワード */
	private String keywords = "";

	/** ヘッダー検索タブCd */
	private String headerSearchTabCd = HeaderSearchTab.PERSON.getCd();

	/** リクエスト処理ID */
	private String requestUuid = "";

	// **************************************************************
	// パフォーマンスのため、このパラメータが存在する場合は、件数取得を行わない
	// ただし、選択した種別は最新の件数を取得する
	/** 名簿件数 入力値が存在する場合、その値を件数に表示する */
	private Long personCount;

	/** 案件件数 入力値が存在する場合、その値を件数に表示する */
	private Long ankenCount;

	/** 裁判件数 入力値が存在する場合、その値を件数に表示する */
	private Long saibanCount;
	// **************************************************************

	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = PageSize.TEN;

	/** ヘッダータブのEnum取得 */
	public HeaderSearchTab getHeaderSearchTab() {
		return HeaderSearchTab.of(this.headerSearchTabCd);
	}

	/**
	 * ヘッダー検索：名簿一覧検索用オブジェクトを作成する
	 * 
	 * @return
	 */
	public UserHeaderPersonSearchCondition toUserHeaderPersonSearchCondition() {
		return UserHeaderPersonSearchCondition.builder()
				.keywords(StringUtils.removeSpaceCharacter(this.keywords))
				.build();
	}

	/**
	 * ヘッダー検索：案件一覧検索用オブジェクトを作成する
	 * 
	 * @return
	 */
	public UserHeaderAnkenSearchCondition toUserHeaderAnkenSearchCondition() {
		return UserHeaderAnkenSearchCondition.builder()
				.keywords(StringUtils.removeSpaceCharacter(this.keywords))
				.build();
	}

	/**
	 * ヘッダー検索：裁判一覧検索用オブジェクトを作成する
	 * 
	 * @return
	 */
	public UserHeaderSaibanSearchCondition toUserHeaderSaibanSearchCondition() {
		return UserHeaderSaibanSearchCondition.builder()
				.keywords(StringUtils.removeSpaceCharacter(this.keywords))
				.build();
	}

}
