package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 *  裁判に関する相手方情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanAitegataBean {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方数 */
	private Long numberOfAitegata;

}