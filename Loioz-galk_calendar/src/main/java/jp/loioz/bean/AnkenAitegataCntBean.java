package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 *  案件に関する相手方情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenAitegataCntBean {

	/** 案件ID */
	private AnkenId ankenId;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方数 */
	private Long numberOfAitegata;

}