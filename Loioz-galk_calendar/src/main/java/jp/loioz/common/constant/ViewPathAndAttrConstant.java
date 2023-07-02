package jp.loioz.common.constant;

/**
 * view(template)のパス情報と、viewで参照する属性名（フォームオブジェクトの名前など）を定義する定数クラス
 */
public class ViewPathAndAttrConstant {

	/**************************************
	 * ヘッダー検索
	 **************************************/
	/** ヘッダー検索フラグメントのパス */
	public static final String USER_HEADER_SEARCH_FRAGMENT_PATH = "commonLayoutFragment::headerSearchFragment";
	/** ヘッダー検索フラグメントの画面フォーム */
	public final static String USER_HEADER_SEARCH_FRAGMENT_VIEW_FORM = "headerSearchListViewForm";

	/** 新会計：取引状況フラグメントのパス */
	public static final String ACCG_DOC_SUMMARY_FRAGMENT_PATH = "common/accg/accgDocSummary::accgDocSummaryFragment";
	/** 新会計：取引状況表示で使用するフォームオブジェクト名 */
	public static final String ACCG_DOC_SUMMARY_VIEW_FORM_NAME = "accgDocSummaryForm";

	/**
	 * 請求書/精算書 共通フラグメントパス
	 */
	public static class AccgInvociceStatementPathAndAttrConstant {

		/** 請求書/精算書詳細のフラグメントhtmlパス */
		public static final String ACCG_INVOICE_STATEMENT_FRAGMENT_PATH = "common/accg/accgInvoiceStatementFragment";

		/** 請求書/精算書詳細のフラグメントのパス */
		public static final String ACCG_DETAIL_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::accgInvoiceStatementDetailFragment";
		/** 請求書/精算書詳細のフォームオブジェクト名 */
		public static final String ACCG_DETAIL_FORM_NAME = "detailViewForm";

		/** 請求書/精算書詳細の案件フラグメントのパス */
		public static final String ACCG_DETAIL_ANKEN_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::ankenFragment";
		/** 請求書/精算書詳細の案件表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_ANKEN_FORM_NAME = "ankenForm";

		/** 請求書/精算書詳細ファイル送付用フラグメントパス */
		public static final String ACCG_FILE_SEND_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::accgDocFileSendInputFragment";
		/** 請求書/精算書詳細ファイル送付用フラグメントパス */
		public static final String ACCG_FILE_SEND_PREVIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::accgDocFileSendPreviewFragment";
		/** 請求書/精算書詳細ファイル送付用フラグメントパス */
		public static final String ACCG_FILE_SEND_INPUT_FORM_NAME = "fileSendInputForm";

		/** 請求書/精算書詳細ファイル印刷送付用フラグメントパス */
		public static final String ACCG_FILE_PRINT_SEND_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::accgDocFilePrintSendFragment";
		/** 請求書/精算書詳細ファイル印刷送付用フラグメントパス */
		public static final String ACCG_FILE_PRINT_SEND_VIEW_FORM_NAME = "filePrintSendViewForm";

		/** 請求書/精算書詳細の設定表示フラグメントのパス */
		public static final String ACCG_DETAIL_SETTING_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::settingViewFragment";
		/** 請求書/精算書詳細の設定表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_SETTING_VIEW_FORM_NAME = "settingViewForm";

		/** 請求書/精算書詳細の設定入力フラグメントのパス */
		public static final String ACCG_DETAIL_SETTING_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::settingInputFragment";
		/** 請求書/精算書詳細の設定入力で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_SETTING_INPUT_FORM_NAME = "settingInputForm";

		/** 請求書/精算書詳細の内部メモ表示フラグメントのパス */
		public static final String ACCG_DETAIL_MEMO_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::memoViewFragment";
		/** 請求書/精算書詳細の内部メモ表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_MEMO_VIEW_FORM_NAME = "memoViewForm";

		/** 請求書/精算書詳細の内部メモ入力フラグメントのパス */
		public static final String ACCG_DETAIL_MEMO_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::memoInputFragment";
		/** 請求書/精算書詳細の内部メモ入力で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_MEMO_INPUT_FORM_NAME = "memoInputForm";

		/** 請求書/精算書詳細のタブフラグメントのパス */
		public static final String ACCG_DETAIL_DOC_CONTENTS_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::docContentsFragment";
		/** 請求書/精算書詳細のタブフラグメントで使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_DOC_CONTENTS_FORM_NAME = "docContentsForm";

		/** 請求書/精算書詳細のタイトル表示フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_TITLE_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseTitleViewFragment";
		/** 請求書/精算書詳細の基本タイトル表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_TITLE_VIEW_FORM_NAME = "baseTitleViewForm";

		/** 請求書/精算書詳細のタイトル入力フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_TITLE_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseTitleInputFragment";
		/** 請求書/精算書詳細の基本タイトル入力で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_TITLE_INPUT_FORM_NAME = "baseTitleInputForm";

		/** 請求書/精算書詳細の請求先表示フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_TO_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseToViewFragment";
		/** 請求書/精算書詳細の基本請求先表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_TO_VIEW_FORM_NAME = "baseToViewForm";

		/** 請求書/精算書詳細の請求先入力フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_TO_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseToInputFragment";
		/** 請求書/精算書詳細の基本請求先入力で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_TO_INPUT_FORM_NAME = "baseToInputForm";

		/** 請求書/精算書詳細の請求元表示フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_FROM_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseFromViewFragment";
		/** 請求書/精算書詳細の基本請求元表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_FROM_VIEW_FORM_NAME = "baseFromViewForm";

		/** 請求書/精算書詳細の請求元入力フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_FROM_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseFromInputFragment";
		/** 請求書/精算書詳細の基本請求元入力で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_FROM_INPUT_FORM_NAME = "baseFromInputForm";

		/** 請求書/精算書詳細の挿入文表示フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_OTHER_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseOtherViewFragment";
		/** 請求書/精算書詳細の基本挿入文表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_OTHER_VIEW_FORM_NAME = "baseOtherViewForm";

		/** 請求書/精算書詳細の挿入文入力フラグメントのパス */
		public static final String ACCG_DETAIL_BASE_OTHER_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::baseOtherInputFragment";
		/** 請求書/精算書詳細の基本挿入文入力で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_BASE_OTHER_INPUT_FORM_NAME = "baseOtherInputForm";

		/** 請求書/精算書詳細の振込先表示フラグメントのパス */
		public static final String ACCG_DETAIL_BANK_DETAIL_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::bankDetailViewFragment";
		/** 請求書/精算書詳細のお振込先表示用オブジェクト名 */
		public static final String ACCG_DETAIL_BANK_DETAIL_VIEW_FORM_NAME = "bankDetailViewForm";

		/** 請求書/精算書詳細の振込先入力フラグメントのパス */
		public static final String ACCG_DETAIL_BANK_DETAIL_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::bankDetailInputFragment";
		/** 請求書/精算書詳細のお振込先入力用オブジェクト名 */
		public static final String ACCG_DETAIL_BANK_DETAIL_INPUT_FORM_NAME = "bankDetailInputForm";

		/** 請求書/精算書詳細の備考表示フラグメントのパス */
		public static final String ACCG_DETAIL_REMARKS_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::remarksViewFragment";
		/** 請求書/精算書詳細の備考表示用オブジェクト名 */
		public static final String ACCG_DETAIL_REMARKS_VIEW_FORM_NAME = "remarksViewForm";

		/** 請求書/精算書詳細の備考入力フラグメントのパス */
		public static final String ACCG_DETAIL_REMARKS_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::remarksInputFragment";
		/** 請求書/精算書詳細の備考入力用オブジェクト名 */
		public static final String ACCG_DETAIL_REMARKS_INPUT_FORM_NAME = "remarksInputForm";

		/** 請求書/精算書詳細の請求書フラグメントのパス */
		public static final String ACCG_DETAIL_INVOICE_PDF_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::docInvoicePdfFragment";
		/** 請求書/精算書詳細の請求書表示用オブジェクト名 */
		public static final String ACCG_DETAIL_INVOICE_PDF_VIEW_FORM_NAME = "docInvoicePdfViewForm";

		/** 請求書/精算書詳細の実費明細フラグメントのパス */
		public static final String ACCG_DETAIL_DIPOSIT_RECORD_PDF_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::dipositRecordPdfFragment";
		/** 請求書/精算書詳細の実費明細表示用オブジェクト名 */
		public static final String ACCG_DETAIL_DIPOSIT_RECORD_PDF_VIEW_FORM_NAME = "dipositRecordPdfViewForm";

		/** 請求書/精算書詳細の精算書フラグメントのパス */
		public static final String ACCG_DETAIL_STATEMENT_PDF_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::docStatementPdfFragment";
		/** 請求書/精算書詳細の精算書表示用オブジェクト名 */
		public static final String ACCG_DETAIL_STATEMENT_PDF_VIEW_FORM_NAME = "docStatementPdfViewForm";

		/** 請求書/精算書詳細の進行状況フラグメントのパス */
		public static final String ACCG_DETAIL_DOC_ACTIVITY_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::docActivityFragment";
		/** 請求書/精算書詳細の進行状況表示で使用するフォームオブジェクト名 */
		public static final String ACCG_DETAIL_DOC_ACTIVITY_VIEW_FORM_NAME = "docActivityForm";

		/** 請求書詳細の既入金表示フラグメントのパス */
		public static final String INVOICE_DETAIL_REPAY_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::repayViewFragment";
		/** 請求書詳細の既入金表示で使用するフォームオブジェクト名 */
		public static final String INVOICE_DETAIL_REPAY_VIEW_FORM_NAME = "repayViewForm";

		/** 請求書詳細の既入金入力フラグメントのパス */
		public static final String INVOICE_DETAIL_REPAY_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::repayInputFragment";
		/** 請求書詳細の既入金入力で使用するフォームオブジェクト名 */
		public static final String INVOICE_DETAIL_REPAY_INPUT_FORM_NAME = "repayInputForm";

		/** 請求書詳細の既入金入力リストフラグメントのパス */
		public static final String INVOICE_DETAIL_REPAY_INPUT_LIST_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::repayInputListFragment";
		/** 請求書詳細の既入金入力リストで使用するフォームオブジェクト名 */
		public static final String INVOICE_DETAIL_REPAY_LIST_INPUT_FORM_NAME = "repayInputList";

		/** 請求書詳細の請求表示フラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_VIEW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceViewFragment";
		/** 請求書詳細の請求表示で使用するフォームオブジェクト名 */
		public static final String INVOICE_DETAIL_INVOICE_VIEW_FORM_NAME = "invoiceViewForm";

		/** 請求書詳細の請求入力フラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceInputFragment";
		/** 請求書詳細の請求入力で使用するフォームオブジェクト名 */
		public static final String INVOICE_DETAIL_INVOICE_INPUT_FORM_NAME = "invoiceInputForm";

		/** 請求書詳細の請求書入力リストフラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_INPUT_LIST_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceInputListFragment";
		/** 請求書詳細の請求入力リストで使用するフォームオブジェクト名 */
		public static final String INVOICE_DETAIL_INVOICE_LIST_INPUT_FORM_NAME = "invoiceInputList";

		/** 請求書詳細の報酬用請求入力（1行）フラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_ROW_FEE_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceRowFeeInputFragment";
		/** 請求書詳細の請求入力（1行）表示で使用するオブジェクト名 */
		public static final String INVOICE_DETAIL_INVOICE_ROW_INPUT_FORM_NAME = "invoiceRowInputForm";

		/** 請求書詳細の値引き用請求入力（1行）フラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_ROW_DISCOUNT_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceRowDiscountInputFragment";

		/** 請求書詳細のテキスト用請求入力（1行）フラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_ROW_TEXT_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceRowTextInputFragment";

		/** 請求書詳細の請求入力合計額フラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_TOTAL_AMOUNT_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceTotalAmountInputFragment";
		/** 請求書詳細の請求入力合計額表示で使用するオブジェクト名 */
		public static final String INVOICE_DETAIL_INVOICE_TOTAL_AMOUNT_INPUT_FORM_NAME = "invoiceTotalAmountInputForm";

		/** 請求書詳細の預り金・実費用請求入力（1行）フラグメントのパス */
		public static final String INVOICE_DETAIL_INVOICE_ROW_DEPOSIT_EXPENSE_INPUT_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceRowDepositExpenseInputFragment";
		/** 請求書詳細の預り金・明細（1行）表示で使用するオブジェクト名 */
		public static final String INVOICE_DETAIL_INVOICE_ROW_DEPOSIT_EXPENSE_INPUT_FORM_NAME = "invoiceRowInputForm";

		/** 項目候補値のデータリストFragmentパス */
		public static final String ITEM_LIST_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::itemFragment";
		/** 項目の候補値データオブジェクト名 */
		public static final String ITEM_LIST_NAME = "itemList";

		/** 請求書/精算書詳細 PDF作成中フラグメントのパス */
		public static final String ACCG_DETAIL_PDF_CREATING_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::pdfCreatingFragment";
		/** 請求書/精算書詳細 PDF作成中フラグメントの表示用オブジェクト名 */
		public static final String ACCG_DETAIL_PDF_CREATING_VIEW_FORM_NAME = "pdfCreatingViewForm";

	}

}
