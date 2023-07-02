package jp.loioz.app.user.schedule.logic;

import java.time.LocalDate;
import java.util.Map;

import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.common.constant.CommonConstant.JsDayOfWeek;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 予定日時条件を予定繰り返し条件として扱うアダプター
 */
@RequiredArgsConstructor
public class ScheduleRepeatConditionDateTimeAdapter<T extends ScheduleDateTimeInput> implements ScheduleRepeatCondition {

	@Getter
	private final T scheduleDateTime;

	@Override
	public LocalDate getDateFrom() {
		if (scheduleDateTime.isRepeat()) {
			// 繰り返し指定あり
			if (scheduleDateTime.isUseRepeatDate()) {
				// 繰り返し期間指定あり
				return scheduleDateTime.getRepeatDateFrom();

			} else {
				// 繰り返し期間指定なし
				// 繰り返し日付は入力されないので、Fromは通常の日付を使用
				return scheduleDateTime.getDateFrom();
			}

		} else {
			// 繰り返し指定なし
			return scheduleDateTime.getDateFrom();
		}
	};

	@Override
	public LocalDate getDateTo() {
		if (scheduleDateTime.isRepeat()) {
			// 繰り返し指定あり
			if (scheduleDateTime.isUseRepeatDate()) {
				// 繰り返し期間指定あり
				return scheduleDateTime.getRepeatDateTo();

			} else {
				// 繰り返し期間指定なし
				// Toは繰り返し期間上限を設定
				return ScheduleCommonService.calcScheduleRepeatMaxDate(scheduleDateTime.getDateFrom());
			}

		} else {
			// 繰り返し指定なし
			if (scheduleDateTime.isAllday()) {
				// 終日の場合は日付の範囲
				return scheduleDateTime.getDateTo();
			} else {
				// 終日でない場合は1日分の範囲(From = To)
				return scheduleDateTime.getDateFrom();
			}
		}
	};

	@Override
	public boolean isRepeat() {
		return scheduleDateTime.isRepeat();
	};

	@Override
	public ScheduleRepeatType getRepeatType() {
		return scheduleDateTime.getRepeatType();
	};

	@Override
	public Map<JsDayOfWeek, Boolean> getRepeatYobi() {
		return scheduleDateTime.getRepeatYobi();
	};

	@Override
	public Long getRepeatDayOfMonth() {
		return scheduleDateTime.getRepeatDayOfMonth();
	};
}