package jp.loioz.app.user.azukariItem.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.user.azukariItem.enums.AzukariSearchStatus;
import jp.loioz.common.constant.CommonConstant.AzukariItemStatus;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.domain.condition.AzukariItemSearchCondition;
import lombok.Data;

/**
 * 預り品一覧画面の画面検索フォームクラス
 */
@Data
public class AzukariItemSearchForm implements PagerForm {

	/** 遷移元の案件ID **/
	private Long transitionAnkenId;
	/** 遷移元の顧客ID **/
	private Long transitionCustomerId;
	/** 預かり検索ステータス */
	private String azukariSearchStatus = AzukariSearchStatus.ALL.getCd();;

	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.azukariSearchStatus = AzukariSearchStatus.ALL.getCd();
		this.page = DEFAULT_PAGE;
		this.pageSize = DEFAULT_SIZE;
	}

	/**
	 * 最初のページ番号に設定
	 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
	}

	/**
	 * 預かり品一覧検索条件に変換する
	 * 
	 * @return 預かり品検索条件
	 */
	public AzukariItemSearchCondition toSearchCondition() {

		// 画面上の検索条件と実際に検索をする項目が異なるため、変換する
		String azukariItemStatus = "";
		if (AzukariSearchStatus.HOKAN.equalsByCode(this.azukariSearchStatus)) {
			azukariItemStatus = AzukariItemStatus.HOKAN.getCd();
		} else if (AzukariSearchStatus.HENKYAKU.equalsByCode(this.azukariSearchStatus)) {
			azukariItemStatus = AzukariItemStatus.HENKYAKU.getCd();
		} else {
			// 「すべて」の場合はSQLの絞り込みを行わないため、値を設定しない
		}

		return AzukariItemSearchCondition.builder()
				.ankenId(transitionAnkenId)
				.azukariItemStatus(azukariItemStatus)
				.build();
	}

}