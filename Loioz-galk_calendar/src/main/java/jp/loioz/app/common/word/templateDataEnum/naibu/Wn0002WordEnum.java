package jp.loioz.app.common.word.templateDataEnum.naibu;

import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.common.constant.CommonConstant;
import lombok.AllArgsConstructor;

/**
 * 預かり証データEnum
 */
@AllArgsConstructor
public enum Wn0002WordEnum implements WordEnum {

	TXT_ANKENID("TXT_ankenId", CommonConstant.BLANK),
	TXT_CREATED_AT("TXT_createdAt", CommonConstant.BLANK),
	TXT_ATESAKI1("TXT_atesaki1", CommonConstant.BLANK),
	TXT_ATESAKI2("TXT_atesaki2", CommonConstant.BLANK),
	TXT_TENANT_ZIPCODE("TXT_tenantZipCode", CommonConstant.BLANK),
	TXT_TENANT_ADDRESS1("TXT_tenantAddress1", CommonConstant.BLANK),
	TXT_TENANT_ADDRESS2("TXT_tenantAddress2", CommonConstant.BLANK),
	TXT_TENANT_NAME("TXT_tenantName", CommonConstant.BLANK),
	TXT_TENANT_TELNO("TXT_tenantTelNo", CommonConstant.BLANK),
	TXT_TENANT_FAXNO("TXT_tenantFaxNo", CommonConstant.BLANK),
	TXT_TANTO_LAWYER("TXT_lawyer", CommonConstant.BLANK),
	TXT_HINMOKU("TXT_hinmoku", CommonConstant.BLANK),
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
	class Wn0002WordEnumDto implements WordEnum {

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

		return new Wn0002WordEnumDto(this.key, dtoValue);
	}

}
