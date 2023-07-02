package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 関与者情報Bean
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class KanyoshaBean {

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 関係 (案件に対する) */
	private String kankei;

	/** 表示順 (案件に対する) */
	private Long dispOrder;

	/** 備考 (案件に対する) */
	private String remarks;

	/** 顧客ID */
	private Long customerId;

	/** 顧客種別(個人・法人・代理人) */
	private String customerType;

	/** 顧客姓 (関与者姓) */
	private String customerNameSei;

	/** 顧客名 (関与者名) */
	private String customerNameMei;

	/** 顧客せい (関与者せい) */
	private String customerNameSeiKana;

	/** 顧客めい (関与者めい) */
	private String customerNameMeiKana;

	// ▼ 企業・団体付帯情報
	/** 代表者 */
	private String daihyoName;

	/** 担当者 */
	private String tantoName;

	// ▼ 弁護士付帯情報
	/** 事務所名 */
	private String jimushoName;

	/** 部署名 */
	private String bushoName;

	// 裁判SEQをキーとしてデータ取得したときのみ、パラメータを保持する
	/** 裁判SEQ */
	private Long saibanSeq;
}
