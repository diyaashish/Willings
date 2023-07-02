package jp.loioz.common.constant;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.JapaneseEra;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.seasar.doma.Domain;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Common処理系の定数クラス
 */
public class CommonConstant {

	/** 定数追加 ここから */

	/** loiozブルー */
	public static final String LOIOZ_BULE = "#17acc7";

	/** アプリケーションの起動モードの文字列（管理者） ※プロパティファイルの値か、アプリケーションの起動コマンドのパラメータで指定する値 */
	public static final String ADMIN_APP_BOOT_MODE_VAL = "admin";

	/** HTTPヘッダー名（コンテキスパスまでのURI情報） */
	public static final String HEADER_NAME_OF_URI_UNTIL_CONTEXT_PATH = "X-Uri-Until-Context-Path";

	/** HTTPヘッダー名（HTMLを返却するajaxのControllerメソッドの処理結果情報） */
	public static final String HEADER_NAME_OF_AJAX_PROC_RESULT = "X-Ajax-Proc-Result";
	/** HTTPヘッダー名（HTMLを返却するajaxのControllerメソッドの処理結果メッセージ） */
	public static final String HEADER_NAME_OF_AJAX_PROC_RESULT_MESSAGE = "X-Ajax-Proc-Result-Message";
	/** HTTPヘッダー値（HTMLを返却するajaxのControllerメソッドの処理結果情報の値（成功）） */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_SUCCESS = "success";
	/** HTTPヘッダー値（HTMLを返却するajaxのControllerメソッドの処理結果情報の値（失敗）） */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_FAILURE = "failure";

	/** グローバルアクセスのテナントSEQを識別ダミー桁数 */
	public static final int GLOBAL_TENANT_AUTH_DUMMY_LENGTH = 5;

	/** ログインフォームの属性名 */
	public static final String LOGIN_FORM_OF_ACCOUNT_ID_NAME = "username";
	public static final String LOGIN_FORM_OF_PASS_WORD_NAME = "password";
	public static final String LOGIN_FORM_OF_KEEP_ACCOUNT_FLG_FLG_NAME = "keepAccountFlg";
	public static final String LOGIN_FORM_OF_KEEP_AUTHENTICATION_FLG_NAME = "keepAuthenticationFlg";

	public static final Long ADMIN_TENANT_SEQ = 0L;
	/** セッションに保持するテナント連番の属性名 */
	public static final String TENANT_SEQ_SESSION_NAME = "tenantSeq";
	/** システムがアクションをした場合に使用するアカウント連番 */
	public static final Long SYSTEM_ACCOUNT_SEQ = 0L;

	/** ブランク */
	public static final String BLANK = "";
	/** 半角スペース */
	public static final String SPACE = " ";
	/** 全角スペース */
	public static final String FULL_SPACE = "　";
	/** 半角スペース 4 */
	public static final String FOUR_SPACE = StringUtils.repeat(SPACE, 4);
	/** 半角スペース 6 */
	public static final String SIX_SPACE = StringUtils.repeat(SPACE, 6);
	/** 改行コード（CRLF） */
	public static final String CRLF_CODE = "\r\n";
	/** 改行コード（ラインフィード） */
	public static final String LINE_FEED_CODE = "\n";
	/** ハイフン */
	public static final String HYPHEN = "-";
	/** アンダーバー */
	public static final String UNDER_BAR = "_";
	/** ピリオド */
	public static final String PERIOD = "\\.";
	/** カンマ */
	public static final String COMMA = ",";
	/** カンマ+半角スペース */
	public static final String COMMA_SP = "," + SPACE;
	/** ゼロ */
	public static final String ZERO = "0";
	/** エクスクラメーションマーク */
	public static final String EXCLAMATIONMARK = "!";

	/** 画面表示モード：登録モード */
	public static final boolean VIEW_MODE_REGIST = false;
	/** 画面表示モード：編集モード */
	public static final boolean VIEW_MODE_UPDATE = true;

	// Integer.toUnsignedLong(Integer.MAX_VALUE*2+1)
	// と求めることが可能だが、定数式である必要があったため固定で指定
	/** 符号なしIntの最大値 */
	public static final long MAX_VALUE_OF_UNSIGNED_INT = 4294967295L;

	/** 符号ありIntの最大値 */
	public static final long MAX_VALUE_OF_INT = 2147483647L;

	/** 画面で入力可能な金額（BigInt）の最大値（文字列） */
	public static final String MAX_VALUE_OF_BIGINT_TEN_THOUSAND_STR = "9223372036854775807";

	/** 画面で入力可能な金額（BigInt）の最小値（文字列） */
	public static final String MIN_VALUE_OF_BIGINT_TEN_THOUSAND_STR = "-9223372036854775807";

	/** 金額項目のスケール */
	public static final int AMOUNT_DECIMAL_SCALE = 2;

	/** 金額項目のフォーマット */
	public static final String AMOUNT_LABEL_FORMAT = "#,###.##";

	/** 精算処理 分割支払い上限 */
	public static final int BUNKATU_LIMIT_COUNT = 60;// ※注 分割回数のエラーメッセージは回数がベタ書きなので変更する場合massages.propatiesも修正する必要がある。

	/** 消費税率 10％ 施行日 */
	public static final LocalDate TAX_TEN_PERSENT_STARTING_DATE = LocalDate.of(2019, 10, 01);

	/** ヘッダー検索 カウンタ上限値 */
	public static final int HEADER_SEARCH_COUNTER_LIMIT = 99;

	/** スケジュール画面(Top画面)内 タスク表示上限件数 */
	public static final int SCHEDULE_DISP_ON_TASK_LIMIT = 8;

	/** 施設登録上限 */
	public static final int SHISETSU_REGIST_LIMIT = 50;
	/** 部門登録上限 */
	public static final int BUSHO_REGIST_LIMIT = 50;
	/** 相談経路登録上限 */
	public static final int SODAN_KEIRO_REGIST_LIMIT = 100;
	/** 預り金項目登録上限 */
	public static final int DEPOSIR_MASTER_REGIST_LIMIT = 50;
	/** 報酬項目マスタ登録上限 */
	public static final int FEE_MASTER_REGIST_LIMIT = 50;
	/** メールテンプレートマスタ 登録上限 */
	public static final int MAIL_TEMPLATE_REGIST_LIMIT = 50;
	/** メールテンプレートCC 登録上限 */
	public static final int MAIL_TEMPLATE_CC_LIMIT = 10;
	/** メールテンプレートBCC 登録上限 */
	public static final int MAIL_TEMPLATE_BCC_LIMIT = 10;

	/** 顧客に紐づく案件 追加上限 */
	public static final int ANKEN_ADD_LIMIT = 100;
	/** 案件に紐づく顧客 追加上限 */
	public static final int CUSTOMER_ADD_LIMIT = 100;
	/** モーダル内の検索結果表示 表示上限 */
	public static final int OVER_VIEW_LIMIT = 100;
	/** 分野登録上限 */
	public static final int BUNYA_REGIST_LIMIT = 50;
	/** 裁判追加上限 */
	public static final int SAIBAN_ADD_LIMIT = 20;
	/** 裁判の裁判官の上限数 */
	public static final int SAIBAN_SAIBANKAN_ADD_LIMIT = 3;

	/** 案件ダッシュボード 業務履歴の表示限界 */
	public static final int ANKEN_DASH_GYOMU_HISTORY_LIMIT = 10;

	/** テナント・ユーザー 銀行口座テナント作成時追加上限 */
	public static final int GINKO_KOZA_ADD_LIMIT = 2;

	/** テナント・ユーザー 銀行口web画面からの追加上限 */
	public static final int GINKO_KOZA_ADD_LIMIT_FOR_WEB = 10;

	/** 顧客連絡先追加上限 */
	public static final int CUSTOMER_CONTACT_ADD_LIMIT = 10;

	/** 旧住所追加上限 */
	public static final int OLD_ADDRESS_ADD_LIMIT = 3;

	/** 顧客の居住地／所在地以外の住所（その他住所）の追加上限 */
	public static final int OTHER_ADDRESS_ADD_LIMIT = 10;

	/** 案件売上計上先追加上限 */
	public static final int ANKEN_SALES_OWNER_ADD_LIMIT = 1;

	/** 案件担当者追加上限 */
	public static final int ANKEN_TANTO_ADD_LIMIT = 6;

	/** 顧問契約担当者追加上限 */
	public static final int CONTRACT_TANTO_ADD_LIMIT = 6;

	/** フォルダ追加上限 */
	public static final int DENGON_FOLDER_ADD_LIMIT = 20;

	/** 在監場所追加上限 */
	public static final int ZAIKAN_ADD_LIMIT = 5;

	/** 接見追加上限 */
	public static final int SEKKEN_ADD_LIMIT = 100;

	/** 案件刑事捜査機関 追加上限 */
	public static final int KEIJI_SOSAKIKAN_ADD_LIMIT = 5;

	/** 被疑情報（追起訴）追加上限 */
	public static final int JIKEN_INFO_ADD_LIMIT = 10;

	/** 入力金額上限桁数 */
	public static final int MAX_KINGAKU_INPUT_DIGIT = 9;

	/** 記事に紐づけ可能タグ上限 */
	public static final int KIJI_RELATE_TAG_LIMIT = 10;

	/** 伝言編集画面 顧客検索上限 */
	public static final int DENGON_CUSTOMER_SEARCH_LIMIT = 10;

	/** タスクのサブタスク上限数 */
	public static final int TASK_CHECK_ITEM_ADD_LIMIT = 100;

	/** 案件タスク登録上限 */
	public static final int ANKEN_TASK_ADD_REGIST_LIMIT = 100;

	/** 報酬登録上限 */
	public static final int FEE_ADD_REGIST_LIMIT = 1000;

	/** 預り金登録上限 */
	public static final int DEPOSIT_RECV_ADD_REGIST_LIMIT = 1000;

	/** 請求書・精算書作成モーダルの顧客候補上限 */
	public static final int ACCG_DOC_REGIST_CUSTOMER_LIST_LIMIT = 30;

	/** 支払い期間過去（年） */
	public static final int FROM_SHIHARAI_KIKAN_YEAR = 3;

	/** 支払い期間未来（年） */
	public static final int TO_SHIHARAI_KIKAN_YEAR = 5;

	/** 支払い日付最大 */
	public static final int MAX_MONTH_SHIHARAI_DATE = 29;

	/** セッションタイムアウト用のエラーコード */
	public static final int CUSTOM_SESSION_TIMEOUT_ERROR_CODE = 901;
	/** 二重ログイン用のエラーコード */
	public static final int CUSTOM_MULTI_LOGIN_ERROR_CODE = 902;
	/** プラン別機能制限用のエラーコード */
	public static final int CUSTOME_PLAN_FUNC_RESTRICT_ERROR_CODE = 903;
	/** 利用中にプランが変更した旨のエラーコード */
	public static final int CUSTOME_PLAN_CHANGED_ERROR_CODE = 904;
	/** ロイオズ管理者制御による権限エラーコード */
	public static final int CUSTOME_LOIOZ_ADMIN_CONTROL_ERROR_CODE = 905;

	/** URLを抽出するための正規表現パターン */
	public static final Pattern CONV_URL_LINK_PTN = Pattern.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
			Pattern.CASE_INSENSITIVE);

	/** 登録モード */
	public static final boolean REGIST_MODE = true;

	/** 更新モード */
	public static final boolean UPDATE_MODE = false;

	/** ファイル名の禁則文字(半角スペース区切り) */
	public static final String FILENAME_PROHIBITION_CHARACTER = "\\ / : * ? \" < > |";

	/** ファイル管理画面：ファイルパスの区切り文字 */
	public static final String FILE_MANAGEMENT_SPLIT_PATH_DELIMITER = "/";

	/** ファイル管理画面：アップロード時の1ファイルのアップロード許可容量 50.5MB */
	public static final BigInteger FILE_UPLOAD_MAX_STRAGE = new BigInteger("52953088");

	/** ファイル管理画面：アップロード時の1回でのアップロード許可容量 500MB */
	public static final BigInteger FILE_UPLOAD_MAX_STRAGE_BY_ONCE = new BigInteger("524288000");

	public static final String FILE_UPLOAD_MAX_STRAGE_STRING = "50MB";

	/** 事務所職印 ファイルアップロード許容容量 2MB */
	public static final long OFFICE_STAMP_MAX_SIZE = 2097152L;
	/** 弁護士職印 ファイルアップロード許容容量 2MB */
	public static final long LAWYER_STAMP_MAX_SIZE = 2097152L;

	/** 登録可能な金額の最大値の桁数 */
	public static final int MAX_KINGAKU_KETA = 10;

	/** 登録可能な金額の最大値の桁数 */
	public static final String FORMAT_3KETA = "%,d";

	/** 参加者：部署絞り込み時に「全員」を「ALL」として扱う */
	public static final String SANKASHA_SELECT_OPTION_ALL = "ALL";
	/** 参加者：部署絞り込み時に「案件担当」を「ANKEN_TANTO」として扱う */
	public static final String SANKASHA_SELECT_OPTION_ANKEN_TANTO = "ANKEN_TANTO";
	/** 参加者：部署絞り込み時に「裁判担当」を「SAIBAN_TANTO」として扱う */
	public static final String SANKASHA_SELECT_OPTION_SAIBAN_TANTO = "SAIBAN_TANTO";

	/** 部門未所属 */
	public static final String BUMON_MI_SHOZOKU = "部門未所属";

	/** 問い合わせ画面 ファイルアップロード上限 (10MB) */
	public static final BigInteger TOIAWASE_FILE_UPLOAD_MAX_STRAGE = new BigInteger("10485760");// 10 * 1024 * 1024

	/** 案件メニュー内の事件名最大表示文字数 */
	public static final int MAX_CHAR_COUNT_ANKEN_MENU_JIKEN_NAME = 20;

	/** 顧客に紐づく全ての案件リスト */
	public static final boolean ALL_ANKEN_LIST_FOR_CUSTOMERS = true;

	/** 完了、不受理を除いた顧客に紐づく案件リスト */
	public static final boolean INCOMPLETE_ANKEN_LIST_FOR_CUSTOMERS = false;

	/** 裁判のステータス「すべて」のコード */
	public static final String SAIBAN_STATUS_ALL_CD = "11";

	/** 裁判のステータス未完了（準備中、進行中）のコード */
	public static final String SAIBAN_STATUS_INCOMPLETE_CD = "10";

	/** 案件のステータス「すべて」のコード */
	public static final String ANKEN_STATUS_ALL_CD = "11";

	/** 案件のステータス未完了（相談、進行中、精算待ち、完了待ち）のコード */
	public static final String ANKEN_STATUS_INCOMPLETE_CD = "10";

	/** 使用不可となった「顧問」分野ID */
	public static final String ADVISOR_OLD_BUNYA_ID = "8";

	/** タスクのステータス完了以外（未着手、進行中）の表記名 */
	public static final String TASK_STATUS_INCOMPLETE_NAME = "未完了";

	/** タスク画面：期限付きのタスク情報 今日分の表記名 */
	public static final String FUTURE_TASK_TODAY = "今日";

	/** タスク画面：期限付きのタスク情報 明日分の表記名 */
	public static final String FUTURE_TASK_TOMORROW = "明日";

	/** タスク画面：期限付きのタスク情報 明後日分の表記名 */
	public static final String FUTURE_TASK_DAY_AFTER_TOMORROW = "明後日";

	/** タスク画面：期限付きのタスク情報 明後日以降分の表記名 */
	public static final String FUTURE_TASK_SUB_SEQUENT = "以降";

	/** タスク画面：アクティビティ履歴 作成アクション表記名 */
	public static final String TASK_ACTIVITY_CREATE_ACTION = "作成";

	/** タスク画面：アクティビティ履歴 変更アクション表記名 */
	public static final String TASK_ACTIVITY_UPDATE_ACTION = "変更";

	/** タスク画面：アクティビティ履歴 削除アクション表記名 */
	public static final String TASK_ACTIVITY_DELETE_ACTION = "削除";

	/** タスク画面：アクティビティ履歴 タスク作成項目表記名 */
	public static final String TASK_ACTIVITY_TASK_CREATE_ITEM_NAME = "タスク";

	/** タスク詳細画面：詳細文初期表示文字数 */
	public static final int INIT_SHOW_MAX_CHAR_COUNT_TASK_DETAIL_CONTENT = 200;

	/** 定数追加 ここまで */

	// -----------------------------------------------------------------------------
	// システムフラグ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SystemFlg implements DefaultEnum {
		FLG_ON("1", "フラグオン"),
		FLG_OFF("0", "フラグオフ");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * 論理値からEnum値を取得する
		 *
		 * @param isFlgOn 論理値(フラグONの場合にtrue)
		 * @return Enum
		 */
		public static SystemFlg fromBoolean(boolean isFlgOn) {
			return isFlgOn ? FLG_ON : FLG_OFF;
		}

		/**
		 * 論理値からコードを取得する
		 *
		 * @param isFlgOn 論理値(フラグONの場合にtrue)
		 * @return コード
		 */
		public static String booleanToCode(boolean isFlgOn) {
			return fromBoolean(isFlgOn).getCd();
		}

		/**
		 * コードから論理値を取得する
		 *
		 * @param cd コード
		 * @return true: フラグON / false: フラグOFF
		 */
		public static boolean codeToBoolean(String cd) {
			return SystemFlg.of(cd) == SystemFlg.FLG_ON;
		}

		/**
		 * フラグ判定関数結果から論理値を取得する
		 * 
		 * @param 入力値オブジェクト
		 * @param target フラグ判定用関数
		 * @return
		 */
		public static <T> Predicate<? super T> codeToBoolean(Function<? super T, String> target) {
			Objects.requireNonNull(target);
			return (t) -> target.andThen(SystemFlg::codeToBoolean).apply(t);
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SystemFlg of(String cd) {
			return DefaultEnum.getEnum(SystemFlg.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// loioz運用事務局
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum Loioz implements DefaultEnum {
		BENZO("1", "loioz運用事務局"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

	}

	// -----------------------------------------------------------------------------
	// システム管理者フラグ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SystemMngFlg implements DefaultEnum {

		USER("0", "ユーザー"),
		SYSTEM_MNG("1", "システム管理者");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SystemMngFlg of(String cd) {
			return DefaultEnum.getEnum(SystemMngFlg.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// システム種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum SystemType implements DefaultEnum {
		ADMIN("1", "管理システム"),
		USER("2", "user"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

	}

	// -----------------------------------------------------------------------------
	// 無効フラグ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum DisabledFlg implements DefaultEnum {
		AVAILABLE("0", "利用中"),
		NOT_AVAILABLE("1", "停止中"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DisabledFlg of(String cd) {
			return DefaultEnum.getEnum(DisabledFlg.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// デバイス種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum DeviceType implements DefaultEnum {
		PC("1", "PC"),
		MOBILE("2", "Mobile"),
		TABLET("3", "Tablet"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DeviceType of(String cd) {
			return DefaultEnum.getEnum(DeviceType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 画面表示レイアウト種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum ViewType implements DefaultEnum {
		PC("1", "PC"),
		MOBILE("2", "Mobile"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ViewType of(String cd) {
			return DefaultEnum.getEnum(ViewType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 区分値
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum KubunKindCd implements DefaultEnum {
		// 種別
		XXX_TYPE("0", "_TYPE");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;
	}

	// -----------------------------------------------------------------------------
	// アカウントタイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccountType implements DefaultEnum {

		LAWYER("1", "弁護士"),
		JIMU("2", "事務職員"),
		OTHER("3", "その他");

		/** アカウントタイプコード */
		private String cd;

		/** アカウント名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccountType of(String cd) {
			return DefaultEnum.getEnum(AccountType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// アカウントステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccountStatus implements DefaultEnum {

		ENABLED("1", "有効"),
		DISABLED("0", "無効");

		/** アカウントステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccountStatus of(String cd) {
			return DefaultEnum.getEnum(AccountStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// システム設定（サイドメニュー）
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum SystemSettingMenu implements DefaultEnum {
		OFFICE_EDIT("1", "事務所情報の設定"),
		OFFICE_BUMON("2", "部門の設定"),
		DATA_ROOM("3", "施設の設定"),
		KEIYAKU_ACCOUNT("4", "アカウントの管理"),
		OFFICE_KOZA("5", "事務所口座の設定"),
		FRACTION_SETTING("6", "端数処理"),
		DATA_SODANKEIRO("7", "相談経路の設定"),
		DATA_SAIBANSHO("8", "裁判所の設定"),
		DATA_SOSAKIKAN("9", "捜査機関の設定"),
		KEIYAKU_PLAN("10", "ご利用プラン／請求情報"),
		STORAGE("11", "ファイル管理"),
		DATA_BUNYA("12", "分野の設定"),
		ACCG_FEE_MST("13", "報酬項目の設定"),
		ACCG_DEPOSIT_MST("19", "預り金／実費項目の設定"),
		DOC_INVOICE("14", "請求書"),
		DOC_STATEMENT("15", "精算書"),
		MAIL_BASE("16", "基本設定"),
		MAIL_TEMPLATE("17", "メールテンプレート"),
		MAIL_TEMPLATE_EDIT("18", "メールテンプレートの作成"),
		OFFICE_CONTRACT_MANAGER("20", "契約担当者の設定"),
		FUNC_OLD_KAIKEI_SETTING("21", "旧会計管理の設定"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SystemSettingMenu of(String cd) {
			return DefaultEnum.getEnum(SystemSettingMenu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// アカウント設定（サイドメニュー）
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum AccountSettingMenu implements DefaultEnum {
		MY_ACCOUNT_EDIT("1", "個人情報の設定"),
		MY_ACCOUNT_KOZA("2", "個人口座の設定"),
		MY_ACCOUNT_PASSWORD("5", "パスワード変更"),
		MY_EXTERNAL_SERIVE("3", "外部サービス接続"),
		TOIAWASE("4", "ご質問・ご要望"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccountSettingMenu of(String cd) {
			return DefaultEnum.getEnum(AccountSettingMenu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 都道府県コード
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum CompanyTodofuken implements DefaultEnum {
		HOKKAIDOU("01", "北海道"),
		AOMORI("02", "青森県"),
		IWATE("03", "岩手県"),
		MIYAGI("04", "宮城県"),
		AKITA("05", "秋田県"),
		YAMAGATA("06", "山形県"),
		FUKUSHIMA("07", "福島県"),
		IBARAKI("08", "茨城県"),
		TOTIGI("09", "栃木県"),
		GUNMA("10", "群馬県"),
		SAITAMA("11", "埼玉県"),
		CHIBA("12", "千葉県"),
		TOKYO("13", "東京都"),
		KANAGAWA("14", "神奈川県"),
		NIIGATA("15", "新潟県"),
		TOYAMA("16", "富山県"),
		ISHIKAWA("17", "石川県"),
		FUKUI("18", "福井県"),
		YAMANASHI("19", "山梨県"),
		NAGANO("20", "長野県"),
		GIFU("21", "岐阜県"),
		SHIZUOKA("22", "静岡県"),
		AICHI("23", "愛知県"),
		MIE("24", "三重県"),
		SHIGA("25", "滋賀県"),
		KYOTO("26", "京都府"),
		OOSAKA("27", "大阪府"),
		HYOUGO("28", "兵庫県"),
		NARA("29", "奈良県"),
		WAKAYAMA("30", "和歌山県"),
		TOTTORI("31", "鳥取県"),
		SHIMANE("32", "島根県"),
		OKAYAMA("33", "岡山県"),
		HIROSHIMA("34", "広島県"),
		YAMAGUCHI("35", "山口県"),
		TOKUSHIMA("36", "徳島県"),
		KAGAWA("37", "香川県"),
		EHIME("38", "愛媛県"),
		KOUCHI("39", "高知県"),
		FUKUOKA("40", "福岡県"),
		SAGA("41", "佐賀県"),
		NAGASAKI("42", "長崎県"),
		KUMAMOTO("43", "熊本県"),
		OOITA("44", "大分県"),
		MIYAZAKI("45", "宮崎県"),
		KAGOSHIMA("46", "鹿児島県"),
		OKINAWA("47", "沖縄県"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CompanyTodofuken of(String cd) {
			return DefaultEnum.getEnum(CompanyTodofuken.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 既読フラグ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum MidokuKidokuCd implements DefaultEnum {
		MIDOKU("0", "未読"),
		KIDOKU("1", "既読"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static MidokuKidokuCd of(String cd) {
			return DefaultEnum.getEnum(MidokuKidokuCd.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 契約プラン
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum ServicePlan implements DefaultEnum {

		FREE("0", "無料アカウント"),
		PAID_STANDARD("1", "有料アカウント(標準プラン)"),
		PAID_XX("2", "有料アカウント(XXプラン)");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;
	}

	// -----------------------------------------------------------------------------
	// ページャーの表示件数
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum PageSize implements DefaultEnum {
		TEN("10", "10件", 10),
		FIFTEEN("15", "15件", 15),
		TWENTY("20", "20件", 20),
		THIRTY("30", "30件", 30),
		FIFTY("50", "50件", 50),
		HANDRED("100", "100件", 100);

		/** 取得件数コード */
		private String cd;

		/** 取得件数名称 */
		private String val;

		/** 件数 */
		private int intVal;

	}

	// -----------------------------------------------------------------------------
	// 月
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum Month implements DefaultEnum {
		JANUARY("1", "1"),
		FEBRUARY("2", "2"),
		MARCH("3", "3"),
		APRIL("4", "4"),
		MAY("5", "5"),
		JUNE("6", "6"),
		JULY("7", "7"),
		AUGUST("8", "8"),
		SEPTEMBER("9", "9"),
		OCTOBER("10", "10"),
		NOVEMBER("11", "11"),
		DECEMBER("12", "12"),;

		/** 月コード */
		private String cd;

		/** 月 */
		private String val;

		public String getSelectDispVal() {
			// プルダウン用の表示文字列を取得
			return val + "月";
		}

		public String getZeroPaddingVal() {
			// プルダウン用の表示文字列を取得
			if (val.length() == 1) {
				return "0" + val;
			} else {
				return val;
			}
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static Month of(String cd) {
			return DefaultEnum.getEnum(Month.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// すべてのタスク一覧並び順
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AllTaskListSortKey implements DefaultEnum {

		DEFAULT("1", "設定した順"),
		LIMIT_DATE_ASC("2", "期限-昇順"),
		LIMIT_DATE_DESC("3", "期限-降順");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AllTaskListSortKey of(String cd) {
			return DefaultEnum.getEnum(AllTaskListSortKey.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 完了したタスク一覧並び順
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CloseTaskListSortKey implements DefaultEnum {

		DEFAULT("1", "完了順"),
		LIMIT_DATE_ASC("2", "期限-昇順"),
		LIMIT_DATE_DESC("3", "期限-降順");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CloseTaskListSortKey of(String cd) {
			return DefaultEnum.getEnum(CloseTaskListSortKey.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 案件タスク一覧並び順
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskAnkenListSortKey implements DefaultEnum {

		DEFAULT("1", "登録順"),
		LIMIT_DATE_ASC("2", "期限-昇順"),
		LIMIT_DATE_DESC("3", "期限-降順");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskAnkenListSortKey of(String cd) {
			return DefaultEnum.getEnum(TaskAnkenListSortKey.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 案件タスクの表示切替
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskAnkenListDispKey implements DefaultEnum {

		INCOMPLETE("1", "未完了タスク"),
		COMPLETED("9", "完了タスク");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskAnkenListDispKey of(String cd) {
			return DefaultEnum.getEnum(TaskAnkenListDispKey.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タスクステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskStatus implements DefaultEnum {

		PREPARING("1", "未着手", "notstart"),
		WORKING("2", "進行中", "notstart"),
		COMPLETED("9", "完了", "done");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/** 背景スタイル */
		private String style;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskStatus of(String cd) {
			return DefaultEnum.getEnum(TaskStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タスクフィルター
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskFilter implements DefaultEnum {

		MINE("1", "自分のタスク"),
		RECIEVE("2", "依頼されたタスク"),
		OFFER("3", "依頼したタスク"),
		COMPLETE("4", "完了したタスク");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/** 背景スタイル */

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskFilter of(String cd) {
			return DefaultEnum.getEnum(TaskFilter.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タスク-履歴タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskHistoryType implements DefaultEnum {

		COMMENT("1", "コメント追加", "isHistoryComment"),
		HISTORY("2", "履歴更新", "isHistoryLog"),
		CREATE("3", "新規登録", "isHistoryLog");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/** スタイル */
		private String style;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskHistoryType of(String cd) {
			return DefaultEnum.getEnum(TaskHistoryType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タスク-履歴更新内容
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskUpdateItem implements DefaultEnum {

		TITLE("1", "タイトル"),
		CONTENT("2", "詳細"),
		WORKER("3", "割り当て"),
		LIMIT_DATE("4", "期限"),
		STATUS("5", "ステータス"),
		UPDATED_STATUS("6", "更新後ステータス"),
		ANKEN("7", "案件"),
		UPDATED_ANKEN_KBN("8", "案件更新後ステータス"),
		CHECK_ITEM("9", "サブタスク");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskUpdateItem of(String cd) {
			return DefaultEnum.getEnum(TaskUpdateItem.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タスク-案件更新内容
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AnkenReletedUpdateKbn implements DefaultEnum {

		ANKEN_TOUROKU("1", "登録"),
		ANKEN_HENKOU("2", "変更"),
		ANKEN_KAIJO("3", "解除");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AnkenReletedUpdateKbn of(String cd) {
			return DefaultEnum.getEnum(AnkenReletedUpdateKbn.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タスク-サブタスク更新区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskCheckItemUpdateKbn implements DefaultEnum {

		REGIST("1", "登録"),
		UPDATE("2", "サブタスク名を変更"),
		IN_COMPLETE("3", "ステータスを未完了"),
		COMPLETE("4", "ステータスを完了"),
		DELETE("5", "削除");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskCheckItemUpdateKbn of(String cd) {
			return DefaultEnum.getEnum(TaskCheckItemUpdateKbn.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タスクアラートレベル
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum TaskAlertLevel implements DefaultEnum {

		EXPIRED("3", "期限切れ", Duration.ZERO),
		HIGH("2", "高", Duration.ofDays(3)),
		MIDDLE("1", "中", Duration.ofDays(7)),
		LOW("0", "低", Duration.ofSeconds(Long.MAX_VALUE));

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/** 期限 */
		private Duration limit;
	}

	// -----------------------------------------------------------------------------
	// タスクメニュー
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaskMenu implements DefaultEnum {

		TODAY_TASK("is_today_task", "今日のタスク"),
		FUTURE_TASK("is_future_task", "期限付きのタスク"),
		ALL_TASK("is_all_task", "すべてのタスク"),
		OVERDUE_TASK("is_overdue_task", "期限を過ぎたタスク"),
		ASSIGNED_TASK("is_assigned_task", "割り当てられたタスク"),
		ASSIGN_TASK("is_assign_task", "割り当てたタスク"),
		CLOSE_TASK("is_close_task", "完了したタスク"),
		SEARCH_RESULTS_TASK("is_search_results_task", "検索結果"),
		TASK_ANKEN("is_task_anken", "案件タスク"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaskMenu of(String cd) {
			return DefaultEnum.getEnum(TaskMenu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// メールフォルダ種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum MailBoxType implements DefaultEnum {

		RECEIVE("1", "受信BOX"),
		RECEIVE_CUSTOM("2", "カスタムフォルダ"),
		RECEIVE_SUB_CUSTOM("3", "カスタムサブフォルダ"),
		SEND("4", "送信BOX"),
		DRAFT("5", "下書き"),
		TRASHED("6", "ごみ箱");

		/** アカウントステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static MailBoxType of(String cd) {
			return DefaultEnum.getEnum(MailBoxType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// メールフィルタ種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum MailFilterType implements DefaultEnum {

		UNREAD("1", "- 未読"),
		REQUIRED_REPLY("2", "- 要返信"),
		IMPORTANT("3", "- 重要");

		/** アカウントステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static MailFilterType of(String cd) {
			return DefaultEnum.getEnum(MailFilterType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// アカウント編集画面Tab
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum AccountEditTab implements DefaultEnum {
		ACCOUNT("1", "アカウント"),
		GINKO_KOZA("2", "銀行口座"),
		PASSWORD("3", "パスワード");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

	}

	// -----------------------------------------------------------------------------
	// ID関連
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum IdFormat implements DefaultEnum {
		CUSTOMER("1", "名"),
		ANKEN("2", "案");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

	}

	/**
	 * ヘッダー検索タブ種別
	 */
	@Getter
	@AllArgsConstructor
	public enum HeaderSearchTab implements DefaultEnum {
		PERSON("1", "名簿"),
		ANKEN("2", "案件"),
		SAIBAN("3", "裁判");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static HeaderSearchTab of(String cd) {
			return DefaultEnum.getEnum(HeaderSearchTab.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧客ー個人、法人、弁護士区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CustomerType implements DefaultEnum {
		KOJIN("0", "個人"),
		HOJIN("1", "企業・団体"),
		LAWYER("2", "弁護士");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CustomerType of(String cd) {
			return DefaultEnum.getEnum(CustomerType.class, cd);
		}

		/**
		 * 弁護士を除いたCustomerTypeのvaluesを取得
		 * 
		 * @return
		 */
		public static CustomerType[] getValuesExcludedLawyer() {
			return Stream.of(CustomerType.values())
					.filter(e -> e != CustomerType.LAWYER)
					.toArray(CustomerType[]::new);
		}
	}

	// -----------------------------------------------------------------------------
	// 名簿属性
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum PersonAttributeCd implements DefaultEnum {
		CUSTOMER("1", "顧客"),
		ADVISOR("2", "顧問先"),
		LAWYER("3", "弁護士"),
		OTHER("9", "その他");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static PersonAttributeCd of(String cd) {
			return DefaultEnum.getEnum(PersonAttributeCd.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧客ー性別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum Gender implements DefaultEnum {
		UNKNOW("0", "不明"),
		MALE("1", "男性"),
		FEMALE("2", "女性"),
		HUNOU("9", "適用不能");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static Gender of(String cd) {
			return DefaultEnum.getEnum(Gender.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧客（個人）住所区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AddressTypeKojin implements DefaultEnum {
		UNKNOW("0", "-"),
		KYOJUUCHI("1", "居住地"),
		OTHER("2", "その他");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AddressTypeKojin of(String cd) {
			return DefaultEnum.getEnum(AddressTypeKojin.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧客（法人）住所区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AddressTypeHojin implements DefaultEnum {
		UNKNOW("0", "-"),
		SHOZAICHI("1", "所在地"),
		OTHER("2", "その他");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AddressTypeHojin of(String cd) {
			return DefaultEnum.getEnum(AddressTypeHojin.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧客ー郵送先住所区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TransferAddressType implements DefaultEnum {
		UNKNOW("0", "-"),
		KYOJUUCHI("1", "居住地"), // ※顧客が個人の場合は「居住地」だが、顧客が企業・団体、弁護士の場合は「所在地」なので注意すること
		OTHER("2", "その他");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TransferAddressType of(String cd) {
			return DefaultEnum.getEnum(TransferAddressType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 案件ー源泉徴収対象
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum GensenTarget implements DefaultEnum {
		TAISHOUGAI("0", "源泉徴収対象外"),
		TAISHOU("1", "源泉徴収対象");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

	}

	// -----------------------------------------------------------------------------
	// 案件ー顧客登録区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum RegistCustomerType implements DefaultEnum {
		NEW("0", "新規に名簿を追加し設定"),
		REGISTERED("1", "登録済みの名簿から設定");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static RegistCustomerType of(String cd) {
			return DefaultEnum.getEnum(RegistCustomerType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 案件ー案件区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AnkenType implements DefaultEnum {
		JIMUSHO("0", "事務所案件"),
		KOJIN("1", "個人案件");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AnkenType of(String cd) {
			return DefaultEnum.getEnum(AnkenType.class, cd);
		}

		/**
		 * AnkenTypeを選択肢用（SelectOptionForm）に変換したListを取得する<br>
		 * （選択肢用では「案件」という文字を除外する）
		 * 
		 * @return
		 */
		public static List<SelectOptionForm> getSelectOptions() {
			List<SelectOptionForm> selectOptionList = new ArrayList<>();

			for (AnkenType ankenType : AnkenType.values()) {
				String value = ankenType.getCd();
				String label = "";

				if (ankenType == AnkenType.JIMUSHO) {
					label = "事務所";
				} else if (ankenType == AnkenType.KOJIN) {
					label = "個人";
				} else {
					label = ankenType.getVal();
				}

				selectOptionList.add(new SelectOptionForm(value, label));
			}

			return selectOptionList;
		}
	}

	// -----------------------------------------------------------------------------
	// 預かり品ーステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AzukariItemStatus implements DefaultEnum {
		SHUSHU("0", "収集中"),
		HOKAN("1", "保管中"),
		HENKYAKU("2", "返却済");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AzukariItemStatus of(String cd) {
			return DefaultEnum.getEnum(AzukariItemStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 預かり品ー預かり元 / 精算書作成支払い者 相手種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TargetType implements DefaultEnum {
		CUSTOMER("0", "顧客から選択"),
		KANYOSHA("1", "関与者から選択"),
		FREE("2", "自由入力");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TargetType of(String cd) {
			return DefaultEnum.getEnum(TargetType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 裁判ー訴訟人ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum SoshoninType implements DefaultEnum {

		GENKOKU("0", "原告", "5", "被告"),
		SAIKENSHA("1", "債権者", "6", "債務者"),
		MOUSITATENIN("2", "申立人", "7", "相手方"),
		KOUSONIN("3", "控訴人", "8", "被控訴人"),
		JOUKOKUNIN("4", "上告人", "9", "被上告人"),
		HIKOKU("5", "被告", "0", "原告"),
		SAIMUSHA("6", "債務者", "1", "債権者"),
		AITEGATA("7", "相手方", "2", "申立人"),
		HIKOUSONIN("8", "被控訴人", "3", "控訴人"),
		HIJOUKOKUNIN("9", "被上告人", "4", "上告人");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/** ペアステータスコード */
		private String pairCd;

		/** ペアステータス名称 */
		private String pairVal;

	}

	// -----------------------------------------------------------------------------
	// 遷移元種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TransitionType implements DefaultEnum {
		CUSTOMER("1", "顧"),
		ANKEN("2", "案"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TransitionType of(String cd) {
			return DefaultEnum.getEnum(TransitionType.class, cd);
		}

		/**
		 * IDからEnum値を取得する
		 *
		 * @param 案件ID
		 * @param 顧客ID
		 * @return Enum
		 */
		public static TransitionType of(Long transitionAnkenId, Long transitionCustomerId) {
			if (transitionCustomerId != null && transitionAnkenId == null) {
				return TransitionType.CUSTOMER;
			} else if (transitionAnkenId != null && transitionCustomerId == null) {
				return TransitionType.ANKEN;
			} else {
				return null;
			}
		}
	}

	// -----------------------------------------------------------------------------
	// 伝言ーステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum DengonStatus implements DefaultEnum {
		MISHORI("0", "未処理"),
		YOUHENSHIN("1", "要返信");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DengonStatus of(String cd) {
			return DefaultEnum.getEnum(DengonStatus.class, cd);
		}

	}

	// -----------------------------------------------------------------------------
	// 紀元分類
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum EraCategory implements DefaultEnum {

		JP("0", "和暦"),
		CE("1", "西暦");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static EraCategory of(String cd) {
			return DefaultEnum.getEnum(EraCategory.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 紀元
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum EraType implements DefaultEnum {

		SEIREKI("0", "西暦", EraCategory.CE),
		REIWA("5", "令和", EraCategory.JP),
		HEISEI("4", "平成", EraCategory.JP),
		SHOWA("3", "昭和", EraCategory.JP),
		TAISHO("2", "大正", EraCategory.JP),
		MEIJI("1", "明治", EraCategory.JP);

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/** 紀元分類 */
		private EraCategory category;

		/**
		 * 和暦であるか判定する
		 *
		 * @return true:和暦 / false:西暦
		 */
		public boolean isJp() {
			return this.category == EraCategory.JP;
		}

		/**
		 * 裁判番号に使う年号かを判定する
		 *
		 * @return true:和暦 / false:西暦
		 */
		public boolean isUseSaiban() {
			if (EraType.REIWA.cd.equals(this.cd) || EraType.HEISEI.cd.equals(this.cd)) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 和暦を取得する
		 *
		 * @return 和暦
		 */
		public static EraType[] jpValues() {
			return Stream.of(EraType.values()).filter(EraType::isJp).toArray(EraType[]::new);
		}

		/**
		 * 和暦を取得する
		 * 
		 * @return
		 */
		public static List<EraType> jpValuesList() {
			return Arrays.asList(jpValues());
		}

		/**
		 * 裁判番号に使う年号を取得する
		 *
		 * @return 和暦
		 */
		public static EraType[] saibanValues() {
			return Stream.of(EraType.values()).filter(EraType::isUseSaiban).toArray(EraType[]::new);
		}

		/**
		 * 裁判番号に使う年号を取得する
		 * 
		 * @return
		 */
		public static List<EraType> saibanValuesList() {
			return Arrays.asList(saibanValues());
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static EraType of(String cd) {
			return DefaultEnum.getEnum(EraType.class, cd);
		}

		/**
		 * java.time.chrono.JapaneseEraに対応したEnumを取得する
		 * 
		 * @param era
		 * @return
		 */
		public static EraType of(JapaneseEra era) {

			if (era == null) {
				return null;
			}

			if (era == JapaneseEra.HEISEI) {
				return EraType.HEISEI;
			} else if (era == JapaneseEra.SHOWA) {
				return EraType.SHOWA;
			} else if (era == JapaneseEra.TAISHO) {
				return EraType.TAISHO;
			} else if (era == JapaneseEra.MEIJI) {
				return EraType.MEIJI;
			} else {
				return EraType.REIWA;
			}
		}
	}

	// -----------------------------------------------------------------------------
	// 紀元-年号
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum EraEpoch implements DefaultEnum {

		REIWA(EraType.REIWA.getCd(), String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 2018), LocalDate.of(2019, 5, 1), null),
		HEISEI(EraType.HEISEI.getCd(), "31", LocalDate.of(1989, 1, 8), LocalDate.of(2019, 4, 30)),
		SHOWA(EraType.SHOWA.getCd(), "64", LocalDate.of(1926, 12, 25), LocalDate.of(1989, 1, 7)),
		TAISHO(EraType.TAISHO.getCd(), "15", LocalDate.of(1912, 7, 30), LocalDate.of(1926, 12, 24)),
		MEIJI(EraType.MEIJI.getCd(), "45", LocalDate.of(1868, 9, 8), LocalDate.of(1912, 7, 29));

		/** EraTypeコード値 */
		private String cd;

		/** 年号別の紀元 */
		private String val;

		/** 開始日（施行日） */
		private LocalDate from;

		/** 終了日 */
		private LocalDate to;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static EraEpoch of(String cd) {
			return DefaultEnum.getEnum(EraEpoch.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 連絡先区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ContactCategory implements DefaultEnum {

		TEL("1", "電話番号"),
		FAX("2", "FAX番号"),
		EMAIL("3", "メールアドレス");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ContactCategory of(String cd) {
			return DefaultEnum.getEnum(ContactCategory.class, cd);
		}
	}

	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ContactType implements DefaultEnum {

		HOME("1", "自宅"),
		OFFICE("2", "勤務先"),
		MOBILE("3", "携帯"),
		OTHER("9", "その他");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * 連絡先区分を取得する
		 * 
		 * @param category
		 * @return
		 */
		public static ContactType[] values(ContactCategory category) {
			ContactType[] contactTypeAry = {};
			switch (category) {
			case TEL:
				contactTypeAry = ContactType.telValues();
				break;
			case FAX:
				contactTypeAry = ContactType.faxValues();
				break;
			case EMAIL:
				contactTypeAry = ContactType.emailValues();
				break;
			default:
			}
			return contactTypeAry;
		}

		/**
		 * 電話番号の連絡先区分を取得する
		 *
		 * @return 連絡先区分
		 */
		public static ContactType[] telValues() {
			return ContactType.values();
		}

		/**
		 * FAXの連絡先区分を取得する
		 *
		 * @return 連絡先区分
		 */
		public static ContactType[] faxValues() {
			return new ContactType[]{
					HOME, OFFICE, OTHER};
		}

		/**
		 * メールアドレスの連絡先区分を取得する
		 *
		 * @return 連絡先区分
		 */
		public static ContactType[] emailValues() {
			return ContactType.values();
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ContactType of(String cd) {
			return DefaultEnum.getEnum(ContactType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 許可種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AllowType implements DefaultEnum {

		ALLOW("1", "電話連絡【可】"),
		DENY("0", "電話連絡【不可】");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AllowType of(String cd) {
			return DefaultEnum.getEnum(AllowType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 郵送方法
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum MailingType implements DefaultEnum {

		UNKNOWN("0", "-"),
		OFFICE_ENVELOPE("1", "事務所封筒"),
		PLAIN_ENVELOPE("2", "個人名・無地"),
		POSTE_RESTANTE("3", "郵便局留め"),
		PERSONAL_DELIVERY("4", "来所受取"),
		REFUSE("5", "郵送不可");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static MailingType of(String cd) {
			return DefaultEnum.getEnum(MailingType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 口座紐づき種類
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum KozaRelateType implements DefaultEnum {

		TENANT("1", "事務所"),
		ACCOUNT("2", "アカウント"),
		CUSTOMER("3", "顧客"),
		KANYOSHA("4", "関与者"),;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コード値からEnumを取得
		 *
		 * @param cd
		 * @return
		 */
		public static KozaRelateType of(String cd) {
			return DefaultEnum.getEnum(KozaRelateType.class, cd);
		}

	}

	// -----------------------------------------------------------------------------
	// 口座種類
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum KozaType implements DefaultEnum {

		FUTSU("0", "普通"),
		TOZA("1", "当座"),
		SONOTA("2", "その他"),;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static KozaType of(String cd) {
			return DefaultEnum.getEnum(KozaType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 担当種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TantoType implements DefaultEnum {

		SALES_OWNER("1", "売上計上先"),
		LAWYER("2", "担当弁護士"),
		JIMU("3", "担当事務員");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TantoType of(String cd) {
			return DefaultEnum.getEnum(TantoType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 選択肢区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SelectType implements DefaultEnum {

		SODANKEIRO("1", "相談経路"),
		TUIKAJOHO("2", "追加情報");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SelectType of(String cd) {
			return DefaultEnum.getEnum(SelectType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 入出金項目 分野区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum BunyaKubun implements DefaultEnum {

		BASIC_CASE("1", "一般事件"),
		OTHER_CASE("2", "その他事件");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static BunyaKubun of(String cd) {
			return DefaultEnum.getEnum(BunyaKubun.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 入出金項目 入出金区分
	// -----------------------------------------------------------------------------
	/**
	 * 入出金項目 入出金区分
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum NyushukkinType implements DefaultEnum {

		NYUKIN("1", "入金"),
		SHUKKIN("2", "出金");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static NyushukkinType of(String cd) {
			return DefaultEnum.getEnum(NyushukkinType.class, cd);
		}
	}

	/**
	 * 預り金項目種別
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum DepositType implements DefaultEnum {

		NYUKIN("1", "入金"),
		SHUKKIN("2", "出金");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DepositType of(String cd) {
			return DefaultEnum.getEnum(DepositType.class, cd);
		}

		/**
		 * Enum定義に存在するコードか確認します
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isExist(String cd) {
			return Arrays.stream(DepositType.values()).anyMatch(p -> p.equalsByCode(cd));
		}
	}

	// -----------------------------------------------------------------------------
	// 入出金項目 課税区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaxFlg implements DefaultEnum {

		FREE_TAX("0", "非課税"),
		INTERNAL_TAX("1", "課税（内税）"),
		FOREIGN_TAX("2", "課税（外税）");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * 外税のEnumを除いたTaxFlgのEnum配列を取得する
		 * 
		 * @return TaxFlg[]
		 */
		public static TaxFlg[] valuesExcludeForeignTax() {
			return Stream.of(TaxFlg.values())
					.filter(e -> e != TaxFlg.FOREIGN_TAX)
					.toArray(TaxFlg[]::new);
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaxFlg of(String cd) {
			return DefaultEnum.getEnum(TaxFlg.class, cd);
		}

		/**
		 * Enum定義に存在するコードか確認します
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isExist(String cd) {
			return Arrays.stream(TaxFlg.values()).anyMatch(p -> p.equalsByCode(cd));
		}
	}

	// -----------------------------------------------------------------------------
	// 裁判ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SaibanStatus implements DefaultEnum {

		// 10は「未完了」のコード：SAIBAN_STATUS_INCOMPLETE_CD、11は「すべて」のコード：SAIBAN_STATUS_ALL_CD
		// として定数で使用中
		PREPARING("1", "準備中"),
		PROGRESSING("2", "進行中"),
		CLOSED("3", "完了");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * 裁判ステータスを取得する
		 *
		 * @param saibanStartDate 申立日
		 * @param saibanEndDate 終了日
		 * @return 裁判ステータス
		 */
		public static SaibanStatus calc(LocalDate saibanStartDate, LocalDate saibanEndDate) {
			if (saibanEndDate != null) {
				return CLOSED;
			}
			if (saibanStartDate != null) {
				return PROGRESSING;
			}
			return PREPARING;
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SaibanStatus of(String cd) {
			return DefaultEnum.getEnum(SaibanStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 併合反訴種類キー情報
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ConnectStatusMapKey implements DefaultEnum {
		KIHON("isKihon", "基本"),
		HONSO("isHonso", "本訴"),
		HEIGO("isHeigo", "併合"),
		HANSO("isHanso", "反訴");

		/** コード値 */
		private String cd;
		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ConnectStatusMapKey of(String cd) {
			return DefaultEnum.getEnum(ConnectStatusMapKey.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 併合反訴種類
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum HeigoHansoType implements DefaultEnum {
		HEIGO("1", "併合"),
		HANSO("2", "反訴");

		/** コード値 */
		private String cd;
		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static HeigoHansoType of(String cd) {
			return DefaultEnum.getEnum(HeigoHansoType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 関与者種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum KanyoshaType implements DefaultEnum {

		KYODOSOSHONIN("1", "共同訴訟人"),
		AITEGATA("2", "相手方"),
		KYOHANSHA("3", "共犯者"),
		HIGAISHA("4", "被害者");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static KanyoshaType of(String cd) {
			return DefaultEnum.getEnum(KanyoshaType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 関与者ー個人、法人、代理人区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum KanyoshaKubun implements DefaultEnum {
		KOJIN("0", "個人"),
		HOJIN("1", "法人"),
		DAIRININ("2", "代理人");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static KanyoshaKubun of(String cd) {
			return DefaultEnum.getEnum(KanyoshaKubun.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 民事・刑事区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum BunyaType implements DefaultEnum {

		MINJI("0", "民事"),
		KEIJI("1", "刑事");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static BunyaType of(String cd) {
			return DefaultEnum.getEnum(BunyaType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 案件ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AnkenStatus implements DefaultEnum {

		// 10は「未完了」のコード：ANKEN_STATUS_INCOMPLETE_CD、11は「すべて」のコード：ANKEN_STATUS_ALL_CD
		// として定数で使用中
		SODAN("1", "相談中"),
		SHINKOCHU("2", "進行中"),
		MENDAN_YOTEI("3", "相談中"),
		SEISAN_MACHI("4", "精算待ち"),
		KANRYO_MACHI("5", "完了待ち"),
		KANRYO("6", "完了"),
		FUJUNIN("9", "不受任");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AnkenStatus of(String cd) {
			return DefaultEnum.getEnum(AnkenStatus.class, cd);
		}

		/**
		 * 会計情報の登録が可能かどうかを案件ステータスから判断する<br>
		 * ※ 案件の終了は精算完了とする ※注：条件を変更する場合はすべての会計情報編集処理で整合姓が合うことを確認すること
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isSeisanComp(String cd) {
			return getSeisanCompletedStatusCd().contains(cd);
		}

		/**
		 * 精算完了ステータスのEnumCdをリスト化し、返却
		 * 
		 * @return
		 */
		public static List<String> getSeisanCompletedStatusCd() {
			List<String> seisanCompletedStatusCdList = new ArrayList<>(Arrays.asList(AnkenStatus.KANRYO_MACHI.getCd()));
			seisanCompletedStatusCdList.addAll(getCompletedStatusCd());
			return seisanCompletedStatusCdList;
		}

		/**
		 * 案件が終了したかどうかを判定する <br>
		 * ※ 該当ステータス：完了, 不受任
		 *
		 * @param cd
		 * @return 結果
		 */
		public static boolean isComp(String cd) {
			return getCompletedStatusCd().contains(cd);
		}

		/**
		 * 完了ステータスのEnumCdをリスト化し、返却
		 * 
		 * @return
		 */
		public static List<String> getCompletedStatusCd() {
			return Arrays.asList(AnkenStatus.KANRYO.getCd(), AnkenStatus.FUJUNIN.getCd());
		}

	}

	// -----------------------------------------------------------------------------
	// 当事者表記種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TojishaHyokiType implements DefaultEnum {

		GENKOKU_SIDE("1", "原告側"),
		HIKOKU_SIDE("2", "被告側"),
		OUT_SIDER("3", "第三者"),;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TojishaHyokiType of(String cd) {
			return DefaultEnum.getEnum(TojishaHyokiType.class, cd);
		}

	}

	// -----------------------------------------------------------------------------
	// 当事者表記
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TojishaHyoki implements DefaultEnum {

		GENKOKU("01", "原告", true, TojishaHyokiType.GENKOKU_SIDE),
		HIKOKU("02", "被告", true, TojishaHyokiType.HIKOKU_SIDE),
		MOSHITATENIN("03", "申立人", true, TojishaHyokiType.GENKOKU_SIDE),
		AITEGATA("04", "相手方", true, TojishaHyokiType.HIKOKU_SIDE),
		KOSONIN("05", "控訴人", true, TojishaHyokiType.GENKOKU_SIDE),
		HIKOSONIN("06", "被控訴人", true, TojishaHyokiType.HIKOKU_SIDE),
		JOKOKUNIN("07", "上告人", true, TojishaHyokiType.GENKOKU_SIDE),
		HIJOKOKUNIN("08", "被上告人", true, TojishaHyokiType.HIKOKU_SIDE),
		HANSO_GENKOKU("09", "反訴原告", true, TojishaHyokiType.GENKOKU_SIDE),
		HANSO_HIKOKU("10", "反訴被告", true, TojishaHyokiType.HIKOKU_SIDE),
		SAIKENSHA("11", "債権者", true, TojishaHyokiType.GENKOKU_SIDE),
		SAIMUSHA("12", "債務者", true, TojishaHyokiType.HIKOKU_SIDE),
		DAISAN_SAIMUSHA("13", "第三債務者", true, TojishaHyokiType.HIKOKU_SIDE),
		KOKOKUNIN("14", "抗告人", true, TojishaHyokiType.GENKOKU_SIDE),
		HIKOKOKUNIN("15", "被抗告人", true, TojishaHyokiType.HIKOKU_SIDE),
		SAISHIN_GENKOKU("16", "再審原告", true, TojishaHyokiType.GENKOKU_SIDE),
		SAISHIN_HIKOKU("17", "再審被告", true, TojishaHyokiType.HIKOKU_SIDE),
		SAISEI_SAIMUSHA("18", "再生債務者", false, TojishaHyokiType.OUT_SIDER),
		KOSEI_KAISHA("19", "更生会社", false, TojishaHyokiType.OUT_SIDER),;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/** 対の関係があるか */
		private boolean hasPair;

		/** 当事者表記種別 */
		private TojishaHyokiType tojishaHyokiType;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TojishaHyoki of(String cd) {
			return DefaultEnum.getEnum(TojishaHyoki.class, cd);
		}

		/**
		 * 当事者種別ごとのvaluesを取得
		 *
		 * @param tojishaHyokiTypea
		 * @return
		 */
		public static TojishaHyoki[] valuesOfType(TojishaHyokiType tojishaHyokiType) {
			return Stream.of(TojishaHyoki.values()).filter(type -> type.getTojishaHyokiType() == tojishaHyokiType).toArray(TojishaHyoki[]::new);
		}

	}

	// -----------------------------------------------------------------------------
	// 報酬入金ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum FeePaymentStatus implements DefaultEnum {

		UNCLAIMED("1", "未請求"),
		AWAITING_PAYMENT("2", "入金待ち"),
		DEPOSITED("3", "入金済み"),
		PARTIAL_DEPOSIT("4", "一部入金");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static FeePaymentStatus of(String cd) {
			return DefaultEnum.getEnum(FeePaymentStatus.class, cd);
		}

		/**
		 * FeePaymentStatusを選択肢用（SelectOptionForm）に変換したListを取得する<br>
		 * 「未請求」、「入金待ち」、「入金済み」のみ
		 * 
		 * @return
		 */
		public static List<SelectOptionForm> getSelectOptions() {
			return getSelectOptions(null);
		}

		/**
		 * FeePaymentStatusを選択肢用（SelectOptionForm）に変換したListを取得する<br>
		 * 「未請求」、「入金待ち」、「入金済み」 + 追加したいステータス
		 * 
		 * @param addFeePaymentStatusCd
		 * @return
		 */
		public static List<SelectOptionForm> getSelectOptions(String addFeePaymentStatusCd) {
			List<SelectOptionForm> selectOptionList = new ArrayList<>();

			Arrays.stream(FeePaymentStatus.values()).forEach(e -> {
				// 未請求、入金待ち、入金済みを追加
				if (Integer.parseInt(e.getCd()) < Integer.parseInt((FeePaymentStatus.PARTIAL_DEPOSIT.getCd()))) {
					SelectOptionForm form = new SelectOptionForm(e.getCd(), e.getVal());
					selectOptionList.add(form);
				}
			});

			// 追加ステータスがあれば追加
			if (!StringUtils.isEmpty(addFeePaymentStatusCd)) {
				FeePaymentStatus status = FeePaymentStatus.of(addFeePaymentStatusCd);
				SelectOptionForm form = new SelectOptionForm(status.getCd(), status.getVal());
				if (!selectOptionList.contains(form)) {
					selectOptionList.add(form);
				}
			}

			return selectOptionList;
		}

		/**
		 * Enum定義に存在するコードか確認します
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isExist(String cd) {
			return Arrays.stream(FeePaymentStatus.values()).anyMatch(p -> p.equalsByCode(cd));
		}
	}

	// -----------------------------------------------------------------------------
	// 発行ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum IssueStatus implements DefaultEnum {

		DRAFT("1", "下書き"),
		WAIT_DELIV("2", "送付待ち"),
		SENT("3", "送付済み");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * 発行済かどうか
		 * 
		 * @return
		 */
		public boolean isIssued() {
			return DRAFT != this;
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static IssueStatus of(String cd) {
			return DefaultEnum.getEnum(IssueStatus.class, cd);
		}

		/**
		 * 発行済かどうか
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isIssued(String cd) {
			return Optional.ofNullable(of(cd))
					.map(IssueStatus::isIssued)
					.orElse(false);
		}
	}

	// -----------------------------------------------------------------------------
	// 請求書入金ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum InvoicePaymentStatus implements DefaultEnum {

		DRAFT("1", "発行待ち"),
		AWAITING_PAYMENT("2", "入金待ち"),
		DEPOSITED("3", "入金済み"),
		PARTIAL_DEPOSIT("4", "一部入金"),
		OVER_PAYMENT("5", "過入金"),
		UNCOLLECTIBLE("6", "回収不能");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static InvoicePaymentStatus of(String cd) {
			return DefaultEnum.getEnum(InvoicePaymentStatus.class, cd);
		}
	}

	/**
	 * 精算書返金ステータス
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum StatementRefundStatus implements DefaultEnum {

		DRAFT("1", "発行待ち"),
		AWAITING_STATEMENT("2", "精算待ち"),
		PAID("3", "精算済み");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static StatementRefundStatus of(String cd) {
			return DefaultEnum.getEnum(StatementRefundStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 会計管理メニュー
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccgMenu implements DefaultEnum {

		FEE("is_fee", "報酬管理"),
		DEPOSIT_RECV("is_deposit_recv", "預り金／実費管理"),
		INVOICE("is_invoice", "請求書"),
		STATEMENT("is_statement", "精算書"),
		SETTING("is_setting", "設定"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgMenu of(String cd) {
			return DefaultEnum.getEnum(AccgMenu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 曜日(JavaScriptでの定義)
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum JsDayOfWeek implements DefaultEnum {

		SUNDAY("0", "日", DayOfWeek.SUNDAY),
		MONDAY("1", "月", DayOfWeek.MONDAY),
		TUESDAY("2", "火", DayOfWeek.TUESDAY),
		WEDNESDAY("3", "水", DayOfWeek.WEDNESDAY),
		THURSDAY("4", "木", DayOfWeek.THURSDAY),
		FRIDAY("5", "金", DayOfWeek.FRIDAY),
		SATURDAY("6", "土", DayOfWeek.SATURDAY);

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/** 曜日 */
		private DayOfWeek dayOfWeek;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static JsDayOfWeek of(String cd) {
			return DefaultEnum.getEnum(JsDayOfWeek.class, cd);
		}
	}

	/**
	 * 予定データ以外のカレンダー表示種別Enum
	 */
	@Getter
	@AllArgsConstructor
	public enum CalenderOutputType implements DefaultEnum {

		HOLIDAY("1", "日本の祝日", "0-H"),;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/** 頭文字 */
		private String initial;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CalenderOutputType of(String cd) {
			return DefaultEnum.getEnum(CalenderOutputType.class, cd);
		}

	}

	// -----------------------------------------------------------------------------
	// 予定繰り返しタイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ScheduleRepeatType implements DefaultEnum {

		DAILY("1", "毎日"),
		WEEKLY("2", "毎週"),
		MONTHLY("3", "毎月");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ScheduleRepeatType of(String cd) {
			return DefaultEnum.getEnum(ScheduleRepeatType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 予定権限
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SchedulePermission implements DefaultEnum {

		ALL("0", "全員"),
		MEMBER("1", "参加者");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SchedulePermission of(String cd) {
			return DefaultEnum.getEnum(SchedulePermission.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 予定公開
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CalendarOpenRange implements DefaultEnum {

		PUBLIC("0", "公開"),
		PRIVATE("1", "非公開");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CalendarOpenRange of(String cd) {
			return DefaultEnum.getEnum(CalendarOpenRange.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 予定通知
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CalendarNotificationType implements DefaultEnum {

		PUSH("1", "通知"),
		NO("0", "通知しない");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CalendarNotificationType of(String cd) {
			return DefaultEnum.getEnum(CalendarNotificationType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 予定の紐づけ設定
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CalendarRelatedSetting implements DefaultEnum {

		DEFAULT("1", "紐づけなし"),
		PERSON("2", "名簿"),
		ANKEN("2", "案件"),
		SAIBAN("3", "裁判");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CalendarRelatedSetting of(String cd) {
			return DefaultEnum.getEnum(CalendarRelatedSetting.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 予定の出席状況
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccountInvitationStatus implements DefaultEnum {

		ATTEND("1", "出席"),
		ABSENT("2", "欠席"),
		UNDECIDED("3", "未定");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccountInvitationStatus of(String cd) {
			return DefaultEnum.getEnum(AccountInvitationStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 予定の招待種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum InvitationType implements DefaultEnum {

		OWNER("1", "所有者"),
		REQUIRED("2", "必須"),
		OPTIONAL("3", "任意"),
		SHARE("4", "共有");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static InvitationType of(String cd) {
			return DefaultEnum.getEnum(InvitationType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 予定カラー
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CalendarScheduleColorType implements DefaultEnum {

		LOGIN_ACCOUNT_COLOR("0", ""),
		ANKEN_BLUE("1", "#005b90"),
		BASE_RED("2", "#c93a40"),
		BASE_YELLOW("3", "#ffdd76"),
		BASE_GREEN("4", "#428B7B"),
		BASE_ORANGE("5", "#de9610"),
		BASE_PINK("6", "#d06d8c"),
		BASE_PURPLE("7", "#9460a0"),
		BASE_YELLOW_GREEN("8", "#a0c238"),
		BASE_GREY("9", "#7d7d7d"),
		;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CalendarScheduleColorType of(String cd) {
			return DefaultEnum.getEnum(CalendarScheduleColorType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 施設区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SosakikanType implements DefaultEnum {

		KEISATSUSHO("01", "警察署"),
		KOUTISHO("02", "拘置所"),
		KENSATSUCHO("03", "検察庁");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SosakikanType of(String cd) {
			return DefaultEnum.getEnum(SosakikanType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 勾留ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum KoryuStatus implements DefaultEnum {

		ZAITAKU("1", "在宅"),
		MIGARA("2", "身柄");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static KoryuStatus of(String cd) {
			return DefaultEnum.getEnum(KoryuStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 弁護士報酬項目
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum LawyerHoshu implements DefaultEnum {

		CHAKUSHUKIN("1", "着手金"),
		TSUIKA_CHAKUSHUKIN("2", "追加着手金"),
		SEIKO_HOSHU("3", "成功報酬"),
		BUNSHO_TSUSHINHI("4", "文書通信費"),
		TESURYO("5", "手数料"),
		TIME_CHARGE("6", "タイムチャージ"),
		NITTO("7", "日当"),
		ADVISOR_HOSHU("8", "顧問報酬"),
		SODANRYO("9", "相談料"),
		OTHER("10", "その他");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static LawyerHoshu of(String cd) {
			return DefaultEnum.getEnum(LawyerHoshu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 消費税率
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaxRate implements DefaultEnum {

		EIGHT_PERCENT("1", "8", "2014/04/01", "2019/09/30"),
		TEN_PERCENT("2", "10", "2019/10/01", null),
		TAX_FREE("3", "0", null, null),
		;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/** 開始日 */
		private String startDate;

		/** 終了日 */
		private String endDate;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaxRate of(String cd) {
			return DefaultEnum.getEnum(TaxRate.class, cd);
		}

		/**
		 * 日時から税率を取得する
		 *
		 * @param date 判定したい日付
		 * @return 消費税率
		 */
		public static TaxRate of(LocalDate date) {

			if (date == null) {
				return null;
			}

			// 念のためCd値でソート
			List<TaxRate> taxRateList = Arrays.asList(TaxRate.EIGHT_PERCENT, TaxRate.TEN_PERCENT).stream()
					.sorted(Comparator.comparing(TaxRate::getCd))
					.collect(Collectors.toList());

			// Enumに登録されている税率すべて検証する
			for (TaxRate taxRate : taxRateList) {

				// 終了日がnullものは現在施行されている税率
				if (taxRate.getEndDate() == null) {
					return taxRate;
				}

				// 型変換
				LocalDate startDate = DateUtils.parseToLocalDate(taxRate.getStartDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
				LocalDate endDate = DateUtils.parseToLocalDate(taxRate.getEndDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);

				// 施行日と終了日の間のときにreturnする(同日を含む)
				if (!(startDate.isAfter(date) || endDate.isBefore(date))) {
					return taxRate;
				}

			}
			// どれにも当てはまらない場合Nullを返却
			return null;
		}

		/**
		 * Enum定義に存在するコードか確認します
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isExist(String cd) {
			return Arrays.stream(TaxRate.values()).anyMatch(p -> p.equalsByCode(cd));
		}
	}

	// -----------------------------------------------------------------------------
	// 源泉徴収率
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum GensenChoshuRate implements DefaultEnum {

		TEN("1", "10.21"),
		TWENTY("2", "20.42");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static GensenChoshuRate of(String cd) {
			return DefaultEnum.getEnum(GensenChoshuRate.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 源泉徴収するかどうか
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum WithholdingFlg implements DefaultEnum {

		DO("1", "あり"),
		DO_NOT("0", "なし");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static WithholdingFlg of(String cd) {
			return DefaultEnum.getEnum(WithholdingFlg.class, cd);
		}

		/**
		 * Enum定義に存在するコードか確認します
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isExist(String cd) {
			return Arrays.stream(WithholdingFlg.values()).anyMatch(p -> p.equalsByCode(cd));
		}
	}

	// -----------------------------------------------------------------------------
	// 源泉徴収するかどうか TODO 新会計 WithholdingFlgに移行
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum GensenChoshu implements DefaultEnum {

		DO("1", "あり"),
		DO_NOT("0", "なし");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static GensenChoshu of(String cd) {
			return DefaultEnum.getEnum(GensenChoshu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 消費税の端数処理の方法
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TaxHasuType implements DefaultEnum {

		TRUNCATION("1", "切り捨て", RoundingMode.DOWN),
		ROUND_UP("2", "切り上げ", RoundingMode.UP),
		ROUND_OFF("3", "四捨五入", RoundingMode.HALF_UP);

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/** 計算処理種別 */
		private RoundingMode roundingMode;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TaxHasuType of(String cd) {
			return DefaultEnum.getEnum(TaxHasuType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 報酬金額の端数処理の方法
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum HoshuHasuType implements DefaultEnum {

		TRUNCATION("1", "切り捨て", RoundingMode.DOWN),
		ROUND_UP("2", "切り上げ", RoundingMode.UP),
		ROUND_OFF("3", "四捨五入", RoundingMode.HALF_UP);

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/** 計算処理種別 */
		private RoundingMode roundingMode;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static HoshuHasuType of(String cd) {
			return DefaultEnum.getEnum(HoshuHasuType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ★★★★★注意★★★★★
	// お知らせ区分
	// ※システム管理側と統一する必要があり
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum InfoType implements DefaultEnum {

		INFO("1", "お知らせ"),
		UPDATE("2", "アップデート情報"),
		MENTENANCE("3", "メンテナンス情報");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static InfoType of(String cd) {
			return DefaultEnum.getEnum(InfoType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 表示フラグ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum InfoOpenFlg implements DefaultEnum {

		HIDDEN("0", "非表示"),
		DISPLAY("1", "表示");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static InfoOpenFlg of(String cd) {
			return DefaultEnum.getEnum(InfoOpenFlg.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 会計管理：入出金予定
	// 出金時の支払先口座のアカウント種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ShukkinShiharaiSakiAcctType implements DefaultEnum {

		CUSTOMER("1", "顧客"),
		KANYOSHA("2", "関与者");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ShukkinShiharaiSakiAcctType of(String cd) {
			return DefaultEnum.getEnum(ShukkinShiharaiSakiAcctType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 精算記録 - 区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SeisanKirokuKubun implements DefaultEnum {

		SEISAN("1", "精算"),
		SEIKYU("2", "請求");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SeisanKirokuKubun of(String cd) {
			return DefaultEnum.getEnum(SeisanKirokuKubun.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 精算記録 - 毎月の支払日
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SeisanShiharaiMonthDay implements DefaultEnum {

		LASTDAY("2", "月末"),
		DESIGNATEDDAY("1", SPACE); // 指定日付（画面上は空文字）

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SeisanShiharaiMonthDay of(String cd) {
			return DefaultEnum.getEnum(SeisanShiharaiMonthDay.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 請求種別 - 区分 TODO 新会計 InvoiceTypeに移行
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SeikyuType implements DefaultEnum {

		IKKATSU("1", "一括請求"),
		BUNKATSU("2", "分割請求");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SeikyuType of(String cd) {
			return DefaultEnum.getEnum(SeikyuType.class, cd);
		}

		/**
		 * Enum定義に存在するコードか確認します
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isExist(String cd) {
			return Arrays.stream(SeikyuType.values()).anyMatch(p -> p.equalsByCode(cd));
		}
	}

	// -----------------------------------------------------------------------------
	// 請求種別 - 区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum InvoiceType implements DefaultEnum {

		IKKATSU("1", "一括"),
		BUNKATSU("2", "分割");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static InvoiceType of(String cd) {
			return DefaultEnum.getEnum(InvoiceType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 端数
	// TODO FractionalMonthTypeに移行
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum Hasu implements DefaultEnum {

		FIRST("1", "初回"),
		LAST("2", "最終回");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static Hasu of(String cd) {
			return DefaultEnum.getEnum(Hasu.class, cd);
		}
	}

	/**
	 * 支払分割条件端数処理種別
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum FractionalMonthType implements DefaultEnum {

		FIRST("1", "初回"),
		LAST("2", "最終回");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static FractionalMonthType of(String cd) {
			return DefaultEnum.getEnum(FractionalMonthType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 精算記録 -画面表示区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum DispSeisanKubun implements DefaultEnum {

		IKKATSU_SEIKYU("1", "一括請求"),
		BUNKATSU_SEIKYU("2", "分割請求"),
		SHIHARAI("3", "支払");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DispSeisanKubun of(String cd) {
			return DefaultEnum.getEnum(DispSeisanKubun.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// HTMLタグ変換
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum EditerTag implements DefaultEnum {
		HEAD2S("1", "見出し（開始タグ）", "[HL]", "<h2 class=\"dokuji-head2 c-gray\">"),
		HEAD2E("2", "見出し（終了タグ）", "[/HL]", "</h2>"),
		HEAD3S("3", "小見出し（開始タグ）", "[SH]", "<h3 class=\"dokuji-head3 c-gray\">"),
		HEAD3E("4", "小見出し（終了タグ）", "[/SH]", "</h3>"),
		PRES("5", "本文（開始タグ）", "[TX]", "<pre class=\"dokuji-pre\" >"),
		PREE("6", "本文（終了タグ）", "[/TX]", "</pre>"),
		BS("7", "太字（開始タグ）", "[BO]", "<b>"),
		BE("8", "太字（終了タグ）", "[/BO]", "</b>"),
		IS("9", "斜体（開始タグ）", "[IT]", "<i>"),
		IE("10", "斜体（終了タグ）", "[/IT]", "</i>"),
		ULS("11", "箇条書き外（開始タグ）", "[LP]", "<ul>"),
		ULE("12", "箇条書き外（終了タグ）", "[/LP]", "</ul>"),
		LIS("13", "箇条書き内（開始タグ）", "[Lc]", "<li class=\"li-item\">"),
		LIE("14", "箇条書き内（終了タグ）", "[/Lc]", "</li>"),
		HR("15", "横線", "[LN]", "<hr/>"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/** Editerタグ */
		private String editerTag;

		/** HTMLタグ */
		private String htmlTag;

		/**
		 * [デフォルト実装] コード値が同一かどうかをチェックする
		 *
		 * @param code
		 * @return
		 */
		public boolean equalsByTag(String editerTag) {
			return getEditerTag().equals(editerTag);
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static EditerTag of(String cd) {
			return DefaultEnum.getEnum(EditerTag.class, cd);
		}

		/**
		 * editerTagからEnum値を取得する
		 *
		 * @param editerTag
		 * @return
		 */
		public static EditerTag ofTag(String editerTag) {
			return Arrays.stream(EditerTag.values()).filter(e -> e.getEditerTag().equals(editerTag)).findFirst().orElse(null);
		}

		/**
		 * editerTagからHTMLタグを取得する
		 *
		 * @param cd コード
		 * @return tag HTMLタグ
		 */
		public static String changeTag(String editerTag) {
			EditerTag tag = EditerTag.ofTag(editerTag);
			if (tag == null) {
				return null;
			} else {
				return tag.getHtmlTag();
			}
		}
	}

	// -----------------------------------------------------------------------------
	// 法人格「旧：テナント種別」
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TenantType implements DefaultEnum {
		HOJIN("2", "あり"), // 法人事務所
		KOJIN("1", "なし"); // 個人事務所

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TenantType of(String cd) {
			return DefaultEnum.getEnum(TenantType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// アカウント権限
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccountKengen implements DefaultEnum {
		GENERAL("1", "一般"),
		SYSTEM_MNG("2", "システム管理者");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccountKengen of(String cd) {
			return DefaultEnum.getEnum(AccountKengen.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 預り金プール種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum PoolType implements DefaultEnum {
		POOL_MOTO("1", "プール元"),
		POOL_SAKI("2", "プール先");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static PoolType of(String cd) {
			return DefaultEnum.getEnum(PoolType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 出廷種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ShutteiType implements DefaultEnum {

		REQUIRED("1", "要出廷"),
		NOT_REQUIRED("2", "不要"),
		PHONE("3", "電話会議"),
		WEB("4", "ウェブ会議");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ShutteiType of(String cd) {
			return DefaultEnum.getEnum(ShutteiType.class, cd);
		}
	}

	/****************************************
	 * バッチ関連
	 ****************************************/
	// -----------------------------------------------------------------------------
	// バッチID
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum BatchId implements DefaultEnum {
		// バッチを各種定義する
		B0001("B0001", "預かり品管理-返却期限アラートメール送信"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String id;

		@Override
		public String getVal() {
			return cd;
		}
	}

	// -----------------------------------------------------------------------------
	// 実行ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum ExecuteStatus implements DefaultEnum {

		SUCCESS("1", "成功"),
		FAILED("9", "失敗");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String id;

		@Override
		public String getVal() {
			return cd;
		}
	}

	// -----------------------------------------------------------------------------
	// 帳票種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ChohyoType implements DefaultEnum {
		NAIBU("0", "内部帳票"),
		GAIBU("1", "外部帳票");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ChohyoType of(String cd) {
			return DefaultEnum.getEnum(ChohyoType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 送付書タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SouhushoType implements DefaultEnum {
		IPPAN("1", "一般"),
		FAX_USE("2", "FAX用"),
		ININN("3", "委任契約書用");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SouhushoType of(String cd) {
			return DefaultEnum.getEnum(SouhushoType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 私選 / 国選
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum BengoType implements DefaultEnum {
		SHISEN("1", "私選"),
		KOKUSEN("2", "国選");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static BengoType of(String cd) {
			return DefaultEnum.getEnum(BengoType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 処分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ShobunType implements DefaultEnum {
		KOHAN_SEIKYU("1", "起訴（公判請求）"),
		RYAKUSHIKI_MEIREI("2", "起訴（略式命令）"),
		SOKKETSU_SAIBAN("3", "起訴（即決裁判）"),
		FUKISO("4", "不起訴");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ShobunType of(String cd) {
			return DefaultEnum.getEnum(ShobunType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// QA種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum QaType implements DefaultEnum {
		Q("1", "Q"),
		A("2", "A");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static QaType of(String cd) {
			return DefaultEnum.getEnum(QaType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// QA機能分類
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum QaKinoCategory implements DefaultEnum {
		SCHEDULE("1", "予定表"),
		TASK("2", "タスク管理"),
		DENGON("3", "伝言"),
		KEIJIBAN("4", "掲示板"),
		NOTICE("5", "お知らせ"),
		MENU("6", "メニュー"),
		QSEARCH("7", "クイック検索"),
		HELQ("8", "ヘルプ"),
		ACCOUNT("9", "アカウント管理"),;

		/** 機能分類カテゴリコード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static QaKinoCategory of(String cd) {
			return DefaultEnum.getEnum(QaKinoCategory.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// タイムチャージ時間の指定方法
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TimeChargeTimeShitei implements DefaultEnum {
		START_END_TIME("1", "開始・終了時間を指定"),
		MINUTES("2", "時間(分)を指定");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TimeChargeTimeShitei of(String cd) {
			return DefaultEnum.getEnum(TimeChargeTimeShitei.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 時刻（時間部分）
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum HourType implements DefaultEnum {

		HOUR_ZERO("0", "00"),
		HOUR_ONE("1", "01"),
		HOUR_TWO("2", "02"),
		HOUR_THREE("3", "03"),
		HOUR_FOUR("4", "04"),
		HOUR_FIVE("5", "05"),
		HOUR_SIX("6", "06"),
		HOUR_SEVEN("7", "07"),
		HOUR_EIGHT("8", "08"),
		HOUR_NINE("9", "09"),
		HOUR_TEN("10", "10"),
		HOUR_ELEVEN("11", "11"),
		HOUR_TWELVE("12", "12"),
		HOUR_THIRTEEN("13", "13"),
		HOUR_FOURTEEN("14", "14"),
		HOUR_FIFTEEN("15", "15"),
		HOUR_SIXTEEN("16", "16"),
		HOUR_SEVENTEEN("17", "17"),
		HOUR_EIGHTEEN("18", "18"),
		HOUR_NINETEEN("19", "19"),
		HOUR_TWENTY("20", "20"),
		HOUR_TWENTY_ONE("21", "21"),
		HOUR_TWENTY_TWO("22", "22"),
		HOUR_TWENTY_THREE("23", "23");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static HourType of(String cd) {
			return DefaultEnum.getEnum(HourType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 時刻（分部分）
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum MinuteType implements DefaultEnum {

		MINUTE_ZERO("0", "00"),
		MINUTE_ONE("1", "01"),
		MINUTE_TWO("2", "02"),
		MINUTE_THREE("3", "03"),
		MINUTE_FOUR("4", "04"),
		MINUTE_FIVE("5", "05"),
		MINUTE_SIX("6", "06"),
		MINUTE_SEVEN("7", "07"),
		MINUTE_EIGHT("8", "08"),
		MINUTE_NINE("9", "09"),
		MINUTE_TEN("10", "10"),
		MINUTE_ELEVEN("11", "11"),
		MINUTE_TWELVE("12", "12"),
		MINUTE_THIRTEEN("13", "13"),
		MINUTE_FOURTEEN("14", "14"),
		MINUTE_FIFTEEN("15", "15"),
		MINUTE_SIXTEEN("16", "16"),
		MINUTE_SEVENTEEN("17", "17"),
		MINUTE_EIGHTEEN("18", "18"),
		MINUTE_NINETEEN("19", "19"),
		MINUTE_TWENTY("20", "20"),
		MINUTE_TWENTY_ONE("21", "21"),
		MINUTE_TWENTY_TWO("22", "22"),
		MINUTE_TWENTY_THREE("23", "23"),
		MINUTE_TWENTY_FOUR("24", "24"),
		MINUTE_TWENTY_FIVE("25", "25"),
		MINUTE_TWENTY_SIX("26", "26"),
		MINUTE_TWENTY_SEVEN("27", "27"),
		MINUTE_TWENTY_EIGHT("28", "28"),
		MINUTE_TWENTY_NINE("29", "29"),
		MINUTE_THIRTY("30", "30"),
		MINUTE_THIRTY_ONE("31", "31"),
		MINUTE_THIRTY_TWO("32", "32"),
		MINUTE_THIRTY_THREE("33", "33"),
		MINUTE_THIRTY_FOUR("34", "34"),
		MINUTE_THIRTY_FIVE("35", "35"),
		MINUTE_THIRTY_SIX("36", "36"),
		MINUTE_THIRTY_SEVEN("37", "37"),
		MINUTE_THIRTY_EIGHT("38", "38"),
		MINUTE_THIRTY_NINE("39", "39"),
		MINUTE_FORTY("40", "40"),
		MINUTE_FORTY_ONE("41", "41"),
		MINUTE_FORTY_TWO("42", "42"),
		MINUTE_FORTY_THREE("43", "43"),
		MINUTE_FORTY_FOUR("44", "44"),
		MINUTE_FORTY_FIVE("45", "45"),
		MINUTE_FORTY_SIX("46", "46"),
		MINUTE_FORTY_SEVEN("47", "47"),
		MINUTE_FORTY_EIGHT("48", "48"),
		MINUTE_FORTY_NINE("49", "49"),
		MINUTE_FIFTY("50", "50"),
		MINUTE_FIFTY_ONE("51", "51"),
		MINUTE_FIFTY_TWO("52", "52"),
		MINUTE_FIFTY_THREE("53", "53"),
		MINUTE_FIFTY_FOUR("54", "54"),
		MINUTE_FIFTY_FIVE("55", "55"),
		MINUTE_FIFTY_SIX("56", "56"),
		MINUTE_FIFTY_SEVEN("57", "57"),
		MINUTE_FIFTY_EIGHT("58", "58"),
		MINUTE_FIFTY_NINE("59", "59"),;

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static MinuteType of(String cd) {
			return DefaultEnum.getEnum(MinuteType.class, cd);
		}

		/**
		 * 指定分間隔ごとの時間(分)
		 *
		 * @param Interval
		 * @return Enum
		 */
		public static MinuteType[] intervalOfTimeValues(int interval) {
			return Stream.of(MinuteType.values()).filter(type -> {
				return Integer.parseInt(type.cd) % interval == 0;
			}).toArray(MinuteType[]::new);
		}
	}

	// -----------------------------------------------------------------------------
	// 精算タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum SeisanStatus implements DefaultEnum {
		KARI_SEISAN("1", "仮精算"),
		SEISAN("2", "精算");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SeisanStatus of(String cd) {
			return DefaultEnum.getEnum(SeisanStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 入出金項目タイプ
	// ★注意事項★
	// テナント作成時の初期データと連動しているため、修正時は注意してください。
	// 対象のテーブル：m_nyushukkin_komoku
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum NyushukkinKomokuType implements DefaultEnum {

		SEIKYU("1", "入金(請求)"),
		NYUKIN_POOL("2", "他案件から振替"),
		CANNOT_KAISHU("3", "回収不能"),
		HENKIN("4", "出金(返金)"),
		SHUKKIN_POOL("5", "他案件へ振替");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static NyushukkinKomokuType of(String cd) {
			return DefaultEnum.getEnum(NyushukkinKomokuType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ファイル区分
	// -----------------------------------------------------------------------------

	@Getter
	@AllArgsConstructor
	public enum FileKubun implements DefaultEnum {

		IS_ROOT_FOLDER("0", "ルートフォルダ"),
		IS_FOLDER("1", "フォルダ"),
		IS_FILE("2", "ファイル");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static FileKubun of(String cd) {
			return DefaultEnum.getEnum(FileKubun.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ゴミ箱フラグ
	// -----------------------------------------------------------------------------

	@Getter
	@AllArgsConstructor
	public enum TrashBoxFlg implements DefaultEnum {

		IS_NOT_TRASH("0", "通常ファイル/フォルダ"),
		IS_TRASH("1", "ゴミ箱に移動したファイル/フォルダ");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TrashBoxFlg of(String cd) {
			return DefaultEnum.getEnum(TrashBoxFlg.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 軸区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AxisKubun implements DefaultEnum {

		KOKYAKU("0", "顧客軸"),
		ANKEN("1", "案件軸");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AxisKubun of(String cd) {
			return DefaultEnum.getEnum(AxisKubun.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ファイル管理表示優先順位ID
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum FileManagementDisplayPriorityId {

		CUSTOMER_ROOT_FOLDER_GROUP(1L, "顧客ルートフォルダ群"),
		ANKEN_ROOT_FOLDER_GROUP(2L, "案件ルートフォルダ群"),
		JUDGE_ROOT_FOLDER_GROUP(3L, "裁判ルートフォルダ群"),
		NEW_FOLDER_GROUP(4L, "新規フォルダ群"),
		FILE_GROUP(5L, "ファイル群");

		/** コード値 */
		private Long cd;

		/** 名称 */
		private String val;

	}

	// -----------------------------------------------------------------------------
	// 閲覧制限
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum ViewLimit implements DefaultEnum {

		NO_VIEW_LIMIT("0", "閲覧制限無し"),
		VIEWING_LIMITED("1", "関係者のみ閲覧可");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ViewLimit of(String cd) {
			return DefaultEnum.getEnum(ViewLimit.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ファイル管理_遷移元判別区分
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum FileManagementTransitionSourceKubun implements DefaultEnum {

		FROM_ANKEN_AXIS("0", "案件軸の画面からの遷移"),
		FROM_CUSTOMER_AXIS("1", "顧客軸の画面からの遷移"),
		FROM_DOUBLE_CLICK_FOLDER_ROW("2", "フォルダダブルクリックによる遷移"),
		FROM_CLICK_FOLDER_PATH("3", "サブフォルダのフォルダパスクリックによる遷移"),
		FROM_FILE_NAME_QUICK_SEARCH("4", "ファイル名クイック検索による遷移");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static FileManagementTransitionSourceKubun of(String cd) {
			return DefaultEnum.getEnum(FileManagementTransitionSourceKubun.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ストレージ単位
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum StorageUnit {

		MEGA("MB"),
		GIGA("GB");

		/** 単位文字列 */
		private String unitString;
	}

	// -----------------------------------------------------------------------------
	// メッセージレベル
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum MessageLevel implements DefaultEnum {

		INFO("1", "INFO"),
		WARN("2", "WARN"),
		ERROR("3", "ERROR");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static MessageLevel of(String cd) {
			return DefaultEnum.getEnum(MessageLevel.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 会計用合計金額の計算タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CalType implements DefaultEnum {

		NYUKIN_TOTAL("1", "入金額計"),
		SHUKKIN_TOTAL_EXCEPT_HOSHU("2", "出金額計の計算(報酬を除く税込み)"),
		SHUKKIN_TOTAL_INCLUDE_HOSHU("3", "出金額計の計算(すべて ※報酬を含む)"),
		TAX_TOTAL("4", "出金額計の計算(すべて ※報酬を含む)"),
		GENSEN_TOTAL("5", "源泉徴収合計"),
		TAX_TOTAL_EIGHT("6", "出金額計の計算(消費税8％分 ※報酬を含む)"),
		TAX_TOTAL_TEN("7", "出金額計の計算(消費税10％分 ※報酬を含む)");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CalType of(String cd) {
			return DefaultEnum.getEnum(CalType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧客属性
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CustomerAttribute implements DefaultEnum {

		ALL("1", "すべて"),
		CUSTOMER("2", "顧客"),
		AITEGATA("3", "相手方"),
		KANYOSHA("4", "関与者");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CustomerAttribute of(String cd) {
			return DefaultEnum.getEnum(CustomerAttribute.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ファイルタイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum FileType implements DefaultEnum {

		FOLDER("1", "フォルダ"),
		FILE("2", "ファイル"),
		EXCEL("3", "excel"),
		WORD("4", "word"),
		PDF("5", "pdf"),
		JPEG("6", "jpeg"),
		PNG("7", "png"),
		TEXT("8", "text"),
		OTHER("9", "その他");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static FileType of(String cd) {
			return DefaultEnum.getEnum(FileType.class, cd);
		}
	}

	/** ファイル拡張子種別のInterface */
	public interface FileExtensionCategory {
	}

	/**
	 * ファイル拡張子
	 * 
	 * ロイオズ内で扱う拡張子情報を管理する TODO
	 * 〇〇FileExtensionクラスは廃止予定(valの使用は拡張子情報はFileExtension.getExtensionWithInDotを利用する)
	 */
	@Getter
	@AllArgsConstructor
	public enum FileExtension implements DefaultEnum {

		XLSX("1", "xlsx", ExcelFileExtension.class),
		XLSX_U("101", "XLSX", ExcelFileExtension.class),
		XLS("2", "xls", ExcelFileExtension.class),
		XLS_U("102", "XLS", ExcelFileExtension.class),
		DOCX("3", "docx", WordFileExtension.class),
		DOCX_U("103", "DOCX", WordFileExtension.class),
		DOC("4", "doc", WordFileExtension.class),
		DOC_U("104", "DOC", WordFileExtension.class),
		PDF("5", "pdf", PdfFileExtension.class),
		PDF_U("105", "PDF", PdfFileExtension.class),
		JPEG("6", "jpeg", JpgFileExtension.class),
		JPEG_U("106", "JPEG", JpgFileExtension.class),
		JPG("7", "jpg", JpgFileExtension.class),
		JPG_U("107", "JPG", JpgFileExtension.class),
		PNG("8", "png", PngFileExtension.class),
		PNG_U("108", "PNG", PngFileExtension.class),
		TXT("9", "txt", TextFileExtension.class),
		TXT_U("109", "TXT", TextFileExtension.class),
		GIF("10", "gif", GifFileExtension.class),
		GIF_U("110", "GIF", GifFileExtension.class),
		ZIP("11", "zip", ZipFileExtension.class),
		ZIP_U("111", "ZIP", ZipFileExtension.class),
		;

		/** コード ※この値は利用しない。 */
		private String cd;

		/** 名称 */
		private String val;

		/** カテゴリー */
		private Class<? extends FileExtensionCategory> categoryClazz;

		/**
		 * ドット入りの拡張子を取得する
		 * 
		 * @return
		 */
		public String getExtensionWithInDot() {
			return "." + this.getVal();
		}

		/**
		 * 拡張子の文字列比較
		 * 
		 * @param extensions
		 * @return
		 */
		public boolean equalsByValue(String extension) {
			return this.getVal().equals(extension);
		}

		/**
		 * 拡張子文字列から拡張子Enumを取得する
		 * 
		 * @param extension
		 * @return
		 */
		public static FileExtension ofExtension(String extension) {
			return Arrays.stream(FileExtension.values())
					.filter(e -> e.equalsByValue(extension))
					.findAny()
					.orElse(null);
		}

		/**
		 * 各ファイル拡張子Enumクラスから該当するEnum配列を取得
		 * 
		 * @param categoryClazz
		 * @return
		 */
		public static List<FileExtension> getFileExtensionList(Class<? extends FileExtensionCategory> categoryClazz) {
			return Arrays.stream(FileExtension.values()).filter(e -> {
				return e.getCategoryClazz() == categoryClazz;
			}).collect(Collectors.toList());
		}

	}

	/**
	 * Zipファイル拡張子
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ZipFileExtension implements FileExtensionCategory, DefaultEnum {

		ZIP("1", ".zip"),;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ZipFileExtension of(String cd) {
			return DefaultEnum.getEnum(ZipFileExtension.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// エクセルファイル拡張子
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ExcelFileExtension implements FileExtensionCategory, DefaultEnum {

		XLSX("1", ".xlsx"),
		XLS("2", ".xls");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ExcelFileExtension of(String cd) {
			return DefaultEnum.getEnum(ExcelFileExtension.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// ワードファイル拡張子
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum WordFileExtension implements FileExtensionCategory, DefaultEnum {

		DOCX("1", ".docx"),
		DOC("2", ".doc");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static WordFileExtension of(String cd) {
			return DefaultEnum.getEnum(WordFileExtension.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// PDFファイル拡張子
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum PdfFileExtension implements FileExtensionCategory, DefaultEnum {

		PDF("1", ".pdf");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static PdfFileExtension of(String cd) {
			return DefaultEnum.getEnum(PdfFileExtension.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// jpgファイル拡張子
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum JpgFileExtension implements FileExtensionCategory, DefaultEnum {

		JPEG("1", ".jpeg"),
		JPG("2", ".jpg");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static JpgFileExtension of(String cd) {
			return DefaultEnum.getEnum(JpgFileExtension.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// pngファイル拡張子
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum PngFileExtension implements FileExtensionCategory, DefaultEnum {

		PNG("1", ".png");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static PngFileExtension of(String cd) {
			return DefaultEnum.getEnum(PngFileExtension.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// textファイル拡張子
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TextFileExtension implements FileExtensionCategory, DefaultEnum {

		TXT("1", ".txt");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TextFileExtension of(String cd) {
			return DefaultEnum.getEnum(TextFileExtension.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// gifファイル拡張子
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum GifFileExtension implements FileExtensionCategory, DefaultEnum {

		GIF("1", ".gif");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static GifFileExtension of(String cd) {
			return DefaultEnum.getEnum(GifFileExtension.class, cd);
		}
	}

	/**
	 * 問い合わせ種別
	 */
	@Getter
	@AllArgsConstructor
	public enum ToiawaseType implements DefaultEnum {

		OPERATION("1", "操作方法に関するご質問"),
		PROBLEM("2", "技術的な問題や不具合に関するお問い合わせ"),
		AGREEMENT("3", "契約に関するお問い合わせ（料金やアカウントなど）"),
		OPINIONS("4", "サービスに対するご要望（機能改善や機能追加など）"),
		OTHER("5", "その他");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ToiawaseType of(String cd) {
			return DefaultEnum.getEnum(ToiawaseType.class, cd);
		}
	}

	/**
	 * 問い合わせステータス
	 */
	@Getter
	@AllArgsConstructor
	public enum ToiawaseStatus implements DefaultEnum {

		INQUIRING("1", "問い合わせ中"),
		HAS_REPLY("2", "返信あり"),
		COMPLETED("3", "解決済み");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ToiawaseStatus of(String cd) {
			return DefaultEnum.getEnum(ToiawaseStatus.class, cd);
		}
	}

	/**
	 * 案件ダッシュボード：業務履歴エリア表示種別
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum DashGyomuAreaDispType implements DefaultEnum {

		GYOMUHISTORY("1", "業務履歴"),
		TIMELINE("2", "タイムライン"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DashGyomuAreaDispType of(String cd) {
			return DefaultEnum.getEnum(DashGyomuAreaDispType.class, cd);
		}

	}

	/**
	 * 外部サービス
	 */
	@Getter
	@AllArgsConstructor
	public enum ExternalService implements DefaultEnum {

		GOOGLE("01", "Google"),
		BOX("02", "Box"),
		DROPBOX("03", "Dropbox");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ExternalService of(String cd) {
			return DefaultEnum.getEnum(ExternalService.class, cd);
		}

		/**
		 * サービス種別ごとの連携可能サービスを取得する
		 * 
		 * @param externalServiceType
		 * @return
		 */
		public static List<ExternalService> getExternalService(String externalServiceTypeCd) {
			ExternalServiceType externalServiceType = ExternalServiceType.of(externalServiceTypeCd);
			switch (externalServiceType) {
			case STORAGE:
				return Arrays.asList(GOOGLE, BOX, DROPBOX);
			default:
				return Collections.emptyList();
			}
		}

		/**
		 * サービス種別ごとの連携可能サービスを取得する
		 * 
		 * @param externalServiceType
		 * @return
		 */
		public static List<String> getExternalServiceCd(String externalServiceTypeCd) {
			return getExternalService(externalServiceTypeCd).stream().map(ExternalService::getCd).collect(Collectors.toList());
		}

	}

	/**
	 * 外部サービス種別
	 */
	@Getter
	@AllArgsConstructor
	public enum ExternalServiceType implements DefaultEnum {

		STORAGE("01", "ストレージ"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ExternalServiceType of(String cd) {
			return DefaultEnum.getEnum(ExternalServiceType.class, cd);
		}

	}

	// -----------------------------------------------------------------------------
	// サブタスクステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CheckItemStatus implements DefaultEnum {

		INCOMPLETE("0", "未完了"),
		COMPLETED("1", "完了");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static CheckItemStatus of(String cd) {
			return DefaultEnum.getEnum(CheckItemStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 案件一覧情報／名簿情報メニュー
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AnkenMeiboMenu implements DefaultEnum {

		SUBETE_ANKEN("is_subete_anken", "すべて"),
		SOUDANCHU_ANKEN("is_soudanchu_anken", "相談中"),
		SHINKOUCHU_ANKEN("is_shinkouchu_anken", "進行中"),
		SOSHOUCHU_ANKEN("is_soshouchu_anken", "訴訟中"),
		SHUURYOU_ANKEN("is_shuuryou_anken", "終了（完了／不受任）"),
		MITOUROKU_ANKEN("is_mitouroku_anken", "案件未登録"),
		KOKYAKU_MEIBO("is_kokyaku_meibo", "顧客情報"),
		TOUJISHA_MEIBO("is_toujisha_meibo", "当事者・関与者"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AnkenMeiboMenu of(String cd) {
			return DefaultEnum.getEnum(AnkenMeiboMenu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 案件一覧情報メニュー
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AnkenListMenu implements DefaultEnum {

		All_ANKEN_LIST("is_all_anken_list", "すべて"),
		MY_ANKEN_LIST("is_my_anken_list", "自分の担当案件"),
		ADVISOR_ANKEN_LIST("is_advisor_anken_list", "顧問取引先の案件"),
		MINJI_SAIBAN_LIST("is_minji_saiban_list", "民事裁判"),
		KEIJI_SAIBAN_LIST("is_keiji_saiban_list", "刑事裁判"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AnkenListMenu of(String cd) {
			return DefaultEnum.getEnum(AnkenListMenu.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 戻るリンク遷移先情報
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ReturnDestinationScreen implements DefaultEnum {

		// 一覧 (一覧を戻り先とする場合の表示文言は、wrapHeader.htmlに直接記載しており、ここでの表示名は利用しないため空としている)
		ANKEN_LIST("anken_list", "案件一覧", ""),
		MEIBO_LIST("meibo_list", "名簿一覧", ""),
		SAIBAN_LIST("saiban_list", "裁判一覧", ""),

		// 名簿軸 (名簿軸を戻り先とするケースは現在ないため、戻るの表示名は空としている)
		MEIBO_INFO("meibo_info", "名簿情報", ""),
		PERSON_CASE_ACCOUNTING("person_case_accounting", "案件", ""),
		ADVISOR_CONTRACT("advisor_contract", "顧問契約", ""),
		GYOMU_HISTORY_CUSTOMER("gyomu_history_customer", "業務履歴", ""),

		// 案件軸 (案件軸の各画面の、戻るの表示名は、共通で「案件」とする)
		ANKEN_COMMON("anken_common", "案件情報", "案件"),
		ANKEN_MINJI("anken_minji", "案件情報", "案件"),
		ANKEN_KEIJI("anken_keiji", "案件情報", "案件"),
		ANKEN_DASHBOARD("anken_dashboard", "案件ダッシュボード", "案件"),
		SAIBAN_COMMON("saiban_common", "裁判管理", "案件"),
		SAIBAN_MINJI("saiban_minji", "裁判管理", "案件"),
		SAIBAN_KEIJI("saiban_keiji", "裁判管理", "案件"),
		KANYOSHA_LIST("kanyosha_list", "当事者・関与者", "案件"),
		GYOMU_HISTORY_ANKEN("gyomu_history_anken", "業務履歴", "案件"),
		ANKEN_CASE_ACCOUNTING("anken_case_accounting", "会計", "案件"),

		// URLのパラメータにより、名簿軸、案件軸が分かれる画面 (名簿軸を戻り先とするケースは現在ないため、戻るの表示名は「案件」としている)
		AZUKARIITEM("azukariitem", "預り品", "案件"),
		FILE_MANAGEMENT("file_management", "ファイル", "案件"),
		FILE_TRASHBOX("file_trashbox", "ファイルゴミ箱", "案件"),

		// 案件と顧客の両方が必須パラメータの画面（案件軸画面、顧客軸画面、独立した画面の3パターンの表示が可能）
		FEE_DETAIL("fee_detail", "報酬管理", "案件"),
		DEPOSIT_RECV_DETAIL("deposit_recv_detail", "預り金／実費管理", "案件"),
		CASE_INVOICE_STATEMENT("case_invoice_statement", "請求書／精算書", "案件"),
		INVOICE_DETAIL("invoice_detail", "請求書", "案件"),
		STATEMENT_DETAIL("statement_detail", "精算書", "案件"),
		RECORD_DETAIL("record_detail", "取引実績", "案件"),
		;

		/** コード */
		private String cd;

		/** 画面 */
		private String val;

		/** 戻るの表示名 */
		private String dispVal;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ReturnDestinationScreen of(String cd) {
			return DefaultEnum.getEnum(ReturnDestinationScreen.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧問契約ステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ContractStatus implements DefaultEnum {

		CONTRACT_NEW("1", "契約準備中"),
		CONTRACTING("2", "契約中"),
		CONTRACT_CANCELL("3", "契約解除"),
		CONTRACT_END("4", "終了");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ContractStatus of(String cd) {
			return DefaultEnum.getEnum(ContractStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 顧問契約タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum ContractType implements DefaultEnum {

		JIMUSHO("1", "事務所"),
		KOJIN("2", "個人");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ContractType of(String cd) {
			return DefaultEnum.getEnum(ContractType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 死亡フラグ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum DeathFlg implements DefaultEnum {

		FLG_ON("1", "故人"),
		FLG_OFF("0", "故人ではない");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DeathFlg of(String cd) {
			return DefaultEnum.getEnum(DeathFlg.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 預り金残高合計
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum TotalDepositBalance implements DefaultEnum {
		All("1", "すべて"),
		PLUS_ONLY("2", "プラスのみ"),
		MINUS_ONLY("3", "マイナスのみ");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static TotalDepositBalance of(String cd) {
			return DefaultEnum.getEnum(TotalDepositBalance.class, cd);
		}
	}

	/**
	 * 請求書/精算書：年フォーマット
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccgNoYFmt implements DefaultEnum {

		UNSPECIFIED("1", "-"),
		YYYY("2", "yyyy"),
		YY("3", "yy"),
		JP_ERA("4", "和暦");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgNoYFmt of(String cd) {
			return DefaultEnum.getEnum(AccgNoYFmt.class, cd);
		}
	}

	/**
	 * 請求書/精算書：月フォーマット
	 */
	@Getter
	@AllArgsConstructor
	public enum AccgNoMFmt implements DefaultEnum {

		UNSPECIFIED("1", "-"),
		MM("2", "MM"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgNoMFmt of(String cd) {
			return DefaultEnum.getEnum(AccgNoMFmt.class, cd);
		}
	}

	/**
	 * 請求書/精算書：日フォーマット
	 */
	@Getter
	@AllArgsConstructor
	public enum AccgNoDFmt implements DefaultEnum {

		UNSPECIFIED("1", "-"),
		DD("2", "dd"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgNoDFmt of(String cd) {
			return DefaultEnum.getEnum(AccgNoDFmt.class, cd);
		}
	}

	/**
	 * 請求書/精算書：ナンバリング種別
	 */
	@Getter
	@AllArgsConstructor
	public enum AccgNoNumberingType implements DefaultEnum {

		SEQ("1", "連番"),
		YEAR_DELIMITER("2", "年毎の連番"),
		MONTH_DELIMITER("3", "月毎の連番"),
		DAYS_DELIMITER("4", "日毎の連番"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgNoNumberingType of(String cd) {
			return DefaultEnum.getEnum(AccgNoNumberingType.class, cd);
		}
	}

	/**
	 * 事務所共有のメール定型文種別
	 */
	@Getter
	@AllArgsConstructor
	public enum MailTemplateType implements DefaultEnum {

		INVOICE("1", "請求書"),
		STATEMENT("2", "精算書"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static MailTemplateType of(String cd) {
			return DefaultEnum.getEnum(MailTemplateType.class, cd);
		}
	}

	/**
	 * 画像利用種別
	 */
	@Getter
	@AllArgsConstructor
	public enum ImageRoleType implements DefaultEnum {

		LAWYER_STAMP("1", "弁護士職印"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ImageRoleType of(String cd) {
			return DefaultEnum.getEnum(ImageRoleType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 敬称
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum NameEnd implements DefaultEnum {

		SAMA("1", "様"),
		ONCHU("2", "御中"),
		NONE("3", "");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static NameEnd of(String cd) {
			return DefaultEnum.getEnum(NameEnd.class, cd);
		}

		/**
		 * Enum定義に存在するコードか確認します
		 * 
		 * @param cd
		 * @return
		 */
		public static boolean isExist(String cd) {
			return Arrays.stream(NameEnd.values()).anyMatch(p -> p.equalsByCode(cd));
		}
	}

	// -----------------------------------------------------------------------------
	// 請求預り金項目タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum InvoiceDepositType implements DefaultEnum {

		DEPOSIT("1", "預り金"),
		EXPENSE("2", "実費");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static InvoiceDepositType of(String cd) {
			return DefaultEnum.getEnum(InvoiceDepositType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 請求その他項目タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum InvoiceOtherItemType implements DefaultEnum {

		DISCOUNT("1", "値引き"),
		TEXT("2", "テキスト");

		/** コード値 */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static InvoiceOtherItemType of(String cd) {
			return DefaultEnum.getEnum(InvoiceOtherItemType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 会計書類タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccgDocType implements DefaultEnum {

		STATEMENT("1", "精算書"),
		INVOICE("2", "請求書"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * 精算書かどうか
		 * 
		 * @return
		 */
		public boolean isStatement() {
			return STATEMENT == this;
		}

		/**
		 * 請求書かどうか
		 * 
		 * @return
		 */
		public boolean isInvoice() {
			return INVOICE == this;
		}

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgDocType of(String cd) {
			return DefaultEnum.getEnum(AccgDocType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 会計書類対応タイプ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccgDocActType implements DefaultEnum {

		NEW("1", "新規作成"),
		ISSUE("2", "発行"),
		SEND("3", "送付"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgDocActType of(String cd) {
			return DefaultEnum.getEnum(AccgDocActType.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// 会計書類送付種別
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccgDocSendType implements DefaultEnum {

		WEB("1", "Web共有"),
		MAIL("2", "メール添付"),
		CHANGE_TO_SEND("3", "印刷して送付"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgDocSendType of(String cd) {
			return DefaultEnum.getEnum(AccgDocSendType.class, cd);
		}
	}

	/**
	 * 会計書類ファイル種別
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AccgDocFileType implements DefaultEnum {

		INVOICE("1", "請求書", 1),
		STATEMENT("2", "精算書", 2),
		DEPOSIT_DETAIL("3", "実費明細書", 3),
		INVOICE_PAYMENT_PLAN("4", "分割予定表", 4),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/** 表示順番号 */
		private int dispOrder;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccgDocFileType of(String cd) {
			return DefaultEnum.getEnum(AccgDocFileType.class, cd);
		}
	}

	/**
	 * 取引種別
	 */
	@Getter
	@AllArgsConstructor
	public enum RecordType implements DefaultEnum {

		INTO("1", "入金", "badge_cyan"),
		WITHDRAW("2", "出金", "badge_red"),
		TRANSFER_DEPOSIT_INTO("3", "振替(預り金)", "badge_cyan"),
		OVER_PAYMENT_REFUND("4", "過入金返金", "badge_red"),
		;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/** ステータス名称 */
		private String styleClass;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static RecordType of(String cd) {
			return DefaultEnum.getEnum(RecordType.class, cd);
		}
	}

	/**
	 * 請求書/精算書詳細画面のタブ種別
	 */
	@Getter
	@AllArgsConstructor
	public enum AccggInvoiceStatementDetailViewTab implements DefaultEnum {

		EDIT_TAB("1", "編集"),
		INVOICE_PDF_TAB("2", "請求書"),
		STATEMENT_PDF_TAB("3", "精算書"),
		DEPOSIT_DETAIL_PDF_TAB("4", "実費明細書"),
		INVOICE_PLAN_TAB("5", "分割予定表"),;

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static AccggInvoiceStatementDetailViewTab of(String cd) {
			return DefaultEnum.getEnum(AccggInvoiceStatementDetailViewTab.class, cd);
		}
	}

	/**
	 * 預り金作成タイプ
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum DepositRecvCreatedType implements DefaultEnum {

		USER_CREATED("1", "ユーザーの手動作成"),
		CREATED_BY_ISSUANCE("2", "請求書／精算書の発行による予定データ作成"),
		TRANFER_TO_FEE_UPON_ISSUANCE("3", "請求書／精算書の発行による既入金の報酬振替（出金）による作成");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DepositRecvCreatedType of(String cd) {
			return DefaultEnum.getEnum(DepositRecvCreatedType.class, cd);
		}

	}

	/**
	 * 実費入金フラグ
	 */
	@Getter
	@AllArgsConstructor
	public enum ExpenseInvoiceFlg implements DefaultEnum {

		EXCEPT_EXPENSE("0", "実費入金以外のデータ"),
		EXPENSE("1", "実費入金のデータ（請求書作成時のみ）");

		/** ステータスコード */
		private String cd;

		/** ステータス名称 */
		private String val;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static ExpenseInvoiceFlg of(String cd) {
			return DefaultEnum.getEnum(ExpenseInvoiceFlg.class, cd);
		}

	}

	/**
	 * 預り金利用状況
	 */
	@Getter
	@AllArgsConstructor
	public enum DepositUseStatus implements DefaultEnum {

		UN_USED("1", "未処理", BLANK),
		USED("2", "処理済み", "済"),
		USING("3", "今回対象", "✓");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/** ラベル */
		private String label;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static DepositUseStatus of(String cd) {
			return DefaultEnum.getEnum(DepositUseStatus.class, cd);
		}

	}

}