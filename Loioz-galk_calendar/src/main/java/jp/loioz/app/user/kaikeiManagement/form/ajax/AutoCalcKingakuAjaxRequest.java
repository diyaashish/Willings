package jp.loioz.app.user.kaikeiManagement.form.ajax;

import jp.loioz.common.constant.CommonConstant.GensenChoshu;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class AutoCalcKingakuAjaxRequest {

	/** 金額 */
	@Required
	@MinNumericValue(min = 0)
	private String kingaku;

	/** 「税抜、税込」選択 */
	@Required
	@EnumType(value = TaxFlg.class)
	private String taxFlg;
	
	/** 消費税率 */
	@Required
	@EnumType(value = TaxRate.class)
	private String taxRate;

	/** 源泉徴収フラグ */
	@Required
	@EnumType(value = GensenChoshu.class)
	private String gensenchoshuFlg;

}
