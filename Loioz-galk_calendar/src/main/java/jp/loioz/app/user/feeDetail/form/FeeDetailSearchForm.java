package jp.loioz.app.user.feeDetail.form;

import jp.loioz.common.constant.SortConstant.FeeDetailSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.domain.condition.FeeDetailSortCondition;
import lombok.Data;

/**
 * 報酬明細画面の検索条件フォーム
 */
@Data
public class FeeDetailSearchForm {

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	// -----------------------------------------------
	// 一覧ソート情報
	// -----------------------------------------------
	/** 一覧のソートキー */
	private FeeDetailSortItem feeDetailSortItem;

	/** 一覧のソート順 */
	private SortOrder feeDetailSortOrder;

	/**
	 * フォームの初期化
	 */
	public void initForm() {

		// 初期表示の一覧ソート順
		this.setFeeDetailSortItem(FeeDetailSortItem.FEE_DATE);
		this.setFeeDetailSortOrder(SortOrder.ASC);

	}

	/** ソート条件オブジェクトを作成 */
	public FeeDetailSortCondition toFeeDetailSortCondition() {
		return FeeDetailSortCondition.builder()
				.sortItem(this.feeDetailSortItem)
				.sortOrder(this.feeDetailSortOrder)
				.build();
	}
}
