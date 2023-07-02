package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 *  案件に関する担当フラグ情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenTantoFlgBean {

	/** 案件ID */
	private AnkenId ankenId;

	/** 担当案件フラグ  1:担当 */
	private String tantoAnkenFlg;

}