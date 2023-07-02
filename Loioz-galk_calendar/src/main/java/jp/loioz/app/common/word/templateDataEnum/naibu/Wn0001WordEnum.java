package jp.loioz.app.common.word.templateDataEnum.naibu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.common.constant.CommonConstant;
import lombok.AllArgsConstructor;

/**
 * 受領証（Word）のデータEnum
 */
@AllArgsConstructor
public enum Wn0001WordEnum implements WordEnum {

	TXT_ANKEN_ID("TXT_ankenId", CommonConstant.BLANK, Collections.emptyList()),
	TXT_LAWYER("TXT_lawyer", CommonConstant.BLANK, Collections.emptyList()),
	TXT_CREATED_AT("TXT_createdAt", CommonConstant.BLANK, Collections.emptyList()),
	TXT_HINMOKU("TXT_hinmoku", CommonConstant.BLANK, Collections.emptyList()),
	;

	/** wordのテンプレートに記載しているKey文字列 */
	private String key;

	/** Key文字列と対応する値文字列 */
	private String value;

	/** Key文字列と対応する値文字列(複数) */
	private List<String> values;

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public List<String> getListValue() {
		return this.values;
	}

	@AllArgsConstructor
	class Wn0001WordEnumDto implements WordEnum {

		/** wordのテンプレートに記載しているKey文字列 */
		private String key;

		/** Key文字列と対応する値文字列 */
		private String value;

		/** Key文字列と対応する値文字列(複数) */
		private List<String> values;

		@Override
		public String getKey() {
			return this.key;
		}

		@Override
		public String getValue() {
			return this.value;
		}

		@Override
		public List<String> getListValue() {
			return this.values;
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

		return new Wn0001WordEnumDto(this.key, dtoValue, Collections.emptyList());
	}

	/**
	 * 値文字列をセットする(配列)
	 *
	 * @param value
	 * @return
	 */
	public WordEnum setValue(List<String> values) {
		List<String> list = new ArrayList<String>();

		if (ListUtils.isEmpty(values)) {
			list = Collections.emptyList();
		} else {
			list = values;
		}

		return new Wn0001WordEnumDto(this.key, this.value, list);
	}
}
