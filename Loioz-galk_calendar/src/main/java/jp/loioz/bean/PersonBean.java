package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class PersonBean {

	/** 名簿ID */
	@Column(name = "person_id")
	private String personId;

	/** 顧客ID */
	@Column(name = "customer_id")
	private String customerId;

	/** 個人・法人区分 */
	@Column(name = "customer_type")
	private String customerType;

	/** 顧客姓 */
	@Column(name = "customer_name_sei")
	private String customerNameSei;

	/** 顧客姓かな */
	@Column(name = "customer_name_sei_kana")
	private String customerNameSeiKana;

	/** 顧客名 */
	@Column(name = "customer_name_mei")
	private String customerNameMei;

	/** 顧客名かな */
	@Column(name = "customer_name_mei_kana")
	private String customerNameMeiKana;

	/** 郵便番号 */
	@Column(name = "zip_code")
	private String zipCode;

	/** 地域 */
	@Column(name = "address1")
	private String address1;

	/** 番地・建物名 */
	@Column(name = "address2")
	private String address2;

	/** 電話連絡可否 */
	@Column(name = "contact_type")
	private String contactType;

	/** 郵送先住所区分 */
	@Column(name = "transfer_address_type")
	private String transferAddressType;

	/** 郵送先郵便番号 */
	@Column(name = "transfer_zip_code")
	private String transferZipCode;

	/** 郵送先地域 */
	@Column(name = "transfer_address1")
	private String transferAddress1;

	/** 郵送先番地・建物名 */
	@Column(name = "transfer_address2")
	private String transferAddress2;

	/** 宛名 */
	@Column(name = "transfer_name")
	private String transferName;

	/** 郵送方法 */
	@Column(name = "transfer_type")
	private String transferType;

	/** 銀行名 */
	@Column(name = "ginko_name")
	private String ginkoName;

	/** 支店名 */
	@Column(name = "shiten_name")
	private String shitenName;

	/** 支店番号 */
	@Column(name = "shiten_no")
	private String shitenNo;

	/** 口座種類 */
	@Column(name = "koza_type")
	private String kozaType;

	/** 口座番号 */
	@Column(name = "koza_no")
	private String kozaNo;

	/** 口座名義 */
	@Column(name = "koza_name")
	private String kozaName;

	/** 口座名義カナ */
	@Column(name = "koza_name_kana")
	private String kozaNameKana;

	/** 備考 */
	@Column(name = "address_remarks")
	private String addressRemarks;

	/*******************************
	 * 個人付帯情報
	 *******************************/

	/** 住民票登録地住所区分 */
	@Column(name = "juminhyo_address_type")
	private String juminhyoAddressType;

	/** 住民票登録地郵便番号 */
	@Column(name = "juminhyo_zip_code")
	private String juminhyoZipCode;

	/** 住民票登録地地域 */
	@Column(name = "juminhyo_address1")
	private String juminhyoAddress1;

	/** 住民票登録地番地・建物名 */
	@Column(name = "juminhyo_address2")
	private String juminhyoAddress2;

	/** 住民票登録地備考 */
	@Column(name = "juminhyo_remarks")
	String juminhyoRemarks;

	/** 職業 */
	@Column(name = "job")
	String job;

	/** 職業 */
	@Column(name = "work_place")
	String workPlace;

	/** 職業 */
	@Column(name = "kojin_busho_name")
	String kojinBushoName;

	/*******************************
	 * 法人付帯情報
	 *******************************/

	/** 代表者 */
	@Column(name = "daihyo_name")
	private String daihyoName;

	/** 代表者かな */
	@Column(name = "daihyo_name_kana")
	private String daihyoNameKana;

	/** 代表者役職 */
	@Column(name = "daihyo_position_name")
	private String daihyoPositionName;

	/** 担当者 */
	@Column(name = "tanto_name")
	private String tantoName;

	/** 担当者かな */
	@Column(name = "tanto_name_kana")
	private String tantoNameKana;

	/** 登記住所区分 */
	@Column(name = "toki_address_type")
	private String tokiAddressType;

	/** 登記郵便番号 */
	@Column(name = "toki_zip_code")
	private String tokiZipCode;

	/** 登記地域 */
	@Column(name = "toki_address1")
	private String tokiAddress1;

	/** 登記番地・建物名 */
	@Column(name = "toki_address2")
	private String tokiAddress2;

	/** 登記住所備考 */
	@Column(name = "toki_address_remarks")
	String tokiAddressRemarks;

	/*******************************
	 * 弁護士付帯情報
	 *******************************/

	/** 事務所名 */
	@Column(name = "jimusho_name")
	private String jimushoName;

	/** 部署・役職名 */
	@Column(name = "lawyer_busho_name")
	private String lawyerBushoName;


}
