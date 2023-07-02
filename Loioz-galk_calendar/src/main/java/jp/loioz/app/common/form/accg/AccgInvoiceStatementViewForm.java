package jp.loioz.app.common.form.accg;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AccggInvoiceStatementDetailViewTab;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.FractionalMonthType;
import jp.loioz.common.constant.CommonConstant.SeisanShiharaiMonthDay;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.PersonAttribute;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 請求書・精算書詳細表示用共通フォームクラス
 */
@Data
public class AccgInvoiceStatementViewForm {

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 請求書SEQ */
	private Long invoiceSeq;

	/** 精算書SEQ */
	private Long statementSeq;

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 会計書類種別 */
	private AccgDocType accgDocType;

	/** 案件フラグメントフォーム */
	private AnkenForm ankenForm;

	/** 設定フラグメントフォーム */
	private SettingViewForm settingViewForm;

	/** 内部メモフラグメントフォーム */
	private MemoViewForm memoViewForm;

	/** タブフラグメントフォーム */
	private DocContentsForm docContentsForm;

	/** タイトルフラグメントフォーム */
	private BaseTitleViewForm baseTitleViewForm;

	/** 請求先フラグメント */
	private BaseToViewForm baseToViewForm;

	/** 請求元フラグメント */
	private BaseFromViewForm baseFromViewForm;

	/** 挿入文フラグメント */
	private BaseOtherViewForm baseOtherViewForm;

	/** 既入金フラグメント */
	private RepayViewForm repayViewForm;

	/** 既入金（1行）フラグメント */
	private RepayRowViewForm repayRowViewForm;

	/** 請求フラグメント */
	private InvoiceViewForm invoiceViewForm;

	/** 請求（1行）フラグメント */
	private InvoiceRowViewForm invoiceRowViewForm;

	/** 振込先フラグメント */
	private BankDetailViewForm bankDetailViewForm;

	/** 備考フラグメント */
	private RemarksViewForm remarksViewForm;

	/** 取引状況フラグメント */
	private AccgDocSummaryForm docSummaryForm;

	/** 進行状況フラグメント */
	private DocActivityForm docActivityForm;

	/** 請求書タブフラグメント */
	private DocInvoicePdfViewForm docInvoicePdfViewForm;

	/** 精算書タブフラグメント */
	private DocStatementPdfViewForm docStatementPdfViewForm;

	/**
	 * 案件フラグメント用フォーム
	 */
	@Data
	public static class AnkenForm {

		/** 請求書/精算書種別 */
		private AccgDocType accgDocType;

		/** 案件ID */
		private Long ankenId;

		/** 分野名 */
		private String bunyaName;

		/** 案件区分 */
		private AnkenType ankenType;

		/** 案件区分名 */
		private String ankenTypeName;

		/** 案件名 */
		private String ankenName;

		/** 案件ステータス */
		private AnkenStatus ankenStatus;

		/** 名簿ID */
		private Long personId;

		/** 名簿属性 */
		private PersonAttribute personAttribute;

		/** 名簿名 */
		private String personName;

		/** 名簿タイプ */
		private String customerType;

		/** 発行ステータス */
		private String issueStatus;

		/** 設定表示フラグメント用フォーム */
		private SettingViewForm settingViewForm;

		/** 精算書かどうか */
		public boolean isStatement() {
			return this.accgDocType == AccgDocType.STATEMENT;
		}

		/** 請求書かどうか */
		public boolean isInvoice() {
			return this.accgDocType == AccgDocType.INVOICE;
		}

	}

	/**
	 * 会計書類送付入力フォームオブジェクト
	 */
	@Data
	public static class FileSendPreviewForm {

		/** 送信元メールアドレス */
		private String sendFrom;

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 送信先 */
		private String sendTo;

		/** 送信先CC */
		private String sendCc;

		/** 送信先CC */
		private String sendBcc;

		/** 返信先メールアドレス */
		private String replyTo;

		/** 送信元名 */
		private String sendFromName;

		/** 件名 */
		private String sendSubject;

		/** 内容 */
		private String sendBody;

		/** パスワード */
		private String password;

		/** 送信種別 */
		private String sendType;

	}

	/**
	 * 会計書類：印刷して送付画面表示用オブジェクト
	 */
	@Data
	public static class FilePrintSendViewForm {

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 会計書類種別タイプ */
		private AccgDocType accgDocType;

	}

	/**
	 * 設定表示フラグメント用フォーム
	 */
	@Data
	public static class SettingViewForm {

		/** 会計書類種別 */
		private AccgDocType accgDocType;

		/** 請求方法（請求時のみ） */
		private String invoiceTypeName;

		/** 売上日 */
		private LocalDate salesDate;

		/** 売上計上先名 */
		private String salesAccountName;

		/** 実費明細添付フラグ */
		private String depositDetailAttachFlg;

		/** 支払計画添付フラグ (請求書のみ) */
		private String paymentPlanAttachFlg;

		/**
		 * /区切りに編集した売上日を取得します。
		 * 
		 * @return
		 */
		public String getSalesDateFormat() {
			return DateUtils.parseToString(this.salesDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		}
	}

	/**
	 * 内部メモ表示フラグメント用フォーム
	 */
	@Data
	public static class MemoViewForm {

		/** メモ */
		private String memo;

	}

	/**
	 * タブフラグメント用フォーム
	 */
	@Data
	public static class DocContentsForm {

		/** 選択中タブ種別 */
		private AccggInvoiceStatementDetailViewTab selectedTab;

		/** 会計書類種別 */
		private AccgDocType accgDocType;

		/** 請求方法（請求時のみ） */
		private String invoiceType;

		/** 実費明細添付フラグ */
		private boolean depositDetailAttachFlg;

		/** 支払計画書添付フラグ（請求時のみ） */
		private boolean paymentPlanAttachFlg;

		/** 支払計画タブ_アラート */
		private boolean hasPaymentPlanWarning;

		/** 編集タブを表示するか */
		private boolean showEditTab;
	}

	/**
	 * タイトル表示フラグメント用フォーム
	 */
	@Data
	public static class BaseTitleViewForm {

		/** 会計書類種別 */
		private AccgDocType accgDocType;

		/** タイトル */
		private String baseTitle;

		/** 日付 */
		private LocalDate baseDate;

		/** 番号 */
		private String baseNumber;

		/**
		 * /区切りに編集した日付を取得します。
		 * 
		 * @return
		 */
		public String getBaseDateFormat() {
			return DateUtils.parseToString(this.baseDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		}
	}

	/**
	 * 請求先表示フラグメント用フォーム
	 */
	@Data
	public static class BaseToViewForm {

		/** 名称 */
		private String baseToName;

		/** 詳細 */
		private String baseToDetail;
	}

	/**
	 * 請求元表示フラグメント用フォーム
	 */
	@Data
	public static class BaseFromViewForm {

		/** 事務所名 */
		private String baseFromOfficeName;

		/** 詳細 */
		private String baseFromDetail;

		/** 印影フラグ */
		private String tenantStampPrintFlg;

		/**
		 * 支払期限を印字可能か返す
		 * 
		 * @return
		 */
		public boolean isTenantStampPrintView() {
			return SystemFlg.FLG_ON.equalsByCode(this.tenantStampPrintFlg);
		}
	}

	/**
	 * 挿入文表示フラグメント用フォーム
	 */
	@Data
	public static class BaseOtherViewForm {

		/** 会計書類種別 */
		private AccgDocType accgDocType;

		/** 挿入文 */
		private String subText;

		/** 期限日 */
		private LocalDate deadline;

		/** 件名 */
		private String title;

		/** 支払期限印字フラグ */
		private String dueDatePrintFlg;

		/** 返金期日印字フラグ */
		private String refundDatePrintFlg;

		/**
		 * /区切りに編集した期限日を取得します。
		 * 
		 * @return
		 */
		public String getDeadlineFormat() {
			return DateUtils.parseToString(this.deadline, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		}

		/**
		 * 支払期限を印字可能か返す
		 * 
		 * @return
		 */
		public boolean isDueDatePrintView() {
			return SystemFlg.codeToBoolean(this.dueDatePrintFlg);
		}

		/**
		 * 返金期日が印字可能か
		 * 
		 * @return
		 */
		public boolean isRefundDatePrintView() {
			return SystemFlg.codeToBoolean(this.refundDatePrintFlg);
		}

	}

	/**
	 * 既入金表示フラグメント用フォーム
	 */
	@Data
	public static class RepayViewForm {

		/** 既入金情報 */
		private List<RepayRowViewForm> repayRowList;
		
		/** 既入金合計 */
		private String totalRepayAmount;
	}

	/**
	 * 既入金表示（1行）フラグメント用フォーム
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RepayRowViewForm {

		/** 日付 */
		private LocalDate repayTransactionDate;

		/** 項目 */
		private String repayItemName;

		/** 摘要 */
		private String sumText;

		/** 金額 */
		private String repayAmount;

		/** 預り金SEQ（まとめて表示している場合複数） */
		private List<Long> depositRecvSeqList;

		/**
		 * 〇月〇日に編集した日付を取得します。
		 * 
		 * @return
		 */
		public String getRepayTransactionDateFormat() {
			return DateUtils.parseToString(this.repayTransactionDate, DateUtils.DATE_JP_M_D);
		}
	}

	/**
	 * 請求表示フラグメント用フォーム
	 */
	@Data
	public static class InvoiceViewForm {

		/** 請求情報 */
		private List<InvoiceRowViewForm> invoiceRowList;

		/** 小計 */
		private String subTotal;

		/** 消費税 */
		private String tax;

		/** 源泉徴収税 */
		private String withholding;

		/** 合計 */
		private String total;

		/** 10%対象請求額 */
		private String target10;

		/** 10%対象消費税 */
		private String target10Tax;

		/** 8%対象請求額 */
		private String target8;

		/** 8%対象消費税 */
		private String target8Tax;

		/**
		 * 消費税10%対象の請求額があるかどうか
		 * 
		 * @return
		 */
		public boolean isEmptyTarget10() {
			BigDecimal target10Decimal = LoiozNumberUtils.parseAsBigDecimal(this.target10);
			if (target10Decimal == null || LoiozNumberUtils.isLessThan(target10Decimal, BigDecimal.ONE)) {
				return true;
			}
			return false;
		}

		/**
		 * 消費税8%対象の請求額があるかどうか
		 * 
		 * @return
		 */
		public boolean isEmptyTarget8() {
			BigDecimal target8Decimal = LoiozNumberUtils.parseAsBigDecimal(this.target8);
			if (target8Decimal == null || LoiozNumberUtils.isLessThan(target8Decimal, BigDecimal.ONE)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 請求表示（1行）フラグメント用フォーム
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class InvoiceRowViewForm {

		/** 日付 */
		private LocalDate transactionDate;

		/** 内容 */
		private String itemName;

		/** 金額 */
		private String amount;

		/** 摘要 */
		private String sumText;

		/** 預り金SEQ（まとめて表示している場合複数） */
		private List<Long> depositRecvSeqList;

		/**
		 * 〇月〇日に編集した日付を取得します。
		 * 
		 * @return
		 */
		public String getTransactionDateFormat() {
			return DateUtils.parseToString(this.transactionDate, DateUtils.DATE_JP_M_D);
		}
	}

	/**
	 * 振込先表示フラグメント用フォーム
	 */
	@Data
	public static class BankDetailViewForm {

		/** 案件ID */
		private Long ankenId;

		/** 振込先口座 */
		private String tenantBankDetail;
	}

	/**
	 * 備考表示フラグメント用フォーム
	 */
	@Data
	public static class RemarksViewForm {

		/** 備考 */
		private String remarks;
	}

	/**
	 * 進行状況フラグメント用フォーム
	 */
	@Data
	public static class DocActivityForm {

		/** 発行ステータス */
		private String issueStatus;

		/** 新規作成の進行状況データリスト */
		private List<DocActivityRowForm> newDocActivityList;

		/** 新規作成が実行されているか */
		private boolean created;

		/** 発行の進行状況データリスト */
		private List<DocActivityRowForm> issueDocActivityList;

		/** 新規作成が実行されているか */
		private boolean issued;

		/** 送付の進行状況データリスト */
		private List<DocActivityRowForm> sendDocActivityList;

		/** 送付が実行されているか */
		private boolean send;
	}

	/**
	 * 進行状況（1件）フラグメント用フォーム
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DocActivityRowForm {

		/** 対応名 */
		private String activityTypeName;

		/** 対応者名 */
		private String activityByName;

		/** 対応日時 */
		private LocalDateTime activityAt;

		/** 会計書類対応送付SEQ */
		private Long accgDocActSendSeq;

		/** 会計書類送付種別 */
		private AccgDocSendType accgDocSendType;

		/** ダウンロード状況(Activityが送付の場合のみ) */
		private List<DocActivityDownloadStatus> downloadStatus;

		/**
		 * /区切りに編集した日付を取得します。
		 * 
		 * @return
		 */
		public String getBaseDateFormat() {
			return DateUtils.parseToString(this.activityAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
		}
	}

	/**
	 * 進捗状況：ダウンロード状況の項目
	 * 
	 */
	@Data
	@Builder
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class DocActivityDownloadStatus {

		/** 会計書類ファイルSEQ */
		private Long accgDocFileSeq;

		/** ファイルタイプ名 */
		private String fileTypeName;

		/** ダウンロード日時 */
		private String downloadedDate;

		/** ダウンロード済かどうか */
		private boolean isDownloaded;

	}

	/**
	 * 請求書タブ内フラグメント用フォーム
	 */
	@Data
	public static class DocInvoicePdfViewForm {

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 請求書SEQ */
		private Long invoiceSeq;

		/** 請求書PNG HTMLソース */
		private List<String> invoicePngSrc = Collections.emptyList();

		/** 再作成ボタンの表示有無 */
		private boolean canReCreate;

	}

	/**
	 * 精算書タブ内フラグメント用フォーム
	 */
	@Data
	public static class DocStatementPdfViewForm {

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 精算書SEQ */
		private Long statementSeq;

		/** 精算書PNG HTMLソース */
		private List<String> statementPngSrc = Collections.emptyList();

		/** 再作成ボタンの表示有無 */
		private boolean canReCreate;

	}

	/**
	 * 実費明細フラグメント用フォーム
	 */
	@Data
	public static class DipositRecordPdfViewForm {

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 会計書類種別 */
		private AccgDocType accgDocType;

		/** 取引実績明細PNG HTMLソース */
		private List<String> depositRecordPngSrc = Collections.emptyList();

	}

	/**
	 * 支払条件表示フラグメント用フォーム
	 */
	@Data
	public static class PaymentPlanConditionViewForm {

		/** 請求書SEQ */
		private Long invoiceSeq;

		/** 支払金額（月額） */
		private String monthPaymentAmount;

		/** 支払い開始年月 */
		private LocalDate paymentStartDate;

		/** 支払日 */
		private String monthPaymentDate;

		/** 端数種別 */
		private FractionalMonthType fractionalMonthType;

		/** 支払回数 */
		private Integer numberOfPayment;

		/** 発行済みかどうか */
		private boolean isIssued;

		/** 支払計画の直接編集可否 */
		private boolean canPlanEdit;

		/** 下書きに戻す処理時にPDFの作成が失敗した */
		private boolean isLostPlanPdf;

		/** 支払計画が再作成実行待ちかどうか */
		private boolean needReCreatePlan;

		/** 支払計画書PNG HTMLソース */
		private List<String> planPngSrc = Collections.emptyList();

		/**
		 * 年月に編集した支払開始年月を取得します。
		 * 
		 * @return
		 */
		public String getPaymentStartDateFormat() {
			return DateUtils.parseToString(this.paymentStartDate, DateUtils.DATE_JP_YYYY_M);
		}

		/**
		 * 支払日を〇日に編集します。31日の場合は「月末」に編集します。
		 * 
		 * @return
		 */
		public String getMonthPaymentDateFormat() {
			if (StringUtils.isEmpty(this.monthPaymentDate)) {
				return "";
			} else if (DateUtils.END_OF_MONTH.equals(this.monthPaymentDate)) {
				return SeisanShiharaiMonthDay.LASTDAY.getVal();
			} else {
				return this.monthPaymentDate + "日";
			}
		}

	}

}
