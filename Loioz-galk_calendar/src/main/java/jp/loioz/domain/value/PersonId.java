package jp.loioz.domain.value;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.CommonConstant.IdFormat;
import lombok.EqualsAndHashCode;

/**
 * 名簿ID
 */
@EqualsAndHashCode(callSuper = true)
@Domain(valueType = Long.class, accessorMethod = "asLong", factoryMethod = "of")
public class PersonId extends Id<PersonId> {

	private static final long serialVersionUID = 2637264890873614656L;

	/**
	 * コンストラクタ
	 *
	 * @param numValue ID数値
	 * @param strValue ID文字列
	 */
	private PersonId(Long numValue, String strValue) {
		super(numValue, strValue);
	}

	/**
	 * 数値から名簿IDを作成する
	 *
	 * @param numValue 数値
	 * @return 名簿ID
	 */
	public static PersonId of(Number numValue) {
		return Id.of(numValue, PersonId::new, IdFormat.CUSTOMER.getVal());
	}

	/**
	 * 文字列から名簿IDを作成する
	 *
	 * @param strValue 文字列
	 * @return 名簿ID
	 */
	public static PersonId of(String strValue) {
		return Id.of(strValue, PersonId::new, IdFormat.CUSTOMER.getVal());
	}
}
