package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 精算書の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeisanshoSearchCondition extends SearchCondition {

	/** 顧客ID */
	private Long customerId;

	/** 案件ID */
	private Long ankenId;

	/** 発生日From */
	private String hasseiDateFrom;

	/** 発生日To */
	private String hasseiDateTo;

	/** 項目ID 弁護士報酬 */
	private boolean bengoshiHoshu;

	/** 項目ID 入金項目 */
	private boolean nyukinItem;

	/** 項目ID 出金項目 */
	private boolean shukkinItem;

}
