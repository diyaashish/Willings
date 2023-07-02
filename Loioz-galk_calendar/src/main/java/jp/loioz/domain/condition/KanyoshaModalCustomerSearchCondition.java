package jp.loioz.domain.condition;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 関与者検索モーダル内、一覧検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanyoshaModalCustomerSearchCondition extends SearchCondition {

	/** 顧客種別 */
	private String customerType;

	/** 除外とする顧客の名簿ID */
	private List<Long> excludePersonIdList;

	/** キーワード */
	private String keywords;

}
