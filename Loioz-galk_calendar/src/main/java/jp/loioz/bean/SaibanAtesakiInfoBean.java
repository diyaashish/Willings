package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanAtesakiInfoBean {
	/** 裁判所名 */
	private String saibanshoName;

	/**  書記官名 */
	private String tantoShoki;

	/** 裁判所FAX番号 */
	private String keizokuBuFaxNo;
}
