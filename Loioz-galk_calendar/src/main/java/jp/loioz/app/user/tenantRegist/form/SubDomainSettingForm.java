package jp.loioz.app.user.tenantRegist.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.SubDomain;
import lombok.Data;

@Data
public class SubDomainSettingForm {

	// ドメイン情報
	/** ドメイン名 */
	@Required
	@MaxDigit(max = 30)
	@SubDomain
	String domain;

}
