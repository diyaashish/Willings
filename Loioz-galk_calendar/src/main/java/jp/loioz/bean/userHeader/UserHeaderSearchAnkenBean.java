package jp.loioz.bean.userHeader;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * ヘッダー検索：案件一覧Bean
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class UserHeaderSearchAnkenBean {

	/** 案件ID */
	private Long ankenId;

	/** 分野ID */
	private Long bunyaId;

	/** 分野名 */
	private String bunyaName;

	/** 案件名 */
	private String ankenName;

	/** 案件種別 */
	private String ankenType;

}
