package jp.loioz.domain.condition;

import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 入出金項目：一覧検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuncOldKaikeiSettingListSearchCondition extends SearchCondition {

	/** 入出金種別 */
	private String nyushukkinType;

	/** 検索条件が存在するか */
	@Override
	public boolean hasCondition() {
		return StringUtils.isNotEmpty(this.nyushukkinType);
	}

}
