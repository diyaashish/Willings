package jp.loioz.bean.userHeader;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * ヘッダー検索：名簿一覧Bean
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class UserHeaderSearchPersonBean {

	/** 名簿ID */
	private Long personId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 顧客種別 */
	private String customerType;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客せい */
	private String customerNameSeiKana;

	/** 顧客めい */
	private String customerNameMeiKana;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

}
