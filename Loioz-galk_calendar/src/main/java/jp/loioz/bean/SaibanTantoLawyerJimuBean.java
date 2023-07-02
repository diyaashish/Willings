package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 *  裁判に関する担当弁護士、担当事務情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanTantoLawyerJimuBean {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 担当タイプ */
	private String tantoType;

	/** 担当者名 （担当者全ての名前をカンマ区切りで） */
	private String tantoName;
}