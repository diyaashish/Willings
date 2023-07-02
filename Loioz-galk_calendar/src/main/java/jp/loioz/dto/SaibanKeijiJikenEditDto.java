package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 裁判(刑事弁護)：事件情報のDto
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class SaibanKeijiJikenEditDto {

	/** 事件SEQ */
	private Long jikenSeq;

	/** 事件番号元号 */
	private String jikenGengo;

	/** 事件番号年 */
	private String jikenYear;

	/** 事件番号符号 */
	private String jikenMark;

	/** 事件番号 */
	private String jikenNo;

	/** 事件名 */
	private String jikenName;
}