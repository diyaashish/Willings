package jp.loioz.domain.condition;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KaikeiListMeisaiSearchCondition extends SearchCondition {

	/** 案件ID */
	private Long transitionAnkenId;

	/** 顧客ID */
	private Long transitionCustomerId;

	/** 案件画面の場合 */
	private boolean isAnkenView;

	/** 顧客画面の場合 */
	private boolean isCustomerView;

	/** 処理済みの際に除外する案件ステータス */
	private List<String> completedExcludeAnkenStatus;

	/** 処理済みかどうか */
	private boolean isCompleted;

}
