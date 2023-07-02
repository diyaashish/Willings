package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 顧客-案件一覧検索結果格納用Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class CustomerAnkenSearchListBean {

	/** 案件ID */
	private Long ankenId;

	/** 分野名 */
	private String bunyaName;

	/** 案件名 */
	private String ankenName;

	/** 顧客名 */
	private String customerName;

}
