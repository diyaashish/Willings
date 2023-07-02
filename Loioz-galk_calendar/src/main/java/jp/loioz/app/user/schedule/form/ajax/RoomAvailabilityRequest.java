package jp.loioz.app.user.schedule.form.ajax;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.user.schedule.logic.ScheduleDateTimeInput;
import jp.loioz.common.constant.CommonConstant.JsDayOfWeek;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;
import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * 会議室予約状況取得のリクエスト情報
 */
@Data
public class RoomAvailabilityRequest implements ScheduleDateTimeInput {

	/** 予定SEQ */
	private Long scheduleSeq;

	/** 日付From */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate dateFrom;

	/** 日付To */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate dateTo;

	/** 時間From */
	@DateTimeFormat(pattern = DateUtils.TIME_FORMAT)
	private LocalTime timeFrom;

	/** 時間To */
	@DateTimeFormat(pattern = DateUtils.TIME_FORMAT)
	private LocalTime timeTo;

	/** 時間：時間From */
	private int hourFrom;

	/** 時間：分From */
	private int minFrom;

	/** 時間：時間To */
	private int hourTo;

	/** 時間：分To */
	private int minTo;

	/** 終日 */
	private boolean allday;

	/** 繰返し */
	private boolean repeat;

	/** 繰返しタイプ */
	private ScheduleRepeatType repeatType;

	/** 繰返し曜日 */
	private Map<JsDayOfWeek, Boolean> repeatYobi = new HashMap<>();

	/** 繰返し日 */
	private Long repeatDayOfMonth;

	/** 繰返し期間指定 */
	private boolean useRepeatDate;

	/** 繰返し日付From */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate repeatDateFrom;

	/** 繰返し日付To */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate repeatDateTo;
}