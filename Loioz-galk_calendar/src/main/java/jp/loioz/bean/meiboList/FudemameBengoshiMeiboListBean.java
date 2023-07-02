package jp.loioz.bean.meiboList;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class FudemameBengoshiMeiboListBean {

	/** レコード件数 */
	private Integer count;

	/** 名簿ID */
	private Long personId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 顧客種別 */
	private String customerType;

	/** 事務所名 */
	private String jimushoName;

	/** 部署名 */
	private String bushoName;

	/** 名前（姓） */
	private String bengoshiNameSei;

	/** 名前（名） */
	private String bengoshiNameMei;

	/** 名前（せい） */
	private String bengoshiNameSeiKana;

	/** 名前（めい） */
	private String bengoshiNameMeiKana;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 住所備考 */
	private String addressRemarks;

	/** 電話番号 */
	private String telNo;

	/** ファックス番号 */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;

	/** 備考 */
	private String remarks;

}
