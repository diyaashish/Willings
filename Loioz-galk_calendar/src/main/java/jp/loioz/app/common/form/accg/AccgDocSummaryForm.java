package jp.loioz.app.common.form.accg;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import lombok.Data;

/**
 * 取引状況サマリーフォームオブジェクト
 */
@Data
public class AccgDocSummaryForm {

	/** 取引実績SEQ */
	private Long accgRecordSeq;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 案件ステータス */
	private AnkenStatus ankenStatus;

	/** ドキュメント種別 */
	private AccgDocType accgDocType;

	/** 請求書サマリ情報 */
	private InvoiceSummary invoiceSummary;

	/** 精算書サマリ情報 */
	private StatementSummary statementSummary;

	/** 取引実績画面かどうか (取引実績の登録ボタンの表示制御) */
	private boolean isRecordDetailView;

	/**
	 * 案件が完了しているかどうか
	 * 
	 * @return
	 */
	public boolean isAnkenComp() {
		return AnkenStatus.isComp(this.ankenStatus.getCd());
	}

	/**
	 * 取引状況サマリ：請求書サマリー情報
	 */
	@Data
	public static class InvoiceSummary {

		/** 請求書SEQ */
		private Long invoiceSeq;

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 請求書ステータス */
		private InvoicePaymentStatus invoicePaymentStatus;

		/** 請求金額 */
		private String invoiceAmount;

		/** 請求方法 */
		private String invoiceTypeName;

		/** 請求番号 */
		private String invoiceNo;

		/** 請求日付 */
		private LocalDate invoiceDate;

		/** 支払期限 */
		private LocalDate dueDate;

		/** 既入金 */
		private String repayAmount;

		/** 報酬 */
		private String feeAmount;

		/** 実費/預り金合計 */
		private String totalDepositAllAmount;

		/** 実費 */
		private String advanceMoneyAmount;

		/** 預り金 */
		private String depositAmount;

	}

	/**
	 * 取引状況サマリ：精算書サマリ情報
	 */
	@Data
	public static class StatementSummary {

		/** 精算書SEQ */
		private Long statementSeq;

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 精算書ステータス */
		private StatementRefundStatus statementRefundStatus;

		/** 精算金額 */
		private String statementAmount;

		/** 精算番号 */
		private String statementNo;

		/** 精算日付 */
		private LocalDate statementDate;

		/** 返金期限 */
		private LocalDate refundDate;

		/** 既入金 */
		private String repayAmount;

		/** 報酬 */
		private String feeAmount;

		/** 実費 */
		private String advanceMoneyAmount;

		/** 預り金 */
		private String depositAmount;

		/** 0円精算かどうか */
		private boolean isStatementAmountZero;

	}

}
