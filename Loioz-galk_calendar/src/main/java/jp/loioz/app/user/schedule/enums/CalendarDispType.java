package jp.loioz.app.user.schedule.enums;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 予定表：カレンダー表示種別
 */
@Getter
@AllArgsConstructor
public enum CalendarDispType implements DefaultEnum {

	DAILY("1", "日"),
	WEEKLY("2", "週"),
	MONTHLY("3", "月"),
	ROOM("4", "施設");

	/** コード値 */
	private String cd;

	/** 名称 */
	private String val;
}