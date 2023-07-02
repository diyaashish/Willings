package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 案件タスク追加モーダル内、一覧検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAnkenAddModalSearchCondition extends SearchCondition {

	/** 検索条件 名前 */
	private String name;

	/** 検索条件 案件ID */
	private String ankenId;

	/** 検索条件 分野 */
	private String bunyaId;

	/** 検索条件 案件名 */
	private String ankenName;

	/** 検索条件 担当弁護士 */
	private Long tantoLaywer;

	/** 検索条件 担当事務 */
	private Long tantoJimu;

	/** 案件登録日From */
	private String ankenCreateDateFrom;

	/** 案件登録日To */
	private String ankenCreateDateTo;
}
