package jp.loioz.app.user.azukarikinManagement.Enums;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerTotalSearchType implements DefaultEnum {

	ALL("0", "すべて"),
	PLUS("1", "プラスのみ"),
	MINUS("2", "マイナスのみ"),
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
	public static CustomerTotalSearchType of(String cd) {
		return DefaultEnum.getEnum(CustomerTotalSearchType.class, cd);
	}
}
