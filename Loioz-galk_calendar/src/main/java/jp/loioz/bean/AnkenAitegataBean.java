package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件相手方情報Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenAitegataBean {

	/** 案件ID */
	private Long ankenId;

	/** 関与者ID */
	private Long kanyoshaSeq;

	/** 表示順 */
	private Long dispOrder;

	/** 代理フラグ */
	private String dairiFlg;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方名かな */
	private String aitegataNameKana;

	/** 旧姓 */
	private String oldName;

	/** 旧姓かな */
	private String oldNameKana;

}
