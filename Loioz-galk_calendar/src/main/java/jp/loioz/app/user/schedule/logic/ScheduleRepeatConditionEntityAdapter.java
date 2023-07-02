package jp.loioz.app.user.schedule.logic;

import java.time.LocalDate;
import java.util.Map;

import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.common.constant.CommonConstant.JsDayOfWeek;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.entity.TScheduleEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 予定Entityを予定繰り返し条件として扱うアダプター
 */
@RequiredArgsConstructor
public class ScheduleRepeatConditionEntityAdapter implements ScheduleRepeatCondition {

	@Getter
	private final TScheduleEntity entity;

	@Override
	public LocalDate getDateFrom() {
		return entity.getDateFrom();
	};

	@Override
	public LocalDate getDateTo() {
		return entity.getDateTo();
	};

	@Override
	public boolean isRepeat() {
		return SystemFlg.codeToBoolean(entity.getRepeatFlg());
	};

	@Override
	public ScheduleRepeatType getRepeatType() {
		return ScheduleRepeatType.of(entity.getRepeatType());
	};

	@Override
	public Map<JsDayOfWeek, Boolean> getRepeatYobi() {
		return ScheduleCommonService.decodeRepeatYobi(entity.getRepeatYobi());
	};

	@Override
	public Long getRepeatDayOfMonth() {
		return entity.getRepeatDayOfMonth();
	};
}