package jp.loioz.bean;

import java.sql.Blob;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

/**
 * テナント情報と既定口座情報<br>
 * ※既定口座がない場合は、事務所口座の1番目
 */
@Entity
@Data
public class TenantDetailKozaBean {

	/** テナントSEQ */
	@Column(name = "tenant_seq")
	private Long personId;

	/** 事務所名 */
	@Column(name = "tenant_name")
	private String tenantName;

	/** 事務所名（カナ） */
	@Column(name = "tenant_name_kana")
	private String tenantNameKana;
	/** 個人・法人区分 */
	@Column(name = "tenant_type")
	private String tenantType;

	/** 郵便番号 */
	@Column(name = "tenant_zip_cd")
	private String tenantZipCd;

	/** 住所１ */
	@Column(name = "tenant_address1")
	private String tenantAddress1;

	/** 住所２ */
	@Column(name = "tenant_address2")
	private String tenantAddress2;

	/** 電話番号 */
	@Column(name = "tenant_tel_no")
	private String tenantTelNo;

	/** FAX番号 */
	@Column(name = "tenant_fax_no")
	private String tenantFaxNo;

	/** 適格請求書発行事業者登録番号 */
	@Column(name = "tenant_invoice_registration_no")
	private String tenantInvoiceRegistrationNo;

	/** 事務所印画像 */
	@Column(name = "tenant_stamp_img")
	private Blob tenantStampImg;

	/** 事務所印画像拡張子 */
	@Column(name = "tenant_stamp_img_extension")
	private String tenantStampImgExtension;

	/** 代表姓 */
	@Column(name = "tenant_daihyo_name_sei")
	private String tenantDaihyoNameSei;

	/** 代表姓（かな） */
	@Column(name = "tenant_daihyo_name_sei_kana")
	private String tenantDaihyoNameSeiKana;

	/** 代表名 */
	@Column(name = "tenant_daihyo_name_mei")
	private String tenantDaihyoNameMei;

	/** 代表名（かな） */
	@Column(name = "tenant_daihyo_name_mei_kana")
	private String tenantDaihyoNameMeiKana;

	/** 代表者メールアドレス */
	@Column(name = "tenant_daihyo_mail_address")
	private String tenantDaihyoMailAddress;

	/** 消費税の端数処理の方法 */
	@Column(name = "tax_hasu_type")
	private String taxHasuType;

	/** 報酬の端数処理の方法 */
	@Column(name = "hoshu_hasu_type")
	private String hoshuHasuType;

	/** 口座連番 */
	@Column(name = "branch_no")
	private Long branchNo;

	/** 表示名 */
	@Column(name = "label_name")
	private String labelName;

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

	/** 口座名義かな */
	@Column(name = "koza_name_kana")
	private String kozaNamekana;

	/** 既定利用フラグ */
	@Column(name = "default_use_flg")
	private String defaultUseFlg;

}