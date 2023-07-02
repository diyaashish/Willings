package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件(刑事弁護)画面の裁判情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenKeijiSaibanDto {

	// t_saiban
	/** 裁判SEQ */
	private Long saibanSeq;

	/** 案件ID */
	private Long ankenId;

	/** 裁判枝番 */
	private Long saibanBranchNo;

	/** 裁判ステータス */
	private String saibanStatus;

	/** 裁判所ID */
	private Long saibanshoId;

	// m_saibansho
	/** 裁判所名 */
	private String saibanshoName;
}
