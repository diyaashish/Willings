package jp.loioz.dto;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.SaibanId;
import lombok.Data;

/**
 * 裁判管理画面の裁判一覧情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanKeijiEditSaibanDto {

	// -------------------------------------
	// t_saiban
	// -------------------------------------
	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判ID */
	private SaibanId saibanId;

	/** 裁判ステータス */
	private SaibanStatus saibanStatus;

	/** 起訴日/上訴日 */
	private LocalDate saibanStartDate;

	/** 裁判所ID */
	private Long saibanshoId;

	// -------------------------------------
	// t_saiban_jiken
	// -------------------------------------
	/** 事件SEQ */
	private Long jikenSeq;

	/** 事件番号 */
	private CaseNumber caseNumber;

	/** 事件名 */
	private String jikenName;

	// -------------------------------------
	// t_saiban_jiken
	// -------------------------------------
	/** 担当弁護士 */
	private Long tantoLawyer;

	/** 担当事務 */
	private Long tantoJimu;
}
