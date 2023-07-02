package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 問い合わせ一覧用検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToiawaseListSearchCondition extends SearchCondition {

	/** 件名・文字列 */
	private String text;

	/** 問い合わせステータス */
	private String toiawaseStatus;

}
