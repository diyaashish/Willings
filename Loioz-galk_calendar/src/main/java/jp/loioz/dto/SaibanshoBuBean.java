package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class SaibanshoBuBean {

	/** 係属部ID */
	@Column(name = "keizoku_bu_id")
	Long keizokuBuId;

	/** 裁判所ID */
	@Column(name = "saibansho_id")
	Long saibanshoId;

	/** 都道府県ID */
	@Column(name = "todofuken_id")
	String todofukenId;

	/** 係属部名 */
	@Column(name = "keizoku_bu_name")
	String keizokuBuName;

	/** 電話番号 */
	@Column(name = "keizoku_bu_tel_no")
	String keizokuBuTelNo;

	/** FAX番号 */
	@Column(name = "keizoku_bu_fax_no")
	String keizokuBuFaxNo;

}