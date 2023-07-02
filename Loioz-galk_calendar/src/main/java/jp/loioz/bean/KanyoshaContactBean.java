package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 関与者連絡先情報Bean
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class KanyoshaContactBean {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 顧客ID */
	private Long customerId;

	/** 郵便番号 */
	private String zipCode;

	/** 地域 */
	private String address1;

	/** 番地・建物 */
	private String address2;

	/** 住所備考 */
	private String addressRemarks;

	/** 電話連絡可否 */
	private String contactType;

	/** 郵送先住所区分 */
	private String transferAddressType;

	/** 郵送先郵便番号 */
	private String transferZipCode;

	/** 郵送先地域 */
	private String transferAddress1;

	/** 郵送先番地・建物名 */
	private String transferAddress2;

	/** 宛名 */
	private String transferName;

	/** 郵送方法 */
	private String transferType;

	/*******************************
	 * 個人付帯情報
	 *******************************/

	/** 住民票登録地住所区分 */
	private String juminhyoAddressType;

	/** 住民票登録地郵便番号 */
	private String juminhyoZipCode;

	/** 住民票登録地地域 */
	private String juminhyoAddress1;

	/** 住民票登録地番地・建物名 */
	private String juminhyoAddress2;

	/** 住民票登録地備考 */
	String juminhyoRemarks;

	/** 職業 */
	String job;

	/** 職業 */
	String workPlace;

	/** 職業 */
	String kojinBushoName;

	/*******************************
	 * 法人付帯情報
	 *******************************/

	/** 代表者 */
	private String daihyoName;

	/** 代表者かな */
	private String daihyoNameKana;

	/** 代表者役職 */
	private String daihyoPositionName;

	/** 担当者 */
	private String tantoName;

	/** 担当者かな */
	private String tantoNameKana;

	/** 登記住所区分 */
	private String tokiAddressType;

	/** 登記郵便番号 */
	private String tokiZipCode;

	/** 登記地域 */
	private String tokiAddress1;

	/** 登記番地・建物名 */
	private String tokiAddress2;

	/** 登記住所備考 */
	String tokiAddressRemarks;

	/*******************************
	 * 弁護士付帯情報
	 *******************************/

	/** 事務所名 */
	private String jimushoName;

	/** 部署・役職名 */
	private String lawyerBushoName;

}
