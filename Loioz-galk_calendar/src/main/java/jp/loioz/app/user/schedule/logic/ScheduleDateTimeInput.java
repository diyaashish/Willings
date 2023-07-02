package jp.loioz.app.user.schedule.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import jp.loioz.common.constant.CommonConstant.JsDayOfWeek;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;

/**
 * 予定日時条件の画面入力情報
 */
public interface ScheduleDateTimeInput {

	/** 日付From */
	public LocalDate getDateFrom();

	/** 日付To */
	public LocalDate getDateTo();

	/** 時間From */
	public LocalTime getTimeFrom();

	/** 時間To */
	public LocalTime getTimeTo();

	/** 時間：時間From */
	public int getHourFrom();

	/** 時間：分From */
	public int getMinFrom();

	/** 時間：時間To */
	public int getHourTo();

	/** 時間：分To */
	public int getMinTo();

	/** 終日 */
	public boolean isAllday();

	/** 繰返し */
	public boolean isRepeat();

	/** 繰返しタイプ */
	public ScheduleRepeatType getRepeatType();

	/** 繰返し曜日 */
	public Map<JsDayOfWeek, Boolean> getRepeatYobi();

	/** 繰返し日 */
	public Long getRepeatDayOfMonth();

	/** 繰返し期間指定 */
	public boolean isUseRepeatDate();

	/** 繰返し日付From */
	public LocalDate getRepeatDateFrom();

	/** 繰返し日付To */
	public LocalDate getRepeatDateTo();
}