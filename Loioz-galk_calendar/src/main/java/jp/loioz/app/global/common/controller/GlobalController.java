package jp.loioz.app.global.common.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * グローバル系コントローラーのInterfaceクラス
 */
public interface GlobalController {

	/**
	 * DBの接続先を指定したテナントDBに変更する
	 * 
	 * @param tenantSeq
	 */
	default void useTenantDB(Long tenantSeq) {
		SchemaContextHolder.setTenantSeq(tenantSeq);
	}

	/**
	 * DBの接続先を解除する
	 */
	default void clearSchemaContext() {
		SchemaContextHolder.clear();
	}
	
	/**
	 * エラー画面を表示する
	 *
	 * @param errorMsg エラーメッセージ
	 * @return エラー画面情報
	 */
	default ModelAndView globalErrorPage(String errorMsg) {
		return ModelAndViewUtils.getModelAndView("error/globalError")
				.addObject("errorMsg", errorMsg);
	}

	/**
	 * エラー画面を表示する
	 * 
	 * @param errorTitle エラータイトル
	 * @param errorMsg エラーメッセージ
	 * @return エラー画面情報
	 */
	default ModelAndView globalErrorPage(String errorTitle, String errorMsg) {
		return this.globalErrorPage(errorMsg).addObject("errorTitle", errorTitle);
	}
	
	/**
	 * 指定のエラータイプのGlobalAuthExceptionが発生した場合にgrobalError.htmlに表示する<br>
	 * エラータイトルとエラーメッセージのメッセージキーのリストを取得する。
	 * 
	 * @param errorTypeEnum
	 * @return 必ず要素数2つのListを返却する。[0] = エラータイトルのメッセージキー、[1] = エラーメッセージのメッセージキー
	 */
	default List<String> getErrorMsgKeyList(MessageEnum errorTypeEnum) {
		
		// エラータイトルのメッセージキー
		String errorTitleMsgKey = "";
		// エラーメッセージのメッセージキー
		String errorMsgMsgKey = "";
		
		if (errorTypeEnum == null) {
			// エラータイプが未設定の場合
			// ページが見つかりません
			errorTitleMsgKey = MessageEnum.MSG_E00175.getMessageKey();
			// URLに誤りがないかご確認をお願いします。
			errorMsgMsgKey = MessageEnum.MSG_E00192.getMessageKey();
		}
		
		switch(errorTypeEnum) {
			// ページが見つからない（URLの不備など）
			case MSG_E00175:
				// ページが見つかりません。
				errorTitleMsgKey = MessageEnum.MSG_E00175.getMessageKey();
				// URLに誤りがないかご確認をお願いします。
				errorMsgMsgKey = MessageEnum.MSG_E00192.getMessageKey();
				break;
			// 有効期限切れ
			case MSG_E00193:
				// ダウンロードの有効期限が終了しました。
				errorTitleMsgKey = MessageEnum.MSG_E00193.getMessageKey();
				// 有効期限が終了したため、ダウンロードを行うことができません。
				errorMsgMsgKey = MessageEnum.MSG_E00194.getMessageKey();
				break;
			// アクセスエラー（認証トークンの検証エラーなど）
			case MSG_E00195:
				// アクセスに失敗しました。
				errorTitleMsgKey = MessageEnum.MSG_E00195.getMessageKey();
				// アクセスを正常に行うことができませんでした。
				errorMsgMsgKey = MessageEnum.MSG_E00196.getMessageKey();
				break;
			// その他
			default:
				// ページが見つかりません
				errorTitleMsgKey = MessageEnum.MSG_E00175.getMessageKey();
				// URLに誤りがないかご確認をお願いします。
				errorMsgMsgKey = MessageEnum.MSG_E00192.getMessageKey();
				break;
		}
		
		// タイトル、メッセージの順で格納
		return Arrays.asList(errorTitleMsgKey, errorMsgMsgKey);
	}
}
