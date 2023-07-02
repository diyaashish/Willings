package jp.loioz.app.common.word.templateDataEnum.gaibu;

import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.common.constant.CommonConstant;
import lombok.AllArgsConstructor;

/**
 * 特定技能所属機関の役員に関する誓約書（Word）のデータEnum
 */
@AllArgsConstructor
public enum Wg0001WordEnum implements WordEnum {

	BOARDMEMBER_NAME_KANA_1("(BOARD-MEMBER-NAME-KANA-1)", ""),
	BOARDMEMBER_NAME_1("(BOARD-MEMBER-NAME-1)", ""),
	BOARDMEMBER_NAME_KANA_2("(BOARD-MEMBER-NAME-KANA-2)", ""),
	BOARDMEMBER_NAME_2("(BOARD-MEMBER-NAME-2)", ""),
	BOARDMEMBER_NAME_KANA_3("(BOARD-MEMBER-NAME-KANA-3)", ""),
	BOARDMEMBER_NAME_3("(BOARD-MEMBER-NAME-3)", ""),
	BOARDMEMBER_NAME_KANA_4("(BOARD-MEMBER-NAME-KANA-4)", ""),
	BOARDMEMBER_NAME_4("(BOARD-MEMBER-NAME-4)", ""),
	BOARDMEMBER_NAME_KANA_5("(BOARD-MEMBER-NAME-KANA-5)", ""),
	BOARDMEMBER_NAME_5("(BOARD-MEMBER-NAME-5)", ""),
	BOARDMEMBER_NAME_KANA_6("(BOARD-MEMBER-NAME-KANA-6)", ""),
	BOARDMEMBER_NAME_6("(BOARD-MEMBER-NAME-6)", ""),
	BOARDMEMBER_NAME_KANA_7("(BOARD-MEMBER-NAME-KANA-7)", ""),
	BOARDMEMBER_NAME_7("(BOARD-MEMBER-NAME-7)", ""),
	BOARDMEMBER_NAME_KANA_8("(BOARD-MEMBER-NAME-KANA-8)", ""),
	BOARDMEMBER_NAME_8("(BOARD-MEMBER-NAME-8)", ""),
	CREATE_DATE("(CREATE-DATE)", ""),
	RSO_NAME("(RSO-NAME)", ""),
	;

	/** wordのテンプレートに記載しているKey文字列 */
	private String key;

	/** Key文字列と対応する値文字列 */
	private String value;

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@AllArgsConstructor
	class Wg0001WordEnumDto implements WordEnum {

		/** wordのテンプレートに記載しているKey文字列 */
		private String key;

		/** Key文字列と対応する値文字列 */
		private String value;

		@Override
		public String getKey() {
			return this.key;
		}

		@Override
		public String getValue() {
			return this.value;
		}
	}
	
	/**
	 * 値文字列をセットする
	 * 
	 * @param value
	 * @return
	 */
	public WordEnum setValue(String value) {

		String dtoValue = null;

		if (value == null) {
			dtoValue = CommonConstant.BLANK;
		} else {
			dtoValue = value;
		}

		return new Wg0001WordEnumDto(this.key, dtoValue);
	}
}
