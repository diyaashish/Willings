package jp.loioz.app.user.gyomuHistory.enums;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Sessionに保持している業務履歴のステータス
 */
@Getter
@AllArgsConstructor
public enum SessionStatus implements DefaultEnum {

	NO_DATA("1", "データなし"),
	NOT_EQUAL_TRANSITION_ID("2", "遷移元IDと異なる"),
	HAS_DATA("3", "想定データを取得"),;

	/** コード値 */
	private String cd;

	/** 名称 */
	private String val;

}
