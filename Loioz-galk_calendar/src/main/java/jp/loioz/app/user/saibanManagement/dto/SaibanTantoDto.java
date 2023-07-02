package jp.loioz.app.user.saibanManagement.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SaibanTantoDto {

	/** アカウントSEQ */
	private Long accountSeq;

	/** 担当名（アカウント名） */
	private String tantoName;

	/** 主担当フラグ */
	private String mainTantoFlg;

	/** アカウント色 */
	private String accountColor;

}
