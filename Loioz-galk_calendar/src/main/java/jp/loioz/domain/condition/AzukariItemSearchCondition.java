package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 預かり品の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AzukariItemSearchCondition extends SearchCondition {

	/** 案件ID */
	private Long ankenId;

	/** 預かり品ステータス */
	private String azukariItemStatus;

}
