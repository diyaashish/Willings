package jp.loioz.domain.condition;

import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 預かり金項目：一覧検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRecvMasterListSearchCondition extends SearchCondition {

	/** 預り金種別 */
	private String depositType;

	/** 検索条件が存在するか */
	public boolean hasCondition() {
		return StringUtils.isNotEmpty(this.depositType);
	}

}
