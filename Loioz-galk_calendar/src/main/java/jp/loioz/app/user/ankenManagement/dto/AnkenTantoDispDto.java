package jp.loioz.app.user.ankenManagement.dto;

import lombok.Data;

@Data
public class AnkenTantoDispDto {

	/** 案件主担当フラグ */
	private boolean isMain;

	/** アカウントSEQ */
	private Long accountSeq;

	/** アカウント名 */
	private String accountName;

	/** 担当種別枝番 */
	private Long tantoTypeBranchNo;

	/** アカウント色 */
	private String accountColor;

	/** アカウント種別 */
	private String accountTypeStr;

}
