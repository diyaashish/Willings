package jp.loioz.app.common.mvc.accgDepositRecvSelect.form;

import java.util.List;

import jp.loioz.app.common.mvc.accgDepositRecvSelect.dto.AccgInvoiceStatementDepositRecvSelectDto;
import lombok.Data;

/**
 * 預り金選択モーダルフォームクラス
 */
@Data
public class AccgInvoiceStatementDepositRecvSelectViewForm {

	/** 預り金リスト */
	private List<AccgInvoiceStatementDepositRecvSelectDto> depositRecvList;

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 預り金種別 */
	private String depositType;

	/** 預り金登録件数 */
	private Integer numberOfRegistDeposit;
}
