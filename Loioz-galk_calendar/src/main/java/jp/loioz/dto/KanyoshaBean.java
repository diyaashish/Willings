package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class KanyoshaBean {

	/** 個人、企業・団体、弁護士 */
	@Column(name = "customer_type")
	private String customerType;

	/** 関与者名 */
	@Column(name = "kanyosha_name")
	private String  kanyoshaName;
	
	/** 代表者 */
	@Column(name = "daihyo_name")
	private String daihyoName;
	
	/** 名簿ID */
	@Column(name = "person_id")
	private Long personId;
	
	/** 郵便番号 */
	@Column(name = "zip_code")
	private String zipCode;
	
	/** 地域 */
	@Column(name = "address1")
	private String address1;

	/** 番地・建物名 */
	@Column(name = "address2")
	private String address2;
	
	/** 口座名義 */
	@Column(name = "koza_name")
	private String kozaName;
	
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
	
}