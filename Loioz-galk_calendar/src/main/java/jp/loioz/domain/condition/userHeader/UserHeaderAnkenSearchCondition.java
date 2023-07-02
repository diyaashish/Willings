package jp.loioz.domain.condition.userHeader;

import jp.loioz.domain.condition.SearchCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * ヘッダー検索：案件一覧の検索用オブジェクト
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHeaderAnkenSearchCondition extends SearchCondition implements UserHeaderGetCountCondition {

	/** キーワード */
	private String keywords;

	/** 件数を取得する場合 */
	private boolean isGetCount;

	/** 取得上限を自身で設定する場合の件数 */
	private Integer limitCount;

}
