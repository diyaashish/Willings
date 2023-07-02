package jp.loioz.app.user.schedule.enums;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 予定表：ユーザー選択タブ種別
 */
@Getter
@AllArgsConstructor
public enum UserSelectType implements DefaultEnum {

	/** 部署(画面表示上は"ユーザー") */
	BUSHO("1", "ユーザー"),
	/** グループ */
	GROUP("2", "グループ");

	/** コード値 */
	private String cd;

	/** 名称 */
	private String val;
}