package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件検索モーダル：相手方 用のDto
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenKanyoshaListDto {

	// -------------------------------------
	// t_anken_related_kanyosha
	// -------------------------------------
	/** 案件Id */
	private Long ankenId;

	/** 関与者SEQ */
	private Long kanyoshaSeq;

	// -------------------------------------
	// t_person
	// -------------------------------------
	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 個人/法人/弁護士 */
	private String customerType;

	// -------------------------------------
	// t_person_add_hojin
	// -------------------------------------
	/** 代表者名 */
	private String daihyoName;

}
