package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 関与者一覧画面の検索用オブジェクト
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanyoshaListSearchCondition extends SearchCondition {

	/** 案件ID */
	private Long ankenId;

}
