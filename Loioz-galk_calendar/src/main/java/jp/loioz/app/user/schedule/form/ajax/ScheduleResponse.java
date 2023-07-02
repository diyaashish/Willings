package jp.loioz.app.user.schedule.form.ajax;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.config.JsonConfig.LocalDateKeySerializer;
import lombok.Data;

/**
 * 予定表：予定取得のレスポンス情報
 */
@Data
public class ScheduleResponse {

	/** 予定 */
	private Map<String, ScheduleDetail> schedule = new HashMap<>();

	/** 日付毎の予定 */
	@JsonSerialize(keyUsing = LocalDateKeySerializer.class)
	private Map<LocalDate, List<String>> scheduleByDate = new HashMap<>();

	/** アカウント毎に分割していない予定(祝日も含む) */
	private List<ScheduleDetail> scheduleList = Collections.emptyList();
}