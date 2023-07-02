package jp.loioz.common.constant.meiboList;

import org.seasar.doma.Domain;

import jp.loioz.common.constant.DefaultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 名簿一覧系の定数クラス
 */
public class MeiboListConstant {

	/**
	 * 名簿一覧 メニュー項目
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum MeiboMenu implements DefaultEnum {

		ALL("1", "すべて"),
		CUSTOMER_ALL("2", "顧客"),
		CUSTOMER_KOJIN("3", "個人"),
		CUSTOMER_HOJIN("4", "法人"),
		ADVISOR("5", "顧問"),
		BENGOSHI("6", "弁護士"),
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
		public static MeiboMenu of(String cd) {
			return DefaultEnum.getEnum(MeiboMenu.class, cd);
		}
	}

	/**
	 * 「名簿一覧：すべて」のソート順
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AllMeiboListSortItem implements DefaultEnum {

		PERSON_ID("1", "名簿ID"),
		NAME("2", "名前"),
		CUSTOMER_CREATE_DATE("3", "登録日"),
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
		public static AllMeiboListSortItem of(String cd) {
			return DefaultEnum.getEnum(AllMeiboListSortItem.class, cd);
		}
	}

	/**
	 * 「名簿一覧：個人顧客」のソート順
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CustomerKojinMeiboListSortItem implements DefaultEnum {

		PERSON_ID("1", "名簿ID"),
		CUSTOMER_NAME("2", "顧客名"),
		CUSTOMER_CREATE_DATE("3", "登録日"),
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
		public static CustomerKojinMeiboListSortItem of(String cd) {
			return DefaultEnum.getEnum(CustomerKojinMeiboListSortItem.class, cd);
		}
	}

	/**
	 * 「名簿一覧：法人顧客」のソート順
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CustomerHojinMeiboListSortItem implements DefaultEnum {

		PERSON_ID("1", "名簿ID"),
		CUSTOMER_HOJIN_NAME("2", "法人名"),
		CUSTOMER_DAIHYO_NAME("3", "代表者名"),
		CUSTOMER_CREATE_DATE("4", "登録日"),
		CUSTOMER_TANTO_NAME("5", "担当者名"),
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
		public static CustomerHojinMeiboListSortItem of(String cd) {
			return DefaultEnum.getEnum(CustomerHojinMeiboListSortItem.class, cd);
		}
	}

	/**
	 * 「名簿一覧：顧客」のソート順
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum CustomerAllMeiboListSortItem implements DefaultEnum {

		PERSON_ID("1", "名簿ID"),
		NAME("2", "名前（かな）"),
		CUSTOMER_CREATE_DATE("3", "登録日"),
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
		public static CustomerAllMeiboListSortItem of(String cd) {
			return DefaultEnum.getEnum(CustomerAllMeiboListSortItem.class, cd);
		}
	}

	/**
	 * 「名簿一覧：顧問」のソート順
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum AdvisorMeiboListSortItem implements DefaultEnum {

		PERSON_ID("1", "名簿ID"),
		NAME("2", "名前（かな）"),
		CUSTOMER_CREATE_DATE("3", "顧客登録日"),
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
		public static AdvisorMeiboListSortItem of(String cd) {
			return DefaultEnum.getEnum(AdvisorMeiboListSortItem.class, cd);
		}
	}

	/**
	 * 「名簿一覧：弁護士」のソート順
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum BengoshiMeiboListSortItem implements DefaultEnum {

		PERSON_ID("1", "名簿ID"),
		NAME("2", "名前（かな）"),
		CREATE_DATE("3", "登録日"),
		JIMUSHO_NAME("4", "事務所名"),
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
		public static BengoshiMeiboListSortItem of(String cd) {
			return DefaultEnum.getEnum(BengoshiMeiboListSortItem.class, cd);
		}
	}

}
