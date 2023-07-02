package jp.loioz.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * メール定数
 */
public class MailConstants {

	/**
	 * メール設定
	 */
	@Getter
	@AllArgsConstructor
	public enum MailIdList implements DefaultEnum {

		// ■顧客に送るメール：M + 連番(0埋め4桁)
		/** 仮アカウント発行メール（アカウント登録画面へ遷移するためのURLを記載したメール） */
		MAIL_1("M0001", "仮アカウント発行"),
		/** アカウント登録完了メール（ログイン画面へ遷移するためのURLを記載したメール） */
		MAIL_2("M0002", "アカウント登録完了"),
		/** パスワード忘れ申請メール（パスワード再設定画面へ遷移するためのURLを記載したメール） */
		MAIL_3("M0003", "パスワード忘れ申請"),
		/** 伝言受信の通知メール */
		MAIL_4("M0004", "伝言受信通知"),
		/** 有料プラン登録の通知メール */
		MAIL_5("M0005", "有料プラン登録通知"),
		/** 契約内容（プラン）の変更の通知メール */
		MAIL_6("M0006", "契約内容の変更通知"),
		/** 解約完了の通知メール */
		MAIL_7("M0007", "解約完了通知"),
		/** 問い合わ返信の通知メール */
		MAIL_8("M0008", "問い合わせ返信通知"),
		/** 問い合わせ新規作成の通知メール */
		MAIL_9("M0009", "問い合わせ送信完了通知"),
		/** アカウント登録完了メール（ログイン画面へ遷移するためのURLを記載したメール） */
		MAIL_10("M0010", "初期アカウント設定通知"),
		/** アカウント登録完了メール */
		MAIL_11("M0011", "アカウント登録完了"),
		/** アカウント登録完了メール（管理者用) */
		MAIL_12("M0012", "アカウント登録完了(管理者用)"),
		/** 会計書類送付 WEB共有 顧客宛 */
		MAIL_13("M0013", "会計書類送付WEB共有 顧客宛"),
		/** 会計書類送付 WEB共有パスワード 顧客宛 */
		MAIL_14("M0014", "会計書類送付 WEB共有パスワード 顧客宛"),
		/** 会計書類送付 メール送付 顧客宛 */
		MAIL_15("M0015", "会計書類送付 メール送付 顧客宛"),

		// ■管理者に送るメール：M + 9千番台から連番
		/** 新規事務所申し込み完了の通知メール */
		MAIL_9001("M9001", "新規事務所申し込み完了通知"),
		/** 有料プランの登録申し込み完了通知メール */
		MAIL_9002("M9002", "有料プランの登録申し込み完了通知"),
		/** 契約内容の変更申し込み完了通知メール */
		MAIL_9003("M9003", "契約内容の変更申し込み完了通知"),
		/** 解約完了通知メール */
		MAIL_9004("M9004", "解約申し込み完了通知"),
		/** 問い合わせ通知メール */
		MAIL_9005("M9005", "問い合わせ通知"),
		/** 問い合わせ解決済み通知メール */
		MAIL_9006("M9006", "問い合わせ解決済み通知"),
		/** 無料トライアル仮申込通知メール */
		MAIL_9007("M9007", "無料トライアル仮申込通知"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;
	}

	/**
	 * メール形式
	 */
	@Getter
	@AllArgsConstructor
	public enum MailType implements DefaultEnum {
		// 1:テキスト形式、2:html形式
		MAIL_PLAIN("1", "plain"),
		MAIL_HTNL("2", "html"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;
	}

	/**
	 * メール送信ステータス
	 */
	@Getter
	@AllArgsConstructor
	public enum MailSendStatus implements DefaultEnum {
		// 仮アカウント発行
		SEND_BEFORE("0", "メール送信前"),
		SEND_OK("1", "メール送信成功"),
		SEND_NG90("90", "メール送信失敗(SendFailedException)"),
		SEND_NG91("91", "メール送信失敗(MessagingException)"),
		SEND_NG99("99", "メール送信失敗(その他)"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;
	}

}
