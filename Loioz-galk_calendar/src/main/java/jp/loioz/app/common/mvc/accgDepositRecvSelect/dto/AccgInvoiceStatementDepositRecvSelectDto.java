package jp.loioz.app.common.mvc.accgDepositRecvSelect.dto;

import lombok.Data;

/**
 * 預り金選択モーダル表示用Dto
 */
@Data
public class AccgInvoiceStatementDepositRecvSelectDto {

	/** 預り金SEQ */
	private Long depositRecvSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 項目 */
	private String depositItemName;

	/** 発生日 */
	private String depositDate;

	/** 入出金完了フラグ */
	private String depositCompleteFlg;

	/** 入出金タイプ */
	private String depositType;

	/** 入金額 */
	private String depositAmount;

	/** 出金額 */
	private String withdrawalAmount;

	/** 摘要 */
	private String sumText;

	/** 事務所負担フラグ */
	private String tenantBearFlg;

	/** メモ */
	private String memo;

	/** 選択チェックボックス */
	private boolean isChecked;
}
