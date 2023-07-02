package jp.loioz.domain.condition;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 予定の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSearchCondition extends SearchCondition {

	/** 予定SEQ */
	private Long scheduleSeq;

	/** 日付From */
	private LocalDate dateFrom;

	/** 日付To */
	private LocalDate dateTo;

	/** 時間Form */
	private LocalTime timeFrom;

	/** 時間To */
	private LocalTime timeTo;

	/** 繰返し曜日 */
	private String repeatYobi;

	/** 繰返し日 */
	private Long repeatDayOfMonth;
}