package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 裁判-関与者関係者Bean
 * RelatedKanyoshaSeqを自己結合して取得する用
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanRelatedSelfJoinKanyoshaBean {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 関与者種別 */
	private String kanyoshaType;

	/** 当事者表記 */
	private String saibanTojishaHyoki;

	/** メインフラグ */
	private String mainFlg;

	/** 代理フラグ */
	private String dairiFlg;

	/** 名簿ID */
	private Long personId;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客せい */
	private String customerNameSeiKana;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客めい */
	private String customerNameMeiKana;

	/** 代理関与者SEQ */
	private Long alternateKanyoshaSeq;

	/** 代理関与者種別 */
	private String alternateKanyoshaType;

	/** 代理当事者表記 */
	private String alternateSaibanTojishaHyoki;

	/** 代理メインフラグ */
	private String alternateMainFlg;

	/** 代理フラグ */
	private String alternateDairiFlg;

	/** 代理名簿ID */
	private Long alternatePersonId;

	/** 代理顧客姓 */
	private String alternateCustomerNameSei;

	/** 代理顧客せい */
	private String alternateCustomerNameSeiKana;

	/** 代理顧客名 */
	private String alternateCustomerNameMei;

	/** 代理顧客めい */
	private String alternateCustomerNameMeiKana;

}
