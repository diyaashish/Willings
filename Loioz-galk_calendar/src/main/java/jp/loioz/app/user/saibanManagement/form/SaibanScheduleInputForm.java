package jp.loioz.app.user.saibanManagement.form;

import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 裁判期日予定の入力フォームクラス
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SaibanScheduleInputForm extends ScheduleInputForm {

	/** 期日回数 */
	private long saibanLimitCount;
}