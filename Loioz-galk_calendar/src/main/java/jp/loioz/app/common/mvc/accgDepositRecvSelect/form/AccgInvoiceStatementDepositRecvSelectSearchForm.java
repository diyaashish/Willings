package jp.loioz.app.common.mvc.accgDepositRecvSelect.form;

import java.util.List;

import lombok.Data;

/**
 * 預り金選択モーダル検索フォームクラス
 */
@Data
public class AccgInvoiceStatementDepositRecvSelectSearchForm {

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 預り金種別 */
	private String depositType;

	/** 除外する預り金SEQリスト */
	private List<Long> excludeDepositRecvSeqList;
}
