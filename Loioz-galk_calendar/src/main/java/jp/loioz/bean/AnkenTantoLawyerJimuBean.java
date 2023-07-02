package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 *  案件に関する担当者（弁護士、事務）情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenTantoLawyerJimuBean {

	/** 案件ID */
	private AnkenId ankenId;

	/** 担当タイプ */
	private String tantoType;

	/** 担当者名 （担当者全ての名前をカンマ区切りで） */
	private String tantoName;
}