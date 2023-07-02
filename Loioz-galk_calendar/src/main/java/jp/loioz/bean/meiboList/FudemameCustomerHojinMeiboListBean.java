package jp.loioz.bean.meiboList;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class FudemameCustomerHojinMeiboListBean {

	/** レコード件数 */
	private Integer count;

	/** 名簿No */
	private Long personId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 顧客種別 */
	private String customerType;

	/** 名前（姓） */
	private String customerNameSei;

	/** 名前（せい） */
	private String customerNameSeiKana;

	/** 代表者名 */
	private String daihyoName;

	/** 代表者名かな */
	private String daihyoNameKana;

	/** 代表者ポジション */
	private String daihyoPositionName;

	/** 担当者名 */
	private String tantoName;

	/** 担当者名かな */
	private String tantoNameKana;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 電話番号 */
	private String telNo;

	/** FAX番号 */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;

	/** 備考 */
	private String remarks;

}
