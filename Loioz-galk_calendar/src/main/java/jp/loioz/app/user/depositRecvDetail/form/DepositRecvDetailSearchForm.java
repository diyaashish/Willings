package jp.loioz.app.user.depositRecvDetail.form;

import jp.loioz.common.constant.SortConstant.DepositRecvDetailSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.domain.condition.DepositRecvDetailSortCondition;
import lombok.Data;

/**
 * 預り金明細画面の検索条件フォーム
 */
@Data
public class DepositRecvDetailSearchForm {

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	// -----------------------------------------------
	// 一覧ソート情報
	// -----------------------------------------------
	/** 一覧のソートキー */
	private DepositRecvDetailSortItem depositRecvDetailSortItem;

	/** 一覧のソート順 */
	private SortOrder depositRecvDetailSortOrder;

	/**
	 * フォームの初期化
	 */
	public void initForm() {

		// 初期表示の一覧ソート順
		this.setDepositRecvDetailSortItem(DepositRecvDetailSortItem.DEPOSIT_DATE);
		this.setDepositRecvDetailSortOrder(SortOrder.ASC);
	}

	/** ソート条件オブジェクトを作成 */
	public DepositRecvDetailSortCondition toDepositRecvDetailSortCondition() {
		return DepositRecvDetailSortCondition.builder()
				.sortItem(this.depositRecvDetailSortItem)
				.sortOrder(this.depositRecvDetailSortOrder)
				.build();
	}
}
