package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class KanyoshaTojishaDto {

	/** 裁判SEQ */
	Long saibanSeq;

	/** 関与者SEQ */
	Long kanyoshaSeq;

	/** 顧客ID */
	Long customerId;

	/** 事故結合関与者SEQ */
	Long relatedKanyoshaSeq;

	/** 名前 */
	String name;

	/** 関与者種別 */
	String kanyoshaType;

	/** 当時者表記 */
	String tojishaHyoki;

	/** 筆頭フラグ */
	String mainFlg;

	/** 代理フラグ */
	String dairiFlg;

}
