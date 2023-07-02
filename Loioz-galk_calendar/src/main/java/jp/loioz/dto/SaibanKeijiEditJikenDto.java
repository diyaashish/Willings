package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.CaseNumber;
import lombok.Data;

/**
 * 裁判管理画面の本起訴事件情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanKeijiEditJikenDto {

	// -------------------------------------
	// t_saiban_jiken
	// -------------------------------------
	/** 事件SEQ */
	private Long jikenSeq;

	/** 事件番号 */
	private CaseNumber caseNumber;

	/** 事件名 */
	private String jikenName;
}
