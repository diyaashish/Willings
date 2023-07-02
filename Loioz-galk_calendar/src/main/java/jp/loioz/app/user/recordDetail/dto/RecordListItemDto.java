package jp.loioz.app.user.recordDetail.dto;

import jp.loioz.common.constant.CommonConstant.RecordType;
import lombok.Data;

/**
 * 取引実績一覧項目Dto
 */
@Data
public class RecordListItemDto {

	/** 取引実績詳細SEQ */
	private Long accgRecordDetailSeq;

	/** 取引実績種別 */
	private RecordType recordType;

	/** 決済日 */
	private String recordDate;

	/** 実績入金額 */
	private String recordAmount;

	/** 報酬入金額 */
	private String recordFeeAmount;

	/** 預り金入金額 */
	private String recordDepositRecvAmount;

	/** 個別入力 */
	private boolean isRecordSeparateInputFlg;

	/** 取引実績明細過入金SEQ */
	private Long accgRecordDetailOverPaymentSeq;

	/** 過入金額 */
	private String overPaymentAmount;

	/** 過入金が発生しているかどうか */
	private boolean isOverPayment;

	/** 過剰入金返金 */
	private boolean isOverPaymentRefund;

	/** 預り金出金額 */
	private String recordDepositPaymentAmount;

	/** 備考 */
	private String remarks;

	/** 入金：報酬項目・預り金を表示 */
	private boolean showFeeAndDepositRecv;

}
