package jp.loioz.app.user.schedule.logic;

import java.time.LocalDate;
import java.util.Map;

import jp.loioz.common.constant.CommonConstant.JsDayOfWeek;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;

/**
 * 予定繰り返し条件
 */
public interface ScheduleRepeatCondition {

	/** 日付From */
	public LocalDate getDateFrom();

	/** 日付To */
	public LocalDate getDateTo();

	/** 繰返し */
	public boolean isRepeat();

	/** 繰返しタイプ */
	public ScheduleRepeatType getRepeatType();

	/** 繰返し曜日 */
	public Map<JsDayOfWeek, Boolean> getRepeatYobi();

	/** 繰返し日 */
	public Long getRepeatDayOfMonth();
}