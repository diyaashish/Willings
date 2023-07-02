package jp.loioz.domain.condition;

import jp.loioz.common.constant.SortConstant;
import jp.loioz.common.constant.SortConstant.SaibanListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 裁判一覧画面ソート条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaibanListSortCondition extends SortCondition {

	/** ソート項目 */
	private SaibanListSortItem saibanListSortItem;

	/** 昇順 ／ 降順 */
	private SortOrder sortOrder;

	/**
	 * ソート順が降順の場合 true を返します
	 * 
	 * @return
	 */
	public boolean isDesc() {
		if (SortConstant.SortOrder.DESC.equalsByCode(this.sortOrder.getCd())) {
			return true;
		}
		return false;
	}
}
