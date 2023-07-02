package jp.loioz.domain.value;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.CommonConstant.IdFormat;
import lombok.EqualsAndHashCode;

/**
 * 顧客ID
 */
@EqualsAndHashCode(callSuper = true)
@Domain(valueType = Long.class, accessorMethod = "asLong", factoryMethod = "of")
public class CustomerId extends Id<CustomerId> {

	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ
	 *
	 * @param numValue ID数値
	 * @param strValue ID文字列
	 */
	private CustomerId(Long numValue, String strValue) {
		super(numValue, strValue);
	}

	/**
	 * 数値から顧客IDを作成する
	 *
	 * @param numValue 数値
	 * @return 顧客ID
	 */
	public static CustomerId of(Number numValue) {
		return Id.of(numValue, CustomerId::new, IdFormat.CUSTOMER.getVal());
	}

	/**
	 * 文字列から顧客IDを作成する
	 *
	 * @param strValue 文字列
	 * @return 顧客ID
	 */
	public static CustomerId of(String strValue) {
		return Id.of(strValue, CustomerId::new, IdFormat.CUSTOMER.getVal());
	}
}
