package jp.loioz.app.user.schedule.form.ajax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.loioz.app.user.schedule.form.ScheduleDetail;
import lombok.Data;

/**
 * 予定表：会議室表示での予定取得のレスポンス情報
 */
@Data
public class RoomScheduleResponse {

	/** 予定 */
	private Map<String, ScheduleDetail> schedule = new HashMap<>();

	/** 会議室毎の予定 */
	private Map<Long, List<String>> scheduleByRoom = new HashMap<>();
}