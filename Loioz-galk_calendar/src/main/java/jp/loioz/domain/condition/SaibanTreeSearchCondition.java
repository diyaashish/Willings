package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 裁判ツリーの検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaibanTreeSearchCondition extends SearchCondition {

	/** 先祖裁判SEQ */
	private Long ancestorSaibanSeq;

	/** 子孫裁判SEQ */
	private Long descendantSaibanSeq;

	/** 深さ */
	private Long depth;
}
