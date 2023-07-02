package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenKanryoFlgGroupByAnkenBean {

	/** 案件ID */
	Long ankenId;

	/** 
	 * 完了フラグ<br>
	 * 案件の顧客全員が完了のときに「1」（完了）<br>
	 * 1人でも完了でなければ「0」
	 */
	String kanryoFlg;

}
