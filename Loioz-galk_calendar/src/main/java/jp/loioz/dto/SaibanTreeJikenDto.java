package jp.loioz.dto;

import jp.loioz.domain.value.CaseNumber;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaibanTreeJikenDto {

	/** 裁判ツリーSEQ **/
	private Long saibanTreeSeq;

	/** 裁判SEQ **/
	private Long saibanSeq;

	/** 併合・反訴 **/
	private String connectType;

	/** 事件SEQ */
	private Long jikenSeq;

	/** 事件No **/
	private CaseNumber jikenNo;

	/** 事件名 **/
	private String jikenName;

}
