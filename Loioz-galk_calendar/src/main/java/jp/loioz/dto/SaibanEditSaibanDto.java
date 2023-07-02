package jp.loioz.dto;

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
public class SaibanEditSaibanDto {

	// 裁判情報

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判ID */
	private SaibanId saibanId;

	/** 裁判ステータス */
	private SaibanStatus saibanStatus;

	// 裁判ツリー

	/** 親裁判SEQ */
	private Long parentSeq;

	/** 併合・反訴種別 */
	private String connectType;

	/** 本訴フラグ */
	private String honsoFlg;

	/** 基本事件フラグ */
	private String kihonFlg;

	// 裁判-事件

	/** 事件番号 */
	private CaseNumber caseNumber;

	/** 事件名 */
	private String jikenName;
}
