package jp.loioz.app.common.word.templateDataEnum.naibu;

import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.common.constant.ChohyoWordConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.DateUtils;
import lombok.AllArgsConstructor;

/**
 * 公判期日請書のデータEnum
 */
@AllArgsConstructor
public enum Wn0009WordEnum implements WordEnum {

	/** 共通 */
	OUTPUT_DATE(ChohyoWordConstant.TXT_OUTPUT_DATE, DateUtils.getDateToJaDate()),

	/** FAX送信書 */
	TXT_FAXNO(ChohyoWordConstant.TXT_FAXNO, CommonConstant.BLANK),
	TXT_ATESAKI1(ChohyoWordConstant.TXT_ATESAKI1, CommonConstant.BLANK),
	TXT_ATESAKI2(ChohyoWordConstant.TXT_ATESAKI2, CommonConstant.BLANK),
	TXT_TENANT_ZIPCODE(ChohyoWordConstant.TXT_TENANT_ZIPCODE, CommonConstant.BLANK),
	TXT_TENANT_ADDRESS1(ChohyoWordConstant.TXT_TENANT_ADDRESS1, CommonConstant.BLANK),
	TXT_TENANT_ADDRESS2(ChohyoWordConstant.TXT_TENANT_ADDRESS2, CommonConstant.BLANK),
	TXT_TENANT_NAME(ChohyoWordConstant.TXT_TENANT_NAME, CommonConstant.BLANK),
	TXT_TENANT_TELNO(ChohyoWordConstant.TXT_TENANT_TELNO, CommonConstant.BLANK),
	TXT_TENANT_FAXNO(ChohyoWordConstant.TXT_TENANT_FAXNO, CommonConstant.BLANK),

	/** 期日請書 */
	TXT_JIKEN_NO1(ChohyoWordConstant.TXT_JIKEN_NO1, CommonConstant.BLANK),
	TXT_JIKEN_NAME1(ChohyoWordConstant.TXT_JIKEN_NAME1, CommonConstant.BLANK),
	TXT_JIKEN_NO2(ChohyoWordConstant.TXT_JIKEN_NO2, CommonConstant.BLANK),
	TXT_JIKEN_NAME2(ChohyoWordConstant.TXT_JIKEN_NAME2, CommonConstant.BLANK),
	TXT_HIKOKUNIN(ChohyoWordConstant.TXT_HIKOKUNIN, CommonConstant.BLANK),

	TXT_SAIBANSHO(ChohyoWordConstant.TXT_SAIBANSHO, CommonConstant.BLANK),

	TXT_BENGONIN(ChohyoWordConstant.TXT_BENGONIN, CommonConstant.BLANK),

	TXT_LIMIT_DATE(ChohyoWordConstant.TXT_LIMIT_DATE, CommonConstant.BLANK),
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
	class Wn0009WordEnumDto implements WordEnum {

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

		return new Wn0009WordEnumDto(this.key, dtoValue);
	}

}
