package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 案件管理画面：顧客検索モーダルの検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnkenCustomerSearchCondition extends SearchCondition {

	/** 案件ID */
	private Long ankenId;

	/** 顧客ID */
	private Long personId;

	/** 顧客名 */
	private String name;

	/** 電話番号 */
	private String telNo;
}
