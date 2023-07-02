package jp.loioz.app.common.form.accg;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.FractionalMonthType;
import jp.loioz.common.constant.CommonConstant.InvoiceDepositType;
import jp.loioz.common.constant.CommonConstant.InvoiceOtherItemType;
import jp.loioz.common.constant.CommonConstant.SeisanShiharaiMonthDay;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.ValidateUtils;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.NumericAlphabet;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 請求書・精算書詳細入力用共通フォームクラス
 */
@Data
public class AccgInvoiceStatementInputForm {

	/**
	 * 会計書類送付入力フォームオブジェクト
	 */
	@Data
	public static class FileSendInputForm {

		// 表示用プロパティ
		/** テンプレート選択情報 */
		private List<SelectOptionForm> templateOptions = Collections.emptyList();

		/** 送信元メールアドレス */
		private String sendFrom;

		/** 会計書類種別 */
		private AccgDocType accgDocType;

		/** 表示用メール送信本文 */
		private String dispMailSendBody;

		// 入力用プロパティ

		/** 会計書類SEQ */
		@Required
		private Long accgDocSeq;

		/** テンプレートSEQ */
		private Long templateSeq;

		/** 送信先 */
		@Required
		@EmailPattern
		@MaxDigit(max = 256)
		private String sendTo;

		/** 送信先CC */
		@MaxDigit(max = 2000)
		private String sendCc;

		/** 送信先CC */
		@MaxDigit(max = 2000)
		private String sendBcc;

		/** 返信先メールアドレス */
		@Required
		@EmailPattern
		private String replyTo;

		/** 送信元名 */
		@Required
		@MaxDigit(max = 100)
		private String sendFromName;

		/** 件名 */
		@Required
		@MaxDigit(max = 100)
		private String sendSubject;

		/** 内容 */
		@Required
		@MaxDigit(max = 10000)
		private String sendBody;

		/** パスワード有効化設定 */
		private boolean isDownloadViewPasswordEnabled;

		/** パスワード */
		@NumericAlphabet
		@DigitRange(min = 8, max = 64)
		private String password;

		/** 署名 */
		@MaxDigit(max = 10000)
		private String mailSignature;

		/** 送信種別 */
		@Required
		@EnumType(value = AccgDocSendType.class)
		private String sendType;

		/** 精算書の場合 */
		public boolean isStatement() {
			return this.accgDocType == AccgDocType.STATEMENT;
		}

		/** 請求書の場合 */
		public boolean isInvoice() {
			return this.accgDocType == AccgDocType.INVOICE;
		}

		/**
		 * 相関バリデーション
		 * 
		 * @param result
		 */
		public void rejectValues(BindingResult result) {
			if (!StringUtils.isEmpty(this.getSendCc())) {
				// CCのアドレスパターンチェック
				List<String> addressList = StringUtils.toArray(this.getSendCc());
				for (String addrss : addressList) {
					if (!ValidateUtils.isEmailPatternValid(addrss)) {
						result.rejectValue("sendCc", "", null, "「" + addrss + "」" + "の入力形式に誤りがあります。");
					}
				}
			}

			if (!StringUtils.isEmpty(this.getSendBcc())) {
				// BCCのアドレスパターンチェック
				List<String> addressList = StringUtils.toArray(this.getSendBcc());
				for (String addrss : addressList) {
					if (!ValidateUtils.isEmailPatternValid(addrss)) {
						result.rejectValue("sendBcc", "", null, "「" + addrss + "」" + "の入力形式に誤りがあります。");
					}
				}
			}

			if (AccgDocSendType.WEB.equalsByCode(this.getSendType())) {
				// WEB共有の場合

				if (this.isDownloadViewPasswordEnabled() && StringUtils.isEmpty(this.getPassword())) {
					result.rejectValue("password", MessageEnum.VARIDATE_MSG_E00001.getMessageKey(), null, "");
				}
			}

		}

	}

	/**
	 * 設定入力フラグメント用フォーム
	 */
	@Data
	public static class SettingInputForm {

		// 表示用プロパティ

		/** 会計書類種別 */
		private AccgDocType accgDocType;

		/** 売上計上先候補リスト */
		private List<SelectOptionForm> salesAccountList;

		/** 発行ステータス */
		private String issueStatus;

		// 入力用プロパティ

		/** 請求方法 */
		private String invoiceType;

		/** 売上計上日 */
		@Required
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String salesDate;

		/** 売上計上先 */
		@Required
		private Long salesAccount;

		/** 実費明細添付フラグ */
		private boolean depositDetailAttachFlg;

		/** 支払計画添付フラグ */
		private boolean paymentPlanAttachFlg;

	}

	/**
	 * 内部メモ入力フラグメント用フォーム
	 */
	@Data
	public static class MemoInputForm {

		// 表示用プロパティ
		/** 内部メモ */
		@MaxDigit(max = AccgConstant.MX_LEN_MEMO)
		private String memo;

	}

	/**
	 * タイトル入力フラグメント用フォーム
	 */
	@Data
	public static class BaseTitleInputForm {

		// 表示用プロパティ
		/** 会計書類種別 */
		private AccgDocType accgDocType;

		// 入力用プロパティ

		/** 請求書タイトル */
		@Required
		@MaxDigit(max = AccgConstant.MX_LEN_TITLE)
		private String baseTitle;

		/** 日付 */
		@Required
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String baseDate;

		/** 請求番号 / 精算番号 */
		@Required
		@MaxDigit(max = AccgConstant.MX_LEN_ACCG_NO)
		private String baseNo;
	}

	/**
	 * 請求先入力フラグメント用フォーム
	 */
	@Data
	public static class BaseToInputForm {

		// 表示用項目
		/** 請求先変更リスト */
		private List<CustomerKanyoshaPulldownDto> customerKanyoshaList;

		// 入力用項目
		/** 案件ID */
		private Long ankenId;

		/** 案件名 */
		private String ankenName;

		/** 請求先名簿ID */
		private Long billToPersonId;

		/** 請求先名 */
		private String billToPersonName;

		/** 請求先変更ID */
		private Long changeBillToPersonId;

		/** 名称 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_TO_NAME)
		private String baseToName;

		/** 敬称 */
		private String baseToNameEnd;

		/** 詳細 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_TO_DETAIL)
		private String baseToDetail;
	}

	/**
	 * 請求元入力フラグメント用フォーム
	 */
	@Data
	public static class BaseFromInputForm {
		/** 請求元事務所名 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_FROM_OFFICE_NAME)
		private String baseFromOfficeName;

		/** 請求元詳細 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_FROM_DETAIL)
		private String baseFromDetail;

		/** 印影画像表示フラグ */
		private boolean tenantStampPrintFlg;
	}

	/**
	 * 挿入文入力フラグメント用フォーム
	 */
	@Data
	public static class BaseOtherInputForm {

		// 表示用プロパティ
		/** 会計書類種別 */
		private AccgDocType accgDocType;

		// 入力用プロパティ

		/** 挿入文 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_SUB_TEXT)
		private String subText;

		/** 期限日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String deadline;

		/** 件名 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_SUBJECT)
		private String title;

		/** 期限日印字フラグ */
		private boolean deadLinePrintFlg;
	}

	/**
	 * 既入金入力フラグメント用フォーム
	 */
	@Data
	public static class RepayInputForm {
		/** 既入金情報 */
		@Valid
		private List<RepayRowInputForm> repayRowList;

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 案件ID */
		private Long ankenId;

		/** 名簿ID */
		private Long personId;

		/** 取引日-印字フラグ（既入金） */
		private boolean repayTransactionDatePrintFlg;

		/** 既入金項目合算フラグ */
		private boolean repaySumFlg;

		/** 合算時の既入金項目SEQ */
		private Long docRepaySeqWhenSummed;
	}

	/**
	 * 既入金入力（1行）フラグメント用フォーム
	 */
	@Data
	public static class RepayRowInputForm {
		/** 既入金項目SEQ */
		private Long docRepaySeq;

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 預り金SEQ（まとめて表示している場合複数） */
		private List<Long> depositRecvSeqList;

		/** 日付 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String repayTransactionDate;

		/** 項目 */
		private String repayItemName;

		/** 摘要 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_SUM_TEXT)
		private String sumText;

		/** 金額 */
		private String repayAmount;

		/** 並び順 */
		private Long docRepayOrder;

		/** 会計書類から作成された入金かどうか */
		private boolean depositMadeFromAccgDocFlg;

		/** 追加フラグ（画面で新規追加された（登録対象となる）行の場合はこの値がtrueになる） */
		private boolean addFlg;

		/** 削除フラグ（画面で削除された（削除対象となる）行の場合はこの値がtrueになる） */
		private boolean deleteFlg;

		/** 既入金合算項目フラグ（まとめるチェックにより、1つにまとめられている行の場合はこの値がtrueになる） */
		private boolean rowRepaySumFlg;

		/** 表示フラグ */
		private boolean displayFlg;

		/** 行数 */
		private Long repayRowCount;
	}

	/**
	 * 請求入力フラグメント用フォーム
	 */
	@Data
	public static class InvoiceInputForm {
		/** 請求情報 */
		@Valid
		private List<InvoiceRowInputForm> invoiceRowList;

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 案件ID */
		private Long ankenId;

		/** 名簿ID */
		private Long personId;

		/** 更新フラグ（請求入力フラグメントのデータに更新があるか） */
		private boolean updateFlg;

		/** 取引日-印字フラグ（請求） */
		private boolean invoiceTransactionDatePrintFlg;

		/** 請求項目合算フラグ（請求） */
		private boolean expenseSumFlg;

		/** 合算時の請求項目SEQ */
		private Long docInvoiceSeqWhenSummed;

		/** 合算時の請求預り金項目SEQ */
		private Long docInvoiceDepositSeqWhenSummed;

		/** 合計額 */
		private InvoiceTotalAmountInputForm invoiceTotalAmountInputForm;
	}

	/**
	 * 請求入力の合計額フラグメント用フォーム
	 */
	@Data
	public static class InvoiceTotalAmountInputForm {
		/** 小計 */
		private String subTotal;

		/** 消費税 */
		private String tax;

		/** 源泉徴収税 */
		private String withholding;

		/** 合計 */
		private String total;

		/** 10%対象 */
		private String target10;

		/** 10%対象消費税 */
		private String target10Tax;

		/** 8%対象 */
		private String target8;

		/** 8%対象消費税 */
		private String target8Tax;

		/**
		 * 10%対象額があるかどうか
		 * 
		 * @return
		 */
		public boolean isThereTarget10() {
			BigDecimal target10Decimal = LoiozNumberUtils.parseAsBigDecimal(this.target10);
			if (target10Decimal == null || LoiozNumberUtils.equalsZero(target10Decimal)) {
				return false;
			}
			return true;
		}

		/**
		 * 8%対象額があるかどうか
		 * 
		 * @return
		 */
		public boolean isThereTarget8() {
			BigDecimal target8Decimal = LoiozNumberUtils.parseAsBigDecimal(this.target8);
			if (target8Decimal == null || LoiozNumberUtils.equalsZero(target8Decimal)) {
				return false;
			}
			return true;
		}
	}

	/**
	 * 請求入力（1行）フラグメント用フォーム
	 */
	@Data
	public static class InvoiceRowInputForm {
		/** 請求項目SEQ */
		private Long docInvoiceSeq;

		/** 請求預り金項目SEQ */
		private Long docInvoiceDepositSeq;

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 報酬SEQ */
		private Long feeSeq;

		/** 預り金SEQ */
		private List<Long> depositRecvSeqList;

		/** 並び順 */
		private Long docInvoiceOrder;

		/** 日付 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String transactionDate;

		/** 項目 */
		private String itemName;

		/** 摘要 */
		@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_SUM_TEXT)
		private String sumText;

		/** 金額 */
		private String amount;

		/** 消費税率 */
		private String taxRateType;

		/** 請求項目の種類 */
		private String invoieType;

		/** 報酬の行かどうか */
		private boolean feeFlg;

		/** 未精算報酬の行かどうか（未精算報酬追加操作） */
		private boolean unPaidFeeFlg;

		/** 預り金の行かどうか */
		private boolean depositRecvFlg;

		/** 値引きの行かどうか */
		private boolean discountFlg;

		/** テキストの行かどうか */
		private boolean textFlg;

		/** 源泉徴収フラグ */
		private boolean withholdingFlg;

		/** タイムチャージフラグ */
		private boolean feeTimeChargeFlg;

		/** タイムチャージ単価 */
		private String hourPrice;

		/** タイムチャージ時間 */
		private Long workTimeMinute;

		/** 削除フラグ（画面の削除操作） */
		private boolean deleteFlg;

		/** 追加フラグ（画面の追加操作） */
		private boolean addFlg;

		/** 請求項目合算フラグ */
		private boolean rowExpenseSumFlg;

		/** 表示フラグ */
		private boolean displayFlg;

		/** 行数 */
		private Long invoiceRowCount;

		/**
		 * <pre>
		 * 空データか判定します。
		 * 請求書、精算書詳細画面の請求項目で新規追加分を✕で削除した場合、
		 * フロントのDOMを削除しているため、残っているDOMのname属性について、Listのインデックス番号が歯抜けになることがあり
		 * 歯抜け分のinputFormがFormをnewしただけの全フィールドが初期値（nullやfalse）の状態でListに追加される。
		 * その歯抜け分データかどうかを判定するため、歯抜けデータでなければ必ず設定されている会計情報SEQがnullかどうかチェックする。
		 * </pre>
		 * 
		 * @return
		 */
		public boolean isEmpty() {
			boolean isEmpty = false;
			// 会計情報SEQが無い場合は空データ
			if (this.accgDocSeq == null) {
				isEmpty = true;
			}
			return isEmpty;
		}

		/**
		 * 未入力か判定します
		 * 
		 * @return
		 */
		public boolean isNotEntered() {
			boolean isNotEntered = false;
			// 報酬SEQか預り金SEQか請求項目SEQの全てが空（新規追加分）の場合に
			// 画面で入力する日付、項目、摘要、金額、タイムチャージ単価、時間が全て空であれば未入力状態とする
			if (this.feeSeq == null && LoiozCollectionUtils.isEmpty(this.depositRecvSeqList) && this.docInvoiceSeq == null
					&& StringUtils.isEmpty(this.transactionDate) && StringUtils.isEmpty(this.itemName)
					&& StringUtils.isEmpty(this.sumText) && StringUtils.isEmpty(this.amount)
					&& StringUtils.isEmpty(this.hourPrice) && this.workTimeMinute == null) {
				isNotEntered = true;
			}
			return isNotEntered;
		}

		/**
		 * 請求項目の実費／預り金の行について、「種別」の値の編集が行えるかを判定する。<br>
		 * ※注意：実費／預り金の行に対しての判定でしか使えないので注意。（報酬や値引き、テキストの行の判定では使えない。）
		 * 
		 * @return true:「種別」の編集が行える、false:「種別」の編集が行えない
		 */
		public boolean isCanEditInvoieTypeForInvoiceDeposit() {
			
			// まとめ行かどうか
			boolean isSumRow = this.rowExpenseSumFlg;
			
			// 新規行かどうか（「新規行」は請求項目で新規に追加する行。預り金のモーダルから追加するものはここでいう「新規行」とは判断しない。）
			// ※追加フラグがONで、預り金のSEQがないものが「新規行」。（モーダルから追加するものは追加フラグがONで、預り金SEQが存在している。）
			boolean isNewRow = (this.addFlg && LoiozCollectionUtils.isEmpty(this.depositRecvSeqList));
			
			if (isSumRow || !isNewRow) {
				// 編集不可とする
				return false;
			} else {
				// 編集可とする（このケースになるのは、預り金請求を新規追加したときのみ。（一度保存すると編集不可になる。））
				return true;
			}
		}
		
		/**
		 * 請求項目の報酬／その他（値引き、テキスト）について、「種別」の値の編集が行えるかを判定する。<br>
		 * ※注意：報酬／その他（値引き、テキスト）の行に対しての判定でしか使えないので注意。（実費／預り金の行の判定では使えない。）
		 * 
		 * @return true:「種別」の編集が行える、false:「種別」の編集が行えない
		 */
		public boolean isCanEditInvoieTypeForInvoiceFeeOrOther() {
			
			if (this.unPaidFeeFlg) {
				// 未請求の報酬のモーダルから追加されたもの
				// -> 編集不可
				return false;
			}
			
			// 以下、モーダルから追加されたものではない、報酬かその他（値引き、テキスト）
			
			if (this.addFlg) {
				// モーダルから追加されたもの以外で、追加フラグがONの場合は新規追加の行なので編集可能
				return true;
			} else {
				// 新規追加の行ではない場合は編集不可
				return false;
			}
		}
		
		/**
		 * 請求項目種別が、報酬レコードであり、「報酬」を選択しているかどうか
		 * 
		 * @return
		 */
		public boolean isInvoiceFee() {
			if (this.feeFlg && this.getInvoiceOtherItemType() == null) {
				return true;
			}
			return false;
		}

		/**
		 * 請求項目種別はデータケースによって、対応する扱うEnumが変わるの
		 * その他種別入力パターンの場合のEnum値を返却するメソッドを作成する
		 * 
		 * @return
		 */
		public InvoiceOtherItemType getInvoiceOtherItemType() {
			if (this.depositRecvFlg) {
				// 預り金の場合、その他項目Enum値はNull
				return null;
			}

			return InvoiceOtherItemType.of(this.invoieType);
		}

		/**
		 * 請求項目種別はデータケースによって、対応する扱うEnumが変わる
		 * 預り金入力パターンの場合のEnum値を返却するメソッドを作成する
		 * 
		 * @return
		 */
		public InvoiceDepositType getInvoiceDepositType() {
			if (!this.depositRecvFlg) {
				// 報酬の場合、預り金種別Enum値はNull

				return null;
			}

			return InvoiceDepositType.of(this.invoieType);
		}

	}

	/**
	 * 振込先入力フラグメント用フォーム
	 */
	@Data
	public static class BankDetailInputForm {
		/** 振込先口座 */
		@MaxDigit(max = AccgConstant.MX_LEN_TENANT_BANK_DETAIL)
		private String tenantBankDetail;
	}

	/**
	 * 備考入力フラグメント用フォーム
	 */
	@Data
	public static class RemarksInputForm {
		/** 備考 */
		@MaxDigit(max = AccgConstant.MX_LEN_REMARKS)
		private String remarks;
	}

	/**
	 * 支払条件入力フラグメント用フォーム
	 */
	@Data
	public static class PaymentPlanConditionInputForm {

		// 表示用プロパティ
		/** 年：プルダウン */
		private List<SelectOptionForm> yearSelectOptionFormList = Collections.emptyList();

		// 月・日のプルダウンは変動しないためThymeleafで作成

		// 入力用プロパティ
		/**
		 * 新規登録かどうか<br>
		 * 
		 * 通常は更新対象のSEQ有無によって判定するが、支払条件テーブルはPKが外部キー(請求書SEQ)であるため 初期表示時に新規登録かどうかを判定する。
		 * 保存処理時もこの値を受け渡すことにより、 バリデーションなどで再レンダリング時の登録処理か更新処理が切り替わらないことを担保する
		 */
		private boolean isNew;

		/** 請求書SEQ */
		private Long invoiceSeq;

		/** 月々の支払い額 */
		@Required
		@Numeric
		@MaxDigit(max = 9)
		@MinNumericValue(min = 1L)
		private String monthPaymentAmount;

		/** 支払開始年月：年 */
		@Required
		@Numeric
		@DigitRange(min = 4, max = 4) // 4桁の数値を許容
		private String year;

		/** 支払開始年月：月 */
		@Required
		@Numeric
		@MinNumericValue(min = 1L)
		@MaxNumericValue(max = 12L)
		private String month;

		/** 月末 or 日払い */
		@Required
		@EnumType(value = SeisanShiharaiMonthDay.class)
		private String seisanShiharaiMonthDay;

		/** 月々の支払日（日） */
		@Numeric
		@MinNumericValue(min = 1L)
		@MaxNumericValue(max = 29L)
		private String monthPaymentDd;

		/** 端数金額の支払月種別 */
		@Required
		@EnumType(value = FractionalMonthType.class)
		private String fractionalMonthType;

		/** 月々の支払い金額 */
		public BigDecimal getMonthPaymentAmountDecimal() {
			return LoiozNumberUtils.parseAsBigDecimal(this.monthPaymentAmount);
		}

		/** 支払開始年月：年 */
		public Integer getYearInteger() {
			return LoiozNumberUtils.parseAsInteger(this.year);
		}

		/** 支払開始年月：年 */
		public Integer getMonthInteger() {
			return LoiozNumberUtils.parseAsInteger(this.month);
		}

		/** 月々の支払日（日） */
		public Integer getMonthPaymentDdInteger() {
			return LoiozNumberUtils.parseAsInteger(this.monthPaymentDd);
		}

	}

	/**
	 * PDF作成中に一時的に表示するフラグメントの表示用オブジェクト
	 * 基本的にメッセージしか表示しない想定なので、PDF種別のみプロパティを格納
	 */
	@Data
	@AllArgsConstructor
	public static class PdfCreatingViewForm {

		/** 会計書類種別 */
		private AccgDocFileType accgDocFileType;

	}

}
