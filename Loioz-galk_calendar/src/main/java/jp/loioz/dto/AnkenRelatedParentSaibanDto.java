package jp.loioz.dto;

import jp.loioz.domain.value.CaseNumber;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnkenRelatedParentSaibanDto {

	/** 案件ID */
	private Long ankenId;

	/** 分野ID */
	private Long bunyaId;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判枝番 */
	private Long saibanBranchNo;

	/** 事件名 */
	private String jikenName;

	/** 事件番号元号 */
	private CaseNumber jikenNo;

	/** 裁判ツリーSEQ */
	private Long saibanTreeSeq;

	/** 親裁判SEQ */
	private Long parentSeq;

	/** 併合反訴種別 */
	private String connectType;

	/** 本訴フラグ */
	private String honsoFlg;

	/** 本訴フラグ */
	private String kihonFlg;

}
