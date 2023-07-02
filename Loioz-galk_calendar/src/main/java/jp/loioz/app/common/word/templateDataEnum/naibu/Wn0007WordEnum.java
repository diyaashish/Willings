package jp.loioz.app.common.word.templateDataEnum.naibu;

import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.common.constant.ChohyoWordConstant;
import jp.loioz.common.constant.CommonConstant;
import lombok.AllArgsConstructor;

/**
 * 送付書（一般）のデータEnum
 */
@AllArgsConstructor
public enum Wn0007WordEnum implements WordEnum {

	TXT_ID(ChohyoWordConstant.TXT_id, CommonConstant.BLANK),
	TXT_ZIPCODE(ChohyoWordConstant.TXT_ZIPCODE, CommonConstant.BLANK),
	TXT_ATESAKI1(ChohyoWordConstant.TXT_ATESAKI1, CommonConstant.BLANK),
	TXT_ATESAKI2(ChohyoWordConstant.TXT_ATESAKI2, CommonConstant.BLANK),
	TXT_ATESAKI3(ChohyoWordConstant.TXT_ATESAKI3, CommonConstant.BLANK),
	TXT_FAXNO(ChohyoWordConstant.TXT_FAXNO, CommonConstant.BLANK),
	TXT_CREATED_AT(ChohyoWordConstant.TXT_CREATED_AT, CommonConstant.BLANK),
	TXT_TENANT_ZIPCODE(ChohyoWordConstant.TXT_TENANT_ZIPCODE, CommonConstant.BLANK),
	TXT_TENANT_ADDRESS1(ChohyoWordConstant.TXT_TENANT_ADDRESS1, CommonConstant.BLANK),
	TXT_TENANT_ADDRESS2(ChohyoWordConstant.TXT_TENANT_ADDRESS2, CommonConstant.BLANK),
	TXT_TENANT_NAME(ChohyoWordConstant.TXT_TENANT_NAME, CommonConstant.BLANK),
	TXT_TENANT_TELNO(ChohyoWordConstant.TXT_TENANT_TELNO, CommonConstant.BLANK),
	TXT_TENANT_FAXNO(ChohyoWordConstant.TXT_TENANT_FAXNO, CommonConstant.BLANK),
	TXT_TANTO_LAWYER(ChohyoWordConstant.TXT_TANTO_LAWYER, CommonConstant.BLANK),
	TXT_TANTO_JIMU(ChohyoWordConstant.TXT_TANTO_JIMU, CommonConstant.BLANK);

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
	class Wn0007WordEnumDto implements WordEnum {

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

		return new Wn0007WordEnumDto(this.key, dtoValue);
	}

}