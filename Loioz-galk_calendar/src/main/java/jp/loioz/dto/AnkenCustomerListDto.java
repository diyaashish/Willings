package jp.loioz.dto;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.PersonId;
import lombok.Data;

/**
 * 顧客検索モーダルに表示する 顧客一覧情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenCustomerListDto {

	public static final String GROUP_CONCAT_SEPARATOR = ",";

	/** 名簿ID */
	private PersonId personId;

	/** 顧客名 */
	private String customerName;

	/** 顧客名かな */
	private String customerNameKana;

	/** 種別 */
	private String customerType;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 生年月日 */
	private LocalDate birthday;

	/** 生年月日表示区分 */
	private String birthdayDisplayType;

	/** 郵便番号 */
	private String zipCode;

	/** 住所1 */
	private String address1;

	/** 住所2 */
	private String address2;

}
