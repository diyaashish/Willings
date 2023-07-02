package jp.loioz.app.user.tenantRegist.form;

import jp.loioz.common.constant.CommonConstant.CompanyTodofuken;
import jp.loioz.common.constant.CommonConstant.TenantType;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.Hiragana;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import jp.loioz.common.validation.annotation.ZipPattern;
import lombok.Data;

@Data
public class TenantSettingForm {

	// 事務所情報
	/** 事務所名 */
	@Required
	@MaxDigit(max = 64)
	String tenantName;

	/** 個人・法人区分 */
	@Required
	@EnumType(TenantType.class)
	String tenantType;

	/** 郵便番号 */
	@ZipPattern
	String tenantZipCd;

	/** 都道府県 */
	@EnumType(CompanyTodofuken.class)
	String tenantTodofukenCd;

	/** 住所1 */
	@MaxDigit(max = 256)
	String tenantAddress1;

	/** 住所2 */
	@MaxDigit(max = 128)
	String tenantAddress2;

	/** 電話番号 */
	@Required
	@TelPattern
	@MaxDigit(max = 13)
	String tenantTelNo;

	/** FAX番号 */
	@TelPattern
	@MaxDigit(max = 13)
	String tenantFaxNo;

	/** 代表者姓 */
	@Required
	@MaxDigit(max = 24)
	String tenantDaihyoNameSei;

	/** 代表者姓（かな） */
	@Required
	@MaxDigit(max = 64)
	@Hiragana
	String tenantDaihyoNameSeiKana;

	/** 代表者名 */
	@Required
	@MaxDigit(max = 24)
	String tenantDaihyoNameMei;

	/** 代表者名（かな） */
	@Required
	@MaxDigit(max = 64)
	@Hiragana
	String tenantDaihyoNameMeiKana;

}
