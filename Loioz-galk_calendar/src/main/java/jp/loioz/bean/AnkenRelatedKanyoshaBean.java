package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件-関与者関係者Bean
 * 
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class AnkenRelatedKanyoshaBean {

	/** 案件ID */
	private Long ankenId;

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 代理人関与者 */
	private Long relatedKanyoshaSeq;

	/** 関与者種別 */
	private String kanyoshaType;

	/** 代理人フラグ */
	private String dairiFlg;

	/** 名簿ID */
	private Long personId;

	/** 顧客ID */
	private Long customerId;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 顧客種別 */
	private String customerType;

	/** 関係 */
	private String kankei;

	/** 備考 */
	private String remarks;

}
