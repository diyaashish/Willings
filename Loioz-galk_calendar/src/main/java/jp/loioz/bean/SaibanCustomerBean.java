package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class SaibanCustomerBean {

	/** 裁判-顧客 */

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 名簿ID */
	@Column(name = "person_id")
	private Long personId;

	/** 顧客ID */
	@Column(name = "customer_id")
	private Long customerId;

	/** 当事者表記 */
	@Column(name = "saiban_tojisha_hyoki")
	private String saibanTojishaHyoki;

	/** 筆頭フラグ */
	@Column(name = "main_flg")
	private String mainFlg;

	/** 顧客情報 */

	/** 顧客名（姓） */
	@Column(name = "customer_name_sei")
	private String customerNameSei;

	/** 顧客名（せい） */
	@Column(name = "customer_name_sei_kana")
	private String customerNameSeiKana;

	/** 顧客名（名） */
	@Column(name = "customer_name_mei")
	private String customerNameMei;

	/** 顧客名（名） */
	@Column(name = "customer_name_mei_kana")
	private String customerNameMeiKana;

	/** 顧客フラグ */
	@Column(name = "customer_flg")
	private String customerFlg;

	/** 顧問フラグ */
	@Column(name = "advisor_flg")
	private String advisorFlg;

	/** 顧客種別 */
	@Column(name = "customer_type")
	private String customerType;

}
