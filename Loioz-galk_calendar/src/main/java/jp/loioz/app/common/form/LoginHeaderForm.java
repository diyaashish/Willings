package jp.loioz.app.common.form;

import jp.loioz.common.constant.CommonConstant.ExternalService;
import lombok.Data;

/**
 * アカウント情報詳細入力画面のユーザー情報のフォームクラス
 */
@Data
public class LoginHeaderForm {

	/** タスク管理新着件数 */
	int newTaskCount;

	/** 伝言管理新着件数 */
	int newDengonCount;

	/** お知らせ管理新着件数 */
	int newInformation;

	/** 未読の問い合わせ件数 */
	int hasNewDetailToiawaseCount;

	/** 連携中ストレージ */
	ExternalService storageConnectedService;
	
	/** ログイン色 */
	String loginColor;
}
