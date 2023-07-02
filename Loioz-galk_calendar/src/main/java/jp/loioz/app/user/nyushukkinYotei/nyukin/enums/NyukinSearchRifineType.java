package jp.loioz.app.user.nyushukkinYotei.nyukin.enums;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入金予定一覧 絞り込み条件Enum
 */
@Getter
@AllArgsConstructor
@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
public enum NyukinSearchRifineType implements DefaultEnum {

	ALL("1", "すべて"),
	PROCESSED("2", "処理済"),
	UNPROCESSED("3", "未処理");

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
	public static NyukinSearchRifineType of(String cd) {
		return DefaultEnum.getEnum(NyukinSearchRifineType.class, cd);
	}

}
