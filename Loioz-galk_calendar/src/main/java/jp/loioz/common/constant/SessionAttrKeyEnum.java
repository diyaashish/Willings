package jp.loioz.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * セッションKEYを表現するクラス
 */
@Getter
@AllArgsConstructor
public enum SessionAttrKeyEnum {

	// ---------------------------------------------------------
	// セッションKEY
	// ---------------------------------------------------------
	/** ログインエラーメッセージ */
	LOGIN_FAILURE_MSG("LOGIN_FAILURE_MSG"),

	/** 顧客・案件一覧 詳細検索 開閉状態 */
	CUSTOMER_DETAIL_SEARCH_OPEN("CUSTOMER_DETAIL_SEARCH_OPEN"),
	/** 顧客管理 精算口座選択状態 */
	CUSTOMER_MGN_SELECTED_SEISAN_KOZA("CUSTOMER_MGN_SELECTED_SEISAN_KOZA"),

	/** 顧客案件メニュー 開閉状態（クリック） */
	CUSTOMER_ANKEN_MENU_OPEN_CLICK("CUSTOMER_ANKEN_MENU_OPEN_CLICK"),
	/** 顧客案件メニュー 開閉状態（リサイズ） */
	CUSTOMER_ANKEN_MENU_OPEN_RESIZE("CUSTOMER_ANKEN_MENU_OPEN_RESIZE"),

	/** OAuthの認証時に送信するパラメータをsessionに保持する(UUIDのみ) */
	OAUTH_STATE_PARAMS("OAUTH_STATE_PARAMS"),

	/** 一覧へ戻るリンクで遷移する画面情報 */
	RETURN_LIST_SCREEN("RETURN_LIST_SCREEN"),

	/** 前の画面へ戻るリンクで遷移する画面情報 */
	RETURN_PREV_SCREEN_NAME("RETURN_PREV_SCREEN_NAME"),
	/** 前の画面へ戻るリンクで遷移するURL情報 */
	RETURN_PREV_SCREEN_URL("RETURN_PREV_SCREEN_URL"),

	ACCG_ANKEN_SCREEN_BOOL("ACCG_ANKEN_SCREEN_BOOL"),
	ACCG_PERSON_SCREEN_BOOL("ACCG_PERSON_SCREEN_BOOL"),

	/** クイック検索の入力ワード */
	QUICK_SEARCH_WORD("QUICK_SEARCH_WORD"),

	;
	/** ステータスコードのキー */
	private final String codeKey;

}
