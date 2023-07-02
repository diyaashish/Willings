package jp.loioz.app.user.schedule.form.ajax;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * 予定表：会議室表示での予定取得のリクエスト情報
 */
@Data
public class RoomScheduleRequest {

	/** 日付 */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate date;
}
