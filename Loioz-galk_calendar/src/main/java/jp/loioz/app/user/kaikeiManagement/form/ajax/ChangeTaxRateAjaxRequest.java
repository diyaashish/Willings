package jp.loioz.app.user.kaikeiManagement.form.ajax;

import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class ChangeTaxRateAjaxRequest {

	/** 顧客ID */
	@Required
	private Long customerId;

	/** 案件ID */
	@Required
	private Long ankenId;

	/** 報酬項目ID */
	@EnumType(value = LawyerHoshu.class)
	private String hoshuKomokuId;

	/** タイムチャージ時間の指定方法 */
	@EnumType(value = TimeChargeTimeShitei.class)
	private String timeChargeTimeShitei;

	/** 発生日 */
	@LocalDatePattern
	private String hasseiDate;

	/** タイムチャージ開始日 */
	@LocalDatePattern
	private String timeChargeStartDate;

}
