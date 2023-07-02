package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

/**
 * 裁判（刑事）公判担当検察官のBean
 */
@Data
@Entity
public class SaibanKensatsukanBean {

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 本起訴事件SEQ */
	@Column(name = "main_jiken_seq")
	private Long mainJikenSeq;

	/** 捜査機関ID */
	@Column(name = "sosakikan_id")
	private Long sosakikanId;

	/** 検察庁名 */
	@Column(name = "kensatsucho_name")
	private String kensatsuchoName;

	/** 担当部 */
	@Column(name = "kensatsucho_tanto_bu_name")
	private String kensatsuchoTantoBuName;

	/** 検察官名 */
	@Column(name = "kensatsukan_name")
	private String kensatsukanName;

	/** 検察官名かな */
	@Column(name = "kensatsukan_name_kana")
	private String kensatsukanNameKana;

	/** 事務官名 */
	@Column(name = "jimukan_name")
	private String jimukanName;

	/** 事務官名かな */
	@Column(name = "jimukan_name_kana")
	private String jimukanNameKana;

	/** 検察電話番号 */
	@Column(name = "kensatsu_tel_no")
	private String kensatsuTelNo;

	/** 検察内線番号 */
	@Column(name = "kensatsu_extension_no")
	private String kensatsuExtensionNo;

	/** 検察FAX番号 */
	@Column(name = "kensatsu_fax_no")
	private String kensatsuFaxNo;

	/** 検察号室 */
	@Column(name = "kensatsu_room_no")
	private String kensatsuRoomNo;

	/** 検察備考 */
	@Column(name = "kensatsu_remarks")
	private String kensatsuRemarks;

}
