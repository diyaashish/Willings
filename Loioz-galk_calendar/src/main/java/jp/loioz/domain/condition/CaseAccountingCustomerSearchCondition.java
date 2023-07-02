package jp.loioz.domain.condition;

import jp.loioz.common.constant.SortConstant.CaseAccgCustomerListItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 会計画面 顧客一覧検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseAccountingCustomerSearchCondition extends SearchCondition {

	/** 案件ID */
	private Long ankenId;

	/** ソート項目 */
	private CaseAccgCustomerListItem item;

}
