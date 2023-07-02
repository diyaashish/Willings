package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 関与者一覧の検索結果Bean
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class KanyoshaListBean {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

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

	/** 顧客せい */
	private String customerNameSeiKana;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客めい */
	private String customerNameMeiKana;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 住所備考 */
	private String addressRemarks;

	/** 銀行名 */
	private String ginkoName;

	/** 支店名 */
	private String shitenName;

	/** 支店番号 */
	private String shitenNo;

	/** 口座種別 */
	private String kozaType;

	/** 口座番号 */
	private String kozaNo;

	/** 口座名 */
	private String kozaName;

	/** 口座名かな */
	private String kozaNameKana;

	/** 口座備考 */
	private String kozaRemarks;

	/** 案件ID */
	private Long ankenId;

	/** 関与者表示順(案件に対する) */
	private Long dispOrder;

	/** 関与者関係(案件に対する) */
	private String kankei;

	/** 関与者備考 */
	private String kanyoshaRemarks;

	/** 弁護士名簿付帯情報：事務所名 */
	private String jimushoName;

}
