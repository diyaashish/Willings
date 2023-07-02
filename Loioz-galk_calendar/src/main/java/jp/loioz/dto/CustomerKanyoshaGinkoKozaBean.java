package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class CustomerKanyoshaGinkoKozaBean {

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
}
