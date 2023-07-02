package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 *  裁判に関する担当フラグ情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanTantoFlgBean {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 担当裁判フラグ  1:担当 */
	private String tantoSaibanFlg;

}