package jp.loioz.app.user.nyushukkinYotei.shukkin.enums;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 出金予定一覧 日付絞り込み条件Enum
 */
@Getter
@AllArgsConstructor
@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
public enum ShukkinSearchYoteiDateType implements DefaultEnum {

	ALL("1", "すべて"),
	SPECIFICATION("2", "日付指定"),
	;

	/** コード値 */
	private String cd;

	/** 名称 */
	private String val;

	/**
	 * コードからEnum値を取得する
	 *
	 * @param cd コード
	 * @return Enum
	 */
	public static ShukkinSearchYoteiDateType of(String cd) {
		return DefaultEnum.getEnum(ShukkinSearchYoteiDateType.class, cd);
	}
}
