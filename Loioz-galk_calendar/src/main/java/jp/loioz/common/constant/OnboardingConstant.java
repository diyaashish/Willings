package jp.loioz.common.constant;

import org.seasar.doma.Domain;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * オンボーディングのコンスタントクラス
 */
public class OnboardingConstant {

	/**
	 * オンボーディングEnum
	 */
	@Getter
	@AllArgsConstructor
	@Domain(valueType = String.class, accessorMethod = "getCd", factoryMethod = "of")
	public enum Onboarding implements DefaultEnum {

		FIRST_LOGIN("1", "初回ログイン", "firstLoginOnboarding"),
		;

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/** HTMLファイル名(拡張子を除く) */
		private String html;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd
		 * @return
		 */
		public static Onboarding of(String cd) {
			return DefaultEnum.getEnum(Onboarding.class, cd);
		}

		/**
		 * Enum値からHTMLファイル名(拡張子を除く)を取得する
		 *
		 * @param onboarding
		 * @return
		 */
		public static String getHtml(@Nullable Onboarding onboarding) {
			if (onboarding == null) {
				return null;
			}
			return onboarding.getHtml();
		}

		/**
		 * コードからHTMLファイル名(拡張子を除く)を取得する
		 *
		 * @param cd
		 * @return
		 */
		public static String getHtml(String cd) {
			Onboarding onboarding = Onboarding.of(cd);
			return Onboarding.getHtml(onboarding);
		}

	}

}
