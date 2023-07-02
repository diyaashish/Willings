package jp.loioz.common.constant;

/**
 * 会計管理（Accg系）の定数クラス ※定数のみ
 * 
 */
public class AccgConstant {

	// =================================================
	// 金額フォーマット
	// =================================================
	public static final String AMOUNT_LABEL_FORMAT = "#,###.##";

	// =================================================
	// 桁数
	// =================================================

	/** 会計管理：送付時の認証キーLength */
	public static final int ACCG_FILE_VALIFICATION_KEY_LENGTH = 64;

	/** 請求書-摘要のmaxlength */
	public static final int MX_LEN_INVOICE_SUM_TEXT = 150;

	/** メモのmaxlength */
	public static final int MX_LEN_MEMO = 300;

	/** タイトルのmaxlength */
	public static final int MX_LEN_TITLE = 20;

	/** 日付のmaxlength */
	public static final int MX_LEN_DATE = 10;

	/** 請求番号、精算番号のmaxlength */
	public static final int MX_LEN_ACCG_NO = 21;

	/** 請求先名のmaxlength */
	public static final int MX_LEN_INVOICE_TO_NAME = 256;

	/** 請求先詳細のmaxlength */
	public static final int MX_LEN_INVOICE_TO_DETAIL = 300;

	/**
	 * <pre>
	 * 請求元事務所名のmaxlength 
	 * RFC3696仕様でメールアドレスは320バイトまで可能だが
	 * Base64変換の仕様を考慮し、余裕をもって50文字をMAXとする。
	 * </pre>
	 */
	public static final int MX_LEN_INVOICE_FROM_OFFICE_NAME = 50;

	/** 請求元詳細のmaxlength */
	public static final int MX_LEN_INVOICE_FROM_DETAIL = 500;

	/** 挿入文のmaxlength */
	public static final int MX_LEN_INVOICE_SUB_TEXT = 20;

	/** 件名のmaxlength */
	public static final int MX_LEN_INVOICE_SUBJECT = 160;

	/** 振込先のmaxlength */
	public static final int MX_LEN_TENANT_BANK_DETAIL = 300;

	/** 備考のmaxlength */
	public static final int MX_LEN_REMARKS = 300;

	/** DBに登録できる金額系の最大桁数 */
	public static final int MX_TOTAL_INVOICE_AMOUNT = 10;

	// =================================================
	// ■ LIMIT_COUNT
	// =================================================

	/** 新会計管理：請求/精算一覧 表示件数 */
	public static final Integer ACCG_INVOICE_STATEMENT_LIST_DISP_LIMIT = 3;

	/** 支払計画 分割可能回数上限 */
	public static final int ACCG_PAYMENT_PLAN_LIMIT = 60;

	// =================================================
	// ■ 定数文字列
	// =================================================

	/**
	 * 会計書類送付モーダル_内容のデフォルト値に追加するテナント名の文言フォーマット
	 * 
	 * @args1 テナント名
	 */
	public static final String ACCG_FILE_SEND_INPUT_MODAL_BODY_ADD_TENANT_NAME_MSG = "" +
			"いつもお世話になっております。\r\n" +
			"%sでございます。";

	/**
	 * 請求書送付_WEB共有メール 追加メッセージ本文追加コンテンツ
	 * 
	 * @args1 請求書 or 精算書
	 * @args2 パスワード設定を行う場合のみ、{@link #ACCG_INVOICE_FILE_SEND_WEB_SHARED_PASSWORD_MSG}
	 * @args3 URL
	 * @args4 日時
	 */
	public static final String ACCG_INVOICE_FILE_SEND_WEB_SHARED_ADD_MSG = "" +
			"%sは、以下のURLよりダウンロードすることができます。\r\n" +
			"%s" +
			"＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝\r\n" +
			"ＵＲＬ　：%s \r\n" +
			"有効期限：%s \r\n" +
			"＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝";

	/** 請求書送付_WEB共有メール 追加メッセージパスワード設定文言 */
	public static final String ACCG_INVOICE_FILE_SEND_WEB_SHARED_PASSWORD_MSG = "別メールでお送りしたパスワードでログインしてください。\r\n";

	/** 預り金出金予定の項目名 */
	public static final String ACCG_ITEM_NAME_OF_WITHDRAWAL_SCHEDULE = "預り金精算";

	/** 預り金出金予定の摘要 */
	public static final String ACCG_SUM_TEXT_OF_WITHDRAWAL_SCHEDULE = "預り金の返金として";

	/** 預り金入金予定の項目名 */
	public static final String ACCG_ITEM_NAME_OF_DEPOSIT_PAYMENT_SCHEDULE = "預り金";

	/** 実費入金予定の項目名 */
	public static final String ACCG_ITEM_NAME_OF_EXPENSE_PAYMENT_SCHEDULE = "実費請求";

	/** 実費入金予定の摘要 */
	public static final String ACCG_SUM_TEXT_OF_EXPENSE_PAYMENT_SCHEDULE = "立替実費分として";

	/** 預り金報酬振替の項目名 */
	public static final String ACCG_ITEM_NAME_OF_TRANSFER_TO_FEE = "報酬振替";

	/** 預り金報酬振替の摘要 */
	public static final String ACCG_SUM_TEXT_OF_TRANSFER_TO_FEE = "報酬への振替";

	/** 実費まとめ表示用の項目名 */
	public static final String ACCG_ITEM_NAME_OF_SUM_EXPENSE = "実費合計";

	/** 実費まとめ表示用の摘要 */
	public static final String ACCG_SUM_TEXT_OF_SUM_EXPENSE = "内容は、実費明細書をご確認ください。";

	/** 預り金明細発生日表示用のステータス名 */
	public static final String STATUS_DEPOSIT_PAYMENT_NOT_YET = "精算待ち";

	/** 預り金明細発生日表示用のステータス名 */
	public static final String STATUS_DEPOSIT_RECV_NOT_YET = "入金待ち";
}