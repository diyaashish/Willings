package jp.loioz.dto;

import jp.loioz.domain.value.CaseNumber;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaibanTsuikisoJikenDto {

	/** 裁判SEQ **/
	private Long saibanSeq;

	/** 事件SEQ **/
	private Long jikenSeq;

	/** 事件No **/
	private CaseNumber jikenNo;

	/** 事件名 **/
	private String jikenName;

}
