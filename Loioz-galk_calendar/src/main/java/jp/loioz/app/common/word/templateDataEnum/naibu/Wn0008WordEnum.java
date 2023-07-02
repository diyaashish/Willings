package jp.loioz.app.common.word.templateDataEnum.naibu;

import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.common.constant.ChohyoWordConstant;
import jp.loioz.common.constant.CommonConstant;
import lombok.AllArgsConstructor;

/**
 * 口頭弁論期日請書のデータEnum
 */
@AllArgsConstructor
public enum Wn0008WordEnum implements WordEnum {

	/** 共通 */
	TXT_OUTPUT_DATE(ChohyoWordConstant.TXT_OUTPUT_DATE, CommonConstant.BLANK),
	TXT_TOJISHA_UP(ChohyoWordConstant.TXT_TOJISHA_UP, CommonConstant.BLANK),
	TXT_TOJISHA_MID(ChohyoWordConstant.TXT_TOJISHA_MID, CommonConstant.BLANK),
	TXT_TOJISHA_LOW(ChohyoWordConstant.TXT_TOJISHA_LOW, CommonConstant.BLANK),

	/** FAX送信書 */
	TXT_ATESAKI1(ChohyoWordConstant.TXT_ATESAKI1, CommonConstant.BLANK),
	TXT_ATESAKI2(ChohyoWordConstant.TXT_ATESAKI2, CommonConstant.BLANK),
	TXT_FAXNO(ChohyoWordConstant.TXT_FAXNO, CommonConstant.BLANK),
	TXT_TENANT_ZIPCODE(ChohyoWordConstant.TXT_TENANT_ZIPCODE, CommonConstant.BLANK),
	TXT_TENANT_ADDRESS1(ChohyoWordConstant.TXT_TENANT_ADDRESS1, CommonConstant.BLANK),
	TXT_TENANT_ADDRESS2(ChohyoWordConstant.TXT_TENANT_ADDRESS2, CommonConstant.BLANK),
	TXT_TENANT_NAME(ChohyoWordConstant.TXT_TENANT_NAME, CommonConstant.BLANK),
	TXT_TENANT_TELNO(ChohyoWordConstant.TXT_TENANT_TELNO, CommonConstant.BLANK),
	TXT_TENANT_FAXNO(ChohyoWordConstant.TXT_TENANT_FAXNO, CommonConstant.BLANK),

	TXT_JIKEN_NO1(ChohyoWordConstant.TXT_JIKEN_NO1, CommonConstant.BLANK),
	TXT_JIKEN_NAME1(ChohyoWordConstant.TXT_JIKEN_NAME1, CommonConstant.BLANK),
	TXT_JIKEN_NO2(ChohyoWordConstant.TXT_JIKEN_NO2, CommonConstant.BLANK),
	TXT_JIKEN_NAME2(ChohyoWordConstant.TXT_JIKEN_NAME2, CommonConstant.BLANK),

	/** 期日請書 */
	TXT_SAIBANSHO(ChohyoWordConstant.TXT_SAIBANSHO, CommonConstant.BLANK),
	TXT_LIMIT_COUNT(ChohyoWordConstant.TXT_LIMIT_COUNT, CommonConstant.BLANK),
	TXT_LIMIT_DATE(ChohyoWordConstant.TXT_LIMIT_DATE, CommonConstant.BLANK),
	TXT_TOJISHA_DAIRININ(ChohyoWordConstant.TXT_TOJISHA_DAIRININ, CommonConstant.BLANK);

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
	class Wn0008WordEnumDto implements WordEnum {

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

		return new Wn0008WordEnumDto(this.key, dtoValue);
	}

}
