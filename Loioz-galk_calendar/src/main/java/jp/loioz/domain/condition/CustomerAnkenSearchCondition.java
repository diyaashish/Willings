package jp.loioz.domain.condition;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 顧客に紐づく案件を登録する案件を検索する画面の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAnkenSearchCondition extends SearchCondition {

	// ************************
	// 案件情報
	// ************************
	/** 案件ID */
	private Long ankenId;

	/** 分野 */
	private Long bunya;

	/** 案件名 */
	private String ankenName;

	// ************************
	// 顧客情報
	// ************************
	/** 名簿ID */
	private Long personId;

	/** 顧客名 */
	private String name;

	/** 除外案件IDリスト */
	private List<Long> excludeAnkenIdList;
}
