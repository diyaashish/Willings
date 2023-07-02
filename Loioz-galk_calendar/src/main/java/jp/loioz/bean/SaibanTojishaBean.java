package jp.loioz.bean;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 裁判当時者Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanTojishaBean {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 姓 */
	private String nameSei;

	/** 名 */
	private String nameMei;

	/** 姓(かな) */
	private String nameSeiKana;

	/** 名(かな) */
	private String nameMeiKana;

	/** 関与者種別 */
	private String kanyoshaType;

	/** 当事者表記 */
	private String saibanTojishaHyoki;

	/** 主担当フラグ */
	private String mainFlg;

	/** 代理フラグ */
	private String dairiFlg;

	/** 作成日時 */
	private LocalDateTime createdAt;

	/** 顧客当事者かどうか */
	public boolean isCustomerTojisha() {
		return kanyoshaType == null;
	}

	/** その他当事者かどうか */
	public boolean isOtherTojisha() {
		return kanyoshaType != null;
	}

}
