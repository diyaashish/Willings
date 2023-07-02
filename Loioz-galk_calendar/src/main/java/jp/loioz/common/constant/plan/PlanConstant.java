package jp.loioz.common.constant.plan;

import java.util.Arrays;
import java.util.Optional;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Common処理系の定数クラス
 */
public class PlanConstant {

	// -----------------------------------------------------------------------------
	// ドメイン
	// -----------------------------------------------------------------------------
	
	/** プラン設定画面用のサブドメイン */
	public static final String PLAN_SETTING_SUB_DOMAIN = "plan-setting";

	// -----------------------------------------------------------------------------
	// パス関連
	// -----------------------------------------------------------------------------
	
	/** 通常のプラン画面への入り口となるメソッドのパス */
	public static final String PLAN_SETTING_GATEWAY_PATH = "prepareAccessPlanSetting";
	
	/** 上記のプラン画面の入り口となるメソッドを介さずに、リダイレクトでプラン画面へアクセスするためのメソッドのパス */
	public static final String PLAN_SETTING_GATEWAY_REDIRECT_PATH = "prepareAndRedirectPlanSetting";

	/** プラン画面で認証エラーが発生した場合に認証エラー画面を表示するメソッドのパス */
	public static final String PLAN_SETTING_AUTH_ERROR_PATH = "planSettingAuthError";
	
	// -----------------------------------------------------------------------------
	// Sessionテーブル関連
	// -----------------------------------------------------------------------------
	
	/** セッションテーブルのレコードを残しておく時間（h）（レコードの最終更新からこの時間を経過したレコードは削除対象とする） */
	public static final int HOURS_TO_KEEP_SESSION_TABLE_RECORD = 6;
	
	/** セッションテーブルのレコード（UUID）が有効であると判断する時間（レコードの最終更新からこの時間を経過したレコードは有効ではないと判断する） */
	public static final int HOURS_TO_JUDGE_VALID_SESSION_TABLE_RECORD = 1;
	
	// -----------------------------------------------------------------------------
	// コード値
	// -----------------------------------------------------------------------------
	
	/** 認証エラー用のエラーコード */
	public static final int CUSTOM_PLAN_SETTING_AUTH_ERROR_CODE = 941;
	
	// -----------------------------------------------------------------------------
	// プラン画面の機能関連
	// -----------------------------------------------------------------------------
	
	/** 無料プラン申し込み時の、デフォルトライセンス数 */
	public static final long FREE_PLAN_LICENSE_COUNT = 3L;
	/** 無料プラン申し込み時の、デフォルトストレージ利用量 */
	public static final long FREE_PLAN_STORAGE_CAPACITY = StoragePrice.STORAGE_15.getQuantity();
	
	/** 課金金額の限度額（税込） */
	public static final long LIMIT_CHARGE_AMOUNT = 1000000L;
	/** 1ヶ月間のプラン登録／変更の実施回数上限値 */
	public static final int LIMIT_COUNT_OF_PLAN_CHANGE_FOR_MONTH = 8;

	// バリデーション
	
	/** 最小値（ライセンス数） */
	public static final int LICENSE_NUM_MIN = 1;
	/** 最大値（ライセンス数） */
	public static final int LICENSE_NUM_MAX = 9999;
	
	/** 最小値（ストレージ容量） */
	public static final int STORAGE_CAPA_MIN = 0;
	/** 最大値（ストレージ容量） */
	public static final int STORAGE_CAPA_MAX = 9999999;
	
	// -----------------------------------------------------------------------------
	// 利用プランステータス
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum PlanStatus implements DefaultEnum {

		FREE("0", "無料トライアル"),
		ENABLED("1", "利用中"),
		CHANGING("7", "変更中"),
		STOPPED("8", "停止"),
		CANCELED("9", "解約");

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
		public static PlanStatus of(String cd) {
			return DefaultEnum.getEnum(PlanStatus.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// プランタイプ
	// -----------------------------------------------------------------------------
	/**
	 * プランタイプEnum<br>
	 * ※IDの値はDBに保持しているため変更しないこと
	 */
	@Getter
	@AllArgsConstructor
	public enum PlanType {

		STARTER("1", "スタータープラン", LicensePrice.LICENSE_STARTER_1), // スタータープラン
		STANDARD("2", "スタンダードプラン", LicensePrice.LICENSE_STANDARD_1), // スタンダードプラン
		;

		/** ID */
		private String id;
		
		/** タイトル */
		private String title;

		/** ライセンス金額のEnum */
		private LicensePrice licensePrice;
		
		/**
		 * IDからEnum値を取得する
		 *
		 * @param id ID
		 * @return Enum
		 */
		public static PlanType of(String id) {
			
			Optional<PlanType> planTypeOpt = Arrays.stream(PlanType.values())
					.filter(e -> e.getId().equals(id))
					.findFirst();

			return planTypeOpt.orElse(null);
		}
	}
	
	// -----------------------------------------------------------------------------
	// プラン料金
	// -----------------------------------------------------------------------------
	/**
	 * ライセンス金額Enum
	 */
	@Getter
	@AllArgsConstructor
	public enum LicensePrice {

		LICENSE_STARTER_1(1, 1480), // 1ライセンスの料金（スターター）
		LICENSE_STANDARD_1(2, 3980), // 1ライセンスの料金（スタンダード）
		;

		/** ID */
		private int id;
		
		/** 金額 */
		private int price;
	}
	
	/**
	 * ストレージ金額Enum
	 */
	@Getter
	@AllArgsConstructor
	public enum StoragePrice {

		STORAGE_15(1, 15, 0), // 15GBの料金
		STORAGE_100(2, 100, 500), // 100GBの料金
		STORAGE_300(3, 300, 1200), // 300GBの料金
		STORAGE_500(4, 500, 1800) // 500GBの料金
		;

		/** ID */
		private int id;

		/** 数量 */
		private int quantity;
		
		/** 金額 */
		private int price;

		/**
		 * 指定数量の金額Enumを取得する
		 *
		 * @param quantity
		 * @return
		 */
		public static StoragePrice valueOf(int quantity) {

			Optional<StoragePrice> storagePriceOpt = Arrays.stream(StoragePrice.values())
					.filter(e -> e.getQuantity() == quantity)
					.findFirst();

			return storagePriceOpt.orElse(null);
		}
	}
	
	// -----------------------------------------------------------------------------
	// プラン機能制限
	// -----------------------------------------------------------------------------
	/**
	 * プランタイプによる機能制限の対象となる「機能のID」をあらわすEnum<br>
	 * ※ID値はDBの値（m_plan_func_restrict.func_restrict_id）と連動している
	 */
	@Getter
	@AllArgsConstructor
	public enum PlanFuncRestrict {
		
		PF0001("PF0001"), // メッセージ
		PF0002("PF0002"); // 会計管理
		
		/** 機能制限ID */
		private String funcRestrictId;
		
		/**
		 * IDからEnumを取得する
		 * 
		 * @param id
		 * @return
		 */
		public static PlanFuncRestrict of(String id) {
			
			Optional<PlanFuncRestrict> planFuncRestrictOpt = Arrays.stream(PlanFuncRestrict.values())
					.filter(e -> e.getFuncRestrictId().equals(id))
					.findFirst();

			return planFuncRestrictOpt.orElse(null);
		}
	}

}
