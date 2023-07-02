package jp.loioz.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 一覧表示処理系の並び順定数クラス
 * 
 */
public class SortConstant {

	// =========================================================================
	// 設定項目Enumの定義
	// =========================================================================

	/**
	 * ソートの選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum SortOrder implements DefaultEnum {
		ASC("1", "昇順"),
		DESC("2", "降順"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 案件一覧画面のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum AnkenListSortItem implements DefaultEnum {
		ANKEN_NAME("1", "案件名"),
		ANKEN_CREATE_DATE("2", "案件登録日"),
		CUSTOMER_CREATE_DATE("3", "顧客登録日"),
		JUNIN_DATE("4", "受任日"),
		BUNYA_ID("5", "分野ID"),
		ANKEN_STATUS_ID("6", "進捗"),
		CUSTOMER_NAME_KANA("7", "顧客名（かな）"),
		ANKEN_ID("8", "案件ID"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 裁判一覧画面のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum SaibanListSortItem implements DefaultEnum {
		BUNYA_ID("1", "分野ID"),
		SAIBANSHO_NAME("2", "裁判所名"),
		JIKEN_NO("3", "事件番号"),
		JIKEN_NAME("4", "事件名"),
		CUSTOMER_NAME_KANA("5", "顧客名（かな）"),
		AITEGATA_NAME_KANA("6", "相手方名（かな）"),
		KENSATUCHO_NAME("7", "検察庁名"),
		SAIBAN_STATUS_ID("8", "裁判手続きID"),
		SAIBAN_START_DATE("9", "申立日／起訴日"),
		SAIBAN_END_DATE("10", "終了日／判決日"),
		ANKEN_ID("11", "案件ID"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 報酬一覧画面のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum FeeListSortItem implements DefaultEnum {
		PERSON_ID("1", "名簿ID"),
		PERSON_NAME("2", "名前"),
		ANKEN_ID("3", "案件ID"),
		BUNYA_ID("4", "分野ID"),
		ANKEN_NAME("5", "案件名"),
		ANKEN_STATUS("6", "顧客ステータス"),
		FEE_TOTAL("7", "報酬合計"),
		UNTREATED("8", "未処理"),
		LAST_UPDATED_AT("9", "最終更新日時"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 預り金管理一覧画面のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum DepositRecvListSortItem implements DefaultEnum {
		PERSON_ID("1", "名簿ID"),
		PERSON_NAME("2", "名前"),
		ANKEN_ID("3", "案件ID"),
		BUNYA_ID("4", "分野ID"),
		ANKEN_NAME("5", "案件名"),
		ANKEN_STATUS("6", "顧客ステータス"),
		DEPOSIT_AMOUNT("7", "入金額"),
		WITHDRAWAL_AMOUNT("8", "出金額"),
		DEPOSIT_BALANCE("9", "預り金残高"),
		LAST_EDIT_AT("10", "最終更新日時"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 報酬明細一覧のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum FeeDetailSortItem implements DefaultEnum {
		ITEM("1", "項目"),
		FEE_DATE("2", "発生日"),
		FEE_STATUS("3", "ステータス"),
		FEE_AMOUNT("4", "報酬額(税込)"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 預り金明細一覧のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum DepositRecvDetailSortItem implements DefaultEnum {
		DEPOSIT_TYPE("1", "種別"),
		ITEM("2", "項目"),
		DEPOSIT_DATE("3", "発生日"),
		DEPOSIT_AMOUNT("4", "入金額"),
		WITHDRAWAL_AMOUNT("5", "出金額"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 請求書一覧画面のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum InvoiceListSortItem implements DefaultEnum {
		PERSON_ID("1", "名簿ID"),
		PERSON_NAME("2", "名前"),
		ANKEN_ID("3", "案件ID"),
		ANKEN_NAME("4", "案件名"),
		INVOICE_ISSUE_STATUS("5", "発行ステータス"),
		INVOICE_PAYMENT_STATUS("6", "入金状況"),
		INVOICE_DATE("7", "請求日"),
		DUE_DATE("8", "支払期日"),
		INVOICE_AMOUNT("9", "請求額"),
		INVOICE_NO("10", "請求番号"),
		INVOICE_TYPE("11", "請求方法"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 精算書書一覧画面のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum StatementListSortItem implements DefaultEnum {
		PERSON_ID("1", "名簿ID"),
		PERSON_NAME("2", "名前"),
		ANKEN_ID("3", "案件ID"),
		ANKEN_NAME("4", "案件名"),
		STATEMENT_ISSUE_STATUS("5", "発行ステータス"),
		STATEMENT_REFUND_STATUS("6", "精算状況"),
		STATEMENT_DATE("7", "精算日"),
		REFUND_DATE("8", "期日"),
		STATEMENT_AMOUNT("9", "精算額"),
		STATEMENT_NO("10", "精算番号"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

	/**
	 * 会計画面 顧客一覧項目のソート設定の選択肢<br>
	 */
	@Getter
	@AllArgsConstructor
	public enum CaseAccgCustomerListItem implements DefaultEnum {
		CREATED_AT_ASC("1", "登録日：昇順"),
		CREATED_AT_DESC("2", "登録日：降順"),
		ANKEN_CUSTOMER_STATUS("3", "顧客ステータス"),
		;

		/** 選択肢のCD */
		private String cd;
		/** 選択肢の値 */
		private String val;
	}

}
