package jp.loioz.app.user.azukariItem.enums;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
public enum AzukariSearchStatus implements DefaultEnum {

	ALL("1", "すべて"),
	HOKAN("2", "保管中"),
	HENKYAKU("3", "返却済"),
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
	public static AzukariSearchStatus of(String cd) {
		return DefaultEnum.getEnum(AzukariSearchStatus.class, cd);
	}
}
