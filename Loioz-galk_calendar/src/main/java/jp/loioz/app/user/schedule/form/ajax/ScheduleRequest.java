package jp.loioz.app.user.schedule.form.ajax;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * 予定表：予定取得のリクエスト情報
 */
@Data
public class ScheduleRequest {

	/** 日付From */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate dateFrom;

	/** 日付To */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate dateTo;
}
