package jp.loioz.app.user.taskManagement.dto;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * タスクの案件選択時の選択用Dto
 */
@Data
public class TaskAnkenSelectListDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 顧客名（カンマ区切り） */
	private String customerName;

	/** 顧客名（カンマ区切り） */
	private String customerNameKana;

	/** 表示用ラベル */
	public String getDispLabel() {

		String dispLabel = "";
		dispLabel += StringUtils.isEmpty(this.ankenName) ? "(案件名未入力)" : this.ankenName;
		dispLabel += " (" + this.ankenId.toString() + ")";
		dispLabel += "　";
		dispLabel += this.customerName;

		return dispLabel;
	}

}
