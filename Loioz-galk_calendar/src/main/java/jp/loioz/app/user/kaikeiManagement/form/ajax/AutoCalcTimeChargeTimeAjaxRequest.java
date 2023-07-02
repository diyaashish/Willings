package jp.loioz.app.user.kaikeiManagement.form.ajax;

import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.LocalTimePattern;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * タイムチャージ時間の自動計算用 リクエストオブジェクト
 */
@Data
public class AutoCalcTimeChargeTimeAjaxRequest {

	/** 報酬項目ID */
	@Required
	private String hoshuKomokuId;

	/** 時間の指定方法 */
	@EnumType(value = TimeChargeTimeShitei.class)
	private String timeChargeTimeShitei;

	/** 日付From */
	@LocalDatePattern
	private String dateFrom;

	/** 時間From */
	@LocalTimePattern
	private String timeFrom;

	/** 日付To */
	@LocalDatePattern
	private String dateTo;

	/** 時間To */
	@LocalTimePattern
	private String timeTo;
	
	/** 経過時間(分) */
	private Long timeChargeTime;
}
