package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenCustomerGroupByAnkenBean {

	/** 案件ID */
	Long ankenId;

	/** 案件名 */
	String ankenName;

	/** 
	 * 案件の顧客名（カンマ区切りの全顧客名）
	 */
	String customerName;

	/** 
	 * 案件の顧客名かな（カンマ区切りの全顧客名かな）
	 */
	String customerNameKana;

	/** 
	 * 案件ステータス（全顧客の中で最小値のステータス）
	 */
	String ankenStatus;

	/** 
	 * 担当案件フラグ（データ取得時に指定したアカウントSEQが担当している案件:1、担当していない案件:0）
	 * @link jp.loioz.dao.TAnkenCustomerDao#selectCustomerNameAnkenNameGroupByAnken(String, Long) selectCustomerNameAnkenNameGroupByAnken
	 */
	String tantoAnken;
}
