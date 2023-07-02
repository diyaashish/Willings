package jp.loioz.domain.value;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.CommonConstant.IdFormat;
import lombok.EqualsAndHashCode;

/**
 * 案件ID
 */
@EqualsAndHashCode(callSuper = true)
@Domain(valueType = Long.class, accessorMethod = "asLong", factoryMethod = "of")
public class AnkenId extends Id<AnkenId> {

	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ
	 *
	 * @param numValue ID数値
	 * @param strValue ID文字列
	 */
	private AnkenId(Long numValue, String strValue) {
		super(numValue, strValue);
	}

	/**
	 * 数値から案件IDを作成する
	 *
	 * @param numValue 数値
	 * @return 案件ID
	 */
	public static AnkenId of(Number numValue) {
		return Id.of(numValue, AnkenId::new, IdFormat.ANKEN.getVal());
	}

	/**
	 * 文字列から案件IDを作成する
	 *
	 * @param strValue 文字列
	 * @return 案件ID
	 */
	public static AnkenId of(String strValue) {
		return Id.of(strValue, AnkenId::new, IdFormat.ANKEN.getVal());
	}
}
