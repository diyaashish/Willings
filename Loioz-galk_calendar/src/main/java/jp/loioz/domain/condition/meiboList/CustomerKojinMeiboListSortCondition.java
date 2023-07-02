package jp.loioz.domain.condition.meiboList;

import jp.loioz.common.constant.SortConstant;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.constant.meiboList.MeiboListConstant.CustomerKojinMeiboListSortItem;
import jp.loioz.domain.condition.SortCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 名簿一覧：個人顧客の一覧画面ソート条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerKojinMeiboListSortCondition extends SortCondition {

	/** ソート項目 */
	private CustomerKojinMeiboListSortItem sortItem;

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
